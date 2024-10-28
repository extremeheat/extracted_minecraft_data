package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import net.minecraft.util.datafix.ComponentDataFixUtils;

public class ItemWrittenBookPagesStrictJsonFix extends DataFix {
   public ItemWrittenBookPagesStrictJsonFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   public Dynamic<?> fixTag(Dynamic<?> var1) {
      return var1.update("pages", (var1x) -> {
         DataResult var10000 = var1x.asStreamOpt().map((var0) -> {
            return var0.map(ComponentDataFixUtils::rewriteFromLenient);
         });
         Objects.requireNonNull(var1);
         return (Dynamic)DataFixUtils.orElse(var10000.map(var1::createList).result(), var1.emptyList());
      });
   }

   public TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.ITEM_STACK);
      OpticFinder var2 = var1.findField("tag");
      return this.fixTypeEverywhereTyped("ItemWrittenBookPagesStrictJsonFix", var1, (var2x) -> {
         return var2x.updateTyped(var2, (var1) -> {
            return var1.update(DSL.remainderFinder(), this::fixTag);
         });
      });
   }
}
