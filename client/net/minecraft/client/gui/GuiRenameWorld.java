package net.minecraft.client.gui;

import net.minecraft.client.resources.I18n;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import org.lwjgl.input.Keyboard;

public class GuiRenameWorld extends GuiScreen {
   private GuiScreen field_146585_a;
   private GuiTextField field_146583_f;
   private final String field_146584_g;

   public GuiRenameWorld(GuiScreen var1, String var2) {
      super();
      this.field_146585_a = var1;
      this.field_146584_g = var2;
   }

   public void func_73876_c() {
      this.field_146583_f.func_146178_a();
   }

   public void func_73866_w_() {
      Keyboard.enableRepeatEvents(true);
      this.field_146292_n.clear();
      this.field_146292_n.add(new GuiButton(0, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 96 + 12, I18n.func_135052_a("selectWorld.renameButton")));
      this.field_146292_n.add(new GuiButton(1, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 120 + 12, I18n.func_135052_a("gui.cancel")));
      ISaveFormat var1 = this.field_146297_k.func_71359_d();
      WorldInfo var2 = var1.func_75803_c(this.field_146584_g);
      String var3 = var2.func_76065_j();
      this.field_146583_f = new GuiTextField(2, this.field_146289_q, this.field_146294_l / 2 - 100, 60, 200, 20);
      this.field_146583_f.func_146195_b(true);
      this.field_146583_f.func_146180_a(var3);
   }

   public void func_146281_b() {
      Keyboard.enableRepeatEvents(false);
   }

   protected void func_146284_a(GuiButton var1) {
      if (var1.field_146124_l) {
         if (var1.field_146127_k == 1) {
            this.field_146297_k.func_147108_a(this.field_146585_a);
         } else if (var1.field_146127_k == 0) {
            ISaveFormat var2 = this.field_146297_k.func_71359_d();
            var2.func_75806_a(this.field_146584_g, this.field_146583_f.func_146179_b().trim());
            this.field_146297_k.func_147108_a(this.field_146585_a);
         }

      }
   }

   protected void func_73869_a(char var1, int var2) {
      this.field_146583_f.func_146201_a(var1, var2);
      ((GuiButton)this.field_146292_n.get(0)).field_146124_l = this.field_146583_f.func_146179_b().trim().length() > 0;
      if (var2 == 28 || var2 == 156) {
         this.func_146284_a((GuiButton)this.field_146292_n.get(0));
      }

   }

   protected void func_73864_a(int var1, int var2, int var3) {
      super.func_73864_a(var1, var2, var3);
      this.field_146583_f.func_146192_a(var1, var2, var3);
   }

   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146276_q_();
      this.func_73732_a(this.field_146289_q, I18n.func_135052_a("selectWorld.renameTitle"), this.field_146294_l / 2, 20, 16777215);
      this.func_73731_b(this.field_146289_q, I18n.func_135052_a("selectWorld.enterName"), this.field_146294_l / 2 - 100, 47, 10526880);
      this.field_146583_f.func_146194_f();
      super.func_73863_a(var1, var2, var3);
   }
}
