package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmorDyeable;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class RecipesArmorDyes extends IRecipeHidden {
   public RecipesArmorDyes(ResourceLocation var1) {
      super(var1);
   }

   public boolean func_77569_a(IInventory var1, World var2) {
      if (!(var1 instanceof InventoryCrafting)) {
         return false;
      } else {
         ItemStack var3 = ItemStack.field_190927_a;
         ArrayList var4 = Lists.newArrayList();

         for(int var5 = 0; var5 < var1.func_70302_i_(); ++var5) {
            ItemStack var6 = var1.func_70301_a(var5);
            if (!var6.func_190926_b()) {
               if (var6.func_77973_b() instanceof ItemArmorDyeable) {
                  if (!var3.func_190926_b()) {
                     return false;
                  }

                  var3 = var6;
               } else {
                  if (!(var6.func_77973_b() instanceof ItemDye)) {
                     return false;
                  }

                  var4.add(var6);
               }
            }
         }

         return !var3.func_190926_b() && !var4.isEmpty();
      }
   }

   public ItemStack func_77572_b(IInventory var1) {
      ItemStack var2 = ItemStack.field_190927_a;
      int[] var3 = new int[3];
      int var4 = 0;
      int var5 = 0;
      ItemArmorDyeable var6 = null;

      int var7;
      float var11;
      int var19;
      for(var7 = 0; var7 < var1.func_70302_i_(); ++var7) {
         ItemStack var8 = var1.func_70301_a(var7);
         if (!var8.func_190926_b()) {
            Item var9 = var8.func_77973_b();
            if (var9 instanceof ItemArmorDyeable) {
               var6 = (ItemArmorDyeable)var9;
               if (!var2.func_190926_b()) {
                  return ItemStack.field_190927_a;
               }

               var2 = var8.func_77946_l();
               var2.func_190920_e(1);
               if (var6.func_200883_f_(var8)) {
                  int var10 = var6.func_200886_f(var2);
                  var11 = (float)(var10 >> 16 & 255) / 255.0F;
                  float var12 = (float)(var10 >> 8 & 255) / 255.0F;
                  float var13 = (float)(var10 & 255) / 255.0F;
                  var4 = (int)((float)var4 + Math.max(var11, Math.max(var12, var13)) * 255.0F);
                  var3[0] = (int)((float)var3[0] + var11 * 255.0F);
                  var3[1] = (int)((float)var3[1] + var12 * 255.0F);
                  var3[2] = (int)((float)var3[2] + var13 * 255.0F);
                  ++var5;
               }
            } else {
               if (!(var9 instanceof ItemDye)) {
                  return ItemStack.field_190927_a;
               }

               float[] var16 = ((ItemDye)var9).func_195962_g().func_193349_f();
               int var18 = (int)(var16[0] * 255.0F);
               var19 = (int)(var16[1] * 255.0F);
               int var20 = (int)(var16[2] * 255.0F);
               var4 += Math.max(var18, Math.max(var19, var20));
               var3[0] += var18;
               var3[1] += var19;
               var3[2] += var20;
               ++var5;
            }
         }
      }

      if (var6 == null) {
         return ItemStack.field_190927_a;
      } else {
         var7 = var3[0] / var5;
         int var14 = var3[1] / var5;
         int var15 = var3[2] / var5;
         float var17 = (float)var4 / (float)var5;
         var11 = (float)Math.max(var7, Math.max(var14, var15));
         var7 = (int)((float)var7 * var17 / var11);
         var14 = (int)((float)var14 * var17 / var11);
         var15 = (int)((float)var15 * var17 / var11);
         var19 = (var7 << 8) + var14;
         var19 = (var19 << 8) + var15;
         var6.func_200885_a(var2, var19);
         return var2;
      }
   }

   public boolean func_194133_a(int var1, int var2) {
      return var1 * var2 >= 2;
   }

   public IRecipeSerializer<?> func_199559_b() {
      return RecipeSerializers.field_199577_c;
   }
}
