package net.minecraft.world.level.levelgen;

import com.google.common.annotations.VisibleForTesting;
import java.util.Objects;
import java.util.Set;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.BlockColumn;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.densityfunction.DensitySamplerSet;
import net.minecraft.world.level.levelgen.densityfunction.DensityVolume;
import net.minecraft.world.level.levelgen.material.MaterialRuleContext;
import net.minecraft.world.level.levelgen.material.rule.BlockRule;
import net.minecraft.world.level.levelgen.material.rule.MaterialRule;
import net.minecraft.world.level.levelgen.material.rule.RuleEvaluator;
import org.jspecify.annotations.Nullable;

public class ChunkTerrainBuilder {
   private static final MaterialRule DISABLED_MATERIAL_RULE;
   private final MaterialRuleContext ruleContext;
   private final RuleEvaluator ruleEvaluator;
   private final SurfaceExtensions surfaceExtensions;
   private final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

   public ChunkTerrainBuilder(final RandomState randomState, final DensitySamplerSet densitySamplers, final DensityVolume expectedVolume, final VerticalAnchor.Context verticalAnchorContext, final MaterialRule materialRule, final BiomeResolver biomeResolver, final @Nullable Set<Holder<Biome>> possibleBiomes) {
      super();
      this.ruleContext = new MaterialRuleContext(randomState.materialSystem(), randomState, expectedVolume, densitySamplers, biomeResolver, verticalAnchorContext, possibleBiomes);
      MaterialRule modifiedRule = SharedConstants.DEBUG_DISABLE_SURFACE ? DISABLED_MATERIAL_RULE : materialRule;
      this.ruleEvaluator = modifiedRule.compile(this.ruleContext);
      this.surfaceExtensions = randomState.materialSystem().surfaceExtensions();
   }

   public void fillChunk(final ChunkAccess chunk, final NoiseChunk noiseChunk, final @Nullable CarvingMask carvingMask) {
      DensityVolume volume = noiseChunk.volume();

      for(LevelChunkSection section : chunk.getSections()) {
         section.acquire();
      }

      boolean var20 = false;

      try {
         var20 = true;

         for(int var22 = 0; var22 < volume.sizeZ(); ++var22) {
            int blockZ = volume.blockZ(var22);

            for(int x = 0; x < volume.sizeX(); ++x) {
               int blockX = volume.blockX(x);
               NoiseColumn column = noiseChunk.prepareColumn(x, var22);
               int initialHeight = column.topBlockY() + 1;
               Holder<Biome> surfaceBiome = chunk.getBiome(blockX, initialHeight, blockZ);
               if (surfaceBiome.is(Biomes.ERODED_BADLANDS)) {
                  this.surfaceExtensions.extendErodedBadlands(column, blockX, blockZ);
               }

               CarvingMask.Column carvingColumn = carvingMask != null ? carvingMask.getColumn(x, var22) : null;
               this.fillColumn(chunk, column, blockX, blockZ, carvingColumn, noiseChunk.aquifer());
               if (surfaceBiome.is(Biomes.FROZEN_OCEAN) || surfaceBiome.is(Biomes.DEEP_FROZEN_OCEAN)) {
                  BlockColumn blockColumn = this.createBlockColumn(chunk, blockX, blockZ, carvingColumn);
                  this.surfaceExtensions.extendFrozenOcean(this.ruleContext.getMinSurfaceLevel(), surfaceBiome.value(), blockColumn, blockX, blockZ, initialHeight);
               }
            }
         }

         var20 = false;
      } finally {
         if (var20) {
            for(LevelChunkSection section : chunk.getSections()) {
               section.release();
            }

         }
      }

      for(LevelChunkSection section : chunk.getSections()) {
         section.release();
      }

   }

   private BlockColumn createBlockColumn(final ChunkAccess chunk, final int blockX, final int blockZ, final CarvingMask.@Nullable Column carvingColumn) {
      final LevelHeightAccessor generationHeight = chunk.getHeightAccessorForGeneration();
      final int localX = SectionPos.sectionRelative(blockX);
      final int localZ = SectionPos.sectionRelative(blockZ);
      return new BlockColumn() {
         private final BlockPos.MutableBlockPos mutablePos;

         {
            Objects.requireNonNull(ChunkTerrainBuilder.this);
            this.mutablePos = (new BlockPos.MutableBlockPos()).set(blockX, 0, blockZ);
         }

         public BlockState getBlock(final int blockY) {
            return chunk.getBlockState(this.mutablePos.setY(blockY));
         }

         public void setBlock(final int blockY, final BlockState state) {
            if (!generationHeight.isOutsideBuildHeight(blockY)) {
               if (carvingColumn == null || !carvingColumn.isCarved(blockY)) {
                  ChunkTerrainBuilder.setBlock(chunk, localX, blockY, localZ, state);
                  if (!state.getFluidState().isEmpty()) {
                     chunk.markPosForPostProcessing(this.mutablePos.setY(blockY));
                  }

               }
            }
         }
      };
   }

   @VisibleForTesting
   public void fillColumn(final ChunkAccess chunk, final NoiseColumn column, final int blockX, final int blockZ, final CarvingMask.@Nullable Column carvingColumn, final Aquifer aquifer) {
      BlockPos.MutableBlockPos mutablePos = this.mutablePos.setX(blockX).setZ(blockZ);
      int localX = SectionPos.sectionRelative(blockX);
      int localZ = SectionPos.sectionRelative(blockZ);
      LevelHeightAccessor heightAccessor = chunk.getHeightAccessorForGeneration();
      int minGenerationY = heightAccessor.getMinY();
      int maxGenerationY = heightAccessor.getMaxY();
      this.ruleContext.updateXZ(blockX, blockZ, column.surfaceGradientX(), column.surfaceGradientZ());
      int solidAboveDepth = 0;
      int fluidHeight = -2147483648;
      int nextCeilingY = 2147483647;
      boolean carvedTopBlock = false;

      for(int y = column.topIndexY(); y >= 0; --y) {
         int blockY = column.blockY(y);
         boolean inReplaceableRange = blockY >= minGenerationY && blockY <= maxGenerationY;
         mutablePos.setY(blockY);
         boolean carved = carvingColumn != null && carvingColumn.isCarved(blockY);
         BlockState noiseBlock = column.getBlockForIndex(y);
         if (noiseBlock == NoiseColumn.AIR) {
            solidAboveDepth = 0;
            fluidHeight = -2147483648;
            carvedTopBlock &= carved;
         } else if (noiseBlock != NoiseColumn.SOLID) {
            if (fluidHeight == -2147483648) {
               fluidHeight = blockY + 1;
            }

            if (inReplaceableRange) {
               setBlock(chunk, localX, blockY, localZ, noiseBlock);
               if (column.scheduleFluidUpdateForIndex(y)) {
                  chunk.markPosForPostProcessing(mutablePos);
               }
            }

            carvedTopBlock &= carved;
         } else {
            if (y <= nextCeilingY) {
               nextCeilingY = column.getCeilingBelowIndex(y);
            }

            ++solidAboveDepth;
            if (inReplaceableRange) {
               int solidBelowDepth = y - nextCeilingY + 1;
               this.ruleContext.updateY(solidAboveDepth, solidBelowDepth, fluidHeight, blockY);
               BlockState block = this.ruleEvaluator.tryApply(blockX, blockY, blockZ);
               if (carved && (block == null || !block.is(BlockTags.UNCARVABLE))) {
                  BlockState aquiferBlock = aquifer.computeSubstance(blockX, blockY, blockZ, 0.0);
                  if (aquiferBlock == NoiseColumn.AIR) {
                     carvedTopBlock |= solidAboveDepth == 1;
                     continue;
                  }

                  if (aquiferBlock != NoiseColumn.SOLID) {
                     carvedTopBlock |= solidAboveDepth == 1;
                     setBlock(chunk, localX, blockY, localZ, aquiferBlock);
                     if (aquifer.shouldScheduleFluidUpdate()) {
                        chunk.markPosForPostProcessing(mutablePos);
                     }
                     continue;
                  }
               }

               if (carvedTopBlock) {
                  if (block != null && block.is(Blocks.DIRT)) {
                     this.ruleContext.updateY(1, solidBelowDepth, fluidHeight, blockY);
                     block = this.ruleEvaluator.tryApply(blockX, blockY, blockZ);
                  }

                  carvedTopBlock = false;
               }

               if (block != null) {
                  setBlock(chunk, localX, blockY, localZ, block);
                  if (!block.getFluidState().isEmpty()) {
                     chunk.markPosForPostProcessing(mutablePos);
                  }
               }
            }
         }
      }

   }

   private static void setBlock(final ChunkAccess chunk, final int x, final int blockY, final int z, final BlockState block) {
      LevelChunkSection section = chunk.getSection(chunk.getSectionIndex(blockY));
      section.setBlockState(x, SectionPos.sectionRelative(blockY), z, block, false);
   }

   static {
      DISABLED_MATERIAL_RULE = new BlockRule(Blocks.STONE.defaultBlockState());
   }
}
