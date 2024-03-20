package net.minecraft.world.level.storage.loot.providers.score;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.scores.ScoreHolder;

public record ContextScoreboardNameProvider(LootContext.EntityTarget c) implements ScoreboardNameProvider {
   private final LootContext.EntityTarget target;
   public static final Codec<ContextScoreboardNameProvider> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(LootContext.EntityTarget.CODEC.fieldOf("target").forGetter(ContextScoreboardNameProvider::target))
            .apply(var0, ContextScoreboardNameProvider::new)
   );
   public static final Codec<ContextScoreboardNameProvider> INLINE_CODEC = LootContext.EntityTarget.CODEC
      .xmap(ContextScoreboardNameProvider::new, ContextScoreboardNameProvider::target);

   public ContextScoreboardNameProvider(LootContext.EntityTarget var1) {
      super();
      this.target = var1;
   }

   public static ScoreboardNameProvider forTarget(LootContext.EntityTarget var0) {
      return new ContextScoreboardNameProvider(var0);
   }

   @Override
   public LootScoreProviderType getType() {
      return ScoreboardNameProviders.CONTEXT;
   }

   @Nullable
   @Override
   public ScoreHolder getScoreHolder(LootContext var1) {
      return var1.getParamOrNull(this.target.getParam());
   }

   @Override
   public Set<LootContextParam<?>> getReferencedContextParams() {
      return ImmutableSet.of(this.target.getParam());
   }
}