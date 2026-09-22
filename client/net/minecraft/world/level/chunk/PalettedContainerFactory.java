package net.minecraft.world.level.chunk;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public record PalettedContainerFactory(Strategy<BlockState> blockStatesStrategy, BlockState defaultBlockState, Codec<PalettedContainer<BlockState>> blockStatesContainerCodec, Holder<Biome> defaultBiome, Strategy<Holder<Biome>> noiseBiomeStrategy, Codec<PalettedContainerRO<Holder<Biome>>> noiseBiomeContainerCodec, Strategy<Holder<Biome>> biomeStrategy, Codec<PalettedContainerRO<Holder<Biome>>> biomeContainerCodec) {
   public PalettedContainerFactory {
      super();
   }

   public static PalettedContainerFactory create(final RegistryAccess registries) {
      return create(registries.lookupOrThrow(Registries.BIOME));
   }

   @VisibleForTesting
   public static PalettedContainerFactory create(final Registry<Biome> biomes) {
      Strategy<BlockState> blockStateStrategy = Strategy.<BlockState>createForBlockStates(Block.BLOCK_STATE_REGISTRY);
      BlockState defaultBlockState = Blocks.AIR.defaultBlockState();
      Strategy<Holder<Biome>> noiseBiomeStrategy = Strategy.<Holder<Biome>>createForNoiseBiomes(biomes.asHolderIdMap());
      Strategy<Holder<Biome>> biomeStrategy = Strategy.<Holder<Biome>>createForBiomes(biomes.asHolderIdMap());
      Holder.Reference<Biome> defaultBiome = biomes.getOrThrow(Biomes.PLAINS);
      return new PalettedContainerFactory(blockStateStrategy, defaultBlockState, PalettedContainer.codecRW(BlockState.CODEC, blockStateStrategy, defaultBlockState), defaultBiome, noiseBiomeStrategy, PalettedContainer.codecRO(biomes.holderByNameCodec(), noiseBiomeStrategy, defaultBiome), biomeStrategy, PalettedContainer.codecRO(biomes.holderByNameCodec(), biomeStrategy, defaultBiome));
   }

   public PalettedContainer<BlockState> createForBlockStates() {
      return new PalettedContainer<BlockState>(this.defaultBlockState, this.blockStatesStrategy);
   }

   public PalettedContainer<Holder<Biome>> createForNoiseBiomes(final PalettedContainer.Initializer<Holder<Biome>> initializer) {
      return PalettedContainer.<Holder<Biome>>fromInitializer(this.noiseBiomeStrategy, initializer);
   }

   public PalettedContainer<Holder<Biome>> createForNoiseBiomes() {
      return new PalettedContainer<Holder<Biome>>(this.defaultBiome, this.noiseBiomeStrategy);
   }

   public PalettedContainer<Holder<Biome>> createForBiomes() {
      return new PalettedContainer<Holder<Biome>>(this.defaultBiome, this.biomeStrategy);
   }
}
