package net.minecraft.client.gui.font.providers;

import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.Platform;
import org.lwjgl.util.freetype.FT_Face;
import org.lwjgl.util.freetype.FreeType.Functions;

public class FreeTypeButNotCrashing {
   private static final boolean PLATFORM_IS_32_BITS;
   private static final boolean FT_LONG_IS_32_BITS;
   private static final ValueLayout FT_LONG_LAYOUT;
   private static final MethodHandle FT_NEW_MEMORY_FACE;
   private static final MethodHandle FT_GET_FIRST_CHAR;
   private static final MethodHandle FT_GET_NEXT_CHAR;

   public FreeTypeButNotCrashing() {
      super();
   }

   public static int FT_New_Memory_Face(final long library, final ByteBuffer fileBase, final int faceIndex, final PointerBuffer aface) {
      MemorySegment libraryAddress = MemorySegment.ofAddress(library);
      MemorySegment fileBaseAddress = MemorySegment.ofBuffer(fileBase);
      MemorySegment faceAddress = MemorySegment.ofAddress(aface.address());

      try {
         return FT_NEW_MEMORY_FACE.invoke(libraryAddress, fileBaseAddress, fileBase.remaining(), faceIndex, faceAddress);
      } catch (Throwable t) {
         throw new IllegalStateException("Failed to open TTF font", t);
      }
   }

   public static long FT_Get_First_Char(final FT_Face face, final IntBuffer output) {
      MemorySegment faceAddress = MemorySegment.ofAddress(face.address());
      MemorySegment outputAddress = MemorySegment.ofBuffer(output);

      try {
         return FT_GET_FIRST_CHAR.invoke(faceAddress, outputAddress);
      } catch (Throwable t) {
         throw new IllegalStateException("Failed to list glyphs in font", t);
      }
   }

   public static long FT_Get_Next_Char(final FT_Face face, final int charCode, final IntBuffer output) {
      MemorySegment faceAddress = MemorySegment.ofAddress(face.address());
      MemorySegment outputAddress = MemorySegment.ofBuffer(output);

      try {
         return FT_GET_NEXT_CHAR.invoke(faceAddress, charCode, outputAddress);
      } catch (Throwable t) {
         throw new IllegalStateException("Failed to list glyphs in font", t);
      }
   }

   static {
      PLATFORM_IS_32_BITS = ValueLayout.ADDRESS.byteSize() == 4L;
      FT_LONG_IS_32_BITS = PLATFORM_IS_32_BITS || Platform.get() == Platform.WINDOWS;
      FT_LONG_LAYOUT = (ValueLayout)(FT_LONG_IS_32_BITS ? ValueLayout.JAVA_INT : ValueLayout.JAVA_LONG);
      FT_NEW_MEMORY_FACE = Linker.nativeLinker().downcallHandle(MemorySegment.ofAddress(Functions.New_Memory_Face), FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, FT_LONG_LAYOUT, FT_LONG_LAYOUT, ValueLayout.ADDRESS));
      FT_GET_FIRST_CHAR = Linker.nativeLinker().downcallHandle(MemorySegment.ofAddress(Functions.Get_First_Char), FunctionDescriptor.of(FT_LONG_LAYOUT, ValueLayout.ADDRESS, ValueLayout.ADDRESS));
      FT_GET_NEXT_CHAR = Linker.nativeLinker().downcallHandle(MemorySegment.ofAddress(Functions.Get_Next_Char), FunctionDescriptor.of(FT_LONG_LAYOUT, ValueLayout.ADDRESS, FT_LONG_LAYOUT, ValueLayout.ADDRESS));
   }
}
