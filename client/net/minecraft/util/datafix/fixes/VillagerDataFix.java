package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class VillagerDataFix extends NamedEntityFix {
   public VillagerDataFix(Schema var1, String var2) {
      super(var1, false, "Villager profession data fix (" + var2 + ")", References.ENTITY, var2);
   }

   protected Typed<?> fix(Typed<?> var1) {
      Dynamic var2 = (Dynamic)var1.get(DSL.remainderFinder());
      return var1.set(DSL.remainderFinder(), var2.remove("Profession").remove("Career").remove("CareerLevel").set("VillagerData", var2.createMap(ImmutableMap.of(var2.createString("type"), var2.createString("minecraft:plains"), var2.createString("profession"), var2.createString(upgradeData(var2.get("Profession").asInt(0), var2.get("Career").asInt(0))), var2.createString("level"), (Dynamic)DataFixUtils.orElse(var2.get("CareerLevel").result(), var2.createInt(1))))));
   }

   private static String upgradeData(int var0, int var1) {
      if (var0 == 0) {
         if (var1 == 2) {
            return "minecraft:fisherman";
         } else if (var1 == 3) {
            return "minecraft:shepherd";
         } else {
            return var1 == 4 ? "minecraft:fletcher" : "minecraft:farmer";
         }
      } else if (var0 == 1) {
         return var1 == 2 ? "minecraft:cartographer" : "minecraft:librarian";
      } else if (var0 == 2) {
         return "minecraft:cleric";
      } else if (var0 == 3) {
         if (var1 == 2) {
            return "minecraft:weaponsmith";
         } else {
            return var1 == 3 ? "minecraft:toolsmith" : "minecraft:armorer";
         }
      } else if (var0 == 4) {
         return var1 == 2 ? "minecraft:leatherworker" : "minecraft:butcher";
      } else {
         return var0 == 5 ? "minecraft:nitwit" : "minecraft:none";
      }
   }
}
