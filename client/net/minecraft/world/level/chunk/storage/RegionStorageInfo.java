package net.minecraft.world.level.chunk.storage;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public record RegionStorageInfo(String a, ResourceKey<Level> b, String c) {
   private final String level;
   private final ResourceKey<Level> dimension;
   private final String type;

   public RegionStorageInfo(String var1, ResourceKey<Level> var2, String var3) {
      super();
      this.level = var1;
      this.dimension = var2;
      this.type = var3;
   }

   public RegionStorageInfo withTypeSuffix(String var1) {
      return new RegionStorageInfo(this.level, this.dimension, this.type + var1);
   }
}
