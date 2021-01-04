package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;

public abstract class AbstractZombieModel<T extends Monster> extends HumanoidModel<T> {
   protected AbstractZombieModel(float var1, float var2, int var3, int var4) {
      super(var1, var2, var3, var4);
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      super.setupAnim((LivingEntity)var1, var2, var3, var4, var5, var6, var7);
      boolean var8 = this.isAggressive(var1);
      float var9 = Mth.sin(this.attackTime * 3.1415927F);
      float var10 = Mth.sin((1.0F - (1.0F - this.attackTime) * (1.0F - this.attackTime)) * 3.1415927F);
      this.rightArm.zRot = 0.0F;
      this.leftArm.zRot = 0.0F;
      this.rightArm.yRot = -(0.1F - var9 * 0.6F);
      this.leftArm.yRot = 0.1F - var9 * 0.6F;
      float var11 = -3.1415927F / (var8 ? 1.5F : 2.25F);
      this.rightArm.xRot = var11;
      this.leftArm.xRot = var11;
      ModelPart var10000 = this.rightArm;
      var10000.xRot += var9 * 1.2F - var10 * 0.4F;
      var10000 = this.leftArm;
      var10000.xRot += var9 * 1.2F - var10 * 0.4F;
      var10000 = this.rightArm;
      var10000.zRot += Mth.cos(var4 * 0.09F) * 0.05F + 0.05F;
      var10000 = this.leftArm;
      var10000.zRot -= Mth.cos(var4 * 0.09F) * 0.05F + 0.05F;
      var10000 = this.rightArm;
      var10000.xRot += Mth.sin(var4 * 0.067F) * 0.05F;
      var10000 = this.leftArm;
      var10000.xRot -= Mth.sin(var4 * 0.067F) * 0.05F;
   }

   public abstract boolean isAggressive(T var1);
}
