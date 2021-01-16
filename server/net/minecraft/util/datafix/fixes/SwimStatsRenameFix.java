package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class SwimStatsRenameFix extends DataFix {
   public SwimStatsRenameFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = this.getOutputSchema().getType(References.STATS);
      Type var2 = this.getInputSchema().getType(References.STATS);
      OpticFinder var3 = var2.findField("stats");
      OpticFinder var4 = var3.type().findField("minecraft:custom");
      OpticFinder var5 = NamespacedSchema.namespacedString().finder();
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
