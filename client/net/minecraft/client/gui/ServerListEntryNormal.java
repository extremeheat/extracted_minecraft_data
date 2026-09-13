package net.minecraft.client.gui;

import com.google.common.base.Charsets;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

public class ServerListEntryNormal implements GuiListExtended$IGuiListEntry {
   private static final Logger field_148304_a = LogManager.getLogger();
   private static final ThreadPoolExecutor field_148302_b = new ScheduledThreadPoolExecutor(
      5, new ThreadFactoryBuilder().setNameFormat("Server Pinger #%d").setDaemon(true).build()
   );
   private final GuiMultiplayer field_148303_c;
   private final Minecraft field_148300_d;
   private final ServerData field_148301_e;
   private long field_148298_f;
   private String field_148299_g;
   private DynamicTexture field_148305_h;
   private ResourceLocation field_148306_i;

   protected ServerListEntryNormal(GuiMultiplayer var1, ServerData var2) {
      super();
      this.field_148303_c = var1;
      this.field_148301_e = var2;
      this.field_148300_d = Minecraft.func_71410_x();
      this.field_148306_i = new ResourceLocation("servers/" + var2.field_78845_b + "/icon");
      this.field_148305_h = (DynamicTexture)this.field_148300_d.func_110434_K().func_110581_b(this.field_148306_i);
   }

   @Override
   public void func_148279_a(int var1, int var2, int var3, int var4, int var5, Tessellator var6, int var7, int var8, boolean var9) {
      if (!this.field_148301_e.field_78841_f) {
         this.field_148301_e.field_78841_f = true;
         this.field_148301_e.field_78844_e = -2L;
         this.field_148301_e.field_78843_d = "";
         this.field_148301_e.field_78846_c = "";
         field_148302_b.submit(new ServerListEntryNormal$1(this));
      }

      boolean var10 = this.field_148301_e.field_82821_f > 5;
      boolean var11 = this.field_148301_e.field_82821_f < 5;
      boolean var12 = var10 || var11;
      this.field_148300_d.field_71466_p.func_78276_b(this.field_148301_e.field_78847_a, var2 + 32 + 3, var3 + 1, 16777215);
      List var13 = this.field_148300_d.field_71466_p.func_78271_c(this.field_148301_e.field_78843_d, var4 - 32 - 2);

      for(int var14 = 0; var14 < Math.min(var13.size(), 2); ++var14) {
         this.field_148300_d
            .field_71466_p
            .func_78276_b((String)var13.get(var14), var2 + 32 + 3, var3 + 12 + this.field_148300_d.field_71466_p.field_78288_b * var14, 8421504);
      }

      String var22 = var12 ? EnumChatFormatting.DARK_RED + this.field_148301_e.field_82822_g : this.field_148301_e.field_78846_c;
      int var15 = this.field_148300_d.field_71466_p.func_78256_a(var22);
      this.field_148300_d.field_71466_p.func_78276_b(var22, var2 + var4 - var15 - 15 - 2, var3 + 1, 8421504);
      byte var16 = 0;
      String var18 = null;
      int var17;
      String var19;
      if (var12) {
         var17 = 5;
         var19 = var10 ? "Client out of date!" : "Server out of date!";
         var18 = this.field_148301_e.field_147412_i;
      } else if (this.field_148301_e.field_78841_f && this.field_148301_e.field_78844_e != -2L) {
         if (this.field_148301_e.field_78844_e < 0L) {
            var17 = 5;
         } else if (this.field_148301_e.field_78844_e < 150L) {
            var17 = 0;
         } else if (this.field_148301_e.field_78844_e < 300L) {
            var17 = 1;
         } else if (this.field_148301_e.field_78844_e < 600L) {
            var17 = 2;
         } else if (this.field_148301_e.field_78844_e < 1000L) {
            var17 = 3;
         } else {
            var17 = 4;
         }

         if (this.field_148301_e.field_78844_e < 0L) {
            var19 = "(no connection)";
         } else {
            var19 = this.field_148301_e.field_78844_e + "ms";
            var18 = this.field_148301_e.field_147412_i;
         }
      } else {
         var16 = 1;
         var17 = (int)(Minecraft.func_71386_F() / 100L + (long)(var1 * 2) & 7L);
         if (var17 > 4) {
            var17 = 8 - var17;
         }

         var19 = "Pinging...";
      }

      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.field_148300_d.func_110434_K().func_110577_a(Gui.field_110324_m);
      Gui.func_146110_a(var2 + var4 - 15, var3, (float)(var16 * 10), (float)(176 + var17 * 8), 10, 8, 256.0F, 256.0F);
      if (this.field_148301_e.func_147409_e() != null && !this.field_148301_e.func_147409_e().equals(this.field_148299_g)) {
         this.field_148299_g = this.field_148301_e.func_147409_e();
         this.func_148297_b();
         this.field_148303_c.func_146795_p().func_78855_b();
      }

      if (this.field_148305_h != null) {
         this.field_148300_d.func_110434_K().func_110577_a(this.field_148306_i);
         Gui.func_146110_a(var2, var3, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
      }

      int var20 = var7 - var2;
      int var21 = var8 - var3;
      if (var20 >= var4 - 15 && var20 <= var4 - 5 && var21 >= 0 && var21 <= 8) {
         this.field_148303_c.func_146793_a(var19);
      } else if (var20 >= var4 - var15 - 15 - 2 && var20 <= var4 - 15 - 2 && var21 >= 0 && var21 <= 8) {
         this.field_148303_c.func_146793_a(var18);
      }
   }

   private void func_148297_b() {
      if (this.field_148301_e.func_147409_e() == null) {
         this.field_148300_d.func_110434_K().func_147645_c(this.field_148306_i);
         this.field_148305_h = null;
      } else {
         ByteBuf var2 = Unpooled.copiedBuffer(this.field_148301_e.func_147409_e(), Charsets.UTF_8);
         ByteBuf var3 = Base64.decode(var2);

         BufferedImage var1;
         label62: {
            try {
               var1 = ImageIO.read(new ByteBufInputStream(var3));
               Validate.validState(var1.getWidth() == 64, "Must be 64 pixels wide", new Object[0]);
               Validate.validState(var1.getHeight() == 64, "Must be 64 pixels high", new Object[0]);
               break label62;
            } catch (Exception var8) {
               field_148304_a.error("Invalid icon for server " + this.field_148301_e.field_78847_a + " (" + this.field_148301_e.field_78845_b + ")", var8);
               this.field_148301_e.func_147407_a(null);
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

   @Override
   public boolean func_148278_a(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.field_148303_c.func_146790_a(var1);
      if (Minecraft.func_71386_F() - this.field_148298_f < 250L) {
         this.field_148303_c.func_146796_h();
      }

      this.field_148298_f = Minecraft.func_71386_F();
      return false;
   }

   @Override
   public void func_148277_b(int var1, int var2, int var3, int var4, int var5, int var6) {
   }

   public ServerData func_148296_a() {
      return this.field_148301_e;
   }
}
