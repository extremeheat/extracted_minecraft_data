package net.minecraft.world.level;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

public interface ServerLevelAccessor extends LevelAccessor {
   ServerLevel getLevel();

   default void addFreshEntityWithPassengers(Entity var1) {
      var1.getSelfAndPassengers().forEach(this::addFreshEntity);
   }
}
