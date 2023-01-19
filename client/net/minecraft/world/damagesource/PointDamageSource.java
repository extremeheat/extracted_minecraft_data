package net.minecraft.world.damagesource;

import net.minecraft.world.phys.Vec3;

public class PointDamageSource extends DamageSource {
   private final Vec3 damageSourcePosition;

   public PointDamageSource(String var1, Vec3 var2) {
      super(var1);
      this.damageSourcePosition = var2;
   }

   @Override
   public Vec3 getSourcePosition() {
      return this.damageSourcePosition;
   }
}
