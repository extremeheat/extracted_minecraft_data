package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.npc.Villager;

public class StrollToPoiList extends Behavior<Villager> {
   private final MemoryModuleType<List<GlobalPos>> strollToMemoryType;
   private final MemoryModuleType<GlobalPos> mustBeCloseToMemoryType;
   private final float speedModifier;
   private final int closeEnoughDist;
   private final int maxDistanceFromPoi;
   private long nextOkStartTime;
   @Nullable
   private GlobalPos targetPos;

   public StrollToPoiList(MemoryModuleType<List<GlobalPos>> var1, float var2, int var3, int var4, MemoryModuleType<GlobalPos> var5) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, var1, MemoryStatus.VALUE_PRESENT, var5, MemoryStatus.VALUE_PRESENT));
      this.strollToMemoryType = var1;
      this.speedModifier = var2;
      this.closeEnoughDist = var3;
      this.maxDistanceFromPoi = var4;
      this.mustBeCloseToMemoryType = var5;
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, Villager var2) {
      Optional var3 = var2.getBrain().getMemory(this.strollToMemoryType);
      Optional var4 = var2.getBrain().getMemory(this.mustBeCloseToMemoryType);
      if (var3.isPresent() && var4.isPresent()) {
         List var5 = (List)var3.get();
         if (!var5.isEmpty()) {
            this.targetPos = (GlobalPos)var5.get(var1.getRandom().nextInt(var5.size()));
            return this.targetPos != null && var1.dimension() == this.targetPos.dimension() && ((GlobalPos)var4.get()).pos().closerThan(var2.position(), (double)this.maxDistanceFromPoi);
         }
      }

      return false;
   }

   protected void start(ServerLevel var1, Villager var2, long var3) {
      if (var3 > this.nextOkStartTime && this.targetPos != null) {
         var2.getBrain().setMemory(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget(this.targetPos.pos(), this.speedModifier, this.closeEnoughDist)));
         this.nextOkStartTime = var3 + 100L;
      }

   }
}
