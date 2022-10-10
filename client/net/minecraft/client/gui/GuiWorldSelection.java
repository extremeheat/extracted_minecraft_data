package net.minecraft.client.gui;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import javax.annotation.Nullable;
import net.minecraft.client.resources.I18n;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiWorldSelection extends GuiScreen {
   private static final Logger field_184868_g = LogManager.getLogger();
   protected GuiScreen field_184864_a;
   protected String field_184867_f = "Select world";
   private String field_184869_h;
   private GuiButton field_146642_y;
   private GuiButton field_146641_z;
   private GuiButton field_146630_A;
   private GuiButton field_184865_t;
   protected GuiTextField field_212352_g;
   private GuiListWorldSelection field_184866_u;

   public GuiWorldSelection(GuiScreen var1) {
      super();
      this.field_184864_a = var1;
   }

   public boolean mouseScrolled(double var1) {
      return this.field_184866_u.mouseScrolled(var1);
   }

   public void func_73876_c() {
      this.field_212352_g.func_146178_a();
   }

   protected void func_73866_w_() {
      this.field_146297_k.field_195559_v.func_197967_a(true);
      this.field_184867_f = I18n.func_135052_a("selectWorld.title");
      this.field_212352_g = new GuiTextField(0, this.field_146289_q, this.field_146294_l / 2 - 100, 22, 200, 20, this.field_212352_g) {
         public void func_146195_b(boolean var1) {
            super.func_146195_b(true);
         }
      };
      this.field_212352_g.func_195609_a((var1, var2) -> {
         this.field_184866_u.func_212330_a(() -> {
            return var2;
         }, false);
      });
      this.field_184866_u = new GuiListWorldSelection(this, this.field_146297_k, this.field_146294_l, this.field_146295_m, 48, this.field_146295_m - 64, 36, () -> {
         return this.field_212352_g.func_146179_b();
      }, this.field_184866_u);
      this.field_146641_z = this.func_189646_b(new GuiButton(1, this.field_146294_l / 2 - 154, this.field_146295_m - 52, 150, 20, I18n.func_135052_a("selectWorld.select")) {
         public void func_194829_a(double var1, double var3) {
            GuiListWorldSelectionEntry var5 = GuiWorldSelection.this.field_184866_u.func_186794_f();
            if (var5 != null) {
               var5.func_186774_a();
            }

         }
      });
      this.func_189646_b(new GuiButton(3, this.field_146294_l / 2 + 4, this.field_146295_m - 52, 150, 20, I18n.func_135052_a("selectWorld.create")) {
         public void func_194829_a(double var1, double var3) {
            GuiWorldSelection.this.field_146297_k.func_147108_a(new GuiCreateWorld(GuiWorldSelection.this));
         }
      });
      this.field_146630_A = this.func_189646_b(new GuiButton(4, this.field_146294_l / 2 - 154, this.field_146295_m - 28, 72, 20, I18n.func_135052_a("selectWorld.edit")) {
         public void func_194829_a(double var1, double var3) {
            GuiListWorldSelectionEntry var5 = GuiWorldSelection.this.field_184866_u.func_186794_f();
            if (var5 != null) {
               var5.func_186778_c();
            }

         }
      });
      this.field_146642_y = this.func_189646_b(new GuiButton(2, this.field_146294_l / 2 - 76, this.field_146295_m - 28, 72, 20, I18n.func_135052_a("selectWorld.delete")) {
         public void func_194829_a(double var1, double var3) {
            GuiListWorldSelectionEntry var5 = GuiWorldSelection.this.field_184866_u.func_186794_f();
            if (var5 != null) {
               var5.func_186776_b();
            }

         }
      });
      this.field_184865_t = this.func_189646_b(new GuiButton(5, this.field_146294_l / 2 + 4, this.field_146295_m - 28, 72, 20, I18n.func_135052_a("selectWorld.recreate")) {
         public void func_194829_a(double var1, double var3) {
            GuiListWorldSelectionEntry var5 = GuiWorldSelection.this.field_184866_u.func_186794_f();
            if (var5 != null) {
               var5.func_186779_d();
            }

         }
      });
      this.func_189646_b(new GuiButton(0, this.field_146294_l / 2 + 82, this.field_146295_m - 28, 72, 20, I18n.func_135052_a("gui.cancel")) {
         public void func_194829_a(double var1, double var3) {
            GuiWorldSelection.this.field_146297_k.func_147108_a(GuiWorldSelection.this.field_184864_a);
         }
      });
      this.field_146641_z.field_146124_l = false;
      this.field_146642_y.field_146124_l = false;
      this.field_146630_A.field_146124_l = false;
      this.field_184865_t.field_146124_l = false;
      this.field_195124_j.add(this.field_212352_g);
      this.field_195124_j.add(this.field_184866_u);
      this.field_212352_g.func_146195_b(true);
      this.field_212352_g.func_146205_d(false);
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      return super.keyPressed(var1, var2, var3) ? true : this.field_212352_g.keyPressed(var1, var2, var3);
   }

   public boolean charTyped(char var1, int var2) {
      return this.field_212352_g.charTyped(var1, var2);
   }

   public void func_73863_a(int var1, int var2, float var3) {
      this.field_184869_h = null;
      this.field_184866_u.func_148128_a(var1, var2, var3);
      this.field_212352_g.func_195608_a(var1, var2, var3);
      this.func_73732_a(this.field_146289_q, this.field_184867_f, this.field_146294_l / 2, 8, 16777215);
      super.func_73863_a(var1, var2, var3);
      if (this.field_184869_h != null) {
         this.func_146283_a(Lists.newArrayList(Splitter.on("\n").split(this.field_184869_h)), var1, var2);
      }

   }

   public void func_184861_a(String var1) {
      this.field_184869_h = var1;
   }

   public void func_184863_a(@Nullable GuiListWorldSelectionEntry var1) {
      boolean var2 = var1 != null;
      this.field_146641_z.field_146124_l = var2;
      this.field_146642_y.field_146124_l = var2;
      this.field_146630_A.field_146124_l = var2;
      this.field_184865_t.field_146124_l = var2;
   }

   public void func_146281_b() {
      if (this.field_184866_u != null) {
         this.field_184866_u.func_195074_b().forEach(GuiListWorldSelectionEntry::close);
      }

   }
}
