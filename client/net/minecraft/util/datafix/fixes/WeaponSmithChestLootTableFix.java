package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;

public class WeaponSmithChestLootTableFix extends NamedEntityFix {
   public WeaponSmithChestLootTableFix(Schema var1, boolean var2) {
      super(var1, var2, "WeaponSmithChestLootTableFix", References.BLOCK_ENTITY, "minecraft:chest");
   }

   @Override
   protected Typed<?> fix(Typed<?> var1) {
      return var1.update(
         DSL.remainderFinder(),
         var0 -> {
            String var1x = var0.get("LootTable").asString("");
            return var1x.equals("minecraft:chests/village_blacksmith")
               ? var0.set("LootTable", var0.createString("minecraft:chests/village/village_weaponsmith"))
               : var0;
         }
      );
   }
}
