package net.minecraft.world.entity.ai.behavior;

import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;

public class LocateHidingPlace {
   public LocateHidingPlace() {
      super();
   }

   public static OneShot<LivingEntity> create(int var0, float var1, int var2) {
      return BehaviorBuilder.create((var3) -> {
         return var3.group(var3.absent(MemoryModuleType.WALK_TARGET), var3.registered(MemoryModuleType.HOME), var3.registered(MemoryModuleType.HIDING_PLACE), var3.registered(MemoryModuleType.PATH), var3.registered(MemoryModuleType.LOOK_TARGET), var3.registered(MemoryModuleType.BREED_TARGET), var3.registered(MemoryModuleType.INTERACTION_TARGET)).apply(var3, (var4, var5, var6, var7, var8, var9, var10) -> {
            return (var11, var12, var13) -> {
               var11.getPoiManager().find((var0x) -> {
                  return var0x.is(PoiTypes.HOME);
               }, (var0x) -> {
                  return true;
               }, var12.blockPosition(), var2 + 1, PoiManager.Occupancy.ANY).filter((var2x) -> {
                  return var2x.closerToCenterThan(var12.position(), (double)var2);
               }).or(() -> {
                  return var11.getPoiManager().getRandom((var0x) -> {
                     return var0x.is(PoiTypes.HOME);
                  }, (var0x) -> {
                     return true;
                  }, PoiManager.Occupancy.ANY, var12.blockPosition(), var0, var12.getRandom());
               }).or(() -> {
                  return var3.tryGet(var5).map(GlobalPos::pos);
               }).ifPresent((var10x) -> {
                  var7.erase();
                  var8.erase();
                  var9.erase();
                  var10.erase();
                  var6.set(GlobalPos.of(var11.dimension(), var10x));
                  if (!var10x.closerToCenterThan(var12.position(), (double)var2)) {
                     var4.set(new WalkTarget(var10x, var1, var2));
                  }

               });
               return true;
            };
         });
      });
   }
}
