package net.minecraft.core.dispenser;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class FillBucketDispenseBehavior implements DispenseBehavior {
   public static final FillBucketDispenseBehavior INSTANCE = new FillBucketDispenseBehavior();
   public static final MapCodec<FillBucketDispenseBehavior> MAP_CODEC;

   private FillBucketDispenseBehavior() {
      super();
   }

   public DispenseBehavior.DispenseResult execute(final BlockSource source, final ItemStack dispensed) {
      LevelAccessor level = source.level();
      BlockPos target = source.pos().relative((Direction)source.state().getValue(DispenserBlock.FACING));
      BlockState blockState = level.getBlockState(target);
      Block block = blockState.getBlock();
      if (block instanceof BucketPickup pickupSource) {
         ItemStack pickup = pickupSource.pickupBlock((LivingEntity)null, level, target, blockState);
         if (pickup.isEmpty()) {
            return DispenseBehavior.DispenseResult.failure();
         } else {
            pickupSource.getPickupSound().ifPresent((soundEvent) -> level.playSound((Entity)null, target, soundEvent, SoundSource.BLOCKS));
            level.gameEvent((Entity)null, (Holder)GameEvent.FLUID_PICKUP, (BlockPos)target);
            return DispenseBehavior.consumeWithRemainder(source, dispensed, new ItemStack(pickup.getItem()));
         }
      } else {
         return DispenseBehavior.DispenseResult.failure();
      }
   }

   public MapCodec<FillBucketDispenseBehavior> codec() {
      return MAP_CODEC;
   }

   static {
      MAP_CODEC = MapCodec.unit(INSTANCE);
   }
}
