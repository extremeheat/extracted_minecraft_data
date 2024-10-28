package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import net.minecraft.util.datafix.ExtraDataFixUtils;

public class LegacyDragonFightFix extends DataFix {
   public LegacyDragonFightFix(Schema var1) {
      super(var1, false);
   }

   private static <T> Dynamic<T> fixDragonFight(Dynamic<T> var0) {
      return var0.update("ExitPortalLocation", ExtraDataFixUtils::fixBlockPos);
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("LegacyDragonFightFix", this.getInputSchema().getType(References.LEVEL), (var0) -> {
         return var0.update(DSL.remainderFinder(), (var0x) -> {
            OptionalDynamic var1 = var0x.get("DragonFight");
            if (var1.result().isPresent()) {
               return var0x;
            } else {
               Dynamic var2 = var0x.get("DimensionData").get("1").get("DragonFight").orElseEmptyMap();
               return var0x.set("DragonFight", fixDragonFight(var2));
            }
         });
      });
   }
}
