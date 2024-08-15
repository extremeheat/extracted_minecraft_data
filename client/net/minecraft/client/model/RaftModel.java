package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
import net.minecraft.util.Mth;

public class RaftModel extends EntityModel<BoatRenderState> {
   private final ModelPart root;
   private final ModelPart leftPaddle;
   private final ModelPart rightPaddle;

   public RaftModel(ModelPart var1) {
      super();
      this.root = var1;
      this.leftPaddle = var1.getChild("left_paddle");
      this.rightPaddle = var1.getChild("right_paddle");
   }

   public static void addCommonParts(PartDefinition var0) {
      var0.addOrReplaceChild(
         "bottom",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(-14.0F, -11.0F, -4.0F, 28.0F, 20.0F, 4.0F)
            .texOffs(0, 0)
            .addBox(-14.0F, -9.0F, -8.0F, 28.0F, 16.0F, 4.0F),
         PartPose.offsetAndRotation(0.0F, -2.1F, 1.0F, 1.5708F, 0.0F, 0.0F)
      );
      byte var1 = 20;
      byte var2 = 7;
      byte var3 = 6;
      float var4 = -5.0F;
      var0.addOrReplaceChild(
         "left_paddle",
         CubeListBuilder.create().texOffs(0, 24).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F).addBox(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F),
         PartPose.offsetAndRotation(3.0F, -4.0F, 9.0F, 0.0F, 0.0F, 0.19634955F)
      );
      var0.addOrReplaceChild(
         "right_paddle",
         CubeListBuilder.create().texOffs(40, 24).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F).addBox(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F),
         PartPose.offsetAndRotation(3.0F, -4.0F, -9.0F, 0.0F, 3.1415927F, 0.19634955F)
      );
   }

   public static LayerDefinition createRaftModel() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      addCommonParts(var1);
      return LayerDefinition.create(var0, 128, 64);
   }

   public static LayerDefinition createChestRaftModel() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      addCommonParts(var1);
      var1.addOrReplaceChild(
         "chest_bottom",
         CubeListBuilder.create().texOffs(0, 76).addBox(0.0F, 0.0F, 0.0F, 12.0F, 8.0F, 12.0F),
         PartPose.offsetAndRotation(-2.0F, -10.1F, -6.0F, 0.0F, -1.5707964F, 0.0F)
      );
      var1.addOrReplaceChild(
         "chest_lid",
         CubeListBuilder.create().texOffs(0, 59).addBox(0.0F, 0.0F, 0.0F, 12.0F, 4.0F, 12.0F),
         PartPose.offsetAndRotation(-2.0F, -14.1F, -6.0F, 0.0F, -1.5707964F, 0.0F)
      );
      var1.addOrReplaceChild(
         "chest_lock",
         CubeListBuilder.create().texOffs(0, 59).addBox(0.0F, 0.0F, 0.0F, 2.0F, 4.0F, 1.0F),
         PartPose.offsetAndRotation(-1.0F, -11.1F, -1.0F, 0.0F, -1.5707964F, 0.0F)
      );
      return LayerDefinition.create(var0, 128, 128);
   }

   public void setupAnim(BoatRenderState var1) {
      animatePaddle(var1.rowingTimeLeft, 0, this.leftPaddle);
      animatePaddle(var1.rowingTimeRight, 1, this.rightPaddle);
   }

   @Override
   public ModelPart root() {
      return this.root;
   }

   private static void animatePaddle(float var0, int var1, ModelPart var2) {
      var2.xRot = Mth.clampedLerp(-1.0471976F, -0.2617994F, (Mth.sin(-var0) + 1.0F) / 2.0F);
      var2.yRot = Mth.clampedLerp(-0.7853982F, 0.7853982F, (Mth.sin(-var0 + 1.0F) + 1.0F) / 2.0F);
      if (var1 == 1) {
         var2.yRot = 3.1415927F - var2.yRot;
      }
   }
}
