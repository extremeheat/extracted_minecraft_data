package net.minecraft.client.gui.inventory;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class GuiEditSign extends GuiScreen {
   private TileEntitySign field_146848_f;
   private int field_146849_g;
   private int field_146851_h;
   private GuiButton field_146852_i;

   public GuiEditSign(TileEntitySign var1) {
      super();
      this.field_146848_f = var1;
   }

   @Override
   public void func_73866_w_() {
      this.field_146292_n.clear();
      Keyboard.enableRepeatEvents(true);
      this.field_146292_n
         .add(this.field_146852_i = new GuiButton(0, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 120, I18n.func_135052_a("gui.done")));
      this.field_146848_f.func_145913_a(false);
   }

   @Override
   public void func_146281_b() {
      Keyboard.enableRepeatEvents(false);
      NetHandlerPlayClient var1 = this.field_146297_k.func_147114_u();
      if (var1 != null) {
         var1.func_147297_a(
            new C12PacketUpdateSign(
               this.field_146848_f.field_145851_c, this.field_146848_f.field_145848_d, this.field_146848_f.field_145849_e, this.field_146848_f.field_145915_a
            )
         );
      }

      this.field_146848_f.func_145913_a(true);
   }

   @Override
   public void func_73876_c() {
      ++this.field_146849_g;
   }

   @Override
   protected void func_146284_a(GuiButton var1) {
      if (var1.field_146124_l) {
         if (var1.field_146127_k == 0) {
            this.field_146848_f.func_70296_d();
            this.field_146297_k.func_147108_a(null);
         }
      }
   }

   @Override
   protected void func_73869_a(char var1, int var2) {
      if (var2 == 200) {
         this.field_146851_h = this.field_146851_h - 1 & 3;
      }

      if (var2 == 208 || var2 == 28 || var2 == 156) {
         this.field_146851_h = this.field_146851_h + 1 & 3;
      }

      if (var2 == 14 && this.field_146848_f.field_145915_a[this.field_146851_h].length() > 0) {
         this.field_146848_f.field_145915_a[this.field_146851_h] = this.field_146848_f.field_145915_a[this.field_146851_h]
            .substring(0, this.field_146848_f.field_145915_a[this.field_146851_h].length() - 1);
      }

      if (ChatAllowedCharacters.func_71566_a(var1) && this.field_146848_f.field_145915_a[this.field_146851_h].length() < 15) {
         this.field_146848_f.field_145915_a[this.field_146851_h] = this.field_146848_f.field_145915_a[this.field_146851_h] + var1;
      }

      if (var2 == 1) {
         this.func_146284_a(this.field_146852_i);
      }
   }

   @Override
   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146276_q_();
      this.func_73732_a(this.field_146289_q, I18n.func_135052_a("sign.edit"), this.field_146294_l / 2, 40, 16777215);
      GL11.glPushMatrix();
      GL11.glTranslatef((float)(this.field_146294_l / 2), 0.0F, 50.0F);
      float var4 = 93.75F;
      GL11.glScalef(-var4, -var4, -var4);
      GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
      Block var5 = this.field_146848_f.func_145838_q();
      if (var5 == Blocks.field_150472_an) {
         float var6 = (float)(this.field_146848_f.func_145832_p() * 360) / 16.0F;
         GL11.glRotatef(var6, 0.0F, 1.0F, 0.0F);
         GL11.glTranslatef(0.0F, -1.0625F, 0.0F);
      } else {
         int var8 = this.field_146848_f.func_145832_p();
         float var7 = 0.0F;
         if (var8 == 2) {
            var7 = 180.0F;
         }

         if (var8 == 4) {
            var7 = 90.0F;
         }

         if (var8 == 5) {
            var7 = -90.0F;
         }

         GL11.glRotatef(var7, 0.0F, 1.0F, 0.0F);
         GL11.glTranslatef(0.0F, -1.0625F, 0.0F);
      }

      if (this.field_146849_g / 6 % 2 == 0) {
         this.field_146848_f.field_145918_i = this.field_146851_h;
      }

      TileEntityRendererDispatcher.field_147556_a.func_147549_a(this.field_146848_f, -0.5, -0.75, -0.5, 0.0F);
      this.field_146848_f.field_145918_i = -1;
      GL11.glPopMatrix();
      super.func_73863_a(var1, var2, var3);
   }
}
