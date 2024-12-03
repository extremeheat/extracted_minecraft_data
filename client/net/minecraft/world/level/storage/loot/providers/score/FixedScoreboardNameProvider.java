package net.minecraft.world.level.storage.loot.providers.score;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.scores.ScoreHolder;

public record FixedScoreboardNameProvider(String name) implements ScoreboardNameProvider {
   public static final MapCodec<FixedScoreboardNameProvider> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Codec.STRING.fieldOf("name").forGetter(FixedScoreboardNameProvider::name)).apply(var0, FixedScoreboardNameProvider::new));

   public FixedScoreboardNameProvider(String var1) {
      super();
      this.name = var1;
   }

   public static ScoreboardNameProvider forName(String var0) {
      return new FixedScoreboardNameProvider(var0);
   }

   public LootScoreProviderType getType() {
      return ScoreboardNameProviders.FIXED;
   }

   public ScoreHolder getScoreHolder(LootContext var1) {
      return ScoreHolder.forNameOnly(this.name);
   }

   public Set<ContextKey<?>> getReferencedContextParams() {
      return Set.of();
   }
}
