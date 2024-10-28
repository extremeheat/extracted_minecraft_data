package net.minecraft.world.entity.ai.behavior;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.animal.allay.AllayAi;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class GoAndGiveItemsToTarget<E extends LivingEntity & InventoryCarrier> extends Behavior<E> {
   private static final int CLOSE_ENOUGH_DISTANCE_TO_TARGET = 3;
   private static final int ITEM_PICKUP_COOLDOWN_AFTER_THROWING = 60;
   private final Function<LivingEntity, Optional<PositionTracker>> targetPositionGetter;
   private final float speedModifier;

   public GoAndGiveItemsToTarget(Function<LivingEntity, Optional<PositionTracker>> var1, float var2, int var3) {
      super(Map.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS, MemoryStatus.REGISTERED), var3);
      this.targetPositionGetter = var1;
      this.speedModifier = var2;
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, E var2) {
      return this.canThrowItemToTarget(var2);
   }

   protected boolean canStillUse(ServerLevel var1, E var2, long var3) {
      return this.canThrowItemToTarget(var2);
   }

   protected void start(ServerLevel var1, E var2, long var3) {
      ((Optional)this.targetPositionGetter.apply(var2)).ifPresent((var2x) -> {
         BehaviorUtils.setWalkAndLookTargetMemories(var2, (PositionTracker)var2x, this.speedModifier, 3);
      });
   }

   protected void tick(ServerLevel var1, E var2, long var3) {
      Optional var5 = (Optional)this.targetPositionGetter.apply(var2);
      if (!var5.isEmpty()) {
         PositionTracker var6 = (PositionTracker)var5.get();
         double var7 = var6.currentPosition().distanceTo(var2.getEyePosition());
         if (var7 < 3.0) {
            ItemStack var9 = ((InventoryCarrier)var2).getInventory().removeItem(0, 1);
            if (!var9.isEmpty()) {
               throwItem(var2, var9, getThrowPosition(var6));
               if (var2 instanceof Allay) {
                  Allay var10 = (Allay)var2;
                  AllayAi.getLikedPlayer(var10).ifPresent((var3x) -> {
                     this.triggerDropItemOnBlock(var6, var9, var3x);
                  });
               }

               var2.getBrain().setMemory(MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS, (int)60);
            }
         }

      }
   }

   private void triggerDropItemOnBlock(PositionTracker var1, ItemStack var2, ServerPlayer var3) {
      BlockPos var4 = var1.currentBlockPosition().below();
      CriteriaTriggers.ALLAY_DROP_ITEM_ON_BLOCK.trigger(var3, var4, var2);
   }

   private boolean canThrowItemToTarget(E var1) {
      if (((InventoryCarrier)var1).getInventory().isEmpty()) {
         return false;
      } else {
         Optional var2 = (Optional)this.targetPositionGetter.apply(var1);
         return var2.isPresent();
      }
   }

   private static Vec3 getThrowPosition(PositionTracker var0) {
      return var0.currentPosition().add(0.0, 1.0, 0.0);
   }

   public static void throwItem(LivingEntity var0, ItemStack var1, Vec3 var2) {
      Vec3 var3 = new Vec3(0.20000000298023224, 0.30000001192092896, 0.20000000298023224);
      BehaviorUtils.throwItem(var0, var1, var2, var3, 0.2F);
      Level var4 = var0.level();
      if (var4.getGameTime() % 7L == 0L && var4.random.nextDouble() < 0.9) {
         float var5 = (Float)Util.getRandom((List)Allay.THROW_SOUND_PITCHES, var4.getRandom());
         var4.playSound((Player)null, (Entity)var0, SoundEvents.ALLAY_THROW, SoundSource.NEUTRAL, 1.0F, var5);
      }

   }
}
