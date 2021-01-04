package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.pathfinder.Path;

public class MakeLove extends Behavior<Villager> {
   private long birthTimestamp;

   public MakeLove() {
      super(ImmutableMap.of(MemoryModuleType.BREED_TARGET, MemoryStatus.VALUE_PRESENT, MemoryModuleType.VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT), 350, 350);
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, Villager var2) {
      return this.isBreedingPossible(var2);
   }

   protected boolean canStillUse(ServerLevel var1, Villager var2, long var3) {
      return var3 <= this.birthTimestamp && this.isBreedingPossible(var2);
   }

   protected void start(ServerLevel var1, Villager var2, long var3) {
      Villager var5 = this.getBreedingTarget(var2);
      BehaviorUtils.lockGazeAndWalkToEachOther(var2, var5);
      var1.broadcastEntityEvent(var5, (byte)18);
      var1.broadcastEntityEvent(var2, (byte)18);
      int var6 = 275 + var2.getRandom().nextInt(50);
      this.birthTimestamp = var3 + (long)var6;
   }

   protected void tick(ServerLevel var1, Villager var2, long var3) {
      Villager var5 = this.getBreedingTarget(var2);
      if (var2.distanceToSqr(var5) <= 5.0D) {
         BehaviorUtils.lockGazeAndWalkToEachOther(var2, var5);
         if (var3 >= this.birthTimestamp) {
            var2.eatAndDigestFood();
            var5.eatAndDigestFood();
            this.tryToGiveBirth(var1, var2, var5);
         } else if (var2.getRandom().nextInt(35) == 0) {
            var1.broadcastEntityEvent(var5, (byte)12);
            var1.broadcastEntityEvent(var2, (byte)12);
         }

      }
   }

   private void tryToGiveBirth(ServerLevel var1, Villager var2, Villager var3) {
      Optional var4 = this.takeVacantBed(var1, var2);
      if (!var4.isPresent()) {
         var1.broadcastEntityEvent(var3, (byte)13);
         var1.broadcastEntityEvent(var2, (byte)13);
      } else {
         Optional var5 = this.breed(var2, var3);
         if (var5.isPresent()) {
            this.giveBedToChild(var1, (Villager)var5.get(), (BlockPos)var4.get());
         } else {
            var1.getPoiManager().release((BlockPos)var4.get());
         }
      }

   }

   protected void stop(ServerLevel var1, Villager var2, long var3) {
      var2.getBrain().eraseMemory(MemoryModuleType.BREED_TARGET);
   }

   private Villager getBreedingTarget(Villager var1) {
      return (Villager)var1.getBrain().getMemory(MemoryModuleType.BREED_TARGET).get();
   }

   private boolean isBreedingPossible(Villager var1) {
      Brain var2 = var1.getBrain();
      if (!var2.getMemory(MemoryModuleType.BREED_TARGET).isPresent()) {
         return false;
      } else {
         Villager var3 = this.getBreedingTarget(var1);
         return BehaviorUtils.targetIsValid(var2, MemoryModuleType.BREED_TARGET, EntityType.VILLAGER) && var1.canBreed() && var3.canBreed();
      }
   }

   private Optional<BlockPos> takeVacantBed(ServerLevel var1, Villager var2) {
      return var1.getPoiManager().take(PoiType.HOME.getPredicate(), (var2x) -> {
         return this.canReach(var2, var2x);
      }, new BlockPos(var2), 48);
   }

   private boolean canReach(Villager var1, BlockPos var2) {
      Path var3 = var1.getNavigation().createPath(var2, PoiType.HOME.getValidRange());
      return var3 != null && var3.canReach();
   }

   private Optional<Villager> breed(Villager var1, Villager var2) {
      Villager var3 = var1.getBreedOffspring(var2);
      if (var3 == null) {
         return Optional.empty();
      } else {
         var1.setAge(6000);
         var2.setAge(6000);
         var3.setAge(-24000);
         var3.moveTo(var1.x, var1.y, var1.z, 0.0F, 0.0F);
         var1.level.addFreshEntity(var3);
         var1.level.broadcastEntityEvent(var3, (byte)12);
         return Optional.of(var3);
      }
   }

   private void giveBedToChild(ServerLevel var1, Villager var2, BlockPos var3) {
      GlobalPos var4 = GlobalPos.of(var1.getDimension().getType(), var3);
      var2.getBrain().setMemory(MemoryModuleType.HOME, (Object)var4);
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
   protected void tick(ServerLevel var1, LivingEntity var2, long var3) {
      this.tick(var1, (Villager)var2, var3);
   }

   // $FF: synthetic method
   protected void start(ServerLevel var1, LivingEntity var2, long var3) {
      this.start(var1, (Villager)var2, var3);
   }
}
