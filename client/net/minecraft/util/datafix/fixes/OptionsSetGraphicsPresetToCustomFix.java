package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;

public class OptionsSetGraphicsPresetToCustomFix extends DataFix {
   public OptionsSetGraphicsPresetToCustomFix(Schema var1) {
      super(var1, true);
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("graphicsPreset set to \"custom\"", this.getInputSchema().getType(References.OPTIONS), (var0) -> var0.update(DSL.remainderFinder(), (var0x) -> var0x.set("graphicsPreset", var0x.createString("custom"))));
   }
}
