package com.mojang.renderpearl.util.dx.d3d12;

import com.mojang.renderpearl.util.dx.dxgi.DXGISampleDesc;
import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.StructLayout;
import java.lang.foreign.ValueLayout;
import java.lang.foreign.MemoryLayout.PathElement;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

public class D3D12ResourceDesc {
   public static final StructLayout LAYOUT;
   private static final VarHandle DIMENSION_HANDLE;
   private static final VarHandle ALIGNMENT_HANDLE;
   private static final VarHandle WIDTH_HANDLE;
   private static final VarHandle HEIGHT_HANDLE;
   private static final VarHandle DEPTH_OR_ARRAY_STRIDE_HANDLE;
   private static final VarHandle MIP_LEVELS_HANDLE;
   private static final VarHandle FORMAT_HANDLE;
   private static final long SAMPLE_DESC_OFFSET;
   private static final VarHandle LAYOUT_HANDLE;
   private static final VarHandle FLAGS_HANDLE;
   private static final VarHandle NODE_MASK_HANDLE;
   private final MemorySegment segment;

   public D3D12ResourceDesc(final MemorySegment segment) {
      super();
      if (segment.byteSize() != LAYOUT.byteSize()) {
         throw new IllegalArgumentException();
      } else {
         this.segment = segment;
      }
   }

   public D3D12ResourceDesc(final Arena arena) {
      this(arena.allocate(LAYOUT));
   }

   public MemorySegment address() {
      return this.segment;
   }

   public int dimension() {
      return DIMENSION_HANDLE.get(this.segment);
   }

   public void dimension(final int dimensions) {
      DIMENSION_HANDLE.set(this.segment, dimensions);
   }

   public int alignment() {
      return ALIGNMENT_HANDLE.get(this.segment);
   }

   public void alignment(final int alignment) {
      ALIGNMENT_HANDLE.set(this.segment, alignment);
   }

   public long width() {
      return WIDTH_HANDLE.get(this.segment);
   }

   public void width(final long width) {
      WIDTH_HANDLE.set(this.segment, width);
   }

   public int height() {
      return HEIGHT_HANDLE.get(this.segment);
   }

   public void height(final int height) {
      HEIGHT_HANDLE.set(this.segment, height);
   }

   public short depthOrArrayStride() {
      return DEPTH_OR_ARRAY_STRIDE_HANDLE.get(this.segment);
   }

   public void depthOrArrayStride(final short depthOrArrayStride) {
      DEPTH_OR_ARRAY_STRIDE_HANDLE.set(this.segment, depthOrArrayStride);
   }

   public short mipLevels() {
      return MIP_LEVELS_HANDLE.get(this.segment);
   }

   public void mipLevels(final short mipLevels) {
      MIP_LEVELS_HANDLE.set(this.segment, mipLevels);
   }

   public int format() {
      return FORMAT_HANDLE.get(this.segment);
   }

   public void format(final int format) {
      FORMAT_HANDLE.set(this.segment, format);
   }

   public DXGISampleDesc sampleDesc() {
      return new DXGISampleDesc(this.segment.asSlice(SAMPLE_DESC_OFFSET, DXGISampleDesc.LAYOUT.byteSize()));
   }

   public int layout() {
      return LAYOUT_HANDLE.get(this.segment);
   }

   public void layout(final int layout) {
      LAYOUT_HANDLE.set(this.segment, layout);
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
      LAYOUT = MemoryLayout.structLayout(ValueLayout.JAVA_INT.withName("Dimension"), ValueLayout.JAVA_INT.withName("__PADDING1"), ValueLayout.JAVA_LONG.withName("Alignment"), ValueLayout.JAVA_LONG.withName("Width"), ValueLayout.JAVA_INT.withName("Height"), ValueLayout.JAVA_SHORT.withName("DepthOrArrayStride"), ValueLayout.JAVA_SHORT.withName("MipLevels"), ValueLayout.JAVA_INT.withName("Format"), DXGISampleDesc.LAYOUT.withName("SampleDesc"), ValueLayout.JAVA_INT.withName("Layout"), ValueLayout.JAVA_INT.withName("Flags"), ValueLayout.JAVA_INT.withName("NodeMask"), ValueLayout.JAVA_INT.withName("__PADDING2"));
      DIMENSION_HANDLE = MethodHandles.insertCoordinates(LAYOUT.varHandle(new MemoryLayout.PathElement[]{PathElement.groupElement("Dimension")}), 1, new Object[]{0});
      ALIGNMENT_HANDLE = MethodHandles.insertCoordinates(LAYOUT.varHandle(new MemoryLayout.PathElement[]{PathElement.groupElement("Alignment")}), 1, new Object[]{0});
      WIDTH_HANDLE = MethodHandles.insertCoordinates(LAYOUT.varHandle(new MemoryLayout.PathElement[]{PathElement.groupElement("Width")}), 1, new Object[]{0});
      HEIGHT_HANDLE = MethodHandles.insertCoordinates(LAYOUT.varHandle(new MemoryLayout.PathElement[]{PathElement.groupElement("Height")}), 1, new Object[]{0});
      DEPTH_OR_ARRAY_STRIDE_HANDLE = MethodHandles.insertCoordinates(LAYOUT.varHandle(new MemoryLayout.PathElement[]{PathElement.groupElement("DepthOrArrayStride")}), 1, new Object[]{0});
      MIP_LEVELS_HANDLE = MethodHandles.insertCoordinates(LAYOUT.varHandle(new MemoryLayout.PathElement[]{PathElement.groupElement("MipLevels")}), 1, new Object[]{0});
      FORMAT_HANDLE = MethodHandles.insertCoordinates(LAYOUT.varHandle(new MemoryLayout.PathElement[]{PathElement.groupElement("Format")}), 1, new Object[]{0});
      SAMPLE_DESC_OFFSET = LAYOUT.byteOffset(new MemoryLayout.PathElement[]{PathElement.groupElement("SampleDesc")});
      LAYOUT_HANDLE = MethodHandles.insertCoordinates(LAYOUT.varHandle(new MemoryLayout.PathElement[]{PathElement.groupElement("Layout")}), 1, new Object[]{0});
      FLAGS_HANDLE = MethodHandles.insertCoordinates(LAYOUT.varHandle(new MemoryLayout.PathElement[]{PathElement.groupElement("Flags")}), 1, new Object[]{0});
      NODE_MASK_HANDLE = MethodHandles.insertCoordinates(LAYOUT.varHandle(new MemoryLayout.PathElement[]{PathElement.groupElement("NodeMask")}), 1, new Object[]{0});
   }
}
