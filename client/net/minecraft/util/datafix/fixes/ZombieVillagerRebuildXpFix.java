package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.Optional;

public class ZombieVillagerRebuildXpFix extends NamedEntityFix {
   public ZombieVillagerRebuildXpFix(Schema var1, boolean var2) {
      super(var1, var2, "Zombie Villager XP rebuild", References.ENTITY, "minecraft:zombie_villager");
   }

   protected Typed<?> fix(Typed<?> var1) {
      return var1.update(DSL.remainderFinder(), (var0) -> {
         Optional var1 = var0.get("Xp").asNumber().result();
         if (var1.isEmpty()) {
            int var2 = var0.get("VillagerData").get("level").asInt(1);
            return var0.set("Xp", var0.createInt(VillagerRebuildLevelAndXpFix.getMinXpPerLevel(var2)));
         } else {
            return var0;
         }
      });
   }
}
