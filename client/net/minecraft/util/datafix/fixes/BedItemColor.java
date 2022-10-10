package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.util.datafix.TypeReferences;

public class BedItemColor extends DataFix {
   public BedItemColor(Schema var1, boolean var2) {
      super(var1, var2);
   }

   public TypeRewriteRule makeRule() {
      OpticFinder var1 = DSL.fieldFinder("id", DSL.named(TypeReferences.field_211301_q.typeName(), DSL.namespacedString()));
      return this.fixTypeEverywhereTyped("BedItemColorFix", this.getInputSchema().getType(TypeReferences.field_211295_k), (var1x) -> {
         Optional var2 = var1x.getOptional(var1);
         if (var2.isPresent() && Objects.equals(((Pair)var2.get()).getSecond(), "minecraft:bed")) {
            Dynamic var3 = (Dynamic)var1x.get(DSL.remainderFinder());
            if (var3.getShort("Damage") == 0) {
               return var1x.set(DSL.remainderFinder(), var3.set("Damage", var3.createShort((short)14)));
            }
         }

         return var1x;
      });
   }
}
