package net.gudenau.minecraft.customskins.renderer;

import net.gudenau.minecraft.customskins.duck.PlayerEntityRendererDuck;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;

public abstract class CustomFeatureRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>{
    private final PlayerEntityRendererDuck context;

    public CustomFeatureRenderer(PlayerEntityRenderer context){
        super(context);
        this.context = (PlayerEntityRendererDuck)context;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, float limbAngle, float limbDistance, float tickDelta, float customAngle, float headYaw, float headPitch){
        render(matrices, vertexConsumers, light, player, limbAngle, limbDistance, tickDelta, customAngle, headYaw, headPitch, context.getExtras());
    }

    public abstract void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, float limbAngle, float limbDistance, float tickDelta, float customAngle, float headYaw, float headPitch, PlayerSkinExtras extras);
}
