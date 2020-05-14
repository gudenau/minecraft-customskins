package net.gudenau.minecraft.customskins.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.gudenau.minecraft.customskins.gl.ShaderTexture;
import net.gudenau.minecraft.customskins.gl.Shaders;
import net.gudenau.minecraft.customskins.renderer.ShaderRenderLayer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;

@Environment(EnvType.CLIENT)
@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixinNoGenerics extends LivingEntityRenderer<LivingEntity, EntityModel<LivingEntity>>{
    private PlayerEntityRendererMixinNoGenerics(){
        super(null, null, Float.NaN);
    }

    @Override
    protected RenderLayer getRenderLayer(LivingEntity entity, boolean showBody, boolean translucent){
        return new ShaderRenderLayer(
            Shaders.SHADER_TEST,
            new ShaderTexture(0, getTexture(entity))
        );
    }
}
