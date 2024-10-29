package net.minecraft.world.level.storage.loot.providers.score;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.scores.ScoreHolder;

public record ContextScoreboardNameProvider(LootContext.EntityTarget target) implements ScoreboardNameProvider {
   public static final MapCodec<ContextScoreboardNameProvider> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(LootContext.EntityTarget.CODEC.fieldOf("target").forGetter(ContextScoreboardNameProvider::target)).apply(var0, ContextScoreboardNameProvider::new);
   });
   public static final Codec<ContextScoreboardNameProvider> INLINE_CODEC;

   public ContextScoreboardNameProvider(LootContext.EntityTarget var1) {
      super();
      this.target = var1;
   }

   public static ScoreboardNameProvider forTarget(LootContext.EntityTarget var0) {
      return new ContextScoreboardNameProvider(var0);
   }

   public LootScoreProviderType getType() {
      return ScoreboardNameProviders.CONTEXT;
   }

   @Nullable
   public ScoreHolder getScoreHolder(LootContext var1) {
      return (ScoreHolder)var1.getOptionalParameter(this.target.getParam());
   }

   public Set<ContextKey<?>> getReferencedContextParams() {
      return Set.of(this.target.getParam());
   }

   public LootContext.EntityTarget target() {
      return this.target;
   }

   static {
      INLINE_CODEC = LootContext.EntityTarget.CODEC.xmap(ContextScoreboardNameProvider::new, ContextScoreboardNameProvider::target);
   }
}
