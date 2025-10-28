package net.minecraft.world.inventory;

import net.minecraft.network.HashedPatchMap;
import net.minecraft.network.HashedStack;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public interface RemoteSlot {
   RemoteSlot PLACEHOLDER = new RemoteSlot() {
      public void receive(HashedStack var1) {
      }

      public void force(ItemStack var1) {
      }

      public boolean matches(ItemStack var1) {
         return true;
      }
   };

   void force(ItemStack var1);

   void receive(HashedStack var1);

   boolean matches(ItemStack var1);

   public static class Synchronized implements RemoteSlot {
      private final HashedPatchMap.HashGenerator hasher;
      private @Nullable ItemStack remoteStack = null;
      private @Nullable HashedStack remoteHash = null;

      public Synchronized(HashedPatchMap.HashGenerator var1) {
         super();
         this.hasher = var1;
      }

      public void force(ItemStack var1) {
         this.remoteStack = var1.copy();
         this.remoteHash = null;
      }

      public void receive(HashedStack var1) {
         this.remoteStack = null;
         this.remoteHash = var1;
      }

      public boolean matches(ItemStack var1) {
         if (this.remoteStack != null) {
            return ItemStack.matches(this.remoteStack, var1);
         } else if (this.remoteHash != null && this.remoteHash.matches(var1, this.hasher)) {
            this.remoteStack = var1.copy();
            return true;
         } else {
            return false;
         }
      }

      public void copyFrom(Synchronized var1) {
         this.remoteStack = var1.remoteStack;
         this.remoteHash = var1.remoteHash;
      }
   }
}
