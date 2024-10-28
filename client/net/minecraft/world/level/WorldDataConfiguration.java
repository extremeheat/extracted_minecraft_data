package net.minecraft.world.level;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;

public record WorldDataConfiguration(DataPackConfig dataPacks, FeatureFlagSet enabledFeatures) {
   public static final String ENABLED_FEATURES_ID = "enabled_features";
   public static final Codec<WorldDataConfiguration> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(DataPackConfig.CODEC.lenientOptionalFieldOf("DataPacks", DataPackConfig.DEFAULT).forGetter(WorldDataConfiguration::dataPacks), FeatureFlags.CODEC.lenientOptionalFieldOf("enabled_features", FeatureFlags.DEFAULT_FLAGS).forGetter(WorldDataConfiguration::enabledFeatures)).apply(var0, WorldDataConfiguration::new);
   });
   public static final WorldDataConfiguration DEFAULT;

   public WorldDataConfiguration(DataPackConfig dataPacks, FeatureFlagSet enabledFeatures) {
      super();
      this.dataPacks = dataPacks;
      this.enabledFeatures = enabledFeatures;
   }

   public WorldDataConfiguration expandFeatures(FeatureFlagSet var1) {
      return new WorldDataConfiguration(this.dataPacks, this.enabledFeatures.join(var1));
   }

   public DataPackConfig dataPacks() {
      return this.dataPacks;
   }

   public FeatureFlagSet enabledFeatures() {
      return this.enabledFeatures;
   }

   static {
      DEFAULT = new WorldDataConfiguration(DataPackConfig.DEFAULT, FeatureFlags.DEFAULT_FLAGS);
   }
}
