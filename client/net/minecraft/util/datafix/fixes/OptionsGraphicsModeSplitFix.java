package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class OptionsGraphicsModeSplitFix extends DataFix {
   private final String newFieldName;
   private final String valueIfFast;
   private final String valueIfFancy;
   private final String valueIfFabulous;

   public OptionsGraphicsModeSplitFix(Schema var1, String var2, String var3, String var4, String var5) {
      super(var1, true);
      this.newFieldName = var2;
      this.valueIfFast = var3;
      this.valueIfFancy = var4;
      this.valueIfFabulous = var5;
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("graphicsMode split to " + this.newFieldName, this.getInputSchema().getType(References.OPTIONS), (var1) -> var1.update(DSL.remainderFinder(), (var1x) -> (Dynamic)DataFixUtils.orElseGet(var1x.get("graphicsMode").asString().map((var2) -> var1x.set(this.newFieldName, var1x.createString(this.getValue(var2)))).result(), () -> var1x.set(this.newFieldName, var1x.createString(this.valueIfFancy)))));
   }

   private String getValue(String var1) {
      String var10000;
      switch (var1) {
         case "2" -> var10000 = this.valueIfFabulous;
         case "0" -> var10000 = this.valueIfFast;
         default -> var10000 = this.valueIfFancy;
      }

      return var10000;
   }
}
