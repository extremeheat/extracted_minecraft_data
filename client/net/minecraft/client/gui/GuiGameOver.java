package net.minecraft.client.gui;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;

public class GuiGameOver extends GuiScreen implements GuiYesNoCallback {
   private int field_146347_a;
   private boolean field_146346_f = false;

   public GuiGameOver() {
      super();
   }

   @Override
   public void func_73866_w_() {
      this.field_146292_n.clear();
      if (this.field_146297_k.field_71441_e.func_72912_H().func_76093_s()) {
         if (this.field_146297_k.func_71387_A()) {
            this.field_146292_n
               .add(new GuiButton(1, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 96, I18n.func_135052_a("deathScreen.deleteWorld")));
         } else {
            this.field_146292_n
               .add(new GuiButton(1, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 96, I18n.func_135052_a("deathScreen.leaveServer")));
         }
      } else {
         this.field_146292_n.add(new GuiButton(0, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 72, I18n.func_135052_a("deathScreen.respawn")));
         this.field_146292_n.add(new GuiButton(1, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 96, I18n.func_135052_a("deathScreen.titleScreen")));
         if (this.field_146297_k.func_110432_I() == null) {
            ((GuiButton)this.field_146292_n.get(1)).field_146124_l = false;
         }
      }

      for(GuiButton var2 : this.field_146292_n) {
         var2.field_146124_l = false;
      }
   }

   @Override
   protected void func_73869_a(char var1, int var2) {
   }

   @Override
   protected void func_146284_a(GuiButton var1) {
      switch(var1.field_146127_k) {
         case 0:
            this.field_146297_k.field_71439_g.func_71004_bE();
            this.field_146297_k.func_147108_a(null);
            break;
         case 1:
            GuiYesNo var2 = new GuiYesNo(
               this,
               I18n.func_135052_a("deathScreen.quit.confirm"),
               "",
               I18n.func_135052_a("deathScreen.titleScreen"),
               I18n.func_135052_a("deathScreen.respawn"),
               0
            );
            this.field_146297_k.func_147108_a(var2);
            var2.func_146350_a(20);
      }
   }

   @Override
   public void func_73878_a(boolean var1, int var2) {
      if (var1) {
         this.field_146297_k.field_71441_e.func_72882_A();
         this.field_146297_k.func_71403_a(null);
         this.field_146297_k.func_147108_a(new GuiMainMenu());
      } else {
         this.field_146297_k.field_71439_g.func_71004_bE();
         this.field_146297_k.func_147108_a(null);
      }
   }

   @Override
   public void func_73863_a(int var1, int var2, float var3) {
      this.func_73733_a(0, 0, this.field_146294_l, this.field_146295_m, 1615855616, -1602211792);
      GL11.glPushMatrix();
      GL11.glScalef(2.0F, 2.0F, 2.0F);
      boolean var4 = this.field_146297_k.field_71441_e.func_72912_H().func_76093_s();
      String var5 = var4 ? I18n.func_135052_a("deathScreen.title.hardcore") : I18n.func_135052_a("deathScreen.title");
      this.func_73732_a(this.field_146289_q, var5, this.field_146294_l / 2 / 2, 30, 16777215);
      GL11.glPopMatrix();
      if (var4) {
         this.func_73732_a(this.field_146289_q, I18n.func_135052_a("deathScreen.hardcoreInfo"), this.field_146294_l / 2, 144, 16777215);
      }

      this.func_73732_a(
         this.field_146289_q,
         I18n.func_135052_a("deathScreen.score") + ": " + EnumChatFormatting.YELLOW + this.field_146297_k.field_71439_g.func_71037_bA(),
         this.field_146294_l / 2,
         100,
         16777215
      );
      super.func_73863_a(var1, var2, var3);
   }

   @Override
   public boolean func_73868_f() {
      return false;
   }

   @Override
   public void func_73876_c() {
      super.func_73876_c();
      ++this.field_146347_a;
      if (this.field_146347_a == 20) {
         for(GuiButton var2 : this.field_146292_n) {
            var2.field_146124_l = true;
         }
      }
   }
}
