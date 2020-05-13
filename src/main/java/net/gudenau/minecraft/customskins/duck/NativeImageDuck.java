package net.gudenau.minecraft.customskins.duck;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.nio.ByteBuffer;

@Environment(EnvType.CLIENT)
public interface NativeImageDuck{
    ByteBuffer getBuffer();
}
