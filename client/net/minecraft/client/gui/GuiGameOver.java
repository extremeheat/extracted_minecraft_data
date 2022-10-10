package net.minecraft.client.gui;

import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;

public class GuiGameOver extends GuiScreen {
   private int field_146347_a;
   private final ITextComponent field_184871_f;

   public GuiGameOver(@Nullable ITextComponent var1) {
      super();
      this.field_184871_f = var1;
   }

   protected void func_73866_w_() {
      this.field_146347_a = 0;
      String var1;
      String var2;
      if (this.field_146297_k.field_71441_e.func_72912_H().func_76093_s()) {
         var1 = I18n.func_135052_a("deathScreen.spectate");
         var2 = I18n.func_135052_a("deathScreen." + (this.field_146297_k.func_71387_A() ? "deleteWorld" : "leaveServer"));
      } else {
         var1 = I18n.func_135052_a("deathScreen.respawn");
         var2 = I18n.func_135052_a("deathScreen.titleScreen");
      }

      this.func_189646_b(new GuiButton(0, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 72, var1) {
         public void func_194829_a(double var1, double var3) {
            GuiGameOver.this.field_146297_k.field_71439_g.func_71004_bE();
            GuiGameOver.this.field_146297_k.func_147108_a((GuiScreen)null);
         }
      });
      GuiButton var3 = this.func_189646_b(new GuiButton(1, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 96, var2) {
         public void func_194829_a(double var1, double var3) {
            if (GuiGameOver.this.field_146297_k.field_71441_e.func_72912_H().func_76093_s()) {
               GuiGameOver.this.field_146297_k.func_147108_a(new GuiMainMenu());
            } else {
               GuiYesNo var5 = new GuiYesNo(GuiGameOver.this, I18n.func_135052_a("deathScreen.quit.confirm"), "", I18n.func_135052_a("deathScreen.titleScreen"), I18n.func_135052_a("deathScreen.respawn"), 0);
               GuiGameOver.this.field_146297_k.func_147108_a(var5);
               var5.func_146350_a(20);
            }
         }
      });
      if (!this.field_146297_k.field_71441_e.func_72912_H().func_76093_s() && this.field_146297_k.func_110432_I() == null) {
         var3.field_146124_l = false;
      }

      GuiButton var5;
      for(Iterator var4 = this.field_146292_n.iterator(); var4.hasNext(); var5.field_146124_l = false) {
         var5 = (GuiButton)var4.next();
      }

   }

   public boolean func_195120_Y_() {
      return false;
   }

   public void confirmResult(boolean var1, int var2) {
      if (var2 == 31102009) {
         super.confirmResult(var1, var2);
      } else if (var1) {
         if (this.field_146297_k.field_71441_e != null) {
            this.field_146297_k.field_71441_e.func_72882_A();
         }

         this.field_146297_k.func_205055_a((WorldClient)null, new GuiDirtMessageScreen(I18n.func_135052_a("menu.savingLevel")));
         this.field_146297_k.func_147108_a(new GuiMainMenu());
      } else {
         this.field_146297_k.field_71439_g.func_71004_bE();
         this.field_146297_k.func_147108_a((GuiScreen)null);
      }

   }

   public void func_73863_a(int var1, int var2, float var3) {
      boolean var4 = this.field_146297_k.field_71441_e.func_72912_H().func_76093_s();
      this.func_73733_a(0, 0, this.field_146294_l, this.field_146295_m, 1615855616, -1602211792);
      GlStateManager.func_179094_E();
      GlStateManager.func_179152_a(2.0F, 2.0F, 2.0F);
      this.func_73732_a(this.field_146289_q, I18n.func_135052_a(var4 ? "deathScreen.title.hardcore" : "deathScreen.title"), this.field_146294_l / 2 / 2, 30, 16777215);
      GlStateManager.func_179121_F();
      if (this.field_184871_f != null) {
         this.func_73732_a(this.field_146289_q, this.field_184871_f.func_150254_d(), this.field_146294_l / 2, 85, 16777215);
      }

      this.func_73732_a(this.field_146289_q, I18n.func_135052_a("deathScreen.score") + ": " + TextFormatting.YELLOW + this.field_146297_k.field_71439_g.func_71037_bA(), this.field_146294_l / 2, 100, 16777215);
      if (this.field_184871_f != null && var2 > 85 && var2 < 85 + this.field_146289_q.field_78288_b) {
         ITextComponent var5 = this.func_184870_b(var1);
         if (var5 != null && var5.func_150256_b().func_150210_i() != null) {
            this.func_175272_a(var5, var1, var2);
         }
      }

      super.func_73863_a(var1, var2, var3);
   }

   @Nullable
   public ITextComponent func_184870_b(int var1) {
      if (this.field_184871_f == null) {
         return null;
      } else {
         int var2 = this.field_146297_k.field_71466_p.func_78256_a(this.field_184871_f.func_150254_d());
         int var3 = this.field_146294_l / 2 - var2 / 2;
         int var4 = this.field_146294_l / 2 + var2 / 2;
         int var5 = var3;
         if (var1 >= var3 && var1 <= var4) {
            Iterator var6 = this.field_184871_f.iterator();

            ITextComponent var7;
            do {
               if (!var6.hasNext()) {
                  return null;
               }

               var7 = (ITextComponent)var6.next();
               var5 += this.field_146297_k.field_71466_p.func_78256_a(GuiUtilRenderComponents.func_178909_a(var7.func_150261_e(), false));
            } while(var5 <= var1);

            return var7;
         } else {
            return null;
         }
      }
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (this.field_184871_f != null && var3 > 85.0D && var3 < (double)(85 + this.field_146289_q.field_78288_b)) {
         ITextComponent var6 = this.func_184870_b((int)var1);
         if (var6 != null && var6.func_150256_b().func_150235_h() != null && var6.func_150256_b().func_150235_h().func_150669_a() == ClickEvent.Action.OPEN_URL) {
            this.func_175276_a(var6);
            return false;
         }
      }

      return super.mouseClicked(var1, var3, var5);
   }

   public boolean func_73868_f() {
      return false;
   }

   public void func_73876_c() {
      super.func_73876_c();
      ++this.field_146347_a;
      GuiButton var2;
      if (this.field_146347_a == 20) {
         for(Iterator var1 = this.field_146292_n.iterator(); var1.hasNext(); var2.field_146124_l = true) {
            var2 = (GuiButton)var1.next();
         }
      }

   }
}
