package net.gudenau.minecraft.customskins.json;

import com.google.gson.annotations.Expose;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class CustomSkinMetadata{
    @Expose public CustomSkinOverrides overrides;
    @Expose public CustomSkinTextures textures;
}
