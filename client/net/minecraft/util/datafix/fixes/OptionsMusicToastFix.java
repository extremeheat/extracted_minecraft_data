package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;

public class OptionsMusicToastFix extends DataFix {
   public OptionsMusicToastFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("OptionsMusicToastFix", this.getInputSchema().getType(References.OPTIONS), (var0) -> var0.update(DSL.remainderFinder(), (var0x) -> var0x.renameAndFixField("showNowPlayingToast", "musicToast", (var1) -> var0x.createString(var1.asString("false").equals("false") ? "never" : "pause_and_toast"))));
   }
}
