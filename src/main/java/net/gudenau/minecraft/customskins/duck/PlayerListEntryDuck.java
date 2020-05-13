package net.gudenau.minecraft.customskins.duck;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public interface PlayerListEntryDuck {
    void putTexture(MinecraftProfileTexture.Type type, Identifier identifier);
}
