package net.minecraft.client.settings;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.stream.TwitchStream;
import net.minecraft.entity.player.EntityPlayer$EnumChatVisibility;
import net.minecraft.network.play.client.C15PacketClientSettings;
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
   private static final ParameterizedType field_151449_az = new GameSettings$1();
   private static final String[] field_74367_ae = new String[]{
      "options.guiScale.auto", "options.guiScale.small", "options.guiScale.normal", "options.guiScale.large"
   };
   private static final String[] field_74364_ag = new String[]{"options.particles.all", "options.particles.decreased", "options.particles.minimal"};
   private static final String[] field_98303_au = new String[]{"options.ao.off", "options.ao.min", "options.ao.max"};
   private static final String[] field_152391_aS = new String[]{
      "options.stream.compression.low", "options.stream.compression.medium", "options.stream.compression.high"
   };
   private static final String[] field_152392_aT = new String[]{
      "options.stream.chat.enabled.streaming", "options.stream.chat.enabled.always", "options.stream.chat.enabled.never"
   };
   private static final String[] field_152393_aU = new String[]{
      "options.stream.chat.userFilter.all", "options.stream.chat.userFilter.subs", "options.stream.chat.userFilter.mods"
   };
   private static final String[] field_152394_aV = new String[]{"options.stream.mic_toggle.mute", "options.stream.mic_toggle.talk"};
   public float field_74341_c = 0.5F;
   public boolean field_74338_d;
   public int field_151451_c = -1;
   public boolean field_74336_f = true;
   public boolean field_74337_g;
   public boolean field_74349_h;
   public boolean field_151448_g = true;
   public int field_74350_i = 120;
   public boolean field_74347_j = true;
   public int field_74348_k = 2;
   public boolean field_74345_l = true;
   public List field_151453_l = new ArrayList();
   public EntityPlayer$EnumChatVisibility field_74343_n = EntityPlayer$EnumChatVisibility.FULL;
   public boolean field_74344_o = true;
   public boolean field_74359_p = true;
   public boolean field_74358_q = true;
   public float field_74357_r = 1.0F;
   public boolean field_74355_t = true;
   public boolean field_74353_u;
   public boolean field_74352_v = true;
   public boolean field_80005_w;
   public boolean field_82882_x;
   public boolean field_82881_y = true;
   public boolean field_82880_z = true;
   public boolean field_85185_A;
   public int field_92118_B;
   public int field_92119_C;
   public boolean field_92117_D = true;
   public float field_96691_E = 1.0F;
   public float field_96692_F = 1.0F;
   public float field_96693_G = 0.44366196F;
   public float field_96694_H = 1.0F;
   public boolean field_151441_H = true;
   public int field_151442_I = 4;
   public int field_151443_J = 1;
   private Map field_151446_aD = Maps.newEnumMap(SoundCategory.class);
   public float field_152400_J = 0.5F;
   public float field_152401_K = 1.0F;
   public float field_152402_L = 1.0F;
   public float field_152403_M = 0.5412844F;
   public float field_152404_N = 0.31690142F;
   public int field_152405_O = 1;
   public boolean field_152406_P = true;
   public String field_152407_Q = "";
   public int field_152408_R = 0;
   public int field_152409_S = 0;
   public int field_152410_T = 0;
   public KeyBinding field_74351_w = new KeyBinding("key.forward", 17, "key.categories.movement");
   public KeyBinding field_74370_x = new KeyBinding("key.left", 30, "key.categories.movement");
   public KeyBinding field_74368_y = new KeyBinding("key.back", 31, "key.categories.movement");
   public KeyBinding field_74366_z = new KeyBinding("key.right", 32, "key.categories.movement");
   public KeyBinding field_74314_A = new KeyBinding("key.jump", 57, "key.categories.movement");
   public KeyBinding field_74311_E = new KeyBinding("key.sneak", 42, "key.categories.movement");
   public KeyBinding field_151445_Q = new KeyBinding("key.inventory", 18, "key.categories.inventory");
   public KeyBinding field_74313_G = new KeyBinding("key.use", -99, "key.categories.gameplay");
   public KeyBinding field_74316_C = new KeyBinding("key.drop", 16, "key.categories.gameplay");
   public KeyBinding field_74312_F = new KeyBinding("key.attack", -100, "key.categories.gameplay");
   public KeyBinding field_74322_I = new KeyBinding("key.pickItem", -98, "key.categories.gameplay");
   public KeyBinding field_151444_V = new KeyBinding("key.sprint", 29, "key.categories.gameplay");
   public KeyBinding field_74310_D = new KeyBinding("key.chat", 20, "key.categories.multiplayer");
   public KeyBinding field_74321_H = new KeyBinding("key.playerlist", 15, "key.categories.multiplayer");
   public KeyBinding field_74323_J = new KeyBinding("key.command", 53, "key.categories.multiplayer");
   public KeyBinding field_151447_Z = new KeyBinding("key.screenshot", 60, "key.categories.misc");
   public KeyBinding field_151457_aa = new KeyBinding("key.togglePerspective", 63, "key.categories.misc");
   public KeyBinding field_151458_ab = new KeyBinding("key.smoothCamera", 0, "key.categories.misc");
   public KeyBinding field_152395_am = new KeyBinding("key.fullscreen", 87, "key.categories.misc");
   public KeyBinding field_152396_an = new KeyBinding("key.streamStartStop", 64, "key.categories.stream");
   public KeyBinding field_152397_ao = new KeyBinding("key.streamPauseUnpause", 65, "key.categories.stream");
   public KeyBinding field_152398_ap = new KeyBinding("key.streamCommercial", 0, "key.categories.stream");
   public KeyBinding field_152399_aq = new KeyBinding("key.streamToggleMic", 0, "key.categories.stream");
   public KeyBinding[] field_151456_ac = new KeyBinding[]{
      new KeyBinding("key.hotbar.1", 2, "key.categories.inventory"),
      new KeyBinding("key.hotbar.2", 3, "key.categories.inventory"),
      new KeyBinding("key.hotbar.3", 4, "key.categories.inventory"),
      new KeyBinding("key.hotbar.4", 5, "key.categories.inventory"),
      new KeyBinding("key.hotbar.5", 6, "key.categories.inventory"),
      new KeyBinding("key.hotbar.6", 7, "key.categories.inventory"),
      new KeyBinding("key.hotbar.7", 8, "key.categories.inventory"),
      new KeyBinding("key.hotbar.8", 9, "key.categories.inventory"),
      new KeyBinding("key.hotbar.9", 10, "key.categories.inventory")
   };
   public KeyBinding[] field_74324_K = (KeyBinding[])ArrayUtils.addAll(
      new KeyBinding[]{
         this.field_74312_F,
         this.field_74313_G,
         this.field_74351_w,
         this.field_74370_x,
         this.field_74368_y,
         this.field_74366_z,
         this.field_74314_A,
         this.field_74311_E,
         this.field_74316_C,
         this.field_151445_Q,
         this.field_74310_D,
         this.field_74321_H,
         this.field_74322_I,
         this.field_74323_J,
         this.field_151447_Z,
         this.field_151457_aa,
         this.field_151458_ab,
         this.field_151444_V,
         this.field_152396_an,
         this.field_152397_ao,
         this.field_152398_ap,
         this.field_152399_aq,
         this.field_152395_am
      },
      this.field_151456_ac
   );
   protected Minecraft field_74317_L;
   private File field_74354_ai;
   public EnumDifficulty field_74318_M = EnumDifficulty.NORMAL;
   public boolean field_74319_N;
   public int field_74320_O;
   public boolean field_74330_P;
   public boolean field_74329_Q;
   public String field_74332_R = "";
   public boolean field_74331_S;
   public boolean field_74326_T;
   public boolean field_74325_U;
   public float field_74328_V = 1.0F;
   public float field_74327_W = 1.0F;
   public float field_74334_X = 70.0F;
   public float field_74333_Y;
   public float field_151452_as;
   public int field_74335_Z;
   public int field_74362_aa;
   public String field_74363_ab = "en_US";
   public boolean field_151455_aw = false;

   public GameSettings(Minecraft var1, File var2) {
      super();
      this.field_74317_L = var1;
      this.field_74354_ai = new File(var2, "options.txt");
      GameSettings$Options.RENDER_DISTANCE.func_148263_a(16.0F);
      this.field_151451_c = var1.func_147111_S() ? 12 : 8;
      this.func_74300_a();
   }

   public GameSettings() {
      super();
   }

   public static String func_74298_c(int var0) {
      return var0 < 0 ? I18n.func_135052_a("key.mouseButton", var0 + 101) : Keyboard.getKeyName(var0);
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

   public void func_74304_a(GameSettings$Options var1, float var2) {
      if (var1 == GameSettings$Options.SENSITIVITY) {
         this.field_74341_c = var2;
      }

      if (var1 == GameSettings$Options.FOV) {
         this.field_74334_X = var2;
      }

      if (var1 == GameSettings$Options.GAMMA) {
         this.field_74333_Y = var2;
      }

      if (var1 == GameSettings$Options.FRAMERATE_LIMIT) {
         this.field_74350_i = (int)var2;
      }

      if (var1 == GameSettings$Options.CHAT_OPACITY) {
         this.field_74357_r = var2;
         this.field_74317_L.field_71456_v.func_146158_b().func_146245_b();
      }

      if (var1 == GameSettings$Options.CHAT_HEIGHT_FOCUSED) {
         this.field_96694_H = var2;
         this.field_74317_L.field_71456_v.func_146158_b().func_146245_b();
      }

      if (var1 == GameSettings$Options.CHAT_HEIGHT_UNFOCUSED) {
         this.field_96693_G = var2;
         this.field_74317_L.field_71456_v.func_146158_b().func_146245_b();
      }

      if (var1 == GameSettings$Options.CHAT_WIDTH) {
         this.field_96692_F = var2;
         this.field_74317_L.field_71456_v.func_146158_b().func_146245_b();
      }

      if (var1 == GameSettings$Options.CHAT_SCALE) {
         this.field_96691_E = var2;
         this.field_74317_L.field_71456_v.func_146158_b().func_146245_b();
      }

      if (var1 == GameSettings$Options.ANISOTROPIC_FILTERING) {
         int var3 = this.field_151443_J;
         this.field_151443_J = (int)var2;
         if ((float)var3 != var2) {
            this.field_74317_L.func_147117_R().func_147632_b(this.field_151443_J);
            this.field_74317_L.func_147106_B();
         }
      }

      if (var1 == GameSettings$Options.MIPMAP_LEVELS) {
         int var4 = this.field_151442_I;
         this.field_151442_I = (int)var2;
         if ((float)var4 != var2) {
            this.field_74317_L.func_147117_R().func_147633_a(this.field_151442_I);
            this.field_74317_L.func_147106_B();
         }
      }

      if (var1 == GameSettings$Options.RENDER_DISTANCE) {
         this.field_151451_c = (int)var2;
      }

      if (var1 == GameSettings$Options.STREAM_BYTES_PER_PIXEL) {
         this.field_152400_J = var2;
      }

      if (var1 == GameSettings$Options.STREAM_VOLUME_MIC) {
         this.field_152401_K = var2;
         this.field_74317_L.func_152346_Z().func_152915_s();
      }

      if (var1 == GameSettings$Options.STREAM_VOLUME_SYSTEM) {
         this.field_152402_L = var2;
         this.field_74317_L.func_152346_Z().func_152915_s();
      }

      if (var1 == GameSettings$Options.STREAM_KBPS) {
         this.field_152403_M = var2;
      }

      if (var1 == GameSettings$Options.STREAM_FPS) {
         this.field_152404_N = var2;
      }
   }

   public void func_74306_a(GameSettings$Options var1, int var2) {
      if (var1 == GameSettings$Options.INVERT_MOUSE) {
         this.field_74338_d = !this.field_74338_d;
      }

      if (var1 == GameSettings$Options.GUI_SCALE) {
         this.field_74335_Z = this.field_74335_Z + var2 & 3;
      }

      if (var1 == GameSettings$Options.PARTICLES) {
         this.field_74362_aa = (this.field_74362_aa + var2) % 3;
      }

      if (var1 == GameSettings$Options.VIEW_BOBBING) {
         this.field_74336_f = !this.field_74336_f;
      }

      if (var1 == GameSettings$Options.RENDER_CLOUDS) {
         this.field_74345_l = !this.field_74345_l;
      }

      if (var1 == GameSettings$Options.FORCE_UNICODE_FONT) {
         this.field_151455_aw = !this.field_151455_aw;
         this.field_74317_L.field_71466_p.func_78264_a(this.field_74317_L.func_135016_M().func_135042_a() || this.field_151455_aw);
      }

      if (var1 == GameSettings$Options.ADVANCED_OPENGL) {
         this.field_74349_h = !this.field_74349_h;
         this.field_74317_L.field_71438_f.func_72712_a();
      }

      if (var1 == GameSettings$Options.FBO_ENABLE) {
         this.field_151448_g = !this.field_151448_g;
      }

      if (var1 == GameSettings$Options.ANAGLYPH) {
         this.field_74337_g = !this.field_74337_g;
         this.field_74317_L.func_110436_a();
      }

      if (var1 == GameSettings$Options.DIFFICULTY) {
         this.field_74318_M = EnumDifficulty.func_151523_a(this.field_74318_M.func_151525_a() + var2 & 3);
      }

      if (var1 == GameSettings$Options.GRAPHICS) {
         this.field_74347_j = !this.field_74347_j;
         this.field_74317_L.field_71438_f.func_72712_a();
      }

      if (var1 == GameSettings$Options.AMBIENT_OCCLUSION) {
         this.field_74348_k = (this.field_74348_k + var2) % 3;
         this.field_74317_L.field_71438_f.func_72712_a();
      }

      if (var1 == GameSettings$Options.CHAT_VISIBILITY) {
         this.field_74343_n = EntityPlayer$EnumChatVisibility.func_151426_a((this.field_74343_n.func_151428_a() + var2) % 3);
      }

      if (var1 == GameSettings$Options.STREAM_COMPRESSION) {
         this.field_152405_O = (this.field_152405_O + var2) % 3;
      }

      if (var1 == GameSettings$Options.STREAM_SEND_METADATA) {
         this.field_152406_P = !this.field_152406_P;
      }

      if (var1 == GameSettings$Options.STREAM_CHAT_ENABLED) {
         this.field_152408_R = (this.field_152408_R + var2) % 3;
      }

      if (var1 == GameSettings$Options.STREAM_CHAT_USER_FILTER) {
         this.field_152409_S = (this.field_152409_S + var2) % 3;
      }

      if (var1 == GameSettings$Options.STREAM_MIC_TOGGLE_BEHAVIOR) {
         this.field_152410_T = (this.field_152410_T + var2) % 2;
      }

      if (var1 == GameSettings$Options.CHAT_COLOR) {
         this.field_74344_o = !this.field_74344_o;
      }

      if (var1 == GameSettings$Options.CHAT_LINKS) {
         this.field_74359_p = !this.field_74359_p;
      }

      if (var1 == GameSettings$Options.CHAT_LINKS_PROMPT) {
         this.field_74358_q = !this.field_74358_q;
      }

      if (var1 == GameSettings$Options.SNOOPER_ENABLED) {
         this.field_74355_t = !this.field_74355_t;
      }

      if (var1 == GameSettings$Options.SHOW_CAPE) {
         this.field_82880_z = !this.field_82880_z;
      }

      if (var1 == GameSettings$Options.TOUCHSCREEN) {
         this.field_85185_A = !this.field_85185_A;
      }

      if (var1 == GameSettings$Options.USE_FULLSCREEN) {
         this.field_74353_u = !this.field_74353_u;
         if (this.field_74317_L.func_71372_G() != this.field_74353_u) {
            this.field_74317_L.func_71352_k();
         }
      }

      if (var1 == GameSettings$Options.ENABLE_VSYNC) {
         this.field_74352_v = !this.field_74352_v;
         Display.setVSyncEnabled(this.field_74352_v);
      }

      this.func_74303_b();
   }

   public float func_74296_a(GameSettings$Options var1) {
      if (var1 == GameSettings$Options.FOV) {
         return this.field_74334_X;
      } else if (var1 == GameSettings$Options.GAMMA) {
         return this.field_74333_Y;
      } else if (var1 == GameSettings$Options.SATURATION) {
         return this.field_151452_as;
      } else if (var1 == GameSettings$Options.SENSITIVITY) {
         return this.field_74341_c;
      } else if (var1 == GameSettings$Options.CHAT_OPACITY) {
         return this.field_74357_r;
      } else if (var1 == GameSettings$Options.CHAT_HEIGHT_FOCUSED) {
         return this.field_96694_H;
      } else if (var1 == GameSettings$Options.CHAT_HEIGHT_UNFOCUSED) {
         return this.field_96693_G;
      } else if (var1 == GameSettings$Options.CHAT_SCALE) {
         return this.field_96691_E;
      } else if (var1 == GameSettings$Options.CHAT_WIDTH) {
         return this.field_96692_F;
      } else if (var1 == GameSettings$Options.FRAMERATE_LIMIT) {
         return (float)this.field_74350_i;
      } else if (var1 == GameSettings$Options.ANISOTROPIC_FILTERING) {
         return (float)this.field_151443_J;
      } else if (var1 == GameSettings$Options.MIPMAP_LEVELS) {
         return (float)this.field_151442_I;
      } else if (var1 == GameSettings$Options.RENDER_DISTANCE) {
         return (float)this.field_151451_c;
      } else if (var1 == GameSettings$Options.STREAM_BYTES_PER_PIXEL) {
         return this.field_152400_J;
      } else if (var1 == GameSettings$Options.STREAM_VOLUME_MIC) {
         return this.field_152401_K;
      } else if (var1 == GameSettings$Options.STREAM_VOLUME_SYSTEM) {
         return this.field_152402_L;
      } else if (var1 == GameSettings$Options.STREAM_KBPS) {
         return this.field_152403_M;
      } else {
         return var1 == GameSettings$Options.STREAM_FPS ? this.field_152404_N : 0.0F;
      }
   }

   public boolean func_74308_b(GameSettings$Options var1) {
      switch(var1) {
         case INVERT_MOUSE:
            return this.field_74338_d;
         case VIEW_BOBBING:
            return this.field_74336_f;
         case ANAGLYPH:
            return this.field_74337_g;
         case ADVANCED_OPENGL:
            return this.field_74349_h;
         case FBO_ENABLE:
            return this.field_151448_g;
         case RENDER_CLOUDS:
            return this.field_74345_l;
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
         case SHOW_CAPE:
            return this.field_82880_z;
         case TOUCHSCREEN:
            return this.field_85185_A;
         case STREAM_SEND_METADATA:
            return this.field_152406_P;
         case FORCE_UNICODE_FONT:
            return this.field_151455_aw;
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

   public String func_74297_c(GameSettings$Options var1) {
      String var2 = I18n.func_135052_a(var1.func_74378_d()) + ": ";
      if (var1.func_74380_a()) {
         float var6 = this.func_74296_a(var1);
         float var4 = var1.func_148266_c(var6);
         if (var1 == GameSettings$Options.SENSITIVITY) {
            if (var4 == 0.0F) {
               return var2 + I18n.func_135052_a("options.sensitivity.min");
            } else {
               return var4 == 1.0F ? var2 + I18n.func_135052_a("options.sensitivity.max") : var2 + (int)(var4 * 200.0F) + "%";
            }
         } else if (var1 == GameSettings$Options.FOV) {
            if (var6 == 70.0F) {
               return var2 + I18n.func_135052_a("options.fov.min");
            } else {
               return var6 == 110.0F ? var2 + I18n.func_135052_a("options.fov.max") : var2 + (int)var6;
            }
         } else if (var1 == GameSettings$Options.FRAMERATE_LIMIT) {
            return var6 == GameSettings$Options.access$100(var1) ? var2 + I18n.func_135052_a("options.framerateLimit.max") : var2 + (int)var6 + " fps";
         } else if (var1 == GameSettings$Options.GAMMA) {
            if (var4 == 0.0F) {
               return var2 + I18n.func_135052_a("options.gamma.min");
            } else {
               return var4 == 1.0F ? var2 + I18n.func_135052_a("options.gamma.max") : var2 + "+" + (int)(var4 * 100.0F) + "%";
            }
         } else if (var1 == GameSettings$Options.SATURATION) {
            return var2 + (int)(var4 * 400.0F) + "%";
         } else if (var1 == GameSettings$Options.CHAT_OPACITY) {
            return var2 + (int)(var4 * 90.0F + 10.0F) + "%";
         } else if (var1 == GameSettings$Options.CHAT_HEIGHT_UNFOCUSED) {
            return var2 + GuiNewChat.func_146243_b(var4) + "px";
         } else if (var1 == GameSettings$Options.CHAT_HEIGHT_FOCUSED) {
            return var2 + GuiNewChat.func_146243_b(var4) + "px";
         } else if (var1 == GameSettings$Options.CHAT_WIDTH) {
            return var2 + GuiNewChat.func_146233_a(var4) + "px";
         } else if (var1 == GameSettings$Options.RENDER_DISTANCE) {
            return var2 + (int)var6 + " chunks";
         } else if (var1 == GameSettings$Options.ANISOTROPIC_FILTERING) {
            return var6 == 1.0F ? var2 + I18n.func_135052_a("options.off") : var2 + (int)var6;
         } else if (var1 == GameSettings$Options.MIPMAP_LEVELS) {
            return var6 == 0.0F ? var2 + I18n.func_135052_a("options.off") : var2 + (int)var6;
         } else if (var1 == GameSettings$Options.STREAM_FPS) {
            return var2 + TwitchStream.func_152948_a(var4) + " fps";
         } else if (var1 == GameSettings$Options.STREAM_KBPS) {
            return var2 + TwitchStream.func_152946_b(var4) + " Kbps";
         } else if (var1 == GameSettings$Options.STREAM_BYTES_PER_PIXEL) {
            return var2 + String.format("%.3f bpp", TwitchStream.func_152947_c(var4));
         } else {
            return var4 == 0.0F ? var2 + I18n.func_135052_a("options.off") : var2 + (int)(var4 * 100.0F) + "%";
         }
      } else if (var1.func_74382_b()) {
         boolean var5 = this.func_74308_b(var1);
         return var5 ? var2 + I18n.func_135052_a("options.on") : var2 + I18n.func_135052_a("options.off");
      } else if (var1 == GameSettings$Options.DIFFICULTY) {
         return var2 + I18n.func_135052_a(this.field_74318_M.func_151526_b());
      } else if (var1 == GameSettings$Options.GUI_SCALE) {
         return var2 + func_74299_a(field_74367_ae, this.field_74335_Z);
      } else if (var1 == GameSettings$Options.CHAT_VISIBILITY) {
         return var2 + I18n.func_135052_a(this.field_74343_n.func_151429_b());
      } else if (var1 == GameSettings$Options.PARTICLES) {
         return var2 + func_74299_a(field_74364_ag, this.field_74362_aa);
      } else if (var1 == GameSettings$Options.AMBIENT_OCCLUSION) {
         return var2 + func_74299_a(field_98303_au, this.field_74348_k);
      } else if (var1 == GameSettings$Options.STREAM_COMPRESSION) {
         return var2 + func_74299_a(field_152391_aS, this.field_152405_O);
      } else if (var1 == GameSettings$Options.STREAM_CHAT_ENABLED) {
         return var2 + func_74299_a(field_152392_aT, this.field_152408_R);
      } else if (var1 == GameSettings$Options.STREAM_CHAT_USER_FILTER) {
         return var2 + func_74299_a(field_152393_aU, this.field_152409_S);
      } else if (var1 == GameSettings$Options.STREAM_MIC_TOGGLE_BEHAVIOR) {
         return var2 + func_74299_a(field_152394_aV, this.field_152410_T);
      } else if (var1 == GameSettings$Options.GRAPHICS) {
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

               if (var3[0].equals("invertYMouse")) {
                  this.field_74338_d = var3[1].equals("true");
               }

               if (var3[0].equals("fov")) {
                  this.field_74334_X = this.func_74305_a(var3[1]);
               }

               if (var3[0].equals("gamma")) {
                  this.field_74333_Y = this.func_74305_a(var3[1]);
               }

               if (var3[0].equals("saturation")) {
                  this.field_151452_as = this.func_74305_a(var3[1]);
               }

               if (var3[0].equals("fov")) {
                  this.field_74334_X = this.func_74305_a(var3[1]) * 40.0F + 70.0F;
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

               if (var3[0].equals("advancedOpengl")) {
                  this.field_74349_h = var3[1].equals("true");
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

               if (var3[0].equals("clouds")) {
                  this.field_74345_l = var3[1].equals("true");
               }

               if (var3[0].equals("resourcePacks")) {
                  this.field_151453_l = (List)field_151450_ay.fromJson(var2.substring(var2.indexOf(58) + 1), field_151449_az);
                  if (this.field_151453_l == null) {
                     this.field_151453_l = new ArrayList();
                  }
               }

               if (var3[0].equals("lastServer") && var3.length >= 2) {
                  this.field_74332_R = var2.substring(var2.indexOf(58) + 1);
               }

               if (var3[0].equals("lang") && var3.length >= 2) {
                  this.field_74363_ab = var3[1];
               }

               if (var3[0].equals("chatVisibility")) {
                  this.field_74343_n = EntityPlayer$EnumChatVisibility.func_151426_a(Integer.parseInt(var3[1]));
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

               if (var3[0].equals("hideServerAddress")) {
                  this.field_80005_w = var3[1].equals("true");
               }

               if (var3[0].equals("advancedItemTooltips")) {
                  this.field_82882_x = var3[1].equals("true");
               }

               if (var3[0].equals("pauseOnLostFocus")) {
                  this.field_82881_y = var3[1].equals("true");
               }

               if (var3[0].equals("showCape")) {
                  this.field_82880_z = var3[1].equals("true");
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

               if (var3[0].equals("anisotropicFiltering")) {
                  this.field_151443_J = Integer.parseInt(var3[1]);
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

               for(KeyBinding var7 : this.field_74324_K) {
                  if (var3[0].equals("key_" + var7.func_151464_g())) {
                     var7.func_151462_b(Integer.parseInt(var3[1]));
                  }
               }

               for(SoundCategory var14 : SoundCategory.values()) {
                  if (var3[0].equals("soundCategory_" + var14.func_147155_a())) {
                     this.field_151446_aD.put(var14, this.func_74305_a(var3[1]));
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
         var1.println("advancedOpengl:" + this.field_74349_h);
         var1.println("maxFps:" + this.field_74350_i);
         var1.println("fboEnable:" + this.field_151448_g);
         var1.println("difficulty:" + this.field_74318_M.func_151525_a());
         var1.println("fancyGraphics:" + this.field_74347_j);
         var1.println("ao:" + this.field_74348_k);
         var1.println("clouds:" + this.field_74345_l);
         var1.println("resourcePacks:" + field_151450_ay.toJson(this.field_151453_l));
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
         var1.println("hideServerAddress:" + this.field_80005_w);
         var1.println("advancedItemTooltips:" + this.field_82882_x);
         var1.println("pauseOnLostFocus:" + this.field_82881_y);
         var1.println("showCape:" + this.field_82880_z);
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
         var1.println("anisotropicFiltering:" + this.field_151443_J);
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

         for(KeyBinding var5 : this.field_74324_K) {
            var1.println("key_" + var5.func_151464_g() + ":" + var5.func_151463_i());
         }

         for(SoundCategory var10 : SoundCategory.values()) {
            var1.println("soundCategory_" + var10.func_147155_a() + ":" + this.func_151438_a(var10));
         }

         var1.close();
      } catch (Exception var6) {
         field_151454_ax.error("Failed to save options", var6);
      }

      this.func_82879_c();
   }

   public float func_151438_a(SoundCategory var1) {
      return this.field_151446_aD.containsKey(var1) ? this.field_151446_aD.get(var1) : 1.0F;
   }

   public void func_151439_a(SoundCategory var1, float var2) {
      this.field_74317_L.func_147118_V().func_147684_a(var1, var2);
      this.field_151446_aD.put(var1, var2);
   }

   public void func_82879_c() {
      if (this.field_74317_L.field_71439_g != null) {
         this.field_74317_L
            .field_71439_g
            .field_71174_a
            .func_147297_a(
               new C15PacketClientSettings(
                  this.field_74363_ab, this.field_151451_c, this.field_74343_n, this.field_74344_o, this.field_74318_M, this.field_82880_z
               )
            );
      }
   }

   public boolean func_74309_c() {
      return this.field_151451_c >= 4 && this.field_74345_l;
   }
}
