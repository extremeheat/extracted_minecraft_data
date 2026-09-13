package net.minecraft.client.gui;

import java.util.Date;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.world.storage.SaveFormatComparator;

class GuiSelectWorld$List extends GuiSlot {
   public GuiSelectWorld$List(GuiSelectWorld var1) {
      super(var1.field_146297_k, var1.field_146294_l, var1.field_146295_m, 32, var1.field_146295_m - 64, 36);
      this.field_148207_k = var1;
   }

   @Override
   protected int func_148127_b() {
      return GuiSelectWorld.access$000(this.field_148207_k).size();
   }

   @Override
   protected void func_148144_a(int var1, boolean var2, int var3, int var4) {
      GuiSelectWorld.access$102(this.field_148207_k, var1);
      boolean var5 = GuiSelectWorld.access$100(this.field_148207_k) >= 0 && GuiSelectWorld.access$100(this.field_148207_k) < this.func_148127_b();
      GuiSelectWorld.access$200(this.field_148207_k).field_146124_l = var5;
      GuiSelectWorld.access$300(this.field_148207_k).field_146124_l = var5;
      GuiSelectWorld.access$400(this.field_148207_k).field_146124_l = var5;
      GuiSelectWorld.access$500(this.field_148207_k).field_146124_l = var5;
      if (var2 && var5) {
         this.field_148207_k.func_146615_e(var1);
      }
   }

   @Override
   protected boolean func_148131_a(int var1) {
      return var1 == GuiSelectWorld.access$100(this.field_148207_k);
   }

   @Override
   protected int func_148138_e() {
      return GuiSelectWorld.access$000(this.field_148207_k).size() * 36;
   }

   @Override
   protected void func_148123_a() {
      this.field_148207_k.func_146276_q_();
   }

   @Override
   protected void func_148126_a(int var1, int var2, int var3, int var4, Tessellator var5, int var6, int var7) {
      SaveFormatComparator var8 = (SaveFormatComparator)GuiSelectWorld.access$000(this.field_148207_k).get(var1);
      String var9 = var8.func_75788_b();
      if (var9 == null || MathHelper.func_76139_a(var9)) {
         var9 = GuiSelectWorld.access$600(this.field_148207_k) + " " + (var1 + 1);
      }

      String var10 = var8.func_75786_a();
      var10 = var10 + " (" + GuiSelectWorld.access$700(this.field_148207_k).format(new Date(var8.func_75784_e()));
      var10 = var10 + ")";
      String var11 = "";
      if (var8.func_75785_d()) {
         var11 = GuiSelectWorld.access$800(this.field_148207_k) + " " + var11;
      } else {
         var11 = GuiSelectWorld.access$900(this.field_148207_k)[var8.func_75790_f().func_77148_a()];
         if (var8.func_75789_g()) {
            var11 = EnumChatFormatting.DARK_RED + I18n.func_135052_a("gameMode.hardcore") + EnumChatFormatting.RESET;
         }

         if (var8.func_75783_h()) {
            var11 = var11 + ", " + I18n.func_135052_a("selectWorld.cheats");
         }
      }

      this.field_148207_k.func_73731_b(this.field_148207_k.field_146289_q, var9, var2 + 2, var3 + 1, 16777215);
      this.field_148207_k.func_73731_b(this.field_148207_k.field_146289_q, var10, var2 + 2, var3 + 12, 8421504);
      this.field_148207_k.func_73731_b(this.field_148207_k.field_146289_q, var11, var2 + 2, var3 + 12 + 10, 8421504);
   }
}
