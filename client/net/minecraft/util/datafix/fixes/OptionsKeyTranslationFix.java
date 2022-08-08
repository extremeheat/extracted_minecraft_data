package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Map;
import java.util.stream.Collectors;

public class OptionsKeyTranslationFix extends DataFix {
   public OptionsKeyTranslationFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("OptionsKeyTranslationFix", this.getInputSchema().getType(References.OPTIONS), (var0) -> {
         return var0.update(DSL.remainderFinder(), (var0x) -> {
            return (Dynamic)var0x.getMapValues().map((var1) -> {
               return var0x.createMap((Map)var1.entrySet().stream().map((var1x) -> {
                  if (((Dynamic)var1x.getKey()).asString("").startsWith("key_")) {
                     String var2 = ((Dynamic)var1x.getValue()).asString("");
                     if (!var2.startsWith("key.mouse") && !var2.startsWith("scancode.")) {
                        return Pair.of((Dynamic)var1x.getKey(), var0x.createString("key.keyboard." + var2.substring("key.".length())));
                     }
                  }

                  return Pair.of((Dynamic)var1x.getKey(), (Dynamic)var1x.getValue());
               }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
            }).result().orElse(var0x);
         });
      });
   }
}
