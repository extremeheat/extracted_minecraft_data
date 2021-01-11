package net.minecraft.client.renderer;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;

public class BlockFluidRenderer {
   private TextureAtlasSprite[] field_178272_a = new TextureAtlasSprite[2];
   private TextureAtlasSprite[] field_178271_b = new TextureAtlasSprite[2];

   public BlockFluidRenderer() {
      super();
      this.func_178268_a();
   }

   protected void func_178268_a() {
      TextureMap var1 = Minecraft.func_71410_x().func_147117_R();
      this.field_178272_a[0] = var1.func_110572_b("minecraft:blocks/lava_still");
      this.field_178272_a[1] = var1.func_110572_b("minecraft:blocks/lava_flow");
      this.field_178271_b[0] = var1.func_110572_b("minecraft:blocks/water_still");
      this.field_178271_b[1] = var1.func_110572_b("minecraft:blocks/water_flow");
   }

   public boolean func_178270_a(IBlockAccess var1, IBlockState var2, BlockPos var3, WorldRenderer var4) {
      BlockLiquid var5 = (BlockLiquid)var2.func_177230_c();
      var5.func_180654_a(var1, var3);
      TextureAtlasSprite[] var6 = var5.func_149688_o() == Material.field_151587_i ? this.field_178272_a : this.field_178271_b;
      int var7 = var5.func_176202_d(var1, var3);
      float var8 = (float)(var7 >> 16 & 255) / 255.0F;
      float var9 = (float)(var7 >> 8 & 255) / 255.0F;
      float var10 = (float)(var7 & 255) / 255.0F;
      boolean var11 = var5.func_176225_a(var1, var3.func_177984_a(), EnumFacing.UP);
      boolean var12 = var5.func_176225_a(var1, var3.func_177977_b(), EnumFacing.DOWN);
      boolean[] var13 = new boolean[]{var5.func_176225_a(var1, var3.func_177978_c(), EnumFacing.NORTH), var5.func_176225_a(var1, var3.func_177968_d(), EnumFacing.SOUTH), var5.func_176225_a(var1, var3.func_177976_e(), EnumFacing.WEST), var5.func_176225_a(var1, var3.func_177974_f(), EnumFacing.EAST)};
      if (!var11 && !var12 && !var13[0] && !var13[1] && !var13[2] && !var13[3]) {
         return false;
      } else {
         boolean var14 = false;
         float var15 = 0.5F;
         float var16 = 1.0F;
         float var17 = 0.8F;
         float var18 = 0.6F;
         Material var19 = var5.func_149688_o();
         float var20 = this.func_178269_a(var1, var3, var19);
         float var21 = this.func_178269_a(var1, var3.func_177968_d(), var19);
         float var22 = this.func_178269_a(var1, var3.func_177974_f().func_177968_d(), var19);
         float var23 = this.func_178269_a(var1, var3.func_177974_f(), var19);
         double var24 = (double)var3.func_177958_n();
         double var26 = (double)var3.func_177956_o();
         double var28 = (double)var3.func_177952_p();
         float var30 = 0.001F;
         TextureAtlasSprite var31;
         float var32;
         float var33;
         float var34;
         float var35;
         float var36;
         float var37;
         float var46;
         if (var11) {
            var14 = true;
            var31 = var6[0];
            var32 = (float)BlockLiquid.func_180689_a(var1, var3, var19);
            if (var32 > -999.0F) {
               var31 = var6[1];
            }

            var20 -= var30;
            var21 -= var30;
            var22 -= var30;
            var23 -= var30;
            float var38;
            float var39;
            float var40;
            if (var32 < -999.0F) {
               var33 = var31.func_94214_a(0.0D);
               var37 = var31.func_94207_b(0.0D);
               var34 = var33;
               var38 = var31.func_94207_b(16.0D);
               var35 = var31.func_94214_a(16.0D);
               var39 = var38;
               var36 = var35;
               var40 = var37;
            } else {
               float var41 = MathHelper.func_76126_a(var32) * 0.25F;
               float var42 = MathHelper.func_76134_b(var32) * 0.25F;
               float var43 = 8.0F;
               var33 = var31.func_94214_a((double)(8.0F + (-var42 - var41) * 16.0F));
               var37 = var31.func_94207_b((double)(8.0F + (-var42 + var41) * 16.0F));
               var34 = var31.func_94214_a((double)(8.0F + (-var42 + var41) * 16.0F));
               var38 = var31.func_94207_b((double)(8.0F + (var42 + var41) * 16.0F));
               var35 = var31.func_94214_a((double)(8.0F + (var42 + var41) * 16.0F));
               var39 = var31.func_94207_b((double)(8.0F + (var42 - var41) * 16.0F));
               var36 = var31.func_94214_a((double)(8.0F + (var42 - var41) * 16.0F));
               var40 = var31.func_94207_b((double)(8.0F + (-var42 - var41) * 16.0F));
            }

            int var67 = var5.func_176207_c(var1, var3);
            int var68 = var67 >> 16 & '\uffff';
            int var70 = var67 & '\uffff';
            float var44 = var16 * var8;
            float var45 = var16 * var9;
            var46 = var16 * var10;
            var4.func_181662_b(var24 + 0.0D, var26 + (double)var20, var28 + 0.0D).func_181666_a(var44, var45, var46, 1.0F).func_181673_a((double)var33, (double)var37).func_181671_a(var68, var70).func_181675_d();
            var4.func_181662_b(var24 + 0.0D, var26 + (double)var21, var28 + 1.0D).func_181666_a(var44, var45, var46, 1.0F).func_181673_a((double)var34, (double)var38).func_181671_a(var68, var70).func_181675_d();
            var4.func_181662_b(var24 + 1.0D, var26 + (double)var22, var28 + 1.0D).func_181666_a(var44, var45, var46, 1.0F).func_181673_a((double)var35, (double)var39).func_181671_a(var68, var70).func_181675_d();
            var4.func_181662_b(var24 + 1.0D, var26 + (double)var23, var28 + 0.0D).func_181666_a(var44, var45, var46, 1.0F).func_181673_a((double)var36, (double)var40).func_181671_a(var68, var70).func_181675_d();
            if (var5.func_176364_g(var1, var3.func_177984_a())) {
               var4.func_181662_b(var24 + 0.0D, var26 + (double)var20, var28 + 0.0D).func_181666_a(var44, var45, var46, 1.0F).func_181673_a((double)var33, (double)var37).func_181671_a(var68, var70).func_181675_d();
               var4.func_181662_b(var24 + 1.0D, var26 + (double)var23, var28 + 0.0D).func_181666_a(var44, var45, var46, 1.0F).func_181673_a((double)var36, (double)var40).func_181671_a(var68, var70).func_181675_d();
               var4.func_181662_b(var24 + 1.0D, var26 + (double)var22, var28 + 1.0D).func_181666_a(var44, var45, var46, 1.0F).func_181673_a((double)var35, (double)var39).func_181671_a(var68, var70).func_181675_d();
               var4.func_181662_b(var24 + 0.0D, var26 + (double)var21, var28 + 1.0D).func_181666_a(var44, var45, var46, 1.0F).func_181673_a((double)var34, (double)var38).func_181671_a(var68, var70).func_181675_d();
            }
         }

         if (var12) {
            var32 = var6[0].func_94209_e();
            var33 = var6[0].func_94212_f();
            var34 = var6[0].func_94206_g();
            var35 = var6[0].func_94210_h();
            int var62 = var5.func_176207_c(var1, var3.func_177977_b());
            int var63 = var62 >> 16 & '\uffff';
            int var64 = var62 & '\uffff';
            var4.func_181662_b(var24, var26, var28 + 1.0D).func_181666_a(var15, var15, var15, 1.0F).func_181673_a((double)var32, (double)var35).func_181671_a(var63, var64).func_181675_d();
            var4.func_181662_b(var24, var26, var28).func_181666_a(var15, var15, var15, 1.0F).func_181673_a((double)var32, (double)var34).func_181671_a(var63, var64).func_181675_d();
            var4.func_181662_b(var24 + 1.0D, var26, var28).func_181666_a(var15, var15, var15, 1.0F).func_181673_a((double)var33, (double)var34).func_181671_a(var63, var64).func_181675_d();
            var4.func_181662_b(var24 + 1.0D, var26, var28 + 1.0D).func_181666_a(var15, var15, var15, 1.0F).func_181673_a((double)var33, (double)var35).func_181671_a(var63, var64).func_181675_d();
            var14 = true;
         }

         for(int var58 = 0; var58 < 4; ++var58) {
            int var59 = 0;
            int var60 = 0;
            if (var58 == 0) {
               --var60;
            }

            if (var58 == 1) {
               ++var60;
            }

            if (var58 == 2) {
               --var59;
            }

            if (var58 == 3) {
               ++var59;
            }

            BlockPos var61 = var3.func_177982_a(var59, 0, var60);
            var31 = var6[1];
            if (var13[var58]) {
               double var65;
               double var66;
               double var69;
               double var71;
               if (var58 == 0) {
                  var36 = var20;
                  var37 = var23;
                  var65 = var24;
                  var69 = var24 + 1.0D;
                  var66 = var28 + (double)var30;
                  var71 = var28 + (double)var30;
               } else if (var58 == 1) {
                  var36 = var22;
                  var37 = var21;
                  var65 = var24 + 1.0D;
                  var69 = var24;
                  var66 = var28 + 1.0D - (double)var30;
                  var71 = var28 + 1.0D - (double)var30;
               } else if (var58 == 2) {
                  var36 = var21;
                  var37 = var20;
                  var65 = var24 + (double)var30;
                  var69 = var24 + (double)var30;
                  var66 = var28 + 1.0D;
                  var71 = var28;
               } else {
                  var36 = var23;
                  var37 = var22;
                  var65 = var24 + 1.0D - (double)var30;
                  var69 = var24 + 1.0D - (double)var30;
                  var66 = var28;
                  var71 = var28 + 1.0D;
               }

               var14 = true;
               var46 = var31.func_94214_a(0.0D);
               float var47 = var31.func_94214_a(8.0D);
               float var48 = var31.func_94207_b((double)((1.0F - var36) * 16.0F * 0.5F));
               float var49 = var31.func_94207_b((double)((1.0F - var37) * 16.0F * 0.5F));
               float var50 = var31.func_94207_b(8.0D);
               int var51 = var5.func_176207_c(var1, var61);
               int var52 = var51 >> 16 & '\uffff';
               int var53 = var51 & '\uffff';
               float var54 = var58 < 2 ? var17 : var18;
               float var55 = var16 * var54 * var8;
               float var56 = var16 * var54 * var9;
               float var57 = var16 * var54 * var10;
               var4.func_181662_b(var65, var26 + (double)var36, var66).func_181666_a(var55, var56, var57, 1.0F).func_181673_a((double)var46, (double)var48).func_181671_a(var52, var53).func_181675_d();
               var4.func_181662_b(var69, var26 + (double)var37, var71).func_181666_a(var55, var56, var57, 1.0F).func_181673_a((double)var47, (double)var49).func_181671_a(var52, var53).func_181675_d();
               var4.func_181662_b(var69, var26 + 0.0D, var71).func_181666_a(var55, var56, var57, 1.0F).func_181673_a((double)var47, (double)var50).func_181671_a(var52, var53).func_181675_d();
               var4.func_181662_b(var65, var26 + 0.0D, var66).func_181666_a(var55, var56, var57, 1.0F).func_181673_a((double)var46, (double)var50).func_181671_a(var52, var53).func_181675_d();
               var4.func_181662_b(var65, var26 + 0.0D, var66).func_181666_a(var55, var56, var57, 1.0F).func_181673_a((double)var46, (double)var50).func_181671_a(var52, var53).func_181675_d();
               var4.func_181662_b(var69, var26 + 0.0D, var71).func_181666_a(var55, var56, var57, 1.0F).func_181673_a((double)var47, (double)var50).func_181671_a(var52, var53).func_181675_d();
               var4.func_181662_b(var69, var26 + (double)var37, var71).func_181666_a(var55, var56, var57, 1.0F).func_181673_a((double)var47, (double)var49).func_181671_a(var52, var53).func_181675_d();
               var4.func_181662_b(var65, var26 + (double)var36, var66).func_181666_a(var55, var56, var57, 1.0F).func_181673_a((double)var46, (double)var48).func_181671_a(var52, var53).func_181675_d();
            }
         }

         return var14;
      }
   }

   private float func_178269_a(IBlockAccess var1, BlockPos var2, Material var3) {
      int var4 = 0;
      float var5 = 0.0F;

      for(int var6 = 0; var6 < 4; ++var6) {
         BlockPos var7 = var2.func_177982_a(-(var6 & 1), 0, -(var6 >> 1 & 1));
         if (var1.func_180495_p(var7.func_177984_a()).func_177230_c().func_149688_o() == var3) {
            return 1.0F;
         }

         IBlockState var8 = var1.func_180495_p(var7);
         Material var9 = var8.func_177230_c().func_149688_o();
         if (var9 != var3) {
            if (!var9.func_76220_a()) {
               ++var5;
               ++var4;
            }
         } else {
            int var10 = (Integer)var8.func_177229_b(BlockLiquid.field_176367_b);
            if (var10 >= 8 || var10 == 0) {
               var5 += BlockLiquid.func_149801_b(var10) * 10.0F;
               var4 += 10;
            }

            var5 += BlockLiquid.func_149801_b(var10);
            ++var4;
         }
      }

      return 1.0F - var5 / (float)var4;
   }
}
