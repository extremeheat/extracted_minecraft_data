package net.minecraft.client.gui.stream;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.stream.IStream;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import tv.twitch.chat.ChatUserInfo;
import tv.twitch.chat.ChatUserMode;
import tv.twitch.chat.ChatUserSubscription;

public class GuiTwitchUserMode extends GuiScreen {
   private static final EnumChatFormatting field_152331_a;
   private static final EnumChatFormatting field_152335_f;
   private static final EnumChatFormatting field_152336_g;
   private final ChatUserInfo field_152337_h;
   private final IChatComponent field_152338_i;
   private final List<IChatComponent> field_152332_r = Lists.newArrayList();
   private final IStream field_152333_s;
   private int field_152334_t;

   public GuiTwitchUserMode(IStream var1, ChatUserInfo var2) {
      super();
      this.field_152333_s = var1;
      this.field_152337_h = var2;
      this.field_152338_i = new ChatComponentText(var2.displayName);
      this.field_152332_r.addAll(func_152328_a(var2.modes, var2.subscriptions, var1));
   }

   public static List<IChatComponent> func_152328_a(Set<ChatUserMode> var0, Set<ChatUserSubscription> var1, IStream var2) {
      String var3 = var2 == null ? null : var2.func_152921_C();
      boolean var4 = var2 != null && var2.func_152927_B();
      ArrayList var5 = Lists.newArrayList();
      Iterator var6 = var0.iterator();

      IChatComponent var8;
      ChatComponentText var9;
      while(var6.hasNext()) {
         ChatUserMode var7 = (ChatUserMode)var6.next();
         var8 = func_152329_a(var7, var3, var4);
         if (var8 != null) {
            var9 = new ChatComponentText("- ");
            var9.func_150257_a(var8);
            var5.add(var9);
         }
      }

      var6 = var1.iterator();

      while(var6.hasNext()) {
         ChatUserSubscription var10 = (ChatUserSubscription)var6.next();
         var8 = func_152330_a(var10, var3, var4);
         if (var8 != null) {
            var9 = new ChatComponentText("- ");
            var9.func_150257_a(var8);
            var5.add(var9);
         }
      }

      return var5;
   }

   public static IChatComponent func_152330_a(ChatUserSubscription var0, String var1, boolean var2) {
      ChatComponentTranslation var3 = null;
      if (var0 == ChatUserSubscription.TTV_CHAT_USERSUB_SUBSCRIBER) {
         if (var1 == null) {
            var3 = new ChatComponentTranslation("stream.user.subscription.subscriber", new Object[0]);
         } else if (var2) {
            var3 = new ChatComponentTranslation("stream.user.subscription.subscriber.self", new Object[0]);
         } else {
            var3 = new ChatComponentTranslation("stream.user.subscription.subscriber.other", new Object[]{var1});
         }

         var3.func_150256_b().func_150238_a(field_152331_a);
      } else if (var0 == ChatUserSubscription.TTV_CHAT_USERSUB_TURBO) {
         var3 = new ChatComponentTranslation("stream.user.subscription.turbo", new Object[0]);
         var3.func_150256_b().func_150238_a(field_152336_g);
      }

      return var3;
   }

   public static IChatComponent func_152329_a(ChatUserMode var0, String var1, boolean var2) {
      ChatComponentTranslation var3 = null;
      if (var0 == ChatUserMode.TTV_CHAT_USERMODE_ADMINSTRATOR) {
         var3 = new ChatComponentTranslation("stream.user.mode.administrator", new Object[0]);
         var3.func_150256_b().func_150238_a(field_152336_g);
      } else if (var0 == ChatUserMode.TTV_CHAT_USERMODE_BANNED) {
         if (var1 == null) {
            var3 = new ChatComponentTranslation("stream.user.mode.banned", new Object[0]);
         } else if (var2) {
            var3 = new ChatComponentTranslation("stream.user.mode.banned.self", new Object[0]);
         } else {
            var3 = new ChatComponentTranslation("stream.user.mode.banned.other", new Object[]{var1});
         }

         var3.func_150256_b().func_150238_a(field_152335_f);
      } else if (var0 == ChatUserMode.TTV_CHAT_USERMODE_BROADCASTER) {
         if (var1 == null) {
            var3 = new ChatComponentTranslation("stream.user.mode.broadcaster", new Object[0]);
         } else if (var2) {
            var3 = new ChatComponentTranslation("stream.user.mode.broadcaster.self", new Object[0]);
         } else {
            var3 = new ChatComponentTranslation("stream.user.mode.broadcaster.other", new Object[0]);
         }

         var3.func_150256_b().func_150238_a(field_152331_a);
      } else if (var0 == ChatUserMode.TTV_CHAT_USERMODE_MODERATOR) {
         if (var1 == null) {
            var3 = new ChatComponentTranslation("stream.user.mode.moderator", new Object[0]);
         } else if (var2) {
            var3 = new ChatComponentTranslation("stream.user.mode.moderator.self", new Object[0]);
         } else {
            var3 = new ChatComponentTranslation("stream.user.mode.moderator.other", new Object[]{var1});
         }

         var3.func_150256_b().func_150238_a(field_152331_a);
      } else if (var0 == ChatUserMode.TTV_CHAT_USERMODE_STAFF) {
         var3 = new ChatComponentTranslation("stream.user.mode.staff", new Object[0]);
         var3.func_150256_b().func_150238_a(field_152336_g);
      }

      return var3;
   }

   public void func_73866_w_() {
      int var1 = this.field_146294_l / 3;
      int var2 = var1 - 130;
      this.field_146292_n.add(new GuiButton(1, var1 * 0 + var2 / 2, this.field_146295_m - 70, 130, 20, I18n.func_135052_a("stream.userinfo.timeout")));
      this.field_146292_n.add(new GuiButton(0, var1 * 1 + var2 / 2, this.field_146295_m - 70, 130, 20, I18n.func_135052_a("stream.userinfo.ban")));
      this.field_146292_n.add(new GuiButton(2, var1 * 2 + var2 / 2, this.field_146295_m - 70, 130, 20, I18n.func_135052_a("stream.userinfo.mod")));
      this.field_146292_n.add(new GuiButton(5, var1 * 0 + var2 / 2, this.field_146295_m - 45, 130, 20, I18n.func_135052_a("gui.cancel")));
      this.field_146292_n.add(new GuiButton(3, var1 * 1 + var2 / 2, this.field_146295_m - 45, 130, 20, I18n.func_135052_a("stream.userinfo.unban")));
      this.field_146292_n.add(new GuiButton(4, var1 * 2 + var2 / 2, this.field_146295_m - 45, 130, 20, I18n.func_135052_a("stream.userinfo.unmod")));
      int var3 = 0;

      IChatComponent var5;
      for(Iterator var4 = this.field_152332_r.iterator(); var4.hasNext(); var3 = Math.max(var3, this.field_146289_q.func_78256_a(var5.func_150254_d()))) {
         var5 = (IChatComponent)var4.next();
      }

      this.field_152334_t = this.field_146294_l / 2 - var3 / 2;
   }

   protected void func_146284_a(GuiButton var1) {
      if (var1.field_146124_l) {
         if (var1.field_146127_k == 0) {
            this.field_152333_s.func_152917_b("/ban " + this.field_152337_h.displayName);
         } else if (var1.field_146127_k == 3) {
            this.field_152333_s.func_152917_b("/unban " + this.field_152337_h.displayName);
         } else if (var1.field_146127_k == 2) {
            this.field_152333_s.func_152917_b("/mod " + this.field_152337_h.displayName);
         } else if (var1.field_146127_k == 4) {
            this.field_152333_s.func_152917_b("/unmod " + this.field_152337_h.displayName);
         } else if (var1.field_146127_k == 1) {
            this.field_152333_s.func_152917_b("/timeout " + this.field_152337_h.displayName);
         }

         this.field_146297_k.func_147108_a((GuiScreen)null);
      }
   }

   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146276_q_();
      this.func_73732_a(this.field_146289_q, this.field_152338_i.func_150260_c(), this.field_146294_l / 2, 70, 16777215);
      int var4 = 80;

      for(Iterator var5 = this.field_152332_r.iterator(); var5.hasNext(); var4 += this.field_146289_q.field_78288_b) {
         IChatComponent var6 = (IChatComponent)var5.next();
         this.func_73731_b(this.field_146289_q, var6.func_150254_d(), this.field_152334_t, var4, 16777215);
      }

      super.func_73863_a(var1, var2, var3);
   }

   static {
      field_152331_a = EnumChatFormatting.DARK_GREEN;
      field_152335_f = EnumChatFormatting.RED;
      field_152336_g = EnumChatFormatting.DARK_PURPLE;
   }
}
