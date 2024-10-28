package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.player.Player;

public class FollowTemptation extends Behavior<PathfinderMob> {
   public static final int TEMPTATION_COOLDOWN = 100;
   public static final double DEFAULT_CLOSE_ENOUGH_DIST = 2.5;
   public static final double BACKED_UP_CLOSE_ENOUGH_DIST = 3.5;
   private final Function<LivingEntity, Float> speedModifier;
   private final Function<LivingEntity, Double> closeEnoughDistance;

   public FollowTemptation(Function<LivingEntity, Float> var1) {
      this(var1, (var0) -> {
         return 2.5;
      });
   }

   public FollowTemptation(Function<LivingEntity, Float> var1, Function<LivingEntity, Double> var2) {
      super((Map)Util.make(() -> {
         ImmutableMap.Builder var0 = ImmutableMap.builder();
         var0.put(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED);
         var0.put(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED);
         var0.put(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, MemoryStatus.VALUE_ABSENT);
         var0.put(MemoryModuleType.IS_TEMPTED, MemoryStatus.REGISTERED);
         var0.put(MemoryModuleType.TEMPTING_PLAYER, MemoryStatus.VALUE_PRESENT);
         var0.put(MemoryModuleType.BREED_TARGET, MemoryStatus.VALUE_ABSENT);
         var0.put(MemoryModuleType.IS_PANICKING, MemoryStatus.VALUE_ABSENT);
         return var0.build();
      }));
      this.speedModifier = var1;
      this.closeEnoughDistance = var2;
   }

   protected float getSpeedModifier(PathfinderMob var1) {
      return (Float)this.speedModifier.apply(var1);
   }

   private Optional<Player> getTemptingPlayer(PathfinderMob var1) {
      return var1.getBrain().getMemory(MemoryModuleType.TEMPTING_PLAYER);
   }

   protected boolean timedOut(long var1) {
      return false;
   }

   protected boolean canStillUse(ServerLevel var1, PathfinderMob var2, long var3) {
      return this.getTemptingPlayer(var2).isPresent() && !var2.getBrain().hasMemoryValue(MemoryModuleType.BREED_TARGET) && !var2.getBrain().hasMemoryValue(MemoryModuleType.IS_PANICKING);
   }

   protected void start(ServerLevel var1, PathfinderMob var2, long var3) {
      var2.getBrain().setMemory(MemoryModuleType.IS_TEMPTED, (Object)true);
   }

   protected void stop(ServerLevel var1, PathfinderMob var2, long var3) {
      Brain var5 = var2.getBrain();
      var5.setMemory(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, (int)100);
      var5.setMemory(MemoryModuleType.IS_TEMPTED, (Object)false);
      var5.eraseMemory(MemoryModuleType.WALK_TARGET);
      var5.eraseMemory(MemoryModuleType.LOOK_TARGET);
   }

   protected void tick(ServerLevel var1, PathfinderMob var2, long var3) {
      Player var5 = (Player)this.getTemptingPlayer(var2).get();
      Brain var6 = var2.getBrain();
      var6.setMemory(MemoryModuleType.LOOK_TARGET, (Object)(new EntityTracker(var5, true)));
      double var7 = (Double)this.closeEnoughDistance.apply(var2);
      if (var2.distanceToSqr(var5) < Mth.square(var7)) {
         var6.eraseMemory(MemoryModuleType.WALK_TARGET);
      } else {
         var6.setMemory(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget(new EntityTracker(var5, false), this.getSpeedModifier(var2), 2)));
      }

   }

   // $FF: synthetic method
   protected void stop(final ServerLevel var1, final LivingEntity var2, final long var3) {
      this.stop(var1, (PathfinderMob)var2, var3);
   }

   // $FF: synthetic method
   protected void tick(final ServerLevel var1, final LivingEntity var2, final long var3) {
      this.tick(var1, (PathfinderMob)var2, var3);
   }

   // $FF: synthetic method
   protected void start(final ServerLevel var1, final LivingEntity var2, final long var3) {
      this.start(var1, (PathfinderMob)var2, var3);
   }
}
