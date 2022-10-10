package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.ResourcePackListEntryFound;
import net.minecraft.util.text.TextFormatting;

public abstract class GuiResourcePackList extends GuiListExtended<ResourcePackListEntryFound> {
   protected final Minecraft field_148205_k;

   public GuiResourcePackList(Minecraft var1, int var2, int var3) {
      super(var1, var2, var3, 32, var3 - 55 + 4, 36);
      this.field_148205_k = var1;
      this.field_148163_i = false;
      this.func_148133_a(true, (int)((float)var1.field_71466_p.field_78288_b * 1.5F));
   }

   protected void func_148129_a(int var1, int var2, Tessellator var3) {
      String var4 = TextFormatting.UNDERLINE + "" + TextFormatting.BOLD + this.func_148202_k();
      this.field_148205_k.field_71466_p.func_211126_b(var4, (float)(var1 + this.field_148155_a / 2 - this.field_148205_k.field_71466_p.func_78256_a(var4) / 2), (float)Math.min(this.field_148153_b + 3, var2), 16777215);
   }

   protected abstract String func_148202_k();

   public int func_148139_c() {
      return this.field_148155_a;
   }

   protected int func_148137_d() {
      return this.field_148151_d - 6;
   }

   public void func_195095_a(ResourcePackListEntryFound var1) {
      super.func_195085_a(var1);
   }
}
