package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;

public class RandomSequenceSettingsFix extends DataFix {
   public RandomSequenceSettingsFix(Schema var1) {
      super(var1, false);
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("RandomSequenceSettingsFix", this.getInputSchema().getType(References.SAVED_DATA_RANDOM_SEQUENCES), (var0) -> {
         return var0.update(DSL.remainderFinder(), (var0x) -> {
            return var0x.update("data", (var0) -> {
               return var0.emptyMap().set("sequences", var0);
            });
         });
      });
   }
}
