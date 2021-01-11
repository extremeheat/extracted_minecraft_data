package net.minecraft.client.stream;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.properties.Property;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.stream.GuiTwitchUserMode;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.HttpUtil;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.lwjgl.opengl.GL11;
import tv.twitch.AuthToken;
import tv.twitch.ErrorCode;
import tv.twitch.broadcast.EncodingCpuUsage;
import tv.twitch.broadcast.FrameBuffer;
import tv.twitch.broadcast.GameInfo;
import tv.twitch.broadcast.IngestList;
import tv.twitch.broadcast.IngestServer;
import tv.twitch.broadcast.StreamInfo;
import tv.twitch.broadcast.VideoParams;
import tv.twitch.chat.ChatRawMessage;
import tv.twitch.chat.ChatTokenizedMessage;
import tv.twitch.chat.ChatUserInfo;
import tv.twitch.chat.ChatUserMode;
import tv.twitch.chat.ChatUserSubscription;

public class TwitchStream implements BroadcastController.BroadcastListener, ChatController.ChatListener, IngestServerTester.IngestTestListener, IStream {
   private static final Logger field_152950_b = LogManager.getLogger();
   public static final Marker field_152949_a = MarkerManager.getMarker("STREAM");
   private final BroadcastController field_152951_c;
   private final ChatController field_152952_d;
   private String field_176029_e;
   private final Minecraft field_152953_e;
   private final IChatComponent field_152954_f = new ChatComponentText("Twitch");
   private final Map<String, ChatUserInfo> field_152955_g = Maps.newHashMap();
   private Framebuffer field_152956_h;
   private boolean field_152957_i;
   private int field_152958_j = 30;
   private long field_152959_k = 0L;
   private boolean field_152960_l = false;
   private boolean field_152961_m;
   private boolean field_152962_n;
   private boolean field_152963_o;
   private IStream.AuthFailureReason field_152964_p;
   private static boolean field_152965_q;

   public TwitchStream(Minecraft var1, final Property var2) {
      super();
      this.field_152964_p = IStream.AuthFailureReason.ERROR;
      this.field_152953_e = var1;
      this.field_152951_c = new BroadcastController();
      this.field_152952_d = new ChatController();
      this.field_152951_c.func_152841_a(this);
      this.field_152952_d.func_152990_a(this);
      this.field_152951_c.func_152842_a("nmt37qblda36pvonovdkbopzfzw3wlq");
      this.field_152952_d.func_152984_a("nmt37qblda36pvonovdkbopzfzw3wlq");
      this.field_152954_f.func_150256_b().func_150238_a(EnumChatFormatting.DARK_PURPLE);
      if (var2 != null && !Strings.isNullOrEmpty(var2.getValue()) && OpenGlHelper.field_148823_f) {
         Thread var3 = new Thread("Twitch authenticator") {
            public void run() {
               try {
                  URL var1 = new URL("https://api.twitch.tv/kraken?oauth_token=" + URLEncoder.encode(var2.getValue(), "UTF-8"));
                  String var2x = HttpUtil.func_152755_a(var1);
                  JsonObject var3 = JsonUtils.func_151210_l((new JsonParser()).parse(var2x), "Response");
                  JsonObject var4 = JsonUtils.func_152754_s(var3, "token");
                  if (JsonUtils.func_151212_i(var4, "valid")) {
                     String var5 = JsonUtils.func_151200_h(var4, "user_name");
                     TwitchStream.field_152950_b.debug(TwitchStream.field_152949_a, "Authenticated with twitch; username is {}", new Object[]{var5});
                     AuthToken var6 = new AuthToken();
                     var6.data = var2.getValue();
                     TwitchStream.this.field_152951_c.func_152818_a(var5, var6);
                     TwitchStream.this.field_152952_d.func_152998_c(var5);
                     TwitchStream.this.field_152952_d.func_152994_a(var6);
                     Runtime.getRuntime().addShutdownHook(new Thread("Twitch shutdown hook") {
                        public void run() {
                           TwitchStream.this.func_152923_i();
                        }
                     });
                     TwitchStream.this.field_152951_c.func_152817_A();
                     TwitchStream.this.field_152952_d.func_175984_n();
                  } else {
                     TwitchStream.this.field_152964_p = IStream.AuthFailureReason.INVALID_TOKEN;
                     TwitchStream.field_152950_b.error(TwitchStream.field_152949_a, "Given twitch access token is invalid");
                  }
               } catch (IOException var7) {
                  TwitchStream.this.field_152964_p = IStream.AuthFailureReason.ERROR;
                  TwitchStream.field_152950_b.error(TwitchStream.field_152949_a, "Could not authenticate with twitch", var7);
               }

            }
         };
         var3.setDaemon(true);
         var3.start();
      }

   }

   public void func_152923_i() {
      field_152950_b.debug(field_152949_a, "Shutdown streaming");
      this.field_152951_c.statCallback();
      this.field_152952_d.func_175988_p();
   }

   public void func_152935_j() {
      int var1 = this.field_152953_e.field_71474_y.field_152408_R;
      boolean var2 = this.field_176029_e != null && this.field_152952_d.func_175990_d(this.field_176029_e);
      boolean var3 = this.field_152952_d.func_153000_j() == ChatController.ChatState.Initialized && (this.field_176029_e == null || this.field_152952_d.func_175989_e(this.field_176029_e) == ChatController.EnumChannelState.Disconnected);
      if (var1 == 2) {
         if (var2) {
            field_152950_b.debug(field_152949_a, "Disconnecting from twitch chat per user options");
            this.field_152952_d.func_175991_l(this.field_176029_e);
         }
      } else if (var1 == 1) {
         if (var3 && this.field_152951_c.func_152849_q()) {
            field_152950_b.debug(field_152949_a, "Connecting to twitch chat per user options");
            this.func_152942_I();
         }
      } else if (var1 == 0) {
         if (var2 && !this.func_152934_n()) {
            field_152950_b.debug(field_152949_a, "Disconnecting from twitch chat as user is no longer streaming");
            this.field_152952_d.func_175991_l(this.field_176029_e);
         } else if (var3 && this.func_152934_n()) {
            field_152950_b.debug(field_152949_a, "Connecting to twitch chat as user is streaming");
            this.func_152942_I();
         }
      }

      this.field_152951_c.func_152821_H();
      this.field_152952_d.func_152997_n();
   }

   protected void func_152942_I() {
      ChatController.ChatState var1 = this.field_152952_d.func_153000_j();
      String var2 = this.field_152951_c.func_152843_l().name;
      this.field_176029_e = var2;
      if (var1 != ChatController.ChatState.Initialized) {
         field_152950_b.warn("Invalid twitch chat state {}", new Object[]{var1});
      } else if (this.field_152952_d.func_175989_e(this.field_176029_e) == ChatController.EnumChannelState.Disconnected) {
         this.field_152952_d.func_152986_d(var2);
      } else {
         field_152950_b.warn("Invalid twitch chat state {}", new Object[]{var1});
      }

   }

   public void func_152922_k() {
      if (this.field_152951_c.func_152850_m() && !this.field_152951_c.func_152839_p()) {
         long var1 = System.nanoTime();
         long var3 = (long)(1000000000 / this.field_152958_j);
         long var5 = var1 - this.field_152959_k;
         boolean var7 = var5 >= var3;
         if (var7) {
            FrameBuffer var8 = this.field_152951_c.func_152822_N();
            Framebuffer var9 = this.field_152953_e.func_147110_a();
            this.field_152956_h.func_147610_a(true);
            GlStateManager.func_179128_n(5889);
            GlStateManager.func_179094_E();
            GlStateManager.func_179096_D();
            GlStateManager.func_179130_a(0.0D, (double)this.field_152956_h.field_147621_c, (double)this.field_152956_h.field_147618_d, 0.0D, 1000.0D, 3000.0D);
            GlStateManager.func_179128_n(5888);
            GlStateManager.func_179094_E();
            GlStateManager.func_179096_D();
            GlStateManager.func_179109_b(0.0F, 0.0F, -2000.0F);
            GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.func_179083_b(0, 0, this.field_152956_h.field_147621_c, this.field_152956_h.field_147618_d);
            GlStateManager.func_179098_w();
            GlStateManager.func_179118_c();
            GlStateManager.func_179084_k();
            float var10 = (float)this.field_152956_h.field_147621_c;
            float var11 = (float)this.field_152956_h.field_147618_d;
            float var12 = (float)var9.field_147621_c / (float)var9.field_147622_a;
            float var13 = (float)var9.field_147618_d / (float)var9.field_147620_b;
            var9.func_147612_c();
            GL11.glTexParameterf(3553, 10241, 9729.0F);
            GL11.glTexParameterf(3553, 10240, 9729.0F);
            Tessellator var14 = Tessellator.func_178181_a();
            WorldRenderer var15 = var14.func_178180_c();
            var15.func_181668_a(7, DefaultVertexFormats.field_181707_g);
            var15.func_181662_b(0.0D, (double)var11, 0.0D).func_181673_a(0.0D, (double)var13).func_181675_d();
            var15.func_181662_b((double)var10, (double)var11, 0.0D).func_181673_a((double)var12, (double)var13).func_181675_d();
            var15.func_181662_b((double)var10, 0.0D, 0.0D).func_181673_a((double)var12, 0.0D).func_181675_d();
            var15.func_181662_b(0.0D, 0.0D, 0.0D).func_181673_a(0.0D, 0.0D).func_181675_d();
            var14.func_78381_a();
            var9.func_147606_d();
            GlStateManager.func_179121_F();
            GlStateManager.func_179128_n(5889);
            GlStateManager.func_179121_F();
            GlStateManager.func_179128_n(5888);
            this.field_152951_c.func_152846_a(var8);
            this.field_152956_h.func_147609_e();
            this.field_152951_c.func_152859_b(var8);
            this.field_152959_k = var1;
         }

      }
   }

   public boolean func_152936_l() {
      return this.field_152951_c.func_152849_q();
   }

   public boolean func_152924_m() {
      return this.field_152951_c.func_152857_n();
   }

   public boolean func_152934_n() {
      return this.field_152951_c.func_152850_m();
   }

   public void func_152911_a(Metadata var1, long var2) {
      if (this.func_152934_n() && this.field_152957_i) {
         long var4 = this.field_152951_c.func_152844_x();
         if (!this.field_152951_c.func_152840_a(var1.func_152810_c(), var4 + var2, var1.func_152809_a(), var1.func_152806_b())) {
            field_152950_b.warn(field_152949_a, "Couldn't send stream metadata action at {}: {}", new Object[]{var4 + var2, var1});
         } else {
            field_152950_b.debug(field_152949_a, "Sent stream metadata action at {}: {}", new Object[]{var4 + var2, var1});
         }

      }
   }

   public void func_176026_a(Metadata var1, long var2, long var4) {
      if (this.func_152934_n() && this.field_152957_i) {
         long var6 = this.field_152951_c.func_152844_x();
         String var8 = var1.func_152809_a();
         String var9 = var1.func_152806_b();
         long var10 = this.field_152951_c.func_177946_b(var1.func_152810_c(), var6 + var2, var8, var9);
         if (var10 < 0L) {
            field_152950_b.warn(field_152949_a, "Could not send stream metadata sequence from {} to {}: {}", new Object[]{var6 + var2, var6 + var4, var1});
         } else if (this.field_152951_c.func_177947_a(var1.func_152810_c(), var6 + var4, var10, var8, var9)) {
            field_152950_b.debug(field_152949_a, "Sent stream metadata sequence from {} to {}: {}", new Object[]{var6 + var2, var6 + var4, var1});
         } else {
            field_152950_b.warn(field_152949_a, "Half-sent stream metadata sequence from {} to {}: {}", new Object[]{var6 + var2, var6 + var4, var1});
         }

      }
   }

   public boolean func_152919_o() {
      return this.field_152951_c.func_152839_p();
   }

   public void func_152931_p() {
      if (this.field_152951_c.func_152830_D()) {
         field_152950_b.debug(field_152949_a, "Requested commercial from Twitch");
      } else {
         field_152950_b.warn(field_152949_a, "Could not request commercial from Twitch");
      }

   }

   public void func_152916_q() {
      this.field_152951_c.func_152847_F();
      this.field_152962_n = true;
      this.func_152915_s();
   }

   public void func_152933_r() {
      this.field_152951_c.func_152854_G();
      this.field_152962_n = false;
      this.func_152915_s();
   }

   public void func_152915_s() {
      if (this.func_152934_n()) {
         float var1 = this.field_152953_e.field_71474_y.field_152402_L;
         boolean var2 = this.field_152962_n || var1 <= 0.0F;
         this.field_152951_c.func_152837_b(var2 ? 0.0F : var1);
         this.field_152951_c.func_152829_a(this.func_152929_G() ? 0.0F : this.field_152953_e.field_71474_y.field_152401_K);
      }

   }

   public void func_152930_t() {
      GameSettings var1 = this.field_152953_e.field_71474_y;
      VideoParams var2 = this.field_152951_c.func_152834_a(func_152946_b(var1.field_152403_M), func_152948_a(var1.field_152404_N), func_152947_c(var1.field_152400_J), (float)this.field_152953_e.field_71443_c / (float)this.field_152953_e.field_71440_d);
      switch(var1.field_152405_O) {
      case 0:
         var2.encodingCpuUsage = EncodingCpuUsage.TTV_ECU_LOW;
         break;
      case 1:
         var2.encodingCpuUsage = EncodingCpuUsage.TTV_ECU_MEDIUM;
         break;
      case 2:
         var2.encodingCpuUsage = EncodingCpuUsage.TTV_ECU_HIGH;
      }

      if (this.field_152956_h == null) {
         this.field_152956_h = new Framebuffer(var2.outputWidth, var2.outputHeight, false);
      } else {
         this.field_152956_h.func_147613_a(var2.outputWidth, var2.outputHeight);
      }

      if (var1.field_152407_Q != null && var1.field_152407_Q.length() > 0) {
         IngestServer[] var3 = this.func_152925_v();
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            IngestServer var6 = var3[var5];
            if (var6.serverUrl.equals(var1.field_152407_Q)) {
               this.field_152951_c.func_152824_a(var6);
               break;
            }
         }
      }

      this.field_152958_j = var2.targetFps;
      this.field_152957_i = var1.field_152406_P;
      this.field_152951_c.func_152836_a(var2);
      field_152950_b.info(field_152949_a, "Streaming at {}/{} at {} kbps to {}", new Object[]{var2.outputWidth, var2.outputHeight, var2.maxKbps, this.field_152951_c.func_152833_s().serverUrl});
      this.field_152951_c.func_152828_a((String)null, "Minecraft", (String)null);
   }

   public void func_152914_u() {
      if (this.field_152951_c.func_152819_E()) {
         field_152950_b.info(field_152949_a, "Stopped streaming to Twitch");
      } else {
         field_152950_b.warn(field_152949_a, "Could not stop streaming to Twitch");
      }

   }

   public void func_152900_a(ErrorCode var1, AuthToken var2) {
   }

   public void func_152897_a(ErrorCode var1) {
      if (ErrorCode.succeeded(var1)) {
         field_152950_b.debug(field_152949_a, "Login attempt successful");
         this.field_152961_m = true;
      } else {
         field_152950_b.warn(field_152949_a, "Login attempt unsuccessful: {} (error code {})", new Object[]{ErrorCode.getString(var1), var1.getValue()});
         this.field_152961_m = false;
      }

   }

   public void func_152898_a(ErrorCode var1, GameInfo[] var2) {
   }

   public void func_152891_a(BroadcastController.BroadcastState var1) {
      field_152950_b.debug(field_152949_a, "Broadcast state changed to {}", new Object[]{var1});
      if (var1 == BroadcastController.BroadcastState.Initialized) {
         this.field_152951_c.func_152827_a(BroadcastController.BroadcastState.Authenticated);
      }

   }

   public void func_152895_a() {
      field_152950_b.info(field_152949_a, "Logged out of twitch");
   }

   public void func_152894_a(StreamInfo var1) {
      field_152950_b.debug(field_152949_a, "Stream info updated; {} viewers on stream ID {}", new Object[]{var1.viewers, var1.streamId});
   }

   public void func_152896_a(IngestList var1) {
   }

   public void func_152893_b(ErrorCode var1) {
      field_152950_b.warn(field_152949_a, "Issue submitting frame: {} (Error code {})", new Object[]{ErrorCode.getString(var1), var1.getValue()});
      this.field_152953_e.field_71456_v.func_146158_b().func_146234_a(new ChatComponentText("Issue streaming frame: " + var1 + " (" + ErrorCode.getString(var1) + ")"), 2);
   }

   public void func_152899_b() {
      this.func_152915_s();
      field_152950_b.info(field_152949_a, "Broadcast to Twitch has started");
   }

   public void func_152901_c() {
      field_152950_b.info(field_152949_a, "Broadcast to Twitch has stopped");
   }

   public void func_152892_c(ErrorCode var1) {
      ChatComponentTranslation var2;
      if (var1 == ErrorCode.TTV_EC_SOUNDFLOWER_NOT_INSTALLED) {
         var2 = new ChatComponentTranslation("stream.unavailable.soundflower.chat.link", new Object[0]);
         var2.func_150256_b().func_150241_a(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://help.mojang.com/customer/portal/articles/1374877-configuring-soundflower-for-streaming-on-apple-computers"));
         var2.func_150256_b().func_150228_d(true);
         ChatComponentTranslation var3 = new ChatComponentTranslation("stream.unavailable.soundflower.chat", new Object[]{var2});
         var3.func_150256_b().func_150238_a(EnumChatFormatting.DARK_RED);
         this.field_152953_e.field_71456_v.func_146158_b().func_146227_a(var3);
      } else {
         var2 = new ChatComponentTranslation("stream.unavailable.unknown.chat", new Object[]{ErrorCode.getString(var1)});
         var2.func_150256_b().func_150238_a(EnumChatFormatting.DARK_RED);
         this.field_152953_e.field_71456_v.func_146158_b().func_146227_a(var2);
      }

   }

   public void func_152907_a(IngestServerTester var1, IngestServerTester.IngestTestState var2) {
      field_152950_b.debug(field_152949_a, "Ingest test state changed to {}", new Object[]{var2});
      if (var2 == IngestServerTester.IngestTestState.Finished) {
         this.field_152960_l = true;
      }

   }

   public static int func_152948_a(float var0) {
      return MathHelper.func_76141_d(10.0F + var0 * 50.0F);
   }

   public static int func_152946_b(float var0) {
      return MathHelper.func_76141_d(230.0F + var0 * 3270.0F);
   }

   public static float func_152947_c(float var0) {
      return 0.1F + var0 * 0.1F;
   }

   public IngestServer[] func_152925_v() {
      return this.field_152951_c.func_152855_t().getServers();
   }

   public void func_152909_x() {
      IngestServerTester var1 = this.field_152951_c.func_152838_J();
      if (var1 != null) {
         var1.func_153042_a(this);
      }

   }

   public IngestServerTester func_152932_y() {
      return this.field_152951_c.func_152856_w();
   }

   public boolean func_152908_z() {
      return this.field_152951_c.func_152825_o();
   }

   public int func_152920_A() {
      return this.func_152934_n() ? this.field_152951_c.func_152816_j().viewers : 0;
   }

   public void func_176023_d(ErrorCode var1) {
      if (ErrorCode.failed(var1)) {
         field_152950_b.error(field_152949_a, "Chat failed to initialize");
      }

   }

   public void func_176022_e(ErrorCode var1) {
      if (ErrorCode.failed(var1)) {
         field_152950_b.error(field_152949_a, "Chat failed to shutdown");
      }

   }

   public void func_176017_a(ChatController.ChatState var1) {
   }

   public void func_180605_a(String var1, ChatRawMessage[] var2) {
      ChatRawMessage[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ChatRawMessage var6 = var3[var5];
         this.func_176027_a(var6.userName, var6);
         if (this.func_176028_a(var6.modes, var6.subscriptions, this.field_152953_e.field_71474_y.field_152409_S)) {
            ChatComponentText var7 = new ChatComponentText(var6.userName);
            ChatComponentTranslation var8 = new ChatComponentTranslation("chat.stream." + (var6.action ? "emote" : "text"), new Object[]{this.field_152954_f, var7, EnumChatFormatting.func_110646_a(var6.message)});
            if (var6.action) {
               var8.func_150256_b().func_150217_b(true);
            }

            ChatComponentText var9 = new ChatComponentText("");
            var9.func_150257_a(new ChatComponentTranslation("stream.userinfo.chatTooltip", new Object[0]));
            Iterator var10 = GuiTwitchUserMode.func_152328_a(var6.modes, var6.subscriptions, (IStream)null).iterator();

            while(var10.hasNext()) {
               IChatComponent var11 = (IChatComponent)var10.next();
               var9.func_150258_a("\n");
               var9.func_150257_a(var11);
            }

            var7.func_150256_b().func_150209_a(new HoverEvent(HoverEvent.Action.SHOW_TEXT, var9));
            var7.func_150256_b().func_150241_a(new ClickEvent(ClickEvent.Action.TWITCH_USER_INFO, var6.userName));
            this.field_152953_e.field_71456_v.func_146158_b().func_146227_a(var8);
         }
      }

   }

   public void func_176025_a(String var1, ChatTokenizedMessage[] var2) {
   }

   private void func_176027_a(String var1, ChatRawMessage var2) {
      ChatUserInfo var3 = (ChatUserInfo)this.field_152955_g.get(var1);
      if (var3 == null) {
         var3 = new ChatUserInfo();
         var3.displayName = var1;
         this.field_152955_g.put(var1, var3);
      }

      var3.subscriptions = var2.subscriptions;
      var3.modes = var2.modes;
      var3.nameColorARGB = var2.nameColorARGB;
   }

   private boolean func_176028_a(Set<ChatUserMode> var1, Set<ChatUserSubscription> var2, int var3) {
      if (var1.contains(ChatUserMode.TTV_CHAT_USERMODE_BANNED)) {
         return false;
      } else if (var1.contains(ChatUserMode.TTV_CHAT_USERMODE_ADMINSTRATOR)) {
         return true;
      } else if (var1.contains(ChatUserMode.TTV_CHAT_USERMODE_MODERATOR)) {
         return true;
      } else if (var1.contains(ChatUserMode.TTV_CHAT_USERMODE_STAFF)) {
         return true;
      } else if (var3 == 0) {
         return true;
      } else {
         return var3 == 1 ? var2.contains(ChatUserSubscription.TTV_CHAT_USERSUB_SUBSCRIBER) : false;
      }
   }

   public void func_176018_a(String var1, ChatUserInfo[] var2, ChatUserInfo[] var3, ChatUserInfo[] var4) {
      ChatUserInfo[] var5 = var3;
      int var6 = var3.length;

      int var7;
      ChatUserInfo var8;
      for(var7 = 0; var7 < var6; ++var7) {
         var8 = var5[var7];
         this.field_152955_g.remove(var8.displayName);
      }

      var5 = var4;
      var6 = var4.length;

      for(var7 = 0; var7 < var6; ++var7) {
         var8 = var5[var7];
         this.field_152955_g.put(var8.displayName, var8);
      }

      var5 = var2;
      var6 = var2.length;

      for(var7 = 0; var7 < var6; ++var7) {
         var8 = var5[var7];
         this.field_152955_g.put(var8.displayName, var8);
      }

   }

   public void func_180606_a(String var1) {
      field_152950_b.debug(field_152949_a, "Chat connected");
   }

   public void func_180607_b(String var1) {
      field_152950_b.debug(field_152949_a, "Chat disconnected");
      this.field_152955_g.clear();
   }

   public void func_176019_a(String var1, String var2) {
   }

   public void func_176021_d() {
   }

   public void func_176024_e() {
   }

   public void func_176016_c(String var1) {
   }

   public void func_176020_d(String var1) {
   }

   public boolean func_152927_B() {
      return this.field_176029_e != null && this.field_176029_e.equals(this.field_152951_c.func_152843_l().name);
   }

   public String func_152921_C() {
      return this.field_176029_e;
   }

   public ChatUserInfo func_152926_a(String var1) {
      return (ChatUserInfo)this.field_152955_g.get(var1);
   }

   public void func_152917_b(String var1) {
      this.field_152952_d.func_175986_a(this.field_176029_e, var1);
   }

   public boolean func_152928_D() {
      return field_152965_q && this.field_152951_c.func_152858_b();
   }

   public ErrorCode func_152912_E() {
      return !field_152965_q ? ErrorCode.TTV_EC_OS_TOO_OLD : this.field_152951_c.func_152852_P();
   }

   public boolean func_152913_F() {
      return this.field_152961_m;
   }

   public void func_152910_a(boolean var1) {
      this.field_152963_o = var1;
      this.func_152915_s();
   }

   public boolean func_152929_G() {
      boolean var1 = this.field_152953_e.field_71474_y.field_152410_T == 1;
      return this.field_152962_n || this.field_152953_e.field_71474_y.field_152401_K <= 0.0F || var1 != this.field_152963_o;
   }

   public IStream.AuthFailureReason func_152918_H() {
      return this.field_152964_p;
   }

   static {
      try {
         if (Util.func_110647_a() == Util.EnumOS.WINDOWS) {
            System.loadLibrary("avutil-ttv-51");
            System.loadLibrary("swresample-ttv-0");
            System.loadLibrary("libmp3lame-ttv");
            if (System.getProperty("os.arch").contains("64")) {
               System.loadLibrary("libmfxsw64");
            } else {
               System.loadLibrary("libmfxsw32");
            }
         }

         field_152965_q = true;
      } catch (Throwable var1) {
         field_152965_q = false;
      }

   }
}
