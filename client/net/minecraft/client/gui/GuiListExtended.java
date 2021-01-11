package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;

public abstract class GuiListExtended extends GuiSlot {
   public GuiListExtended(Minecraft var1, int var2, int var3, int var4, int var5, int var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   protected void func_148144_a(int var1, boolean var2, int var3, int var4) {
   }

   protected boolean func_148131_a(int var1) {
      return false;
   }

   protected void func_148123_a() {
   }

   protected void func_180791_a(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.func_148180_b(var1).func_180790_a(var1, var2, var3, this.func_148139_c(), var4, var5, var6, this.func_148124_c(var5, var6) == var1);
   }

   protected void func_178040_a(int var1, int var2, int var3) {
      this.func_148180_b(var1).func_178011_a(var1, var2, var3);
   }

   public boolean func_148179_a(int var1, int var2, int var3) {
      if (this.func_148141_e(var2)) {
         int var4 = this.func_148124_c(var1, var2);
         if (var4 >= 0) {
            int var5 = this.field_148152_e + this.field_148155_a / 2 - this.func_148139_c() / 2 + 2;
            int var6 = this.field_148153_b + 4 - this.func_148148_g() + var4 * this.field_148149_f + this.field_148160_j;
            int var7 = var1 - var5;
            int var8 = var2 - var6;
            if (this.func_148180_b(var4).func_148278_a(var4, var1, var2, var3, var7, var8)) {
               this.func_148143_b(false);
               return true;
            }
         }
      }

      return false;
   }

   public boolean func_148181_b(int var1, int var2, int var3) {
      for(int var4 = 0; var4 < this.func_148127_b(); ++var4) {
         int var5 = this.field_148152_e + this.field_148155_a / 2 - this.func_148139_c() / 2 + 2;
         int var6 = this.field_148153_b + 4 - this.func_148148_g() + var4 * this.field_148149_f + this.field_148160_j;
         int var7 = var1 - var5;
         int var8 = var2 - var6;
         this.func_148180_b(var4).func_148277_b(var4, var1, var2, var3, var7, var8);
      }

      this.func_148143_b(true);
      return false;
   }

   public abstract GuiListExtended.IGuiListEntry func_148180_b(int var1);

   public interface IGuiListEntry {
      void func_178011_a(int var1, int var2, int var3);

      void func_180790_a(int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8);

      boolean func_148278_a(int var1, int var2, int var3, int var4, int var5, int var6);

      void func_148277_b(int var1, int var2, int var3, int var4, int var5, int var6);
   }
}
