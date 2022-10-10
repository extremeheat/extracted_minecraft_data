package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
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
         int var3 = this.field_146253_i.size();
         double var4 = this.field_146247_f.field_71474_y.field_74357_r * 0.8999999761581421D + 0.10000000149011612D;
         if (var3 > 0) {
            boolean var6 = false;
            if (this.func_146241_e()) {
               var6 = true;
            }

            double var7 = this.func_194815_g();
            int var9 = MathHelper.func_76143_f((double)this.func_146228_f() / var7);
            GlStateManager.func_179094_E();
            GlStateManager.func_179109_b(2.0F, 8.0F, 0.0F);
            GlStateManager.func_179139_a(var7, var7, 1.0D);
            int var10 = 0;

            int var11;
            int var13;
            int var16;
            for(var11 = 0; var11 + this.field_146250_j < this.field_146253_i.size() && var11 < var2; ++var11) {
               ChatLine var12 = (ChatLine)this.field_146253_i.get(var11 + this.field_146250_j);
               if (var12 != null) {
                  var13 = var1 - var12.func_74540_b();
                  if (var13 < 200 || var6) {
                     double var14 = (double)var13 / 200.0D;
                     var14 = 1.0D - var14;
                     var14 *= 10.0D;
                     var14 = MathHelper.func_151237_a(var14, 0.0D, 1.0D);
                     var14 *= var14;
                     var16 = (int)(255.0D * var14);
                     if (var6) {
                        var16 = 255;
                     }

                     var16 = (int)((double)var16 * var4);
                     ++var10;
                     if (var16 > 3) {
                        boolean var17 = false;
                        int var18 = -var11 * 9;
                        func_73734_a(-2, var18 - 9, 0 + var9 + 4, var18, var16 / 2 << 24);
                        String var19 = var12.func_151461_a().func_150254_d();
                        GlStateManager.func_179147_l();
                        this.field_146247_f.field_71466_p.func_175063_a(var19, 0.0F, (float)(var18 - 8), 16777215 + (var16 << 24));
                        GlStateManager.func_179118_c();
                        GlStateManager.func_179084_k();
                     }
                  }
               }
            }

            if (var6) {
               var11 = this.field_146247_f.field_71466_p.field_78288_b;
               GlStateManager.func_179109_b(-3.0F, 0.0F, 0.0F);
               int var20 = var3 * var11 + var3;
               var13 = var10 * var11 + var10;
               int var21 = this.field_146250_j * var13 / var3;
               int var15 = var13 * var13 / var20;
               if (var20 != var13) {
                  var16 = var21 > 0 ? 170 : 96;
                  int var22 = this.field_146251_k ? 13382451 : 3355562;
                  func_73734_a(0, -var21, 2, -var21 - var15, var22 + (var16 << 24));
                  func_73734_a(2, -var21, 1, -var21 - var15, 13421772 + (var16 << 24));
               }
            }

            GlStateManager.func_179121_F();
         }
      }
   }

   public void func_146231_a(boolean var1) {
      this.field_146253_i.clear();
      this.field_146252_h.clear();
      if (var1) {
         this.field_146248_g.clear();
      }

   }

   public void func_146227_a(ITextComponent var1) {
      this.func_146234_a(var1, 0);
   }

   public void func_146234_a(ITextComponent var1, int var2) {
      this.func_146237_a(var1, var2, this.field_146247_f.field_71456_v.func_73834_c(), false);
      field_146249_a.info("[CHAT] {}", var1.getString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n"));
   }

   private void func_146237_a(ITextComponent var1, int var2, int var3, boolean var4) {
      if (var2 != 0) {
         this.func_146242_c(var2);
      }

      int var5 = MathHelper.func_76128_c((double)this.func_146228_f() / this.func_194815_g());
      List var6 = GuiUtilRenderComponents.func_178908_a(var1, var5, this.field_146247_f.field_71466_p, false, false);
      boolean var7 = this.func_146241_e();

      ITextComponent var9;
      for(Iterator var8 = var6.iterator(); var8.hasNext(); this.field_146253_i.add(0, new ChatLine(var3, var9, var2))) {
         var9 = (ITextComponent)var8.next();
         if (var7 && this.field_146250_j > 0) {
            this.field_146251_k = true;
            this.func_194813_a(1.0D);
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

   public void func_194813_a(double var1) {
      this.field_146250_j = (int)((double)this.field_146250_j + var1);
      int var3 = this.field_146253_i.size();
      if (this.field_146250_j > var3 - this.func_146232_i()) {
         this.field_146250_j = var3 - this.func_146232_i();
      }

      if (this.field_146250_j <= 0) {
         this.field_146250_j = 0;
         this.field_146251_k = false;
      }

   }

   @Nullable
   public ITextComponent func_194817_a(double var1, double var3) {
      if (!this.func_146241_e()) {
         return null;
      } else {
         double var5 = this.func_194815_g();
         double var7 = var1 - 2.0D;
         double var9 = (double)this.field_146247_f.field_195558_d.func_198087_p() - var3 - 40.0D;
         var7 = (double)MathHelper.func_76128_c(var7 / var5);
         var9 = (double)MathHelper.func_76128_c(var9 / var5);
         if (var7 >= 0.0D && var9 >= 0.0D) {
            int var11 = Math.min(this.func_146232_i(), this.field_146253_i.size());
            if (var7 <= (double)MathHelper.func_76128_c((double)this.func_146228_f() / this.func_194815_g()) && var9 < (double)(this.field_146247_f.field_71466_p.field_78288_b * var11 + var11)) {
               int var12 = (int)(var9 / (double)this.field_146247_f.field_71466_p.field_78288_b + (double)this.field_146250_j);
               if (var12 >= 0 && var12 < this.field_146253_i.size()) {
                  ChatLine var13 = (ChatLine)this.field_146253_i.get(var12);
                  int var14 = 0;
                  Iterator var15 = var13.func_151461_a().iterator();

                  while(var15.hasNext()) {
                     ITextComponent var16 = (ITextComponent)var15.next();
                     if (var16 instanceof TextComponentString) {
                        var14 += this.field_146247_f.field_71466_p.func_78256_a(GuiUtilRenderComponents.func_178909_a(((TextComponentString)var16).func_150265_g(), false));
                        if ((double)var14 > var7) {
                           return var16;
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
      return func_194814_b(this.field_146247_f.field_71474_y.field_96692_F);
   }

   public int func_146246_g() {
      return func_194816_c(this.func_146241_e() ? this.field_146247_f.field_71474_y.field_96694_H : this.field_146247_f.field_71474_y.field_96693_G);
   }

   public double func_194815_g() {
      return this.field_146247_f.field_71474_y.field_96691_E;
   }

   public static int func_194814_b(double var0) {
      boolean var2 = true;
      boolean var3 = true;
      return MathHelper.func_76128_c(var0 * 280.0D + 40.0D);
   }

   public static int func_194816_c(double var0) {
      boolean var2 = true;
      boolean var3 = true;
      return MathHelper.func_76128_c(var0 * 160.0D + 20.0D);
   }

   public int func_146232_i() {
      return this.func_146246_g() / 9;
   }
}
