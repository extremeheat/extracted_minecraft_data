package net.minecraft.client.gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.I18n;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.demo.DemoWorldServer;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.io.Charsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.Project;

public class GuiMainMenu extends GuiScreen implements GuiYesNoCallback {
   private static final Logger field_146974_g = LogManager.getLogger();
   private static final Random field_73976_a = new Random();
   private float field_73974_b;
   private String field_73975_c;
   private GuiButton field_73973_d;
   private int field_73979_m;
   private DynamicTexture field_73977_n;
   private final Object field_104025_t = new Object();
   private String field_92025_p;
   private String field_146972_A;
   private String field_104024_v;
   private static final ResourceLocation field_110353_x = new ResourceLocation("texts/splashes.txt");
   private static final ResourceLocation field_110352_y = new ResourceLocation("textures/gui/title/minecraft.png");
   private static final ResourceLocation[] field_73978_o = new ResourceLocation[]{
      new ResourceLocation("textures/gui/title/background/panorama_0.png"),
      new ResourceLocation("textures/gui/title/background/panorama_1.png"),
      new ResourceLocation("textures/gui/title/background/panorama_2.png"),
      new ResourceLocation("textures/gui/title/background/panorama_3.png"),
      new ResourceLocation("textures/gui/title/background/panorama_4.png"),
      new ResourceLocation("textures/gui/title/background/panorama_5.png")
   };
   public static final String field_96138_a = "Please click " + EnumChatFormatting.UNDERLINE + "here" + EnumChatFormatting.RESET + " for more information.";
   private int field_92024_r;
   private int field_92023_s;
   private int field_92022_t;
   private int field_92021_u;
   private int field_92020_v;
   private int field_92019_w;
   private ResourceLocation field_110351_G;

   public GuiMainMenu() {
      super();
      this.field_146972_A = field_96138_a;
      this.field_73975_c = "missingno";
      BufferedReader var1 = null;

      try {
         ArrayList var2 = new ArrayList();
         var1 = new BufferedReader(
            new InputStreamReader(Minecraft.func_71410_x().func_110442_L().func_110536_a(field_110353_x).func_110527_b(), Charsets.UTF_8)
         );

         String var3;
         while((var3 = var1.readLine()) != null) {
            var3 = var3.trim();
            if (!var3.isEmpty()) {
               var2.add(var3);
            }
         }

         if (!var2.isEmpty()) {
            do {
               this.field_73975_c = (String)var2.get(field_73976_a.nextInt(var2.size()));
            } while(this.field_73975_c.hashCode() == 125780783);
         }
      } catch (IOException var12) {
      } finally {
         if (var1 != null) {
            try {
               var1.close();
            } catch (IOException var11) {
            }
         }
      }

      this.field_73974_b = field_73976_a.nextFloat();
      this.field_92025_p = "";
      if (!GLContext.getCapabilities().OpenGL20 && !OpenGlHelper.func_153193_b()) {
         this.field_92025_p = I18n.func_135052_a("title.oldgl1");
         this.field_146972_A = I18n.func_135052_a("title.oldgl2");
         this.field_104024_v = "https://help.mojang.com/customer/portal/articles/325948?ref=game";
      }
   }

   @Override
   public void func_73876_c() {
      ++this.field_73979_m;
   }

   @Override
   public boolean func_73868_f() {
      return false;
   }

   @Override
   protected void func_73869_a(char var1, int var2) {
   }

   @Override
   public void func_73866_w_() {
      this.field_73977_n = new DynamicTexture(256, 256);
      this.field_110351_G = this.field_146297_k.func_110434_K().func_110578_a("background", this.field_73977_n);
      Calendar var1 = Calendar.getInstance();
      var1.setTime(new Date());
      if (var1.get(2) + 1 == 11 && var1.get(5) == 9) {
         this.field_73975_c = "Happy birthday, ez!";
      } else if (var1.get(2) + 1 == 6 && var1.get(5) == 1) {
         this.field_73975_c = "Happy birthday, Notch!";
      } else if (var1.get(2) + 1 == 12 && var1.get(5) == 24) {
         this.field_73975_c = "Merry X-mas!";
      } else if (var1.get(2) + 1 == 1 && var1.get(5) == 1) {
         this.field_73975_c = "Happy new year!";
      } else if (var1.get(2) + 1 == 10 && var1.get(5) == 31) {
         this.field_73975_c = "OOoooOOOoooo! Spooky!";
      }

      boolean var2 = true;
      int var3 = this.field_146295_m / 4 + 48;
      if (this.field_146297_k.func_71355_q()) {
         this.func_73972_b(var3, 24);
      } else {
         this.func_73969_a(var3, 24);
      }

      this.field_146292_n.add(new GuiButton(0, this.field_146294_l / 2 - 100, var3 + 72 + 12, 98, 20, I18n.func_135052_a("menu.options")));
      this.field_146292_n.add(new GuiButton(4, this.field_146294_l / 2 + 2, var3 + 72 + 12, 98, 20, I18n.func_135052_a("menu.quit")));
      this.field_146292_n.add(new GuiButtonLanguage(5, this.field_146294_l / 2 - 124, var3 + 72 + 12));
      synchronized(this.field_104025_t) {
         this.field_92023_s = this.field_146289_q.func_78256_a(this.field_92025_p);
         this.field_92024_r = this.field_146289_q.func_78256_a(this.field_146972_A);
         int var5 = Math.max(this.field_92023_s, this.field_92024_r);
         this.field_92022_t = (this.field_146294_l - var5) / 2;
         this.field_92021_u = ((GuiButton)this.field_146292_n.get(0)).field_146129_i - 24;
         this.field_92020_v = this.field_92022_t + var5;
         this.field_92019_w = this.field_92021_u + 24;
      }
   }

   private void func_73969_a(int var1, int var2) {
      this.field_146292_n.add(new GuiButton(1, this.field_146294_l / 2 - 100, var1, I18n.func_135052_a("menu.singleplayer")));
      this.field_146292_n.add(new GuiButton(2, this.field_146294_l / 2 - 100, var1 + var2 * 1, I18n.func_135052_a("menu.multiplayer")));
      this.field_146292_n.add(new GuiButton(14, this.field_146294_l / 2 - 100, var1 + var2 * 2, I18n.func_135052_a("menu.online")));
   }

   private void func_73972_b(int var1, int var2) {
      this.field_146292_n.add(new GuiButton(11, this.field_146294_l / 2 - 100, var1, I18n.func_135052_a("menu.playdemo")));
      this.field_146292_n.add(this.field_73973_d = new GuiButton(12, this.field_146294_l / 2 - 100, var1 + var2 * 1, I18n.func_135052_a("menu.resetdemo")));
      ISaveFormat var3 = this.field_146297_k.func_71359_d();
      WorldInfo var4 = var3.func_75803_c("Demo_World");
      if (var4 == null) {
         this.field_73973_d.field_146124_l = false;
      }
   }

   @Override
   protected void func_146284_a(GuiButton var1) {
      if (var1.field_146127_k == 0) {
         this.field_146297_k.func_147108_a(new GuiOptions(this, this.field_146297_k.field_71474_y));
      }

      if (var1.field_146127_k == 5) {
         this.field_146297_k.func_147108_a(new GuiLanguage(this, this.field_146297_k.field_71474_y, this.field_146297_k.func_135016_M()));
      }

      if (var1.field_146127_k == 1) {
         this.field_146297_k.func_147108_a(new GuiSelectWorld(this));
      }

      if (var1.field_146127_k == 2) {
         this.field_146297_k.func_147108_a(new GuiMultiplayer(this));
      }

      if (var1.field_146127_k == 14) {
         this.func_140005_i();
      }

      if (var1.field_146127_k == 4) {
         this.field_146297_k.func_71400_g();
      }

      if (var1.field_146127_k == 11) {
         this.field_146297_k.func_71371_a("Demo_World", "Demo_World", DemoWorldServer.field_73071_a);
      }

      if (var1.field_146127_k == 12) {
         ISaveFormat var2 = this.field_146297_k.func_71359_d();
         WorldInfo var3 = var2.func_75803_c("Demo_World");
         if (var3 != null) {
            GuiYesNo var4 = GuiSelectWorld.func_152129_a(this, var3.func_76065_j(), 12);
            this.field_146297_k.func_147108_a(var4);
         }
      }
   }

   private void func_140005_i() {
      RealmsBridge var1 = new RealmsBridge();
      var1.switchToRealms(this);
   }

   @Override
   public void func_73878_a(boolean var1, int var2) {
      if (var1 && var2 == 12) {
         ISaveFormat var6 = this.field_146297_k.func_71359_d();
         var6.func_75800_d();
         var6.func_75802_e("Demo_World");
         this.field_146297_k.func_147108_a(this);
      } else if (var2 == 13) {
         if (var1) {
            try {
               Class var3 = Class.forName("java.awt.Desktop");
               Object var4 = var3.getMethod("getDesktop").invoke(null);
               var3.getMethod("browse", URI.class).invoke(var4, new URI(this.field_104024_v));
            } catch (Throwable var5) {
               field_146974_g.error("Couldn't open link", var5);
            }
         }

         this.field_146297_k.func_147108_a(this);
      }
   }

   private void func_73970_b(int var1, int var2, float var3) {
      Tessellator var4 = Tessellator.field_78398_a;
      GL11.glMatrixMode(5889);
      GL11.glPushMatrix();
      GL11.glLoadIdentity();
      Project.gluPerspective(120.0F, 1.0F, 0.05F, 10.0F);
      GL11.glMatrixMode(5888);
      GL11.glPushMatrix();
      GL11.glLoadIdentity();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
      GL11.glEnable(3042);
      GL11.glDisable(3008);
      GL11.glDisable(2884);
      GL11.glDepthMask(false);
      OpenGlHelper.func_148821_a(770, 771, 1, 0);
      byte var5 = 8;

      for(int var6 = 0; var6 < var5 * var5; ++var6) {
         GL11.glPushMatrix();
         float var7 = ((float)(var6 % var5) / (float)var5 - 0.5F) / 64.0F;
         float var8 = ((float)(var6 / var5) / (float)var5 - 0.5F) / 64.0F;
         float var9 = 0.0F;
         GL11.glTranslatef(var7, var8, var9);
         GL11.glRotatef(MathHelper.func_76126_a(((float)this.field_73979_m + var3) / 400.0F) * 25.0F + 20.0F, 1.0F, 0.0F, 0.0F);
         GL11.glRotatef(-((float)this.field_73979_m + var3) * 0.1F, 0.0F, 1.0F, 0.0F);

         for(int var10 = 0; var10 < 6; ++var10) {
            GL11.glPushMatrix();
            if (var10 == 1) {
               GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
            }

            if (var10 == 2) {
               GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
            }

            if (var10 == 3) {
               GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
            }

            if (var10 == 4) {
               GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
            }

            if (var10 == 5) {
               GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
            }

            this.field_146297_k.func_110434_K().func_110577_a(field_73978_o[var10]);
            var4.func_78382_b();
            var4.func_78384_a(16777215, 255 / (var6 + 1));
            float var11 = 0.0F;
            var4.func_78374_a(-1.0, -1.0, 1.0, (double)(0.0F + var11), (double)(0.0F + var11));
            var4.func_78374_a(1.0, -1.0, 1.0, (double)(1.0F - var11), (double)(0.0F + var11));
            var4.func_78374_a(1.0, 1.0, 1.0, (double)(1.0F - var11), (double)(1.0F - var11));
            var4.func_78374_a(-1.0, 1.0, 1.0, (double)(0.0F + var11), (double)(1.0F - var11));
            var4.func_78381_a();
            GL11.glPopMatrix();
         }

         GL11.glPopMatrix();
         GL11.glColorMask(true, true, true, false);
      }

      var4.func_78373_b(0.0, 0.0, 0.0);
      GL11.glColorMask(true, true, true, true);
      GL11.glMatrixMode(5889);
      GL11.glPopMatrix();
      GL11.glMatrixMode(5888);
      GL11.glPopMatrix();
      GL11.glDepthMask(true);
      GL11.glEnable(2884);
      GL11.glEnable(2929);
   }

   private void func_73968_a(float var1) {
      this.field_146297_k.func_110434_K().func_110577_a(this.field_110351_G);
      GL11.glTexParameteri(3553, 10241, 9729);
      GL11.glTexParameteri(3553, 10240, 9729);
      GL11.glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, 256, 256);
      GL11.glEnable(3042);
      OpenGlHelper.func_148821_a(770, 771, 1, 0);
      GL11.glColorMask(true, true, true, false);
      Tessellator var2 = Tessellator.field_78398_a;
      var2.func_78382_b();
      GL11.glDisable(3008);
      byte var3 = 3;

      for(int var4 = 0; var4 < var3; ++var4) {
         var2.func_78369_a(1.0F, 1.0F, 1.0F, 1.0F / (float)(var4 + 1));
         int var5 = this.field_146294_l;
         int var6 = this.field_146295_m;
         float var7 = (float)(var4 - var3 / 2) / 256.0F;
         var2.func_78374_a((double)var5, (double)var6, (double)this.field_73735_i, (double)(0.0F + var7), 1.0);
         var2.func_78374_a((double)var5, 0.0, (double)this.field_73735_i, (double)(1.0F + var7), 1.0);
         var2.func_78374_a(0.0, 0.0, (double)this.field_73735_i, (double)(1.0F + var7), 0.0);
         var2.func_78374_a(0.0, (double)var6, (double)this.field_73735_i, (double)(0.0F + var7), 0.0);
      }

      var2.func_78381_a();
      GL11.glEnable(3008);
      GL11.glColorMask(true, true, true, true);
   }

   private void func_73971_c(int var1, int var2, float var3) {
      this.field_146297_k.func_147110_a().func_147609_e();
      GL11.glViewport(0, 0, 256, 256);
      this.func_73970_b(var1, var2, var3);
      this.func_73968_a(var3);
      this.func_73968_a(var3);
      this.func_73968_a(var3);
      this.func_73968_a(var3);
      this.func_73968_a(var3);
      this.func_73968_a(var3);
      this.func_73968_a(var3);
      this.field_146297_k.func_147110_a().func_147610_a(true);
      GL11.glViewport(0, 0, this.field_146297_k.field_71443_c, this.field_146297_k.field_71440_d);
      Tessellator var4 = Tessellator.field_78398_a;
      var4.func_78382_b();
      float var5 = this.field_146294_l > this.field_146295_m ? 120.0F / (float)this.field_146294_l : 120.0F / (float)this.field_146295_m;
      float var6 = (float)this.field_146295_m * var5 / 256.0F;
      float var7 = (float)this.field_146294_l * var5 / 256.0F;
      var4.func_78369_a(1.0F, 1.0F, 1.0F, 1.0F);
      int var8 = this.field_146294_l;
      int var9 = this.field_146295_m;
      var4.func_78374_a(0.0, (double)var9, (double)this.field_73735_i, (double)(0.5F - var6), (double)(0.5F + var7));
      var4.func_78374_a((double)var8, (double)var9, (double)this.field_73735_i, (double)(0.5F - var6), (double)(0.5F - var7));
      var4.func_78374_a((double)var8, 0.0, (double)this.field_73735_i, (double)(0.5F + var6), (double)(0.5F - var7));
      var4.func_78374_a(0.0, 0.0, (double)this.field_73735_i, (double)(0.5F + var6), (double)(0.5F + var7));
      var4.func_78381_a();
   }

   @Override
   public void func_73863_a(int var1, int var2, float var3) {
      GL11.glDisable(3008);
      this.func_73971_c(var1, var2, var3);
      GL11.glEnable(3008);
      Tessellator var4 = Tessellator.field_78398_a;
      short var5 = 274;
      int var6 = this.field_146294_l / 2 - var5 / 2;
      byte var7 = 30;
      this.func_73733_a(0, 0, this.field_146294_l, this.field_146295_m, -2130706433, 16777215);
      this.func_73733_a(0, 0, this.field_146294_l, this.field_146295_m, 0, -2147483648);
      this.field_146297_k.func_110434_K().func_110577_a(field_110352_y);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      if ((double)this.field_73974_b < 1.0E-4) {
         this.func_73729_b(var6 + 0, var7 + 0, 0, 0, 99, 44);
         this.func_73729_b(var6 + 99, var7 + 0, 129, 0, 27, 44);
         this.func_73729_b(var6 + 99 + 26, var7 + 0, 126, 0, 3, 44);
         this.func_73729_b(var6 + 99 + 26 + 3, var7 + 0, 99, 0, 26, 44);
         this.func_73729_b(var6 + 155, var7 + 0, 0, 45, 155, 44);
      } else {
         this.func_73729_b(var6 + 0, var7 + 0, 0, 0, 155, 44);
         this.func_73729_b(var6 + 155, var7 + 0, 0, 45, 155, 44);
      }

      var4.func_78378_d(-1);
      GL11.glPushMatrix();
      GL11.glTranslatef((float)(this.field_146294_l / 2 + 90), 70.0F, 0.0F);
      GL11.glRotatef(-20.0F, 0.0F, 0.0F, 1.0F);
      float var8 = 1.8F - MathHelper.func_76135_e(MathHelper.func_76126_a((float)(Minecraft.func_71386_F() % 1000L) / 1000.0F * 3.1415927F * 2.0F) * 0.1F);
      var8 = var8 * 100.0F / (float)(this.field_146289_q.func_78256_a(this.field_73975_c) + 32);
      GL11.glScalef(var8, var8, var8);
      this.func_73732_a(this.field_146289_q, this.field_73975_c, 0, -8, -256);
      GL11.glPopMatrix();
      String var9 = "Minecraft 1.7.10";
      if (this.field_146297_k.func_71355_q()) {
         var9 = var9 + " Demo";
      }

      this.func_73731_b(this.field_146289_q, var9, 2, this.field_146295_m - 10, -1);
      String var10 = "Copyright Mojang AB. Do not distribute!";
      this.func_73731_b(this.field_146289_q, var10, this.field_146294_l - this.field_146289_q.func_78256_a(var10) - 2, this.field_146295_m - 10, -1);
      if (this.field_92025_p != null && this.field_92025_p.length() > 0) {
         func_73734_a(this.field_92022_t - 2, this.field_92021_u - 2, this.field_92020_v + 2, this.field_92019_w - 1, 1428160512);
         this.func_73731_b(this.field_146289_q, this.field_92025_p, this.field_92022_t, this.field_92021_u, -1);
         this.func_73731_b(
            this.field_146289_q,
            this.field_146972_A,
            (this.field_146294_l - this.field_92024_r) / 2,
            ((GuiButton)this.field_146292_n.get(0)).field_146129_i - 12,
            -1
         );
      }

      super.func_73863_a(var1, var2, var3);
   }

   @Override
   protected void func_73864_a(int var1, int var2, int var3) {
      super.func_73864_a(var1, var2, var3);
      synchronized(this.field_104025_t) {
         if (this.field_92025_p.length() > 0
            && var1 >= this.field_92022_t
            && var1 <= this.field_92020_v
            && var2 >= this.field_92021_u
            && var2 <= this.field_92019_w) {
            GuiConfirmOpenLink var5 = new GuiConfirmOpenLink(this, this.field_104024_v, 13, true);
            var5.func_146358_g();
            this.field_146297_k.func_147108_a(var5);
         }
      }
   }
}
