package net.minecraft.client.renderer;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.system.MemoryStack;

public class GlobalSettingsUniform implements AutoCloseable {
   public static final int UBO_SIZE = (new Std140SizeCalculator()).putIVec3().putVec3().putVec2().putFloat().putFloat().putInt().get();
   private final GpuBuffer buffer;

   public GlobalSettingsUniform() {
      super();
      this.buffer = RenderSystem.getDevice().createBuffer(() -> "Global Settings UBO", 136, UBO_SIZE);
   }

   public void update(int var1, int var2, double var3, long var5, DeltaTracker var7, int var8, Camera var9) {
      Vec3 var10 = var9.position();
      MemoryStack var11 = MemoryStack.stackPush();

      try {
         int var12 = Mth.floor(var10.x);
         int var13 = Mth.floor(var10.y);
         int var14 = Mth.floor(var10.z);
         ByteBuffer var15 = Std140Builder.onStack(var11, UBO_SIZE).putIVec3(var12, var13, var14).putVec3((float)((double)var12 - var10.x), (float)((double)var13 - var10.y), (float)((double)var14 - var10.z)).putVec2((float)var1, (float)var2).putFloat((float)var3).putFloat(((float)(var5 % 24000L) + var7.getGameTimeDeltaPartialTick(false)) / 24000.0F).putInt(var8).get();
         RenderSystem.getDevice().createCommandEncoder().writeToBuffer(this.buffer.slice(), var15);
      } catch (Throwable var17) {
         if (var11 != null) {
            try {
               var11.close();
            } catch (Throwable var16) {
               var17.addSuppressed(var16);
            }
         }

         throw var17;
      }

      if (var11 != null) {
         var11.close();
      }

      RenderSystem.setGlobalSettingsUniform(this.buffer);
   }

   public void close() {
      this.buffer.close();
   }
}
