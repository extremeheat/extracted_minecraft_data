package net.minecraft.world.entity.ai.goal;

import net.minecraft.world.entity.Mob;

public class OpenDoorGoal extends DoorInteractGoal {
   private final boolean closeDoor;
   private int forgetTime;

   public OpenDoorGoal(Mob var1, boolean var2) {
      super(var1);
      this.mob = var1;
      this.closeDoor = var2;
   }

   public boolean canContinueToUse() {
      return this.closeDoor && this.forgetTime > 0 && super.canContinueToUse();
   }

   public void start() {
      this.forgetTime = 20;
      this.setOpen(true);
   }

   public void stop() {
      this.setOpen(false);
   }

   public void tick() {
      --this.forgetTime;
      super.tick();
   }
}
