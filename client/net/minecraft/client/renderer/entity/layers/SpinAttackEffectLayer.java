package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class SpinAttackEffectLayer<T extends LivingEntity> extends RenderLayer<T, PlayerModel<T>> {
   public static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/trident_riptide.png");
   public static final String BOX = "box";
   private final ModelPart box;

   public SpinAttackEffectLayer(RenderLayerParent<T, PlayerModel<T>> var1, EntityModelSet var2) {
      super(var1);
      ModelPart var3 = var2.bakeLayer(ModelLayers.PLAYER_SPIN_ATTACK);
      this.box = var3.getChild("box");
   }

   public static LayerDefinition createLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      var1.addOrReplaceChild("box", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -16.0F, -8.0F, 16.0F, 32.0F, 16.0F), PartPose.ZERO);
      return LayerDefinition.create(var0, 64, 64);
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, T var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      if (var4.isAutoSpinAttack()) {
         VertexConsumer var11 = var2.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));

         for(int var12 = 0; var12 < 3; ++var12) {
            var1.pushPose();
            float var13 = var8 * (float)(-(45 + var12 * 5));
            var1.mulPose(Axis.YP.rotationDegrees(var13));
            float var14 = 0.75F * (float)var12;
            var1.scale(var14, var14, var14);
            var1.translate(0.0F, -0.2F + 0.6F * (float)var12, 0.0F);
            this.box.render(var1, var11, var3, OverlayTexture.NO_OVERLAY);
            var1.popPose();
         }

      }
   }
}
