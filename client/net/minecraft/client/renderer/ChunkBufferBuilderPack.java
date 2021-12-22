package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.BufferBuilder;
import java.util.Map;
import java.util.stream.Collectors;

public class ChunkBufferBuilderPack {
   private final Map<RenderType, BufferBuilder> builders = (Map)RenderType.chunkBufferLayers().stream().collect(Collectors.toMap((var0) -> {
      return var0;
   }, (var0) -> {
      return new BufferBuilder(var0.bufferSize());
   }));

   public ChunkBufferBuilderPack() {
      super();
   }

   public BufferBuilder builder(RenderType var1) {
      return (BufferBuilder)this.builders.get(var1);
   }

   public void clearAll() {
      this.builders.values().forEach(BufferBuilder::clear);
   }

   public void discardAll() {
      this.builders.values().forEach(BufferBuilder::discard);
   }
}
