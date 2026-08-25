package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;

public class RemoveBlockTransformerComponentFix extends DataFix {
   public RemoveBlockTransformerComponentFix(final Schema outputSchema) {
      super(outputSchema, false);
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("Remove inline block transformer component", this.getInputSchema().getType(References.DATA_COMPONENTS), (components) -> components.update(DSL.remainderFinder(), (remainder) -> remainder.remove("minecraft:block_transformer")));
   }
}
