package com.mojang.renderpearl.util.dx.dxgi;

import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.StructLayout;
import java.lang.foreign.ValueLayout;
import java.lang.foreign.MemoryLayout.PathElement;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

public class DXGISwapchainDesc1 {
   public static final StructLayout LAYOUT;
   private static final VarHandle WIDTH_HANDLE;
   private static final VarHandle HEIGHT_HANDLE;
   private static final VarHandle FORMAT_HANDLE;
   private static final VarHandle STEREO_HANDLE;
   private static final long SAMPLE_DESC_OFFSET;
   private static final VarHandle BUFFER_USAGE_HANDLE;
   private static final VarHandle BUFFER_COUNT_HANDLE;
   private static final VarHandle SCALING_HANDLE;
   private static final VarHandle SWAP_EFFECT_HANDLE;
   private static final VarHandle ALPHA_MODE_HANDLE;
   private static final VarHandle FLAGS_HANDLE;
   private final MemorySegment segment;

   public DXGISwapchainDesc1(final MemorySegment segment) {
      super();
      if (segment.byteSize() != LAYOUT.byteSize()) {
         throw new IllegalArgumentException();
      } else {
         this.segment = segment;
      }
   }

   public DXGISwapchainDesc1(final Arena arena) {
      this(arena.allocate(LAYOUT));
   }

   public MemorySegment address() {
      return this.segment;
   }

   public int width() {
      return WIDTH_HANDLE.get(this.segment);
   }

   public void width(final int width) {
      WIDTH_HANDLE.set(this.segment, width);
   }

   public int height() {
      return HEIGHT_HANDLE.get(this.segment);
   }

   public void height(final int height) {
      HEIGHT_HANDLE.set(this.segment, height);
   }

   public int format() {
      return FORMAT_HANDLE.get(this.segment);
   }

   public void format(final int format) {
      FORMAT_HANDLE.set(this.segment, format);
   }

   public boolean stereo() {
      return STEREO_HANDLE.get(this.segment) != 0;
   }

   public void stereo(final boolean stereo) {
      STEREO_HANDLE.set(this.segment, stereo ? 1 : 0);
   }

   public DXGISampleDesc sampleDesc() {
      return new DXGISampleDesc(this.segment.asSlice(SAMPLE_DESC_OFFSET, DXGISampleDesc.LAYOUT.byteSize()));
   }

   public int bufferUsage() {
      return BUFFER_USAGE_HANDLE.get(this.segment);
   }

   public void bufferUsage(final int bufferUsage) {
      BUFFER_USAGE_HANDLE.set(this.segment, bufferUsage);
   }

   public int bufferCount() {
      return BUFFER_COUNT_HANDLE.get(this.segment);
   }

   public void bufferCount(final int bufferCount) {
      BUFFER_COUNT_HANDLE.set(this.segment, bufferCount);
   }

   public int scaling() {
      return SCALING_HANDLE.get(this.segment);
   }

   public void scaling(final int scaling) {
      SCALING_HANDLE.set(this.segment, scaling);
   }

   public int swapEffect() {
      return SWAP_EFFECT_HANDLE.get(this.segment);
   }

   public void swapEffect(final int swapEffect) {
      SWAP_EFFECT_HANDLE.set(this.segment, swapEffect);
   }

   public int alphaMode() {
      return ALPHA_MODE_HANDLE.get(this.segment);
   }

   public void alphaMode(final int alphaMode) {
      ALPHA_MODE_HANDLE.set(this.segment, alphaMode);
   }

   public int flags() {
      return FLAGS_HANDLE.get(this.segment);
   }

   public void flags(final int flags) {
      FLAGS_HANDLE.set(this.segment, flags);
   }

   static {
      LAYOUT = MemoryLayout.structLayout(ValueLayout.JAVA_INT.withName("Width"), ValueLayout.JAVA_INT.withName("Height"), ValueLayout.JAVA_INT.withName("Format"), ValueLayout.JAVA_INT.withName("Stereo"), DXGISampleDesc.LAYOUT.withName("SampleDesc"), ValueLayout.JAVA_INT.withName("BufferUsage"), ValueLayout.JAVA_INT.withName("BufferCount"), ValueLayout.JAVA_INT.withName("Scaling"), ValueLayout.JAVA_INT.withName("SwapEffect"), ValueLayout.JAVA_INT.withName("AlphaMode"), ValueLayout.JAVA_INT.withName("Flags"));
      WIDTH_HANDLE = MethodHandles.insertCoordinates(LAYOUT.varHandle(new MemoryLayout.PathElement[]{PathElement.groupElement("Width")}), 1, new Object[]{0});
      HEIGHT_HANDLE = MethodHandles.insertCoordinates(LAYOUT.varHandle(new MemoryLayout.PathElement[]{PathElement.groupElement("Height")}), 1, new Object[]{0});
      FORMAT_HANDLE = MethodHandles.insertCoordinates(LAYOUT.varHandle(new MemoryLayout.PathElement[]{PathElement.groupElement("Format")}), 1, new Object[]{0});
      STEREO_HANDLE = MethodHandles.insertCoordinates(LAYOUT.varHandle(new MemoryLayout.PathElement[]{PathElement.groupElement("Stereo")}), 1, new Object[]{0});
      SAMPLE_DESC_OFFSET = LAYOUT.byteOffset(new MemoryLayout.PathElement[]{PathElement.groupElement("SampleDesc")});
      BUFFER_USAGE_HANDLE = MethodHandles.insertCoordinates(LAYOUT.varHandle(new MemoryLayout.PathElement[]{PathElement.groupElement("BufferUsage")}), 1, new Object[]{0});
      BUFFER_COUNT_HANDLE = MethodHandles.insertCoordinates(LAYOUT.varHandle(new MemoryLayout.PathElement[]{PathElement.groupElement("BufferCount")}), 1, new Object[]{0});
      SCALING_HANDLE = MethodHandles.insertCoordinates(LAYOUT.varHandle(new MemoryLayout.PathElement[]{PathElement.groupElement("Scaling")}), 1, new Object[]{0});
      SWAP_EFFECT_HANDLE = MethodHandles.insertCoordinates(LAYOUT.varHandle(new MemoryLayout.PathElement[]{PathElement.groupElement("SwapEffect")}), 1, new Object[]{0});
      ALPHA_MODE_HANDLE = MethodHandles.insertCoordinates(LAYOUT.varHandle(new MemoryLayout.PathElement[]{PathElement.groupElement("AlphaMode")}), 1, new Object[]{0});
      FLAGS_HANDLE = MethodHandles.insertCoordinates(LAYOUT.varHandle(new MemoryLayout.PathElement[]{PathElement.groupElement("Flags")}), 1, new Object[]{0});
   }
}
