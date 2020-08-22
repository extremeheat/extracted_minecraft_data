package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.RandomValueBounds;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;

public class EntityHasScoreCondition implements LootItemCondition {
   private final Map scores;
   private final LootContext.EntityTarget entityTarget;

   private EntityHasScoreCondition(Map var1, LootContext.EntityTarget var2) {
      this.scores = ImmutableMap.copyOf(var1);
      this.entityTarget = var2;
   }

   public Set getReferencedContextParams() {
      return ImmutableSet.of(this.entityTarget.getParam());
   }

   public boolean test(LootContext var1) {
      Entity var2 = (Entity)var1.getParamOrNull(this.entityTarget.getParam());
      if (var2 == null) {
         return false;
      } else {
         Scoreboard var3 = var2.level.getScoreboard();
         Iterator var4 = this.scores.entrySet().iterator();

         Entry var5;
         do {
            if (!var4.hasNext()) {
               return true;
            }

            var5 = (Entry)var4.next();
         } while(this.hasScore(var2, var3, (String)var5.getKey(), (RandomValueBounds)var5.getValue()));

         return false;
      }
   }

   protected boolean hasScore(Entity var1, Scoreboard var2, String var3, RandomValueBounds var4) {
      Objective var5 = var2.getObjective(var3);
      if (var5 == null) {
         return false;
      } else {
         String var6 = var1.getScoreboardName();
         return !var2.hasPlayerScore(var6, var5) ? false : var4.matchesValue(var2.getOrCreatePlayerScore(var6, var5).getScore());
      }
   }

   // $FF: synthetic method
   public boolean test(Object var1) {
      return this.test((LootContext)var1);
   }

   // $FF: synthetic method
   EntityHasScoreCondition(Map var1, LootContext.EntityTarget var2, Object var3) {
      this(var1, var2);
   }

   public static class Serializer extends LootItemCondition.Serializer {
      protected Serializer() {
         super(new ResourceLocation("entity_scores"), EntityHasScoreCondition.class);
      }

      public void serialize(JsonObject var1, EntityHasScoreCondition var2, JsonSerializationContext var3) {
         JsonObject var4 = new JsonObject();
         Iterator var5 = var2.scores.entrySet().iterator();

         while(var5.hasNext()) {
            Entry var6 = (Entry)var5.next();
            var4.add((String)var6.getKey(), var3.serialize(var6.getValue()));
         }

         var1.add("scores", var4);
         var1.add("entity", var3.serialize(var2.entityTarget));
      }

      public EntityHasScoreCondition deserialize(JsonObject var1, JsonDeserializationContext var2) {
         Set var3 = GsonHelper.getAsJsonObject(var1, "scores").entrySet();
         LinkedHashMap var4 = Maps.newLinkedHashMap();
         Iterator var5 = var3.iterator();

         while(var5.hasNext()) {
            Entry var6 = (Entry)var5.next();
            var4.put(var6.getKey(), GsonHelper.convertToObject((JsonElement)var6.getValue(), "score", var2, RandomValueBounds.class));
         }

         return new EntityHasScoreCondition(var4, (LootContext.EntityTarget)GsonHelper.getAsObject(var1, "entity", var2, LootContext.EntityTarget.class));
      }

      // $FF: synthetic method
      public LootItemCondition deserialize(JsonObject var1, JsonDeserializationContext var2) {
         return this.deserialize(var1, var2);
      }
   }
}
