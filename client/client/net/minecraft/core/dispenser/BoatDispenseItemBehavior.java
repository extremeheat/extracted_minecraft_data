package net.minecraft.core.dispenser;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.Vec3;

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
      Direction var3 = var1.state().getValue(DispenserBlock.FACING);
      ServerLevel var4 = var1.level();
      Vec3 var5 = var1.center();
      double var6 = 0.5625 + (double)EntityType.BOAT.getWidth() / 2.0;
      double var8 = var5.x() + (double)var3.getStepX() * var6;
      double var10 = var5.y() + (double)((float)var3.getStepY() * 1.125F);
      double var12 = var5.z() + (double)var3.getStepZ() * var6;
      BlockPos var14 = var1.pos().relative(var3);
      double var15;
      if (var4.getFluidState(var14).is(FluidTags.WATER)) {
         var15 = 1.0;
      } else {
         if (!var4.getBlockState(var14).isAir() || !var4.getFluidState(var14.below()).is(FluidTags.WATER)) {
            return this.defaultDispenseItemBehavior.dispense(var1, var2);
         }

         var15 = 0.0;
      }

      Object var17 = this.isChestBoat ? new ChestBoat(var4, var8, var10 + var15, var12) : new Boat(var4, var8, var10 + var15, var12);
      EntityType.createDefaultStackConfig(var4, var2, null).accept(var17);
      ((Boat)var17).setVariant(this.type);
      ((Boat)var17).setYRot(var3.toYRot());
      var4.addFreshEntity((Entity)var17);
      var2.shrink(1);
      return var2;
   }

   @Override
   protected void playSound(BlockSource var1) {
      var1.level().levelEvent(1000, var1.pos(), 0);
   }
}