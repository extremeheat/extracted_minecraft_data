package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.animal.PolarBear;

public class PolarBearModel<T extends PolarBear> extends QuadrupedModel<T> {
   public PolarBearModel(ModelPart var1) {
      super(var1, true, 16.0F, 4.0F, 2.25F, 2.0F, 24);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      var1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-3.5F, -3.0F, -3.0F, 7.0F, 7.0F, 7.0F).texOffs(0, 44).addBox("mouth", -2.5F, 1.0F, -6.0F, 5.0F, 3.0F, 3.0F).texOffs(26, 0).addBox("right_ear", -4.5F, -4.0F, -1.0F, 2.0F, 2.0F, 1.0F).texOffs(26, 0).mirror().addBox("left_ear", 2.5F, -4.0F, -1.0F, 2.0F, 2.0F, 1.0F), PartPose.offset(0.0F, 10.0F, -16.0F));
      var1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 19).addBox(-5.0F, -13.0F, -7.0F, 14.0F, 14.0F, 11.0F).texOffs(39, 0).addBox(-4.0F, -25.0F, -7.0F, 12.0F, 12.0F, 10.0F), PartPose.offsetAndRotation(-2.0F, 9.0F, 12.0F, 1.5707964F, 0.0F, 0.0F));
      boolean var2 = true;
      CubeListBuilder var3 = CubeListBuilder.create().texOffs(50, 22).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 8.0F);
      var1.addOrReplaceChild("right_hind_leg", var3, PartPose.offset(-4.5F, 14.0F, 6.0F));
      var1.addOrReplaceChild("left_hind_leg", var3, PartPose.offset(4.5F, 14.0F, 6.0F));
      CubeListBuilder var4 = CubeListBuilder.create().texOffs(50, 40).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 6.0F);
      var1.addOrReplaceChild("right_front_leg", var4, PartPose.offset(-3.5F, 14.0F, -8.0F));
      var1.addOrReplaceChild("left_front_leg", var4, PartPose.offset(3.5F, 14.0F, -8.0F));
      return LayerDefinition.create(var0, 128, 64);
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6) {
      super.setupAnim(var1, var2, var3, var4, var5, var6);
      float var7 = var4 - (float)var1.tickCount;
      float var8 = var1.getStandingAnimationScale(var7);
      var8 *= var8;
      float var9 = 1.0F - var8;
      this.body.xRot = 1.5707964F - var8 * 3.1415927F * 0.35F;
      this.body.y = 9.0F * var9 + 11.0F * var8;
      this.rightFrontLeg.y = 14.0F * var9 - 6.0F * var8;
      this.rightFrontLeg.z = -8.0F * var9 - 4.0F * var8;
      ModelPart var10000 = this.rightFrontLeg;
      var10000.xRot -= var8 * 3.1415927F * 0.45F;
      this.leftFrontLeg.y = this.rightFrontLeg.y;
      this.leftFrontLeg.z = this.rightFrontLeg.z;
      var10000 = this.leftFrontLeg;
      var10000.xRot -= var8 * 3.1415927F * 0.45F;
      if (this.young) {
         this.head.y = 10.0F * var9 - 9.0F * var8;
         this.head.z = -16.0F * var9 - 7.0F * var8;
      } else {
         this.head.y = 10.0F * var9 - 14.0F * var8;
         this.head.z = -16.0F * var9 - 3.0F * var8;
      }

      var10000 = this.head;
      var10000.xRot += var8 * 3.1415927F * 0.15F;
   }
}
