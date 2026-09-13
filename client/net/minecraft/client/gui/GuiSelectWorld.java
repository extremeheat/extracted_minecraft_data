package net.minecraft.client.gui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldSettings$GameType;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SaveFormatComparator;
import net.minecraft.world.storage.WorldInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiSelectWorld extends GuiScreen implements GuiYesNoCallback {
   private static final Logger field_146629_g = LogManager.getLogger();
   private final DateFormat field_146633_h = new SimpleDateFormat();
   protected GuiScreen field_146632_a;
   protected String field_146628_f = "Select world";
   private boolean field_146634_i;
   private int field_146640_r;
   private List field_146639_s;
   private GuiSelectWorld$List field_146638_t;
   private String field_146637_u;
   private String field_146636_v;
   private String[] field_146635_w = new String[3];
   private boolean field_146643_x;
   private GuiButton field_146642_y;
   private GuiButton field_146641_z;
   private GuiButton field_146630_A;
   private GuiButton field_146631_B;

   public GuiSelectWorld(GuiScreen var1) {
      super();
      this.field_146632_a = var1;
   }

   @Override
   public void func_73866_w_() {
      this.field_146628_f = I18n.func_135052_a("selectWorld.title");

      try {
         this.func_146627_h();
      } catch (AnvilConverterException var2) {
         field_146629_g.error("Couldn't load level list", var2);
         this.field_146297_k.func_147108_a(new GuiErrorScreen("Unable to load worlds", var2.getMessage()));
         return;
      }

      this.field_146637_u = I18n.func_135052_a("selectWorld.world");
      this.field_146636_v = I18n.func_135052_a("selectWorld.conversion");
      this.field_146635_w[WorldSettings$GameType.SURVIVAL.func_77148_a()] = I18n.func_135052_a("gameMode.survival");
      this.field_146635_w[WorldSettings$GameType.CREATIVE.func_77148_a()] = I18n.func_135052_a("gameMode.creative");
      this.field_146635_w[WorldSettings$GameType.ADVENTURE.func_77148_a()] = I18n.func_135052_a("gameMode.adventure");
      this.field_146638_t = new GuiSelectWorld$List(this);
      this.field_146638_t.func_148134_d(4, 5);
      this.func_146618_g();
   }

   private void func_146627_h() {
      ISaveFormat var1 = this.field_146297_k.func_71359_d();
      this.field_146639_s = var1.func_75799_b();
      Collections.sort(this.field_146639_s);
      this.field_146640_r = -1;
   }

   protected String func_146621_a(int var1) {
      return ((SaveFormatComparator)this.field_146639_s.get(var1)).func_75786_a();
   }

   protected String func_146614_d(int var1) {
      String var2 = ((SaveFormatComparator)this.field_146639_s.get(var1)).func_75788_b();
      if (var2 == null || MathHelper.func_76139_a(var2)) {
         var2 = I18n.func_135052_a("selectWorld.world") + " " + (var1 + 1);
      }

      return var2;
   }

   public void func_146618_g() {
      this.field_146292_n
         .add(
            this.field_146641_z = new GuiButton(1, this.field_146294_l / 2 - 154, this.field_146295_m - 52, 150, 20, I18n.func_135052_a("selectWorld.select"))
         );
      this.field_146292_n.add(new GuiButton(3, this.field_146294_l / 2 + 4, this.field_146295_m - 52, 150, 20, I18n.func_135052_a("selectWorld.create")));
      this.field_146292_n
         .add(
            this.field_146630_A = new GuiButton(6, this.field_146294_l / 2 - 154, this.field_146295_m - 28, 72, 20, I18n.func_135052_a("selectWorld.rename"))
         );
      this.field_146292_n
         .add(this.field_146642_y = new GuiButton(2, this.field_146294_l / 2 - 76, this.field_146295_m - 28, 72, 20, I18n.func_135052_a("selectWorld.delete")));
      this.field_146292_n
         .add(
            this.field_146631_B = new GuiButton(7, this.field_146294_l / 2 + 4, this.field_146295_m - 28, 72, 20, I18n.func_135052_a("selectWorld.recreate"))
         );
      this.field_146292_n.add(new GuiButton(0, this.field_146294_l / 2 + 82, this.field_146295_m - 28, 72, 20, I18n.func_135052_a("gui.cancel")));
      this.field_146641_z.field_146124_l = false;
      this.field_146642_y.field_146124_l = false;
      this.field_146630_A.field_146124_l = false;
      this.field_146631_B.field_146124_l = false;
   }

   @Override
   protected void func_146284_a(GuiButton var1) {
      if (var1.field_146124_l) {
         if (var1.field_146127_k == 2) {
            String var2 = this.func_146614_d(this.field_146640_r);
            if (var2 != null) {
               this.field_146643_x = true;
               GuiYesNo var3 = func_152129_a(this, var2, this.field_146640_r);
               this.field_146297_k.func_147108_a(var3);
            }
         } else if (var1.field_146127_k == 1) {
            this.func_146615_e(this.field_146640_r);
         } else if (var1.field_146127_k == 3) {
            this.field_146297_k.func_147108_a(new GuiCreateWorld(this));
         } else if (var1.field_146127_k == 6) {
            this.field_146297_k.func_147108_a(new GuiRenameWorld(this, this.func_146621_a(this.field_146640_r)));
         } else if (var1.field_146127_k == 0) {
            this.field_146297_k.func_147108_a(this.field_146632_a);
         } else if (var1.field_146127_k == 7) {
            GuiCreateWorld var5 = new GuiCreateWorld(this);
            ISaveHandler var6 = this.field_146297_k.func_71359_d().func_75804_a(this.func_146621_a(this.field_146640_r), false);
            WorldInfo var4 = var6.func_75757_d();
            var6.func_75759_a();
            var5.func_146318_a(var4);
            this.field_146297_k.func_147108_a(var5);
         } else {
            this.field_146638_t.func_148147_a(var1);
         }
      }
   }

   public void func_146615_e(int var1) {
      this.field_146297_k.func_147108_a(null);
      if (!this.field_146634_i) {
         this.field_146634_i = true;
         String var2 = this.func_146621_a(var1);
         if (var2 == null) {
            var2 = "World" + var1;
         }

         String var3 = this.func_146614_d(var1);
         if (var3 == null) {
            var3 = "World" + var1;
         }

         if (this.field_146297_k.func_71359_d().func_90033_f(var2)) {
            this.field_146297_k.func_71371_a(var2, var3, null);
         }
      }
   }

   @Override
   public void func_73878_a(boolean var1, int var2) {
      if (this.field_146643_x) {
         this.field_146643_x = false;
         if (var1) {
            ISaveFormat var3 = this.field_146297_k.func_71359_d();
            var3.func_75800_d();
            var3.func_75802_e(this.func_146621_a(var2));

            try {
               this.func_146627_h();
            } catch (AnvilConverterException var5) {
               field_146629_g.error("Couldn't load level list", var5);
            }
         }

         this.field_146297_k.func_147108_a(this);
      }
   }

   @Override
   public void func_73863_a(int var1, int var2, float var3) {
      this.field_146638_t.func_148128_a(var1, var2, var3);
      this.func_73732_a(this.field_146289_q, this.field_146628_f, this.field_146294_l / 2, 20, 16777215);
      super.func_73863_a(var1, var2, var3);
   }

   public static GuiYesNo func_152129_a(GuiYesNoCallback var0, String var1, int var2) {
      String var3 = I18n.func_135052_a("selectWorld.deleteQuestion");
      String var4 = "'" + var1 + "' " + I18n.func_135052_a("selectWorld.deleteWarning");
      String var5 = I18n.func_135052_a("selectWorld.deleteButton");
      String var6 = I18n.func_135052_a("gui.cancel");
      return new GuiYesNo(var0, var3, var4, var5, var6, var2);
   }
}
