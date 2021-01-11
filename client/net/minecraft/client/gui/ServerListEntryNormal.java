package net.minecraft.client.gui;

import com.google.common.base.Charsets;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import java.awt.image.BufferedImage;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerListEntryNormal implements GuiListExtended.IGuiListEntry {
   private static final Logger field_148304_a = LogManager.getLogger();
   private static final ThreadPoolExecutor field_148302_b = new ScheduledThreadPoolExecutor(5, (new ThreadFactoryBuilder()).setNameFormat("Server Pinger #%d").setDaemon(true).build());
   private static final ResourceLocation field_178015_c = new ResourceLocation("textures/misc/unknown_server.png");
   private static final ResourceLocation field_178014_d = new ResourceLocation("textures/gui/server_selection.png");
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
      this.field_148306_i = new ResourceLocation("servers/" + var2.field_78845_b + "/icon");
      this.field_148305_h = (DynamicTexture)this.field_148300_d.func_110434_K().func_110581_b(this.field_148306_i);
   }

   public void func_180790_a(int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8) {
      if (!this.field_148301_e.field_78841_f) {
         this.field_148301_e.field_78841_f = true;
         this.field_148301_e.field_78844_e = -2L;
         this.field_148301_e.field_78843_d = "";
         this.field_148301_e.field_78846_c = "";
         field_148302_b.submit(new Runnable() {
            public void run() {
               try {
                  ServerListEntryNormal.this.field_148303_c.func_146789_i().func_147224_a(ServerListEntryNormal.this.field_148301_e);
               } catch (UnknownHostException var2) {
                  ServerListEntryNormal.this.field_148301_e.field_78844_e = -1L;
                  ServerListEntryNormal.this.field_148301_e.field_78843_d = EnumChatFormatting.DARK_RED + "Can't resolve hostname";
               } catch (Exception var3) {
                  ServerListEntryNormal.this.field_148301_e.field_78844_e = -1L;
                  ServerListEntryNormal.this.field_148301_e.field_78843_d = EnumChatFormatting.DARK_RED + "Can't connect to server.";
               }

            }
         });
      }

      boolean var9 = this.field_148301_e.field_82821_f > 47;
      boolean var10 = this.field_148301_e.field_82821_f < 47;
      boolean var11 = var9 || var10;
      this.field_148300_d.field_71466_p.func_78276_b(this.field_148301_e.field_78847_a, var2 + 32 + 3, var3 + 1, 16777215);
      List var12 = this.field_148300_d.field_71466_p.func_78271_c(this.field_148301_e.field_78843_d, var4 - 32 - 2);

      for(int var13 = 0; var13 < Math.min(var12.size(), 2); ++var13) {
         this.field_148300_d.field_71466_p.func_78276_b((String)var12.get(var13), var2 + 32 + 3, var3 + 12 + this.field_148300_d.field_71466_p.field_78288_b * var13, 8421504);
      }

      String var23 = var11 ? EnumChatFormatting.DARK_RED + this.field_148301_e.field_82822_g : this.field_148301_e.field_78846_c;
      int var14 = this.field_148300_d.field_71466_p.func_78256_a(var23);
      this.field_148300_d.field_71466_p.func_78276_b(var23, var2 + var4 - var14 - 15 - 2, var3 + 1, 8421504);
      byte var15 = 0;
      String var17 = null;
      int var16;
      String var18;
      if (var11) {
         var16 = 5;
         var18 = var9 ? "Client out of date!" : "Server out of date!";
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
            var18 = "(no connection)";
         } else {
            var18 = this.field_148301_e.field_78844_e + "ms";
            var17 = this.field_148301_e.field_147412_i;
         }
      } else {
         var15 = 1;
         var16 = (int)(Minecraft.func_71386_F() / 100L + (long)(var1 * 2) & 7L);
         if (var16 > 4) {
            var16 = 8 - var16;
         }

         var18 = "Pinging...";
      }

      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      this.field_148300_d.func_110434_K().func_110577_a(Gui.field_110324_m);
      Gui.func_146110_a(var2 + var4 - 15, var3, (float)(var15 * 10), (float)(176 + var16 * 8), 10, 8, 256.0F, 256.0F);
      if (this.field_148301_e.func_147409_e() != null && !this.field_148301_e.func_147409_e().equals(this.field_148299_g)) {
         this.field_148299_g = this.field_148301_e.func_147409_e();
         this.func_148297_b();
         this.field_148303_c.func_146795_p().func_78855_b();
      }

      if (this.field_148305_h != null) {
         this.func_178012_a(var2, var3, this.field_148306_i);
      } else {
         this.func_178012_a(var2, var3, field_178015_c);
      }

      int var19 = var6 - var2;
      int var20 = var7 - var3;
      if (var19 >= var4 - 15 && var19 <= var4 - 5 && var20 >= 0 && var20 <= 8) {
         this.field_148303_c.func_146793_a(var18);
      } else if (var19 >= var4 - var14 - 15 - 2 && var19 <= var4 - 15 - 2 && var20 >= 0 && var20 <= 8) {
         this.field_148303_c.func_146793_a(var17);
      }

      if (this.field_148300_d.field_71474_y.field_85185_A || var8) {
         this.field_148300_d.func_110434_K().func_110577_a(field_178014_d);
         Gui.func_73734_a(var2, var3, var2 + 32, var3 + 32, -1601138544);
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         int var21 = var6 - var2;
         int var22 = var7 - var3;
         if (this.func_178013_b()) {
            if (var21 < 32 && var21 > 16) {
               Gui.func_146110_a(var2, var3, 0.0F, 32.0F, 32, 32, 256.0F, 256.0F);
            } else {
               Gui.func_146110_a(var2, var3, 0.0F, 0.0F, 32, 32, 256.0F, 256.0F);
            }
         }

         if (this.field_148303_c.func_175392_a(this, var1)) {
            if (var21 < 16 && var22 < 16) {
               Gui.func_146110_a(var2, var3, 96.0F, 32.0F, 32, 32, 256.0F, 256.0F);
            } else {
               Gui.func_146110_a(var2, var3, 96.0F, 0.0F, 32, 32, 256.0F, 256.0F);
            }
         }

         if (this.field_148303_c.func_175394_b(this, var1)) {
            if (var21 < 16 && var22 > 16) {
               Gui.func_146110_a(var2, var3, 64.0F, 32.0F, 32, 32, 256.0F, 256.0F);
            } else {
               Gui.func_146110_a(var2, var3, 64.0F, 0.0F, 32, 32, 256.0F, 256.0F);
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
         this.field_148305_h = null;
      } else {
         ByteBuf var2 = Unpooled.copiedBuffer(this.field_148301_e.func_147409_e(), Charsets.UTF_8);
         ByteBuf var3 = Base64.decode(var2);

         BufferedImage var1;
         label79: {
            try {
               var1 = TextureUtil.func_177053_a(new ByteBufInputStream(var3));
               Validate.validState(var1.getWidth() == 64, "Must be 64 pixels wide", new Object[0]);
               Validate.validState(var1.getHeight() == 64, "Must be 64 pixels high", new Object[0]);
               break label79;
            } catch (Throwable var8) {
               field_148304_a.error("Invalid icon for server " + this.field_148301_e.field_78847_a + " (" + this.field_148301_e.field_78845_b + ")", var8);
               this.field_148301_e.func_147407_a((String)null);
            } finally {
               var2.release();
               var3.release();
            }

            return;
         }

         if (this.field_148305_h == null) {
            this.field_148305_h = new DynamicTexture(var1.getWidth(), var1.getHeight());
            this.field_148300_d.func_110434_K().func_110579_a(this.field_148306_i, this.field_148305_h);
         }

         var1.getRGB(0, 0, var1.getWidth(), var1.getHeight(), this.field_148305_h.func_110565_c(), 0, var1.getWidth());
         this.field_148305_h.func_110564_a();
      }

   }

   public boolean func_148278_a(int var1, int var2, int var3, int var4, int var5, int var6) {
      if (var5 <= 32) {
         if (var5 < 32 && var5 > 16 && this.func_178013_b()) {
            this.field_148303_c.func_146790_a(var1);
            this.field_148303_c.func_146796_h();
            return true;
         }

         if (var5 < 16 && var6 < 16 && this.field_148303_c.func_175392_a(this, var1)) {
            this.field_148303_c.func_175391_a(this, var1, GuiScreen.func_146272_n());
            return true;
         }

         if (var5 < 16 && var6 > 16 && this.field_148303_c.func_175394_b(this, var1)) {
            this.field_148303_c.func_175393_b(this, var1, GuiScreen.func_146272_n());
            return true;
         }
      }

      this.field_148303_c.func_146790_a(var1);
      if (Minecraft.func_71386_F() - this.field_148298_f < 250L) {
         this.field_148303_c.func_146796_h();
      }

      this.field_148298_f = Minecraft.func_71386_F();
      return false;
   }

   public void func_178011_a(int var1, int var2, int var3) {
   }

   public void func_148277_b(int var1, int var2, int var3, int var4, int var5, int var6) {
   }

   public ServerData func_148296_a() {
      return this.field_148301_e;
   }
}
