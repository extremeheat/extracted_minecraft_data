package net.minecraft.advancements.critereon;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerLevel;
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

public class PlayerPredicate implements EntitySubPredicate {
   public static final int LOOKING_AT_RANGE = 100;
   private final MinMaxBounds.Ints level;
   @Nullable
   private final GameType gameType;
   private final Map<Stat<?>, MinMaxBounds.Ints> stats;
   private final Object2BooleanMap<ResourceLocation> recipes;
   private final Map<ResourceLocation, AdvancementPredicate> advancements;
   private final EntityPredicate lookingAt;

   private static AdvancementPredicate advancementPredicateFromJson(JsonElement var0) {
      if (var0.isJsonPrimitive()) {
         boolean var3 = var0.getAsBoolean();
         return new AdvancementDonePredicate(var3);
      } else {
         Object2BooleanOpenHashMap var1 = new Object2BooleanOpenHashMap();
         JsonObject var2 = GsonHelper.convertToJsonObject(var0, "criterion data");
         var2.entrySet().forEach((var1x) -> {
            boolean var2 = GsonHelper.convertToBoolean((JsonElement)var1x.getValue(), "criterion test");
            var1.put((String)var1x.getKey(), var2);
         });
         return new AdvancementCriterionsPredicate(var1);
      }
   }

   PlayerPredicate(MinMaxBounds.Ints var1, @Nullable GameType var2, Map<Stat<?>, MinMaxBounds.Ints> var3, Object2BooleanMap<ResourceLocation> var4, Map<ResourceLocation, AdvancementPredicate> var5, EntityPredicate var6) {
      super();
      this.level = var1;
      this.gameType = var2;
      this.stats = var3;
      this.recipes = var4;
      this.advancements = var5;
      this.lookingAt = var6;
   }

   public boolean matches(Entity var1, ServerLevel var2, @Nullable Vec3 var3) {
      if (!(var1 instanceof ServerPlayer var4)) {
         return false;
      } else if (!this.level.matches(var4.experienceLevel)) {
         return false;
      } else if (this.gameType != null && this.gameType != var4.gameMode.getGameModeForPlayer()) {
         return false;
      } else {
         ServerStatsCounter var5 = var4.getStats();
         Iterator var6 = this.stats.entrySet().iterator();

         while(var6.hasNext()) {
            Map.Entry var7 = (Map.Entry)var6.next();
            int var8 = var5.getValue((Stat)var7.getKey());
            if (!((MinMaxBounds.Ints)var7.getValue()).matches(var8)) {
               return false;
            }
         }

         ServerRecipeBook var12 = var4.getRecipeBook();
         ObjectIterator var13 = this.recipes.object2BooleanEntrySet().iterator();

         while(var13.hasNext()) {
            Object2BooleanMap.Entry var16 = (Object2BooleanMap.Entry)var13.next();
            if (var12.contains((ResourceLocation)var16.getKey()) != var16.getBooleanValue()) {
               return false;
            }
         }

         if (!this.advancements.isEmpty()) {
            PlayerAdvancements var14 = var4.getAdvancements();
            ServerAdvancementManager var17 = var4.getServer().getAdvancements();
            Iterator var9 = this.advancements.entrySet().iterator();

            while(var9.hasNext()) {
               Map.Entry var10 = (Map.Entry)var9.next();
               Advancement var11 = var17.getAdvancement((ResourceLocation)var10.getKey());
               if (var11 == null || !((AdvancementPredicate)var10.getValue()).test(var14.getOrStartProgress(var11))) {
                  return false;
               }
            }
         }

         if (this.lookingAt != EntityPredicate.ANY) {
            Vec3 var15 = var4.getEyePosition();
            Vec3 var18 = var4.getViewVector(1.0F);
            Vec3 var19 = var15.add(var18.x * 100.0, var18.y * 100.0, var18.z * 100.0);
            EntityHitResult var20 = ProjectileUtil.getEntityHitResult(var4.level, var4, var15, var19, (new AABB(var15, var19)).inflate(1.0), (var0) -> {
               return !var0.isSpectator();
            }, 0.0F);
            if (var20 != null && var20.getType() == HitResult.Type.ENTITY) {
               Entity var21 = var20.getEntity();
               if (this.lookingAt.matches(var4, var21) && var4.hasLineOfSight(var21)) {
                  return true;
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return true;
         }
      }
   }

   public static PlayerPredicate fromJson(JsonObject var0) {
      MinMaxBounds.Ints var1 = MinMaxBounds.Ints.fromJson(var0.get("level"));
      String var2 = GsonHelper.getAsString(var0, "gamemode", "");
      GameType var3 = GameType.byName(var2, (GameType)null);
      HashMap var4 = Maps.newHashMap();
      JsonArray var5 = GsonHelper.getAsJsonArray(var0, "stats", (JsonArray)null);
      if (var5 != null) {
         Iterator var6 = var5.iterator();

         while(var6.hasNext()) {
            JsonElement var7 = (JsonElement)var6.next();
            JsonObject var8 = GsonHelper.convertToJsonObject(var7, "stats entry");
            ResourceLocation var9 = new ResourceLocation(GsonHelper.getAsString(var8, "type"));
            StatType var10 = (StatType)Registry.STAT_TYPE.get(var9);
            if (var10 == null) {
               throw new JsonParseException("Invalid stat type: " + var9);
            }

            ResourceLocation var11 = new ResourceLocation(GsonHelper.getAsString(var8, "stat"));
            Stat var12 = getStat(var10, var11);
            MinMaxBounds.Ints var13 = MinMaxBounds.Ints.fromJson(var8.get("value"));
            var4.put(var12, var13);
         }
      }

      Object2BooleanOpenHashMap var14 = new Object2BooleanOpenHashMap();
      JsonObject var15 = GsonHelper.getAsJsonObject(var0, "recipes", new JsonObject());
      Iterator var16 = var15.entrySet().iterator();

      while(var16.hasNext()) {
         Map.Entry var18 = (Map.Entry)var16.next();
         ResourceLocation var20 = new ResourceLocation((String)var18.getKey());
         boolean var23 = GsonHelper.convertToBoolean((JsonElement)var18.getValue(), "recipe present");
         var14.put(var20, var23);
      }

      HashMap var17 = Maps.newHashMap();
      JsonObject var19 = GsonHelper.getAsJsonObject(var0, "advancements", new JsonObject());
      Iterator var21 = var19.entrySet().iterator();

      while(var21.hasNext()) {
         Map.Entry var24 = (Map.Entry)var21.next();
         ResourceLocation var25 = new ResourceLocation((String)var24.getKey());
         AdvancementPredicate var26 = advancementPredicateFromJson((JsonElement)var24.getValue());
         var17.put(var25, var26);
      }

      EntityPredicate var22 = EntityPredicate.fromJson(var0.get("looking_at"));
      return new PlayerPredicate(var1, var3, var4, var14, var17, var22);
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

   public JsonObject serializeCustomData() {
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

   public EntitySubPredicate.Type type() {
      return EntitySubPredicate.Types.PLAYER;
   }

   private static class AdvancementDonePredicate implements AdvancementPredicate {
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

   private static class AdvancementCriterionsPredicate implements AdvancementPredicate {
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

         Object2BooleanMap.Entry var3;
         CriterionProgress var4;
         do {
            if (!var2.hasNext()) {
               return true;
            }

            var3 = (Object2BooleanMap.Entry)var2.next();
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
      private final Map<ResourceLocation, AdvancementPredicate> advancements;
      private EntityPredicate lookingAt;

      public Builder() {
         super();
         this.level = MinMaxBounds.Ints.ANY;
         this.stats = Maps.newHashMap();
         this.recipes = new Object2BooleanOpenHashMap();
         this.advancements = Maps.newHashMap();
         this.lookingAt = EntityPredicate.ANY;
      }

      public static Builder player() {
         return new Builder();
      }

      public Builder setLevel(MinMaxBounds.Ints var1) {
         this.level = var1;
         return this;
      }

      public Builder addStat(Stat<?> var1, MinMaxBounds.Ints var2) {
         this.stats.put(var1, var2);
         return this;
      }

      public Builder addRecipe(ResourceLocation var1, boolean var2) {
         this.recipes.put(var1, var2);
         return this;
      }

      public Builder setGameType(GameType var1) {
         this.gameType = var1;
         return this;
      }

      public Builder setLookingAt(EntityPredicate var1) {
         this.lookingAt = var1;
         return this;
      }

      public Builder checkAdvancementDone(ResourceLocation var1, boolean var2) {
         this.advancements.put(var1, new AdvancementDonePredicate(var2));
         return this;
      }

      public Builder checkAdvancementCriterions(ResourceLocation var1, Map<String, Boolean> var2) {
         this.advancements.put(var1, new AdvancementCriterionsPredicate(new Object2BooleanOpenHashMap(var2)));
         return this;
      }

      public PlayerPredicate build() {
         return new PlayerPredicate(this.level, this.gameType, this.stats, this.recipes, this.advancements, this.lookingAt);
      }
   }
}
