package net.minecraft.client.settings;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.stream.TwitchStream;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

public class GameSettings {
   private static final Logger field_151454_ax = LogManager.getLogger();
   private static final Gson field_151450_ay = new Gson();
   private static final ParameterizedType field_151449_az = new ParameterizedType() {
      public Type[] getActualTypeArguments() {
         return new Type[]{String.class};
      }

      public Type getRawType() {
         return List.class;
      }

      public Type getOwnerType() {
         return null;
      }
   };
   private static final String[] field_74367_ae = new String[]{"options.guiScale.auto", "options.guiScale.small", "options.guiScale.normal", "options.guiScale.large"};
   private static final String[] field_74364_ag = new String[]{"options.particles.all", "options.particles.decreased", "options.particles.minimal"};
   private static final String[] field_98303_au = new String[]{"options.ao.off", "options.ao.min", "options.ao.max"};
   private static final String[] field_152391_aS = new String[]{"options.stream.compression.low", "options.stream.compression.medium", "options.stream.compression.high"};
   private static final String[] field_152392_aT = new String[]{"options.stream.chat.enabled.streaming", "options.stream.chat.enabled.always", "options.stream.chat.enabled.never"};
   private static final String[] field_152393_aU = new String[]{"options.stream.chat.userFilter.all", "options.stream.chat.userFilter.subs", "options.stream.chat.userFilter.mods"};
   private static final String[] field_152394_aV = new String[]{"options.stream.mic_toggle.mute", "options.stream.mic_toggle.talk"};
   private static final String[] field_181149_aW = new String[]{"options.off", "options.graphics.fast", "options.graphics.fancy"};
   public float field_74341_c = 0.5F;
   public boolean field_74338_d;
   public int field_151451_c = -1;
   public boolean field_74336_f = true;
   public boolean field_74337_g;
   public boolean field_151448_g = true;
   public int field_74350_i = 120;
   public int field_74345_l = 2;
   public boolean field_74347_j = true;
   public int field_74348_k = 2;
   public List<String> field_151453_l = Lists.newArrayList();
   public List<String> field_183018_l = Lists.newArrayList();
   public EntityPlayer.EnumChatVisibility field_74343_n;
   public boolean field_74344_o;
   public boolean field_74359_p;
   public boolean field_74358_q;
   public float field_74357_r;
   public boolean field_74355_t;
   public boolean field_74353_u;
   public boolean field_74352_v;
   public boolean field_178881_t;
   public boolean field_178880_u;
   public boolean field_178879_v;
   public boolean field_80005_w;
   public boolean field_82882_x;
   public boolean field_82881_y;
   private final Set<EnumPlayerModelParts> field_178882_aU;
   public boolean field_85185_A;
   public int field_92118_B;
   public int field_92119_C;
   public boolean field_92117_D;
   public float field_96691_E;
   public float field_96692_F;
   public float field_96693_G;
   public float field_96694_H;
   public boolean field_151441_H;
   public int field_151442_I;
   private Map<SoundCategory, Float> field_151446_aD;
   public float field_152400_J;
   public float field_152401_K;
   public float field_152402_L;
   public float field_152403_M;
   public float field_152404_N;
   public int field_152405_O;
   public boolean field_152406_P;
   public String field_152407_Q;
   public int field_152408_R;
   public int field_152409_S;
   public int field_152410_T;
   public boolean field_181150_U;
   public boolean field_181151_V;
   public boolean field_183509_X;
   public KeyBinding field_74351_w;
   public KeyBinding field_74370_x;
   public KeyBinding field_74368_y;
   public KeyBinding field_74366_z;
   public KeyBinding field_74314_A;
   public KeyBinding field_74311_E;
   public KeyBinding field_151444_V;
   public KeyBinding field_151445_Q;
   public KeyBinding field_74313_G;
   public KeyBinding field_74316_C;
   public KeyBinding field_74312_F;
   public KeyBinding field_74322_I;
   public KeyBinding field_74310_D;
   public KeyBinding field_74321_H;
   public KeyBinding field_74323_J;
   public KeyBinding field_151447_Z;
   public KeyBinding field_151457_aa;
   public KeyBinding field_151458_ab;
   public KeyBinding field_152395_am;
   public KeyBinding field_178883_an;
   public KeyBinding field_152396_an;
   public KeyBinding field_152397_ao;
   public KeyBinding field_152398_ap;
   public KeyBinding field_152399_aq;
   public KeyBinding[] field_151456_ac;
   public KeyBinding[] field_74324_K;
   protected Minecraft field_74317_L;
   private File field_74354_ai;
   public EnumDifficulty field_74318_M;
   public boolean field_74319_N;
   public int field_74320_O;
   public boolean field_74330_P;
   public boolean field_74329_Q;
   public boolean field_181657_aC;
   public String field_74332_R;
   public boolean field_74326_T;
   public boolean field_74325_U;
   public float field_74334_X;
   public float field_74333_Y;
   public float field_151452_as;
   public int field_74335_Z;
   public int field_74362_aa;
   public String field_74363_ab;
   public boolean field_151455_aw;

   public GameSettings(Minecraft var1, File var2) {
      super();
      this.field_74343_n = EntityPlayer.EnumChatVisibility.FULL;
      this.field_74344_o = true;
      this.field_74359_p = true;
      this.field_74358_q = true;
      this.field_74357_r = 1.0F;
      this.field_74355_t = true;
      this.field_74352_v = true;
      this.field_178881_t = false;
      this.field_178880_u = true;
      this.field_178879_v = false;
      this.field_82881_y = true;
      this.field_178882_aU = Sets.newHashSet(EnumPlayerModelParts.values());
      this.field_92117_D = true;
      this.field_96691_E = 1.0F;
      this.field_96692_F = 1.0F;
      this.field_96693_G = 0.44366196F;
      this.field_96694_H = 1.0F;
      this.field_151441_H = true;
      this.field_151442_I = 4;
      this.field_151446_aD = Maps.newEnumMap(SoundCategory.class);
      this.field_152400_J = 0.5F;
      this.field_152401_K = 1.0F;
      this.field_152402_L = 1.0F;
      this.field_152403_M = 0.5412844F;
      this.field_152404_N = 0.31690142F;
      this.field_152405_O = 1;
      this.field_152406_P = true;
      this.field_152407_Q = "";
      this.field_152408_R = 0;
      this.field_152409_S = 0;
      this.field_152410_T = 0;
      this.field_181150_U = true;
      this.field_181151_V = true;
      this.field_183509_X = true;
      this.field_74351_w = new KeyBinding("key.forward", 17, "key.categories.movement");
      this.field_74370_x = new KeyBinding("key.left", 30, "key.categories.movement");
      this.field_74368_y = new KeyBinding("key.back", 31, "key.categories.movement");
      this.field_74366_z = new KeyBinding("key.right", 32, "key.categories.movement");
      this.field_74314_A = new KeyBinding("key.jump", 57, "key.categories.movement");
      this.field_74311_E = new KeyBinding("key.sneak", 42, "key.categories.movement");
      this.field_151444_V = new KeyBinding("key.sprint", 29, "key.categories.movement");
      this.field_151445_Q = new KeyBinding("key.inventory", 18, "key.categories.inventory");
      this.field_74313_G = new KeyBinding("key.use", -99, "key.categories.gameplay");
      this.field_74316_C = new KeyBinding("key.drop", 16, "key.categories.gameplay");
      this.field_74312_F = new KeyBinding("key.attack", -100, "key.categories.gameplay");
      this.field_74322_I = new KeyBinding("key.pickItem", -98, "key.categories.gameplay");
      this.field_74310_D = new KeyBinding("key.chat", 20, "key.categories.multiplayer");
      this.field_74321_H = new KeyBinding("key.playerlist", 15, "key.categories.multiplayer");
      this.field_74323_J = new KeyBinding("key.command", 53, "key.categories.multiplayer");
      this.field_151447_Z = new KeyBinding("key.screenshot", 60, "key.categories.misc");
      this.field_151457_aa = new KeyBinding("key.togglePerspective", 63, "key.categories.misc");
      this.field_151458_ab = new KeyBinding("key.smoothCamera", 0, "key.categories.misc");
      this.field_152395_am = new KeyBinding("key.fullscreen", 87, "key.categories.misc");
      this.field_178883_an = new KeyBinding("key.spectatorOutlines", 0, "key.categories.misc");
      this.field_152396_an = new KeyBinding("key.streamStartStop", 64, "key.categories.stream");
      this.field_152397_ao = new KeyBinding("key.streamPauseUnpause", 65, "key.categories.stream");
      this.field_152398_ap = new KeyBinding("key.streamCommercial", 0, "key.categories.stream");
      this.field_152399_aq = new KeyBinding("key.streamToggleMic", 0, "key.categories.stream");
      this.field_151456_ac = new KeyBinding[]{new KeyBinding("key.hotbar.1", 2, "key.categories.inventory"), new KeyBinding("key.hotbar.2", 3, "key.categories.inventory"), new KeyBinding("key.hotbar.3", 4, "key.categories.inventory"), new KeyBinding("key.hotbar.4", 5, "key.categories.inventory"), new KeyBinding("key.hotbar.5", 6, "key.categories.inventory"), new KeyBinding("key.hotbar.6", 7, "key.categories.inventory"), new KeyBinding("key.hotbar.7", 8, "key.categories.inventory"), new KeyBinding("key.hotbar.8", 9, "key.categories.inventory"), new KeyBinding("key.hotbar.9", 10, "key.categories.inventory")};
      this.field_74324_K = (KeyBinding[])ArrayUtils.addAll(new KeyBinding[]{this.field_74312_F, this.field_74313_G, this.field_74351_w, this.field_74370_x, this.field_74368_y, this.field_74366_z, this.field_74314_A, this.field_74311_E, this.field_151444_V, this.field_74316_C, this.field_151445_Q, this.field_74310_D, this.field_74321_H, this.field_74322_I, this.field_74323_J, this.field_151447_Z, this.field_151457_aa, this.field_151458_ab, this.field_152396_an, this.field_152397_ao, this.field_152398_ap, this.field_152399_aq, this.field_152395_am, this.field_178883_an}, this.field_151456_ac);
      this.field_74318_M = EnumDifficulty.NORMAL;
      this.field_74332_R = "";
      this.field_74334_X = 70.0F;
      this.field_74363_ab = "en_US";
      this.field_151455_aw = false;
      this.field_74317_L = var1;
      this.field_74354_ai = new File(var2, "options.txt");
      if (var1.func_147111_S() && Runtime.getRuntime().maxMemory() >= 1000000000L) {
         GameSettings.Options.RENDER_DISTANCE.func_148263_a(32.0F);
      } else {
         GameSettings.Options.RENDER_DISTANCE.func_148263_a(16.0F);
      }

      this.field_151451_c = var1.func_147111_S() ? 12 : 8;
      this.func_74300_a();
   }

   public GameSettings() {
      super();
      this.field_74343_n = EntityPlayer.EnumChatVisibility.FULL;
      this.field_74344_o = true;
      this.field_74359_p = true;
      this.field_74358_q = true;
      this.field_74357_r = 1.0F;
      this.field_74355_t = true;
      this.field_74352_v = true;
      this.field_178881_t = false;
      this.field_178880_u = true;
      this.field_178879_v = false;
      this.field_82881_y = true;
      this.field_178882_aU = Sets.newHashSet(EnumPlayerModelParts.values());
      this.field_92117_D = true;
      this.field_96691_E = 1.0F;
      this.field_96692_F = 1.0F;
      this.field_96693_G = 0.44366196F;
      this.field_96694_H = 1.0F;
      this.field_151441_H = true;
      this.field_151442_I = 4;
      this.field_151446_aD = Maps.newEnumMap(SoundCategory.class);
      this.field_152400_J = 0.5F;
      this.field_152401_K = 1.0F;
      this.field_152402_L = 1.0F;
      this.field_152403_M = 0.5412844F;
      this.field_152404_N = 0.31690142F;
      this.field_152405_O = 1;
      this.field_152406_P = true;
      this.field_152407_Q = "";
      this.field_152408_R = 0;
      this.field_152409_S = 0;
      this.field_152410_T = 0;
      this.field_181150_U = true;
      this.field_181151_V = true;
      this.field_183509_X = true;
      this.field_74351_w = new KeyBinding("key.forward", 17, "key.categories.movement");
      this.field_74370_x = new KeyBinding("key.left", 30, "key.categories.movement");
      this.field_74368_y = new KeyBinding("key.back", 31, "key.categories.movement");
      this.field_74366_z = new KeyBinding("key.right", 32, "key.categories.movement");
      this.field_74314_A = new KeyBinding("key.jump", 57, "key.categories.movement");
      this.field_74311_E = new KeyBinding("key.sneak", 42, "key.categories.movement");
      this.field_151444_V = new KeyBinding("key.sprint", 29, "key.categories.movement");
      this.field_151445_Q = new KeyBinding("key.inventory", 18, "key.categories.inventory");
      this.field_74313_G = new KeyBinding("key.use", -99, "key.categories.gameplay");
      this.field_74316_C = new KeyBinding("key.drop", 16, "key.categories.gameplay");
      this.field_74312_F = new KeyBinding("key.attack", -100, "key.categories.gameplay");
      this.field_74322_I = new KeyBinding("key.pickItem", -98, "key.categories.gameplay");
      this.field_74310_D = new KeyBinding("key.chat", 20, "key.categories.multiplayer");
      this.field_74321_H = new KeyBinding("key.playerlist", 15, "key.categories.multiplayer");
      this.field_74323_J = new KeyBinding("key.command", 53, "key.categories.multiplayer");
      this.field_151447_Z = new KeyBinding("key.screenshot", 60, "key.categories.misc");
      this.field_151457_aa = new KeyBinding("key.togglePerspective", 63, "key.categories.misc");
      this.field_151458_ab = new KeyBinding("key.smoothCamera", 0, "key.categories.misc");
      this.field_152395_am = new KeyBinding("key.fullscreen", 87, "key.categories.misc");
      this.field_178883_an = new KeyBinding("key.spectatorOutlines", 0, "key.categories.misc");
      this.field_152396_an = new KeyBinding("key.streamStartStop", 64, "key.categories.stream");
      this.field_152397_ao = new KeyBinding("key.streamPauseUnpause", 65, "key.categories.stream");
      this.field_152398_ap = new KeyBinding("key.streamCommercial", 0, "key.categories.stream");
      this.field_152399_aq = new KeyBinding("key.streamToggleMic", 0, "key.categories.stream");
      this.field_151456_ac = new KeyBinding[]{new KeyBinding("key.hotbar.1", 2, "key.categories.inventory"), new KeyBinding("key.hotbar.2", 3, "key.categories.inventory"), new KeyBinding("key.hotbar.3", 4, "key.categories.inventory"), new KeyBinding("key.hotbar.4", 5, "key.categories.inventory"), new KeyBinding("key.hotbar.5", 6, "key.categories.inventory"), new KeyBinding("key.hotbar.6", 7, "key.categories.inventory"), new KeyBinding("key.hotbar.7", 8, "key.categories.inventory"), new KeyBinding("key.hotbar.8", 9, "key.categories.inventory"), new KeyBinding("key.hotbar.9", 10, "key.categories.inventory")};
      this.field_74324_K = (KeyBinding[])ArrayUtils.addAll(new KeyBinding[]{this.field_74312_F, this.field_74313_G, this.field_74351_w, this.field_74370_x, this.field_74368_y, this.field_74366_z, this.field_74314_A, this.field_74311_E, this.field_151444_V, this.field_74316_C, this.field_151445_Q, this.field_74310_D, this.field_74321_H, this.field_74322_I, this.field_74323_J, this.field_151447_Z, this.field_151457_aa, this.field_151458_ab, this.field_152396_an, this.field_152397_ao, this.field_152398_ap, this.field_152399_aq, this.field_152395_am, this.field_178883_an}, this.field_151456_ac);
      this.field_74318_M = EnumDifficulty.NORMAL;
      this.field_74332_R = "";
      this.field_74334_X = 70.0F;
      this.field_74363_ab = "en_US";
      this.field_151455_aw = false;
   }

   public static String func_74298_c(int var0) {
      if (var0 < 0) {
         return I18n.func_135052_a("key.mouseButton", var0 + 101);
      } else {
         return var0 < 256 ? Keyboard.getKeyName(var0) : String.format("%c", (char)(var0 - 256)).toUpperCase();
      }
   }

   public static boolean func_100015_a(KeyBinding var0) {
      if (var0.func_151463_i() == 0) {
         return false;
      } else {
         return var0.func_151463_i() < 0 ? Mouse.isButtonDown(var0.func_151463_i() + 100) : Keyboard.isKeyDown(var0.func_151463_i());
      }
   }

   public void func_151440_a(KeyBinding var1, int var2) {
      var1.func_151462_b(var2);
      this.func_74303_b();
   }

   public void func_74304_a(GameSettings.Options var1, float var2) {
      if (var1 == GameSettings.Options.SENSITIVITY) {
         this.field_74341_c = var2;
      }

      if (var1 == GameSettings.Options.FOV) {
         this.field_74334_X = var2;
      }

      if (var1 == GameSettings.Options.GAMMA) {
         this.field_74333_Y = var2;
      }

      if (var1 == GameSettings.Options.FRAMERATE_LIMIT) {
         this.field_74350_i = (int)var2;
      }

      if (var1 == GameSettings.Options.CHAT_OPACITY) {
         this.field_74357_r = var2;
         this.field_74317_L.field_71456_v.func_146158_b().func_146245_b();
      }

      if (var1 == GameSettings.Options.CHAT_HEIGHT_FOCUSED) {
         this.field_96694_H = var2;
         this.field_74317_L.field_71456_v.func_146158_b().func_146245_b();
      }

      if (var1 == GameSettings.Options.CHAT_HEIGHT_UNFOCUSED) {
         this.field_96693_G = var2;
         this.field_74317_L.field_71456_v.func_146158_b().func_146245_b();
      }

      if (var1 == GameSettings.Options.CHAT_WIDTH) {
         this.field_96692_F = var2;
         this.field_74317_L.field_71456_v.func_146158_b().func_146245_b();
      }

      if (var1 == GameSettings.Options.CHAT_SCALE) {
         this.field_96691_E = var2;
         this.field_74317_L.field_71456_v.func_146158_b().func_146245_b();
      }

      if (var1 == GameSettings.Options.MIPMAP_LEVELS) {
         int var3 = this.field_151442_I;
         this.field_151442_I = (int)var2;
         if ((float)var3 != var2) {
            this.field_74317_L.func_147117_R().func_147633_a(this.field_151442_I);
            this.field_74317_L.func_110434_K().func_110577_a(TextureMap.field_110575_b);
            this.field_74317_L.func_147117_R().func_174937_a(false, this.field_151442_I > 0);
            this.field_74317_L.func_175603_A();
         }
      }

      if (var1 == GameSettings.Options.BLOCK_ALTERNATIVES) {
         this.field_178880_u = !this.field_178880_u;
         this.field_74317_L.field_71438_f.func_72712_a();
      }

      if (var1 == GameSettings.Options.RENDER_DISTANCE) {
         this.field_151451_c = (int)var2;
         this.field_74317_L.field_71438_f.func_174979_m();
      }

      if (var1 == GameSettings.Options.STREAM_BYTES_PER_PIXEL) {
         this.field_152400_J = var2;
      }

      if (var1 == GameSettings.Options.STREAM_VOLUME_MIC) {
         this.field_152401_K = var2;
         this.field_74317_L.func_152346_Z().func_152915_s();
      }

      if (var1 == GameSettings.Options.STREAM_VOLUME_SYSTEM) {
         this.field_152402_L = var2;
         this.field_74317_L.func_152346_Z().func_152915_s();
      }

      if (var1 == GameSettings.Options.STREAM_KBPS) {
         this.field_152403_M = var2;
      }

      if (var1 == GameSettings.Options.STREAM_FPS) {
         this.field_152404_N = var2;
      }

   }

   public void func_74306_a(GameSettings.Options var1, int var2) {
      if (var1 == GameSettings.Options.INVERT_MOUSE) {
         this.field_74338_d = !this.field_74338_d;
      }

      if (var1 == GameSettings.Options.GUI_SCALE) {
         this.field_74335_Z = this.field_74335_Z + var2 & 3;
      }

      if (var1 == GameSettings.Options.PARTICLES) {
         this.field_74362_aa = (this.field_74362_aa + var2) % 3;
      }

      if (var1 == GameSettings.Options.VIEW_BOBBING) {
         this.field_74336_f = !this.field_74336_f;
      }

      if (var1 == GameSettings.Options.RENDER_CLOUDS) {
         this.field_74345_l = (this.field_74345_l + var2) % 3;
      }

      if (var1 == GameSettings.Options.FORCE_UNICODE_FONT) {
         this.field_151455_aw = !this.field_151455_aw;
         this.field_74317_L.field_71466_p.func_78264_a(this.field_74317_L.func_135016_M().func_135042_a() || this.field_151455_aw);
      }

      if (var1 == GameSettings.Options.FBO_ENABLE) {
         this.field_151448_g = !this.field_151448_g;
      }

      if (var1 == GameSettings.Options.ANAGLYPH) {
         this.field_74337_g = !this.field_74337_g;
         this.field_74317_L.func_110436_a();
      }

      if (var1 == GameSettings.Options.GRAPHICS) {
         this.field_74347_j = !this.field_74347_j;
         this.field_74317_L.field_71438_f.func_72712_a();
      }

      if (var1 == GameSettings.Options.AMBIENT_OCCLUSION) {
         this.field_74348_k = (this.field_74348_k + var2) % 3;
         this.field_74317_L.field_71438_f.func_72712_a();
      }

      if (var1 == GameSettings.Options.CHAT_VISIBILITY) {
         this.field_74343_n = EntityPlayer.EnumChatVisibility.func_151426_a((this.field_74343_n.func_151428_a() + var2) % 3);
      }

      if (var1 == GameSettings.Options.STREAM_COMPRESSION) {
         this.field_152405_O = (this.field_152405_O + var2) % 3;
      }

      if (var1 == GameSettings.Options.STREAM_SEND_METADATA) {
         this.field_152406_P = !this.field_152406_P;
      }

      if (var1 == GameSettings.Options.STREAM_CHAT_ENABLED) {
         this.field_152408_R = (this.field_152408_R + var2) % 3;
      }

      if (var1 == GameSettings.Options.STREAM_CHAT_USER_FILTER) {
         this.field_152409_S = (this.field_152409_S + var2) % 3;
      }

      if (var1 == GameSettings.Options.STREAM_MIC_TOGGLE_BEHAVIOR) {
         this.field_152410_T = (this.field_152410_T + var2) % 2;
      }

      if (var1 == GameSettings.Options.CHAT_COLOR) {
         this.field_74344_o = !this.field_74344_o;
      }

      if (var1 == GameSettings.Options.CHAT_LINKS) {
         this.field_74359_p = !this.field_74359_p;
      }

      if (var1 == GameSettings.Options.CHAT_LINKS_PROMPT) {
         this.field_74358_q = !this.field_74358_q;
      }

      if (var1 == GameSettings.Options.SNOOPER_ENABLED) {
         this.field_74355_t = !this.field_74355_t;
      }

      if (var1 == GameSettings.Options.TOUCHSCREEN) {
         this.field_85185_A = !this.field_85185_A;
      }

      if (var1 == GameSettings.Options.USE_FULLSCREEN) {
         this.field_74353_u = !this.field_74353_u;
         if (this.field_74317_L.func_71372_G() != this.field_74353_u) {
            this.field_74317_L.func_71352_k();
         }
      }

      if (var1 == GameSettings.Options.ENABLE_VSYNC) {
         this.field_74352_v = !this.field_74352_v;
         Display.setVSyncEnabled(this.field_74352_v);
      }

      if (var1 == GameSettings.Options.USE_VBO) {
         this.field_178881_t = !this.field_178881_t;
         this.field_74317_L.field_71438_f.func_72712_a();
      }

      if (var1 == GameSettings.Options.BLOCK_ALTERNATIVES) {
         this.field_178880_u = !this.field_178880_u;
         this.field_74317_L.field_71438_f.func_72712_a();
      }

      if (var1 == GameSettings.Options.REDUCED_DEBUG_INFO) {
         this.field_178879_v = !this.field_178879_v;
      }

      if (var1 == GameSettings.Options.ENTITY_SHADOWS) {
         this.field_181151_V = !this.field_181151_V;
      }

      if (var1 == GameSettings.Options.REALMS_NOTIFICATIONS) {
         this.field_183509_X = !this.field_183509_X;
      }

      this.func_74303_b();
   }

   public float func_74296_a(GameSettings.Options var1) {
      if (var1 == GameSettings.Options.FOV) {
         return this.field_74334_X;
      } else if (var1 == GameSettings.Options.GAMMA) {
         return this.field_74333_Y;
      } else if (var1 == GameSettings.Options.SATURATION) {
         return this.field_151452_as;
      } else if (var1 == GameSettings.Options.SENSITIVITY) {
         return this.field_74341_c;
      } else if (var1 == GameSettings.Options.CHAT_OPACITY) {
         return this.field_74357_r;
      } else if (var1 == GameSettings.Options.CHAT_HEIGHT_FOCUSED) {
         return this.field_96694_H;
      } else if (var1 == GameSettings.Options.CHAT_HEIGHT_UNFOCUSED) {
         return this.field_96693_G;
      } else if (var1 == GameSettings.Options.CHAT_SCALE) {
         return this.field_96691_E;
      } else if (var1 == GameSettings.Options.CHAT_WIDTH) {
         return this.field_96692_F;
      } else if (var1 == GameSettings.Options.FRAMERATE_LIMIT) {
         return (float)this.field_74350_i;
      } else if (var1 == GameSettings.Options.MIPMAP_LEVELS) {
         return (float)this.field_151442_I;
      } else if (var1 == GameSettings.Options.RENDER_DISTANCE) {
         return (float)this.field_151451_c;
      } else if (var1 == GameSettings.Options.STREAM_BYTES_PER_PIXEL) {
         return this.field_152400_J;
      } else if (var1 == GameSettings.Options.STREAM_VOLUME_MIC) {
         return this.field_152401_K;
      } else if (var1 == GameSettings.Options.STREAM_VOLUME_SYSTEM) {
         return this.field_152402_L;
      } else if (var1 == GameSettings.Options.STREAM_KBPS) {
         return this.field_152403_M;
      } else {
         return var1 == GameSettings.Options.STREAM_FPS ? this.field_152404_N : 0.0F;
      }
   }

   public boolean func_74308_b(GameSettings.Options var1) {
      switch(var1) {
      case INVERT_MOUSE:
         return this.field_74338_d;
      case VIEW_BOBBING:
         return this.field_74336_f;
      case ANAGLYPH:
         return this.field_74337_g;
      case FBO_ENABLE:
         return this.field_151448_g;
      case CHAT_COLOR:
         return this.field_74344_o;
      case CHAT_LINKS:
         return this.field_74359_p;
      case CHAT_LINKS_PROMPT:
         return this.field_74358_q;
      case SNOOPER_ENABLED:
         return this.field_74355_t;
      case USE_FULLSCREEN:
         return this.field_74353_u;
      case ENABLE_VSYNC:
         return this.field_74352_v;
      case USE_VBO:
         return this.field_178881_t;
      case TOUCHSCREEN:
         return this.field_85185_A;
      case STREAM_SEND_METADATA:
         return this.field_152406_P;
      case FORCE_UNICODE_FONT:
         return this.field_151455_aw;
      case BLOCK_ALTERNATIVES:
         return this.field_178880_u;
      case REDUCED_DEBUG_INFO:
         return this.field_178879_v;
      case ENTITY_SHADOWS:
         return this.field_181151_V;
      case REALMS_NOTIFICATIONS:
         return this.field_183509_X;
      default:
         return false;
      }
   }

   private static String func_74299_a(String[] var0, int var1) {
      if (var1 < 0 || var1 >= var0.length) {
         var1 = 0;
      }

      return I18n.func_135052_a(var0[var1]);
   }

   public String func_74297_c(GameSettings.Options var1) {
      String var2 = I18n.func_135052_a(var1.func_74378_d()) + ": ";
      if (var1.func_74380_a()) {
         float var6 = this.func_74296_a(var1);
         float var4 = var1.func_148266_c(var6);
         if (var1 == GameSettings.Options.SENSITIVITY) {
            if (var4 == 0.0F) {
               return var2 + I18n.func_135052_a("options.sensitivity.min");
            } else {
               return var4 == 1.0F ? var2 + I18n.func_135052_a("options.sensitivity.max") : var2 + (int)(var4 * 200.0F) + "%";
            }
         } else if (var1 == GameSettings.Options.FOV) {
            if (var6 == 70.0F) {
               return var2 + I18n.func_135052_a("options.fov.min");
            } else {
               return var6 == 110.0F ? var2 + I18n.func_135052_a("options.fov.max") : var2 + (int)var6;
            }
         } else if (var1 == GameSettings.Options.FRAMERATE_LIMIT) {
            return var6 == var1.field_148272_O ? var2 + I18n.func_135052_a("options.framerateLimit.max") : var2 + (int)var6 + " fps";
         } else if (var1 == GameSettings.Options.RENDER_CLOUDS) {
            return var6 == var1.field_148271_N ? var2 + I18n.func_135052_a("options.cloudHeight.min") : var2 + ((int)var6 + 128);
         } else if (var1 == GameSettings.Options.GAMMA) {
            if (var4 == 0.0F) {
               return var2 + I18n.func_135052_a("options.gamma.min");
            } else {
               return var4 == 1.0F ? var2 + I18n.func_135052_a("options.gamma.max") : var2 + "+" + (int)(var4 * 100.0F) + "%";
            }
         } else if (var1 == GameSettings.Options.SATURATION) {
            return var2 + (int)(var4 * 400.0F) + "%";
         } else if (var1 == GameSettings.Options.CHAT_OPACITY) {
            return var2 + (int)(var4 * 90.0F + 10.0F) + "%";
         } else if (var1 == GameSettings.Options.CHAT_HEIGHT_UNFOCUSED) {
            return var2 + GuiNewChat.func_146243_b(var4) + "px";
         } else if (var1 == GameSettings.Options.CHAT_HEIGHT_FOCUSED) {
            return var2 + GuiNewChat.func_146243_b(var4) + "px";
         } else if (var1 == GameSettings.Options.CHAT_WIDTH) {
            return var2 + GuiNewChat.func_146233_a(var4) + "px";
         } else if (var1 == GameSettings.Options.RENDER_DISTANCE) {
            return var2 + (int)var6 + " chunks";
         } else if (var1 == GameSettings.Options.MIPMAP_LEVELS) {
            return var6 == 0.0F ? var2 + I18n.func_135052_a("options.off") : var2 + (int)var6;
         } else if (var1 == GameSettings.Options.STREAM_FPS) {
            return var2 + TwitchStream.func_152948_a(var4) + " fps";
         } else if (var1 == GameSettings.Options.STREAM_KBPS) {
            return var2 + TwitchStream.func_152946_b(var4) + " Kbps";
         } else if (var1 == GameSettings.Options.STREAM_BYTES_PER_PIXEL) {
            return var2 + String.format("%.3f bpp", TwitchStream.func_152947_c(var4));
         } else {
            return var4 == 0.0F ? var2 + I18n.func_135052_a("options.off") : var2 + (int)(var4 * 100.0F) + "%";
         }
      } else if (var1.func_74382_b()) {
         boolean var5 = this.func_74308_b(var1);
         return var5 ? var2 + I18n.func_135052_a("options.on") : var2 + I18n.func_135052_a("options.off");
      } else if (var1 == GameSettings.Options.GUI_SCALE) {
         return var2 + func_74299_a(field_74367_ae, this.field_74335_Z);
      } else if (var1 == GameSettings.Options.CHAT_VISIBILITY) {
         return var2 + I18n.func_135052_a(this.field_74343_n.func_151429_b());
      } else if (var1 == GameSettings.Options.PARTICLES) {
         return var2 + func_74299_a(field_74364_ag, this.field_74362_aa);
      } else if (var1 == GameSettings.Options.AMBIENT_OCCLUSION) {
         return var2 + func_74299_a(field_98303_au, this.field_74348_k);
      } else if (var1 == GameSettings.Options.STREAM_COMPRESSION) {
         return var2 + func_74299_a(field_152391_aS, this.field_152405_O);
      } else if (var1 == GameSettings.Options.STREAM_CHAT_ENABLED) {
         return var2 + func_74299_a(field_152392_aT, this.field_152408_R);
      } else if (var1 == GameSettings.Options.STREAM_CHAT_USER_FILTER) {
         return var2 + func_74299_a(field_152393_aU, this.field_152409_S);
      } else if (var1 == GameSettings.Options.STREAM_MIC_TOGGLE_BEHAVIOR) {
         return var2 + func_74299_a(field_152394_aV, this.field_152410_T);
      } else if (var1 == GameSettings.Options.RENDER_CLOUDS) {
         return var2 + func_74299_a(field_181149_aW, this.field_74345_l);
      } else if (var1 == GameSettings.Options.GRAPHICS) {
         if (this.field_74347_j) {
            return var2 + I18n.func_135052_a("options.graphics.fancy");
         } else {
            String var3 = "options.graphics.fast";
            return var2 + I18n.func_135052_a("options.graphics.fast");
         }
      } else {
         return var2;
      }
   }

   public void func_74300_a() {
      try {
         if (!this.field_74354_ai.exists()) {
            return;
         }

         BufferedReader var1 = new BufferedReader(new FileReader(this.field_74354_ai));
         String var2 = "";
         this.field_151446_aD.clear();

         while((var2 = var1.readLine()) != null) {
            try {
               String[] var3 = var2.split(":");
               if (var3[0].equals("mouseSensitivity")) {
                  this.field_74341_c = this.func_74305_a(var3[1]);
               }

               if (var3[0].equals("fov")) {
                  this.field_74334_X = this.func_74305_a(var3[1]) * 40.0F + 70.0F;
               }

               if (var3[0].equals("gamma")) {
                  this.field_74333_Y = this.func_74305_a(var3[1]);
               }

               if (var3[0].equals("saturation")) {
                  this.field_151452_as = this.func_74305_a(var3[1]);
               }

               if (var3[0].equals("invertYMouse")) {
                  this.field_74338_d = var3[1].equals("true");
               }

               if (var3[0].equals("renderDistance")) {
                  this.field_151451_c = Integer.parseInt(var3[1]);
               }

               if (var3[0].equals("guiScale")) {
                  this.field_74335_Z = Integer.parseInt(var3[1]);
               }

               if (var3[0].equals("particles")) {
                  this.field_74362_aa = Integer.parseInt(var3[1]);
               }

               if (var3[0].equals("bobView")) {
                  this.field_74336_f = var3[1].equals("true");
               }

               if (var3[0].equals("anaglyph3d")) {
                  this.field_74337_g = var3[1].equals("true");
               }

               if (var3[0].equals("maxFps")) {
                  this.field_74350_i = Integer.parseInt(var3[1]);
               }

               if (var3[0].equals("fboEnable")) {
                  this.field_151448_g = var3[1].equals("true");
               }

               if (var3[0].equals("difficulty")) {
                  this.field_74318_M = EnumDifficulty.func_151523_a(Integer.parseInt(var3[1]));
               }

               if (var3[0].equals("fancyGraphics")) {
                  this.field_74347_j = var3[1].equals("true");
               }

               if (var3[0].equals("ao")) {
                  if (var3[1].equals("true")) {
                     this.field_74348_k = 2;
                  } else if (var3[1].equals("false")) {
                     this.field_74348_k = 0;
                  } else {
                     this.field_74348_k = Integer.parseInt(var3[1]);
                  }
               }

               if (var3[0].equals("renderClouds")) {
                  if (var3[1].equals("true")) {
                     this.field_74345_l = 2;
                  } else if (var3[1].equals("false")) {
                     this.field_74345_l = 0;
                  } else if (var3[1].equals("fast")) {
                     this.field_74345_l = 1;
                  }
               }

               if (var3[0].equals("resourcePacks")) {
                  this.field_151453_l = (List)field_151450_ay.fromJson(var2.substring(var2.indexOf(58) + 1), field_151449_az);
                  if (this.field_151453_l == null) {
                     this.field_151453_l = Lists.newArrayList();
                  }
               }

               if (var3[0].equals("incompatibleResourcePacks")) {
                  this.field_183018_l = (List)field_151450_ay.fromJson(var2.substring(var2.indexOf(58) + 1), field_151449_az);
                  if (this.field_183018_l == null) {
                     this.field_183018_l = Lists.newArrayList();
                  }
               }

               if (var3[0].equals("lastServer") && var3.length >= 2) {
                  this.field_74332_R = var2.substring(var2.indexOf(58) + 1);
               }

               if (var3[0].equals("lang") && var3.length >= 2) {
                  this.field_74363_ab = var3[1];
               }

               if (var3[0].equals("chatVisibility")) {
                  this.field_74343_n = EntityPlayer.EnumChatVisibility.func_151426_a(Integer.parseInt(var3[1]));
               }

               if (var3[0].equals("chatColors")) {
                  this.field_74344_o = var3[1].equals("true");
               }

               if (var3[0].equals("chatLinks")) {
                  this.field_74359_p = var3[1].equals("true");
               }

               if (var3[0].equals("chatLinksPrompt")) {
                  this.field_74358_q = var3[1].equals("true");
               }

               if (var3[0].equals("chatOpacity")) {
                  this.field_74357_r = this.func_74305_a(var3[1]);
               }

               if (var3[0].equals("snooperEnabled")) {
                  this.field_74355_t = var3[1].equals("true");
               }

               if (var3[0].equals("fullscreen")) {
                  this.field_74353_u = var3[1].equals("true");
               }

               if (var3[0].equals("enableVsync")) {
                  this.field_74352_v = var3[1].equals("true");
               }

               if (var3[0].equals("useVbo")) {
                  this.field_178881_t = var3[1].equals("true");
               }

               if (var3[0].equals("hideServerAddress")) {
                  this.field_80005_w = var3[1].equals("true");
               }

               if (var3[0].equals("advancedItemTooltips")) {
                  this.field_82882_x = var3[1].equals("true");
               }

               if (var3[0].equals("pauseOnLostFocus")) {
                  this.field_82881_y = var3[1].equals("true");
               }

               if (var3[0].equals("touchscreen")) {
                  this.field_85185_A = var3[1].equals("true");
               }

               if (var3[0].equals("overrideHeight")) {
                  this.field_92119_C = Integer.parseInt(var3[1]);
               }

               if (var3[0].equals("overrideWidth")) {
                  this.field_92118_B = Integer.parseInt(var3[1]);
               }

               if (var3[0].equals("heldItemTooltips")) {
                  this.field_92117_D = var3[1].equals("true");
               }

               if (var3[0].equals("chatHeightFocused")) {
                  this.field_96694_H = this.func_74305_a(var3[1]);
               }

               if (var3[0].equals("chatHeightUnfocused")) {
                  this.field_96693_G = this.func_74305_a(var3[1]);
               }

               if (var3[0].equals("chatScale")) {
                  this.field_96691_E = this.func_74305_a(var3[1]);
               }

               if (var3[0].equals("chatWidth")) {
                  this.field_96692_F = this.func_74305_a(var3[1]);
               }

               if (var3[0].equals("showInventoryAchievementHint")) {
                  this.field_151441_H = var3[1].equals("true");
               }

               if (var3[0].equals("mipmapLevels")) {
                  this.field_151442_I = Integer.parseInt(var3[1]);
               }

               if (var3[0].equals("streamBytesPerPixel")) {
                  this.field_152400_J = this.func_74305_a(var3[1]);
               }

               if (var3[0].equals("streamMicVolume")) {
                  this.field_152401_K = this.func_74305_a(var3[1]);
               }

               if (var3[0].equals("streamSystemVolume")) {
                  this.field_152402_L = this.func_74305_a(var3[1]);
               }

               if (var3[0].equals("streamKbps")) {
                  this.field_152403_M = this.func_74305_a(var3[1]);
               }

               if (var3[0].equals("streamFps")) {
                  this.field_152404_N = this.func_74305_a(var3[1]);
               }

               if (var3[0].equals("streamCompression")) {
                  this.field_152405_O = Integer.parseInt(var3[1]);
               }

               if (var3[0].equals("streamSendMetadata")) {
                  this.field_152406_P = var3[1].equals("true");
               }

               if (var3[0].equals("streamPreferredServer") && var3.length >= 2) {
                  this.field_152407_Q = var2.substring(var2.indexOf(58) + 1);
               }

               if (var3[0].equals("streamChatEnabled")) {
                  this.field_152408_R = Integer.parseInt(var3[1]);
               }

               if (var3[0].equals("streamChatUserFilter")) {
                  this.field_152409_S = Integer.parseInt(var3[1]);
               }

               if (var3[0].equals("streamMicToggleBehavior")) {
                  this.field_152410_T = Integer.parseInt(var3[1]);
               }

               if (var3[0].equals("forceUnicodeFont")) {
                  this.field_151455_aw = var3[1].equals("true");
               }

               if (var3[0].equals("allowBlockAlternatives")) {
                  this.field_178880_u = var3[1].equals("true");
               }

               if (var3[0].equals("reducedDebugInfo")) {
                  this.field_178879_v = var3[1].equals("true");
               }

               if (var3[0].equals("useNativeTransport")) {
                  this.field_181150_U = var3[1].equals("true");
               }

               if (var3[0].equals("entityShadows")) {
                  this.field_181151_V = var3[1].equals("true");
               }

               if (var3[0].equals("realmsNotifications")) {
                  this.field_183509_X = var3[1].equals("true");
               }

               KeyBinding[] var4 = this.field_74324_K;
               int var5 = var4.length;

               int var6;
               for(var6 = 0; var6 < var5; ++var6) {
                  KeyBinding var7 = var4[var6];
                  if (var3[0].equals("key_" + var7.func_151464_g())) {
                     var7.func_151462_b(Integer.parseInt(var3[1]));
                  }
               }

               SoundCategory[] var10 = SoundCategory.values();
               var5 = var10.length;

               for(var6 = 0; var6 < var5; ++var6) {
                  SoundCategory var12 = var10[var6];
                  if (var3[0].equals("soundCategory_" + var12.func_147155_a())) {
                     this.field_151446_aD.put(var12, this.func_74305_a(var3[1]));
                  }
               }

               EnumPlayerModelParts[] var11 = EnumPlayerModelParts.values();
               var5 = var11.length;

               for(var6 = 0; var6 < var5; ++var6) {
                  EnumPlayerModelParts var13 = var11[var6];
                  if (var3[0].equals("modelPart_" + var13.func_179329_c())) {
                     this.func_178878_a(var13, var3[1].equals("true"));
                  }
               }
            } catch (Exception var8) {
               field_151454_ax.warn("Skipping bad option: " + var2);
            }
         }

         KeyBinding.func_74508_b();
         var1.close();
      } catch (Exception var9) {
         field_151454_ax.error("Failed to load options", var9);
      }

   }

   private float func_74305_a(String var1) {
      if (var1.equals("true")) {
         return 1.0F;
      } else {
         return var1.equals("false") ? 0.0F : Float.parseFloat(var1);
      }
   }

   public void func_74303_b() {
      try {
         PrintWriter var1 = new PrintWriter(new FileWriter(this.field_74354_ai));
         var1.println("invertYMouse:" + this.field_74338_d);
         var1.println("mouseSensitivity:" + this.field_74341_c);
         var1.println("fov:" + (this.field_74334_X - 70.0F) / 40.0F);
         var1.println("gamma:" + this.field_74333_Y);
         var1.println("saturation:" + this.field_151452_as);
         var1.println("renderDistance:" + this.field_151451_c);
         var1.println("guiScale:" + this.field_74335_Z);
         var1.println("particles:" + this.field_74362_aa);
         var1.println("bobView:" + this.field_74336_f);
         var1.println("anaglyph3d:" + this.field_74337_g);
         var1.println("maxFps:" + this.field_74350_i);
         var1.println("fboEnable:" + this.field_151448_g);
         var1.println("difficulty:" + this.field_74318_M.func_151525_a());
         var1.println("fancyGraphics:" + this.field_74347_j);
         var1.println("ao:" + this.field_74348_k);
         switch(this.field_74345_l) {
         case 0:
            var1.println("renderClouds:false");
            break;
         case 1:
            var1.println("renderClouds:fast");
            break;
         case 2:
            var1.println("renderClouds:true");
         }

         var1.println("resourcePacks:" + field_151450_ay.toJson(this.field_151453_l));
         var1.println("incompatibleResourcePacks:" + field_151450_ay.toJson(this.field_183018_l));
         var1.println("lastServer:" + this.field_74332_R);
         var1.println("lang:" + this.field_74363_ab);
         var1.println("chatVisibility:" + this.field_74343_n.func_151428_a());
         var1.println("chatColors:" + this.field_74344_o);
         var1.println("chatLinks:" + this.field_74359_p);
         var1.println("chatLinksPrompt:" + this.field_74358_q);
         var1.println("chatOpacity:" + this.field_74357_r);
         var1.println("snooperEnabled:" + this.field_74355_t);
         var1.println("fullscreen:" + this.field_74353_u);
         var1.println("enableVsync:" + this.field_74352_v);
         var1.println("useVbo:" + this.field_178881_t);
         var1.println("hideServerAddress:" + this.field_80005_w);
         var1.println("advancedItemTooltips:" + this.field_82882_x);
         var1.println("pauseOnLostFocus:" + this.field_82881_y);
         var1.println("touchscreen:" + this.field_85185_A);
         var1.println("overrideWidth:" + this.field_92118_B);
         var1.println("overrideHeight:" + this.field_92119_C);
         var1.println("heldItemTooltips:" + this.field_92117_D);
         var1.println("chatHeightFocused:" + this.field_96694_H);
         var1.println("chatHeightUnfocused:" + this.field_96693_G);
         var1.println("chatScale:" + this.field_96691_E);
         var1.println("chatWidth:" + this.field_96692_F);
         var1.println("showInventoryAchievementHint:" + this.field_151441_H);
         var1.println("mipmapLevels:" + this.field_151442_I);
         var1.println("streamBytesPerPixel:" + this.field_152400_J);
         var1.println("streamMicVolume:" + this.field_152401_K);
         var1.println("streamSystemVolume:" + this.field_152402_L);
         var1.println("streamKbps:" + this.field_152403_M);
         var1.println("streamFps:" + this.field_152404_N);
         var1.println("streamCompression:" + this.field_152405_O);
         var1.println("streamSendMetadata:" + this.field_152406_P);
         var1.println("streamPreferredServer:" + this.field_152407_Q);
         var1.println("streamChatEnabled:" + this.field_152408_R);
         var1.println("streamChatUserFilter:" + this.field_152409_S);
         var1.println("streamMicToggleBehavior:" + this.field_152410_T);
         var1.println("forceUnicodeFont:" + this.field_151455_aw);
         var1.println("allowBlockAlternatives:" + this.field_178880_u);
         var1.println("reducedDebugInfo:" + this.field_178879_v);
         var1.println("useNativeTransport:" + this.field_181150_U);
         var1.println("entityShadows:" + this.field_181151_V);
         var1.println("realmsNotifications:" + this.field_183509_X);
         KeyBinding[] var2 = this.field_74324_K;
         int var3 = var2.length;

         int var4;
         for(var4 = 0; var4 < var3; ++var4) {
            KeyBinding var5 = var2[var4];
            var1.println("key_" + var5.func_151464_g() + ":" + var5.func_151463_i());
         }

         SoundCategory[] var7 = SoundCategory.values();
         var3 = var7.length;

         for(var4 = 0; var4 < var3; ++var4) {
            SoundCategory var9 = var7[var4];
            var1.println("soundCategory_" + var9.func_147155_a() + ":" + this.func_151438_a(var9));
         }

         EnumPlayerModelParts[] var8 = EnumPlayerModelParts.values();
         var3 = var8.length;

         for(var4 = 0; var4 < var3; ++var4) {
            EnumPlayerModelParts var10 = var8[var4];
            var1.println("modelPart_" + var10.func_179329_c() + ":" + this.field_178882_aU.contains(var10));
         }

         var1.close();
      } catch (Exception var6) {
         field_151454_ax.error("Failed to save options", var6);
      }

      this.func_82879_c();
   }

   public float func_151438_a(SoundCategory var1) {
      return this.field_151446_aD.containsKey(var1) ? (Float)this.field_151446_aD.get(var1) : 1.0F;
   }

   public void func_151439_a(SoundCategory var1, float var2) {
      this.field_74317_L.func_147118_V().func_147684_a(var1, var2);
      this.field_151446_aD.put(var1, var2);
   }

   public void func_82879_c() {
      if (this.field_74317_L.field_71439_g != null) {
         int var1 = 0;

         EnumPlayerModelParts var3;
         for(Iterator var2 = this.field_178882_aU.iterator(); var2.hasNext(); var1 |= var3.func_179327_a()) {
            var3 = (EnumPlayerModelParts)var2.next();
         }

         this.field_74317_L.field_71439_g.field_71174_a.func_147297_a(new C15PacketClientSettings(this.field_74363_ab, this.field_151451_c, this.field_74343_n, this.field_74344_o, var1));
      }

   }

   public Set<EnumPlayerModelParts> func_178876_d() {
      return ImmutableSet.copyOf(this.field_178882_aU);
   }

   public void func_178878_a(EnumPlayerModelParts var1, boolean var2) {
      if (var2) {
         this.field_178882_aU.add(var1);
      } else {
         this.field_178882_aU.remove(var1);
      }

      this.func_82879_c();
   }

   public void func_178877_a(EnumPlayerModelParts var1) {
      if (!this.func_178876_d().contains(var1)) {
         this.field_178882_aU.add(var1);
      } else {
         this.field_178882_aU.remove(var1);
      }

      this.func_82879_c();
   }

   public int func_181147_e() {
      return this.field_151451_c >= 4 ? this.field_74345_l : 0;
   }

   public boolean func_181148_f() {
      return this.field_181150_U;
   }

   public static enum Options {
      INVERT_MOUSE("options.invertMouse", false, true),
      SENSITIVITY("options.sensitivity", true, false),
      FOV("options.fov", true, false, 30.0F, 110.0F, 1.0F),
      GAMMA("options.gamma", true, false),
      SATURATION("options.saturation", true, false),
      RENDER_DISTANCE("options.renderDistance", true, false, 2.0F, 16.0F, 1.0F),
      VIEW_BOBBING("options.viewBobbing", false, true),
      ANAGLYPH("options.anaglyph", false, true),
      FRAMERATE_LIMIT("options.framerateLimit", true, false, 10.0F, 260.0F, 10.0F),
      FBO_ENABLE("options.fboEnable", false, true),
      RENDER_CLOUDS("options.renderClouds", false, false),
      GRAPHICS("options.graphics", false, false),
      AMBIENT_OCCLUSION("options.ao", false, false),
      GUI_SCALE("options.guiScale", false, false),
      PARTICLES("options.particles", false, false),
      CHAT_VISIBILITY("options.chat.visibility", false, false),
      CHAT_COLOR("options.chat.color", false, true),
      CHAT_LINKS("options.chat.links", false, true),
      CHAT_OPACITY("options.chat.opacity", true, false),
      CHAT_LINKS_PROMPT("options.chat.links.prompt", false, true),
      SNOOPER_ENABLED("options.snooper", false, true),
      USE_FULLSCREEN("options.fullscreen", false, true),
      ENABLE_VSYNC("options.vsync", false, true),
      USE_VBO("options.vbo", false, true),
      TOUCHSCREEN("options.touchscreen", false, true),
      CHAT_SCALE("options.chat.scale", true, false),
      CHAT_WIDTH("options.chat.width", true, false),
      CHAT_HEIGHT_FOCUSED("options.chat.height.focused", true, false),
      CHAT_HEIGHT_UNFOCUSED("options.chat.height.unfocused", true, false),
      MIPMAP_LEVELS("options.mipmapLevels", true, false, 0.0F, 4.0F, 1.0F),
      FORCE_UNICODE_FONT("options.forceUnicodeFont", false, true),
      STREAM_BYTES_PER_PIXEL("options.stream.bytesPerPixel", true, false),
      STREAM_VOLUME_MIC("options.stream.micVolumne", true, false),
      STREAM_VOLUME_SYSTEM("options.stream.systemVolume", true, false),
      STREAM_KBPS("options.stream.kbps", true, false),
      STREAM_FPS("options.stream.fps", true, false),
      STREAM_COMPRESSION("options.stream.compression", false, false),
      STREAM_SEND_METADATA("options.stream.sendMetadata", false, true),
      STREAM_CHAT_ENABLED("options.stream.chat.enabled", false, false),
      STREAM_CHAT_USER_FILTER("options.stream.chat.userFilter", false, false),
      STREAM_MIC_TOGGLE_BEHAVIOR("options.stream.micToggleBehavior", false, false),
      BLOCK_ALTERNATIVES("options.blockAlternatives", false, true),
      REDUCED_DEBUG_INFO("options.reducedDebugInfo", false, true),
      ENTITY_SHADOWS("options.entityShadows", false, true),
      REALMS_NOTIFICATIONS("options.realmsNotifications", false, true);

      private final boolean field_74385_A;
      private final boolean field_74386_B;
      private final String field_74387_C;
      private final float field_148270_M;
      private float field_148271_N;
      private float field_148272_O;

      public static GameSettings.Options func_74379_a(int var0) {
         GameSettings.Options[] var1 = values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            GameSettings.Options var4 = var1[var3];
            if (var4.func_74381_c() == var0) {
               return var4;
            }
         }

         return null;
      }

      private Options(String var3, boolean var4, boolean var5) {
         this(var3, var4, var5, 0.0F, 1.0F, 0.0F);
      }

      private Options(String var3, boolean var4, boolean var5, float var6, float var7, float var8) {
         this.field_74387_C = var3;
         this.field_74385_A = var4;
         this.field_74386_B = var5;
         this.field_148271_N = var6;
         this.field_148272_O = var7;
         this.field_148270_M = var8;
      }

      public boolean func_74380_a() {
         return this.field_74385_A;
      }

      public boolean func_74382_b() {
         return this.field_74386_B;
      }

      public int func_74381_c() {
         return this.ordinal();
      }

      public String func_74378_d() {
         return this.field_74387_C;
      }

      public float func_148267_f() {
         return this.field_148272_O;
      }

      public void func_148263_a(float var1) {
         this.field_148272_O = var1;
      }

      public float func_148266_c(float var1) {
         return MathHelper.func_76131_a((this.func_148268_e(var1) - this.field_148271_N) / (this.field_148272_O - this.field_148271_N), 0.0F, 1.0F);
      }

      public float func_148262_d(float var1) {
         return this.func_148268_e(this.field_148271_N + (this.field_148272_O - this.field_148271_N) * MathHelper.func_76131_a(var1, 0.0F, 1.0F));
      }

      public float func_148268_e(float var1) {
         var1 = this.func_148264_f(var1);
         return MathHelper.func_76131_a(var1, this.field_148271_N, this.field_148272_O);
      }

      protected float func_148264_f(float var1) {
         if (this.field_148270_M > 0.0F) {
            var1 = this.field_148270_M * (float)Math.round(var1 / this.field_148270_M);
         }

         return var1;
      }
   }
}
