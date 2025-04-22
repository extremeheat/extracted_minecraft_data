package net.minecraft.client.renderer;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import net.minecraft.client.DeltaTracker;
import org.lwjgl.system.MemoryStack;

public class GlobalSettingsUniform implements AutoCloseable {
   public static final int UBO_SIZE = (new Std140SizeCalculator()).putVec2().putFloat().putFloat().putInt().get();
   private final GpuBuffer buffer;

   public GlobalSettingsUniform() {
      super();
      this.buffer = RenderSystem.getDevice().createBuffer(() -> "Global Settings UBO", 136, UBO_SIZE);
   }

   public void update(int var1, int var2, double var3, long var5, DeltaTracker var7, int var8) {
      MemoryStack var9 = MemoryStack.stackPush();

      try {
         ByteBuffer var10 = Std140Builder.onStack(var9, UBO_SIZE).putVec2((float)var1, (float)var2).putFloat((float)var3).putFloat(((float)(var5 % 24000L) + var7.getGameTimeDeltaPartialTick(false)) / 24000.0F).putInt(var8).get();
         RenderSystem.getDevice().createCommandEncoder().writeToBuffer(this.buffer.slice(), var10);
      } catch (Throwable var13) {
         if (var9 != null) {
            try {
               var9.close();
            } catch (Throwable var12) {
               var13.addSuppressed(var12);
            }
         }

         throw var13;
      }

      if (var9 != null) {
         var9.close();
      }

      RenderSystem.setGlobalSettingsUniform(this.buffer);
   }

   public void close() {
      this.buffer.close();
   }
}
