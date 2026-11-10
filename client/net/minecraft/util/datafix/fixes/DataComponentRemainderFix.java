package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import net.minecraft.util.datafix.schemas.NamespacedSchema;
import org.jspecify.annotations.Nullable;

public abstract class DataComponentRemainderFix extends DataFix {
   private final String name;
   private final String componentId;
   private final String newComponentId;

   public DataComponentRemainderFix(final Schema outputSchema, final String name, final String componentId) {
      this(outputSchema, name, componentId, componentId);
   }

   public DataComponentRemainderFix(final Schema outputSchema, final String name, final String componentId, final String newComponentId) {
      super(outputSchema, false);
      if (componentId.equals("minecraft:tooltip_display")) {
         throw new IllegalArgumentException("Cannot fix minecraft:tooltip_display");
      } else {
         this.name = name;
         this.componentId = componentId;
         this.newComponentId = newComponentId;
      }
   }

   public final TypeRewriteRule makeRule() {
      Type<?> dataComponentsType = this.getInputSchema().getType(References.DATA_COMPONENTS);
      return this.fixTypeEverywhereTyped(this.name, dataComponentsType, (components) -> components.update(DSL.remainderFinder(), (remainder) -> {
            remainder = remainder.update("minecraft:tooltip_display", this::fixTooltipDisplay);
            Optional<? extends Dynamic<?>> component = remainder.get(this.componentId).result();
            if (component.isEmpty()) {
               return remainder;
            } else {
               Dynamic<?> newComponent = this.fixComponent((Dynamic)component.get());
               return remainder.remove(this.componentId).setFieldIfPresent(this.newComponentId, Optional.ofNullable(newComponent));
            }
         }));
   }

   private Dynamic<?> fixTooltipDisplay(final Dynamic<?> tooltipDisplay) {
      return tooltipDisplay.update("hidden_components", (hiddenComponents) -> (Dynamic)DataFixUtils.orElse(hiddenComponents.asStreamOpt().result().map((stream) -> hiddenComponents.createList(stream.map(this::fixComponentId))), hiddenComponents));
   }

   private Dynamic<?> fixComponentId(final Dynamic<?> component) {
      return NamespacedSchema.namespacedIdMatches(component, this.componentId) ? component.createString(this.newComponentId) : component;
   }

   protected abstract <T> @Nullable Dynamic<T> fixComponent(Dynamic<T> input);
}
