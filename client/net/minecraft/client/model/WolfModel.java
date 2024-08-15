package net.minecraft.client.model;

import java.util.Set;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.WolfRenderState;
import net.minecraft.util.Mth;

public class WolfModel extends EntityModel<WolfRenderState> {
   public static final MeshTransformer BABY_TRANSFORMER = new BabyModelTransform(Set.of("head"));
   private static final String REAL_HEAD = "real_head";
   private static final String UPPER_BODY = "upper_body";
   private static final String REAL_TAIL = "real_tail";
   private final ModelPart root;
   private final ModelPart head;
   private final ModelPart realHead;
   private final ModelPart body;
   private final ModelPart rightHindLeg;
   private final ModelPart leftHindLeg;
   private final ModelPart rightFrontLeg;
   private final ModelPart leftFrontLeg;
   private final ModelPart tail;
   private final ModelPart realTail;
   private final ModelPart upperBody;
   private static final int LEG_SIZE = 8;

   public WolfModel(ModelPart var1) {
      super();
      this.root = var1;
      this.head = var1.getChild("head");
      this.realHead = this.head.getChild("real_head");
      this.body = var1.getChild("body");
      this.upperBody = var1.getChild("upper_body");
      this.rightHindLeg = var1.getChild("right_hind_leg");
      this.leftHindLeg = var1.getChild("left_hind_leg");
      this.rightFrontLeg = var1.getChild("right_front_leg");
      this.leftFrontLeg = var1.getChild("left_front_leg");
      this.tail = var1.getChild("tail");
      this.realTail = this.tail.getChild("real_tail");
   }

   public static MeshDefinition createMeshDefinition(CubeDeformation var0) {
      MeshDefinition var1 = new MeshDefinition();
      PartDefinition var2 = var1.getRoot();
      float var3 = 13.5F;
      PartDefinition var4 = var2.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(-1.0F, 13.5F, -7.0F));
      var4.addOrReplaceChild(
         "real_head",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(-2.0F, -3.0F, -2.0F, 6.0F, 6.0F, 4.0F, var0)
            .texOffs(16, 14)
            .addBox(-2.0F, -5.0F, 0.0F, 2.0F, 2.0F, 1.0F, var0)
            .texOffs(16, 14)
            .addBox(2.0F, -5.0F, 0.0F, 2.0F, 2.0F, 1.0F, var0)
            .texOffs(0, 10)
            .addBox(-0.5F, -0.001F, -5.0F, 3.0F, 3.0F, 4.0F, var0),
         PartPose.ZERO
      );
      var2.addOrReplaceChild(
         "body",
         CubeListBuilder.create().texOffs(18, 14).addBox(-3.0F, -2.0F, -3.0F, 6.0F, 9.0F, 6.0F, var0),
         PartPose.offsetAndRotation(0.0F, 14.0F, 2.0F, 1.5707964F, 0.0F, 0.0F)
      );
      var2.addOrReplaceChild(
         "upper_body",
         CubeListBuilder.create().texOffs(21, 0).addBox(-3.0F, -3.0F, -3.0F, 8.0F, 6.0F, 7.0F, var0),
         PartPose.offsetAndRotation(-1.0F, 14.0F, -3.0F, 1.5707964F, 0.0F, 0.0F)
      );
      CubeListBuilder var5 = CubeListBuilder.create().texOffs(0, 18).addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, var0);
      var2.addOrReplaceChild("right_hind_leg", var5, PartPose.offset(-2.5F, 16.0F, 7.0F));
      var2.addOrReplaceChild("left_hind_leg", var5, PartPose.offset(0.5F, 16.0F, 7.0F));
      var2.addOrReplaceChild("right_front_leg", var5, PartPose.offset(-2.5F, 16.0F, -4.0F));
      var2.addOrReplaceChild("left_front_leg", var5, PartPose.offset(0.5F, 16.0F, -4.0F));
      PartDefinition var6 = var2.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.0F, 12.0F, 8.0F, 0.62831855F, 0.0F, 0.0F));
      var6.addOrReplaceChild("real_tail", CubeListBuilder.create().texOffs(9, 18).addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, var0), PartPose.ZERO);
      return var1;
   }

   public void setupAnim(WolfRenderState var1) {
      this.body.resetPose();
      this.upperBody.resetPose();
      this.tail.resetPose();
      this.rightHindLeg.resetPose();
      this.leftHindLeg.resetPose();
      this.rightFrontLeg.resetPose();
      this.leftFrontLeg.resetPose();
      float var2 = var1.walkAnimationPos;
      float var3 = var1.walkAnimationSpeed;
      if (var1.isAngry) {
         this.tail.yRot = 0.0F;
      } else {
         this.tail.yRot = Mth.cos(var2 * 0.6662F) * 1.4F * var3;
      }

      if (var1.isSitting) {
         float var4 = var1.ageScale;
         this.upperBody.y += 2.0F * var4;
         this.upperBody.xRot = 1.2566371F;
         this.upperBody.yRot = 0.0F;
         this.body.y += 4.0F * var4;
         this.body.z -= 2.0F * var4;
         this.body.xRot = 0.7853982F;
         this.tail.y += 9.0F * var4;
         this.tail.z -= 2.0F * var4;
         this.rightHindLeg.y += 6.7F * var4;
         this.rightHindLeg.z -= 5.0F * var4;
         this.rightHindLeg.xRot = 4.712389F;
         this.leftHindLeg.y += 6.7F * var4;
         this.leftHindLeg.z -= 5.0F * var4;
         this.leftHindLeg.xRot = 4.712389F;
         this.rightFrontLeg.xRot = 5.811947F;
         this.rightFrontLeg.x += 0.01F * var4;
         this.rightFrontLeg.y += 1.0F * var4;
         this.leftFrontLeg.xRot = 5.811947F;
         this.leftFrontLeg.x -= 0.01F * var4;
         this.leftFrontLeg.y += 1.0F * var4;
      } else {
         this.rightHindLeg.xRot = Mth.cos(var2 * 0.6662F) * 1.4F * var3;
         this.leftHindLeg.xRot = Mth.cos(var2 * 0.6662F + 3.1415927F) * 1.4F * var3;
         this.rightFrontLeg.xRot = Mth.cos(var2 * 0.6662F + 3.1415927F) * 1.4F * var3;
         this.leftFrontLeg.xRot = Mth.cos(var2 * 0.6662F) * 1.4F * var3;
      }

      this.realHead.zRot = var1.headRollAngle + var1.getBodyRollAngle(0.0F);
      this.upperBody.zRot = var1.getBodyRollAngle(-0.08F);
      this.body.zRot = var1.getBodyRollAngle(-0.16F);
      this.realTail.zRot = var1.getBodyRollAngle(-0.2F);
      this.head.xRot = var1.xRot * 0.017453292F;
      this.head.yRot = var1.yRot * 0.017453292F;
      this.tail.xRot = var1.tailAngle;
   }

   @Override
   public ModelPart root() {
      return this.root;
   }
}
