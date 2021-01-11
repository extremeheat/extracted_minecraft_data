package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.LanServerDetector;
import net.minecraft.client.resources.I18n;

public class ServerListEntryLanDetected implements GuiListExtended.IGuiListEntry {
   private final GuiMultiplayer field_148292_c;
   protected final Minecraft field_148293_a;
   protected final LanServerDetector.LanServer field_148291_b;
   private long field_148290_d = 0L;

   protected ServerListEntryLanDetected(GuiMultiplayer var1, LanServerDetector.LanServer var2) {
      super();
      this.field_148292_c = var1;
      this.field_148291_b = var2;
      this.field_148293_a = Minecraft.func_71410_x();
   }

   public void func_180790_a(int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8) {
      this.field_148293_a.field_71466_p.func_78276_b(I18n.func_135052_a("lanServer.title"), var2 + 32 + 3, var3 + 1, 16777215);
      this.field_148293_a.field_71466_p.func_78276_b(this.field_148291_b.func_77487_a(), var2 + 32 + 3, var3 + 12, 8421504);
      if (this.field_148293_a.field_71474_y.field_80005_w) {
         this.field_148293_a.field_71466_p.func_78276_b(I18n.func_135052_a("selectServer.hiddenAddress"), var2 + 32 + 3, var3 + 12 + 11, 3158064);
      } else {
         this.field_148293_a.field_71466_p.func_78276_b(this.field_148291_b.func_77488_b(), var2 + 32 + 3, var3 + 12 + 11, 3158064);
      }

   }

   public boolean func_148278_a(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.field_148292_c.func_146790_a(var1);
      if (Minecraft.func_71386_F() - this.field_148290_d < 250L) {
         this.field_148292_c.func_146796_h();
      }

      this.field_148290_d = Minecraft.func_71386_F();
      return false;
   }

   public void func_178011_a(int var1, int var2, int var3) {
   }

   public void func_148277_b(int var1, int var2, int var3, int var4, int var5, int var6) {
   }

   public LanServerDetector.LanServer func_148289_a() {
      return this.field_148291_b;
   }
}
