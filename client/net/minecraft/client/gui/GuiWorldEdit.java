package net.minecraft.client.gui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.toasts.GuiToast;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.io.FileUtils;

public class GuiWorldEdit extends GuiScreen {
   private GuiButton field_195327_a;
   private final GuiYesNoCallback field_184858_a;
   private GuiTextField field_184859_f;
   private final String field_184860_g;

   public GuiWorldEdit(GuiYesNoCallback var1, String var2) {
      super();
      this.field_184858_a = var1;
      this.field_184860_g = var2;
   }

   public void func_73876_c() {
      this.field_184859_f.func_146178_a();
   }

   protected void func_73866_w_() {
      this.field_146297_k.field_195559_v.func_197967_a(true);
      GuiButton var1 = this.func_189646_b(new GuiButton(3, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 24 + 5, I18n.func_135052_a("selectWorld.edit.resetIcon")) {
         public void func_194829_a(double var1, double var3) {
            ISaveFormat var5 = GuiWorldEdit.this.field_146297_k.func_71359_d();
            FileUtils.deleteQuietly(var5.func_186352_b(GuiWorldEdit.this.field_184860_g, "icon.png"));
            this.field_146124_l = false;
         }
      });
      this.func_189646_b(new GuiButton(4, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 48 + 5, I18n.func_135052_a("selectWorld.edit.openFolder")) {
         public void func_194829_a(double var1, double var3) {
            ISaveFormat var5 = GuiWorldEdit.this.field_146297_k.func_71359_d();
            Util.func_110647_a().func_195641_a(var5.func_186352_b(GuiWorldEdit.this.field_184860_g, "icon.png").getParentFile());
         }
      });
      this.func_189646_b(new GuiButton(5, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 72 + 5, I18n.func_135052_a("selectWorld.edit.backup")) {
         public void func_194829_a(double var1, double var3) {
            ISaveFormat var5 = GuiWorldEdit.this.field_146297_k.func_71359_d();
            GuiWorldEdit.func_200212_a(var5, GuiWorldEdit.this.field_184860_g);
            GuiWorldEdit.this.field_184858_a.confirmResult(false, 0);
         }
      });
      this.func_189646_b(new GuiButton(6, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 96 + 5, I18n.func_135052_a("selectWorld.edit.backupFolder")) {
         public void func_194829_a(double var1, double var3) {
            ISaveFormat var5 = GuiWorldEdit.this.field_146297_k.func_71359_d();
            Path var6 = var5.func_197712_e();

            try {
               Files.createDirectories(Files.exists(var6, new LinkOption[0]) ? var6.toRealPath() : var6);
            } catch (IOException var8) {
               throw new RuntimeException(var8);
            }

            Util.func_110647_a().func_195641_a(var6.toFile());
         }
      });
      this.func_189646_b(new GuiButton(7, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 120 + 5, I18n.func_135052_a("selectWorld.edit.optimize")) {
         public void func_194829_a(double var1, double var3) {
            GuiWorldEdit.this.field_146297_k.func_147108_a(new GuiConfirmBackup(GuiWorldEdit.this, (var1x) -> {
               if (var1x) {
                  GuiWorldEdit.func_200212_a(GuiWorldEdit.this.field_146297_k.func_71359_d(), GuiWorldEdit.this.field_184860_g);
               }

               GuiWorldEdit.this.field_146297_k.func_147108_a(new GuiOptimizeWorld(GuiWorldEdit.this.field_184858_a, GuiWorldEdit.this.field_184860_g, GuiWorldEdit.this.field_146297_k.func_71359_d()));
            }, I18n.func_135052_a("optimizeWorld.confirm.title"), I18n.func_135052_a("optimizeWorld.confirm.description")));
         }
      });
      this.field_195327_a = this.func_189646_b(new GuiButton(0, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 144 + 5, 98, 20, I18n.func_135052_a("selectWorld.edit.save")) {
         public void func_194829_a(double var1, double var3) {
            GuiWorldEdit.this.func_195317_h();
         }
      });
      this.func_189646_b(new GuiButton(1, this.field_146294_l / 2 + 2, this.field_146295_m / 4 + 144 + 5, 98, 20, I18n.func_135052_a("gui.cancel")) {
         public void func_194829_a(double var1, double var3) {
            GuiWorldEdit.this.field_184858_a.confirmResult(false, 0);
         }
      });
      var1.field_146124_l = this.field_146297_k.func_71359_d().func_186352_b(this.field_184860_g, "icon.png").isFile();
      ISaveFormat var2 = this.field_146297_k.func_71359_d();
      WorldInfo var3 = var2.func_75803_c(this.field_184860_g);
      String var4 = var3 == null ? "" : var3.func_76065_j();
      this.field_184859_f = new GuiTextField(2, this.field_146289_q, this.field_146294_l / 2 - 100, 53, 200, 20);
      this.field_184859_f.func_146195_b(true);
      this.field_184859_f.func_146180_a(var4);
      this.field_195124_j.add(this.field_184859_f);
   }

   public void func_175273_b(Minecraft var1, int var2, int var3) {
      String var4 = this.field_184859_f.func_146179_b();
      this.func_146280_a(var1, var2, var3);
      this.field_184859_f.func_146180_a(var4);
   }

   public void func_146281_b() {
      this.field_146297_k.field_195559_v.func_197967_a(false);
   }

   private void func_195317_h() {
      ISaveFormat var1 = this.field_146297_k.func_71359_d();
      var1.func_75806_a(this.field_184860_g, this.field_184859_f.func_146179_b().trim());
      this.field_184858_a.confirmResult(true, 0);
   }

   public static void func_200212_a(ISaveFormat var0, String var1) {
      GuiToast var2 = Minecraft.func_71410_x().func_193033_an();
      long var3 = 0L;
      IOException var5 = null;

      try {
         var3 = var0.func_197713_h(var1);
      } catch (IOException var8) {
         var5 = var8;
      }

      TextComponentTranslation var6;
      Object var7;
      if (var5 != null) {
         var6 = new TextComponentTranslation("selectWorld.edit.backupFailed", new Object[0]);
         var7 = new TextComponentString(var5.getMessage());
      } else {
         var6 = new TextComponentTranslation("selectWorld.edit.backupCreated", new Object[]{var1});
         var7 = new TextComponentTranslation("selectWorld.edit.backupSize", new Object[]{MathHelper.func_76143_f((double)var3 / 1048576.0D)});
      }

      var2.func_192988_a(new SystemToast(SystemToast.Type.WORLD_BACKUP, var6, (ITextComponent)var7));
   }

   public boolean charTyped(char var1, int var2) {
      if (this.field_184859_f.charTyped(var1, var2)) {
         this.field_195327_a.field_146124_l = !this.field_184859_f.func_146179_b().trim().isEmpty();
         return true;
      } else {
         return false;
      }
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (this.field_184859_f.keyPressed(var1, var2, var3)) {
         this.field_195327_a.field_146124_l = !this.field_184859_f.func_146179_b().trim().isEmpty();
         return true;
      } else if (var1 != 257 && var1 != 335) {
         return false;
      } else {
         this.func_195317_h();
         return true;
      }
   }

   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146276_q_();
      this.func_73732_a(this.field_146289_q, I18n.func_135052_a("selectWorld.edit.title"), this.field_146294_l / 2, 20, 16777215);
      this.func_73731_b(this.field_146289_q, I18n.func_135052_a("selectWorld.enterName"), this.field_146294_l / 2 - 100, 40, 10526880);
      this.field_184859_f.func_195608_a(var1, var2, var3);
      super.func_73863_a(var1, var2, var3);
   }
}
