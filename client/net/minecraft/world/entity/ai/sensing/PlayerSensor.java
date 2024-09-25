package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
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

   @Override
   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.of(MemoryModuleType.NEAREST_PLAYERS, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER);
   }

   @Override
   protected void doTick(ServerLevel var1, LivingEntity var2) {
      List var3 = var1.players()
         .stream()
         .filter(EntitySelector.NO_SPECTATORS)
         .filter(var2x -> var2.closerThan(var2x, this.getFollowDistance(var2)))
         .sorted(Comparator.comparingDouble(var2::distanceToSqr))
         .collect(Collectors.toList());
      Brain var4 = var2.getBrain();
      var4.setMemory(MemoryModuleType.NEAREST_PLAYERS, var3);
      List var5 = var3.stream().filter(var2x -> isEntityTargetable(var1, var2, var2x)).collect(Collectors.toList());
      var4.setMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER, var5.isEmpty() ? null : (Player)var5.get(0));
      Optional var6 = var5.stream().filter(var2x -> isEntityAttackable(var1, var2, var2x)).findFirst();
      var4.setMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER, var6);
   }

   protected double getFollowDistance(LivingEntity var1) {
      return var1.getAttributeValue(Attributes.FOLLOW_RANGE);
   }
}
