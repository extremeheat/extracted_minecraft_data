package net.minecraft.core.component.predicates;

import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;

public record AnyValue(DataComponentType<?> type) implements DataComponentPredicate {
   public AnyValue {
      super();
   }

   public boolean matches(final DataComponentGetter components) {
      return components.get(this.type) != null;
   }
}
