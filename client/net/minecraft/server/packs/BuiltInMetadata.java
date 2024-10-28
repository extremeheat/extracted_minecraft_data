package net.minecraft.server.packs;

import java.util.Map;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;

public class BuiltInMetadata {
   private static final BuiltInMetadata EMPTY = new BuiltInMetadata(Map.of());
   private final Map<MetadataSectionSerializer<?>, ?> values;

   private BuiltInMetadata(Map<MetadataSectionSerializer<?>, ?> var1) {
      super();
      this.values = var1;
   }

   public <T> T get(MetadataSectionSerializer<T> var1) {
      return this.values.get(var1);
   }

   public static BuiltInMetadata of() {
      return EMPTY;
   }

   public static <T> BuiltInMetadata of(MetadataSectionSerializer<T> var0, T var1) {
      return new BuiltInMetadata(Map.of(var0, var1));
   }

   public static <T1, T2> BuiltInMetadata of(MetadataSectionSerializer<T1> var0, T1 var1, MetadataSectionSerializer<T2> var2, T2 var3) {
      return new BuiltInMetadata(Map.of(var0, var1, var2, var3));
   }
}
