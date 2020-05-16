package net.gudenau.minecraft.customskins.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.gudenau.minecraft.customskins.duck.PlayerEntityRendererDuck;
import net.gudenau.minecraft.customskins.gl.CustomTexture;
import net.gudenau.minecraft.customskins.gl.ShaderTexture;
import net.gudenau.minecraft.customskins.gl.Shaders;
import net.gudenau.minecraft.customskins.renderer.PlayerSkinExtras;
import net.gudenau.minecraft.customskins.renderer.ShaderRenderLayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Environment(EnvType.CLIENT)
@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixinNoGenerics extends LivingEntityRenderer<LivingEntity, EntityModel<LivingEntity>>{
    @Shadow public abstract Identifier getTexture(AbstractClientPlayerEntity abstractClientPlayerEntity);

    private PlayerEntityRendererMixinNoGenerics(){
        super(null, null, Float.NaN);
    }

    @Override
    protected RenderLayer getRenderLayer(LivingEntity entity, boolean showBody, boolean translucent){
        PlayerSkinExtras extras = ((PlayerEntityRendererDuck)this).getExtras();
        if(extras == null){
            return super.getRenderLayer(entity, showBody, translucent);
        }

        if(extras.emissive != null){
            CustomTexture skinTexture = extras.emissive.skin;
            if(skinTexture != null){
                return new ShaderRenderLayer(
                    Shaders.EMISSIVE,
                    new ShaderTexture(0, getTexture(entity)),
                    new ShaderTexture(1, ((LightmapTextureManagerMixin)MinecraftClient.getInstance().gameRenderer.getLightmapTextureManager()).getTextureIdentifier()),
                    new ShaderTexture(3, skinTexture)
                );
            }
        }

        return new ShaderRenderLayer(
            Shaders.SHADER_TEST,
            new ShaderTexture(0, getTexture(entity)),
            new ShaderTexture(1, ((LightmapTextureManagerMixin)MinecraftClient.getInstance().gameRenderer.getLightmapTextureManager()).getTextureIdentifier())
        );
    }
}
