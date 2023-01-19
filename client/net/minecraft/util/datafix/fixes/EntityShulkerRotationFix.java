package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.List;

public class EntityShulkerRotationFix extends NamedEntityFix {
   public EntityShulkerRotationFix(Schema var1) {
      super(var1, false, "EntityShulkerRotationFix", References.ENTITY, "minecraft:shulker");
   }

   public Dynamic<?> fixTag(Dynamic<?> var1) {
      List var2 = var1.get("Rotation").asList(var0 -> var0.asDouble(180.0));
      if (!var2.isEmpty()) {
         var2.set(0, var2.get(0) - 180.0);
         return var1.set("Rotation", var1.createList(var2.stream().map(var1::createDouble)));
      } else {
         return var1;
      }
   }

   @Override
   protected Typed<?> fix(Typed<?> var1) {
      return var1.update(DSL.remainderFinder(), this::fixTag);
   }
}
