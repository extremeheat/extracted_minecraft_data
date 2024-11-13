package net.minecraft.world.item.crafting.display;

import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface DisplayContentsFactory<T> {
   public interface ForStacks<T> extends DisplayContentsFactory<T> {
      default T forStack(Holder<Item> var1) {
         return (T)this.forStack(new ItemStack(var1));
      }

      default T forStack(Item var1) {
         return (T)this.forStack(new ItemStack(var1));
      }

      T forStack(ItemStack var1);
   }

   public interface ForRemainders<T> extends DisplayContentsFactory<T> {
      T addRemainder(T var1, List<T> var2);
   }
}
