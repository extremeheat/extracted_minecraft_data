package net.minecraft.world.level.storage.loot.providers.score;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.scores.ScoreHolder;

public record FixedScoreboardNameProvider(String name) implements ScoreboardNameProvider {
   public static final MapCodec<FixedScoreboardNameProvider> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(Codec.STRING.fieldOf("name").forGetter(FixedScoreboardNameProvider::name)).apply(var0, FixedScoreboardNameProvider::new);
   });

   public FixedScoreboardNameProvider(String name) {
      super();
      this.name = name;
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

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return ImmutableSet.of();
   }

   public String name() {
      return this.name;
   }
}
