package net.minecraft.world.gen;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;

public class ChunkProviderDebug implements IChunkProvider {
   private static final List<IBlockState> field_177464_a = Lists.newArrayList();
   private static final int field_177462_b;
   private static final int field_181039_c;
   private final World field_177463_c;

   public ChunkProviderDebug(World var1) {
      super();
      this.field_177463_c = var1;
   }

   public Chunk func_73154_d(int var1, int var2) {
      ChunkPrimer var3 = new ChunkPrimer();

      int var7;
      for(int var4 = 0; var4 < 16; ++var4) {
         for(int var5 = 0; var5 < 16; ++var5) {
            int var6 = var1 * 16 + var4;
            var7 = var2 * 16 + var5;
            var3.func_177855_a(var4, 60, var5, Blocks.field_180401_cv.func_176223_P());
            IBlockState var8 = func_177461_b(var6, var7);
            if (var8 != null) {
               var3.func_177855_a(var4, 70, var5, var8);
            }
         }
      }

      Chunk var9 = new Chunk(this.field_177463_c, var3, var1, var2);
      var9.func_76603_b();
      BiomeGenBase[] var10 = this.field_177463_c.func_72959_q().func_76933_b((BiomeGenBase[])null, var1 * 16, var2 * 16, 16, 16);
      byte[] var11 = var9.func_76605_m();

      for(var7 = 0; var7 < var11.length; ++var7) {
         var11[var7] = (byte)var10[var7].field_76756_M;
      }

      var9.func_76603_b();
      return var9;
   }

   public static IBlockState func_177461_b(int var0, int var1) {
      IBlockState var2 = null;
      if (var0 > 0 && var1 > 0 && var0 % 2 != 0 && var1 % 2 != 0) {
         var0 /= 2;
         var1 /= 2;
         if (var0 <= field_177462_b && var1 <= field_181039_c) {
            int var3 = MathHelper.func_76130_a(var0 * field_177462_b + var1);
            if (var3 < field_177464_a.size()) {
               var2 = (IBlockState)field_177464_a.get(var3);
            }
         }
      }

      return var2;
   }

   public boolean func_73149_a(int var1, int var2) {
      return true;
   }

   public void func_73153_a(IChunkProvider var1, int var2, int var3) {
   }

   public boolean func_177460_a(IChunkProvider var1, Chunk var2, int var3, int var4) {
      return false;
   }

   public boolean func_73151_a(boolean var1, IProgressUpdate var2) {
      return true;
   }

   public void func_104112_b() {
   }

   public boolean func_73156_b() {
      return false;
   }

   public boolean func_73157_c() {
      return true;
   }

   public String func_73148_d() {
      return "DebugLevelSource";
   }

   public List<BiomeGenBase.SpawnListEntry> func_177458_a(EnumCreatureType var1, BlockPos var2) {
      BiomeGenBase var3 = this.field_177463_c.func_180494_b(var2);
      return var3.func_76747_a(var1);
   }

   public BlockPos func_180513_a(World var1, String var2, BlockPos var3) {
      return null;
   }

   public int func_73152_e() {
      return 0;
   }

   public void func_180514_a(Chunk var1, int var2, int var3) {
   }

   public Chunk func_177459_a(BlockPos var1) {
      return this.func_73154_d(var1.func_177958_n() >> 4, var1.func_177952_p() >> 4);
   }

   static {
      Iterator var0 = Block.field_149771_c.iterator();

      while(var0.hasNext()) {
         Block var1 = (Block)var0.next();
         field_177464_a.addAll(var1.func_176194_O().func_177619_a());
      }

      field_177462_b = MathHelper.func_76123_f(MathHelper.func_76129_c((float)field_177464_a.size()));
      field_181039_c = MathHelper.func_76123_f((float)field_177464_a.size() / (float)field_177462_b);
   }
}
