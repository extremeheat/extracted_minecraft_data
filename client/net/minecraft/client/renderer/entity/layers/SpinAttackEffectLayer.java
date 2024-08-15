package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
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
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class SpinAttackEffectLayer extends RenderLayer<PlayerRenderState, PlayerModel> {
   public static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/trident_riptide.png");
   private static final int BOX_COUNT = 2;
   private final Model model;
   private final ModelPart[] boxes = new ModelPart[2];

   public SpinAttackEffectLayer(RenderLayerParent<PlayerRenderState, PlayerModel> var1, EntityModelSet var2) {
      super(var1);
      ModelPart var3 = var2.bakeLayer(ModelLayers.PLAYER_SPIN_ATTACK);
      this.model = new Model.Simple(var3, RenderType::entityCutoutNoCull);

      for (int var4 = 0; var4 < 2; var4++) {
         this.boxes[var4] = var3.getChild(boxName(var4));
      }
   }

   private static String boxName(int var0) {
      return "box" + var0;
   }

   public static LayerDefinition createLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();

      for (int var2 = 0; var2 < 2; var2++) {
         float var3 = -3.2F + 9.6F * (float)(var2 + 1);
         float var4 = 0.75F * (float)(var2 + 1);
         var1.addOrReplaceChild(
            boxName(var2), CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -16.0F + var3, -8.0F, 16.0F, 32.0F, 16.0F), PartPose.ZERO.withScale(var4)
         );
      }

      return LayerDefinition.create(var0, 64, 64);
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, PlayerRenderState var4, float var5, float var6) {
      if (var4.isAutoSpinAttack) {
         for (int var7 = 0; var7 < this.boxes.length; var7++) {
            float var8 = var4.ageInTicks * (float)(-(45 + (var7 + 1) * 5));
            this.boxes[var7].yRot = Mth.wrapDegrees(var8) * 0.017453292F;
         }

         VertexConsumer var9 = var2.getBuffer(this.model.renderType(TEXTURE));
         this.model.renderToBuffer(var1, var9, var3, OverlayTexture.NO_OVERLAY);
      }
   }
}
