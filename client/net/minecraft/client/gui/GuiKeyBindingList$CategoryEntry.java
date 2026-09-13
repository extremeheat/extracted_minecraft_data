package net.minecraft.client.gui;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;

public class GuiKeyBindingList$CategoryEntry implements GuiListExtended$IGuiListEntry {
   private final String field_148285_b;
   private final int field_148286_c;

   public GuiKeyBindingList$CategoryEntry(GuiKeyBindingList var1, String var2) {
      super();
      this.field_148287_a = var1;
      this.field_148285_b = I18n.func_135052_a(var2);
      this.field_148286_c = GuiKeyBindingList.access$100(var1).field_71466_p.func_78256_a(this.field_148285_b);
   }

   @Override
   public void func_148279_a(int var1, int var2, int var3, int var4, int var5, Tessellator var6, int var7, int var8, boolean var9) {
      GuiKeyBindingList.access$100(this.field_148287_a)
         .field_71466_p
         .func_78276_b(
            this.field_148285_b,
            GuiKeyBindingList.access$100(this.field_148287_a).field_71462_r.field_146294_l / 2 - this.field_148286_c / 2,
            var3 + var5 - GuiKeyBindingList.access$100(this.field_148287_a).field_71466_p.field_78288_b - 1,
            16777215
         );
   }

   @Override
   public boolean func_148278_a(int var1, int var2, int var3, int var4, int var5, int var6) {
      return false;
   }

   @Override
   public void func_148277_b(int var1, int var2, int var3, int var4, int var5, int var6) {
   }
}
