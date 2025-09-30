package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class CopperGolemWeatherStateFix extends NamedEntityFix {
   public CopperGolemWeatherStateFix(Schema var1) {
      super(var1, false, "CopperGolemWeatherStateFix", References.ENTITY, "minecraft:copper_golem");
   }

   protected Typed<?> fix(Typed<?> var1) {
      return var1.update(DSL.remainderFinder(), (var0) -> var0.update("weather_state", CopperGolemWeatherStateFix::fixWeatherState));
   }

   private static Dynamic<?> fixWeatherState(Dynamic<?> var0) {
      Dynamic var10000;
      switch (var0.asInt(0)) {
         case 1 -> var10000 = var0.createString("exposed");
         case 2 -> var10000 = var0.createString("weathered");
         case 3 -> var10000 = var0.createString("oxidized");
         default -> var10000 = var0.createString("unaffected");
      }

      return var10000;
   }
}
