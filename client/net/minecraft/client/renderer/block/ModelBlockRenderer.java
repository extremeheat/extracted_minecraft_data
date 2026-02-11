package net.minecraft.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadBrightness;
import com.mojang.blaze3d.vertex.QuadLightmapCoords;
import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import java.util.List;
import java.util.Objects;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3fc;

public class ModelBlockRenderer {
   private static final Direction[] DIRECTIONS = Direction.values();
   private final BlockColors blockColors;
   private static final int CACHE_SIZE = 100;
   private static final ThreadLocal<Cache> CACHE = ThreadLocal.withInitial(Cache::new);

   public ModelBlockRenderer(final BlockColors blockColors) {
      super();
      this.blockColors = blockColors;
   }

   public void tesselateBlock(final BlockAndTintGetter level, final List<BlockModelPart> parts, final BlockState blockState, final BlockPos pos, final PoseStack poseStack, final BakedQuadOutput output, final boolean cull, final int overlayCoords) {
      if (!parts.isEmpty()) {
         boolean useAO = Minecraft.useAmbientOcclusion() && blockState.getLightEmission() == 0 && ((BlockModelPart)parts.getFirst()).useAmbientOcclusion();
         poseStack.translate(blockState.getOffset(pos));

         try {
            if (useAO) {
               this.tesselateWithAO(level, parts, blockState, pos, poseStack, output, cull, overlayCoords);
            } else {
               this.tesselateWithoutAO(level, parts, blockState, pos, poseStack, output, cull, overlayCoords);
            }

         } catch (Throwable t) {
            CrashReport report = CrashReport.forThrowable(t, "Tesselating block model");
            CrashReportCategory category = report.addCategory("Block model being tesselated");
            CrashReportCategory.populateBlockDetails(category, level, pos, blockState);
            category.setDetail("Using AO", useAO);
            throw new ReportedException(report);
         }
      }
   }

   private static boolean shouldRenderFace(final BlockAndTintGetter level, final BlockState state, final boolean cullEnabled, final Direction direction, final BlockPos neighborPos) {
      if (!cullEnabled) {
         return true;
      } else {
         BlockState neighborState = level.getBlockState(neighborPos);
         return Block.shouldRenderFace(state, neighborState, direction);
      }
   }

   public void tesselateWithAO(final BlockAndTintGetter level, final List<BlockModelPart> parts, final BlockState state, final BlockPos pos, final PoseStack poseStack, final BakedQuadOutput output, final boolean cull, final int overlayCoords) {
      AmbientOcclusionRenderStorage scratch = new AmbientOcclusionRenderStorage();
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
                     shouldRenderFace = shouldRenderFace(level, state, cull, direction, scratch.scratchPos.setWithOffset(pos, (Direction)direction));
                     cacheValid |= cacheMask;
                     if (shouldRenderFace) {
                        shouldRenderFaceCache |= cacheMask;
                     }
                  }

                  if (shouldRenderFace) {
                     this.renderModelFaceAO(level, state, pos, poseStack, output, culledQuads, scratch, overlayCoords);
                  }
               }
            }
         }

         List<BakedQuad> unculledQuads = part.getQuads((Direction)null);
         if (!unculledQuads.isEmpty()) {
            this.renderModelFaceAO(level, state, pos, poseStack, output, unculledQuads, scratch, overlayCoords);
         }
      }

   }

   public void tesselateWithoutAO(final BlockAndTintGetter level, final List<BlockModelPart> parts, final BlockState state, final BlockPos pos, final PoseStack poseStack, final BakedQuadOutput output, final boolean cull, final int overlayCoords) {
      CommonRenderStorage scratch = new CommonRenderStorage();
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
                  BlockPos relativePos = scratch.scratchPos.setWithOffset(pos, (Direction)direction);
                  if (!validCacheForDirection) {
                     shouldRenderFace = shouldRenderFace(level, state, cull, direction, relativePos);
                     cacheValid |= cacheMask;
                     if (shouldRenderFace) {
                        shouldRenderFaceCache |= cacheMask;
                     }
                  }

                  if (shouldRenderFace) {
                     int lightCoords = scratch.cache.getLightCoords(state, level, relativePos);
                     this.renderModelFaceFlat(level, state, pos, lightCoords, overlayCoords, false, poseStack, output, culledQuads, scratch);
                  }
               }
            }
         }

         List<BakedQuad> unculledQuads = part.getQuads((Direction)null);
         if (!unculledQuads.isEmpty()) {
            this.renderModelFaceFlat(level, state, pos, -1, overlayCoords, true, poseStack, output, unculledQuads, scratch);
         }
      }

   }

   private void renderModelFaceAO(final BlockAndTintGetter level, final BlockState state, final BlockPos pos, final PoseStack poseStack, final BakedQuadOutput output, final List<BakedQuad> quads, final AmbientOcclusionRenderStorage storage, final int overlayCoords) {
      for(BakedQuad quad : quads) {
         calculateShape(level, state, pos, quad, storage);
         storage.calculate(level, state, pos, quad.direction(), quad.shade());
         this.putQuadData(level, state, pos, output, poseStack.last(), quad, storage, overlayCoords);
      }

   }

   private void putQuadData(final BlockAndTintGetter level, final BlockState state, final BlockPos pos, final BakedQuadOutput output, final PoseStack.Pose pose, final BakedQuad quad, final CommonRenderStorage renderStorage, final int overlayCoords) {
      int tintIndex = quad.tintIndex();
      int tintColor = -1;
      if (tintIndex != -1) {
         if (renderStorage.tintCacheIndex == tintIndex) {
            tintColor = renderStorage.tintCacheValue;
         } else {
            tintColor = this.blockColors.getColor(state, level, pos, tintIndex);
            renderStorage.tintCacheIndex = tintIndex;
            renderStorage.tintCacheValue = tintColor;
         }
      }

      output.put(pose, quad, renderStorage.brightness, tintColor, renderStorage.lightmap, overlayCoords);
   }

   private static void calculateShape(final BlockAndTintGetter level, final BlockState state, final BlockPos pos, final BakedQuad quad, final CommonRenderStorage storage) {
      float minX = 32.0F;
      float minY = 32.0F;
      float minZ = 32.0F;
      float maxX = -32.0F;
      float maxY = -32.0F;
      float maxZ = -32.0F;

      for(int i = 0; i < 4; ++i) {
         Vector3fc position = quad.position(i);
         float x = position.x();
         float y = position.y();
         float z = position.z();
         minX = Math.min(minX, x);
         minY = Math.min(minY, y);
         minZ = Math.min(minZ, z);
         maxX = Math.max(maxX, x);
         maxY = Math.max(maxY, y);
         maxZ = Math.max(maxZ, z);
      }

      if (storage instanceof AmbientOcclusionRenderStorage aoStorage) {
         aoStorage.faceShape[ModelBlockRenderer.SizeInfo.WEST.index] = minX;
         aoStorage.faceShape[ModelBlockRenderer.SizeInfo.EAST.index] = maxX;
         aoStorage.faceShape[ModelBlockRenderer.SizeInfo.DOWN.index] = minY;
         aoStorage.faceShape[ModelBlockRenderer.SizeInfo.UP.index] = maxY;
         aoStorage.faceShape[ModelBlockRenderer.SizeInfo.NORTH.index] = minZ;
         aoStorage.faceShape[ModelBlockRenderer.SizeInfo.SOUTH.index] = maxZ;
         aoStorage.faceShape[ModelBlockRenderer.SizeInfo.FLIP_WEST.index] = 1.0F - minX;
         aoStorage.faceShape[ModelBlockRenderer.SizeInfo.FLIP_EAST.index] = 1.0F - maxX;
         aoStorage.faceShape[ModelBlockRenderer.SizeInfo.FLIP_DOWN.index] = 1.0F - minY;
         aoStorage.faceShape[ModelBlockRenderer.SizeInfo.FLIP_UP.index] = 1.0F - maxY;
         aoStorage.faceShape[ModelBlockRenderer.SizeInfo.FLIP_NORTH.index] = 1.0F - minZ;
         aoStorage.faceShape[ModelBlockRenderer.SizeInfo.FLIP_SOUTH.index] = 1.0F - maxZ;
      }

      float minEpsilon = 1.0E-4F;
      float maxEpsilon = 0.9999F;
      boolean var10001;
      switch (quad.direction()) {
         case DOWN:
         case UP:
            var10001 = minX >= 1.0E-4F || minZ >= 1.0E-4F || maxX <= 0.9999F || maxZ <= 0.9999F;
            break;
         case NORTH:
         case SOUTH:
            var10001 = minX >= 1.0E-4F || minY >= 1.0E-4F || maxX <= 0.9999F || maxY <= 0.9999F;
            break;
         case WEST:
         case EAST:
            var10001 = minY >= 1.0E-4F || minZ >= 1.0E-4F || maxY <= 0.9999F || maxZ <= 0.9999F;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      storage.facePartial = var10001;
      switch (quad.direction()) {
         case DOWN -> var10001 = minY == maxY && (minY < 1.0E-4F || state.isCollisionShapeFullBlock(level, pos));
         case UP -> var10001 = minY == maxY && (maxY > 0.9999F || state.isCollisionShapeFullBlock(level, pos));
         case NORTH -> var10001 = minZ == maxZ && (minZ < 1.0E-4F || state.isCollisionShapeFullBlock(level, pos));
         case SOUTH -> var10001 = minZ == maxZ && (maxZ > 0.9999F || state.isCollisionShapeFullBlock(level, pos));
         case WEST -> var10001 = minX == maxX && (minX < 1.0E-4F || state.isCollisionShapeFullBlock(level, pos));
         case EAST -> var10001 = minX == maxX && (maxX > 0.9999F || state.isCollisionShapeFullBlock(level, pos));
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      storage.faceCubic = var10001;
   }

   private void renderModelFaceFlat(final BlockAndTintGetter level, final BlockState state, final BlockPos pos, int lightCoords, final int overlayCoords, final boolean checkLight, final PoseStack poseStack, final BakedQuadOutput output, final List<BakedQuad> quads, final CommonRenderStorage shapeState) {
      for(BakedQuad quad : quads) {
         if (checkLight) {
            calculateShape(level, state, pos, quad, shapeState);
            BlockPos lightPos = (BlockPos)(shapeState.faceCubic ? shapeState.scratchPos.setWithOffset(pos, (Direction)quad.direction()) : pos);
            lightCoords = shapeState.cache.getLightCoords(state, level, lightPos);
         }

         float directionalBrightness = level.getShade(quad.direction(), quad.shade());
         shapeState.brightness.setAll(directionalBrightness);
         shapeState.lightmap.setAll(lightCoords);
         this.putQuadData(level, state, pos, output, poseStack.last(), quad, shapeState, overlayCoords);
      }

   }

   public static void renderModel(final PoseStack.Pose pose, final BakedQuadOutput output, final BlockStateModel model, final int tintColor, final int lightCoords, final int overlayCoords) {
      QuadLightmapCoords wrappedLightmapCoords = QuadLightmapCoords.create(lightCoords);

      for(BlockModelPart part : model.collectParts(RandomSource.create(42L))) {
         for(Direction direction : DIRECTIONS) {
            renderQuadList(pose, output, tintColor, part.getQuads(direction), wrappedLightmapCoords, overlayCoords);
         }

         renderQuadList(pose, output, tintColor, part.getQuads((Direction)null), wrappedLightmapCoords, overlayCoords);
      }

   }

   private static void renderQuadList(final PoseStack.Pose pose, final BakedQuadOutput output, final int tintColor, final List<BakedQuad> quads, final QuadLightmapCoords lightCoords, final int overlayCoords) {
      for(BakedQuad quad : quads) {
         int quadColor = quad.isTinted() ? tintColor : -1;
         output.put(pose, quad, QuadBrightness.ALL_BRIGHT, quadColor, lightCoords, overlayCoords);
      }

   }

   public static void enableCaching() {
      ((Cache)CACHE.get()).enable();
   }

   public static void clearCache() {
      ((Cache)CACHE.get()).disable();
   }

   private static enum AmbientVertexRemap {
      DOWN(0, 1, 2, 3),
      UP(2, 3, 0, 1),
      NORTH(3, 0, 1, 2),
      SOUTH(0, 1, 2, 3),
      WEST(3, 0, 1, 2),
      EAST(1, 2, 3, 0);

      private final int vert0;
      private final int vert1;
      private final int vert2;
      private final int vert3;
      private static final AmbientVertexRemap[] BY_FACING = (AmbientVertexRemap[])Util.make(new AmbientVertexRemap[6], (map) -> {
         map[Direction.DOWN.get3DDataValue()] = DOWN;
         map[Direction.UP.get3DDataValue()] = UP;
         map[Direction.NORTH.get3DDataValue()] = NORTH;
         map[Direction.SOUTH.get3DDataValue()] = SOUTH;
         map[Direction.WEST.get3DDataValue()] = WEST;
         map[Direction.EAST.get3DDataValue()] = EAST;
      });

      private AmbientVertexRemap(final int vert0, final int vert1, final int vert2, final int vert3) {
         this.vert0 = vert0;
         this.vert1 = vert1;
         this.vert2 = vert2;
         this.vert3 = vert3;
      }

      public static AmbientVertexRemap fromFacing(final Direction direction) {
         return BY_FACING[direction.get3DDataValue()];
      }

      // $FF: synthetic method
      private static AmbientVertexRemap[] $values() {
         return new AmbientVertexRemap[]{DOWN, UP, NORTH, SOUTH, WEST, EAST};
      }
   }

   private static class Cache {
      private boolean enabled;
      private final Long2IntLinkedOpenHashMap colorCache = (Long2IntLinkedOpenHashMap)Util.make(() -> {
         Long2IntLinkedOpenHashMap map = new Long2IntLinkedOpenHashMap(100, 0.25F) {
            {
               Objects.requireNonNull(Cache.this);
            }

            protected void rehash(final int newN) {
            }
         };
         map.defaultReturnValue(2147483647);
         return map;
      });
      private final Long2FloatLinkedOpenHashMap brightnessCache = (Long2FloatLinkedOpenHashMap)Util.make(() -> {
         Long2FloatLinkedOpenHashMap map = new Long2FloatLinkedOpenHashMap(100, 0.25F) {
            {
               Objects.requireNonNull(Cache.this);
            }

            protected void rehash(final int newN) {
            }
         };
         map.defaultReturnValue(0.0F / 0.0F);
         return map;
      });
      private final LevelRenderer.BrightnessGetter cachedBrightnessGetter = (level, pos) -> {
         long key = pos.asLong();
         int cached = this.colorCache.get(key);
         if (cached != 2147483647) {
            return cached;
         } else {
            int value = LevelRenderer.BrightnessGetter.DEFAULT.packedBrightness(level, pos);
            if (this.colorCache.size() == 100) {
               this.colorCache.removeFirstInt();
            }

            this.colorCache.put(key, value);
            return value;
         }
      };

      private Cache() {
         super();
      }

      public void enable() {
         this.enabled = true;
      }

      public void disable() {
         this.enabled = false;
         this.colorCache.clear();
         this.brightnessCache.clear();
      }

      public int getLightCoords(final BlockState state, final BlockAndTintGetter level, final BlockPos pos) {
         return LevelRenderer.getLightCoords(this.enabled ? this.cachedBrightnessGetter : LevelRenderer.BrightnessGetter.DEFAULT, level, state, pos);
      }

      public float getShadeBrightness(final BlockState state, final BlockAndTintGetter level, final BlockPos pos) {
         long key = pos.asLong();
         if (this.enabled) {
            float cached = this.brightnessCache.get(key);
            if (!Float.isNaN(cached)) {
               return cached;
            }
         }

         float brightness = state.getShadeBrightness(level, pos);
         if (this.enabled) {
            if (this.brightnessCache.size() == 100) {
               this.brightnessCache.removeFirstFloat();
            }

            this.brightnessCache.put(key, brightness);
         }

         return brightness;
      }
   }

   private static class CommonRenderStorage {
      public final BlockPos.MutableBlockPos scratchPos = new BlockPos.MutableBlockPos();
      public boolean faceCubic;
      public boolean facePartial;
      public final QuadBrightness.Mutable brightness = new QuadBrightness.Mutable();
      public final QuadLightmapCoords.Mutable lightmap = new QuadLightmapCoords.Mutable();
      public int tintCacheIndex = -1;
      public int tintCacheValue;
      public final Cache cache;

      private CommonRenderStorage() {
         super();
         this.cache = (Cache)ModelBlockRenderer.CACHE.get();
      }
   }

   private static class AmbientOcclusionRenderStorage extends CommonRenderStorage {
      private final float[] faceShape;

      public AmbientOcclusionRenderStorage() {
         super();
         this.faceShape = new float[ModelBlockRenderer.SizeInfo.COUNT];
      }

      public void calculate(final BlockAndTintGetter level, final BlockState state, final BlockPos centerPosition, final Direction direction, final boolean shade) {
         BlockPos basePosition = this.faceCubic ? centerPosition.relative(direction) : centerPosition;
         AdjacencyInfo info = ModelBlockRenderer.AdjacencyInfo.fromFacing(direction);
         BlockPos.MutableBlockPos pos = this.scratchPos;
         pos.setWithOffset(basePosition, (Direction)info.corners[0]);
         BlockState state0 = level.getBlockState(pos);
         int light0 = this.cache.getLightCoords(state0, level, pos);
         float shade0 = this.cache.getShadeBrightness(state0, level, pos);
         pos.setWithOffset(basePosition, (Direction)info.corners[1]);
         BlockState state1 = level.getBlockState(pos);
         int light1 = this.cache.getLightCoords(state1, level, pos);
         float shade1 = this.cache.getShadeBrightness(state1, level, pos);
         pos.setWithOffset(basePosition, (Direction)info.corners[2]);
         BlockState state2 = level.getBlockState(pos);
         int light2 = this.cache.getLightCoords(state2, level, pos);
         float shade2 = this.cache.getShadeBrightness(state2, level, pos);
         pos.setWithOffset(basePosition, (Direction)info.corners[3]);
         BlockState state3 = level.getBlockState(pos);
         int light3 = this.cache.getLightCoords(state3, level, pos);
         float shade3 = this.cache.getShadeBrightness(state3, level, pos);
         BlockState corner0 = level.getBlockState(pos.setWithOffset(basePosition, (Direction)info.corners[0]).move(direction));
         boolean translucent0 = !corner0.isViewBlocking(level, pos) || corner0.getLightBlock() == 0;
         BlockState corner1 = level.getBlockState(pos.setWithOffset(basePosition, (Direction)info.corners[1]).move(direction));
         boolean translucent1 = !corner1.isViewBlocking(level, pos) || corner1.getLightBlock() == 0;
         BlockState corner2 = level.getBlockState(pos.setWithOffset(basePosition, (Direction)info.corners[2]).move(direction));
         boolean translucent2 = !corner2.isViewBlocking(level, pos) || corner2.getLightBlock() == 0;
         BlockState corner3 = level.getBlockState(pos.setWithOffset(basePosition, (Direction)info.corners[3]).move(direction));
         boolean translucent3 = !corner3.isViewBlocking(level, pos) || corner3.getLightBlock() == 0;
         float shadeCorner02;
         int lightCorner02;
         if (!translucent2 && !translucent0) {
            shadeCorner02 = shade0;
            lightCorner02 = light0;
         } else {
            pos.setWithOffset(basePosition, (Direction)info.corners[0]).move(info.corners[2]);
            BlockState state02 = level.getBlockState(pos);
            shadeCorner02 = this.cache.getShadeBrightness(state02, level, pos);
            lightCorner02 = this.cache.getLightCoords(state02, level, pos);
         }

         float shadeCorner03;
         int lightCorner03;
         if (!translucent3 && !translucent0) {
            shadeCorner03 = shade0;
            lightCorner03 = light0;
         } else {
            pos.setWithOffset(basePosition, (Direction)info.corners[0]).move(info.corners[3]);
            BlockState state03 = level.getBlockState(pos);
            shadeCorner03 = this.cache.getShadeBrightness(state03, level, pos);
            lightCorner03 = this.cache.getLightCoords(state03, level, pos);
         }

         float shadeCorner12;
         int lightCorner12;
         if (!translucent2 && !translucent1) {
            shadeCorner12 = shade0;
            lightCorner12 = light0;
         } else {
            pos.setWithOffset(basePosition, (Direction)info.corners[1]).move(info.corners[2]);
            BlockState state12 = level.getBlockState(pos);
            shadeCorner12 = this.cache.getShadeBrightness(state12, level, pos);
            lightCorner12 = this.cache.getLightCoords(state12, level, pos);
         }

         float shadeCorner13;
         int lightCorner13;
         if (!translucent3 && !translucent1) {
            shadeCorner13 = shade0;
            lightCorner13 = light0;
         } else {
            pos.setWithOffset(basePosition, (Direction)info.corners[1]).move(info.corners[3]);
            BlockState state13 = level.getBlockState(pos);
            shadeCorner13 = this.cache.getShadeBrightness(state13, level, pos);
            lightCorner13 = this.cache.getLightCoords(state13, level, pos);
         }

         int lightCenter = this.cache.getLightCoords(state, level, centerPosition);
         pos.setWithOffset(centerPosition, (Direction)direction);
         BlockState nextState = level.getBlockState(pos);
         if (this.faceCubic || !nextState.isSolidRender()) {
            lightCenter = this.cache.getLightCoords(nextState, level, pos);
         }

         float shadeCenter = this.faceCubic ? this.cache.getShadeBrightness(level.getBlockState(basePosition), level, basePosition) : this.cache.getShadeBrightness(level.getBlockState(centerPosition), level, centerPosition);
         AmbientVertexRemap remap = ModelBlockRenderer.AmbientVertexRemap.fromFacing(direction);
         if (this.facePartial && info.doNonCubicWeight) {
            float tempShade1 = (shade3 + shade0 + shadeCorner03 + shadeCenter) * 0.25F;
            float tempShade2 = (shade2 + shade0 + shadeCorner02 + shadeCenter) * 0.25F;
            float tempShade3 = (shade2 + shade1 + shadeCorner12 + shadeCenter) * 0.25F;
            float tempShade4 = (shade3 + shade1 + shadeCorner13 + shadeCenter) * 0.25F;
            float vert0weight01 = this.faceShape[info.vert0Weights[0].index] * this.faceShape[info.vert0Weights[1].index];
            float vert0weight23 = this.faceShape[info.vert0Weights[2].index] * this.faceShape[info.vert0Weights[3].index];
            float vert0weight45 = this.faceShape[info.vert0Weights[4].index] * this.faceShape[info.vert0Weights[5].index];
            float vert0weight67 = this.faceShape[info.vert0Weights[6].index] * this.faceShape[info.vert0Weights[7].index];
            float vert1weight01 = this.faceShape[info.vert1Weights[0].index] * this.faceShape[info.vert1Weights[1].index];
            float vert1weight23 = this.faceShape[info.vert1Weights[2].index] * this.faceShape[info.vert1Weights[3].index];
            float vert1weight45 = this.faceShape[info.vert1Weights[4].index] * this.faceShape[info.vert1Weights[5].index];
            float vert1weight67 = this.faceShape[info.vert1Weights[6].index] * this.faceShape[info.vert1Weights[7].index];
            float vert2weight01 = this.faceShape[info.vert2Weights[0].index] * this.faceShape[info.vert2Weights[1].index];
            float vert2weight23 = this.faceShape[info.vert2Weights[2].index] * this.faceShape[info.vert2Weights[3].index];
            float vert2weight45 = this.faceShape[info.vert2Weights[4].index] * this.faceShape[info.vert2Weights[5].index];
            float vert2weight67 = this.faceShape[info.vert2Weights[6].index] * this.faceShape[info.vert2Weights[7].index];
            float vert3weight01 = this.faceShape[info.vert3Weights[0].index] * this.faceShape[info.vert3Weights[1].index];
            float vert3weight23 = this.faceShape[info.vert3Weights[2].index] * this.faceShape[info.vert3Weights[3].index];
            float vert3weight45 = this.faceShape[info.vert3Weights[4].index] * this.faceShape[info.vert3Weights[5].index];
            float vert3weight67 = this.faceShape[info.vert3Weights[6].index] * this.faceShape[info.vert3Weights[7].index];
            this.brightness.set(remap.vert0, Math.clamp(tempShade1 * vert0weight01 + tempShade2 * vert0weight23 + tempShade3 * vert0weight45 + tempShade4 * vert0weight67, 0.0F, 1.0F));
            this.brightness.set(remap.vert1, Math.clamp(tempShade1 * vert1weight01 + tempShade2 * vert1weight23 + tempShade3 * vert1weight45 + tempShade4 * vert1weight67, 0.0F, 1.0F));
            this.brightness.set(remap.vert2, Math.clamp(tempShade1 * vert2weight01 + tempShade2 * vert2weight23 + tempShade3 * vert2weight45 + tempShade4 * vert2weight67, 0.0F, 1.0F));
            this.brightness.set(remap.vert3, Math.clamp(tempShade1 * vert3weight01 + tempShade2 * vert3weight23 + tempShade3 * vert3weight45 + tempShade4 * vert3weight67, 0.0F, 1.0F));
            int _tc1 = LightCoordsUtil.smoothBlend(light3, light0, lightCorner03, lightCenter);
            int _tc2 = LightCoordsUtil.smoothBlend(light2, light0, lightCorner02, lightCenter);
            int _tc3 = LightCoordsUtil.smoothBlend(light2, light1, lightCorner12, lightCenter);
            int _tc4 = LightCoordsUtil.smoothBlend(light3, light1, lightCorner13, lightCenter);
            this.lightmap.set(remap.vert0, LightCoordsUtil.smoothWeightedBlend(_tc1, _tc2, _tc3, _tc4, vert0weight01, vert0weight23, vert0weight45, vert0weight67));
            this.lightmap.set(remap.vert1, LightCoordsUtil.smoothWeightedBlend(_tc1, _tc2, _tc3, _tc4, vert1weight01, vert1weight23, vert1weight45, vert1weight67));
            this.lightmap.set(remap.vert2, LightCoordsUtil.smoothWeightedBlend(_tc1, _tc2, _tc3, _tc4, vert2weight01, vert2weight23, vert2weight45, vert2weight67));
            this.lightmap.set(remap.vert3, LightCoordsUtil.smoothWeightedBlend(_tc1, _tc2, _tc3, _tc4, vert3weight01, vert3weight23, vert3weight45, vert3weight67));
         } else {
            float lightLevel1 = (shade3 + shade0 + shadeCorner03 + shadeCenter) * 0.25F;
            float lightLevel2 = (shade2 + shade0 + shadeCorner02 + shadeCenter) * 0.25F;
            float lightLevel3 = (shade2 + shade1 + shadeCorner12 + shadeCenter) * 0.25F;
            float lightLevel4 = (shade3 + shade1 + shadeCorner13 + shadeCenter) * 0.25F;
            this.lightmap.set(remap.vert0, LightCoordsUtil.smoothBlend(light3, light0, lightCorner03, lightCenter));
            this.lightmap.set(remap.vert1, LightCoordsUtil.smoothBlend(light2, light0, lightCorner02, lightCenter));
            this.lightmap.set(remap.vert2, LightCoordsUtil.smoothBlend(light2, light1, lightCorner12, lightCenter));
            this.lightmap.set(remap.vert3, LightCoordsUtil.smoothBlend(light3, light1, lightCorner13, lightCenter));
            this.brightness.set(remap.vert0, lightLevel1);
            this.brightness.set(remap.vert1, lightLevel2);
            this.brightness.set(remap.vert2, lightLevel3);
            this.brightness.set(remap.vert3, lightLevel4);
         }

         this.brightness.multiplyAll(level.getShade(direction, shade));
      }
   }

   protected static enum SizeInfo {
      DOWN(0),
      UP(1),
      NORTH(2),
      SOUTH(3),
      WEST(4),
      EAST(5),
      FLIP_DOWN(6),
      FLIP_UP(7),
      FLIP_NORTH(8),
      FLIP_SOUTH(9),
      FLIP_WEST(10),
      FLIP_EAST(11);

      public static final int COUNT = values().length;
      private final int index;

      private SizeInfo(final int index) {
         this.index = index;
      }

      // $FF: synthetic method
      private static SizeInfo[] $values() {
         return new SizeInfo[]{DOWN, UP, NORTH, SOUTH, WEST, EAST, FLIP_DOWN, FLIP_UP, FLIP_NORTH, FLIP_SOUTH, FLIP_WEST, FLIP_EAST};
      }
   }

   protected static enum AdjacencyInfo {
      DOWN(new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH}, 0.5F, true, new SizeInfo[]{ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.SOUTH, ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.SOUTH}, new SizeInfo[]{ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.NORTH, ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.NORTH}, new SizeInfo[]{ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.NORTH, ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.NORTH}, new SizeInfo[]{ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.SOUTH, ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.SOUTH}),
      UP(new Direction[]{Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH}, 1.0F, true, new SizeInfo[]{ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.SOUTH, ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.SOUTH}, new SizeInfo[]{ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.NORTH, ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.NORTH}, new SizeInfo[]{ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.NORTH, ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.NORTH}, new SizeInfo[]{ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.SOUTH, ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.SOUTH}),
      NORTH(new Direction[]{Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST}, 0.8F, true, new SizeInfo[]{ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.FLIP_WEST}, new SizeInfo[]{ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.FLIP_EAST}, new SizeInfo[]{ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.FLIP_EAST}, new SizeInfo[]{ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.FLIP_WEST}),
      SOUTH(new Direction[]{Direction.WEST, Direction.EAST, Direction.DOWN, Direction.UP}, 0.8F, true, new SizeInfo[]{ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.WEST}, new SizeInfo[]{ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.WEST}, new SizeInfo[]{ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.EAST}, new SizeInfo[]{ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.EAST}),
      WEST(new Direction[]{Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH}, 0.6F, true, new SizeInfo[]{ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.SOUTH, ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.SOUTH}, new SizeInfo[]{ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.NORTH, ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.NORTH}, new SizeInfo[]{ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.NORTH, ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.NORTH}, new SizeInfo[]{ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.SOUTH, ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.SOUTH}),
      EAST(new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH}, 0.6F, true, new SizeInfo[]{ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.SOUTH, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.SOUTH}, new SizeInfo[]{ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.NORTH, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.NORTH}, new SizeInfo[]{ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.NORTH, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.NORTH}, new SizeInfo[]{ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.SOUTH, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.SOUTH});

      private final Direction[] corners;
      private final boolean doNonCubicWeight;
      private final SizeInfo[] vert0Weights;
      private final SizeInfo[] vert1Weights;
      private final SizeInfo[] vert2Weights;
      private final SizeInfo[] vert3Weights;
      private static final AdjacencyInfo[] BY_FACING = (AdjacencyInfo[])Util.make(new AdjacencyInfo[6], (map) -> {
         map[Direction.DOWN.get3DDataValue()] = DOWN;
         map[Direction.UP.get3DDataValue()] = UP;
         map[Direction.NORTH.get3DDataValue()] = NORTH;
         map[Direction.SOUTH.get3DDataValue()] = SOUTH;
         map[Direction.WEST.get3DDataValue()] = WEST;
         map[Direction.EAST.get3DDataValue()] = EAST;
      });

      private AdjacencyInfo(final Direction[] corners, final float shadeWeight, final boolean doNonCubicWeight, final SizeInfo[] vert0Weights, final SizeInfo[] vert1Weights, final SizeInfo[] vert2Weights, final SizeInfo[] vert3Weights) {
         this.corners = corners;
         this.doNonCubicWeight = doNonCubicWeight;
         this.vert0Weights = vert0Weights;
         this.vert1Weights = vert1Weights;
         this.vert2Weights = vert2Weights;
         this.vert3Weights = vert3Weights;
      }

      public static AdjacencyInfo fromFacing(final Direction direction) {
         return BY_FACING[direction.get3DDataValue()];
      }

      // $FF: synthetic method
      private static AdjacencyInfo[] $values() {
         return new AdjacencyInfo[]{DOWN, UP, NORTH, SOUTH, WEST, EAST};
      }
   }
}
