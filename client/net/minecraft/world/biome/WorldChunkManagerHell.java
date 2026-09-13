package net.minecraft.world.biome;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.world.ChunkPosition;

public class WorldChunkManagerHell extends WorldChunkManager {
   private BiomeGenBase field_76947_d;
   private float field_76946_f;

   public WorldChunkManagerHell(BiomeGenBase var1, float var2) {
      super();
      this.field_76947_d = var1;
      this.field_76946_f = var2;
   }

   @Override
   public BiomeGenBase func_76935_a(int var1, int var2) {
      return this.field_76947_d;
   }

   @Override
   public BiomeGenBase[] func_76937_a(BiomeGenBase[] var1, int var2, int var3, int var4, int var5) {
      if (var1 == null || var1.length < var4 * var5) {
         var1 = new BiomeGenBase[var4 * var5];
      }

      Arrays.fill(var1, 0, var4 * var5, this.field_76947_d);
      return var1;
   }

   @Override
   public float[] func_76936_a(float[] var1, int var2, int var3, int var4, int var5) {
      if (var1 == null || var1.length < var4 * var5) {
         var1 = new float[var4 * var5];
      }

      Arrays.fill(var1, 0, var4 * var5, this.field_76946_f);
      return var1;
   }

   @Override
   public BiomeGenBase[] func_76933_b(BiomeGenBase[] var1, int var2, int var3, int var4, int var5) {
      if (var1 == null || var1.length < var4 * var5) {
         var1 = new BiomeGenBase[var4 * var5];
      }

      Arrays.fill(var1, 0, var4 * var5, this.field_76947_d);
      return var1;
   }

   @Override
   public BiomeGenBase[] func_76931_a(BiomeGenBase[] var1, int var2, int var3, int var4, int var5, boolean var6) {
      return this.func_76933_b(var1, var2, var3, var4, var5);
   }

   @Override
   public ChunkPosition func_150795_a(int var1, int var2, int var3, List var4, Random var5) {
      return var4.contains(this.field_76947_d)
         ? new ChunkPosition(var1 - var3 + var5.nextInt(var3 * 2 + 1), 0, var2 - var3 + var5.nextInt(var3 * 2 + 1))
         : null;
   }

   @Override
   public boolean func_76940_a(int var1, int var2, int var3, List var4) {
      return var4.contains(this.field_76947_d);
   }
}
