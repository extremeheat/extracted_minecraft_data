package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;

public class SignsOpFlagFix extends DataFix {
   public SignsOpFlagFix(final Schema outputSchema) {
      super(outputSchema, false);
   }

   private TypeRewriteRule makeRuleForType(final String entityName) {
      Type<?> entityType = this.getInputSchema().getChoiceType(References.BLOCK_ENTITY, entityName);
      OpticFinder<?> entityF = DSL.namedChoice(entityName, entityType);
      return this.fixTypeEverywhereTyped("Add allow-op flag to " + entityName, this.getInputSchema().getType(References.BLOCK_ENTITY), (input) -> input.updateTyped(entityF, entityType, (entity) -> entity.update(DSL.remainderFinder(), (remainder) -> remainder.set("allow_op_features", remainder.createBoolean(true)))));
   }

   protected TypeRewriteRule makeRule() {
      return TypeRewriteRule.seq(this.makeRuleForType("minecraft:sign"), this.makeRuleForType("minecraft:hanging_sign"));
   }
}
