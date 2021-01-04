package net.minecraft.core.dispenser;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

public class BoatDispenseItemBehavior extends DefaultDispenseItemBehavior {
   private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();
   private final Boat.Type type;

   public BoatDispenseItemBehavior(Boat.Type var1) {
      super();
      this.type = var1;
   }

   public ItemStack execute(BlockSource var1, ItemStack var2) {
      Direction var3 = (Direction)var1.getBlockState().getValue(DispenserBlock.FACING);
      Level var4 = var1.getLevel();
      double var5 = var1.x() + (double)((float)var3.getStepX() * 1.125F);
      double var7 = var1.y() + (double)((float)var3.getStepY() * 1.125F);
      double var9 = var1.z() + (double)((float)var3.getStepZ() * 1.125F);
      BlockPos var11 = var1.getPos().relative(var3);
      double var12;
      if (var4.getFluidState(var11).is(FluidTags.WATER)) {
         var12 = 1.0D;
      } else {
         if (!var4.getBlockState(var11).isAir() || !var4.getFluidState(var11.below()).is(FluidTags.WATER)) {
            return this.defaultDispenseItemBehavior.dispense(var1, var2);
         }

         var12 = 0.0D;
      }

      Boat var14 = new Boat(var4, var5, var7 + var12, var9);
      var14.setType(this.type);
      var14.yRot = var3.toYRot();
      var4.addFreshEntity(var14);
      var2.shrink(1);
      return var2;
   }

   protected void playSound(BlockSource var1) {
      var1.getLevel().levelEvent(1000, var1.getPos(), 0);
   }
}
