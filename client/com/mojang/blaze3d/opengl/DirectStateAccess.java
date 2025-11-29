package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.GraphicsWorkarounds;
import com.mojang.blaze3d.buffers.GpuBuffer;
import java.nio.ByteBuffer;
import java.util.Set;
import org.jspecify.annotations.Nullable;
import org.lwjgl.opengl.ARBBufferStorage;
import org.lwjgl.opengl.ARBDirectStateAccess;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GLCapabilities;

public abstract class DirectStateAccess {
   public DirectStateAccess() {
      super();
   }

   public static DirectStateAccess create(GLCapabilities var0, Set<String> var1, GraphicsWorkarounds var2) {
      if (var0.GL_ARB_direct_state_access && GlDevice.USE_GL_ARB_direct_state_access && !var2.isGlOnDx12()) {
         var1.add("GL_ARB_direct_state_access");
         return new Core();
      } else {
         return new Emulated();
      }
   }

   abstract int createBuffer();

   abstract void bufferData(int var1, long var2, @GpuBuffer.Usage int var4);

   abstract void bufferData(int var1, ByteBuffer var2, @GpuBuffer.Usage int var3);

   abstract void bufferSubData(int var1, long var2, ByteBuffer var4, @GpuBuffer.Usage int var5);

   abstract void bufferStorage(int var1, long var2, @GpuBuffer.Usage int var4);

   abstract void bufferStorage(int var1, ByteBuffer var2, @GpuBuffer.Usage int var3);

   abstract @Nullable ByteBuffer mapBufferRange(int var1, long var2, long var4, int var6, @GpuBuffer.Usage int var7);

   abstract void unmapBuffer(int var1, @GpuBuffer.Usage int var2);

   abstract int createFrameBufferObject();

   abstract void bindFrameBufferTextures(int var1, int var2, int var3, int var4, int var5);

   abstract void blitFrameBuffers(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12);

   abstract void flushMappedBufferRange(int var1, long var2, long var4, @GpuBuffer.Usage int var6);

   abstract void copyBufferSubData(int var1, int var2, long var3, long var5, long var7);

   static class Core extends DirectStateAccess {
      Core() {
         super();
      }

      int createBuffer() {
         GlStateManager.incrementTrackedBuffers();
         return ARBDirectStateAccess.glCreateBuffers();
      }

      void bufferData(int var1, long var2, @GpuBuffer.Usage int var4) {
         ARBDirectStateAccess.glNamedBufferData(var1, var2, GlConst.bufferUsageToGlEnum(var4));
      }

      void bufferData(int var1, ByteBuffer var2, @GpuBuffer.Usage int var3) {
         ARBDirectStateAccess.glNamedBufferData(var1, var2, GlConst.bufferUsageToGlEnum(var3));
      }

      void bufferSubData(int var1, long var2, ByteBuffer var4, @GpuBuffer.Usage int var5) {
         ARBDirectStateAccess.glNamedBufferSubData(var1, var2, var4);
      }

      void bufferStorage(int var1, long var2, @GpuBuffer.Usage int var4) {
         ARBDirectStateAccess.glNamedBufferStorage(var1, var2, GlConst.bufferUsageToGlFlag(var4));
      }

      void bufferStorage(int var1, ByteBuffer var2, @GpuBuffer.Usage int var3) {
         ARBDirectStateAccess.glNamedBufferStorage(var1, var2, GlConst.bufferUsageToGlFlag(var3));
      }

      @Nullable ByteBuffer mapBufferRange(int var1, long var2, long var4, int var6, @GpuBuffer.Usage int var7) {
         return ARBDirectStateAccess.glMapNamedBufferRange(var1, var2, var4, var6);
      }

      void unmapBuffer(int var1, int var2) {
         ARBDirectStateAccess.glUnmapNamedBuffer(var1);
      }

      public int createFrameBufferObject() {
         return ARBDirectStateAccess.glCreateFramebuffers();
      }

      public void bindFrameBufferTextures(int var1, int var2, int var3, int var4, @GpuBuffer.Usage int var5) {
         ARBDirectStateAccess.glNamedFramebufferTexture(var1, 36064, var2, var4);
         ARBDirectStateAccess.glNamedFramebufferTexture(var1, 36096, var3, var4);
         if (var5 != 0) {
            GlStateManager._glBindFramebuffer(var5, var1);
         }

      }

      public void blitFrameBuffers(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12) {
         ARBDirectStateAccess.glBlitNamedFramebuffer(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12);
      }

      void flushMappedBufferRange(int var1, long var2, long var4, @GpuBuffer.Usage int var6) {
         ARBDirectStateAccess.glFlushMappedNamedBufferRange(var1, var2, var4);
      }

      void copyBufferSubData(int var1, int var2, long var3, long var5, long var7) {
         ARBDirectStateAccess.glCopyNamedBufferSubData(var1, var2, var3, var5, var7);
      }
   }

   static class Emulated extends DirectStateAccess {
      Emulated() {
         super();
      }

      private int selectBufferBindTarget(@GpuBuffer.Usage int var1) {
         if ((var1 & 32) != 0) {
            return 34962;
         } else if ((var1 & 64) != 0) {
            return 34963;
         } else {
            return (var1 & 128) != 0 ? '\u8a11' : '\u8f37';
         }
      }

      int createBuffer() {
         return GlStateManager._glGenBuffers();
      }

      void bufferData(int var1, long var2, @GpuBuffer.Usage int var4) {
         int var5 = this.selectBufferBindTarget(var4);
         GlStateManager._glBindBuffer(var5, var1);
         GlStateManager._glBufferData(var5, var2, GlConst.bufferUsageToGlEnum(var4));
         GlStateManager._glBindBuffer(var5, 0);
      }

      void bufferData(int var1, ByteBuffer var2, @GpuBuffer.Usage int var3) {
         int var4 = this.selectBufferBindTarget(var3);
         GlStateManager._glBindBuffer(var4, var1);
         GlStateManager._glBufferData(var4, var2, GlConst.bufferUsageToGlEnum(var3));
         GlStateManager._glBindBuffer(var4, 0);
      }

      void bufferSubData(int var1, long var2, ByteBuffer var4, @GpuBuffer.Usage int var5) {
         int var6 = this.selectBufferBindTarget(var5);
         GlStateManager._glBindBuffer(var6, var1);
         GlStateManager._glBufferSubData(var6, var2, var4);
         GlStateManager._glBindBuffer(var6, 0);
      }

      void bufferStorage(int var1, long var2, @GpuBuffer.Usage int var4) {
         int var5 = this.selectBufferBindTarget(var4);
         GlStateManager._glBindBuffer(var5, var1);
         ARBBufferStorage.glBufferStorage(var5, var2, GlConst.bufferUsageToGlFlag(var4));
         GlStateManager._glBindBuffer(var5, 0);
      }

      void bufferStorage(int var1, ByteBuffer var2, @GpuBuffer.Usage int var3) {
         int var4 = this.selectBufferBindTarget(var3);
         GlStateManager._glBindBuffer(var4, var1);
         ARBBufferStorage.glBufferStorage(var4, var2, GlConst.bufferUsageToGlFlag(var3));
         GlStateManager._glBindBuffer(var4, 0);
      }

      @Nullable ByteBuffer mapBufferRange(int var1, long var2, long var4, int var6, @GpuBuffer.Usage int var7) {
         int var8 = this.selectBufferBindTarget(var7);
         GlStateManager._glBindBuffer(var8, var1);
         ByteBuffer var9 = GlStateManager._glMapBufferRange(var8, var2, var4, var6);
         GlStateManager._glBindBuffer(var8, 0);
         return var9;
      }

      void unmapBuffer(int var1, @GpuBuffer.Usage int var2) {
         int var3 = this.selectBufferBindTarget(var2);
         GlStateManager._glBindBuffer(var3, var1);
         GlStateManager._glUnmapBuffer(var3);
         GlStateManager._glBindBuffer(var3, 0);
      }

      void flushMappedBufferRange(int var1, long var2, long var4, @GpuBuffer.Usage int var6) {
         int var7 = this.selectBufferBindTarget(var6);
         GlStateManager._glBindBuffer(var7, var1);
         GL30.glFlushMappedBufferRange(var7, var2, var4);
         GlStateManager._glBindBuffer(var7, 0);
      }

      void copyBufferSubData(int var1, int var2, long var3, long var5, long var7) {
         GlStateManager._glBindBuffer(36662, var1);
         GlStateManager._glBindBuffer(36663, var2);
         GL31.glCopyBufferSubData(36662, 36663, var3, var5, var7);
         GlStateManager._glBindBuffer(36662, 0);
         GlStateManager._glBindBuffer(36663, 0);
      }

      public int createFrameBufferObject() {
         return GlStateManager.glGenFramebuffers();
      }

      public void bindFrameBufferTextures(int var1, int var2, int var3, int var4, int var5) {
         int var6 = var5 == 0 ? '\u8ca9' : var5;
         int var7 = GlStateManager.getFrameBuffer(var6);
         GlStateManager._glBindFramebuffer(var6, var1);
         GlStateManager._glFramebufferTexture2D(var6, 36064, 3553, var2, var4);
         GlStateManager._glFramebufferTexture2D(var6, 36096, 3553, var3, var4);
         if (var5 == 0) {
            GlStateManager._glBindFramebuffer(var6, var7);
         }

      }

      public void blitFrameBuffers(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12) {
         int var13 = GlStateManager.getFrameBuffer(36008);
         int var14 = GlStateManager.getFrameBuffer(36009);
         GlStateManager._glBindFramebuffer(36008, var1);
         GlStateManager._glBindFramebuffer(36009, var2);
         GlStateManager._glBlitFrameBuffer(var3, var4, var5, var6, var7, var8, var9, var10, var11, var12);
         GlStateManager._glBindFramebuffer(36008, var13);
         GlStateManager._glBindFramebuffer(36009, var14);
      }
   }
}
