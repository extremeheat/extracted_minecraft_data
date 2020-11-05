package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class EntityWolfColorFix extends NamedEntityFix {
   public EntityWolfColorFix(Schema var1, boolean var2) {
      super(var1, var2, "EntityWolfColorFix", References.ENTITY, "minecraft:wolf");
   }

   public Dynamic<?> fixTag(Dynamic<?> var1) {
      return var1.update("CollarColor", (var0) -> {
         return var0.createByte((byte)(15 - var0.asInt(0)));
      });
   }

   protected Typed<?> fix(Typed<?> var1) {
      return var1.update(DSL.remainderFinder(), this::fixTag);
   }
}
