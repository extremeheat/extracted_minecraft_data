package net.minecraft.world;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;

public class ContainerHelper {
   public static final String TAG_ITEMS = "Items";

   public ContainerHelper() {
      super();
   }

   public static ItemStack removeItem(List<ItemStack> var0, int var1, int var2) {
      return var1 >= 0 && var1 < var0.size() && !((ItemStack)var0.get(var1)).isEmpty() && var2 > 0 ? ((ItemStack)var0.get(var1)).split(var2) : ItemStack.EMPTY;
   }

   public static ItemStack takeItem(List<ItemStack> var0, int var1) {
      return var1 >= 0 && var1 < var0.size() ? (ItemStack)var0.set(var1, ItemStack.EMPTY) : ItemStack.EMPTY;
   }

   public static CompoundTag saveAllItems(CompoundTag var0, NonNullList<ItemStack> var1, HolderLookup.Provider var2) {
      return saveAllItems(var0, var1, true, var2);
   }

   public static CompoundTag saveAllItems(CompoundTag var0, NonNullList<ItemStack> var1, boolean var2, HolderLookup.Provider var3) {
      ListTag var4 = new ListTag();

      for(int var5 = 0; var5 < var1.size(); ++var5) {
         ItemStack var6 = (ItemStack)var1.get(var5);
         if (!var6.isEmpty()) {
            CompoundTag var7 = new CompoundTag();
            var7.putByte("Slot", (byte)var5);
            var4.add(var6.save(var3, var7));
         }
      }

      if (!var4.isEmpty() || var2) {
         var0.put("Items", var4);
      }

      return var0;
   }

   public static void loadAllItems(CompoundTag var0, NonNullList<ItemStack> var1, HolderLookup.Provider var2) {
      ListTag var3 = var0.getList("Items", 10);

      for(int var4 = 0; var4 < var3.size(); ++var4) {
         CompoundTag var5 = var3.getCompound(var4);
         int var6 = var5.getByte("Slot") & 255;
         if (var6 >= 0 && var6 < var1.size()) {
            var1.set(var6, (ItemStack)ItemStack.parse(var2, var5).orElse(ItemStack.EMPTY));
         }
      }

   }

   public static int clearOrCountMatchingItems(Container var0, Predicate<ItemStack> var1, int var2, boolean var3) {
      int var4 = 0;

      for(int var5 = 0; var5 < var0.getContainerSize(); ++var5) {
         ItemStack var6 = var0.getItem(var5);
         int var7 = clearOrCountMatchingItems(var6, var1, var2 - var4, var3);
         if (var7 > 0 && !var3 && var6.isEmpty()) {
            var0.setItem(var5, ItemStack.EMPTY);
         }

         var4 += var7;
      }

      return var4;
   }

   public static int clearOrCountMatchingItems(ItemStack var0, Predicate<ItemStack> var1, int var2, boolean var3) {
      if (!var0.isEmpty() && var1.test(var0)) {
         if (var3) {
            return var0.getCount();
         } else {
            int var4 = var2 < 0 ? var0.getCount() : Math.min(var2, var0.getCount());
            var0.shrink(var4);
            return var4;
         }
      } else {
         return 0;
      }
   }
}
