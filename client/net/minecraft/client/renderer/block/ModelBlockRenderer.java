package net.minecraft.client.renderer.block;

import com.mojang.blaze3d.vertex.QuadInstance;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class ModelBlockRenderer {
   private static final Direction[] DIRECTIONS = Direction.values();
   private final BlockModelLighter lighter = new BlockModelLighter();
   private final boolean ambientOcclusion;
   private final boolean cull;
   private final BlockColors blockColors;
   private final RandomSource random = RandomSource.createThreadLocalInstance(0L);
   private final List<BlockModelPart> parts = new ObjectArrayList();
   private final BlockPos.MutableBlockPos scratchPos = new BlockPos.MutableBlockPos();
   private final QuadInstance quadInstance = new QuadInstance();
   private final QuadInstance tintedQuadInstance = new QuadInstance();
   private int tintCacheIndex = -1;
   private int tintCacheValue;

   public ModelBlockRenderer(final boolean ambientOcclusion, final boolean cull, final BlockColors blockColors) {
      super();
      this.ambientOcclusion = ambientOcclusion;
      this.cull = cull;
      this.blockColors = blockColors;
   }

   public static boolean forceOpaque(final boolean cutoutLeaves, final BlockState blockState) {
      return !cutoutLeaves && blockState.getBlock() instanceof LeavesBlock;
   }

   public void tesselateBlock(final BlockQuadOutput output, final float x, final float y, final float z, final BlockAndTintGetter level, final BlockPos pos, final BlockState blockState, final BlockStateModel model, final long seed) {
      this.random.setSeed(seed);
      model.collectParts(this.random, this.parts);
      if (!this.parts.isEmpty()) {
         try {
            Vec3 offset = blockState.getOffset(pos);
            if (this.ambientOcclusion && blockState.getLightEmission() == 0 && ((BlockModelPart)this.parts.getFirst()).useAmbientOcclusion()) {
               this.tesselateAmbientOcclusion(output, x + (float)offset.x, y + (float)offset.y, z + (float)offset.z, this.parts, level, blockState, pos);
            } else {
               this.tesselateFlat(output, x + (float)offset.x, y + (float)offset.y, z + (float)offset.z, this.parts, level, blockState, pos);
            }
         } finally {
            this.parts.clear();
            this.tintCacheIndex = -1;
         }

      }
   }

   private void tesselateAmbientOcclusion(final BlockQuadOutput output, final float x, final float y, final float z, final List<BlockModelPart> parts, final BlockAndTintGetter level, final BlockState state, final BlockPos pos) {
      int cacheValid = 0;
      int shouldRenderFaceCache = 0;

      for(BlockModelPart part : parts) {
         for(Direction direction : DIRECTIONS) {
            int cacheMask = 1 << direction.ordinal();
            boolean validCacheForDirection = (cacheValid & cacheMask) == 1;
            boolean shouldRenderFace = (shouldRenderFaceCache & cacheMask) == 1;
            if (!validCacheForDirection || shouldRenderFace) {
               List<BakedQuad> culledQuads = part.getQuads(direction);
               if (!culledQuads.isEmpty()) {
                  if (!validCacheForDirection) {
                     shouldRenderFace = this.shouldRenderFace(level, state, direction, this.scratchPos.setWithOffset(pos, (Direction)direction));
                     cacheValid |= cacheMask;
                     if (shouldRenderFace) {
                        shouldRenderFaceCache |= cacheMask;
                     }
                  }

                  if (shouldRenderFace) {
                     for(BakedQuad quad : culledQuads) {
                        this.lighter.prepareQuadAmbientOcclusion(level, state, pos, quad, this.quadInstance);
                        this.putQuadWithTint(output, x, y, z, level, state, pos, quad);
                     }
                  }
               }
            }
         }

         for(BakedQuad quad : part.getQuads((Direction)null)) {
            this.lighter.prepareQuadAmbientOcclusion(level, state, pos, quad, this.quadInstance);
            this.putQuadWithTint(output, x, y, z, level, state, pos, quad);
         }
      }

   }

   private void tesselateFlat(final BlockQuadOutput output, final float x, final float y, final float z, final List<BlockModelPart> parts, final BlockAndTintGetter level, final BlockState state, final BlockPos pos) {
      int cacheValid = 0;
      int shouldRenderFaceCache = 0;

      for(BlockModelPart part : parts) {
         for(Direction direction : DIRECTIONS) {
            int cacheMask = 1 << direction.ordinal();
            boolean validCacheForDirection = (cacheValid & cacheMask) == 1;
            boolean shouldRenderFace = (shouldRenderFaceCache & cacheMask) == 1;
            if (!validCacheForDirection || shouldRenderFace) {
               List<BakedQuad> culledQuads = part.getQuads(direction);
               if (!culledQuads.isEmpty()) {
                  BlockPos relativePos = this.scratchPos.setWithOffset(pos, (Direction)direction);
                  if (!validCacheForDirection) {
                     shouldRenderFace = this.shouldRenderFace(level, state, direction, relativePos);
                     cacheValid |= cacheMask;
                     if (shouldRenderFace) {
                        shouldRenderFaceCache |= cacheMask;
                     }
                  }

                  if (shouldRenderFace) {
                     int lightCoords = this.lighter.getLightCoords(state, level, relativePos);

                     for(BakedQuad quad : culledQuads) {
                        this.lighter.prepareQuadFlat(level, state, pos, lightCoords, quad, this.quadInstance);
                        this.putQuadWithTint(output, x, y, z, level, state, pos, quad);
                     }
                  }
               }
            }
         }

         for(BakedQuad quad : part.getQuads((Direction)null)) {
            this.lighter.prepareQuadFlat(level, state, pos, -1, quad, this.quadInstance);
            this.putQuadWithTint(output, x, y, z, level, state, pos, quad);
         }
      }

   }

   private boolean shouldRenderFace(final BlockAndTintGetter level, final BlockState state, final Direction direction, final BlockPos neighborPos) {
      if (!this.cull) {
         return true;
      } else {
         BlockState neighborState = level.getBlockState(neighborPos);
         return Block.shouldRenderFace(state, neighborState, direction);
      }
   }

   private void putQuadWithTint(final BlockQuadOutput output, final float x, final float y, final float z, final BlockAndTintGetter level, final BlockState state, final BlockPos pos, final BakedQuad quad) {
      int tintIndex = quad.tintIndex();
      if (tintIndex != -1) {
         this.tintedQuadInstance.copyFrom(this.quadInstance);
         this.tintedQuadInstance.multiplyColor(this.getTintColor(level, state, pos, tintIndex));
         output.put(x, y, z, quad, this.tintedQuadInstance);
      } else {
         output.put(x, y, z, quad, this.quadInstance);
      }

   }

   private int getTintColor(final BlockAndTintGetter level, final BlockState state, final BlockPos pos, final int tintIndex) {
      if (this.tintCacheIndex == tintIndex) {
         return this.tintCacheValue;
      } else {
         int tintColor = this.blockColors.getColor(state, level, pos, tintIndex);
         this.tintCacheIndex = tintIndex;
         this.tintCacheValue = tintColor;
         return tintColor;
      }
   }
}
