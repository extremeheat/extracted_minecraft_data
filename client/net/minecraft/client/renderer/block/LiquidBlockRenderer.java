package net.minecraft.client.renderer.block;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.SpriteGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ARGB;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LiquidBlockRenderer {
   private static final float MAX_FLUID_HEIGHT = 0.8888889F;
   private final TextureAtlasSprite lavaStill;
   private final TextureAtlasSprite lavaFlowing;
   private final TextureAtlasSprite waterStill;
   private final TextureAtlasSprite waterFlowing;
   private final TextureAtlasSprite waterOverlay;

   public LiquidBlockRenderer(final SpriteGetter sprites) {
      super();
      this.lavaStill = sprites.get(ModelBakery.LAVA_STILL);
      this.lavaFlowing = sprites.get(ModelBakery.LAVA_FLOW);
      this.waterStill = sprites.get(ModelBakery.WATER_STILL);
      this.waterFlowing = sprites.get(ModelBakery.WATER_FLOW);
      this.waterOverlay = sprites.get(ModelBakery.WATER_OVERLAY);
   }

   private static boolean isNeighborSameFluid(final FluidState fluidState, final FluidState neighborFluidState) {
      return neighborFluidState.getType().isSame(fluidState.getType());
   }

   private static boolean isFaceOccludedByState(final Direction direction, final float height, final BlockState state) {
      VoxelShape occluder = state.getFaceOcclusionShape(direction.getOpposite());
      if (occluder == Shapes.empty()) {
         return false;
      } else if (occluder == Shapes.block()) {
         boolean fullBlock = height == 1.0F;
         return direction != Direction.UP || fullBlock;
      } else {
         VoxelShape shape = Shapes.box(0.0, 0.0, 0.0, 1.0, (double)height, 1.0);
         return Shapes.blockOccludes(shape, occluder, direction);
      }
   }

   private static boolean isFaceOccludedByNeighbor(final Direction direction, final float height, final BlockState neighborState) {
      return isFaceOccludedByState(direction, height, neighborState);
   }

   private static boolean isFaceOccludedBySelf(final BlockState state, final Direction direction) {
      return isFaceOccludedByState(direction.getOpposite(), 1.0F, state);
   }

   public static boolean shouldRenderFace(final FluidState fluidState, final BlockState blockState, final Direction direction, final FluidState neighborFluidState) {
      return !isNeighborSameFluid(fluidState, neighborFluidState) && !isFaceOccludedBySelf(blockState, direction);
   }

   public void tesselate(final BlockAndTintGetter level, final BlockPos pos, final VertexConsumer builder, final BlockState blockState, final FluidState fluidState) {
      BlockState blockStateDown = level.getBlockState(pos.relative(Direction.DOWN));
      FluidState fluidStateDown = blockStateDown.getFluidState();
      BlockState blockStateUp = level.getBlockState(pos.relative(Direction.UP));
      FluidState fluidStateUp = blockStateUp.getFluidState();
      BlockState blockStateNorth = level.getBlockState(pos.relative(Direction.NORTH));
      FluidState fluidStateNorth = blockStateNorth.getFluidState();
      BlockState blockStateSouth = level.getBlockState(pos.relative(Direction.SOUTH));
      FluidState fluidStateSouth = blockStateSouth.getFluidState();
      BlockState blockStateWest = level.getBlockState(pos.relative(Direction.WEST));
      FluidState fluidStateWest = blockStateWest.getFluidState();
      BlockState blockStateEast = level.getBlockState(pos.relative(Direction.EAST));
      FluidState fluidStateEast = blockStateEast.getFluidState();
      boolean renderUp = !isNeighborSameFluid(fluidState, fluidStateUp);
      boolean renderDown = shouldRenderFace(fluidState, blockState, Direction.DOWN, fluidStateDown) && !isFaceOccludedByNeighbor(Direction.DOWN, 0.8888889F, blockStateDown);
      boolean renderNorth = shouldRenderFace(fluidState, blockState, Direction.NORTH, fluidStateNorth);
      boolean renderSouth = shouldRenderFace(fluidState, blockState, Direction.SOUTH, fluidStateSouth);
      boolean renderWest = shouldRenderFace(fluidState, blockState, Direction.WEST, fluidStateWest);
      boolean renderEast = shouldRenderFace(fluidState, blockState, Direction.EAST, fluidStateEast);
      if (renderUp || renderDown || renderEast || renderWest || renderNorth || renderSouth) {
         boolean isLava = fluidState.is(FluidTags.LAVA);
         TextureAtlasSprite stillSprite = isLava ? this.lavaStill : this.waterStill;
         TextureAtlasSprite flowingSprite = isLava ? this.lavaFlowing : this.waterFlowing;
         int color = isLava ? -1 : BiomeColors.getAverageWaterColor(level, pos);
         float shadeDown = level.getShade(Direction.DOWN, true);
         float shadeUp = level.getShade(Direction.UP, true);
         float shadeNorth = level.getShade(Direction.NORTH, true);
         float shadeWest = level.getShade(Direction.WEST, true);
         Fluid type = fluidState.getType();
         float heightSelf = this.getHeight(level, type, pos, blockState, fluidState);
         float heightNorthEast;
         float heightNorthWest;
         float heightSouthEast;
         float heightSouthWest;
         if (heightSelf >= 1.0F) {
            heightNorthEast = 1.0F;
            heightNorthWest = 1.0F;
            heightSouthEast = 1.0F;
            heightSouthWest = 1.0F;
         } else {
            float heightNorth = this.getHeight(level, type, pos.north(), blockStateNorth, fluidStateNorth);
            float heightSouth = this.getHeight(level, type, pos.south(), blockStateSouth, fluidStateSouth);
            float heightEast = this.getHeight(level, type, pos.east(), blockStateEast, fluidStateEast);
            float heightWest = this.getHeight(level, type, pos.west(), blockStateWest, fluidStateWest);
            heightNorthEast = this.calculateAverageHeight(level, type, heightSelf, heightNorth, heightEast, pos.relative(Direction.NORTH).relative(Direction.EAST));
            heightNorthWest = this.calculateAverageHeight(level, type, heightSelf, heightNorth, heightWest, pos.relative(Direction.NORTH).relative(Direction.WEST));
            heightSouthEast = this.calculateAverageHeight(level, type, heightSelf, heightSouth, heightEast, pos.relative(Direction.SOUTH).relative(Direction.EAST));
            heightSouthWest = this.calculateAverageHeight(level, type, heightSelf, heightSouth, heightWest, pos.relative(Direction.SOUTH).relative(Direction.WEST));
         }

         float x = (float)(pos.getX() & 15);
         float y = (float)(pos.getY() & 15);
         float z = (float)(pos.getZ() & 15);
         float offs = 0.001F;
         float bottomOffs = renderDown ? 0.001F : 0.0F;
         if (renderUp && !isFaceOccludedByNeighbor(Direction.UP, Math.min(Math.min(heightNorthWest, heightSouthWest), Math.min(heightSouthEast, heightNorthEast)), blockStateUp)) {
            heightNorthWest -= 0.001F;
            heightSouthWest -= 0.001F;
            heightSouthEast -= 0.001F;
            heightNorthEast -= 0.001F;
            Vec3 flow = fluidState.getFlow(level, pos);
            float u00;
            float u01;
            float u10;
            float u11;
            float v00;
            float v01;
            float v10;
            float v11;
            if (flow.x == 0.0 && flow.z == 0.0) {
               u00 = stillSprite.getU(0.0F);
               v00 = stillSprite.getV(0.0F);
               u01 = u00;
               v01 = stillSprite.getV(1.0F);
               u10 = stillSprite.getU(1.0F);
               v10 = v01;
               u11 = u10;
               v11 = v00;
            } else {
               float angle = (float)Mth.atan2(flow.z, flow.x) - 1.5707964F;
               float s = Mth.sin((double)angle) * 0.25F;
               float c = Mth.cos((double)angle) * 0.25F;
               float cc = 0.5F;
               u00 = flowingSprite.getU(0.5F + (-c - s));
               v00 = flowingSprite.getV(0.5F + -c + s);
               u01 = flowingSprite.getU(0.5F + -c + s);
               v01 = flowingSprite.getV(0.5F + c + s);
               u10 = flowingSprite.getU(0.5F + c + s);
               v10 = flowingSprite.getV(0.5F + (c - s));
               u11 = flowingSprite.getU(0.5F + (c - s));
               v11 = flowingSprite.getV(0.5F + (-c - s));
            }

            int topLightCoords = this.getLightCoords(level, pos);
            int topColor = ARGB.scaleRGB(color, shadeUp);
            this.vertex(builder, x + 0.0F, y + heightNorthWest, z + 0.0F, topColor, u00, v00, topLightCoords);
            this.vertex(builder, x + 0.0F, y + heightSouthWest, z + 1.0F, topColor, u01, v01, topLightCoords);
            this.vertex(builder, x + 1.0F, y + heightSouthEast, z + 1.0F, topColor, u10, v10, topLightCoords);
            this.vertex(builder, x + 1.0F, y + heightNorthEast, z + 0.0F, topColor, u11, v11, topLightCoords);
            if (fluidState.shouldRenderBackwardUpFace(level, pos.above())) {
               this.vertex(builder, x + 0.0F, y + heightNorthWest, z + 0.0F, topColor, u00, v00, topLightCoords);
               this.vertex(builder, x + 1.0F, y + heightNorthEast, z + 0.0F, topColor, u11, v11, topLightCoords);
               this.vertex(builder, x + 1.0F, y + heightSouthEast, z + 1.0F, topColor, u10, v10, topLightCoords);
               this.vertex(builder, x + 0.0F, y + heightSouthWest, z + 1.0F, topColor, u01, v01, topLightCoords);
            }
         }

         if (renderDown) {
            float u0 = stillSprite.getU0();
            float u1 = stillSprite.getU1();
            float v0 = stillSprite.getV0();
            float v1 = stillSprite.getV1();
            int belowLightCoords = this.getLightCoords(level, pos.below());
            int belowColor = ARGB.scaleRGB(color, shadeDown);
            this.vertex(builder, x, y + bottomOffs, z + 1.0F, belowColor, u0, v1, belowLightCoords);
            this.vertex(builder, x, y + bottomOffs, z, belowColor, u0, v0, belowLightCoords);
            this.vertex(builder, x + 1.0F, y + bottomOffs, z, belowColor, u1, v0, belowLightCoords);
            this.vertex(builder, x + 1.0F, y + bottomOffs, z + 1.0F, belowColor, u1, v1, belowLightCoords);
         }

         int sideLightCoords = this.getLightCoords(level, pos);

         for(Direction faceDir : Direction.Plane.HORIZONTAL) {
            float hh0;
            float hh1;
            float x0;
            float z0;
            float x1;
            float z1;
            boolean renderCondition;
            switch (faceDir) {
               case NORTH:
                  hh0 = heightNorthWest;
                  hh1 = heightNorthEast;
                  x0 = x;
                  x1 = x + 1.0F;
                  z0 = z + 0.001F;
                  z1 = z + 0.001F;
                  renderCondition = renderNorth;
                  break;
               case SOUTH:
                  hh0 = heightSouthEast;
                  hh1 = heightSouthWest;
                  x0 = x + 1.0F;
                  x1 = x;
                  z0 = z + 1.0F - 0.001F;
                  z1 = z + 1.0F - 0.001F;
                  renderCondition = renderSouth;
                  break;
               case WEST:
                  hh0 = heightSouthWest;
                  hh1 = heightNorthWest;
                  x0 = x + 0.001F;
                  x1 = x + 0.001F;
                  z0 = z + 1.0F;
                  z1 = z;
                  renderCondition = renderWest;
                  break;
               default:
                  hh0 = heightNorthEast;
                  hh1 = heightSouthEast;
                  x0 = x + 1.0F - 0.001F;
                  x1 = x + 1.0F - 0.001F;
                  z0 = z;
                  z1 = z + 1.0F;
                  renderCondition = renderEast;
            }

            if (renderCondition && !isFaceOccludedByNeighbor(faceDir, Math.max(hh0, hh1), level.getBlockState(pos.relative(faceDir)))) {
               BlockPos tPos = pos.relative(faceDir);
               TextureAtlasSprite sprite = flowingSprite;
               if (!isLava) {
                  Block relativeBlock = level.getBlockState(tPos).getBlock();
                  if (relativeBlock instanceof HalfTransparentBlock || relativeBlock instanceof LeavesBlock) {
                     sprite = this.waterOverlay;
                  }
               }

               float u0 = sprite.getU(0.0F);
               float u1 = sprite.getU(0.5F);
               float v01 = sprite.getV((1.0F - hh0) * 0.5F);
               float v02 = sprite.getV((1.0F - hh1) * 0.5F);
               float v1 = sprite.getV(0.5F);
               float shadeSide = faceDir.getAxis() == Direction.Axis.Z ? shadeNorth : shadeWest;
               int faceColor = ARGB.scaleRGB(color, shadeUp * shadeSide);
               this.vertex(builder, x0, y + hh0, z0, faceColor, u0, v01, sideLightCoords);
               this.vertex(builder, x1, y + hh1, z1, faceColor, u1, v02, sideLightCoords);
               this.vertex(builder, x1, y + bottomOffs, z1, faceColor, u1, v1, sideLightCoords);
               this.vertex(builder, x0, y + bottomOffs, z0, faceColor, u0, v1, sideLightCoords);
               if (sprite != this.waterOverlay) {
                  this.vertex(builder, x0, y + bottomOffs, z0, faceColor, u0, v1, sideLightCoords);
                  this.vertex(builder, x1, y + bottomOffs, z1, faceColor, u1, v1, sideLightCoords);
                  this.vertex(builder, x1, y + hh1, z1, faceColor, u1, v02, sideLightCoords);
                  this.vertex(builder, x0, y + hh0, z0, faceColor, u0, v01, sideLightCoords);
               }
            }
         }

      }
   }

   private float calculateAverageHeight(final BlockAndTintGetter level, final Fluid type, final float heightSelf, final float height2, final float height1, final BlockPos cornerPos) {
      if (!(height1 >= 1.0F) && !(height2 >= 1.0F)) {
         float[] weightedHeight = new float[2];
         if (height1 > 0.0F || height2 > 0.0F) {
            float heightCorner = this.getHeight(level, type, cornerPos);
            if (heightCorner >= 1.0F) {
               return 1.0F;
            }

            this.addWeightedHeight(weightedHeight, heightCorner);
         }

         this.addWeightedHeight(weightedHeight, heightSelf);
         this.addWeightedHeight(weightedHeight, height1);
         this.addWeightedHeight(weightedHeight, height2);
         return weightedHeight[0] / weightedHeight[1];
      } else {
         return 1.0F;
      }
   }

   private void addWeightedHeight(final float[] weightedHeight, final float height) {
      if (height >= 0.8F) {
         weightedHeight[0] += height * 10.0F;
         weightedHeight[1] += 10.0F;
      } else if (height >= 0.0F) {
         weightedHeight[0] += height;
         int var10002 = weightedHeight[1]++;
      }

   }

   private float getHeight(final BlockAndTintGetter level, final Fluid fluidType, final BlockPos pos) {
      BlockState state = level.getBlockState(pos);
      return this.getHeight(level, fluidType, pos, state, state.getFluidState());
   }

   private float getHeight(final BlockAndTintGetter level, final Fluid fluidType, final BlockPos pos, final BlockState state, final FluidState fluidState) {
      if (fluidType.isSame(fluidState.getType())) {
         BlockState aboveState = level.getBlockState(pos.above());
         return fluidType.isSame(aboveState.getFluidState().getType()) ? 1.0F : fluidState.getOwnHeight();
      } else {
         return !state.isSolid() ? 0.0F : -1.0F;
      }
   }

   private void vertex(final VertexConsumer builder, final float x, final float y, final float z, final int color, final float u, final float v, final int lightCoords) {
      builder.addVertex(x, y, z, color, u, v, OverlayTexture.NO_OVERLAY, lightCoords, 0.0F, 1.0F, 0.0F);
   }

   private int getLightCoords(final BlockAndTintGetter level, final BlockPos pos) {
      return LightCoordsUtil.max(LevelRenderer.getLightCoords(level, pos), LevelRenderer.getLightCoords(level, pos.above()));
   }
}
