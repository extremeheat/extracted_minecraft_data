package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;

public class GuiOptionsRowList extends GuiListExtended {
   private final List<GuiOptionsRowList.Row> field_148184_k = Lists.newArrayList();

   public GuiOptionsRowList(Minecraft var1, int var2, int var3, int var4, int var5, int var6, GameSettings.Options... var7) {
      super(var1, var2, var3, var4, var5, var6);
      this.field_148163_i = false;

      for(int var8 = 0; var8 < var7.length; var8 += 2) {
         GameSettings.Options var9 = var7[var8];
         GameSettings.Options var10 = var8 < var7.length - 1 ? var7[var8 + 1] : null;
         GuiButton var11 = this.func_148182_a(var1, var2 / 2 - 155, 0, var9);
         GuiButton var12 = this.func_148182_a(var1, var2 / 2 - 155 + 160, 0, var10);
         this.field_148184_k.add(new GuiOptionsRowList.Row(var11, var12));
      }

   }

   private GuiButton func_148182_a(Minecraft var1, int var2, int var3, GameSettings.Options var4) {
      if (var4 == null) {
         return null;
      } else {
         int var5 = var4.func_74381_c();
         return (GuiButton)(var4.func_74380_a() ? new GuiOptionSlider(var5, var2, var3, var4) : new GuiOptionButton(var5, var2, var3, var4, var1.field_71474_y.func_74297_c(var4)));
      }
   }

   public GuiOptionsRowList.Row func_148180_b(int var1) {
      return (GuiOptionsRowList.Row)this.field_148184_k.get(var1);
   }

   protected int func_148127_b() {
      return this.field_148184_k.size();
   }

   public int func_148139_c() {
      return 400;
   }

   protected int func_148137_d() {
      return super.func_148137_d() + 32;
   }

   // $FF: synthetic method
   public GuiListExtended.IGuiListEntry func_148180_b(int var1) {
      return this.func_148180_b(var1);
   }

   public static class Row implements GuiListExtended.IGuiListEntry {
      private final Minecraft field_148325_a = Minecraft.func_71410_x();
      private final GuiButton field_148323_b;
      private final GuiButton field_148324_c;

      public Row(GuiButton var1, GuiButton var2) {
         super();
         this.field_148323_b = var1;
         this.field_148324_c = var2;
      }

      public void func_180790_a(int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8) {
         if (this.field_148323_b != null) {
            this.field_148323_b.field_146129_i = var3;
            this.field_148323_b.func_146112_a(this.field_148325_a, var6, var7);
         }

         if (this.field_148324_c != null) {
            this.field_148324_c.field_146129_i = var3;
            this.field_148324_c.func_146112_a(this.field_148325_a, var6, var7);
         }

      }

      public boolean func_148278_a(int var1, int var2, int var3, int var4, int var5, int var6) {
         if (this.field_148323_b.func_146116_c(this.field_148325_a, var2, var3)) {
            if (this.field_148323_b instanceof GuiOptionButton) {
               this.field_148325_a.field_71474_y.func_74306_a(((GuiOptionButton)this.field_148323_b).func_146136_c(), 1);
               this.field_148323_b.field_146126_j = this.field_148325_a.field_71474_y.func_74297_c(GameSettings.Options.func_74379_a(this.field_148323_b.field_146127_k));
            }

            return true;
         } else if (this.field_148324_c != null && this.field_148324_c.func_146116_c(this.field_148325_a, var2, var3)) {
            if (this.field_148324_c instanceof GuiOptionButton) {
               this.field_148325_a.field_71474_y.func_74306_a(((GuiOptionButton)this.field_148324_c).func_146136_c(), 1);
               this.field_148324_c.field_146126_j = this.field_148325_a.field_71474_y.func_74297_c(GameSettings.Options.func_74379_a(this.field_148324_c.field_146127_k));
            }

            return true;
         } else {
            return false;
         }
      }

      public void func_148277_b(int var1, int var2, int var3, int var4, int var5, int var6) {
         if (this.field_148323_b != null) {
            this.field_148323_b.func_146118_a(var2, var3);
         }

         if (this.field_148324_c != null) {
            this.field_148324_c.func_146118_a(var2, var3);
         }

      }

      public void func_178011_a(int var1, int var2, int var3) {
      }
   }
}
