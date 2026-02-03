package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class OptionsAmbientOcclusionFix extends DataFix {
   public OptionsAmbientOcclusionFix(final Schema outputSchema) {
      super(outputSchema, false);
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("OptionsAmbientOcclusionFix", this.getInputSchema().getType(References.OPTIONS), (input) -> input.update(DSL.remainderFinder(), (tag) -> (Dynamic)DataFixUtils.orElse(tag.get("ao").asString().map((value) -> tag.set("ao", tag.createString(updateValue(value)))).result(), tag)));
   }

   private static String updateValue(final String value) {
      String var10000;
      switch (value) {
         case "0":
            var10000 = "false";
            break;
         case "1":
         case "2":
            var10000 = "true";
            break;
         default:
            var10000 = value;
      }

      return var10000;
   }
}
