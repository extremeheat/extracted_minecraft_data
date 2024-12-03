package net.minecraft.world.entity.ai.behavior;

import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.level.pathfinder.Path;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableLong;

public class SetClosestHomeAsWalkTarget {
   private static final int CACHE_TIMEOUT = 40;
   private static final int BATCH_SIZE = 5;
   private static final int RATE = 20;
   private static final int OK_DISTANCE_SQR = 4;

   public SetClosestHomeAsWalkTarget() {
      super();
   }

   public static BehaviorControl<PathfinderMob> create(float var0) {
      Long2LongOpenHashMap var1 = new Long2LongOpenHashMap();
      MutableLong var2 = new MutableLong(0L);
      return BehaviorBuilder.create((Function)((var3) -> var3.group(var3.absent(MemoryModuleType.WALK_TARGET), var3.absent(MemoryModuleType.HOME)).apply(var3, (var3x, var4) -> (var4x, var5, var6) -> {
               if (var4x.getGameTime() - var2.getValue() < 20L) {
                  return false;
               } else {
                  PoiManager var8 = var4x.getPoiManager();
                  Optional var9 = var8.findClosest((var0x) -> var0x.is(PoiTypes.HOME), var5.blockPosition(), 48, PoiManager.Occupancy.ANY);
                  if (!var9.isEmpty() && !(((BlockPos)var9.get()).distSqr(var5.blockPosition()) <= 4.0)) {
                     MutableInt var10 = new MutableInt(0);
                     var2.setValue(var4x.getGameTime() + (long)var4x.getRandom().nextInt(20));
                     Predicate var11 = (var3) -> {
                        long var4 = var3.asLong();
                        if (var1.containsKey(var4)) {
                           return false;
                        } else if (var10.incrementAndGet() >= 5) {
                           return false;
                        } else {
                           var1.put(var4, var2.getValue() + 40L);
                           return true;
                        }
                     };
                     Set var12 = (Set)var8.findAllWithType((var0x) -> var0x.is(PoiTypes.HOME), var11, var5.blockPosition(), 48, PoiManager.Occupancy.ANY).collect(Collectors.toSet());
                     Path var13 = AcquirePoi.findPathToPois(var5, var12);
                     if (var13 != null && var13.canReach()) {
                        BlockPos var14 = var13.getTarget();
                        Optional var15 = var8.getType(var14);
                        if (var15.isPresent()) {
                           var3x.set(new WalkTarget(var14, var0, 1));
                           DebugPackets.sendPoiTicketCountPacket(var4x, var14);
                        }
                     } else if (var10.getValue() < 5) {
                        var1.long2LongEntrySet().removeIf((var1x) -> var1x.getLongValue() < var2.getValue());
                     }

                     return true;
                  } else {
                     return false;
                  }
               }
            })));
   }
}
