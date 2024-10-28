package net.minecraft.world.entity.ai.behavior;

import com.mojang.datafixers.util.Pair;
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

   public static BehaviorControl<PathfinderMob> create(Predicate<Holder<PoiType>> var0, MemoryModuleType<GlobalPos> var1, boolean var2, Optional<Byte> var3) {
      return create(var0, var1, var1, var2, var3);
   }

   public static BehaviorControl<PathfinderMob> create(Predicate<Holder<PoiType>> var0, MemoryModuleType<GlobalPos> var1, MemoryModuleType<GlobalPos> var2, boolean var3, Optional<Byte> var4) {
      boolean var5 = true;
      boolean var6 = true;
      MutableLong var7 = new MutableLong(0L);
      Long2ObjectOpenHashMap var8 = new Long2ObjectOpenHashMap();
      OneShot var9 = BehaviorBuilder.create((var6x) -> {
         return var6x.group(var6x.absent(var2)).apply(var6x, (var5) -> {
            return (var6, var7x, var8x) -> {
               if (var3 && var7x.isBaby()) {
                  return false;
               } else if (var7.getValue() == 0L) {
                  var7.setValue(var6.getGameTime() + (long)var6.random.nextInt(20));
                  return false;
               } else if (var6.getGameTime() < var7.getValue()) {
                  return false;
               } else {
                  var7.setValue(var8x + 20L + (long)var6.getRandom().nextInt(20));
                  PoiManager var10 = var6.getPoiManager();
                  var8.long2ObjectEntrySet().removeIf((var2) -> {
                     return !((JitteredLinearRetry)var2.getValue()).isStillValid(var8x);
                  });
                  Predicate var11 = (var3x) -> {
                     JitteredLinearRetry var4 = (JitteredLinearRetry)var8.get(var3x.asLong());
                     if (var4 == null) {
                        return true;
                     } else if (!var4.shouldRetry(var8x)) {
                        return false;
                     } else {
                        var4.markAttempt(var8x);
                        return true;
                     }
                  };
                  Set var12 = (Set)var10.findAllClosestFirstWithType(var0, var11, var7x.blockPosition(), 48, PoiManager.Occupancy.HAS_SPACE).limit(5L).collect(Collectors.toSet());
                  Path var13 = findPathToPois(var7x, var12);
                  if (var13 != null && var13.canReach()) {
                     BlockPos var16 = var13.getTarget();
                     var10.getType(var16).ifPresent((var8xx) -> {
                        var10.take(var0, (var1, var2) -> {
                           return var2.equals(var16);
                        }, var16, 1);
                        var5.set(GlobalPos.of(var6.dimension(), var16));
                        var4.ifPresent((var2) -> {
                           var6.broadcastEntityEvent(var7x, var2);
                        });
                        var8.clear();
                        DebugPackets.sendPoiTicketCountPacket(var6, var16);
                     });
                  } else {
                     Iterator var14 = var12.iterator();

                     while(var14.hasNext()) {
                        Pair var15 = (Pair)var14.next();
                        var8.computeIfAbsent(((BlockPos)var15.getSecond()).asLong(), (var3x) -> {
                           return new JitteredLinearRetry(var6.random, var8x);
                        });
                     }
                  }

                  return true;
               }
            };
         });
      });
      return var2 == var1 ? var9 : BehaviorBuilder.create((var2x) -> {
         return var2x.group(var2x.absent(var1)).apply(var2x, (var1x) -> {
            return var9;
         });
      });
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

   private static class JitteredLinearRetry {
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
