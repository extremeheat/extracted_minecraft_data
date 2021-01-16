package com.google.common.escape;

import com.google.common.annotations.GwtCompatible;

@GwtCompatible(
   emulated = true
)
final class Platform {
   private static final ThreadLocal<char[]> DEST_TL = new ThreadLocal<char[]>() {
      protected char[] initialValue() {
         return new char[1024];
      }
   };

   private Platform() {
      super();
   }

   static char[] charBufferFromThreadLocal() {
      return (char[])DEST_TL.get();
   }
}
