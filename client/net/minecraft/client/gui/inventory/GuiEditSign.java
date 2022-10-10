package net.minecraft.client.gui.inventory;

import net.minecraft.block.BlockStandingSign;
import net.minecraft.block.BlockWallSign;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketUpdateSign;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.text.TextComponentString;

public class GuiEditSign extends GuiScreen {
   private final TileEntitySign field_146848_f;
   private int field_146849_g;
   private int field_146851_h;
   private GuiButton field_146852_i;

   public GuiEditSign(TileEntitySign var1) {
      super();
      this.field_146848_f = var1;
   }

   protected void func_73866_w_() {
      this.field_146297_k.field_195559_v.func_197967_a(true);
      this.field_146852_i = this.func_189646_b(new GuiButton(0, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 120, I18n.func_135052_a("gui.done")) {
         public void func_194829_a(double var1, double var3) {
            GuiEditSign.this.func_195269_h();
         }
      });
      this.field_146848_f.func_145913_a(false);
   }

   public void func_146281_b() {
      this.field_146297_k.field_195559_v.func_197967_a(false);
      NetHandlerPlayClient var1 = this.field_146297_k.func_147114_u();
      if (var1 != null) {
         var1.func_147297_a(new CPacketUpdateSign(this.field_146848_f.func_174877_v(), this.field_146848_f.func_212366_a(0), this.field_146848_f.func_212366_a(1), this.field_146848_f.func_212366_a(2), this.field_146848_f.func_212366_a(3)));
      }

      this.field_146848_f.func_145913_a(true);
   }

   public void func_73876_c() {
      ++this.field_146849_g;
   }

   private void func_195269_h() {
      this.field_146848_f.func_70296_d();
      this.field_146297_k.func_147108_a((GuiScreen)null);
   }

   public boolean charTyped(char var1, int var2) {
      String var3 = this.field_146848_f.func_212366_a(this.field_146851_h).getString();
      if (SharedConstants.func_71566_a(var1) && this.field_146289_q.func_78256_a(var3 + var1) <= 90) {
         var3 = var3 + var1;
      }

      this.field_146848_f.func_212365_a(this.field_146851_h, new TextComponentString(var3));
      return true;
   }

   public void func_195122_V_() {
      this.func_195269_h();
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 265) {
         this.field_146851_h = this.field_146851_h - 1 & 3;
         return true;
      } else if (var1 != 264 && var1 != 257 && var1 != 335) {
         if (var1 == 259) {
            String var4 = this.field_146848_f.func_212366_a(this.field_146851_h).getString();
            if (!var4.isEmpty()) {
               var4 = var4.substring(0, var4.length() - 1);
               this.field_146848_f.func_212365_a(this.field_146851_h, new TextComponentString(var4));
            }

            return true;
         } else {
            return super.keyPressed(var1, var2, var3);
         }
      } else {
         this.field_146851_h = this.field_146851_h + 1 & 3;
         return true;
      }
   }

   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146276_q_();
      this.func_73732_a(this.field_146289_q, I18n.func_135052_a("sign.edit"), this.field_146294_l / 2, 40, 16777215);
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.func_179094_E();
      GlStateManager.func_179109_b((float)(this.field_146294_l / 2), 0.0F, 50.0F);
      float var4 = 93.75F;
      GlStateManager.func_179152_a(-93.75F, -93.75F, -93.75F);
      GlStateManager.func_179114_b(180.0F, 0.0F, 1.0F, 0.0F);
      IBlockState var5 = this.field_146848_f.func_195044_w();
      float var6;
      if (var5.func_177230_c() == Blocks.field_196649_cc) {
         var6 = (float)((Integer)var5.func_177229_b(BlockStandingSign.field_176413_a) * 360) / 16.0F;
      } else {
         var6 = ((EnumFacing)var5.func_177229_b(BlockWallSign.field_176412_a)).func_185119_l();
      }

      GlStateManager.func_179114_b(var6, 0.0F, 1.0F, 0.0F);
      GlStateManager.func_179109_b(0.0F, -1.0625F, 0.0F);
      if (this.field_146849_g / 6 % 2 == 0) {
         this.field_146848_f.field_145918_i = this.field_146851_h;
      }

      TileEntityRendererDispatcher.field_147556_a.func_147549_a(this.field_146848_f, -0.5D, -0.75D, -0.5D, 0.0F);
      this.field_146848_f.field_145918_i = -1;
      GlStateManager.func_179121_F();
      super.func_73863_a(var1, var2, var3);
   }
}
