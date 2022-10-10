package net.minecraft.client;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.mojang.datafixers.DataFixTypes;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.renderer.VideoMode;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.ResourcePackInfoClient;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.tutorial.TutorialSteps;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.play.client.CPacketClientSettings;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameSettings {
   private static final Logger field_151454_ax = LogManager.getLogger();
   private static final Gson field_151450_ay = new Gson();
   private static final Type field_151449_az = new ParameterizedType() {
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
   public static final Splitter field_189990_a = Splitter.on(':');
   private static final String[] field_74364_ag = new String[]{"options.particles.all", "options.particles.decreased", "options.particles.minimal"};
   private static final String[] field_98303_au = new String[]{"options.ao.off", "options.ao.min", "options.ao.max"};
   private static final String[] field_181149_aW = new String[]{"options.off", "options.clouds.fast", "options.clouds.fancy"};
   private static final String[] field_186713_aK = new String[]{"options.off", "options.attack.crosshair", "options.attack.hotbar"};
   public static final String[] field_193632_b = new String[]{"options.narrator.off", "options.narrator.all", "options.narrator.chat", "options.narrator.system"};
   public double field_74341_c = 0.5D;
   public boolean field_74338_d;
   public int field_151451_c = -1;
   public boolean field_74336_f = true;
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
   public double field_74357_r;
   public boolean field_74355_t;
   public boolean field_74353_u;
   @Nullable
   public String field_198019_u;
   public boolean field_74352_v;
   public boolean field_178881_t;
   public boolean field_178879_v;
   public boolean field_80005_w;
   public boolean field_82882_x;
   public boolean field_82881_y;
   private final Set<EnumPlayerModelParts> field_178882_aU;
   public boolean field_85185_A;
   public EnumHandSide field_186715_A;
   public int field_92118_B;
   public int field_92119_C;
   public boolean field_92117_D;
   public double field_96691_E;
   public double field_96692_F;
   public double field_96693_G;
   public double field_96694_H;
   public int field_151442_I;
   private final Map<SoundCategory, Float> field_186714_aM;
   public boolean field_181150_U;
   public boolean field_181151_V;
   public int field_186716_M;
   public boolean field_189422_N;
   public boolean field_186717_N;
   public boolean field_183509_X;
   public boolean field_189989_R;
   public TutorialSteps field_193631_S;
   public boolean field_198018_T;
   public int field_205217_U;
   public double field_208033_V;
   public int field_209231_W;
   public KeyBinding field_74351_w;
   public KeyBinding field_74370_x;
   public KeyBinding field_74368_y;
   public KeyBinding field_74366_z;
   public KeyBinding field_74314_A;
   public KeyBinding field_74311_E;
   public KeyBinding field_151444_V;
   public KeyBinding field_151445_Q;
   public KeyBinding field_186718_X;
   public KeyBinding field_74316_C;
   public KeyBinding field_74313_G;
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
   public KeyBinding field_194146_ao;
   public KeyBinding[] field_151456_ac;
   public KeyBinding field_193629_ap;
   public KeyBinding field_193630_aq;
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
   public double field_74334_X;
   public double field_74333_Y;
   public float field_151452_as;
   public int field_74335_Z;
   public int field_74362_aa;
   public int field_192571_R;
   public String field_74363_ab;
   public boolean field_211842_aO;

   public GameSettings(Minecraft var1, File var2) {
      super();
      this.field_74343_n = EntityPlayer.EnumChatVisibility.FULL;
      this.field_74344_o = true;
      this.field_74359_p = true;
      this.field_74358_q = true;
      this.field_74357_r = 1.0D;
      this.field_74355_t = true;
      this.field_74352_v = true;
      this.field_178881_t = true;
      this.field_82881_y = true;
      this.field_178882_aU = Sets.newHashSet(EnumPlayerModelParts.values());
      this.field_186715_A = EnumHandSide.RIGHT;
      this.field_92117_D = true;
      this.field_96691_E = 1.0D;
      this.field_96692_F = 1.0D;
      this.field_96693_G = 0.44366195797920227D;
      this.field_96694_H = 1.0D;
      this.field_151442_I = 4;
      this.field_186714_aM = Maps.newEnumMap(SoundCategory.class);
      this.field_181150_U = true;
      this.field_181151_V = true;
      this.field_186716_M = 1;
      this.field_183509_X = true;
      this.field_189989_R = true;
      this.field_193631_S = TutorialSteps.MOVEMENT;
      this.field_198018_T = true;
      this.field_205217_U = 2;
      this.field_208033_V = 1.0D;
      this.field_209231_W = 1;
      this.field_74351_w = new KeyBinding("key.forward", 87, "key.categories.movement");
      this.field_74370_x = new KeyBinding("key.left", 65, "key.categories.movement");
      this.field_74368_y = new KeyBinding("key.back", 83, "key.categories.movement");
      this.field_74366_z = new KeyBinding("key.right", 68, "key.categories.movement");
      this.field_74314_A = new KeyBinding("key.jump", 32, "key.categories.movement");
      this.field_74311_E = new KeyBinding("key.sneak", 340, "key.categories.movement");
      this.field_151444_V = new KeyBinding("key.sprint", 341, "key.categories.movement");
      this.field_151445_Q = new KeyBinding("key.inventory", 69, "key.categories.inventory");
      this.field_186718_X = new KeyBinding("key.swapHands", 70, "key.categories.inventory");
      this.field_74316_C = new KeyBinding("key.drop", 81, "key.categories.inventory");
      this.field_74313_G = new KeyBinding("key.use", InputMappings.Type.MOUSE, 1, "key.categories.gameplay");
      this.field_74312_F = new KeyBinding("key.attack", InputMappings.Type.MOUSE, 0, "key.categories.gameplay");
      this.field_74322_I = new KeyBinding("key.pickItem", InputMappings.Type.MOUSE, 2, "key.categories.gameplay");
      this.field_74310_D = new KeyBinding("key.chat", 84, "key.categories.multiplayer");
      this.field_74321_H = new KeyBinding("key.playerlist", 258, "key.categories.multiplayer");
      this.field_74323_J = new KeyBinding("key.command", 47, "key.categories.multiplayer");
      this.field_151447_Z = new KeyBinding("key.screenshot", 291, "key.categories.misc");
      this.field_151457_aa = new KeyBinding("key.togglePerspective", 294, "key.categories.misc");
      this.field_151458_ab = new KeyBinding("key.smoothCamera", -1, "key.categories.misc");
      this.field_152395_am = new KeyBinding("key.fullscreen", 300, "key.categories.misc");
      this.field_178883_an = new KeyBinding("key.spectatorOutlines", -1, "key.categories.misc");
      this.field_194146_ao = new KeyBinding("key.advancements", 76, "key.categories.misc");
      this.field_151456_ac = new KeyBinding[]{new KeyBinding("key.hotbar.1", 49, "key.categories.inventory"), new KeyBinding("key.hotbar.2", 50, "key.categories.inventory"), new KeyBinding("key.hotbar.3", 51, "key.categories.inventory"), new KeyBinding("key.hotbar.4", 52, "key.categories.inventory"), new KeyBinding("key.hotbar.5", 53, "key.categories.inventory"), new KeyBinding("key.hotbar.6", 54, "key.categories.inventory"), new KeyBinding("key.hotbar.7", 55, "key.categories.inventory"), new KeyBinding("key.hotbar.8", 56, "key.categories.inventory"), new KeyBinding("key.hotbar.9", 57, "key.categories.inventory")};
      this.field_193629_ap = new KeyBinding("key.saveToolbarActivator", 67, "key.categories.creative");
      this.field_193630_aq = new KeyBinding("key.loadToolbarActivator", 88, "key.categories.creative");
      this.field_74324_K = (KeyBinding[])ArrayUtils.addAll(new KeyBinding[]{this.field_74312_F, this.field_74313_G, this.field_74351_w, this.field_74370_x, this.field_74368_y, this.field_74366_z, this.field_74314_A, this.field_74311_E, this.field_151444_V, this.field_74316_C, this.field_151445_Q, this.field_74310_D, this.field_74321_H, this.field_74322_I, this.field_74323_J, this.field_151447_Z, this.field_151457_aa, this.field_151458_ab, this.field_152395_am, this.field_178883_an, this.field_186718_X, this.field_193629_ap, this.field_193630_aq, this.field_194146_ao}, this.field_151456_ac);
      this.field_74318_M = EnumDifficulty.NORMAL;
      this.field_74332_R = "";
      this.field_74334_X = 70.0D;
      this.field_74363_ab = "en_us";
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
      this.field_74357_r = 1.0D;
      this.field_74355_t = true;
      this.field_74352_v = true;
      this.field_178881_t = true;
      this.field_82881_y = true;
      this.field_178882_aU = Sets.newHashSet(EnumPlayerModelParts.values());
      this.field_186715_A = EnumHandSide.RIGHT;
      this.field_92117_D = true;
      this.field_96691_E = 1.0D;
      this.field_96692_F = 1.0D;
      this.field_96693_G = 0.44366195797920227D;
      this.field_96694_H = 1.0D;
      this.field_151442_I = 4;
      this.field_186714_aM = Maps.newEnumMap(SoundCategory.class);
      this.field_181150_U = true;
      this.field_181151_V = true;
      this.field_186716_M = 1;
      this.field_183509_X = true;
      this.field_189989_R = true;
      this.field_193631_S = TutorialSteps.MOVEMENT;
      this.field_198018_T = true;
      this.field_205217_U = 2;
      this.field_208033_V = 1.0D;
      this.field_209231_W = 1;
      this.field_74351_w = new KeyBinding("key.forward", 87, "key.categories.movement");
      this.field_74370_x = new KeyBinding("key.left", 65, "key.categories.movement");
      this.field_74368_y = new KeyBinding("key.back", 83, "key.categories.movement");
      this.field_74366_z = new KeyBinding("key.right", 68, "key.categories.movement");
      this.field_74314_A = new KeyBinding("key.jump", 32, "key.categories.movement");
      this.field_74311_E = new KeyBinding("key.sneak", 340, "key.categories.movement");
      this.field_151444_V = new KeyBinding("key.sprint", 341, "key.categories.movement");
      this.field_151445_Q = new KeyBinding("key.inventory", 69, "key.categories.inventory");
      this.field_186718_X = new KeyBinding("key.swapHands", 70, "key.categories.inventory");
      this.field_74316_C = new KeyBinding("key.drop", 81, "key.categories.inventory");
      this.field_74313_G = new KeyBinding("key.use", InputMappings.Type.MOUSE, 1, "key.categories.gameplay");
      this.field_74312_F = new KeyBinding("key.attack", InputMappings.Type.MOUSE, 0, "key.categories.gameplay");
      this.field_74322_I = new KeyBinding("key.pickItem", InputMappings.Type.MOUSE, 2, "key.categories.gameplay");
      this.field_74310_D = new KeyBinding("key.chat", 84, "key.categories.multiplayer");
      this.field_74321_H = new KeyBinding("key.playerlist", 258, "key.categories.multiplayer");
      this.field_74323_J = new KeyBinding("key.command", 47, "key.categories.multiplayer");
      this.field_151447_Z = new KeyBinding("key.screenshot", 291, "key.categories.misc");
      this.field_151457_aa = new KeyBinding("key.togglePerspective", 294, "key.categories.misc");
      this.field_151458_ab = new KeyBinding("key.smoothCamera", -1, "key.categories.misc");
      this.field_152395_am = new KeyBinding("key.fullscreen", 300, "key.categories.misc");
      this.field_178883_an = new KeyBinding("key.spectatorOutlines", -1, "key.categories.misc");
      this.field_194146_ao = new KeyBinding("key.advancements", 76, "key.categories.misc");
      this.field_151456_ac = new KeyBinding[]{new KeyBinding("key.hotbar.1", 49, "key.categories.inventory"), new KeyBinding("key.hotbar.2", 50, "key.categories.inventory"), new KeyBinding("key.hotbar.3", 51, "key.categories.inventory"), new KeyBinding("key.hotbar.4", 52, "key.categories.inventory"), new KeyBinding("key.hotbar.5", 53, "key.categories.inventory"), new KeyBinding("key.hotbar.6", 54, "key.categories.inventory"), new KeyBinding("key.hotbar.7", 55, "key.categories.inventory"), new KeyBinding("key.hotbar.8", 56, "key.categories.inventory"), new KeyBinding("key.hotbar.9", 57, "key.categories.inventory")};
      this.field_193629_ap = new KeyBinding("key.saveToolbarActivator", 67, "key.categories.creative");
      this.field_193630_aq = new KeyBinding("key.loadToolbarActivator", 88, "key.categories.creative");
      this.field_74324_K = (KeyBinding[])ArrayUtils.addAll(new KeyBinding[]{this.field_74312_F, this.field_74313_G, this.field_74351_w, this.field_74370_x, this.field_74368_y, this.field_74366_z, this.field_74314_A, this.field_74311_E, this.field_151444_V, this.field_74316_C, this.field_151445_Q, this.field_74310_D, this.field_74321_H, this.field_74322_I, this.field_74323_J, this.field_151447_Z, this.field_151457_aa, this.field_151458_ab, this.field_152395_am, this.field_178883_an, this.field_186718_X, this.field_193629_ap, this.field_193630_aq, this.field_194146_ao}, this.field_151456_ac);
      this.field_74318_M = EnumDifficulty.NORMAL;
      this.field_74332_R = "";
      this.field_74334_X = 70.0D;
      this.field_74363_ab = "en_us";
   }

   public void func_198014_a(KeyBinding var1, InputMappings.Input var2) {
      var1.func_197979_b(var2);
      this.func_74303_b();
   }

   public void func_198016_a(GameSettings.Options var1, double var2) {
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
         int var4 = this.field_151442_I;
         this.field_151442_I = (int)var2;
         if ((double)var4 != var2) {
            this.field_74317_L.func_147117_R().func_147633_a(this.field_151442_I);
            this.field_74317_L.func_110434_K().func_110577_a(TextureMap.field_110575_b);
            this.field_74317_L.func_147117_R().func_174937_a(false, this.field_151442_I > 0);
            this.field_74317_L.func_175603_A();
         }
      }

      if (var1 == GameSettings.Options.RENDER_DISTANCE) {
         this.field_151451_c = (int)var2;
         this.field_74317_L.field_71438_f.func_174979_m();
      }

      if (var1 == GameSettings.Options.BIOME_BLEND_RADIUS) {
         this.field_205217_U = MathHelper.func_76125_a((int)var2, 0, 7);
         this.field_74317_L.field_71438_f.func_72712_a();
      }

      if (var1 == GameSettings.Options.FULLSCREEN_RESOLUTION) {
         this.field_74317_L.field_195558_d.func_198104_b((int)var2);
      }

      if (var1 == GameSettings.Options.MOUSE_WHEEL_SENSITIVITY) {
         this.field_208033_V = var2;
      }

   }

   public void func_74306_a(GameSettings.Options var1, int var2) {
      if (var1 == GameSettings.Options.RENDER_DISTANCE) {
         this.func_198016_a(var1, MathHelper.func_151237_a((double)(this.field_151451_c + var2), var1.func_198007_e(), var1.func_198009_f()));
      }

      if (var1 == GameSettings.Options.MAIN_HAND) {
         this.field_186715_A = this.field_186715_A.func_188468_a();
      }

      if (var1 == GameSettings.Options.INVERT_MOUSE) {
         this.field_74338_d = !this.field_74338_d;
      }

      if (var1 == GameSettings.Options.GUI_SCALE) {
         this.field_74335_Z = Integer.remainderUnsigned(this.field_74335_Z + var2, this.field_74317_L.field_195558_d.func_198078_c(0) + 1);
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
         this.field_211842_aO = !this.field_211842_aO;
         this.field_74317_L.func_211500_ak().func_211825_a(this.field_211842_aO);
      }

      if (var1 == GameSettings.Options.FBO_ENABLE) {
         this.field_151448_g = !this.field_151448_g;
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
         if (this.field_74317_L.field_195558_d.func_198113_j() != this.field_74353_u) {
            this.field_74317_L.field_195558_d.func_198077_g();
         }
      }

      if (var1 == GameSettings.Options.ENABLE_VSYNC) {
         this.field_74352_v = !this.field_74352_v;
         this.field_74317_L.field_195558_d.func_209548_c();
      }

      if (var1 == GameSettings.Options.USE_VBO) {
         this.field_178881_t = !this.field_178881_t;
         this.field_74317_L.field_71438_f.func_72712_a();
      }

      if (var1 == GameSettings.Options.REDUCED_DEBUG_INFO) {
         this.field_178879_v = !this.field_178879_v;
      }

      if (var1 == GameSettings.Options.ENTITY_SHADOWS) {
         this.field_181151_V = !this.field_181151_V;
      }

      if (var1 == GameSettings.Options.ATTACK_INDICATOR) {
         this.field_186716_M = (this.field_186716_M + var2) % 3;
      }

      if (var1 == GameSettings.Options.SHOW_SUBTITLES) {
         this.field_186717_N = !this.field_186717_N;
      }

      if (var1 == GameSettings.Options.REALMS_NOTIFICATIONS) {
         this.field_183509_X = !this.field_183509_X;
      }

      if (var1 == GameSettings.Options.AUTO_JUMP) {
         this.field_189989_R = !this.field_189989_R;
      }

      if (var1 == GameSettings.Options.AUTO_SUGGESTIONS) {
         this.field_198018_T = !this.field_198018_T;
      }

      if (var1 == GameSettings.Options.NARRATOR) {
         if (NarratorChatListener.field_193643_a.func_193640_a()) {
            this.field_192571_R = (this.field_192571_R + var2) % field_193632_b.length;
         } else {
            this.field_192571_R = 0;
         }

         NarratorChatListener.field_193643_a.func_193641_a(this.field_192571_R);
      }

      this.func_74303_b();
   }

   public double func_198015_a(GameSettings.Options var1) {
      if (var1 == GameSettings.Options.BIOME_BLEND_RADIUS) {
         return (double)this.field_205217_U;
      } else if (var1 == GameSettings.Options.FOV) {
         return this.field_74334_X;
      } else if (var1 == GameSettings.Options.GAMMA) {
         return this.field_74333_Y;
      } else if (var1 == GameSettings.Options.SATURATION) {
         return (double)this.field_151452_as;
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
         return (double)this.field_74350_i;
      } else if (var1 == GameSettings.Options.MIPMAP_LEVELS) {
         return (double)this.field_151442_I;
      } else if (var1 == GameSettings.Options.RENDER_DISTANCE) {
         return (double)this.field_151451_c;
      } else if (var1 == GameSettings.Options.FULLSCREEN_RESOLUTION) {
         return (double)this.field_74317_L.field_195558_d.func_198090_e();
      } else {
         return var1 == GameSettings.Options.MOUSE_WHEEL_SENSITIVITY ? this.field_208033_V : 0.0D;
      }
   }

   public boolean func_74308_b(GameSettings.Options var1) {
      switch(var1) {
      case INVERT_MOUSE:
         return this.field_74338_d;
      case VIEW_BOBBING:
         return this.field_74336_f;
      case FBO_ENABLE:
         return this.field_151448_g;
      case CHAT_COLOR:
         return this.field_74344_o;
      case CHAT_LINKS:
         return this.field_74359_p;
      case CHAT_LINKS_PROMPT:
         return this.field_74358_q;
      case SNOOPER_ENABLED:
         if (this.field_74355_t) {
         }

         return false;
      case USE_FULLSCREEN:
         return this.field_74353_u;
      case ENABLE_VSYNC:
         return this.field_74352_v;
      case USE_VBO:
         return this.field_178881_t;
      case TOUCHSCREEN:
         return this.field_85185_A;
      case FORCE_UNICODE_FONT:
         return this.field_211842_aO;
      case REDUCED_DEBUG_INFO:
         return this.field_178879_v;
      case ENTITY_SHADOWS:
         return this.field_181151_V;
      case SHOW_SUBTITLES:
         return this.field_186717_N;
      case REALMS_NOTIFICATIONS:
         return this.field_183509_X;
      case ENABLE_WEAK_ATTACKS:
         return this.field_189422_N;
      case AUTO_JUMP:
         return this.field_189989_R;
      case AUTO_SUGGESTIONS:
         return this.field_198018_T;
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
         double var9 = this.func_198015_a(var1);
         double var5 = var1.func_198008_a(var9);
         if (var1 == GameSettings.Options.SENSITIVITY) {
            if (var5 == 0.0D) {
               return var2 + I18n.func_135052_a("options.sensitivity.min");
            } else {
               return var5 == 1.0D ? var2 + I18n.func_135052_a("options.sensitivity.max") : var2 + (int)(var5 * 200.0D) + "%";
            }
         } else if (var1 == GameSettings.Options.BIOME_BLEND_RADIUS) {
            if (var5 == 0.0D) {
               return var2 + I18n.func_135052_a("options.off");
            } else {
               int var7 = this.field_205217_U * 2 + 1;
               return var2 + var7 + "x" + var7;
            }
         } else if (var1 == GameSettings.Options.FOV) {
            if (var9 == 70.0D) {
               return var2 + I18n.func_135052_a("options.fov.min");
            } else {
               return var9 == 110.0D ? var2 + I18n.func_135052_a("options.fov.max") : var2 + (int)var9;
            }
         } else if (var1 == GameSettings.Options.FRAMERATE_LIMIT) {
            return var9 == var1.field_148272_O ? var2 + I18n.func_135052_a("options.framerateLimit.max") : var2 + I18n.func_135052_a("options.framerate", (int)var9);
         } else if (var1 == GameSettings.Options.RENDER_CLOUDS) {
            return var9 == var1.field_148271_N ? var2 + I18n.func_135052_a("options.cloudHeight.min") : var2 + ((int)var9 + 128);
         } else if (var1 == GameSettings.Options.GAMMA) {
            if (var5 == 0.0D) {
               return var2 + I18n.func_135052_a("options.gamma.min");
            } else {
               return var5 == 1.0D ? var2 + I18n.func_135052_a("options.gamma.max") : var2 + "+" + (int)(var5 * 100.0D) + "%";
            }
         } else if (var1 == GameSettings.Options.SATURATION) {
            return var2 + (int)(var5 * 400.0D) + "%";
         } else if (var1 == GameSettings.Options.CHAT_OPACITY) {
            return var2 + (int)(var5 * 90.0D + 10.0D) + "%";
         } else if (var1 == GameSettings.Options.CHAT_HEIGHT_UNFOCUSED) {
            return var2 + GuiNewChat.func_194816_c(var5) + "px";
         } else if (var1 == GameSettings.Options.CHAT_HEIGHT_FOCUSED) {
            return var2 + GuiNewChat.func_194816_c(var5) + "px";
         } else if (var1 == GameSettings.Options.CHAT_WIDTH) {
            return var2 + GuiNewChat.func_194814_b(var5) + "px";
         } else if (var1 == GameSettings.Options.RENDER_DISTANCE) {
            return var2 + I18n.func_135052_a("options.chunks", (int)var9);
         } else if (var1 == GameSettings.Options.MOUSE_WHEEL_SENSITIVITY) {
            return var5 == 1.0D ? var2 + I18n.func_135052_a("options.mouseWheelSensitivity.default") : var2 + "+" + (int)var5 + "." + (int)(var5 * 10.0D) % 10;
         } else if (var1 == GameSettings.Options.MIPMAP_LEVELS) {
            return var9 == 0.0D ? var2 + I18n.func_135052_a("options.off") : var2 + (int)var9;
         } else if (var1 == GameSettings.Options.FULLSCREEN_RESOLUTION) {
            return var9 == 0.0D ? var2 + I18n.func_135052_a("options.fullscreen.current") : var2 + this.field_74317_L.field_195558_d.func_198088_a((int)var9 - 1);
         } else {
            return var5 == 0.0D ? var2 + I18n.func_135052_a("options.off") : var2 + (int)(var5 * 100.0D) + "%";
         }
      } else if (var1.func_74382_b()) {
         boolean var8 = this.func_74308_b(var1);
         return var8 ? var2 + I18n.func_135052_a("options.on") : var2 + I18n.func_135052_a("options.off");
      } else if (var1 == GameSettings.Options.MAIN_HAND) {
         return var2 + this.field_186715_A;
      } else if (var1 == GameSettings.Options.GUI_SCALE) {
         return var2 + (this.field_74335_Z == 0 ? I18n.func_135052_a("options.guiScale.auto") : this.field_74335_Z);
      } else if (var1 == GameSettings.Options.CHAT_VISIBILITY) {
         return var2 + I18n.func_135052_a(this.field_74343_n.func_151429_b());
      } else if (var1 == GameSettings.Options.PARTICLES) {
         return var2 + func_74299_a(field_74364_ag, this.field_74362_aa);
      } else if (var1 == GameSettings.Options.AMBIENT_OCCLUSION) {
         return var2 + func_74299_a(field_98303_au, this.field_74348_k);
      } else if (var1 == GameSettings.Options.RENDER_CLOUDS) {
         return var2 + func_74299_a(field_181149_aW, this.field_74345_l);
      } else if (var1 == GameSettings.Options.GRAPHICS) {
         if (this.field_74347_j) {
            return var2 + I18n.func_135052_a("options.graphics.fancy");
         } else {
            String var3 = "options.graphics.fast";
            return var2 + I18n.func_135052_a("options.graphics.fast");
         }
      } else if (var1 == GameSettings.Options.ATTACK_INDICATOR) {
         return var2 + func_74299_a(field_186713_aK, this.field_186716_M);
      } else if (var1 == GameSettings.Options.NARRATOR) {
         return NarratorChatListener.field_193643_a.func_193640_a() ? var2 + func_74299_a(field_193632_b, this.field_192571_R) : var2 + I18n.func_135052_a("options.narrator.notavailable");
      } else {
         return var2;
      }
   }

   public void func_74300_a() {
      try {
         if (!this.field_74354_ai.exists()) {
            return;
         }

         this.field_186714_aM.clear();
         List var1 = IOUtils.readLines(new FileInputStream(this.field_74354_ai));
         NBTTagCompound var2 = new NBTTagCompound();
         Iterator var3 = var1.iterator();

         String var4;
         while(var3.hasNext()) {
            var4 = (String)var3.next();

            try {
               Iterator var5 = field_189990_a.omitEmptyStrings().limit(2).split(var4).iterator();
               var2.func_74778_a((String)var5.next(), (String)var5.next());
            } catch (Exception var10) {
               field_151454_ax.warn("Skipping bad option: {}", var4);
            }
         }

         var2 = this.func_189988_a(var2);
         var3 = var2.func_150296_c().iterator();

         while(var3.hasNext()) {
            var4 = (String)var3.next();
            String var13 = var2.func_74779_i(var4);

            try {
               if ("mouseSensitivity".equals(var4)) {
                  this.field_74341_c = (double)this.func_74305_a(var13);
               }

               if ("fov".equals(var4)) {
                  this.field_74334_X = (double)(this.func_74305_a(var13) * 40.0F + 70.0F);
               }

               if ("gamma".equals(var4)) {
                  this.field_74333_Y = (double)this.func_74305_a(var13);
               }

               if ("saturation".equals(var4)) {
                  this.field_151452_as = this.func_74305_a(var13);
               }

               if ("invertYMouse".equals(var4)) {
                  this.field_74338_d = "true".equals(var13);
               }

               if ("renderDistance".equals(var4)) {
                  this.field_151451_c = Integer.parseInt(var13);
               }

               if ("guiScale".equals(var4)) {
                  this.field_74335_Z = Integer.parseInt(var13);
               }

               if ("particles".equals(var4)) {
                  this.field_74362_aa = Integer.parseInt(var13);
               }

               if ("bobView".equals(var4)) {
                  this.field_74336_f = "true".equals(var13);
               }

               if ("maxFps".equals(var4)) {
                  this.field_74350_i = Integer.parseInt(var13);
               }

               if ("fboEnable".equals(var4)) {
                  this.field_151448_g = "true".equals(var13);
               }

               if ("difficulty".equals(var4)) {
                  this.field_74318_M = EnumDifficulty.func_151523_a(Integer.parseInt(var13));
               }

               if ("fancyGraphics".equals(var4)) {
                  this.field_74347_j = "true".equals(var13);
               }

               if ("tutorialStep".equals(var4)) {
                  this.field_193631_S = TutorialSteps.func_193307_a(var13);
               }

               if ("ao".equals(var4)) {
                  if ("true".equals(var13)) {
                     this.field_74348_k = 2;
                  } else if ("false".equals(var13)) {
                     this.field_74348_k = 0;
                  } else {
                     this.field_74348_k = Integer.parseInt(var13);
                  }
               }

               if ("renderClouds".equals(var4)) {
                  if ("true".equals(var13)) {
                     this.field_74345_l = 2;
                  } else if ("false".equals(var13)) {
                     this.field_74345_l = 0;
                  } else if ("fast".equals(var13)) {
                     this.field_74345_l = 1;
                  }
               }

               if ("attackIndicator".equals(var4)) {
                  if ("0".equals(var13)) {
                     this.field_186716_M = 0;
                  } else if ("1".equals(var13)) {
                     this.field_186716_M = 1;
                  } else if ("2".equals(var13)) {
                     this.field_186716_M = 2;
                  }
               }

               if ("resourcePacks".equals(var4)) {
                  this.field_151453_l = (List)JsonUtils.func_193840_a(field_151450_ay, var13, field_151449_az);
                  if (this.field_151453_l == null) {
                     this.field_151453_l = Lists.newArrayList();
                  }
               }

               if ("incompatibleResourcePacks".equals(var4)) {
                  this.field_183018_l = (List)JsonUtils.func_193840_a(field_151450_ay, var13, field_151449_az);
                  if (this.field_183018_l == null) {
                     this.field_183018_l = Lists.newArrayList();
                  }
               }

               if ("lastServer".equals(var4)) {
                  this.field_74332_R = var13;
               }

               if ("lang".equals(var4)) {
                  this.field_74363_ab = var13;
               }

               if ("chatVisibility".equals(var4)) {
                  this.field_74343_n = EntityPlayer.EnumChatVisibility.func_151426_a(Integer.parseInt(var13));
               }

               if ("chatColors".equals(var4)) {
                  this.field_74344_o = "true".equals(var13);
               }

               if ("chatLinks".equals(var4)) {
                  this.field_74359_p = "true".equals(var13);
               }

               if ("chatLinksPrompt".equals(var4)) {
                  this.field_74358_q = "true".equals(var13);
               }

               if ("chatOpacity".equals(var4)) {
                  this.field_74357_r = (double)this.func_74305_a(var13);
               }

               if ("snooperEnabled".equals(var4)) {
                  this.field_74355_t = "true".equals(var13);
               }

               if ("fullscreen".equals(var4)) {
                  this.field_74353_u = "true".equals(var13);
               }

               if ("fullscreenResolution".equals(var4)) {
                  this.field_198019_u = var13;
               }

               if ("enableVsync".equals(var4)) {
                  this.field_74352_v = "true".equals(var13);
               }

               if ("useVbo".equals(var4)) {
                  this.field_178881_t = "true".equals(var13);
               }

               if ("hideServerAddress".equals(var4)) {
                  this.field_80005_w = "true".equals(var13);
               }

               if ("advancedItemTooltips".equals(var4)) {
                  this.field_82882_x = "true".equals(var13);
               }

               if ("pauseOnLostFocus".equals(var4)) {
                  this.field_82881_y = "true".equals(var13);
               }

               if ("touchscreen".equals(var4)) {
                  this.field_85185_A = "true".equals(var13);
               }

               if ("overrideHeight".equals(var4)) {
                  this.field_92119_C = Integer.parseInt(var13);
               }

               if ("overrideWidth".equals(var4)) {
                  this.field_92118_B = Integer.parseInt(var13);
               }

               if ("heldItemTooltips".equals(var4)) {
                  this.field_92117_D = "true".equals(var13);
               }

               if ("chatHeightFocused".equals(var4)) {
                  this.field_96694_H = (double)this.func_74305_a(var13);
               }

               if ("chatHeightUnfocused".equals(var4)) {
                  this.field_96693_G = (double)this.func_74305_a(var13);
               }

               if ("chatScale".equals(var4)) {
                  this.field_96691_E = (double)this.func_74305_a(var13);
               }

               if ("chatWidth".equals(var4)) {
                  this.field_96692_F = (double)this.func_74305_a(var13);
               }

               if ("mipmapLevels".equals(var4)) {
                  this.field_151442_I = Integer.parseInt(var13);
               }

               if ("forceUnicodeFont".equals(var4)) {
                  this.field_211842_aO = "true".equals(var13);
               }

               if ("reducedDebugInfo".equals(var4)) {
                  this.field_178879_v = "true".equals(var13);
               }

               if ("useNativeTransport".equals(var4)) {
                  this.field_181150_U = "true".equals(var13);
               }

               if ("entityShadows".equals(var4)) {
                  this.field_181151_V = "true".equals(var13);
               }

               if ("mainHand".equals(var4)) {
                  this.field_186715_A = "left".equals(var13) ? EnumHandSide.LEFT : EnumHandSide.RIGHT;
               }

               if ("showSubtitles".equals(var4)) {
                  this.field_186717_N = "true".equals(var13);
               }

               if ("realmsNotifications".equals(var4)) {
                  this.field_183509_X = "true".equals(var13);
               }

               if ("enableWeakAttacks".equals(var4)) {
                  this.field_189422_N = "true".equals(var13);
               }

               if ("autoJump".equals(var4)) {
                  this.field_189989_R = "true".equals(var13);
               }

               if ("narrator".equals(var4)) {
                  this.field_192571_R = Integer.parseInt(var13);
               }

               if ("autoSuggestions".equals(var4)) {
                  this.field_198018_T = "true".equals(var13);
               }

               if ("biomeBlendRadius".equals(var4)) {
                  this.field_205217_U = Integer.parseInt(var13);
               }

               if ("mouseWheelSensitivity".equals(var4)) {
                  this.field_208033_V = (double)this.func_74305_a(var13);
               }

               if ("glDebugVerbosity".equals(var4)) {
                  this.field_209231_W = Integer.parseInt(var13);
               }

               KeyBinding[] var6 = this.field_74324_K;
               int var7 = var6.length;

               int var8;
               for(var8 = 0; var8 < var7; ++var8) {
                  KeyBinding var9 = var6[var8];
                  if (var4.equals("key_" + var9.func_151464_g())) {
                     var9.func_197979_b(InputMappings.func_197955_a(var13));
                  }
               }

               SoundCategory[] var14 = SoundCategory.values();
               var7 = var14.length;

               for(var8 = 0; var8 < var7; ++var8) {
                  SoundCategory var16 = var14[var8];
                  if (var4.equals("soundCategory_" + var16.func_187948_a())) {
                     this.field_186714_aM.put(var16, this.func_74305_a(var13));
                  }
               }

               EnumPlayerModelParts[] var15 = EnumPlayerModelParts.values();
               var7 = var15.length;

               for(var8 = 0; var8 < var7; ++var8) {
                  EnumPlayerModelParts var17 = var15[var8];
                  if (var4.equals("modelPart_" + var17.func_179329_c())) {
                     this.func_178878_a(var17, "true".equals(var13));
                  }
               }
            } catch (Exception var11) {
               field_151454_ax.warn("Skipping bad option: {}:{}", var4, var13);
            }
         }

         KeyBinding.func_74508_b();
      } catch (Exception var12) {
         field_151454_ax.error("Failed to load options", var12);
      }

   }

   private NBTTagCompound func_189988_a(NBTTagCompound var1) {
      int var2 = 0;

      try {
         var2 = Integer.parseInt(var1.func_74779_i("version"));
      } catch (RuntimeException var4) {
      }

      return NBTUtil.func_210822_a(this.field_74317_L.func_184126_aj(), DataFixTypes.OPTIONS, var1, var2);
   }

   private float func_74305_a(String var1) {
      if ("true".equals(var1)) {
         return 1.0F;
      } else {
         return "false".equals(var1) ? 0.0F : Float.parseFloat(var1);
      }
   }

   public void func_74303_b() {
      PrintWriter var1 = null;

      try {
         var1 = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.field_74354_ai), StandardCharsets.UTF_8));
         var1.println("version:1631");
         var1.println("invertYMouse:" + this.field_74338_d);
         var1.println("mouseSensitivity:" + this.field_74341_c);
         var1.println("fov:" + (this.field_74334_X - 70.0D) / 40.0D);
         var1.println("gamma:" + this.field_74333_Y);
         var1.println("saturation:" + this.field_151452_as);
         var1.println("renderDistance:" + this.field_151451_c);
         var1.println("guiScale:" + this.field_74335_Z);
         var1.println("particles:" + this.field_74362_aa);
         var1.println("bobView:" + this.field_74336_f);
         var1.println("maxFps:" + this.field_74350_i);
         var1.println("fboEnable:" + this.field_151448_g);
         var1.println("difficulty:" + this.field_74318_M.func_151525_a());
         var1.println("fancyGraphics:" + this.field_74347_j);
         var1.println("ao:" + this.field_74348_k);
         var1.println("biomeBlendRadius:" + this.field_205217_U);
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
         if (this.field_74317_L.field_195558_d.func_198106_d().isPresent()) {
            var1.println("fullscreenResolution:" + ((VideoMode)this.field_74317_L.field_195558_d.func_198106_d().get()).func_198066_g());
         }

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
         var1.println("mipmapLevels:" + this.field_151442_I);
         var1.println("forceUnicodeFont:" + this.field_211842_aO);
         var1.println("reducedDebugInfo:" + this.field_178879_v);
         var1.println("useNativeTransport:" + this.field_181150_U);
         var1.println("entityShadows:" + this.field_181151_V);
         var1.println("mainHand:" + (this.field_186715_A == EnumHandSide.LEFT ? "left" : "right"));
         var1.println("attackIndicator:" + this.field_186716_M);
         var1.println("showSubtitles:" + this.field_186717_N);
         var1.println("realmsNotifications:" + this.field_183509_X);
         var1.println("enableWeakAttacks:" + this.field_189422_N);
         var1.println("autoJump:" + this.field_189989_R);
         var1.println("narrator:" + this.field_192571_R);
         var1.println("tutorialStep:" + this.field_193631_S.func_193308_a());
         var1.println("autoSuggestions:" + this.field_198018_T);
         var1.println("mouseWheelSensitivity:" + this.field_208033_V);
         var1.println("glDebugVerbosity:" + this.field_209231_W);
         KeyBinding[] var2 = this.field_74324_K;
         int var3 = var2.length;

         int var4;
         for(var4 = 0; var4 < var3; ++var4) {
            KeyBinding var5 = var2[var4];
            var1.println("key_" + var5.func_151464_g() + ":" + var5.func_197982_m());
         }

         SoundCategory[] var11 = SoundCategory.values();
         var3 = var11.length;

         for(var4 = 0; var4 < var3; ++var4) {
            SoundCategory var13 = var11[var4];
            var1.println("soundCategory_" + var13.func_187948_a() + ":" + this.func_186711_a(var13));
         }

         EnumPlayerModelParts[] var12 = EnumPlayerModelParts.values();
         var3 = var12.length;

         for(var4 = 0; var4 < var3; ++var4) {
            EnumPlayerModelParts var14 = var12[var4];
            var1.println("modelPart_" + var14.func_179329_c() + ":" + this.field_178882_aU.contains(var14));
         }
      } catch (Exception var9) {
         field_151454_ax.error("Failed to save options", var9);
      } finally {
         IOUtils.closeQuietly(var1);
      }

      this.func_82879_c();
   }

   public float func_186711_a(SoundCategory var1) {
      return this.field_186714_aM.containsKey(var1) ? (Float)this.field_186714_aM.get(var1) : 1.0F;
   }

   public void func_186712_a(SoundCategory var1, float var2) {
      this.field_74317_L.func_147118_V().func_184399_a(var1, var2);
      this.field_186714_aM.put(var1, var2);
   }

   public void func_82879_c() {
      if (this.field_74317_L.field_71439_g != null) {
         int var1 = 0;

         EnumPlayerModelParts var3;
         for(Iterator var2 = this.field_178882_aU.iterator(); var2.hasNext(); var1 |= var3.func_179327_a()) {
            var3 = (EnumPlayerModelParts)var2.next();
         }

         this.field_74317_L.field_71439_g.field_71174_a.func_147297_a(new CPacketClientSettings(this.field_74363_ab, this.field_151451_c, this.field_74343_n, this.field_74344_o, var1, this.field_186715_A));
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
      if (this.func_178876_d().contains(var1)) {
         this.field_178882_aU.remove(var1);
      } else {
         this.field_178882_aU.add(var1);
      }

      this.func_82879_c();
   }

   public int func_181147_e() {
      return this.field_151451_c >= 4 ? this.field_74345_l : 0;
   }

   public boolean func_181148_f() {
      return this.field_181150_U;
   }

   public void func_198017_a(ResourcePackList<ResourcePackInfoClient> var1) {
      var1.func_198983_a();
      LinkedHashSet var2 = Sets.newLinkedHashSet();
      Iterator var3 = this.field_151453_l.iterator();

      while(true) {
         while(var3.hasNext()) {
            String var4 = (String)var3.next();
            ResourcePackInfoClient var5 = (ResourcePackInfoClient)var1.func_198981_a(var4);
            if (var5 == null && !var4.startsWith("file/")) {
               var5 = (ResourcePackInfoClient)var1.func_198981_a("file/" + var4);
            }

            if (var5 == null) {
               field_151454_ax.warn("Removed resource pack {} from options because it doesn't seem to exist anymore", var4);
               var3.remove();
            } else if (!var5.func_195791_d().func_198968_a() && !this.field_183018_l.contains(var4)) {
               field_151454_ax.warn("Removed resource pack {} from options because it is no longer compatible", var4);
               var3.remove();
            } else if (var5.func_195791_d().func_198968_a() && this.field_183018_l.contains(var4)) {
               field_151454_ax.info("Removed resource pack {} from incompatibility list because it's now compatible", var4);
               this.field_183018_l.remove(var4);
            } else {
               var2.add(var5);
            }
         }

         var1.func_198985_a(var2);
         return;
      }
   }

   public static enum Options {
      INVERT_MOUSE("options.invertMouse", false, true),
      SENSITIVITY("options.sensitivity", true, false),
      FOV("options.fov", true, false, 30.0D, 110.0D, 1.0F),
      GAMMA("options.gamma", true, false),
      SATURATION("options.saturation", true, false),
      RENDER_DISTANCE("options.renderDistance", true, false, 2.0D, 16.0D, 1.0F),
      VIEW_BOBBING("options.viewBobbing", false, true),
      FRAMERATE_LIMIT("options.framerateLimit", true, false, 10.0D, 260.0D, 10.0F),
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
      FULLSCREEN_RESOLUTION("options.fullscreen.resolution", true, false, 0.0D, 0.0D, 1.0F),
      USE_FULLSCREEN("options.fullscreen", false, true),
      ENABLE_VSYNC("options.vsync", false, true),
      USE_VBO("options.vbo", false, true),
      TOUCHSCREEN("options.touchscreen", false, true),
      CHAT_SCALE("options.chat.scale", true, false),
      CHAT_WIDTH("options.chat.width", true, false),
      CHAT_HEIGHT_FOCUSED("options.chat.height.focused", true, false),
      CHAT_HEIGHT_UNFOCUSED("options.chat.height.unfocused", true, false),
      MIPMAP_LEVELS("options.mipmapLevels", true, false, 0.0D, 4.0D, 1.0F),
      FORCE_UNICODE_FONT("options.forceUnicodeFont", false, true),
      REDUCED_DEBUG_INFO("options.reducedDebugInfo", false, true),
      ENTITY_SHADOWS("options.entityShadows", false, true),
      MAIN_HAND("options.mainHand", false, false),
      ATTACK_INDICATOR("options.attackIndicator", false, false),
      ENABLE_WEAK_ATTACKS("options.enableWeakAttacks", false, true),
      SHOW_SUBTITLES("options.showSubtitles", false, true),
      REALMS_NOTIFICATIONS("options.realmsNotifications", false, true),
      AUTO_JUMP("options.autoJump", false, true),
      NARRATOR("options.narrator", false, false),
      AUTO_SUGGESTIONS("options.autoSuggestCommands", false, true),
      BIOME_BLEND_RADIUS("options.biomeBlendRadius", true, false, 0.0D, 7.0D, 1.0F),
      MOUSE_WHEEL_SENSITIVITY("options.mouseWheelSensitivity", true, false, 1.0D, 10.0D, 0.5F);

      private final boolean field_74385_A;
      private final boolean field_74386_B;
      private final String field_74387_C;
      private final float field_148270_M;
      private double field_148271_N;
      private double field_148272_O;

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
         this(var3, var4, var5, 0.0D, 1.0D, 0.0F);
      }

      private Options(String var3, boolean var4, boolean var5, double var6, double var8, float var10) {
         this.field_74387_C = var3;
         this.field_74385_A = var4;
         this.field_74386_B = var5;
         this.field_148271_N = var6;
         this.field_148272_O = var8;
         this.field_148270_M = var10;
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

      public double func_198007_e() {
         return this.field_148271_N;
      }

      public double func_198009_f() {
         return this.field_148272_O;
      }

      public void func_148263_a(float var1) {
         this.field_148272_O = (double)var1;
      }

      public double func_198008_a(double var1) {
         return MathHelper.func_151237_a((this.func_198011_c(var1) - this.field_148271_N) / (this.field_148272_O - this.field_148271_N), 0.0D, 1.0D);
      }

      public double func_198004_b(double var1) {
         return this.func_198011_c(this.field_148271_N + (this.field_148272_O - this.field_148271_N) * MathHelper.func_151237_a(var1, 0.0D, 1.0D));
      }

      public double func_198011_c(double var1) {
         var1 = this.func_198006_d(var1);
         return MathHelper.func_151237_a(var1, this.field_148271_N, this.field_148272_O);
      }

      private double func_198006_d(double var1) {
         if (this.field_148270_M > 0.0F) {
            var1 = (double)(this.field_148270_M * (float)Math.round(var1 / (double)this.field_148270_M));
         }

         return var1;
      }
   }
}
