package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.pathfinder.Path;

public class SetClosestHomeAsWalkTarget extends Behavior<LivingEntity> {
   private static final int CACHE_TIMEOUT = 40;
   private static final int BATCH_SIZE = 5;
   private static final int RATE = 20;
   private static final int OK_DISTANCE_SQR = 4;
   private final float speedModifier;
   private final Long2LongMap batchCache = new Long2LongOpenHashMap();
   private int triedCount;
   private long lastUpdate;

   public SetClosestHomeAsWalkTarget(float var1) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.HOME, MemoryStatus.VALUE_ABSENT));
      this.speedModifier = var1;
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, LivingEntity var2) {
      if (var1.getGameTime() - this.lastUpdate < 20L) {
         return false;
      } else {
         PathfinderMob var3 = (PathfinderMob)var2;
         PoiManager var4 = var1.getPoiManager();
         Optional var5 = var4.findClosest(PoiType.HOME.getPredicate(), var2.blockPosition(), 48, PoiManager.Occupancy.ANY);
         return var5.isPresent() && !(((BlockPos)var5.get()).distSqr(var3.blockPosition()) <= 4.0D);
      }
   }

   protected void start(ServerLevel var1, LivingEntity var2, long var3) {
      this.triedCount = 0;
      this.lastUpdate = var1.getGameTime() + (long)var1.getRandom().nextInt(20);
      PathfinderMob var5 = (PathfinderMob)var2;
      PoiManager var6 = var1.getPoiManager();
      Predicate var7 = (var1x) -> {
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
      Stream var8 = var6.findAll(PoiType.HOME.getPredicate(), var7, var2.blockPosition(), 48, PoiManager.Occupancy.ANY);
      Path var9 = var5.getNavigation().createPath(var8, PoiType.HOME.getValidRange());
      if (var9 != null && var9.canReach()) {
         BlockPos var10 = var9.getTarget();
         Optional var11 = var6.getType(var10);
         if (var11.isPresent()) {
            var2.getBrain().setMemory(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget(var10, this.speedModifier, 1)));
            DebugPackets.sendPoiTicketCountPacket(var1, var10);
         }
      } else if (this.triedCount < 5) {
         this.batchCache.long2LongEntrySet().removeIf((var1x) -> {
            return var1x.getLongValue() < this.lastUpdate;
         });
      }

   }
}
