package net.minecraft.world.level.levelgen.feature.rootplacers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record MangroveRootPlacement(HolderSet<Block> canGrowThrough, HolderSet<Block> muddyRootsIn, BlockStateProvider muddyRootsProvider, int maxRootWidth, int maxRootLength, float randomSkewChance) {
   public static final Codec<MangroveRootPlacement> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("can_grow_through").forGetter((var0x) -> {
         return var0x.canGrowThrough;
      }), RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("muddy_roots_in").forGetter((var0x) -> {
         return var0x.muddyRootsIn;
      }), BlockStateProvider.CODEC.fieldOf("muddy_roots_provider").forGetter((var0x) -> {
         return var0x.muddyRootsProvider;
      }), Codec.intRange(1, 12).fieldOf("max_root_width").forGetter((var0x) -> {
         return var0x.maxRootWidth;
      }), Codec.intRange(1, 64).fieldOf("max_root_length").forGetter((var0x) -> {
         return var0x.maxRootLength;
      }), Codec.floatRange(0.0F, 1.0F).fieldOf("random_skew_chance").forGetter((var0x) -> {
         return var0x.randomSkewChance;
      })).apply(var0, MangroveRootPlacement::new);
   });

   public MangroveRootPlacement(HolderSet<Block> canGrowThrough, HolderSet<Block> muddyRootsIn, BlockStateProvider muddyRootsProvider, int maxRootWidth, int maxRootLength, float randomSkewChance) {
      super();
      this.canGrowThrough = canGrowThrough;
      this.muddyRootsIn = muddyRootsIn;
      this.muddyRootsProvider = muddyRootsProvider;
      this.maxRootWidth = maxRootWidth;
      this.maxRootLength = maxRootLength;
      this.randomSkewChance = randomSkewChance;
   }

   public HolderSet<Block> canGrowThrough() {
      return this.canGrowThrough;
   }

   public HolderSet<Block> muddyRootsIn() {
      return this.muddyRootsIn;
   }

   public BlockStateProvider muddyRootsProvider() {
      return this.muddyRootsProvider;
   }

   public int maxRootWidth() {
      return this.maxRootWidth;
   }

   public int maxRootLength() {
      return this.maxRootLength;
   }

   public float randomSkewChance() {
      return this.randomSkewChance;
   }
}
