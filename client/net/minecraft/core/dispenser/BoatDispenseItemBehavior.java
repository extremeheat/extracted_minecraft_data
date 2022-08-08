package net.minecraft.core.dispenser;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;

public class BoatDispenseItemBehavior extends DefaultDispenseItemBehavior {
   private final DefaultDispenseItemBehavior defaultDispenseItemBehavior;
   private final Boat.Type type;
   private final boolean isChestBoat;

   public BoatDispenseItemBehavior(Boat.Type var1) {
      this(var1, false);
   }

   public BoatDispenseItemBehavior(Boat.Type var1, boolean var2) {
      super();
      this.defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();
      this.type = var1;
      this.isChestBoat = var2;
   }

   public ItemStack execute(BlockSource var1, ItemStack var2) {
      Direction var3 = (Direction)var1.getBlockState().getValue(DispenserBlock.FACING);
      ServerLevel var4 = var1.getLevel();
      double var5 = var1.x() + (double)((float)var3.getStepX() * 1.125F);
      double var7 = var1.y() + (double)((float)var3.getStepY() * 1.125F);
      double var9 = var1.z() + (double)((float)var3.getStepZ() * 1.125F);
      BlockPos var11 = var1.getPos().relative(var3);
      double var12;
      if (var4.getFluidState(var11).is(FluidTags.WATER)) {
         var12 = 1.0;
      } else {
         if (!var4.getBlockState(var11).isAir() || !var4.getFluidState(var11.below()).is(FluidTags.WATER)) {
            return this.defaultDispenseItemBehavior.dispense(var1, var2);
         }

         var12 = 0.0;
      }

      Object var14 = this.isChestBoat ? new ChestBoat(var4, var5, var7 + var12, var9) : new Boat(var4, var5, var7 + var12, var9);
      ((Boat)var14).setType(this.type);
      ((Boat)var14).setYRot(var3.toYRot());
      var4.addFreshEntity((Entity)var14);
      var2.shrink(1);
      return var2;
   }

   protected void playSound(BlockSource var1) {
      var1.getLevel().levelEvent(1000, var1.getPos(), 0);
   }
}
