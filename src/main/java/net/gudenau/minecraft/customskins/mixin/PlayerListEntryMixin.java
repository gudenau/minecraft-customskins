package net.gudenau.minecraft.customskins.mixin;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.gudenau.minecraft.customskins.duck.PlayerListEntryDuck;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Environment(EnvType.CLIENT)
@Mixin(PlayerListEntry.class)
public abstract class PlayerListEntryMixin implements PlayerListEntryDuck {
    @Shadow @Final private Map<MinecraftProfileTexture.Type, Identifier> textures;

    @Override
    public void putTexture(MinecraftProfileTexture.Type type, Identifier identifier){
        textures.put(type, identifier);
    }
}
