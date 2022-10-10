package net.minecraft.client.renderer.debug;

import java.util.Iterator;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.World;

public class DebugRendererSolidFace implements DebugRenderer.IDebugRenderer {
   private final Minecraft field_193851_a;

   public DebugRendererSolidFace(Minecraft var1) {
      super();
      this.field_193851_a = var1;
   }

   public void func_190060_a(float var1, long var2) {
      EntityPlayerSP var4 = this.field_193851_a.field_71439_g;
      double var5 = var4.field_70142_S + (var4.field_70165_t - var4.field_70142_S) * (double)var1;
      double var7 = var4.field_70137_T + (var4.field_70163_u - var4.field_70137_T) * (double)var1;
      double var9 = var4.field_70136_U + (var4.field_70161_v - var4.field_70136_U) * (double)var1;
      World var11 = this.field_193851_a.field_71439_g.field_70170_p;
      Iterable var12 = BlockPos.func_191532_a(MathHelper.func_76128_c(var4.field_70165_t - 6.0D), MathHelper.func_76128_c(var4.field_70163_u - 6.0D), MathHelper.func_76128_c(var4.field_70161_v - 6.0D), MathHelper.func_76128_c(var4.field_70165_t + 6.0D), MathHelper.func_76128_c(var4.field_70163_u + 6.0D), MathHelper.func_76128_c(var4.field_70161_v + 6.0D));
      GlStateManager.func_179147_l();
      GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.func_187441_d(2.0F);
      GlStateManager.func_179090_x();
      GlStateManager.func_179132_a(false);
      Iterator var13 = var12.iterator();

      while(true) {
         BlockPos var14;
         IBlockState var15;
         do {
            if (!var13.hasNext()) {
               GlStateManager.func_179132_a(true);
               GlStateManager.func_179098_w();
               GlStateManager.func_179084_k();
               return;
            }

            var14 = (BlockPos)var13.next();
            var15 = var11.func_180495_p(var14);
         } while(var15.func_177230_c() == Blocks.field_150350_a);

         VoxelShape var16 = var15.func_196954_c(var11, var14);
         Iterator var17 = var16.func_197756_d().iterator();

         while(var17.hasNext()) {
            AxisAlignedBB var18 = (AxisAlignedBB)var17.next();
            AxisAlignedBB var19 = var18.func_186670_a(var14).func_186662_g(0.002D).func_72317_d(-var5, -var7, -var9);
            double var20 = var19.field_72340_a;
            double var22 = var19.field_72338_b;
            double var24 = var19.field_72339_c;
            double var26 = var19.field_72336_d;
            double var28 = var19.field_72337_e;
            double var30 = var19.field_72334_f;
            float var32 = 1.0F;
            float var33 = 0.0F;
            float var34 = 0.0F;
            float var35 = 0.5F;
            Tessellator var36;
            BufferBuilder var37;
            if (var15.func_193401_d(var11, var14, EnumFacing.WEST) == BlockFaceShape.SOLID) {
               var36 = Tessellator.func_178181_a();
               var37 = var36.func_178180_c();
               var37.func_181668_a(5, DefaultVertexFormats.field_181706_f);
               var37.func_181662_b(var20, var22, var24).func_181666_a(1.0F, 0.0F, 0.0F, 0.5F).func_181675_d();
               var37.func_181662_b(var20, var22, var30).func_181666_a(1.0F, 0.0F, 0.0F, 0.5F).func_181675_d();
               var37.func_181662_b(var20, var28, var24).func_181666_a(1.0F, 0.0F, 0.0F, 0.5F).func_181675_d();
               var37.func_181662_b(var20, var28, var30).func_181666_a(1.0F, 0.0F, 0.0F, 0.5F).func_181675_d();
               var36.func_78381_a();
            }

            if (var15.func_193401_d(var11, var14, EnumFacing.SOUTH) == BlockFaceShape.SOLID) {
               var36 = Tessellator.func_178181_a();
               var37 = var36.func_178180_c();
               var37.func_181668_a(5, DefaultVertexFormats.field_181706_f);
               var37.func_181662_b(var20, var28, var30).func_181666_a(1.0F, 0.0F, 0.0F, 0.5F).func_181675_d();
               var37.func_181662_b(var20, var22, var30).func_181666_a(1.0F, 0.0F, 0.0F, 0.5F).func_181675_d();
               var37.func_181662_b(var26, var28, var30).func_181666_a(1.0F, 0.0F, 0.0F, 0.5F).func_181675_d();
               var37.func_181662_b(var26, var22, var30).func_181666_a(1.0F, 0.0F, 0.0F, 0.5F).func_181675_d();
               var36.func_78381_a();
            }

            if (var15.func_193401_d(var11, var14, EnumFacing.EAST) == BlockFaceShape.SOLID) {
               var36 = Tessellator.func_178181_a();
               var37 = var36.func_178180_c();
               var37.func_181668_a(5, DefaultVertexFormats.field_181706_f);
               var37.func_181662_b(var26, var22, var30).func_181666_a(1.0F, 0.0F, 0.0F, 0.5F).func_181675_d();
               var37.func_181662_b(var26, var22, var24).func_181666_a(1.0F, 0.0F, 0.0F, 0.5F).func_181675_d();
               var37.func_181662_b(var26, var28, var30).func_181666_a(1.0F, 0.0F, 0.0F, 0.5F).func_181675_d();
               var37.func_181662_b(var26, var28, var24).func_181666_a(1.0F, 0.0F, 0.0F, 0.5F).func_181675_d();
               var36.func_78381_a();
            }

            if (var15.func_193401_d(var11, var14, EnumFacing.NORTH) == BlockFaceShape.SOLID) {
               var36 = Tessellator.func_178181_a();
               var37 = var36.func_178180_c();
               var37.func_181668_a(5, DefaultVertexFormats.field_181706_f);
               var37.func_181662_b(var26, var28, var24).func_181666_a(1.0F, 0.0F, 0.0F, 0.5F).func_181675_d();
               var37.func_181662_b(var26, var22, var24).func_181666_a(1.0F, 0.0F, 0.0F, 0.5F).func_181675_d();
               var37.func_181662_b(var20, var28, var24).func_181666_a(1.0F, 0.0F, 0.0F, 0.5F).func_181675_d();
               var37.func_181662_b(var20, var22, var24).func_181666_a(1.0F, 0.0F, 0.0F, 0.5F).func_181675_d();
               var36.func_78381_a();
            }

            if (var15.func_193401_d(var11, var14, EnumFacing.DOWN) == BlockFaceShape.SOLID) {
               var36 = Tessellator.func_178181_a();
               var37 = var36.func_178180_c();
               var37.func_181668_a(5, DefaultVertexFormats.field_181706_f);
               var37.func_181662_b(var20, var22, var24).func_181666_a(1.0F, 0.0F, 0.0F, 0.5F).func_181675_d();
               var37.func_181662_b(var26, var22, var24).func_181666_a(1.0F, 0.0F, 0.0F, 0.5F).func_181675_d();
               var37.func_181662_b(var20, var22, var30).func_181666_a(1.0F, 0.0F, 0.0F, 0.5F).func_181675_d();
               var37.func_181662_b(var26, var22, var30).func_181666_a(1.0F, 0.0F, 0.0F, 0.5F).func_181675_d();
               var36.func_78381_a();
            }

            if (var15.func_193401_d(var11, var14, EnumFacing.UP) == BlockFaceShape.SOLID) {
               var36 = Tessellator.func_178181_a();
               var37 = var36.func_178180_c();
               var37.func_181668_a(5, DefaultVertexFormats.field_181706_f);
               var37.func_181662_b(var20, var28, var24).func_181666_a(1.0F, 0.0F, 0.0F, 0.5F).func_181675_d();
               var37.func_181662_b(var20, var28, var30).func_181666_a(1.0F, 0.0F, 0.0F, 0.5F).func_181675_d();
               var37.func_181662_b(var26, var28, var24).func_181666_a(1.0F, 0.0F, 0.0F, 0.5F).func_181675_d();
               var37.func_181662_b(var26, var28, var30).func_181666_a(1.0F, 0.0F, 0.0F, 0.5F).func_181675_d();
               var36.func_78381_a();
            }
         }
      }
   }
}
