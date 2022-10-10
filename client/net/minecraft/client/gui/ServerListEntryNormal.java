package net.minecraft.client.gui;

import com.google.common.hash.Hashing;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.DefaultUncaughtExceptionHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.MemoryStack;

public class ServerListEntryNormal extends ServerSelectionList.Entry {
   private static final Logger field_148304_a = LogManager.getLogger();
   private static final ThreadPoolExecutor field_148302_b;
   private static final ResourceLocation field_178015_c;
   private static final ResourceLocation field_178014_d;
   private final GuiMultiplayer field_148303_c;
   private final Minecraft field_148300_d;
   private final ServerData field_148301_e;
   private final ResourceLocation field_148306_i;
   private String field_148299_g;
   private DynamicTexture field_148305_h;
   private long field_148298_f;

   protected ServerListEntryNormal(GuiMultiplayer var1, ServerData var2) {
      super();
      this.field_148303_c = var1;
      this.field_148301_e = var2;
      this.field_148300_d = Minecraft.func_71410_x();
      this.field_148306_i = new ResourceLocation("servers/" + Hashing.sha1().hashUnencodedChars(var2.field_78845_b) + "/icon");
      this.field_148305_h = (DynamicTexture)this.field_148300_d.func_110434_K().func_110581_b(this.field_148306_i);
   }

   public void func_194999_a(int var1, int var2, int var3, int var4, boolean var5, float var6) {
      int var7 = this.func_195001_c();
      int var8 = this.func_195002_d();
      if (!this.field_148301_e.field_78841_f) {
         this.field_148301_e.field_78841_f = true;
         this.field_148301_e.field_78844_e = -2L;
         this.field_148301_e.field_78843_d = "";
         this.field_148301_e.field_78846_c = "";
         field_148302_b.submit(() -> {
            try {
               this.field_148303_c.func_146789_i().func_147224_a(this.field_148301_e);
            } catch (UnknownHostException var2) {
               this.field_148301_e.field_78844_e = -1L;
               this.field_148301_e.field_78843_d = TextFormatting.DARK_RED + I18n.func_135052_a("multiplayer.status.cannot_resolve");
            } catch (Exception var3) {
               this.field_148301_e.field_78844_e = -1L;
               this.field_148301_e.field_78843_d = TextFormatting.DARK_RED + I18n.func_135052_a("multiplayer.status.cannot_connect");
            }

         });
      }

      boolean var9 = this.field_148301_e.field_82821_f > 404;
      boolean var10 = this.field_148301_e.field_82821_f < 404;
      boolean var11 = var9 || var10;
      this.field_148300_d.field_71466_p.func_211126_b(this.field_148301_e.field_78847_a, (float)(var8 + 32 + 3), (float)(var7 + 1), 16777215);
      List var12 = this.field_148300_d.field_71466_p.func_78271_c(this.field_148301_e.field_78843_d, var1 - 32 - 2);

      for(int var13 = 0; var13 < Math.min(var12.size(), 2); ++var13) {
         this.field_148300_d.field_71466_p.func_211126_b((String)var12.get(var13), (float)(var8 + 32 + 3), (float)(var7 + 12 + this.field_148300_d.field_71466_p.field_78288_b * var13), 8421504);
      }

      String var23 = var11 ? TextFormatting.DARK_RED + this.field_148301_e.field_82822_g : this.field_148301_e.field_78846_c;
      int var14 = this.field_148300_d.field_71466_p.func_78256_a(var23);
      this.field_148300_d.field_71466_p.func_211126_b(var23, (float)(var8 + var1 - var14 - 15 - 2), (float)(var7 + 1), 8421504);
      byte var15 = 0;
      String var17 = null;
      int var16;
      String var18;
      if (var11) {
         var16 = 5;
         var18 = I18n.func_135052_a(var9 ? "multiplayer.status.client_out_of_date" : "multiplayer.status.server_out_of_date");
         var17 = this.field_148301_e.field_147412_i;
      } else if (this.field_148301_e.field_78841_f && this.field_148301_e.field_78844_e != -2L) {
         if (this.field_148301_e.field_78844_e < 0L) {
            var16 = 5;
         } else if (this.field_148301_e.field_78844_e < 150L) {
            var16 = 0;
         } else if (this.field_148301_e.field_78844_e < 300L) {
            var16 = 1;
         } else if (this.field_148301_e.field_78844_e < 600L) {
            var16 = 2;
         } else if (this.field_148301_e.field_78844_e < 1000L) {
            var16 = 3;
         } else {
            var16 = 4;
         }

         if (this.field_148301_e.field_78844_e < 0L) {
            var18 = I18n.func_135052_a("multiplayer.status.no_connection");
         } else {
            var18 = this.field_148301_e.field_78844_e + "ms";
            var17 = this.field_148301_e.field_147412_i;
         }
      } else {
         var15 = 1;
         var16 = (int)(Util.func_211177_b() / 100L + (long)(this.func_195003_b() * 2) & 7L);
         if (var16 > 4) {
            var16 = 8 - var16;
         }

         var18 = I18n.func_135052_a("multiplayer.status.pinging");
      }

      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      this.field_148300_d.func_110434_K().func_110577_a(Gui.field_110324_m);
      Gui.func_146110_a(var8 + var1 - 15, var7, (float)(var15 * 10), (float)(176 + var16 * 8), 10, 8, 256.0F, 256.0F);
      if (this.field_148301_e.func_147409_e() != null && !this.field_148301_e.func_147409_e().equals(this.field_148299_g)) {
         this.field_148299_g = this.field_148301_e.func_147409_e();
         this.func_148297_b();
         this.field_148303_c.func_146795_p().func_78855_b();
      }

      if (this.field_148305_h != null) {
         this.func_178012_a(var8, var7, this.field_148306_i);
      } else {
         this.func_178012_a(var8, var7, field_178015_c);
      }

      int var19 = var3 - var8;
      int var20 = var4 - var7;
      if (var19 >= var1 - 15 && var19 <= var1 - 5 && var20 >= 0 && var20 <= 8) {
         this.field_148303_c.func_146793_a(var18);
      } else if (var19 >= var1 - var14 - 15 - 2 && var19 <= var1 - 15 - 2 && var20 >= 0 && var20 <= 8) {
         this.field_148303_c.func_146793_a(var17);
      }

      if (this.field_148300_d.field_71474_y.field_85185_A || var5) {
         this.field_148300_d.func_110434_K().func_110577_a(field_178014_d);
         Gui.func_73734_a(var8, var7, var8 + 32, var7 + 32, -1601138544);
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         int var21 = var3 - var8;
         int var22 = var4 - var7;
         if (this.func_178013_b()) {
            if (var21 < 32 && var21 > 16) {
               Gui.func_146110_a(var8, var7, 0.0F, 32.0F, 32, 32, 256.0F, 256.0F);
            } else {
               Gui.func_146110_a(var8, var7, 0.0F, 0.0F, 32, 32, 256.0F, 256.0F);
            }
         }

         if (this.field_148303_c.func_175392_a(this, this.func_195003_b())) {
            if (var21 < 16 && var22 < 16) {
               Gui.func_146110_a(var8, var7, 96.0F, 32.0F, 32, 32, 256.0F, 256.0F);
            } else {
               Gui.func_146110_a(var8, var7, 96.0F, 0.0F, 32, 32, 256.0F, 256.0F);
            }
         }

         if (this.field_148303_c.func_175394_b(this, this.func_195003_b())) {
            if (var21 < 16 && var22 > 16) {
               Gui.func_146110_a(var8, var7, 64.0F, 32.0F, 32, 32, 256.0F, 256.0F);
            } else {
               Gui.func_146110_a(var8, var7, 64.0F, 0.0F, 32, 32, 256.0F, 256.0F);
            }
         }
      }

   }

   protected void func_178012_a(int var1, int var2, ResourceLocation var3) {
      this.field_148300_d.func_110434_K().func_110577_a(var3);
      GlStateManager.func_179147_l();
      Gui.func_146110_a(var1, var2, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
      GlStateManager.func_179084_k();
   }

   private boolean func_178013_b() {
      return true;
   }

   private void func_148297_b() {
      if (this.field_148301_e.func_147409_e() == null) {
         this.field_148300_d.func_110434_K().func_147645_c(this.field_148306_i);
         this.field_148305_h.func_195414_e().close();
         this.field_148305_h = null;
      } else {
         try {
            MemoryStack var1 = MemoryStack.stackPush();
            Throwable var2 = null;

            try {
               ByteBuffer var3 = var1.UTF8(this.field_148301_e.func_147409_e(), false);
               ByteBuffer var4 = Base64.getDecoder().decode(var3);
               ByteBuffer var5 = var1.malloc(var4.remaining());
               var5.put(var4);
               var5.rewind();
               NativeImage var6 = NativeImage.func_195704_a(var5);
               Validate.validState(var6.func_195702_a() == 64, "Must be 64 pixels wide", new Object[0]);
               Validate.validState(var6.func_195714_b() == 64, "Must be 64 pixels high", new Object[0]);
               if (this.field_148305_h == null) {
                  this.field_148305_h = new DynamicTexture(var6);
               } else {
                  this.field_148305_h.func_195415_a(var6);
                  this.field_148305_h.func_110564_a();
               }

               this.field_148300_d.func_110434_K().func_110579_a(this.field_148306_i, this.field_148305_h);
            } catch (Throwable var15) {
               var2 = var15;
               throw var15;
            } finally {
               if (var1 != null) {
                  if (var2 != null) {
                     try {
                        var1.close();
                     } catch (Throwable var14) {
                        var2.addSuppressed(var14);
                     }
                  } else {
                     var1.close();
                  }
               }

            }
         } catch (Throwable var17) {
            field_148304_a.error("Invalid icon for server {} ({})", this.field_148301_e.field_78847_a, this.field_148301_e.field_78845_b, var17);
            this.field_148301_e.func_147407_a((String)null);
         }
      }

   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      double var6 = var1 - (double)this.func_195002_d();
      double var8 = var3 - (double)this.func_195001_c();
      if (var6 <= 32.0D) {
         if (var6 < 32.0D && var6 > 16.0D && this.func_178013_b()) {
            this.field_148303_c.func_146790_a(this.func_195003_b());
            this.field_148303_c.func_146796_h();
            return true;
         }

         if (var6 < 16.0D && var8 < 16.0D && this.field_148303_c.func_175392_a(this, this.func_195003_b())) {
            this.field_148303_c.func_175391_a(this, this.func_195003_b(), GuiScreen.func_146272_n());
            return true;
         }

         if (var6 < 16.0D && var8 > 16.0D && this.field_148303_c.func_175394_b(this, this.func_195003_b())) {
            this.field_148303_c.func_175393_b(this, this.func_195003_b(), GuiScreen.func_146272_n());
            return true;
         }
      }

      this.field_148303_c.func_146790_a(this.func_195003_b());
      if (Util.func_211177_b() - this.field_148298_f < 250L) {
         this.field_148303_c.func_146796_h();
      }

      this.field_148298_f = Util.func_211177_b();
      return false;
   }

   public ServerData func_148296_a() {
      return this.field_148301_e;
   }

   static {
      field_148302_b = new ScheduledThreadPoolExecutor(5, (new ThreadFactoryBuilder()).setNameFormat("Server Pinger #%d").setDaemon(true).setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(field_148304_a)).build());
      field_178015_c = new ResourceLocation("textures/misc/unknown_server.png");
      field_178014_d = new ResourceLocation("textures/gui/server_selection.png");
   }
}
