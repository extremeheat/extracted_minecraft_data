package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.item.ItemEntity;

public class GoToWantedItem<E extends LivingEntity> extends Behavior<E> {
   private final Predicate<E> predicate;
   private final int maxDistToWalk;
   private final float speedModifier;

   public GoToWantedItem(float var1, boolean var2, int var3) {
      this((var0) -> {
         return true;
      }, var1, var2, var3);
   }

   public GoToWantedItem(Predicate<E> var1, float var2, boolean var3, int var4) {
      super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.WALK_TARGET, var3 ? MemoryStatus.REGISTERED : MemoryStatus.VALUE_ABSENT, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryStatus.VALUE_PRESENT));
      this.predicate = var1;
      this.maxDistToWalk = var4;
      this.speedModifier = var2;
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, E var2) {
      return this.predicate.test(var2) && this.getClosestLovedItem(var2).closerThan(var2, (double)this.maxDistToWalk);
   }

   protected void start(ServerLevel var1, E var2, long var3) {
      BehaviorUtils.setWalkAndLookTargetMemories(var2, (Entity)this.getClosestLovedItem(var2), this.speedModifier, 0);
   }

   private ItemEntity getClosestLovedItem(E var1) {
      return (ItemEntity)var1.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM).get();
   }
}
