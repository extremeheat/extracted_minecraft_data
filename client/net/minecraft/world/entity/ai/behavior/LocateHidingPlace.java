package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;

public class LocateHidingPlace extends Behavior<LivingEntity> {
   private final float speedModifier;
   private final int radius;
   private final int closeEnoughDist;
   private Optional<BlockPos> currentPos = Optional.empty();

   public LocateHidingPlace(int var1, float var2, int var3) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.HOME, MemoryStatus.REGISTERED, MemoryModuleType.HIDING_PLACE, MemoryStatus.REGISTERED));
      this.radius = var1;
      this.speedModifier = var2;
      this.closeEnoughDist = var3;
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, LivingEntity var2) {
      Optional var3 = var1.getPoiManager().find((var0) -> {
         return var0 == PoiType.HOME;
      }, (var0) -> {
         return true;
      }, var2.blockPosition(), this.closeEnoughDist + 1, PoiManager.Occupancy.ANY);
      if (var3.isPresent() && ((BlockPos)var3.get()).closerThan(var2.position(), (double)this.closeEnoughDist)) {
         this.currentPos = var3;
      } else {
         this.currentPos = Optional.empty();
      }

      return true;
   }

   protected void start(ServerLevel var1, LivingEntity var2, long var3) {
      Brain var5 = var2.getBrain();
      Optional var6 = this.currentPos;
      if (!var6.isPresent()) {
         var6 = var1.getPoiManager().getRandom((var0) -> {
            return var0 == PoiType.HOME;
         }, (var0) -> {
            return true;
         }, PoiManager.Occupancy.ANY, var2.blockPosition(), this.radius, var2.getRandom());
         if (!var6.isPresent()) {
            Optional var7 = var5.getMemory(MemoryModuleType.HOME);
            if (var7.isPresent()) {
               var6 = Optional.of(((GlobalPos)var7.get()).pos());
            }
         }
      }

      if (var6.isPresent()) {
         var5.eraseMemory(MemoryModuleType.PATH);
         var5.eraseMemory(MemoryModuleType.LOOK_TARGET);
         var5.eraseMemory(MemoryModuleType.BREED_TARGET);
         var5.eraseMemory(MemoryModuleType.INTERACTION_TARGET);
         var5.setMemory(MemoryModuleType.HIDING_PLACE, (Object)GlobalPos.of(var1.dimension(), (BlockPos)var6.get()));
         if (!((BlockPos)var6.get()).closerThan(var2.position(), (double)this.closeEnoughDist)) {
            var5.setMemory(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget((BlockPos)var6.get(), this.speedModifier, this.closeEnoughDist)));
         }
      }

   }
}
