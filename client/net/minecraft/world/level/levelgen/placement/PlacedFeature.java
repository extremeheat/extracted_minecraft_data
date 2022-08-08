package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.apache.commons.lang3.mutable.MutableBoolean;

public record PlacedFeature(Holder<ConfiguredFeature<?, ?>> e, List<PlacementModifier> f) {
   private final Holder<ConfiguredFeature<?, ?>> feature;
   private final List<PlacementModifier> placement;
   public static final Codec<PlacedFeature> DIRECT_CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(ConfiguredFeature.CODEC.fieldOf("feature").forGetter((var0x) -> {
         return var0x.feature;
      }), PlacementModifier.CODEC.listOf().fieldOf("placement").forGetter((var0x) -> {
         return var0x.placement;
      })).apply(var0, PlacedFeature::new);
   });
   public static final Codec<Holder<PlacedFeature>> CODEC;
   public static final Codec<HolderSet<PlacedFeature>> LIST_CODEC;
   public static final Codec<List<HolderSet<PlacedFeature>>> LIST_OF_LISTS_CODEC;

   public PlacedFeature(Holder<ConfiguredFeature<?, ?>> var1, List<PlacementModifier> var2) {
      super();
      this.feature = var1;
      this.placement = var2;
   }

   public boolean place(WorldGenLevel var1, ChunkGenerator var2, RandomSource var3, BlockPos var4) {
      return this.placeWithContext(new PlacementContext(var1, var2, Optional.empty()), var3, var4);
   }

   public boolean placeWithBiomeCheck(WorldGenLevel var1, ChunkGenerator var2, RandomSource var3, BlockPos var4) {
      return this.placeWithContext(new PlacementContext(var1, var2, Optional.of(this)), var3, var4);
   }

   private boolean placeWithContext(PlacementContext var1, RandomSource var2, BlockPos var3) {
      Stream var4 = Stream.of(var3);

      PlacementModifier var6;
      for(Iterator var5 = this.placement.iterator(); var5.hasNext(); var4 = var4.flatMap((var3x) -> {
         return var6.getPositions(var1, var2, var3x);
      })) {
         var6 = (PlacementModifier)var5.next();
      }

      ConfiguredFeature var7 = (ConfiguredFeature)this.feature.value();
      MutableBoolean var8 = new MutableBoolean();
      var4.forEach((var4x) -> {
         if (var7.place(var1.getLevel(), var1.generator(), var2, var4x)) {
            var8.setTrue();
         }

      });
      return var8.isTrue();
   }

   public Stream<ConfiguredFeature<?, ?>> getFeatures() {
      return ((ConfiguredFeature)this.feature.value()).getFeatures();
   }

   public String toString() {
      return "Placed " + this.feature;
   }

   public Holder<ConfiguredFeature<?, ?>> feature() {
      return this.feature;
   }

   public List<PlacementModifier> placement() {
      return this.placement;
   }

   static {
      CODEC = RegistryFileCodec.create(Registry.PLACED_FEATURE_REGISTRY, DIRECT_CODEC);
      LIST_CODEC = RegistryCodecs.homogeneousList(Registry.PLACED_FEATURE_REGISTRY, DIRECT_CODEC);
      LIST_OF_LISTS_CODEC = RegistryCodecs.homogeneousList(Registry.PLACED_FEATURE_REGISTRY, DIRECT_CODEC, true).listOf();
   }

   static record test(int a) {
      private test(int var1) {
         super();
         this.a = var1;
      }

      public int a() {
         return this.a;
      }
   }
}
