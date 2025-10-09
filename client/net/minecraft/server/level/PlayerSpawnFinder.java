package net.minecraft.server.level;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import javax.annotation.Nullable;
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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

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

   private PlayerSpawnFinder(ServerLevel var1, BlockPos var2, int var3) {
      super();
      this.level = var1;
      this.spawnSuggestion = var2;
      this.radius = var3;
      long var4 = (long)var3 * 2L + 1L;
      this.candidateCount = (int)Math.min(1024L, var4 * var4);
      this.coprime = getCoprime(this.candidateCount);
      this.offset = RandomSource.create().nextInt(this.candidateCount);
   }

   public static CompletableFuture<Vec3> findSpawn(ServerLevel var0, BlockPos var1) {
      if (var0.dimensionType().hasSkyLight() && var0.getServer().getWorldData().getGameType() != GameType.ADVENTURE) {
         int var2 = Math.max(0, var0.getGameRules().getInt(GameRules.RULE_SPAWN_RADIUS));
         int var3 = Mth.floor(var0.getWorldBorder().getDistanceToBorder((double)var1.getX(), (double)var1.getZ()));
         if (var3 < var2) {
            var2 = var3;
         }

         if (var3 <= 1) {
            var2 = 1;
         }

         PlayerSpawnFinder var4 = new PlayerSpawnFinder(var0, var1, var2);
         var4.scheduleNext();
         return var4.finishedFuture;
      } else {
         return CompletableFuture.completedFuture(fixupSpawnHeight(var0, var1));
      }
   }

   private void scheduleNext() {
      int var1 = this.nextCandidateIndex++;
      if (var1 < this.candidateCount) {
         int var2 = (this.offset + this.coprime * var1) % this.candidateCount;
         int var3 = var2 % (this.radius * 2 + 1);
         int var4 = var2 / (this.radius * 2 + 1);
         int var5 = this.spawnSuggestion.getX() + var3 - this.radius;
         int var6 = this.spawnSuggestion.getZ() + var4 - this.radius;
         this.scheduleCandidate(var5, var6, var1, () -> {
            BlockPos var3 = getOverworldRespawnPos(this.level, var5, var6);
            return var3 != null && noCollisionNoLiquid(this.level, var3) ? Optional.of(Vec3.atBottomCenterOf(var3)) : Optional.empty();
         });
      } else {
         this.scheduleCandidate(this.spawnSuggestion.getX(), this.spawnSuggestion.getZ(), var1, () -> Optional.of(fixupSpawnHeight(this.level, this.spawnSuggestion)));
      }

   }

   private static Vec3 fixupSpawnHeight(CollisionGetter var0, BlockPos var1) {
      BlockPos.MutableBlockPos var2 = var1.mutable();

      while(!noCollisionNoLiquid(var0, var2) && var2.getY() < var0.getMaxY()) {
         var2.move(Direction.UP);
      }

      var2.move(Direction.DOWN);

      while(noCollisionNoLiquid(var0, var2) && var2.getY() > var0.getMinY()) {
         var2.move(Direction.DOWN);
      }

      var2.move(Direction.UP);
      return Vec3.atBottomCenterOf(var2);
   }

   private static boolean noCollisionNoLiquid(CollisionGetter var0, BlockPos var1) {
      return var0.noCollision((Entity)null, PLAYER_DIMENSIONS.makeBoundingBox(var1.getBottomCenter()), true);
   }

   private static int getCoprime(int var0) {
      return var0 <= 16 ? var0 - 1 : 17;
   }

   private void scheduleCandidate(int var1, int var2, int var3, Supplier<Optional<Vec3>> var4) {
      if (!this.finishedFuture.isDone()) {
         int var5 = SectionPos.blockToSectionCoord(var1);
         int var6 = SectionPos.blockToSectionCoord(var2);
         this.level.getChunkSource().addTicketAndLoadWithRadius(TicketType.SPAWN_SEARCH, new ChunkPos(var5, var6), 0).whenCompleteAsync((var5x, var6x) -> {
            if (var6x == null) {
               try {
                  Optional var7 = (Optional)var4.get();
                  if (var7.isPresent()) {
                     this.finishedFuture.complete((Vec3)var7.get());
                  } else {
                     this.scheduleNext();
                  }
               } catch (Throwable var9) {
                  var6x = var9;
               }
            }

            if (var6x != null) {
               CrashReport var10 = CrashReport.forThrowable(var6x, "Searching for spawn");
               CrashReportCategory var8 = var10.addCategory("Spawn Lookup");
               BlockPos var10002 = this.spawnSuggestion;
               Objects.requireNonNull(var10002);
               var8.setDetail("Origin", var10002::toString);
               var8.setDetail("Radius", (CrashReportDetail)(() -> Integer.toString(this.radius)));
               var8.setDetail("Candidate", (CrashReportDetail)(() -> "[" + var1 + "," + var2 + "]"));
               var8.setDetail("Progress", (CrashReportDetail)(() -> var3 + " out of " + this.candidateCount));
               this.finishedFuture.completeExceptionally(new ReportedException(var10));
            }

         }, this.level.getServer());
      }
   }

   @Nullable
   protected static BlockPos getOverworldRespawnPos(ServerLevel var0, int var1, int var2) {
      boolean var3 = var0.dimensionType().hasCeiling();
      LevelChunk var4 = var0.getChunk(SectionPos.blockToSectionCoord(var1), SectionPos.blockToSectionCoord(var2));
      int var5 = var3 ? var0.getChunkSource().getGenerator().getSpawnHeight(var0) : var4.getHeight(Heightmap.Types.MOTION_BLOCKING, var1 & 15, var2 & 15);
      if (var5 < var0.getMinY()) {
         return null;
      } else {
         int var6 = var4.getHeight(Heightmap.Types.WORLD_SURFACE, var1 & 15, var2 & 15);
         if (var6 <= var5 && var6 > var4.getHeight(Heightmap.Types.OCEAN_FLOOR, var1 & 15, var2 & 15)) {
            return null;
         } else {
            BlockPos.MutableBlockPos var7 = new BlockPos.MutableBlockPos();

            for(int var8 = var5 + 1; var8 >= var0.getMinY(); --var8) {
               var7.set(var1, var8, var2);
               BlockState var9 = var0.getBlockState(var7);
               if (!var9.getFluidState().isEmpty()) {
                  break;
               }

               if (Block.isFaceFull(var9.getCollisionShape(var0, var7), Direction.UP)) {
                  return var7.above().immutable();
               }
            }

            return null;
         }
      }
   }

   @Nullable
   public static BlockPos getSpawnPosInChunk(ServerLevel var0, ChunkPos var1) {
      if (SharedConstants.debugVoidTerrain(var1)) {
         return null;
      } else {
         for(int var2 = var1.getMinBlockX(); var2 <= var1.getMaxBlockX(); ++var2) {
            for(int var3 = var1.getMinBlockZ(); var3 <= var1.getMaxBlockZ(); ++var3) {
               BlockPos var4 = getOverworldRespawnPos(var0, var2, var3);
               if (var4 != null) {
                  return var4;
               }
            }
         }

         return null;
      }
   }

   static {
      PLAYER_DIMENSIONS = EntityType.PLAYER.getDimensions();
   }
}
