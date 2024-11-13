package net.minecraft.server.packs.metadata;

import com.mojang.serialization.Codec;

public record MetadataSectionType<T>(String name, Codec<T> codec) {
   public MetadataSectionType(String var1, Codec<T> var2) {
      super();
      this.name = var1;
      this.codec = var2;
   }
}
