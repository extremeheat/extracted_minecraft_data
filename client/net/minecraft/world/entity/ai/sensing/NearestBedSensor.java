package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.AcquirePoi;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.level.pathfinder.Path;

public class NearestBedSensor extends Sensor<Mob> {
   private static final int CACHE_TIMEOUT = 40;
   private static final int BATCH_SIZE = 5;
   private static final int RATE = 20;
   private final Long2LongMap batchCache = new Long2LongOpenHashMap();
   private int triedCount;
   private long lastUpdate;

   public NearestBedSensor() {
      super(20);
   }

   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.of(MemoryModuleType.NEAREST_BED);
   }

   protected void doTick(ServerLevel var1, Mob var2) {
      if (var2.isBaby()) {
         this.triedCount = 0;
         this.lastUpdate = var1.getGameTime() + (long)var1.getRandom().nextInt(20);
         PoiManager var3 = var1.getPoiManager();
         Predicate var4 = (var1x) -> {
            long var2 = var1x.asLong();
            if (this.batchCache.containsKey(var2)) {
               return false;
            } else if (++this.triedCount >= 5) {
               return false;
            } else {
               this.batchCache.put(var2, this.lastUpdate + 40L);
               return true;
            }
         };
         Set var5 = (Set)var3.findAllWithType((var0) -> {
            return var0.is(PoiTypes.HOME);
         }, var4, var2.blockPosition(), 48, PoiManager.Occupancy.ANY).collect(Collectors.toSet());
         Path var6 = AcquirePoi.findPathToPois(var2, var5);
         if (var6 != null && var6.canReach()) {
            BlockPos var7 = var6.getTarget();
            Optional var8 = var3.getType(var7);
            if (var8.isPresent()) {
               var2.getBrain().setMemory(MemoryModuleType.NEAREST_BED, (Object)var7);
            }
         } else if (this.triedCount < 5) {
            this.batchCache.long2LongEntrySet().removeIf((var1x) -> {
               return var1x.getLongValue() < this.lastUpdate;
            });
         }

      }
   }
}
