package net.minecraft.item.crafting;

import java.util.ArrayList;
import net.minecraft.block.BlockColored;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor$ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class RecipesArmorDyes implements IRecipe {
   public RecipesArmorDyes() {
      super();
   }

   @Override
   public boolean func_77569_a(InventoryCrafting var1, World var2) {
      ItemStack var3 = null;
      ArrayList var4 = new ArrayList();

      for(int var5 = 0; var5 < var1.func_70302_i_(); ++var5) {
         ItemStack var6 = var1.func_70301_a(var5);
         if (var6 != null) {
            if (var6.func_77973_b() instanceof ItemArmor) {
               ItemArmor var7 = (ItemArmor)var6.func_77973_b();
               if (var7.func_82812_d() != ItemArmor$ArmorMaterial.CLOTH || var3 != null) {
                  return false;
               }

               var3 = var6;
            } else {
               if (var6.func_77973_b() != Items.field_151100_aR) {
                  return false;
               }

               var4.add(var6);
            }
         }
      }

      return var3 != null && !var4.isEmpty();
   }

   @Override
   public ItemStack func_77572_b(InventoryCrafting var1) {
      ItemStack var2 = null;
      int[] var3 = new int[3];
      int var4 = 0;
      int var5 = 0;
      ItemArmor var6 = null;

      for(int var7 = 0; var7 < var1.func_70302_i_(); ++var7) {
         ItemStack var8 = var1.func_70301_a(var7);
         if (var8 != null) {
            if (var8.func_77973_b() instanceof ItemArmor) {
               var6 = (ItemArmor)var8.func_77973_b();
               if (var6.func_82812_d() != ItemArmor$ArmorMaterial.CLOTH || var2 != null) {
                  return null;
               }

               var2 = var8.func_77946_l();
               var2.field_77994_a = 1;
               if (var6.func_82816_b_(var8)) {
                  int var9 = var6.func_82814_b(var2);
                  float var10 = (float)(var9 >> 16 & 0xFF) / 255.0F;
                  float var11 = (float)(var9 >> 8 & 0xFF) / 255.0F;
                  float var12 = (float)(var9 & 0xFF) / 255.0F;
                  var4 = (int)((float)var4 + Math.max(var10, Math.max(var11, var12)) * 255.0F);
                  var3[0] = (int)((float)var3[0] + var10 * 255.0F);
                  var3[1] = (int)((float)var3[1] + var11 * 255.0F);
                  var3[2] = (int)((float)var3[2] + var12 * 255.0F);
                  ++var5;
               }
            } else {
               if (var8.func_77973_b() != Items.field_151100_aR) {
                  return null;
               }

               float[] var17 = EntitySheep.field_70898_d[BlockColored.func_150032_b(var8.func_77960_j())];
               int var20 = (int)(var17[0] * 255.0F);
               int var22 = (int)(var17[1] * 255.0F);
               int var24 = (int)(var17[2] * 255.0F);
               var4 += Math.max(var20, Math.max(var22, var24));
               var3[0] += var20;
               var3[1] += var22;
               var3[2] += var24;
               ++var5;
            }
         }
      }

      if (var6 == null) {
         return null;
      } else {
         int var13 = var3[0] / var5;
         int var15 = var3[1] / var5;
         int var18 = var3[2] / var5;
         float var21 = (float)var4 / (float)var5;
         float var23 = (float)Math.max(var13, Math.max(var15, var18));
         var13 = (int)((float)var13 * var21 / var23);
         var15 = (int)((float)var15 * var21 / var23);
         var18 = (int)((float)var18 * var21 / var23);
         int var25 = (var13 << 8) + var15;
         var25 = (var25 << 8) + var18;
         var6.func_82813_b(var2, var25);
         return var2;
      }
   }

   @Override
   public int func_77570_a() {
      return 10;
   }

   @Override
   public ItemStack func_77571_b() {
      return null;
   }
}
