package net.minecraft.client.gui.advancements;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class GuiAdvancementTab extends Gui {
   private final Minecraft field_191802_a;
   private final GuiScreenAdvancements field_193938_f;
   private final AdvancementTabType field_191803_f;
   private final int field_191804_g;
   private final Advancement field_191805_h;
   private final DisplayInfo field_191806_i;
   private final ItemStack field_191807_j;
   private final String field_191808_k;
   private final GuiAdvancement field_191809_l;
   private final Map<Advancement, GuiAdvancement> field_191810_m = Maps.newLinkedHashMap();
   private double field_191811_n;
   private double field_191812_o;
   private int field_193939_q = 2147483647;
   private int field_193940_r = 2147483647;
   private int field_191813_p = -2147483648;
   private int field_191814_q = -2147483648;
   private float field_191815_r;
   private boolean field_192992_s;

   public GuiAdvancementTab(Minecraft var1, GuiScreenAdvancements var2, AdvancementTabType var3, int var4, Advancement var5, DisplayInfo var6) {
      super();
      this.field_191802_a = var1;
      this.field_193938_f = var2;
      this.field_191803_f = var3;
      this.field_191804_g = var4;
      this.field_191805_h = var5;
      this.field_191806_i = var6;
      this.field_191807_j = var6.func_192298_b();
      this.field_191808_k = var6.func_192297_a().func_150254_d();
      this.field_191809_l = new GuiAdvancement(this, var1, var5, var6);
      this.func_193937_a(this.field_191809_l, var5);
   }

   public Advancement func_193935_c() {
      return this.field_191805_h;
   }

   public String func_191795_d() {
      return this.field_191808_k;
   }

   public void func_191798_a(int var1, int var2, boolean var3) {
      this.field_191803_f.func_192651_a(this, var1, var2, var3, this.field_191804_g);
   }

   public void func_191796_a(int var1, int var2, ItemRenderer var3) {
      this.field_191803_f.func_192652_a(var1, var2, this.field_191804_g, var3, this.field_191807_j);
   }

   public void func_191799_a() {
      if (!this.field_192992_s) {
         this.field_191811_n = (double)(117 - (this.field_191813_p + this.field_193939_q) / 2);
         this.field_191812_o = (double)(56 - (this.field_191814_q + this.field_193940_r) / 2);
         this.field_192992_s = true;
      }

      GlStateManager.func_179143_c(518);
      func_73734_a(0, 0, 234, 113, -16777216);
      GlStateManager.func_179143_c(515);
      ResourceLocation var1 = this.field_191806_i.func_192293_c();
      if (var1 != null) {
         this.field_191802_a.func_110434_K().func_110577_a(var1);
      } else {
         this.field_191802_a.func_110434_K().func_110577_a(TextureManager.field_194008_a);
      }

      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      int var2 = MathHelper.func_76128_c(this.field_191811_n);
      int var3 = MathHelper.func_76128_c(this.field_191812_o);
      int var4 = var2 % 16;
      int var5 = var3 % 16;

      for(int var6 = -1; var6 <= 15; ++var6) {
         for(int var7 = -1; var7 <= 8; ++var7) {
            func_146110_a(var4 + 16 * var6, var5 + 16 * var7, 0.0F, 0.0F, 16, 16, 16.0F, 16.0F);
         }
      }

      this.field_191809_l.func_191819_a(var2, var3, true);
      this.field_191809_l.func_191819_a(var2, var3, false);
      this.field_191809_l.func_191817_b(var2, var3);
   }

   public void func_192991_a(int var1, int var2, int var3, int var4) {
      GlStateManager.func_179094_E();
      GlStateManager.func_179109_b(0.0F, 0.0F, 200.0F);
      func_73734_a(0, 0, 234, 113, MathHelper.func_76141_d(this.field_191815_r * 255.0F) << 24);
      boolean var5 = false;
      int var6 = MathHelper.func_76128_c(this.field_191811_n);
      int var7 = MathHelper.func_76128_c(this.field_191812_o);
      if (var1 > 0 && var1 < 234 && var2 > 0 && var2 < 113) {
         Iterator var8 = this.field_191810_m.values().iterator();

         while(var8.hasNext()) {
            GuiAdvancement var9 = (GuiAdvancement)var8.next();
            if (var9.func_191816_c(var6, var7, var1, var2)) {
               var5 = true;
               var9.func_191821_a(var6, var7, this.field_191815_r, var3, var4);
               break;
            }
         }
      }

      GlStateManager.func_179121_F();
      if (var5) {
         this.field_191815_r = MathHelper.func_76131_a(this.field_191815_r + 0.02F, 0.0F, 0.3F);
      } else {
         this.field_191815_r = MathHelper.func_76131_a(this.field_191815_r - 0.04F, 0.0F, 1.0F);
      }

   }

   public boolean func_195627_a(int var1, int var2, double var3, double var5) {
      return this.field_191803_f.func_198891_a(var1, var2, this.field_191804_g, var3, var5);
   }

   @Nullable
   public static GuiAdvancementTab func_193936_a(Minecraft var0, GuiScreenAdvancements var1, int var2, Advancement var3) {
      if (var3.func_192068_c() == null) {
         return null;
      } else {
         AdvancementTabType[] var4 = AdvancementTabType.values();
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            AdvancementTabType var7 = var4[var6];
            if (var2 < var7.func_192650_a()) {
               return new GuiAdvancementTab(var0, var1, var7, var2, var3, var3.func_192068_c());
            }

            var2 -= var7.func_192650_a();
         }

         return null;
      }
   }

   public void func_195626_a(double var1, double var3) {
      if (this.field_191813_p - this.field_193939_q > 234) {
         this.field_191811_n = MathHelper.func_151237_a(this.field_191811_n + var1, (double)(-(this.field_191813_p - 234)), 0.0D);
      }

      if (this.field_191814_q - this.field_193940_r > 113) {
         this.field_191812_o = MathHelper.func_151237_a(this.field_191812_o + var3, (double)(-(this.field_191814_q - 113)), 0.0D);
      }

   }

   public void func_191800_a(Advancement var1) {
      if (var1.func_192068_c() != null) {
         GuiAdvancement var2 = new GuiAdvancement(this, this.field_191802_a, var1, var1.func_192068_c());
         this.func_193937_a(var2, var1);
      }
   }

   private void func_193937_a(GuiAdvancement var1, Advancement var2) {
      this.field_191810_m.put(var2, var1);
      int var3 = var1.func_191823_d();
      int var4 = var3 + 28;
      int var5 = var1.func_191820_c();
      int var6 = var5 + 27;
      this.field_193939_q = Math.min(this.field_193939_q, var3);
      this.field_191813_p = Math.max(this.field_191813_p, var4);
      this.field_193940_r = Math.min(this.field_193940_r, var5);
      this.field_191814_q = Math.max(this.field_191814_q, var6);
      Iterator var7 = this.field_191810_m.values().iterator();

      while(var7.hasNext()) {
         GuiAdvancement var8 = (GuiAdvancement)var7.next();
         var8.func_191825_b();
      }

   }

   @Nullable
   public GuiAdvancement func_191794_b(Advancement var1) {
      return (GuiAdvancement)this.field_191810_m.get(var1);
   }

   public GuiScreenAdvancements func_193934_g() {
      return this.field_193938_f;
   }
}
