package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.Util;

public class SectionBufferBuilderPack implements AutoCloseable {
   private static final List<RenderType> RENDER_TYPES = RenderType.chunkBufferLayers();
   public static final int TOTAL_BUFFERS_SIZE;
   private final Map<RenderType, ByteBufferBuilder> buffers;

   public SectionBufferBuilderPack() {
      super();
      this.buffers = (Map)Util.make(new Reference2ObjectArrayMap(RENDER_TYPES.size()), (var0) -> {
         Iterator var1 = RENDER_TYPES.iterator();

         while(var1.hasNext()) {
            RenderType var2 = (RenderType)var1.next();
            var0.put(var2, new ByteBufferBuilder(var2.bufferSize()));
         }

      });
   }

   public ByteBufferBuilder buffer(RenderType var1) {
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

   static {
      TOTAL_BUFFERS_SIZE = RENDER_TYPES.stream().mapToInt(RenderType::bufferSize).sum();
   }
}
