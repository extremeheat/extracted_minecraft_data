package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.stream.Stream;
import net.minecraft.network.chat.Component;

public class ItemLoreFix extends DataFix {
   public ItemLoreFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.ITEM_STACK);
      OpticFinder var2 = var1.findField("tag");
      return this.fixTypeEverywhereTyped(
         "Item Lore componentize",
         var1,
         var1x -> var1x.updateTyped(
               var2,
               var0x -> var0x.update(
                     DSL.remainderFinder(),
                     var0xx -> var0xx.update(
                           "display",
                           var0xxx -> var0xxx.update(
                                 "Lore",
                                 var0xxxx -> (Dynamic)DataFixUtils.orElse(
                                       var0xxxx.asStreamOpt().map(ItemLoreFix::fixLoreList).map(var0xxxx::createList).result(), var0xxxx
                                    )
                              )
                        )
                  )
            )
      );
   }

   private static <T> Stream<Dynamic<T>> fixLoreList(Stream<Dynamic<T>> var0) {
      return var0.map(var0x -> (Dynamic)DataFixUtils.orElse(var0x.asString().map(ItemLoreFix::fixLoreEntry).map(var0x::createString).result(), var0x));
   }

   private static String fixLoreEntry(String var0) {
      return Component.Serializer.toJson(Component.literal(var0));
   }
}
