package net.minecraft.core.dispenser;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractBoat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.Vec3;

public class BoatDispenseItemBehavior extends DefaultDispenseItemBehavior {
   private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();
   private final EntityType<? extends AbstractBoat> type;

   public BoatDispenseItemBehavior(EntityType<? extends AbstractBoat> var1) {
      super();
      this.type = var1;
   }

   public ItemStack execute(BlockSource var1, ItemStack var2) {
      Direction var3 = (Direction)var1.state().getValue(DispenserBlock.FACING);
      ServerLevel var4 = var1.level();
      Vec3 var5 = var1.center();
      double var6 = 0.5625 + (double)this.type.getWidth() / 2.0;
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

      AbstractBoat var17 = this.type.create(var4, EntitySpawnReason.DISPENSER);
      if (var17 != null) {
         var17.setInitialPos(var8, var10 + var15, var12);
         EntityType.createDefaultStackConfig(var4, var2, (Player)null).accept(var17);
         var17.setYRot(var3.toYRot());
         var4.addFreshEntity(var17);
         var2.shrink(1);
      }

      return var2;
   }

   protected void playSound(BlockSource var1) {
      var1.level().levelEvent(1000, var1.pos(), 0);
   }
}
