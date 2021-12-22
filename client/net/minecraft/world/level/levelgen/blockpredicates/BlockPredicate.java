package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.BiPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;

public interface BlockPredicate extends BiPredicate<WorldGenLevel, BlockPos> {
   Codec<BlockPredicate> CODEC = Registry.BLOCK_PREDICATE_TYPES.byNameCodec().dispatch(BlockPredicate::type, BlockPredicateType::codec);
   BlockPredicate ONLY_IN_AIR_PREDICATE = matchesBlock(Blocks.AIR, BlockPos.ZERO);
   BlockPredicate ONLY_IN_AIR_OR_WATER_PREDICATE = matchesBlocks(List.of(Blocks.AIR, Blocks.WATER), BlockPos.ZERO);

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

   static BlockPredicate matchesBlocks(List<Block> var0, Vec3i var1) {
      return new MatchingBlocksPredicate(var1, var0);
   }

   static BlockPredicate matchesBlocks(List<Block> var0) {
      return matchesBlocks(var0, Vec3i.ZERO);
   }

   static BlockPredicate matchesBlock(Block var0, Vec3i var1) {
      return matchesBlocks(List.of(var0), var1);
   }

   static BlockPredicate matchesTag(Tag<Block> var0, Vec3i var1) {
      return new MatchingBlockTagPredicate(var1, var0);
   }

   static BlockPredicate matchesTag(Tag<Block> var0) {
      return matchesTag(var0, Vec3i.ZERO);
   }

   static BlockPredicate matchesFluids(List<Fluid> var0, Vec3i var1) {
      return new MatchingFluidsPredicate(var1, var0);
   }

   static BlockPredicate matchesFluid(Fluid var0, Vec3i var1) {
      return matchesFluids(List.of(var0), var1);
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

   static BlockPredicate insideWorld(Vec3i var0) {
      return new InsideWorldBoundsPredicate(var0);
   }

   static BlockPredicate alwaysTrue() {
      return TrueBlockPredicate.INSTANCE;
   }
}
