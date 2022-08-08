package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Bee;

public class BeeModel<T extends Bee> extends AgeableListModel<T> {
   private static final float BEE_Y_BASE = 19.0F;
   private static final String BONE = "bone";
   private static final String STINGER = "stinger";
   private static final String LEFT_ANTENNA = "left_antenna";
   private static final String RIGHT_ANTENNA = "right_antenna";
   private static final String FRONT_LEGS = "front_legs";
   private static final String MIDDLE_LEGS = "middle_legs";
   private static final String BACK_LEGS = "back_legs";
   private final ModelPart bone;
   private final ModelPart rightWing;
   private final ModelPart leftWing;
   private final ModelPart frontLeg;
   private final ModelPart midLeg;
   private final ModelPart backLeg;
   private final ModelPart stinger;
   private final ModelPart leftAntenna;
   private final ModelPart rightAntenna;
   private float rollAmount;

   public BeeModel(ModelPart var1) {
      super(false, 24.0F, 0.0F);
      this.bone = var1.getChild("bone");
      ModelPart var2 = this.bone.getChild("body");
      this.stinger = var2.getChild("stinger");
      this.leftAntenna = var2.getChild("left_antenna");
      this.rightAntenna = var2.getChild("right_antenna");
      this.rightWing = this.bone.getChild("right_wing");
      this.leftWing = this.bone.getChild("left_wing");
      this.frontLeg = this.bone.getChild("front_legs");
      this.midLeg = this.bone.getChild("middle_legs");
      this.backLeg = this.bone.getChild("back_legs");
   }

   public static LayerDefinition createBodyLayer() {
      float var0 = 19.0F;
      MeshDefinition var1 = new MeshDefinition();
      PartDefinition var2 = var1.getRoot();
      PartDefinition var3 = var2.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offset(0.0F, 19.0F, 0.0F));
      PartDefinition var4 = var3.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 10.0F), PartPose.ZERO);
      var4.addOrReplaceChild("stinger", CubeListBuilder.create().texOffs(26, 7).addBox(0.0F, -1.0F, 5.0F, 0.0F, 1.0F, 2.0F), PartPose.ZERO);
      var4.addOrReplaceChild("left_antenna", CubeListBuilder.create().texOffs(2, 0).addBox(1.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F), PartPose.offset(0.0F, -2.0F, -5.0F));
      var4.addOrReplaceChild("right_antenna", CubeListBuilder.create().texOffs(2, 3).addBox(-2.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F), PartPose.offset(0.0F, -2.0F, -5.0F));
      CubeDeformation var5 = new CubeDeformation(0.001F);
      var3.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(0, 18).addBox(-9.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, var5), PartPose.offsetAndRotation(-1.5F, -4.0F, -3.0F, 0.0F, -0.2618F, 0.0F));
      var3.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(0, 18).mirror().addBox(0.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, var5), PartPose.offsetAndRotation(1.5F, -4.0F, -3.0F, 0.0F, 0.2618F, 0.0F));
      var3.addOrReplaceChild("front_legs", CubeListBuilder.create().addBox("front_legs", -5.0F, 0.0F, 0.0F, 7, 2, 0, 26, 1), PartPose.offset(1.5F, 3.0F, -2.0F));
      var3.addOrReplaceChild("middle_legs", CubeListBuilder.create().addBox("middle_legs", -5.0F, 0.0F, 0.0F, 7, 2, 0, 26, 3), PartPose.offset(1.5F, 3.0F, 0.0F));
      var3.addOrReplaceChild("back_legs", CubeListBuilder.create().addBox("back_legs", -5.0F, 0.0F, 0.0F, 7, 2, 0, 26, 5), PartPose.offset(1.5F, 3.0F, 2.0F));
      return LayerDefinition.create(var1, 64, 64);
   }

   public void prepareMobModel(T var1, float var2, float var3, float var4) {
      super.prepareMobModel(var1, var2, var3, var4);
      this.rollAmount = var1.getRollAmount(var4);
      this.stinger.visible = !var1.hasStung();
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6) {
      this.rightWing.xRot = 0.0F;
      this.leftAntenna.xRot = 0.0F;
      this.rightAntenna.xRot = 0.0F;
      this.bone.xRot = 0.0F;
      boolean var7 = var1.isOnGround() && var1.getDeltaMovement().lengthSqr() < 1.0E-7;
      float var8;
      if (var7) {
         this.rightWing.yRot = -0.2618F;
         this.rightWing.zRot = 0.0F;
         this.leftWing.xRot = 0.0F;
         this.leftWing.yRot = 0.2618F;
         this.leftWing.zRot = 0.0F;
         this.frontLeg.xRot = 0.0F;
         this.midLeg.xRot = 0.0F;
         this.backLeg.xRot = 0.0F;
      } else {
         var8 = var4 * 120.32113F * 0.017453292F;
         this.rightWing.yRot = 0.0F;
         this.rightWing.zRot = Mth.cos(var8) * 3.1415927F * 0.15F;
         this.leftWing.xRot = this.rightWing.xRot;
         this.leftWing.yRot = this.rightWing.yRot;
         this.leftWing.zRot = -this.rightWing.zRot;
         this.frontLeg.xRot = 0.7853982F;
         this.midLeg.xRot = 0.7853982F;
         this.backLeg.xRot = 0.7853982F;
         this.bone.xRot = 0.0F;
         this.bone.yRot = 0.0F;
         this.bone.zRot = 0.0F;
      }

      if (!var1.isAngry()) {
         this.bone.xRot = 0.0F;
         this.bone.yRot = 0.0F;
         this.bone.zRot = 0.0F;
         if (!var7) {
            var8 = Mth.cos(var4 * 0.18F);
            this.bone.xRot = 0.1F + var8 * 3.1415927F * 0.025F;
            this.leftAntenna.xRot = var8 * 3.1415927F * 0.03F;
            this.rightAntenna.xRot = var8 * 3.1415927F * 0.03F;
            this.frontLeg.xRot = -var8 * 3.1415927F * 0.1F + 0.3926991F;
            this.backLeg.xRot = -var8 * 3.1415927F * 0.05F + 0.7853982F;
            this.bone.y = 19.0F - Mth.cos(var4 * 0.18F) * 0.9F;
         }
      }

      if (this.rollAmount > 0.0F) {
         this.bone.xRot = ModelUtils.rotlerpRad(this.bone.xRot, 3.0915928F, this.rollAmount);
      }

   }

   protected Iterable<ModelPart> headParts() {
      return ImmutableList.of();
   }

   protected Iterable<ModelPart> bodyParts() {
      return ImmutableList.of(this.bone);
   }
}
