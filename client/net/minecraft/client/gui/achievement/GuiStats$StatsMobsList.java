package net.minecraft.client.gui.achievement;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityList$EntityEggInfo;

class GuiStats$StatsMobsList extends GuiSlot {
   private final List field_148222_l;

   public GuiStats$StatsMobsList(GuiStats var1) {
      super(GuiStats.access$1900(var1), var1.field_146294_l, var1.field_146295_m, 32, var1.field_146295_m - 64, GuiStats.access$2000(var1).field_78288_b * 4);
      this.field_148223_k = var1;
      this.field_148222_l = new ArrayList();
      this.func_148130_a(false);

      for(EntityList$EntityEggInfo var3 : EntityList.field_75627_a.values()) {
         if (GuiStats.access$200(var1).func_77444_a(var3.field_151512_d) > 0 || GuiStats.access$200(var1).func_77444_a(var3.field_151513_e) > 0) {
            this.field_148222_l.add(var3);
         }
      }
   }

   @Override
   protected int func_148127_b() {
      return this.field_148222_l.size();
   }

   @Override
   protected void func_148144_a(int var1, boolean var2, int var3, int var4) {
   }

   @Override
   protected boolean func_148131_a(int var1) {
      return false;
   }

   @Override
   protected int func_148138_e() {
      return this.func_148127_b() * GuiStats.access$2100(this.field_148223_k).field_78288_b * 4;
   }

   @Override
   protected void func_148123_a() {
      this.field_148223_k.func_146276_q_();
   }

   @Override
   protected void func_148126_a(int var1, int var2, int var3, int var4, Tessellator var5, int var6, int var7) {
      EntityList$EntityEggInfo var8 = (EntityList$EntityEggInfo)this.field_148222_l.get(var1);
      String var9 = I18n.func_135052_a("entity." + EntityList.func_75617_a(var8.field_75613_a) + ".name");
      int var10 = GuiStats.access$200(this.field_148223_k).func_77444_a(var8.field_151512_d);
      int var11 = GuiStats.access$200(this.field_148223_k).func_77444_a(var8.field_151513_e);
      String var12 = I18n.func_135052_a("stat.entityKills", var10, var9);
      String var13 = I18n.func_135052_a("stat.entityKilledBy", var9, var11);
      if (var10 == 0) {
         var12 = I18n.func_135052_a("stat.entityKills.none", var9);
      }

      if (var11 == 0) {
         var13 = I18n.func_135052_a("stat.entityKilledBy.none", var9);
      }

      this.field_148223_k.func_73731_b(GuiStats.access$2200(this.field_148223_k), var9, var2 + 2 - 10, var3 + 1, 16777215);
      this.field_148223_k
         .func_73731_b(
            GuiStats.access$2300(this.field_148223_k),
            var12,
            var2 + 2,
            var3 + 1 + GuiStats.access$2400(this.field_148223_k).field_78288_b,
            var10 == 0 ? 6316128 : 9474192
         );
      this.field_148223_k
         .func_73731_b(
            GuiStats.access$2500(this.field_148223_k),
            var13,
            var2 + 2,
            var3 + 1 + GuiStats.access$2600(this.field_148223_k).field_78288_b * 2,
            var11 == 0 ? 6316128 : 9474192
         );
   }
}
