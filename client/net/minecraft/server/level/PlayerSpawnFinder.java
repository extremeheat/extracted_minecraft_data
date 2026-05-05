package net.minecraft.server.level;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class PlayerSpawnFinder {
   private static final EntityDimensions PLAYER_DIMENSIONS;
   private static final int ABSOLUTE_MAX_ATTEMPTS = 1024;
   private final ServerLevel level;
   private final BlockPos spawnSuggestion;
   private final int radius;
   private final int candidateCount;
   private final int coprime;
   private final int offset;
   private int nextCandidateIndex;
   private final CompletableFuture<Vec3> finishedFuture = new CompletableFuture();

   private PlayerSpawnFinder(final ServerLevel level, final BlockPos spawnSuggestion, final int radius) {
      super();
      this.level = level;
      this.spawnSuggestion = spawnSuggestion;
      this.radius = radius;
      long squareSide = (long)radius * 2L + 1L;
      this.candidateCount = (int)Math.min(1024L, squareSide * squareSide);
      this.coprime = getCoprime(this.candidateCount);
      this.offset = RandomSource.createThreadLocalInstance().nextInt(this.candidateCount);
   }

   public static CompletableFuture<Vec3> findSpawn(final ServerLevel level, final BlockPos spawnSuggestion) {
      if (level.dimensionType().hasSkyLight() && level.getServer().getWorldData().getGameType() != GameType.ADVENTURE) {
         int radius = Math.max(0, (Integer)level.getGameRules().get(GameRules.RESPAWN_RADIUS));
         int distToBorder = Mth.floor(level.getWorldBorder().getDistanceToBorder((double)spawnSuggestion.getX(), (double)spawnSuggestion.getZ()));
         if (distToBorder < radius) {
            radius = distToBorder;
         }

         if (distToBorder <= 1) {
            radius = 1;
         }

         PlayerSpawnFinder finder = new PlayerSpawnFinder(level, spawnSuggestion, radius);
         finder.scheduleNext();
         return finder.finishedFuture;
      } else {
         return CompletableFuture.completedFuture(fixupSpawnHeight(level, spawnSuggestion));
      }
   }

   private void scheduleNext() {
      int candidateIndex = this.nextCandidateIndex++;
      if (candidateIndex < this.candidateCount) {
         int value = (this.offset + this.coprime * candidateIndex) % this.candidateCount;
         int deltaX = value % (this.radius * 2 + 1);
         int deltaZ = value / (this.radius * 2 + 1);
         int targetX = this.spawnSuggestion.getX() + deltaX - this.radius;
         int targetZ = this.spawnSuggestion.getZ() + deltaZ - this.radius;
         this.scheduleCandidate(targetX, targetZ, candidateIndex, () -> {
            BlockPos spawnPos = getOverworldRespawnPos(this.level, targetX, targetZ);
            return spawnPos != null && noCollisionNoLiquid(this.level, spawnPos) ? Optional.of(Vec3.atBottomCenterOf(spawnPos)) : Optional.empty();
         });
      } else {
         this.scheduleCandidate(this.spawnSuggestion.getX(), this.spawnSuggestion.getZ(), candidateIndex, () -> Optional.of(fixupSpawnHeight(this.level, this.spawnSuggestion)));
      }

   }

   private static Vec3 fixupSpawnHeight(final CollisionGetter level, final BlockPos spawnPos) {
      BlockPos.MutableBlockPos mutablePos = spawnPos.mutable();

      while(!noCollisionNoLiquid(level, mutablePos) && mutablePos.getY() < level.getMaxY()) {
         mutablePos.move(Direction.UP);
      }

      mutablePos.move(Direction.DOWN);

      while(noCollisionNoLiquid(level, mutablePos) && mutablePos.getY() > level.getMinY()) {
         mutablePos.move(Direction.DOWN);
      }

      mutablePos.move(Direction.UP);
      return Vec3.atBottomCenterOf(mutablePos);
   }

   private static boolean noCollisionNoLiquid(final CollisionGetter level, final BlockPos pos) {
      return level.noCollision((Entity)null, PLAYER_DIMENSIONS.makeBoundingBox(Vec3.atBottomCenterOf(pos)), true);
   }

   private static int getCoprime(final int possibleOrigins) {
      return possibleOrigins <= 16 ? possibleOrigins - 1 : 17;
   }

   private void scheduleCandidate(final int candidateX, final int candidateZ, final int candidateIndex, final Supplier<Optional<Vec3>> candidateChecker) {
      if (!this.finishedFuture.isDone()) {
         int chunkX = SectionPos.blockToSectionCoord(candidateX);
         int chunkZ = SectionPos.blockToSectionCoord(candidateZ);
         this.level.getChunkSource().addTicketAndLoadWithRadius(TicketType.SPAWN_SEARCH, new ChunkPos(chunkX, chunkZ), 0).whenCompleteAsync((ignored, throwable) -> {
            if (throwable == null) {
               try {
                  Optional<Vec3> spawnPos = (Optional)candidateChecker.get();
                  if (spawnPos.isPresent()) {
                     this.finishedFuture.complete((Vec3)spawnPos.get());
                  } else {
                     this.scheduleNext();
                  }
               } catch (Throwable t) {
                  throwable = t;
               }
            }

            if (throwable != null) {
               CrashReport report = CrashReport.forThrowable(throwable, "Searching for spawn");
               CrashReportCategory details = report.addCategory("Spawn Lookup");
               BlockPos var10002 = this.spawnSuggestion;
               Objects.requireNonNull(var10002);
               details.setDetail("Origin", var10002::toString);
               details.setDetail("Radius", (CrashReportDetail)(() -> Integer.toString(this.radius)));
               details.setDetail("Candidate", (CrashReportDetail)(() -> "[" + candidateX + "," + candidateZ + "]"));
               details.setDetail("Progress", (CrashReportDetail)(() -> candidateIndex + " out of " + this.candidateCount));
               this.finishedFuture.completeExceptionally(new ReportedException(report));
            }

         }, this.level.getServer());
      }
   }

   protected static @Nullable BlockPos getOverworldRespawnPos(final ServerLevel level, final int x, final int z) {
      boolean caveWorld = level.dimensionType().hasCeiling();
      LevelChunk chunk = level.getChunk(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z));
      int topY = caveWorld ? level.getChunkSource().getGenerator().getSpawnHeight(level) : chunk.getHeight(Heightmap.Types.MOTION_BLOCKING, x & 15, z & 15);
      if (topY < level.getMinY()) {
         return null;
      } else {
         int surface = chunk.getHeight(Heightmap.Types.WORLD_SURFACE, x & 15, z & 15);
         if (surface <= topY && surface > chunk.getHeight(Heightmap.Types.OCEAN_FLOOR, x & 15, z & 15)) {
            return null;
         } else {
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

            for(int y = topY + 1; y >= level.getMinY(); --y) {
               pos.set(x, y, z);
               BlockState blockState = level.getBlockState(pos);
               if (!blockState.getFluidState().isEmpty()) {
                  break;
               }

               if (Block.isFaceFull(blockState.getCollisionShape(level, pos), Direction.UP)) {
                  return pos.above().immutable();
               }
            }

            return null;
         }
      }
   }

   public static @Nullable BlockPos getSpawnPosInChunk(final ServerLevel level, final ChunkPos chunkPos) {
      if (SharedConstants.debugVoidTerrain(chunkPos)) {
         return null;
      } else {
         for(int x = chunkPos.getMinBlockX(); x <= chunkPos.getMaxBlockX(); ++x) {
            for(int z = chunkPos.getMinBlockZ(); z <= chunkPos.getMaxBlockZ(); ++z) {
               BlockPos validSpawnPosition = getOverworldRespawnPos(level, x, z);
               if (validSpawnPosition != null) {
                  return validSpawnPosition;
               }
            }
         }

         return null;
      }
   }

   static {
      PLAYER_DIMENSIONS = EntityTypes.PLAYER.getDimensions();
   }
}
