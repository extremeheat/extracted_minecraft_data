package net.minecraft.world.level.chunk;

import java.util.List;

public interface Configuration {
   boolean alwaysRepack();

   int bitsInMemory();

   int bitsInStorage();

   <T> Palette<T> createPalette(Strategy<T> var1, List<T> var2);

   public static record Simple(Palette.Factory factory, int bits) implements Configuration {
      public Simple(Palette.Factory var1, int var2) {
         super();
         this.factory = var1;
         this.bits = var2;
      }

      public boolean alwaysRepack() {
         return false;
      }

      public <T> Palette<T> createPalette(Strategy<T> var1, List<T> var2) {
         return this.factory.<T>create(this.bits, var2);
      }

      public int bitsInMemory() {
         return this.bits;
      }

      public int bitsInStorage() {
         return this.bits;
      }
   }

   public static record Global(int bitsInMemory, int bitsInStorage) implements Configuration {
      public Global(int var1, int var2) {
         super();
         this.bitsInMemory = var1;
         this.bitsInStorage = var2;
      }

      public boolean alwaysRepack() {
         return true;
      }

      public <T> Palette<T> createPalette(Strategy<T> var1, List<T> var2) {
         return var1.globalPalette();
      }
   }
}
