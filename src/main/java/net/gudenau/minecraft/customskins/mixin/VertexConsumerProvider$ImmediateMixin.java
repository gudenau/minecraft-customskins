package net.gudenau.minecraft.customskins.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.gudenau.minecraft.customskins.renderer.ShaderRenderLayer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(VertexConsumerProvider.Immediate.class)
public class VertexConsumerProvider$ImmediateMixin{
    @Inject(
        method = "getBufferInternal",
        at = @At("HEAD"),
        cancellable = true
    )
    private void getBufferInternal(RenderLayer layer, CallbackInfoReturnable<BufferBuilder> callbackInfo) {
        if(layer instanceof ShaderRenderLayer){
            BufferBuilder builder = ((ShaderRenderLayer)layer).getBufferBuilder();
            if(builder != null){
                callbackInfo.setReturnValue(builder);
            }
        }
    }
}
