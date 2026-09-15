package com.mojang.renderpearl.api.pipeline;

import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.shaderc.ShadercIncludeResult;

public interface ShaderSource extends AutoCloseable {
   @Nullable String getShader(Identifier id, ShaderType type);

   @Nullable CachedIncludeSource getInclude(Identifier id);

   void close();

   public static record CachedIncludeSource(long includeResultPtr) implements AutoCloseable {
      public CachedIncludeSource {
         super();
      }

      public static CachedIncludeSource create(final Identifier id, final String source) {
         ShadercIncludeResult result = ShadercIncludeResult.calloc();
         result.source_name(MemoryUtil.memUTF8(id.toString(), false));
         result.content(MemoryUtil.memUTF8(source, false));
         return new CachedIncludeSource(result.address());
      }

      public static CachedIncludeSource createError(final String message) {
         ShadercIncludeResult result = ShadercIncludeResult.calloc();
         result.source_name(MemoryUtil.memUTF8("", false));
         result.content(MemoryUtil.memUTF8(message, false));
         return new CachedIncludeSource(result.address());
      }

      public void close() {
         MemoryUtil.nmemFree(MemoryUtil.memGetAddress(this.includeResultPtr + (long)ShadercIncludeResult.SOURCE_NAME));
         MemoryUtil.nmemFree(MemoryUtil.memGetAddress(this.includeResultPtr + (long)ShadercIncludeResult.CONTENT));
         MemoryUtil.nmemFree(this.includeResultPtr);
      }
   }
}
