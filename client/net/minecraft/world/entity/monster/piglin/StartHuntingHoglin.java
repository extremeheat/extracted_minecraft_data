package net.minecraft.world.entity.monster.piglin;

import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.hoglin.Hoglin;

public class StartHuntingHoglin {
   public StartHuntingHoglin() {
      super();
   }

   public static OneShot<Piglin> create() {
      return BehaviorBuilder.create(
         var0 -> var0.group(
                  var0.present(MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN),
                  var0.absent(MemoryModuleType.ANGRY_AT),
                  var0.absent(MemoryModuleType.HUNTED_RECENTLY),
                  var0.registered(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS)
               )
               .apply(var0, (var1, var2, var3, var4) -> (var3x, var4x, var5) -> {
                     if (!var4x.isBaby()
                        && !var0.<List>tryGet(var4).map(var0xxx -> var0xxx.stream().anyMatch(StartHuntingHoglin::hasHuntedRecently)).isPresent()) {
                        Hoglin var7 = var0.get(var1);
                        PiglinAi.setAngerTarget(var4x, var7);
                        PiglinAi.dontKillAnyMoreHoglinsForAWhile(var4x);
                        PiglinAi.broadcastAngerTarget(var4x, var7);
                        var0.<List>tryGet(var4).ifPresent(var0xxx -> var0xxx.forEach(PiglinAi::dontKillAnyMoreHoglinsForAWhile));
                        return true;
                     } else {
                        return false;
                     }
                  })
      );
   }

   private static boolean hasHuntedRecently(AbstractPiglin var0) {
      return var0.getBrain().hasMemoryValue(MemoryModuleType.HUNTED_RECENTLY);
   }
}
