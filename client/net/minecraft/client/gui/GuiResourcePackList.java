package net.minecraft.client.gui;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.ResourcePackListEntry;
import net.minecraft.util.EnumChatFormatting;

public abstract class GuiResourcePackList extends GuiListExtended {
   protected final Minecraft field_148205_k;
   protected final List<ResourcePackListEntry> field_148204_l;

   public GuiResourcePackList(Minecraft var1, int var2, int var3, List<ResourcePackListEntry> var4) {
      super(var1, var2, var3, 32, var3 - 55 + 4, 36);
      this.field_148205_k = var1;
      this.field_148204_l = var4;
      this.field_148163_i = false;
      this.func_148133_a(true, (int)((float)var1.field_71466_p.field_78288_b * 1.5F));
   }

   protected void func_148129_a(int var1, int var2, Tessellator var3) {
      String var4 = EnumChatFormatting.UNDERLINE + "" + EnumChatFormatting.BOLD + this.func_148202_k();
      this.field_148205_k.field_71466_p.func_78276_b(var4, var1 + this.field_148155_a / 2 - this.field_148205_k.field_71466_p.func_78256_a(var4) / 2, Math.min(this.field_148153_b + 3, var2), 16777215);
   }

   protected abstract String func_148202_k();

   public List<ResourcePackListEntry> func_148201_l() {
      return this.field_148204_l;
   }

   protected int func_148127_b() {
      return this.func_148201_l().size();
   }

   public ResourcePackListEntry func_148180_b(int var1) {
      return (ResourcePackListEntry)this.func_148201_l().get(var1);
   }

   public int func_148139_c() {
      return this.field_148155_a;
   }

   protected int func_148137_d() {
      return this.field_148151_d - 6;
   }

   // $FF: synthetic method
   public GuiListExtended.IGuiListEntry func_148180_b(int var1) {
      return this.func_148180_b(var1);
   }
}
