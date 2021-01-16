package net.minecraft.tags;

import java.util.Map;
import java.util.stream.Collectors;

public class SerializationTags {
   private static volatile TagContainer instance = TagContainer.of(TagCollection.of((Map)BlockTags.getWrappers().stream().collect(Collectors.toMap(Tag.Named::getName, (var0) -> {
      return var0;
   }))), TagCollection.of((Map)ItemTags.getWrappers().stream().collect(Collectors.toMap(Tag.Named::getName, (var0) -> {
      return var0;
   }))), TagCollection.of((Map)FluidTags.getWrappers().stream().collect(Collectors.toMap(Tag.Named::getName, (var0) -> {
      return var0;
   }))), TagCollection.of((Map)EntityTypeTags.getWrappers().stream().collect(Collectors.toMap(Tag.Named::getName, (var0) -> {
      return var0;
   }))));

   public static TagContainer getInstance() {
      return instance;
   }

   public static void bind(TagContainer var0) {
      instance = var0;
   }
}
