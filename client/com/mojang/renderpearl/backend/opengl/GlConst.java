package com.mojang.renderpearl.backend.opengl;

import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.buffers.GpuBuffer;
import com.mojang.renderpearl.api.pipeline.BlendFactor;
import com.mojang.renderpearl.api.pipeline.BlendOp;
import com.mojang.renderpearl.api.pipeline.CompareOp;
import com.mojang.renderpearl.api.pipeline.IndexType;
import com.mojang.renderpearl.api.pipeline.PolygonMode;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.renderpearl.api.pipeline.ShaderType;
import com.mojang.renderpearl.api.textures.AddressMode;

public class GlConst {
   public static final int GL_READ_FRAMEBUFFER = 36008;
   public static final int GL_DRAW_FRAMEBUFFER = 36009;
   public static final int GL_TRUE = 1;
   public static final int GL_FALSE = 0;
   public static final int GL_NONE = 0;
   public static final int GL_LINES = 1;
   public static final int GL_LINE_STRIP = 3;
   public static final int GL_TRIANGLE_STRIP = 5;
   public static final int GL_TRIANGLE_FAN = 6;
   public static final int GL_TRIANGLES = 4;
   public static final int GL_POINTS = 0;
   public static final int GL_WRITE_ONLY = 35001;
   public static final int GL_READ_ONLY = 35000;
   public static final int GL_READ_WRITE = 35002;
   public static final int GL_MAP_READ_BIT = 1;
   public static final int GL_MAP_WRITE_BIT = 2;
   public static final int GL_EQUAL = 514;
   public static final int GL_LEQUAL = 515;
   public static final int GL_LESS = 513;
   public static final int GL_GREATER = 516;
   public static final int GL_GEQUAL = 518;
   public static final int GL_ALWAYS = 519;
   public static final int GL_TEXTURE_MAG_FILTER = 10240;
   public static final int GL_TEXTURE_MIN_FILTER = 10241;
   public static final int GL_TEXTURE_WRAP_S = 10242;
   public static final int GL_TEXTURE_WRAP_T = 10243;
   public static final int GL_NEAREST = 9728;
   public static final int GL_LINEAR = 9729;
   public static final int GL_NEAREST_MIPMAP_LINEAR = 9986;
   public static final int GL_LINEAR_MIPMAP_LINEAR = 9987;
   public static final int GL_CLAMP_TO_EDGE = 33071;
   public static final int GL_REPEAT = 10497;
   public static final int GL_FRONT = 1028;
   public static final int GL_FRONT_AND_BACK = 1032;
   public static final int GL_LINE = 6913;
   public static final int GL_FILL = 6914;
   public static final int GL_BYTE = 5120;
   public static final int GL_UNSIGNED_BYTE = 5121;
   public static final int GL_SHORT = 5122;
   public static final int GL_UNSIGNED_SHORT = 5123;
   public static final int GL_INT = 5124;
   public static final int GL_UNSIGNED_INT = 5125;
   public static final int GL_FLOAT = 5126;
   public static final int GL_ZERO = 0;
   public static final int GL_ONE = 1;
   public static final int GL_SRC_COLOR = 768;
   public static final int GL_ONE_MINUS_SRC_COLOR = 769;
   public static final int GL_SRC_ALPHA = 770;
   public static final int GL_ONE_MINUS_SRC_ALPHA = 771;
   public static final int GL_DST_ALPHA = 772;
   public static final int GL_ONE_MINUS_DST_ALPHA = 773;
   public static final int GL_DST_COLOR = 774;
   public static final int GL_ONE_MINUS_DST_COLOR = 775;
   public static final int GL_REPLACE = 7681;
   public static final int GL_DEPTH_BUFFER_BIT = 256;
   public static final int GL_COLOR_BUFFER_BIT = 16384;
   public static final int GL_RGBA8 = 32856;
   public static final int GL_PROXY_TEXTURE_2D = 32868;
   public static final int GL_RGBA = 6408;
   public static final int GL_TEXTURE_WIDTH = 4096;
   public static final int GL_BGR = 32992;
   public static final int GL_FUNC_ADD = 32774;
   public static final int GL_MIN = 32775;
   public static final int GL_MAX = 32776;
   public static final int GL_FUNC_SUBTRACT = 32778;
   public static final int GL_FUNC_REVERSE_SUBTRACT = 32779;
   public static final int GL_DEPTH_COMPONENT24 = 33190;
   public static final int GL_STATIC_DRAW = 35044;
   public static final int GL_DYNAMIC_DRAW = 35048;
   public static final int GL_STREAM_DRAW = 35040;
   public static final int GL_STATIC_READ = 35045;
   public static final int GL_DYNAMIC_READ = 35049;
   public static final int GL_STREAM_READ = 35041;
   public static final int GL_STATIC_COPY = 35046;
   public static final int GL_DYNAMIC_COPY = 35050;
   public static final int GL_STREAM_COPY = 35042;
   public static final int GL_SYNC_GPU_COMMANDS_COMPLETE = 37143;
   public static final int GL_TIMEOUT_EXPIRED = 37147;
   public static final int GL_WAIT_FAILED = 37149;
   public static final int GL_UNPACK_SWAP_BYTES = 3312;
   public static final int GL_UNPACK_LSB_FIRST = 3313;
   public static final int GL_UNPACK_ROW_LENGTH = 3314;
   public static final int GL_UNPACK_SKIP_ROWS = 3315;
   public static final int GL_UNPACK_SKIP_PIXELS = 3316;
   public static final int GL_UNPACK_ALIGNMENT = 3317;
   public static final int GL_PACK_ALIGNMENT = 3333;
   public static final int GL_PACK_ROW_LENGTH = 3330;
   public static final int GL_MAX_TEXTURE_SIZE = 3379;
   public static final int GL_TEXTURE_2D = 3553;
   public static final int[] CUBEMAP_TARGETS = new int[]{34069, 34070, 34071, 34072, 34073, 34074};
   public static final int GL_DEPTH_COMPONENT = 6402;
   public static final int GL_DEPTH_COMPONENT32 = 33191;
   public static final int GL_FRAMEBUFFER = 36160;
   public static final int GL_RENDERBUFFER = 36161;
   public static final int GL_COLOR_ATTACHMENT0 = 36064;
   public static final int GL_DEPTH_ATTACHMENT = 36096;
   public static final int GL_FRAMEBUFFER_COMPLETE = 36053;
   public static final int GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT = 36054;
   public static final int GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT = 36055;
   public static final int GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER = 36059;
   public static final int GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER = 36060;
   public static final int GL_FRAMEBUFFER_UNSUPPORTED = 36061;
   public static final int GL_LINK_STATUS = 35714;
   public static final int GL_COMPILE_STATUS = 35713;
   public static final int GL_VERTEX_SHADER = 35633;
   public static final int GL_FRAGMENT_SHADER = 35632;
   public static final int GL_TEXTURE0 = 33984;
   public static final int GL_TEXTURE1 = 33985;
   public static final int GL_TEXTURE2 = 33986;
   public static final int GL_TEXTURE_COMPARE_MODE = 34892;
   public static final int GL_ARRAY_BUFFER = 34962;
   public static final int GL_ELEMENT_ARRAY_BUFFER = 34963;
   public static final int GL_PIXEL_PACK_BUFFER = 35051;
   public static final int GL_COPY_READ_BUFFER = 36662;
   public static final int GL_COPY_WRITE_BUFFER = 36663;
   public static final int GL_PIXEL_UNPACK_BUFFER = 35052;
   public static final int GL_UNIFORM_BUFFER = 35345;
   public static final int GL_RGB = 6407;
   public static final int GL_RG = 33319;
   public static final int GL_R8 = 33321;
   public static final int GL_RED = 6403;
   public static final int GL_OUT_OF_MEMORY = 1285;

   public GlConst() {
      super();
   }

   public static int toGl(final CompareOp compareOp) {
      short var10000;
      switch (compareOp) {
         case ALWAYS_PASS -> var10000 = 519;
         case LESS_THAN -> var10000 = 513;
         case LESS_THAN_OR_EQUAL -> var10000 = 515;
         case EQUAL -> var10000 = 514;
         case NOT_EQUAL -> var10000 = 517;
         case GREATER_THAN_OR_EQUAL -> var10000 = 518;
         case GREATER_THAN -> var10000 = 516;
         case NEVER_PASS -> var10000 = 512;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public static int toGl(final PolygonMode polygonMode) {
      short var10000;
      switch (polygonMode) {
         case WIREFRAME -> var10000 = 6913;
         default -> var10000 = 6914;
      }

      return var10000;
   }

   public static int toGl(final BlendFactor blendFactor) {
      char var10000;
      switch (blendFactor) {
         case CONSTANT_ALPHA -> var10000 = '\u8003';
         case CONSTANT_COLOR -> var10000 = '\u8001';
         case DST_ALPHA -> var10000 = 772;
         case DST_COLOR -> var10000 = 774;
         case ONE -> var10000 = 1;
         case ONE_MINUS_CONSTANT_ALPHA -> var10000 = '\u8004';
         case ONE_MINUS_CONSTANT_COLOR -> var10000 = '\u8002';
         case ONE_MINUS_DST_ALPHA -> var10000 = 773;
         case ONE_MINUS_DST_COLOR -> var10000 = 775;
         case ONE_MINUS_SRC_ALPHA -> var10000 = 771;
         case ONE_MINUS_SRC_COLOR -> var10000 = 769;
         case SRC_ALPHA -> var10000 = 770;
         case SRC_ALPHA_SATURATE -> var10000 = 776;
         case SRC_COLOR -> var10000 = 768;
         case ZERO -> var10000 = 0;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public static int toGl(final BlendOp blendOp) {
      char var10000;
      switch (blendOp) {
         case ADD -> var10000 = '\u8006';
         case SUBTRACT -> var10000 = '\u800a';
         case REVERSE_SUBTRACT -> var10000 = '\u800b';
         case MIN -> var10000 = '\u8007';
         case MAX -> var10000 = '\u8008';
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public static int toGl(final PrimitiveTopology primitiveTopology) {
      byte var10000;
      switch (primitiveTopology) {
         case LINES -> var10000 = 4;
         case DEBUG_LINES -> var10000 = 1;
         case DEBUG_LINE_STRIP -> var10000 = 3;
         case POINTS -> var10000 = 0;
         case TRIANGLES -> var10000 = 4;
         case TRIANGLE_STRIP -> var10000 = 5;
         case TRIANGLE_FAN -> var10000 = 6;
         case QUADS -> var10000 = 4;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public static int toGl(final IndexType indexType) {
      short var10000;
      switch (indexType) {
         case SHORT -> var10000 = 5123;
         case INT -> var10000 = 5125;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public static int toGl(final AddressMode addressMode) {
      char var10000;
      switch (addressMode) {
         case REPEAT -> var10000 = 10497;
         case CLAMP_TO_EDGE -> var10000 = '\u812f';
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public static int glFormatChannelCount(final int glExternalID) {
      if (glExternalID != 36249 && glExternalID != 6408) {
         if (glExternalID != 36248 && glExternalID != 6407) {
            if (glExternalID != 33320 && glExternalID != 33319) {
               return glExternalID != 36244 && glExternalID != 6403 ? 0 : 1;
            } else {
               return 2;
            }
         } else {
            return 3;
         }
      } else {
         return 4;
      }
   }

   public static boolean isGlFormatInteger(final int glExternalID) {
      return glExternalID == 36249 || glExternalID == 36248 || glExternalID == 33320 || glExternalID == 36244;
   }

   public static boolean isFormatNormalized(final GpuFormat gpuFormat) {
      boolean var10000;
      switch (gpuFormat) {
         case R8_UNORM:
         case R8_SNORM:
         case R16_UNORM:
         case R16_SNORM:
         case RG8_UNORM:
         case RG8_SNORM:
         case RG16_UNORM:
         case RG16_SNORM:
         case RGB8_UNORM:
         case RGB8_SNORM:
         case RGB16_UNORM:
         case RGB16_SNORM:
         case RGBA8_UNORM:
         case RGBA8_SNORM:
         case RGBA16_UNORM:
         case RGB10A2_UNORM:
         case D16_UNORM:
            var10000 = true;
            break;
         default:
            var10000 = false;
      }

      return var10000;
   }

   public static int toGlInternalId(final GpuFormat gpuFormat) {
      char var10000;
      switch (gpuFormat) {
         case R8_UNORM:
            var10000 = '\u8229';
            break;
         case R8_SNORM:
            var10000 = '\u8f94';
            break;
         case R16_UNORM:
            var10000 = '\u822a';
            break;
         case R16_SNORM:
            var10000 = '\u8f98';
            break;
         case RG8_UNORM:
            var10000 = '\u822b';
            break;
         case RG8_SNORM:
            var10000 = '\u8f95';
            break;
         case RG16_UNORM:
            var10000 = '\u822c';
            break;
         case RG16_SNORM:
            var10000 = '\u8f99';
            break;
         case RGB8_UNORM:
         case RGB8_SNORM:
         case RGB16_UNORM:
         case RGB16_SNORM:
         default:
            var10000 = 0;
            break;
         case RGBA8_UNORM:
            var10000 = '\u8058';
            break;
         case RGBA8_SNORM:
            var10000 = '\u8f97';
            break;
         case RGBA16_UNORM:
            var10000 = '\u805b';
            break;
         case RGB10A2_UNORM:
            var10000 = '\u8059';
            break;
         case D16_UNORM:
            var10000 = '\u81a5';
            break;
         case RGBA16_SNORM:
            var10000 = '\u8f9b';
            break;
         case R8_UINT:
            var10000 = '\u8232';
            break;
         case R8_SINT:
            var10000 = '\u8231';
            break;
         case RG8_UINT:
            var10000 = '\u8238';
            break;
         case RG8_SINT:
            var10000 = '\u8237';
            break;
         case RGBA8_UINT:
            var10000 = '\u8d7c';
            break;
         case RGBA8_SINT:
            var10000 = '\u8d8e';
            break;
         case R16_UINT:
            var10000 = '\u8234';
            break;
         case R16_SINT:
            var10000 = '\u8233';
            break;
         case RG16_UINT:
            var10000 = '\u823a';
            break;
         case RG16_SINT:
            var10000 = '\u8239';
            break;
         case RGBA16_UINT:
            var10000 = '\u8d76';
            break;
         case RGBA16_SINT:
            var10000 = '\u8d88';
            break;
         case R32_UINT:
            var10000 = '\u8236';
            break;
         case R32_SINT:
            var10000 = '\u8235';
            break;
         case RG32_UINT:
            var10000 = '\u823c';
            break;
         case RG32_SINT:
            var10000 = '\u823b';
            break;
         case RGB32_UINT:
            var10000 = '\u8d71';
            break;
         case RGB32_SINT:
            var10000 = '\u8d83';
            break;
         case RGBA32_UINT:
            var10000 = '\u8d70';
            break;
         case RGBA32_SINT:
            var10000 = '\u8d82';
            break;
         case R16_FLOAT:
            var10000 = '\u822d';
            break;
         case RG16_FLOAT:
            var10000 = '\u822f';
            break;
         case RGBA16_FLOAT:
            var10000 = '\u881a';
            break;
         case R32_FLOAT:
            var10000 = '\u822e';
            break;
         case RG32_FLOAT:
            var10000 = '\u8230';
            break;
         case RGBA32_FLOAT:
            var10000 = '\u8814';
            break;
         case RGB10A2_UINT:
            var10000 = '\u906f';
            break;
         case RG11B10_FLOAT:
            var10000 = '\u8c3a';
            break;
         case D32_FLOAT:
            var10000 = '\u8cac';
            break;
         case D32_FLOAT_S8_UINT:
            var10000 = '\u8cad';
            break;
         case D24_UNORM_S8_UINT:
            var10000 = '\u88f0';
            break;
         case S8_UINT:
            var10000 = '\u8d48';
      }

      return var10000;
   }

   public static int toGlExternalId(final GpuFormat gpuFormat) {
      char var10000;
      switch (gpuFormat) {
         case R8_UNORM:
         case R8_SNORM:
         case R16_UNORM:
         case R16_SNORM:
         case R16_FLOAT:
         case R32_FLOAT:
            var10000 = 6403;
            break;
         case RG8_UNORM:
         case RG8_SNORM:
         case RG16_UNORM:
         case RG16_SNORM:
         case RG16_FLOAT:
         case RG32_FLOAT:
            var10000 = '\u8227';
            break;
         case RGB8_UNORM:
         case RGB8_SNORM:
         case RGB16_UNORM:
         case RGB16_SNORM:
         case RG11B10_FLOAT:
         case RGB16_FLOAT:
         case RGB32_FLOAT:
            var10000 = 6407;
            break;
         case RGBA8_UNORM:
         case RGBA8_SNORM:
         case RGBA16_UNORM:
         case RGB10A2_UNORM:
         case RGBA16_SNORM:
         case RGBA16_FLOAT:
         case RGBA32_FLOAT:
            var10000 = 6408;
            break;
         case D16_UNORM:
         case D32_FLOAT:
            var10000 = 6402;
            break;
         case R8_UINT:
         case R8_SINT:
         case R16_UINT:
         case R16_SINT:
         case R32_UINT:
         case R32_SINT:
            var10000 = '\u8d94';
            break;
         case RG8_UINT:
         case RG8_SINT:
         case RG16_UINT:
         case RG16_SINT:
         case RG32_UINT:
         case RG32_SINT:
            var10000 = '\u8228';
            break;
         case RGBA8_UINT:
         case RGBA8_SINT:
         case RGBA16_UINT:
         case RGBA16_SINT:
         case RGBA32_UINT:
         case RGBA32_SINT:
         case RGB10A2_UINT:
            var10000 = '\u8d99';
            break;
         case RGB32_UINT:
         case RGB32_SINT:
         case RGB8_UINT:
         case RGB8_SINT:
         case RGB16_UINT:
         case RGB16_SINT:
            var10000 = '\u8d98';
            break;
         case D32_FLOAT_S8_UINT:
         case D24_UNORM_S8_UINT:
            var10000 = '\u84f9';
            break;
         case S8_UINT:
            var10000 = 6401;
            break;
         default:
            var10000 = 0;
      }

      return var10000;
   }

   public static int toGlType(final GpuFormat gpuFormat) {
      char var10000;
      switch (gpuFormat) {
         case R8_UNORM:
         case RG8_UNORM:
         case RGB8_UNORM:
         case RGBA8_UNORM:
         case R8_UINT:
         case RG8_UINT:
         case RGBA8_UINT:
         case S8_UINT:
         case RGB8_UINT:
            var10000 = 5121;
            break;
         case R8_SNORM:
         case RG8_SNORM:
         case RGB8_SNORM:
         case RGBA8_SNORM:
         case R8_SINT:
         case RG8_SINT:
         case RGBA8_SINT:
         case RGB8_SINT:
            var10000 = 5120;
            break;
         case R16_UNORM:
         case RG16_UNORM:
         case RGB16_UNORM:
         case RGBA16_UNORM:
         case D16_UNORM:
         case R16_UINT:
         case RG16_UINT:
         case RGBA16_UINT:
         case RGB16_UINT:
            var10000 = 5123;
            break;
         case R16_SNORM:
         case RG16_SNORM:
         case RGB16_SNORM:
         case RGBA16_SNORM:
         case R16_SINT:
         case RG16_SINT:
         case RGBA16_SINT:
         case RGB16_SINT:
            var10000 = 5122;
            break;
         case RGB10A2_UNORM:
         case RGB10A2_UINT:
            var10000 = '\u8368';
            break;
         case R32_UINT:
         case RG32_UINT:
         case RGB32_UINT:
         case RGBA32_UINT:
            var10000 = 5125;
            break;
         case R32_SINT:
         case RG32_SINT:
         case RGB32_SINT:
         case RGBA32_SINT:
            var10000 = 5124;
            break;
         case R16_FLOAT:
         case RG16_FLOAT:
         case RGBA16_FLOAT:
         case RGB16_FLOAT:
            var10000 = 5131;
            break;
         case R32_FLOAT:
         case RG32_FLOAT:
         case RGBA32_FLOAT:
         case D32_FLOAT:
         case RGB32_FLOAT:
            var10000 = 5126;
            break;
         case RG11B10_FLOAT:
            var10000 = '\u8c3b';
            break;
         case D32_FLOAT_S8_UINT:
            var10000 = '\u8dad';
            break;
         case D24_UNORM_S8_UINT:
            var10000 = '\u84fa';
            break;
         default:
            var10000 = 0;
      }

      return var10000;
   }

   public static int toGl(final ShaderType type) {
      char var10000;
      switch (type) {
         case VERTEX -> var10000 = '\u8b31';
         case FRAGMENT -> var10000 = '\u8b30';
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public static int bufferUsageToGlFlag(final @GpuBuffer.Usage int usage) {
      int result = 0;
      if ((usage & 1) != 0) {
         result |= 193;
      }

      if ((usage & 2) != 0) {
         result |= 194;
      }

      if ((usage & 8) != 0) {
         result |= 256;
      }

      if ((usage & 4) != 0) {
         result |= 512;
      }

      return result;
   }

   public static int bufferUsageToGlEnum(final @GpuBuffer.Usage int usage) {
      boolean clientStorage = (usage & 4) != 0;
      if ((usage & 2) != 0) {
         return clientStorage ? '\u88e0' : '\u88e4';
      } else if ((usage & 1) != 0) {
         return clientStorage ? '\u88e1' : '\u88e5';
      } else {
         return 35044;
      }
   }
}
