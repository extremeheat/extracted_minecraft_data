package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;

public class LegacyDimensionIdFix extends DataFix {
   public LegacyDimensionIdFix(Schema var1) {
      super(var1, false);
   }

   public TypeRewriteRule makeRule() {
      TypeRewriteRule var1 = this.fixTypeEverywhereTyped("PlayerLegacyDimensionFix", this.getInputSchema().getType(References.PLAYER), (var1x) -> var1x.update(DSL.remainderFinder(), this::fixPlayer));
      Type var2 = this.getInputSchema().getType(References.SAVED_DATA_MAP_DATA);
      OpticFinder var3 = var2.findField("data");
      TypeRewriteRule var4 = this.fixTypeEverywhereTyped("MapLegacyDimensionFix", var2, (var2x) -> var2x.updateTyped(var3, (var1) -> var1.update(DSL.remainderFinder(), this::fixMap)));
      return TypeRewriteRule.seq(var1, var4);
   }

   private <T> Dynamic<T> fixMap(Dynamic<T> var1) {
      return var1.update("dimension", this::fixDimensionId);
   }

   private <T> Dynamic<T> fixPlayer(Dynamic<T> var1) {
      return var1.update("Dimension", this::fixDimensionId);
   }

   private <T> Dynamic<T> fixDimensionId(Dynamic<T> var1) {
      return (Dynamic)DataFixUtils.orElse(var1.asNumber().result().map((var1x) -> {
         Dynamic var10000;
         switch (var1x.intValue()) {
            case -1 -> var10000 = var1.createString("minecraft:the_nether");
            case 1 -> var10000 = var1.createString("minecraft:the_end");
            default -> var10000 = var1.createString("minecraft:overworld");
         }

         return var10000;
      }), var1);
   }
}
