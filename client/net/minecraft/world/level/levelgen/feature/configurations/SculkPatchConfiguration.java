package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.util.valueproviders.IntProvider;

public record SculkPatchConfiguration(int b, int c, int d, int e, int f, IntProvider g, float h) implements FeatureConfiguration {
   private final int chargeCount;
   private final int amountPerCharge;
   private final int spreadAttempts;
   private final int growthRounds;
   private final int spreadRounds;
   private final IntProvider extraRareGrowths;
   private final float catalystChance;
   public static final Codec<SculkPatchConfiguration> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               Codec.intRange(1, 32).fieldOf("charge_count").forGetter(SculkPatchConfiguration::chargeCount),
               Codec.intRange(1, 500).fieldOf("amount_per_charge").forGetter(SculkPatchConfiguration::amountPerCharge),
               Codec.intRange(1, 64).fieldOf("spread_attempts").forGetter(SculkPatchConfiguration::spreadAttempts),
               Codec.intRange(0, 8).fieldOf("growth_rounds").forGetter(SculkPatchConfiguration::growthRounds),
               Codec.intRange(0, 8).fieldOf("spread_rounds").forGetter(SculkPatchConfiguration::spreadRounds),
               IntProvider.CODEC.fieldOf("extra_rare_growths").forGetter(SculkPatchConfiguration::extraRareGrowths),
               Codec.floatRange(0.0F, 1.0F).fieldOf("catalyst_chance").forGetter(SculkPatchConfiguration::catalystChance)
            )
            .apply(var0, SculkPatchConfiguration::new)
   );

   public SculkPatchConfiguration(int var1, int var2, int var3, int var4, int var5, IntProvider var6, float var7) {
      super();
      this.chargeCount = var1;
      this.amountPerCharge = var2;
      this.spreadAttempts = var3;
      this.growthRounds = var4;
      this.spreadRounds = var5;
      this.extraRareGrowths = var6;
      this.catalystChance = var7;
   }
}
