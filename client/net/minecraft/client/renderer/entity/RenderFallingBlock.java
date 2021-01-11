package net.minecraft.client.renderer.entity;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class RenderFallingBlock extends Render<EntityFallingBlock> {
   public RenderFallingBlock(RenderManager var1) {
      super(var1);
      this.field_76989_e = 0.5F;
   }

   public void func_76986_a(EntityFallingBlock var1, double var2, double var4, double var6, float var8, float var9) {
      if (var1.func_175131_l() != null) {
         this.func_110776_a(TextureMap.field_110575_b);
         IBlockState var10 = var1.func_175131_l();
         Block var11 = var10.func_177230_c();
         BlockPos var12 = new BlockPos(var1);
         World var13 = var1.func_145807_e();
         if (var10 != var13.func_180495_p(var12) && var11.func_149645_b() != -1) {
            if (var11.func_149645_b() == 3) {
               GlStateManager.func_179094_E();
               GlStateManager.func_179109_b((float)var2, (float)var4, (float)var6);
               GlStateManager.func_179140_f();
               Tessellator var14 = Tessellator.func_178181_a();
               WorldRenderer var15 = var14.func_178180_c();
               var15.func_181668_a(7, DefaultVertexFormats.field_176600_a);
               int var16 = var12.func_177958_n();
               int var17 = var12.func_177956_o();
               int var18 = var12.func_177952_p();
               var15.func_178969_c((double)((float)(-var16) - 0.5F), (double)(-var17), (double)((float)(-var18) - 0.5F));
               BlockRendererDispatcher var19 = Minecraft.func_71410_x().func_175602_ab();
               IBakedModel var20 = var19.func_175022_a(var10, var13, (BlockPos)null);
               var19.func_175019_b().func_178267_a(var13, var20, var10, var12, var15, false);
               var15.func_178969_c(0.0D, 0.0D, 0.0D);
               var14.func_78381_a();
               GlStateManager.func_179145_e();
               GlStateManager.func_179121_F();
               super.func_76986_a(var1, var2, var4, var6, var8, var9);
            }
         }
      }
   }

   protected ResourceLocation func_110775_a(EntityFallingBlock var1) {
      return TextureMap.field_110575_b;
   }
}
