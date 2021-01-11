package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class ServerListEntryLanScan implements GuiListExtended.IGuiListEntry {
   private final Minecraft field_148288_a = Minecraft.func_71410_x();

   public ServerListEntryLanScan() {
      super();
   }

   public void func_180790_a(int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8) {
      int var9 = var3 + var5 / 2 - this.field_148288_a.field_71466_p.field_78288_b / 2;
      this.field_148288_a.field_71466_p.func_78276_b(I18n.func_135052_a("lanServer.scanning"), this.field_148288_a.field_71462_r.field_146294_l / 2 - this.field_148288_a.field_71466_p.func_78256_a(I18n.func_135052_a("lanServer.scanning")) / 2, var9, 16777215);
      String var10;
      switch((int)(Minecraft.func_71386_F() / 300L % 4L)) {
      case 0:
      default:
         var10 = "O o o";
         break;
      case 1:
      case 3:
         var10 = "o O o";
         break;
      case 2:
         var10 = "o o O";
      }

      this.field_148288_a.field_71466_p.func_78276_b(var10, this.field_148288_a.field_71462_r.field_146294_l / 2 - this.field_148288_a.field_71466_p.func_78256_a(var10) / 2, var9 + this.field_148288_a.field_71466_p.field_78288_b, 8421504);
   }

   public void func_178011_a(int var1, int var2, int var3) {
   }

   public boolean func_148278_a(int var1, int var2, int var3, int var4, int var5, int var6) {
      return false;
   }

   public void func_148277_b(int var1, int var2, int var3, int var4, int var5, int var6) {
   }
}
