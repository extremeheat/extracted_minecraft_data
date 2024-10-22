package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class VillagerSetCanPickUpLootFix extends NamedEntityFix {
   private static final String CAN_PICK_UP_LOOT = "CanPickUpLoot";

   public VillagerSetCanPickUpLootFix(Schema var1) {
      super(var1, true, "Villager CanPickUpLoot default value", References.ENTITY, "Villager");
   }

   @Override
   protected Typed<?> fix(Typed<?> var1) {
      return var1.update(DSL.remainderFinder(), VillagerSetCanPickUpLootFix::fixValue);
   }

   private static Dynamic<?> fixValue(Dynamic<?> var0) {
      return var0.set("CanPickUpLoot", var0.createBoolean(true));
   }
}
