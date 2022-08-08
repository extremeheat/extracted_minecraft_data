package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
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
   private final Predicate<Holder<PoiType>> poiType;
   private final MemoryModuleType<GlobalPos> memoryToAcquire;
   private final boolean onlyIfAdult;
   private final Optional<Byte> onPoiAcquisitionEvent;
   private long nextScheduledStart;
   private final Long2ObjectMap<JitteredLinearRetry> batchCache;

   public AcquirePoi(Predicate<Holder<PoiType>> var1, MemoryModuleType<GlobalPos> var2, MemoryModuleType<GlobalPos> var3, boolean var4, Optional<Byte> var5) {
      super(constructEntryConditionMap(var2, var3));
      this.batchCache = new Long2ObjectOpenHashMap();
      this.poiType = var1;
      this.memoryToAcquire = var3;
      this.onlyIfAdult = var4;
      this.onPoiAcquisitionEvent = var5;
   }

   public AcquirePoi(Predicate<Holder<PoiType>> var1, MemoryModuleType<GlobalPos> var2, boolean var3, Optional<Byte> var4) {
      this(var1, var2, var2, var3, var4);
   }

   private static ImmutableMap<MemoryModuleType<?>, MemoryStatus> constructEntryConditionMap(MemoryModuleType<GlobalPos> var0, MemoryModuleType<GlobalPos> var1) {
      ImmutableMap.Builder var2 = ImmutableMap.builder();
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
         return !((JitteredLinearRetry)var2x.getValue()).isStillValid(var3);
      });
      Predicate var6 = (var3x) -> {
         JitteredLinearRetry var4 = (JitteredLinearRetry)this.batchCache.get(var3x.asLong());
         if (var4 == null) {
            return true;
         } else if (!var4.shouldRetry(var3)) {
            return false;
         } else {
            var4.markAttempt(var3);
            return true;
         }
      };
      Set var7 = (Set)var5.findAllClosestFirstWithType(this.poiType, var6, var2.blockPosition(), 48, PoiManager.Occupancy.HAS_SPACE).limit(5L).collect(Collectors.toSet());
      Path var8 = findPathToPois(var2, var7);
      if (var8 != null && var8.canReach()) {
         BlockPos var11 = var8.getTarget();
         var5.getType(var11).ifPresent((var5x) -> {
            var5.take(this.poiType, (var1x, var2x) -> {
               return var2x.equals(var11);
            }, var11, 1);
            var2.getBrain().setMemory(this.memoryToAcquire, (Object)GlobalPos.of(var1.dimension(), var11));
            this.onPoiAcquisitionEvent.ifPresent((var2x) -> {
               var1.broadcastEntityEvent(var2, var2x);
            });
            this.batchCache.clear();
            DebugPackets.sendPoiTicketCountPacket(var1, var11);
         });
      } else {
         Iterator var9 = var7.iterator();

         while(var9.hasNext()) {
            Pair var10 = (Pair)var9.next();
            this.batchCache.computeIfAbsent(((BlockPos)var10.getSecond()).asLong(), (var3x) -> {
               return new JitteredLinearRetry(var2.level.random, var3);
            });
         }
      }

   }

   @Nullable
   public static Path findPathToPois(Mob var0, Set<Pair<Holder<PoiType>, BlockPos>> var1) {
      if (var1.isEmpty()) {
         return null;
      } else {
         HashSet var2 = new HashSet();
         int var3 = 1;
         Iterator var4 = var1.iterator();

         while(var4.hasNext()) {
            Pair var5 = (Pair)var4.next();
            var3 = Math.max(var3, ((PoiType)((Holder)var5.getFirst()).value()).validRange());
            var2.add((BlockPos)var5.getSecond());
         }

         return var0.getNavigation().createPath((Set)var2, var3);
      }
   }

   static class JitteredLinearRetry {
      private static final int MIN_INTERVAL_INCREASE = 40;
      private static final int MAX_INTERVAL_INCREASE = 80;
      private static final int MAX_RETRY_PATHFINDING_INTERVAL = 400;
      private final RandomSource random;
      private long previousAttemptTimestamp;
      private long nextScheduledAttemptTimestamp;
      private int currentDelay;

      JitteredLinearRetry(RandomSource var1, long var2) {
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
