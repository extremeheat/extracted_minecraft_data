package com.mojang.renderpearl.frontend.shaders;

import com.mojang.renderpearl.api.pipeline.UniformType;
import com.mojang.renderpearl.util.ShaderCompileException;
import it.unimi.dsi.fastutil.ints.IntList;
import java.nio.IntBuffer;
import org.lwjgl.util.spvc.Spvc;
import org.lwjgl.util.spvc.SpvcReflectedResource;

public class SpvUtil {
   public static final IntList DESCRIPTOR_TYPES = IntList.of(new int[]{1, 2, 6, 7, 10, 11});

   public SpvUtil() {
      super();
   }

   public static void crashIfError(final int result, final String message) {
      if (result != 0) {
         throw new IllegalStateException(message + " (" + spvcErrorString(result) + ")");
      }
   }

   public static void throwIfError(final int result, final String message) throws ShaderCompileException {
      if (result != 0) {
         throw new ShaderCompileException(message + " (" + spvcErrorString(result) + ")");
      }
   }

   public static String spvcErrorString(final int result) {
      String var10000;
      switch (result) {
         case -4 -> var10000 = "SPVC_ERROR_INVALID_ARGUMENT";
         case -3 -> var10000 = "SPVC_ERROR_OUT_OF_MEMORY";
         case -2 -> var10000 = "SPVC_ERROR_UNSUPPORTED_SPIRV";
         case -1 -> var10000 = "SPVC_ERROR_INVALID_SPIRV";
         default -> var10000 = Integer.toString(result);
      }

      return var10000;
   }

   public static int getDecorationOffset(final long compiler, final SpvcReflectedResource resource, final int decoration, final IntBuffer returnBuffer) throws ShaderCompileException {
      if (!Spvc.spvc_compiler_get_binary_offset_for_decoration(compiler, resource.id(), decoration, returnBuffer)) {
         throw new ShaderCompileException("Couldn't find byte offset for location decoration of " + resource.nameString());
      } else {
         return returnBuffer.get(0);
      }
   }

   public static int resourceType(final UniformType uniformType) {
      byte var10000;
      switch (uniformType) {
         case COMBINED_IMAGE_SAMPLER -> var10000 = 7;
         case UNIFORM_BUFFER -> var10000 = 1;
         case TEXEL_BUFFER -> var10000 = 7;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public static String baseTypeString(final int baseType) {
      String var10000;
      switch (baseType) {
         case 0 -> var10000 = "unknown";
         case 1 -> var10000 = "void";
         case 2 -> var10000 = "bool";
         case 3 -> var10000 = "int8";
         case 4 -> var10000 = "uint8";
         case 5 -> var10000 = "int16";
         case 6 -> var10000 = "uint16";
         case 7 -> var10000 = "int32";
         case 8 -> var10000 = "uint32";
         case 9 -> var10000 = "int64";
         case 10 -> var10000 = "uint64";
         case 11 -> var10000 = "atomic_counter";
         case 12 -> var10000 = "fp16";
         case 13 -> var10000 = "fp32";
         case 14 -> var10000 = "fp64";
         case 15 -> var10000 = "struct";
         case 16 -> var10000 = "image";
         case 17 -> var10000 = "sampled_image";
         case 18 -> var10000 = "sampler";
         case 19 -> var10000 = "acceleration_structure";
         default -> var10000 = "UNKNOWN_TYPE";
      }

      return var10000;
   }
}
