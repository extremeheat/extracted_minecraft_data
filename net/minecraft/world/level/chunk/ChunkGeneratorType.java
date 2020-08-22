package net.minecraft.world.level.chunk;

import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.levelgen.DebugGeneratorSettings;
import net.minecraft.world.level.levelgen.DebugLevelSource;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.NetherGeneratorSettings;
import net.minecraft.world.level.levelgen.NetherLevelSource;
import net.minecraft.world.level.levelgen.OverworldGeneratorSettings;
import net.minecraft.world.level.levelgen.OverworldLevelSource;
import net.minecraft.world.level.levelgen.TheEndGeneratorSettings;
import net.minecraft.world.level.levelgen.TheEndLevelSource;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;

public class ChunkGeneratorType implements ChunkGeneratorFactory {
   public static final ChunkGeneratorType SURFACE = register("surface", OverworldLevelSource::new, OverworldGeneratorSettings::new, true);
   public static final ChunkGeneratorType CAVES = register("caves", NetherLevelSource::new, NetherGeneratorSettings::new, true);
   public static final ChunkGeneratorType FLOATING_ISLANDS = register("floating_islands", TheEndLevelSource::new, TheEndGeneratorSettings::new, true);
   public static final ChunkGeneratorType DEBUG = register("debug", DebugLevelSource::new, DebugGeneratorSettings::new, false);
   public static final ChunkGeneratorType FLAT = register("flat", FlatLevelSource::new, FlatLevelGeneratorSettings::new, false);
   private final ChunkGeneratorFactory factory;
   private final boolean isPublic;
   private final Supplier settingsFactory;

   private static ChunkGeneratorType register(String var0, ChunkGeneratorFactory var1, Supplier var2, boolean var3) {
      return (ChunkGeneratorType)Registry.register(Registry.CHUNK_GENERATOR_TYPE, (String)var0, new ChunkGeneratorType(var1, var3, var2));
   }

   public ChunkGeneratorType(ChunkGeneratorFactory var1, boolean var2, Supplier var3) {
      this.factory = var1;
      this.isPublic = var2;
      this.settingsFactory = var3;
   }

   public ChunkGenerator create(Level var1, BiomeSource var2, ChunkGeneratorSettings var3) {
      return this.factory.create(var1, var2, var3);
   }

   public ChunkGeneratorSettings createSettings() {
      return (ChunkGeneratorSettings)this.settingsFactory.get();
   }

   public boolean isPublic() {
      return this.isPublic;
   }
}
