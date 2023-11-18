package net.minecraft.world.level.storage.loot.providers.score;

import com.mojang.serialization.Codec;

public record LootScoreProviderType(Codec<? extends ScoreboardNameProvider> a) {
   private final Codec<? extends ScoreboardNameProvider> codec;

   public LootScoreProviderType(Codec<? extends ScoreboardNameProvider> var1) {
      super();
      this.codec = var1;
   }
}
