package net.minecraft.client.gui;

import net.minecraft.util.IProgressUpdate;

public class GuiScreenWorking extends GuiScreen implements IProgressUpdate {
   private String field_146591_a = "";
   private String field_146589_f = "";
   private int field_146590_g;
   private boolean field_146592_h;

   public GuiScreenWorking() {
      super();
   }

   public void func_73720_a(String var1) {
      this.func_73721_b(var1);
   }

   public void func_73721_b(String var1) {
      this.field_146591_a = var1;
      this.func_73719_c("Working...");
   }

   public void func_73719_c(String var1) {
      this.field_146589_f = var1;
      this.func_73718_a(0);
   }

   public void func_73718_a(int var1) {
      this.field_146590_g = var1;
   }

   public void func_146586_a() {
      this.field_146592_h = true;
   }

   public void func_73863_a(int var1, int var2, float var3) {
      if (this.field_146592_h) {
         if (!this.field_146297_k.func_181540_al()) {
            this.field_146297_k.func_147108_a((GuiScreen)null);
         }

      } else {
         this.func_146276_q_();
         this.func_73732_a(this.field_146289_q, this.field_146591_a, this.field_146294_l / 2, 70, 16777215);
         this.func_73732_a(this.field_146289_q, this.field_146589_f + " " + this.field_146590_g + "%", this.field_146294_l / 2, 90, 16777215);
         super.func_73863_a(var1, var2, var3);
      }
   }
}
