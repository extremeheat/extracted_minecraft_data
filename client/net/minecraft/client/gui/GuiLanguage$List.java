package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.Language;
import net.minecraft.client.settings.GameSettings$Options;

class GuiLanguage$List extends GuiSlot {
   private final List field_148176_l;
   private final Map field_148177_m;

   public GuiLanguage$List(GuiLanguage var1) {
      super(var1.field_146297_k, var1.field_146294_l, var1.field_146295_m, 32, var1.field_146295_m - 65 + 4, 18);
      this.field_148178_k = var1;
      this.field_148176_l = Lists.newArrayList();
      this.field_148177_m = Maps.newHashMap();

      for(Language var3 : GuiLanguage.access$000(var1).func_135040_d()) {
         this.field_148177_m.put(var3.func_135034_a(), var3);
         this.field_148176_l.add(var3.func_135034_a());
      }
   }

   @Override
   protected int func_148127_b() {
      return this.field_148176_l.size();
   }

   @Override
   protected void func_148144_a(int var1, boolean var2, int var3, int var4) {
      Language var5 = (Language)this.field_148177_m.get(this.field_148176_l.get(var1));
      GuiLanguage.access$000(this.field_148178_k).func_135045_a(var5);
      GuiLanguage.access$100(this.field_148178_k).field_74363_ab = var5.func_135034_a();
      this.field_148178_k.field_146297_k.func_110436_a();
      this.field_148178_k
         .field_146289_q
         .func_78264_a(GuiLanguage.access$000(this.field_148178_k).func_135042_a() || GuiLanguage.access$100(this.field_148178_k).field_151455_aw);
      this.field_148178_k.field_146289_q.func_78275_b(GuiLanguage.access$000(this.field_148178_k).func_135044_b());
      GuiLanguage.access$200(this.field_148178_k).field_146126_j = I18n.func_135052_a("gui.done");
      GuiLanguage.access$300(this.field_148178_k).field_146126_j = GuiLanguage.access$100(this.field_148178_k)
         .func_74297_c(GameSettings$Options.FORCE_UNICODE_FONT);
      GuiLanguage.access$100(this.field_148178_k).func_74303_b();
   }

   @Override
   protected boolean func_148131_a(int var1) {
      return ((String)this.field_148176_l.get(var1)).equals(GuiLanguage.access$000(this.field_148178_k).func_135041_c().func_135034_a());
   }

   @Override
   protected int func_148138_e() {
      return this.func_148127_b() * 18;
   }

   @Override
   protected void func_148123_a() {
      this.field_148178_k.func_146276_q_();
   }

   @Override
   protected void func_148126_a(int var1, int var2, int var3, int var4, Tessellator var5, int var6, int var7) {
      this.field_148178_k.field_146289_q.func_78275_b(true);
      this.field_148178_k
         .func_73732_a(
            this.field_148178_k.field_146289_q,
            ((Language)this.field_148177_m.get(this.field_148176_l.get(var1))).toString(),
            this.field_148155_a / 2,
            var3 + 1,
            16777215
         );
      this.field_148178_k.field_146289_q.func_78275_b(GuiLanguage.access$000(this.field_148178_k).func_135041_c().func_135035_b());
   }
}
