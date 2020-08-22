package net.minecraft.world.level.dimension;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.io.File;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Serializable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeZoomer;
import net.minecraft.world.level.biome.FuzzyOffsetBiomeZoomer;
import net.minecraft.world.level.biome.FuzzyOffsetConstantColumnBiomeZoomer;
import net.minecraft.world.level.dimension.end.TheEndDimension;

public class DimensionType implements Serializable {
   public static final DimensionType OVERWORLD;
   public static final DimensionType NETHER;
   public static final DimensionType THE_END;
   private final int id;
   private final String fileSuffix;
   private final String folder;
   private final BiFunction factory;
   private final boolean hasSkylight;
   private final BiomeZoomer biomeZoomer;

   private static DimensionType register(String var0, DimensionType var1) {
      return (DimensionType)Registry.registerMapping(Registry.DIMENSION_TYPE, var1.id, var0, var1);
   }

   protected DimensionType(int var1, String var2, String var3, BiFunction var4, boolean var5, BiomeZoomer var6) {
      this.id = var1;
      this.fileSuffix = var2;
      this.folder = var3;
      this.factory = var4;
      this.hasSkylight = var5;
      this.biomeZoomer = var6;
   }

   public static DimensionType of(Dynamic var0) {
      return (DimensionType)Registry.DIMENSION_TYPE.get(new ResourceLocation(var0.asString("")));
   }

   public static Iterable getAllTypes() {
      return Registry.DIMENSION_TYPE;
   }

   public int getId() {
      return this.id + -1;
   }

   public String getFileSuffix() {
      return this.fileSuffix;
   }

   public File getStorageFolder(File var1) {
      return this.folder.isEmpty() ? var1 : new File(var1, this.folder);
   }

   public Dimension create(Level var1) {
      return (Dimension)this.factory.apply(var1, this);
   }

   public String toString() {
      return getName(this).toString();
   }

   @Nullable
   public static DimensionType getById(int var0) {
      return (DimensionType)Registry.DIMENSION_TYPE.byId(var0 - -1);
   }

   @Nullable
   public static DimensionType getByName(ResourceLocation var0) {
      return (DimensionType)Registry.DIMENSION_TYPE.get(var0);
   }

   @Nullable
   public static ResourceLocation getName(DimensionType var0) {
      return Registry.DIMENSION_TYPE.getKey(var0);
   }

   public boolean hasSkyLight() {
      return this.hasSkylight;
   }

   public BiomeZoomer getBiomeZoomer() {
      return this.biomeZoomer;
   }

   public Object serialize(DynamicOps var1) {
      return var1.createString(Registry.DIMENSION_TYPE.getKey(this).toString());
   }

   static {
      OVERWORLD = register("overworld", new DimensionType(1, "", "", NormalDimension::new, true, FuzzyOffsetConstantColumnBiomeZoomer.INSTANCE));
      NETHER = register("the_nether", new DimensionType(0, "_nether", "DIM-1", NetherDimension::new, false, FuzzyOffsetBiomeZoomer.INSTANCE));
      THE_END = register("the_end", new DimensionType(2, "_end", "DIM1", TheEndDimension::new, false, FuzzyOffsetBiomeZoomer.INSTANCE));
   }
}
