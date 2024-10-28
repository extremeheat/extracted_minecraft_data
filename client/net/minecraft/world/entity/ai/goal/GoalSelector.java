package net.minecraft.world.entity.ai.goal;

import com.google.common.annotations.VisibleForTesting;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.util.profiling.ProfilerFiller;

public class GoalSelector {
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
   private final Set<WrappedGoal> availableGoals = new ObjectLinkedOpenHashSet();
   private final Supplier<ProfilerFiller> profiler;
   private final EnumSet<Goal.Flag> disabledFlags = EnumSet.noneOf(Goal.Flag.class);

   public GoalSelector(Supplier<ProfilerFiller> var1) {
      super();
      this.profiler = var1;
   }

   public void addGoal(int var1, Goal var2) {
      this.availableGoals.add(new WrappedGoal(var1, var2));
   }

   @VisibleForTesting
   public void removeAllGoals(Predicate<Goal> var1) {
      this.availableGoals.removeIf((var1x) -> {
         return var1.test(var1x.getGoal());
      });
   }

   public void removeGoal(Goal var1) {
      Iterator var2 = this.availableGoals.iterator();

      while(var2.hasNext()) {
         WrappedGoal var3 = (WrappedGoal)var2.next();
         if (var3.getGoal() == var1 && var3.isRunning()) {
            var3.stop();
         }
      }

      this.availableGoals.removeIf((var1x) -> {
         return var1x.getGoal() == var1;
      });
   }

   private static boolean goalContainsAnyFlags(WrappedGoal var0, EnumSet<Goal.Flag> var1) {
      Iterator var2 = var0.getFlags().iterator();

      Goal.Flag var3;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         var3 = (Goal.Flag)var2.next();
      } while(!var1.contains(var3));

      return true;
   }

   private static boolean goalCanBeReplacedForAllFlags(WrappedGoal var0, Map<Goal.Flag, WrappedGoal> var1) {
      Iterator var2 = var0.getFlags().iterator();

      Goal.Flag var3;
      do {
         if (!var2.hasNext()) {
            return true;
         }

         var3 = (Goal.Flag)var2.next();
      } while(((WrappedGoal)var1.getOrDefault(var3, NO_GOAL)).canBeReplacedBy(var0));

      return false;
   }

   public void tick() {
      ProfilerFiller var1 = (ProfilerFiller)this.profiler.get();
      var1.push("goalCleanup");
      Iterator var2 = this.availableGoals.iterator();

      while(true) {
         WrappedGoal var3;
         do {
            do {
               if (!var2.hasNext()) {
                  this.lockedFlags.entrySet().removeIf((var0) -> {
                     return !((WrappedGoal)var0.getValue()).isRunning();
                  });
                  var1.pop();
                  var1.push("goalUpdate");
                  var2 = this.availableGoals.iterator();

                  while(true) {
                     do {
                        do {
                           do {
                              do {
                                 if (!var2.hasNext()) {
                                    var1.pop();
                                    this.tickRunningGoals(true);
                                    return;
                                 }

                                 var3 = (WrappedGoal)var2.next();
                              } while(var3.isRunning());
                           } while(goalContainsAnyFlags(var3, this.disabledFlags));
                        } while(!goalCanBeReplacedForAllFlags(var3, this.lockedFlags));
                     } while(!var3.canUse());

                     Iterator var4 = var3.getFlags().iterator();

                     while(var4.hasNext()) {
                        Goal.Flag var5 = (Goal.Flag)var4.next();
                        WrappedGoal var6 = (WrappedGoal)this.lockedFlags.getOrDefault(var5, NO_GOAL);
                        var6.stop();
                        this.lockedFlags.put(var5, var3);
                     }

                     var3.start();
                  }
               }

               var3 = (WrappedGoal)var2.next();
            } while(!var3.isRunning());
         } while(!goalContainsAnyFlags(var3, this.disabledFlags) && var3.canContinueToUse());

         var3.stop();
      }
   }

   public void tickRunningGoals(boolean var1) {
      ProfilerFiller var2 = (ProfilerFiller)this.profiler.get();
      var2.push("goalTick");
      Iterator var3 = this.availableGoals.iterator();

      while(true) {
         WrappedGoal var4;
         do {
            do {
               if (!var3.hasNext()) {
                  var2.pop();
                  return;
               }

               var4 = (WrappedGoal)var3.next();
            } while(!var4.isRunning());
         } while(!var1 && !var4.requiresUpdateEveryTick());

         var4.tick();
      }
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
