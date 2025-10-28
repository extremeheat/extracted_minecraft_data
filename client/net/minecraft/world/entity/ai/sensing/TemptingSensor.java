package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class TemptingSensor extends Sensor<PathfinderMob> {
   private static final TargetingConditions TEMPT_TARGETING = TargetingConditions.forNonCombat().ignoreLineOfSight();
   private final BiPredicate<PathfinderMob, ItemStack> temptations;

   public TemptingSensor(Predicate<ItemStack> var1) {
      this((BiPredicate)((var1x, var2) -> var1.test(var2)));
   }

   public static TemptingSensor forAnimal() {
      return new TemptingSensor((var0, var1) -> {
         if (var0 instanceof Animal var2) {
            return var2.isFood(var1);
         } else {
            return false;
         }
      });
   }

   private TemptingSensor(BiPredicate<PathfinderMob, ItemStack> var1) {
      super();
      this.temptations = var1;
   }

   protected void doTick(ServerLevel var1, PathfinderMob var2) {
      Brain var3 = var2.getBrain();
      TargetingConditions var4 = TEMPT_TARGETING.copy().range((double)((float)var2.getAttributeValue(Attributes.TEMPT_RANGE)));
      Stream var10000 = var1.players().stream().filter(EntitySelector.NO_SPECTATORS).filter((var3x) -> var4.test(var1, var2, var3x)).filter((var2x) -> this.playerHoldingTemptation(var2, var2x)).filter((var1x) -> !var2.hasPassenger(var1x));
      Objects.requireNonNull(var2);
      List var5 = (List)var10000.sorted(Comparator.comparingDouble(var2::distanceToSqr)).collect(Collectors.toList());
      if (!var5.isEmpty()) {
         Player var6 = (Player)var5.get(0);
         var3.setMemory(MemoryModuleType.TEMPTING_PLAYER, var6);
      } else {
         var3.eraseMemory(MemoryModuleType.TEMPTING_PLAYER);
      }

   }

   private boolean playerHoldingTemptation(PathfinderMob var1, Player var2) {
      return this.isTemptation(var1, var2.getMainHandItem()) || this.isTemptation(var1, var2.getOffhandItem());
   }

   private boolean isTemptation(PathfinderMob var1, ItemStack var2) {
      return this.temptations.test(var1, var2);
   }

   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.of(MemoryModuleType.TEMPTING_PLAYER);
   }
}
