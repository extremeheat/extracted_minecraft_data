package net.minecraft.client.gui;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import java.util.Iterator;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.Util;
import net.minecraft.util.WorldOptimizer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.ISaveFormat;

public class GuiOptimizeWorld extends GuiScreen {
   private static final Object2IntMap<DimensionType> field_212348_a = (Object2IntMap)Util.func_200696_a(new Object2IntOpenCustomHashMap(Util.func_212443_g()), (var0) -> {
      var0.put(DimensionType.OVERWORLD, -13408734);
      var0.put(DimensionType.NETHER, -10075085);
      var0.put(DimensionType.THE_END, -8943531);
      var0.defaultReturnValue(-2236963);
   });
   private final GuiYesNoCallback field_212134_f;
   private final WorldOptimizer field_212203_f;

   public GuiOptimizeWorld(GuiYesNoCallback var1, String var2, ISaveFormat var3) {
      super();
      this.field_212134_f = var1;
      this.field_212203_f = new WorldOptimizer(var2, var3, var3.func_75803_c(var2));
   }

   protected void func_73866_w_() {
      super.func_73866_w_();
      this.func_189646_b(new GuiButton(0, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 150, I18n.func_135052_a("gui.cancel")) {
         public void func_194829_a(double var1, double var3) {
            GuiOptimizeWorld.this.field_212203_f.func_212217_a();
            GuiOptimizeWorld.this.field_212134_f.confirmResult(false, 0);
         }
      });
   }

   public void func_73876_c() {
      if (this.field_212203_f.func_212218_b()) {
         this.field_212134_f.confirmResult(true, 0);
      }

   }

   public void func_146281_b() {
      this.field_212203_f.func_212217_a();
   }

   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146276_q_();
      this.func_73732_a(this.field_146289_q, I18n.func_135052_a("optimizeWorld.title", this.field_212203_f.func_212214_n()), this.field_146294_l / 2, 20, 16777215);
      int var4 = this.field_146294_l / 2 - 150;
      int var5 = this.field_146294_l / 2 + 150;
      int var6 = this.field_146295_m / 4 + 100;
      int var7 = var6 + 10;
      this.func_73732_a(this.field_146289_q, this.field_212203_f.func_212215_m().func_150254_d(), this.field_146294_l / 2, var6 - this.field_146289_q.field_78288_b - 2, 10526880);
      if (this.field_212203_f.func_212211_j() > 0) {
         func_73734_a(var4 - 1, var6 - 1, var5 + 1, var7 + 1, -16777216);
         this.func_73731_b(this.field_146289_q, I18n.func_135052_a("optimizeWorld.info.converted", this.field_212203_f.func_212208_k()), var4, 40, 10526880);
         this.func_73731_b(this.field_146289_q, I18n.func_135052_a("optimizeWorld.info.skipped", this.field_212203_f.func_212209_l()), var4, 40 + this.field_146289_q.field_78288_b + 3, 10526880);
         this.func_73731_b(this.field_146289_q, I18n.func_135052_a("optimizeWorld.info.total", this.field_212203_f.func_212211_j()), var4, 40 + (this.field_146289_q.field_78288_b + 3) * 2, 10526880);
         int var8 = 0;

         int var11;
         for(Iterator var9 = DimensionType.func_212681_b().iterator(); var9.hasNext(); var8 += var11) {
            DimensionType var10 = (DimensionType)var9.next();
            var11 = MathHelper.func_76141_d(this.field_212203_f.func_212543_a(var10) * (float)(var5 - var4));
            func_73734_a(var4 + var8, var6, var4 + var8 + var11, var7, field_212348_a.getInt(var10));
         }

         int var12 = this.field_212203_f.func_212208_k() + this.field_212203_f.func_212209_l();
         this.func_73732_a(this.field_146289_q, var12 + " / " + this.field_212203_f.func_212211_j(), this.field_146294_l / 2, var6 + 2 * this.field_146289_q.field_78288_b + 2, 10526880);
         this.func_73732_a(this.field_146289_q, MathHelper.func_76141_d(this.field_212203_f.func_212207_i() * 100.0F) + "%", this.field_146294_l / 2, var6 + ((var7 - var6) / 2 - this.field_146289_q.field_78288_b / 2), 10526880);
      }

      super.func_73863_a(var1, var2, var3);
   }
}
