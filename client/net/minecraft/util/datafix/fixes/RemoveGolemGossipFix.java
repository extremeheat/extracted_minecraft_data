package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class RemoveGolemGossipFix extends NamedEntityFix {
   public RemoveGolemGossipFix(Schema var1, boolean var2) {
      super(var1, var2, "Remove Golem Gossip Fix", References.ENTITY, "minecraft:villager");
   }

   protected Typed<?> fix(Typed<?> var1) {
      return var1.update(DSL.remainderFinder(), RemoveGolemGossipFix::fixValue);
   }

   private static Dynamic<?> fixValue(Dynamic<?> var0) {
      return var0.update("Gossips", (var1) -> {
         return var0.createList(var1.asStream().filter((var0x) -> {
            return !var0x.get("Type").asString("").equals("golem");
         }));
      });
   }
}
