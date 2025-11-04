package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import java.util.Arrays;
import java.util.Map;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.util.Util;

public class SectionBufferBuilderPack implements AutoCloseable {
   public static final int TOTAL_BUFFERS_SIZE = Arrays.stream(ChunkSectionLayer.values()).mapToInt(ChunkSectionLayer::bufferSize).sum();
   private final Map<ChunkSectionLayer, ByteBufferBuilder> buffers = Util.<ChunkSectionLayer, ByteBufferBuilder>makeEnumMap(ChunkSectionLayer.class, (var0) -> new ByteBufferBuilder(var0.bufferSize()));

   public SectionBufferBuilderPack() {
      super();
   }

   public ByteBufferBuilder buffer(ChunkSectionLayer var1) {
      return (ByteBufferBuilder)this.buffers.get(var1);
   }

   public void clearAll() {
      this.buffers.values().forEach(ByteBufferBuilder::clear);
   }

   public void discardAll() {
      this.buffers.values().forEach(ByteBufferBuilder::discard);
   }

   public void close() {
      this.buffers.values().forEach(ByteBufferBuilder::close);
   }
}
