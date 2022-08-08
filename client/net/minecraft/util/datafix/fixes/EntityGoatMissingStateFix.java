package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;

public class EntityGoatMissingStateFix extends NamedEntityFix {
   public EntityGoatMissingStateFix(Schema var1) {
      super(var1, false, "EntityGoatMissingStateFix", References.ENTITY, "minecraft:goat");
   }

   protected Typed<?> fix(Typed<?> var1) {
      return var1.update(DSL.remainderFinder(), (var0) -> {
         return var0.set("HasLeftHorn", var0.createBoolean(true)).set("HasRightHorn", var0.createBoolean(true));
      });
   }
}
