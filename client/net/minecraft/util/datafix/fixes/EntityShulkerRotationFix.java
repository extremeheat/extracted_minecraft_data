package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class EntityShulkerRotationFix extends NamedEntityFix {
   public EntityShulkerRotationFix(Schema var1) {
      super(var1, false, "EntityShulkerRotationFix", References.ENTITY, "minecraft:shulker");
   }

   public Dynamic<?> fixTag(Dynamic<?> var1) {
      List var2 = var1.get("Rotation").asList((var0) -> {
         return var0.asDouble(180.0D);
      });
      if (!var2.isEmpty()) {
         var2.set(0, (Double)var2.get(0) - 180.0D);
         Stream var10003 = var2.stream();
         Objects.requireNonNull(var1);
         return var1.set("Rotation", var1.createList(var10003.map(var1::createDouble)));
      } else {
         return var1;
      }
   }

   protected Typed<?> fix(Typed<?> var1) {
      return var1.update(DSL.remainderFinder(), this::fixTag);
   }
}
