package net.minecraft.world.level.chunk;

import java.util.List;

public interface Configuration {
   boolean alwaysRepack();

   int bitsInMemory();

   int bitsInStorage();

   <T> Palette<T> createPalette(Strategy<T> strategy, List<T> paletteEntries);

   public static record Simple(Palette.Factory factory, int bits) implements Configuration {
      public Simple {
         super();
      }

      public boolean alwaysRepack() {
         return false;
      }

      public <T> Palette<T> createPalette(final Strategy<T> strategy, final List<T> paletteEntries) {
         return this.factory.<T>create(this.bits, paletteEntries);
      }

      public int bitsInMemory() {
         return this.bits;
      }

      public int bitsInStorage() {
         return this.bits;
      }
   }

   public static record Global(int bitsInMemory, int bitsInStorage) implements Configuration {
      public Global {
         super();
      }

      public boolean alwaysRepack() {
         return true;
      }

      public <T> Palette<T> createPalette(final Strategy<T> strategy, final List<T> paletteEntries) {
         return strategy.globalPalette();
      }
   }
}
