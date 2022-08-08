package net.minecraft.client.player.inventory;

import com.google.common.collect.ForwardingList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class Hotbar extends ForwardingList<ItemStack> {
   private final NonNullList<ItemStack> items;

   public Hotbar() {
      super();
      this.items = NonNullList.withSize(Inventory.getSelectionSize(), ItemStack.EMPTY);
   }

   protected List<ItemStack> delegate() {
      return this.items;
   }

   public ListTag createTag() {
      ListTag var1 = new ListTag();
      Iterator var2 = this.delegate().iterator();

      while(var2.hasNext()) {
         ItemStack var3 = (ItemStack)var2.next();
         var1.add(var3.save(new CompoundTag()));
      }

      return var1;
   }

   public void fromTag(ListTag var1) {
      List var2 = this.delegate();

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         var2.set(var3, ItemStack.of(var1.getCompound(var3)));
      }

   }

   public boolean isEmpty() {
      Iterator var1 = this.delegate().iterator();

      ItemStack var2;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         var2 = (ItemStack)var1.next();
      } while(var2.isEmpty());

      return false;
   }
}
