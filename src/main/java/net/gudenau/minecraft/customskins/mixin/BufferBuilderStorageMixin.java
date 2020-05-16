package net.gudenau.minecraft.customskins.mixin;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.gudenau.minecraft.customskins.gl.Shaders;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.RenderLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(BufferBuilderStorage.class)
public class BufferBuilderStorageMixin{
    //   private synthetic method_22999(Lit/unimi/dsi/fastutil/objects/Object2ObjectLinkedOpenHashMap;)V
    @Inject(
        method = "method_22999",
        at = @At("TAIL")
    )
    private void method_22999(Object2ObjectLinkedOpenHashMap<RenderLayer, BufferBuilder> map, CallbackInfo callbackInfo){
        Shaders.registerBufferBuilders(map);
    }
}
