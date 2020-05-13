package net.gudenau.minecraft.customskins.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.gudenau.minecraft.customskins.duck.NativeImageDuck;
import net.minecraft.client.texture.NativeImage;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@Environment(EnvType.CLIENT)
@Mixin(NativeImage.class)
public abstract class NativeImageMixin implements NativeImageDuck, AutoCloseable{
    @Shadow private long pointer;
    @Shadow @Final private long sizeBytes;

    @Override
    public ByteBuffer getBuffer(){
        return MemoryUtil.memByteBuffer(pointer, (int)sizeBytes).order(ByteOrder.LITTLE_ENDIAN);
    }
}
