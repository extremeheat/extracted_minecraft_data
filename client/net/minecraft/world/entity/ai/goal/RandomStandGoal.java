package net.minecraft.world.entity.ai.goal;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.animal.horse.AbstractHorse;

public class RandomStandGoal extends Goal {
   private final AbstractHorse horse;
   private int nextStand;

   public RandomStandGoal(AbstractHorse var1) {
      super();
      this.horse = var1;
      this.resetStandInterval(var1);
   }

   public void start() {
      this.horse.standIfPossible();
      this.playStandSound();
   }

   private void playStandSound() {
      SoundEvent var1 = this.horse.getAmbientStandSound();
      if (var1 != null) {
         this.horse.playSound(var1);
      }

   }

   public boolean canContinueToUse() {
      return false;
   }

   public boolean canUse() {
      ++this.nextStand;
      if (this.nextStand > 0 && this.horse.getRandom().nextInt(1000) < this.nextStand) {
         this.resetStandInterval(this.horse);
         return !this.horse.isImmobile() && this.horse.getRandom().nextInt(10) == 0;
      } else {
         return false;
      }
   }

   private void resetStandInterval(AbstractHorse var1) {
      this.nextStand = -var1.getAmbientStandInterval();
   }

   public boolean requiresUpdateEveryTick() {
      return true;
   }
}
