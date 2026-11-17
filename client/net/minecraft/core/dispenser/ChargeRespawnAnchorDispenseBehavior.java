package net.minecraft.core.dispenser;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ChargeRespawnAnchorDispenseBehavior implements DispenseBehavior {
   public static final ChargeRespawnAnchorDispenseBehavior INSTANCE = new ChargeRespawnAnchorDispenseBehavior();
   public static final MapCodec<ChargeRespawnAnchorDispenseBehavior> MAP_CODEC;

   private ChargeRespawnAnchorDispenseBehavior() {
      super();
   }

   public DispenseBehavior.DispenseResult execute(final BlockSource source, final ItemStack dispensed) {
      Direction direction = (Direction)source.state().getValue(DispenserBlock.FACING);
      BlockPos pos = source.pos().relative(direction);
      Level level = source.level();
      BlockState blockState = level.getBlockState(pos);
      if (blockState.is(Blocks.RESPAWN_ANCHOR)) {
         if ((Integer)blockState.getValue(RespawnAnchorBlock.CHARGE) != 4) {
            RespawnAnchorBlock.charge((Entity)null, level, pos, blockState);
            dispensed.shrink(1);
            return DispenseBehavior.DispenseResult.success(dispensed);
         } else {
            return DispenseBehavior.DispenseResult.stoppingFailure();
         }
      } else {
         return DispenseBehavior.DispenseResult.failure();
      }
   }

   public MapCodec<ChargeRespawnAnchorDispenseBehavior> codec() {
      return MAP_CODEC;
   }

   static {
      MAP_CODEC = MapCodec.unit(INSTANCE);
   }
}
