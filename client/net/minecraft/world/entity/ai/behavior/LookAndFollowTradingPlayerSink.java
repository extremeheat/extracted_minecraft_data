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

public class LookAndFollowTradingPlayerSink extends Behavior<Villager> {
   private final float speedModifier;

   public LookAndFollowTradingPlayerSink(float var1) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED), 2147483647);
      this.speedModifier = var1;
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, Villager var2) {
      Player var3 = var2.getTradingPlayer();
      return var2.isAlive() && var3 != null && !var2.isInWater() && !var2.hurtMarked && var2.distanceToSqr(var3) <= 16.0 && var3.containerMenu != null;
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
      Brain var2 = var1.getBrain();
      var2.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new EntityTracker(var1.getTradingPlayer(), false), this.speedModifier, 2));
      var2.setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker(var1.getTradingPlayer(), true));
   }

   // $FF: synthetic method
   protected void stop(final ServerLevel var1, final LivingEntity var2, final long var3) {
      this.stop(var1, (Villager)var2, var3);
   }

   // $FF: synthetic method
   protected void tick(final ServerLevel var1, final LivingEntity var2, final long var3) {
      this.tick(var1, (Villager)var2, var3);
   }

   // $FF: synthetic method
   protected void start(final ServerLevel var1, final LivingEntity var2, final long var3) {
      this.start(var1, (Villager)var2, var3);
   }
}
