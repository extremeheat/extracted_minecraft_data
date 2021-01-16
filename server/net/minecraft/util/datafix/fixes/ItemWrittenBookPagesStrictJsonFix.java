package net.minecraft.util.datafix.fixes;

import com.google.gson.JsonParseException;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.GsonHelper;
import org.apache.commons.lang3.StringUtils;

public class ItemWrittenBookPagesStrictJsonFix extends DataFix {
   public ItemWrittenBookPagesStrictJsonFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   public Dynamic<?> fixTag(Dynamic<?> var1) {
      return var1.update("pages", (var1x) -> {
         DataResult var10000 = var1x.asStreamOpt().map((var0) -> {
            return var0.map((var0x) -> {
               if (!var0x.asString().result().isPresent()) {
                  return var0x;
               } else {
                  String var1 = var0x.asString("");
                  Object var2 = null;
                  if (!"null".equals(var1) && !StringUtils.isEmpty(var1)) {
                     if (var1.charAt(0) == '"' && var1.charAt(var1.length() - 1) == '"' || var1.charAt(0) == '{' && var1.charAt(var1.length() - 1) == '}') {
                        try {
                           var2 = (Component)GsonHelper.fromJson(BlockEntitySignTextStrictJsonFix.GSON, var1, Component.class, true);
                           if (var2 == null) {
                              var2 = TextComponent.EMPTY;
                           }
                        } catch (JsonParseException var6) {
                        }

                        if (var2 == null) {
                           try {
                              var2 = Component.Serializer.fromJson(var1);
                           } catch (JsonParseException var5) {
                           }
                        }

                        if (var2 == null) {
                           try {
                              var2 = Component.Serializer.fromJsonLenient(var1);
                           } catch (JsonParseException var4) {
                           }
                        }

                        if (var2 == null) {
                           var2 = new TextComponent(var1);
                        }
                     } else {
                        var2 = new TextComponent(var1);
                     }
                  } else {
                     var2 = TextComponent.EMPTY;
                  }

                  return var0x.createString(Component.Serializer.toJson((Component)var2));
               }
            });
         });
         var1.getClass();
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
