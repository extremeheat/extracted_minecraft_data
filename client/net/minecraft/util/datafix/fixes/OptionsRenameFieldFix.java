package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;

public class OptionsRenameFieldFix extends DataFix {
   private final String fixName;
   private final String fieldFrom;
   private final String fieldTo;

   public OptionsRenameFieldFix(Schema var1, boolean var2, String var3, String var4, String var5) {
      super(var1, var2);
      this.fixName = var3;
      this.fieldFrom = var4;
      this.fieldTo = var5;
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped(this.fixName, this.getInputSchema().getType(References.OPTIONS), (var1) -> var1.update(DSL.remainderFinder(), (var1x) -> var1x.renameField(this.fieldFrom, this.fieldTo)));
   }
}
