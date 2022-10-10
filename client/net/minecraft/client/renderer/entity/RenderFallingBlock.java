package net.minecraft.client.renderer.entity;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RenderFallingBlock extends Render<EntityFallingBlock> {
   public RenderFallingBlock(RenderManager var1) {
      super(var1);
      this.field_76989_e = 0.5F;
   }

   public void func_76986_a(EntityFallingBlock var1, double var2, double var4, double var6, float var8, float var9) {
      IBlockState var10 = var1.func_195054_l();
      if (var10.func_185901_i() == EnumBlockRenderType.MODEL) {
         World var11 = var1.func_145807_e();
         if (var10 != var11.func_180495_p(new BlockPos(var1)) && var10.func_185901_i() != EnumBlockRenderType.INVISIBLE) {
            this.func_110776_a(TextureMap.field_110575_b);
            GlStateManager.func_179094_E();
            GlStateManager.func_179140_f();
            Tessellator var12 = Tessellator.func_178181_a();
            BufferBuilder var13 = var12.func_178180_c();
            if (this.field_188301_f) {
               GlStateManager.func_179142_g();
               GlStateManager.func_187431_e(this.func_188298_c(var1));
            }

            var13.func_181668_a(7, DefaultVertexFormats.field_176600_a);
            BlockPos var14 = new BlockPos(var1.field_70165_t, var1.func_174813_aQ().field_72337_e, var1.field_70161_v);
            GlStateManager.func_179109_b((float)(var2 - (double)var14.func_177958_n() - 0.5D), (float)(var4 - (double)var14.func_177956_o()), (float)(var6 - (double)var14.func_177952_p() - 0.5D));
            BlockRendererDispatcher var15 = Minecraft.func_71410_x().func_175602_ab();
            var15.func_175019_b().func_199324_a(var11, var15.func_184389_a(var10), var10, var14, var13, false, new Random(), var10.func_209533_a(var1.func_184531_j()));
            var12.func_78381_a();
            if (this.field_188301_f) {
               GlStateManager.func_187417_n();
               GlStateManager.func_179119_h();
            }

            GlStateManager.func_179145_e();
            GlStateManager.func_179121_F();
            super.func_76986_a(var1, var2, var4, var6, var8, var9);
         }
      }
   }

   protected ResourceLocation func_110775_a(EntityFallingBlock var1) {
      return TextureMap.field_110575_b;
   }
}
