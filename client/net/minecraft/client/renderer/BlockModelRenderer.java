package net.minecraft.client.renderer;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import java.util.BitSet;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.IWorldReaderBase;

public class BlockModelRenderer {
   private final BlockColors field_187499_a;
   private static final ThreadLocal<Object2IntLinkedOpenHashMap<BlockPos>> field_210267_b = ThreadLocal.withInitial(() -> {
      Object2IntLinkedOpenHashMap var0 = new Object2IntLinkedOpenHashMap<BlockPos>(50) {
         protected void rehash(int var1) {
         }
      };
      var0.defaultReturnValue(2147483647);
      return var0;
   });
   private static final ThreadLocal<Boolean> field_211848_c = ThreadLocal.withInitial(() -> {
      return false;
   });

   public BlockModelRenderer(BlockColors var1) {
      super();
      this.field_187499_a = var1;
   }

   public boolean func_199324_a(IWorldReader var1, IBakedModel var2, IBlockState var3, BlockPos var4, BufferBuilder var5, boolean var6, Random var7, long var8) {
      boolean var10 = Minecraft.func_71379_u() && var3.func_185906_d() == 0 && var2.func_177555_b();

      try {
         return var10 ? this.func_199326_b(var1, var2, var3, var4, var5, var6, var7, var8) : this.func_199325_c(var1, var2, var3, var4, var5, var6, var7, var8);
      } catch (Throwable var14) {
         CrashReport var12 = CrashReport.func_85055_a(var14, "Tesselating block model");
         CrashReportCategory var13 = var12.func_85058_a("Block model being tesselated");
         CrashReportCategory.func_175750_a(var13, var4, var3);
         var13.func_71507_a("Using AO", var10);
         throw new ReportedException(var12);
      }
   }

   public boolean func_199326_b(IWorldReader var1, IBakedModel var2, IBlockState var3, BlockPos var4, BufferBuilder var5, boolean var6, Random var7, long var8) {
      boolean var10 = false;
      float[] var11 = new float[EnumFacing.values().length * 2];
      BitSet var12 = new BitSet(3);
      BlockModelRenderer.AmbientOcclusionFace var13 = new BlockModelRenderer.AmbientOcclusionFace();
      EnumFacing[] var14 = EnumFacing.values();
      int var15 = var14.length;

      for(int var16 = 0; var16 < var15; ++var16) {
         EnumFacing var17 = var14[var16];
         var7.setSeed(var8);
         List var18 = var2.func_200117_a(var3, var17, var7);
         if (!var18.isEmpty() && (!var6 || Block.func_176225_a(var3, var1, var4, var17))) {
            this.func_187492_a(var1, var3, var4, var5, var18, var11, var12, var13);
            var10 = true;
         }
      }

      var7.setSeed(var8);
      List var19 = var2.func_200117_a(var3, (EnumFacing)null, var7);
      if (!var19.isEmpty()) {
         this.func_187492_a(var1, var3, var4, var5, var19, var11, var12, var13);
         var10 = true;
      }

      return var10;
   }

   public boolean func_199325_c(IWorldReader var1, IBakedModel var2, IBlockState var3, BlockPos var4, BufferBuilder var5, boolean var6, Random var7, long var8) {
      boolean var10 = false;
      BitSet var11 = new BitSet(3);
      EnumFacing[] var12 = EnumFacing.values();
      int var13 = var12.length;

      for(int var14 = 0; var14 < var13; ++var14) {
         EnumFacing var15 = var12[var14];
         var7.setSeed(var8);
         List var16 = var2.func_200117_a(var3, var15, var7);
         if (!var16.isEmpty() && (!var6 || Block.func_176225_a(var3, var1, var4, var15))) {
            int var17 = var3.func_185889_a(var1, var4.func_177972_a(var15));
            this.func_187496_a(var1, var3, var4, var17, false, var5, var16, var11);
            var10 = true;
         }
      }

      var7.setSeed(var8);
      List var18 = var2.func_200117_a(var3, (EnumFacing)null, var7);
      if (!var18.isEmpty()) {
         this.func_187496_a(var1, var3, var4, -1, true, var5, var18, var11);
         var10 = true;
      }

      return var10;
   }

   private void func_187492_a(IWorldReader var1, IBlockState var2, BlockPos var3, BufferBuilder var4, List<BakedQuad> var5, float[] var6, BitSet var7, BlockModelRenderer.AmbientOcclusionFace var8) {
      Vec3d var9 = var2.func_191059_e(var1, var3);
      double var10 = (double)var3.func_177958_n() + var9.field_72450_a;
      double var12 = (double)var3.func_177956_o() + var9.field_72448_b;
      double var14 = (double)var3.func_177952_p() + var9.field_72449_c;
      int var16 = 0;

      for(int var17 = var5.size(); var16 < var17; ++var16) {
         BakedQuad var18 = (BakedQuad)var5.get(var16);
         this.func_187494_a(var2, var18.func_178209_a(), var18.func_178210_d(), var6, var7);
         var8.func_187491_a(var1, var2, var3, var18.func_178210_d(), var6, var7);
         var4.func_178981_a(var18.func_178209_a());
         var4.func_178962_a(var8.field_178207_c[0], var8.field_178207_c[1], var8.field_178207_c[2], var8.field_178207_c[3]);
         if (var18.func_178212_b()) {
            int var19 = this.field_187499_a.func_186724_a(var2, var1, var3, var18.func_178211_c());
            float var20 = (float)(var19 >> 16 & 255) / 255.0F;
            float var21 = (float)(var19 >> 8 & 255) / 255.0F;
            float var22 = (float)(var19 & 255) / 255.0F;
            var4.func_178978_a(var8.field_178206_b[0] * var20, var8.field_178206_b[0] * var21, var8.field_178206_b[0] * var22, 4);
            var4.func_178978_a(var8.field_178206_b[1] * var20, var8.field_178206_b[1] * var21, var8.field_178206_b[1] * var22, 3);
            var4.func_178978_a(var8.field_178206_b[2] * var20, var8.field_178206_b[2] * var21, var8.field_178206_b[2] * var22, 2);
            var4.func_178978_a(var8.field_178206_b[3] * var20, var8.field_178206_b[3] * var21, var8.field_178206_b[3] * var22, 1);
         } else {
            var4.func_178978_a(var8.field_178206_b[0], var8.field_178206_b[0], var8.field_178206_b[0], 4);
            var4.func_178978_a(var8.field_178206_b[1], var8.field_178206_b[1], var8.field_178206_b[1], 3);
            var4.func_178978_a(var8.field_178206_b[2], var8.field_178206_b[2], var8.field_178206_b[2], 2);
            var4.func_178978_a(var8.field_178206_b[3], var8.field_178206_b[3], var8.field_178206_b[3], 1);
         }

         var4.func_178987_a(var10, var12, var14);
      }

   }

   private void func_187494_a(IBlockState var1, int[] var2, EnumFacing var3, @Nullable float[] var4, BitSet var5) {
      float var6 = 32.0F;
      float var7 = 32.0F;
      float var8 = 32.0F;
      float var9 = -32.0F;
      float var10 = -32.0F;
      float var11 = -32.0F;

      int var12;
      float var13;
      for(var12 = 0; var12 < 4; ++var12) {
         var13 = Float.intBitsToFloat(var2[var12 * 7]);
         float var14 = Float.intBitsToFloat(var2[var12 * 7 + 1]);
         float var15 = Float.intBitsToFloat(var2[var12 * 7 + 2]);
         var6 = Math.min(var6, var13);
         var7 = Math.min(var7, var14);
         var8 = Math.min(var8, var15);
         var9 = Math.max(var9, var13);
         var10 = Math.max(var10, var14);
         var11 = Math.max(var11, var15);
      }

      if (var4 != null) {
         var4[EnumFacing.WEST.func_176745_a()] = var6;
         var4[EnumFacing.EAST.func_176745_a()] = var9;
         var4[EnumFacing.DOWN.func_176745_a()] = var7;
         var4[EnumFacing.UP.func_176745_a()] = var10;
         var4[EnumFacing.NORTH.func_176745_a()] = var8;
         var4[EnumFacing.SOUTH.func_176745_a()] = var11;
         var12 = EnumFacing.values().length;
         var4[EnumFacing.WEST.func_176745_a() + var12] = 1.0F - var6;
         var4[EnumFacing.EAST.func_176745_a() + var12] = 1.0F - var9;
         var4[EnumFacing.DOWN.func_176745_a() + var12] = 1.0F - var7;
         var4[EnumFacing.UP.func_176745_a() + var12] = 1.0F - var10;
         var4[EnumFacing.NORTH.func_176745_a() + var12] = 1.0F - var8;
         var4[EnumFacing.SOUTH.func_176745_a() + var12] = 1.0F - var11;
      }

      float var16 = 1.0E-4F;
      var13 = 0.9999F;
      switch(var3) {
      case DOWN:
         var5.set(1, var6 >= 1.0E-4F || var8 >= 1.0E-4F || var9 <= 0.9999F || var11 <= 0.9999F);
         var5.set(0, (var7 < 1.0E-4F || var1.func_185917_h()) && var7 == var10);
         break;
      case UP:
         var5.set(1, var6 >= 1.0E-4F || var8 >= 1.0E-4F || var9 <= 0.9999F || var11 <= 0.9999F);
         var5.set(0, (var10 > 0.9999F || var1.func_185917_h()) && var7 == var10);
         break;
      case NORTH:
         var5.set(1, var6 >= 1.0E-4F || var7 >= 1.0E-4F || var9 <= 0.9999F || var10 <= 0.9999F);
         var5.set(0, (var8 < 1.0E-4F || var1.func_185917_h()) && var8 == var11);
         break;
      case SOUTH:
         var5.set(1, var6 >= 1.0E-4F || var7 >= 1.0E-4F || var9 <= 0.9999F || var10 <= 0.9999F);
         var5.set(0, (var11 > 0.9999F || var1.func_185917_h()) && var8 == var11);
         break;
      case WEST:
         var5.set(1, var7 >= 1.0E-4F || var8 >= 1.0E-4F || var10 <= 0.9999F || var11 <= 0.9999F);
         var5.set(0, (var6 < 1.0E-4F || var1.func_185917_h()) && var6 == var9);
         break;
      case EAST:
         var5.set(1, var7 >= 1.0E-4F || var8 >= 1.0E-4F || var10 <= 0.9999F || var11 <= 0.9999F);
         var5.set(0, (var9 > 0.9999F || var1.func_185917_h()) && var6 == var9);
      }

   }

   private void func_187496_a(IWorldReader var1, IBlockState var2, BlockPos var3, int var4, boolean var5, BufferBuilder var6, List<BakedQuad> var7, BitSet var8) {
      Vec3d var9 = var2.func_191059_e(var1, var3);
      double var10 = (double)var3.func_177958_n() + var9.field_72450_a;
      double var12 = (double)var3.func_177956_o() + var9.field_72448_b;
      double var14 = (double)var3.func_177952_p() + var9.field_72449_c;
      int var16 = 0;

      for(int var17 = var7.size(); var16 < var17; ++var16) {
         BakedQuad var18 = (BakedQuad)var7.get(var16);
         if (var5) {
            this.func_187494_a(var2, var18.func_178209_a(), var18.func_178210_d(), (float[])null, var8);
            BlockPos var19 = var8.get(0) ? var3.func_177972_a(var18.func_178210_d()) : var3;
            var4 = var2.func_185889_a(var1, var19);
         }

         var6.func_178981_a(var18.func_178209_a());
         var6.func_178962_a(var4, var4, var4, var4);
         if (var18.func_178212_b()) {
            int var23 = this.field_187499_a.func_186724_a(var2, var1, var3, var18.func_178211_c());
            float var20 = (float)(var23 >> 16 & 255) / 255.0F;
            float var21 = (float)(var23 >> 8 & 255) / 255.0F;
            float var22 = (float)(var23 & 255) / 255.0F;
            var6.func_178978_a(var20, var21, var22, 4);
            var6.func_178978_a(var20, var21, var22, 3);
            var6.func_178978_a(var20, var21, var22, 2);
            var6.func_178978_a(var20, var21, var22, 1);
         }

         var6.func_178987_a(var10, var12, var14);
      }

   }

   public void func_178262_a(IBakedModel var1, float var2, float var3, float var4, float var5) {
      this.func_187495_a((IBlockState)null, var1, var2, var3, var4, var5);
   }

   public void func_187495_a(@Nullable IBlockState var1, IBakedModel var2, float var3, float var4, float var5, float var6) {
      Random var7 = new Random();
      long var8 = 42L;
      EnumFacing[] var10 = EnumFacing.values();
      int var11 = var10.length;

      for(int var12 = 0; var12 < var11; ++var12) {
         EnumFacing var13 = var10[var12];
         var7.setSeed(42L);
         this.func_178264_a(var3, var4, var5, var6, var2.func_200117_a(var1, var13, var7));
      }

      var7.setSeed(42L);
      this.func_178264_a(var3, var4, var5, var6, var2.func_200117_a(var1, (EnumFacing)null, var7));
   }

   public void func_178266_a(IBakedModel var1, IBlockState var2, float var3, boolean var4) {
      GlStateManager.func_179114_b(90.0F, 0.0F, 1.0F, 0.0F);
      int var5 = this.field_187499_a.func_186724_a(var2, (IWorldReaderBase)null, (BlockPos)null, 0);
      float var6 = (float)(var5 >> 16 & 255) / 255.0F;
      float var7 = (float)(var5 >> 8 & 255) / 255.0F;
      float var8 = (float)(var5 & 255) / 255.0F;
      if (!var4) {
         GlStateManager.func_179131_c(var3, var3, var3, 1.0F);
      }

      this.func_187495_a(var2, var1, var3, var6, var7, var8);
   }

   private void func_178264_a(float var1, float var2, float var3, float var4, List<BakedQuad> var5) {
      Tessellator var6 = Tessellator.func_178181_a();
      BufferBuilder var7 = var6.func_178180_c();
      int var8 = 0;

      for(int var9 = var5.size(); var8 < var9; ++var8) {
         BakedQuad var10 = (BakedQuad)var5.get(var8);
         var7.func_181668_a(7, DefaultVertexFormats.field_176599_b);
         var7.func_178981_a(var10.func_178209_a());
         if (var10.func_178212_b()) {
            var7.func_178990_f(var2 * var1, var3 * var1, var4 * var1);
         } else {
            var7.func_178990_f(var1, var1, var1);
         }

         Vec3i var11 = var10.func_178210_d().func_176730_m();
         var7.func_178975_e((float)var11.func_177958_n(), (float)var11.func_177956_o(), (float)var11.func_177952_p());
         var6.func_78381_a();
      }

   }

   public static void func_211847_a() {
      field_211848_c.set(true);
   }

   public static void func_210266_a() {
      ((Object2IntLinkedOpenHashMap)field_210267_b.get()).clear();
      field_211848_c.set(false);
   }

   private static int func_210264_b(IBlockState var0, IWorldReader var1, BlockPos var2) {
      Boolean var3 = (Boolean)field_211848_c.get();
      Object2IntLinkedOpenHashMap var4 = null;
      int var5;
      if (var3) {
         var4 = (Object2IntLinkedOpenHashMap)field_210267_b.get();
         var5 = var4.getInt(var2);
         if (var5 != 2147483647) {
            return var5;
         }
      }

      var5 = var0.func_185889_a(var1, var2);
      if (var4 != null) {
         if (var4.size() == 50) {
            var4.removeFirstInt();
         }

         var4.put(var2.func_185334_h(), var5);
      }

      return var5;
   }

   public static enum EnumNeighborInfo {
      DOWN(new EnumFacing[]{EnumFacing.WEST, EnumFacing.EAST, EnumFacing.NORTH, EnumFacing.SOUTH}, 0.5F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.SOUTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.SOUTH}),
      UP(new EnumFacing[]{EnumFacing.EAST, EnumFacing.WEST, EnumFacing.NORTH, EnumFacing.SOUTH}, 1.0F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.SOUTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.SOUTH}),
      NORTH(new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN, EnumFacing.EAST, EnumFacing.WEST}, 0.8F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_WEST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_EAST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_EAST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_WEST}),
      SOUTH(new EnumFacing[]{EnumFacing.WEST, EnumFacing.EAST, EnumFacing.DOWN, EnumFacing.UP}, 0.8F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.WEST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.WEST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.EAST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.EAST}),
      WEST(new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.SOUTH}, 0.6F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.SOUTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.SOUTH}),
      EAST(new EnumFacing[]{EnumFacing.DOWN, EnumFacing.UP, EnumFacing.NORTH, EnumFacing.SOUTH}, 0.6F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.SOUTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.SOUTH});

      private final EnumFacing[] field_178276_g;
      private final boolean field_178289_i;
      private final BlockModelRenderer.Orientation[] field_178286_j;
      private final BlockModelRenderer.Orientation[] field_178287_k;
      private final BlockModelRenderer.Orientation[] field_178284_l;
      private final BlockModelRenderer.Orientation[] field_178285_m;
      private static final BlockModelRenderer.EnumNeighborInfo[] field_178282_n = (BlockModelRenderer.EnumNeighborInfo[])Util.func_200696_a(new BlockModelRenderer.EnumNeighborInfo[6], (var0) -> {
         var0[EnumFacing.DOWN.func_176745_a()] = DOWN;
         var0[EnumFacing.UP.func_176745_a()] = UP;
         var0[EnumFacing.NORTH.func_176745_a()] = NORTH;
         var0[EnumFacing.SOUTH.func_176745_a()] = SOUTH;
         var0[EnumFacing.WEST.func_176745_a()] = WEST;
         var0[EnumFacing.EAST.func_176745_a()] = EAST;
      });

      private EnumNeighborInfo(EnumFacing[] var3, float var4, boolean var5, BlockModelRenderer.Orientation[] var6, BlockModelRenderer.Orientation[] var7, BlockModelRenderer.Orientation[] var8, BlockModelRenderer.Orientation[] var9) {
         this.field_178276_g = var3;
         this.field_178289_i = var5;
         this.field_178286_j = var6;
         this.field_178287_k = var7;
         this.field_178284_l = var8;
         this.field_178285_m = var9;
      }

      public static BlockModelRenderer.EnumNeighborInfo func_178273_a(EnumFacing var0) {
         return field_178282_n[var0.func_176745_a()];
      }
   }

   public static enum Orientation {
      DOWN(EnumFacing.DOWN, false),
      UP(EnumFacing.UP, false),
      NORTH(EnumFacing.NORTH, false),
      SOUTH(EnumFacing.SOUTH, false),
      WEST(EnumFacing.WEST, false),
      EAST(EnumFacing.EAST, false),
      FLIP_DOWN(EnumFacing.DOWN, true),
      FLIP_UP(EnumFacing.UP, true),
      FLIP_NORTH(EnumFacing.NORTH, true),
      FLIP_SOUTH(EnumFacing.SOUTH, true),
      FLIP_WEST(EnumFacing.WEST, true),
      FLIP_EAST(EnumFacing.EAST, true);

      private final int field_178229_m;

      private Orientation(EnumFacing var3, boolean var4) {
         this.field_178229_m = var3.func_176745_a() + (var4 ? EnumFacing.values().length : 0);
      }
   }

   class AmbientOcclusionFace {
      private final float[] field_178206_b = new float[4];
      private final int[] field_178207_c = new int[4];

      public AmbientOcclusionFace() {
         super();
      }

      public void func_187491_a(IWorldReader var1, IBlockState var2, BlockPos var3, EnumFacing var4, float[] var5, BitSet var6) {
         BlockPos var7 = var6.get(0) ? var3.func_177972_a(var4) : var3;
         BlockModelRenderer.EnumNeighborInfo var8 = BlockModelRenderer.EnumNeighborInfo.func_178273_a(var4);
         BlockPos.MutableBlockPos var9 = new BlockPos.MutableBlockPos();
         var9.func_189533_g(var7).func_189536_c(var8.field_178276_g[0]);
         int var10 = BlockModelRenderer.func_210264_b(var2, var1, var9);
         float var11 = var1.func_180495_p(var9).func_185892_j();
         var9.func_189533_g(var7).func_189536_c(var8.field_178276_g[1]);
         int var12 = BlockModelRenderer.func_210264_b(var2, var1, var9);
         float var13 = var1.func_180495_p(var9).func_185892_j();
         var9.func_189533_g(var7).func_189536_c(var8.field_178276_g[2]);
         int var14 = BlockModelRenderer.func_210264_b(var2, var1, var9);
         float var15 = var1.func_180495_p(var9).func_185892_j();
         var9.func_189533_g(var7).func_189536_c(var8.field_178276_g[3]);
         int var16 = BlockModelRenderer.func_210264_b(var2, var1, var9);
         float var17 = var1.func_180495_p(var9).func_185892_j();
         var9.func_189533_g(var7).func_189536_c(var8.field_178276_g[0]).func_189536_c(var4);
         boolean var18 = var1.func_180495_p(var9).func_200016_a(var1, var9) == 0;
         var9.func_189533_g(var7).func_189536_c(var8.field_178276_g[1]).func_189536_c(var4);
         boolean var19 = var1.func_180495_p(var9).func_200016_a(var1, var9) == 0;
         var9.func_189533_g(var7).func_189536_c(var8.field_178276_g[2]).func_189536_c(var4);
         boolean var20 = var1.func_180495_p(var9).func_200016_a(var1, var9) == 0;
         var9.func_189533_g(var7).func_189536_c(var8.field_178276_g[3]).func_189536_c(var4);
         boolean var21 = var1.func_180495_p(var9).func_200016_a(var1, var9) == 0;
         float var22;
         int var26;
         if (!var20 && !var18) {
            var22 = var11;
            var26 = var10;
         } else {
            var9.func_189533_g(var7).func_189536_c(var8.field_178276_g[0]).func_189536_c(var8.field_178276_g[2]);
            var22 = var1.func_180495_p(var9).func_185892_j();
            var26 = BlockModelRenderer.func_210264_b(var2, var1, var9);
         }

         float var23;
         int var27;
         if (!var21 && !var18) {
            var23 = var11;
            var27 = var10;
         } else {
            var9.func_189533_g(var7).func_189536_c(var8.field_178276_g[0]).func_189536_c(var8.field_178276_g[3]);
            var23 = var1.func_180495_p(var9).func_185892_j();
            var27 = BlockModelRenderer.func_210264_b(var2, var1, var9);
         }

         float var24;
         int var28;
         if (!var20 && !var19) {
            var24 = var13;
            var28 = var12;
         } else {
            var9.func_189533_g(var7).func_189536_c(var8.field_178276_g[1]).func_189536_c(var8.field_178276_g[2]);
            var24 = var1.func_180495_p(var9).func_185892_j();
            var28 = BlockModelRenderer.func_210264_b(var2, var1, var9);
         }

         float var25;
         int var29;
         if (!var21 && !var19) {
            var25 = var13;
            var29 = var12;
         } else {
            var9.func_189533_g(var7).func_189536_c(var8.field_178276_g[1]).func_189536_c(var8.field_178276_g[3]);
            var25 = var1.func_180495_p(var9).func_185892_j();
            var29 = BlockModelRenderer.func_210264_b(var2, var1, var9);
         }

         int var30 = BlockModelRenderer.func_210264_b(var2, var1, var3);
         var9.func_189533_g(var3).func_189536_c(var4);
         if (var6.get(0) || !var1.func_180495_p(var9).func_200015_d(var1, var9)) {
            var30 = BlockModelRenderer.func_210264_b(var2, var1, var9);
         }

         float var31 = var6.get(0) ? var1.func_180495_p(var7).func_185892_j() : var1.func_180495_p(var3).func_185892_j();
         BlockModelRenderer.VertexTranslations var32 = BlockModelRenderer.VertexTranslations.func_178184_a(var4);
         float var33;
         float var34;
         float var35;
         float var36;
         if (var6.get(1) && var8.field_178289_i) {
            var33 = (var17 + var11 + var23 + var31) * 0.25F;
            var34 = (var15 + var11 + var22 + var31) * 0.25F;
            var35 = (var15 + var13 + var24 + var31) * 0.25F;
            var36 = (var17 + var13 + var25 + var31) * 0.25F;
            float var37 = var5[var8.field_178286_j[0].field_178229_m] * var5[var8.field_178286_j[1].field_178229_m];
            float var38 = var5[var8.field_178286_j[2].field_178229_m] * var5[var8.field_178286_j[3].field_178229_m];
            float var39 = var5[var8.field_178286_j[4].field_178229_m] * var5[var8.field_178286_j[5].field_178229_m];
            float var40 = var5[var8.field_178286_j[6].field_178229_m] * var5[var8.field_178286_j[7].field_178229_m];
            float var41 = var5[var8.field_178287_k[0].field_178229_m] * var5[var8.field_178287_k[1].field_178229_m];
            float var42 = var5[var8.field_178287_k[2].field_178229_m] * var5[var8.field_178287_k[3].field_178229_m];
            float var43 = var5[var8.field_178287_k[4].field_178229_m] * var5[var8.field_178287_k[5].field_178229_m];
            float var44 = var5[var8.field_178287_k[6].field_178229_m] * var5[var8.field_178287_k[7].field_178229_m];
            float var45 = var5[var8.field_178284_l[0].field_178229_m] * var5[var8.field_178284_l[1].field_178229_m];
            float var46 = var5[var8.field_178284_l[2].field_178229_m] * var5[var8.field_178284_l[3].field_178229_m];
            float var47 = var5[var8.field_178284_l[4].field_178229_m] * var5[var8.field_178284_l[5].field_178229_m];
            float var48 = var5[var8.field_178284_l[6].field_178229_m] * var5[var8.field_178284_l[7].field_178229_m];
            float var49 = var5[var8.field_178285_m[0].field_178229_m] * var5[var8.field_178285_m[1].field_178229_m];
            float var50 = var5[var8.field_178285_m[2].field_178229_m] * var5[var8.field_178285_m[3].field_178229_m];
            float var51 = var5[var8.field_178285_m[4].field_178229_m] * var5[var8.field_178285_m[5].field_178229_m];
            float var52 = var5[var8.field_178285_m[6].field_178229_m] * var5[var8.field_178285_m[7].field_178229_m];
            this.field_178206_b[var32.field_178191_g] = var33 * var37 + var34 * var38 + var35 * var39 + var36 * var40;
            this.field_178206_b[var32.field_178200_h] = var33 * var41 + var34 * var42 + var35 * var43 + var36 * var44;
            this.field_178206_b[var32.field_178201_i] = var33 * var45 + var34 * var46 + var35 * var47 + var36 * var48;
            this.field_178206_b[var32.field_178198_j] = var33 * var49 + var34 * var50 + var35 * var51 + var36 * var52;
            int var53 = this.func_147778_a(var16, var10, var27, var30);
            int var54 = this.func_147778_a(var14, var10, var26, var30);
            int var55 = this.func_147778_a(var14, var12, var28, var30);
            int var56 = this.func_147778_a(var16, var12, var29, var30);
            this.field_178207_c[var32.field_178191_g] = this.func_178203_a(var53, var54, var55, var56, var37, var38, var39, var40);
            this.field_178207_c[var32.field_178200_h] = this.func_178203_a(var53, var54, var55, var56, var41, var42, var43, var44);
            this.field_178207_c[var32.field_178201_i] = this.func_178203_a(var53, var54, var55, var56, var45, var46, var47, var48);
            this.field_178207_c[var32.field_178198_j] = this.func_178203_a(var53, var54, var55, var56, var49, var50, var51, var52);
         } else {
            var33 = (var17 + var11 + var23 + var31) * 0.25F;
            var34 = (var15 + var11 + var22 + var31) * 0.25F;
            var35 = (var15 + var13 + var24 + var31) * 0.25F;
            var36 = (var17 + var13 + var25 + var31) * 0.25F;
            this.field_178207_c[var32.field_178191_g] = this.func_147778_a(var16, var10, var27, var30);
            this.field_178207_c[var32.field_178200_h] = this.func_147778_a(var14, var10, var26, var30);
            this.field_178207_c[var32.field_178201_i] = this.func_147778_a(var14, var12, var28, var30);
            this.field_178207_c[var32.field_178198_j] = this.func_147778_a(var16, var12, var29, var30);
            this.field_178206_b[var32.field_178191_g] = var33;
            this.field_178206_b[var32.field_178200_h] = var34;
            this.field_178206_b[var32.field_178201_i] = var35;
            this.field_178206_b[var32.field_178198_j] = var36;
         }

      }

      private int func_147778_a(int var1, int var2, int var3, int var4) {
         if (var1 == 0) {
            var1 = var4;
         }

         if (var2 == 0) {
            var2 = var4;
         }

         if (var3 == 0) {
            var3 = var4;
         }

         return var1 + var2 + var3 + var4 >> 2 & 16711935;
      }

      private int func_178203_a(int var1, int var2, int var3, int var4, float var5, float var6, float var7, float var8) {
         int var9 = (int)((float)(var1 >> 16 & 255) * var5 + (float)(var2 >> 16 & 255) * var6 + (float)(var3 >> 16 & 255) * var7 + (float)(var4 >> 16 & 255) * var8) & 255;
         int var10 = (int)((float)(var1 & 255) * var5 + (float)(var2 & 255) * var6 + (float)(var3 & 255) * var7 + (float)(var4 & 255) * var8) & 255;
         return var9 << 16 | var10;
      }
   }

   static enum VertexTranslations {
      DOWN(0, 1, 2, 3),
      UP(2, 3, 0, 1),
      NORTH(3, 0, 1, 2),
      SOUTH(0, 1, 2, 3),
      WEST(3, 0, 1, 2),
      EAST(1, 2, 3, 0);

      private final int field_178191_g;
      private final int field_178200_h;
      private final int field_178201_i;
      private final int field_178198_j;
      private static final BlockModelRenderer.VertexTranslations[] field_178199_k = (BlockModelRenderer.VertexTranslations[])Util.func_200696_a(new BlockModelRenderer.VertexTranslations[6], (var0) -> {
         var0[EnumFacing.DOWN.func_176745_a()] = DOWN;
         var0[EnumFacing.UP.func_176745_a()] = UP;
         var0[EnumFacing.NORTH.func_176745_a()] = NORTH;
         var0[EnumFacing.SOUTH.func_176745_a()] = SOUTH;
         var0[EnumFacing.WEST.func_176745_a()] = WEST;
         var0[EnumFacing.EAST.func_176745_a()] = EAST;
      });

      private VertexTranslations(int var3, int var4, int var5, int var6) {
         this.field_178191_g = var3;
         this.field_178200_h = var4;
         this.field_178201_i = var5;
         this.field_178198_j = var6;
      }

      public static BlockModelRenderer.VertexTranslations func_178184_a(EnumFacing var0) {
         return field_178199_k[var0.func_176745_a()];
      }
   }
}
