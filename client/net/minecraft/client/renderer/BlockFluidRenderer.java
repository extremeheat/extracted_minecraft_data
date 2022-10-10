package net.minecraft.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.biome.BiomeColors;

public class BlockFluidRenderer {
   private final TextureAtlasSprite[] field_178272_a = new TextureAtlasSprite[2];
   private final TextureAtlasSprite[] field_178271_b = new TextureAtlasSprite[2];
   private TextureAtlasSprite field_187501_d;

   public BlockFluidRenderer() {
      super();
      this.func_178268_a();
   }

   protected void func_178268_a() {
      TextureMap var1 = Minecraft.func_71410_x().func_147117_R();
      this.field_178272_a[0] = Minecraft.func_71410_x().func_209506_al().func_174954_c().func_178125_b(Blocks.field_150353_l.func_176223_P()).func_177554_e();
      this.field_178272_a[1] = var1.func_195424_a(ModelBakery.field_207766_d);
      this.field_178271_b[0] = Minecraft.func_71410_x().func_209506_al().func_174954_c().func_178125_b(Blocks.field_150355_j.func_176223_P()).func_177554_e();
      this.field_178271_b[1] = var1.func_195424_a(ModelBakery.field_207768_f);
      this.field_187501_d = var1.func_195424_a(ModelBakery.field_207769_g);
   }

   private static boolean func_209557_a(IBlockReader var0, BlockPos var1, EnumFacing var2, IFluidState var3) {
      BlockPos var4 = var1.func_177972_a(var2);
      IFluidState var5 = var0.func_204610_c(var4);
      return var5.func_206886_c().func_207187_a(var3.func_206886_c());
   }

   private static boolean func_209556_a(IBlockReader var0, BlockPos var1, EnumFacing var2, float var3) {
      BlockPos var4 = var1.func_177972_a(var2);
      IBlockState var5 = var0.func_180495_p(var4);
      if (var5.func_200132_m()) {
         VoxelShape var6 = VoxelShapes.func_197873_a(0.0D, 0.0D, 0.0D, 1.0D, (double)var3, 1.0D);
         VoxelShape var7 = var5.func_196951_e(var0, var4);
         return VoxelShapes.func_197875_a(var6, var7, var2);
      } else {
         return false;
      }
   }

   public boolean func_205346_a(IWorldReader var1, BlockPos var2, BufferBuilder var3, IFluidState var4) {
      boolean var5 = var4.func_206884_a(FluidTags.field_206960_b);
      TextureAtlasSprite[] var6 = var5 ? this.field_178272_a : this.field_178271_b;
      int var7 = var5 ? 16777215 : BiomeColors.func_180288_c(var1, var2);
      float var8 = (float)(var7 >> 16 & 255) / 255.0F;
      float var9 = (float)(var7 >> 8 & 255) / 255.0F;
      float var10 = (float)(var7 & 255) / 255.0F;
      boolean var11 = !func_209557_a(var1, var2, EnumFacing.UP, var4);
      boolean var12 = !func_209557_a(var1, var2, EnumFacing.DOWN, var4) && !func_209556_a(var1, var2, EnumFacing.DOWN, 0.8888889F);
      boolean var13 = !func_209557_a(var1, var2, EnumFacing.NORTH, var4);
      boolean var14 = !func_209557_a(var1, var2, EnumFacing.SOUTH, var4);
      boolean var15 = !func_209557_a(var1, var2, EnumFacing.WEST, var4);
      boolean var16 = !func_209557_a(var1, var2, EnumFacing.EAST, var4);
      if (!var11 && !var12 && !var16 && !var15 && !var13 && !var14) {
         return false;
      } else {
         boolean var17 = false;
         float var18 = 0.5F;
         float var19 = 1.0F;
         float var20 = 0.8F;
         float var21 = 0.6F;
         float var22 = this.func_204504_a(var1, var2, var4.func_206886_c());
         float var23 = this.func_204504_a(var1, var2.func_177968_d(), var4.func_206886_c());
         float var24 = this.func_204504_a(var1, var2.func_177974_f().func_177968_d(), var4.func_206886_c());
         float var25 = this.func_204504_a(var1, var2.func_177974_f(), var4.func_206886_c());
         double var26 = (double)var2.func_177958_n();
         double var28 = (double)var2.func_177956_o();
         double var30 = (double)var2.func_177952_p();
         float var32 = 0.001F;
         float var33;
         float var34;
         float var35;
         float var36;
         float var40;
         if (var11 && !func_209556_a(var1, var2, EnumFacing.UP, Math.min(Math.min(var22, var23), Math.min(var24, var25)))) {
            var17 = true;
            var22 -= 0.001F;
            var23 -= 0.001F;
            var24 -= 0.001F;
            var25 -= 0.001F;
            Vec3d var41 = var4.func_206887_a(var1, var2);
            float var37;
            float var38;
            float var39;
            TextureAtlasSprite var42;
            float var45;
            float var46;
            if (var41.field_72450_a == 0.0D && var41.field_72449_c == 0.0D) {
               var42 = var6[0];
               var33 = var42.func_94214_a(0.0D);
               var37 = var42.func_94207_b(0.0D);
               var34 = var33;
               var38 = var42.func_94207_b(16.0D);
               var35 = var42.func_94214_a(16.0D);
               var39 = var38;
               var36 = var35;
               var40 = var37;
            } else {
               var42 = var6[1];
               float var43 = (float)MathHelper.func_181159_b(var41.field_72449_c, var41.field_72450_a) - 1.5707964F;
               float var44 = MathHelper.func_76126_a(var43) * 0.25F;
               var45 = MathHelper.func_76134_b(var43) * 0.25F;
               var46 = 8.0F;
               var33 = var42.func_94214_a((double)(8.0F + (-var45 - var44) * 16.0F));
               var37 = var42.func_94207_b((double)(8.0F + (-var45 + var44) * 16.0F));
               var34 = var42.func_94214_a((double)(8.0F + (-var45 + var44) * 16.0F));
               var38 = var42.func_94207_b((double)(8.0F + (var45 + var44) * 16.0F));
               var35 = var42.func_94214_a((double)(8.0F + (var45 + var44) * 16.0F));
               var39 = var42.func_94207_b((double)(8.0F + (var45 - var44) * 16.0F));
               var36 = var42.func_94214_a((double)(8.0F + (var45 - var44) * 16.0F));
               var40 = var42.func_94207_b((double)(8.0F + (-var45 - var44) * 16.0F));
            }

            int var68 = this.func_204835_a(var1, var2);
            int var70 = var68 >> 16 & '\uffff';
            int var72 = var68 & '\uffff';
            var45 = 1.0F * var8;
            var46 = 1.0F * var9;
            float var47 = 1.0F * var10;
            var3.func_181662_b(var26 + 0.0D, var28 + (double)var22, var30 + 0.0D).func_181666_a(var45, var46, var47, 1.0F).func_187315_a((double)var33, (double)var37).func_187314_a(var70, var72).func_181675_d();
            var3.func_181662_b(var26 + 0.0D, var28 + (double)var23, var30 + 1.0D).func_181666_a(var45, var46, var47, 1.0F).func_187315_a((double)var34, (double)var38).func_187314_a(var70, var72).func_181675_d();
            var3.func_181662_b(var26 + 1.0D, var28 + (double)var24, var30 + 1.0D).func_181666_a(var45, var46, var47, 1.0F).func_187315_a((double)var35, (double)var39).func_187314_a(var70, var72).func_181675_d();
            var3.func_181662_b(var26 + 1.0D, var28 + (double)var25, var30 + 0.0D).func_181666_a(var45, var46, var47, 1.0F).func_187315_a((double)var36, (double)var40).func_187314_a(var70, var72).func_181675_d();
            if (var4.func_205586_a(var1, var2.func_177984_a())) {
               var3.func_181662_b(var26 + 0.0D, var28 + (double)var22, var30 + 0.0D).func_181666_a(var45, var46, var47, 1.0F).func_187315_a((double)var33, (double)var37).func_187314_a(var70, var72).func_181675_d();
               var3.func_181662_b(var26 + 1.0D, var28 + (double)var25, var30 + 0.0D).func_181666_a(var45, var46, var47, 1.0F).func_187315_a((double)var36, (double)var40).func_187314_a(var70, var72).func_181675_d();
               var3.func_181662_b(var26 + 1.0D, var28 + (double)var24, var30 + 1.0D).func_181666_a(var45, var46, var47, 1.0F).func_187315_a((double)var35, (double)var39).func_187314_a(var70, var72).func_181675_d();
               var3.func_181662_b(var26 + 0.0D, var28 + (double)var23, var30 + 1.0D).func_181666_a(var45, var46, var47, 1.0F).func_187315_a((double)var34, (double)var38).func_187314_a(var70, var72).func_181675_d();
            }
         }

         if (var12) {
            var33 = var6[0].func_94209_e();
            var34 = var6[0].func_94212_f();
            var35 = var6[0].func_94206_g();
            var36 = var6[0].func_94210_h();
            int var62 = this.func_204835_a(var1, var2.func_177977_b());
            int var63 = var62 >> 16 & '\uffff';
            int var65 = var62 & '\uffff';
            var40 = 0.5F * var8;
            float var66 = 0.5F * var9;
            float var69 = 0.5F * var10;
            var3.func_181662_b(var26, var28, var30 + 1.0D).func_181666_a(var40, var66, var69, 1.0F).func_187315_a((double)var33, (double)var36).func_187314_a(var63, var65).func_181675_d();
            var3.func_181662_b(var26, var28, var30).func_181666_a(var40, var66, var69, 1.0F).func_187315_a((double)var33, (double)var35).func_187314_a(var63, var65).func_181675_d();
            var3.func_181662_b(var26 + 1.0D, var28, var30).func_181666_a(var40, var66, var69, 1.0F).func_187315_a((double)var34, (double)var35).func_187314_a(var63, var65).func_181675_d();
            var3.func_181662_b(var26 + 1.0D, var28, var30 + 1.0D).func_181666_a(var40, var66, var69, 1.0F).func_187315_a((double)var34, (double)var36).func_187314_a(var63, var65).func_181675_d();
            var17 = true;
         }

         for(int var60 = 0; var60 < 4; ++var60) {
            double var61;
            double var64;
            double var67;
            double var71;
            EnumFacing var73;
            boolean var74;
            if (var60 == 0) {
               var34 = var22;
               var35 = var25;
               var61 = var26;
               var67 = var26 + 1.0D;
               var64 = var30 + 0.0010000000474974513D;
               var71 = var30 + 0.0010000000474974513D;
               var73 = EnumFacing.NORTH;
               var74 = var13;
            } else if (var60 == 1) {
               var34 = var24;
               var35 = var23;
               var61 = var26 + 1.0D;
               var67 = var26;
               var64 = var30 + 1.0D - 0.0010000000474974513D;
               var71 = var30 + 1.0D - 0.0010000000474974513D;
               var73 = EnumFacing.SOUTH;
               var74 = var14;
            } else if (var60 == 2) {
               var34 = var23;
               var35 = var22;
               var61 = var26 + 0.0010000000474974513D;
               var67 = var26 + 0.0010000000474974513D;
               var64 = var30 + 1.0D;
               var71 = var30;
               var73 = EnumFacing.WEST;
               var74 = var15;
            } else {
               var34 = var25;
               var35 = var24;
               var61 = var26 + 1.0D - 0.0010000000474974513D;
               var67 = var26 + 1.0D - 0.0010000000474974513D;
               var64 = var30;
               var71 = var30 + 1.0D;
               var73 = EnumFacing.EAST;
               var74 = var16;
            }

            if (var74 && !func_209556_a(var1, var2, var73, Math.max(var34, var35))) {
               var17 = true;
               BlockPos var75 = var2.func_177972_a(var73);
               TextureAtlasSprite var76 = var6[1];
               if (!var5) {
                  Block var48 = var1.func_180495_p(var75).func_177230_c();
                  if (var48 == Blocks.field_150359_w || var48 instanceof BlockStainedGlass) {
                     var76 = this.field_187501_d;
                  }
               }

               float var77 = var76.func_94214_a(0.0D);
               float var49 = var76.func_94214_a(8.0D);
               float var50 = var76.func_94207_b((double)((1.0F - var34) * 16.0F * 0.5F));
               float var51 = var76.func_94207_b((double)((1.0F - var35) * 16.0F * 0.5F));
               float var52 = var76.func_94207_b(8.0D);
               int var53 = this.func_204835_a(var1, var75);
               int var54 = var53 >> 16 & '\uffff';
               int var55 = var53 & '\uffff';
               float var56 = var60 < 2 ? 0.8F : 0.6F;
               float var57 = 1.0F * var56 * var8;
               float var58 = 1.0F * var56 * var9;
               float var59 = 1.0F * var56 * var10;
               var3.func_181662_b(var61, var28 + (double)var34, var64).func_181666_a(var57, var58, var59, 1.0F).func_187315_a((double)var77, (double)var50).func_187314_a(var54, var55).func_181675_d();
               var3.func_181662_b(var67, var28 + (double)var35, var71).func_181666_a(var57, var58, var59, 1.0F).func_187315_a((double)var49, (double)var51).func_187314_a(var54, var55).func_181675_d();
               var3.func_181662_b(var67, var28 + 0.0D, var71).func_181666_a(var57, var58, var59, 1.0F).func_187315_a((double)var49, (double)var52).func_187314_a(var54, var55).func_181675_d();
               var3.func_181662_b(var61, var28 + 0.0D, var64).func_181666_a(var57, var58, var59, 1.0F).func_187315_a((double)var77, (double)var52).func_187314_a(var54, var55).func_181675_d();
               if (var76 != this.field_187501_d) {
                  var3.func_181662_b(var61, var28 + 0.0D, var64).func_181666_a(var57, var58, var59, 1.0F).func_187315_a((double)var77, (double)var52).func_187314_a(var54, var55).func_181675_d();
                  var3.func_181662_b(var67, var28 + 0.0D, var71).func_181666_a(var57, var58, var59, 1.0F).func_187315_a((double)var49, (double)var52).func_187314_a(var54, var55).func_181675_d();
                  var3.func_181662_b(var67, var28 + (double)var35, var71).func_181666_a(var57, var58, var59, 1.0F).func_187315_a((double)var49, (double)var51).func_187314_a(var54, var55).func_181675_d();
                  var3.func_181662_b(var61, var28 + (double)var34, var64).func_181666_a(var57, var58, var59, 1.0F).func_187315_a((double)var77, (double)var50).func_187314_a(var54, var55).func_181675_d();
               }
            }
         }

         return var17;
      }
   }

   private int func_204835_a(IWorldReader var1, BlockPos var2) {
      int var3 = var1.func_175626_b(var2, 0);
      int var4 = var1.func_175626_b(var2.func_177984_a(), 0);
      int var5 = var3 & 255;
      int var6 = var4 & 255;
      int var7 = var3 >> 16 & 255;
      int var8 = var4 >> 16 & 255;
      return (var5 > var6 ? var5 : var6) | (var7 > var8 ? var7 : var8) << 16;
   }

   private float func_204504_a(IWorldReaderBase var1, BlockPos var2, Fluid var3) {
      int var4 = 0;
      float var5 = 0.0F;

      for(int var6 = 0; var6 < 4; ++var6) {
         BlockPos var7 = var2.func_177982_a(-(var6 & 1), 0, -(var6 >> 1 & 1));
         if (var1.func_204610_c(var7.func_177984_a()).func_206886_c().func_207187_a(var3)) {
            return 1.0F;
         }

         IFluidState var8 = var1.func_204610_c(var7);
         if (var8.func_206886_c().func_207187_a(var3)) {
            if (var8.func_206885_f() >= 0.8F) {
               var5 += var8.func_206885_f() * 10.0F;
               var4 += 10;
            } else {
               var5 += var8.func_206885_f();
               ++var4;
            }
         } else if (!var1.func_180495_p(var7).func_185904_a().func_76220_a()) {
            ++var4;
         }
      }

      return var5 / (float)var4;
   }
}
