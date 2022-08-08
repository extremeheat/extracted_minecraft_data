package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Vex;

public class VexModel extends HumanoidModel<Vex> {
   private final ModelPart leftWing;
   private final ModelPart rightWing;

   public VexModel(ModelPart var1) {
      super(var1);
      this.leftLeg.visible = false;
      this.hat.visible = false;
      this.rightWing = var1.getChild("right_wing");
      this.leftWing = var1.getChild("left_wing");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
      PartDefinition var1 = var0.getRoot();
      var1.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(32, 0).addBox(-1.0F, -1.0F, -2.0F, 6.0F, 10.0F, 4.0F), PartPose.offset(-1.9F, 12.0F, 0.0F));
      var1.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(0, 32).addBox(-20.0F, 0.0F, 0.0F, 20.0F, 12.0F, 1.0F), PartPose.ZERO);
      var1.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(0, 32).mirror().addBox(0.0F, 0.0F, 0.0F, 20.0F, 12.0F, 1.0F), PartPose.ZERO);
      return LayerDefinition.create(var0, 64, 64);
   }

   protected Iterable<ModelPart> bodyParts() {
      return Iterables.concat(super.bodyParts(), ImmutableList.of(this.rightWing, this.leftWing));
   }

   public void setupAnim(Vex var1, float var2, float var3, float var4, float var5, float var6) {
      super.setupAnim((LivingEntity)var1, var2, var3, var4, var5, var6);
      if (var1.isCharging()) {
         if (var1.getMainHandItem().isEmpty()) {
            this.rightArm.xRot = 4.712389F;
            this.leftArm.xRot = 4.712389F;
         } else if (var1.getMainArm() == HumanoidArm.RIGHT) {
            this.rightArm.xRot = 3.7699115F;
         } else {
            this.leftArm.xRot = 3.7699115F;
         }
      }

      ModelPart var10000 = this.rightLeg;
      var10000.xRot += 0.62831855F;
      this.rightWing.z = 2.0F;
      this.leftWing.z = 2.0F;
      this.rightWing.y = 1.0F;
      this.leftWing.y = 1.0F;
      this.rightWing.yRot = 0.47123894F + Mth.cos(var4 * 45.836624F * 0.017453292F) * 3.1415927F * 0.05F;
      this.leftWing.yRot = -this.rightWing.yRot;
      this.leftWing.zRot = -0.47123894F;
      this.leftWing.xRot = 0.47123894F;
      this.rightWing.xRot = 0.47123894F;
      this.rightWing.zRot = 0.47123894F;
   }
}
