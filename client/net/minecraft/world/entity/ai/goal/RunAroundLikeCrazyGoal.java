package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class RunAroundLikeCrazyGoal extends Goal {
   private final AbstractHorse horse;
   private final double speedModifier;
   private double posX;
   private double posY;
   private double posZ;

   public RunAroundLikeCrazyGoal(AbstractHorse var1, double var2) {
      super();
      this.horse = var1;
      this.speedModifier = var2;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE));
   }

   public boolean canUse() {
      if (!this.horse.isTamed() && this.horse.isVehicle()) {
         Vec3 var1 = DefaultRandomPos.getPos(this.horse, 5, 4);
         if (var1 == null) {
            return false;
         } else {
            this.posX = var1.x;
            this.posY = var1.y;
            this.posZ = var1.z;
            return true;
         }
      } else {
         return false;
      }
   }

   public void start() {
      this.horse.getNavigation().moveTo(this.posX, this.posY, this.posZ, this.speedModifier);
   }

   public boolean canContinueToUse() {
      return !this.horse.isTamed() && !this.horse.getNavigation().isDone() && this.horse.isVehicle();
   }

   public void tick() {
      if (!this.horse.isTamed() && this.horse.getRandom().nextInt(this.adjustedTickDelay(50)) == 0) {
         Entity var1 = this.horse.getFirstPassenger();
         if (var1 == null) {
            return;
         }

         if (var1 instanceof Player) {
            Player var2 = (Player)var1;
            int var3 = this.horse.getTemper();
            int var4 = this.horse.getMaxTemper();
            if (var4 > 0 && this.horse.getRandom().nextInt(var4) < var3) {
               this.horse.tameWithName(var2);
               return;
            }

            this.horse.modifyTemper(5);
         }

         this.horse.ejectPassengers();
         this.horse.makeMad();
         this.horse.level().broadcastEntityEvent(this.horse, (byte)6);
      }

   }
}
