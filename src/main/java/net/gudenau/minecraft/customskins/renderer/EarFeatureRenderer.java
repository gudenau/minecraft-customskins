package net.gudenau.minecraft.customskins.renderer;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.MathHelper;

public class EarFeatureRenderer extends CustomFeatureRenderer{
    public EarFeatureRenderer(PlayerEntityRenderer context){
        super(context);
    }

    @Override
    public void render(MatrixStack matrix, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, float limbAngle, float limbDistance, float tickDelta, float customAngle, float headYaw, float headPitch, PlayerSkinExtras extras){
        boolean hasEars = "deadmau5".equals(player.getName().getString());

        if(extras != null && extras.ears != null){
            hasEars = extras.ears;
        }

        if(hasEars && player.hasSkinTexture() && !player.isInvisible()) {
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(player.getSkinTexture()));
            int overlay = LivingEntityRenderer.getOverlay(player, 0.0F);

            for(int i = 0; i < 2; ++i) {
                float yaw = MathHelper.lerp(tickDelta, player.prevYaw, player.yaw) - MathHelper.lerp(tickDelta, player.prevBodyYaw, player.bodyYaw);
                float pitch = MathHelper.lerp(tickDelta, player.prevPitch, player.pitch);
                matrix.push();
                matrix.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(yaw));
                matrix.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(pitch));
                matrix.translate(0.375F * (i * 2 - 1), 0.0D, 0.0D);
                matrix.translate(0.0D, -0.375D, 0.0D);
                matrix.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-pitch));
                matrix.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-yaw));
                matrix.scale(1.3333334F, 1.3333334F, 1.3333334F);
                getContextModel().renderEars(matrix, vertexConsumer, light, overlay);
                matrix.pop();
            }
        }
    }
}
