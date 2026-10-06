package com.mojang.renderpearl.util.dx.d3d12;

import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.StructLayout;
import java.lang.foreign.ValueLayout;
import java.lang.foreign.MemoryLayout.PathElement;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

public class D3D12HeapProperties {
   public static final StructLayout LAYOUT;
   private static final VarHandle TYPE_HANDLE;
   private static final VarHandle CPU_PAGE_PROPERTY_HANDLE;
   private static final VarHandle MEMORY_POOL_PREFERENCE_HANDLE;
   private static final VarHandle CREATION_NODE_MASK_HANDLE;
   private static final VarHandle VISIBLE_NODE_MASK_HANDLE;
   private final MemorySegment segment;

   public D3D12HeapProperties(final MemorySegment segment) {
      super();
      if (segment.byteSize() != LAYOUT.byteSize()) {
         throw new IllegalArgumentException();
      } else {
         this.segment = segment;
      }
   }

   public D3D12HeapProperties(final Arena arena) {
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

   public int cpuPageProperty() {
      return CPU_PAGE_PROPERTY_HANDLE.get(this.segment);
   }

   public void cpuPageProperty(final int cpuPageProperty) {
      CPU_PAGE_PROPERTY_HANDLE.set(this.segment, cpuPageProperty);
   }

   public int memoryPoolPreference() {
      return MEMORY_POOL_PREFERENCE_HANDLE.get(this.segment);
   }

   public void memoryPoolPreference(final int memoryPoolPreference) {
      MEMORY_POOL_PREFERENCE_HANDLE.set(this.segment, memoryPoolPreference);
   }

   public int creationNodeMask() {
      return CREATION_NODE_MASK_HANDLE.get(this.segment);
   }

   public void creationNodeMask(final int creationNodeMask) {
      CREATION_NODE_MASK_HANDLE.set(this.segment, creationNodeMask);
   }

   public int visibleNodeMask() {
      return VISIBLE_NODE_MASK_HANDLE.get(this.segment);
   }

   public void visibleNodeMask(final int visibleNodeMask) {
      VISIBLE_NODE_MASK_HANDLE.set(this.segment, visibleNodeMask);
   }

   static {
      LAYOUT = MemoryLayout.structLayout(ValueLayout.JAVA_INT.withName("Type"), ValueLayout.JAVA_INT.withName("CPUPageProperty"), ValueLayout.JAVA_INT.withName("MemoryPoolPreference"), ValueLayout.JAVA_INT.withName("CreationNodeMask"), ValueLayout.JAVA_INT.withName("VisibleNodeMask"));
      TYPE_HANDLE = MethodHandles.insertCoordinates(LAYOUT.varHandle(new MemoryLayout.PathElement[]{PathElement.groupElement("Type")}), 1, new Object[]{0});
      CPU_PAGE_PROPERTY_HANDLE = MethodHandles.insertCoordinates(LAYOUT.varHandle(new MemoryLayout.PathElement[]{PathElement.groupElement("CPUPageProperty")}), 1, new Object[]{0});
      MEMORY_POOL_PREFERENCE_HANDLE = MethodHandles.insertCoordinates(LAYOUT.varHandle(new MemoryLayout.PathElement[]{PathElement.groupElement("MemoryPoolPreference")}), 1, new Object[]{0});
      CREATION_NODE_MASK_HANDLE = MethodHandles.insertCoordinates(LAYOUT.varHandle(new MemoryLayout.PathElement[]{PathElement.groupElement("CreationNodeMask")}), 1, new Object[]{0});
      VISIBLE_NODE_MASK_HANDLE = MethodHandles.insertCoordinates(LAYOUT.varHandle(new MemoryLayout.PathElement[]{PathElement.groupElement("VisibleNodeMask")}), 1, new Object[]{0});
   }
}
