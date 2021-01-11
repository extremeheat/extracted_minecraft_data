package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiNewChat extends Gui {
   private static final Logger field_146249_a = LogManager.getLogger();
   private final Minecraft field_146247_f;
   private final List<String> field_146248_g = Lists.newArrayList();
   private final List<ChatLine> field_146252_h = Lists.newArrayList();
   private final List<ChatLine> field_146253_i = Lists.newArrayList();
   private int field_146250_j;
   private boolean field_146251_k;

   public GuiNewChat(Minecraft var1) {
      super();
      this.field_146247_f = var1;
   }

   public void func_146230_a(int var1) {
      if (this.field_146247_f.field_71474_y.field_74343_n != EntityPlayer.EnumChatVisibility.HIDDEN) {
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
            GlStateManager.func_179094_E();
            GlStateManager.func_179109_b(2.0F, 20.0F, 0.0F);
            GlStateManager.func_179152_a(var7, var7, 1.0F);

            int var9;
            int var11;
            int var14;
            for(var9 = 0; var9 + this.field_146250_j < this.field_146253_i.size() && var9 < var2; ++var9) {
               ChatLine var10 = (ChatLine)this.field_146253_i.get(var9 + this.field_146250_j);
               if (var10 != null) {
                  var11 = var1 - var10.func_74540_b();
                  if (var11 < 200 || var3) {
                     double var12 = (double)var11 / 200.0D;
                     var12 = 1.0D - var12;
                     var12 *= 10.0D;
                     var12 = MathHelper.func_151237_a(var12, 0.0D, 1.0D);
                     var12 *= var12;
                     var14 = (int)(255.0D * var12);
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
                        GlStateManager.func_179147_l();
                        this.field_146247_f.field_71466_p.func_175063_a(var17, (float)var15, (float)(var16 - 8), 16777215 + (var14 << 24));
                        GlStateManager.func_179118_c();
                        GlStateManager.func_179084_k();
                     }
                  }
               }
            }

            if (var3) {
               var9 = this.field_146247_f.field_71466_p.field_78288_b;
               GlStateManager.func_179109_b(-3.0F, 0.0F, 0.0F);
               int var18 = var5 * var9 + var5;
               var11 = var4 * var9 + var4;
               int var19 = this.field_146250_j * var11 / var5;
               int var13 = var11 * var11 / var18;
               if (var18 != var11) {
                  var14 = var19 > 0 ? 170 : 96;
                  int var20 = this.field_146251_k ? 13382451 : 3355562;
                  func_73734_a(0, -var19, 2, -var19 - var13, var20 + (var14 << 24));
                  func_73734_a(2, -var19, 1, -var19 - var13, 13421772 + (var14 << 24));
               }
            }

            GlStateManager.func_179121_F();
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

   private void func_146237_a(IChatComponent var1, int var2, int var3, boolean var4) {
      if (var2 != 0) {
         this.func_146242_c(var2);
      }

      int var5 = MathHelper.func_76141_d((float)this.func_146228_f() / this.func_146244_h());
      List var6 = GuiUtilRenderComponents.func_178908_a(var1, var5, this.field_146247_f.field_71466_p, false, false);
      boolean var7 = this.func_146241_e();

      IChatComponent var9;
      for(Iterator var8 = var6.iterator(); var8.hasNext(); this.field_146253_i.add(0, new ChatLine(var3, var9, var2))) {
         var9 = (IChatComponent)var8.next();
         if (var7 && this.field_146250_j > 0) {
            this.field_146251_k = true;
            this.func_146229_b(1);
         }
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

   public List<String> func_146238_c() {
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
         ScaledResolution var3 = new ScaledResolution(this.field_146247_f);
         int var4 = var3.func_78325_e();
         float var5 = this.func_146244_h();
         int var6 = var1 / var4 - 3;
         int var7 = var2 / var4 - 27;
         var6 = MathHelper.func_76141_d((float)var6 / var5);
         var7 = MathHelper.func_76141_d((float)var7 / var5);
         if (var6 >= 0 && var7 >= 0) {
            int var8 = Math.min(this.func_146232_i(), this.field_146253_i.size());
            if (var6 <= MathHelper.func_76141_d((float)this.func_146228_f() / this.func_146244_h()) && var7 < this.field_146247_f.field_71466_p.field_78288_b * var8 + var8) {
               int var9 = var7 / this.field_146247_f.field_71466_p.field_78288_b + this.field_146250_j;
               if (var9 >= 0 && var9 < this.field_146253_i.size()) {
                  ChatLine var10 = (ChatLine)this.field_146253_i.get(var9);
                  int var11 = 0;
                  Iterator var12 = var10.func_151461_a().iterator();

                  while(var12.hasNext()) {
                     IChatComponent var13 = (IChatComponent)var12.next();
                     if (var13 instanceof ChatComponentText) {
                        var11 += this.field_146247_f.field_71466_p.func_78256_a(GuiUtilRenderComponents.func_178909_a(((ChatComponentText)var13).func_150265_g(), false));
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

      ChatLine var3;
      while(var2.hasNext()) {
         var3 = (ChatLine)var2.next();
         if (var3.func_74539_c() == var1) {
            var2.remove();
         }
      }

      var2 = this.field_146252_h.iterator();

      while(var2.hasNext()) {
         var3 = (ChatLine)var2.next();
         if (var3.func_74539_c() == var1) {
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
