package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.Codec;
import java.util.Collection;
import java.util.List;
import java.util.function.BiPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public interface BlockPredicate extends BiPredicate<WorldGenLevel, BlockPos> {
   Codec<BlockPredicate> CODEC = BuiltInRegistries.BLOCK_PREDICATE_TYPE.byNameCodec().dispatch(BlockPredicate::type, BlockPredicateType::codec);
   BlockPredicate ONLY_IN_AIR_PREDICATE = matchesBlocks(Blocks.AIR);
   BlockPredicate ONLY_IN_AIR_OR_WATER_PREDICATE = matchesBlocks(Blocks.AIR, Blocks.WATER);

   BlockPredicateType<?> type();

   static BlockPredicate allOf(List<BlockPredicate> var0) {
      return new AllOfPredicate(var0);
   }

   static BlockPredicate allOf(BlockPredicate... var0) {
      return allOf(List.of(var0));
   }

   static BlockPredicate allOf(BlockPredicate var0, BlockPredicate var1) {
      return allOf(List.of(var0, var1));
   }

   static BlockPredicate anyOf(List<BlockPredicate> var0) {
      return new AnyOfPredicate(var0);
   }

   static BlockPredicate anyOf(BlockPredicate... var0) {
      return anyOf(List.of(var0));
   }

   static BlockPredicate anyOf(BlockPredicate var0, BlockPredicate var1) {
      return anyOf(List.of(var0, var1));
   }

   static BlockPredicate matchesBlocks(Vec3i var0, List<Block> var1) {
      return new MatchingBlocksPredicate(var0, HolderSet.direct(Block::builtInRegistryHolder, (Collection)var1));
   }

   static BlockPredicate matchesBlocks(List<Block> var0) {
      return matchesBlocks(Vec3i.ZERO, var0);
   }

   static BlockPredicate matchesBlocks(Vec3i var0, Block... var1) {
      return matchesBlocks(var0, List.of(var1));
   }

   static BlockPredicate matchesBlocks(Block... var0) {
      return matchesBlocks(Vec3i.ZERO, var0);
   }

   static BlockPredicate matchesTag(Vec3i var0, TagKey<Block> var1) {
      return new MatchingBlockTagPredicate(var0, var1);
   }

   static BlockPredicate matchesTag(TagKey<Block> var0) {
      return matchesTag(Vec3i.ZERO, var0);
   }

   static BlockPredicate matchesFluids(Vec3i var0, List<Fluid> var1) {
      return new MatchingFluidsPredicate(var0, HolderSet.direct(Fluid::builtInRegistryHolder, (Collection)var1));
   }

   static BlockPredicate matchesFluids(Vec3i var0, Fluid... var1) {
      return matchesFluids(var0, List.of(var1));
   }

   static BlockPredicate matchesFluids(Fluid... var0) {
      return matchesFluids(Vec3i.ZERO, var0);
   }

   static BlockPredicate not(BlockPredicate var0) {
      return new NotPredicate(var0);
   }

   static BlockPredicate replaceable(Vec3i var0) {
      return new ReplaceablePredicate(var0);
   }

   static BlockPredicate replaceable() {
      return replaceable(Vec3i.ZERO);
   }

   static BlockPredicate wouldSurvive(BlockState var0, Vec3i var1) {
      return new WouldSurvivePredicate(var1, var0);
   }

   static BlockPredicate hasSturdyFace(Vec3i var0, Direction var1) {
      return new HasSturdyFacePredicate(var0, var1);
   }

   static BlockPredicate hasSturdyFace(Direction var0) {
      return hasSturdyFace(Vec3i.ZERO, var0);
   }

   static BlockPredicate solid(Vec3i var0) {
      return new SolidPredicate(var0);
   }

   static BlockPredicate solid() {
      return solid(Vec3i.ZERO);
   }

   static BlockPredicate noFluid() {
      return noFluid(Vec3i.ZERO);
   }

   static BlockPredicate noFluid(Vec3i var0) {
      return matchesFluids(var0, Fluids.EMPTY);
   }

   static BlockPredicate insideWorld(Vec3i var0) {
      return new InsideWorldBoundsPredicate(var0);
   }

   static BlockPredicate alwaysTrue() {
      return TrueBlockPredicate.INSTANCE;
   }

   static BlockPredicate unobstructed(Vec3i var0) {
      return new UnobstructedPredicate(var0);
   }

   static BlockPredicate unobstructed() {
      return unobstructed(Vec3i.ZERO);
   }
}
