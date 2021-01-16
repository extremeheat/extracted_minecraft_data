package it.unimi.dsi.fastutil;

public interface Size64 {
   long size64();

   /** @deprecated */
   @Deprecated
   default int size() {
      return (int)Math.min(2147483647L, this.size64());
   }
}
