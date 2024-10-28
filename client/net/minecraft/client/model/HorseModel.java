package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.horse.AbstractHorse;

public class HorseModel<T extends AbstractHorse> extends AgeableListModel<T> {
   private static final float DEG_125 = 2.1816616F;
   private static final float DEG_60 = 1.0471976F;
   private static final float DEG_45 = 0.7853982F;
   private static final float DEG_30 = 0.5235988F;
   private static final float DEG_15 = 0.2617994F;
   protected static final String HEAD_PARTS = "head_parts";
   private static final String LEFT_HIND_BABY_LEG = "left_hind_baby_leg";
   private static final String RIGHT_HIND_BABY_LEG = "right_hind_baby_leg";
   private static final String LEFT_FRONT_BABY_LEG = "left_front_baby_leg";
   private static final String RIGHT_FRONT_BABY_LEG = "right_front_baby_leg";
   private static final String SADDLE = "saddle";
   private static final String LEFT_SADDLE_MOUTH = "left_saddle_mouth";
   private static final String LEFT_SADDLE_LINE = "left_saddle_line";
   private static final String RIGHT_SADDLE_MOUTH = "right_saddle_mouth";
   private static final String RIGHT_SADDLE_LINE = "right_saddle_line";
   private static final String HEAD_SADDLE = "head_saddle";
   private static final String MOUTH_SADDLE_WRAP = "mouth_saddle_wrap";
   protected final ModelPart body;
   protected final ModelPart headParts;
   private final ModelPart rightHindLeg;
   private final ModelPart leftHindLeg;
   private final ModelPart rightFrontLeg;
   private final ModelPart leftFrontLeg;
   private final ModelPart rightHindBabyLeg;
   private final ModelPart leftHindBabyLeg;
   private final ModelPart rightFrontBabyLeg;
   private final ModelPart leftFrontBabyLeg;
   private final ModelPart tail;
   private final ModelPart[] saddleParts;
   private final ModelPart[] ridingParts;

   public HorseModel(ModelPart var1) {
      super(true, 16.2F, 1.36F, 2.7272F, 2.0F, 20.0F);
      this.body = var1.getChild("body");
      this.headParts = var1.getChild("head_parts");
      this.rightHindLeg = var1.getChild("right_hind_leg");
      this.leftHindLeg = var1.getChild("left_hind_leg");
      this.rightFrontLeg = var1.getChild("right_front_leg");
      this.leftFrontLeg = var1.getChild("left_front_leg");
      this.rightHindBabyLeg = var1.getChild("right_hind_baby_leg");
      this.leftHindBabyLeg = var1.getChild("left_hind_baby_leg");
      this.rightFrontBabyLeg = var1.getChild("right_front_baby_leg");
      this.leftFrontBabyLeg = var1.getChild("left_front_baby_leg");
      this.tail = this.body.getChild("tail");
      ModelPart var2 = this.body.getChild("saddle");
      ModelPart var3 = this.headParts.getChild("left_saddle_mouth");
      ModelPart var4 = this.headParts.getChild("right_saddle_mouth");
      ModelPart var5 = this.headParts.getChild("left_saddle_line");
      ModelPart var6 = this.headParts.getChild("right_saddle_line");
      ModelPart var7 = this.headParts.getChild("head_saddle");
      ModelPart var8 = this.headParts.getChild("mouth_saddle_wrap");
      this.saddleParts = new ModelPart[]{var2, var3, var4, var7, var8};
      this.ridingParts = new ModelPart[]{var5, var6};
   }

   public static MeshDefinition createBodyMesh(CubeDeformation var0) {
      MeshDefinition var1 = new MeshDefinition();
      PartDefinition var2 = var1.getRoot();
      PartDefinition var3 = var2.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 32).addBox(-5.0F, -8.0F, -17.0F, 10.0F, 10.0F, 22.0F, new CubeDeformation(0.05F)), PartPose.offset(0.0F, 11.0F, 5.0F));
      PartDefinition var4 = var2.addOrReplaceChild("head_parts", CubeListBuilder.create().texOffs(0, 35).addBox(-2.05F, -6.0F, -2.0F, 4.0F, 12.0F, 7.0F), PartPose.offsetAndRotation(0.0F, 4.0F, -12.0F, 0.5235988F, 0.0F, 0.0F));
      PartDefinition var5 = var4.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 13).addBox(-3.0F, -11.0F, -2.0F, 6.0F, 5.0F, 7.0F, var0), PartPose.ZERO);
      var4.addOrReplaceChild("mane", CubeListBuilder.create().texOffs(56, 36).addBox(-1.0F, -11.0F, 5.01F, 2.0F, 16.0F, 2.0F, var0), PartPose.ZERO);
      var4.addOrReplaceChild("upper_mouth", CubeListBuilder.create().texOffs(0, 25).addBox(-2.0F, -11.0F, -7.0F, 4.0F, 5.0F, 5.0F, var0), PartPose.ZERO);
      var2.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(48, 21).mirror().addBox(-3.0F, -1.01F, -1.0F, 4.0F, 11.0F, 4.0F, var0), PartPose.offset(4.0F, 14.0F, 7.0F));
      var2.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(48, 21).addBox(-1.0F, -1.01F, -1.0F, 4.0F, 11.0F, 4.0F, var0), PartPose.offset(-4.0F, 14.0F, 7.0F));
      var2.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(48, 21).mirror().addBox(-3.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, var0), PartPose.offset(4.0F, 14.0F, -12.0F));
      var2.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(48, 21).addBox(-1.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, var0), PartPose.offset(-4.0F, 14.0F, -12.0F));
      CubeDeformation var6 = var0.extend(0.0F, 5.5F, 0.0F);
      var2.addOrReplaceChild("left_hind_baby_leg", CubeListBuilder.create().texOffs(48, 21).mirror().addBox(-3.0F, -1.01F, -1.0F, 4.0F, 11.0F, 4.0F, var6), PartPose.offset(4.0F, 14.0F, 7.0F));
      var2.addOrReplaceChild("right_hind_baby_leg", CubeListBuilder.create().texOffs(48, 21).addBox(-1.0F, -1.01F, -1.0F, 4.0F, 11.0F, 4.0F, var6), PartPose.offset(-4.0F, 14.0F, 7.0F));
      var2.addOrReplaceChild("left_front_baby_leg", CubeListBuilder.create().texOffs(48, 21).mirror().addBox(-3.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, var6), PartPose.offset(4.0F, 14.0F, -12.0F));
      var2.addOrReplaceChild("right_front_baby_leg", CubeListBuilder.create().texOffs(48, 21).addBox(-1.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, var6), PartPose.offset(-4.0F, 14.0F, -12.0F));
      var3.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(42, 36).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 14.0F, 4.0F, var0), PartPose.offsetAndRotation(0.0F, -5.0F, 2.0F, 0.5235988F, 0.0F, 0.0F));
      var3.addOrReplaceChild("saddle", CubeListBuilder.create().texOffs(26, 0).addBox(-5.0F, -8.0F, -9.0F, 10.0F, 9.0F, 9.0F, new CubeDeformation(0.5F)), PartPose.ZERO);
      var4.addOrReplaceChild("left_saddle_mouth", CubeListBuilder.create().texOffs(29, 5).addBox(2.0F, -9.0F, -6.0F, 1.0F, 2.0F, 2.0F, var0), PartPose.ZERO);
      var4.addOrReplaceChild("right_saddle_mouth", CubeListBuilder.create().texOffs(29, 5).addBox(-3.0F, -9.0F, -6.0F, 1.0F, 2.0F, 2.0F, var0), PartPose.ZERO);
      var4.addOrReplaceChild("left_saddle_line", CubeListBuilder.create().texOffs(32, 2).addBox(3.1F, -6.0F, -8.0F, 0.0F, 3.0F, 16.0F), PartPose.rotation(-0.5235988F, 0.0F, 0.0F));
      var4.addOrReplaceChild("right_saddle_line", CubeListBuilder.create().texOffs(32, 2).addBox(-3.1F, -6.0F, -8.0F, 0.0F, 3.0F, 16.0F), PartPose.rotation(-0.5235988F, 0.0F, 0.0F));
      var4.addOrReplaceChild("head_saddle", CubeListBuilder.create().texOffs(1, 1).addBox(-3.0F, -11.0F, -1.9F, 6.0F, 5.0F, 6.0F, new CubeDeformation(0.22F)), PartPose.ZERO);
      var4.addOrReplaceChild("mouth_saddle_wrap", CubeListBuilder.create().texOffs(19, 0).addBox(-2.0F, -11.0F, -4.0F, 4.0F, 5.0F, 2.0F, new CubeDeformation(0.2F)), PartPose.ZERO);
      var5.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(19, 16).addBox(0.55F, -13.0F, 4.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(-0.001F)), PartPose.ZERO);
      var5.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(19, 16).addBox(-2.55F, -13.0F, 4.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(-0.001F)), PartPose.ZERO);
      return var1;
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6) {
      boolean var7 = var1.isSaddled();
      boolean var8 = var1.isVehicle();
      ModelPart[] var9 = this.saddleParts;
      int var10 = var9.length;

      int var11;
      ModelPart var12;
      for(var11 = 0; var11 < var10; ++var11) {
         var12 = var9[var11];
         var12.visible = var7;
      }

      var9 = this.ridingParts;
      var10 = var9.length;

      for(var11 = 0; var11 < var10; ++var11) {
         var12 = var9[var11];
         var12.visible = var8 && var7;
      }

      this.body.y = 11.0F;
   }

   public Iterable<ModelPart> headParts() {
      return ImmutableList.of(this.headParts);
   }

   protected Iterable<ModelPart> bodyParts() {
      return ImmutableList.of(this.body, this.rightHindLeg, this.leftHindLeg, this.rightFrontLeg, this.leftFrontLeg, this.rightHindBabyLeg, this.leftHindBabyLeg, this.rightFrontBabyLeg, this.leftFrontBabyLeg);
   }

   public void prepareMobModel(T var1, float var2, float var3, float var4) {
      super.prepareMobModel(var1, var2, var3, var4);
      float var5 = Mth.rotLerp(var4, var1.yBodyRotO, var1.yBodyRot);
      float var6 = Mth.rotLerp(var4, var1.yHeadRotO, var1.yHeadRot);
      float var7 = Mth.lerp(var4, var1.xRotO, var1.getXRot());
      float var8 = var6 - var5;
      float var9 = var7 * 0.017453292F;
      if (var8 > 20.0F) {
         var8 = 20.0F;
      }

      if (var8 < -20.0F) {
         var8 = -20.0F;
      }

      if (var3 > 0.2F) {
         var9 += Mth.cos(var2 * 0.8F) * 0.15F * var3;
      }

      float var10 = var1.getEatAnim(var4);
      float var11 = var1.getStandAnim(var4);
      float var12 = 1.0F - var11;
      float var13 = var1.getMouthAnim(var4);
      boolean var14 = var1.tailCounter != 0;
      float var15 = (float)var1.tickCount + var4;
      this.headParts.y = 4.0F;
      this.headParts.z = -12.0F;
      this.body.xRot = 0.0F;
      this.headParts.xRot = 0.5235988F + var9;
      this.headParts.yRot = var8 * 0.017453292F;
      float var16 = var1.isInWater() ? 0.2F : 1.0F;
      float var17 = Mth.cos(var16 * var2 * 0.6662F + 3.1415927F);
      float var18 = var17 * 0.8F * var3;
      float var19 = (1.0F - Math.max(var11, var10)) * (0.5235988F + var9 + var13 * Mth.sin(var15) * 0.05F);
      this.headParts.xRot = var11 * (0.2617994F + var9) + var10 * (2.1816616F + Mth.sin(var15) * 0.05F) + var19;
      this.headParts.yRot = var11 * var8 * 0.017453292F + (1.0F - Math.max(var11, var10)) * this.headParts.yRot;
      this.headParts.y = var11 * -4.0F + var10 * 11.0F + (1.0F - Math.max(var11, var10)) * this.headParts.y;
      this.headParts.z = var11 * -4.0F + var10 * -12.0F + (1.0F - Math.max(var11, var10)) * this.headParts.z;
      this.body.xRot = var11 * -0.7853982F + var12 * this.body.xRot;
      float var20 = 0.2617994F * var11;
      float var21 = Mth.cos(var15 * 0.6F + 3.1415927F);
      this.leftFrontLeg.y = 2.0F * var11 + 14.0F * var12;
      this.leftFrontLeg.z = -6.0F * var11 - 10.0F * var12;
      this.rightFrontLeg.y = this.leftFrontLeg.y;
      this.rightFrontLeg.z = this.leftFrontLeg.z;
      float var22 = (-1.0471976F + var21) * var11 + var18 * var12;
      float var23 = (-1.0471976F - var21) * var11 - var18 * var12;
      this.leftHindLeg.xRot = var20 - var17 * 0.5F * var3 * var12;
      this.rightHindLeg.xRot = var20 + var17 * 0.5F * var3 * var12;
      this.leftFrontLeg.xRot = var22;
      this.rightFrontLeg.xRot = var23;
      this.tail.xRot = 0.5235988F + var3 * 0.75F;
      this.tail.y = -5.0F + var3;
      this.tail.z = 2.0F + var3 * 2.0F;
      if (var14) {
         this.tail.yRot = Mth.cos(var15 * 0.7F);
      } else {
         this.tail.yRot = 0.0F;
      }

      this.rightHindBabyLeg.y = this.rightHindLeg.y;
      this.rightHindBabyLeg.z = this.rightHindLeg.z;
      this.rightHindBabyLeg.xRot = this.rightHindLeg.xRot;
      this.leftHindBabyLeg.y = this.leftHindLeg.y;
      this.leftHindBabyLeg.z = this.leftHindLeg.z;
      this.leftHindBabyLeg.xRot = this.leftHindLeg.xRot;
      this.rightFrontBabyLeg.y = this.rightFrontLeg.y;
      this.rightFrontBabyLeg.z = this.rightFrontLeg.z;
      this.rightFrontBabyLeg.xRot = this.rightFrontLeg.xRot;
      this.leftFrontBabyLeg.y = this.leftFrontLeg.y;
      this.leftFrontBabyLeg.z = this.leftFrontLeg.z;
      this.leftFrontBabyLeg.xRot = this.leftFrontLeg.xRot;
      boolean var24 = var1.isBaby();
      this.rightHindLeg.visible = !var24;
      this.leftHindLeg.visible = !var24;
      this.rightFrontLeg.visible = !var24;
      this.leftFrontLeg.visible = !var24;
      this.rightHindBabyLeg.visible = var24;
      this.leftHindBabyLeg.visible = var24;
      this.rightFrontBabyLeg.visible = var24;
      this.leftFrontBabyLeg.visible = var24;
      this.body.y = var24 ? 10.8F : 0.0F;
   }
}
