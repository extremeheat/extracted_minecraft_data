package net.minecraft.world.entity.ai.behavior;

import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ValidateNearbyPoi {
   private static final int MAX_DISTANCE = 16;

   public ValidateNearbyPoi() {
      super();
   }

   public static BehaviorControl<LivingEntity> create(Predicate<Holder<PoiType>> var0, MemoryModuleType<GlobalPos> var1) {
      return BehaviorBuilder.create((var2) -> {
         return var2.group(var2.present(var1)).apply(var2, (var2x) -> {
            return (var3, var4, var5) -> {
               GlobalPos var7 = (GlobalPos)var2.get(var2x);
               BlockPos var8 = var7.pos();
               if (var3.dimension() == var7.dimension() && var8.closerToCenterThan(var4.position(), 16.0)) {
                  ServerLevel var9 = var3.getServer().getLevel(var7.dimension());
                  if (var9 != null && var9.getPoiManager().exists(var8, var0)) {
                     if (bedIsOccupied(var9, var8, var4)) {
                        var2x.erase();
                        var3.getPoiManager().release(var8);
                        DebugPackets.sendPoiTicketCountPacket(var3, var8);
                     }
                  } else {
                     var2x.erase();
                  }

                  return true;
               } else {
                  return false;
               }
            };
         });
      });
   }

   private static boolean bedIsOccupied(ServerLevel var0, BlockPos var1, LivingEntity var2) {
      BlockState var3 = var0.getBlockState(var1);
      return var3.is(BlockTags.BEDS) && (Boolean)var3.getValue(BedBlock.OCCUPIED) && !var2.isSleeping();
   }
}
