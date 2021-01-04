package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.phys.Vec3;

public class SetWalkTargetFromBlockMemory extends Behavior<Villager> {
   private final MemoryModuleType<GlobalPos> memoryType;
   private final float speed;
   private final int closeEnoughDist;
   private final int tooFarDistance;
   private final int tooLongUnreachableDuration;

   public SetWalkTargetFromBlockMemory(MemoryModuleType<GlobalPos> var1, float var2, int var3, int var4, int var5) {
      super(ImmutableMap.of(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryStatus.REGISTERED, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, var1, MemoryStatus.VALUE_PRESENT));
      this.memoryType = var1;
      this.speed = var2;
      this.closeEnoughDist = var3;
      this.tooFarDistance = var4;
      this.tooLongUnreachableDuration = var5;
   }

   private void dropPOI(Villager var1, long var2) {
      Brain var4 = var1.getBrain();
      var1.releasePoi(this.memoryType);
      var4.eraseMemory(this.memoryType);
      var4.setMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, (Object)var2);
   }

   protected void start(ServerLevel var1, Villager var2, long var3) {
      Brain var5 = var2.getBrain();
      var5.getMemory(this.memoryType).ifPresent((var6) -> {
         if (this.tiredOfTryingToFindTarget(var1, var2)) {
            this.dropPOI(var2, var3);
         } else if (this.tooFar(var1, var2, var6)) {
            Vec3 var7 = null;
            int var8 = 0;

            for(boolean var9 = true; var8 < 1000 && (var7 == null || this.tooFar(var1, var2, GlobalPos.of(var2.dimension, new BlockPos(var7)))); ++var8) {
               var7 = RandomPos.getPosTowards(var2, 15, 7, new Vec3(var6.pos()));
            }

            if (var8 == 1000) {
               this.dropPOI(var2, var3);
               return;
            }

            var5.setMemory(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget(var7, this.speed, this.closeEnoughDist)));
         } else if (!this.closeEnough(var1, var2, var6)) {
            var5.setMemory(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget(var6.pos(), this.speed, this.closeEnoughDist)));
         }

      });
   }

   private boolean tiredOfTryingToFindTarget(ServerLevel var1, Villager var2) {
      Optional var3 = var2.getBrain().getMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
      if (var3.isPresent()) {
         return var1.getGameTime() - (Long)var3.get() > (long)this.tooLongUnreachableDuration;
      } else {
         return false;
      }
   }

   private boolean tooFar(ServerLevel var1, Villager var2, GlobalPos var3) {
      return var3.dimension() != var1.getDimension().getType() || var3.pos().distManhattan(new BlockPos(var2)) > this.tooFarDistance;
   }

   private boolean closeEnough(ServerLevel var1, Villager var2, GlobalPos var3) {
      return var3.dimension() == var1.getDimension().getType() && var3.pos().distManhattan(new BlockPos(var2)) <= this.closeEnoughDist;
   }
}
