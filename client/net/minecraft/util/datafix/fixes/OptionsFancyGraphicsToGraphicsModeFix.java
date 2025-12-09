package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class OptionsFancyGraphicsToGraphicsModeFix extends DataFix {
   public OptionsFancyGraphicsToGraphicsModeFix(Schema var1) {
      super(var1, true);
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("fancyGraphics to graphicsMode", this.getInputSchema().getType(References.OPTIONS), (var0) -> var0.update(DSL.remainderFinder(), (var0x) -> var0x.renameAndFixField("fancyGraphics", "graphicsMode", OptionsFancyGraphicsToGraphicsModeFix::fixGraphicsMode)));
   }

   private static <T> Dynamic<T> fixGraphicsMode(Dynamic<T> var0) {
      return "true".equals(var0.asString("true")) ? var0.createString("1") : var0.createString("0");
   }
}
