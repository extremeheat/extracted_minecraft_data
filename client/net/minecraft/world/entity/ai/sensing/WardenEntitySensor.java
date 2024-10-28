package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.warden.Warden;

public class WardenEntitySensor extends NearestLivingEntitySensor<Warden> {
   public WardenEntitySensor() {
      super();
   }

   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.copyOf(Iterables.concat(super.requires(), List.of(MemoryModuleType.NEAREST_ATTACKABLE)));
   }

   protected void doTick(ServerLevel var1, Warden var2) {
      super.doTick(var1, var2);
      getClosest(var2, (var0) -> {
         return var0.getType() == EntityType.PLAYER;
      }).or(() -> {
         return getClosest(var2, (var0) -> {
            return var0.getType() != EntityType.PLAYER;
         });
      }).ifPresentOrElse((var1x) -> {
         var2.getBrain().setMemory(MemoryModuleType.NEAREST_ATTACKABLE, (Object)var1x);
      }, () -> {
         var2.getBrain().eraseMemory(MemoryModuleType.NEAREST_ATTACKABLE);
      });
   }

   private static Optional<LivingEntity> getClosest(Warden var0, Predicate<LivingEntity> var1) {
      Stream var10000 = var0.getBrain().getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES).stream().flatMap(Collection::stream);
      Objects.requireNonNull(var0);
      return var10000.filter(var0::canTargetEntity).filter(var1).findFirst();
   }

   protected int radiusXZ() {
      return 24;
   }

   protected int radiusY() {
      return 24;
   }
}
