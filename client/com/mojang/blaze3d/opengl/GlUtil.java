package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.buffers.GpuBuffer;

public class GlUtil {
   public GlUtil() {
      super();
   }

   public static int selectBufferBindTarget(final @GpuBuffer.Usage int usage) {
      if ((usage & 32) != 0) {
         return 34962;
      } else if ((usage & 64) != 0) {
         return 34963;
      } else {
         return (usage & 128) != 0 ? '\u8a11' : '\u8f37';
      }
   }
}
