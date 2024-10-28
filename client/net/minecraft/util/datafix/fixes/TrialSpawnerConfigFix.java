package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class TrialSpawnerConfigFix extends NamedEntityWriteReadFix {
   public TrialSpawnerConfigFix(Schema var1) {
      super(var1, true, "Trial Spawner config tag fixer", References.BLOCK_ENTITY, "minecraft:trial_spawner");
   }

   private static <T> Dynamic<T> moveToConfigTag(Dynamic<T> var0) {
      List var1 = List.of("spawn_range", "total_mobs", "simultaneous_mobs", "total_mobs_added_per_player", "simultaneous_mobs_added_per_player", "ticks_between_spawn", "spawn_potentials", "loot_tables_to_eject", "items_to_drop_when_ominous");
      HashMap var2 = new HashMap(var1.size());
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         String var4 = (String)var3.next();
         Optional var5 = var0.get(var4).get().result();
         if (var5.isPresent()) {
            var2.put(var0.createString(var4), (Dynamic)var5.get());
            var0 = var0.remove(var4);
         }
      }

      return var2.isEmpty() ? var0 : var0.set("normal_config", var0.createMap(var2));
   }

   protected <T> Dynamic<T> fix(Dynamic<T> var1) {
      return moveToConfigTag(var1);
   }
}
