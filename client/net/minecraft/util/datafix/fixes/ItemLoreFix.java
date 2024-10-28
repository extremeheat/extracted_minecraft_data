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
import java.util.stream.Stream;
import net.minecraft.util.datafix.ComponentDataFixUtils;

public class ItemLoreFix extends DataFix {
   public ItemLoreFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.ITEM_STACK);
      OpticFinder var2 = var1.findField("tag");
      return this.fixTypeEverywhereTyped("Item Lore componentize", var1, (var1x) -> {
         return var1x.updateTyped(var2, (var0) -> {
            return var0.update(DSL.remainderFinder(), (var0x) -> {
               return var0x.update("display", (var0) -> {
                  return var0.update("Lore", (var0x) -> {
                     DataResult var10000 = var0x.asStreamOpt().map(ItemLoreFix::fixLoreList);
                     Objects.requireNonNull(var0x);
                     return (Dynamic)DataFixUtils.orElse(var10000.map(var0x::createList).result(), var0x);
                  });
               });
            });
         });
      });
   }

   private static <T> Stream<Dynamic<T>> fixLoreList(Stream<Dynamic<T>> var0) {
      return var0.map(ComponentDataFixUtils::wrapLiteralStringAsComponent);
   }
}
