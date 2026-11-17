package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import net.minecraft.util.datafix.ExtraDataFixUtils;

public class RemoveDataComponentFix extends DataFix {
   private final String name;
   private final String componentId;

   public RemoveDataComponentFix(final Schema outputSchema, final String name, final String componentId) {
      super(outputSchema, false);
      this.name = name;
      this.componentId = componentId;
   }

   public final TypeRewriteRule makeRule() {
      Type<?> dataComponentsType = this.getInputSchema().getType(References.DATA_COMPONENTS);
      ExtraDataFixUtils.ComponentIdManipulator idRemover = ExtraDataFixUtils.ComponentIdManipulator.remove(this.componentId);
      return this.fixTypeEverywhereTyped(this.name, dataComponentsType, (components) -> components.update(DSL.remainderFinder(), (remainder) -> ExtraDataFixUtils.fixComponentsIds(remainder, idRemover, true)));
   }
}
