package net.minecraft.world.level.levelgen.feature.rootplacers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record MangroveRootPlacement(HolderSet<Block> b, HolderSet<Block> c, BlockStateProvider d, int e, int f, float g) {
   private final HolderSet<Block> canGrowThrough;
   private final HolderSet<Block> muddyRootsIn;
   private final BlockStateProvider muddyRootsProvider;
   private final int maxRootWidth;
   private final int maxRootLength;
   private final float randomSkewChance;
   public static final Codec<MangroveRootPlacement> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               RegistryCodecs.homogeneousList(Registry.BLOCK_REGISTRY).fieldOf("can_grow_through").forGetter(var0x -> var0x.canGrowThrough),
               RegistryCodecs.homogeneousList(Registry.BLOCK_REGISTRY).fieldOf("muddy_roots_in").forGetter(var0x -> var0x.muddyRootsIn),
               BlockStateProvider.CODEC.fieldOf("muddy_roots_provider").forGetter(var0x -> var0x.muddyRootsProvider),
               Codec.intRange(1, 12).fieldOf("max_root_width").forGetter(var0x -> var0x.maxRootWidth),
               Codec.intRange(1, 64).fieldOf("max_root_length").forGetter(var0x -> var0x.maxRootLength),
               Codec.floatRange(0.0F, 1.0F).fieldOf("random_skew_chance").forGetter(var0x -> var0x.randomSkewChance)
            )
            .apply(var0, MangroveRootPlacement::new)
   );

   public MangroveRootPlacement(HolderSet<Block> var1, HolderSet<Block> var2, BlockStateProvider var3, int var4, int var5, float var6) {
      super();
      this.canGrowThrough = var1;
      this.muddyRootsIn = var2;
      this.muddyRootsProvider = var3;
      this.maxRootWidth = var4;
      this.maxRootLength = var5;
      this.randomSkewChance = var6;
   }
}
