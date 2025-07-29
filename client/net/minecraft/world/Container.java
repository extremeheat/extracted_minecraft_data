package net.minecraft.world;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface Container extends Clearable, Iterable<ItemStack> {
   float DEFAULT_DISTANCE_BUFFER = 4.0F;

   int getContainerSize();

   boolean isEmpty();

   ItemStack getItem(int var1);

   ItemStack removeItem(int var1, int var2);

   ItemStack removeItemNoUpdate(int var1);

   void setItem(int var1, ItemStack var2);

   default int getMaxStackSize() {
      return 99;
   }

   default int getMaxStackSize(ItemStack var1) {
      return Math.min(this.getMaxStackSize(), var1.getMaxStackSize());
   }

   void setChanged();

   boolean stillValid(Player var1);

   default void startOpen(ContainerUser var1) {
   }

   default void stopOpen(ContainerUser var1) {
   }

   default List<ContainerUser> getEntitiesWithContainerOpen() {
      return List.of();
   }

   default boolean canPlaceItem(int var1, ItemStack var2) {
      return true;
   }

   default boolean canTakeItem(Container var1, int var2, ItemStack var3) {
      return true;
   }

   default int countItem(Item var1) {
      int var2 = 0;

      for(ItemStack var4 : this) {
         if (var4.getItem().equals(var1)) {
            var2 += var4.getCount();
         }
      }

      return var2;
   }

   default boolean hasAnyOf(Set<Item> var1) {
      return this.hasAnyMatching((var1x) -> !var1x.isEmpty() && var1.contains(var1x.getItem()));
   }

   default boolean hasAnyMatching(Predicate<ItemStack> var1) {
      for(ItemStack var3 : this) {
         if (var1.test(var3)) {
            return true;
         }
      }

      return false;
   }

   static boolean stillValidBlockEntity(BlockEntity var0, Player var1) {
      return stillValidBlockEntity(var0, var1, 4.0F);
   }

   static boolean stillValidBlockEntity(BlockEntity var0, Player var1, float var2) {
      Level var3 = var0.getLevel();
      BlockPos var4 = var0.getBlockPos();
      if (var3 == null) {
         return false;
      } else {
         return var3.getBlockEntity(var4) != var0 ? false : var1.canInteractWithBlock(var4, (double)var2);
      }
   }

   default Iterator<ItemStack> iterator() {
      return new ContainerIterator(this);
   }

   public static class ContainerIterator implements Iterator<ItemStack> {
      private final Container container;
      private int index;
      private final int size;

      public ContainerIterator(Container var1) {
         super();
         this.container = var1;
         this.size = var1.getContainerSize();
      }

      public boolean hasNext() {
         return this.index < this.size;
      }

      public ItemStack next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            return this.container.getItem(this.index++);
         }
      }

      // $FF: synthetic method
      public Object next() {
         return this.next();
      }
   }
}
