package com.mojang.blaze3d.opengl;

import java.nio.ByteBuffer;
import java.util.Set;
import javax.annotation.Nullable;
import org.lwjgl.opengl.ARBBufferStorage;
import org.lwjgl.opengl.ARBDirectStateAccess;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GLCapabilities;

public abstract class DirectStateAccess {
   public DirectStateAccess() {
      super();
   }

   public static DirectStateAccess create(GLCapabilities var0, Set<String> var1) {
      if (var0.GL_ARB_direct_state_access && GlDevice.USE_GL_ARB_direct_state_access) {
         var1.add("GL_ARB_direct_state_access");
         return new Core();
      } else {
         return new Emulated();
      }
   }

   abstract int createBuffer();

   abstract void bufferData(int var1, long var2, int var4);

   abstract void bufferData(int var1, ByteBuffer var2, int var3);

   abstract void bufferSubData(int var1, int var2, ByteBuffer var3);

   abstract void bufferStorage(int var1, long var2, int var4);

   abstract void bufferStorage(int var1, ByteBuffer var2, int var3);

   @Nullable
   abstract ByteBuffer mapBufferRange(int var1, int var2, int var3, int var4);

   abstract void unmapBuffer(int var1);

   abstract int createFrameBufferObject();

   abstract void bindFrameBufferTextures(int var1, int var2, int var3, int var4, int var5);

   abstract void blitFrameBuffers(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12);

   abstract void flushMappedBufferRange(int var1, int var2, int var3);

   abstract void copyBufferSubData(int var1, int var2, int var3, int var4, int var5);

   static class Core extends DirectStateAccess {
      Core() {
         super();
      }

      int createBuffer() {
         return ARBDirectStateAccess.glCreateBuffers();
      }

      void bufferData(int var1, long var2, int var4) {
         ARBDirectStateAccess.glNamedBufferData(var1, var2, var4);
      }

      void bufferData(int var1, ByteBuffer var2, int var3) {
         ARBDirectStateAccess.glNamedBufferData(var1, var2, var3);
      }

      void bufferSubData(int var1, int var2, ByteBuffer var3) {
         ARBDirectStateAccess.glNamedBufferSubData(var1, (long)var2, var3);
      }

      void bufferStorage(int var1, long var2, int var4) {
         ARBDirectStateAccess.glNamedBufferStorage(var1, var2, var4);
      }

      void bufferStorage(int var1, ByteBuffer var2, int var3) {
         ARBDirectStateAccess.glNamedBufferStorage(var1, var2, var3);
      }

      @Nullable
      ByteBuffer mapBufferRange(int var1, int var2, int var3, int var4) {
         return ARBDirectStateAccess.glMapNamedBufferRange(var1, (long)var2, (long)var3, var4);
      }

      void unmapBuffer(int var1) {
         ARBDirectStateAccess.glUnmapNamedBuffer(var1);
      }

      public int createFrameBufferObject() {
         return ARBDirectStateAccess.glCreateFramebuffers();
      }

      public void bindFrameBufferTextures(int var1, int var2, int var3, int var4, int var5) {
         ARBDirectStateAccess.glNamedFramebufferTexture(var1, 36064, var2, var4);
         ARBDirectStateAccess.glNamedFramebufferTexture(var1, 36096, var3, var4);
         if (var5 != 0) {
            GlStateManager._glBindFramebuffer(var5, var1);
         }

      }

      public void blitFrameBuffers(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12) {
         ARBDirectStateAccess.glBlitNamedFramebuffer(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12);
      }

      void flushMappedBufferRange(int var1, int var2, int var3) {
         ARBDirectStateAccess.glFlushMappedNamedBufferRange(var1, (long)var2, (long)var3);
      }

      void copyBufferSubData(int var1, int var2, int var3, int var4, int var5) {
         ARBDirectStateAccess.glCopyNamedBufferSubData(var1, var2, (long)var3, (long)var4, (long)var5);
      }
   }

   static class Emulated extends DirectStateAccess {
      Emulated() {
         super();
      }

      int createBuffer() {
         return GlStateManager._glGenBuffers();
      }

      void bufferData(int var1, long var2, int var4) {
         GlStateManager._glBindBuffer(36663, var1);
         GlStateManager._glBufferData(36663, var2, GlConst.bufferUsageToGlEnum(var4));
         GlStateManager._glBindBuffer(36663, 0);
      }

      void bufferData(int var1, ByteBuffer var2, int var3) {
         GlStateManager._glBindBuffer(36663, var1);
         GlStateManager._glBufferData(36663, var2, GlConst.bufferUsageToGlEnum(var3));
         GlStateManager._glBindBuffer(36663, 0);
      }

      void bufferSubData(int var1, int var2, ByteBuffer var3) {
         GlStateManager._glBindBuffer(36663, var1);
         GlStateManager._glBufferSubData(36663, var2, var3);
         GlStateManager._glBindBuffer(36663, 0);
      }

      void bufferStorage(int var1, long var2, int var4) {
         GlStateManager._glBindBuffer(36663, var1);
         ARBBufferStorage.glBufferStorage(36663, var2, var4);
         GlStateManager._glBindBuffer(36663, 0);
      }

      void bufferStorage(int var1, ByteBuffer var2, int var3) {
         GlStateManager._glBindBuffer(36663, var1);
         ARBBufferStorage.glBufferStorage(36663, var2, var3);
         GlStateManager._glBindBuffer(36663, 0);
      }

      @Nullable
      ByteBuffer mapBufferRange(int var1, int var2, int var3, int var4) {
         GlStateManager._glBindBuffer(36663, var1);
         ByteBuffer var5 = GlStateManager._glMapBufferRange(36663, var2, var3, var4);
         GlStateManager._glBindBuffer(36663, 0);
         return var5;
      }

      void unmapBuffer(int var1) {
         GlStateManager._glBindBuffer(36663, var1);
         GlStateManager._glUnmapBuffer(36663);
         GlStateManager._glBindBuffer(36663, 0);
      }

      void flushMappedBufferRange(int var1, int var2, int var3) {
         GlStateManager._glBindBuffer(36663, var1);
         GL30.glFlushMappedBufferRange(36663, (long)var2, (long)var3);
         GlStateManager._glBindBuffer(36663, 0);
      }

      void copyBufferSubData(int var1, int var2, int var3, int var4, int var5) {
         GlStateManager._glBindBuffer(36662, var1);
         GlStateManager._glBindBuffer(36663, var2);
         GL31.glCopyBufferSubData(36662, 36663, (long)var3, (long)var4, (long)var5);
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
