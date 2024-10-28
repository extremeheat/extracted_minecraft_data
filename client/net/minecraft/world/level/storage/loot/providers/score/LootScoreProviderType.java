package net.minecraft.world.level.storage.loot.providers.score;

import com.mojang.serialization.MapCodec;

public record LootScoreProviderType(MapCodec<? extends ScoreboardNameProvider> codec) {
   public LootScoreProviderType(MapCodec<? extends ScoreboardNameProvider> codec) {
      super();
      this.codec = codec;
   }

   public MapCodec<? extends ScoreboardNameProvider> codec() {
      return this.codec;
   }
}
