package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Optional;
import java.util.Set;
import net.minecraft.Util;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class ItemRemoveBlockEntityTagFix extends DataFix {
   private final Set<String> blockEntityIdsToDrop;

   public ItemRemoveBlockEntityTagFix(Schema var1, Set<String> var2) {
      super(var1, true);
      this.blockEntityIdsToDrop = var2;
   }

   public TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.ITEM_STACK);
      OpticFinder var2 = var1.findField("tag");
      OpticFinder var3 = var2.type().findField("BlockEntityTag");
      OpticFinder var4 = DSL.fieldFinder("id", NamespacedSchema.namespacedString());
      return TypeRewriteRule.seq(this.fixTypeEverywhereTyped("ItemRemoveBlockEntityTagFix", var1, (var4x) -> var4x.updateTyped(var2, (var4xx) -> {
            Optional var5 = var4xx.getOptionalTyped(var3);
            if (var5.isEmpty()) {
               return var4xx;
            } else {
               String var6 = (String)((Typed)var5.get()).getOptional(var4).orElse("");
               return !this.blockEntityIdsToDrop.contains(var6) ? var4xx : Util.writeAndReadTypedOrThrow(var4xx, var2.type(), (var0) -> var0.remove("BlockEntityTag"));
            }
         })), this.convertUnchecked("ItemRemoveBlockEntityTagFix - update block entity type", this.getInputSchema().getType(References.BLOCK_ENTITY), this.getOutputSchema().getType(References.BLOCK_ENTITY)));
   }
}
