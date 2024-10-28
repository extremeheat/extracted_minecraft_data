package net.minecraft.world.level.storage.loot;

import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;

public interface ContainerComponentManipulator<T> {
   DataComponentType<T> type();

   T empty();

   T setContents(T var1, Stream<ItemStack> var2);

   Stream<ItemStack> getContents(T var1);

   default void setContents(ItemStack var1, T var2, Stream<ItemStack> var3) {
      Object var4 = var1.getOrDefault(this.type(), var2);
      Object var5 = this.setContents(var4, var3);
      var1.set(this.type(), var5);
   }

   default void setContents(ItemStack var1, Stream<ItemStack> var2) {
      this.setContents(var1, this.empty(), var2);
   }

   default void modifyItems(ItemStack var1, UnaryOperator<ItemStack> var2) {
      Object var3 = var1.get(this.type());
      if (var3 != null) {
         UnaryOperator var4 = (var1x) -> {
            if (var1x.isEmpty()) {
               return var1x;
            } else {
               ItemStack var2x = (ItemStack)var2.apply(var1x);
               var2x.limitSize(var2x.getMaxStackSize());
               return var2x;
            }
         };
         this.setContents(var1, this.getContents(var3).map(var4));
      }

   }
}
