package net.minecraft.world.level.block.entity.trialspawner;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;

public interface PlayerDetector {
   PlayerDetector NO_CREATIVE_PLAYERS = (var0, var1, var2, var3) -> var1.getPlayers(
            var0, var3x -> var3x.blockPosition().closerThan(var2, var3) && !var3x.isCreative() && !var3x.isSpectator()
         )
         .stream()
         .map(Entity::getUUID)
         .toList();
   PlayerDetector INCLUDING_CREATIVE_PLAYERS = (var0, var1, var2, var3) -> var1.getPlayers(
            var0, var3x -> var3x.blockPosition().closerThan(var2, var3) && !var3x.isSpectator()
         )
         .stream()
         .map(Entity::getUUID)
         .toList();
   PlayerDetector SHEEP = (var0, var1, var2, var3) -> {
      AABB var5 = new AABB(var2).inflate(var3);
      return var1.<Sheep>getEntities(var0, EntityType.SHEEP, var5, LivingEntity::isAlive).stream().map(Entity::getUUID).toList();
   };

   List<UUID> detect(ServerLevel var1, PlayerDetector.EntitySelector var2, BlockPos var3, double var4);

   public interface EntitySelector {
      PlayerDetector.EntitySelector SELECT_FROM_LEVEL = new PlayerDetector.EntitySelector() {
         @Override
         public List<ServerPlayer> getPlayers(ServerLevel var1, Predicate<? super Player> var2) {
            return var1.getPlayers(var2);
         }

         @Override
         public <T extends Entity> List<T> getEntities(ServerLevel var1, EntityTypeTest<Entity, T> var2, AABB var3, Predicate<? super T> var4) {
            return var1.getEntities(var2, var3, var4);
         }
      };

      List<? extends Player> getPlayers(ServerLevel var1, Predicate<? super Player> var2);

      <T extends Entity> List<T> getEntities(ServerLevel var1, EntityTypeTest<Entity, T> var2, AABB var3, Predicate<? super T> var4);

      static PlayerDetector.EntitySelector onlySelectPlayer(Player var0) {
         return onlySelectPlayers(List.of(var0));
      }

      static PlayerDetector.EntitySelector onlySelectPlayers(final List<Player> var0) {
         return new PlayerDetector.EntitySelector() {
            @Override
            public List<Player> getPlayers(ServerLevel var1, Predicate<? super Player> var2) {
               return var0.stream().filter(var2).toList();
            }

            @Override
            public <T extends Entity> List<T> getEntities(ServerLevel var1, EntityTypeTest<Entity, T> var2, AABB var3, Predicate<? super T> var4) {
               return var0.stream().map(var2::tryCast).filter(Objects::nonNull).filter(var4).toList();
            }
         };
      }
   }
}
