package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Vex;

public class VexModel extends HumanoidModel<Vex> {
   private final ModelPart leftWing;
   private final ModelPart rightWing;

   public VexModel() {
      super(0.0F, 0.0F, 64, 64);
      this.leftLeg.visible = false;
      this.hat.visible = false;
      this.rightLeg = new ModelPart(this, 32, 0);
      this.rightLeg.addBox(-1.0F, -1.0F, -2.0F, 6.0F, 10.0F, 4.0F, 0.0F);
      this.rightLeg.setPos(-1.9F, 12.0F, 0.0F);
      this.rightWing = new ModelPart(this, 0, 32);
      this.rightWing.addBox(-20.0F, 0.0F, 0.0F, 20.0F, 12.0F, 1.0F);
      this.leftWing = new ModelPart(this, 0, 32);
      this.leftWing.mirror = true;
      this.leftWing.addBox(0.0F, 0.0F, 0.0F, 20.0F, 12.0F, 1.0F);
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
      this.rightWing.yRot = 0.47123894F + Mth.cos(var4 * 0.8F) * 3.1415927F * 0.05F;
      this.leftWing.yRot = -this.rightWing.yRot;
      this.leftWing.zRot = -0.47123894F;
      this.leftWing.xRot = 0.47123894F;
      this.rightWing.xRot = 0.47123894F;
      this.rightWing.zRot = 0.47123894F;
   }
}
