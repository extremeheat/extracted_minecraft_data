package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.item.ItemEntity;

public class NearestItemSensor extends Sensor<Mob> {
   public NearestItemSensor() {
      super();
   }

   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.of(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM);
   }

   protected void doTick(ServerLevel var1, Mob var2) {
      Brain var3 = var2.getBrain();
      List var4 = var1.getEntitiesOfClass(ItemEntity.class, var2.getBoundingBox().inflate(8.0D, 4.0D, 8.0D), (var0) -> {
         return true;
      });
      var2.getClass();
      var4.sort(Comparator.comparingDouble(var2::distanceToSqr));
      Stream var10000 = var4.stream().filter((var1x) -> {
         return var2.wantsToPickUp(var1x.getItem());
      }).filter((var1x) -> {
         return var1x.closerThan(var2, 9.0D);
      });
      var2.getClass();
      Optional var5 = var10000.filter(var2::canSee).findFirst();
      var3.setMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, var5);
   }
}
