package net.minecraft.world.level.storage.loot.providers.score;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.scores.ScoreHolder;

public record FixedScoreboardNameProvider(String b) implements ScoreboardNameProvider {
   private final String name;
   public static final Codec<FixedScoreboardNameProvider> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(Codec.STRING.fieldOf("name").forGetter(FixedScoreboardNameProvider::name)).apply(var0, FixedScoreboardNameProvider::new)
   );

   public FixedScoreboardNameProvider(String var1) {
      super();
      this.name = var1;
   }

   public static ScoreboardNameProvider forName(String var0) {
      return new FixedScoreboardNameProvider(var0);
   }

   @Override
   public LootScoreProviderType getType() {
      return ScoreboardNameProviders.FIXED;
   }

   @Override
   public ScoreHolder getScoreHolder(LootContext var1) {
      return ScoreHolder.forNameOnly(this.name);
   }

   @Override
   public Set<LootContextParam<?>> getReferencedContextParams() {
      return ImmutableSet.of();
   }
}
