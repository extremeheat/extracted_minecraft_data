package net.minecraft.client.renderer;

import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Vec3i;
import net.minecraft.world.IBlockAccess;

public class BlockModelRenderer {
   public BlockModelRenderer() {
      super();
   }

   public boolean func_178259_a(IBlockAccess var1, IBakedModel var2, IBlockState var3, BlockPos var4, WorldRenderer var5) {
      Block var6 = var3.func_177230_c();
      var6.func_180654_a(var1, var4);
      return this.func_178267_a(var1, var2, var3, var4, var5, true);
   }

   public boolean func_178267_a(IBlockAccess var1, IBakedModel var2, IBlockState var3, BlockPos var4, WorldRenderer var5, boolean var6) {
      boolean var7 = Minecraft.func_71379_u() && var3.func_177230_c().func_149750_m() == 0 && var2.func_177555_b();

      try {
         Block var8 = var3.func_177230_c();
         return var7 ? this.func_178265_a(var1, var2, var8, var4, var5, var6) : this.func_178258_b(var1, var2, var8, var4, var5, var6);
      } catch (Throwable var11) {
         CrashReport var9 = CrashReport.func_85055_a(var11, "Tesselating block model");
         CrashReportCategory var10 = var9.func_85058_a("Block model being tesselated");
         CrashReportCategory.func_175750_a(var10, var4, var3);
         var10.func_71507_a("Using AO", var7);
         throw new ReportedException(var9);
      }
   }

   public boolean func_178265_a(IBlockAccess var1, IBakedModel var2, Block var3, BlockPos var4, WorldRenderer var5, boolean var6) {
      boolean var7 = false;
      float[] var8 = new float[EnumFacing.values().length * 2];
      BitSet var9 = new BitSet(3);
      BlockModelRenderer.AmbientOcclusionFace var10 = new BlockModelRenderer.AmbientOcclusionFace();
      EnumFacing[] var11 = EnumFacing.values();
      int var12 = var11.length;

      for(int var13 = 0; var13 < var12; ++var13) {
         EnumFacing var14 = var11[var13];
         List var15 = var2.func_177551_a(var14);
         if (!var15.isEmpty()) {
            BlockPos var16 = var4.func_177972_a(var14);
            if (!var6 || var3.func_176225_a(var1, var16, var14)) {
               this.func_178263_a(var1, var3, var4, var5, var15, var8, var9, var10);
               var7 = true;
            }
         }
      }

      List var17 = var2.func_177550_a();
      if (var17.size() > 0) {
         this.func_178263_a(var1, var3, var4, var5, var17, var8, var9, var10);
         var7 = true;
      }

      return var7;
   }

   public boolean func_178258_b(IBlockAccess var1, IBakedModel var2, Block var3, BlockPos var4, WorldRenderer var5, boolean var6) {
      boolean var7 = false;
      BitSet var8 = new BitSet(3);
      EnumFacing[] var9 = EnumFacing.values();
      int var10 = var9.length;

      for(int var11 = 0; var11 < var10; ++var11) {
         EnumFacing var12 = var9[var11];
         List var13 = var2.func_177551_a(var12);
         if (!var13.isEmpty()) {
            BlockPos var14 = var4.func_177972_a(var12);
            if (!var6 || var3.func_176225_a(var1, var14, var12)) {
               int var15 = var3.func_176207_c(var1, var14);
               this.func_178260_a(var1, var3, var4, var12, var15, false, var5, var13, var8);
               var7 = true;
            }
         }
      }

      List var16 = var2.func_177550_a();
      if (var16.size() > 0) {
         this.func_178260_a(var1, var3, var4, (EnumFacing)null, -1, true, var5, var16, var8);
         var7 = true;
      }

      return var7;
   }

   private void func_178263_a(IBlockAccess var1, Block var2, BlockPos var3, WorldRenderer var4, List<BakedQuad> var5, float[] var6, BitSet var7, BlockModelRenderer.AmbientOcclusionFace var8) {
      double var9 = (double)var3.func_177958_n();
      double var11 = (double)var3.func_177956_o();
      double var13 = (double)var3.func_177952_p();
      Block.EnumOffsetType var15 = var2.func_176218_Q();
      if (var15 != Block.EnumOffsetType.NONE) {
         long var16 = MathHelper.func_180186_a(var3);
         var9 += ((double)((float)(var16 >> 16 & 15L) / 15.0F) - 0.5D) * 0.5D;
         var13 += ((double)((float)(var16 >> 24 & 15L) / 15.0F) - 0.5D) * 0.5D;
         if (var15 == Block.EnumOffsetType.XYZ) {
            var11 += ((double)((float)(var16 >> 20 & 15L) / 15.0F) - 1.0D) * 0.2D;
         }
      }

      for(Iterator var22 = var5.iterator(); var22.hasNext(); var4.func_178987_a(var9, var11, var13)) {
         BakedQuad var17 = (BakedQuad)var22.next();
         this.func_178261_a(var2, var17.func_178209_a(), var17.func_178210_d(), var6, var7);
         var8.func_178204_a(var1, var2, var3, var17.func_178210_d(), var6, var7);
         var4.func_178981_a(var17.func_178209_a());
         var4.func_178962_a(var8.field_178207_c[0], var8.field_178207_c[1], var8.field_178207_c[2], var8.field_178207_c[3]);
         if (var17.func_178212_b()) {
            int var18 = var2.func_180662_a(var1, var3, var17.func_178211_c());
            if (EntityRenderer.field_78517_a) {
               var18 = TextureUtil.func_177054_c(var18);
            }

            float var19 = (float)(var18 >> 16 & 255) / 255.0F;
            float var20 = (float)(var18 >> 8 & 255) / 255.0F;
            float var21 = (float)(var18 & 255) / 255.0F;
            var4.func_178978_a(var8.field_178206_b[0] * var19, var8.field_178206_b[0] * var20, var8.field_178206_b[0] * var21, 4);
            var4.func_178978_a(var8.field_178206_b[1] * var19, var8.field_178206_b[1] * var20, var8.field_178206_b[1] * var21, 3);
            var4.func_178978_a(var8.field_178206_b[2] * var19, var8.field_178206_b[2] * var20, var8.field_178206_b[2] * var21, 2);
            var4.func_178978_a(var8.field_178206_b[3] * var19, var8.field_178206_b[3] * var20, var8.field_178206_b[3] * var21, 1);
         } else {
            var4.func_178978_a(var8.field_178206_b[0], var8.field_178206_b[0], var8.field_178206_b[0], 4);
            var4.func_178978_a(var8.field_178206_b[1], var8.field_178206_b[1], var8.field_178206_b[1], 3);
            var4.func_178978_a(var8.field_178206_b[2], var8.field_178206_b[2], var8.field_178206_b[2], 2);
            var4.func_178978_a(var8.field_178206_b[3], var8.field_178206_b[3], var8.field_178206_b[3], 1);
         }
      }

   }

   private void func_178261_a(Block var1, int[] var2, EnumFacing var3, float[] var4, BitSet var5) {
      float var6 = 32.0F;
      float var7 = 32.0F;
      float var8 = 32.0F;
      float var9 = -32.0F;
      float var10 = -32.0F;
      float var11 = -32.0F;

      float var13;
      for(int var12 = 0; var12 < 4; ++var12) {
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
         var4[EnumFacing.WEST.func_176745_a() + EnumFacing.values().length] = 1.0F - var6;
         var4[EnumFacing.EAST.func_176745_a() + EnumFacing.values().length] = 1.0F - var9;
         var4[EnumFacing.DOWN.func_176745_a() + EnumFacing.values().length] = 1.0F - var7;
         var4[EnumFacing.UP.func_176745_a() + EnumFacing.values().length] = 1.0F - var10;
         var4[EnumFacing.NORTH.func_176745_a() + EnumFacing.values().length] = 1.0F - var8;
         var4[EnumFacing.SOUTH.func_176745_a() + EnumFacing.values().length] = 1.0F - var11;
      }

      float var16 = 1.0E-4F;
      var13 = 0.9999F;
      switch(var3) {
      case DOWN:
         var5.set(1, var6 >= 1.0E-4F || var8 >= 1.0E-4F || var9 <= 0.9999F || var11 <= 0.9999F);
         var5.set(0, (var7 < 1.0E-4F || var1.func_149686_d()) && var7 == var10);
         break;
      case UP:
         var5.set(1, var6 >= 1.0E-4F || var8 >= 1.0E-4F || var9 <= 0.9999F || var11 <= 0.9999F);
         var5.set(0, (var10 > 0.9999F || var1.func_149686_d()) && var7 == var10);
         break;
      case NORTH:
         var5.set(1, var6 >= 1.0E-4F || var7 >= 1.0E-4F || var9 <= 0.9999F || var10 <= 0.9999F);
         var5.set(0, (var8 < 1.0E-4F || var1.func_149686_d()) && var8 == var11);
         break;
      case SOUTH:
         var5.set(1, var6 >= 1.0E-4F || var7 >= 1.0E-4F || var9 <= 0.9999F || var10 <= 0.9999F);
         var5.set(0, (var11 > 0.9999F || var1.func_149686_d()) && var8 == var11);
         break;
      case WEST:
         var5.set(1, var7 >= 1.0E-4F || var8 >= 1.0E-4F || var10 <= 0.9999F || var11 <= 0.9999F);
         var5.set(0, (var6 < 1.0E-4F || var1.func_149686_d()) && var6 == var9);
         break;
      case EAST:
         var5.set(1, var7 >= 1.0E-4F || var8 >= 1.0E-4F || var10 <= 0.9999F || var11 <= 0.9999F);
         var5.set(0, (var9 > 0.9999F || var1.func_149686_d()) && var6 == var9);
      }

   }

   private void func_178260_a(IBlockAccess var1, Block var2, BlockPos var3, EnumFacing var4, int var5, boolean var6, WorldRenderer var7, List<BakedQuad> var8, BitSet var9) {
      double var10 = (double)var3.func_177958_n();
      double var12 = (double)var3.func_177956_o();
      double var14 = (double)var3.func_177952_p();
      Block.EnumOffsetType var16 = var2.func_176218_Q();
      if (var16 != Block.EnumOffsetType.NONE) {
         int var17 = var3.func_177958_n();
         int var18 = var3.func_177952_p();
         long var19 = (long)(var17 * 3129871) ^ (long)var18 * 116129781L;
         var19 = var19 * var19 * 42317861L + var19 * 11L;
         var10 += ((double)((float)(var19 >> 16 & 15L) / 15.0F) - 0.5D) * 0.5D;
         var14 += ((double)((float)(var19 >> 24 & 15L) / 15.0F) - 0.5D) * 0.5D;
         if (var16 == Block.EnumOffsetType.XYZ) {
            var12 += ((double)((float)(var19 >> 20 & 15L) / 15.0F) - 1.0D) * 0.2D;
         }
      }

      for(Iterator var23 = var8.iterator(); var23.hasNext(); var7.func_178987_a(var10, var12, var14)) {
         BakedQuad var24 = (BakedQuad)var23.next();
         if (var6) {
            this.func_178261_a(var2, var24.func_178209_a(), var24.func_178210_d(), (float[])null, var9);
            var5 = var9.get(0) ? var2.func_176207_c(var1, var3.func_177972_a(var24.func_178210_d())) : var2.func_176207_c(var1, var3);
         }

         var7.func_178981_a(var24.func_178209_a());
         var7.func_178962_a(var5, var5, var5, var5);
         if (var24.func_178212_b()) {
            int var25 = var2.func_180662_a(var1, var3, var24.func_178211_c());
            if (EntityRenderer.field_78517_a) {
               var25 = TextureUtil.func_177054_c(var25);
            }

            float var20 = (float)(var25 >> 16 & 255) / 255.0F;
            float var21 = (float)(var25 >> 8 & 255) / 255.0F;
            float var22 = (float)(var25 & 255) / 255.0F;
            var7.func_178978_a(var20, var21, var22, 4);
            var7.func_178978_a(var20, var21, var22, 3);
            var7.func_178978_a(var20, var21, var22, 2);
            var7.func_178978_a(var20, var21, var22, 1);
         }
      }

   }

   public void func_178262_a(IBakedModel var1, float var2, float var3, float var4, float var5) {
      EnumFacing[] var6 = EnumFacing.values();
      int var7 = var6.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         EnumFacing var9 = var6[var8];
         this.func_178264_a(var2, var3, var4, var5, var1.func_177551_a(var9));
      }

      this.func_178264_a(var2, var3, var4, var5, var1.func_177550_a());
   }

   public void func_178266_a(IBakedModel var1, IBlockState var2, float var3, boolean var4) {
      Block var5 = var2.func_177230_c();
      var5.func_149683_g();
      GlStateManager.func_179114_b(90.0F, 0.0F, 1.0F, 0.0F);
      int var6 = var5.func_180644_h(var5.func_176217_b(var2));
      if (EntityRenderer.field_78517_a) {
         var6 = TextureUtil.func_177054_c(var6);
      }

      float var7 = (float)(var6 >> 16 & 255) / 255.0F;
      float var8 = (float)(var6 >> 8 & 255) / 255.0F;
      float var9 = (float)(var6 & 255) / 255.0F;
      if (!var4) {
         GlStateManager.func_179131_c(var3, var3, var3, 1.0F);
      }

      this.func_178262_a(var1, var3, var7, var8, var9);
   }

   private void func_178264_a(float var1, float var2, float var3, float var4, List<BakedQuad> var5) {
      Tessellator var6 = Tessellator.func_178181_a();
      WorldRenderer var7 = var6.func_178180_c();
      Iterator var8 = var5.iterator();

      while(var8.hasNext()) {
         BakedQuad var9 = (BakedQuad)var8.next();
         var7.func_181668_a(7, DefaultVertexFormats.field_176599_b);
         var7.func_178981_a(var9.func_178209_a());
         if (var9.func_178212_b()) {
            var7.func_178990_f(var2 * var1, var3 * var1, var4 * var1);
         } else {
            var7.func_178990_f(var1, var1, var1);
         }

         Vec3i var10 = var9.func_178210_d().func_176730_m();
         var7.func_178975_e((float)var10.func_177958_n(), (float)var10.func_177956_o(), (float)var10.func_177952_p());
         var6.func_78381_a();
      }

   }

   public static enum EnumNeighborInfo {
      DOWN(new EnumFacing[]{EnumFacing.WEST, EnumFacing.EAST, EnumFacing.NORTH, EnumFacing.SOUTH}, 0.5F, false, new BlockModelRenderer.Orientation[0], new BlockModelRenderer.Orientation[0], new BlockModelRenderer.Orientation[0], new BlockModelRenderer.Orientation[0]),
      UP(new EnumFacing[]{EnumFacing.EAST, EnumFacing.WEST, EnumFacing.NORTH, EnumFacing.SOUTH}, 1.0F, false, new BlockModelRenderer.Orientation[0], new BlockModelRenderer.Orientation[0], new BlockModelRenderer.Orientation[0], new BlockModelRenderer.Orientation[0]),
      NORTH(new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN, EnumFacing.EAST, EnumFacing.WEST}, 0.8F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_WEST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_EAST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_EAST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_WEST}),
      SOUTH(new EnumFacing[]{EnumFacing.WEST, EnumFacing.EAST, EnumFacing.DOWN, EnumFacing.UP}, 0.8F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.WEST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.WEST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.EAST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.EAST}),
      WEST(new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.SOUTH}, 0.6F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.SOUTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.SOUTH}),
      EAST(new EnumFacing[]{EnumFacing.DOWN, EnumFacing.UP, EnumFacing.NORTH, EnumFacing.SOUTH}, 0.6F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.SOUTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.SOUTH});

      protected final EnumFacing[] field_178276_g;
      protected final float field_178288_h;
      protected final boolean field_178289_i;
      protected final BlockModelRenderer.Orientation[] field_178286_j;
      protected final BlockModelRenderer.Orientation[] field_178287_k;
      protected final BlockModelRenderer.Orientation[] field_178284_l;
      protected final BlockModelRenderer.Orientation[] field_178285_m;
      private static final BlockModelRenderer.EnumNeighborInfo[] field_178282_n = new BlockModelRenderer.EnumNeighborInfo[6];

      private EnumNeighborInfo(EnumFacing[] var3, float var4, boolean var5, BlockModelRenderer.Orientation[] var6, BlockModelRenderer.Orientation[] var7, BlockModelRenderer.Orientation[] var8, BlockModelRenderer.Orientation[] var9) {
         this.field_178276_g = var3;
         this.field_178288_h = var4;
         this.field_178289_i = var5;
         this.field_178286_j = var6;
         this.field_178287_k = var7;
         this.field_178284_l = var8;
         this.field_178285_m = var9;
      }

      public static BlockModelRenderer.EnumNeighborInfo func_178273_a(EnumFacing var0) {
         return field_178282_n[var0.func_176745_a()];
      }

      static {
         field_178282_n[EnumFacing.DOWN.func_176745_a()] = DOWN;
         field_178282_n[EnumFacing.UP.func_176745_a()] = UP;
         field_178282_n[EnumFacing.NORTH.func_176745_a()] = NORTH;
         field_178282_n[EnumFacing.SOUTH.func_176745_a()] = SOUTH;
         field_178282_n[EnumFacing.WEST.func_176745_a()] = WEST;
         field_178282_n[EnumFacing.EAST.func_176745_a()] = EAST;
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

      protected final int field_178229_m;

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

      public void func_178204_a(IBlockAccess var1, Block var2, BlockPos var3, EnumFacing var4, float[] var5, BitSet var6) {
         BlockPos var7 = var6.get(0) ? var3.func_177972_a(var4) : var3;
         BlockModelRenderer.EnumNeighborInfo var8 = BlockModelRenderer.EnumNeighborInfo.func_178273_a(var4);
         BlockPos var9 = var7.func_177972_a(var8.field_178276_g[0]);
         BlockPos var10 = var7.func_177972_a(var8.field_178276_g[1]);
         BlockPos var11 = var7.func_177972_a(var8.field_178276_g[2]);
         BlockPos var12 = var7.func_177972_a(var8.field_178276_g[3]);
         int var13 = var2.func_176207_c(var1, var9);
         int var14 = var2.func_176207_c(var1, var10);
         int var15 = var2.func_176207_c(var1, var11);
         int var16 = var2.func_176207_c(var1, var12);
         float var17 = var1.func_180495_p(var9).func_177230_c().func_149685_I();
         float var18 = var1.func_180495_p(var10).func_177230_c().func_149685_I();
         float var19 = var1.func_180495_p(var11).func_177230_c().func_149685_I();
         float var20 = var1.func_180495_p(var12).func_177230_c().func_149685_I();
         boolean var21 = var1.func_180495_p(var9.func_177972_a(var4)).func_177230_c().func_149751_l();
         boolean var22 = var1.func_180495_p(var10.func_177972_a(var4)).func_177230_c().func_149751_l();
         boolean var23 = var1.func_180495_p(var11.func_177972_a(var4)).func_177230_c().func_149751_l();
         boolean var24 = var1.func_180495_p(var12.func_177972_a(var4)).func_177230_c().func_149751_l();
         float var25;
         int var29;
         BlockPos var33;
         if (!var23 && !var21) {
            var25 = var17;
            var29 = var13;
         } else {
            var33 = var9.func_177972_a(var8.field_178276_g[2]);
            var25 = var1.func_180495_p(var33).func_177230_c().func_149685_I();
            var29 = var2.func_176207_c(var1, var33);
         }

         float var26;
         int var30;
         if (!var24 && !var21) {
            var26 = var17;
            var30 = var13;
         } else {
            var33 = var9.func_177972_a(var8.field_178276_g[3]);
            var26 = var1.func_180495_p(var33).func_177230_c().func_149685_I();
            var30 = var2.func_176207_c(var1, var33);
         }

         float var27;
         int var31;
         if (!var23 && !var22) {
            var27 = var18;
            var31 = var14;
         } else {
            var33 = var10.func_177972_a(var8.field_178276_g[2]);
            var27 = var1.func_180495_p(var33).func_177230_c().func_149685_I();
            var31 = var2.func_176207_c(var1, var33);
         }

         float var28;
         int var32;
         if (!var24 && !var22) {
            var28 = var18;
            var32 = var14;
         } else {
            var33 = var10.func_177972_a(var8.field_178276_g[3]);
            var28 = var1.func_180495_p(var33).func_177230_c().func_149685_I();
            var32 = var2.func_176207_c(var1, var33);
         }

         int var60 = var2.func_176207_c(var1, var3);
         if (var6.get(0) || !var1.func_180495_p(var3.func_177972_a(var4)).func_177230_c().func_149662_c()) {
            var60 = var2.func_176207_c(var1, var3.func_177972_a(var4));
         }

         float var34 = var6.get(0) ? var1.func_180495_p(var7).func_177230_c().func_149685_I() : var1.func_180495_p(var3).func_177230_c().func_149685_I();
         BlockModelRenderer.VertexTranslations var35 = BlockModelRenderer.VertexTranslations.func_178184_a(var4);
         float var36;
         float var37;
         float var38;
         float var39;
         if (var6.get(1) && var8.field_178289_i) {
            var36 = (var20 + var17 + var26 + var34) * 0.25F;
            var37 = (var19 + var17 + var25 + var34) * 0.25F;
            var38 = (var19 + var18 + var27 + var34) * 0.25F;
            var39 = (var20 + var18 + var28 + var34) * 0.25F;
            float var40 = var5[var8.field_178286_j[0].field_178229_m] * var5[var8.field_178286_j[1].field_178229_m];
            float var41 = var5[var8.field_178286_j[2].field_178229_m] * var5[var8.field_178286_j[3].field_178229_m];
            float var42 = var5[var8.field_178286_j[4].field_178229_m] * var5[var8.field_178286_j[5].field_178229_m];
            float var43 = var5[var8.field_178286_j[6].field_178229_m] * var5[var8.field_178286_j[7].field_178229_m];
            float var44 = var5[var8.field_178287_k[0].field_178229_m] * var5[var8.field_178287_k[1].field_178229_m];
            float var45 = var5[var8.field_178287_k[2].field_178229_m] * var5[var8.field_178287_k[3].field_178229_m];
            float var46 = var5[var8.field_178287_k[4].field_178229_m] * var5[var8.field_178287_k[5].field_178229_m];
            float var47 = var5[var8.field_178287_k[6].field_178229_m] * var5[var8.field_178287_k[7].field_178229_m];
            float var48 = var5[var8.field_178284_l[0].field_178229_m] * var5[var8.field_178284_l[1].field_178229_m];
            float var49 = var5[var8.field_178284_l[2].field_178229_m] * var5[var8.field_178284_l[3].field_178229_m];
            float var50 = var5[var8.field_178284_l[4].field_178229_m] * var5[var8.field_178284_l[5].field_178229_m];
            float var51 = var5[var8.field_178284_l[6].field_178229_m] * var5[var8.field_178284_l[7].field_178229_m];
            float var52 = var5[var8.field_178285_m[0].field_178229_m] * var5[var8.field_178285_m[1].field_178229_m];
            float var53 = var5[var8.field_178285_m[2].field_178229_m] * var5[var8.field_178285_m[3].field_178229_m];
            float var54 = var5[var8.field_178285_m[4].field_178229_m] * var5[var8.field_178285_m[5].field_178229_m];
            float var55 = var5[var8.field_178285_m[6].field_178229_m] * var5[var8.field_178285_m[7].field_178229_m];
            this.field_178206_b[var35.field_178191_g] = var36 * var40 + var37 * var41 + var38 * var42 + var39 * var43;
            this.field_178206_b[var35.field_178200_h] = var36 * var44 + var37 * var45 + var38 * var46 + var39 * var47;
            this.field_178206_b[var35.field_178201_i] = var36 * var48 + var37 * var49 + var38 * var50 + var39 * var51;
            this.field_178206_b[var35.field_178198_j] = var36 * var52 + var37 * var53 + var38 * var54 + var39 * var55;
            int var56 = this.func_147778_a(var16, var13, var30, var60);
            int var57 = this.func_147778_a(var15, var13, var29, var60);
            int var58 = this.func_147778_a(var15, var14, var31, var60);
            int var59 = this.func_147778_a(var16, var14, var32, var60);
            this.field_178207_c[var35.field_178191_g] = this.func_178203_a(var56, var57, var58, var59, var40, var41, var42, var43);
            this.field_178207_c[var35.field_178200_h] = this.func_178203_a(var56, var57, var58, var59, var44, var45, var46, var47);
            this.field_178207_c[var35.field_178201_i] = this.func_178203_a(var56, var57, var58, var59, var48, var49, var50, var51);
            this.field_178207_c[var35.field_178198_j] = this.func_178203_a(var56, var57, var58, var59, var52, var53, var54, var55);
         } else {
            var36 = (var20 + var17 + var26 + var34) * 0.25F;
            var37 = (var19 + var17 + var25 + var34) * 0.25F;
            var38 = (var19 + var18 + var27 + var34) * 0.25F;
            var39 = (var20 + var18 + var28 + var34) * 0.25F;
            this.field_178207_c[var35.field_178191_g] = this.func_147778_a(var16, var13, var30, var60);
            this.field_178207_c[var35.field_178200_h] = this.func_147778_a(var15, var13, var29, var60);
            this.field_178207_c[var35.field_178201_i] = this.func_147778_a(var15, var14, var31, var60);
            this.field_178207_c[var35.field_178198_j] = this.func_147778_a(var16, var14, var32, var60);
            this.field_178206_b[var35.field_178191_g] = var36;
            this.field_178206_b[var35.field_178200_h] = var37;
            this.field_178206_b[var35.field_178201_i] = var38;
            this.field_178206_b[var35.field_178198_j] = var39;
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
      private static final BlockModelRenderer.VertexTranslations[] field_178199_k = new BlockModelRenderer.VertexTranslations[6];

      private VertexTranslations(int var3, int var4, int var5, int var6) {
         this.field_178191_g = var3;
         this.field_178200_h = var4;
         this.field_178201_i = var5;
         this.field_178198_j = var6;
      }

      public static BlockModelRenderer.VertexTranslations func_178184_a(EnumFacing var0) {
         return field_178199_k[var0.func_176745_a()];
      }

      static {
         field_178199_k[EnumFacing.DOWN.func_176745_a()] = DOWN;
         field_178199_k[EnumFacing.UP.func_176745_a()] = UP;
         field_178199_k[EnumFacing.NORTH.func_176745_a()] = NORTH;
         field_178199_k[EnumFacing.SOUTH.func_176745_a()] = SOUTH;
         field_178199_k[EnumFacing.WEST.func_176745_a()] = WEST;
         field_178199_k[EnumFacing.EAST.func_176745_a()] = EAST;
      }
   }
}
