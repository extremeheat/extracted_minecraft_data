package net.minecraft.client.gui;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C16PacketClientStatus$EnumState;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.Charsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

public class GuiWinGame extends GuiScreen {
   private static final Logger field_146580_a = LogManager.getLogger();
   private static final ResourceLocation field_146576_f = new ResourceLocation("textures/gui/title/minecraft.png");
   private static final ResourceLocation field_146577_g = new ResourceLocation("textures/misc/vignette.png");
   private int field_146581_h;
   private List field_146582_i;
   private int field_146579_r;
   private float field_146578_s = 0.5F;

   public GuiWinGame() {
      super();
   }

   @Override
   public void func_73876_c() {
      ++this.field_146581_h;
      float var1 = (float)(this.field_146579_r + this.field_146295_m + this.field_146295_m + 24) / this.field_146578_s;
      if ((float)this.field_146581_h > var1) {
         this.func_146574_g();
      }
   }

   @Override
   protected void func_73869_a(char var1, int var2) {
      if (var2 == 1) {
         this.func_146574_g();
      }
   }

   private void func_146574_g() {
      this.field_146297_k.field_71439_g.field_71174_a.func_147297_a(new C16PacketClientStatus(C16PacketClientStatus$EnumState.PERFORM_RESPAWN));
      this.field_146297_k.func_147108_a(null);
   }

   @Override
   public boolean func_73868_f() {
      return true;
   }

   @Override
   public void func_73866_w_() {
      if (this.field_146582_i == null) {
         this.field_146582_i = new ArrayList();

         try {
            String var1 = "";
            String var2 = "" + EnumChatFormatting.WHITE + EnumChatFormatting.OBFUSCATED + EnumChatFormatting.GREEN + EnumChatFormatting.AQUA;
            short var3 = 274;
            BufferedReader var4 = new BufferedReader(
               new InputStreamReader(this.field_146297_k.func_110442_L().func_110536_a(new ResourceLocation("texts/end.txt")).func_110527_b(), Charsets.UTF_8)
            );
            Random var5 = new Random(8124371L);

            while((var1 = var4.readLine()) != null) {
               String var7;
               String var8;
               for(var1 = var1.replaceAll("PLAYERNAME", this.field_146297_k.func_110432_I().func_111285_a());
                  var1.contains(var2);
                  var1 = var7 + EnumChatFormatting.WHITE + EnumChatFormatting.OBFUSCATED + "XXXXXXXX".substring(0, var5.nextInt(4) + 3) + var8
               ) {
                  int var6 = var1.indexOf(var2);
                  var7 = var1.substring(0, var6);
                  var8 = var1.substring(var6 + var2.length());
               }

               this.field_146582_i.addAll(this.field_146297_k.field_71466_p.func_78271_c(var1, var3));
               this.field_146582_i.add("");
            }

            for(int var16 = 0; var16 < 8; ++var16) {
               this.field_146582_i.add("");
            }

            var4 = new BufferedReader(
               new InputStreamReader(
                  this.field_146297_k.func_110442_L().func_110536_a(new ResourceLocation("texts/credits.txt")).func_110527_b(), Charsets.UTF_8
               )
            );

            while((var1 = var4.readLine()) != null) {
               var1 = var1.replaceAll("PLAYERNAME", this.field_146297_k.func_110432_I().func_111285_a());
               var1 = var1.replaceAll("\t", "    ");
               this.field_146582_i.addAll(this.field_146297_k.field_71466_p.func_78271_c(var1, var3));
               this.field_146582_i.add("");
            }

            this.field_146579_r = this.field_146582_i.size() * 12;
         } catch (Exception var9) {
            field_146580_a.error("Couldn't load credits", var9);
         }
      }
   }

   private void func_146575_b(int var1, int var2, float var3) {
      Tessellator var4 = Tessellator.field_78398_a;
      this.field_146297_k.func_110434_K().func_110577_a(Gui.field_110325_k);
      var4.func_78382_b();
      var4.func_78369_a(1.0F, 1.0F, 1.0F, 1.0F);
      int var5 = this.field_146294_l;
      float var6 = 0.0F - ((float)this.field_146581_h + var3) * 0.5F * this.field_146578_s;
      float var7 = (float)this.field_146295_m - ((float)this.field_146581_h + var3) * 0.5F * this.field_146578_s;
      float var8 = 0.015625F;
      float var9 = ((float)this.field_146581_h + var3 - 0.0F) * 0.02F;
      float var10 = (float)(this.field_146579_r + this.field_146295_m + this.field_146295_m + 24) / this.field_146578_s;
      float var11 = (var10 - 20.0F - ((float)this.field_146581_h + var3)) * 0.005F;
      if (var11 < var9) {
         var9 = var11;
      }

      if (var9 > 1.0F) {
         var9 = 1.0F;
      }

      var9 *= var9;
      var9 = var9 * 96.0F / 255.0F;
      var4.func_78386_a(var9, var9, var9);
      var4.func_78374_a(0.0, (double)this.field_146295_m, (double)this.field_73735_i, 0.0, (double)(var6 * var8));
      var4.func_78374_a((double)var5, (double)this.field_146295_m, (double)this.field_73735_i, (double)((float)var5 * var8), (double)(var6 * var8));
      var4.func_78374_a((double)var5, 0.0, (double)this.field_73735_i, (double)((float)var5 * var8), (double)(var7 * var8));
      var4.func_78374_a(0.0, 0.0, (double)this.field_73735_i, 0.0, (double)(var7 * var8));
      var4.func_78381_a();
   }

   @Override
   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146575_b(var1, var2, var3);
      Tessellator var4 = Tessellator.field_78398_a;
      short var5 = 274;
      int var6 = this.field_146294_l / 2 - var5 / 2;
      int var7 = this.field_146295_m + 50;
      float var8 = -((float)this.field_146581_h + var3) * this.field_146578_s;
      GL11.glPushMatrix();
      GL11.glTranslatef(0.0F, var8, 0.0F);
      this.field_146297_k.func_110434_K().func_110577_a(field_146576_f);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.func_73729_b(var6, var7, 0, 0, 155, 44);
      this.func_73729_b(var6 + 155, var7, 0, 45, 155, 44);
      var4.func_78378_d(16777215);
      int var9 = var7 + 200;

      for(int var10 = 0; var10 < this.field_146582_i.size(); ++var10) {
         if (var10 == this.field_146582_i.size() - 1) {
            float var11 = (float)var9 + var8 - (float)(this.field_146295_m / 2 - 6);
            if (var11 < 0.0F) {
               GL11.glTranslatef(0.0F, -var11, 0.0F);
            }
         }

         if ((float)var9 + var8 + 12.0F + 8.0F > 0.0F && (float)var9 + var8 < (float)this.field_146295_m) {
            String var13 = (String)this.field_146582_i.get(var10);
            if (var13.startsWith("[C]")) {
               this.field_146289_q.func_78261_a(var13.substring(3), var6 + (var5 - this.field_146289_q.func_78256_a(var13.substring(3))) / 2, var9, 16777215);
            } else {
               this.field_146289_q.field_78289_c.setSeed((long)var10 * 4238972211L + (long)(this.field_146581_h / 4));
               this.field_146289_q.func_78261_a(var13, var6, var9, 16777215);
            }
         }

         var9 += 12;
      }

      GL11.glPopMatrix();
      this.field_146297_k.func_110434_K().func_110577_a(field_146577_g);
      GL11.glEnable(3042);
      GL11.glBlendFunc(0, 769);
      var4.func_78382_b();
      var4.func_78369_a(1.0F, 1.0F, 1.0F, 1.0F);
      int var12 = this.field_146294_l;
      int var14 = this.field_146295_m;
      var4.func_78374_a(0.0, (double)var14, (double)this.field_73735_i, 0.0, 1.0);
      var4.func_78374_a((double)var12, (double)var14, (double)this.field_73735_i, 1.0, 1.0);
      var4.func_78374_a((double)var12, 0.0, (double)this.field_73735_i, 1.0, 0.0);
      var4.func_78374_a(0.0, 0.0, (double)this.field_73735_i, 0.0, 0.0);
      var4.func_78381_a();
      GL11.glDisable(3042);
      super.func_73863_a(var1, var2, var3);
   }
}
