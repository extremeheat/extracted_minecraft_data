package net.minecraft.world.level.levelgen;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.MapCodec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Interval;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pools.JigsawJunction;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.jspecify.annotations.Nullable;

public class Beardifier implements DensityFunction {
   public static final Interval RANGE;
   public static final int BEARD_KERNEL_RADIUS = 12;
   private static final int BEARD_KERNEL_SIZE = 24;
   private static final float[] BEARD_KERNEL;
   private static final float MAX_BURY_DISTANCE = 6.0F;
   public static final Beardifier EMPTY;
   private final List<Rigid> pieces;
   private final List<JigsawJunction> junctions;
   private final @Nullable BoundingBox affectedBox;

   public static Beardifier forStructuresInChunk(final StructureManager structureManager, final ChunkPos chunkPos) {
      List<StructureStart> structureStarts = structureManager.startsForStructure((ChunkPos)chunkPos, (Predicate)((s) -> s.terrainAdaptation() != TerrainAdjustment.NONE));
      if (structureStarts.isEmpty()) {
         return EMPTY;
      } else {
         int chunkStartBlockX = chunkPos.getMinBlockX();
         int chunkStartBlockZ = chunkPos.getMinBlockZ();
         List<Rigid> rigids = new ArrayList();
         List<JigsawJunction> junctions = new ArrayList();
         BoundingBox anyPieceBoundingBox = null;

         for(StructureStart start : structureStarts) {
            TerrainAdjustment terrainAdjustment = start.getStructure().terrainAdaptation();

            for(StructurePiece piece : start.getPieces()) {
               if (piece.isCloseToChunk(chunkPos, 12)) {
                  if (piece instanceof PoolElementStructurePiece) {
                     PoolElementStructurePiece poolPiece = (PoolElementStructurePiece)piece;
                     StructureTemplatePool.Projection projection = poolPiece.getElement().getProjection();
                     if (projection == StructureTemplatePool.Projection.RIGID) {
                        rigids.add(new Rigid(poolPiece.getBoundingBox(), terrainAdjustment, poolPiece.getGroundLevelDelta()));
                        anyPieceBoundingBox = includeBoundingBox(anyPieceBoundingBox, piece.getBoundingBox());
                     }

                     for(JigsawJunction junction : poolPiece.getJunctions()) {
                        int junctionX = junction.getSourceX();
                        int junctionZ = junction.getSourceZ();
                        if (junctionX > chunkStartBlockX - 12 && junctionZ > chunkStartBlockZ - 12 && junctionX < chunkStartBlockX + 15 + 12 && junctionZ < chunkStartBlockZ + 15 + 12) {
                           junctions.add(junction);
                           BoundingBox junctionBox = new BoundingBox(new BlockPos(junctionX, junction.getSourceGroundY(), junctionZ));
                           anyPieceBoundingBox = includeBoundingBox(anyPieceBoundingBox, junctionBox);
                        }
                     }
                  } else {
                     rigids.add(new Rigid(piece.getBoundingBox(), terrainAdjustment, 0));
                     anyPieceBoundingBox = includeBoundingBox(anyPieceBoundingBox, piece.getBoundingBox());
                  }
               }
            }
         }

         if (anyPieceBoundingBox == null) {
            return EMPTY;
         } else {
            BoundingBox affectedBox = anyPieceBoundingBox.inflatedBy(24);
            return new Beardifier(List.copyOf(rigids), List.copyOf(junctions), affectedBox);
         }
      }
   }

   private static BoundingBox includeBoundingBox(final @Nullable BoundingBox encompassingBox, final BoundingBox newBox) {
      return encompassingBox == null ? newBox : BoundingBox.encapsulating(encompassingBox, newBox);
   }

   @VisibleForTesting
   public Beardifier(final List<Rigid> pieces, final List<JigsawJunction> junctions, final @Nullable BoundingBox affectedBox) {
      super();
      this.pieces = pieces;
      this.junctions = junctions;
      this.affectedBox = affectedBox;
   }

   public void fillArray(final float[] output, final DensityFunction.ContextProvider contextProvider) {
      if (this.affectedBox == null) {
         Arrays.fill(output, 0.0F);
      } else {
         contextProvider.fillAllDirectly(output, this);
      }

   }

   public float compute(final DensityFunction.FunctionContext context) {
      if (this.affectedBox == null) {
         return 0.0F;
      } else {
         int blockX = context.blockX();
         int blockY = context.blockY();
         int blockZ = context.blockZ();
         if (!this.affectedBox.isInside(blockX, blockY, blockZ)) {
            return 0.0F;
         } else {
            float noiseValue = 0.0F;

            for(Rigid rigid : this.pieces) {
               BoundingBox box = rigid.box();
               int groundLevelDelta = rigid.groundLevelDelta();
               int dx = Math.max(0, Math.max(box.minX() - blockX, blockX - box.maxX()));
               int dz = Math.max(0, Math.max(box.minZ() - blockZ, blockZ - box.maxZ()));
               int groundY = box.minY() + groundLevelDelta;
               int dyToGround = blockY - groundY;
               int var10000;
               switch (rigid.terrainAdjustment()) {
                  case NONE:
                     var10000 = 0;
                     break;
                  case BURY:
                  case BEARD_THIN:
                     var10000 = dyToGround;
                     break;
                  case BEARD_BOX:
                     var10000 = Math.max(0, Math.max(groundY - blockY, blockY - box.maxY()));
                     break;
                  case ENCAPSULATE:
                     var10000 = Math.max(0, Math.max(box.minY() - blockY, blockY - box.maxY()));
                     break;
                  default:
                     throw new MatchException((String)null, (Throwable)null);
               }

               int dy = var10000;
               float var10001;
               switch (rigid.terrainAdjustment()) {
                  case NONE:
                     var10001 = 0.0F;
                     break;
                  case BURY:
                     var10001 = getBuryContribution((float)dx, (float)dy / 2.0F, (float)dz);
                     break;
                  case BEARD_THIN:
                  case BEARD_BOX:
                     var10001 = getBeardContribution(dx, dy, dz, dyToGround) * 0.8F;
                     break;
                  case ENCAPSULATE:
                     var10001 = getBuryContribution((float)dx / 2.0F, (float)dy / 2.0F, (float)dz / 2.0F) * 0.8F;
                     break;
                  default:
                     throw new MatchException((String)null, (Throwable)null);
               }

               noiseValue += var10001;
            }

            for(JigsawJunction junction : this.junctions) {
               int dx = blockX - junction.getSourceX();
               int dy = blockY - junction.getSourceGroundY();
               int dz = blockZ - junction.getSourceZ();
               noiseValue += getBeardContribution(dx, dy, dz, dy) * 0.4F;
            }

            return noiseValue;
         }
      }
   }

   public DensityFunction mapChildren(final DensityFunction.Visitor visitor) {
      return this;
   }

   public Interval range() {
      return RANGE;
   }

   public @DensityFunction.Axes int domainAxes() {
      return 7;
   }

   public MapCodec<? extends DensityFunction> codec() {
      throw new UnsupportedOperationException();
   }

   private static float getBuryContribution(final float dx, final float dy, final float dz) {
      float distanceSq = Mth.lengthSquared(dx, dy, dz);
      return distanceSq >= 36.0F ? 0.0F : 1.0F - Mth.sqrt(distanceSq) / 6.0F;
   }

   private static float getBeardContribution(final int dx, final int dy, final int dz, final int yToGround) {
      int xi = dx + 12;
      int yi = dy + 12;
      int zi = dz + 12;
      if (isInKernelRange(xi) && isInKernelRange(yi) && isInKernelRange(zi)) {
         float dyWithOffset = (float)yToGround + 0.5F;
         float distanceSqr = Mth.lengthSquared((float)dx, dyWithOffset, (float)dz);
         float value = -dyWithOffset * (float)Mth.fastInvSqrt((double)(distanceSqr / 2.0F)) / 2.0F;
         return value * BEARD_KERNEL[zi * 24 * 24 + xi * 24 + yi];
      } else {
         return 0.0F;
      }
   }

   private static boolean isInKernelRange(final int xi) {
      return xi >= 0 && xi < 24;
   }

   private static double computeBeardContribution(final int dx, final int dy, final int dz) {
      return computeBeardContribution(dx, (double)dy + 0.5, dz);
   }

   private static double computeBeardContribution(final int dx, final double dy, final int dz) {
      double distanceSqr = Mth.lengthSquared((double)dx, dy, (double)dz);
      double pieceWeight = Math.pow(2.718281828459045, -distanceSqr / 16.0);
      return pieceWeight;
   }

   static {
      RANGE = Interval.INFINITE;
      BEARD_KERNEL = (float[])Util.make(new float[13824], (kernel) -> {
         for(int zi = 0; zi < 24; ++zi) {
            for(int xi = 0; xi < 24; ++xi) {
               for(int yi = 0; yi < 24; ++yi) {
                  kernel[zi * 24 * 24 + xi * 24 + yi] = (float)computeBeardContribution(xi - 12, yi - 12, zi - 12);
               }
            }
         }

      });
      EMPTY = new Beardifier(List.of(), List.of(), (BoundingBox)null);
   }

   @VisibleForTesting
   public static record Rigid(BoundingBox box, TerrainAdjustment terrainAdjustment, int groundLevelDelta) {
      public Rigid {
         super();
      }
   }
}
