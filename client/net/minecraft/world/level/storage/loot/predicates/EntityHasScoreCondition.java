package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Stream;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;

public class EntityHasScoreCondition implements LootItemCondition {
   final Map<String, IntRange> scores;
   final LootContext.EntityTarget entityTarget;

   EntityHasScoreCondition(Map<String, IntRange> var1, LootContext.EntityTarget var2) {
      super();
      this.scores = ImmutableMap.copyOf(var1);
      this.entityTarget = var2;
   }

   @Override
   public LootItemConditionType getType() {
      return LootItemConditions.ENTITY_SCORES;
   }

   @Override
   public Set<LootContextParam<?>> getReferencedContextParams() {
      return Stream.concat(Stream.of(this.entityTarget.getParam()), this.scores.values().stream().flatMap(var0 -> var0.getReferencedContextParams().stream()))
         .collect(ImmutableSet.toImmutableSet());
   }

   public boolean test(LootContext var1) {
      Entity var2 = var1.getParamOrNull(this.entityTarget.getParam());
      if (var2 == null) {
         return false;
      } else {
         Scoreboard var3 = var2.level().getScoreboard();

         for(Entry var5 : this.scores.entrySet()) {
            if (!this.hasScore(var1, var2, var3, (String)var5.getKey(), (IntRange)var5.getValue())) {
               return false;
            }
         }

         return true;
      }
   }

   protected boolean hasScore(LootContext var1, Entity var2, Scoreboard var3, String var4, IntRange var5) {
      Objective var6 = var3.getObjective(var4);
      if (var6 == null) {
         return false;
      } else {
         String var7 = var2.getScoreboardName();
         return !var3.hasPlayerScore(var7, var6) ? false : var5.test(var1, var3.getOrCreatePlayerScore(var7, var6).getScore());
      }
   }

   public static EntityHasScoreCondition.Builder hasScores(LootContext.EntityTarget var0) {
      return new EntityHasScoreCondition.Builder(var0);
   }

   public static class Builder implements LootItemCondition.Builder {
      private final Map<String, IntRange> scores = Maps.newHashMap();
      private final LootContext.EntityTarget entityTarget;

      public Builder(LootContext.EntityTarget var1) {
         super();
         this.entityTarget = var1;
      }

      public EntityHasScoreCondition.Builder withScore(String var1, IntRange var2) {
         this.scores.put(var1, var2);
         return this;
      }

      @Override
      public LootItemCondition build() {
         return new EntityHasScoreCondition(this.scores, this.entityTarget);
      }
   }

   public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<EntityHasScoreCondition> {
      public Serializer() {
         super();
      }

      public void serialize(JsonObject var1, EntityHasScoreCondition var2, JsonSerializationContext var3) {
         JsonObject var4 = new JsonObject();

         for(Entry var6 : var2.scores.entrySet()) {
            var4.add((String)var6.getKey(), var3.serialize(var6.getValue()));
         }

         var1.add("scores", var4);
         var1.add("entity", var3.serialize(var2.entityTarget));
      }

      public EntityHasScoreCondition deserialize(JsonObject var1, JsonDeserializationContext var2) {
         Set var3 = GsonHelper.getAsJsonObject(var1, "scores").entrySet();
         LinkedHashMap var4 = Maps.newLinkedHashMap();

         for(Entry var6 : var3) {
            var4.put((String)var6.getKey(), GsonHelper.convertToObject((JsonElement)var6.getValue(), "score", var2, IntRange.class));
         }

         return new EntityHasScoreCondition(var4, GsonHelper.getAsObject(var1, "entity", var2, LootContext.EntityTarget.class));
      }
   }
}
