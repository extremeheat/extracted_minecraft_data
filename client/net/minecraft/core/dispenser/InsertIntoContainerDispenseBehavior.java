package net.minecraft.core.dispenser;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DropperBlock;
import net.minecraft.world.level.block.entity.HopperBlockEntity;

public class InsertIntoContainerDispenseBehavior implements DispenseBehavior {
   public static final InsertIntoContainerDispenseBehavior INSTANCE = new InsertIntoContainerDispenseBehavior();
   public static final MapCodec<InsertIntoContainerDispenseBehavior> MAP_CODEC;

   private InsertIntoContainerDispenseBehavior() {
      super();
   }

   public DispenseBehavior.DispenseResult execute(final BlockSource source, final ItemStack dispensed) {
      ServerLevel level = source.level();
      BlockPos pos = source.pos();
      Direction direction = (Direction)level.getBlockState(pos).getValue(DropperBlock.FACING);
      Container into = HopperBlockEntity.getContainerAt(level, pos.relative(direction));
      if (into == null) {
         return DispenseBehavior.DispenseResult.failure();
      } else {
         ItemStack remaining = HopperBlockEntity.addItem(source.blockEntity(), into, dispensed.copyWithCount(1), direction.getOpposite());
         if (remaining.isEmpty()) {
            remaining = dispensed.copy();
            remaining.shrink(1);
            return DispenseBehavior.DispenseResult.success(remaining);
         } else {
            return DispenseBehavior.DispenseResult.stoppingFailure();
         }
      }
   }

   public void playSuccessSound(final BlockSource source) {
   }

   public void playFailureSound(final BlockSource source) {
   }

   public void playAnimation(final BlockSource source, final Direction direction) {
   }

   public MapCodec<InsertIntoContainerDispenseBehavior> codec() {
      return MAP_CODEC;
   }

   static {
      MAP_CODEC = MapCodec.unit(INSTANCE);
   }
}
