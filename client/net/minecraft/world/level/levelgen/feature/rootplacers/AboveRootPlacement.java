package net.minecraft.world.level.levelgen.feature.rootplacers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record AboveRootPlacement(BlockStateProvider aboveRootProvider, float aboveRootPlacementChance) {
   public static final Codec<AboveRootPlacement> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(BlockStateProvider.CODEC.fieldOf("above_root_provider").forGetter((var0x) -> {
         return var0x.aboveRootProvider;
      }), Codec.floatRange(0.0F, 1.0F).fieldOf("above_root_placement_chance").forGetter((var0x) -> {
         return var0x.aboveRootPlacementChance;
      })).apply(var0, AboveRootPlacement::new);
   });

   public AboveRootPlacement(BlockStateProvider aboveRootProvider, float aboveRootPlacementChance) {
      super();
      this.aboveRootProvider = aboveRootProvider;
      this.aboveRootPlacementChance = aboveRootPlacementChance;
   }

   public BlockStateProvider aboveRootProvider() {
      return this.aboveRootProvider;
   }

   public float aboveRootPlacementChance() {
      return this.aboveRootPlacementChance;
   }
}
