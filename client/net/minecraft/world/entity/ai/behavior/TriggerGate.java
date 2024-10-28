package net.minecraft.world.entity.ai.behavior;

import com.mojang.datafixers.util.Pair;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.Trigger;

public class TriggerGate {
   public TriggerGate() {
      super();
   }

   public static <E extends LivingEntity> OneShot<E> triggerOneShuffled(List<Pair<? extends Trigger<? super E>, Integer>> var0) {
      return triggerGate(var0, GateBehavior.OrderPolicy.SHUFFLED, GateBehavior.RunningPolicy.RUN_ONE);
   }

   public static <E extends LivingEntity> OneShot<E> triggerGate(List<Pair<? extends Trigger<? super E>, Integer>> var0, GateBehavior.OrderPolicy var1, GateBehavior.RunningPolicy var2) {
      ShufflingList var3 = new ShufflingList();
      var0.forEach((var1x) -> {
         var3.add((Trigger)var1x.getFirst(), (Integer)var1x.getSecond());
      });
      return BehaviorBuilder.create((var3x) -> {
         return var3x.point((var3xx, var4, var5) -> {
            if (var1 == GateBehavior.OrderPolicy.SHUFFLED) {
               var3.shuffle();
            }

            Iterator var7 = var3.iterator();

            while(var7.hasNext()) {
               Trigger var8 = (Trigger)var7.next();
               if (var8.trigger(var3xx, var4, var5) && var2 == GateBehavior.RunningPolicy.RUN_ONE) {
                  break;
               }
            }

            return true;
         });
      });
   }
}
