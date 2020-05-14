package net.gudenau.minecraft.customskins.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.gudenau.minecraft.customskins.duck.RenderLayerDuck;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Environment(EnvType.CLIENT)
@Mixin(RenderLayer.class)
public abstract class RenderLayerMixin extends RenderPhase implements RenderLayerDuck{
    @Shadow @Final private boolean translucent;

    private RenderLayerMixin(){
        super(null, null, null);
    }

    @Override
    public boolean isTranslucent(){
        return translucent;
    }
}
