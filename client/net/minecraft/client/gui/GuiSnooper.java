package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Map.Entry;
import net.minecraft.client.GameSettings;
import net.minecraft.client.resources.I18n;

public class GuiSnooper extends GuiScreen {
   private final GuiScreen field_146608_a;
   private final GameSettings field_146603_f;
   private final java.util.List<String> field_146604_g = Lists.newArrayList();
   private final java.util.List<String> field_146609_h = Lists.newArrayList();
   private String field_146610_i;
   private String[] field_146607_r;
   private GuiSnooper.List field_146606_s;
   private GuiButton field_146605_t;

   public GuiSnooper(GuiScreen var1, GameSettings var2) {
      super();
      this.field_146608_a = var1;
      this.field_146603_f = var2;
   }

   public IGuiEventListener getFocused() {
      return this.field_146606_s;
   }

   protected void func_73866_w_() {
      this.field_146610_i = I18n.func_135052_a("options.snooper.title");
      String var1 = I18n.func_135052_a("options.snooper.desc");
      ArrayList var2 = Lists.newArrayList();
      Iterator var3 = this.field_146289_q.func_78271_c(var1, this.field_146294_l - 30).iterator();

      while(var3.hasNext()) {
         String var4 = (String)var3.next();
         var2.add(var4);
      }

      this.field_146607_r = (String[])var2.toArray(new String[var2.size()]);
      this.field_146604_g.clear();
      this.field_146609_h.clear();
      GuiButton var7 = new GuiButton(1, this.field_146294_l / 2 - 152, this.field_146295_m - 30, 150, 20, this.field_146603_f.func_74297_c(GameSettings.Options.SNOOPER_ENABLED)) {
         public void func_194829_a(double var1, double var3) {
            GuiSnooper.this.field_146603_f.func_74306_a(GameSettings.Options.SNOOPER_ENABLED, 1);
            GuiSnooper.this.field_146605_t.field_146126_j = GuiSnooper.this.field_146603_f.func_74297_c(GameSettings.Options.SNOOPER_ENABLED);
         }
      };
      var7.field_146124_l = false;
      this.field_146605_t = this.func_189646_b(var7);
      this.func_189646_b(new GuiButton(2, this.field_146294_l / 2 + 2, this.field_146295_m - 30, 150, 20, I18n.func_135052_a("gui.done")) {
         public void func_194829_a(double var1, double var3) {
            GuiSnooper.this.field_146603_f.func_74303_b();
            GuiSnooper.this.field_146603_f.func_74303_b();
            GuiSnooper.this.field_146297_k.func_147108_a(GuiSnooper.this.field_146608_a);
         }
      });
      boolean var8 = this.field_146297_k.func_71401_C() != null && this.field_146297_k.func_71401_C().func_80003_ah() != null;
      Iterator var5 = (new TreeMap(this.field_146297_k.func_71378_E().func_76465_c())).entrySet().iterator();

      Entry var6;
      while(var5.hasNext()) {
         var6 = (Entry)var5.next();
         this.field_146604_g.add((var8 ? "C " : "") + (String)var6.getKey());
         this.field_146609_h.add(this.field_146289_q.func_78269_a((String)var6.getValue(), this.field_146294_l - 220));
      }

      if (var8) {
         var5 = (new TreeMap(this.field_146297_k.func_71401_C().func_80003_ah().func_76465_c())).entrySet().iterator();

         while(var5.hasNext()) {
            var6 = (Entry)var5.next();
            this.field_146604_g.add("S " + (String)var6.getKey());
            this.field_146609_h.add(this.field_146289_q.func_78269_a((String)var6.getValue(), this.field_146294_l - 220));
         }
      }

      this.field_146606_s = new GuiSnooper.List();
      this.field_195124_j.add(this.field_146606_s);
   }

   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146276_q_();
      this.field_146606_s.func_148128_a(var1, var2, var3);
      this.func_73732_a(this.field_146289_q, this.field_146610_i, this.field_146294_l / 2, 8, 16777215);
      int var4 = 22;
      String[] var5 = this.field_146607_r;
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         String var8 = var5[var7];
         this.func_73732_a(this.field_146289_q, var8, this.field_146294_l / 2, var4, 8421504);
         var4 += this.field_146289_q.field_78288_b;
      }

      super.func_73863_a(var1, var2, var3);
   }

   class List extends GuiSlot {
      public List() {
         super(GuiSnooper.this.field_146297_k, GuiSnooper.this.field_146294_l, GuiSnooper.this.field_146295_m, 80, GuiSnooper.this.field_146295_m - 40, GuiSnooper.this.field_146289_q.field_78288_b + 1);
      }

      protected int func_148127_b() {
         return GuiSnooper.this.field_146604_g.size();
      }

      protected boolean func_148131_a(int var1) {
         return false;
      }

      protected void func_148123_a() {
      }

      protected void func_192637_a(int var1, int var2, int var3, int var4, int var5, int var6, float var7) {
         GuiSnooper.this.field_146289_q.func_211126_b((String)GuiSnooper.this.field_146604_g.get(var1), 10.0F, (float)var3, 16777215);
         GuiSnooper.this.field_146289_q.func_211126_b((String)GuiSnooper.this.field_146609_h.get(var1), 230.0F, (float)var3, 16777215);
      }

      protected int func_148137_d() {
         return this.field_148155_a - 10;
      }
   }
}
