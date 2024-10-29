package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;

public class EntitySalmonSizeFix extends NamedEntityFix {
   public EntitySalmonSizeFix(Schema var1) {
      super(var1, false, "EntitySalmonSizeFix", References.ENTITY, "minecraft:salmon");
   }

   protected Typed<?> fix(Typed<?> var1) {
      return var1.update(DSL.remainderFinder(), (var0) -> {
         String var1 = var0.get("type").asString("medium");
         return var1.equals("large") ? var0 : var0.set("type", var0.createString("medium"));
      });
   }
}
