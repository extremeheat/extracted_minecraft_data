package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.LivingEntity;

public class EndermanModel<T extends LivingEntity> extends HumanoidModel<T> {
   public boolean carrying;
   public boolean creepy;

   public EndermanModel(ModelPart var1) {
      super(var1);
   }

   public static LayerDefinition createBodyLayer() {
      float var0 = -14.0F;
      MeshDefinition var1 = HumanoidModel.createMesh(CubeDeformation.NONE, -14.0F);
      PartDefinition var2 = var1.getRoot();
      PartPose var3 = PartPose.offset(0.0F, -13.0F, 0.0F);
      var2.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(-0.5F)), var3);
      var2.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F), var3);
      var2.addOrReplaceChild("body", CubeListBuilder.create().texOffs(32, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F), PartPose.offset(0.0F, -14.0F, 0.0F));
      var2.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(56, 0).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 30.0F, 2.0F), PartPose.offset(-5.0F, -12.0F, 0.0F));
      var2.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(56, 0).mirror().addBox(-1.0F, -2.0F, -1.0F, 2.0F, 30.0F, 2.0F), PartPose.offset(5.0F, -12.0F, 0.0F));
      var2.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(56, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 30.0F, 2.0F), PartPose.offset(-2.0F, -5.0F, 0.0F));
      var2.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(56, 0).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 30.0F, 2.0F), PartPose.offset(2.0F, -5.0F, 0.0F));
      return LayerDefinition.create(var1, 64, 32);
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6) {
      super.setupAnim(var1, var2, var3, var4, var5, var6);
      this.head.visible = true;
      boolean var7 = true;
      this.body.xRot = 0.0F;
      this.body.y = -14.0F;
      this.body.z = -0.0F;
      ModelPart var10000 = this.rightLeg;
      var10000.xRot -= 0.0F;
      var10000 = this.leftLeg;
      var10000.xRot -= 0.0F;
      var10000 = this.rightArm;
      var10000.xRot *= 0.5F;
      var10000 = this.leftArm;
      var10000.xRot *= 0.5F;
      var10000 = this.rightLeg;
      var10000.xRot *= 0.5F;
      var10000 = this.leftLeg;
      var10000.xRot *= 0.5F;
      float var8 = 0.4F;
      if (this.rightArm.xRot > 0.4F) {
         this.rightArm.xRot = 0.4F;
      }

      if (this.leftArm.xRot > 0.4F) {
         this.leftArm.xRot = 0.4F;
      }

      if (this.rightArm.xRot < -0.4F) {
         this.rightArm.xRot = -0.4F;
      }

      if (this.leftArm.xRot < -0.4F) {
         this.leftArm.xRot = -0.4F;
      }

      if (this.rightLeg.xRot > 0.4F) {
         this.rightLeg.xRot = 0.4F;
      }

      if (this.leftLeg.xRot > 0.4F) {
         this.leftLeg.xRot = 0.4F;
      }

      if (this.rightLeg.xRot < -0.4F) {
         this.rightLeg.xRot = -0.4F;
      }

      if (this.leftLeg.xRot < -0.4F) {
         this.leftLeg.xRot = -0.4F;
      }

      if (this.carrying) {
         this.rightArm.xRot = -0.5F;
         this.leftArm.xRot = -0.5F;
         this.rightArm.zRot = 0.05F;
         this.leftArm.zRot = -0.05F;
      }

      this.rightLeg.z = 0.0F;
      this.leftLeg.z = 0.0F;
      this.rightLeg.y = -5.0F;
      this.leftLeg.y = -5.0F;
      this.head.z = -0.0F;
      this.head.y = -13.0F;
      this.hat.x = this.head.x;
      this.hat.y = this.head.y;
      this.hat.z = this.head.z;
      this.hat.xRot = this.head.xRot;
      this.hat.yRot = this.head.yRot;
      this.hat.zRot = this.head.zRot;
      if (this.creepy) {
         float var9 = 1.0F;
         var10000 = this.head;
         var10000.y -= 5.0F;
      }

      boolean var10 = true;
      this.rightArm.setPos(-5.0F, -12.0F, 0.0F);
      this.leftArm.setPos(5.0F, -12.0F, 0.0F);
   }
}
