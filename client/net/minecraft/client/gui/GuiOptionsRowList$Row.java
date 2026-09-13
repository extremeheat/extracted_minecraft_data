package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.settings.GameSettings$Options;

public class GuiOptionsRowList$Row implements GuiListExtended$IGuiListEntry {
   private final Minecraft field_148325_a = Minecraft.func_71410_x();
   private final GuiButton field_148323_b;
   private final GuiButton field_148324_c;

   public GuiOptionsRowList$Row(GuiButton var1, GuiButton var2) {
      super();
      this.field_148323_b = var1;
      this.field_148324_c = var2;
   }

   @Override
   public void func_148279_a(int var1, int var2, int var3, int var4, int var5, Tessellator var6, int var7, int var8, boolean var9) {
      if (this.field_148323_b != null) {
         this.field_148323_b.field_146129_i = var3;
         this.field_148323_b.func_146112_a(this.field_148325_a, var7, var8);
      }

      if (this.field_148324_c != null) {
         this.field_148324_c.field_146129_i = var3;
         this.field_148324_c.func_146112_a(this.field_148325_a, var7, var8);
      }
   }

   @Override
   public boolean func_148278_a(int var1, int var2, int var3, int var4, int var5, int var6) {
      if (this.field_148323_b.func_146116_c(this.field_148325_a, var2, var3)) {
         if (this.field_148323_b instanceof GuiOptionButton) {
            this.field_148325_a.field_71474_y.func_74306_a(((GuiOptionButton)this.field_148323_b).func_146136_c(), 1);
            this.field_148323_b.field_146126_j = this.field_148325_a
               .field_71474_y
               .func_74297_c(GameSettings$Options.func_74379_a(this.field_148323_b.field_146127_k));
         }

         return true;
      } else if (this.field_148324_c != null && this.field_148324_c.func_146116_c(this.field_148325_a, var2, var3)) {
         if (this.field_148324_c instanceof GuiOptionButton) {
            this.field_148325_a.field_71474_y.func_74306_a(((GuiOptionButton)this.field_148324_c).func_146136_c(), 1);
            this.field_148324_c.field_146126_j = this.field_148325_a
               .field_71474_y
               .func_74297_c(GameSettings$Options.func_74379_a(this.field_148324_c.field_146127_k));
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public void func_148277_b(int var1, int var2, int var3, int var4, int var5, int var6) {
      if (this.field_148323_b != null) {
         this.field_148323_b.func_146118_a(var2, var3);
      }

      if (this.field_148324_c != null) {
         this.field_148324_c.func_146118_a(var2, var3);
      }
   }
}
