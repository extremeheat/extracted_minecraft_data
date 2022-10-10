package net.minecraft.client.settings;

import com.google.common.collect.ForwardingList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;

public class HotbarSnapshot extends ForwardingList<ItemStack> {
   private final NonNullList<ItemStack> field_199424_a;

   public HotbarSnapshot() {
      super();
      this.field_199424_a = NonNullList.func_191197_a(InventoryPlayer.func_70451_h(), ItemStack.field_190927_a);
   }

   protected List<ItemStack> delegate() {
      return this.field_199424_a;
   }

   public NBTTagList func_192834_a() {
      NBTTagList var1 = new NBTTagList();
      Iterator var2 = this.delegate().iterator();

      while(var2.hasNext()) {
         ItemStack var3 = (ItemStack)var2.next();
         var1.add((INBTBase)var3.func_77955_b(new NBTTagCompound()));
      }

      return var1;
   }

   public void func_192833_a(NBTTagList var1) {
      List var2 = this.delegate();

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         var2.set(var3, ItemStack.func_199557_a(var1.func_150305_b(var3)));
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
      } while(var2.func_190926_b());

      return false;
   }
}
