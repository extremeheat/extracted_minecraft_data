package net.minecraft.world.level.chunk.status;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.VisibleForTesting;

public class ChunkStatus {
   public static final int MAX_STRUCTURE_DISTANCE = 8;
   private static final EnumSet<Heightmap.Types> WORLDGEN_HEIGHTMAPS;
   public static final EnumSet<Heightmap.Types> FINAL_HEIGHTMAPS;
   public static final ChunkStatus EMPTY;
   public static final ChunkStatus STRUCTURE_STARTS;
   public static final ChunkStatus STRUCTURE_REFERENCES;
   public static final ChunkStatus BIOMES;
   public static final ChunkStatus NOISE;
   public static final ChunkStatus SURFACE;
   public static final ChunkStatus CARVERS;
   public static final ChunkStatus FEATURES;
   public static final ChunkStatus INITIALIZE_LIGHT;
   public static final ChunkStatus LIGHT;
   public static final ChunkStatus SPAWN;
   public static final ChunkStatus FULL;
   private final int index;
   private final ChunkStatus parent;
   private final ChunkType chunkType;
   private final EnumSet<Heightmap.Types> heightmapsAfter;

   private static ChunkStatus register(String var0, @Nullable ChunkStatus var1, EnumSet<Heightmap.Types> var2, ChunkType var3) {
      return (ChunkStatus)Registry.register(BuiltInRegistries.CHUNK_STATUS, (String)var0, new ChunkStatus(var1, var2, var3));
   }

   public static List<ChunkStatus> getStatusList() {
      ArrayList var0 = Lists.newArrayList();

      ChunkStatus var1;
      for(var1 = FULL; var1.getParent() != var1; var1 = var1.getParent()) {
         var0.add(var1);
      }

      var0.add(var1);
      Collections.reverse(var0);
      return var0;
   }

   @VisibleForTesting
   protected ChunkStatus(@Nullable ChunkStatus var1, EnumSet<Heightmap.Types> var2, ChunkType var3) {
      super();
      this.parent = var1 == null ? this : var1;
      this.chunkType = var3;
      this.heightmapsAfter = var2;
      this.index = var1 == null ? 0 : var1.getIndex() + 1;
   }

   public int getIndex() {
      return this.index;
   }

   public ChunkStatus getParent() {
      return this.parent;
   }

   public ChunkType getChunkType() {
      return this.chunkType;
   }

   public static ChunkStatus byName(String var0) {
      return (ChunkStatus)BuiltInRegistries.CHUNK_STATUS.get(ResourceLocation.tryParse(var0));
   }

   public EnumSet<Heightmap.Types> heightmapsAfter() {
      return this.heightmapsAfter;
   }

   public boolean isOrAfter(ChunkStatus var1) {
      return this.getIndex() >= var1.getIndex();
   }

   public boolean isAfter(ChunkStatus var1) {
      return this.getIndex() > var1.getIndex();
   }

   public boolean isOrBefore(ChunkStatus var1) {
      return this.getIndex() <= var1.getIndex();
   }

   public boolean isBefore(ChunkStatus var1) {
      return this.getIndex() < var1.getIndex();
   }

   public static ChunkStatus max(ChunkStatus var0, ChunkStatus var1) {
      return var0.isAfter(var1) ? var0 : var1;
   }

   public String toString() {
      return this.getName();
   }

   public String getName() {
      return BuiltInRegistries.CHUNK_STATUS.getKey(this).toString();
   }

   static {
      WORLDGEN_HEIGHTMAPS = EnumSet.of(Heightmap.Types.OCEAN_FLOOR_WG, Heightmap.Types.WORLD_SURFACE_WG);
      FINAL_HEIGHTMAPS = EnumSet.of(Heightmap.Types.OCEAN_FLOOR, Heightmap.Types.WORLD_SURFACE, Heightmap.Types.MOTION_BLOCKING, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES);
      EMPTY = register("empty", (ChunkStatus)null, WORLDGEN_HEIGHTMAPS, ChunkType.PROTOCHUNK);
      STRUCTURE_STARTS = register("structure_starts", EMPTY, WORLDGEN_HEIGHTMAPS, ChunkType.PROTOCHUNK);
      STRUCTURE_REFERENCES = register("structure_references", STRUCTURE_STARTS, WORLDGEN_HEIGHTMAPS, ChunkType.PROTOCHUNK);
      BIOMES = register("biomes", STRUCTURE_REFERENCES, WORLDGEN_HEIGHTMAPS, ChunkType.PROTOCHUNK);
      NOISE = register("noise", BIOMES, WORLDGEN_HEIGHTMAPS, ChunkType.PROTOCHUNK);
      SURFACE = register("surface", NOISE, WORLDGEN_HEIGHTMAPS, ChunkType.PROTOCHUNK);
      CARVERS = register("carvers", SURFACE, FINAL_HEIGHTMAPS, ChunkType.PROTOCHUNK);
      FEATURES = register("features", CARVERS, FINAL_HEIGHTMAPS, ChunkType.PROTOCHUNK);
      INITIALIZE_LIGHT = register("initialize_light", FEATURES, FINAL_HEIGHTMAPS, ChunkType.PROTOCHUNK);
      LIGHT = register("light", INITIALIZE_LIGHT, FINAL_HEIGHTMAPS, ChunkType.PROTOCHUNK);
      SPAWN = register("spawn", LIGHT, FINAL_HEIGHTMAPS, ChunkType.PROTOCHUNK);
      FULL = register("full", SPAWN, FINAL_HEIGHTMAPS, ChunkType.LEVELCHUNK);
   }
}
