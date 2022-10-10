package net.minecraft.item.crafting;

import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWrittenBook;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class RecipeBookCloning extends IRecipeHidden {
   public RecipeBookCloning(ResourceLocation var1) {
      super(var1);
   }

   public boolean func_77569_a(IInventory var1, World var2) {
      if (!(var1 instanceof InventoryCrafting)) {
         return false;
      } else {
         int var3 = 0;
         ItemStack var4 = ItemStack.field_190927_a;

         for(int var5 = 0; var5 < var1.func_70302_i_(); ++var5) {
            ItemStack var6 = var1.func_70301_a(var5);
            if (!var6.func_190926_b()) {
               if (var6.func_77973_b() == Items.field_151164_bB) {
                  if (!var4.func_190926_b()) {
                     return false;
                  }

                  var4 = var6;
               } else {
                  if (var6.func_77973_b() != Items.field_151099_bA) {
                     return false;
                  }

                  ++var3;
               }
            }
         }

         return !var4.func_190926_b() && var4.func_77942_o() && var3 > 0;
      }
   }

   public ItemStack func_77572_b(IInventory var1) {
      int var2 = 0;
      ItemStack var3 = ItemStack.field_190927_a;

      for(int var4 = 0; var4 < var1.func_70302_i_(); ++var4) {
         ItemStack var5 = var1.func_70301_a(var4);
         if (!var5.func_190926_b()) {
            if (var5.func_77973_b() == Items.field_151164_bB) {
               if (!var3.func_190926_b()) {
                  return ItemStack.field_190927_a;
               }

               var3 = var5;
            } else {
               if (var5.func_77973_b() != Items.field_151099_bA) {
                  return ItemStack.field_190927_a;
               }

               ++var2;
            }
         }
      }

      if (!var3.func_190926_b() && var3.func_77942_o() && var2 >= 1 && ItemWrittenBook.func_179230_h(var3) < 2) {
         ItemStack var6 = new ItemStack(Items.field_151164_bB, var2);
         NBTTagCompound var7 = var3.func_77978_p().func_74737_b();
         var7.func_74768_a("generation", ItemWrittenBook.func_179230_h(var3) + 1);
         var6.func_77982_d(var7);
         return var6;
      } else {
         return ItemStack.field_190927_a;
      }
   }

   public NonNullList<ItemStack> func_179532_b(IInventory var1) {
      NonNullList var2 = NonNullList.func_191197_a(var1.func_70302_i_(), ItemStack.field_190927_a);

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         ItemStack var4 = var1.func_70301_a(var3);
         if (var4.func_77973_b().func_77634_r()) {
            var2.set(var3, new ItemStack(var4.func_77973_b().func_77668_q()));
         } else if (var4.func_77973_b() instanceof ItemWrittenBook) {
            ItemStack var5 = var4.func_77946_l();
            var5.func_190920_e(1);
            var2.set(var3, var5);
            break;
         }
      }

      return var2;
   }

   public IRecipeSerializer<?> func_199559_b() {
      return RecipeSerializers.field_199578_d;
   }

   public boolean func_194133_a(int var1, int var2) {
      return var1 >= 3 && var2 >= 3;
   }
}
