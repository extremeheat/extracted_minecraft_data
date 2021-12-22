package net.minecraft.world.entity;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public interface ItemSteerable {
   boolean boost();

   void travelWithInput(Vec3 var1);

   float getSteeringSpeed();

   default boolean travel(Mob var1, ItemBasedSteering var2, Vec3 var3) {
      if (!var1.isAlive()) {
         return false;
      } else {
         Entity var4 = var1.getFirstPassenger();
         if (var1.isVehicle() && var1.canBeControlledByRider() && var4 instanceof Player) {
            var1.setYRot(var4.getYRot());
            var1.yRotO = var1.getYRot();
            var1.setXRot(var4.getXRot() * 0.5F);
            var1.setRot(var1.getYRot(), var1.getXRot());
            var1.yBodyRot = var1.getYRot();
            var1.yHeadRot = var1.getYRot();
            var1.maxUpStep = 1.0F;
            var1.flyingSpeed = var1.getSpeed() * 0.1F;
            if (var2.boosting && var2.boostTime++ > var2.boostTimeTotal) {
               var2.boosting = false;
            }

            if (var1.isControlledByLocalInstance()) {
               float var5 = this.getSteeringSpeed();
               if (var2.boosting) {
                  var5 += var5 * 1.15F * Mth.sin((float)var2.boostTime / (float)var2.boostTimeTotal * 3.1415927F);
               }

               var1.setSpeed(var5);
               this.travelWithInput(new Vec3(0.0D, 0.0D, 1.0D));
               var1.lerpSteps = 0;
            } else {
               var1.calculateEntityAnimation(var1, false);
               var1.setDeltaMovement(Vec3.ZERO);
            }

            var1.tryCheckInsideBlocks();
            return true;
         } else {
            var1.maxUpStep = 0.5F;
            var1.flyingSpeed = 0.02F;
            this.travelWithInput(var3);
            return false;
         }
      }
   }
}
