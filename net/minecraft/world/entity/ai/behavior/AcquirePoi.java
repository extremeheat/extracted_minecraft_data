package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import java.util.function.Predicate;
import java.util.stream.Stream;
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

public class AcquirePoi extends Behavior {
   private final PoiType poiType;
   private final MemoryModuleType memoryType;
   private final boolean onlyIfAdult;
   private long lastUpdate;
   private final Long2LongMap batchCache = new Long2LongOpenHashMap();
   private int triedCount;

   public AcquirePoi(PoiType var1, MemoryModuleType var2, boolean var3) {
      super(ImmutableMap.of(var2, MemoryStatus.VALUE_ABSENT));
      this.poiType = var1;
      this.memoryType = var2;
      this.onlyIfAdult = var3;
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, PathfinderMob var2) {
      if (this.onlyIfAdult && var2.isBaby()) {
         return false;
      } else {
         return var1.getGameTime() - this.lastUpdate >= 20L;
      }
   }

   protected void start(ServerLevel var1, PathfinderMob var2, long var3) {
      this.triedCount = 0;
      this.lastUpdate = var1.getGameTime() + (long)var1.getRandom().nextInt(20);
      PoiManager var5 = var1.getPoiManager();
      Predicate var6 = (var1x) -> {
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
      Stream var7 = var5.findAll(this.poiType.getPredicate(), var6, new BlockPos(var2), 48, PoiManager.Occupancy.HAS_SPACE);
      Path var8 = var2.getNavigation().createPath(var7, this.poiType.getValidRange());
      if (var8 != null && var8.canReach()) {
         BlockPos var9 = var8.getTarget();
         var5.getType(var9).ifPresent((var5x) -> {
            var5.take(this.poiType.getPredicate(), (var1x) -> {
               return var1x.equals(var9);
            }, var9, 1);
            var2.getBrain().setMemory(this.memoryType, (Object)GlobalPos.of(var1.getDimension().getType(), var9));
            DebugPackets.sendPoiTicketCountPacket(var1, var9);
         });
      } else if (this.triedCount < 5) {
         this.batchCache.long2LongEntrySet().removeIf((var1x) -> {
            return var1x.getLongValue() < this.lastUpdate;
         });
      }

   }
}
