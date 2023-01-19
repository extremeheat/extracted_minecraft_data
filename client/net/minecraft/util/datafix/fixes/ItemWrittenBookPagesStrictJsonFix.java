package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;
import org.apache.commons.lang3.StringUtils;

public class ItemWrittenBookPagesStrictJsonFix extends DataFix {
   public ItemWrittenBookPagesStrictJsonFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   public Dynamic<?> fixTag(Dynamic<?> var1) {
      return var1.update(
         "pages",
         var1x -> (Dynamic)DataFixUtils.orElse(
               var1x.asStreamOpt()
                  .map(
                     var0x -> var0x.map(
                           var0xx -> {
                              if (!var0xx.asString().result().isPresent()) {
                                 return var0xx;
                              } else {
                                 String var1xx = var0xx.asString("");
                                 Object var2 = null;
                                 if (!"null".equals(var1xx) && !StringUtils.isEmpty(var1xx)) {
                                    if (var1xx.charAt(0) == '"' && var1xx.charAt(var1xx.length() - 1) == '"'
                                       || var1xx.charAt(0) == '{' && var1xx.charAt(var1xx.length() - 1) == '}') {
                                       try {
                                          var2 = GsonHelper.fromNullableJson(BlockEntitySignTextStrictJsonFix.GSON, var1xx, Component.class, true);
                                          if (var2 == null) {
                                             var2 = CommonComponents.EMPTY;
                                          }
                                       } catch (Exception var6) {
                                       }
               
                                       if (var2 == null) {
                                          try {
                                             var2 = Component.Serializer.fromJson(var1xx);
                                          } catch (Exception var5) {
                                          }
                                       }
               
                                       if (var2 == null) {
                                          try {
                                             var2 = Component.Serializer.fromJsonLenient(var1xx);
                                          } catch (Exception var4) {
                                          }
                                       }
               
                                       if (var2 == null) {
                                          var2 = Component.literal(var1xx);
                                       }
                                    } else {
                                       var2 = Component.literal(var1xx);
                                    }
                                 } else {
                                    var2 = CommonComponents.EMPTY;
                                 }
               
                                 return var0xx.createString(Component.Serializer.toJson((Component)var2));
                              }
                           }
                        )
                  )
                  .map(var1::createList)
                  .result(),
               var1.emptyList()
            )
      );
   }

   public TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.ITEM_STACK);
      OpticFinder var2 = var1.findField("tag");
      return this.fixTypeEverywhereTyped(
         "ItemWrittenBookPagesStrictJsonFix", var1, var2x -> var2x.updateTyped(var2, var1xx -> var1xx.update(DSL.remainderFinder(), this::fixTag))
      );
   }
}
