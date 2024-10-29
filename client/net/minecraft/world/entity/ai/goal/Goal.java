package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public abstract class Goal {
   private final EnumSet<Flag> flags = EnumSet.noneOf(Flag.class);

   public Goal() {
      super();
   }

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

   public boolean requiresUpdateEveryTick() {
      return false;
   }

   public void tick() {
   }

   public void setFlags(EnumSet<Flag> var1) {
      this.flags.clear();
      this.flags.addAll(var1);
   }

   public String toString() {
      return this.getClass().getSimpleName();
   }

   public EnumSet<Flag> getFlags() {
      return this.flags;
   }

   protected int adjustedTickDelay(int var1) {
      return this.requiresUpdateEveryTick() ? var1 : reducedTickDelay(var1);
   }

   protected static int reducedTickDelay(int var0) {
      return Mth.positiveCeilDiv(var0, 2);
   }

   protected static ServerLevel getServerLevel(Entity var0) {
      return (ServerLevel)var0.level();
   }

   protected static ServerLevel getServerLevel(Level var0) {
      return (ServerLevel)var0;
   }

   public static enum Flag {
      MOVE,
      LOOK,
      JUMP,
      TARGET;

      private Flag() {
      }

      // $FF: synthetic method
      private static Flag[] $values() {
         return new Flag[]{MOVE, LOOK, JUMP, TARGET};
      }
   }
}
