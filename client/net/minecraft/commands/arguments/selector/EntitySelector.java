package net.minecraft.commands.arguments.selector;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class EntitySelector {
   public static final int INFINITE = 2147483647;
   public static final BiConsumer<Vec3, List<? extends Entity>> ORDER_ARBITRARY = (var0, var1) -> {
   };
   private static final EntityTypeTest<Entity, ?> ANY_TYPE = new EntityTypeTest<Entity, Entity>() {
      public Entity tryCast(Entity var1) {
         return var1;
      }

      @Override
      public Class<? extends Entity> getBaseClass() {
         return Entity.class;
      }
   };
   private final int maxResults;
   private final boolean includesEntities;
   private final boolean worldLimited;
   private final Predicate<Entity> predicate;
   private final MinMaxBounds.Doubles range;
   private final Function<Vec3, Vec3> position;
   @Nullable
   private final AABB aabb;
   private final BiConsumer<Vec3, List<? extends Entity>> order;
   private final boolean currentEntity;
   @Nullable
   private final String playerName;
   @Nullable
   private final UUID entityUUID;
   private final EntityTypeTest<Entity, ?> type;
   private final boolean usesSelector;

   public EntitySelector(
      int var1,
      boolean var2,
      boolean var3,
      Predicate<Entity> var4,
      MinMaxBounds.Doubles var5,
      Function<Vec3, Vec3> var6,
      @Nullable AABB var7,
      BiConsumer<Vec3, List<? extends Entity>> var8,
      boolean var9,
      @Nullable String var10,
      @Nullable UUID var11,
      @Nullable EntityType<?> var12,
      boolean var13
   ) {
      super();
      this.maxResults = var1;
      this.includesEntities = var2;
      this.worldLimited = var3;
      this.predicate = var4;
      this.range = var5;
      this.position = var6;
      this.aabb = var7;
      this.order = var8;
      this.currentEntity = var9;
      this.playerName = var10;
      this.entityUUID = var11;
      this.type = (EntityTypeTest<Entity, ?>)(var12 == null ? ANY_TYPE : var12);
      this.usesSelector = var13;
   }

   public int getMaxResults() {
      return this.maxResults;
   }

   public boolean includesEntities() {
      return this.includesEntities;
   }

   public boolean isSelfSelector() {
      return this.currentEntity;
   }

   public boolean isWorldLimited() {
      return this.worldLimited;
   }

   public boolean usesSelector() {
      return this.usesSelector;
   }

   private void checkPermissions(CommandSourceStack var1) throws CommandSyntaxException {
      if (this.usesSelector && !var1.hasPermission(2)) {
         throw EntityArgument.ERROR_SELECTORS_NOT_ALLOWED.create();
      }
   }

   public Entity findSingleEntity(CommandSourceStack var1) throws CommandSyntaxException {
      this.checkPermissions(var1);
      List var2 = this.findEntities(var1);
      if (var2.isEmpty()) {
         throw EntityArgument.NO_ENTITIES_FOUND.create();
      } else if (var2.size() > 1) {
         throw EntityArgument.ERROR_NOT_SINGLE_ENTITY.create();
      } else {
         return (Entity)var2.get(0);
      }
   }

   public List<? extends Entity> findEntities(CommandSourceStack var1) throws CommandSyntaxException {
      return this.findEntitiesRaw(var1).stream().filter(var1x -> var1x.getType().isEnabled(var1.enabledFeatures())).toList();
   }

   private List<? extends Entity> findEntitiesRaw(CommandSourceStack var1) throws CommandSyntaxException {
      this.checkPermissions(var1);
      if (!this.includesEntities) {
         return this.findPlayers(var1);
      } else if (this.playerName != null) {
         ServerPlayer var8 = var1.getServer().getPlayerList().getPlayerByName(this.playerName);
         return (List<? extends Entity>)(var8 == null ? Collections.emptyList() : Lists.newArrayList(new ServerPlayer[]{var8}));
      } else if (this.entityUUID != null) {
         for(ServerLevel var9 : var1.getServer().getAllLevels()) {
            Entity var10 = var9.getEntity(this.entityUUID);
            if (var10 != null) {
               return Lists.newArrayList(new Entity[]{var10});
            }
         }

         return Collections.emptyList();
      } else {
         Vec3 var2 = this.position.apply(var1.getPosition());
         Predicate var3 = this.getPredicate(var2);
         if (this.currentEntity) {
            return (List<? extends Entity>)(var1.getEntity() != null && var3.test(var1.getEntity())
               ? Lists.newArrayList(new Entity[]{var1.getEntity()})
               : Collections.emptyList());
         } else {
            ArrayList var4 = Lists.newArrayList();
            if (this.isWorldLimited()) {
               this.addEntities(var4, var1.getLevel(), var2, var3);
            } else {
               for(ServerLevel var6 : var1.getServer().getAllLevels()) {
                  this.addEntities(var4, var6, var2, var3);
               }
            }

            return this.sortAndLimit(var2, var4);
         }
      }
   }

   private void addEntities(List<Entity> var1, ServerLevel var2, Vec3 var3, Predicate<Entity> var4) {
      int var5 = this.getResultLimit();
      if (var1.size() < var5) {
         if (this.aabb != null) {
            var2.getEntities(this.type, this.aabb.move(var3), var4, var1, var5);
         } else {
            var2.getEntities(this.type, var4, var1, var5);
         }
      }
   }

   private int getResultLimit() {
      return this.order == ORDER_ARBITRARY ? this.maxResults : 2147483647;
   }

   public ServerPlayer findSinglePlayer(CommandSourceStack var1) throws CommandSyntaxException {
      this.checkPermissions(var1);
      List var2 = this.findPlayers(var1);
      if (var2.size() != 1) {
         throw EntityArgument.NO_PLAYERS_FOUND.create();
      } else {
         return (ServerPlayer)var2.get(0);
      }
   }

   public List<ServerPlayer> findPlayers(CommandSourceStack var1) throws CommandSyntaxException {
      this.checkPermissions(var1);
      if (this.playerName != null) {
         ServerPlayer var9 = var1.getServer().getPlayerList().getPlayerByName(this.playerName);
         return (List<ServerPlayer>)(var9 == null ? Collections.emptyList() : Lists.newArrayList(new ServerPlayer[]{var9}));
      } else if (this.entityUUID != null) {
         ServerPlayer var8 = var1.getServer().getPlayerList().getPlayer(this.entityUUID);
         return (List<ServerPlayer>)(var8 == null ? Collections.emptyList() : Lists.newArrayList(new ServerPlayer[]{var8}));
      } else {
         Vec3 var2 = this.position.apply(var1.getPosition());
         Predicate var3 = this.getPredicate(var2);
         if (this.currentEntity) {
            if (var1.getEntity() instanceof ServerPlayer var10 && var3.test(var10)) {
               return Lists.newArrayList(new ServerPlayer[]{(ServerPlayer)var10});
            }

            return Collections.emptyList();
         } else {
            int var5 = this.getResultLimit();
            Object var4;
            if (this.isWorldLimited()) {
               var4 = var1.getLevel().getPlayers(var3, var5);
            } else {
               var4 = Lists.newArrayList();

               for(ServerPlayer var7 : var1.getServer().getPlayerList().getPlayers()) {
                  if (var3.test(var7)) {
                     var4.add(var7);
                     if (var4.size() >= var5) {
                        return (List<ServerPlayer>)var4;
                     }
                  }
               }
            }

            return this.sortAndLimit(var2, (List<ServerPlayer>)var4);
         }
      }
   }

   private Predicate<Entity> getPredicate(Vec3 var1) {
      Predicate var2 = this.predicate;
      if (this.aabb != null) {
         AABB var3 = this.aabb.move(var1);
         var2 = var2.and(var1x -> var3.intersects(var1x.getBoundingBox()));
      }

      if (!this.range.isAny()) {
         var2 = var2.and(var2x -> this.range.matchesSqr(var2x.distanceToSqr(var1)));
      }

      return var2;
   }

   private <T extends Entity> List<T> sortAndLimit(Vec3 var1, List<T> var2) {
      if (var2.size() > 1) {
         this.order.accept(var1, var2);
      }

      return var2.subList(0, Math.min(this.maxResults, var2.size()));
   }

   public static Component joinNames(List<? extends Entity> var0) {
      return ComponentUtils.formatList(var0, Entity::getDisplayName);
   }
}
