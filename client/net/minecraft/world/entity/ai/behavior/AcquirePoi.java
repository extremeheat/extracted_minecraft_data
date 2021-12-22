package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Iterator;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.pathfinder.Path;

public class AcquirePoi extends Behavior<PathfinderMob> {
   private static final int BATCH_SIZE = 5;
   private static final int RATE = 20;
   public static final int SCAN_RANGE = 48;
   private final PoiType poiType;
   private final MemoryModuleType<GlobalPos> memoryToAcquire;
   private final boolean onlyIfAdult;
   private final Optional<Byte> onPoiAcquisitionEvent;
   private long nextScheduledStart;
   private final Long2ObjectMap<AcquirePoi.JitteredLinearRetry> batchCache;

   public AcquirePoi(PoiType var1, MemoryModuleType<GlobalPos> var2, MemoryModuleType<GlobalPos> var3, boolean var4, Optional<Byte> var5) {
      super(constructEntryConditionMap(var2, var3));
      this.batchCache = new Long2ObjectOpenHashMap();
      this.poiType = var1;
      this.memoryToAcquire = var3;
      this.onlyIfAdult = var4;
      this.onPoiAcquisitionEvent = var5;
   }

   public AcquirePoi(PoiType var1, MemoryModuleType<GlobalPos> var2, boolean var3, Optional<Byte> var4) {
      this(var1, var2, var2, var3, var4);
   }

   private static ImmutableMap<MemoryModuleType<?>, MemoryStatus> constructEntryConditionMap(MemoryModuleType<GlobalPos> var0, MemoryModuleType<GlobalPos> var1) {
      Builder var2 = ImmutableMap.builder();
      var2.put(var0, MemoryStatus.VALUE_ABSENT);
      if (var1 != var0) {
         var2.put(var1, MemoryStatus.VALUE_ABSENT);
      }

      return var2.build();
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, PathfinderMob var2) {
      if (this.onlyIfAdult && var2.isBaby()) {
         return false;
      } else if (this.nextScheduledStart == 0L) {
         this.nextScheduledStart = var2.level.getGameTime() + (long)var1.random.nextInt(20);
         return false;
      } else {
         return var1.getGameTime() >= this.nextScheduledStart;
      }
   }

   protected void start(ServerLevel var1, PathfinderMob var2, long var3) {
      this.nextScheduledStart = var3 + 20L + (long)var1.getRandom().nextInt(20);
      PoiManager var5 = var1.getPoiManager();
      this.batchCache.long2ObjectEntrySet().removeIf((var2x) -> {
         return !((AcquirePoi.JitteredLinearRetry)var2x.getValue()).isStillValid(var3);
      });
      Predicate var6 = (var3x) -> {
         AcquirePoi.JitteredLinearRetry var4 = (AcquirePoi.JitteredLinearRetry)this.batchCache.get(var3x.asLong());
         if (var4 == null) {
            return true;
         } else if (!var4.shouldRetry(var3)) {
            return false;
         } else {
            var4.markAttempt(var3);
            return true;
         }
      };
      Set var7 = (Set)var5.findAllClosestFirst(this.poiType.getPredicate(), var6, var2.blockPosition(), 48, PoiManager.Occupancy.HAS_SPACE).limit(5L).collect(Collectors.toSet());
      Path var8 = var2.getNavigation().createPath(var7, this.poiType.getValidRange());
      if (var8 != null && var8.canReach()) {
         BlockPos var11 = var8.getTarget();
         var5.getType(var11).ifPresent((var5x) -> {
            var5.take(this.poiType.getPredicate(), (var1x) -> {
               return var1x.equals(var11);
            }, var11, 1);
            var2.getBrain().setMemory(this.memoryToAcquire, (Object)GlobalPos.method_122(var1.dimension(), var11));
            this.onPoiAcquisitionEvent.ifPresent((var2x) -> {
               var1.broadcastEntityEvent(var2, var2x);
            });
            this.batchCache.clear();
            DebugPackets.sendPoiTicketCountPacket(var1, var11);
         });
      } else {
         Iterator var9 = var7.iterator();

         while(var9.hasNext()) {
            BlockPos var10 = (BlockPos)var9.next();
            this.batchCache.computeIfAbsent(var10.asLong(), (var3x) -> {
               return new AcquirePoi.JitteredLinearRetry(var2.level.random, var3);
            });
         }
      }

   }

   static class JitteredLinearRetry {
      private static final int MIN_INTERVAL_INCREASE = 40;
      private static final int MAX_INTERVAL_INCREASE = 80;
      private static final int MAX_RETRY_PATHFINDING_INTERVAL = 400;
      private final Random random;
      private long previousAttemptTimestamp;
      private long nextScheduledAttemptTimestamp;
      private int currentDelay;

      JitteredLinearRetry(Random var1, long var2) {
         super();
         this.random = var1;
         this.markAttempt(var2);
      }

      public void markAttempt(long var1) {
         this.previousAttemptTimestamp = var1;
         int var3 = this.currentDelay + this.random.nextInt(40) + 40;
         this.currentDelay = Math.min(var3, 400);
         this.nextScheduledAttemptTimestamp = var1 + (long)this.currentDelay;
      }

      public boolean isStillValid(long var1) {
         return var1 - this.previousAttemptTimestamp < 400L;
      }

      public boolean shouldRetry(long var1) {
         return var1 >= this.nextScheduledAttemptTimestamp;
      }

      public String toString() {
         return "RetryMarker{, previousAttemptAt=" + this.previousAttemptTimestamp + ", nextScheduledAttemptAt=" + this.nextScheduledAttemptTimestamp + ", currentDelay=" + this.currentDelay + "}";
      }
   }
}
