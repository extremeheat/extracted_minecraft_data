package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class VillagerFollowRangeFix extends NamedEntityFix {
   private static final double ORIGINAL_VALUE = 16.0;
   private static final double NEW_BASE_VALUE = 48.0;

   public VillagerFollowRangeFix(Schema var1) {
      super(var1, false, "Villager Follow Range Fix", References.ENTITY, "minecraft:villager");
   }

   protected Typed<?> fix(Typed<?> var1) {
      return var1.update(DSL.remainderFinder(), VillagerFollowRangeFix::fixValue);
   }

   private static Dynamic<?> fixValue(Dynamic<?> var0) {
      return var0.update("Attributes", (var1) -> var0.createList(var1.asStream().map((var0x) -> var0x.get("Name").asString("").equals("generic.follow_range") && var0x.get("Base").asDouble(0.0) == 16.0 ? var0x.set("Base", var0x.createDouble(48.0)) : var0x)));
   }
}
