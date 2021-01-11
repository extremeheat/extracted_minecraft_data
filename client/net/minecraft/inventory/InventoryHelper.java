package net.minecraft.inventory;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class InventoryHelper {
   private static final Random field_180177_a = new Random();

   public static void func_180175_a(World var0, BlockPos var1, IInventory var2) {
      func_180174_a(var0, (double)var1.func_177958_n(), (double)var1.func_177956_o(), (double)var1.func_177952_p(), var2);
   }

   public static void func_180176_a(World var0, Entity var1, IInventory var2) {
      func_180174_a(var0, var1.field_70165_t, var1.field_70163_u, var1.field_70161_v, var2);
   }

   private static void func_180174_a(World var0, double var1, double var3, double var5, IInventory var7) {
      for(int var8 = 0; var8 < var7.func_70302_i_(); ++var8) {
         ItemStack var9 = var7.func_70301_a(var8);
         if (var9 != null) {
            func_180173_a(var0, var1, var3, var5, var9);
         }
      }

   }

   private static void func_180173_a(World var0, double var1, double var3, double var5, ItemStack var7) {
      float var8 = field_180177_a.nextFloat() * 0.8F + 0.1F;
      float var9 = field_180177_a.nextFloat() * 0.8F + 0.1F;
      float var10 = field_180177_a.nextFloat() * 0.8F + 0.1F;

      while(var7.field_77994_a > 0) {
         int var11 = field_180177_a.nextInt(21) + 10;
         if (var11 > var7.field_77994_a) {
            var11 = var7.field_77994_a;
         }

         var7.field_77994_a -= var11;
         EntityItem var12 = new EntityItem(var0, var1 + (double)var8, var3 + (double)var9, var5 + (double)var10, new ItemStack(var7.func_77973_b(), var11, var7.func_77960_j()));
         if (var7.func_77942_o()) {
            var12.func_92059_d().func_77982_d((NBTTagCompound)var7.func_77978_p().func_74737_b());
         }

         float var13 = 0.05F;
         var12.field_70159_w = field_180177_a.nextGaussian() * (double)var13;
         var12.field_70181_x = field_180177_a.nextGaussian() * (double)var13 + 0.20000000298023224D;
         var12.field_70179_y = field_180177_a.nextGaussian() * (double)var13;
         var0.func_72838_d(var12);
      }

   }
}
