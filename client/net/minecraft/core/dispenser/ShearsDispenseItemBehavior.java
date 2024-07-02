package net.minecraft.core.dispenser;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;

public class ShearsDispenseItemBehavior extends OptionalDispenseItemBehavior {
   public ShearsDispenseItemBehavior() {
      super();
   }

   @Override
   protected ItemStack execute(BlockSource var1, ItemStack var2) {
      ServerLevel var3 = var1.level();
      if (!var3.isClientSide()) {
         BlockPos var4 = var1.pos().relative(var1.state().getValue(DispenserBlock.FACING));
         this.setSuccess(tryShearBeehive(var3, var4) || tryShearLivingEntity(var3, var4));
         if (this.isSuccess()) {
            var2.hurtAndBreak(1, var3, null, var0 -> {
            });
         }
      }

      return var2;
   }

   private static boolean tryShearBeehive(ServerLevel var0, BlockPos var1) {
      BlockState var2 = var0.getBlockState(var1);
      if (var2.is(BlockTags.BEEHIVES, var0x -> var0x.hasProperty(BeehiveBlock.HONEY_LEVEL) && var0x.getBlock() instanceof BeehiveBlock)) {
         int var3 = var2.getValue(BeehiveBlock.HONEY_LEVEL);
         if (var3 >= 5) {
            var0.playSound(null, var1, SoundEvents.BEEHIVE_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
            BeehiveBlock.dropHoneycomb(var0, var1);
            ((BeehiveBlock)var2.getBlock()).releaseBeesAndResetHoneyLevel(var0, var2, var1, null, BeehiveBlockEntity.BeeReleaseStatus.BEE_RELEASED);
            var0.gameEvent(null, GameEvent.SHEAR, var1);
            return true;
         }
      }

      return false;
   }

   private static boolean tryShearLivingEntity(ServerLevel var0, BlockPos var1) {
      for (LivingEntity var4 : var0.getEntitiesOfClass(LivingEntity.class, new AABB(var1), EntitySelector.NO_SPECTATORS)) {
         if (var4 instanceof Shearable var5 && var5.readyForShearing()) {
            var5.shear(SoundSource.BLOCKS);
            var0.gameEvent(null, GameEvent.SHEAR, var1);
            return true;
         }
      }

      return false;
   }
}
