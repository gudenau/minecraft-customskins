package net.gudenau.minecraft.customskins.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.GlDebug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(GlDebug.class)
public abstract class GlDebugMixin{
    @Inject(
        method = "info",
        at = @At("TAIL")
    )
    private static void info(int source, int type, int id, int severity, int i, long l, long m, CallbackInfo callbackInfo) {
        new Throwable().printStackTrace(System.err);
        System.out.print("");
    }
}
