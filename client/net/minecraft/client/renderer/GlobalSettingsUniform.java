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
   public static final int UBO_SIZE = (new Std140SizeCalculator()).putIVec3().putVec3().putVec2().putFloat().putFloat().putInt().putInt().get();
   private final GpuBuffer buffer;

   public GlobalSettingsUniform() {
      super();
      this.buffer = RenderSystem.getDevice().createBuffer(() -> "Global Settings UBO", 136, (long)UBO_SIZE);
   }

   public void update(int var1, int var2, double var3, long var5, DeltaTracker var7, int var8, Camera var9, boolean var10) {
      Vec3 var11 = var9.position();
      MemoryStack var12 = MemoryStack.stackPush();

      try {
         int var13 = Mth.floor(var11.x);
         int var14 = Mth.floor(var11.y);
         int var15 = Mth.floor(var11.z);
         ByteBuffer var16 = Std140Builder.onStack(var12, UBO_SIZE).putIVec3(var13, var14, var15).putVec3((float)((double)var13 - var11.x), (float)((double)var14 - var11.y), (float)((double)var15 - var11.z)).putVec2((float)var1, (float)var2).putFloat((float)var3).putFloat(((float)(var5 % 24000L) + var7.getGameTimeDeltaPartialTick(false)) / 24000.0F).putInt(var8).putInt(var10 ? 1 : 0).get();
         RenderSystem.getDevice().createCommandEncoder().writeToBuffer(this.buffer.slice(), var16);
      } catch (Throwable var18) {
         if (var12 != null) {
            try {
               var12.close();
            } catch (Throwable var17) {
               var18.addSuppressed(var17);
            }
         }

         throw var18;
      }

      if (var12 != null) {
         var12.close();
      }

      RenderSystem.setGlobalSettingsUniform(this.buffer);
   }

   public void close() {
      this.buffer.close();
   }
}
