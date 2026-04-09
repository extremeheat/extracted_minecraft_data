package com.mojang.blaze3d.vulkan;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.platform.BlendFactor;
import com.mojang.blaze3d.platform.BlendOp;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.platform.PolygonMode;
import com.mojang.blaze3d.systems.GpuSurface;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.vertex.VertexFormat;

public final class VulkanConst {
   public VulkanConst() {
      super();
   }

   public static int textureUsageToVk(final @GpuTexture.Usage int usage, final GpuFormat format) {
      int vkUsage = 0;
      if ((usage & 8) != 0) {
         if (format.hasColorAspect()) {
            vkUsage |= 16;
         }

         if (format.hasDepthAspect()) {
            vkUsage |= 32;
         }
      }

      if ((usage & 4) != 0) {
         vkUsage |= 4;
      }

      if ((usage & 1) != 0) {
         vkUsage |= 2;
      }

      if ((usage & 2) != 0) {
         vkUsage |= 1;
      }

      return vkUsage;
   }

   public static int bufferUsageToVk(final @GpuBuffer.Usage int usage) {
      int result = 0;
      if ((usage & 8) != 0) {
         result |= 2;
      }

      if ((usage & 16) != 0) {
         result |= 1;
      }

      if ((usage & 32) != 0) {
         result |= 128;
      }

      if ((usage & 64) != 0) {
         result |= 64;
      }

      if ((usage & 128) != 0) {
         result |= 16;
      }

      if ((usage & 256) != 0) {
         result |= 4;
      }

      return result;
   }

   public static int formatAspectMask(final GpuFormat format) {
      int aspectMask = 0;
      if (format.hasColorAspect()) {
         aspectMask |= 1;
      }

      if (format.hasDepthAspect()) {
         aspectMask |= 2;
      }

      if (format.hasStencilAspect()) {
         aspectMask |= 4;
      }

      return aspectMask;
   }

   public static int toVk(final AddressMode addressMode) {
      byte var10000;
      switch (addressMode) {
         case REPEAT -> var10000 = 0;
         case CLAMP_TO_EDGE -> var10000 = 2;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public static int toVk(final FilterMode filter) {
      byte var10000;
      switch (filter) {
         case NEAREST -> var10000 = 0;
         case LINEAR -> var10000 = 1;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public static int toVk(final GpuFormat format) {
      short var10000;
      switch (format) {
         case R8_UNORM -> var10000 = 9;
         case R8_SNORM -> var10000 = 10;
         case RG8_UNORM -> var10000 = 16;
         case RG8_SNORM -> var10000 = 17;
         case RGB8_UNORM -> var10000 = 23;
         case RGB8_SNORM -> var10000 = 24;
         case RGBA8_UNORM -> var10000 = 37;
         case RGBA8_SNORM -> var10000 = 38;
         case R16_UNORM -> var10000 = 70;
         case R16_SNORM -> var10000 = 71;
         case RG16_UNORM -> var10000 = 77;
         case RG16_SNORM -> var10000 = 78;
         case RGB16_UNORM -> var10000 = 84;
         case RGB16_SNORM -> var10000 = 85;
         case RGBA16_UNORM -> var10000 = 91;
         case RGBA16_SNORM -> var10000 = 92;
         case R8_UINT -> var10000 = 13;
         case R8_SINT -> var10000 = 14;
         case RG8_UINT -> var10000 = 20;
         case RG8_SINT -> var10000 = 21;
         case RGB8_UINT -> var10000 = 27;
         case RGB8_SINT -> var10000 = 28;
         case RGBA8_UINT -> var10000 = 41;
         case RGBA8_SINT -> var10000 = 42;
         case R16_UINT -> var10000 = 74;
         case R16_SINT -> var10000 = 75;
         case RG16_UINT -> var10000 = 81;
         case RG16_SINT -> var10000 = 82;
         case RGB16_UINT -> var10000 = 88;
         case RGB16_SINT -> var10000 = 89;
         case RGBA16_UINT -> var10000 = 95;
         case RGBA16_SINT -> var10000 = 96;
         case R32_UINT -> var10000 = 98;
         case R32_SINT -> var10000 = 99;
         case RG32_UINT -> var10000 = 101;
         case RG32_SINT -> var10000 = 102;
         case RGB32_UINT -> var10000 = 104;
         case RGB32_SINT -> var10000 = 105;
         case RGBA32_UINT -> var10000 = 107;
         case RGBA32_SINT -> var10000 = 108;
         case R16_FLOAT -> var10000 = 76;
         case RG16_FLOAT -> var10000 = 83;
         case RGB16_FLOAT -> var10000 = 90;
         case RGBA16_FLOAT -> var10000 = 97;
         case R32_FLOAT -> var10000 = 100;
         case RG32_FLOAT -> var10000 = 103;
         case RGB32_FLOAT -> var10000 = 106;
         case RGBA32_FLOAT -> var10000 = 109;
         case RGB10A2_UNORM -> var10000 = 64;
         case RGB10A2_UINT -> var10000 = 68;
         case RG11B10_FLOAT -> var10000 = 122;
         case D32_FLOAT -> var10000 = 126;
         case D32_FLOAT_S8_UINT -> var10000 = 130;
         case D24_UNORM_S8_UINT -> var10000 = 129;
         case D16_UNORM -> var10000 = 124;
         case S8_UINT -> var10000 = 127;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public static int toVk(final BlendFactor factor) {
      byte var10000;
      switch (factor) {
         case CONSTANT_ALPHA -> var10000 = 12;
         case CONSTANT_COLOR -> var10000 = 10;
         case ONE_MINUS_CONSTANT_ALPHA -> var10000 = 13;
         case ONE_MINUS_CONSTANT_COLOR -> var10000 = 11;
         case DST_ALPHA -> var10000 = 8;
         case DST_COLOR -> var10000 = 4;
         case ONE -> var10000 = 1;
         case ONE_MINUS_DST_ALPHA -> var10000 = 9;
         case ONE_MINUS_DST_COLOR -> var10000 = 5;
         case ONE_MINUS_SRC_ALPHA -> var10000 = 7;
         case ONE_MINUS_SRC_COLOR -> var10000 = 3;
         case SRC_ALPHA -> var10000 = 6;
         case SRC_ALPHA_SATURATE -> var10000 = 14;
         case SRC_COLOR -> var10000 = 2;
         case ZERO -> var10000 = 0;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public static int toVk(final BlendOp blendOp) {
      byte var10000;
      switch (blendOp) {
         case ADD -> var10000 = 0;
         case SUBTRACT -> var10000 = 1;
         case REVERSE_SUBTRACT -> var10000 = 2;
         case MIN -> var10000 = 3;
         case MAX -> var10000 = 4;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public static int toVk(final CompareOp op) {
      byte var10000;
      switch (op) {
         case ALWAYS_PASS -> var10000 = 7;
         case LESS_THAN -> var10000 = 1;
         case LESS_THAN_OR_EQUAL -> var10000 = 3;
         case EQUAL -> var10000 = 2;
         case NOT_EQUAL -> var10000 = 5;
         case GREATER_THAN_OR_EQUAL -> var10000 = 6;
         case GREATER_THAN -> var10000 = 4;
         case NEVER_PASS -> var10000 = 0;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public static int toVk(final PolygonMode polygonMode) {
      byte var10000;
      switch (polygonMode) {
         case FILL -> var10000 = 0;
         case WIREFRAME -> var10000 = 1;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public static int toVk(final VertexFormat.Mode vertexFormatMode) {
      byte var10000;
      switch (vertexFormatMode) {
         case LINES -> var10000 = 3;
         case DEBUG_LINES -> var10000 = 1;
         case DEBUG_LINE_STRIP -> var10000 = 2;
         case POINTS -> var10000 = 0;
         case TRIANGLES -> var10000 = 3;
         case TRIANGLE_STRIP -> var10000 = 4;
         case TRIANGLE_FAN -> var10000 = 5;
         case QUADS -> var10000 = 3;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   static int toVk(final ColorTargetState colorTargetState) {
      int result = 0;
      if (colorTargetState.writeAlpha()) {
         result |= 8;
      }

      if (colorTargetState.writeRed()) {
         result |= 1;
      }

      if (colorTargetState.writeGreen()) {
         result |= 2;
      }

      if (colorTargetState.writeBlue()) {
         result |= 4;
      }

      return result;
   }

   static int toVk(final GpuSurface.PresentMode mode) {
      byte var10000;
      switch (mode) {
         case IMMEDIATE -> var10000 = 0;
         case MAILBOX -> var10000 = 1;
         case FIFO -> var10000 = 2;
         case FIFO_RELAXED -> var10000 = 3;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }
}
