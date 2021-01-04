package net.minecraft.world.level.biome;

import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.Registry;

public class BiomeSourceType<C extends BiomeSourceSettings, T extends BiomeSource> {
   public static final BiomeSourceType<CheckerboardBiomeSourceSettings, CheckerboardBiomeSource> CHECKERBOARD = register("checkerboard", CheckerboardBiomeSource::new, CheckerboardBiomeSourceSettings::new);
   public static final BiomeSourceType<FixedBiomeSourceSettings, FixedBiomeSource> FIXED = register("fixed", FixedBiomeSource::new, FixedBiomeSourceSettings::new);
   public static final BiomeSourceType<OverworldBiomeSourceSettings, OverworldBiomeSource> VANILLA_LAYERED = register("vanilla_layered", OverworldBiomeSource::new, OverworldBiomeSourceSettings::new);
   public static final BiomeSourceType<TheEndBiomeSourceSettings, TheEndBiomeSource> THE_END = register("the_end", TheEndBiomeSource::new, TheEndBiomeSourceSettings::new);
   private final Function<C, T> factory;
   private final Supplier<C> settingsFactory;

   private static <C extends BiomeSourceSettings, T extends BiomeSource> BiomeSourceType<C, T> register(String var0, Function<C, T> var1, Supplier<C> var2) {
      return (BiomeSourceType)Registry.register(Registry.BIOME_SOURCE_TYPE, (String)var0, new BiomeSourceType(var1, var2));
   }

   public BiomeSourceType(Function<C, T> var1, Supplier<C> var2) {
      super();
      this.factory = var1;
      this.settingsFactory = var2;
   }

   public T create(C var1) {
      return (BiomeSource)this.factory.apply(var1);
   }

   public C createSettings() {
      return (BiomeSourceSettings)this.settingsFactory.get();
   }
}
