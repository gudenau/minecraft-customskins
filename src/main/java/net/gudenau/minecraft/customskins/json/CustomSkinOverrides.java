package net.gudenau.minecraft.customskins.json;

import com.google.gson.annotations.Expose;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class CustomSkinOverrides {
    @Expose public Boolean ears;
    @Expose public String cape;
    @Expose public String elytra;
}
