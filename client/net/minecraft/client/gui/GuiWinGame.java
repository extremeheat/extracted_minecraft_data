package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiWinGame extends GuiScreen {
   private static final Logger field_146580_a = LogManager.getLogger();
   private static final ResourceLocation field_146576_f = new ResourceLocation("textures/gui/title/minecraft.png");
   private static final ResourceLocation field_194401_g = new ResourceLocation("textures/gui/title/edition.png");
   private static final ResourceLocation field_146577_g = new ResourceLocation("textures/misc/vignette.png");
   private final boolean field_193980_h;
   private final Runnable field_193981_i;
   private float field_146581_h;
   private List<String> field_146582_i;
   private int field_146579_r;
   private float field_146578_s = 0.5F;

   public GuiWinGame(boolean var1, Runnable var2) {
      super();
      this.field_193980_h = var1;
      this.field_193981_i = var2;
      if (!var1) {
         this.field_146578_s = 0.75F;
      }

   }

   public void func_73876_c() {
      this.field_146297_k.func_181535_r().func_73660_a();
      this.field_146297_k.func_147118_V().func_73660_a();
      float var1 = (float)(this.field_146579_r + this.field_146295_m + this.field_146295_m + 24) / this.field_146578_s;
      if (this.field_146581_h > var1) {
         this.func_146574_g();
      }

   }

   public void func_195122_V_() {
      this.func_146574_g();
   }

   private void func_146574_g() {
      this.field_193981_i.run();
      this.field_146297_k.func_147108_a((GuiScreen)null);
   }

   protected void func_73866_w_() {
      if (this.field_146582_i == null) {
         this.field_146582_i = Lists.newArrayList();
         IResource var1 = null;

         try {
            String var2 = "" + TextFormatting.WHITE + TextFormatting.OBFUSCATED + TextFormatting.GREEN + TextFormatting.AQUA;
            boolean var3 = true;
            InputStream var4;
            BufferedReader var5;
            if (this.field_193980_h) {
               var1 = this.field_146297_k.func_195551_G().func_199002_a(new ResourceLocation("texts/end.txt"));
               var4 = var1.func_199027_b();
               var5 = new BufferedReader(new InputStreamReader(var4, StandardCharsets.UTF_8));
               Random var6 = new Random(8124371L);

               label113:
               while(true) {
                  String var7;
                  int var8;
                  if ((var7 = var5.readLine()) == null) {
                     var4.close();
                     var8 = 0;

                     while(true) {
                        if (var8 >= 8) {
                           break label113;
                        }

                        this.field_146582_i.add("");
                        ++var8;
                     }
                  }

                  String var9;
                  String var10;
                  for(var7 = var7.replaceAll("PLAYERNAME", this.field_146297_k.func_110432_I().func_111285_a()); var7.contains(var2); var7 = var9 + TextFormatting.WHITE + TextFormatting.OBFUSCATED + "XXXXXXXX".substring(0, var6.nextInt(4) + 3) + var10) {
                     var8 = var7.indexOf(var2);
                     var9 = var7.substring(0, var8);
                     var10 = var7.substring(var8 + var2.length());
                  }

                  this.field_146582_i.addAll(this.field_146297_k.field_71466_p.func_78271_c(var7, 274));
                  this.field_146582_i.add("");
               }
            }

            var4 = this.field_146297_k.func_195551_G().func_199002_a(new ResourceLocation("texts/credits.txt")).func_199027_b();
            var5 = new BufferedReader(new InputStreamReader(var4, StandardCharsets.UTF_8));

            String var16;
            while((var16 = var5.readLine()) != null) {
               var16 = var16.replaceAll("PLAYERNAME", this.field_146297_k.func_110432_I().func_111285_a());
               var16 = var16.replaceAll("\t", "    ");
               this.field_146582_i.addAll(this.field_146297_k.field_71466_p.func_78271_c(var16, 274));
               this.field_146582_i.add("");
            }

            var4.close();
            this.field_146579_r = this.field_146582_i.size() * 12;
         } catch (Exception var14) {
            field_146580_a.error("Couldn't load credits", var14);
         } finally {
            IOUtils.closeQuietly(var1);
         }

      }
   }

   private void func_146575_b(int var1, int var2, float var3) {
      Tessellator var4 = Tessellator.func_178181_a();
      BufferBuilder var5 = var4.func_178180_c();
      this.field_146297_k.func_110434_K().func_110577_a(Gui.field_110325_k);
      var5.func_181668_a(7, DefaultVertexFormats.field_181709_i);
      int var6 = this.field_146294_l;
      float var7 = -this.field_146581_h * 0.5F * this.field_146578_s;
      float var8 = (float)this.field_146295_m - this.field_146581_h * 0.5F * this.field_146578_s;
      float var9 = 0.015625F;
      float var10 = this.field_146581_h * 0.02F;
      float var11 = (float)(this.field_146579_r + this.field_146295_m + this.field_146295_m + 24) / this.field_146578_s;
      float var12 = (var11 - 20.0F - this.field_146581_h) * 0.005F;
      if (var12 < var10) {
         var10 = var12;
      }

      if (var10 > 1.0F) {
         var10 = 1.0F;
      }

      var10 *= var10;
      var10 = var10 * 96.0F / 255.0F;
      var5.func_181662_b(0.0D, (double)this.field_146295_m, (double)this.field_73735_i).func_187315_a(0.0D, (double)(var7 * 0.015625F)).func_181666_a(var10, var10, var10, 1.0F).func_181675_d();
      var5.func_181662_b((double)var6, (double)this.field_146295_m, (double)this.field_73735_i).func_187315_a((double)((float)var6 * 0.015625F), (double)(var7 * 0.015625F)).func_181666_a(var10, var10, var10, 1.0F).func_181675_d();
      var5.func_181662_b((double)var6, 0.0D, (double)this.field_73735_i).func_187315_a((double)((float)var6 * 0.015625F), (double)(var8 * 0.015625F)).func_181666_a(var10, var10, var10, 1.0F).func_181675_d();
      var5.func_181662_b(0.0D, 0.0D, (double)this.field_73735_i).func_187315_a(0.0D, (double)(var8 * 0.015625F)).func_181666_a(var10, var10, var10, 1.0F).func_181675_d();
      var4.func_78381_a();
   }

   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146575_b(var1, var2, var3);
      Tessellator var4 = Tessellator.func_178181_a();
      BufferBuilder var5 = var4.func_178180_c();
      boolean var6 = true;
      int var7 = this.field_146294_l / 2 - 137;
      int var8 = this.field_146295_m + 50;
      this.field_146581_h += var3;
      float var9 = -this.field_146581_h * this.field_146578_s;
      GlStateManager.func_179094_E();
      GlStateManager.func_179109_b(0.0F, var9, 0.0F);
      this.field_146297_k.func_110434_K().func_110577_a(field_146576_f);
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.func_179141_d();
      this.func_73729_b(var7, var8, 0, 0, 155, 44);
      this.func_73729_b(var7 + 155, var8, 0, 45, 155, 44);
      this.field_146297_k.func_110434_K().func_110577_a(field_194401_g);
      func_146110_a(var7 + 88, var8 + 37, 0.0F, 0.0F, 98, 14, 128.0F, 16.0F);
      GlStateManager.func_179118_c();
      int var10 = var8 + 100;

      int var11;
      for(var11 = 0; var11 < this.field_146582_i.size(); ++var11) {
         if (var11 == this.field_146582_i.size() - 1) {
            float var12 = (float)var10 + var9 - (float)(this.field_146295_m / 2 - 6);
            if (var12 < 0.0F) {
               GlStateManager.func_179109_b(0.0F, -var12, 0.0F);
            }
         }

         if ((float)var10 + var9 + 12.0F + 8.0F > 0.0F && (float)var10 + var9 < (float)this.field_146295_m) {
            String var13 = (String)this.field_146582_i.get(var11);
            if (var13.startsWith("[C]")) {
               this.field_146289_q.func_175063_a(var13.substring(3), (float)(var7 + (274 - this.field_146289_q.func_78256_a(var13.substring(3))) / 2), (float)var10, 16777215);
            } else {
               this.field_146289_q.field_78289_c.setSeed((long)((float)((long)var11 * 4238972211L) + this.field_146581_h / 4.0F));
               this.field_146289_q.func_175063_a(var13, (float)var7, (float)var10, 16777215);
            }
         }

         var10 += 12;
      }

      GlStateManager.func_179121_F();
      this.field_146297_k.func_110434_K().func_110577_a(field_146577_g);
      GlStateManager.func_179147_l();
      GlStateManager.func_187401_a(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR);
      var11 = this.field_146294_l;
      int var14 = this.field_146295_m;
      var5.func_181668_a(7, DefaultVertexFormats.field_181709_i);
      var5.func_181662_b(0.0D, (double)var14, (double)this.field_73735_i).func_187315_a(0.0D, 1.0D).func_181666_a(1.0F, 1.0F, 1.0F, 1.0F).func_181675_d();
      var5.func_181662_b((double)var11, (double)var14, (double)this.field_73735_i).func_187315_a(1.0D, 1.0D).func_181666_a(1.0F, 1.0F, 1.0F, 1.0F).func_181675_d();
      var5.func_181662_b((double)var11, 0.0D, (double)this.field_73735_i).func_187315_a(1.0D, 0.0D).func_181666_a(1.0F, 1.0F, 1.0F, 1.0F).func_181675_d();
      var5.func_181662_b(0.0D, 0.0D, (double)this.field_73735_i).func_187315_a(0.0D, 0.0D).func_181666_a(1.0F, 1.0F, 1.0F, 1.0F).func_181675_d();
      var4.func_78381_a();
      GlStateManager.func_179084_k();
      super.func_73863_a(var1, var2, var3);
   }
}
