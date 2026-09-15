package com.mojang.renderpearl.util;

import org.jspecify.annotations.Nullable;

public interface UncheckedAutoCloseable extends AutoCloseable {
   void close();

   static void safeClose(final @Nullable UncheckedAutoCloseable closeable) {
      if (closeable != null) {
         closeable.close();
      }
   }
}
