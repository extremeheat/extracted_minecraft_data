package net.minecraft.world.level;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface CommonLevelAccessor extends EntityGetter, LevelReader, LevelSimulatedRW {
   default Stream<VoxelShape> getEntityCollisions(@Nullable Entity var1, AABB var2, Predicate<Entity> var3) {
      return EntityGetter.super.getEntityCollisions(var1, var2, var3);
   }

   default boolean isUnobstructed(@Nullable Entity var1, VoxelShape var2) {
      return EntityGetter.super.isUnobstructed(var1, var2);
   }

   default BlockPos getHeightmapPos(Heightmap.Types var1, BlockPos var2) {
      return LevelReader.super.getHeightmapPos(var1, var2);
   }

   RegistryAccess registryAccess();

   default Optional<ResourceKey<Biome>> getBiomeName(BlockPos var1) {
      return this.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getResourceKey(this.getBiome(var1));
   }
}
