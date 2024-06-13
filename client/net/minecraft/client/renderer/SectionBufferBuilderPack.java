package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import java.util.List;
import java.util.Map;
import net.minecraft.Util;

public class SectionBufferBuilderPack implements AutoCloseable {
   private static final List<RenderType> RENDER_TYPES = RenderType.chunkBufferLayers();
   public static final int TOTAL_BUFFERS_SIZE = RENDER_TYPES.stream().mapToInt(RenderType::bufferSize).sum();
   private final Map<RenderType, ByteBufferBuilder> buffers = Util.make(new Reference2ObjectArrayMap(RENDER_TYPES.size()), var0 -> {
      for (RenderType var2 : RENDER_TYPES) {
         var0.put(var2, new ByteBufferBuilder(var2.bufferSize()));
      }
   });

   public SectionBufferBuilderPack() {
      super();
   }

   public ByteBufferBuilder buffer(RenderType var1) {
      return this.buffers.get(var1);
   }

   public void clearAll() {
      this.buffers.values().forEach(ByteBufferBuilder::clear);
   }

   public void discardAll() {
      this.buffers.values().forEach(ByteBufferBuilder::discard);
   }

   @Override
   public void close() {
      this.buffers.values().forEach(ByteBufferBuilder::close);
   }
}
