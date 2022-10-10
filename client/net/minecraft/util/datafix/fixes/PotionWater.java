package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import net.minecraft.util.datafix.TypeReferences;

public class PotionWater extends DataFix {
   public PotionWater(Schema var1, boolean var2) {
      super(var1, var2);
   }

   public TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(TypeReferences.field_211295_k);
      OpticFinder var2 = DSL.fieldFinder("id", DSL.named(TypeReferences.field_211301_q.typeName(), DSL.namespacedString()));
      OpticFinder var3 = var1.findField("tag");
      return this.fixTypeEverywhereTyped("ItemWaterPotionFix", var1, (var2x) -> {
         Optional var3x = var2x.getOptional(var2);
         if (var3x.isPresent()) {
            String var4 = (String)((Pair)var3x.get()).getSecond();
            if ("minecraft:potion".equals(var4) || "minecraft:splash_potion".equals(var4) || "minecraft:lingering_potion".equals(var4) || "minecraft:tipped_arrow".equals(var4)) {
               Typed var5 = var2x.getOrCreateTyped(var3);
               Dynamic var6 = (Dynamic)var5.get(DSL.remainderFinder());
               if (!var6.get("Potion").flatMap(Dynamic::getStringValue).isPresent()) {
                  var6 = var6.set("Potion", var6.createString("minecraft:water"));
               }

               return var2x.set(var3, var5.set(DSL.remainderFinder(), var6));
            }
         }

         return var2x;
      });
   }
}
