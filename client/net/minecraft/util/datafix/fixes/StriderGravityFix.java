package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class StriderGravityFix extends NamedEntityFix {
   public StriderGravityFix(Schema var1, boolean var2) {
      super(var1, var2, "StriderGravityFix", References.ENTITY, "minecraft:strider");
   }

   public Dynamic<?> fixTag(Dynamic<?> var1) {
      return var1.get("NoGravity").asBoolean(false) ? var1.set("NoGravity", var1.createBoolean(false)) : var1;
   }

   protected Typed<?> fix(Typed<?> var1) {
      return var1.update(DSL.remainderFinder(), this::fixTag);
   }
}
