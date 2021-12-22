package net.minecraft.world.entity.animal.axolotl;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class PlayDead extends Behavior<Axolotl> {
   public PlayDead() {
      super(ImmutableMap.of(MemoryModuleType.PLAY_DEAD_TICKS, MemoryStatus.VALUE_PRESENT, MemoryModuleType.HURT_BY_ENTITY, MemoryStatus.VALUE_PRESENT), 200);
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, Axolotl var2) {
      return var2.isInWaterOrBubble();
   }

   protected boolean canStillUse(ServerLevel var1, Axolotl var2, long var3) {
      return var2.isInWaterOrBubble() && var2.getBrain().hasMemoryValue(MemoryModuleType.PLAY_DEAD_TICKS);
   }

   protected void start(ServerLevel var1, Axolotl var2, long var3) {
      Brain var5 = var2.getBrain();
      var5.eraseMemory(MemoryModuleType.WALK_TARGET);
      var5.eraseMemory(MemoryModuleType.LOOK_TARGET);
      var2.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 0));
   }

   // $FF: synthetic method
   protected boolean canStillUse(ServerLevel var1, LivingEntity var2, long var3) {
      return this.canStillUse(var1, (Axolotl)var2, var3);
   }

   // $FF: synthetic method
   protected void start(ServerLevel var1, LivingEntity var2, long var3) {
      this.start(var1, (Axolotl)var2, var3);
   }
}
