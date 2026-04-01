package net.minecraft.world.entity.livingblock.behavior;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public interface LivingBlockBehavior {
   boolean canStartUsing(LivingBlock entity);

   boolean tick(LivingBlock entity, final ServerLevel level, int tickCount);

   default void onStart(final LivingBlock entity) {
   }

   default void onStop(final LivingBlock entity) {
   }

   default void save(final ValueOutput output, final LivingBlock livingBlock) {
   }

   default void loadData(final ValueInput input) {
   }

   default String getDataTag() {
      return "";
   }

   default void onDeath(final LivingBlock entity, final ServerLevel level) {
   }

   default void onRemoval(final LivingBlock entity, final ServerLevel level) {
   }
}
