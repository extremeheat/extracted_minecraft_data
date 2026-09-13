package net.minecraft.client.gui;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumChatFormatting;

public class GuiKeyBindingList$KeyEntry implements GuiListExtended$IGuiListEntry {
   private final KeyBinding field_148282_b;
   private final String field_148283_c;
   private final GuiButton field_148280_d;
   private final GuiButton field_148281_e;

   private GuiKeyBindingList$KeyEntry(GuiKeyBindingList var1, KeyBinding var2) {
      super();
      this.field_148284_a = var1;
      this.field_148282_b = var2;
      this.field_148283_c = I18n.func_135052_a(var2.func_151464_g());
      this.field_148280_d = new GuiButton(0, 0, 0, 75, 18, I18n.func_135052_a(var2.func_151464_g()));
      this.field_148281_e = new GuiButton(0, 0, 0, 50, 18, I18n.func_135052_a("controls.reset"));
   }

   @Override
   public void func_148279_a(int var1, int var2, int var3, int var4, int var5, Tessellator var6, int var7, int var8, boolean var9) {
      boolean var10 = GuiKeyBindingList.access$200(this.field_148284_a).field_146491_f == this.field_148282_b;
      GuiKeyBindingList.access$100(this.field_148284_a)
         .field_71466_p
         .func_78276_b(
            this.field_148283_c,
            var2 + 90 - GuiKeyBindingList.access$300(this.field_148284_a),
            var3 + var5 / 2 - GuiKeyBindingList.access$100(this.field_148284_a).field_71466_p.field_78288_b / 2,
            16777215
         );
      this.field_148281_e.field_146128_h = var2 + 190;
      this.field_148281_e.field_146129_i = var3;
      this.field_148281_e.field_146124_l = this.field_148282_b.func_151463_i() != this.field_148282_b.func_151469_h();
      this.field_148281_e.func_146112_a(GuiKeyBindingList.access$100(this.field_148284_a), var7, var8);
      this.field_148280_d.field_146128_h = var2 + 105;
      this.field_148280_d.field_146129_i = var3;
      this.field_148280_d.field_146126_j = GameSettings.func_74298_c(this.field_148282_b.func_151463_i());
      boolean var11 = false;
      if (this.field_148282_b.func_151463_i() != 0) {
         for(KeyBinding var15 : GuiKeyBindingList.access$100(this.field_148284_a).field_71474_y.field_74324_K) {
            if (var15 != this.field_148282_b && var15.func_151463_i() == this.field_148282_b.func_151463_i()) {
               var11 = true;
               break;
            }
         }
      }

      if (var10) {
         this.field_148280_d.field_146126_j = EnumChatFormatting.WHITE
            + "> "
            + EnumChatFormatting.YELLOW
            + this.field_148280_d.field_146126_j
            + EnumChatFormatting.WHITE
            + " <";
      } else if (var11) {
         this.field_148280_d.field_146126_j = EnumChatFormatting.RED + this.field_148280_d.field_146126_j;
      }

      this.field_148280_d.func_146112_a(GuiKeyBindingList.access$100(this.field_148284_a), var7, var8);
   }

   @Override
   public boolean func_148278_a(int var1, int var2, int var3, int var4, int var5, int var6) {
      if (this.field_148280_d.func_146116_c(GuiKeyBindingList.access$100(this.field_148284_a), var2, var3)) {
         GuiKeyBindingList.access$200(this.field_148284_a).field_146491_f = this.field_148282_b;
         return true;
      } else if (this.field_148281_e.func_146116_c(GuiKeyBindingList.access$100(this.field_148284_a), var2, var3)) {
         GuiKeyBindingList.access$100(this.field_148284_a).field_71474_y.func_151440_a(this.field_148282_b, this.field_148282_b.func_151469_h());
         KeyBinding.func_74508_b();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void func_148277_b(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.field_148280_d.func_146118_a(var2, var3);
      this.field_148281_e.func_146118_a(var2, var3);
   }
}
