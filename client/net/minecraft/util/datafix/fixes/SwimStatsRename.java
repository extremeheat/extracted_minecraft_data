package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import net.minecraft.util.datafix.TypeReferences;

public class SwimStatsRename extends DataFix {
   public SwimStatsRename(Schema var1, boolean var2) {
      super(var1, var2);
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = this.getOutputSchema().getType(TypeReferences.field_211291_g);
      Type var2 = this.getInputSchema().getType(TypeReferences.field_211291_g);
      OpticFinder var3 = var2.findField("stats");
      OpticFinder var4 = var3.type().findField("minecraft:custom");
      OpticFinder var5 = DSL.namespacedString().finder();
      return this.fixTypeEverywhereTyped("SwimStatsRenameFix", var2, var1, (var3x) -> {
         return var3x.updateTyped(var3, (var2) -> {
            return var2.updateTyped(var4, (var1) -> {
               return var1.update(var5, (var0) -> {
                  if (var0.equals("minecraft:swim_one_cm")) {
                     return "minecraft:walk_on_water_one_cm";
                  } else {
                     return var0.equals("minecraft:dive_one_cm") ? "minecraft:walk_under_water_one_cm" : var0;
                  }
               });
            });
         });
      });
   }
}
