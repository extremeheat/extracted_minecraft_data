package net.minecraft.client.gui.stream;

import com.google.common.collect.Lists;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.stream.IStream;
import net.minecraft.client.stream.NullStream;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Session;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import tv.twitch.ErrorCode;

public class GuiStreamUnavailable extends GuiScreen {
   private static final Logger field_152322_a = LogManager.getLogger();
   private final IChatComponent field_152324_f;
   private final GuiScreen field_152325_g;
   private final GuiStreamUnavailable.Reason field_152326_h;
   private final List<ChatComponentTranslation> field_152327_i;
   private final List<String> field_152323_r;

   public GuiStreamUnavailable(GuiScreen var1, GuiStreamUnavailable.Reason var2) {
      this(var1, var2, (List)null);
   }

   public GuiStreamUnavailable(GuiScreen var1, GuiStreamUnavailable.Reason var2, List<ChatComponentTranslation> var3) {
      super();
      this.field_152324_f = new ChatComponentTranslation("stream.unavailable.title", new Object[0]);
      this.field_152323_r = Lists.newArrayList();
      this.field_152325_g = var1;
      this.field_152326_h = var2;
      this.field_152327_i = var3;
   }

   public void func_73866_w_() {
      if (this.field_152323_r.isEmpty()) {
         this.field_152323_r.addAll(this.field_146289_q.func_78271_c(this.field_152326_h.func_152561_a().func_150254_d(), (int)((float)this.field_146294_l * 0.75F)));
         if (this.field_152327_i != null) {
            this.field_152323_r.add("");
            Iterator var1 = this.field_152327_i.iterator();

            while(var1.hasNext()) {
               ChatComponentTranslation var2 = (ChatComponentTranslation)var1.next();
               this.field_152323_r.add(var2.func_150261_e());
            }
         }
      }

      if (this.field_152326_h.func_152559_b() != null) {
         this.field_146292_n.add(new GuiButton(0, this.field_146294_l / 2 - 155, this.field_146295_m - 50, 150, 20, I18n.func_135052_a("gui.cancel")));
         this.field_146292_n.add(new GuiButton(1, this.field_146294_l / 2 - 155 + 160, this.field_146295_m - 50, 150, 20, I18n.func_135052_a(this.field_152326_h.func_152559_b().func_150254_d())));
      } else {
         this.field_146292_n.add(new GuiButton(0, this.field_146294_l / 2 - 75, this.field_146295_m - 50, 150, 20, I18n.func_135052_a("gui.cancel")));
      }

   }

   public void func_146281_b() {
   }

   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146276_q_();
      int var4 = Math.max((int)((double)this.field_146295_m * 0.85D / 2.0D - (double)((float)(this.field_152323_r.size() * this.field_146289_q.field_78288_b) / 2.0F)), 50);
      this.func_73732_a(this.field_146289_q, this.field_152324_f.func_150254_d(), this.field_146294_l / 2, var4 - this.field_146289_q.field_78288_b * 2, 16777215);

      for(Iterator var5 = this.field_152323_r.iterator(); var5.hasNext(); var4 += this.field_146289_q.field_78288_b) {
         String var6 = (String)var5.next();
         this.func_73732_a(this.field_146289_q, var6, this.field_146294_l / 2, var4, 10526880);
      }

      super.func_73863_a(var1, var2, var3);
   }

   protected void func_146284_a(GuiButton var1) {
      if (var1.field_146124_l) {
         if (var1.field_146127_k == 1) {
            switch(this.field_152326_h) {
            case ACCOUNT_NOT_BOUND:
            case FAILED_TWITCH_AUTH:
               this.func_152320_a("https://account.mojang.com/me/settings");
               break;
            case ACCOUNT_NOT_MIGRATED:
               this.func_152320_a("https://account.mojang.com/migrate");
               break;
            case UNSUPPORTED_OS_MAC:
               this.func_152320_a("http://www.apple.com/osx/");
               break;
            case UNKNOWN:
            case LIBRARY_FAILURE:
            case INITIALIZATION_FAILURE:
               this.func_152320_a("http://bugs.mojang.com/browse/MC");
            }
         }

         this.field_146297_k.func_147108_a(this.field_152325_g);
      }
   }

   private void func_152320_a(String var1) {
      try {
         Class var2 = Class.forName("java.awt.Desktop");
         Object var3 = var2.getMethod("getDesktop").invoke((Object)null);
         var2.getMethod("browse", URI.class).invoke(var3, new URI(var1));
      } catch (Throwable var4) {
         field_152322_a.error("Couldn't open link", var4);
      }

   }

   public static void func_152321_a(GuiScreen var0) {
      Minecraft var1 = Minecraft.func_71410_x();
      IStream var2 = var1.func_152346_Z();
      if (!OpenGlHelper.field_148823_f) {
         ArrayList var3 = Lists.newArrayList();
         var3.add(new ChatComponentTranslation("stream.unavailable.no_fbo.version", new Object[]{GL11.glGetString(7938)}));
         var3.add(new ChatComponentTranslation("stream.unavailable.no_fbo.blend", new Object[]{GLContext.getCapabilities().GL_EXT_blend_func_separate}));
         var3.add(new ChatComponentTranslation("stream.unavailable.no_fbo.arb", new Object[]{GLContext.getCapabilities().GL_ARB_framebuffer_object}));
         var3.add(new ChatComponentTranslation("stream.unavailable.no_fbo.ext", new Object[]{GLContext.getCapabilities().GL_EXT_framebuffer_object}));
         var1.func_147108_a(new GuiStreamUnavailable(var0, GuiStreamUnavailable.Reason.NO_FBO, var3));
      } else if (var2 instanceof NullStream) {
         if (((NullStream)var2).func_152937_a().getMessage().contains("Can't load AMD 64-bit .dll on a IA 32-bit platform")) {
            var1.func_147108_a(new GuiStreamUnavailable(var0, GuiStreamUnavailable.Reason.LIBRARY_ARCH_MISMATCH));
         } else {
            var1.func_147108_a(new GuiStreamUnavailable(var0, GuiStreamUnavailable.Reason.LIBRARY_FAILURE));
         }
      } else if (!var2.func_152928_D() && var2.func_152912_E() == ErrorCode.TTV_EC_OS_TOO_OLD) {
         switch(Util.func_110647_a()) {
         case WINDOWS:
            var1.func_147108_a(new GuiStreamUnavailable(var0, GuiStreamUnavailable.Reason.UNSUPPORTED_OS_WINDOWS));
            break;
         case OSX:
            var1.func_147108_a(new GuiStreamUnavailable(var0, GuiStreamUnavailable.Reason.UNSUPPORTED_OS_MAC));
            break;
         default:
            var1.func_147108_a(new GuiStreamUnavailable(var0, GuiStreamUnavailable.Reason.UNSUPPORTED_OS_OTHER));
         }
      } else if (!var1.func_180509_L().containsKey("twitch_access_token")) {
         if (var1.func_110432_I().func_152428_f() == Session.Type.LEGACY) {
            var1.func_147108_a(new GuiStreamUnavailable(var0, GuiStreamUnavailable.Reason.ACCOUNT_NOT_MIGRATED));
         } else {
            var1.func_147108_a(new GuiStreamUnavailable(var0, GuiStreamUnavailable.Reason.ACCOUNT_NOT_BOUND));
         }
      } else if (!var2.func_152913_F()) {
         switch(var2.func_152918_H()) {
         case INVALID_TOKEN:
            var1.func_147108_a(new GuiStreamUnavailable(var0, GuiStreamUnavailable.Reason.FAILED_TWITCH_AUTH));
            break;
         case ERROR:
         default:
            var1.func_147108_a(new GuiStreamUnavailable(var0, GuiStreamUnavailable.Reason.FAILED_TWITCH_AUTH_ERROR));
         }
      } else if (var2.func_152912_E() != null) {
         List var4 = Arrays.asList(new ChatComponentTranslation("stream.unavailable.initialization_failure.extra", new Object[]{ErrorCode.getString(var2.func_152912_E())}));
         var1.func_147108_a(new GuiStreamUnavailable(var0, GuiStreamUnavailable.Reason.INITIALIZATION_FAILURE, var4));
      } else {
         var1.func_147108_a(new GuiStreamUnavailable(var0, GuiStreamUnavailable.Reason.UNKNOWN));
      }

   }

   public static enum Reason {
      NO_FBO(new ChatComponentTranslation("stream.unavailable.no_fbo", new Object[0])),
      LIBRARY_ARCH_MISMATCH(new ChatComponentTranslation("stream.unavailable.library_arch_mismatch", new Object[0])),
      LIBRARY_FAILURE(new ChatComponentTranslation("stream.unavailable.library_failure", new Object[0]), new ChatComponentTranslation("stream.unavailable.report_to_mojang", new Object[0])),
      UNSUPPORTED_OS_WINDOWS(new ChatComponentTranslation("stream.unavailable.not_supported.windows", new Object[0])),
      UNSUPPORTED_OS_MAC(new ChatComponentTranslation("stream.unavailable.not_supported.mac", new Object[0]), new ChatComponentTranslation("stream.unavailable.not_supported.mac.okay", new Object[0])),
      UNSUPPORTED_OS_OTHER(new ChatComponentTranslation("stream.unavailable.not_supported.other", new Object[0])),
      ACCOUNT_NOT_MIGRATED(new ChatComponentTranslation("stream.unavailable.account_not_migrated", new Object[0]), new ChatComponentTranslation("stream.unavailable.account_not_migrated.okay", new Object[0])),
      ACCOUNT_NOT_BOUND(new ChatComponentTranslation("stream.unavailable.account_not_bound", new Object[0]), new ChatComponentTranslation("stream.unavailable.account_not_bound.okay", new Object[0])),
      FAILED_TWITCH_AUTH(new ChatComponentTranslation("stream.unavailable.failed_auth", new Object[0]), new ChatComponentTranslation("stream.unavailable.failed_auth.okay", new Object[0])),
      FAILED_TWITCH_AUTH_ERROR(new ChatComponentTranslation("stream.unavailable.failed_auth_error", new Object[0])),
      INITIALIZATION_FAILURE(new ChatComponentTranslation("stream.unavailable.initialization_failure", new Object[0]), new ChatComponentTranslation("stream.unavailable.report_to_mojang", new Object[0])),
      UNKNOWN(new ChatComponentTranslation("stream.unavailable.unknown", new Object[0]), new ChatComponentTranslation("stream.unavailable.report_to_mojang", new Object[0]));

      private final IChatComponent field_152574_m;
      private final IChatComponent field_152575_n;

      private Reason(IChatComponent var3) {
         this(var3, (IChatComponent)null);
      }

      private Reason(IChatComponent var3, IChatComponent var4) {
         this.field_152574_m = var3;
         this.field_152575_n = var4;
      }

      public IChatComponent func_152561_a() {
         return this.field_152574_m;
      }

      public IChatComponent func_152559_b() {
         return this.field_152575_n;
      }
   }
}
