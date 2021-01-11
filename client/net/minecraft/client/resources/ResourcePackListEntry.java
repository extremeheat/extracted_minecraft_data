package net.minecraft.client.resources;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;

public abstract class ResourcePackListEntry implements GuiListExtended.IGuiListEntry {
   private static final ResourceLocation field_148316_c = new ResourceLocation("textures/gui/resource_packs.png");
   private static final IChatComponent field_183020_d = new ChatComponentTranslation("resourcePack.incompatible", new Object[0]);
   private static final IChatComponent field_183021_e = new ChatComponentTranslation("resourcePack.incompatible.old", new Object[0]);
   private static final IChatComponent field_183022_f = new ChatComponentTranslation("resourcePack.incompatible.new", new Object[0]);
   protected final Minecraft field_148317_a;
   protected final GuiScreenResourcePacks field_148315_b;

   public ResourcePackListEntry(GuiScreenResourcePacks var1) {
      super();
      this.field_148315_b = var1;
      this.field_148317_a = Minecraft.func_71410_x();
   }

   public void func_180790_a(int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8) {
      int var9 = this.func_183019_a();
      if (var9 != 1) {
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         Gui.func_73734_a(var2 - 1, var3 - 1, var2 + var4 - 9, var3 + var5 + 1, -8978432);
      }

      this.func_148313_c();
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      Gui.func_146110_a(var2, var3, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
      String var10 = this.func_148312_b();
      String var11 = this.func_148311_a();
      int var12;
      if ((this.field_148317_a.field_71474_y.field_85185_A || var8) && this.func_148310_d()) {
         this.field_148317_a.func_110434_K().func_110577_a(field_148316_c);
         Gui.func_73734_a(var2, var3, var2 + 32, var3 + 32, -1601138544);
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         var12 = var6 - var2;
         int var13 = var7 - var3;
         if (var9 < 1) {
            var10 = field_183020_d.func_150254_d();
            var11 = field_183021_e.func_150254_d();
         } else if (var9 > 1) {
            var10 = field_183020_d.func_150254_d();
            var11 = field_183022_f.func_150254_d();
         }

         if (this.func_148309_e()) {
            if (var12 < 32) {
               Gui.func_146110_a(var2, var3, 0.0F, 32.0F, 32, 32, 256.0F, 256.0F);
            } else {
               Gui.func_146110_a(var2, var3, 0.0F, 0.0F, 32, 32, 256.0F, 256.0F);
            }
         } else {
            if (this.func_148308_f()) {
               if (var12 < 16) {
                  Gui.func_146110_a(var2, var3, 32.0F, 32.0F, 32, 32, 256.0F, 256.0F);
               } else {
                  Gui.func_146110_a(var2, var3, 32.0F, 0.0F, 32, 32, 256.0F, 256.0F);
               }
            }

            if (this.func_148314_g()) {
               if (var12 < 32 && var12 > 16 && var13 < 16) {
                  Gui.func_146110_a(var2, var3, 96.0F, 32.0F, 32, 32, 256.0F, 256.0F);
               } else {
                  Gui.func_146110_a(var2, var3, 96.0F, 0.0F, 32, 32, 256.0F, 256.0F);
               }
            }

            if (this.func_148307_h()) {
               if (var12 < 32 && var12 > 16 && var13 > 16) {
                  Gui.func_146110_a(var2, var3, 64.0F, 32.0F, 32, 32, 256.0F, 256.0F);
               } else {
                  Gui.func_146110_a(var2, var3, 64.0F, 0.0F, 32, 32, 256.0F, 256.0F);
               }
            }
         }
      }

      var12 = this.field_148317_a.field_71466_p.func_78256_a(var10);
      if (var12 > 157) {
         var10 = this.field_148317_a.field_71466_p.func_78269_a(var10, 157 - this.field_148317_a.field_71466_p.func_78256_a("...")) + "...";
      }

      this.field_148317_a.field_71466_p.func_175063_a(var10, (float)(var2 + 32 + 2), (float)(var3 + 1), 16777215);
      List var15 = this.field_148317_a.field_71466_p.func_78271_c(var11, 157);

      for(int var14 = 0; var14 < 2 && var14 < var15.size(); ++var14) {
         this.field_148317_a.field_71466_p.func_175063_a((String)var15.get(var14), (float)(var2 + 32 + 2), (float)(var3 + 12 + 10 * var14), 8421504);
      }

   }

   protected abstract int func_183019_a();

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

   public boolean func_148278_a(int var1, int var2, int var3, int var4, int var5, int var6) {
      if (this.func_148310_d() && var5 <= 32) {
         if (this.func_148309_e()) {
            this.field_148315_b.func_175288_g();
            int var10 = this.func_183019_a();
            if (var10 != 1) {
               String var11 = I18n.func_135052_a("resourcePack.incompatible.confirm.title");
               String var9 = I18n.func_135052_a("resourcePack.incompatible.confirm." + (var10 > 1 ? "new" : "old"));
               this.field_148317_a.func_147108_a(new GuiYesNo(new GuiYesNoCallback() {
                  public void func_73878_a(boolean var1, int var2) {
                     List var3 = ResourcePackListEntry.this.field_148315_b.func_146962_b(ResourcePackListEntry.this);
                     ResourcePackListEntry.this.field_148317_a.func_147108_a(ResourcePackListEntry.this.field_148315_b);
                     if (var1) {
                        var3.remove(ResourcePackListEntry.this);
                        ResourcePackListEntry.this.field_148315_b.func_146963_h().add(0, ResourcePackListEntry.this);
                     }

                  }
               }, var11, var9, 0));
            } else {
               this.field_148315_b.func_146962_b(this).remove(this);
               this.field_148315_b.func_146963_h().add(0, this);
            }

            return true;
         }

         if (var5 < 16 && this.func_148308_f()) {
            this.field_148315_b.func_146962_b(this).remove(this);
            this.field_148315_b.func_146964_g().add(0, this);
            this.field_148315_b.func_175288_g();
            return true;
         }

         List var7;
         int var8;
         if (var5 > 16 && var6 < 16 && this.func_148314_g()) {
            var7 = this.field_148315_b.func_146962_b(this);
            var8 = var7.indexOf(this);
            var7.remove(this);
            var7.add(var8 - 1, this);
            this.field_148315_b.func_175288_g();
            return true;
         }

         if (var5 > 16 && var6 > 16 && this.func_148307_h()) {
            var7 = this.field_148315_b.func_146962_b(this);
            var8 = var7.indexOf(this);
            var7.remove(this);
            var7.add(var8 + 1, this);
            this.field_148315_b.func_175288_g();
            return true;
         }
      }

      return false;
   }

   public void func_178011_a(int var1, int var2, int var3) {
   }

   public void func_148277_b(int var1, int var2, int var3, int var4, int var5, int var6) {
   }
}
