package com.mojang.renderpearl.util.dx;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

public class OutPtrHelper implements AutoCloseable {
   private final Arena arena = Arena.ofConfined();
   private final MemorySegment targetPtr;

   public OutPtrHelper() {
      super();
      this.targetPtr = this.arena.allocate(COMUnknown.Types.COM_OUTPTR);
   }

   public MemorySegment target() {
      return this.targetPtr;
   }

   public MemorySegment result() {
      return this.targetPtr.get(ValueLayout.ADDRESS, 0L);
   }

   public Arena arena() {
      return this.arena;
   }

   public MemorySegment allocate(final IID iid) {
      return iid.allocate(this.arena);
   }

   public void close() {
      this.arena.close();
   }
}
