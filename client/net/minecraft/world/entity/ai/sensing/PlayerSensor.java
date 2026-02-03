package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.player.Player;

public class PlayerSensor extends Sensor<LivingEntity> {
   public PlayerSensor() {
      super();
   }

   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.of(MemoryModuleType.NEAREST_PLAYERS, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYERS);
   }

   protected void doTick(final ServerLevel level, final LivingEntity body) {
      Stream var10000 = level.players().stream().filter(EntitySelector.NO_SPECTATORS).filter((player) -> body.closerThan(player, this.getFollowDistance(body)));
      Objects.requireNonNull(body);
      List<Player> players = (List)var10000.sorted(Comparator.comparingDouble(body::distanceToSqr)).collect(Collectors.toList());
      Brain<?> brain = body.getBrain();
      brain.setMemory(MemoryModuleType.NEAREST_PLAYERS, players);
      List<Player> visiblePlayers = (List)players.stream().filter((livingEntity) -> isEntityTargetable(level, body, livingEntity)).collect(Collectors.toList());
      brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER, visiblePlayers.isEmpty() ? null : (Player)visiblePlayers.get(0));
      List<Player> visibleAttackablePlayers = visiblePlayers.stream().filter((livingEntity) -> isEntityAttackable(level, body, livingEntity)).toList();
      brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYERS, visibleAttackablePlayers);
      brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER, visibleAttackablePlayers.isEmpty() ? null : (Player)visibleAttackablePlayers.get(0));
   }

   protected double getFollowDistance(final LivingEntity body) {
      return body.getAttributeValue(Attributes.FOLLOW_RANGE);
   }
}
