package net.minecraft.commands.arguments.selector;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlagSet;
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

      public Class<? extends Entity> getBaseClass() {
         return Entity.class;
      }
   };
   private final int maxResults;
   private final boolean includesEntities;
   private final boolean worldLimited;
   private final List<Predicate<Entity>> contextFreePredicates;
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

   public EntitySelector(int var1, boolean var2, boolean var3, List<Predicate<Entity>> var4, MinMaxBounds.Doubles var5, Function<Vec3, Vec3> var6, @Nullable AABB var7, BiConsumer<Vec3, List<? extends Entity>> var8, boolean var9, @Nullable String var10, @Nullable UUID var11, @Nullable EntityType<?> var12, boolean var13) {
      super();
      this.maxResults = var1;
      this.includesEntities = var2;
      this.worldLimited = var3;
      this.contextFreePredicates = var4;
      this.range = var5;
      this.position = var6;
      this.aabb = var7;
      this.order = var8;
      this.currentEntity = var9;
      this.playerName = var10;
      this.entityUUID = var11;
      this.type = (EntityTypeTest)(var12 == null ? ANY_TYPE : var12);
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
      this.checkPermissions(var1);
      if (!this.includesEntities) {
         return this.findPlayers(var1);
      } else if (this.playerName != null) {
         ServerPlayer var9 = var1.getServer().getPlayerList().getPlayerByName(this.playerName);
         return var9 == null ? List.of() : List.of(var9);
      } else if (this.entityUUID != null) {
         Iterator var8 = var1.getServer().getAllLevels().iterator();

         while(var8.hasNext()) {
            ServerLevel var10 = (ServerLevel)var8.next();
            Entity var11 = var10.getEntity(this.entityUUID);
            if (var11 != null) {
               if (var11.getType().isEnabled(var1.enabledFeatures())) {
                  return List.of(var11);
               }
               break;
            }
         }

         return List.of();
      } else {
         Vec3 var2 = (Vec3)this.position.apply(var1.getPosition());
         AABB var3 = this.getAbsoluteAabb(var2);
         Predicate var4;
         if (this.currentEntity) {
            var4 = this.getPredicate(var2, var3, (FeatureFlagSet)null);
            return var1.getEntity() != null && var4.test(var1.getEntity()) ? List.of(var1.getEntity()) : List.of();
         } else {
            var4 = this.getPredicate(var2, var3, var1.enabledFeatures());
            ObjectArrayList var5 = new ObjectArrayList();
            if (this.isWorldLimited()) {
               this.addEntities(var5, var1.getLevel(), var3, var4);
            } else {
               Iterator var6 = var1.getServer().getAllLevels().iterator();

               while(var6.hasNext()) {
                  ServerLevel var7 = (ServerLevel)var6.next();
                  this.addEntities(var5, var7, var3, var4);
               }
            }

            return this.sortAndLimit(var2, var5);
         }
      }
   }

   private void addEntities(List<Entity> var1, ServerLevel var2, @Nullable AABB var3, Predicate<Entity> var4) {
      int var5 = this.getResultLimit();
      if (var1.size() < var5) {
         if (var3 != null) {
            var2.getEntities(this.type, var3, var4, var1, var5);
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
      ServerPlayer var9;
      if (this.playerName != null) {
         var9 = var1.getServer().getPlayerList().getPlayerByName(this.playerName);
         return var9 == null ? List.of() : List.of(var9);
      } else if (this.entityUUID != null) {
         var9 = var1.getServer().getPlayerList().getPlayer(this.entityUUID);
         return var9 == null ? List.of() : List.of(var9);
      } else {
         Vec3 var2 = (Vec3)this.position.apply(var1.getPosition());
         AABB var3 = this.getAbsoluteAabb(var2);
         Predicate var4 = this.getPredicate(var2, var3, (FeatureFlagSet)null);
         if (this.currentEntity) {
            Entity var11 = var1.getEntity();
            if (var11 instanceof ServerPlayer) {
               ServerPlayer var10 = (ServerPlayer)var11;
               if (var4.test(var10)) {
                  return List.of(var10);
               }
            }

            return List.of();
         } else {
            int var6 = this.getResultLimit();
            Object var5;
            if (this.isWorldLimited()) {
               var5 = var1.getLevel().getPlayers(var4, var6);
            } else {
               var5 = new ObjectArrayList();
               Iterator var7 = var1.getServer().getPlayerList().getPlayers().iterator();

               while(var7.hasNext()) {
                  ServerPlayer var8 = (ServerPlayer)var7.next();
                  if (var4.test(var8)) {
                     ((List)var5).add(var8);
                     if (((List)var5).size() >= var6) {
                        return (List)var5;
                     }
                  }
               }
            }

            return this.sortAndLimit(var2, (List)var5);
         }
      }
   }

   @Nullable
   private AABB getAbsoluteAabb(Vec3 var1) {
      return this.aabb != null ? this.aabb.move(var1) : null;
   }

   private Predicate<Entity> getPredicate(Vec3 var1, @Nullable AABB var2, @Nullable FeatureFlagSet var3) {
      boolean var4 = var3 != null;
      boolean var5 = var2 != null;
      boolean var6 = !this.range.isAny();
      int var7 = (var4 ? 1 : 0) + (var5 ? 1 : 0) + (var6 ? 1 : 0);
      Object var8;
      if (var7 == 0) {
         var8 = this.contextFreePredicates;
      } else {
         ObjectArrayList var9 = new ObjectArrayList(this.contextFreePredicates.size() + var7);
         var9.addAll(this.contextFreePredicates);
         if (var4) {
            var9.add((var1x) -> {
               return var1x.getType().isEnabled(var3);
            });
         }

         if (var5) {
            var9.add((var1x) -> {
               return var2.intersects(var1x.getBoundingBox());
            });
         }

         if (var6) {
            var9.add((var2x) -> {
               return this.range.matchesSqr(var2x.distanceToSqr(var1));
            });
         }

         var8 = var9;
      }

      return Util.allOf((List)var8);
   }

   private <T extends Entity> List<T> sortAndLimit(Vec3 var1, List<T> var2) {
      if (var2.size() > 1) {
         this.order.accept(var1, var2);
      }

      return var2.subList(0, Math.min(this.maxResults, var2.size()));
   }

   public static Component joinNames(List<? extends Entity> var0) {
      return ComponentUtils.formatList(var0, (Function)(Entity::getDisplayName));
   }
}
