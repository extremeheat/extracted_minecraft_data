package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Vex;

public class VexModel extends HumanoidModel<Vex> {
   private final ModelPart leftWing;
   private final ModelPart rightWing;

   public VexModel() {
      this(0.0F);
   }

   public VexModel(float var1) {
      super(var1, 0.0F, 64, 64);
      this.leftLeg.visible = false;
      this.hat.visible = false;
      this.rightLeg = new ModelPart(this, 32, 0);
      this.rightLeg.addBox(-1.0F, -1.0F, -2.0F, 6, 10, 4, 0.0F);
      this.rightLeg.setPos(-1.9F, 12.0F, 0.0F);
      this.rightWing = new ModelPart(this, 0, 32);
      this.rightWing.addBox(-20.0F, 0.0F, 0.0F, 20, 12, 1);
      this.leftWing = new ModelPart(this, 0, 32);
      this.leftWing.mirror = true;
      this.leftWing.addBox(0.0F, 0.0F, 0.0F, 20, 12, 1);
   }

   public void render(Vex var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      super.render((LivingEntity)var1, var2, var3, var4, var5, var6, var7);
      this.rightWing.render(var7);
      this.leftWing.render(var7);
   }

   public void setupAnim(Vex var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      super.setupAnim((LivingEntity)var1, var2, var3, var4, var5, var6, var7);
      if (var1.isCharging()) {
         if (var1.getMainArm() == HumanoidArm.RIGHT) {
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

   // $FF: synthetic method
   public void setupAnim(LivingEntity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.setupAnim((Vex)var1, var2, var3, var4, var5, var6, var7);
   }

   // $FF: synthetic method
   public void render(LivingEntity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.render((Vex)var1, var2, var3, var4, var5, var6, var7);
   }

   // $FF: synthetic method
   public void setupAnim(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.setupAnim((Vex)var1, var2, var3, var4, var5, var6, var7);
   }

   // $FF: synthetic method
   public void render(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.render((Vex)var1, var2, var3, var4, var5, var6, var7);
   }
}
