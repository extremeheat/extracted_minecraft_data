package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;

public class BlockAgeProcessor extends StructureProcessor {
   public static final MapCodec<BlockAgeProcessor> CODEC;
   private static final float PROBABILITY_OF_REPLACING_FULL_BLOCK = 0.5F;
   private static final float PROBABILITY_OF_REPLACING_STAIRS = 0.5F;
   private static final float PROBABILITY_OF_REPLACING_OBSIDIAN = 0.15F;
   private static final BlockState[] NON_MOSSY_REPLACEMENTS;
   private final float mossiness;

   public BlockAgeProcessor(float var1) {
      super();
      this.mossiness = var1;
   }

   @Nullable
   public StructureTemplate.StructureBlockInfo processBlock(LevelReader var1, BlockPos var2, BlockPos var3, StructureTemplate.StructureBlockInfo var4, StructureTemplate.StructureBlockInfo var5, StructurePlaceSettings var6) {
      RandomSource var7 = var6.getRandom(var5.pos());
      BlockState var8 = var5.state();
      BlockPos var9 = var5.pos();
      BlockState var10 = null;
      if (!var8.is(Blocks.STONE_BRICKS) && !var8.is(Blocks.STONE) && !var8.is(Blocks.CHISELED_STONE_BRICKS)) {
         if (var8.is(BlockTags.STAIRS)) {
            var10 = this.maybeReplaceStairs(var7, var5.state());
         } else if (var8.is(BlockTags.SLABS)) {
            var10 = this.maybeReplaceSlab(var7);
         } else if (var8.is(BlockTags.WALLS)) {
            var10 = this.maybeReplaceWall(var7);
         } else if (var8.is(Blocks.OBSIDIAN)) {
            var10 = this.maybeReplaceObsidian(var7);
         }
      } else {
         var10 = this.maybeReplaceFullStoneBlock(var7);
      }

      return var10 != null ? new StructureTemplate.StructureBlockInfo(var9, var10, var5.nbt()) : var5;
   }

   @Nullable
   private BlockState maybeReplaceFullStoneBlock(RandomSource var1) {
      if (var1.nextFloat() >= 0.5F) {
         return null;
      } else {
         BlockState[] var2 = new BlockState[]{Blocks.CRACKED_STONE_BRICKS.defaultBlockState(), getRandomFacingStairs(var1, Blocks.STONE_BRICK_STAIRS)};
         BlockState[] var3 = new BlockState[]{Blocks.MOSSY_STONE_BRICKS.defaultBlockState(), getRandomFacingStairs(var1, Blocks.MOSSY_STONE_BRICK_STAIRS)};
         return this.getRandomBlock(var1, var2, var3);
      }
   }

   @Nullable
   private BlockState maybeReplaceStairs(RandomSource var1, BlockState var2) {
      Direction var3 = (Direction)var2.getValue(StairBlock.FACING);
      Half var4 = (Half)var2.getValue(StairBlock.HALF);
      if (var1.nextFloat() >= 0.5F) {
         return null;
      } else {
         BlockState[] var5 = new BlockState[]{(BlockState)((BlockState)Blocks.MOSSY_STONE_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.FACING, var3)).setValue(StairBlock.HALF, var4), Blocks.MOSSY_STONE_BRICK_SLAB.defaultBlockState()};
         return this.getRandomBlock(var1, NON_MOSSY_REPLACEMENTS, var5);
      }
   }

   @Nullable
   private BlockState maybeReplaceSlab(RandomSource var1) {
      return var1.nextFloat() < this.mossiness ? Blocks.MOSSY_STONE_BRICK_SLAB.defaultBlockState() : null;
   }

   @Nullable
   private BlockState maybeReplaceWall(RandomSource var1) {
      return var1.nextFloat() < this.mossiness ? Blocks.MOSSY_STONE_BRICK_WALL.defaultBlockState() : null;
   }

   @Nullable
   private BlockState maybeReplaceObsidian(RandomSource var1) {
      return var1.nextFloat() < 0.15F ? Blocks.CRYING_OBSIDIAN.defaultBlockState() : null;
   }

   private static BlockState getRandomFacingStairs(RandomSource var0, Block var1) {
      return (BlockState)((BlockState)var1.defaultBlockState().setValue(StairBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(var0))).setValue(StairBlock.HALF, (Half)Util.getRandom((Object[])Half.values(), var0));
   }

   private BlockState getRandomBlock(RandomSource var1, BlockState[] var2, BlockState[] var3) {
      return var1.nextFloat() < this.mossiness ? getRandomBlock(var1, var3) : getRandomBlock(var1, var2);
   }

   private static BlockState getRandomBlock(RandomSource var0, BlockState[] var1) {
      return var1[var0.nextInt(var1.length)];
   }

   protected StructureProcessorType<?> getType() {
      return StructureProcessorType.BLOCK_AGE;
   }

   static {
      CODEC = Codec.FLOAT.fieldOf("mossiness").xmap(BlockAgeProcessor::new, (var0) -> {
         return var0.mossiness;
      });
      NON_MOSSY_REPLACEMENTS = new BlockState[]{Blocks.STONE_SLAB.defaultBlockState(), Blocks.STONE_BRICK_SLAB.defaultBlockState()};
   }
}
