package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;

public class LookAndFollowTradingPlayerSink extends Behavior {
   private final float speed;

   public LookAndFollowTradingPlayerSink(float var1) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED), Integer.MAX_VALUE);
      this.speed = var1;
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, Villager var2) {
      Player var3 = var2.getTradingPlayer();
      return var2.isAlive() && var3 != null && !var2.isInWater() && !var2.hurtMarked && var2.distanceToSqr(var3) <= 16.0D && var3.containerMenu != null;
   }

   protected boolean canStillUse(ServerLevel var1, Villager var2, long var3) {
      return this.checkExtraStartConditions(var1, var2);
   }

   protected void start(ServerLevel var1, Villager var2, long var3) {
      this.followPlayer(var2);
   }

   protected void stop(ServerLevel var1, Villager var2, long var3) {
      Brain var5 = var2.getBrain();
      var5.eraseMemory(MemoryModuleType.WALK_TARGET);
      var5.eraseMemory(MemoryModuleType.LOOK_TARGET);
   }

   protected void tick(ServerLevel var1, Villager var2, long var3) {
      this.followPlayer(var2);
   }

   protected boolean timedOut(long var1) {
      return false;
   }

   private void followPlayer(Villager var1) {
      EntityPosWrapper var2 = new EntityPosWrapper(var1.getTradingPlayer());
      Brain var3 = var1.getBrain();
      var3.setMemory(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget(var2, this.speed, 2)));
      var3.setMemory(MemoryModuleType.LOOK_TARGET, (Object)var2);
   }

   // $FF: synthetic method
   protected boolean canStillUse(ServerLevel var1, LivingEntity var2, long var3) {
      return this.canStillUse(var1, (Villager)var2, var3);
   }

   // $FF: synthetic method
   protected void stop(ServerLevel var1, LivingEntity var2, long var3) {
      this.stop(var1, (Villager)var2, var3);
   }

   // $FF: synthetic method
   protected void start(ServerLevel var1, LivingEntity var2, long var3) {
      this.start(var1, (Villager)var2, var3);
   }
}
