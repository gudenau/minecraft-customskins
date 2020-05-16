package net.gudenau.minecraft.customskins.json;

import com.google.gson.annotations.Expose;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class CustomSkinTextures {
    @Expose public Group emissive;

    public static class Group {
        @Expose public String skin;
        @Expose public String cape;
        @Expose public String elytra;
    }
}
