package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;

public class OptionsMenuBlurrinessFix extends DataFix {
   public OptionsMenuBlurrinessFix(Schema var1) {
      super(var1, false);
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("OptionsMenuBlurrinessFix", this.getInputSchema().getType(References.OPTIONS), (var1) -> {
         return var1.update(DSL.remainderFinder(), (var1x) -> {
            return var1x.update("menuBackgroundBlurriness", (var1) -> {
               return var1.createInt(this.convertToIntRange(var1.asString("0.5")));
            });
         });
      });
   }

   private int convertToIntRange(String var1) {
      try {
         return Math.round(Float.parseFloat(var1) * 10.0F);
      } catch (NumberFormatException var3) {
         return 5;
      }
   }
}
