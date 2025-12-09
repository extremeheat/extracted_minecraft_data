package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;

public class DebugProfileOverlayReferenceFix extends DataFix {
   public DebugProfileOverlayReferenceFix(Schema var1) {
      super(var1, false);
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("DebugProfileOverlayReferenceFix", this.getInputSchema().getType(References.DEBUG_PROFILE), (var0) -> var0.update(DSL.remainderFinder(), (var0x) -> var0x.update("custom", (var0) -> var0.updateMapValues((var0x) -> var0x.mapSecond((var0) -> var0.asString("").equals("inF3") ? var0.createString("inOverlay") : var0)))));
   }
}
