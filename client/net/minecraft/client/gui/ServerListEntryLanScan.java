package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.Util;

public class ServerListEntryLanScan extends ServerSelectionList.Entry {
   private final Minecraft field_148288_a = Minecraft.func_71410_x();

   public ServerListEntryLanScan() {
      super();
   }

   public void func_194999_a(int var1, int var2, int var3, int var4, boolean var5, float var6) {
      int var7 = this.func_195001_c() + var2 / 2 - this.field_148288_a.field_71466_p.field_78288_b / 2;
      this.field_148288_a.field_71466_p.func_211126_b(I18n.func_135052_a("lanServer.scanning"), (float)(this.field_148288_a.field_71462_r.field_146294_l / 2 - this.field_148288_a.field_71466_p.func_78256_a(I18n.func_135052_a("lanServer.scanning")) / 2), (float)var7, 16777215);
      String var8;
      switch((int)(Util.func_211177_b() / 300L % 4L)) {
      case 0:
      default:
         var8 = "O o o";
         break;
      case 1:
      case 3:
         var8 = "o O o";
         break;
      case 2:
         var8 = "o o O";
      }

      this.field_148288_a.field_71466_p.func_211126_b(var8, (float)(this.field_148288_a.field_71462_r.field_146294_l / 2 - this.field_148288_a.field_71466_p.func_78256_a(var8) / 2), (float)(var7 + this.field_148288_a.field_71466_p.field_78288_b), 8421504);
   }
}
