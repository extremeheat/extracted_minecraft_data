package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

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
      return this.fixTypeEverywhereTyped(this.name, dataComponentsType, (components) -> components.update(DSL.remainderFinder(), (remainder) -> remainder.remove(this.componentId).update("minecraft:tooltip_display", this::fixTooltipDisplay)));
   }

   private Dynamic<?> fixTooltipDisplay(final Dynamic<?> tooltipDisplay) {
      return tooltipDisplay.update("hidden_components", (hiddenComponents) -> (Dynamic)DataFixUtils.orElse(hiddenComponents.asStreamOpt().result().map((stream) -> hiddenComponents.createList(stream.filter((component) -> !NamespacedSchema.namespacedIdMatches(component, this.componentId)))), hiddenComponents));
   }
}
