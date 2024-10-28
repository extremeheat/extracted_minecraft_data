package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;

public class WeaponSmithChestLootTableFix extends NamedEntityFix {
   public WeaponSmithChestLootTableFix(Schema var1, boolean var2) {
      super(var1, var2, "WeaponSmithChestLootTableFix", References.BLOCK_ENTITY, "minecraft:chest");
   }

   protected Typed<?> fix(Typed<?> var1) {
      return var1.update(DSL.remainderFinder(), (var0) -> {
         String var1 = var0.get("LootTable").asString("");
         return var1.equals("minecraft:chests/village_blacksmith") ? var0.set("LootTable", var0.createString("minecraft:chests/village/village_weaponsmith")) : var0;
      });
   }
}
