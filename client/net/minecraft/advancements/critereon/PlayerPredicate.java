package net.minecraft.advancements.critereon;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.ServerRecipeBook;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class PlayerPredicate {
   public static final PlayerPredicate ANY = (new PlayerPredicate.Builder()).build();
   public static final int LOOKING_AT_RANGE = 100;
   private final MinMaxBounds.Ints level;
   @Nullable
   private final GameType gameType;
   private final Map<Stat<?>, MinMaxBounds.Ints> stats;
   private final Object2BooleanMap<ResourceLocation> recipes;
   private final Map<ResourceLocation, PlayerPredicate.AdvancementPredicate> advancements;
   private final EntityPredicate lookingAt;

   private static PlayerPredicate.AdvancementPredicate advancementPredicateFromJson(JsonElement var0) {
      if (var0.isJsonPrimitive()) {
         boolean var3 = var0.getAsBoolean();
         return new PlayerPredicate.AdvancementDonePredicate(var3);
      } else {
         Object2BooleanOpenHashMap var1 = new Object2BooleanOpenHashMap();
         JsonObject var2 = GsonHelper.convertToJsonObject(var0, "criterion data");
         var2.entrySet().forEach((var1x) -> {
            boolean var2 = GsonHelper.convertToBoolean((JsonElement)var1x.getValue(), "criterion test");
            var1.put((String)var1x.getKey(), var2);
         });
         return new PlayerPredicate.AdvancementCriterionsPredicate(var1);
      }
   }

   PlayerPredicate(MinMaxBounds.Ints var1, @Nullable GameType var2, Map<Stat<?>, MinMaxBounds.Ints> var3, Object2BooleanMap<ResourceLocation> var4, Map<ResourceLocation, PlayerPredicate.AdvancementPredicate> var5, EntityPredicate var6) {
      super();
      this.level = var1;
      this.gameType = var2;
      this.stats = var3;
      this.recipes = var4;
      this.advancements = var5;
      this.lookingAt = var6;
   }

   public boolean matches(Entity var1) {
      if (this == ANY) {
         return true;
      } else if (!(var1 instanceof ServerPlayer)) {
         return false;
      } else {
         ServerPlayer var2 = (ServerPlayer)var1;
         if (!this.level.matches(var2.experienceLevel)) {
            return false;
         } else if (this.gameType != null && this.gameType != var2.gameMode.getGameModeForPlayer()) {
            return false;
         } else {
            ServerStatsCounter var3 = var2.getStats();
            Iterator var4 = this.stats.entrySet().iterator();

            while(var4.hasNext()) {
               Entry var5 = (Entry)var4.next();
               int var6 = var3.getValue((Stat)var5.getKey());
               if (!((MinMaxBounds.Ints)var5.getValue()).matches(var6)) {
                  return false;
               }
            }

            ServerRecipeBook var10 = var2.getRecipeBook();
            ObjectIterator var11 = this.recipes.object2BooleanEntrySet().iterator();

            while(var11.hasNext()) {
               it.unimi.dsi.fastutil.objects.Object2BooleanMap.Entry var14 = (it.unimi.dsi.fastutil.objects.Object2BooleanMap.Entry)var11.next();
               if (var10.contains((ResourceLocation)var14.getKey()) != var14.getBooleanValue()) {
                  return false;
               }
            }

            if (!this.advancements.isEmpty()) {
               PlayerAdvancements var12 = var2.getAdvancements();
               ServerAdvancementManager var15 = var2.getServer().getAdvancements();
               Iterator var7 = this.advancements.entrySet().iterator();

               while(var7.hasNext()) {
                  Entry var8 = (Entry)var7.next();
                  Advancement var9 = var15.getAdvancement((ResourceLocation)var8.getKey());
                  if (var9 == null || !((PlayerPredicate.AdvancementPredicate)var8.getValue()).test(var12.getOrStartProgress(var9))) {
                     return false;
                  }
               }
            }

            if (this.lookingAt != EntityPredicate.ANY) {
               Vec3 var13 = var2.getEyePosition();
               Vec3 var16 = var2.getViewVector(1.0F);
               Vec3 var17 = var13.add(var16.field_414 * 100.0D, var16.field_415 * 100.0D, var16.field_416 * 100.0D);
               EntityHitResult var18 = ProjectileUtil.getEntityHitResult(var2.level, var2, var13, var17, (new AABB(var13, var17)).inflate(1.0D), (var0) -> {
                  return !var0.isSpectator();
               }, 0.0F);
               if (var18 == null || var18.getType() != HitResult.Type.ENTITY) {
                  return false;
               }

               Entity var19 = var18.getEntity();
               if (!this.lookingAt.matches(var2, var19) || !var2.hasLineOfSight(var19)) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public static PlayerPredicate fromJson(@Nullable JsonElement var0) {
      if (var0 != null && !var0.isJsonNull()) {
         JsonObject var1 = GsonHelper.convertToJsonObject(var0, "player");
         MinMaxBounds.Ints var2 = MinMaxBounds.Ints.fromJson(var1.get("level"));
         String var3 = GsonHelper.getAsString(var1, "gamemode", "");
         GameType var4 = GameType.byName(var3, (GameType)null);
         HashMap var5 = Maps.newHashMap();
         JsonArray var6 = GsonHelper.getAsJsonArray(var1, "stats", (JsonArray)null);
         if (var6 != null) {
            Iterator var7 = var6.iterator();

            while(var7.hasNext()) {
               JsonElement var8 = (JsonElement)var7.next();
               JsonObject var9 = GsonHelper.convertToJsonObject(var8, "stats entry");
               ResourceLocation var10 = new ResourceLocation(GsonHelper.getAsString(var9, "type"));
               StatType var11 = (StatType)Registry.STAT_TYPE.get(var10);
               if (var11 == null) {
                  throw new JsonParseException("Invalid stat type: " + var10);
               }

               ResourceLocation var12 = new ResourceLocation(GsonHelper.getAsString(var9, "stat"));
               Stat var13 = getStat(var11, var12);
               MinMaxBounds.Ints var14 = MinMaxBounds.Ints.fromJson(var9.get("value"));
               var5.put(var13, var14);
            }
         }

         Object2BooleanOpenHashMap var15 = new Object2BooleanOpenHashMap();
         JsonObject var16 = GsonHelper.getAsJsonObject(var1, "recipes", new JsonObject());
         Iterator var17 = var16.entrySet().iterator();

         while(var17.hasNext()) {
            Entry var19 = (Entry)var17.next();
            ResourceLocation var21 = new ResourceLocation((String)var19.getKey());
            boolean var24 = GsonHelper.convertToBoolean((JsonElement)var19.getValue(), "recipe present");
            var15.put(var21, var24);
         }

         HashMap var18 = Maps.newHashMap();
         JsonObject var20 = GsonHelper.getAsJsonObject(var1, "advancements", new JsonObject());
         Iterator var22 = var20.entrySet().iterator();

         while(var22.hasNext()) {
            Entry var25 = (Entry)var22.next();
            ResourceLocation var26 = new ResourceLocation((String)var25.getKey());
            PlayerPredicate.AdvancementPredicate var27 = advancementPredicateFromJson((JsonElement)var25.getValue());
            var18.put(var26, var27);
         }

         EntityPredicate var23 = EntityPredicate.fromJson(var1.get("looking_at"));
         return new PlayerPredicate(var2, var4, var5, var15, var18, var23);
      } else {
         return ANY;
      }
   }

   private static <T> Stat<T> getStat(StatType<T> var0, ResourceLocation var1) {
      Registry var2 = var0.getRegistry();
      Object var3 = var2.get(var1);
      if (var3 == null) {
         throw new JsonParseException("Unknown object " + var1 + " for stat type " + Registry.STAT_TYPE.getKey(var0));
      } else {
         return var0.get(var3);
      }
   }

   private static <T> ResourceLocation getStatValueId(Stat<T> var0) {
      return var0.getType().getRegistry().getKey(var0.getValue());
   }

   public JsonElement serializeToJson() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject var1 = new JsonObject();
         var1.add("level", this.level.serializeToJson());
         if (this.gameType != null) {
            var1.addProperty("gamemode", this.gameType.getName());
         }

         if (!this.stats.isEmpty()) {
            JsonArray var2 = new JsonArray();
            this.stats.forEach((var1x, var2x) -> {
               JsonObject var3 = new JsonObject();
               var3.addProperty("type", Registry.STAT_TYPE.getKey(var1x.getType()).toString());
               var3.addProperty("stat", getStatValueId(var1x).toString());
               var3.add("value", var2x.serializeToJson());
               var2.add(var3);
            });
            var1.add("stats", var2);
         }

         JsonObject var3;
         if (!this.recipes.isEmpty()) {
            var3 = new JsonObject();
            this.recipes.forEach((var1x, var2x) -> {
               var3.addProperty(var1x.toString(), var2x);
            });
            var1.add("recipes", var3);
         }

         if (!this.advancements.isEmpty()) {
            var3 = new JsonObject();
            this.advancements.forEach((var1x, var2x) -> {
               var3.add(var1x.toString(), var2x.toJson());
            });
            var1.add("advancements", var3);
         }

         var1.add("looking_at", this.lookingAt.serializeToJson());
         return var1;
      }
   }

   private static class AdvancementDonePredicate implements PlayerPredicate.AdvancementPredicate {
      private final boolean state;

      public AdvancementDonePredicate(boolean var1) {
         super();
         this.state = var1;
      }

      public JsonElement toJson() {
         return new JsonPrimitive(this.state);
      }

      public boolean test(AdvancementProgress var1) {
         return var1.isDone() == this.state;
      }

      // $FF: synthetic method
      public boolean test(Object var1) {
         return this.test((AdvancementProgress)var1);
      }
   }

   private static class AdvancementCriterionsPredicate implements PlayerPredicate.AdvancementPredicate {
      private final Object2BooleanMap<String> criterions;

      public AdvancementCriterionsPredicate(Object2BooleanMap<String> var1) {
         super();
         this.criterions = var1;
      }

      public JsonElement toJson() {
         JsonObject var1 = new JsonObject();
         Object2BooleanMap var10000 = this.criterions;
         Objects.requireNonNull(var1);
         var10000.forEach(var1::addProperty);
         return var1;
      }

      public boolean test(AdvancementProgress var1) {
         ObjectIterator var2 = this.criterions.object2BooleanEntrySet().iterator();

         it.unimi.dsi.fastutil.objects.Object2BooleanMap.Entry var3;
         CriterionProgress var4;
         do {
            if (!var2.hasNext()) {
               return true;
            }

            var3 = (it.unimi.dsi.fastutil.objects.Object2BooleanMap.Entry)var2.next();
            var4 = var1.getCriterion((String)var3.getKey());
         } while(var4 != null && var4.isDone() == var3.getBooleanValue());

         return false;
      }

      // $FF: synthetic method
      public boolean test(Object var1) {
         return this.test((AdvancementProgress)var1);
      }
   }

   private interface AdvancementPredicate extends Predicate<AdvancementProgress> {
      JsonElement toJson();
   }

   public static class Builder {
      private MinMaxBounds.Ints level;
      @Nullable
      private GameType gameType;
      private final Map<Stat<?>, MinMaxBounds.Ints> stats;
      private final Object2BooleanMap<ResourceLocation> recipes;
      private final Map<ResourceLocation, PlayerPredicate.AdvancementPredicate> advancements;
      private EntityPredicate lookingAt;

      public Builder() {
         super();
         this.level = MinMaxBounds.Ints.ANY;
         this.stats = Maps.newHashMap();
         this.recipes = new Object2BooleanOpenHashMap();
         this.advancements = Maps.newHashMap();
         this.lookingAt = EntityPredicate.ANY;
      }

      public static PlayerPredicate.Builder player() {
         return new PlayerPredicate.Builder();
      }

      public PlayerPredicate.Builder setLevel(MinMaxBounds.Ints var1) {
         this.level = var1;
         return this;
      }

      public PlayerPredicate.Builder addStat(Stat<?> var1, MinMaxBounds.Ints var2) {
         this.stats.put(var1, var2);
         return this;
      }

      public PlayerPredicate.Builder addRecipe(ResourceLocation var1, boolean var2) {
         this.recipes.put(var1, var2);
         return this;
      }

      public PlayerPredicate.Builder setGameType(GameType var1) {
         this.gameType = var1;
         return this;
      }

      public PlayerPredicate.Builder setLookingAt(EntityPredicate var1) {
         this.lookingAt = var1;
         return this;
      }

      public PlayerPredicate.Builder checkAdvancementDone(ResourceLocation var1, boolean var2) {
         this.advancements.put(var1, new PlayerPredicate.AdvancementDonePredicate(var2));
         return this;
      }

      public PlayerPredicate.Builder checkAdvancementCriterions(ResourceLocation var1, Map<String, Boolean> var2) {
         this.advancements.put(var1, new PlayerPredicate.AdvancementCriterionsPredicate(new Object2BooleanOpenHashMap(var2)));
         return this;
      }

      public PlayerPredicate build() {
         return new PlayerPredicate(this.level, this.gameType, this.stats, this.recipes, this.advancements, this.lookingAt);
      }
   }
}
