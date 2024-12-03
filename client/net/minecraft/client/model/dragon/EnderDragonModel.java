package net.minecraft.client.model.dragon;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.EnderDragonRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.boss.enderdragon.DragonFlightHistory;

public class EnderDragonModel extends EntityModel<EnderDragonRenderState> {
   private static final int NECK_PART_COUNT = 5;
   private static final int TAIL_PART_COUNT = 12;
   private final ModelPart head;
   private final ModelPart[] neckParts = new ModelPart[5];
   private final ModelPart[] tailParts = new ModelPart[12];
   private final ModelPart jaw;
   private final ModelPart body;
   private final ModelPart leftWing;
   private final ModelPart leftWingTip;
   private final ModelPart leftFrontLeg;
   private final ModelPart leftFrontLegTip;
   private final ModelPart leftFrontFoot;
   private final ModelPart leftRearLeg;
   private final ModelPart leftRearLegTip;
   private final ModelPart leftRearFoot;
   private final ModelPart rightWing;
   private final ModelPart rightWingTip;
   private final ModelPart rightFrontLeg;
   private final ModelPart rightFrontLegTip;
   private final ModelPart rightFrontFoot;
   private final ModelPart rightRearLeg;
   private final ModelPart rightRearLegTip;
   private final ModelPart rightRearFoot;

   private static String neckName(int var0) {
      return "neck" + var0;
   }

   private static String tailName(int var0) {
      return "tail" + var0;
   }

   public EnderDragonModel(ModelPart var1) {
      super(var1);
      this.head = var1.getChild("head");
      this.jaw = this.head.getChild("jaw");

      for(int var2 = 0; var2 < this.neckParts.length; ++var2) {
         this.neckParts[var2] = var1.getChild(neckName(var2));
      }

      for(int var3 = 0; var3 < this.tailParts.length; ++var3) {
         this.tailParts[var3] = var1.getChild(tailName(var3));
      }

      this.body = var1.getChild("body");
      this.leftWing = this.body.getChild("left_wing");
      this.leftWingTip = this.leftWing.getChild("left_wing_tip");
      this.leftFrontLeg = this.body.getChild("left_front_leg");
      this.leftFrontLegTip = this.leftFrontLeg.getChild("left_front_leg_tip");
      this.leftFrontFoot = this.leftFrontLegTip.getChild("left_front_foot");
      this.leftRearLeg = this.body.getChild("left_hind_leg");
      this.leftRearLegTip = this.leftRearLeg.getChild("left_hind_leg_tip");
      this.leftRearFoot = this.leftRearLegTip.getChild("left_hind_foot");
      this.rightWing = this.body.getChild("right_wing");
      this.rightWingTip = this.rightWing.getChild("right_wing_tip");
      this.rightFrontLeg = this.body.getChild("right_front_leg");
      this.rightFrontLegTip = this.rightFrontLeg.getChild("right_front_leg_tip");
      this.rightFrontFoot = this.rightFrontLegTip.getChild("right_front_foot");
      this.rightRearLeg = this.body.getChild("right_hind_leg");
      this.rightRearLegTip = this.rightRearLeg.getChild("right_hind_leg_tip");
      this.rightRearFoot = this.rightRearLegTip.getChild("right_hind_foot");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      float var2 = -16.0F;
      PartDefinition var3 = var1.addOrReplaceChild("head", CubeListBuilder.create().addBox("upperlip", -6.0F, -1.0F, -24.0F, 12, 5, 16, 176, 44).addBox("upperhead", -8.0F, -8.0F, -10.0F, 16, 16, 16, 112, 30).mirror().addBox("scale", -5.0F, -12.0F, -4.0F, 2, 4, 6, 0, 0).addBox("nostril", -5.0F, -3.0F, -22.0F, 2, 2, 4, 112, 0).mirror().addBox("scale", 3.0F, -12.0F, -4.0F, 2, 4, 6, 0, 0).addBox("nostril", 3.0F, -3.0F, -22.0F, 2, 2, 4, 112, 0), PartPose.offset(0.0F, 20.0F, -62.0F));
      var3.addOrReplaceChild("jaw", CubeListBuilder.create().addBox("jaw", -6.0F, 0.0F, -16.0F, 12, 4, 16, 176, 65), PartPose.offset(0.0F, 4.0F, -8.0F));
      CubeListBuilder var4 = CubeListBuilder.create().addBox("box", -5.0F, -5.0F, -5.0F, 10, 10, 10, 192, 104).addBox("scale", -1.0F, -9.0F, -3.0F, 2, 4, 6, 48, 0);

      for(int var5 = 0; var5 < 5; ++var5) {
         var1.addOrReplaceChild(neckName(var5), var4, PartPose.offset(0.0F, 20.0F, -12.0F - (float)var5 * 10.0F));
      }

      for(int var16 = 0; var16 < 12; ++var16) {
         var1.addOrReplaceChild(tailName(var16), var4, PartPose.offset(0.0F, 10.0F, 60.0F + (float)var16 * 10.0F));
      }

      PartDefinition var17 = var1.addOrReplaceChild("body", CubeListBuilder.create().addBox("body", -12.0F, 1.0F, -16.0F, 24, 24, 64, 0, 0).addBox("scale", -1.0F, -5.0F, -10.0F, 2, 6, 12, 220, 53).addBox("scale", -1.0F, -5.0F, 10.0F, 2, 6, 12, 220, 53).addBox("scale", -1.0F, -5.0F, 30.0F, 2, 6, 12, 220, 53), PartPose.offset(0.0F, 3.0F, 8.0F));
      PartDefinition var6 = var17.addOrReplaceChild("left_wing", CubeListBuilder.create().mirror().addBox("bone", 0.0F, -4.0F, -4.0F, 56, 8, 8, 112, 88).addBox("skin", 0.0F, 0.0F, 2.0F, 56, 0, 56, -56, 88), PartPose.offset(12.0F, 2.0F, -6.0F));
      var6.addOrReplaceChild("left_wing_tip", CubeListBuilder.create().mirror().addBox("bone", 0.0F, -2.0F, -2.0F, 56, 4, 4, 112, 136).addBox("skin", 0.0F, 0.0F, 2.0F, 56, 0, 56, -56, 144), PartPose.offset(56.0F, 0.0F, 0.0F));
      PartDefinition var7 = var17.addOrReplaceChild("left_front_leg", CubeListBuilder.create().addBox("main", -4.0F, -4.0F, -4.0F, 8, 24, 8, 112, 104), PartPose.offsetAndRotation(12.0F, 17.0F, -6.0F, 1.3F, 0.0F, 0.0F));
      PartDefinition var8 = var7.addOrReplaceChild("left_front_leg_tip", CubeListBuilder.create().addBox("main", -3.0F, -1.0F, -3.0F, 6, 24, 6, 226, 138), PartPose.offsetAndRotation(0.0F, 20.0F, -1.0F, -0.5F, 0.0F, 0.0F));
      var8.addOrReplaceChild("left_front_foot", CubeListBuilder.create().addBox("main", -4.0F, 0.0F, -12.0F, 8, 4, 16, 144, 104), PartPose.offsetAndRotation(0.0F, 23.0F, 0.0F, 0.75F, 0.0F, 0.0F));
      PartDefinition var9 = var17.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().addBox("main", -8.0F, -4.0F, -8.0F, 16, 32, 16, 0, 0), PartPose.offsetAndRotation(16.0F, 13.0F, 34.0F, 1.0F, 0.0F, 0.0F));
      PartDefinition var10 = var9.addOrReplaceChild("left_hind_leg_tip", CubeListBuilder.create().addBox("main", -6.0F, -2.0F, 0.0F, 12, 32, 12, 196, 0), PartPose.offsetAndRotation(0.0F, 32.0F, -4.0F, 0.5F, 0.0F, 0.0F));
      var10.addOrReplaceChild("left_hind_foot", CubeListBuilder.create().addBox("main", -9.0F, 0.0F, -20.0F, 18, 6, 24, 112, 0), PartPose.offsetAndRotation(0.0F, 31.0F, 4.0F, 0.75F, 0.0F, 0.0F));
      PartDefinition var11 = var17.addOrReplaceChild("right_wing", CubeListBuilder.create().addBox("bone", -56.0F, -4.0F, -4.0F, 56, 8, 8, 112, 88).addBox("skin", -56.0F, 0.0F, 2.0F, 56, 0, 56, -56, 88), PartPose.offset(-12.0F, 2.0F, -6.0F));
      var11.addOrReplaceChild("right_wing_tip", CubeListBuilder.create().addBox("bone", -56.0F, -2.0F, -2.0F, 56, 4, 4, 112, 136).addBox("skin", -56.0F, 0.0F, 2.0F, 56, 0, 56, -56, 144), PartPose.offset(-56.0F, 0.0F, 0.0F));
      PartDefinition var12 = var17.addOrReplaceChild("right_front_leg", CubeListBuilder.create().addBox("main", -4.0F, -4.0F, -4.0F, 8, 24, 8, 112, 104), PartPose.offsetAndRotation(-12.0F, 17.0F, -6.0F, 1.3F, 0.0F, 0.0F));
      PartDefinition var13 = var12.addOrReplaceChild("right_front_leg_tip", CubeListBuilder.create().addBox("main", -3.0F, -1.0F, -3.0F, 6, 24, 6, 226, 138), PartPose.offsetAndRotation(0.0F, 20.0F, -1.0F, -0.5F, 0.0F, 0.0F));
      var13.addOrReplaceChild("right_front_foot", CubeListBuilder.create().addBox("main", -4.0F, 0.0F, -12.0F, 8, 4, 16, 144, 104), PartPose.offsetAndRotation(0.0F, 23.0F, 0.0F, 0.75F, 0.0F, 0.0F));
      PartDefinition var14 = var17.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().addBox("main", -8.0F, -4.0F, -8.0F, 16, 32, 16, 0, 0), PartPose.offsetAndRotation(-16.0F, 13.0F, 34.0F, 1.0F, 0.0F, 0.0F));
      PartDefinition var15 = var14.addOrReplaceChild("right_hind_leg_tip", CubeListBuilder.create().addBox("main", -6.0F, -2.0F, 0.0F, 12, 32, 12, 196, 0), PartPose.offsetAndRotation(0.0F, 32.0F, -4.0F, 0.5F, 0.0F, 0.0F));
      var15.addOrReplaceChild("right_hind_foot", CubeListBuilder.create().addBox("main", -9.0F, 0.0F, -20.0F, 18, 6, 24, 112, 0), PartPose.offsetAndRotation(0.0F, 31.0F, 4.0F, 0.75F, 0.0F, 0.0F));
      return LayerDefinition.create(var0, 256, 256);
   }

   public void setupAnim(EnderDragonRenderState var1) {
      super.setupAnim(var1);
      float var2 = var1.flapTime * 6.2831855F;
      this.jaw.xRot = (Mth.sin(var2) + 1.0F) * 0.2F;
      float var3 = Mth.sin(var2 - 1.0F) + 1.0F;
      var3 = (var3 * var3 + var3 * 2.0F) * 0.05F;
      this.root.y = (var3 - 2.0F) * 16.0F;
      this.root.z = -48.0F;
      this.root.xRot = var3 * 2.0F * 0.017453292F;
      float var4 = this.neckParts[0].x;
      float var5 = this.neckParts[0].y;
      float var6 = this.neckParts[0].z;
      float var7 = 1.5F;
      DragonFlightHistory.Sample var8 = var1.getHistoricalPos(6);
      float var9 = Mth.wrapDegrees(var1.getHistoricalPos(5).yRot() - var1.getHistoricalPos(10).yRot());
      float var10 = Mth.wrapDegrees(var1.getHistoricalPos(5).yRot() + var9 / 2.0F);

      for(int var11 = 0; var11 < 5; ++var11) {
         ModelPart var12 = this.neckParts[var11];
         DragonFlightHistory.Sample var13 = var1.getHistoricalPos(5 - var11);
         float var14 = Mth.cos((float)var11 * 0.45F + var2) * 0.15F;
         var12.yRot = Mth.wrapDegrees(var13.yRot() - var8.yRot()) * 0.017453292F * 1.5F;
         var12.xRot = var14 + var1.getHeadPartYOffset(var11, var8, var13) * 0.017453292F * 1.5F * 5.0F;
         var12.zRot = -Mth.wrapDegrees(var13.yRot() - var10) * 0.017453292F * 1.5F;
         var12.y = var5;
         var12.z = var6;
         var12.x = var4;
         var4 -= Mth.sin(var12.yRot) * Mth.cos(var12.xRot) * 10.0F;
         var5 += Mth.sin(var12.xRot) * 10.0F;
         var6 -= Mth.cos(var12.yRot) * Mth.cos(var12.xRot) * 10.0F;
      }

      this.head.y = var5;
      this.head.z = var6;
      this.head.x = var4;
      DragonFlightHistory.Sample var21 = var1.getHistoricalPos(0);
      this.head.yRot = Mth.wrapDegrees(var21.yRot() - var8.yRot()) * 0.017453292F;
      this.head.xRot = Mth.wrapDegrees(var1.getHeadPartYOffset(6, var8, var21)) * 0.017453292F * 1.5F * 5.0F;
      this.head.zRot = -Mth.wrapDegrees(var21.yRot() - var10) * 0.017453292F;
      this.body.zRot = -var9 * 1.5F * 0.017453292F;
      this.leftWing.xRot = 0.125F - Mth.cos(var2) * 0.2F;
      this.leftWing.yRot = -0.25F;
      this.leftWing.zRot = -(Mth.sin(var2) + 0.125F) * 0.8F;
      this.leftWingTip.zRot = (Mth.sin(var2 + 2.0F) + 0.5F) * 0.75F;
      this.rightWing.xRot = this.leftWing.xRot;
      this.rightWing.yRot = -this.leftWing.yRot;
      this.rightWing.zRot = -this.leftWing.zRot;
      this.rightWingTip.zRot = -this.leftWingTip.zRot;
      this.poseLimbs(var3, this.leftFrontLeg, this.leftFrontLegTip, this.leftFrontFoot, this.leftRearLeg, this.leftRearLegTip, this.leftRearFoot);
      this.poseLimbs(var3, this.rightFrontLeg, this.rightFrontLegTip, this.rightFrontFoot, this.rightRearLeg, this.rightRearLegTip, this.rightRearFoot);
      float var22 = 0.0F;
      var5 = this.tailParts[0].y;
      var6 = this.tailParts[0].z;
      var4 = this.tailParts[0].x;
      var8 = var1.getHistoricalPos(11);

      for(int var23 = 0; var23 < 12; ++var23) {
         DragonFlightHistory.Sample var24 = var1.getHistoricalPos(12 + var23);
         var22 += Mth.sin((float)var23 * 0.45F + var2) * 0.05F;
         ModelPart var15 = this.tailParts[var23];
         var15.yRot = (Mth.wrapDegrees(var24.yRot() - var8.yRot()) * 1.5F + 180.0F) * 0.017453292F;
         var15.xRot = var22 + (float)(var24.y() - var8.y()) * 0.017453292F * 1.5F * 5.0F;
         var15.zRot = Mth.wrapDegrees(var24.yRot() - var10) * 0.017453292F * 1.5F;
         var15.y = var5;
         var15.z = var6;
         var15.x = var4;
         var5 += Mth.sin(var15.xRot) * 10.0F;
         var6 -= Mth.cos(var15.yRot) * Mth.cos(var15.xRot) * 10.0F;
         var4 -= Mth.sin(var15.yRot) * Mth.cos(var15.xRot) * 10.0F;
      }

   }

   private void poseLimbs(float var1, ModelPart var2, ModelPart var3, ModelPart var4, ModelPart var5, ModelPart var6, ModelPart var7) {
      var5.xRot = 1.0F + var1 * 0.1F;
      var6.xRot = 0.5F + var1 * 0.1F;
      var7.xRot = 0.75F + var1 * 0.1F;
      var2.xRot = 1.3F + var1 * 0.1F;
      var3.xRot = -0.5F - var1 * 0.1F;
      var4.xRot = 0.75F + var1 * 0.1F;
   }
}
