package net.minecraft.world.item;

import it.unimi.dsi.fastutil.Hash.Strategy;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenCustomHashSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;

public class ItemStackLinkedSet {
   private static final Strategy<? super ItemStack> TYPE_AND_TAG = new Strategy<ItemStack>() {
      public int hashCode(@Nullable ItemStack var1) {
         return ItemStackLinkedSet.hashStackAndTag(var1);
      }

      public boolean equals(@Nullable ItemStack var1, @Nullable ItemStack var2) {
         return var1 == var2 || var1 != null && var2 != null && var1.isEmpty() == var2.isEmpty() && ItemStack.isSameItemSameTags(var1, var2);
      }
   };

   public ItemStackLinkedSet() {
      super();
   }

   static int hashStackAndTag(@Nullable ItemStack var0) {
      if (var0 != null) {
         CompoundTag var1 = var0.getTag();
         int var2 = 31 + var0.getItem().hashCode();
         return 31 * var2 + (var1 == null ? 0 : var1.hashCode());
      } else {
         return 0;
      }
   }

   public static Set<ItemStack> createTypeAndTagSet() {
      return new ObjectLinkedOpenCustomHashSet(TYPE_AND_TAG);
   }
}
