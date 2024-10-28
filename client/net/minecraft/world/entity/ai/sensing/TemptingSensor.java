package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class TemptingSensor extends Sensor<PathfinderMob> {
   public static final int TEMPTATION_RANGE = 10;
   private static final TargetingConditions TEMPT_TARGETING = TargetingConditions.forNonCombat().range(10.0).ignoreLineOfSight();
   private final Predicate<ItemStack> temptations;

   public TemptingSensor(Predicate<ItemStack> var1) {
      super();
      this.temptations = var1;
   }

   protected void doTick(ServerLevel var1, PathfinderMob var2) {
      Brain var3 = var2.getBrain();
      Stream var10000 = var1.players().stream().filter(EntitySelector.NO_SPECTATORS).filter((var1x) -> {
         return TEMPT_TARGETING.test(var2, var1x);
      }).filter((var1x) -> {
         return var2.closerThan(var1x, 10.0);
      }).filter(this::playerHoldingTemptation).filter((var1x) -> {
         return !var2.hasPassenger(var1x);
      });
      Objects.requireNonNull(var2);
      List var4 = (List)var10000.sorted(Comparator.comparingDouble(var2::distanceToSqr)).collect(Collectors.toList());
      if (!var4.isEmpty()) {
         Player var5 = (Player)var4.get(0);
         var3.setMemory(MemoryModuleType.TEMPTING_PLAYER, (Object)var5);
      } else {
         var3.eraseMemory(MemoryModuleType.TEMPTING_PLAYER);
      }

   }

   private boolean playerHoldingTemptation(Player var1) {
      return this.isTemptation(var1.getMainHandItem()) || this.isTemptation(var1.getOffhandItem());
   }

   private boolean isTemptation(ItemStack var1) {
      return this.temptations.test(var1);
   }

   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.of(MemoryModuleType.TEMPTING_PLAYER);
   }
}
