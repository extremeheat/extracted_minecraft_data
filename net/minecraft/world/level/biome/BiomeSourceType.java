package net.minecraft.world.level.biome;

import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.world.level.storage.LevelData;

public class BiomeSourceType {
   public static final BiomeSourceType CHECKERBOARD = register("checkerboard", CheckerboardColumnBiomeSource::new, CheckerboardBiomeSourceSettings::new);
   public static final BiomeSourceType FIXED = register("fixed", FixedBiomeSource::new, FixedBiomeSourceSettings::new);
   public static final BiomeSourceType VANILLA_LAYERED = register("vanilla_layered", OverworldBiomeSource::new, OverworldBiomeSourceSettings::new);
   public static final BiomeSourceType THE_END = register("the_end", TheEndBiomeSource::new, TheEndBiomeSourceSettings::new);
   private final Function factory;
   private final Function settingsFactory;

   private static BiomeSourceType register(String var0, Function var1, Function var2) {
      return (BiomeSourceType)Registry.register(Registry.BIOME_SOURCE_TYPE, (String)var0, new BiomeSourceType(var1, var2));
   }

   private BiomeSourceType(Function var1, Function var2) {
      this.factory = var1;
      this.settingsFactory = var2;
   }

   public BiomeSource create(BiomeSourceSettings var1) {
      return (BiomeSource)this.factory.apply(var1);
   }

   public BiomeSourceSettings createSettings(LevelData var1) {
      return (BiomeSourceSettings)this.settingsFactory.apply(var1);
   }
}
