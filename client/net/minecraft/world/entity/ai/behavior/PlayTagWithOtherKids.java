package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

public class PlayTagWithOtherKids extends Behavior<PathfinderMob> {
   private static final int MAX_FLEE_XZ_DIST = 20;
   private static final int MAX_FLEE_Y_DIST = 8;
   private static final float FLEE_SPEED_MODIFIER = 0.6F;
   private static final float CHASE_SPEED_MODIFIER = 0.6F;
   private static final int MAX_CHASERS_PER_TARGET = 5;
   private static final int AVERAGE_WAIT_TIME_BETWEEN_RUNS = 10;

   public PlayTagWithOtherKids() {
      super(ImmutableMap.of(MemoryModuleType.VISIBLE_VILLAGER_BABIES, MemoryStatus.VALUE_PRESENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.INTERACTION_TARGET, MemoryStatus.REGISTERED));
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, PathfinderMob var2) {
      return var1.getRandom().nextInt(10) == 0 && this.hasFriendsNearby(var2);
   }

   protected void start(ServerLevel var1, PathfinderMob var2, long var3) {
      LivingEntity var5 = this.seeIfSomeoneIsChasingMe(var2);
      if (var5 != null) {
         this.fleeFromChaser(var1, var2, var5);
      } else {
         Optional var6 = this.findSomeoneBeingChased(var2);
         if (var6.isPresent()) {
            chaseKid(var2, (LivingEntity)var6.get());
         } else {
            this.findSomeoneToChase(var2).ifPresent((var1x) -> {
               chaseKid(var2, var1x);
            });
         }
      }
   }

   private void fleeFromChaser(ServerLevel var1, PathfinderMob var2, LivingEntity var3) {
      for(int var4 = 0; var4 < 10; ++var4) {
         Vec3 var5 = LandRandomPos.getPos(var2, 20, 8);
         if (var5 != null && var1.isVillage(new BlockPos(var5))) {
            var2.getBrain().setMemory(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget(var5, 0.6F, 0)));
            return;
         }
      }

   }

   private static void chaseKid(PathfinderMob var0, LivingEntity var1) {
      Brain var2 = var0.getBrain();
      var2.setMemory(MemoryModuleType.INTERACTION_TARGET, (Object)var1);
      var2.setMemory(MemoryModuleType.LOOK_TARGET, (Object)(new EntityTracker(var1, true)));
      var2.setMemory(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget(new EntityTracker(var1, false), 0.6F, 1)));
   }

   private Optional<LivingEntity> findSomeoneToChase(PathfinderMob var1) {
      return this.getFriendsNearby(var1).stream().findAny();
   }

   private Optional<LivingEntity> findSomeoneBeingChased(PathfinderMob var1) {
      Map var2 = this.checkHowManyChasersEachFriendHas(var1);
      return var2.entrySet().stream().sorted(Comparator.comparingInt(Entry::getValue)).filter((var0) -> {
         return (Integer)var0.getValue() > 0 && (Integer)var0.getValue() <= 5;
      }).map(Entry::getKey).findFirst();
   }

   private Map<LivingEntity, Integer> checkHowManyChasersEachFriendHas(PathfinderMob var1) {
      HashMap var2 = Maps.newHashMap();
      this.getFriendsNearby(var1).stream().filter(this::isChasingSomeone).forEach((var2x) -> {
         var2.compute(this.whoAreYouChasing(var2x), (var0, var1) -> {
            return var1 == null ? 1 : var1 + 1;
         });
      });
      return var2;
   }

   private List<LivingEntity> getFriendsNearby(PathfinderMob var1) {
      return (List)var1.getBrain().getMemory(MemoryModuleType.VISIBLE_VILLAGER_BABIES).get();
   }

   private LivingEntity whoAreYouChasing(LivingEntity var1) {
      return (LivingEntity)var1.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).get();
   }

   @Nullable
   private LivingEntity seeIfSomeoneIsChasingMe(LivingEntity var1) {
      return (LivingEntity)((List)var1.getBrain().getMemory(MemoryModuleType.VISIBLE_VILLAGER_BABIES).get()).stream().filter((var2) -> {
         return this.isFriendChasingMe(var1, var2);
      }).findAny().orElse((Object)null);
   }

   private boolean isChasingSomeone(LivingEntity var1) {
      return var1.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).isPresent();
   }

   private boolean isFriendChasingMe(LivingEntity var1, LivingEntity var2) {
      return var2.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).filter((var1x) -> {
         return var1x == var1;
      }).isPresent();
   }

   private boolean hasFriendsNearby(PathfinderMob var1) {
      return var1.getBrain().hasMemoryValue(MemoryModuleType.VISIBLE_VILLAGER_BABIES);
   }
}
