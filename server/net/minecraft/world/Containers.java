package net.minecraft.world;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class Containers {
   private static final Random RANDOM = new Random();

   public static void dropContents(Level var0, BlockPos var1, Container var2) {
      dropContents(var0, (double)var1.getX(), (double)var1.getY(), (double)var1.getZ(), var2);
   }

   public static void dropContents(Level var0, Entity var1, Container var2) {
      dropContents(var0, var1.getX(), var1.getY(), var1.getZ(), var2);
   }

   private static void dropContents(Level var0, double var1, double var3, double var5, Container var7) {
      for(int var8 = 0; var8 < var7.getContainerSize(); ++var8) {
         dropItemStack(var0, var1, var3, var5, var7.getItem(var8));
      }

   }

   public static void dropContents(Level var0, BlockPos var1, NonNullList<ItemStack> var2) {
      var2.forEach((var2x) -> {
         dropItemStack(var0, (double)var1.getX(), (double)var1.getY(), (double)var1.getZ(), var2x);
      });
   }

   public static void dropItemStack(Level var0, double var1, double var3, double var5, ItemStack var7) {
      double var8 = (double)EntityType.ITEM.getWidth();
      double var10 = 1.0D - var8;
      double var12 = var8 / 2.0D;
      double var14 = Math.floor(var1) + RANDOM.nextDouble() * var10 + var12;
      double var16 = Math.floor(var3) + RANDOM.nextDouble() * var10;
      double var18 = Math.floor(var5) + RANDOM.nextDouble() * var10 + var12;

      while(!var7.isEmpty()) {
         ItemEntity var20 = new ItemEntity(var0, var14, var16, var18, var7.split(RANDOM.nextInt(21) + 10));
         float var21 = 0.05F;
         var20.setDeltaMovement(RANDOM.nextGaussian() * 0.05000000074505806D, RANDOM.nextGaussian() * 0.05000000074505806D + 0.20000000298023224D, RANDOM.nextGaussian() * 0.05000000074505806D);
         var0.addFreshEntity(var20);
      }

   }
}
