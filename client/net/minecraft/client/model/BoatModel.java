package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
import net.minecraft.util.Mth;

public class BoatModel extends EntityModel<BoatRenderState> {
   private static final int BOTTOM_WIDTH = 28;
   private static final int WIDTH = 32;
   private static final int DEPTH = 6;
   private static final int LENGTH = 20;
   private static final int Y_OFFSET = 4;
   private static final String WATER_PATCH = "water_patch";
   private static final String BACK = "back";
   private static final String FRONT = "front";
   private static final String RIGHT = "right";
   private static final String LEFT = "left";
   private final ModelPart root;
   private final ModelPart leftPaddle;
   private final ModelPart rightPaddle;

   public BoatModel(ModelPart var1) {
      super();
      this.root = var1;
      this.leftPaddle = var1.getChild("left_paddle");
      this.rightPaddle = var1.getChild("right_paddle");
   }

   private static void addCommonParts(PartDefinition var0) {
      byte var1 = 16;
      byte var2 = 14;
      byte var3 = 10;
      var0.addOrReplaceChild(
         "bottom",
         CubeListBuilder.create().texOffs(0, 0).addBox(-14.0F, -9.0F, -3.0F, 28.0F, 16.0F, 3.0F),
         PartPose.offsetAndRotation(0.0F, 3.0F, 1.0F, 1.5707964F, 0.0F, 0.0F)
      );
      var0.addOrReplaceChild(
         "back",
         CubeListBuilder.create().texOffs(0, 19).addBox(-13.0F, -7.0F, -1.0F, 18.0F, 6.0F, 2.0F),
         PartPose.offsetAndRotation(-15.0F, 4.0F, 4.0F, 0.0F, 4.712389F, 0.0F)
      );
      var0.addOrReplaceChild(
         "front",
         CubeListBuilder.create().texOffs(0, 27).addBox(-8.0F, -7.0F, -1.0F, 16.0F, 6.0F, 2.0F),
         PartPose.offsetAndRotation(15.0F, 4.0F, 0.0F, 0.0F, 1.5707964F, 0.0F)
      );
      var0.addOrReplaceChild(
         "right",
         CubeListBuilder.create().texOffs(0, 35).addBox(-14.0F, -7.0F, -1.0F, 28.0F, 6.0F, 2.0F),
         PartPose.offsetAndRotation(0.0F, 4.0F, -9.0F, 0.0F, 3.1415927F, 0.0F)
      );
      var0.addOrReplaceChild("left", CubeListBuilder.create().texOffs(0, 43).addBox(-14.0F, -7.0F, -1.0F, 28.0F, 6.0F, 2.0F), PartPose.offset(0.0F, 4.0F, 9.0F));
      byte var4 = 20;
      byte var5 = 7;
      byte var6 = 6;
      float var7 = -5.0F;
      var0.addOrReplaceChild(
         "left_paddle",
         CubeListBuilder.create().texOffs(62, 0).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F).addBox(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F),
         PartPose.offsetAndRotation(3.0F, -5.0F, 9.0F, 0.0F, 0.0F, 0.19634955F)
      );
      var0.addOrReplaceChild(
         "right_paddle",
         CubeListBuilder.create().texOffs(62, 20).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F).addBox(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F),
         PartPose.offsetAndRotation(3.0F, -5.0F, -9.0F, 0.0F, 3.1415927F, 0.19634955F)
      );
   }

   public static LayerDefinition createBoatModel() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      addCommonParts(var1);
      return LayerDefinition.create(var0, 128, 64);
   }

   public static LayerDefinition createChestBoatModel() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      addCommonParts(var1);
      var1.addOrReplaceChild(
         "chest_bottom",
         CubeListBuilder.create().texOffs(0, 76).addBox(0.0F, 0.0F, 0.0F, 12.0F, 8.0F, 12.0F),
         PartPose.offsetAndRotation(-2.0F, -5.0F, -6.0F, 0.0F, -1.5707964F, 0.0F)
      );
      var1.addOrReplaceChild(
         "chest_lid",
         CubeListBuilder.create().texOffs(0, 59).addBox(0.0F, 0.0F, 0.0F, 12.0F, 4.0F, 12.0F),
         PartPose.offsetAndRotation(-2.0F, -9.0F, -6.0F, 0.0F, -1.5707964F, 0.0F)
      );
      var1.addOrReplaceChild(
         "chest_lock",
         CubeListBuilder.create().texOffs(0, 59).addBox(0.0F, 0.0F, 0.0F, 2.0F, 4.0F, 1.0F),
         PartPose.offsetAndRotation(-1.0F, -6.0F, -1.0F, 0.0F, -1.5707964F, 0.0F)
      );
      return LayerDefinition.create(var0, 128, 128);
   }

   public static LayerDefinition createWaterPatch() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      var1.addOrReplaceChild(
         "water_patch",
         CubeListBuilder.create().texOffs(0, 0).addBox(-14.0F, -9.0F, -3.0F, 28.0F, 16.0F, 3.0F),
         PartPose.offsetAndRotation(0.0F, -3.0F, 1.0F, 1.5707964F, 0.0F, 0.0F)
      );
      return LayerDefinition.create(var0, 0, 0);
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
