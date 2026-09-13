package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer$EnumChatVisibility;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

public class GuiNewChat extends Gui {
   private static final Logger field_146249_a = LogManager.getLogger();
   private final Minecraft field_146247_f;
   private final List field_146248_g = new ArrayList();
   private final List field_146252_h = new ArrayList();
   private final List field_146253_i = new ArrayList();
   private int field_146250_j;
   private boolean field_146251_k;

   public GuiNewChat(Minecraft var1) {
      super();
      this.field_146247_f = var1;
   }

   public void func_146230_a(int var1) {
      if (this.field_146247_f.field_71474_y.field_74343_n != EntityPlayer$EnumChatVisibility.HIDDEN) {
         int var2 = this.func_146232_i();
         boolean var3 = false;
         int var4 = 0;
         int var5 = this.field_146253_i.size();
         float var6 = this.field_146247_f.field_71474_y.field_74357_r * 0.9F + 0.1F;
         if (var5 > 0) {
            if (this.func_146241_e()) {
               var3 = true;
            }

            float var7 = this.func_146244_h();
            int var8 = MathHelper.func_76123_f((float)this.func_146228_f() / var7);
            GL11.glPushMatrix();
            GL11.glTranslatef(2.0F, 20.0F, 0.0F);
            GL11.glScalef(var7, var7, 1.0F);

            for(int var9 = 0; var9 + this.field_146250_j < this.field_146253_i.size() && var9 < var2; ++var9) {
               ChatLine var10 = (ChatLine)this.field_146253_i.get(var9 + this.field_146250_j);
               if (var10 != null) {
                  int var11 = var1 - var10.func_74540_b();
                  if (var11 < 200 || var3) {
                     double var12 = (double)var11 / 200.0;
                     var12 = 1.0 - var12;
                     var12 *= 10.0;
                     if (var12 < 0.0) {
                        var12 = 0.0;
                     }

                     if (var12 > 1.0) {
                        var12 = 1.0;
                     }

                     var12 *= var12;
                     int var14 = (int)(255.0 * var12);
                     if (var3) {
                        var14 = 255;
                     }

                     var14 = (int)((float)var14 * var6);
                     ++var4;
                     if (var14 > 3) {
                        byte var15 = 0;
                        int var16 = -var9 * 9;
                        func_73734_a(var15, var16 - 9, var15 + var8 + 4, var16, var14 / 2 << 24);
                        String var17 = var10.func_151461_a().func_150254_d();
                        this.field_146247_f.field_71466_p.func_78261_a(var17, var15, var16 - 8, 16777215 + (var14 << 24));
                        GL11.glDisable(3008);
                     }
                  }
               }
            }

            if (var3) {
               int var18 = this.field_146247_f.field_71466_p.field_78288_b;
               GL11.glTranslatef(-3.0F, 0.0F, 0.0F);
               int var19 = var5 * var18 + var5;
               int var20 = var4 * var18 + var4;
               int var24 = this.field_146250_j * var20 / var5;
               int var13 = var20 * var20 / var19;
               if (var19 != var20) {
                  int var26 = var24 > 0 ? 170 : 96;
                  int var27 = this.field_146251_k ? 13382451 : 3355562;
                  func_73734_a(0, -var24, 2, -var24 - var13, var27 + (var26 << 24));
                  func_73734_a(2, -var24, 1, -var24 - var13, 13421772 + (var26 << 24));
               }
            }

            GL11.glPopMatrix();
         }
      }
   }

   public void func_146231_a() {
      this.field_146253_i.clear();
      this.field_146252_h.clear();
      this.field_146248_g.clear();
   }

   public void func_146227_a(IChatComponent var1) {
      this.func_146234_a(var1, 0);
   }

   public void func_146234_a(IChatComponent var1, int var2) {
      this.func_146237_a(var1, var2, this.field_146247_f.field_71456_v.func_73834_c(), false);
      field_146249_a.info("[CHAT] " + var1.func_150260_c());
   }

   private String func_146235_b(String var1) {
      return Minecraft.func_71410_x().field_71474_y.field_74344_o ? var1 : EnumChatFormatting.func_110646_a(var1);
   }

   private void func_146237_a(IChatComponent var1, int var2, int var3, boolean var4) {
      if (var2 != 0) {
         this.func_146242_c(var2);
      }

      int var5 = MathHelper.func_76141_d((float)this.func_146228_f() / this.func_146244_h());
      int var6 = 0;
      ChatComponentText var7 = new ChatComponentText("");
      ArrayList var8 = Lists.newArrayList();
      ArrayList var9 = Lists.newArrayList(var1);

      for(int var10 = 0; var10 < var9.size(); ++var10) {
         IChatComponent var11 = (IChatComponent)var9.get(var10);
         String var12 = this.func_146235_b(var11.func_150256_b().func_150218_j() + var11.func_150261_e());
         int var13 = this.field_146247_f.field_71466_p.func_78256_a(var12);
         ChatComponentText var14 = new ChatComponentText(var12);
         var14.func_150255_a(var11.func_150256_b().func_150232_l());
         boolean var15 = false;
         if (var6 + var13 > var5) {
            String var16 = this.field_146247_f.field_71466_p.func_78262_a(var12, var5 - var6, false);
            String var17 = var16.length() < var12.length() ? var12.substring(var16.length()) : null;
            if (var17 != null && var17.length() > 0) {
               int var18 = var16.lastIndexOf(" ");
               if (var18 >= 0 && this.field_146247_f.field_71466_p.func_78256_a(var12.substring(0, var18)) > 0) {
                  var16 = var12.substring(0, var18);
                  var17 = var12.substring(var18);
               }

               ChatComponentText var19 = new ChatComponentText(var17);
               var19.func_150255_a(var11.func_150256_b().func_150232_l());
               var9.add(var10 + 1, var19);
            }

            var13 = this.field_146247_f.field_71466_p.func_78256_a(var16);
            var14 = new ChatComponentText(var16);
            var14.func_150255_a(var11.func_150256_b().func_150232_l());
            var15 = true;
         }

         if (var6 + var13 <= var5) {
            var6 += var13;
            var7.func_150257_a(var14);
         } else {
            var15 = true;
         }

         if (var15) {
            var8.add(var7);
            var6 = 0;
            var7 = new ChatComponentText("");
         }
      }

      var8.add(var7);
      boolean var20 = this.func_146241_e();

      for(IChatComponent var22 : var8) {
         if (var20 && this.field_146250_j > 0) {
            this.field_146251_k = true;
            this.func_146229_b(1);
         }

         this.field_146253_i.add(0, new ChatLine(var3, var22, var2));
      }

      while(this.field_146253_i.size() > 100) {
         this.field_146253_i.remove(this.field_146253_i.size() - 1);
      }

      if (!var4) {
         this.field_146252_h.add(0, new ChatLine(var3, var1, var2));

         while(this.field_146252_h.size() > 100) {
            this.field_146252_h.remove(this.field_146252_h.size() - 1);
         }
      }
   }

   public void func_146245_b() {
      this.field_146253_i.clear();
      this.func_146240_d();

      for(int var1 = this.field_146252_h.size() - 1; var1 >= 0; --var1) {
         ChatLine var2 = (ChatLine)this.field_146252_h.get(var1);
         this.func_146237_a(var2.func_151461_a(), var2.func_74539_c(), var2.func_74540_b(), true);
      }
   }

   public List func_146238_c() {
      return this.field_146248_g;
   }

   public void func_146239_a(String var1) {
      if (this.field_146248_g.isEmpty() || !((String)this.field_146248_g.get(this.field_146248_g.size() - 1)).equals(var1)) {
         this.field_146248_g.add(var1);
      }
   }

   public void func_146240_d() {
      this.field_146250_j = 0;
      this.field_146251_k = false;
   }

   public void func_146229_b(int var1) {
      this.field_146250_j += var1;
      int var2 = this.field_146253_i.size();
      if (this.field_146250_j > var2 - this.func_146232_i()) {
         this.field_146250_j = var2 - this.func_146232_i();
      }

      if (this.field_146250_j <= 0) {
         this.field_146250_j = 0;
         this.field_146251_k = false;
      }
   }

   public IChatComponent func_146236_a(int var1, int var2) {
      if (!this.func_146241_e()) {
         return null;
      } else {
         ScaledResolution var3 = new ScaledResolution(this.field_146247_f, this.field_146247_f.field_71443_c, this.field_146247_f.field_71440_d);
         int var4 = var3.func_78325_e();
         float var5 = this.func_146244_h();
         int var6 = var1 / var4 - 3;
         int var7 = var2 / var4 - 27;
         var6 = MathHelper.func_76141_d((float)var6 / var5);
         var7 = MathHelper.func_76141_d((float)var7 / var5);
         if (var6 >= 0 && var7 >= 0) {
            int var8 = Math.min(this.func_146232_i(), this.field_146253_i.size());
            if (var6 <= MathHelper.func_76141_d((float)this.func_146228_f() / this.func_146244_h())
               && var7 < this.field_146247_f.field_71466_p.field_78288_b * var8 + var8) {
               int var9 = var7 / this.field_146247_f.field_71466_p.field_78288_b + this.field_146250_j;
               if (var9 >= 0 && var9 < this.field_146253_i.size()) {
                  ChatLine var10 = (ChatLine)this.field_146253_i.get(var9);
                  int var11 = 0;

                  for(IChatComponent var13 : var10.func_151461_a()) {
                     if (var13 instanceof ChatComponentText) {
                        var11 += this.field_146247_f.field_71466_p.func_78256_a(this.func_146235_b(((ChatComponentText)var13).func_150265_g()));
                        if (var11 > var6) {
                           return var13;
                        }
                     }
                  }
               }

               return null;
            } else {
               return null;
            }
         } else {
            return null;
         }
      }
   }

   public boolean func_146241_e() {
      return this.field_146247_f.field_71462_r instanceof GuiChat;
   }

   public void func_146242_c(int var1) {
      Iterator var2 = this.field_146253_i.iterator();

      while(var2.hasNext()) {
         ChatLine var3 = (ChatLine)var2.next();
         if (var3.func_74539_c() == var1) {
            var2.remove();
         }
      }

      var2 = this.field_146252_h.iterator();

      while(var2.hasNext()) {
         ChatLine var5 = (ChatLine)var2.next();
         if (var5.func_74539_c() == var1) {
            var2.remove();
            break;
         }
      }
   }

   public int func_146228_f() {
      return func_146233_a(this.field_146247_f.field_71474_y.field_96692_F);
   }

   public int func_146246_g() {
      return func_146243_b(this.func_146241_e() ? this.field_146247_f.field_71474_y.field_96694_H : this.field_146247_f.field_71474_y.field_96693_G);
   }

   public float func_146244_h() {
      return this.field_146247_f.field_71474_y.field_96691_E;
   }

   public static int func_146233_a(float var0) {
      short var1 = 320;
      byte var2 = 40;
      return MathHelper.func_76141_d(var0 * (float)(var1 - var2) + (float)var2);
   }

   public static int func_146243_b(float var0) {
      short var1 = 180;
      byte var2 = 20;
      return MathHelper.func_76141_d(var0 * (float)(var1 - var2) + (float)var2);
   }

   public int func_146232_i() {
      return this.func_146246_g() / 9;
   }
}
