package net.minecraft.client.gui;

import java.util.Arrays;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.ArrayUtils;

public class GuiKeyBindingList extends GuiListExtended<GuiKeyBindingList.Entry> {
   private final GuiControls field_148191_k;
   private final Minecraft field_148189_l;
   private int field_148188_n;

   public GuiKeyBindingList(GuiControls var1, Minecraft var2) {
      super(var2, var1.field_146294_l + 45, var1.field_146295_m, 63, var1.field_146295_m - 32, 20);
      this.field_148191_k = var1;
      this.field_148189_l = var2;
      KeyBinding[] var3 = (KeyBinding[])ArrayUtils.clone(var2.field_71474_y.field_74324_K);
      Arrays.sort(var3);
      String var4 = null;
      KeyBinding[] var5 = var3;
      int var6 = var3.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         KeyBinding var8 = var5[var7];
         String var9 = var8.func_151466_e();
         if (!var9.equals(var4)) {
            var4 = var9;
            this.func_195085_a(new GuiKeyBindingList.CategoryEntry(var9));
         }

         int var10 = var2.field_71466_p.func_78256_a(I18n.func_135052_a(var8.func_151464_g()));
         if (var10 > this.field_148188_n) {
            this.field_148188_n = var10;
         }

         this.func_195085_a(new GuiKeyBindingList.KeyEntry(var8));
      }

   }

   protected int func_148137_d() {
      return super.func_148137_d() + 15;
   }

   public int func_148139_c() {
      return super.func_148139_c() + 32;
   }

   public class KeyEntry extends GuiKeyBindingList.Entry {
      private final KeyBinding field_148282_b;
      private final String field_148283_c;
      private final GuiButton field_148280_d;
      private final GuiButton field_148281_e;

      private KeyEntry(final KeyBinding var2) {
         super();
         this.field_148282_b = var2;
         this.field_148283_c = I18n.func_135052_a(var2.func_151464_g());
         this.field_148280_d = new GuiButton(0, 0, 0, 75, 20, I18n.func_135052_a(var2.func_151464_g())) {
            public void func_194829_a(double var1, double var3) {
               GuiKeyBindingList.this.field_148191_k.field_146491_f = var2;
            }
         };
         this.field_148281_e = new GuiButton(0, 0, 0, 50, 20, I18n.func_135052_a("controls.reset")) {
            public void func_194829_a(double var1, double var3) {
               GuiKeyBindingList.this.field_148189_l.field_71474_y.func_198014_a(var2, var2.func_197977_i());
               KeyBinding.func_74508_b();
            }
         };
      }

      public void func_194999_a(int var1, int var2, int var3, int var4, boolean var5, float var6) {
         int var7 = this.func_195001_c();
         int var8 = this.func_195002_d();
         boolean var9 = GuiKeyBindingList.this.field_148191_k.field_146491_f == this.field_148282_b;
         GuiKeyBindingList.this.field_148189_l.field_71466_p.func_211126_b(this.field_148283_c, (float)(var8 + 90 - GuiKeyBindingList.this.field_148188_n), (float)(var7 + var2 / 2 - GuiKeyBindingList.this.field_148189_l.field_71466_p.field_78288_b / 2), 16777215);
         this.field_148281_e.field_146128_h = var8 + 190;
         this.field_148281_e.field_146129_i = var7;
         this.field_148281_e.field_146124_l = !this.field_148282_b.func_197985_l();
         this.field_148281_e.func_194828_a(var3, var4, var6);
         this.field_148280_d.field_146128_h = var8 + 105;
         this.field_148280_d.field_146129_i = var7;
         this.field_148280_d.field_146126_j = this.field_148282_b.func_197978_k();
         boolean var10 = false;
         if (!this.field_148282_b.func_197986_j()) {
            KeyBinding[] var11 = GuiKeyBindingList.this.field_148189_l.field_71474_y.field_74324_K;
            int var12 = var11.length;

            for(int var13 = 0; var13 < var12; ++var13) {
               KeyBinding var14 = var11[var13];
               if (var14 != this.field_148282_b && this.field_148282_b.func_197983_b(var14)) {
                  var10 = true;
                  break;
               }
            }
         }

         if (var9) {
            this.field_148280_d.field_146126_j = TextFormatting.WHITE + "> " + TextFormatting.YELLOW + this.field_148280_d.field_146126_j + TextFormatting.WHITE + " <";
         } else if (var10) {
            this.field_148280_d.field_146126_j = TextFormatting.RED + this.field_148280_d.field_146126_j;
         }

         this.field_148280_d.func_194828_a(var3, var4, var6);
      }

      public boolean mouseClicked(double var1, double var3, int var5) {
         if (this.field_148280_d.mouseClicked(var1, var3, var5)) {
            return true;
         } else {
            return this.field_148281_e.mouseClicked(var1, var3, var5);
         }
      }

      public boolean mouseReleased(double var1, double var3, int var5) {
         return this.field_148280_d.mouseReleased(var1, var3, var5) || this.field_148281_e.mouseReleased(var1, var3, var5);
      }

      // $FF: synthetic method
      KeyEntry(KeyBinding var2, Object var3) {
         this(var2);
      }
   }

   public class CategoryEntry extends GuiKeyBindingList.Entry {
      private final String field_148285_b;
      private final int field_148286_c;

      public CategoryEntry(String var2) {
         super();
         this.field_148285_b = I18n.func_135052_a(var2);
         this.field_148286_c = GuiKeyBindingList.this.field_148189_l.field_71466_p.func_78256_a(this.field_148285_b);
      }

      public void func_194999_a(int var1, int var2, int var3, int var4, boolean var5, float var6) {
         GuiKeyBindingList.this.field_148189_l.field_71466_p.func_211126_b(this.field_148285_b, (float)(GuiKeyBindingList.this.field_148189_l.field_71462_r.field_146294_l / 2 - this.field_148286_c / 2), (float)(this.func_195001_c() + var2 - GuiKeyBindingList.this.field_148189_l.field_71466_p.field_78288_b - 1), 16777215);
      }
   }

   public abstract static class Entry extends GuiListExtended.IGuiListEntry<GuiKeyBindingList.Entry> {
      public Entry() {
         super();
      }
   }
}
