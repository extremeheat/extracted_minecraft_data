package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;

public class DebugProfileRenameEntryFix extends DataFix {
   private final String oldName;
   private final String newName;

   public DebugProfileRenameEntryFix(final Schema outputSchema, final String oldName, final String newName) {
      super(outputSchema, false);
      this.oldName = oldName;
      this.newName = newName;
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("DebugProfilePostEffectsFix", this.getInputSchema().getType(References.DEBUG_PROFILE), (input) -> input.update(DSL.remainderFinder(), (remainder) -> remainder.update("custom", (custom) -> custom.renameField(this.oldName, this.newName))));
   }
}
