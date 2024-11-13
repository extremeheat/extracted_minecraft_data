package net.minecraft.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class Containers {
   public Containers() {
      super();
   }

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
      var2.forEach((var2x) -> dropItemStack(var0, (double)var1.getX(), (double)var1.getY(), (double)var1.getZ(), var2x));
   }

   public static void dropItemStack(Level var0, double var1, double var3, double var5, ItemStack var7) {
      double var8 = (double)EntityType.ITEM.getWidth();
      double var10 = 1.0 - var8;
      double var12 = var8 / 2.0;
      double var14 = Math.floor(var1) + var0.random.nextDouble() * var10 + var12;
      double var16 = Math.floor(var3) + var0.random.nextDouble() * var10;
      double var18 = Math.floor(var5) + var0.random.nextDouble() * var10 + var12;

      while(!var7.isEmpty()) {
         ItemEntity var20 = new ItemEntity(var0, var14, var16, var18, var7.split(var0.random.nextInt(21) + 10));
         float var21 = 0.05F;
         var20.setDeltaMovement(var0.random.triangle(0.0, 0.11485000171139836), var0.random.triangle(0.2, 0.11485000171139836), var0.random.triangle(0.0, 0.11485000171139836));
         var0.addFreshEntity(var20);
      }

   }

   public static void dropContentsOnDestroy(BlockState var0, BlockState var1, Level var2, BlockPos var3) {
      if (!var0.is(var1.getBlock())) {
         BlockEntity var4 = var2.getBlockEntity(var3);
         if (var4 instanceof Container) {
            Container var5 = (Container)var4;
            dropContents(var2, var3, var5);
            var2.updateNeighbourForOutputSignal(var3, var0.getBlock());
         }

      }
   }
}
