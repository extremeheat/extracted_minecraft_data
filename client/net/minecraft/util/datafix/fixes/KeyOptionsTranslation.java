package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.util.datafix.TypeReferences;

public class KeyOptionsTranslation extends DataFix {
   public KeyOptionsTranslation(Schema var1, boolean var2) {
      super(var1, var2);
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("OptionsKeyTranslationFix", this.getInputSchema().getType(TypeReferences.field_211289_e), (var0) -> {
         return var0.update(DSL.remainderFinder(), (var0x) -> {
            return (Dynamic)var0x.getMapValues().map((var1) -> {
               return var0x.createMap((Map)var1.entrySet().stream().map((var1x) -> {
                  if (((String)((Dynamic)var1x.getKey()).getStringValue().orElse("")).startsWith("key_")) {
                     String var2 = (String)((Dynamic)var1x.getValue()).getStringValue().orElse("");
                     if (!var2.startsWith("key.mouse") && !var2.startsWith("scancode.")) {
                        return Pair.of(var1x.getKey(), var0x.createString("key.keyboard." + var2.substring("key.".length())));
                     }
                  }

                  return Pair.of(var1x.getKey(), var1x.getValue());
               }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
            }).orElse(var0x);
         });
      });
   }
}
