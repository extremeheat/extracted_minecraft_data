package net.minecraft.world.level.block.entity.trialspawner;

import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

public interface PlayerDetector {
   PlayerDetector PLAYERS = (var0, var1, var2) -> var0.getPlayers(
            var2x -> var2x.blockPosition().closerThan(var1, (double)var2) && !var2x.isCreative() && !var2x.isSpectator()
         )
         .stream()
         .map(Entity::getUUID)
         .toList();
   PlayerDetector SHEEP = (var0, var1, var2) -> {
      AABB var3 = new AABB(var1).inflate((double)var2);
      return var0.getEntities(EntityType.SHEEP, var3, LivingEntity::isAlive).stream().map(Entity::getUUID).toList();
   };

   List<UUID> detect(ServerLevel var1, BlockPos var2, int var3);
}
