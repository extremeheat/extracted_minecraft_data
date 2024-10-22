package net.minecraft.client.model;

import java.util.Set;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.EquineRenderState;
import net.minecraft.util.Mth;

public abstract class AbstractEquineModel<T extends EquineRenderState> extends EntityModel<T> {
   private static final float DEG_125 = 2.1816616F;
   private static final float DEG_60 = 1.0471976F;
   private static final float DEG_45 = 0.7853982F;
   private static final float DEG_30 = 0.5235988F;
   private static final float DEG_15 = 0.2617994F;
   protected static final String HEAD_PARTS = "head_parts";
   private static final String SADDLE = "saddle";
   private static final String LEFT_SADDLE_MOUTH = "left_saddle_mouth";
   private static final String LEFT_SADDLE_LINE = "left_saddle_line";
   private static final String RIGHT_SADDLE_MOUTH = "right_saddle_mouth";
   private static final String RIGHT_SADDLE_LINE = "right_saddle_line";
   private static final String HEAD_SADDLE = "head_saddle";
   private static final String MOUTH_SADDLE_WRAP = "mouth_saddle_wrap";
   protected static final MeshTransformer BABY_TRANSFORMER = new BabyModelTransform(true, 16.2F, 1.36F, 2.7272F, 2.0F, 20.0F, Set.of("head_parts"));
   protected final ModelPart body;
   protected final ModelPart headParts;
   private final ModelPart rightHindLeg;
   private final ModelPart leftHindLeg;
   private final ModelPart rightFrontLeg;
   private final ModelPart leftFrontLeg;
   private final ModelPart tail;
   private final ModelPart[] saddleParts;
   private final ModelPart[] ridingParts;

   public AbstractEquineModel(ModelPart var1) {
      super(var1);
      this.body = var1.getChild("body");
      this.headParts = var1.getChild("head_parts");
      this.rightHindLeg = var1.getChild("right_hind_leg");
      this.leftHindLeg = var1.getChild("left_hind_leg");
      this.rightFrontLeg = var1.getChild("right_front_leg");
      this.leftFrontLeg = var1.getChild("left_front_leg");
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
      PartDefinition var3 = var2.addOrReplaceChild(
         "body",
         CubeListBuilder.create().texOffs(0, 32).addBox(-5.0F, -8.0F, -17.0F, 10.0F, 10.0F, 22.0F, new CubeDeformation(0.05F)),
         PartPose.offset(0.0F, 11.0F, 5.0F)
      );
      PartDefinition var4 = var2.addOrReplaceChild(
         "head_parts",
         CubeListBuilder.create().texOffs(0, 35).addBox(-2.05F, -6.0F, -2.0F, 4.0F, 12.0F, 7.0F),
         PartPose.offsetAndRotation(0.0F, 4.0F, -12.0F, 0.5235988F, 0.0F, 0.0F)
      );
      PartDefinition var5 = var4.addOrReplaceChild(
         "head", CubeListBuilder.create().texOffs(0, 13).addBox(-3.0F, -11.0F, -2.0F, 6.0F, 5.0F, 7.0F, var0), PartPose.ZERO
      );
      var4.addOrReplaceChild("mane", CubeListBuilder.create().texOffs(56, 36).addBox(-1.0F, -11.0F, 5.01F, 2.0F, 16.0F, 2.0F, var0), PartPose.ZERO);
      var4.addOrReplaceChild("upper_mouth", CubeListBuilder.create().texOffs(0, 25).addBox(-2.0F, -11.0F, -7.0F, 4.0F, 5.0F, 5.0F, var0), PartPose.ZERO);
      var2.addOrReplaceChild(
         "left_hind_leg",
         CubeListBuilder.create().texOffs(48, 21).mirror().addBox(-3.0F, -1.01F, -1.0F, 4.0F, 11.0F, 4.0F, var0),
         PartPose.offset(4.0F, 14.0F, 7.0F)
      );
      var2.addOrReplaceChild(
         "right_hind_leg", CubeListBuilder.create().texOffs(48, 21).addBox(-1.0F, -1.01F, -1.0F, 4.0F, 11.0F, 4.0F, var0), PartPose.offset(-4.0F, 14.0F, 7.0F)
      );
      var2.addOrReplaceChild(
         "left_front_leg",
         CubeListBuilder.create().texOffs(48, 21).mirror().addBox(-3.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, var0),
         PartPose.offset(4.0F, 14.0F, -10.0F)
      );
      var2.addOrReplaceChild(
         "right_front_leg",
         CubeListBuilder.create().texOffs(48, 21).addBox(-1.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, var0),
         PartPose.offset(-4.0F, 14.0F, -10.0F)
      );
      var3.addOrReplaceChild(
         "tail",
         CubeListBuilder.create().texOffs(42, 36).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 14.0F, 4.0F, var0),
         PartPose.offsetAndRotation(0.0F, -5.0F, 2.0F, 0.5235988F, 0.0F, 0.0F)
      );
      var3.addOrReplaceChild(
         "saddle", CubeListBuilder.create().texOffs(26, 0).addBox(-5.0F, -8.0F, -9.0F, 10.0F, 9.0F, 9.0F, new CubeDeformation(0.5F)), PartPose.ZERO
      );
      var4.addOrReplaceChild("left_saddle_mouth", CubeListBuilder.create().texOffs(29, 5).addBox(2.0F, -9.0F, -6.0F, 1.0F, 2.0F, 2.0F, var0), PartPose.ZERO);
      var4.addOrReplaceChild("right_saddle_mouth", CubeListBuilder.create().texOffs(29, 5).addBox(-3.0F, -9.0F, -6.0F, 1.0F, 2.0F, 2.0F, var0), PartPose.ZERO);
      var4.addOrReplaceChild(
         "left_saddle_line", CubeListBuilder.create().texOffs(32, 2).addBox(3.1F, -6.0F, -8.0F, 0.0F, 3.0F, 16.0F), PartPose.rotation(-0.5235988F, 0.0F, 0.0F)
      );
      var4.addOrReplaceChild(
         "right_saddle_line",
         CubeListBuilder.create().texOffs(32, 2).addBox(-3.1F, -6.0F, -8.0F, 0.0F, 3.0F, 16.0F),
         PartPose.rotation(-0.5235988F, 0.0F, 0.0F)
      );
      var4.addOrReplaceChild(
         "head_saddle", CubeListBuilder.create().texOffs(1, 1).addBox(-3.0F, -11.0F, -1.9F, 6.0F, 5.0F, 6.0F, new CubeDeformation(0.22F)), PartPose.ZERO
      );
      var4.addOrReplaceChild(
         "mouth_saddle_wrap", CubeListBuilder.create().texOffs(19, 0).addBox(-2.0F, -11.0F, -4.0F, 4.0F, 5.0F, 2.0F, new CubeDeformation(0.2F)), PartPose.ZERO
      );
      var5.addOrReplaceChild(
         "left_ear", CubeListBuilder.create().texOffs(19, 16).addBox(0.55F, -13.0F, 4.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(-0.001F)), PartPose.ZERO
      );
      var5.addOrReplaceChild(
         "right_ear", CubeListBuilder.create().texOffs(19, 16).addBox(-2.55F, -13.0F, 4.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(-0.001F)), PartPose.ZERO
      );
      return var1;
   }

   public static MeshDefinition createBabyMesh(CubeDeformation var0) {
      return BABY_TRANSFORMER.apply(createFullScaleBabyMesh(var0));
   }

   protected static MeshDefinition createFullScaleBabyMesh(CubeDeformation var0) {
      MeshDefinition var1 = createBodyMesh(var0);
      PartDefinition var2 = var1.getRoot();
      CubeDeformation var3 = var0.extend(0.0F, 5.5F, 0.0F);
      var2.addOrReplaceChild(
         "left_hind_leg",
         CubeListBuilder.create().texOffs(48, 21).mirror().addBox(-3.0F, -1.01F, -1.0F, 4.0F, 11.0F, 4.0F, var3),
         PartPose.offset(4.0F, 14.0F, 7.0F)
      );
      var2.addOrReplaceChild(
         "right_hind_leg", CubeListBuilder.create().texOffs(48, 21).addBox(-1.0F, -1.01F, -1.0F, 4.0F, 11.0F, 4.0F, var3), PartPose.offset(-4.0F, 14.0F, 7.0F)
      );
      var2.addOrReplaceChild(
         "left_front_leg",
         CubeListBuilder.create().texOffs(48, 21).mirror().addBox(-3.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, var3),
         PartPose.offset(4.0F, 14.0F, -10.0F)
      );
      var2.addOrReplaceChild(
         "right_front_leg",
         CubeListBuilder.create().texOffs(48, 21).addBox(-1.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, var3),
         PartPose.offset(-4.0F, 14.0F, -10.0F)
      );
      return var1;
   }

   public void setupAnim(T var1) {
      super.setupAnim((T)var1);

      for (ModelPart var5 : this.saddleParts) {
         var5.visible = var1.isSaddled;
      }

      for (ModelPart var26 : this.ridingParts) {
         var26.visible = var1.isRidden && var1.isSaddled;
      }

      float var21 = Mth.clamp(var1.yRot, -20.0F, 20.0F);
      float var23 = var1.xRot * 0.017453292F;
      float var25 = var1.walkAnimationSpeed;
      float var27 = var1.walkAnimationPos;
      if (var25 > 0.2F) {
         var23 += Mth.cos(var27 * 0.8F) * 0.15F * var25;
      }

      float var6 = var1.eatAnimation;
      float var7 = var1.standAnimation;
      float var8 = 1.0F - var7;
      float var9 = var1.feedingAnimation;
      boolean var10 = var1.animateTail;
      this.headParts.xRot = 0.5235988F + var23;
      this.headParts.yRot = var21 * 0.017453292F;
      float var11 = var1.isInWater ? 0.2F : 1.0F;
      float var12 = Mth.cos(var11 * var27 * 0.6662F + 3.1415927F);
      float var13 = var12 * 0.8F * var25;
      float var14 = (1.0F - Math.max(var7, var6)) * (0.5235988F + var23 + var9 * Mth.sin(var1.ageInTicks) * 0.05F);
      this.headParts.xRot = var7 * (0.2617994F + var23) + var6 * (2.1816616F + Mth.sin(var1.ageInTicks) * 0.05F) + var14;
      this.headParts.yRot = var7 * var21 * 0.017453292F + (1.0F - Math.max(var7, var6)) * this.headParts.yRot;
      float var15 = var1.ageScale;
      this.headParts.y = this.headParts.y + Mth.lerp(var6, Mth.lerp(var7, 0.0F, -8.0F * var15), 7.0F * var15);
      this.headParts.z = Mth.lerp(var7, this.headParts.z, -4.0F * var15);
      this.body.xRot = var7 * -0.7853982F + var8 * this.body.xRot;
      float var16 = 0.2617994F * var7;
      float var17 = Mth.cos(var1.ageInTicks * 0.6F + 3.1415927F);
      this.leftFrontLeg.y -= 12.0F * var15 * var7;
      this.leftFrontLeg.z += 4.0F * var15 * var7;
      this.rightFrontLeg.y = this.leftFrontLeg.y;
      this.rightFrontLeg.z = this.leftFrontLeg.z;
      float var18 = (-1.0471976F + var17) * var7 + var13 * var8;
      float var19 = (-1.0471976F - var17) * var7 - var13 * var8;
      this.leftHindLeg.xRot = var16 - var12 * 0.5F * var25 * var8;
      this.rightHindLeg.xRot = var16 + var12 * 0.5F * var25 * var8;
      this.leftFrontLeg.xRot = var18;
      this.rightFrontLeg.xRot = var19;
      this.tail.xRot = 0.5235988F + var25 * 0.75F;
      this.tail.y += var25 * var15;
      this.tail.z += var25 * 2.0F * var15;
      if (var10) {
         this.tail.yRot = Mth.cos(var1.ageInTicks * 0.7F);
      } else {
         this.tail.yRot = 0.0F;
      }
   }
}
