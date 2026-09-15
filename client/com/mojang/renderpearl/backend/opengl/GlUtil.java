package com.mojang.renderpearl.backend.opengl;

import com.mojang.renderpearl.api.buffers.GpuBuffer;

public class GlUtil {
   public GlUtil() {
      super();
   }

   public static int selectBufferBindTarget(final @GpuBuffer.Usage int usage) {
      if ((usage & 32) != 0) {
         return 34962;
      } else if ((usage & 64) != 0) {
         return 34963;
      } else if ((usage & 128) != 0) {
         return 35345;
      } else {
         return (usage & 512) != 0 ? '\u8f3f' : '\u8f37';
      }
   }
}
