package com.mojang.renderpearl.util.dx.dxgi;

import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.StructLayout;
import java.lang.foreign.ValueLayout;
import java.lang.foreign.MemoryLayout.PathElement;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

public class DXGISampleDesc {
   public static final StructLayout LAYOUT;
   private static final VarHandle COUNT_HANDLE;
   private static final VarHandle QUALITY_HANDLE;
   private final MemorySegment segment;

   public DXGISampleDesc(final MemorySegment segment) {
      super();
      if (segment.byteSize() != LAYOUT.byteSize()) {
         throw new IllegalArgumentException();
      } else {
         this.segment = segment;
      }
   }

   public DXGISampleDesc(final Arena arena) {
      this(arena.allocate(LAYOUT));
   }

   public int count() {
      return COUNT_HANDLE.get(this.segment);
   }

   public void count(final int count) {
      COUNT_HANDLE.set(this.segment, count);
   }

   public int quality() {
      return QUALITY_HANDLE.get(this.segment);
   }

   public void quality(final int quality) {
      QUALITY_HANDLE.set(this.segment, quality);
   }

   static {
      LAYOUT = MemoryLayout.structLayout(ValueLayout.JAVA_INT.withName("Count"), ValueLayout.JAVA_INT.withName("Quality"));
      COUNT_HANDLE = MethodHandles.insertCoordinates(LAYOUT.varHandle(new MemoryLayout.PathElement[]{PathElement.groupElement("Count")}), 1, new Object[]{0});
      QUALITY_HANDLE = MethodHandles.insertCoordinates(LAYOUT.varHandle(new MemoryLayout.PathElement[]{PathElement.groupElement("Quality")}), 1, new Object[]{0});
   }
}
