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
      } else if ((usage & 512) != 0) {
         return 36671;
      } else {
         return (usage & 256) != 0 ? '\u8c2a' : '\u8f37';
      }
   }
}
