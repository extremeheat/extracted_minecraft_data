package net.minecraft.world.entity.ai.goal;

import com.google.common.annotations.VisibleForTesting;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;

public class GoalSelector {
   private static final WrappedGoal NO_GOAL = new WrappedGoal(2147483647, new Goal() {
      @Override
      public boolean canUse() {
         return false;
      }
   }) {
      @Override
      public boolean isRunning() {
         return false;
      }
   };
   private final Map<Goal.Flag, WrappedGoal> lockedFlags = new EnumMap<>(Goal.Flag.class);
   private final Set<WrappedGoal> availableGoals = new ObjectLinkedOpenHashSet();
   private final EnumSet<Goal.Flag> disabledFlags = EnumSet.noneOf(Goal.Flag.class);

   public GoalSelector() {
      super();
   }

   public void addGoal(int var1, Goal var2) {
      this.availableGoals.add(new WrappedGoal(var1, var2));
   }

   @VisibleForTesting
   public void removeAllGoals(Predicate<Goal> var1) {
      this.availableGoals.removeIf(var1x -> var1.test(var1x.getGoal()));
   }

   public void removeGoal(Goal var1) {
      for (WrappedGoal var3 : this.availableGoals) {
         if (var3.getGoal() == var1 && var3.isRunning()) {
            var3.stop();
         }
      }

      this.availableGoals.removeIf(var1x -> var1x.getGoal() == var1);
   }

   private static boolean goalContainsAnyFlags(WrappedGoal var0, EnumSet<Goal.Flag> var1) {
      for (Goal.Flag var3 : var0.getFlags()) {
         if (var1.contains(var3)) {
            return true;
         }
      }

      return false;
   }

   private static boolean goalCanBeReplacedForAllFlags(WrappedGoal var0, Map<Goal.Flag, WrappedGoal> var1) {
      for (Goal.Flag var3 : var0.getFlags()) {
         if (!var1.getOrDefault(var3, NO_GOAL).canBeReplacedBy(var0)) {
            return false;
         }
      }

      return true;
   }

   public void tick() {
      ProfilerFiller var1 = Profiler.get();
      var1.push("goalCleanup");

      for (WrappedGoal var3 : this.availableGoals) {
         if (var3.isRunning() && (goalContainsAnyFlags(var3, this.disabledFlags) || !var3.canContinueToUse())) {
            var3.stop();
         }
      }

      this.lockedFlags.entrySet().removeIf(var0 -> !var0.getValue().isRunning());
      var1.pop();
      var1.push("goalUpdate");

      for (WrappedGoal var8 : this.availableGoals) {
         if (!var8.isRunning() && !goalContainsAnyFlags(var8, this.disabledFlags) && goalCanBeReplacedForAllFlags(var8, this.lockedFlags) && var8.canUse()) {
            for (Goal.Flag var5 : var8.getFlags()) {
               WrappedGoal var6 = this.lockedFlags.getOrDefault(var5, NO_GOAL);
               var6.stop();
               this.lockedFlags.put(var5, var8);
            }

            var8.start();
         }
      }

      var1.pop();
      this.tickRunningGoals(true);
   }

   public void tickRunningGoals(boolean var1) {
      ProfilerFiller var2 = Profiler.get();
      var2.push("goalTick");

      for (WrappedGoal var4 : this.availableGoals) {
         if (var4.isRunning() && (var1 || var4.requiresUpdateEveryTick())) {
            var4.tick();
         }
      }

      var2.pop();
   }

   public Set<WrappedGoal> getAvailableGoals() {
      return this.availableGoals;
   }

   public void disableControlFlag(Goal.Flag var1) {
      this.disabledFlags.add(var1);
   }

   public void enableControlFlag(Goal.Flag var1) {
      this.disabledFlags.remove(var1);
   }

   public void setControlFlag(Goal.Flag var1, boolean var2) {
      if (var2) {
         this.enableControlFlag(var1);
      } else {
         this.disableControlFlag(var1);
      }
   }
}
