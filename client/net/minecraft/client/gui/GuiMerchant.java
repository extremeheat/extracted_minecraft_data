package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketSelectTrade;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiMerchant extends GuiContainer {
   private static final Logger field_147039_u = LogManager.getLogger();
   private static final ResourceLocation field_147038_v = new ResourceLocation("textures/gui/container/villager.png");
   private final IMerchant field_147037_w;
   private GuiMerchant.MerchantButton field_147043_x;
   private GuiMerchant.MerchantButton field_147042_y;
   private int field_147041_z;
   private final ITextComponent field_147040_A;
   private final InventoryPlayer field_212355_D;

   public GuiMerchant(InventoryPlayer var1, IMerchant var2, World var3) {
      super(new ContainerMerchant(var1, var2, var3));
      this.field_147037_w = var2;
      this.field_147040_A = var2.func_145748_c_();
      this.field_212355_D = var1;
   }

   private void func_195391_j() {
      ((ContainerMerchant)this.field_147002_h).func_75175_c(this.field_147041_z);
      this.field_146297_k.func_147114_u().func_147297_a(new CPacketSelectTrade(this.field_147041_z));
   }

   protected void func_73866_w_() {
      super.func_73866_w_();
      int var1 = (this.field_146294_l - this.field_146999_f) / 2;
      int var2 = (this.field_146295_m - this.field_147000_g) / 2;
      this.field_147043_x = (GuiMerchant.MerchantButton)this.func_189646_b(new GuiMerchant.MerchantButton(1, var1 + 120 + 27, var2 + 24 - 1, true) {
         public void func_194829_a(double var1, double var3) {
            GuiMerchant.this.field_147041_z++;
            MerchantRecipeList var5 = GuiMerchant.this.field_147037_w.func_70934_b(GuiMerchant.this.field_146297_k.field_71439_g);
            if (var5 != null && GuiMerchant.this.field_147041_z >= var5.size()) {
               GuiMerchant.this.field_147041_z = var5.size() - 1;
            }

            GuiMerchant.this.func_195391_j();
         }
      });
      this.field_147042_y = (GuiMerchant.MerchantButton)this.func_189646_b(new GuiMerchant.MerchantButton(2, var1 + 36 - 19, var2 + 24 - 1, false) {
         public void func_194829_a(double var1, double var3) {
            GuiMerchant.this.field_147041_z--;
            if (GuiMerchant.this.field_147041_z < 0) {
               GuiMerchant.this.field_147041_z = 0;
            }

            GuiMerchant.this.func_195391_j();
         }
      });
      this.field_147043_x.field_146124_l = false;
      this.field_147042_y.field_146124_l = false;
   }

   protected void func_146979_b(int var1, int var2) {
      String var3 = this.field_147040_A.func_150254_d();
      this.field_146289_q.func_211126_b(var3, (float)(this.field_146999_f / 2 - this.field_146289_q.func_78256_a(var3) / 2), 6.0F, 4210752);
      this.field_146289_q.func_211126_b(this.field_212355_D.func_145748_c_().func_150254_d(), 8.0F, (float)(this.field_147000_g - 96 + 2), 4210752);
   }

   public void func_73876_c() {
      super.func_73876_c();
      MerchantRecipeList var1 = this.field_147037_w.func_70934_b(this.field_146297_k.field_71439_g);
      if (var1 != null) {
         this.field_147043_x.field_146124_l = this.field_147041_z < var1.size() - 1;
         this.field_147042_y.field_146124_l = this.field_147041_z > 0;
      }

   }

   protected void func_146976_a(float var1, int var2, int var3) {
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      this.field_146297_k.func_110434_K().func_110577_a(field_147038_v);
      int var4 = (this.field_146294_l - this.field_146999_f) / 2;
      int var5 = (this.field_146295_m - this.field_147000_g) / 2;
      this.func_73729_b(var4, var5, 0, 0, this.field_146999_f, this.field_147000_g);
      MerchantRecipeList var6 = this.field_147037_w.func_70934_b(this.field_146297_k.field_71439_g);
      if (var6 != null && !var6.isEmpty()) {
         int var7 = this.field_147041_z;
         if (var7 < 0 || var7 >= var6.size()) {
            return;
         }

         MerchantRecipe var8 = (MerchantRecipe)var6.get(var7);
         if (var8.func_82784_g()) {
            this.field_146297_k.func_110434_K().func_110577_a(field_147038_v);
            GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.func_179140_f();
            this.func_73729_b(this.field_147003_i + 83, this.field_147009_r + 21, 212, 0, 28, 21);
            this.func_73729_b(this.field_147003_i + 83, this.field_147009_r + 51, 212, 0, 28, 21);
         }
      }

   }

   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146276_q_();
      super.func_73863_a(var1, var2, var3);
      MerchantRecipeList var4 = this.field_147037_w.func_70934_b(this.field_146297_k.field_71439_g);
      if (var4 != null && !var4.isEmpty()) {
         int var5 = (this.field_146294_l - this.field_146999_f) / 2;
         int var6 = (this.field_146295_m - this.field_147000_g) / 2;
         int var7 = this.field_147041_z;
         MerchantRecipe var8 = (MerchantRecipe)var4.get(var7);
         ItemStack var9 = var8.func_77394_a();
         ItemStack var10 = var8.func_77396_b();
         ItemStack var11 = var8.func_77397_d();
         GlStateManager.func_179094_E();
         RenderHelper.func_74520_c();
         GlStateManager.func_179140_f();
         GlStateManager.func_179091_B();
         GlStateManager.func_179142_g();
         GlStateManager.func_179145_e();
         this.field_146296_j.field_77023_b = 100.0F;
         this.field_146296_j.func_180450_b(var9, var5 + 36, var6 + 24);
         this.field_146296_j.func_175030_a(this.field_146289_q, var9, var5 + 36, var6 + 24);
         if (!var10.func_190926_b()) {
            this.field_146296_j.func_180450_b(var10, var5 + 62, var6 + 24);
            this.field_146296_j.func_175030_a(this.field_146289_q, var10, var5 + 62, var6 + 24);
         }

         this.field_146296_j.func_180450_b(var11, var5 + 120, var6 + 24);
         this.field_146296_j.func_175030_a(this.field_146289_q, var11, var5 + 120, var6 + 24);
         this.field_146296_j.field_77023_b = 0.0F;
         GlStateManager.func_179140_f();
         if (this.func_195359_a(36, 24, 16, 16, (double)var1, (double)var2) && !var9.func_190926_b()) {
            this.func_146285_a(var9, var1, var2);
         } else if (!var10.func_190926_b() && this.func_195359_a(62, 24, 16, 16, (double)var1, (double)var2) && !var10.func_190926_b()) {
            this.func_146285_a(var10, var1, var2);
         } else if (!var11.func_190926_b() && this.func_195359_a(120, 24, 16, 16, (double)var1, (double)var2) && !var11.func_190926_b()) {
            this.func_146285_a(var11, var1, var2);
         } else if (var8.func_82784_g() && (this.func_195359_a(83, 21, 28, 21, (double)var1, (double)var2) || this.func_195359_a(83, 51, 28, 21, (double)var1, (double)var2))) {
            this.func_146279_a(I18n.func_135052_a("merchant.deprecated"), var1, var2);
         }

         GlStateManager.func_179121_F();
         GlStateManager.func_179145_e();
         GlStateManager.func_179126_j();
         RenderHelper.func_74519_b();
      }

      this.func_191948_b(var1, var2);
   }

   public IMerchant func_147035_g() {
      return this.field_147037_w;
   }

   abstract static class MerchantButton extends GuiButton {
      private final boolean field_146157_o;

      public MerchantButton(int var1, int var2, int var3, boolean var4) {
         super(var1, var2, var3, 12, 19, "");
         this.field_146157_o = var4;
      }

      public void func_194828_a(int var1, int var2, float var3) {
         if (this.field_146125_m) {
            Minecraft.func_71410_x().func_110434_K().func_110577_a(GuiMerchant.field_147038_v);
            GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
            boolean var4 = var1 >= this.field_146128_h && var2 >= this.field_146129_i && var1 < this.field_146128_h + this.field_146120_f && var2 < this.field_146129_i + this.field_146121_g;
            int var5 = 0;
            int var6 = 176;
            if (!this.field_146124_l) {
               var6 += this.field_146120_f * 2;
            } else if (var4) {
               var6 += this.field_146120_f;
            }

            if (!this.field_146157_o) {
               var5 += this.field_146121_g;
            }

            this.func_73729_b(this.field_146128_h, this.field_146129_i, var6, var5, this.field_146120_f, this.field_146121_g);
         }
      }
   }
}
