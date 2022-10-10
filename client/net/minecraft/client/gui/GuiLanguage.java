package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.Language;
import net.minecraft.client.resources.LanguageManager;

public class GuiLanguage extends GuiScreen {
   protected GuiScreen field_146453_a;
   private GuiLanguage.List field_146450_f;
   private final GameSettings field_146451_g;
   private final LanguageManager field_146454_h;
   private GuiOptionButton field_211832_i;
   private GuiOptionButton field_146452_r;

   public GuiLanguage(GuiScreen var1, GameSettings var2, LanguageManager var3) {
      super();
      this.field_146453_a = var1;
      this.field_146451_g = var2;
      this.field_146454_h = var3;
   }

   public IGuiEventListener getFocused() {
      return this.field_146450_f;
   }

   protected void func_73866_w_() {
      this.field_146450_f = new GuiLanguage.List(this.field_146297_k);
      this.field_195124_j.add(this.field_146450_f);
      this.field_211832_i = (GuiOptionButton)this.func_189646_b(new GuiOptionButton(100, this.field_146294_l / 2 - 155, this.field_146295_m - 38, GameSettings.Options.FORCE_UNICODE_FONT, this.field_146451_g.func_74297_c(GameSettings.Options.FORCE_UNICODE_FONT)) {
         public void func_194829_a(double var1, double var3) {
            GuiLanguage.this.field_146451_g.func_74306_a(this.func_146136_c(), 1);
            this.field_146126_j = GuiLanguage.this.field_146451_g.func_74297_c(GameSettings.Options.FORCE_UNICODE_FONT);
            GuiLanguage.this.func_195181_h();
         }
      });
      this.field_146452_r = (GuiOptionButton)this.func_189646_b(new GuiOptionButton(6, this.field_146294_l / 2 - 155 + 160, this.field_146295_m - 38, I18n.func_135052_a("gui.done")) {
         public void func_194829_a(double var1, double var3) {
            GuiLanguage.this.field_146297_k.func_147108_a(GuiLanguage.this.field_146453_a);
         }
      });
      super.func_73866_w_();
   }

   private void func_195181_h() {
      this.field_146297_k.field_195558_d.func_198098_h();
   }

   public void func_73863_a(int var1, int var2, float var3) {
      this.field_146450_f.func_148128_a(var1, var2, var3);
      this.func_73732_a(this.field_146289_q, I18n.func_135052_a("options.language"), this.field_146294_l / 2, 16, 16777215);
      this.func_73732_a(this.field_146289_q, "(" + I18n.func_135052_a("options.languageWarning") + ")", this.field_146294_l / 2, this.field_146295_m - 56, 8421504);
      super.func_73863_a(var1, var2, var3);
   }

   class List extends GuiSlot {
      private final java.util.List<String> field_148176_l = Lists.newArrayList();
      private final Map<String, Language> field_148177_m = Maps.newHashMap();

      public List(Minecraft var2) {
         super(var2, GuiLanguage.this.field_146294_l, GuiLanguage.this.field_146295_m, 32, GuiLanguage.this.field_146295_m - 65 + 4, 18);
         Iterator var3 = GuiLanguage.this.field_146454_h.func_135040_d().iterator();

         while(var3.hasNext()) {
            Language var4 = (Language)var3.next();
            this.field_148177_m.put(var4.func_135034_a(), var4);
            this.field_148176_l.add(var4.func_135034_a());
         }

      }

      protected int func_148127_b() {
         return this.field_148176_l.size();
      }

      protected boolean func_195078_a(int var1, int var2, double var3, double var5) {
         Language var7 = (Language)this.field_148177_m.get(this.field_148176_l.get(var1));
         GuiLanguage.this.field_146454_h.func_135045_a(var7);
         GuiLanguage.this.field_146451_g.field_74363_ab = var7.func_135034_a();
         this.field_148161_k.func_110436_a();
         GuiLanguage.this.field_146289_q.func_78275_b(GuiLanguage.this.field_146454_h.func_135044_b());
         GuiLanguage.this.field_146452_r.field_146126_j = I18n.func_135052_a("gui.done");
         GuiLanguage.this.field_211832_i.field_146126_j = GuiLanguage.this.field_146451_g.func_74297_c(GameSettings.Options.FORCE_UNICODE_FONT);
         GuiLanguage.this.field_146451_g.func_74303_b();
         GuiLanguage.this.func_195181_h();
         return true;
      }

      protected boolean func_148131_a(int var1) {
         return ((String)this.field_148176_l.get(var1)).equals(GuiLanguage.this.field_146454_h.func_135041_c().func_135034_a());
      }

      protected int func_148138_e() {
         return this.func_148127_b() * 18;
      }

      protected void func_148123_a() {
         GuiLanguage.this.func_146276_q_();
      }

      protected void func_192637_a(int var1, int var2, int var3, int var4, int var5, int var6, float var7) {
         GuiLanguage.this.field_146289_q.func_78275_b(true);
         this.func_73732_a(GuiLanguage.this.field_146289_q, ((Language)this.field_148177_m.get(this.field_148176_l.get(var1))).toString(), this.field_148155_a / 2, var3 + 1, 16777215);
         GuiLanguage.this.field_146289_q.func_78275_b(GuiLanguage.this.field_146454_h.func_135041_c().func_135035_b());
      }
   }
}
