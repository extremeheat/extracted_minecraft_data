package net.minecraft.world.entity.ai.behavior;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;

public class LocateHidingPlace {
   public LocateHidingPlace() {
      super();
   }

   public static OneShot<LivingEntity> create(int var0, float var1, int var2) {
      return BehaviorBuilder.create(
         var3 -> var3.group(
                  var3.absent(MemoryModuleType.WALK_TARGET),
                  var3.registered(MemoryModuleType.HOME),
                  var3.registered(MemoryModuleType.HIDING_PLACE),
                  var3.registered(MemoryModuleType.PATH),
                  var3.registered(MemoryModuleType.LOOK_TARGET),
                  var3.registered(MemoryModuleType.BREED_TARGET),
                  var3.registered(MemoryModuleType.INTERACTION_TARGET)
               )
               .apply(
                  var3,
                  (var4, var5, var6, var7, var8, var9, var10) -> (var11, var12, var13) -> {
                        var11.getPoiManager()
                           .find(var0xxxx -> var0xxxx.is(PoiTypes.HOME), var0xxxx -> true, var12.blockPosition(), var2 + 1, PoiManager.Occupancy.ANY)
                           .filter(var2xxxx -> var2xxxx.closerToCenterThan(var12.position(), (double)var2))
                           .or(
                              () -> var11.getPoiManager()
                                    .getRandom(
                                       var0xxxxx -> var0xxxxx.is(PoiTypes.HOME),
                                       var0xxxxx -> true,
                                       PoiManager.Occupancy.ANY,
                                       var12.blockPosition(),
                                       var0,
                                       var12.getRandom()
                                    )
                           )
                           .or(() -> var3.tryGet(var5).map(GlobalPos::pos))
                           .ifPresent(var10xx -> {
                              var7.erase();
                              var8.erase();
                              var9.erase();
                              var10.erase();
                              var6.set(GlobalPos.of(var11.dimension(), var10xx));
                              if (!var10xx.closerToCenterThan(var12.position(), (double)var2)) {
                                 var4.set(new WalkTarget(var10xx, var1, var2));
                              }
                           });
                        return true;
                     }
               )
      );
   }
}
