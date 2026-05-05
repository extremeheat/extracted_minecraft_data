package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Set;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ReadOnlyScoreInfo;
import net.minecraft.world.scores.Scoreboard;

public record EntityHasScoreCondition(Map<String, IntRange> scores, LootContext.EntityTarget entityTarget) implements LootItemCondition {
   public static final MapCodec<EntityHasScoreCondition> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.unboundedMap(Codec.STRING, IntRange.CODEC).fieldOf("scores").forGetter(EntityHasScoreCondition::scores), LootContext.EntityTarget.CODEC.fieldOf("entity").forGetter(EntityHasScoreCondition::entityTarget)).apply(i, EntityHasScoreCondition::new));

   public EntityHasScoreCondition {
      super();
   }

   public MapCodec<EntityHasScoreCondition> codec() {
      return MAP_CODEC;
   }

   public Set<ContextKey<?>> getReferencedContextParams() {
      return Set.of(this.entityTarget.contextParam());
   }

   public void validate(final ValidationContext context) {
      LootItemCondition.super.validate(context);
      this.scores.forEach((score, value) -> value.validate(context.forMapField("scores", score)));
   }

   public boolean test(final LootContext context) {
      Entity entity = (Entity)context.getOptionalParameter(this.entityTarget.contextParam());
      if (entity == null) {
         return false;
      } else {
         Scoreboard scoreboard = context.getLevel().getScoreboard();

         for(Map.Entry<String, IntRange> entry : this.scores.entrySet()) {
            if (!this.hasScore(context, entity, scoreboard, (String)entry.getKey(), (IntRange)entry.getValue())) {
               return false;
            }
         }

         return true;
      }
   }

   private boolean hasScore(final LootContext context, final Entity entity, final Scoreboard scoreboard, final String objectiveName, final IntRange range) {
      Objective objective = scoreboard.getObjective(objectiveName);
      if (objective == null) {
         return false;
      } else {
         ReadOnlyScoreInfo scoreInfo = scoreboard.getPlayerScoreInfo(entity, objective);
         return scoreInfo == null ? false : range.test(context, scoreInfo.value());
      }
   }

   public static Builder hasScores(final LootContext.EntityTarget target) {
      return new Builder(target);
   }

   public static class Builder implements LootItemCondition.Builder {
      private final ImmutableMap.Builder<String, IntRange> scores = ImmutableMap.builder();
      private final LootContext.EntityTarget entityTarget;

      public Builder(final LootContext.EntityTarget entityTarget) {
         super();
         this.entityTarget = entityTarget;
      }

      public Builder withScore(final String score, final IntRange bounds) {
         this.scores.put(score, bounds);
         return this;
      }

      public LootItemCondition build() {
         return new EntityHasScoreCondition(this.scores.build(), this.entityTarget);
      }
   }
}
