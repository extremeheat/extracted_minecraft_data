package net.minecraft.client.model;

import java.util.Set;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.FelineRenderState;
import net.minecraft.util.Mth;

public class FelineModel<T extends FelineRenderState> extends EntityModel<T> {
   public static final MeshTransformer BABY_TRANSFORMER = new BabyModelTransform(true, 10.0F, 4.0F, Set.of("head"));
   public static final MeshTransformer CAT_TRANSFORMER = MeshTransformer.scaling(0.8F);
   private static final float XO = 0.0F;
   private static final float YO = 16.0F;
   private static final float ZO = -9.0F;
   protected static final float BACK_LEG_Y = 18.0F;
   protected static final float BACK_LEG_Z = 5.0F;
   protected static final float FRONT_LEG_Y = 14.1F;
   private static final float FRONT_LEG_Z = -5.0F;
   private static final String TAIL_1 = "tail1";
   private static final String TAIL_2 = "tail2";
   protected final ModelPart leftHindLeg;
   protected final ModelPart rightHindLeg;
   protected final ModelPart leftFrontLeg;
   protected final ModelPart rightFrontLeg;
   protected final ModelPart tail1;
   protected final ModelPart tail2;
   protected final ModelPart head;
   protected final ModelPart body;

   public FelineModel(ModelPart var1) {
      super(var1);
      this.head = var1.getChild("head");
      this.body = var1.getChild("body");
      this.tail1 = var1.getChild("tail1");
      this.tail2 = var1.getChild("tail2");
      this.leftHindLeg = var1.getChild("left_hind_leg");
      this.rightHindLeg = var1.getChild("right_hind_leg");
      this.leftFrontLeg = var1.getChild("left_front_leg");
      this.rightFrontLeg = var1.getChild("right_front_leg");
   }

   public static MeshDefinition createBodyMesh(CubeDeformation var0) {
      MeshDefinition var1 = new MeshDefinition();
      PartDefinition var2 = var1.getRoot();
      CubeDeformation var3 = new CubeDeformation(-0.02F);
      var2.addOrReplaceChild(
         "head",
         CubeListBuilder.create()
            .addBox("main", -2.5F, -2.0F, -3.0F, 5.0F, 4.0F, 5.0F, var0)
            .addBox("nose", -1.5F, -0.001F, -4.0F, 3, 2, 2, var0, 0, 24)
            .addBox("ear1", -2.0F, -3.0F, 0.0F, 1, 1, 2, var0, 0, 10)
            .addBox("ear2", 1.0F, -3.0F, 0.0F, 1, 1, 2, var0, 6, 10),
         PartPose.offset(0.0F, 15.0F, -9.0F)
      );
      var2.addOrReplaceChild(
         "body",
         CubeListBuilder.create().texOffs(20, 0).addBox(-2.0F, 3.0F, -8.0F, 4.0F, 16.0F, 6.0F, var0),
         PartPose.offsetAndRotation(0.0F, 12.0F, -10.0F, 1.5707964F, 0.0F, 0.0F)
      );
      var2.addOrReplaceChild(
         "tail1",
         CubeListBuilder.create().texOffs(0, 15).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F, var0),
         PartPose.offsetAndRotation(0.0F, 15.0F, 8.0F, 0.9F, 0.0F, 0.0F)
      );
      var2.addOrReplaceChild(
         "tail2", CubeListBuilder.create().texOffs(4, 15).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F, var3), PartPose.offset(0.0F, 20.0F, 14.0F)
      );
      CubeListBuilder var4 = CubeListBuilder.create().texOffs(8, 13).addBox(-1.0F, 0.0F, 1.0F, 2.0F, 6.0F, 2.0F, var0);
      var2.addOrReplaceChild("left_hind_leg", var4, PartPose.offset(1.1F, 18.0F, 5.0F));
      var2.addOrReplaceChild("right_hind_leg", var4, PartPose.offset(-1.1F, 18.0F, 5.0F));
      CubeListBuilder var5 = CubeListBuilder.create().texOffs(40, 0).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 10.0F, 2.0F, var0);
      var2.addOrReplaceChild("left_front_leg", var5, PartPose.offset(1.2F, 14.1F, -5.0F));
      var2.addOrReplaceChild("right_front_leg", var5, PartPose.offset(-1.2F, 14.1F, -5.0F));
      return var1;
   }

   public void setupAnim(T var1) {
      super.setupAnim((T)var1);
      if (var1.isCrouching) {
         this.body.y++;
         this.head.y += 2.0F;
         this.tail1.y++;
         this.tail2.y += -4.0F;
         this.tail2.z += 2.0F;
         this.tail1.xRot = 1.5707964F;
         this.tail2.xRot = 1.5707964F;
      } else if (var1.isSprinting) {
         this.tail2.y = this.tail1.y;
         this.tail2.z += 2.0F;
         this.tail1.xRot = 1.5707964F;
         this.tail2.xRot = 1.5707964F;
      }

      this.head.xRot = var1.xRot * 0.017453292F;
      this.head.yRot = var1.yRot * 0.017453292F;
      if (!var1.isSitting) {
         this.body.xRot = 1.5707964F;
         float var2 = var1.walkAnimationSpeed;
         float var3 = var1.walkAnimationPos;
         if (var1.isSprinting) {
            this.leftHindLeg.xRot = Mth.cos(var3 * 0.6662F) * var2;
            this.rightHindLeg.xRot = Mth.cos(var3 * 0.6662F + 0.3F) * var2;
            this.leftFrontLeg.xRot = Mth.cos(var3 * 0.6662F + 3.1415927F + 0.3F) * var2;
            this.rightFrontLeg.xRot = Mth.cos(var3 * 0.6662F + 3.1415927F) * var2;
            this.tail2.xRot = 1.7278761F + 0.31415927F * Mth.cos(var3) * var2;
         } else {
            this.leftHindLeg.xRot = Mth.cos(var3 * 0.6662F) * var2;
            this.rightHindLeg.xRot = Mth.cos(var3 * 0.6662F + 3.1415927F) * var2;
            this.leftFrontLeg.xRot = Mth.cos(var3 * 0.6662F + 3.1415927F) * var2;
            this.rightFrontLeg.xRot = Mth.cos(var3 * 0.6662F) * var2;
            if (!var1.isCrouching) {
               this.tail2.xRot = 1.7278761F + 0.7853982F * Mth.cos(var3) * var2;
            } else {
               this.tail2.xRot = 1.7278761F + 0.47123894F * Mth.cos(var3) * var2;
            }
         }
      }

      float var4 = var1.ageScale;
      if (var1.isSitting) {
         this.body.xRot = 0.7853982F;
         this.body.y += -4.0F * var4;
         this.body.z += 5.0F * var4;
         this.head.y += -3.3F * var4;
         this.head.z += 1.0F * var4;
         this.tail1.y += 8.0F * var4;
         this.tail1.z += -2.0F * var4;
         this.tail2.y += 2.0F * var4;
         this.tail2.z += -0.8F * var4;
         this.tail1.xRot = 1.7278761F;
         this.tail2.xRot = 2.670354F;
         this.leftFrontLeg.xRot = -0.15707964F;
         this.leftFrontLeg.y += 2.0F * var4;
         this.leftFrontLeg.z -= 2.0F * var4;
         this.rightFrontLeg.xRot = -0.15707964F;
         this.rightFrontLeg.y += 2.0F * var4;
         this.rightFrontLeg.z -= 2.0F * var4;
         this.leftHindLeg.xRot = -1.5707964F;
         this.leftHindLeg.y += 3.0F * var4;
         this.leftHindLeg.z -= 4.0F * var4;
         this.rightHindLeg.xRot = -1.5707964F;
         this.rightHindLeg.y += 3.0F * var4;
         this.rightHindLeg.z -= 4.0F * var4;
      }

      if (var1.lieDownAmount > 0.0F) {
         this.head.zRot = Mth.rotLerp(var1.lieDownAmount, this.head.zRot, -1.2707963F);
         this.head.yRot = Mth.rotLerp(var1.lieDownAmount, this.head.yRot, 1.2707963F);
         this.leftFrontLeg.xRot = -1.2707963F;
         this.rightFrontLeg.xRot = -0.47079635F;
         this.rightFrontLeg.zRot = -0.2F;
         this.rightFrontLeg.x += var4;
         this.leftHindLeg.xRot = -0.4F;
         this.rightHindLeg.xRot = 0.5F;
         this.rightHindLeg.zRot = -0.5F;
         this.rightHindLeg.x += 0.8F * var4;
         this.rightHindLeg.y += 2.0F * var4;
         this.tail1.xRot = Mth.rotLerp(var1.lieDownAmountTail, this.tail1.xRot, 0.8F);
         this.tail2.xRot = Mth.rotLerp(var1.lieDownAmountTail, this.tail2.xRot, -0.4F);
      }

      if (var1.relaxStateOneAmount > 0.0F) {
         this.head.xRot = Mth.rotLerp(var1.relaxStateOneAmount, this.head.xRot, -0.58177644F);
      }
   }
}
