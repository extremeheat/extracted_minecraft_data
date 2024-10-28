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
import java.util.Optional;
import java.util.Set;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class ItemRemoveBlockEntityTagFix extends DataFix {
   private final Set<String> items;

   public ItemRemoveBlockEntityTagFix(Schema var1, boolean var2, Set<String> var3) {
      super(var1, var2);
      this.items = var3;
   }

   public TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.ITEM_STACK);
      OpticFinder var2 = DSL.fieldFinder("id", DSL.named(References.ITEM_NAME.typeName(), NamespacedSchema.namespacedString()));
      OpticFinder var3 = var1.findField("tag");
      OpticFinder var4 = var3.type().findField("BlockEntityTag");
      return this.fixTypeEverywhereTyped("ItemRemoveBlockEntityTagFix", var1, (var4x) -> {
         Optional var5 = var4x.getOptional(var2);
         if (var5.isPresent() && this.items.contains(((Pair)var5.get()).getSecond())) {
            Optional var6 = var4x.getOptionalTyped(var3);
            if (var6.isPresent()) {
               Typed var7 = (Typed)var6.get();
               Optional var8 = var7.getOptionalTyped(var4);
               if (var8.isPresent()) {
                  Optional var9 = var7.write().result();
                  Dynamic var10 = var9.isPresent() ? (Dynamic)var9.get() : (Dynamic)var7.get(DSL.remainderFinder());
                  Dynamic var11 = var10.remove("BlockEntityTag");
                  Optional var12 = var3.type().readTyped(var11).result();
                  if (var12.isEmpty()) {
                     return var4x;
                  }

                  return var4x.set(var3, (Typed)((Pair)var12.get()).getFirst());
               }
            }
         }

         return var4x;
      });
   }
}
