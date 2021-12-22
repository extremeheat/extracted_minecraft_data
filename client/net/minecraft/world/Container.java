package net.minecraft.world;

import java.util.Set;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface Container extends Clearable {
   int LARGE_MAX_STACK_SIZE = 64;

   int getContainerSize();

   boolean isEmpty();

   ItemStack getItem(int var1);

   ItemStack removeItem(int var1, int var2);

   ItemStack removeItemNoUpdate(int var1);

   void setItem(int var1, ItemStack var2);

   default int getMaxStackSize() {
      return 64;
   }

   void setChanged();

   boolean stillValid(Player var1);

   default void startOpen(Player var1) {
   }

   default void stopOpen(Player var1) {
   }

   default boolean canPlaceItem(int var1, ItemStack var2) {
      return true;
   }

   default int countItem(Item var1) {
      int var2 = 0;

      for(int var3 = 0; var3 < this.getContainerSize(); ++var3) {
         ItemStack var4 = this.getItem(var3);
         if (var4.getItem().equals(var1)) {
            var2 += var4.getCount();
         }
      }

      return var2;
   }

   default boolean hasAnyOf(Set<Item> var1) {
      for(int var2 = 0; var2 < this.getContainerSize(); ++var2) {
         ItemStack var3 = this.getItem(var2);
         if (var1.contains(var3.getItem()) && var3.getCount() > 0) {
            return true;
         }
      }

      return false;
   }
}
