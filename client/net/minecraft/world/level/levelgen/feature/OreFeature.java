package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.MapCodec;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.BulkSectionAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;

public class OreFeature extends AbstractOreFeature {
   public static final MapCodec<OreFeature> CODEC = makeCodec(OreFeature::new);
   private static final int STRIDE = 4;
   private static final int X_INDEX = 0;
   private static final int Y_INDEX = 1;
   private static final int Z_INDEX = 2;
   private static final int RADIUS_INDEX = 3;

   public OreFeature(final List<BlockReplacement> targetStates, final int size, final float discardChanceOnAirExposure) {
      super(targetStates, size, discardChanceOnAirExposure);
   }

   public OreFeature(final List<BlockReplacement> targetStates, final int size) {
      this(targetStates, size, 0.0F);
   }

   public OreFeature(final RuleTest target, final BlockState state, final int size) {
      this(List.of(new BlockReplacement(target, state)), size, 0.0F);
   }

   public MapCodec<OreFeature> codec() {
      return CODEC;
   }

   public boolean place(final WorldGenLevel level, final ChunkGenerator chunkGenerator, final RandomSource random, final BlockPos origin) {
      float dir = random.nextFloat() * 3.1415927F;
      float spreadXY = (float)this.size / 8.0F;
      int maxRadius = Mth.ceil(((float)this.size / 16.0F * 2.0F + 1.0F) / 2.0F);
      double x0 = (double)origin.getX() + Math.sin((double)dir) * (double)spreadXY;
      double x1 = (double)origin.getX() - Math.sin((double)dir) * (double)spreadXY;
      double z0 = (double)origin.getZ() + Math.cos((double)dir) * (double)spreadXY;
      double z1 = (double)origin.getZ() - Math.cos((double)dir) * (double)spreadXY;
      int spreadY = 2;
      double y0 = (double)(origin.getY() + random.nextInt(3) - 2);
      double y1 = (double)(origin.getY() + random.nextInt(3) - 2);
      int xStart = origin.getX() - Mth.ceil(spreadXY) - maxRadius;
      int yStart = origin.getY() - 2 - maxRadius;
      int zStart = origin.getZ() - Mth.ceil(spreadXY) - maxRadius;
      int sizeXZ = 2 * (Mth.ceil(spreadXY) + maxRadius);
      return level.anyHeightMatches(Heightmap.Types.OCEAN_FLOOR_WG, xStart, zStart, xStart + sizeXZ - 1, zStart + sizeXZ - 1, yStart, 2147483647) ? this.doPlace(level, random, x0, x1, z0, z1, y0, y1, xStart, zStart, sizeXZ) : false;
   }

   protected boolean doPlace(final WorldGenLevel level, final RandomSource random, final double x0, final double x1, final double z0, final double z1, final double y0, final double y1, final int xStart, final int zStart, final int sizeXZ) {
      int placed = 0;
      BlockPos.MutableBlockPos orePos = new BlockPos.MutableBlockPos();
      double[] data = new double[this.size * 4];
      double maxRadius = 0.0;

      for(int i = 0; i < this.size; ++i) {
         float step = (float)i / (float)this.size;
         double xx = Mth.lerp((double)step, x0, x1);
         double yy = Mth.lerp((double)step, y0, y1);
         double zz = Mth.lerp((double)step, z0, z1);
         double ss = random.nextDouble() * (double)this.size / 16.0;
         double r = ((double)(Mth.sin((double)(3.1415927F * step)) + 1.0F) * ss + 1.0) / 2.0;
         data[i * 4 + 0] = xx;
         data[i * 4 + 1] = yy;
         data[i * 4 + 2] = zz;
         data[i * 4 + 3] = r;
         maxRadius = Math.max(maxRadius, r);
      }

      double stepDist = Mth.length(x1 - x0, y1 - y0, z1 - z0) / (double)this.size;

      for(int i = 0; i < this.size - 1; ++i) {
         if (!(data[i * 4 + 3] <= 0.0)) {
            double radius = data[i * 4 + 3];

            for(int j = i + 1; j < this.size; ++j) {
               double otherRadius = data[j * 4 + 3];
               if (!(otherRadius <= 0.0)) {
                  double dist = (double)(j - i) * stepDist;
                  double dr = radius - otherRadius;
                  if (dr * dr > dist * dist) {
                     if (!(dr > 0.0)) {
                        data[i * 4 + 3] = -1.0;
                        break;
                     }

                     data[j * 4 + 3] = -1.0;
                  }

                  if (dist > maxRadius) {
                     break;
                  }
               }
            }
         }
      }

      int lowestAllowedY = level.getMinY();
      int highestAllowedY = level.getMaxY();
      int gridMaxX = xStart + sizeXZ - 1;
      int gridMaxZ = zStart + sizeXZ - 1;
      int[] columnMinY = new int[sizeXZ * sizeXZ];
      int[] columnMaxY = new int[sizeXZ * sizeXZ];
      Arrays.fill(columnMinY, 2147483647);
      Arrays.fill(columnMaxY, -2147483648);
      int touchedMinX = sizeXZ;
      int touchedMaxX = -1;
      int touchedMinZ = sizeXZ;
      int touchedMaxZ = -1;

      for(int i = 0; i < this.size; ++i) {
         double r = data[i * 4 + 3];
         if (!(r < 0.0)) {
            double rSq = Mth.square(r);
            double xx = data[i * 4 + 0];
            double yy = data[i * 4 + 1];
            double zz = data[i * 4 + 2];
            int xMin = Math.max(Mth.floor(xx - r), xStart);
            int zMin = Math.max(Mth.floor(zz - r), zStart);
            int xMax = Mth.clamp(Mth.floor(xx + r), xMin, gridMaxX);
            int zMax = Mth.clamp(Mth.floor(zz + r), zMin, gridMaxZ);
            touchedMinX = Math.min(touchedMinX, xMin - xStart);
            touchedMaxX = Math.max(touchedMaxX, xMax - xStart);
            touchedMinZ = Math.min(touchedMinZ, zMin - zStart);
            touchedMaxZ = Math.max(touchedMaxZ, zMax - zStart);

            for(int x = xMin; x <= xMax; ++x) {
               double dx = (double)x + 0.5 - xx;
               double remainingAfterXSqr = rSq - dx * dx;
               if (!(remainingAfterXSqr <= 0.0)) {
                  int rowOffset = (x - xStart) * sizeXZ - zStart;

                  for(int z = zMin; z <= zMax; ++z) {
                     double dz = (double)z + 0.5 - zz;
                     double remainingSqr = remainingAfterXSqr - dz * dz;
                     if (!(remainingSqr <= 0.0)) {
                        double halfSpan = Math.sqrt(remainingSqr);
                        int yLow = Math.max(Mth.floor(yy - 0.5 - halfSpan) + 1, lowestAllowedY);
                        int yHigh = Math.min(Mth.ceil(yy - 0.5 + halfSpan) - 1, highestAllowedY);
                        if (yLow <= yHigh) {
                           int index = rowOffset + z;
                           columnMinY[index] = Math.min(columnMinY[index], yLow);
                           columnMaxY[index] = Math.max(columnMaxY[index], yHigh);
                        }
                     }
                  }
               }
            }
         }
      }

      try (BulkSectionAccess sectionGetter = new BulkSectionAccess(level)) {
         Objects.requireNonNull(sectionGetter);
         Function<BlockPos, BlockState> getBlockState = sectionGetter::getBlockState;
         SectionPos activeSection = null;
         LevelChunkSection section = null;

         for(int gridX = touchedMinX; gridX <= touchedMaxX; ++gridX) {
            int x = xStart + gridX;
            int sectionRelativeX = SectionPos.sectionRelative(x);
            int sectionX = SectionPos.blockToSectionCoord(x);
            int rowOffset = gridX * sizeXZ;

            for(int gridZ = touchedMinZ; gridZ <= touchedMaxZ; ++gridZ) {
               int index = rowOffset + gridZ;
               int yMin = columnMinY[index];
               if (yMin != 2147483647) {
                  int yMax = columnMaxY[index];
                  int z = zStart + gridZ;
                  int sectionRelativeZ = SectionPos.sectionRelative(z);
                  int sectionZ = SectionPos.blockToSectionCoord(z);
                  boolean canWriteWholeColumn = level.ensureCanWrite(orePos.set(x, yMin, z)) && (yMax == yMin || level.ensureCanWrite(orePos.set(x, yMax, z)));

                  for(int y = yMin; y <= yMax; ++y) {
                     orePos.setY(y);
                     if (canWriteWholeColumn || level.ensureCanWrite(orePos)) {
                        int sectionY = SectionPos.blockToSectionCoord(y);
                        if (activeSection == null || activeSection.getX() != sectionX || activeSection.getY() != sectionY || activeSection.getZ() != sectionZ) {
                           section = sectionGetter.getSection(orePos);
                           activeSection = SectionPos.of(sectionX, sectionY, sectionZ);
                        }

                        if (section != null) {
                           int sectionRelativeY = SectionPos.sectionRelative(y);
                           BlockState blockState = section.getBlockState(sectionRelativeX, sectionRelativeY, sectionRelativeZ);

                           for(BlockReplacement targetState : this.targetStates) {
                              if (this.canPlaceOre(blockState, getBlockState, random, targetState, orePos)) {
                                 section.setBlockState(sectionRelativeX, sectionRelativeY, sectionRelativeZ, targetState.state(), false);
                                 ++placed;
                                 break;
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      return placed > 0;
   }
}
