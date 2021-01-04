package net.minecraft.client.model;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.AbstractIllager;

public class PillagerModel<T extends AbstractIllager> extends IllagerModel<T> {
   public PillagerModel(float var1, float var2, int var3, int var4) {
      super(var1, var2, var3, var4);
   }

   public void render(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.setupAnim(var1, var2, var3, var4, var5, var6, var7);
      this.head.render(var7);
      this.body.render(var7);
      this.leftLeg.render(var7);
      this.rightLeg.render(var7);
      this.rightArm.render(var7);
      this.leftArm.render(var7);
   }

   // $FF: synthetic method
   public void render(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.render((AbstractIllager)var1, var2, var3, var4, var5, var6, var7);
   }
}
