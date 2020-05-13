package net.gudenau.minecraft.customskins.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.gudenau.minecraft.customskins.duck.PlayerEntityRendererDuck;
import net.gudenau.minecraft.customskins.duck.PlayerSkinTextureDuck;
import net.gudenau.minecraft.customskins.renderer.EarFeatureRenderer;
import net.gudenau.minecraft.customskins.renderer.PlayerSkinExtras;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.Deadmau5FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> implements PlayerEntityRendererDuck{
    private PlayerEntityRendererMixin(){
        super(null, null, Float.NaN);
    }

    private final ThreadLocal<PlayerSkinExtras> extras = new ThreadLocal<>();
    private TextureManager textureManager;

    @Inject(
        method = "<init>(Lnet/minecraft/client/render/entity/EntityRenderDispatcher;Z)V",
        at = @At("TAIL")
    )
    private void init(EntityRenderDispatcher context, boolean slimArms, CallbackInfo info){
        textureManager = MinecraftClient.getInstance().getTextureManager();

        removeFeature(Deadmau5FeatureRenderer.class);

        addFeature(new EarFeatureRenderer((PlayerEntityRenderer)(Object)this));
    }

    private <T extends FeatureRenderer<?, ?>> void removeFeature(Class<T> featureType){
        for(int i = features.size() - 1; i >= 0; i--){
            if(features.get(i).getClass() == featureType){
                features.remove(i);
            }
        }
    }

    @Inject(
        method = "render",
        at = @At("HEAD")
    )
    private void render(AbstractClientPlayerEntity player, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo callbackInfo){
        Identifier skinIdentifier = player.getSkinTexture();
        if(skinIdentifier != null){
            AbstractTexture rawTexture = textureManager.getTexture(skinIdentifier);
            if(rawTexture instanceof PlayerSkinTextureDuck){
                extras.set(((PlayerSkinTextureDuck)rawTexture).getExtras());
                return;
            }
        }
        extras.set(null);
    }

    @Override
    public PlayerSkinExtras getExtras(){
        return extras.get();
    }
}
