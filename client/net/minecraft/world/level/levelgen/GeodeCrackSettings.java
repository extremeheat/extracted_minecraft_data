package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.world.level.levelgen.feature.configurations.GeodeConfiguration;

public class GeodeCrackSettings {
   public static final Codec<GeodeCrackSettings> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               GeodeConfiguration.CHANCE_RANGE.fieldOf("generate_crack_chance").orElse(1.0).forGetter(var0x -> var0x.generateCrackChance),
               Codec.doubleRange(0.0, 5.0).fieldOf("base_crack_size").orElse(2.0).forGetter(var0x -> var0x.baseCrackSize),
               Codec.intRange(0, 10).fieldOf("crack_point_offset").orElse(2).forGetter(var0x -> var0x.crackPointOffset)
            )
            .apply(var0, GeodeCrackSettings::new)
   );
   public final double generateCrackChance;
   public final double baseCrackSize;
   public final int crackPointOffset;

   public GeodeCrackSettings(double var1, double var3, int var5) {
      super();
      this.generateCrackChance = var1;
      this.baseCrackSize = var3;
      this.crackPointOffset = var5;
   }
}
