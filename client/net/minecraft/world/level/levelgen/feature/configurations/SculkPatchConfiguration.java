package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.IntProvider;

public record SculkPatchConfiguration(int chargeCount, int amountPerCharge, int spreadAttempts, int growthRounds, int spreadRounds, IntProvider extraRareGrowths, float catalystChance) implements FeatureConfiguration {
   public static final Codec<SculkPatchConfiguration> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Codec.intRange(1, 32).fieldOf("charge_count").forGetter(SculkPatchConfiguration::chargeCount), Codec.intRange(1, 500).fieldOf("amount_per_charge").forGetter(SculkPatchConfiguration::amountPerCharge), Codec.intRange(1, 64).fieldOf("spread_attempts").forGetter(SculkPatchConfiguration::spreadAttempts), Codec.intRange(0, 8).fieldOf("growth_rounds").forGetter(SculkPatchConfiguration::growthRounds), Codec.intRange(0, 8).fieldOf("spread_rounds").forGetter(SculkPatchConfiguration::spreadRounds), IntProvider.CODEC.fieldOf("extra_rare_growths").forGetter(SculkPatchConfiguration::extraRareGrowths), Codec.floatRange(0.0F, 1.0F).fieldOf("catalyst_chance").forGetter(SculkPatchConfiguration::catalystChance)).apply(var0, SculkPatchConfiguration::new);
   });

   public SculkPatchConfiguration(int chargeCount, int amountPerCharge, int spreadAttempts, int growthRounds, int spreadRounds, IntProvider extraRareGrowths, float catalystChance) {
      super();
      this.chargeCount = chargeCount;
      this.amountPerCharge = amountPerCharge;
      this.spreadAttempts = spreadAttempts;
      this.growthRounds = growthRounds;
      this.spreadRounds = spreadRounds;
      this.extraRareGrowths = extraRareGrowths;
      this.catalystChance = catalystChance;
   }

   public int chargeCount() {
      return this.chargeCount;
   }

   public int amountPerCharge() {
      return this.amountPerCharge;
   }

   public int spreadAttempts() {
      return this.spreadAttempts;
   }

   public int growthRounds() {
      return this.growthRounds;
   }

   public int spreadRounds() {
      return this.spreadRounds;
   }

   public IntProvider extraRareGrowths() {
      return this.extraRareGrowths;
   }

   public float catalystChance() {
      return this.catalystChance;
   }
}
