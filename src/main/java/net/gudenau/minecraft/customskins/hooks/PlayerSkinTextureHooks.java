package net.gudenau.minecraft.customskins.hooks;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.gudenau.minecraft.customskins.CustomSkins;
import net.gudenau.minecraft.customskins.duck.NativeImageDuck;
import net.gudenau.minecraft.customskins.duck.PlayerListEntryDuck;
import net.gudenau.minecraft.customskins.json.CustomSkinMetadata;
import net.gudenau.minecraft.customskins.json.CustomSkinOverrides;
import net.gudenau.minecraft.customskins.json.Json;
import net.gudenau.minecraft.customskins.renderer.PlayerSkinExtras;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.texture.NativeImage;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.BitSet;
import java.util.zip.CRC32;
import java.util.zip.GZIPInputStream;

@Environment(EnvType.CLIENT)
public class PlayerSkinTextureHooks{
    public static void onTextureLoaded(PlayerSkinExtras extras, NativeImage image, PlayerListEntry listEntry){
        @SuppressWarnings("ConstantConditions")
        ByteBuffer imageData = ((NativeImageDuck)(Object)image).getBuffer();

        BitSet skinMask = CustomSkins.SKIN_MASK;
        ByteBuffer data = ByteBuffer.allocate(736 << 2).order(ByteOrder.LITTLE_ENDIAN);
        for(int i = 0; i < 4096; i++){
            if(skinMask.get(i)){
                int pixel = imageData.getInt(i << 2);
                pixel = (pixel & 0xFF00FF00) | ((pixel >>> 16) & 0x000000FF) | ((pixel << 16) & 0x00FF0000);
                data.putInt(pixel);
            }
        }

        data.position(0);
        int magic = data.getInt();
        if(magic != 0x46425243){
            return;
        }

        int crc32 = data.getInt();
        int length = data.getShort();

        if(length > data.remaining()){
            System.out.println("Bad length");
            return;
        }

        byte[] customPayload = new byte[length];
        data.get(customPayload);

        CRC32 messageDigest = new CRC32();
        messageDigest.update(customPayload, 0, length);
        int digest = (int)messageDigest.getValue();

        if(digest != crc32){
            System.out.println("Bad CRC");
            return;
        }

        CustomSkinMetadata metadata;
        try(InputStreamReader reader = new InputStreamReader(new GZIPInputStream(new ByteArrayInputStream(customPayload)))){
            metadata = Json.fromJson(reader, CustomSkinMetadata.class);
        }catch(IOException e){
            throw new RuntimeException("This should never happen", e);
        }

        CustomSkinOverrides overrides = metadata.overrides;
        if(overrides != null){
            if(overrides.cape != null){
                loadTexture(MinecraftProfileTexture.Type.CAPE, overrides.cape, listEntry);
            }
            if(overrides.elytra != null){
                loadTexture(MinecraftProfileTexture.Type.ELYTRA, overrides.elytra, listEntry);
            }
            extras.ears = overrides.ears;
        }
    }

    private static void loadTexture(MinecraftProfileTexture.Type type, String url, PlayerListEntry listEntry) {
        MinecraftClient.getInstance().execute(()->
            RenderSystem.recordRenderCall(()->
                MinecraftClient.getInstance().getSkinProvider().loadSkin(new MinecraftProfileTexture(url, null), type, (type2, identifier, minecraftProfileTexture)->{
                    if(listEntry != null){
                        ((PlayerListEntryDuck)listEntry).putTexture(type2, identifier);
                    }
                })
            )
        );
    }
}
