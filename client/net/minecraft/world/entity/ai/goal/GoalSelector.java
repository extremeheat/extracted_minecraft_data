package net.minecraft.world.entity.ai.goal;

import com.google.common.collect.Sets;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GoalSelector {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final WrappedGoal NO_GOAL = new WrappedGoal(2147483647, new Goal() {
      public boolean canUse() {
         return false;
      }
   }) {
      public boolean isRunning() {
         return false;
      }
   };
   private final Map<Goal.Flag, WrappedGoal> lockedFlags = new EnumMap(Goal.Flag.class);
   private final Set<WrappedGoal> availableGoals = Sets.newLinkedHashSet();
   private final Supplier<ProfilerFiller> profiler;
   private final EnumSet<Goal.Flag> disabledFlags = EnumSet.noneOf(Goal.Flag.class);
   private int newGoalRate = 3;

   public GoalSelector(Supplier<ProfilerFiller> var1) {
      super();
      this.profiler = var1;
   }

   public void addGoal(int var1, Goal var2) {
      this.availableGoals.add(new WrappedGoal(var1, var2));
   }

   public void removeGoal(Goal var1) {
      this.availableGoals.stream().filter((var1x) -> {
         return var1x.getGoal() == var1;
      }).filter(WrappedGoal::isRunning).forEach(WrappedGoal::stop);
      this.availableGoals.removeIf((var1x) -> {
         return var1x.getGoal() == var1;
      });
   }

   public void tick() {
      ProfilerFiller var1 = (ProfilerFiller)this.profiler.get();
      var1.push("goalCleanup");
      this.getRunningGoals().filter((var1x) -> {
         boolean var2;
         if (var1x.isRunning()) {
            Stream var10000 = var1x.getFlags().stream();
            EnumSet var10001 = this.disabledFlags;
            var10001.getClass();
            if (!var10000.anyMatch(var10001::contains) && var1x.canContinueToUse()) {
               var2 = false;
               return var2;
            }
         }

         var2 = true;
         return var2;
      }).forEach(Goal::stop);
      this.lockedFlags.forEach((var1x, var2) -> {
         if (!var2.isRunning()) {
            this.lockedFlags.remove(var1x);
         }

      });
      var1.pop();
      var1.push("goalUpdate");
      this.availableGoals.stream().filter((var0) -> {
         return !var0.isRunning();
      }).filter((var1x) -> {
         Stream var10000 = var1x.getFlags().stream();
         EnumSet var10001 = this.disabledFlags;
         var10001.getClass();
         return var10000.noneMatch(var10001::contains);
      }).filter((var1x) -> {
         return var1x.getFlags().stream().allMatch((var2) -> {
            return ((WrappedGoal)this.lockedFlags.getOrDefault(var2, NO_GOAL)).canBeReplacedBy(var1x);
         });
      }).filter(WrappedGoal::canUse).forEach((var1x) -> {
         var1x.getFlags().forEach((var2) -> {
            WrappedGoal var3 = (WrappedGoal)this.lockedFlags.getOrDefault(var2, NO_GOAL);
            var3.stop();
            this.lockedFlags.put(var2, var1x);
         });
         var1x.start();
      });
      var1.pop();
      var1.push("goalTick");
      this.getRunningGoals().forEach(WrappedGoal::tick);
      var1.pop();
   }

   public Stream<WrappedGoal> getRunningGoals() {
      return this.availableGoals.stream().filter(WrappedGoal::isRunning);
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
