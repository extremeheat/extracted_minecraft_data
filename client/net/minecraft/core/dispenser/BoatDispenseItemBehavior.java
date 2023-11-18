package net.minecraft.core.dispenser;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;

public class BoatDispenseItemBehavior extends DefaultDispenseItemBehavior {
   private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();
   private final Boat.Type type;
   private final boolean isChestBoat;

   public BoatDispenseItemBehavior(Boat.Type var1) {
      this(var1, false);
   }

   public BoatDispenseItemBehavior(Boat.Type var1, boolean var2) {
      super();
      this.type = var1;
      this.isChestBoat = var2;
   }

   @Override
   public ItemStack execute(BlockSource var1, ItemStack var2) {
      Direction var3 = var1.getBlockState().getValue(DispenserBlock.FACING);
      ServerLevel var4 = var1.getLevel();
      double var5 = 0.5625 + (double)EntityType.BOAT.getWidth() / 2.0;
      double var7 = var1.x() + (double)var3.getStepX() * var5;
      double var9 = var1.y() + (double)((float)var3.getStepY() * 1.125F);
      double var11 = var1.z() + (double)var3.getStepZ() * var5;
      BlockPos var13 = var1.getPos().relative(var3);
      double var14;
      if (var4.getFluidState(var13).is(FluidTags.WATER)) {
         var14 = 1.0;
      } else {
         if (!var4.getBlockState(var13).isAir() || !var4.getFluidState(var13.below()).is(FluidTags.WATER)) {
            return this.defaultDispenseItemBehavior.dispense(var1, var2);
         }

         var14 = 0.0;
      }

      Object var16 = this.isChestBoat ? new ChestBoat(var4, var7, var9 + var14, var11) : new Boat(var4, var7, var9 + var14, var11);
      ((Boat)var16).setVariant(this.type);
      ((Boat)var16).setYRot(var3.toYRot());
      var4.addFreshEntity((Entity)var16);
      var2.shrink(1);
      return var2;
   }

   @Override
   protected void playSound(BlockSource var1) {
      var1.getLevel().levelEvent(1000, var1.getPos(), 0);
   }
}
