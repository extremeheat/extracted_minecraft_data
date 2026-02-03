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

   public TemptingSensor(final Predicate<ItemStack> tt) {
      this((BiPredicate)((m, i) -> tt.test(i)));
   }

   public static TemptingSensor forAnimal() {
      return new TemptingSensor((m, i) -> {
         if (m instanceof Animal animal) {
            return animal.isFood(i);
         } else {
            return false;
         }
      });
   }

   private TemptingSensor(final BiPredicate<PathfinderMob, ItemStack> temptations) {
      super();
      this.temptations = temptations;
   }

   protected void doTick(final ServerLevel level, final PathfinderMob body) {
      Brain<?> brain = body.getBrain();
      TargetingConditions targeting = TEMPT_TARGETING.copy().range((double)((float)body.getAttributeValue(Attributes.TEMPT_RANGE)));
      Stream var10000 = level.players().stream().filter(EntitySelector.NO_SPECTATORS).filter((playerx) -> targeting.test(level, body, playerx)).filter((p) -> this.playerHoldingTemptation(body, p)).filter((playerx) -> !body.hasPassenger(playerx));
      Objects.requireNonNull(body);
      List<Player> players = (List)var10000.sorted(Comparator.comparingDouble(body::distanceToSqr)).collect(Collectors.toList());
      if (!players.isEmpty()) {
         Player player = (Player)players.get(0);
         brain.setMemory(MemoryModuleType.TEMPTING_PLAYER, player);
      } else {
         brain.eraseMemory(MemoryModuleType.TEMPTING_PLAYER);
      }

   }

   private boolean playerHoldingTemptation(final PathfinderMob mob, final Player player) {
      return this.isTemptation(mob, player.getMainHandItem()) || this.isTemptation(mob, player.getOffhandItem());
   }

   private boolean isTemptation(final PathfinderMob mob, final ItemStack itemStack) {
      return this.temptations.test(mob, itemStack);
   }

   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.of(MemoryModuleType.TEMPTING_PLAYER);
   }
}
