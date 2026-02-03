package net.minecraft.world.level.storage.loot.providers.score;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.scores.ScoreHolder;
import org.jspecify.annotations.Nullable;

public record ContextScoreboardNameProvider(LootContext.EntityTarget target) implements ScoreboardNameProvider {
   public static final MapCodec<ContextScoreboardNameProvider> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(LootContext.EntityTarget.CODEC.fieldOf("target").forGetter(ContextScoreboardNameProvider::target)).apply(i, ContextScoreboardNameProvider::new));
   public static final Codec<ContextScoreboardNameProvider> INLINE_CODEC;

   public ContextScoreboardNameProvider {
      super();
   }

   public static ScoreboardNameProvider forTarget(final LootContext.EntityTarget target) {
      return new ContextScoreboardNameProvider(target);
   }

   public MapCodec<ContextScoreboardNameProvider> codec() {
      return MAP_CODEC;
   }

   public @Nullable ScoreHolder getScoreHolder(final LootContext context) {
      return (ScoreHolder)context.getOptionalParameter(this.target.contextParam());
   }

   public Set<ContextKey<?>> getReferencedContextParams() {
      return Set.of(this.target.contextParam());
   }

   static {
      INLINE_CODEC = LootContext.EntityTarget.CODEC.xmap(ContextScoreboardNameProvider::new, ContextScoreboardNameProvider::target);
   }
}
