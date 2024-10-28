package net.minecraft.world.item;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenCustomHashSet;
import java.util.Set;
import javax.annotation.Nullable;

public class ItemStackLinkedSet {
   private static final Hash.Strategy<? super ItemStack> TYPE_AND_TAG = new Hash.Strategy<ItemStack>() {
      public int hashCode(@Nullable ItemStack var1) {
         return ItemStack.hashItemAndComponents(var1);
      }

      public boolean equals(@Nullable ItemStack var1, @Nullable ItemStack var2) {
         return var1 == var2 || var1 != null && var2 != null && var1.isEmpty() == var2.isEmpty() && ItemStack.isSameItemSameComponents(var1, var2);
      }

      // $FF: synthetic method
      public boolean equals(@Nullable final Object var1, @Nullable final Object var2) {
         return this.equals((ItemStack)var1, (ItemStack)var2);
      }

      // $FF: synthetic method
      public int hashCode(@Nullable final Object var1) {
         return this.hashCode((ItemStack)var1);
      }
   };

   public ItemStackLinkedSet() {
      super();
   }

   public static Set<ItemStack> createTypeAndComponentsSet() {
      return new ObjectLinkedOpenCustomHashSet(TYPE_AND_TAG);
   }
}
