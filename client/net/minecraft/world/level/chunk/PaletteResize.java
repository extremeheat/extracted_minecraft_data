package net.minecraft.world.level.chunk;

public interface PaletteResize<T> {
   int onResize(int var1, T var2);

   static <T> PaletteResize<T> noResizeExpected() {
      return (var0, var1) -> {
         throw new IllegalArgumentException("Unexpected palette resize, bits = " + var0 + ", added value = " + String.valueOf(var1));
      };
   }
}
