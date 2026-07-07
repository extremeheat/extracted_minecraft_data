package com.mojang.renderpearl.api;

public enum GpuFormat {
   R8_UNORM(GpuFormat.ComponentType.UNORM_8, 1),
   R8_SNORM(GpuFormat.ComponentType.SNORM_8, 1),
   RG8_UNORM(GpuFormat.ComponentType.UNORM_8, 2),
   RG8_SNORM(GpuFormat.ComponentType.SNORM_8, 2),
   RGB8_UNORM(GpuFormat.ComponentType.UNORM_8, 3),
   RGB8_SNORM(GpuFormat.ComponentType.SNORM_8, 3),
   RGBA8_UNORM(GpuFormat.ComponentType.UNORM_8, 4),
   RGBA8_SNORM(GpuFormat.ComponentType.SNORM_8, 4),
   R16_UNORM(GpuFormat.ComponentType.UNORM_16, 1),
   R16_SNORM(GpuFormat.ComponentType.SNORM_16, 1),
   RG16_UNORM(GpuFormat.ComponentType.UNORM_16, 2),
   RG16_SNORM(GpuFormat.ComponentType.SNORM_16, 2),
   RGB16_UNORM(GpuFormat.ComponentType.UNORM_16, 3),
   RGB16_SNORM(GpuFormat.ComponentType.SNORM_16, 3),
   RGBA16_UNORM(GpuFormat.ComponentType.UNORM_16, 4),
   RGBA16_SNORM(GpuFormat.ComponentType.SNORM_16, 4),
   R8_UINT(GpuFormat.ComponentType.UINT_8, 1),
   R8_SINT(GpuFormat.ComponentType.SINT_8, 1),
   RG8_UINT(GpuFormat.ComponentType.UINT_8, 2),
   RG8_SINT(GpuFormat.ComponentType.SINT_8, 2),
   RGB8_UINT(GpuFormat.ComponentType.UINT_8, 3),
   RGB8_SINT(GpuFormat.ComponentType.SINT_8, 3),
   RGBA8_UINT(GpuFormat.ComponentType.UINT_8, 4),
   RGBA8_SINT(GpuFormat.ComponentType.SINT_8, 4),
   R16_UINT(GpuFormat.ComponentType.UINT_16, 1),
   R16_SINT(GpuFormat.ComponentType.SINT_16, 1),
   RG16_UINT(GpuFormat.ComponentType.UINT_16, 2),
   RG16_SINT(GpuFormat.ComponentType.SINT_16, 2),
   RGB16_UINT(GpuFormat.ComponentType.UINT_16, 3),
   RGB16_SINT(GpuFormat.ComponentType.SINT_16, 3),
   RGBA16_UINT(GpuFormat.ComponentType.UINT_16, 4),
   RGBA16_SINT(GpuFormat.ComponentType.SINT_16, 4),
   R32_UINT(GpuFormat.ComponentType.UINT_32, 1),
   R32_SINT(GpuFormat.ComponentType.SINT_32, 1),
   RG32_UINT(GpuFormat.ComponentType.UINT_32, 2),
   RG32_SINT(GpuFormat.ComponentType.SINT_32, 2),
   RGB32_UINT(GpuFormat.ComponentType.UINT_32, 3),
   RGB32_SINT(GpuFormat.ComponentType.SINT_32, 3),
   RGBA32_UINT(GpuFormat.ComponentType.UINT_32, 4),
   RGBA32_SINT(GpuFormat.ComponentType.SINT_32, 4),
   R16_FLOAT(GpuFormat.ComponentType.FLOAT_16, 1),
   RG16_FLOAT(GpuFormat.ComponentType.FLOAT_16, 2),
   RGB16_FLOAT(GpuFormat.ComponentType.FLOAT_16, 3),
   RGBA16_FLOAT(GpuFormat.ComponentType.FLOAT_16, 4),
   R32_FLOAT(GpuFormat.ComponentType.FLOAT_32, 1),
   RG32_FLOAT(GpuFormat.ComponentType.FLOAT_32, 2),
   RGB32_FLOAT(GpuFormat.ComponentType.FLOAT_32, 3),
   RGBA32_FLOAT(GpuFormat.ComponentType.FLOAT_32, 4),
   RGB10A2_UNORM(GpuFormat.ComponentType.OPAQUE_32, 1),
   RGB10A2_UINT(GpuFormat.ComponentType.OPAQUE_32, 1),
   RG11B10_FLOAT(GpuFormat.ComponentType.OPAQUE_32, 1),
   D32_FLOAT(GpuFormat.ComponentType.OPAQUE_32, 1),
   D32_FLOAT_S8_UINT(GpuFormat.ComponentType.OPAQUE_64, 1),
   D24_UNORM_S8_UINT(GpuFormat.ComponentType.OPAQUE_32, 1),
   D16_UNORM(GpuFormat.ComponentType.OPAQUE_16, 1),
   S8_UINT(GpuFormat.ComponentType.OPAQUE_8, 1);

   private final ComponentType componentType;
   private final int componentCount;

   private GpuFormat(final ComponentType componentType, final int componentCount) {
      this.componentType = componentType;
      this.componentCount = componentCount;
   }

   public int blockSize() {
      return this.componentType.byteSize() * this.componentCount;
   }

   public int byteAlignment() {
      return this.componentType.byteSize();
   }

   public ComponentType componentType() {
      return this.componentType;
   }

   public int componentCount() {
      return this.componentCount;
   }

   public boolean hasColorAspect() {
      return !this.hasDepthAspect() && !this.hasStencilAspect();
   }

   public boolean hasDepthAspect() {
      return this == D32_FLOAT || this == D32_FLOAT_S8_UINT || this == D24_UNORM_S8_UINT || this == D16_UNORM;
   }

   public boolean hasStencilAspect() {
      return this == S8_UINT || this == D32_FLOAT_S8_UINT || this == D24_UNORM_S8_UINT;
   }

   // $FF: synthetic method
   private static GpuFormat[] $values() {
      return new GpuFormat[]{R8_UNORM, R8_SNORM, RG8_UNORM, RG8_SNORM, RGB8_UNORM, RGB8_SNORM, RGBA8_UNORM, RGBA8_SNORM, R16_UNORM, R16_SNORM, RG16_UNORM, RG16_SNORM, RGB16_UNORM, RGB16_SNORM, RGBA16_UNORM, RGBA16_SNORM, R8_UINT, R8_SINT, RG8_UINT, RG8_SINT, RGB8_UINT, RGB8_SINT, RGBA8_UINT, RGBA8_SINT, R16_UINT, R16_SINT, RG16_UINT, RG16_SINT, RGB16_UINT, RGB16_SINT, RGBA16_UINT, RGBA16_SINT, R32_UINT, R32_SINT, RG32_UINT, RG32_SINT, RGB32_UINT, RGB32_SINT, RGBA32_UINT, RGBA32_SINT, R16_FLOAT, RG16_FLOAT, RGB16_FLOAT, RGBA16_FLOAT, R32_FLOAT, RG32_FLOAT, RGB32_FLOAT, RGBA32_FLOAT, RGB10A2_UNORM, RGB10A2_UINT, RG11B10_FLOAT, D32_FLOAT, D32_FLOAT_S8_UINT, D24_UNORM_S8_UINT, D16_UNORM, S8_UINT};
   }

   public static enum ComponentType {
      UNORM_8(1),
      SNORM_8(1),
      UINT_8(1),
      SINT_8(1),
      UNORM_16(2),
      SNORM_16(2),
      UINT_16(2),
      SINT_16(2),
      FLOAT_16(2),
      UINT_32(4),
      SINT_32(4),
      FLOAT_32(4),
      OPAQUE_8(1),
      OPAQUE_16(2),
      OPAQUE_32(4),
      OPAQUE_64(8);

      private final int byteSize;

      private ComponentType(final int byteSize) {
         this.byteSize = byteSize;
      }

      public int byteSize() {
         return this.byteSize;
      }

      // $FF: synthetic method
      private static ComponentType[] $values() {
         return new ComponentType[]{UNORM_8, SNORM_8, UINT_8, SINT_8, UNORM_16, SNORM_16, UINT_16, SINT_16, FLOAT_16, UINT_32, SINT_32, FLOAT_32, OPAQUE_8, OPAQUE_16, OPAQUE_32, OPAQUE_64};
      }
   }
}
