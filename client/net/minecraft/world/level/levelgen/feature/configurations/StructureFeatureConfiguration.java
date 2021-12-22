package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.util.ExtraCodecs;

public class StructureFeatureConfiguration {
   public static final Codec<StructureFeatureConfiguration> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Codec.intRange(0, 4096).fieldOf("spacing").forGetter((var0x) -> {
         return var0x.spacing;
      }), Codec.intRange(0, 4096).fieldOf("separation").forGetter((var0x) -> {
         return var0x.separation;
      }), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("salt").forGetter((var0x) -> {
         return var0x.salt;
      })).apply(var0, StructureFeatureConfiguration::new);
   }).comapFlatMap((var0) -> {
      return var0.spacing <= var0.separation ? DataResult.error("Spacing has to be larger than separation") : DataResult.success(var0);
   }, Function.identity());
   private final int spacing;
   private final int separation;
   private final int salt;

   public StructureFeatureConfiguration(int var1, int var2, int var3) {
      super();
      this.spacing = var1;
      this.separation = var2;
      this.salt = var3;
   }

   public int spacing() {
      return this.spacing;
   }

   public int separation() {
      return this.separation;
   }

   public int salt() {
      return this.salt;
   }
}
