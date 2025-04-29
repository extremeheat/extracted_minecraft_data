package net.minecraft.world;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

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

   public static void saveAllItems(ValueOutput var0, NonNullList<ItemStack> var1) {
      saveAllItems(var0, var1, true);
   }

   public static void saveAllItems(ValueOutput var0, NonNullList<ItemStack> var1, boolean var2) {
      ValueOutput.TypedOutputList var3 = var0.list("Items", ItemStackWithSlot.CODEC);

      for(int var4 = 0; var4 < var1.size(); ++var4) {
         ItemStack var5 = (ItemStack)var1.get(var4);
         if (!var5.isEmpty()) {
            var3.add(new ItemStackWithSlot(var4, var5));
         }
      }

      if (var3.isEmpty() && !var2) {
         var0.discard("Items");
      }

   }

   public static void loadAllItems(ValueInput var0, NonNullList<ItemStack> var1) {
      for(ItemStackWithSlot var3 : var0.listOrEmpty("Items", ItemStackWithSlot.CODEC)) {
         if (var3.isValidInContainer(var1.size())) {
            var1.set(var3.slot(), var3.stack());
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
