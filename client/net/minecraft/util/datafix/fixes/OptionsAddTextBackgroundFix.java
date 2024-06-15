package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class OptionsAddTextBackgroundFix extends DataFix {
   public OptionsAddTextBackgroundFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped(
         "OptionsAddTextBackgroundFix",
         this.getInputSchema().getType(References.OPTIONS),
         var1 -> var1.update(
               DSL.remainderFinder(),
               var1x -> (Dynamic)DataFixUtils.orElse(
                     var1x.get("chatOpacity")
                        .asString()
                        .map(var2 -> var1x.set("textBackgroundOpacity", var1x.createDouble(this.calculateBackground(var2))))
                        .result(),
                     var1x
                  )
            )
      );
   }

   private double calculateBackground(String var1) {
      try {
         double var2 = 0.9 * Double.parseDouble(var1) + 0.1;
         return var2 / 2.0;
      } catch (NumberFormatException var4) {
         return 0.5;
      }
   }
}
