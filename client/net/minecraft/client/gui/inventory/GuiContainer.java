package net.minecraft.client.gui.inventory;

import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;

public abstract class GuiContainer extends GuiScreen {
   public static final ResourceLocation field_147001_a = new ResourceLocation("textures/gui/container/inventory.png");
   protected int field_146999_f = 176;
   protected int field_147000_g = 166;
   public Container field_147002_h;
   protected int field_147003_i;
   protected int field_147009_r;
   protected Slot field_147006_u;
   private Slot field_147005_v;
   private boolean field_147004_w;
   private ItemStack field_147012_x;
   private int field_147011_y;
   private int field_147010_z;
   private Slot field_146989_A;
   private long field_146990_B;
   private ItemStack field_146991_C;
   private Slot field_146985_D;
   private long field_146986_E;
   protected final Set<Slot> field_147008_s;
   protected boolean field_147007_t;
   private int field_146987_F;
   private int field_146988_G;
   private boolean field_146995_H;
   private int field_146996_I;
   private long field_146997_J;
   private Slot field_146998_K;
   private int field_146992_L;
   private boolean field_146993_M;
   private ItemStack field_146994_N;

   public GuiContainer(Container var1) {
      super();
      this.field_147012_x = ItemStack.field_190927_a;
      this.field_146991_C = ItemStack.field_190927_a;
      this.field_147008_s = Sets.newHashSet();
      this.field_146994_N = ItemStack.field_190927_a;
      this.field_147002_h = var1;
      this.field_146995_H = true;
   }

   protected void func_73866_w_() {
      super.func_73866_w_();
      this.field_146297_k.field_71439_g.field_71070_bA = this.field_147002_h;
      this.field_147003_i = (this.field_146294_l - this.field_146999_f) / 2;
      this.field_147009_r = (this.field_146295_m - this.field_147000_g) / 2;
   }

   public void func_73863_a(int var1, int var2, float var3) {
      int var4 = this.field_147003_i;
      int var5 = this.field_147009_r;
      this.func_146976_a(var3, var1, var2);
      GlStateManager.func_179101_C();
      RenderHelper.func_74518_a();
      GlStateManager.func_179140_f();
      GlStateManager.func_179097_i();
      super.func_73863_a(var1, var2, var3);
      RenderHelper.func_74520_c();
      GlStateManager.func_179094_E();
      GlStateManager.func_179109_b((float)var4, (float)var5, 0.0F);
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.func_179091_B();
      this.field_147006_u = null;
      boolean var6 = true;
      boolean var7 = true;
      OpenGlHelper.func_77475_a(OpenGlHelper.field_77476_b, 240.0F, 240.0F);
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);

      int var11;
      for(int var8 = 0; var8 < this.field_147002_h.field_75151_b.size(); ++var8) {
         Slot var9 = (Slot)this.field_147002_h.field_75151_b.get(var8);
         if (var9.func_111238_b()) {
            this.func_146977_a(var9);
         }

         if (this.func_195362_a(var9, (double)var1, (double)var2) && var9.func_111238_b()) {
            this.field_147006_u = var9;
            GlStateManager.func_179140_f();
            GlStateManager.func_179097_i();
            int var10 = var9.field_75223_e;
            var11 = var9.field_75221_f;
            GlStateManager.func_179135_a(true, true, true, false);
            this.func_73733_a(var10, var11, var10 + 16, var11 + 16, -2130706433, -2130706433);
            GlStateManager.func_179135_a(true, true, true, true);
            GlStateManager.func_179145_e();
            GlStateManager.func_179126_j();
         }
      }

      RenderHelper.func_74518_a();
      this.func_146979_b(var1, var2);
      RenderHelper.func_74520_c();
      InventoryPlayer var15 = this.field_146297_k.field_71439_g.field_71071_by;
      ItemStack var16 = this.field_147012_x.func_190926_b() ? var15.func_70445_o() : this.field_147012_x;
      if (!var16.func_190926_b()) {
         boolean var17 = true;
         var11 = this.field_147012_x.func_190926_b() ? 8 : 16;
         String var12 = null;
         if (!this.field_147012_x.func_190926_b() && this.field_147004_w) {
            var16 = var16.func_77946_l();
            var16.func_190920_e(MathHelper.func_76123_f((float)var16.func_190916_E() / 2.0F));
         } else if (this.field_147007_t && this.field_147008_s.size() > 1) {
            var16 = var16.func_77946_l();
            var16.func_190920_e(this.field_146996_I);
            if (var16.func_190926_b()) {
               var12 = "" + TextFormatting.YELLOW + "0";
            }
         }

         this.func_146982_a(var16, var1 - var4 - 8, var2 - var5 - var11, var12);
      }

      if (!this.field_146991_C.func_190926_b()) {
         float var18 = (float)(Util.func_211177_b() - this.field_146990_B) / 100.0F;
         if (var18 >= 1.0F) {
            var18 = 1.0F;
            this.field_146991_C = ItemStack.field_190927_a;
         }

         var11 = this.field_146989_A.field_75223_e - this.field_147011_y;
         int var19 = this.field_146989_A.field_75221_f - this.field_147010_z;
         int var13 = this.field_147011_y + (int)((float)var11 * var18);
         int var14 = this.field_147010_z + (int)((float)var19 * var18);
         this.func_146982_a(this.field_146991_C, var13, var14, (String)null);
      }

      GlStateManager.func_179121_F();
      GlStateManager.func_179145_e();
      GlStateManager.func_179126_j();
      RenderHelper.func_74519_b();
   }

   protected void func_191948_b(int var1, int var2) {
      if (this.field_146297_k.field_71439_g.field_71071_by.func_70445_o().func_190926_b() && this.field_147006_u != null && this.field_147006_u.func_75216_d()) {
         this.func_146285_a(this.field_147006_u.func_75211_c(), var1, var2);
      }

   }

   private void func_146982_a(ItemStack var1, int var2, int var3, String var4) {
      GlStateManager.func_179109_b(0.0F, 0.0F, 32.0F);
      this.field_73735_i = 200.0F;
      this.field_146296_j.field_77023_b = 200.0F;
      this.field_146296_j.func_180450_b(var1, var2, var3);
      this.field_146296_j.func_180453_a(this.field_146289_q, var1, var2, var3 - (this.field_147012_x.func_190926_b() ? 0 : 8), var4);
      this.field_73735_i = 0.0F;
      this.field_146296_j.field_77023_b = 0.0F;
   }

   protected void func_146979_b(int var1, int var2) {
   }

   protected abstract void func_146976_a(float var1, int var2, int var3);

   private void func_146977_a(Slot var1) {
      int var2 = var1.field_75223_e;
      int var3 = var1.field_75221_f;
      ItemStack var4 = var1.func_75211_c();
      boolean var5 = false;
      boolean var6 = var1 == this.field_147005_v && !this.field_147012_x.func_190926_b() && !this.field_147004_w;
      ItemStack var7 = this.field_146297_k.field_71439_g.field_71071_by.func_70445_o();
      String var8 = null;
      if (var1 == this.field_147005_v && !this.field_147012_x.func_190926_b() && this.field_147004_w && !var4.func_190926_b()) {
         var4 = var4.func_77946_l();
         var4.func_190920_e(var4.func_190916_E() / 2);
      } else if (this.field_147007_t && this.field_147008_s.contains(var1) && !var7.func_190926_b()) {
         if (this.field_147008_s.size() == 1) {
            return;
         }

         if (Container.func_94527_a(var1, var7, true) && this.field_147002_h.func_94531_b(var1)) {
            var4 = var7.func_77946_l();
            var5 = true;
            Container.func_94525_a(this.field_147008_s, this.field_146987_F, var4, var1.func_75211_c().func_190926_b() ? 0 : var1.func_75211_c().func_190916_E());
            int var9 = Math.min(var4.func_77976_d(), var1.func_178170_b(var4));
            if (var4.func_190916_E() > var9) {
               var8 = TextFormatting.YELLOW.toString() + var9;
               var4.func_190920_e(var9);
            }
         } else {
            this.field_147008_s.remove(var1);
            this.func_146980_g();
         }
      }

      this.field_73735_i = 100.0F;
      this.field_146296_j.field_77023_b = 100.0F;
      if (var4.func_190926_b() && var1.func_111238_b()) {
         String var11 = var1.func_178171_c();
         if (var11 != null) {
            TextureAtlasSprite var10 = this.field_146297_k.func_147117_R().func_110572_b(var11);
            GlStateManager.func_179140_f();
            this.field_146297_k.func_110434_K().func_110577_a(TextureMap.field_110575_b);
            this.func_175175_a(var2, var3, var10, 16, 16);
            GlStateManager.func_179145_e();
            var6 = true;
         }
      }

      if (!var6) {
         if (var5) {
            func_73734_a(var2, var3, var2 + 16, var3 + 16, -2130706433);
         }

         GlStateManager.func_179126_j();
         this.field_146296_j.func_184391_a(this.field_146297_k.field_71439_g, var4, var2, var3);
         this.field_146296_j.func_180453_a(this.field_146289_q, var4, var2, var3, var8);
      }

      this.field_146296_j.field_77023_b = 0.0F;
      this.field_73735_i = 0.0F;
   }

   private void func_146980_g() {
      ItemStack var1 = this.field_146297_k.field_71439_g.field_71071_by.func_70445_o();
      if (!var1.func_190926_b() && this.field_147007_t) {
         if (this.field_146987_F == 2) {
            this.field_146996_I = var1.func_77976_d();
         } else {
            this.field_146996_I = var1.func_190916_E();

            ItemStack var4;
            int var6;
            for(Iterator var2 = this.field_147008_s.iterator(); var2.hasNext(); this.field_146996_I -= var4.func_190916_E() - var6) {
               Slot var3 = (Slot)var2.next();
               var4 = var1.func_77946_l();
               ItemStack var5 = var3.func_75211_c();
               var6 = var5.func_190926_b() ? 0 : var5.func_190916_E();
               Container.func_94525_a(this.field_147008_s, this.field_146987_F, var4, var6);
               int var7 = Math.min(var4.func_77976_d(), var3.func_178170_b(var4));
               if (var4.func_190916_E() > var7) {
                  var4.func_190920_e(var7);
               }
            }

         }
      }
   }

   private Slot func_195360_a(double var1, double var3) {
      for(int var5 = 0; var5 < this.field_147002_h.field_75151_b.size(); ++var5) {
         Slot var6 = (Slot)this.field_147002_h.field_75151_b.get(var5);
         if (this.func_195362_a(var6, var1, var3) && var6.func_111238_b()) {
            return var6;
         }
      }

      return null;
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (super.mouseClicked(var1, var3, var5)) {
         return true;
      } else {
         boolean var6 = this.field_146297_k.field_71474_y.field_74322_I.func_197984_a(var5);
         Slot var7 = this.func_195360_a(var1, var3);
         long var8 = Util.func_211177_b();
         this.field_146993_M = this.field_146998_K == var7 && var8 - this.field_146997_J < 250L && this.field_146992_L == var5;
         this.field_146995_H = false;
         if (var5 == 0 || var5 == 1 || var6) {
            int var10 = this.field_147003_i;
            int var11 = this.field_147009_r;
            boolean var12 = this.func_195361_a(var1, var3, var10, var11, var5);
            int var13 = -1;
            if (var7 != null) {
               var13 = var7.field_75222_d;
            }

            if (var12) {
               var13 = -999;
            }

            if (this.field_146297_k.field_71474_y.field_85185_A && var12 && this.field_146297_k.field_71439_g.field_71071_by.func_70445_o().func_190926_b()) {
               this.field_146297_k.func_147108_a((GuiScreen)null);
               return true;
            }

            if (var13 != -1) {
               if (this.field_146297_k.field_71474_y.field_85185_A) {
                  if (var7 != null && var7.func_75216_d()) {
                     this.field_147005_v = var7;
                     this.field_147012_x = ItemStack.field_190927_a;
                     this.field_147004_w = var5 == 1;
                  } else {
                     this.field_147005_v = null;
                  }
               } else if (!this.field_147007_t) {
                  if (this.field_146297_k.field_71439_g.field_71071_by.func_70445_o().func_190926_b()) {
                     if (this.field_146297_k.field_71474_y.field_74322_I.func_197984_a(var5)) {
                        this.func_184098_a(var7, var13, var5, ClickType.CLONE);
                     } else {
                        boolean var14 = var13 != -999 && (InputMappings.func_197956_a(340) || InputMappings.func_197956_a(344));
                        ClickType var15 = ClickType.PICKUP;
                        if (var14) {
                           this.field_146994_N = var7 != null && var7.func_75216_d() ? var7.func_75211_c().func_77946_l() : ItemStack.field_190927_a;
                           var15 = ClickType.QUICK_MOVE;
                        } else if (var13 == -999) {
                           var15 = ClickType.THROW;
                        }

                        this.func_184098_a(var7, var13, var5, var15);
                     }

                     this.field_146995_H = true;
                  } else {
                     this.field_147007_t = true;
                     this.field_146988_G = var5;
                     this.field_147008_s.clear();
                     if (var5 == 0) {
                        this.field_146987_F = 0;
                     } else if (var5 == 1) {
                        this.field_146987_F = 1;
                     } else if (this.field_146297_k.field_71474_y.field_74322_I.func_197984_a(var5)) {
                        this.field_146987_F = 2;
                     }
                  }
               }
            }
         }

         this.field_146998_K = var7;
         this.field_146997_J = var8;
         this.field_146992_L = var5;
         return true;
      }
   }

   protected boolean func_195361_a(double var1, double var3, int var5, int var6, int var7) {
      return var1 < (double)var5 || var3 < (double)var6 || var1 >= (double)(var5 + this.field_146999_f) || var3 >= (double)(var6 + this.field_147000_g);
   }

   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      Slot var10 = this.func_195360_a(var1, var3);
      ItemStack var11 = this.field_146297_k.field_71439_g.field_71071_by.func_70445_o();
      if (this.field_147005_v != null && this.field_146297_k.field_71474_y.field_85185_A) {
         if (var5 == 0 || var5 == 1) {
            if (this.field_147012_x.func_190926_b()) {
               if (var10 != this.field_147005_v && !this.field_147005_v.func_75211_c().func_190926_b()) {
                  this.field_147012_x = this.field_147005_v.func_75211_c().func_77946_l();
               }
            } else if (this.field_147012_x.func_190916_E() > 1 && var10 != null && Container.func_94527_a(var10, this.field_147012_x, false)) {
               long var12 = Util.func_211177_b();
               if (this.field_146985_D == var10) {
                  if (var12 - this.field_146986_E > 500L) {
                     this.func_184098_a(this.field_147005_v, this.field_147005_v.field_75222_d, 0, ClickType.PICKUP);
                     this.func_184098_a(var10, var10.field_75222_d, 1, ClickType.PICKUP);
                     this.func_184098_a(this.field_147005_v, this.field_147005_v.field_75222_d, 0, ClickType.PICKUP);
                     this.field_146986_E = var12 + 750L;
                     this.field_147012_x.func_190918_g(1);
                  }
               } else {
                  this.field_146985_D = var10;
                  this.field_146986_E = var12;
               }
            }
         }
      } else if (this.field_147007_t && var10 != null && !var11.func_190926_b() && (var11.func_190916_E() > this.field_147008_s.size() || this.field_146987_F == 2) && Container.func_94527_a(var10, var11, true) && var10.func_75214_a(var11) && this.field_147002_h.func_94531_b(var10)) {
         this.field_147008_s.add(var10);
         this.func_146980_g();
      }

      return true;
   }

   public boolean mouseReleased(double var1, double var3, int var5) {
      Slot var6 = this.func_195360_a(var1, var3);
      int var7 = this.field_147003_i;
      int var8 = this.field_147009_r;
      boolean var9 = this.func_195361_a(var1, var3, var7, var8, var5);
      int var10 = -1;
      if (var6 != null) {
         var10 = var6.field_75222_d;
      }

      if (var9) {
         var10 = -999;
      }

      Slot var12;
      Iterator var13;
      if (this.field_146993_M && var6 != null && var5 == 0 && this.field_147002_h.func_94530_a(ItemStack.field_190927_a, var6)) {
         if (func_146272_n()) {
            if (!this.field_146994_N.func_190926_b()) {
               var13 = this.field_147002_h.field_75151_b.iterator();

               while(var13.hasNext()) {
                  var12 = (Slot)var13.next();
                  if (var12 != null && var12.func_82869_a(this.field_146297_k.field_71439_g) && var12.func_75216_d() && var12.field_75224_c == var6.field_75224_c && Container.func_94527_a(var12, this.field_146994_N, true)) {
                     this.func_184098_a(var12, var12.field_75222_d, var5, ClickType.QUICK_MOVE);
                  }
               }
            }
         } else {
            this.func_184098_a(var6, var10, var5, ClickType.PICKUP_ALL);
         }

         this.field_146993_M = false;
         this.field_146997_J = 0L;
      } else {
         if (this.field_147007_t && this.field_146988_G != var5) {
            this.field_147007_t = false;
            this.field_147008_s.clear();
            this.field_146995_H = true;
            return true;
         }

         if (this.field_146995_H) {
            this.field_146995_H = false;
            return true;
         }

         boolean var11;
         if (this.field_147005_v != null && this.field_146297_k.field_71474_y.field_85185_A) {
            if (var5 == 0 || var5 == 1) {
               if (this.field_147012_x.func_190926_b() && var6 != this.field_147005_v) {
                  this.field_147012_x = this.field_147005_v.func_75211_c();
               }

               var11 = Container.func_94527_a(var6, this.field_147012_x, false);
               if (var10 != -1 && !this.field_147012_x.func_190926_b() && var11) {
                  this.func_184098_a(this.field_147005_v, this.field_147005_v.field_75222_d, var5, ClickType.PICKUP);
                  this.func_184098_a(var6, var10, 0, ClickType.PICKUP);
                  if (this.field_146297_k.field_71439_g.field_71071_by.func_70445_o().func_190926_b()) {
                     this.field_146991_C = ItemStack.field_190927_a;
                  } else {
                     this.func_184098_a(this.field_147005_v, this.field_147005_v.field_75222_d, var5, ClickType.PICKUP);
                     this.field_147011_y = MathHelper.func_76128_c(var1 - (double)var7);
                     this.field_147010_z = MathHelper.func_76128_c(var3 - (double)var8);
                     this.field_146989_A = this.field_147005_v;
                     this.field_146991_C = this.field_147012_x;
                     this.field_146990_B = Util.func_211177_b();
                  }
               } else if (!this.field_147012_x.func_190926_b()) {
                  this.field_147011_y = MathHelper.func_76128_c(var1 - (double)var7);
                  this.field_147010_z = MathHelper.func_76128_c(var3 - (double)var8);
                  this.field_146989_A = this.field_147005_v;
                  this.field_146991_C = this.field_147012_x;
                  this.field_146990_B = Util.func_211177_b();
               }

               this.field_147012_x = ItemStack.field_190927_a;
               this.field_147005_v = null;
            }
         } else if (this.field_147007_t && !this.field_147008_s.isEmpty()) {
            this.func_184098_a((Slot)null, -999, Container.func_94534_d(0, this.field_146987_F), ClickType.QUICK_CRAFT);
            var13 = this.field_147008_s.iterator();

            while(var13.hasNext()) {
               var12 = (Slot)var13.next();
               this.func_184098_a(var12, var12.field_75222_d, Container.func_94534_d(1, this.field_146987_F), ClickType.QUICK_CRAFT);
            }

            this.func_184098_a((Slot)null, -999, Container.func_94534_d(2, this.field_146987_F), ClickType.QUICK_CRAFT);
         } else if (!this.field_146297_k.field_71439_g.field_71071_by.func_70445_o().func_190926_b()) {
            if (this.field_146297_k.field_71474_y.field_74322_I.func_197984_a(var5)) {
               this.func_184098_a(var6, var10, var5, ClickType.CLONE);
            } else {
               var11 = var10 != -999 && (InputMappings.func_197956_a(340) || InputMappings.func_197956_a(344));
               if (var11) {
                  this.field_146994_N = var6 != null && var6.func_75216_d() ? var6.func_75211_c().func_77946_l() : ItemStack.field_190927_a;
               }

               this.func_184098_a(var6, var10, var5, var11 ? ClickType.QUICK_MOVE : ClickType.PICKUP);
            }
         }
      }

      if (this.field_146297_k.field_71439_g.field_71071_by.func_70445_o().func_190926_b()) {
         this.field_146997_J = 0L;
      }

      this.field_147007_t = false;
      return true;
   }

   private boolean func_195362_a(Slot var1, double var2, double var4) {
      return this.func_195359_a(var1.field_75223_e, var1.field_75221_f, 16, 16, var2, var4);
   }

   protected boolean func_195359_a(int var1, int var2, int var3, int var4, double var5, double var7) {
      int var9 = this.field_147003_i;
      int var10 = this.field_147009_r;
      var5 -= (double)var9;
      var7 -= (double)var10;
      return var5 >= (double)(var1 - 1) && var5 < (double)(var1 + var3 + 1) && var7 >= (double)(var2 - 1) && var7 < (double)(var2 + var4 + 1);
   }

   protected void func_184098_a(Slot var1, int var2, int var3, ClickType var4) {
      if (var1 != null) {
         var2 = var1.field_75222_d;
      }

      this.field_146297_k.field_71442_b.func_187098_a(this.field_147002_h.field_75152_c, var2, var3, var4, this.field_146297_k.field_71439_g);
   }

   public boolean func_195120_Y_() {
      return false;
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (super.keyPressed(var1, var2, var3)) {
         return true;
      } else {
         if (var1 == 256 || this.field_146297_k.field_71474_y.field_151445_Q.func_197976_a(var1, var2)) {
            this.field_146297_k.field_71439_g.func_71053_j();
         }

         this.func_195363_d(var1, var2);
         if (this.field_147006_u != null && this.field_147006_u.func_75216_d()) {
            if (this.field_146297_k.field_71474_y.field_74322_I.func_197976_a(var1, var2)) {
               this.func_184098_a(this.field_147006_u, this.field_147006_u.field_75222_d, 0, ClickType.CLONE);
            } else if (this.field_146297_k.field_71474_y.field_74316_C.func_197976_a(var1, var2)) {
               this.func_184098_a(this.field_147006_u, this.field_147006_u.field_75222_d, func_146271_m() ? 1 : 0, ClickType.THROW);
            }
         }

         return true;
      }
   }

   protected boolean func_195363_d(int var1, int var2) {
      if (this.field_146297_k.field_71439_g.field_71071_by.func_70445_o().func_190926_b() && this.field_147006_u != null) {
         for(int var3 = 0; var3 < 9; ++var3) {
            if (this.field_146297_k.field_71474_y.field_151456_ac[var3].func_197976_a(var1, var2)) {
               this.func_184098_a(this.field_147006_u, this.field_147006_u.field_75222_d, var3, ClickType.SWAP);
               return true;
            }
         }
      }

      return false;
   }

   public void func_146281_b() {
      if (this.field_146297_k.field_71439_g != null) {
         this.field_147002_h.func_75134_a(this.field_146297_k.field_71439_g);
      }
   }

   public boolean func_73868_f() {
      return false;
   }

   public void func_73876_c() {
      super.func_73876_c();
      if (!this.field_146297_k.field_71439_g.func_70089_S() || this.field_146297_k.field_71439_g.field_70128_L) {
         this.field_146297_k.field_71439_g.func_71053_j();
      }

   }
}
