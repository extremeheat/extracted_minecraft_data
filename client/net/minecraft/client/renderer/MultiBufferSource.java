package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public interface MultiBufferSource {
   static MultiBufferSource.BufferSource immediate(BufferBuilder var0) {
      return immediateWithBuffers(ImmutableMap.of(), var0);
   }

   static MultiBufferSource.BufferSource immediateWithBuffers(Map<RenderType, BufferBuilder> var0, BufferBuilder var1) {
      return new MultiBufferSource.BufferSource(var1, var0);
   }

   VertexConsumer getBuffer(RenderType var1);

   public static class BufferSource implements MultiBufferSource {
      protected final BufferBuilder builder;
      protected final Map<RenderType, BufferBuilder> fixedBuffers;
      protected Optional<RenderType> lastState = Optional.empty();
      protected final Set<BufferBuilder> startedBuffers = Sets.newHashSet();

      protected BufferSource(BufferBuilder var1, Map<RenderType, BufferBuilder> var2) {
         super();
         this.builder = var1;
         this.fixedBuffers = var2;
      }

      @Override
      public VertexConsumer getBuffer(RenderType var1) {
         Optional var2 = var1.asOptional();
         BufferBuilder var3 = this.getBuilderRaw(var1);
         if (!Objects.equals(this.lastState, var2) || !var1.canConsolidateConsecutiveGeometry()) {
            if (this.lastState.isPresent()) {
               RenderType var4 = this.lastState.get();
               if (!this.fixedBuffers.containsKey(var4)) {
                  this.endBatch(var4);
               }
            }

            if (this.startedBuffers.add(var3)) {
               var3.begin(var1.mode(), var1.format());
            }

            this.lastState = var2;
         }

         return var3;
      }

      private BufferBuilder getBuilderRaw(RenderType var1) {
         return this.fixedBuffers.getOrDefault(var1, this.builder);
      }

      public void endLastBatch() {
         if (this.lastState.isPresent()) {
            RenderType var1 = this.lastState.get();
            if (!this.fixedBuffers.containsKey(var1)) {
               this.endBatch(var1);
            }

            this.lastState = Optional.empty();
         }
      }

      public void endBatch() {
         this.lastState.ifPresent(var1 -> {
            VertexConsumer var2xx = this.getBuffer(var1);
            if (var2xx == this.builder) {
               this.endBatch(var1);
            }
         });

         for(RenderType var2 : this.fixedBuffers.keySet()) {
            this.endBatch(var2);
         }
      }

      public void endBatch(RenderType var1) {
         BufferBuilder var2 = this.getBuilderRaw(var1);
         boolean var3 = Objects.equals(this.lastState, var1.asOptional());
         if (var3 || var2 != this.builder) {
            if (this.startedBuffers.remove(var2)) {
               var1.end(var2, RenderSystem.getVertexSorting());
               if (var3) {
                  this.lastState = Optional.empty();
               }
            }
         }
      }
   }
}
