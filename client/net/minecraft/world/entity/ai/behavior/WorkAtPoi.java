package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.npc.Villager;

public class WorkAtPoi extends Behavior<Villager> {
   private static final int CHECK_COOLDOWN = 300;
   private static final double DISTANCE = 1.73D;
   private long lastCheck;

   public WorkAtPoi() {
      super(ImmutableMap.of(MemoryModuleType.JOB_SITE, MemoryStatus.VALUE_PRESENT, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED));
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, Villager var2) {
      if (var1.getGameTime() - this.lastCheck < 300L) {
         return false;
      } else if (var1.random.nextInt(2) != 0) {
         return false;
      } else {
         this.lastCheck = var1.getGameTime();
         GlobalPos var3 = (GlobalPos)var2.getBrain().getMemory(MemoryModuleType.JOB_SITE).get();
         return var3.dimension() == var1.dimension() && var3.pos().closerThan(var2.position(), 1.73D);
      }
   }

   protected void start(ServerLevel var1, Villager var2, long var3) {
      Brain var5 = var2.getBrain();
      var5.setMemory(MemoryModuleType.LAST_WORKED_AT_POI, (Object)var3);
      var5.getMemory(MemoryModuleType.JOB_SITE).ifPresent((var1x) -> {
         var5.setMemory(MemoryModuleType.LOOK_TARGET, (Object)(new BlockPosTracker(var1x.pos())));
      });
      var2.playWorkSound();
      this.useWorkstation(var1, var2);
      if (var2.shouldRestock()) {
         var2.restock();
      }

   }

   protected void useWorkstation(ServerLevel var1, Villager var2) {
   }

   protected boolean canStillUse(ServerLevel var1, Villager var2, long var3) {
      Optional var5 = var2.getBrain().getMemory(MemoryModuleType.JOB_SITE);
      if (!var5.isPresent()) {
         return false;
      } else {
         GlobalPos var6 = (GlobalPos)var5.get();
         return var6.dimension() == var1.dimension() && var6.pos().closerThan(var2.position(), 1.73D);
      }
   }

   // $FF: synthetic method
   protected boolean checkExtraStartConditions(ServerLevel var1, LivingEntity var2) {
      return this.checkExtraStartConditions(var1, (Villager)var2);
   }
}
