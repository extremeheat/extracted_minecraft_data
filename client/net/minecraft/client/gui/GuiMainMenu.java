package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Runnables;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderSkybox;
import net.minecraft.client.renderer.RenderSkyboxCube;
import net.minecraft.client.resources.I18n;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServerDemo;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL;

public class GuiMainMenu extends GuiScreen {
   private static final Random field_175374_h = new Random();
   private final float field_73974_b;
   private String field_73975_c;
   private GuiButton field_195209_i;
   private GuiButton field_73973_d;
   private final Object field_104025_t = new Object();
   public static final String field_96138_a;
   private int field_92024_r;
   private int field_92023_s;
   private int field_92022_t;
   private int field_92021_u;
   private int field_92020_v;
   private int field_92019_w;
   private String field_92025_p;
   private String field_146972_A;
   private String field_104024_v;
   private static final ResourceLocation field_110353_x;
   private static final ResourceLocation field_110352_y;
   private static final ResourceLocation field_194400_H;
   private boolean field_183502_L;
   private GuiScreen field_183503_M;
   private int field_193978_M;
   private int field_193979_N;
   private final RenderSkybox field_209101_K;

   public GuiMainMenu() {
      super();
      this.field_146972_A = field_96138_a;
      this.field_209101_K = new RenderSkybox(new RenderSkyboxCube(new ResourceLocation("textures/gui/title/background/panorama")));
      this.field_73975_c = "missingno";
      IResource var1 = null;

      try {
         ArrayList var2 = Lists.newArrayList();
         var1 = Minecraft.func_71410_x().func_195551_G().func_199002_a(field_110353_x);
         BufferedReader var3 = new BufferedReader(new InputStreamReader(var1.func_199027_b(), StandardCharsets.UTF_8));

         String var4;
         while((var4 = var3.readLine()) != null) {
            var4 = var4.trim();
            if (!var4.isEmpty()) {
               var2.add(var4);
            }
         }

         if (!var2.isEmpty()) {
            do {
               this.field_73975_c = (String)var2.get(field_175374_h.nextInt(var2.size()));
            } while(this.field_73975_c.hashCode() == 125780783);
         }
      } catch (IOException var8) {
      } finally {
         IOUtils.closeQuietly(var1);
      }

      this.field_73974_b = field_175374_h.nextFloat();
      this.field_92025_p = "";
      if (!GL.getCapabilities().OpenGL20 && !OpenGlHelper.func_153193_b()) {
         this.field_92025_p = I18n.func_135052_a("title.oldgl1");
         this.field_146972_A = I18n.func_135052_a("title.oldgl2");
         this.field_104024_v = "https://help.mojang.com/customer/portal/articles/325948?ref=game";
      }

   }

   private boolean func_183501_a() {
      return Minecraft.func_71410_x().field_71474_y.func_74308_b(GameSettings.Options.REALMS_NOTIFICATIONS) && this.field_183503_M != null;
   }

   public void func_73876_c() {
      if (this.func_183501_a()) {
         this.field_183503_M.func_73876_c();
      }

   }

   public boolean func_73868_f() {
      return false;
   }

   public boolean func_195120_Y_() {
      return false;
   }

   protected void func_73866_w_() {
      this.field_193978_M = this.field_146289_q.func_78256_a("Copyright Mojang AB. Do not distribute!");
      this.field_193979_N = this.field_146294_l - this.field_193978_M - 2;
      Calendar var1 = Calendar.getInstance();
      var1.setTime(new Date());
      if (var1.get(2) + 1 == 12 && var1.get(5) == 24) {
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

      this.field_195209_i = this.func_189646_b(new GuiButton(0, this.field_146294_l / 2 - 100, var3 + 72 + 12, 98, 20, I18n.func_135052_a("menu.options")) {
         public void func_194829_a(double var1, double var3) {
            GuiMainMenu.this.field_146297_k.func_147108_a(new GuiOptions(GuiMainMenu.this, GuiMainMenu.this.field_146297_k.field_71474_y));
         }
      });
      this.func_189646_b(new GuiButton(4, this.field_146294_l / 2 + 2, var3 + 72 + 12, 98, 20, I18n.func_135052_a("menu.quit")) {
         public void func_194829_a(double var1, double var3) {
            GuiMainMenu.this.field_146297_k.func_71400_g();
         }
      });
      this.func_189646_b(new GuiButtonLanguage(5, this.field_146294_l / 2 - 124, var3 + 72 + 12) {
         public void func_194829_a(double var1, double var3) {
            GuiMainMenu.this.field_146297_k.func_147108_a(new GuiLanguage(GuiMainMenu.this, GuiMainMenu.this.field_146297_k.field_71474_y, GuiMainMenu.this.field_146297_k.func_135016_M()));
         }
      });
      synchronized(this.field_104025_t) {
         this.field_92023_s = this.field_146289_q.func_78256_a(this.field_92025_p);
         this.field_92024_r = this.field_146289_q.func_78256_a(this.field_146972_A);
         int var5 = Math.max(this.field_92023_s, this.field_92024_r);
         this.field_92022_t = (this.field_146294_l - var5) / 2;
         this.field_92021_u = var3 - 24;
         this.field_92020_v = this.field_92022_t + var5;
         this.field_92019_w = this.field_92021_u + 24;
      }

      this.field_146297_k.func_181537_a(false);
      if (Minecraft.func_71410_x().field_71474_y.func_74308_b(GameSettings.Options.REALMS_NOTIFICATIONS) && !this.field_183502_L) {
         RealmsBridge var4 = new RealmsBridge();
         this.field_183503_M = var4.getNotificationScreen(this);
         this.field_183502_L = true;
      }

      if (this.func_183501_a()) {
         this.field_183503_M.func_146280_a(this.field_146297_k, this.field_146294_l, this.field_146295_m);
      }

   }

   private void func_73969_a(int var1, int var2) {
      this.func_189646_b(new GuiButton(1, this.field_146294_l / 2 - 100, var1, I18n.func_135052_a("menu.singleplayer")) {
         public void func_194829_a(double var1, double var3) {
            GuiMainMenu.this.field_146297_k.func_147108_a(new GuiWorldSelection(GuiMainMenu.this));
         }
      });
      this.func_189646_b(new GuiButton(2, this.field_146294_l / 2 - 100, var1 + var2 * 1, I18n.func_135052_a("menu.multiplayer")) {
         public void func_194829_a(double var1, double var3) {
            GuiMainMenu.this.field_146297_k.func_147108_a(new GuiMultiplayer(GuiMainMenu.this));
         }
      });
      this.func_189646_b(new GuiButton(14, this.field_146294_l / 2 - 100, var1 + var2 * 2, I18n.func_135052_a("menu.online")) {
         public void func_194829_a(double var1, double var3) {
            GuiMainMenu.this.func_140005_i();
         }
      });
   }

   private void func_73972_b(int var1, int var2) {
      this.func_189646_b(new GuiButton(11, this.field_146294_l / 2 - 100, var1, I18n.func_135052_a("menu.playdemo")) {
         public void func_194829_a(double var1, double var3) {
            GuiMainMenu.this.field_146297_k.func_71371_a("Demo_World", "Demo_World", WorldServerDemo.field_73071_a);
         }
      });
      this.field_73973_d = this.func_189646_b(new GuiButton(12, this.field_146294_l / 2 - 100, var1 + var2 * 1, I18n.func_135052_a("menu.resetdemo")) {
         public void func_194829_a(double var1, double var3) {
            ISaveFormat var5 = GuiMainMenu.this.field_146297_k.func_71359_d();
            WorldInfo var6 = var5.func_75803_c("Demo_World");
            if (var6 != null) {
               GuiMainMenu.this.field_146297_k.func_147108_a(new GuiYesNo(GuiMainMenu.this, I18n.func_135052_a("selectWorld.deleteQuestion"), I18n.func_135052_a("selectWorld.deleteWarning", var6.func_76065_j()), I18n.func_135052_a("selectWorld.deleteButton"), I18n.func_135052_a("gui.cancel"), 12));
            }

         }
      });
      ISaveFormat var3 = this.field_146297_k.func_71359_d();
      WorldInfo var4 = var3.func_75803_c("Demo_World");
      if (var4 == null) {
         this.field_73973_d.field_146124_l = false;
      }

   }

   private void func_140005_i() {
      RealmsBridge var1 = new RealmsBridge();
      var1.switchToRealms(this);
   }

   public void confirmResult(boolean var1, int var2) {
      if (var1 && var2 == 12) {
         ISaveFormat var3 = this.field_146297_k.func_71359_d();
         var3.func_75800_d();
         var3.func_75802_e("Demo_World");
         this.field_146297_k.func_147108_a(this);
      } else if (var2 == 12) {
         this.field_146297_k.func_147108_a(this);
      } else if (var2 == 13) {
         if (var1) {
            Util.func_110647_a().func_195640_a(this.field_104024_v);
         }

         this.field_146297_k.func_147108_a(this);
      }

   }

   public void func_73863_a(int var1, int var2, float var3) {
      this.field_209101_K.func_209144_a(var3);
      boolean var4 = true;
      int var5 = this.field_146294_l / 2 - 137;
      boolean var6 = true;
      this.field_146297_k.func_110434_K().func_110577_a(new ResourceLocation("textures/gui/title/background/panorama_overlay.png"));
      func_152125_a(0, 0, 0.0F, 0.0F, 16, 128, this.field_146294_l, this.field_146295_m, 16.0F, 128.0F);
      this.field_146297_k.func_110434_K().func_110577_a(field_110352_y);
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      if ((double)this.field_73974_b < 1.0E-4D) {
         this.func_73729_b(var5 + 0, 30, 0, 0, 99, 44);
         this.func_73729_b(var5 + 99, 30, 129, 0, 27, 44);
         this.func_73729_b(var5 + 99 + 26, 30, 126, 0, 3, 44);
         this.func_73729_b(var5 + 99 + 26 + 3, 30, 99, 0, 26, 44);
         this.func_73729_b(var5 + 155, 30, 0, 45, 155, 44);
      } else {
         this.func_73729_b(var5 + 0, 30, 0, 0, 155, 44);
         this.func_73729_b(var5 + 155, 30, 0, 45, 155, 44);
      }

      this.field_146297_k.func_110434_K().func_110577_a(field_194400_H);
      func_146110_a(var5 + 88, 67, 0.0F, 0.0F, 98, 14, 128.0F, 16.0F);
      GlStateManager.func_179094_E();
      GlStateManager.func_179109_b((float)(this.field_146294_l / 2 + 90), 70.0F, 0.0F);
      GlStateManager.func_179114_b(-20.0F, 0.0F, 0.0F, 1.0F);
      float var7 = 1.8F - MathHelper.func_76135_e(MathHelper.func_76126_a((float)(Util.func_211177_b() % 1000L) / 1000.0F * 6.2831855F) * 0.1F);
      var7 = var7 * 100.0F / (float)(this.field_146289_q.func_78256_a(this.field_73975_c) + 32);
      GlStateManager.func_179152_a(var7, var7, var7);
      this.func_73732_a(this.field_146289_q, this.field_73975_c, 0, -8, -256);
      GlStateManager.func_179121_F();
      String var8 = "Minecraft 1.13.2";
      if (this.field_146297_k.func_71355_q()) {
         var8 = var8 + " Demo";
      } else {
         var8 = var8 + ("release".equalsIgnoreCase(this.field_146297_k.func_184123_d()) ? "" : "/" + this.field_146297_k.func_184123_d());
      }

      this.func_73731_b(this.field_146289_q, var8, 2, this.field_146295_m - 10, -1);
      this.func_73731_b(this.field_146289_q, "Copyright Mojang AB. Do not distribute!", this.field_193979_N, this.field_146295_m - 10, -1);
      if (var1 > this.field_193979_N && var1 < this.field_193979_N + this.field_193978_M && var2 > this.field_146295_m - 10 && var2 < this.field_146295_m) {
         func_73734_a(this.field_193979_N, this.field_146295_m - 1, this.field_193979_N + this.field_193978_M, this.field_146295_m, -1);
      }

      if (this.field_92025_p != null && !this.field_92025_p.isEmpty()) {
         func_73734_a(this.field_92022_t - 2, this.field_92021_u - 2, this.field_92020_v + 2, this.field_92019_w - 1, 1428160512);
         this.func_73731_b(this.field_146289_q, this.field_92025_p, this.field_92022_t, this.field_92021_u, -1);
         this.func_73731_b(this.field_146289_q, this.field_146972_A, (this.field_146294_l - this.field_92024_r) / 2, this.field_92021_u + 12, -1);
      }

      super.func_73863_a(var1, var2, var3);
      if (this.func_183501_a()) {
         this.field_183503_M.func_73863_a(var1, var2, var3);
      }

   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (super.mouseClicked(var1, var3, var5)) {
         return true;
      } else {
         synchronized(this.field_104025_t) {
            if (!this.field_92025_p.isEmpty() && !StringUtils.func_151246_b(this.field_104024_v) && var1 >= (double)this.field_92022_t && var1 <= (double)this.field_92020_v && var3 >= (double)this.field_92021_u && var3 <= (double)this.field_92019_w) {
               GuiConfirmOpenLink var7 = new GuiConfirmOpenLink(this, this.field_104024_v, 13, true);
               var7.func_146358_g();
               this.field_146297_k.func_147108_a(var7);
               return true;
            }
         }

         if (this.func_183501_a() && this.field_183503_M.mouseClicked(var1, var3, var5)) {
            return true;
         } else {
            if (var1 > (double)this.field_193979_N && var1 < (double)(this.field_193979_N + this.field_193978_M) && var3 > (double)(this.field_146295_m - 10) && var3 < (double)this.field_146295_m) {
               this.field_146297_k.func_147108_a(new GuiWinGame(false, Runnables.doNothing()));
            }

            return false;
         }
      }
   }

   public void func_146281_b() {
      if (this.field_183503_M != null) {
         this.field_183503_M.func_146281_b();
      }

   }

   static {
      field_96138_a = "Please click " + TextFormatting.UNDERLINE + "here" + TextFormatting.RESET + " for more information.";
      field_110353_x = new ResourceLocation("texts/splashes.txt");
      field_110352_y = new ResourceLocation("textures/gui/title/minecraft.png");
      field_194400_H = new ResourceLocation("textures/gui/title/edition.png");
   }
}
