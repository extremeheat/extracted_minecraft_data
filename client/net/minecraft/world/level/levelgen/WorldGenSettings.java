package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.Objects;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public final class WorldGenSettings extends SavedData {
   public static final Codec<WorldGenSettings> CODEC = RecordCodecBuilder.create((i) -> i.group(WorldOptions.CODEC.forGetter(WorldGenSettings::options), WorldDimensions.CODEC.forGetter(WorldGenSettings::dimensions)).apply(i, i.stable(WorldGenSettings::new)));
   public static final SavedDataType<WorldGenSettings> TYPE;
   private final WorldOptions options;
   private final WorldDimensions dimensions;

   public WorldGenSettings(final WorldOptions options, final WorldDimensions dimensions) {
      super();
      this.options = options;
      this.dimensions = dimensions;
   }

   public static WorldGenSettings of(final WorldOptions options, final RegistryAccess registryAccess) {
      return new WorldGenSettings(options, new WorldDimensions(registryAccess.lookupOrThrow(Registries.LEVEL_STEM)));
   }

   public WorldOptions options() {
      return this.options;
   }

   public WorldDimensions dimensions() {
      return this.dimensions;
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.options, this.dimensions});
   }

   public String toString() {
      String var10000 = String.valueOf(this.options);
      return "WorldGenSettings[options=" + var10000 + ", dimensions=" + String.valueOf(this.dimensions) + "]";
   }

   static {
      TYPE = new SavedDataType<WorldGenSettings>(Identifier.withDefaultNamespace("world_gen_settings"), () -> new WorldGenSettings(WorldOptions.defaultWithRandomSeed(), new WorldDimensions(new HashMap())), CODEC, DataFixTypes.SAVED_DATA_WORLD_GEN_SETTINGS);
   }
}
