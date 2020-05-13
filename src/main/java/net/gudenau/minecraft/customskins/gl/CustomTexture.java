package net.gudenau.minecraft.customskins.gl;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.TextureUtil;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class CustomTexture {
    private final NativeImage image;
    private int glId = -1;

    private CustomTexture(NativeImage image) {
        this.image = image;
    }

    public static CustomTexture load(String url){
        if(url == null){
            return null;
        }
        try {
            URLConnection connection = new URL(url).openConnection();
            try(NativeImage image = NativeImage.read(connection.getInputStream())){
                return load(image);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static CustomTexture load(NativeImage image) {
        CustomTexture texture = new CustomTexture(image);
        MinecraftClient.getInstance().execute(() -> {
            if (!RenderSystem.isOnRenderThread()) {
                RenderSystem.recordRenderCall(texture::upload);
            } else {
                texture.upload();
            }
        });
        synchronized (texture){
            while(!texture.isLoaded()){
                try {
                    texture.wait(10);
                } catch (InterruptedException ignored) {}
            }
        }
        return texture;
    }

    private void upload() {
        synchronized (this) {
            TextureUtil.prepareImage(getGlId(), image.getWidth(), image.getHeight());
            image.upload(0, 0, 0, true);
            loaded = true;
            this.notify();
        }
    }

    private volatile boolean loaded = false;
    private boolean isLoaded() {
        return loaded;
    }

    private int getGlId() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        if (glId == -1) {
            glId = TextureUtil.generateTextureId();
        }

        return glId;
    }
}
