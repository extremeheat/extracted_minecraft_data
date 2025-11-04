package net.minecraft.advancements.criterion;

import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.predicates.DataComponentPredicate;

public interface SingleComponentItemPredicate<T> extends DataComponentPredicate {
   default boolean matches(DataComponentGetter var1) {
      Object var2 = var1.get(this.componentType());
      return var2 != null && this.matches(var2);
   }

   DataComponentType<T> componentType();

   boolean matches(T var1);
}
