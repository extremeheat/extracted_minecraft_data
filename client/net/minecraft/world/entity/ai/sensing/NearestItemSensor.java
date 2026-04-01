package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.livingblock.LivingBlock;

public class NearestItemSensor extends Sensor<Mob> {
   private static final long XZ_RANGE = 32L;
   private static final long Y_RANGE = 16L;
   public static final int MAX_DISTANCE_TO_WANTED_ITEM = 32;

   public NearestItemSensor() {
      super();
   }

   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.of(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM);
   }

   protected void doTick(final ServerLevel level, final Mob body) {
      Brain<?> brain = body.getBrain();
      List<LivingBlock> items = level.getEntitiesOfClass(LivingBlock.class, body.getBoundingBox().inflate(32.0, 16.0, 32.0), (item) -> true);
      Objects.requireNonNull(body);
      items.sort(Comparator.comparingDouble(body::distanceToSqr));
      Stream var10000 = items.stream().filter((itemEntity) -> body.wantsToPickUp(level, itemEntity.getItemStack())).filter((itemEntity) -> itemEntity.closerThan(body, 32.0));
      Objects.requireNonNull(body);
      Optional<LivingBlock> nearestVisibleLovedItem = var10000.filter(body::hasLineOfSight).findFirst();
      brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, nearestVisibleLovedItem);
   }
}
