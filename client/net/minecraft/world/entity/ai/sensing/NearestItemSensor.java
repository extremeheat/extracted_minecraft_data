package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.item.ItemEntity;

public class NearestItemSensor extends Sensor<Mob> {
   private static final long XZ_RANGE = 32L;
   private static final long Y_RANGE = 16L;
   public static final int MAX_DISTANCE_TO_WANTED_ITEM = 32;

   public NearestItemSensor() {
      super();
   }

   @Override
   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.of(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM);
   }

   protected void doTick(ServerLevel var1, Mob var2) {
      Brain var3 = var2.getBrain();
      List var4 = var1.getEntitiesOfClass(ItemEntity.class, var2.getBoundingBox().inflate(32.0, 16.0, 32.0), var0 -> true);
      var4.sort(Comparator.comparingDouble(var2::distanceToSqr));
      Optional var5 = var4.stream()
         .filter(var2x -> var2.wantsToPickUp(var1, var2x.getItem()))
         .filter(var1x -> var1x.closerThan(var2, 32.0))
         .filter(var2::hasLineOfSight)
         .findFirst();
      var3.setMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, var5);
   }
}
