package net.minecraft.world.level.chunk;

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

public record PalettedContainerFactory(Strategy<BlockState> blockStatesStrategy, BlockState defaultBlockState, Codec<PalettedContainer<BlockState>> blockStatesContainerCodec, Strategy<Holder<Biome>> biomeStrategy, Holder<Biome> defaultBiome, Codec<PalettedContainerRO<Holder<Biome>>> biomeContainerCodec) {
   public PalettedContainerFactory(Strategy<BlockState> var1, BlockState var2, Codec<PalettedContainer<BlockState>> var3, Strategy<Holder<Biome>> var4, Holder<Biome> var5, Codec<PalettedContainerRO<Holder<Biome>>> var6) {
      super();
      this.blockStatesStrategy = var1;
      this.defaultBlockState = var2;
      this.blockStatesContainerCodec = var3;
      this.biomeStrategy = var4;
      this.defaultBiome = var5;
      this.biomeContainerCodec = var6;
   }

   public static PalettedContainerFactory create(RegistryAccess var0) {
      Strategy var1 = Strategy.createForBlockStates(Block.BLOCK_STATE_REGISTRY);
      BlockState var2 = Blocks.AIR.defaultBlockState();
      Registry var3 = var0.lookupOrThrow(Registries.BIOME);
      Strategy var4 = Strategy.createForBiomes(var3.asHolderIdMap());
      Holder.Reference var5 = var3.getOrThrow(Biomes.PLAINS);
      return new PalettedContainerFactory(var1, var2, PalettedContainer.codecRW(BlockState.CODEC, var1, var2), var4, var5, PalettedContainer.codecRO(var3.holderByNameCodec(), var4, var5));
   }

   public PalettedContainer<BlockState> createForBlockStates() {
      return new PalettedContainer<BlockState>(this.defaultBlockState, this.blockStatesStrategy);
   }

   public PalettedContainer<Holder<Biome>> createForBiomes() {
      return new PalettedContainer<Holder<Biome>>(this.defaultBiome, this.biomeStrategy);
   }
}
