package net.minecraft.world.entity.monster.piglin;

import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.hoglin.Hoglin;

public class StartHuntingHoglin {
   public StartHuntingHoglin() {
      super();
   }

   public static OneShot<Piglin> create() {
      return BehaviorBuilder.create((var0) -> {
         return var0.group(var0.present(MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN), var0.absent(MemoryModuleType.ANGRY_AT), var0.absent(MemoryModuleType.HUNTED_RECENTLY), var0.registered(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS)).apply(var0, (var1, var2, var3, var4) -> {
            return (var3x, var4x, var5) -> {
               if (!var4x.isBaby() && !var0.tryGet(var4).map((var0x) -> {
                  return var0x.stream().anyMatch(StartHuntingHoglin::hasHuntedRecently);
               }).isPresent()) {
                  Hoglin var7 = (Hoglin)var0.get(var1);
                  PiglinAi.setAngerTarget(var4x, var7);
                  PiglinAi.dontKillAnyMoreHoglinsForAWhile(var4x);
                  PiglinAi.broadcastAngerTarget(var4x, var7);
                  var0.tryGet(var4).ifPresent((var0x) -> {
                     var0x.forEach(PiglinAi::dontKillAnyMoreHoglinsForAWhile);
                  });
                  return true;
               } else {
                  return false;
               }
            };
         });
      });
   }

   private static boolean hasHuntedRecently(AbstractPiglin var0) {
      return var0.getBrain().hasMemoryValue(MemoryModuleType.HUNTED_RECENTLY);
   }
}
