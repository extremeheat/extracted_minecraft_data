package com.mojang.renderpearl.api.pipeline;

public enum IndexType {
   SHORT(2),
   INT(4);

   public final int bytes;

   private IndexType(final int bytes) {
      this.bytes = bytes;
   }

   public static IndexType least(final int length) {
      return (length & -65536) != 0 ? INT : SHORT;
   }

   // $FF: synthetic method
   private static IndexType[] $values() {
      return new IndexType[]{SHORT, INT};
   }
}
