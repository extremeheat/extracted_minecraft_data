package net.minecraft.world.entity.ai.behavior;

import java.util.function.BiPredicate;
import java.util.function.Function;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class DismountOrSkipMounting {
   public DismountOrSkipMounting() {
      super();
   }

   public static <E extends LivingEntity> BehaviorControl<E> create(final int maxWalkDistToRideTarget, final BiPredicate<E, Entity> dontRideIf) {
      return BehaviorBuilder.create((Function)((i) -> i.group(i.registered(MemoryModuleType.RIDE_TARGET)).apply(i, (rideTarget) -> (level, body, timestamp) -> {
               Entity currentVehicle = body.getVehicle();
               Entity targetVehicle = (Entity)i.tryGet(rideTarget).orElse((Object)null);
               if (currentVehicle == null && targetVehicle == null) {
                  return false;
               } else {
                  Entity vehicle = currentVehicle == null ? targetVehicle : currentVehicle;
                  if (isVehicleValid(body, vehicle, maxWalkDistToRideTarget) && !dontRideIf.test(body, vehicle)) {
                     return false;
                  } else {
                     body.stopRiding();
                     rideTarget.erase();
                     return true;
                  }
               }
            })));
   }

   private static boolean isVehicleValid(final LivingEntity body, final Entity vehicle, final int maxWalkDistToRideTarget) {
      return vehicle.isAlive() && vehicle.closerThan(body, (double)maxWalkDistToRideTarget) && vehicle.level() == body.level();
   }
}
