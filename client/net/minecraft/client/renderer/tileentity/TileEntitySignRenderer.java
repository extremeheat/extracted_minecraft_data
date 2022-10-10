package net.minecraft.client.renderer.tileentity;

import java.util.List;
import net.minecraft.block.BlockStandingSign;
import net.minecraft.block.BlockWallSign;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelSign;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class TileEntitySignRenderer extends TileEntityRenderer<TileEntitySign> {
   private static final ResourceLocation field_147513_b = new ResourceLocation("textures/entity/sign.png");
   private final ModelSign field_147514_c = new ModelSign();

   public TileEntitySignRenderer() {
      super();
   }

   public void func_199341_a(TileEntitySign var1, double var2, double var4, double var6, float var8, int var9) {
      IBlockState var10 = var1.func_195044_w();
      GlStateManager.func_179094_E();
      float var11 = 0.6666667F;
      if (var10.func_177230_c() == Blocks.field_196649_cc) {
         GlStateManager.func_179109_b((float)var2 + 0.5F, (float)var4 + 0.5F, (float)var6 + 0.5F);
         GlStateManager.func_179114_b(-((float)((Integer)var10.func_177229_b(BlockStandingSign.field_176413_a) * 360) / 16.0F), 0.0F, 1.0F, 0.0F);
         this.field_147514_c.func_205064_b().field_78806_j = true;
      } else {
         GlStateManager.func_179109_b((float)var2 + 0.5F, (float)var4 + 0.5F, (float)var6 + 0.5F);
         GlStateManager.func_179114_b(-((EnumFacing)var10.func_177229_b(BlockWallSign.field_176412_a)).func_185119_l(), 0.0F, 1.0F, 0.0F);
         GlStateManager.func_179109_b(0.0F, -0.3125F, -0.4375F);
         this.field_147514_c.func_205064_b().field_78806_j = false;
      }

      if (var9 >= 0) {
         this.func_147499_a(field_178460_a[var9]);
         GlStateManager.func_179128_n(5890);
         GlStateManager.func_179094_E();
         GlStateManager.func_179152_a(4.0F, 2.0F, 1.0F);
         GlStateManager.func_179109_b(0.0625F, 0.0625F, 0.0625F);
         GlStateManager.func_179128_n(5888);
      } else {
         this.func_147499_a(field_147513_b);
      }

      GlStateManager.func_179091_B();
      GlStateManager.func_179094_E();
      GlStateManager.func_179152_a(0.6666667F, -0.6666667F, -0.6666667F);
      this.field_147514_c.func_78164_a();
      GlStateManager.func_179121_F();
      FontRenderer var12 = this.func_147498_b();
      float var13 = 0.010416667F;
      GlStateManager.func_179109_b(0.0F, 0.33333334F, 0.046666667F);
      GlStateManager.func_179152_a(0.010416667F, -0.010416667F, 0.010416667F);
      GlStateManager.func_187432_a(0.0F, 0.0F, -0.010416667F);
      GlStateManager.func_179132_a(false);
      if (var9 < 0) {
         for(int var14 = 0; var14 < 4; ++var14) {
            String var15 = var1.func_212364_a(var14, (var1x) -> {
               List var2 = GuiUtilRenderComponents.func_178908_a(var1x, 90, var12, false, true);
               return var2.isEmpty() ? "" : ((ITextComponent)var2.get(0)).func_150254_d();
            });
            if (var15 != null) {
               if (var14 == var1.field_145918_i) {
                  var15 = "> " + var15 + " <";
               }

               var12.func_211126_b(var15, (float)(-var12.func_78256_a(var15) / 2), (float)(var14 * 10 - var1.field_145915_a.length * 5), 0);
            }
         }
      }

      GlStateManager.func_179132_a(true);
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.func_179121_F();
      if (var9 >= 0) {
         GlStateManager.func_179128_n(5890);
         GlStateManager.func_179121_F();
         GlStateManager.func_179128_n(5888);
      }

   }
}
