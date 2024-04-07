package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.warden.Warden;

public class WardenEntitySensor extends NearestLivingEntitySensor<Warden> {
   public WardenEntitySensor() {
      super();
   }

   @Override
   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.copyOf(Iterables.concat(super.requires(), List.of(MemoryModuleType.NEAREST_ATTACKABLE)));
   }

   protected void doTick(ServerLevel var1, Warden var2) {
      super.doTick(var1, var2);
      getClosest(var2, var0 -> var0.getType() == EntityType.PLAYER)
         .or(() -> getClosest(var2, var0x -> var0x.getType() != EntityType.PLAYER))
         .ifPresentOrElse(
            var1x -> var2.getBrain().setMemory(MemoryModuleType.NEAREST_ATTACKABLE, var1x),
            () -> var2.getBrain().eraseMemory(MemoryModuleType.NEAREST_ATTACKABLE)
         );
   }

   private static Optional<LivingEntity> getClosest(Warden var0, Predicate<LivingEntity> var1) {
      return var0.getBrain()
         .getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES)
         .stream()
         .flatMap(Collection::stream)
         .filter(var0::canTargetEntity)
         .filter(var1)
         .findFirst();
   }

   @Override
   protected int radiusXZ() {
      return 24;
   }

   @Override
   protected int radiusY() {
      return 24;
   }
}
