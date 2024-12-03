package net.minecraft.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2ObjectSortedMaps;
import java.util.HashMap;
import java.util.Map;
import java.util.SequencedMap;
import javax.annotation.Nullable;

public interface MultiBufferSource {
   static BufferSource immediate(ByteBufferBuilder var0) {
      return immediateWithBuffers(Object2ObjectSortedMaps.emptyMap(), var0);
   }

   static BufferSource immediateWithBuffers(SequencedMap<RenderType, ByteBufferBuilder> var0, ByteBufferBuilder var1) {
      return new BufferSource(var1, var0);
   }

   VertexConsumer getBuffer(RenderType var1);

   public static class BufferSource implements MultiBufferSource {
      protected final ByteBufferBuilder sharedBuffer;
      protected final SequencedMap<RenderType, ByteBufferBuilder> fixedBuffers;
      protected final Map<RenderType, BufferBuilder> startedBuilders = new HashMap();
      @Nullable
      protected RenderType lastSharedType;

      protected BufferSource(ByteBufferBuilder var1, SequencedMap<RenderType, ByteBufferBuilder> var2) {
         super();
         this.sharedBuffer = var1;
         this.fixedBuffers = var2;
      }

      public VertexConsumer getBuffer(RenderType var1) {
         BufferBuilder var2 = (BufferBuilder)this.startedBuilders.get(var1);
         if (var2 != null && !var1.canConsolidateConsecutiveGeometry()) {
            this.endBatch(var1, var2);
            var2 = null;
         }

         if (var2 != null) {
            return var2;
         } else {
            ByteBufferBuilder var3 = (ByteBufferBuilder)this.fixedBuffers.get(var1);
            if (var3 != null) {
               var2 = new BufferBuilder(var3, var1.mode(), var1.format());
            } else {
               if (this.lastSharedType != null) {
                  this.endBatch(this.lastSharedType);
               }

               var2 = new BufferBuilder(this.sharedBuffer, var1.mode(), var1.format());
               this.lastSharedType = var1;
            }

            this.startedBuilders.put(var1, var2);
            return var2;
         }
      }

      public void endLastBatch() {
         if (this.lastSharedType != null) {
            this.endBatch(this.lastSharedType);
            this.lastSharedType = null;
         }

      }

      public void endBatch() {
         this.endLastBatch();

         for(RenderType var2 : this.fixedBuffers.keySet()) {
            this.endBatch(var2);
         }

      }

      public void endBatch(RenderType var1) {
         BufferBuilder var2 = (BufferBuilder)this.startedBuilders.remove(var1);
         if (var2 != null) {
            this.endBatch(var1, var2);
         }

      }

      private void endBatch(RenderType var1, BufferBuilder var2) {
         MeshData var3 = var2.build();
         if (var3 != null) {
            if (var1.sortOnUpload()) {
               ByteBufferBuilder var4 = (ByteBufferBuilder)this.fixedBuffers.getOrDefault(var1, this.sharedBuffer);
               var3.sortQuads(var4, RenderSystem.getProjectionType().vertexSorting());
            }

            var1.draw(var3);
         }

         if (var1.equals(this.lastSharedType)) {
            this.lastSharedType = null;
         }

      }
   }
}
