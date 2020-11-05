package net.minecraft.core.dispenser;

import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

public class DefaultDispenseItemBehavior implements DispenseItemBehavior {
   public DefaultDispenseItemBehavior() {
      super();
   }

   public final ItemStack dispense(BlockSource var1, ItemStack var2) {
      ItemStack var3 = this.execute(var1, var2);
      this.playSound(var1);
      this.playAnimation(var1, (Direction)var1.getBlockState().getValue(DispenserBlock.FACING));
      return var3;
   }

   protected ItemStack execute(BlockSource var1, ItemStack var2) {
      Direction var3 = (Direction)var1.getBlockState().getValue(DispenserBlock.FACING);
      Position var4 = DispenserBlock.getDispensePosition(var1);
      ItemStack var5 = var2.split(1);
      spawnItem(var1.getLevel(), var5, 6, var3, var4);
      return var2;
   }

   public static void spawnItem(Level var0, ItemStack var1, int var2, Direction var3, Position var4) {
      double var5 = var4.x();
      double var7 = var4.y();
      double var9 = var4.z();
      if (var3.getAxis() == Direction.Axis.Y) {
         var7 -= 0.125D;
      } else {
         var7 -= 0.15625D;
      }

      ItemEntity var11 = new ItemEntity(var0, var5, var7, var9, var1);
      double var12 = var0.random.nextDouble() * 0.1D + 0.2D;
      var11.setDeltaMovement(var0.random.nextGaussian() * 0.007499999832361937D * (double)var2 + (double)var3.getStepX() * var12, var0.random.nextGaussian() * 0.007499999832361937D * (double)var2 + 0.20000000298023224D, var0.random.nextGaussian() * 0.007499999832361937D * (double)var2 + (double)var3.getStepZ() * var12);
      var0.addFreshEntity(var11);
   }

   protected void playSound(BlockSource var1) {
      var1.getLevel().levelEvent(1000, var1.getPos(), 0);
   }

   protected void playAnimation(BlockSource var1, Direction var2) {
      var1.getLevel().levelEvent(2000, var1.getPos(), var2.get3DDataValue());
   }
}
