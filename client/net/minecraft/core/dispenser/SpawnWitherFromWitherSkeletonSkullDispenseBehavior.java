package net.minecraft.core.dispenser;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.WitherSkullBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.level.gameevent.GameEvent;

public class SpawnWitherFromWitherSkeletonSkullDispenseBehavior implements DispenseBehavior {
   public static final SpawnWitherFromWitherSkeletonSkullDispenseBehavior INSTANCE = new SpawnWitherFromWitherSkeletonSkullDispenseBehavior();
   public static final MapCodec<SpawnWitherFromWitherSkeletonSkullDispenseBehavior> MAP_CODEC;

   private SpawnWitherFromWitherSkeletonSkullDispenseBehavior() {
      super();
   }

   public DispenseBehavior.DispenseResult execute(final BlockSource source, final ItemStack dispensed) {
      Level level = source.level();
      Direction direction = (Direction)source.state().getValue(DispenserBlock.FACING);
      BlockPos target = source.pos().relative(direction);
      if (level.isEmptyBlock(target) && WitherSkullBlock.canSpawnMob(level, target, dispensed)) {
         level.setBlockAndUpdate(target, (BlockState)Blocks.WITHER_SKELETON_SKULL.defaultBlockState().setValue(SkullBlock.ROTATION, RotationSegment.convertToSegment(direction)));
         level.gameEvent((Entity)null, GameEvent.BLOCK_PLACE, target);
         BlockEntity skull = level.getBlockEntity(target);
         if (skull instanceof SkullBlockEntity) {
            SkullBlockEntity skullBlockEntity = (SkullBlockEntity)skull;
            WitherSkullBlock.checkSpawn(level, target, skullBlockEntity);
         }

         dispensed.shrink(1);
         return DispenseBehavior.DispenseResult.success(dispensed);
      } else {
         return DispenseBehavior.DispenseResult.failure();
      }
   }

   public MapCodec<SpawnWitherFromWitherSkeletonSkullDispenseBehavior> codec() {
      return MAP_CODEC;
   }

   static {
      MAP_CODEC = MapCodec.unit(INSTANCE);
   }
}
