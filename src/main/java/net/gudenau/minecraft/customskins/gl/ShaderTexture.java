package net.gudenau.minecraft.customskins.gl;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL20;

public class ShaderTexture{
    private static final TextureManager TEXTURE_MANAGER = MinecraftClient.getInstance().getTextureManager();
    private final int texture;
    private final int textureSlot;

    public ShaderTexture(int textureSlot, Identifier identifier){
        AbstractTexture abstractTexture = TEXTURE_MANAGER.getTexture(identifier);
        if(abstractTexture == null){
            abstractTexture = MissingSprite.getMissingSpriteTexture();
        }
        texture = abstractTexture.getGlId();
        this.textureSlot = textureSlot;
    }

    public void bind(){
        GlStateManager.activeTexture(GL20.GL_TEXTURE0 + textureSlot);
        GlStateManager.enableTexture();
        GlStateManager.bindTexture(texture);
    }

    public void cleanup(){
        GlStateManager.activeTexture(GL20.GL_TEXTURE0 + textureSlot);
        GlStateManager.bindTexture(0);
    }
}
