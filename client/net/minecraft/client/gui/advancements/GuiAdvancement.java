package net.minecraft.client.gui.advancements;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class GuiAdvancement extends Gui {
   private static final ResourceLocation field_191827_a = new ResourceLocation("textures/gui/advancements/widgets.png");
   private static final Pattern field_192996_f = Pattern.compile("(.+) \\S+");
   private final GuiAdvancementTab field_191828_f;
   private final Advancement field_191829_g;
   private final DisplayInfo field_191830_h;
   private final String field_191831_i;
   private final int field_191832_j;
   private final List<String> field_192997_l;
   private final Minecraft field_191833_k;
   private GuiAdvancement field_191834_l;
   private final List<GuiAdvancement> field_191835_m = Lists.newArrayList();
   private AdvancementProgress field_191836_n;
   private final int field_191837_o;
   private final int field_191826_p;

   public GuiAdvancement(GuiAdvancementTab var1, Minecraft var2, Advancement var3, DisplayInfo var4) {
      super();
      this.field_191828_f = var1;
      this.field_191829_g = var3;
      this.field_191830_h = var4;
      this.field_191833_k = var2;
      this.field_191831_i = var2.field_71466_p.func_78269_a(var4.func_192297_a().func_150254_d(), 163);
      this.field_191837_o = MathHelper.func_76141_d(var4.func_192299_e() * 28.0F);
      this.field_191826_p = MathHelper.func_76141_d(var4.func_192296_f() * 27.0F);
      int var5 = var3.func_193124_g();
      int var6 = String.valueOf(var5).length();
      int var7 = var5 > 1 ? var2.field_71466_p.func_78256_a("  ") + var2.field_71466_p.func_78256_a("0") * var6 * 2 + var2.field_71466_p.func_78256_a("/") : 0;
      int var8 = 29 + var2.field_71466_p.func_78256_a(this.field_191831_i) + var7;
      String var9 = var4.func_193222_b().func_150254_d();
      this.field_192997_l = this.func_192995_a(var9, var8);

      String var11;
      for(Iterator var10 = this.field_192997_l.iterator(); var10.hasNext(); var8 = Math.max(var8, var2.field_71466_p.func_78256_a(var11))) {
         var11 = (String)var10.next();
      }

      this.field_191832_j = var8 + 3 + 5;
   }

   private List<String> func_192995_a(String var1, int var2) {
      if (var1.isEmpty()) {
         return Collections.emptyList();
      } else {
         List var3 = this.field_191833_k.field_71466_p.func_78271_c(var1, var2);
         if (var3.size() < 2) {
            return var3;
         } else {
            String var4 = (String)var3.get(0);
            String var5 = (String)var3.get(1);
            int var6 = this.field_191833_k.field_71466_p.func_78256_a(var4 + ' ' + var5.split(" ")[0]);
            if (var6 - var2 <= 10) {
               return this.field_191833_k.field_71466_p.func_78271_c(var1, var6);
            } else {
               Matcher var7 = field_192996_f.matcher(var4);
               if (var7.matches()) {
                  int var8 = this.field_191833_k.field_71466_p.func_78256_a(var7.group(1));
                  if (var2 - var8 <= 10) {
                     return this.field_191833_k.field_71466_p.func_78271_c(var1, var8);
                  }
               }

               return var3;
            }
         }
      }
   }

   @Nullable
   private GuiAdvancement func_191818_a(Advancement var1) {
      do {
         var1 = var1.func_192070_b();
      } while(var1 != null && var1.func_192068_c() == null);

      if (var1 != null && var1.func_192068_c() != null) {
         return this.field_191828_f.func_191794_b(var1);
      } else {
         return null;
      }
   }

   public void func_191819_a(int var1, int var2, boolean var3) {
      if (this.field_191834_l != null) {
         int var4 = var1 + this.field_191834_l.field_191837_o + 13;
         int var5 = var1 + this.field_191834_l.field_191837_o + 26 + 4;
         int var6 = var2 + this.field_191834_l.field_191826_p + 13;
         int var7 = var1 + this.field_191837_o + 13;
         int var8 = var2 + this.field_191826_p + 13;
         int var9 = var3 ? -16777216 : -1;
         if (var3) {
            this.func_73730_a(var5, var4, var6 - 1, var9);
            this.func_73730_a(var5 + 1, var4, var6, var9);
            this.func_73730_a(var5, var4, var6 + 1, var9);
            this.func_73730_a(var7, var5 - 1, var8 - 1, var9);
            this.func_73730_a(var7, var5 - 1, var8, var9);
            this.func_73730_a(var7, var5 - 1, var8 + 1, var9);
            this.func_73728_b(var5 - 1, var8, var6, var9);
            this.func_73728_b(var5 + 1, var8, var6, var9);
         } else {
            this.func_73730_a(var5, var4, var6, var9);
            this.func_73730_a(var7, var5, var8, var9);
            this.func_73728_b(var5, var8, var6, var9);
         }
      }

      Iterator var10 = this.field_191835_m.iterator();

      while(var10.hasNext()) {
         GuiAdvancement var11 = (GuiAdvancement)var10.next();
         var11.func_191819_a(var1, var2, var3);
      }

   }

   public void func_191817_b(int var1, int var2) {
      if (!this.field_191830_h.func_193224_j() || this.field_191836_n != null && this.field_191836_n.func_192105_a()) {
         float var3 = this.field_191836_n == null ? 0.0F : this.field_191836_n.func_192103_c();
         AdvancementState var4;
         if (var3 >= 1.0F) {
            var4 = AdvancementState.OBTAINED;
         } else {
            var4 = AdvancementState.UNOBTAINED;
         }

         this.field_191833_k.func_110434_K().func_110577_a(field_191827_a);
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.func_179147_l();
         this.func_73729_b(var1 + this.field_191837_o + 3, var2 + this.field_191826_p, this.field_191830_h.func_192291_d().func_192309_b(), 128 + var4.func_192667_a() * 26, 26, 26);
         RenderHelper.func_74520_c();
         this.field_191833_k.func_175599_af().func_184391_a((EntityLivingBase)null, this.field_191830_h.func_192298_b(), var1 + this.field_191837_o + 8, var2 + this.field_191826_p + 5);
      }

      Iterator var5 = this.field_191835_m.iterator();

      while(var5.hasNext()) {
         GuiAdvancement var6 = (GuiAdvancement)var5.next();
         var6.func_191817_b(var1, var2);
      }

   }

   public void func_191824_a(AdvancementProgress var1) {
      this.field_191836_n = var1;
   }

   public void func_191822_a(GuiAdvancement var1) {
      this.field_191835_m.add(var1);
   }

   public void func_191821_a(int var1, int var2, float var3, int var4, int var5) {
      boolean var6 = var4 + var1 + this.field_191837_o + this.field_191832_j + 26 >= this.field_191828_f.func_193934_g().field_146294_l;
      String var7 = this.field_191836_n == null ? null : this.field_191836_n.func_193126_d();
      int var8 = var7 == null ? 0 : this.field_191833_k.field_71466_p.func_78256_a(var7);
      boolean var9 = 113 - var2 - this.field_191826_p - 26 <= 6 + this.field_192997_l.size() * this.field_191833_k.field_71466_p.field_78288_b;
      float var10 = this.field_191836_n == null ? 0.0F : this.field_191836_n.func_192103_c();
      int var14 = MathHelper.func_76141_d(var10 * (float)this.field_191832_j);
      AdvancementState var11;
      AdvancementState var12;
      AdvancementState var13;
      if (var10 >= 1.0F) {
         var14 = this.field_191832_j / 2;
         var11 = AdvancementState.OBTAINED;
         var12 = AdvancementState.OBTAINED;
         var13 = AdvancementState.OBTAINED;
      } else if (var14 < 2) {
         var14 = this.field_191832_j / 2;
         var11 = AdvancementState.UNOBTAINED;
         var12 = AdvancementState.UNOBTAINED;
         var13 = AdvancementState.UNOBTAINED;
      } else if (var14 > this.field_191832_j - 2) {
         var14 = this.field_191832_j / 2;
         var11 = AdvancementState.OBTAINED;
         var12 = AdvancementState.OBTAINED;
         var13 = AdvancementState.UNOBTAINED;
      } else {
         var11 = AdvancementState.OBTAINED;
         var12 = AdvancementState.UNOBTAINED;
         var13 = AdvancementState.UNOBTAINED;
      }

      int var15 = this.field_191832_j - var14;
      this.field_191833_k.func_110434_K().func_110577_a(field_191827_a);
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.func_179147_l();
      int var16 = var2 + this.field_191826_p;
      int var17;
      if (var6) {
         var17 = var1 + this.field_191837_o - this.field_191832_j + 26 + 6;
      } else {
         var17 = var1 + this.field_191837_o;
      }

      int var18 = 32 + this.field_192997_l.size() * this.field_191833_k.field_71466_p.field_78288_b;
      if (!this.field_192997_l.isEmpty()) {
         if (var9) {
            this.func_192994_a(var17, var16 + 26 - var18, this.field_191832_j, var18, 10, 200, 26, 0, 52);
         } else {
            this.func_192994_a(var17, var16, this.field_191832_j, var18, 10, 200, 26, 0, 52);
         }
      }

      this.func_73729_b(var17, var16, 0, var11.func_192667_a() * 26, var14, 26);
      this.func_73729_b(var17 + var14, var16, 200 - var15, var12.func_192667_a() * 26, var15, 26);
      this.func_73729_b(var1 + this.field_191837_o + 3, var2 + this.field_191826_p, this.field_191830_h.func_192291_d().func_192309_b(), 128 + var13.func_192667_a() * 26, 26, 26);
      if (var6) {
         this.field_191833_k.field_71466_p.func_175063_a(this.field_191831_i, (float)(var17 + 5), (float)(var2 + this.field_191826_p + 9), -1);
         if (var7 != null) {
            this.field_191833_k.field_71466_p.func_175063_a(var7, (float)(var1 + this.field_191837_o - var8), (float)(var2 + this.field_191826_p + 9), -1);
         }
      } else {
         this.field_191833_k.field_71466_p.func_175063_a(this.field_191831_i, (float)(var1 + this.field_191837_o + 32), (float)(var2 + this.field_191826_p + 9), -1);
         if (var7 != null) {
            this.field_191833_k.field_71466_p.func_175063_a(var7, (float)(var1 + this.field_191837_o + this.field_191832_j - var8 - 5), (float)(var2 + this.field_191826_p + 9), -1);
         }
      }

      int var19;
      if (var9) {
         for(var19 = 0; var19 < this.field_192997_l.size(); ++var19) {
            this.field_191833_k.field_71466_p.func_211126_b((String)this.field_192997_l.get(var19), (float)(var17 + 5), (float)(var16 + 26 - var18 + 7 + var19 * this.field_191833_k.field_71466_p.field_78288_b), -5592406);
         }
      } else {
         for(var19 = 0; var19 < this.field_192997_l.size(); ++var19) {
            this.field_191833_k.field_71466_p.func_211126_b((String)this.field_192997_l.get(var19), (float)(var17 + 5), (float)(var2 + this.field_191826_p + 9 + 17 + var19 * this.field_191833_k.field_71466_p.field_78288_b), -5592406);
         }
      }

      RenderHelper.func_74520_c();
      this.field_191833_k.func_175599_af().func_184391_a((EntityLivingBase)null, this.field_191830_h.func_192298_b(), var1 + this.field_191837_o + 8, var2 + this.field_191826_p + 5);
   }

   protected void func_192994_a(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9) {
      this.func_73729_b(var1, var2, var8, var9, var5, var5);
      this.func_192993_a(var1 + var5, var2, var3 - var5 - var5, var5, var8 + var5, var9, var6 - var5 - var5, var7);
      this.func_73729_b(var1 + var3 - var5, var2, var8 + var6 - var5, var9, var5, var5);
      this.func_73729_b(var1, var2 + var4 - var5, var8, var9 + var7 - var5, var5, var5);
      this.func_192993_a(var1 + var5, var2 + var4 - var5, var3 - var5 - var5, var5, var8 + var5, var9 + var7 - var5, var6 - var5 - var5, var7);
      this.func_73729_b(var1 + var3 - var5, var2 + var4 - var5, var8 + var6 - var5, var9 + var7 - var5, var5, var5);
      this.func_192993_a(var1, var2 + var5, var5, var4 - var5 - var5, var8, var9 + var5, var6, var7 - var5 - var5);
      this.func_192993_a(var1 + var5, var2 + var5, var3 - var5 - var5, var4 - var5 - var5, var8 + var5, var9 + var5, var6 - var5 - var5, var7 - var5 - var5);
      this.func_192993_a(var1 + var3 - var5, var2 + var5, var5, var4 - var5 - var5, var8 + var6 - var5, var9 + var5, var6, var7 - var5 - var5);
   }

   protected void func_192993_a(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      for(int var9 = 0; var9 < var3; var9 += var7) {
         int var10 = var1 + var9;
         int var11 = Math.min(var7, var3 - var9);

         for(int var12 = 0; var12 < var4; var12 += var8) {
            int var13 = var2 + var12;
            int var14 = Math.min(var8, var4 - var12);
            this.func_73729_b(var10, var13, var5, var6, var11, var14);
         }
      }

   }

   public boolean func_191816_c(int var1, int var2, int var3, int var4) {
      if (!this.field_191830_h.func_193224_j() || this.field_191836_n != null && this.field_191836_n.func_192105_a()) {
         int var5 = var1 + this.field_191837_o;
         int var6 = var5 + 26;
         int var7 = var2 + this.field_191826_p;
         int var8 = var7 + 26;
         return var3 >= var5 && var3 <= var6 && var4 >= var7 && var4 <= var8;
      } else {
         return false;
      }
   }

   public void func_191825_b() {
      if (this.field_191834_l == null && this.field_191829_g.func_192070_b() != null) {
         this.field_191834_l = this.func_191818_a(this.field_191829_g);
         if (this.field_191834_l != null) {
            this.field_191834_l.func_191822_a(this);
         }
      }

   }

   public int func_191820_c() {
      return this.field_191826_p;
   }

   public int func_191823_d() {
      return this.field_191837_o;
   }
}
