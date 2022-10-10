package net.minecraft.world.chunk;

import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.world.gen.IChunkGenerator;

public interface IChunkProvider extends AutoCloseable {
   @Nullable
   Chunk func_186025_d(int var1, int var2, boolean var3, boolean var4);

   @Nullable
   default IChunk func_201713_d(int var1, int var2, boolean var3) {
      Chunk var4 = this.func_186025_d(var1, var2, true, false);
      if (var4 == null && var3) {
         throw new UnsupportedOperationException("Could not create an empty chunk");
      } else {
         return var4;
      }
   }

   boolean func_73156_b(BooleanSupplier var1);

   String func_73148_d();

   IChunkGenerator<?> func_201711_g();

   default void close() {
   }
}
