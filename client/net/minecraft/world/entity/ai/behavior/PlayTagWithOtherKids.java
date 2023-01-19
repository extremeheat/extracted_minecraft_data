package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.Maps;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

public class PlayTagWithOtherKids {
   private static final int MAX_FLEE_XZ_DIST = 20;
   private static final int MAX_FLEE_Y_DIST = 8;
   private static final float FLEE_SPEED_MODIFIER = 0.6F;
   private static final float CHASE_SPEED_MODIFIER = 0.6F;
   private static final int MAX_CHASERS_PER_TARGET = 5;
   private static final int AVERAGE_WAIT_TIME_BETWEEN_RUNS = 10;

   public PlayTagWithOtherKids() {
      super();
   }

   public static BehaviorControl<PathfinderMob> create() {
      return BehaviorBuilder.create(
         var0 -> var0.group(
                  var0.present(MemoryModuleType.VISIBLE_VILLAGER_BABIES),
                  var0.absent(MemoryModuleType.WALK_TARGET),
                  var0.registered(MemoryModuleType.LOOK_TARGET),
                  var0.registered(MemoryModuleType.INTERACTION_TARGET)
               )
               .apply(var0, (var1, var2, var3, var4) -> (var5, var6, var7) -> {
                     if (var5.getRandom().nextInt(10) != 0) {
                        return false;
                     } else {
                        List var9 = var0.get(var1);
                        Optional var10 = var9.stream().filter(var1xx -> isFriendChasingMe(var6, var1xx)).findAny();
                        if (!var10.isPresent()) {
                           Optional var13 = findSomeoneBeingChased(var9);
                           if (var13.isPresent()) {
                              chaseKid(var4, var3, var2, (LivingEntity)var13.get());
                              return true;
                           } else {
                              var9.stream().findAny().ifPresent(var3xx -> chaseKid(var4, var3, var2, var3xx));
                              return true;
                           }
                        } else {
                           for(int var11 = 0; var11 < 10; ++var11) {
                              Vec3 var12 = LandRandomPos.getPos(var6, 20, 8);
                              if (var12 != null && var5.isVillage(new BlockPos(var12))) {
                                 var2.set(new WalkTarget(var12, 0.6F, 0));
                                 break;
                              }
                           }
      
                           return true;
                        }
                     }
                  })
      );
   }

   private static void chaseKid(
      MemoryAccessor<?, LivingEntity> var0, MemoryAccessor<?, PositionTracker> var1, MemoryAccessor<?, WalkTarget> var2, LivingEntity var3
   ) {
      var0.set(var3);
      var1.set(new EntityTracker(var3, true));
      var2.set(new WalkTarget(new EntityTracker(var3, false), 0.6F, 1));
   }

   private static Optional<LivingEntity> findSomeoneBeingChased(List<LivingEntity> var0) {
      Map var1 = checkHowManyChasersEachFriendHas(var0);
      return var1.entrySet()
         .stream()
         .sorted(Comparator.comparingInt(Entry::getValue))
         .filter(var0x -> var0x.getValue() > 0 && var0x.getValue() <= 5)
         .map(Entry::getKey)
         .findFirst();
   }

   private static Map<LivingEntity, Integer> checkHowManyChasersEachFriendHas(List<LivingEntity> var0) {
      HashMap var1 = Maps.newHashMap();
      var0.stream()
         .filter(PlayTagWithOtherKids::isChasingSomeone)
         .forEach(var1x -> var1.compute(whoAreYouChasing(var1x), (var0xx, var1xx) -> var1xx == null ? 1 : var1xx + 1));
      return var1;
   }

   private static LivingEntity whoAreYouChasing(LivingEntity var0) {
      return var0.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).get();
   }

   private static boolean isChasingSomeone(LivingEntity var0) {
      return var0.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).isPresent();
   }

   private static boolean isFriendChasingMe(LivingEntity var0, LivingEntity var1) {
      return var1.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).filter(var1x -> var1x == var0).isPresent();
   }
}
