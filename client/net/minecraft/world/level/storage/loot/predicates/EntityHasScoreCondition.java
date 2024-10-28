package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ReadOnlyScoreInfo;
import net.minecraft.world.scores.Scoreboard;

public record EntityHasScoreCondition(Map<String, IntRange> scores, LootContext.EntityTarget entityTarget) implements LootItemCondition {
   public static final MapCodec<EntityHasScoreCondition> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(Codec.unboundedMap(Codec.STRING, IntRange.CODEC).fieldOf("scores").forGetter(EntityHasScoreCondition::scores), LootContext.EntityTarget.CODEC.fieldOf("entity").forGetter(EntityHasScoreCondition::entityTarget)).apply(var0, EntityHasScoreCondition::new);
   });

   public EntityHasScoreCondition(Map<String, IntRange> var1, LootContext.EntityTarget var2) {
      super();
      this.scores = var1;
      this.entityTarget = var2;
   }

   public LootItemConditionType getType() {
      return LootItemConditions.ENTITY_SCORES;
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return (Set)Stream.concat(Stream.of(this.entityTarget.getParam()), this.scores.values().stream().flatMap((var0) -> {
         return var0.getReferencedContextParams().stream();
      })).collect(ImmutableSet.toImmutableSet());
   }

   public boolean test(LootContext var1) {
      Entity var2 = (Entity)var1.getParamOrNull(this.entityTarget.getParam());
      if (var2 == null) {
         return false;
      } else {
         ServerScoreboard var3 = var1.getLevel().getScoreboard();
         Iterator var4 = this.scores.entrySet().iterator();

         Map.Entry var5;
         do {
            if (!var4.hasNext()) {
               return true;
            }

            var5 = (Map.Entry)var4.next();
         } while(this.hasScore(var1, var2, var3, (String)var5.getKey(), (IntRange)var5.getValue()));

         return false;
      }
   }

   protected boolean hasScore(LootContext var1, Entity var2, Scoreboard var3, String var4, IntRange var5) {
      Objective var6 = var3.getObjective(var4);
      if (var6 == null) {
         return false;
      } else {
         ReadOnlyScoreInfo var7 = var3.getPlayerScoreInfo(var2, var6);
         return var7 == null ? false : var5.test(var1, var7.value());
      }
   }

   public static Builder hasScores(LootContext.EntityTarget var0) {
      return new Builder(var0);
   }

   public Map<String, IntRange> scores() {
      return this.scores;
   }

   public LootContext.EntityTarget entityTarget() {
      return this.entityTarget;
   }

   // $FF: synthetic method
   public boolean test(final Object var1) {
      return this.test((LootContext)var1);
   }

   public static class Builder implements LootItemCondition.Builder {
      private final ImmutableMap.Builder<String, IntRange> scores = ImmutableMap.builder();
      private final LootContext.EntityTarget entityTarget;

      public Builder(LootContext.EntityTarget var1) {
         super();
         this.entityTarget = var1;
      }

      public Builder withScore(String var1, IntRange var2) {
         this.scores.put(var1, var2);
         return this;
      }

      public LootItemCondition build() {
         return new EntityHasScoreCondition(this.scores.build(), this.entityTarget);
      }
   }
}
