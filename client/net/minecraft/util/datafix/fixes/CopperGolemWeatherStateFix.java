package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class CopperGolemWeatherStateFix extends NamedEntityFix {
   public CopperGolemWeatherStateFix(final Schema outputSchema) {
      super(outputSchema, false, "CopperGolemWeatherStateFix", References.ENTITY, "minecraft:copper_golem");
   }

   protected Typed<?> fix(final Typed<?> entity) {
      return entity.update(DSL.remainderFinder(), (tag) -> tag.update("weather_state", CopperGolemWeatherStateFix::fixWeatherState));
   }

   private static Dynamic<?> fixWeatherState(final Dynamic<?> value) {
      Dynamic var10000;
      switch (value.asInt(0)) {
         case 1 -> var10000 = value.createString("exposed");
         case 2 -> var10000 = value.createString("weathered");
         case 3 -> var10000 = value.createString("oxidized");
         default -> var10000 = value.createString("unaffected");
      }

      return var10000;
   }
}
