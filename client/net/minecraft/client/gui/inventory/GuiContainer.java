package net.minecraft.client.gui.inventory;

import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

public abstract class GuiContainer extends GuiScreen {
   protected static final ResourceLocation field_147001_a = new ResourceLocation("textures/gui/container/inventory.png");
   protected int field_146999_f = 176;
   protected int field_147000_g = 166;
   public Container field_147002_h;
   protected int field_147003_i;
   protected int field_147009_r;
   private Slot field_147006_u;
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
   protected final Set<Slot> field_147008_s = Sets.newHashSet();
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
      this.field_147002_h = var1;
      this.field_146995_H = true;
   }

   public void func_73866_w_() {
      super.func_73866_w_();
      this.field_146297_k.field_71439_g.field_71070_bA = this.field_147002_h;
      this.field_147003_i = (this.field_146294_l - this.field_146999_f) / 2;
      this.field_147009_r = (this.field_146295_m - this.field_147000_g) / 2;
   }

   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146276_q_();
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
      short var6 = 240;
      short var7 = 240;
      OpenGlHelper.func_77475_a(OpenGlHelper.field_77476_b, (float)var6 / 1.0F, (float)var7 / 1.0F);
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);

      int var11;
      for(int var8 = 0; var8 < this.field_147002_h.field_75151_b.size(); ++var8) {
         Slot var9 = (Slot)this.field_147002_h.field_75151_b.get(var8);
         this.func_146977_a(var9);
         if (this.func_146981_a(var9, var1, var2) && var9.func_111238_b()) {
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
      ItemStack var16 = this.field_147012_x == null ? var15.func_70445_o() : this.field_147012_x;
      if (var16 != null) {
         byte var17 = 8;
         var11 = this.field_147012_x == null ? 8 : 16;
         String var12 = null;
         if (this.field_147012_x != null && this.field_147004_w) {
            var16 = var16.func_77946_l();
            var16.field_77994_a = MathHelper.func_76123_f((float)var16.field_77994_a / 2.0F);
         } else if (this.field_147007_t && this.field_147008_s.size() > 1) {
            var16 = var16.func_77946_l();
            var16.field_77994_a = this.field_146996_I;
            if (var16.field_77994_a == 0) {
               var12 = "" + EnumChatFormatting.YELLOW + "0";
            }
         }

         this.func_146982_a(var16, var1 - var4 - var17, var2 - var5 - var11, var12);
      }

      if (this.field_146991_C != null) {
         float var18 = (float)(Minecraft.func_71386_F() - this.field_146990_B) / 100.0F;
         if (var18 >= 1.0F) {
            var18 = 1.0F;
            this.field_146991_C = null;
         }

         var11 = this.field_146989_A.field_75223_e - this.field_147011_y;
         int var20 = this.field_146989_A.field_75221_f - this.field_147010_z;
         int var13 = this.field_147011_y + (int)((float)var11 * var18);
         int var14 = this.field_147010_z + (int)((float)var20 * var18);
         this.func_146982_a(this.field_146991_C, var13, var14, (String)null);
      }

      GlStateManager.func_179121_F();
      if (var15.func_70445_o() == null && this.field_147006_u != null && this.field_147006_u.func_75216_d()) {
         ItemStack var19 = this.field_147006_u.func_75211_c();
         this.func_146285_a(var19, var1, var2);
      }

      GlStateManager.func_179145_e();
      GlStateManager.func_179126_j();
      RenderHelper.func_74519_b();
   }

   private void func_146982_a(ItemStack var1, int var2, int var3, String var4) {
      GlStateManager.func_179109_b(0.0F, 0.0F, 32.0F);
      this.field_73735_i = 200.0F;
      this.field_146296_j.field_77023_b = 200.0F;
      this.field_146296_j.func_180450_b(var1, var2, var3);
      this.field_146296_j.func_180453_a(this.field_146289_q, var1, var2, var3 - (this.field_147012_x == null ? 0 : 8), var4);
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
      boolean var6 = var1 == this.field_147005_v && this.field_147012_x != null && !this.field_147004_w;
      ItemStack var7 = this.field_146297_k.field_71439_g.field_71071_by.func_70445_o();
      String var8 = null;
      if (var1 == this.field_147005_v && this.field_147012_x != null && this.field_147004_w && var4 != null) {
         var4 = var4.func_77946_l();
         var4.field_77994_a /= 2;
      } else if (this.field_147007_t && this.field_147008_s.contains(var1) && var7 != null) {
         if (this.field_147008_s.size() == 1) {
            return;
         }

         if (Container.func_94527_a(var1, var7, true) && this.field_147002_h.func_94531_b(var1)) {
            var4 = var7.func_77946_l();
            var5 = true;
            Container.func_94525_a(this.field_147008_s, this.field_146987_F, var4, var1.func_75211_c() == null ? 0 : var1.func_75211_c().field_77994_a);
            if (var4.field_77994_a > var4.func_77976_d()) {
               var8 = EnumChatFormatting.YELLOW + "" + var4.func_77976_d();
               var4.field_77994_a = var4.func_77976_d();
            }

            if (var4.field_77994_a > var1.func_178170_b(var4)) {
               var8 = EnumChatFormatting.YELLOW + "" + var1.func_178170_b(var4);
               var4.field_77994_a = var1.func_178170_b(var4);
            }
         } else {
            this.field_147008_s.remove(var1);
            this.func_146980_g();
         }
      }

      this.field_73735_i = 100.0F;
      this.field_146296_j.field_77023_b = 100.0F;
      if (var4 == null) {
         String var9 = var1.func_178171_c();
         if (var9 != null) {
            TextureAtlasSprite var10 = this.field_146297_k.func_147117_R().func_110572_b(var9);
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
         this.field_146296_j.func_180450_b(var4, var2, var3);
         this.field_146296_j.func_180453_a(this.field_146289_q, var4, var2, var3, var8);
      }

      this.field_146296_j.field_77023_b = 0.0F;
      this.field_73735_i = 0.0F;
   }

   private void func_146980_g() {
      ItemStack var1 = this.field_146297_k.field_71439_g.field_71071_by.func_70445_o();
      if (var1 != null && this.field_147007_t) {
         this.field_146996_I = var1.field_77994_a;

         ItemStack var4;
         int var5;
         for(Iterator var2 = this.field_147008_s.iterator(); var2.hasNext(); this.field_146996_I -= var4.field_77994_a - var5) {
            Slot var3 = (Slot)var2.next();
            var4 = var1.func_77946_l();
            var5 = var3.func_75211_c() == null ? 0 : var3.func_75211_c().field_77994_a;
            Container.func_94525_a(this.field_147008_s, this.field_146987_F, var4, var5);
            if (var4.field_77994_a > var4.func_77976_d()) {
               var4.field_77994_a = var4.func_77976_d();
            }

            if (var4.field_77994_a > var3.func_178170_b(var4)) {
               var4.field_77994_a = var3.func_178170_b(var4);
            }
         }

      }
   }

   private Slot func_146975_c(int var1, int var2) {
      for(int var3 = 0; var3 < this.field_147002_h.field_75151_b.size(); ++var3) {
         Slot var4 = (Slot)this.field_147002_h.field_75151_b.get(var3);
         if (this.func_146981_a(var4, var1, var2)) {
            return var4;
         }
      }

      return null;
   }

   protected void func_73864_a(int var1, int var2, int var3) {
      super.func_73864_a(var1, var2, var3);
      boolean var4 = var3 == this.field_146297_k.field_71474_y.field_74322_I.func_151463_i() + 100;
      Slot var5 = this.func_146975_c(var1, var2);
      long var6 = Minecraft.func_71386_F();
      this.field_146993_M = this.field_146998_K == var5 && var6 - this.field_146997_J < 250L && this.field_146992_L == var3;
      this.field_146995_H = false;
      if (var3 == 0 || var3 == 1 || var4) {
         int var8 = this.field_147003_i;
         int var9 = this.field_147009_r;
         boolean var10 = var1 < var8 || var2 < var9 || var1 >= var8 + this.field_146999_f || var2 >= var9 + this.field_147000_g;
         int var11 = -1;
         if (var5 != null) {
            var11 = var5.field_75222_d;
         }

         if (var10) {
            var11 = -999;
         }

         if (this.field_146297_k.field_71474_y.field_85185_A && var10 && this.field_146297_k.field_71439_g.field_71071_by.func_70445_o() == null) {
            this.field_146297_k.func_147108_a((GuiScreen)null);
            return;
         }

         if (var11 != -1) {
            if (this.field_146297_k.field_71474_y.field_85185_A) {
               if (var5 != null && var5.func_75216_d()) {
                  this.field_147005_v = var5;
                  this.field_147012_x = null;
                  this.field_147004_w = var3 == 1;
               } else {
                  this.field_147005_v = null;
               }
            } else if (!this.field_147007_t) {
               if (this.field_146297_k.field_71439_g.field_71071_by.func_70445_o() == null) {
                  if (var3 == this.field_146297_k.field_71474_y.field_74322_I.func_151463_i() + 100) {
                     this.func_146984_a(var5, var11, var3, 3);
                  } else {
                     boolean var12 = var11 != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));
                     byte var13 = 0;
                     if (var12) {
                        this.field_146994_N = var5 != null && var5.func_75216_d() ? var5.func_75211_c() : null;
                        var13 = 1;
                     } else if (var11 == -999) {
                        var13 = 4;
                     }

                     this.func_146984_a(var5, var11, var3, var13);
                  }

                  this.field_146995_H = true;
               } else {
                  this.field_147007_t = true;
                  this.field_146988_G = var3;
                  this.field_147008_s.clear();
                  if (var3 == 0) {
                     this.field_146987_F = 0;
                  } else if (var3 == 1) {
                     this.field_146987_F = 1;
                  } else if (var3 == this.field_146297_k.field_71474_y.field_74322_I.func_151463_i() + 100) {
                     this.field_146987_F = 2;
                  }
               }
            }
         }
      }

      this.field_146998_K = var5;
      this.field_146997_J = var6;
      this.field_146992_L = var3;
   }

   protected void func_146273_a(int var1, int var2, int var3, long var4) {
      Slot var6 = this.func_146975_c(var1, var2);
      ItemStack var7 = this.field_146297_k.field_71439_g.field_71071_by.func_70445_o();
      if (this.field_147005_v != null && this.field_146297_k.field_71474_y.field_85185_A) {
         if (var3 == 0 || var3 == 1) {
            if (this.field_147012_x == null) {
               if (var6 != this.field_147005_v && this.field_147005_v.func_75211_c() != null) {
                  this.field_147012_x = this.field_147005_v.func_75211_c().func_77946_l();
               }
            } else if (this.field_147012_x.field_77994_a > 1 && var6 != null && Container.func_94527_a(var6, this.field_147012_x, false)) {
               long var8 = Minecraft.func_71386_F();
               if (this.field_146985_D == var6) {
                  if (var8 - this.field_146986_E > 500L) {
                     this.func_146984_a(this.field_147005_v, this.field_147005_v.field_75222_d, 0, 0);
                     this.func_146984_a(var6, var6.field_75222_d, 1, 0);
                     this.func_146984_a(this.field_147005_v, this.field_147005_v.field_75222_d, 0, 0);
                     this.field_146986_E = var8 + 750L;
                     --this.field_147012_x.field_77994_a;
                  }
               } else {
                  this.field_146985_D = var6;
                  this.field_146986_E = var8;
               }
            }
         }
      } else if (this.field_147007_t && var6 != null && var7 != null && var7.field_77994_a > this.field_147008_s.size() && Container.func_94527_a(var6, var7, true) && var6.func_75214_a(var7) && this.field_147002_h.func_94531_b(var6)) {
         this.field_147008_s.add(var6);
         this.func_146980_g();
      }

   }

   protected void func_146286_b(int var1, int var2, int var3) {
      Slot var4 = this.func_146975_c(var1, var2);
      int var5 = this.field_147003_i;
      int var6 = this.field_147009_r;
      boolean var7 = var1 < var5 || var2 < var6 || var1 >= var5 + this.field_146999_f || var2 >= var6 + this.field_147000_g;
      int var8 = -1;
      if (var4 != null) {
         var8 = var4.field_75222_d;
      }

      if (var7) {
         var8 = -999;
      }

      Slot var10;
      Iterator var11;
      if (this.field_146993_M && var4 != null && var3 == 0 && this.field_147002_h.func_94530_a((ItemStack)null, var4)) {
         if (func_146272_n()) {
            if (var4 != null && var4.field_75224_c != null && this.field_146994_N != null) {
               var11 = this.field_147002_h.field_75151_b.iterator();

               while(var11.hasNext()) {
                  var10 = (Slot)var11.next();
                  if (var10 != null && var10.func_82869_a(this.field_146297_k.field_71439_g) && var10.func_75216_d() && var10.field_75224_c == var4.field_75224_c && Container.func_94527_a(var10, this.field_146994_N, true)) {
                     this.func_146984_a(var10, var10.field_75222_d, var3, 1);
                  }
               }
            }
         } else {
            this.func_146984_a(var4, var8, var3, 6);
         }

         this.field_146993_M = false;
         this.field_146997_J = 0L;
      } else {
         if (this.field_147007_t && this.field_146988_G != var3) {
            this.field_147007_t = false;
            this.field_147008_s.clear();
            this.field_146995_H = true;
            return;
         }

         if (this.field_146995_H) {
            this.field_146995_H = false;
            return;
         }

         boolean var9;
         if (this.field_147005_v != null && this.field_146297_k.field_71474_y.field_85185_A) {
            if (var3 == 0 || var3 == 1) {
               if (this.field_147012_x == null && var4 != this.field_147005_v) {
                  this.field_147012_x = this.field_147005_v.func_75211_c();
               }

               var9 = Container.func_94527_a(var4, this.field_147012_x, false);
               if (var8 != -1 && this.field_147012_x != null && var9) {
                  this.func_146984_a(this.field_147005_v, this.field_147005_v.field_75222_d, var3, 0);
                  this.func_146984_a(var4, var8, 0, 0);
                  if (this.field_146297_k.field_71439_g.field_71071_by.func_70445_o() != null) {
                     this.func_146984_a(this.field_147005_v, this.field_147005_v.field_75222_d, var3, 0);
                     this.field_147011_y = var1 - var5;
                     this.field_147010_z = var2 - var6;
                     this.field_146989_A = this.field_147005_v;
                     this.field_146991_C = this.field_147012_x;
                     this.field_146990_B = Minecraft.func_71386_F();
                  } else {
                     this.field_146991_C = null;
                  }
               } else if (this.field_147012_x != null) {
                  this.field_147011_y = var1 - var5;
                  this.field_147010_z = var2 - var6;
                  this.field_146989_A = this.field_147005_v;
                  this.field_146991_C = this.field_147012_x;
                  this.field_146990_B = Minecraft.func_71386_F();
               }

               this.field_147012_x = null;
               this.field_147005_v = null;
            }
         } else if (this.field_147007_t && !this.field_147008_s.isEmpty()) {
            this.func_146984_a((Slot)null, -999, Container.func_94534_d(0, this.field_146987_F), 5);
            var11 = this.field_147008_s.iterator();

            while(var11.hasNext()) {
               var10 = (Slot)var11.next();
               this.func_146984_a(var10, var10.field_75222_d, Container.func_94534_d(1, this.field_146987_F), 5);
            }

            this.func_146984_a((Slot)null, -999, Container.func_94534_d(2, this.field_146987_F), 5);
         } else if (this.field_146297_k.field_71439_g.field_71071_by.func_70445_o() != null) {
            if (var3 == this.field_146297_k.field_71474_y.field_74322_I.func_151463_i() + 100) {
               this.func_146984_a(var4, var8, var3, 3);
            } else {
               var9 = var8 != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));
               if (var9) {
                  this.field_146994_N = var4 != null && var4.func_75216_d() ? var4.func_75211_c() : null;
               }

               this.func_146984_a(var4, var8, var3, var9 ? 1 : 0);
            }
         }
      }

      if (this.field_146297_k.field_71439_g.field_71071_by.func_70445_o() == null) {
         this.field_146997_J = 0L;
      }

      this.field_147007_t = false;
   }

   private boolean func_146981_a(Slot var1, int var2, int var3) {
      return this.func_146978_c(var1.field_75223_e, var1.field_75221_f, 16, 16, var2, var3);
   }

   protected boolean func_146978_c(int var1, int var2, int var3, int var4, int var5, int var6) {
      int var7 = this.field_147003_i;
      int var8 = this.field_147009_r;
      var5 -= var7;
      var6 -= var8;
      return var5 >= var1 - 1 && var5 < var1 + var3 + 1 && var6 >= var2 - 1 && var6 < var2 + var4 + 1;
   }

   protected void func_146984_a(Slot var1, int var2, int var3, int var4) {
      if (var1 != null) {
         var2 = var1.field_75222_d;
      }

      this.field_146297_k.field_71442_b.func_78753_a(this.field_147002_h.field_75152_c, var2, var3, var4, this.field_146297_k.field_71439_g);
   }

   protected void func_73869_a(char var1, int var2) {
      if (var2 == 1 || var2 == this.field_146297_k.field_71474_y.field_151445_Q.func_151463_i()) {
         this.field_146297_k.field_71439_g.func_71053_j();
      }

      this.func_146983_a(var2);
      if (this.field_147006_u != null && this.field_147006_u.func_75216_d()) {
         if (var2 == this.field_146297_k.field_71474_y.field_74322_I.func_151463_i()) {
            this.func_146984_a(this.field_147006_u, this.field_147006_u.field_75222_d, 0, 3);
         } else if (var2 == this.field_146297_k.field_71474_y.field_74316_C.func_151463_i()) {
            this.func_146984_a(this.field_147006_u, this.field_147006_u.field_75222_d, func_146271_m() ? 1 : 0, 4);
         }
      }

   }

   protected boolean func_146983_a(int var1) {
      if (this.field_146297_k.field_71439_g.field_71071_by.func_70445_o() == null && this.field_147006_u != null) {
         for(int var2 = 0; var2 < 9; ++var2) {
            if (var1 == this.field_146297_k.field_71474_y.field_151456_ac[var2].func_151463_i()) {
               this.func_146984_a(this.field_147006_u, this.field_147006_u.field_75222_d, var2, 2);
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
