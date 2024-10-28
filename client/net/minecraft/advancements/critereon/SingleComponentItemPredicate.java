package net.minecraft.advancements.critereon;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;

public interface SingleComponentItemPredicate<T> extends ItemSubPredicate {
   default boolean matches(ItemStack var1) {
      Object var2 = var1.get(this.componentType());
      return var2 != null && this.matches(var1, var2);
   }

   DataComponentType<T> componentType();

   boolean matches(ItemStack var1, T var2);
}
