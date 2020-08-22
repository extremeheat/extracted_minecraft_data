package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;

public abstract class Goal {
   private final EnumSet flags = EnumSet.noneOf(Goal.Flag.class);

   public abstract boolean canUse();

   public boolean canContinueToUse() {
      return this.canUse();
   }

   public boolean isInterruptable() {
      return true;
   }

   public void start() {
   }

   public void stop() {
   }

   public void tick() {
   }

   public void setFlags(EnumSet var1) {
      this.flags.clear();
      this.flags.addAll(var1);
   }

   public String toString() {
      return this.getClass().getSimpleName();
   }

   public EnumSet getFlags() {
      return this.flags;
   }

   public static enum Flag {
      MOVE,
      LOOK,
      JUMP,
      TARGET;
   }
}
