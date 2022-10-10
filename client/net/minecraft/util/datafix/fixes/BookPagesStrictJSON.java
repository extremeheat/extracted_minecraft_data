package net.minecraft.util.datafix.fixes;

import com.google.gson.JsonParseException;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Optional;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import org.apache.commons.lang3.StringUtils;

public class BookPagesStrictJSON extends DataFix {
   public BookPagesStrictJSON(Schema var1, boolean var2) {
      super(var1, var2);
   }

   public Dynamic<?> func_209633_a(Dynamic<?> var1) {
      return var1.update("pages", (var1x) -> {
         Optional var10000 = var1x.getStream().map((var0) -> {
            return var0.map((var0x) -> {
               if (!var0x.getStringValue().isPresent()) {
                  return var0x;
               } else {
                  String var1 = (String)var0x.getStringValue().get();
                  Object var2 = null;
                  if (!"null".equals(var1) && !StringUtils.isEmpty(var1)) {
                     if (var1.charAt(0) == '"' && var1.charAt(var1.length() - 1) == '"' || var1.charAt(0) == '{' && var1.charAt(var1.length() - 1) == '}') {
                        try {
                           var2 = (ITextComponent)JsonUtils.func_188176_a(SignStrictJSON.field_188225_a, var1, ITextComponent.class, true);
                           if (var2 == null) {
                              var2 = new TextComponentString("");
                           }
                        } catch (JsonParseException var6) {
                        }

                        if (var2 == null) {
                           try {
                              var2 = ITextComponent.Serializer.func_150699_a(var1);
                           } catch (JsonParseException var5) {
                           }
                        }

                        if (var2 == null) {
                           try {
                              var2 = ITextComponent.Serializer.func_186877_b(var1);
                           } catch (JsonParseException var4) {
                           }
                        }

                        if (var2 == null) {
                           var2 = new TextComponentString(var1);
                        }
                     } else {
                        var2 = new TextComponentString(var1);
                     }
                  } else {
                     var2 = new TextComponentString("");
                  }

                  return var0x.createString(ITextComponent.Serializer.func_150696_a((ITextComponent)var2));
               }
            });
         });
         var1.getClass();
         return (Dynamic)DataFixUtils.orElse(var10000.map(var1::createList), var1.emptyList());
      });
   }

   public TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(TypeReferences.field_211295_k);
      OpticFinder var2 = var1.findField("tag");
      return this.fixTypeEverywhereTyped("ItemWrittenBookPagesStrictJsonFix", var1, (var2x) -> {
         return var2x.updateTyped(var2, (var1) -> {
            return var1.update(DSL.remainderFinder(), this::func_209633_a);
         });
      });
   }
}
