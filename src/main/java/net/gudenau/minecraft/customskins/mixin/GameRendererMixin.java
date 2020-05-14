package net.gudenau.minecraft.customskins.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.gudenau.minecraft.customskins.gl.Shaders;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.resource.SynchronousResourceReloadListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin implements AutoCloseable, SynchronousResourceReloadListener{
    @Inject(
        method = "method_22709",
        at = @At("HEAD")
    )
    private void loadProjectionMatrix(Matrix4f projectionMatrix, CallbackInfo callbackInfo){
        Shaders.projectionMatrixChanged(projectionMatrix);
    }
}
