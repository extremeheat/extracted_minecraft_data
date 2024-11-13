package net.minecraft.world.level.block.entity.trialspawner;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

public interface PlayerDetector {
   PlayerDetector NO_CREATIVE_PLAYERS = (var0, var1, var2, var3, var5) -> var1.getPlayers(var0, (var3x) -> var3x.blockPosition().closerThan(var2, var3) && !var3x.isCreative() && !var3x.isSpectator()).stream().filter((var3x) -> !var5 || inLineOfSight(var0, var2.getCenter(), var3x.getEyePosition())).map(Entity::getUUID).toList();
   PlayerDetector INCLUDING_CREATIVE_PLAYERS = (var0, var1, var2, var3, var5) -> var1.getPlayers(var0, (var3x) -> var3x.blockPosition().closerThan(var2, var3) && !var3x.isSpectator()).stream().filter((var3x) -> !var5 || inLineOfSight(var0, var2.getCenter(), var3x.getEyePosition())).map(Entity::getUUID).toList();
   PlayerDetector SHEEP = (var0, var1, var2, var3, var5) -> {
      AABB var6 = (new AABB(var2)).inflate(var3);
      return var1.getEntities(var0, EntityType.SHEEP, var6, LivingEntity::isAlive).stream().filter((var3x) -> !var5 || inLineOfSight(var0, var2.getCenter(), var3x.getEyePosition())).map(Entity::getUUID).toList();
   };

   List<UUID> detect(ServerLevel var1, EntitySelector var2, BlockPos var3, double var4, boolean var6);

   private static boolean inLineOfSight(Level var0, Vec3 var1, Vec3 var2) {
      BlockHitResult var3 = var0.clip(new ClipContext(var2, var1, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, CollisionContext.empty()));
      return var3.getBlockPos().equals(BlockPos.containing(var1)) || var3.getType() == HitResult.Type.MISS;
   }

   public interface EntitySelector {
      EntitySelector SELECT_FROM_LEVEL = new EntitySelector() {
         public List<ServerPlayer> getPlayers(ServerLevel var1, Predicate<? super Player> var2) {
            return var1.getPlayers(var2);
         }

         public <T extends Entity> List<T> getEntities(ServerLevel var1, EntityTypeTest<Entity, T> var2, AABB var3, Predicate<? super T> var4) {
            return var1.getEntities(var2, var3, var4);
         }
      };

      List<? extends Player> getPlayers(ServerLevel var1, Predicate<? super Player> var2);

      <T extends Entity> List<T> getEntities(ServerLevel var1, EntityTypeTest<Entity, T> var2, AABB var3, Predicate<? super T> var4);

      static EntitySelector onlySelectPlayer(Player var0) {
         return onlySelectPlayers(List.of(var0));
      }

      static EntitySelector onlySelectPlayers(final List<Player> var0) {
         return new EntitySelector() {
            public List<Player> getPlayers(ServerLevel var1, Predicate<? super Player> var2) {
               return var0.stream().filter(var2).toList();
            }

            public <T extends Entity> List<T> getEntities(ServerLevel var1, EntityTypeTest<Entity, T> var2, AABB var3, Predicate<? super T> var4) {
               Stream var10000 = var0.stream();
               Objects.requireNonNull(var2);
               return var10000.map(var2::tryCast).filter(Objects::nonNull).filter(var4).toList();
            }
         };
      }
   }
}
