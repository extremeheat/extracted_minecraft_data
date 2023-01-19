package net.minecraft.world.entity.ai.goal;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

public class GoalSelector {
   private static final Logger LOGGER = LogUtils.getLogger();
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
   private final Set<WrappedGoal> availableGoals = Sets.newLinkedHashSet();
   private final Supplier<ProfilerFiller> profiler;
   private final EnumSet<Goal.Flag> disabledFlags = EnumSet.noneOf(Goal.Flag.class);
   private int tickCount;
   private int newGoalRate = 3;

   public GoalSelector(Supplier<ProfilerFiller> var1) {
      super();
      this.profiler = var1;
   }

   public void addGoal(int var1, Goal var2) {
      this.availableGoals.add(new WrappedGoal(var1, var2));
   }

   @VisibleForTesting
   public void removeAllGoals() {
      this.availableGoals.clear();
   }

   public void removeGoal(Goal var1) {
      this.availableGoals.stream().filter(var1x -> var1x.getGoal() == var1).filter(WrappedGoal::isRunning).forEach(WrappedGoal::stop);
      this.availableGoals.removeIf(var1x -> var1x.getGoal() == var1);
   }

   private static boolean goalContainsAnyFlags(WrappedGoal var0, EnumSet<Goal.Flag> var1) {
      for(Goal.Flag var3 : var0.getFlags()) {
         if (var1.contains(var3)) {
            return true;
         }
      }

      return false;
   }

   private static boolean goalCanBeReplacedForAllFlags(WrappedGoal var0, Map<Goal.Flag, WrappedGoal> var1) {
      for(Goal.Flag var3 : var0.getFlags()) {
         if (!var1.getOrDefault(var3, NO_GOAL).canBeReplacedBy(var0)) {
            return false;
         }
      }

      return true;
   }

   public void tick() {
      ProfilerFiller var1 = this.profiler.get();
      var1.push("goalCleanup");

      for(WrappedGoal var3 : this.availableGoals) {
         if (var3.isRunning() && (goalContainsAnyFlags(var3, this.disabledFlags) || !var3.canContinueToUse())) {
            var3.stop();
         }
      }

      Iterator var7 = this.lockedFlags.entrySet().iterator();

      while(var7.hasNext()) {
         Entry var9 = (Entry)var7.next();
         if (!((WrappedGoal)var9.getValue()).isRunning()) {
            var7.remove();
         }
      }

      var1.pop();
      var1.push("goalUpdate");

      for(WrappedGoal var10 : this.availableGoals) {
         if (!var10.isRunning() && !goalContainsAnyFlags(var10, this.disabledFlags) && goalCanBeReplacedForAllFlags(var10, this.lockedFlags) && var10.canUse()
            )
          {
            for(Goal.Flag var5 : var10.getFlags()) {
               WrappedGoal var6 = this.lockedFlags.getOrDefault(var5, NO_GOAL);
               var6.stop();
               this.lockedFlags.put(var5, var10);
            }

            var10.start();
         }
      }

      var1.pop();
      this.tickRunningGoals(true);
   }

   public void tickRunningGoals(boolean var1) {
      ProfilerFiller var2 = this.profiler.get();
      var2.push("goalTick");

      for(WrappedGoal var4 : this.availableGoals) {
         if (var4.isRunning() && (var1 || var4.requiresUpdateEveryTick())) {
            var4.tick();
         }
      }

      var2.pop();
   }

   public Set<WrappedGoal> getAvailableGoals() {
      return this.availableGoals;
   }

   public Stream<WrappedGoal> getRunningGoals() {
      return this.availableGoals.stream().filter(WrappedGoal::isRunning);
   }

   public void setNewGoalRate(int var1) {
      this.newGoalRate = var1;
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
