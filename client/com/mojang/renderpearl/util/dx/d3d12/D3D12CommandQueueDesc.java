package com.mojang.renderpearl.util.dx.d3d12;

import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.StructLayout;
import java.lang.foreign.ValueLayout;
import java.lang.foreign.MemoryLayout.PathElement;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

public class D3D12CommandQueueDesc {
   public static final StructLayout LAYOUT;
   private static final VarHandle TYPE_HANDLE;
   private static final VarHandle PRIORITY_HANDLE;
   private static final VarHandle FLAGS_HANDLE;
   private static final VarHandle NODE_MASK_HANDLE;
   private final MemorySegment segment;

   public D3D12CommandQueueDesc(final MemorySegment segment) {
      super();
      if (segment.byteSize() != LAYOUT.byteSize()) {
         throw new IllegalArgumentException();
      } else {
         this.segment = segment;
      }
   }

   public D3D12CommandQueueDesc(final Arena arena) {
      this(arena.allocate(LAYOUT));
   }

   public MemorySegment address() {
      return this.segment;
   }

   public int type() {
      return TYPE_HANDLE.get(this.segment);
   }

   public void type(final int type) {
      TYPE_HANDLE.set(this.segment, type);
   }

   public int priority() {
      return PRIORITY_HANDLE.get(this.segment);
   }

   public void priority(final int priority) {
      PRIORITY_HANDLE.set(this.segment, priority);
   }

   public int flags() {
      return FLAGS_HANDLE.get(this.segment);
   }

   public void flags(final int flags) {
      FLAGS_HANDLE.set(this.segment, flags);
   }

   public int nodeMask() {
      return NODE_MASK_HANDLE.get(this.segment);
   }

   public void nodeMask(final int nodeMask) {
      NODE_MASK_HANDLE.set(this.segment, nodeMask);
   }

   static {
      LAYOUT = MemoryLayout.structLayout(ValueLayout.JAVA_INT.withName("Type"), ValueLayout.JAVA_INT.withName("Priority"), ValueLayout.JAVA_INT.withName("Flags"), ValueLayout.JAVA_INT.withName("NodeMask"));
      TYPE_HANDLE = MethodHandles.insertCoordinates(LAYOUT.varHandle(new MemoryLayout.PathElement[]{PathElement.groupElement("Type")}), 1, new Object[]{0});
      PRIORITY_HANDLE = MethodHandles.insertCoordinates(LAYOUT.varHandle(new MemoryLayout.PathElement[]{PathElement.groupElement("Priority")}), 1, new Object[]{0});
      FLAGS_HANDLE = MethodHandles.insertCoordinates(LAYOUT.varHandle(new MemoryLayout.PathElement[]{PathElement.groupElement("Flags")}), 1, new Object[]{0});
      NODE_MASK_HANDLE = MethodHandles.insertCoordinates(LAYOUT.varHandle(new MemoryLayout.PathElement[]{PathElement.groupElement("NodeMask")}), 1, new Object[]{0});
   }
}
