package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;

public class BlockStateFieldNamesFix extends DataFix {
   public BlockStateFieldNamesFix(final Schema outputSchema) {
      super(outputSchema, false);
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("BlockStateFieldNamesFix", this.getInputSchema().getType(References.BLOCK_STATE), (input) -> input.update(DSL.remainderFinder(), (remainder) -> remainder.renameField("Name", "id").renameField("Properties", "properties")));
   }
}
