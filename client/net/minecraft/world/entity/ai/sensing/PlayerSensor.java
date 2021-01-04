package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class PlayerSensor extends Sensor<LivingEntity> {
   public PlayerSensor() {
      super();
   }

   protected void doTick(ServerLevel var1, LivingEntity var2) {
      Stream var10000 = var1.players().stream().filter(EntitySelector.NO_SPECTATORS).filter((var1x) -> {
         return var2.distanceToSqr(var1x) < 256.0D;
      });
      var2.getClass();
      List var3 = (List)var10000.sorted(Comparator.comparingDouble(var2::distanceToSqr)).collect(Collectors.toList());
      Brain var4 = var2.getBrain();
      var4.setMemory(MemoryModuleType.NEAREST_PLAYERS, (Object)var3);
      MemoryModuleType var10001 = MemoryModuleType.NEAREST_VISIBLE_PLAYER;
      Stream var10002 = var3.stream();
      var2.getClass();
      var4.setMemory(var10001, var10002.filter(var2::canSee).findFirst());
   }

   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.of(MemoryModuleType.NEAREST_PLAYERS, MemoryModuleType.NEAREST_VISIBLE_PLAYER);
   }
}
