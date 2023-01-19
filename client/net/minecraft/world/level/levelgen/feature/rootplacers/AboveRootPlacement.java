package net.minecraft.world.level.levelgen.feature.rootplacers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record AboveRootPlacement(BlockStateProvider b, float c) {
   private final BlockStateProvider aboveRootProvider;
   private final float aboveRootPlacementChance;
   public static final Codec<AboveRootPlacement> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               BlockStateProvider.CODEC.fieldOf("above_root_provider").forGetter(var0x -> var0x.aboveRootProvider),
               Codec.floatRange(0.0F, 1.0F).fieldOf("above_root_placement_chance").forGetter(var0x -> var0x.aboveRootPlacementChance)
            )
            .apply(var0, AboveRootPlacement::new)
   );

   public AboveRootPlacement(BlockStateProvider var1, float var2) {
      super();
      this.aboveRootProvider = var1;
      this.aboveRootPlacementChance = var2;
   }
}
