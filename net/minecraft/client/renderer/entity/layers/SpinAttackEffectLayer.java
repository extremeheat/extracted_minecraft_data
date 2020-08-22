package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class SpinAttackEffectLayer extends RenderLayer {
   public static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/trident_riptide.png");
   private final ModelPart box = new ModelPart(64, 64, 0, 0);

   public SpinAttackEffectLayer(RenderLayerParent var1) {
      super(var1);
      this.box.addBox(-8.0F, -16.0F, -8.0F, 16.0F, 32.0F, 16.0F);
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, LivingEntity var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      if (var4.isAutoSpinAttack()) {
         VertexConsumer var11 = var2.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));

         for(int var12 = 0; var12 < 3; ++var12) {
            var1.pushPose();
            float var13 = var8 * (float)(-(45 + var12 * 5));
            var1.mulPose(Vector3f.YP.rotationDegrees(var13));
            float var14 = 0.75F * (float)var12;
            var1.scale(var14, var14, var14);
            var1.translate(0.0D, (double)(-0.2F + 0.6F * (float)var12), 0.0D);
            this.box.render(var1, var11, var3, OverlayTexture.NO_OVERLAY);
            var1.popPose();
         }

      }
   }
}
