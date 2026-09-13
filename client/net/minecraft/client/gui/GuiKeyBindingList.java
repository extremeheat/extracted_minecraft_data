package net.minecraft.client.gui;

import java.util.Arrays;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import org.apache.commons.lang3.ArrayUtils;

public class GuiKeyBindingList extends GuiListExtended {
   private final GuiControls field_148191_k;
   private final Minecraft field_148189_l;
   private final GuiListExtended$IGuiListEntry[] field_148190_m;
   private int field_148188_n = 0;

   public GuiKeyBindingList(GuiControls var1, Minecraft var2) {
      super(var2, var1.field_146294_l, var1.field_146295_m, 63, var1.field_146295_m - 32, 20);
      this.field_148191_k = var1;
      this.field_148189_l = var2;
      KeyBinding[] var3 = (KeyBinding[])ArrayUtils.clone(var2.field_71474_y.field_74324_K);
      this.field_148190_m = new GuiListExtended$IGuiListEntry[var3.length + KeyBinding.func_151467_c().size()];
      Arrays.sort((Object[])var3);
      int var4 = 0;
      String var5 = null;

      for(KeyBinding var9 : var3) {
         String var10 = var9.func_151466_e();
         if (!var10.equals(var5)) {
            var5 = var10;
            this.field_148190_m[var4++] = new GuiKeyBindingList$CategoryEntry(this, var10);
         }

         int var11 = var2.field_71466_p.func_78256_a(I18n.func_135052_a(var9.func_151464_g()));
         if (var11 > this.field_148188_n) {
            this.field_148188_n = var11;
         }

         this.field_148190_m[var4++] = new GuiKeyBindingList$KeyEntry(this, var9, null);
      }
   }

   @Override
   protected int func_148127_b() {
      return this.field_148190_m.length;
   }

   @Override
   public GuiListExtended$IGuiListEntry func_148180_b(int var1) {
      return this.field_148190_m[var1];
   }

   @Override
   protected int func_148137_d() {
      return super.func_148137_d() + 15;
   }

   @Override
   public int func_148139_c() {
      return super.func_148139_c() + 32;
   }
}
