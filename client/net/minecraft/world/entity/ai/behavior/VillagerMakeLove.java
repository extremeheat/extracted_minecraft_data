package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.pathfinder.Path;

public class VillagerMakeLove extends Behavior<Villager> {
   private long birthTimestamp;

   public VillagerMakeLove() {
      super(ImmutableMap.of(MemoryModuleType.BREED_TARGET, MemoryStatus.VALUE_PRESENT, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT), 350, 350);
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, Villager var2) {
      return this.isBreedingPossible(var2);
   }

   protected boolean canStillUse(ServerLevel var1, Villager var2, long var3) {
      return var3 <= this.birthTimestamp && this.isBreedingPossible(var2);
   }

   protected void start(ServerLevel var1, Villager var2, long var3) {
      AgeableMob var5 = (AgeableMob)var2.getBrain().getMemory(MemoryModuleType.BREED_TARGET).get();
      BehaviorUtils.lockGazeAndWalkToEachOther(var2, var5, 0.5F, 2);
      var1.broadcastEntityEvent(var5, (byte)18);
      var1.broadcastEntityEvent(var2, (byte)18);
      int var6 = 275 + var2.getRandom().nextInt(50);
      this.birthTimestamp = var3 + (long)var6;
   }

   protected void tick(ServerLevel var1, Villager var2, long var3) {
      Villager var5 = (Villager)var2.getBrain().getMemory(MemoryModuleType.BREED_TARGET).get();
      if (!(var2.distanceToSqr(var5) > 5.0)) {
         BehaviorUtils.lockGazeAndWalkToEachOther(var2, var5, 0.5F, 2);
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
      if (var4.isEmpty()) {
         var1.broadcastEntityEvent(var3, (byte)13);
         var1.broadcastEntityEvent(var2, (byte)13);
      } else {
         Optional var5 = this.breed(var1, var2, var3);
         if (var5.isPresent()) {
            this.giveBedToChild(var1, (Villager)var5.get(), (BlockPos)var4.get());
         } else {
            var1.getPoiManager().release((BlockPos)var4.get());
            DebugPackets.sendPoiTicketCountPacket(var1, (BlockPos)var4.get());
         }
      }

   }

   protected void stop(ServerLevel var1, Villager var2, long var3) {
      var2.getBrain().eraseMemory(MemoryModuleType.BREED_TARGET);
   }

   private boolean isBreedingPossible(Villager var1) {
      Brain var2 = var1.getBrain();
      Optional var3 = var2.getMemory(MemoryModuleType.BREED_TARGET).filter((var0) -> {
         return var0.getType() == EntityType.VILLAGER;
      });
      if (var3.isEmpty()) {
         return false;
      } else {
         return BehaviorUtils.targetIsValid(var2, MemoryModuleType.BREED_TARGET, EntityType.VILLAGER) && var1.canBreed() && ((AgeableMob)var3.get()).canBreed();
      }
   }

   private Optional<BlockPos> takeVacantBed(ServerLevel var1, Villager var2) {
      return var1.getPoiManager().take((var0) -> {
         return var0.is(PoiTypes.HOME);
      }, (var2x, var3) -> {
         return this.canReach(var2, var3, var2x);
      }, var2.blockPosition(), 48);
   }

   private boolean canReach(Villager var1, BlockPos var2, Holder<PoiType> var3) {
      Path var4 = var1.getNavigation().createPath(var2, ((PoiType)var3.value()).validRange());
      return var4 != null && var4.canReach();
   }

   private Optional<Villager> breed(ServerLevel var1, Villager var2, Villager var3) {
      Villager var4 = var2.getBreedOffspring(var1, var3);
      if (var4 == null) {
         return Optional.empty();
      } else {
         var2.setAge(6000);
         var3.setAge(6000);
         var4.setAge(-24000);
         var4.moveTo(var2.getX(), var2.getY(), var2.getZ(), 0.0F, 0.0F);
         var1.addFreshEntityWithPassengers(var4);
         var1.broadcastEntityEvent(var4, (byte)12);
         return Optional.of(var4);
      }
   }

   private void giveBedToChild(ServerLevel var1, Villager var2, BlockPos var3) {
      GlobalPos var4 = GlobalPos.of(var1.dimension(), var3);
      var2.getBrain().setMemory(MemoryModuleType.HOME, (Object)var4);
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
