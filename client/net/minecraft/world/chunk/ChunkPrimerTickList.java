package net.minecraft.world.chunk;

import it.unimi.dsi.fastutil.shorts.ShortList;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ITickList;
import net.minecraft.world.TickPriority;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;

public class ChunkPrimerTickList<T> implements ITickList<T> {
   protected final Predicate<T> field_205382_a;
   protected final Function<T, ResourceLocation> field_205383_b;
   protected final Function<ResourceLocation, T> field_205384_c;
   private final ChunkPos field_205385_d;
   private final ShortList[] field_205386_e = new ShortList[16];

   public ChunkPrimerTickList(Predicate<T> var1, Function<T, ResourceLocation> var2, Function<ResourceLocation, T> var3, ChunkPos var4) {
      super();
      this.field_205382_a = var1;
      this.field_205383_b = var2;
      this.field_205384_c = var3;
      this.field_205385_d = var4;
   }

   public NBTTagList func_205379_a() {
      return AnvilChunkLoader.func_202163_a(this.field_205386_e);
   }

   public void func_205380_a(NBTTagList var1) {
      for(int var2 = 0; var2 < var1.size(); ++var2) {
         NBTTagList var3 = var1.func_202169_e(var2);

         for(int var4 = 0; var4 < var3.size(); ++var4) {
            ChunkPrimer.func_205330_a(this.field_205386_e, var2).add(var3.func_202170_f(var4));
         }
      }

   }

   public void func_205381_a(ITickList<T> var1, Function<BlockPos, T> var2) {
      for(int var3 = 0; var3 < this.field_205386_e.length; ++var3) {
         if (this.field_205386_e[var3] != null) {
            ShortListIterator var4 = this.field_205386_e[var3].iterator();

            while(var4.hasNext()) {
               Short var5 = (Short)var4.next();
               BlockPos var6 = ChunkPrimer.func_201635_a(var5, var3, this.field_205385_d);
               var1.func_205360_a(var6, var2.apply(var6), 0);
            }

            this.field_205386_e[var3].clear();
         }
      }

   }

   public boolean func_205359_a(BlockPos var1, T var2) {
      return false;
   }

   public void func_205362_a(BlockPos var1, T var2, int var3, TickPriority var4) {
      ChunkPrimer.func_205330_a(this.field_205386_e, var1.func_177956_o() >> 4).add(ChunkPrimer.func_201651_i(var1));
   }

   public boolean func_205361_b(BlockPos var1, T var2) {
      return false;
   }
}
