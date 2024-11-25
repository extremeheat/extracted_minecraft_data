package net.minecraft.world.entity.ai.behavior;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;
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
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.pathfinder.Path;
import org.apache.commons.lang3.mutable.MutableLong;

public class AcquirePoi {
   public static final int SCAN_RANGE = 48;

   public AcquirePoi() {
      super();
   }

   public static BehaviorControl<PathfinderMob> create(Predicate<Holder<PoiType>> var0, MemoryModuleType<GlobalPos> var1, boolean var2, Optional<Byte> var3, BiPredicate<ServerLevel, BlockPos> var4) {
      return create(var0, var1, var1, var2, var3, var4);
   }

   public static BehaviorControl<PathfinderMob> create(Predicate<Holder<PoiType>> var0, MemoryModuleType<GlobalPos> var1, boolean var2, Optional<Byte> var3) {
      return create(var0, var1, var1, var2, var3, (var0x, var1x) -> true);
   }

   public static BehaviorControl<PathfinderMob> create(Predicate<Holder<PoiType>> var0, MemoryModuleType<GlobalPos> var1, MemoryModuleType<GlobalPos> var2, boolean var3, Optional<Byte> var4, BiPredicate<ServerLevel, BlockPos> var5) {
      boolean var6 = true;
      boolean var7 = true;
      MutableLong var8 = new MutableLong(0L);
      Long2ObjectOpenHashMap var9 = new Long2ObjectOpenHashMap();
      OneShot var10 = BehaviorBuilder.create((Function)((var7x) -> var7x.group(var7x.absent(var2)).apply(var7x, (var6) -> (var7, var8x, var9x) -> {
               if (var3 && var8x.isBaby()) {
                  return false;
               } else if (var8.getValue() == 0L) {
                  var8.setValue(var7.getGameTime() + (long)var7.random.nextInt(20));
                  return false;
               } else if (var7.getGameTime() < var8.getValue()) {
                  return false;
               } else {
                  var8.setValue(var9x + 20L + (long)var7.getRandom().nextInt(20));
                  PoiManager var11 = var7.getPoiManager();
                  var9.long2ObjectEntrySet().removeIf((var2) -> !((JitteredLinearRetry)var2.getValue()).isStillValid(var9x));
                  Predicate var12 = (var3x) -> {
                     JitteredLinearRetry var4 = (JitteredLinearRetry)var9.get(var3x.asLong());
                     if (var4 == null) {
                        return true;
                     } else if (!var4.shouldRetry(var9x)) {
                        return false;
                     } else {
                        var4.markAttempt(var9x);
                        return true;
                     }
                  };
                  Set var13 = (Set)var11.findAllClosestFirstWithType(var0, var12, var8x.blockPosition(), 48, PoiManager.Occupancy.HAS_SPACE).limit(5L).filter((var2) -> var5.test(var7, (BlockPos)var2.getSecond())).collect(Collectors.toSet());
                  Path var14 = findPathToPois(var8x, var13);
                  if (var14 != null && var14.canReach()) {
                     BlockPos var17 = var14.getTarget();
                     var11.getType(var17).ifPresent((var8xx) -> {
                        var11.take(var0, (var1, var2) -> var2.equals(var17), var17, 1);
                        var6.set(GlobalPos.of(var7.dimension(), var17));
                        var4.ifPresent((var2) -> var7.broadcastEntityEvent(var8x, var2));
                        var9.clear();
                        DebugPackets.sendPoiTicketCountPacket(var7, var17);
                     });
                  } else {
                     for(Pair var16 : var13) {
                        var9.computeIfAbsent(((BlockPos)var16.getSecond()).asLong(), (var3x) -> new JitteredLinearRetry(var7.random, var9x));
                     }
                  }

                  return true;
               }
            })));
      return var2 == var1 ? var10 : BehaviorBuilder.create((Function)((var2x) -> var2x.group(var2x.absent(var1)).apply(var2x, (var1x) -> var10)));
   }

   @Nullable
   public static Path findPathToPois(Mob var0, Set<Pair<Holder<PoiType>, BlockPos>> var1) {
      if (var1.isEmpty()) {
         return null;
      } else {
         HashSet var2 = new HashSet();
         int var3 = 1;

         for(Pair var5 : var1) {
            var3 = Math.max(var3, ((PoiType)((Holder)var5.getFirst()).value()).validRange());
            var2.add((BlockPos)var5.getSecond());
         }

         return var0.getNavigation().createPath(var2, var3);
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
