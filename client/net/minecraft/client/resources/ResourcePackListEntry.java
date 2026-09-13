package net.minecraft.client.resources;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiListExtended$IGuiListEntry;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public abstract class ResourcePackListEntry implements GuiListExtended$IGuiListEntry {
   private static final ResourceLocation field_148316_c = new ResourceLocation("textures/gui/resource_packs.png");
   protected final Minecraft field_148317_a;
   protected final GuiScreenResourcePacks field_148315_b;

   public ResourcePackListEntry(GuiScreenResourcePacks var1) {
      super();
      this.field_148315_b = var1;
      this.field_148317_a = Minecraft.func_71410_x();
   }

   @Override
   public void func_148279_a(int var1, int var2, int var3, int var4, int var5, Tessellator var6, int var7, int var8, boolean var9) {
      this.func_148313_c();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      Gui.func_146110_a(var2, var3, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
      if ((this.field_148317_a.field_71474_y.field_85185_A || var9) && this.func_148310_d()) {
         this.field_148317_a.func_110434_K().func_110577_a(field_148316_c);
         Gui.func_73734_a(var2, var3, var2 + 32, var3 + 32, -1601138544);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         int var10 = var7 - var2;
         int var11 = var8 - var3;
         if (this.func_148309_e()) {
            if (var10 < 32) {
               Gui.func_146110_a(var2, var3, 0.0F, 32.0F, 32, 32, 256.0F, 256.0F);
            } else {
               Gui.func_146110_a(var2, var3, 0.0F, 0.0F, 32, 32, 256.0F, 256.0F);
            }
         } else {
            if (this.func_148308_f()) {
               if (var10 < 16) {
                  Gui.func_146110_a(var2, var3, 32.0F, 32.0F, 32, 32, 256.0F, 256.0F);
               } else {
                  Gui.func_146110_a(var2, var3, 32.0F, 0.0F, 32, 32, 256.0F, 256.0F);
               }
            }

            if (this.func_148314_g()) {
               if (var10 < 32 && var10 > 16 && var11 < 16) {
                  Gui.func_146110_a(var2, var3, 96.0F, 32.0F, 32, 32, 256.0F, 256.0F);
               } else {
                  Gui.func_146110_a(var2, var3, 96.0F, 0.0F, 32, 32, 256.0F, 256.0F);
               }
            }

            if (this.func_148307_h()) {
               if (var10 < 32 && var10 > 16 && var11 > 16) {
                  Gui.func_146110_a(var2, var3, 64.0F, 32.0F, 32, 32, 256.0F, 256.0F);
               } else {
                  Gui.func_146110_a(var2, var3, 64.0F, 0.0F, 32, 32, 256.0F, 256.0F);
               }
            }
         }
      }

      String var14 = this.func_148312_b();
      int var15 = this.field_148317_a.field_71466_p.func_78256_a(var14);
      if (var15 > 157) {
         var14 = this.field_148317_a.field_71466_p.func_78269_a(var14, 157 - this.field_148317_a.field_71466_p.func_78256_a("...")) + "...";
      }

      this.field_148317_a.field_71466_p.func_78261_a(var14, var2 + 32 + 2, var3 + 1, 16777215);
      List var12 = this.field_148317_a.field_71466_p.func_78271_c(this.func_148311_a(), 157);

      for(int var13 = 0; var13 < 2 && var13 < var12.size(); ++var13) {
         this.field_148317_a.field_71466_p.func_78261_a((String)var12.get(var13), var2 + 32 + 2, var3 + 12 + 10 * var13, 8421504);
      }
   }

   protected abstract String func_148311_a();

   protected abstract String func_148312_b();

   protected abstract void func_148313_c();

   protected boolean func_148310_d() {
      return true;
   }

   protected boolean func_148309_e() {
      return !this.field_148315_b.func_146961_a(this);
   }

   protected boolean func_148308_f() {
      return this.field_148315_b.func_146961_a(this);
   }

   protected boolean func_148314_g() {
      List var1 = this.field_148315_b.func_146962_b(this);
      int var2 = var1.indexOf(this);
      return var2 > 0 && ((ResourcePackListEntry)var1.get(var2 - 1)).func_148310_d();
   }

   protected boolean func_148307_h() {
      List var1 = this.field_148315_b.func_146962_b(this);
      int var2 = var1.indexOf(this);
      return var2 >= 0 && var2 < var1.size() - 1 && ((ResourcePackListEntry)var1.get(var2 + 1)).func_148310_d();
   }

   @Override
   public boolean func_148278_a(int var1, int var2, int var3, int var4, int var5, int var6) {
      if (this.func_148310_d() && var5 <= 32) {
         if (this.func_148309_e()) {
            this.field_148315_b.func_146962_b(this).remove(this);
            this.field_148315_b.func_146963_h().add(0, this);
            return true;
         }

         if (var5 < 16 && this.func_148308_f()) {
            this.field_148315_b.func_146962_b(this).remove(this);
            this.field_148315_b.func_146964_g().add(0, this);
            return true;
         }

         if (var5 > 16 && var6 < 16 && this.func_148314_g()) {
            List var9 = this.field_148315_b.func_146962_b(this);
            int var10 = var9.indexOf(this);
            var9.remove(this);
            var9.add(var10 - 1, this);
            return true;
         }

         if (var5 > 16 && var6 > 16 && this.func_148307_h()) {
            List var7 = this.field_148315_b.func_146962_b(this);
            int var8 = var7.indexOf(this);
            var7.remove(this);
            var7.add(var8 + 1, this);
            return true;
         }
      }

      return false;
   }

   @Override
   public void func_148277_b(int var1, int var2, int var3, int var4, int var5, int var6) {
   }
}
