package net.minecraft.client.resources;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiResourcePackSelected;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.resources.PackCompatibility;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class ResourcePackListEntryFound extends GuiListExtended.IGuiListEntry<ResourcePackListEntryFound> {
   private static final ResourceLocation field_195028_e = new ResourceLocation("textures/gui/resource_packs.png");
   private static final ITextComponent field_195029_f = new TextComponentTranslation("resourcePack.incompatible", new Object[0]);
   private static final ITextComponent field_195030_g = new TextComponentTranslation("resourcePack.incompatible.confirm.title", new Object[0]);
   protected final Minecraft field_195026_c;
   protected final GuiScreenResourcePacks field_195027_d;
   private final ResourcePackInfoClient field_148319_c;

   public ResourcePackListEntryFound(GuiScreenResourcePacks var1, ResourcePackInfoClient var2) {
      super();
      this.field_195027_d = var1;
      this.field_195026_c = Minecraft.func_71410_x();
      this.field_148319_c = var2;
   }

   public void func_195020_a(GuiResourcePackSelected var1) {
      this.func_195017_i().func_195792_i().func_198993_a(var1.func_195074_b(), this, ResourcePackListEntryFound::func_195017_i, true);
   }

   protected void func_148313_c() {
      this.field_148319_c.func_195808_a(this.field_195026_c.func_110434_K());
   }

   protected PackCompatibility func_195019_f() {
      return this.field_148319_c.func_195791_d();
   }

   protected String func_148311_a() {
      return this.field_148319_c.func_195795_c().func_150254_d();
   }

   protected String func_148312_b() {
      return this.field_148319_c.func_195789_b().func_150254_d();
   }

   public ResourcePackInfoClient func_195017_i() {
      return this.field_148319_c;
   }

   public void func_194999_a(int var1, int var2, int var3, int var4, boolean var5, float var6) {
      int var7 = this.func_195001_c();
      int var8 = this.func_195002_d();
      PackCompatibility var9 = this.func_195019_f();
      if (!var9.func_198968_a()) {
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         Gui.func_73734_a(var8 - 1, var7 - 1, var8 + var1 - 9, var7 + var2 + 1, -8978432);
      }

      this.func_148313_c();
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      Gui.func_146110_a(var8, var7, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
      String var10 = this.func_148312_b();
      String var11 = this.func_148311_a();
      int var12;
      if (this.func_195024_j() && (this.field_195026_c.field_71474_y.field_85185_A || var5)) {
         this.field_195026_c.func_110434_K().func_110577_a(field_195028_e);
         Gui.func_73734_a(var8, var7, var8 + 32, var7 + 32, -1601138544);
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         var12 = var3 - var8;
         int var13 = var4 - var7;
         if (!var9.func_198968_a()) {
            var10 = field_195029_f.func_150254_d();
            var11 = var9.func_198967_b().func_150254_d();
         }

         if (this.func_195025_k()) {
            if (var12 < 32) {
               Gui.func_146110_a(var8, var7, 0.0F, 32.0F, 32, 32, 256.0F, 256.0F);
            } else {
               Gui.func_146110_a(var8, var7, 0.0F, 0.0F, 32, 32, 256.0F, 256.0F);
            }
         } else {
            if (this.func_195022_l()) {
               if (var12 < 16) {
                  Gui.func_146110_a(var8, var7, 32.0F, 32.0F, 32, 32, 256.0F, 256.0F);
               } else {
                  Gui.func_146110_a(var8, var7, 32.0F, 0.0F, 32, 32, 256.0F, 256.0F);
               }
            }

            if (this.func_195023_m()) {
               if (var12 < 32 && var12 > 16 && var13 < 16) {
                  Gui.func_146110_a(var8, var7, 96.0F, 32.0F, 32, 32, 256.0F, 256.0F);
               } else {
                  Gui.func_146110_a(var8, var7, 96.0F, 0.0F, 32, 32, 256.0F, 256.0F);
               }
            }

            if (this.func_195021_n()) {
               if (var12 < 32 && var12 > 16 && var13 > 16) {
                  Gui.func_146110_a(var8, var7, 64.0F, 32.0F, 32, 32, 256.0F, 256.0F);
               } else {
                  Gui.func_146110_a(var8, var7, 64.0F, 0.0F, 32, 32, 256.0F, 256.0F);
               }
            }
         }
      }

      var12 = this.field_195026_c.field_71466_p.func_78256_a(var10);
      if (var12 > 157) {
         var10 = this.field_195026_c.field_71466_p.func_78269_a(var10, 157 - this.field_195026_c.field_71466_p.func_78256_a("...")) + "...";
      }

      this.field_195026_c.field_71466_p.func_175063_a(var10, (float)(var8 + 32 + 2), (float)(var7 + 1), 16777215);
      List var15 = this.field_195026_c.field_71466_p.func_78271_c(var11, 157);

      for(int var14 = 0; var14 < 2 && var14 < var15.size(); ++var14) {
         this.field_195026_c.field_71466_p.func_175063_a((String)var15.get(var14), (float)(var8 + 32 + 2), (float)(var7 + 12 + 10 * var14), 8421504);
      }

   }

   protected boolean func_195024_j() {
      return !this.field_148319_c.func_195798_h() || !this.field_148319_c.func_195797_g();
   }

   protected boolean func_195025_k() {
      return !this.field_195027_d.func_195312_c(this);
   }

   protected boolean func_195022_l() {
      return this.field_195027_d.func_195312_c(this) && !this.field_148319_c.func_195797_g();
   }

   protected boolean func_195023_m() {
      List var1 = this.func_194998_a().func_195074_b();
      int var2 = var1.indexOf(this);
      return var2 > 0 && !((ResourcePackListEntryFound)var1.get(var2 - 1)).field_148319_c.func_195798_h();
   }

   protected boolean func_195021_n() {
      List var1 = this.func_194998_a().func_195074_b();
      int var2 = var1.indexOf(this);
      return var2 >= 0 && var2 < var1.size() - 1 && !((ResourcePackListEntryFound)var1.get(var2 + 1)).field_148319_c.func_195798_h();
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      double var6 = var1 - (double)this.func_195002_d();
      double var8 = var3 - (double)this.func_195001_c();
      if (this.func_195024_j() && var6 <= 32.0D) {
         if (this.func_195025_k()) {
            this.func_195018_o().func_175288_g();
            PackCompatibility var13 = this.func_195019_f();
            if (var13.func_198968_a()) {
               this.func_195018_o().func_195301_a(this);
            } else {
               String var14 = field_195030_g.func_150254_d();
               String var12 = var13.func_198971_c().func_150254_d();
               this.field_195026_c.func_147108_a(new GuiYesNo((var1x, var2) -> {
                  this.field_195026_c.func_147108_a(this.func_195018_o());
                  if (var1x) {
                     this.func_195018_o().func_195301_a(this);
                  }

               }, var14, var12, 0));
            }

            return true;
         }

         if (var6 < 16.0D && this.func_195022_l()) {
            this.func_195018_o().func_195305_b(this);
            return true;
         }

         List var10;
         int var11;
         if (var6 > 16.0D && var8 < 16.0D && this.func_195023_m()) {
            var10 = this.func_194998_a().func_195074_b();
            var11 = var10.indexOf(this);
            var10.remove(this);
            var10.add(var11 - 1, this);
            this.func_195018_o().func_175288_g();
            return true;
         }

         if (var6 > 16.0D && var8 > 16.0D && this.func_195021_n()) {
            var10 = this.func_194998_a().func_195074_b();
            var11 = var10.indexOf(this);
            var10.remove(this);
            var10.add(var11 + 1, this);
            this.func_195018_o().func_175288_g();
            return true;
         }
      }

      return false;
   }

   public GuiScreenResourcePacks func_195018_o() {
      return this.field_195027_d;
   }
}
