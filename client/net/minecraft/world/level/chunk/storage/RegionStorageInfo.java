package net.minecraft.world.level.chunk.storage;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public record RegionStorageInfo(String level, ResourceKey<Level> dimension, String type) {
   public RegionStorageInfo(String level, ResourceKey<Level> dimension, String type) {
      super();
      this.level = level;
      this.dimension = dimension;
      this.type = type;
   }

   public RegionStorageInfo withTypeSuffix(String var1) {
      return new RegionStorageInfo(this.level, this.dimension, this.type + var1);
   }
}
