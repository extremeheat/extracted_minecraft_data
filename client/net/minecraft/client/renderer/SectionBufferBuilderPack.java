package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.BufferBuilder;
import java.util.Map;
import java.util.stream.Collectors;

public class SectionBufferBuilderPack implements AutoCloseable {
   public static final int TOTAL_BUFFERS_SIZE = RenderType.chunkBufferLayers().stream().mapToInt(RenderType::bufferSize).sum();
   private final Map<RenderType, BufferBuilder> builders = RenderType.chunkBufferLayers()
      .stream()
      .collect(Collectors.toMap(var0 -> var0, var0 -> new BufferBuilder(var0.bufferSize())));

   public SectionBufferBuilderPack() {
      super();
   }

   public BufferBuilder builder(RenderType var1) {
      return this.builders.get(var1);
   }

   public void clearAll() {
      this.builders.values().forEach(BufferBuilder::clear);
   }

   public void discardAll() {
      this.builders.values().forEach(BufferBuilder::discard);
   }

   @Override
   public void close() {
      this.builders.values().forEach(BufferBuilder::release);
   }
}
