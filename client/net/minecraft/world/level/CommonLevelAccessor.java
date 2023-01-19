package net.minecraft.world.level;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface CommonLevelAccessor extends EntityGetter, LevelReader, LevelSimulatedRW {
   @Override
   default <T extends BlockEntity> Optional<T> getBlockEntity(BlockPos var1, BlockEntityType<T> var2) {
      return LevelReader.super.getBlockEntity(var1, var2);
   }

   @Override
   default List<VoxelShape> getEntityCollisions(@Nullable Entity var1, AABB var2) {
      return EntityGetter.super.getEntityCollisions(var1, var2);
   }

   @Override
   default boolean isUnobstructed(@Nullable Entity var1, VoxelShape var2) {
      return EntityGetter.super.isUnobstructed(var1, var2);
   }

   @Override
   default BlockPos getHeightmapPos(Heightmap.Types var1, BlockPos var2) {
      return LevelReader.super.getHeightmapPos(var1, var2);
   }

   RegistryAccess registryAccess();
}
