package net.gudenau.minecraft.customskins.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Environment(EnvType.CLIENT)
@Mixin(BufferBuilder.class)
public interface BufferBuilderMixin{
    @Accessor boolean getField_21594();
    @Accessor boolean getField_21595();
    @Accessor int getElementOffset();
    @Accessor void setElementOffset(int value);

    @Invoker void invokeGrow(int i);
}
