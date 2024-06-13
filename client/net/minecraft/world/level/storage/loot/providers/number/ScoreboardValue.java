package net.minecraft.world.level.storage.loot.providers.number;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.providers.score.ContextScoreboardNameProvider;
import net.minecraft.world.level.storage.loot.providers.score.ScoreboardNameProvider;
import net.minecraft.world.level.storage.loot.providers.score.ScoreboardNameProviders;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ReadOnlyScoreInfo;
import net.minecraft.world.scores.ScoreHolder;

public record ScoreboardValue(ScoreboardNameProvider target, String score, float scale) implements NumberProvider {
   public static final MapCodec<ScoreboardValue> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(
               ScoreboardNameProviders.CODEC.fieldOf("target").forGetter(ScoreboardValue::target),
               Codec.STRING.fieldOf("score").forGetter(ScoreboardValue::score),
               Codec.FLOAT.fieldOf("scale").orElse(1.0F).forGetter(ScoreboardValue::scale)
            )
            .apply(var0, ScoreboardValue::new)
   );

   public ScoreboardValue(ScoreboardNameProvider target, String score, float scale) {
      super();
      this.target = target;
      this.score = score;
      this.scale = scale;
   }

   @Override
   public LootNumberProviderType getType() {
      return NumberProviders.SCORE;
   }

   @Override
   public Set<LootContextParam<?>> getReferencedContextParams() {
      return this.target.getReferencedContextParams();
   }

   public static ScoreboardValue fromScoreboard(LootContext.EntityTarget var0, String var1) {
      return fromScoreboard(var0, var1, 1.0F);
   }

   public static ScoreboardValue fromScoreboard(LootContext.EntityTarget var0, String var1, float var2) {
      return new ScoreboardValue(ContextScoreboardNameProvider.forTarget(var0), var1, var2);
   }

   @Override
   public float getFloat(LootContext var1) {
      ScoreHolder var2 = this.target.getScoreHolder(var1);
      if (var2 == null) {
         return 0.0F;
      } else {
         ServerScoreboard var3 = var1.getLevel().getScoreboard();
         Objective var4 = var3.getObjective(this.score);
         if (var4 == null) {
            return 0.0F;
         } else {
            ReadOnlyScoreInfo var5 = var3.getPlayerScoreInfo(var2, var4);
            return var5 == null ? 0.0F : (float)var5.value() * this.scale;
         }
      }
   }
}
