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
      return this.fixTypeEverywhereTyped(
         "OptionsMenuBlurrinessFix",
         this.getInputSchema().getType(References.OPTIONS),
         var1 -> var1.update(
               DSL.remainderFinder(),
               var1x -> var1x.update("menuBackgroundBlurriness", var1xx -> var1xx.createInt(this.convertToIntRange(var1xx.asString("0.5"))))
            )
      );
   }

   private int convertToIntRange(String var1) {
      try {
         return Math.round(Float.parseFloat(var1) * 10.0F);
      } catch (NumberFormatException var3) {
         return 5;
      }
   }
}
