package net.minecraft.client.renderer;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuFence;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.function.Supplier;

public class MappableRingBuffer implements AutoCloseable {
   private static final int BUFFER_COUNT = 3;
   private final GpuBuffer[] buffers = new GpuBuffer[3];
   private final GpuFence[] fences = new GpuFence[3];
   private final int size;
   private int current = 0;

   public MappableRingBuffer(Supplier<String> var1, int var2, int var3) {
      super();
      GpuDevice var4 = RenderSystem.getDevice();
      if ((var2 & 1) == 0 && (var2 & 2) == 0) {
         throw new IllegalArgumentException("MappableRingBuffer requires at least one of USAGE_MAP_READ or USAGE_MAP_WRITE");
      } else {
         for(int var5 = 0; var5 < 3; ++var5) {
            this.buffers[var5] = var4.createBuffer(() -> {
               String var10000 = (String)var1.get();
               return var10000 + " #" + var5;
            }, var2, var3);
            this.fences[var5] = null;
         }

         this.size = var3;
      }
   }

   public int size() {
      return this.size;
   }

   public GpuBuffer currentBuffer() {
      GpuFence var1 = this.fences[this.current];
      if (var1 != null) {
         var1.awaitCompletion(9223372036854775807L);
         var1.close();
         this.fences[this.current] = null;
      }

      return this.buffers[this.current];
   }

   public void rotate() {
      if (this.fences[this.current] != null) {
         this.fences[this.current].close();
      }

      this.fences[this.current] = RenderSystem.getDevice().createCommandEncoder().createFence();
      this.current = (this.current + 1) % 3;
   }

   public void close() {
      for(int var1 = 0; var1 < 3; ++var1) {
         this.buffers[var1].close();
         if (this.fences[var1] != null) {
            this.fences[var1].close();
         }
      }

   }
}
