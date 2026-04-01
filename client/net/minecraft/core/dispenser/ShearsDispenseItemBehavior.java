package net.minecraft.core.dispenser;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;

public class ShearsDispenseItemBehavior extends OptionalDispenseItemBehavior {
   public ShearsDispenseItemBehavior() {
      super();
   }

   protected ItemStack execute(final DispenseSource source, final ItemStack dispensed) {
      ServerLevel level = source.level();
      if (!level.isClientSide()) {
         BlockPos pos = source.pos().relative(source.direction());
         this.setSuccess(tryShearBeehive(level, dispensed, pos) || tryShearEntity(level, pos, dispensed));
         if (this.isSuccess()) {
            dispensed.hurtAndBreak(1, level, (ServerPlayer)null, (item) -> {
            });
         }
      }

      return dispensed;
   }

   private static boolean tryShearBeehive(final ServerLevel level, final ItemStack tool, final BlockPos pos) {
      BlockState state = level.getBlockState(pos);
      if (state.is(BlockTags.BEEHIVES, (s) -> s.hasProperty(BeehiveBlock.HONEY_LEVEL) && s.getBlock() instanceof BeehiveBlock)) {
         int honeyLevel = (Integer)state.getValue(BeehiveBlock.HONEY_LEVEL);
         if (honeyLevel >= 5) {
            level.playSound((Entity)null, pos, SoundEvents.BEEHIVE_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
            BeehiveBlock.dropHoneycomb(level, tool, state, level.getBlockEntity(pos), (Entity)null, pos);
            ((BeehiveBlock)state.getBlock()).releaseBeesAndResetHoneyLevel(level, state, pos, (Player)null, BeehiveBlockEntity.BeeReleaseStatus.BEE_RELEASED);
            level.gameEvent((Entity)null, GameEvent.SHEAR, pos);
            return true;
         }
      }

      return false;
   }

   private static boolean tryShearEntity(final ServerLevel level, final BlockPos pos, final ItemStack tool) {
      for(Entity entity : level.getEntitiesOfClass(Entity.class, new AABB(pos), EntitySelector.NO_SPECTATORS)) {
         if (entity.shearOffAllLeashConnections((Player)null)) {
            return true;
         }

         if (entity instanceof Shearable shearable) {
            if (shearable.readyForShearing()) {
               shearable.shear(level, SoundSource.BLOCKS, tool);
               level.gameEvent((Entity)null, GameEvent.SHEAR, pos);
               return true;
            }
         }
      }

      return false;
   }
}
