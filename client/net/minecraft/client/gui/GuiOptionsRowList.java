package net.minecraft.client.gui;

import javax.annotation.Nullable;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;

public class GuiOptionsRowList extends GuiListExtended<GuiOptionsRowList.Row> {
   public GuiOptionsRowList(Minecraft var1, int var2, int var3, int var4, int var5, int var6, GameSettings.Options... var7) {
      super(var1, var2, var3, var4, var5, var6);
      this.field_148163_i = false;
      this.func_195085_a(new GuiOptionsRowList.Row(var2, GameSettings.Options.FULLSCREEN_RESOLUTION));

      for(int var8 = 0; var8 < var7.length; var8 += 2) {
         GameSettings.Options var9 = var7[var8];
         GameSettings.Options var10 = var8 < var7.length - 1 ? var7[var8 + 1] : null;
         this.func_195085_a(new GuiOptionsRowList.Row(var2, var9, var10));
      }

   }

   @Nullable
   private static GuiButton func_195092_b(final Minecraft var0, int var1, int var2, int var3, @Nullable final GameSettings.Options var4) {
      if (var4 == null) {
         return null;
      } else {
         int var5 = var4.func_74381_c();
         return (GuiButton)(var4.func_74380_a() ? new GuiOptionSlider(var5, var1, var2, var3, 20, var4, 0.0D, 1.0D) : new GuiOptionButton(var5, var1, var2, var3, 20, var4, var0.field_71474_y.func_74297_c(var4)) {
            public void func_194829_a(double var1, double var3) {
               var0.field_71474_y.func_74306_a(var4, 1);
               this.field_146126_j = var0.field_71474_y.func_74297_c(GameSettings.Options.func_74379_a(this.field_146127_k));
            }
         });
      }
   }

   public int func_148139_c() {
      return 400;
   }

   protected int func_148137_d() {
      return super.func_148137_d() + 32;
   }

   public final class Row extends GuiListExtended.IGuiListEntry<GuiOptionsRowList.Row> {
      @Nullable
      private final GuiButton field_148323_b;
      @Nullable
      private final GuiButton field_148324_c;

      public Row(GuiButton var2, @Nullable GuiButton var3) {
         super();
         this.field_148323_b = var2;
         this.field_148324_c = var3;
      }

      public Row(int var2, GameSettings.Options var3) {
         this(GuiOptionsRowList.func_195092_b(GuiOptionsRowList.this.field_148161_k, var2 / 2 - 155, 0, 310, var3), (GuiButton)null);
      }

      public Row(int var2, GameSettings.Options var3, GameSettings.Options var4) {
         this(GuiOptionsRowList.func_195092_b(GuiOptionsRowList.this.field_148161_k, var2 / 2 - 155, 0, 150, var3), GuiOptionsRowList.func_195092_b(GuiOptionsRowList.this.field_148161_k, var2 / 2 - 155 + 160, 0, 150, var4));
      }

      public void func_194999_a(int var1, int var2, int var3, int var4, boolean var5, float var6) {
         if (this.field_148323_b != null) {
            this.field_148323_b.field_146129_i = this.func_195001_c();
            this.field_148323_b.func_194828_a(var3, var4, var6);
         }

         if (this.field_148324_c != null) {
            this.field_148324_c.field_146129_i = this.func_195001_c();
            this.field_148324_c.func_194828_a(var3, var4, var6);
         }

      }

      public boolean mouseClicked(double var1, double var3, int var5) {
         if (this.field_148323_b.mouseClicked(var1, var3, var5)) {
            return true;
         } else {
            return this.field_148324_c != null && this.field_148324_c.mouseClicked(var1, var3, var5);
         }
      }

      public boolean mouseReleased(double var1, double var3, int var5) {
         boolean var6 = this.field_148323_b != null && this.field_148323_b.mouseReleased(var1, var3, var5);
         boolean var7 = this.field_148324_c != null && this.field_148324_c.mouseReleased(var1, var3, var5);
         return var6 || var7;
      }

      public void func_195000_a(float var1) {
      }
   }
}
