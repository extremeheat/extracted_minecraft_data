package net.minecraft.client.model.animal.equine;

import java.util.Set;
import net.minecraft.client.model.BabyModelTransform;
import net.minecraft.client.model.EntityModel;
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
   protected static final MeshTransformer BABY_TRANSFORMER = new BabyModelTransform(true, 16.2F, 1.36F, 2.7272F, 2.0F, 20.0F, Set.of("head_parts"));
   protected final ModelPart body;
   protected final ModelPart headParts;
   private final ModelPart rightHindLeg;
   private final ModelPart leftHindLeg;
   private final ModelPart rightFrontLeg;
   private final ModelPart leftFrontLeg;
   private final ModelPart tail;

   public AbstractEquineModel(ModelPart var1) {
      super(var1);
      this.body = var1.getChild("body");
      this.headParts = var1.getChild("head_parts");
      this.rightHindLeg = var1.getChild("right_hind_leg");
      this.leftHindLeg = var1.getChild("left_hind_leg");
      this.rightFrontLeg = var1.getChild("right_front_leg");
      this.leftFrontLeg = var1.getChild("left_front_leg");
      this.tail = this.body.getChild("tail");
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
      var2.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(48, 21).mirror().addBox(-3.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, var0), PartPose.offset(4.0F, 14.0F, -10.0F));
      var2.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(48, 21).addBox(-1.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, var0), PartPose.offset(-4.0F, 14.0F, -10.0F));
      var3.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(42, 36).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 14.0F, 4.0F, var0), PartPose.offsetAndRotation(0.0F, -5.0F, 2.0F, 0.5235988F, 0.0F, 0.0F));
      var5.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(19, 16).addBox(0.55F, -13.0F, 4.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(-0.001F)), PartPose.ZERO);
      var5.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(19, 16).addBox(-2.55F, -13.0F, 4.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(-0.001F)), PartPose.ZERO);
      return var1;
   }

   public static MeshDefinition createBabyMesh(CubeDeformation var0) {
      return BABY_TRANSFORMER.apply(createFullScaleBabyMesh(var0));
   }

   protected static MeshDefinition createFullScaleBabyMesh(CubeDeformation var0) {
      MeshDefinition var1 = createBodyMesh(var0);
      PartDefinition var2 = var1.getRoot();
      CubeDeformation var3 = var0.extend(0.0F, 5.5F, 0.0F);
      var2.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(48, 21).mirror().addBox(-3.0F, -1.01F, -1.0F, 4.0F, 11.0F, 4.0F, var3), PartPose.offset(4.0F, 14.0F, 7.0F));
      var2.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(48, 21).addBox(-1.0F, -1.01F, -1.0F, 4.0F, 11.0F, 4.0F, var3), PartPose.offset(-4.0F, 14.0F, 7.0F));
      var2.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(48, 21).mirror().addBox(-3.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, var3), PartPose.offset(4.0F, 14.0F, -10.0F));
      var2.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(48, 21).addBox(-1.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, var3), PartPose.offset(-4.0F, 14.0F, -10.0F));
      return var1;
   }

   public void setupAnim(T var1) {
      super.setupAnim(var1);
      float var2 = Mth.clamp(var1.yRot, -20.0F, 20.0F);
      float var3 = var1.xRot * 0.017453292F;
      float var4 = var1.walkAnimationSpeed;
      float var5 = var1.walkAnimationPos;
      if (var4 > 0.2F) {
         var3 += Mth.cos((double)(var5 * 0.8F)) * 0.15F * var4;
      }

      float var6 = var1.eatAnimation;
      float var7 = var1.standAnimation;
      float var8 = 1.0F - var7;
      float var9 = var1.feedingAnimation;
      boolean var10 = var1.animateTail;
      this.headParts.xRot = 0.5235988F + var3;
      this.headParts.yRot = var2 * 0.017453292F;
      float var11 = var1.isInWater ? 0.2F : 1.0F;
      float var12 = Mth.cos((double)(var11 * var5 * 0.6662F + 3.1415927F));
      float var13 = var12 * 0.8F * var4;
      float var14 = (1.0F - Math.max(var7, var6)) * (0.5235988F + var3 + var9 * Mth.sin((double)var1.ageInTicks) * 0.05F);
      this.headParts.xRot = var7 * (0.2617994F + var3) + var6 * (2.1816616F + Mth.sin((double)var1.ageInTicks) * 0.05F) + var14;
      this.headParts.yRot = var7 * var2 * 0.017453292F + (1.0F - Math.max(var7, var6)) * this.headParts.yRot;
      float var15 = var1.ageScale;
      ModelPart var10000 = this.headParts;
      var10000.y += Mth.lerp(var6, Mth.lerp(var7, 0.0F, -8.0F * var15), 7.0F * var15);
      this.headParts.z = Mth.lerp(var7, this.headParts.z, -4.0F * var15);
      this.body.xRot = var7 * -0.7853982F + var8 * this.body.xRot;
      float var16 = 0.2617994F * var7;
      float var17 = Mth.cos((double)(var1.ageInTicks * 0.6F + 3.1415927F));
      var10000 = this.leftFrontLeg;
      var10000.y -= 12.0F * var15 * var7;
      var10000 = this.leftFrontLeg;
      var10000.z += 4.0F * var15 * var7;
      this.rightFrontLeg.y = this.leftFrontLeg.y;
      this.rightFrontLeg.z = this.leftFrontLeg.z;
      float var18 = (-1.0471976F + var17) * var7 + var13 * var8;
      float var19 = (-1.0471976F - var17) * var7 - var13 * var8;
      this.leftHindLeg.xRot = var16 - var12 * 0.5F * var4 * var8;
      this.rightHindLeg.xRot = var16 + var12 * 0.5F * var4 * var8;
      this.leftFrontLeg.xRot = var18;
      this.rightFrontLeg.xRot = var19;
      this.tail.xRot = 0.5235988F + var4 * 0.75F;
      var10000 = this.tail;
      var10000.y += var4 * var15;
      var10000 = this.tail;
      var10000.z += var4 * 2.0F * var15;
      if (var10) {
         this.tail.yRot = Mth.cos((double)(var1.ageInTicks * 0.7F));
      } else {
         this.tail.yRot = 0.0F;
      }

   }
}
