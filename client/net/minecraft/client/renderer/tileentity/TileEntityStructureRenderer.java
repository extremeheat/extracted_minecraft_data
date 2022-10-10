package net.minecraft.client.renderer.tileentity;

import java.util.Iterator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.state.properties.StructureMode;
import net.minecraft.tileentity.TileEntityStructure;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileEntityStructureRenderer extends TileEntityRenderer<TileEntityStructure> {
   public TileEntityStructureRenderer() {
      super();
   }

   public void func_199341_a(TileEntityStructure var1, double var2, double var4, double var6, float var8, int var9) {
      if (Minecraft.func_71410_x().field_71439_g.func_195070_dx() || Minecraft.func_71410_x().field_71439_g.func_175149_v()) {
         super.func_199341_a(var1, var2, var4, var6, var8, var9);
         BlockPos var10 = var1.func_189711_e();
         BlockPos var11 = var1.func_189717_g();
         if (var11.func_177958_n() >= 1 && var11.func_177956_o() >= 1 && var11.func_177952_p() >= 1) {
            if (var1.func_189700_k() == StructureMode.SAVE || var1.func_189700_k() == StructureMode.LOAD) {
               double var12 = 0.01D;
               double var14 = (double)var10.func_177958_n();
               double var16 = (double)var10.func_177952_p();
               double var24 = var4 + (double)var10.func_177956_o() - 0.01D;
               double var30 = var24 + (double)var11.func_177956_o() + 0.02D;
               double var18;
               double var20;
               switch(var1.func_189716_h()) {
               case LEFT_RIGHT:
                  var18 = (double)var11.func_177958_n() + 0.02D;
                  var20 = -((double)var11.func_177952_p() + 0.02D);
                  break;
               case FRONT_BACK:
                  var18 = -((double)var11.func_177958_n() + 0.02D);
                  var20 = (double)var11.func_177952_p() + 0.02D;
                  break;
               default:
                  var18 = (double)var11.func_177958_n() + 0.02D;
                  var20 = (double)var11.func_177952_p() + 0.02D;
               }

               double var22;
               double var26;
               double var28;
               double var32;
               switch(var1.func_189726_i()) {
               case CLOCKWISE_90:
                  var22 = var2 + (var20 < 0.0D ? var14 - 0.01D : var14 + 1.0D + 0.01D);
                  var26 = var6 + (var18 < 0.0D ? var16 + 1.0D + 0.01D : var16 - 0.01D);
                  var28 = var22 - var20;
                  var32 = var26 + var18;
                  break;
               case CLOCKWISE_180:
                  var22 = var2 + (var18 < 0.0D ? var14 - 0.01D : var14 + 1.0D + 0.01D);
                  var26 = var6 + (var20 < 0.0D ? var16 - 0.01D : var16 + 1.0D + 0.01D);
                  var28 = var22 - var18;
                  var32 = var26 - var20;
                  break;
               case COUNTERCLOCKWISE_90:
                  var22 = var2 + (var20 < 0.0D ? var14 + 1.0D + 0.01D : var14 - 0.01D);
                  var26 = var6 + (var18 < 0.0D ? var16 - 0.01D : var16 + 1.0D + 0.01D);
                  var28 = var22 + var20;
                  var32 = var26 - var18;
                  break;
               default:
                  var22 = var2 + (var18 < 0.0D ? var14 + 1.0D + 0.01D : var14 - 0.01D);
                  var26 = var6 + (var20 < 0.0D ? var16 + 1.0D + 0.01D : var16 - 0.01D);
                  var28 = var22 + var18;
                  var32 = var26 + var20;
               }

               boolean var34 = true;
               boolean var35 = true;
               boolean var36 = true;
               Tessellator var37 = Tessellator.func_178181_a();
               BufferBuilder var38 = var37.func_178180_c();
               GlStateManager.func_179106_n();
               GlStateManager.func_179140_f();
               GlStateManager.func_179090_x();
               GlStateManager.func_179147_l();
               GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
               this.func_190053_a(true);
               if (var1.func_189700_k() == StructureMode.SAVE || var1.func_189721_I()) {
                  this.func_190055_a(var37, var38, var22, var24, var26, var28, var30, var32, 255, 223, 127);
               }

               if (var1.func_189700_k() == StructureMode.SAVE && var1.func_189707_H()) {
                  this.func_190054_a(var1, var2, var4, var6, var10, var37, var38, true);
                  this.func_190054_a(var1, var2, var4, var6, var10, var37, var38, false);
               }

               this.func_190053_a(false);
               GlStateManager.func_187441_d(1.0F);
               GlStateManager.func_179145_e();
               GlStateManager.func_179098_w();
               GlStateManager.func_179126_j();
               GlStateManager.func_179132_a(true);
               GlStateManager.func_179127_m();
            }
         }
      }
   }

   private void func_190054_a(TileEntityStructure var1, double var2, double var4, double var6, BlockPos var8, Tessellator var9, BufferBuilder var10, boolean var11) {
      GlStateManager.func_187441_d(var11 ? 3.0F : 1.0F);
      var10.func_181668_a(3, DefaultVertexFormats.field_181706_f);
      World var12 = var1.func_145831_w();
      BlockPos var13 = var1.func_174877_v();
      BlockPos var14 = var13.func_177971_a(var8);
      Iterator var15 = BlockPos.func_177980_a(var14, var14.func_177971_a(var1.func_189717_g()).func_177982_a(-1, -1, -1)).iterator();

      while(true) {
         BlockPos var16;
         boolean var18;
         boolean var19;
         do {
            if (!var15.hasNext()) {
               var9.func_78381_a();
               return;
            }

            var16 = (BlockPos)var15.next();
            IBlockState var17 = var12.func_180495_p(var16);
            var18 = var17.func_196958_f();
            var19 = var17.func_177230_c() == Blocks.field_189881_dj;
         } while(!var18 && !var19);

         float var20 = var18 ? 0.05F : 0.0F;
         double var21 = (double)((float)(var16.func_177958_n() - var13.func_177958_n()) + 0.45F) + var2 - (double)var20;
         double var23 = (double)((float)(var16.func_177956_o() - var13.func_177956_o()) + 0.45F) + var4 - (double)var20;
         double var25 = (double)((float)(var16.func_177952_p() - var13.func_177952_p()) + 0.45F) + var6 - (double)var20;
         double var27 = (double)((float)(var16.func_177958_n() - var13.func_177958_n()) + 0.55F) + var2 + (double)var20;
         double var29 = (double)((float)(var16.func_177956_o() - var13.func_177956_o()) + 0.55F) + var4 + (double)var20;
         double var31 = (double)((float)(var16.func_177952_p() - var13.func_177952_p()) + 0.55F) + var6 + (double)var20;
         if (var11) {
            WorldRenderer.func_189698_a(var10, var21, var23, var25, var27, var29, var31, 0.0F, 0.0F, 0.0F, 1.0F);
         } else if (var18) {
            WorldRenderer.func_189698_a(var10, var21, var23, var25, var27, var29, var31, 0.5F, 0.5F, 1.0F, 1.0F);
         } else {
            WorldRenderer.func_189698_a(var10, var21, var23, var25, var27, var29, var31, 1.0F, 0.25F, 0.25F, 1.0F);
         }
      }
   }

   private void func_190055_a(Tessellator var1, BufferBuilder var2, double var3, double var5, double var7, double var9, double var11, double var13, int var15, int var16, int var17) {
      GlStateManager.func_187441_d(2.0F);
      var2.func_181668_a(3, DefaultVertexFormats.field_181706_f);
      var2.func_181662_b(var3, var5, var7).func_181666_a((float)var16, (float)var16, (float)var16, 0.0F).func_181675_d();
      var2.func_181662_b(var3, var5, var7).func_181669_b(var16, var16, var16, var15).func_181675_d();
      var2.func_181662_b(var9, var5, var7).func_181669_b(var16, var17, var17, var15).func_181675_d();
      var2.func_181662_b(var9, var5, var13).func_181669_b(var16, var16, var16, var15).func_181675_d();
      var2.func_181662_b(var3, var5, var13).func_181669_b(var16, var16, var16, var15).func_181675_d();
      var2.func_181662_b(var3, var5, var7).func_181669_b(var17, var17, var16, var15).func_181675_d();
      var2.func_181662_b(var3, var11, var7).func_181669_b(var17, var16, var17, var15).func_181675_d();
      var2.func_181662_b(var9, var11, var7).func_181669_b(var16, var16, var16, var15).func_181675_d();
      var2.func_181662_b(var9, var11, var13).func_181669_b(var16, var16, var16, var15).func_181675_d();
      var2.func_181662_b(var3, var11, var13).func_181669_b(var16, var16, var16, var15).func_181675_d();
      var2.func_181662_b(var3, var11, var7).func_181669_b(var16, var16, var16, var15).func_181675_d();
      var2.func_181662_b(var3, var11, var13).func_181669_b(var16, var16, var16, var15).func_181675_d();
      var2.func_181662_b(var3, var5, var13).func_181669_b(var16, var16, var16, var15).func_181675_d();
      var2.func_181662_b(var9, var5, var13).func_181669_b(var16, var16, var16, var15).func_181675_d();
      var2.func_181662_b(var9, var11, var13).func_181669_b(var16, var16, var16, var15).func_181675_d();
      var2.func_181662_b(var9, var11, var7).func_181669_b(var16, var16, var16, var15).func_181675_d();
      var2.func_181662_b(var9, var5, var7).func_181669_b(var16, var16, var16, var15).func_181675_d();
      var2.func_181662_b(var9, var5, var7).func_181666_a((float)var16, (float)var16, (float)var16, 0.0F).func_181675_d();
      var1.func_78381_a();
      GlStateManager.func_187441_d(1.0F);
   }

   public boolean func_188185_a(TileEntityStructure var1) {
      return true;
   }
}
