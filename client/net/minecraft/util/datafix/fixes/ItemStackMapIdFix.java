package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class ItemStackMapIdFix extends DataFix {
   public ItemStackMapIdFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   public TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.ITEM_STACK);
      OpticFinder var2 = DSL.fieldFinder("id", DSL.named(References.ITEM_NAME.typeName(), NamespacedSchema.namespacedString()));
      OpticFinder var3 = var1.findField("tag");
      return this.fixTypeEverywhereTyped("ItemInstanceMapIdFix", var1, (var2x) -> {
         Optional var3x = var2x.getOptional(var2);
         if (var3x.isPresent() && Objects.equals(((Pair)var3x.get()).getSecond(), "minecraft:filled_map")) {
            Dynamic var4 = (Dynamic)var2x.get(DSL.remainderFinder());
            Typed var5 = var2x.getOrCreateTyped(var3);
            Dynamic var6 = (Dynamic)var5.get(DSL.remainderFinder());
            var6 = var6.set("map", var6.createInt(var4.get("Damage").asInt(0)));
            return var2x.set(var3, var5.set(DSL.remainderFinder(), var6));
         } else {
            return var2x;
         }
      });
   }
}
