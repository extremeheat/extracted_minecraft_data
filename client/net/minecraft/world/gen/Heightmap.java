package net.minecraft.world.gen;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.AllowsMovementAndSolidMatcher;
import net.minecraft.block.state.pattern.AllowsMovementMatcher;
import net.minecraft.block.state.pattern.BlockMatcherReaderAware;
import net.minecraft.block.state.pattern.BlockTagMatcher;
import net.minecraft.block.state.pattern.IBlockMatcherReaderAware;
import net.minecraft.block.state.pattern.LightEmittingMatcher;
import net.minecraft.block.state.pattern.LiquidBlockMatcher;
import net.minecraft.block.state.pattern.ReaderAwareMatchers;
import net.minecraft.init.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.BitArray;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.IChunk;

public class Heightmap {
   private final BitArray field_202275_a = new BitArray(9, 256);
   private final IBlockMatcherReaderAware<IBlockState> field_202276_b;
   private final IChunk field_202277_c;

   public Heightmap(IChunk var1, Heightmap.Type var2) {
      super();
      this.field_202276_b = ReaderAwareMatchers.func_202084_a(ReaderAwareMatchers.func_202083_b(var2.func_202264_a()));
      this.field_202277_c = var1;
   }

   public void func_202266_a() {
      int var1 = this.field_202277_c.func_76625_h() + 16;
      BlockPos.PooledMutableBlockPos var2 = BlockPos.PooledMutableBlockPos.func_185346_s();
      Throwable var3 = null;

      try {
         for(int var4 = 0; var4 < 16; ++var4) {
            for(int var5 = 0; var5 < 16; ++var5) {
               this.func_202272_a(var4, var5, this.func_208518_a(var2, var4, var5, this.field_202276_b, var1));
            }
         }
      } catch (Throwable var13) {
         var3 = var13;
         throw var13;
      } finally {
         if (var2 != null) {
            if (var3 != null) {
               try {
                  var2.close();
               } catch (Throwable var12) {
                  var3.addSuppressed(var12);
               }
            } else {
               var2.close();
            }
         }

      }

   }

   public boolean func_202270_a(int var1, int var2, int var3, @Nullable IBlockState var4) {
      int var5 = this.func_202273_a(var1, var3);
      if (var2 <= var5 - 2) {
         return false;
      } else {
         if (this.field_202276_b.test(var4, this.field_202277_c, new BlockPos(var1, var2, var3))) {
            if (var2 >= var5) {
               this.func_202272_a(var1, var3, var2 + 1);
               return true;
            }
         } else if (var5 - 1 == var2) {
            this.func_202272_a(var1, var3, this.func_208518_a((BlockPos.MutableBlockPos)null, var1, var3, this.field_202276_b, var2));
            return true;
         }

         return false;
      }
   }

   private int func_208518_a(@Nullable BlockPos.MutableBlockPos var1, int var2, int var3, IBlockMatcherReaderAware<IBlockState> var4, int var5) {
      if (var1 == null) {
         var1 = new BlockPos.MutableBlockPos();
      }

      for(int var6 = var5 - 1; var6 >= 0; --var6) {
         var1.func_181079_c(var2, var6, var3);
         IBlockState var7 = this.field_202277_c.func_180495_p(var1);
         if (var4.test(var7, this.field_202277_c, var1)) {
            return var6 + 1;
         }
      }

      return 0;
   }

   public int func_202273_a(int var1, int var2) {
      return this.func_202274_b(func_202267_b(var1, var2));
   }

   private int func_202274_b(int var1) {
      return this.field_202275_a.func_188142_a(var1);
   }

   private void func_202272_a(int var1, int var2, int var3) {
      this.field_202275_a.func_188141_a(func_202267_b(var1, var2), var3);
   }

   public void func_202268_a(long[] var1) {
      System.arraycopy(var1, 0, this.field_202275_a.func_188143_a(), 0, var1.length);
   }

   public long[] func_202269_a() {
      return this.field_202275_a.func_188143_a();
   }

   private static int func_202267_b(int var0, int var1) {
      return var0 + var1 * 16;
   }

   public static enum Type {
      WORLD_SURFACE_WG("WORLD_SURFACE_WG", Heightmap.Usage.WORLDGEN, new IBlockMatcherReaderAware[]{BlockMatcherReaderAware.func_202081_a(Blocks.field_150350_a)}),
      OCEAN_FLOOR_WG("OCEAN_FLOOR_WG", Heightmap.Usage.WORLDGEN, new IBlockMatcherReaderAware[]{BlockMatcherReaderAware.func_202081_a(Blocks.field_150350_a), LiquidBlockMatcher.func_206902_a()}),
      LIGHT_BLOCKING("LIGHT_BLOCKING", Heightmap.Usage.LIVE_WORLD, new IBlockMatcherReaderAware[]{BlockMatcherReaderAware.func_202081_a(Blocks.field_150350_a), LightEmittingMatcher.func_202073_a()}),
      MOTION_BLOCKING("MOTION_BLOCKING", Heightmap.Usage.LIVE_WORLD, new IBlockMatcherReaderAware[]{BlockMatcherReaderAware.func_202081_a(Blocks.field_150350_a), AllowsMovementAndSolidMatcher.func_209402_a()}),
      MOTION_BLOCKING_NO_LEAVES("MOTION_BLOCKING_NO_LEAVES", Heightmap.Usage.LIVE_WORLD, new IBlockMatcherReaderAware[]{BlockMatcherReaderAware.func_202081_a(Blocks.field_150350_a), BlockTagMatcher.func_206904_a(BlockTags.field_206952_E), AllowsMovementAndSolidMatcher.func_209402_a()}),
      OCEAN_FLOOR("OCEAN_FLOOR", Heightmap.Usage.LIVE_WORLD, new IBlockMatcherReaderAware[]{BlockMatcherReaderAware.func_202081_a(Blocks.field_150350_a), AllowsMovementMatcher.func_202079_a()}),
      WORLD_SURFACE("WORLD_SURFACE", Heightmap.Usage.LIVE_WORLD, new IBlockMatcherReaderAware[]{BlockMatcherReaderAware.func_202081_a(Blocks.field_150350_a)});

      private final IBlockMatcherReaderAware<IBlockState>[] field_202265_e;
      private final String field_203502_f;
      private final Heightmap.Usage field_207513_i;
      private static final Map<String, Heightmap.Type> field_203503_g = (Map)Util.func_200696_a(Maps.newHashMap(), (var0) -> {
         Heightmap.Type[] var1 = values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            Heightmap.Type var4 = var1[var3];
            var0.put(var4.field_203502_f, var4);
         }

      });

      private Type(String var3, Heightmap.Usage var4, IBlockMatcherReaderAware<IBlockState>... var5) {
         this.field_203502_f = var3;
         this.field_202265_e = var5;
         this.field_207513_i = var4;
      }

      public IBlockMatcherReaderAware<IBlockState>[] func_202264_a() {
         return this.field_202265_e;
      }

      public String func_203500_b() {
         return this.field_203502_f;
      }

      public Heightmap.Usage func_207512_c() {
         return this.field_207513_i;
      }

      public static Heightmap.Type func_203501_a(String var0) {
         return (Heightmap.Type)field_203503_g.get(var0);
      }
   }

   public static enum Usage {
      WORLDGEN,
      LIVE_WORLD;

      private Usage() {
      }
   }
}
