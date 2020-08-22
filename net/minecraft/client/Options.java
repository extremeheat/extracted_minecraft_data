package net.minecraft.client;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.VideoMode;
import java.io.BufferedReader;
import java.io.File;
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
import net.minecraft.SharedConstants;
import net.minecraft.client.resources.UnopenedResourcePack;
import net.minecraft.client.tutorial.TutorialSteps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundClientInformationPacket;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.ChatVisiblity;
import net.minecraft.world.entity.player.PlayerModelPart;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Options {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = new Gson();
   private static final Type RESOURCE_PACK_TYPE = new ParameterizedType() {
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
   private static final Splitter OPTION_SPLITTER = Splitter.on(':').limit(2);
   public double sensitivity = 0.5D;
   public int renderDistance = -1;
   public int framerateLimit = 120;
   public CloudStatus renderClouds;
   public boolean fancyGraphics;
   public AmbientOcclusionStatus ambientOcclusion;
   public List resourcePacks;
   public List incompatibleResourcePacks;
   public ChatVisiblity chatVisibility;
   public double chatOpacity;
   public double textBackgroundOpacity;
   @Nullable
   public String fullscreenVideoModeString;
   public boolean hideServerAddress;
   public boolean advancedItemTooltips;
   public boolean pauseOnLostFocus;
   private final Set modelParts;
   public HumanoidArm mainHand;
   public int overrideWidth;
   public int overrideHeight;
   public boolean heldItemTooltips;
   public double chatScale;
   public double chatWidth;
   public double chatHeightUnfocused;
   public double chatHeightFocused;
   public int mipmapLevels;
   private final Map sourceVolumes;
   public boolean useNativeTransport;
   public AttackIndicatorStatus attackIndicator;
   public TutorialSteps tutorialStep;
   public int biomeBlendRadius;
   public double mouseWheelSensitivity;
   public boolean rawMouseInput;
   public int glDebugVerbosity;
   public boolean autoJump;
   public boolean autoSuggestions;
   public boolean chatColors;
   public boolean chatLinks;
   public boolean chatLinksPrompt;
   public boolean enableVsync;
   public boolean entityShadows;
   public boolean forceUnicodeFont;
   public boolean invertYMouse;
   public boolean discreteMouseScroll;
   public boolean realmsNotifications;
   public boolean reducedDebugInfo;
   public boolean snooperEnabled;
   public boolean showSubtitles;
   public boolean backgroundForChatOnly;
   public boolean touchscreen;
   public boolean fullscreen;
   public boolean bobView;
   public boolean toggleCrouch;
   public boolean toggleSprint;
   public final KeyMapping keyUp;
   public final KeyMapping keyLeft;
   public final KeyMapping keyDown;
   public final KeyMapping keyRight;
   public final KeyMapping keyJump;
   public final KeyMapping keyShift;
   public final KeyMapping keySprint;
   public final KeyMapping keyInventory;
   public final KeyMapping keySwapHands;
   public final KeyMapping keyDrop;
   public final KeyMapping keyUse;
   public final KeyMapping keyAttack;
   public final KeyMapping keyPickItem;
   public final KeyMapping keyChat;
   public final KeyMapping keyPlayerList;
   public final KeyMapping keyCommand;
   public final KeyMapping keyScreenshot;
   public final KeyMapping keyTogglePerspective;
   public final KeyMapping keySmoothCamera;
   public final KeyMapping keyFullscreen;
   public final KeyMapping keySpectatorOutlines;
   public final KeyMapping keyAdvancements;
   public final KeyMapping[] keyHotbarSlots;
   public final KeyMapping keySaveHotbarActivator;
   public final KeyMapping keyLoadHotbarActivator;
   public final KeyMapping[] keyMappings;
   protected Minecraft minecraft;
   private final File optionsFile;
   public Difficulty difficulty;
   public boolean hideGui;
   public int thirdPersonView;
   public boolean renderDebug;
   public boolean renderDebugCharts;
   public boolean renderFpsChart;
   public String lastMpIp;
   public boolean smoothCamera;
   public double fov;
   public double gamma;
   public int guiScale;
   public ParticleStatus particles;
   public NarratorStatus narratorStatus;
   public String languageCode;

   public Options(Minecraft var1, File var2) {
      this.renderClouds = CloudStatus.FANCY;
      this.fancyGraphics = true;
      this.ambientOcclusion = AmbientOcclusionStatus.MAX;
      this.resourcePacks = Lists.newArrayList();
      this.incompatibleResourcePacks = Lists.newArrayList();
      this.chatVisibility = ChatVisiblity.FULL;
      this.chatOpacity = 1.0D;
      this.textBackgroundOpacity = 0.5D;
      this.pauseOnLostFocus = true;
      this.modelParts = Sets.newHashSet(PlayerModelPart.values());
      this.mainHand = HumanoidArm.RIGHT;
      this.heldItemTooltips = true;
      this.chatScale = 1.0D;
      this.chatWidth = 1.0D;
      this.chatHeightUnfocused = 0.44366195797920227D;
      this.chatHeightFocused = 1.0D;
      this.mipmapLevels = 4;
      this.sourceVolumes = Maps.newEnumMap(SoundSource.class);
      this.useNativeTransport = true;
      this.attackIndicator = AttackIndicatorStatus.CROSSHAIR;
      this.tutorialStep = TutorialSteps.MOVEMENT;
      this.biomeBlendRadius = 2;
      this.mouseWheelSensitivity = 1.0D;
      this.rawMouseInput = true;
      this.glDebugVerbosity = 1;
      this.autoJump = true;
      this.autoSuggestions = true;
      this.chatColors = true;
      this.chatLinks = true;
      this.chatLinksPrompt = true;
      this.enableVsync = true;
      this.entityShadows = true;
      this.realmsNotifications = true;
      this.snooperEnabled = true;
      this.backgroundForChatOnly = true;
      this.bobView = true;
      this.keyUp = new KeyMapping("key.forward", 87, "key.categories.movement");
      this.keyLeft = new KeyMapping("key.left", 65, "key.categories.movement");
      this.keyDown = new KeyMapping("key.back", 83, "key.categories.movement");
      this.keyRight = new KeyMapping("key.right", 68, "key.categories.movement");
      this.keyJump = new KeyMapping("key.jump", 32, "key.categories.movement");
      this.keyShift = new ToggleKeyMapping("key.sneak", 340, "key.categories.movement", () -> {
         return this.toggleCrouch;
      });
      this.keySprint = new ToggleKeyMapping("key.sprint", 341, "key.categories.movement", () -> {
         return this.toggleSprint;
      });
      this.keyInventory = new KeyMapping("key.inventory", 69, "key.categories.inventory");
      this.keySwapHands = new KeyMapping("key.swapHands", 70, "key.categories.inventory");
      this.keyDrop = new KeyMapping("key.drop", 81, "key.categories.inventory");
      this.keyUse = new KeyMapping("key.use", InputConstants.Type.MOUSE, 1, "key.categories.gameplay");
      this.keyAttack = new KeyMapping("key.attack", InputConstants.Type.MOUSE, 0, "key.categories.gameplay");
      this.keyPickItem = new KeyMapping("key.pickItem", InputConstants.Type.MOUSE, 2, "key.categories.gameplay");
      this.keyChat = new KeyMapping("key.chat", 84, "key.categories.multiplayer");
      this.keyPlayerList = new KeyMapping("key.playerlist", 258, "key.categories.multiplayer");
      this.keyCommand = new KeyMapping("key.command", 47, "key.categories.multiplayer");
      this.keyScreenshot = new KeyMapping("key.screenshot", 291, "key.categories.misc");
      this.keyTogglePerspective = new KeyMapping("key.togglePerspective", 294, "key.categories.misc");
      this.keySmoothCamera = new KeyMapping("key.smoothCamera", InputConstants.UNKNOWN.getValue(), "key.categories.misc");
      this.keyFullscreen = new KeyMapping("key.fullscreen", 300, "key.categories.misc");
      this.keySpectatorOutlines = new KeyMapping("key.spectatorOutlines", InputConstants.UNKNOWN.getValue(), "key.categories.misc");
      this.keyAdvancements = new KeyMapping("key.advancements", 76, "key.categories.misc");
      this.keyHotbarSlots = new KeyMapping[]{new KeyMapping("key.hotbar.1", 49, "key.categories.inventory"), new KeyMapping("key.hotbar.2", 50, "key.categories.inventory"), new KeyMapping("key.hotbar.3", 51, "key.categories.inventory"), new KeyMapping("key.hotbar.4", 52, "key.categories.inventory"), new KeyMapping("key.hotbar.5", 53, "key.categories.inventory"), new KeyMapping("key.hotbar.6", 54, "key.categories.inventory"), new KeyMapping("key.hotbar.7", 55, "key.categories.inventory"), new KeyMapping("key.hotbar.8", 56, "key.categories.inventory"), new KeyMapping("key.hotbar.9", 57, "key.categories.inventory")};
      this.keySaveHotbarActivator = new KeyMapping("key.saveToolbarActivator", 67, "key.categories.creative");
      this.keyLoadHotbarActivator = new KeyMapping("key.loadToolbarActivator", 88, "key.categories.creative");
      this.keyMappings = (KeyMapping[])ArrayUtils.addAll(new KeyMapping[]{this.keyAttack, this.keyUse, this.keyUp, this.keyLeft, this.keyDown, this.keyRight, this.keyJump, this.keyShift, this.keySprint, this.keyDrop, this.keyInventory, this.keyChat, this.keyPlayerList, this.keyPickItem, this.keyCommand, this.keyScreenshot, this.keyTogglePerspective, this.keySmoothCamera, this.keyFullscreen, this.keySpectatorOutlines, this.keySwapHands, this.keySaveHotbarActivator, this.keyLoadHotbarActivator, this.keyAdvancements}, this.keyHotbarSlots);
      this.difficulty = Difficulty.NORMAL;
      this.lastMpIp = "";
      this.fov = 70.0D;
      this.particles = ParticleStatus.ALL;
      this.narratorStatus = NarratorStatus.OFF;
      this.languageCode = "en_us";
      this.minecraft = var1;
      this.optionsFile = new File(var2, "options.txt");
      if (var1.is64Bit() && Runtime.getRuntime().maxMemory() >= 1000000000L) {
         Option.RENDER_DISTANCE.setMaxValue(32.0F);
      } else {
         Option.RENDER_DISTANCE.setMaxValue(16.0F);
      }

      this.renderDistance = var1.is64Bit() ? 12 : 8;
      this.load();
   }

   public float getBackgroundOpacity(float var1) {
      return this.backgroundForChatOnly ? var1 : (float)this.textBackgroundOpacity;
   }

   public int getBackgroundColor(float var1) {
      return (int)(this.getBackgroundOpacity(var1) * 255.0F) << 24 & -16777216;
   }

   public int getBackgroundColor(int var1) {
      return this.backgroundForChatOnly ? var1 : (int)(this.textBackgroundOpacity * 255.0D) << 24 & -16777216;
   }

   public void setKey(KeyMapping var1, InputConstants.Key var2) {
      var1.setKey(var2);
      this.save();
   }

   public void load() {
      try {
         if (!this.optionsFile.exists()) {
            return;
         }

         this.sourceVolumes.clear();
         CompoundTag var1 = new CompoundTag();
         BufferedReader var2 = Files.newReader(this.optionsFile, Charsets.UTF_8);
         Throwable var3 = null;

         try {
            var2.lines().forEach((var1x) -> {
               try {
                  Iterator var2 = OPTION_SPLITTER.split(var1x).iterator();
                  var1.putString((String)var2.next(), (String)var2.next());
               } catch (Exception var3) {
                  LOGGER.warn("Skipping bad option: {}", var1x);
               }

            });
         } catch (Throwable var17) {
            var3 = var17;
            throw var17;
         } finally {
            if (var2 != null) {
               if (var3 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var16) {
                     var3.addSuppressed(var16);
                  }
               } else {
                  var2.close();
               }
            }

         }

         CompoundTag var21 = this.dataFix(var1);
         Iterator var22 = var21.getAllKeys().iterator();

         while(var22.hasNext()) {
            String var4 = (String)var22.next();
            String var5 = var21.getString(var4);

            try {
               if ("autoJump".equals(var4)) {
                  Option.AUTO_JUMP.set(this, var5);
               }

               if ("autoSuggestions".equals(var4)) {
                  Option.AUTO_SUGGESTIONS.set(this, var5);
               }

               if ("chatColors".equals(var4)) {
                  Option.CHAT_COLOR.set(this, var5);
               }

               if ("chatLinks".equals(var4)) {
                  Option.CHAT_LINKS.set(this, var5);
               }

               if ("chatLinksPrompt".equals(var4)) {
                  Option.CHAT_LINKS_PROMPT.set(this, var5);
               }

               if ("enableVsync".equals(var4)) {
                  Option.ENABLE_VSYNC.set(this, var5);
               }

               if ("entityShadows".equals(var4)) {
                  Option.ENTITY_SHADOWS.set(this, var5);
               }

               if ("forceUnicodeFont".equals(var4)) {
                  Option.FORCE_UNICODE_FONT.set(this, var5);
               }

               if ("discrete_mouse_scroll".equals(var4)) {
                  Option.DISCRETE_MOUSE_SCROLL.set(this, var5);
               }

               if ("invertYMouse".equals(var4)) {
                  Option.INVERT_MOUSE.set(this, var5);
               }

               if ("realmsNotifications".equals(var4)) {
                  Option.REALMS_NOTIFICATIONS.set(this, var5);
               }

               if ("reducedDebugInfo".equals(var4)) {
                  Option.REDUCED_DEBUG_INFO.set(this, var5);
               }

               if ("showSubtitles".equals(var4)) {
                  Option.SHOW_SUBTITLES.set(this, var5);
               }

               if ("snooperEnabled".equals(var4)) {
                  Option.SNOOPER_ENABLED.set(this, var5);
               }

               if ("touchscreen".equals(var4)) {
                  Option.TOUCHSCREEN.set(this, var5);
               }

               if ("fullscreen".equals(var4)) {
                  Option.USE_FULLSCREEN.set(this, var5);
               }

               if ("bobView".equals(var4)) {
                  Option.VIEW_BOBBING.set(this, var5);
               }

               if ("toggleCrouch".equals(var4)) {
                  this.toggleCrouch = "true".equals(var5);
               }

               if ("toggleSprint".equals(var4)) {
                  this.toggleSprint = "true".equals(var5);
               }

               if ("mouseSensitivity".equals(var4)) {
                  this.sensitivity = (double)readFloat(var5);
               }

               if ("fov".equals(var4)) {
                  this.fov = (double)(readFloat(var5) * 40.0F + 70.0F);
               }

               if ("gamma".equals(var4)) {
                  this.gamma = (double)readFloat(var5);
               }

               if ("renderDistance".equals(var4)) {
                  this.renderDistance = Integer.parseInt(var5);
               }

               if ("guiScale".equals(var4)) {
                  this.guiScale = Integer.parseInt(var5);
               }

               if ("particles".equals(var4)) {
                  this.particles = ParticleStatus.byId(Integer.parseInt(var5));
               }

               if ("maxFps".equals(var4)) {
                  this.framerateLimit = Integer.parseInt(var5);
                  if (this.minecraft.getWindow() != null) {
                     this.minecraft.getWindow().setFramerateLimit(this.framerateLimit);
                  }
               }

               if ("difficulty".equals(var4)) {
                  this.difficulty = Difficulty.byId(Integer.parseInt(var5));
               }

               if ("fancyGraphics".equals(var4)) {
                  this.fancyGraphics = "true".equals(var5);
               }

               if ("tutorialStep".equals(var4)) {
                  this.tutorialStep = TutorialSteps.getByName(var5);
               }

               if ("ao".equals(var4)) {
                  if ("true".equals(var5)) {
                     this.ambientOcclusion = AmbientOcclusionStatus.MAX;
                  } else if ("false".equals(var5)) {
                     this.ambientOcclusion = AmbientOcclusionStatus.OFF;
                  } else {
                     this.ambientOcclusion = AmbientOcclusionStatus.byId(Integer.parseInt(var5));
                  }
               }

               if ("renderClouds".equals(var4)) {
                  if ("true".equals(var5)) {
                     this.renderClouds = CloudStatus.FANCY;
                  } else if ("false".equals(var5)) {
                     this.renderClouds = CloudStatus.OFF;
                  } else if ("fast".equals(var5)) {
                     this.renderClouds = CloudStatus.FAST;
                  }
               }

               if ("attackIndicator".equals(var4)) {
                  this.attackIndicator = AttackIndicatorStatus.byId(Integer.parseInt(var5));
               }

               if ("resourcePacks".equals(var4)) {
                  this.resourcePacks = (List)GsonHelper.fromJson(GSON, var5, RESOURCE_PACK_TYPE);
                  if (this.resourcePacks == null) {
                     this.resourcePacks = Lists.newArrayList();
                  }
               }

               if ("incompatibleResourcePacks".equals(var4)) {
                  this.incompatibleResourcePacks = (List)GsonHelper.fromJson(GSON, var5, RESOURCE_PACK_TYPE);
                  if (this.incompatibleResourcePacks == null) {
                     this.incompatibleResourcePacks = Lists.newArrayList();
                  }
               }

               if ("lastServer".equals(var4)) {
                  this.lastMpIp = var5;
               }

               if ("lang".equals(var4)) {
                  this.languageCode = var5;
               }

               if ("chatVisibility".equals(var4)) {
                  this.chatVisibility = ChatVisiblity.byId(Integer.parseInt(var5));
               }

               if ("chatOpacity".equals(var4)) {
                  this.chatOpacity = (double)readFloat(var5);
               }

               if ("textBackgroundOpacity".equals(var4)) {
                  this.textBackgroundOpacity = (double)readFloat(var5);
               }

               if ("backgroundForChatOnly".equals(var4)) {
                  this.backgroundForChatOnly = "true".equals(var5);
               }

               if ("fullscreenResolution".equals(var4)) {
                  this.fullscreenVideoModeString = var5;
               }

               if ("hideServerAddress".equals(var4)) {
                  this.hideServerAddress = "true".equals(var5);
               }

               if ("advancedItemTooltips".equals(var4)) {
                  this.advancedItemTooltips = "true".equals(var5);
               }

               if ("pauseOnLostFocus".equals(var4)) {
                  this.pauseOnLostFocus = "true".equals(var5);
               }

               if ("overrideHeight".equals(var4)) {
                  this.overrideHeight = Integer.parseInt(var5);
               }

               if ("overrideWidth".equals(var4)) {
                  this.overrideWidth = Integer.parseInt(var5);
               }

               if ("heldItemTooltips".equals(var4)) {
                  this.heldItemTooltips = "true".equals(var5);
               }

               if ("chatHeightFocused".equals(var4)) {
                  this.chatHeightFocused = (double)readFloat(var5);
               }

               if ("chatHeightUnfocused".equals(var4)) {
                  this.chatHeightUnfocused = (double)readFloat(var5);
               }

               if ("chatScale".equals(var4)) {
                  this.chatScale = (double)readFloat(var5);
               }

               if ("chatWidth".equals(var4)) {
                  this.chatWidth = (double)readFloat(var5);
               }

               if ("mipmapLevels".equals(var4)) {
                  this.mipmapLevels = Integer.parseInt(var5);
               }

               if ("useNativeTransport".equals(var4)) {
                  this.useNativeTransport = "true".equals(var5);
               }

               if ("mainHand".equals(var4)) {
                  this.mainHand = "left".equals(var5) ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
               }

               if ("narrator".equals(var4)) {
                  this.narratorStatus = NarratorStatus.byId(Integer.parseInt(var5));
               }

               if ("biomeBlendRadius".equals(var4)) {
                  this.biomeBlendRadius = Integer.parseInt(var5);
               }

               if ("mouseWheelSensitivity".equals(var4)) {
                  this.mouseWheelSensitivity = (double)readFloat(var5);
               }

               if ("rawMouseInput".equals(var4)) {
                  this.rawMouseInput = "true".equals(var5);
               }

               if ("glDebugVerbosity".equals(var4)) {
                  this.glDebugVerbosity = Integer.parseInt(var5);
               }

               KeyMapping[] var6 = this.keyMappings;
               int var7 = var6.length;

               int var8;
               for(var8 = 0; var8 < var7; ++var8) {
                  KeyMapping var9 = var6[var8];
                  if (var4.equals("key_" + var9.getName())) {
                     var9.setKey(InputConstants.getKey(var5));
                  }
               }

               SoundSource[] var23 = SoundSource.values();
               var7 = var23.length;

               for(var8 = 0; var8 < var7; ++var8) {
                  SoundSource var25 = var23[var8];
                  if (var4.equals("soundCategory_" + var25.getName())) {
                     this.sourceVolumes.put(var25, readFloat(var5));
                  }
               }

               PlayerModelPart[] var24 = PlayerModelPart.values();
               var7 = var24.length;

               for(var8 = 0; var8 < var7; ++var8) {
                  PlayerModelPart var26 = var24[var8];
                  if (var4.equals("modelPart_" + var26.getId())) {
                     this.setModelPart(var26, "true".equals(var5));
                  }
               }
            } catch (Exception var19) {
               LOGGER.warn("Skipping bad option: {}:{}", var4, var5);
            }
         }

         KeyMapping.resetMapping();
      } catch (Exception var20) {
         LOGGER.error("Failed to load options", var20);
      }

   }

   private CompoundTag dataFix(CompoundTag var1) {
      int var2 = 0;

      try {
         var2 = Integer.parseInt(var1.getString("version"));
      } catch (RuntimeException var4) {
      }

      return NbtUtils.update(this.minecraft.getFixerUpper(), DataFixTypes.OPTIONS, var1, var2);
   }

   private static float readFloat(String var0) {
      if ("true".equals(var0)) {
         return 1.0F;
      } else {
         return "false".equals(var0) ? 0.0F : Float.parseFloat(var0);
      }
   }

   public void save() {
      try {
         PrintWriter var1 = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.optionsFile), StandardCharsets.UTF_8));
         Throwable var2 = null;

         try {
            var1.println("version:" + SharedConstants.getCurrentVersion().getWorldVersion());
            var1.println("autoJump:" + Option.AUTO_JUMP.get(this));
            var1.println("autoSuggestions:" + Option.AUTO_SUGGESTIONS.get(this));
            var1.println("chatColors:" + Option.CHAT_COLOR.get(this));
            var1.println("chatLinks:" + Option.CHAT_LINKS.get(this));
            var1.println("chatLinksPrompt:" + Option.CHAT_LINKS_PROMPT.get(this));
            var1.println("enableVsync:" + Option.ENABLE_VSYNC.get(this));
            var1.println("entityShadows:" + Option.ENTITY_SHADOWS.get(this));
            var1.println("forceUnicodeFont:" + Option.FORCE_UNICODE_FONT.get(this));
            var1.println("discrete_mouse_scroll:" + Option.DISCRETE_MOUSE_SCROLL.get(this));
            var1.println("invertYMouse:" + Option.INVERT_MOUSE.get(this));
            var1.println("realmsNotifications:" + Option.REALMS_NOTIFICATIONS.get(this));
            var1.println("reducedDebugInfo:" + Option.REDUCED_DEBUG_INFO.get(this));
            var1.println("snooperEnabled:" + Option.SNOOPER_ENABLED.get(this));
            var1.println("showSubtitles:" + Option.SHOW_SUBTITLES.get(this));
            var1.println("touchscreen:" + Option.TOUCHSCREEN.get(this));
            var1.println("fullscreen:" + Option.USE_FULLSCREEN.get(this));
            var1.println("bobView:" + Option.VIEW_BOBBING.get(this));
            var1.println("toggleCrouch:" + this.toggleCrouch);
            var1.println("toggleSprint:" + this.toggleSprint);
            var1.println("mouseSensitivity:" + this.sensitivity);
            var1.println("fov:" + (this.fov - 70.0D) / 40.0D);
            var1.println("gamma:" + this.gamma);
            var1.println("renderDistance:" + this.renderDistance);
            var1.println("guiScale:" + this.guiScale);
            var1.println("particles:" + this.particles.getId());
            var1.println("maxFps:" + this.framerateLimit);
            var1.println("difficulty:" + this.difficulty.getId());
            var1.println("fancyGraphics:" + this.fancyGraphics);
            var1.println("ao:" + this.ambientOcclusion.getId());
            var1.println("biomeBlendRadius:" + this.biomeBlendRadius);
            switch(this.renderClouds) {
            case FANCY:
               var1.println("renderClouds:true");
               break;
            case FAST:
               var1.println("renderClouds:fast");
               break;
            case OFF:
               var1.println("renderClouds:false");
            }

            var1.println("resourcePacks:" + GSON.toJson(this.resourcePacks));
            var1.println("incompatibleResourcePacks:" + GSON.toJson(this.incompatibleResourcePacks));
            var1.println("lastServer:" + this.lastMpIp);
            var1.println("lang:" + this.languageCode);
            var1.println("chatVisibility:" + this.chatVisibility.getId());
            var1.println("chatOpacity:" + this.chatOpacity);
            var1.println("textBackgroundOpacity:" + this.textBackgroundOpacity);
            var1.println("backgroundForChatOnly:" + this.backgroundForChatOnly);
            if (this.minecraft.getWindow().getPreferredFullscreenVideoMode().isPresent()) {
               var1.println("fullscreenResolution:" + ((VideoMode)this.minecraft.getWindow().getPreferredFullscreenVideoMode().get()).write());
            }

            var1.println("hideServerAddress:" + this.hideServerAddress);
            var1.println("advancedItemTooltips:" + this.advancedItemTooltips);
            var1.println("pauseOnLostFocus:" + this.pauseOnLostFocus);
            var1.println("overrideWidth:" + this.overrideWidth);
            var1.println("overrideHeight:" + this.overrideHeight);
            var1.println("heldItemTooltips:" + this.heldItemTooltips);
            var1.println("chatHeightFocused:" + this.chatHeightFocused);
            var1.println("chatHeightUnfocused:" + this.chatHeightUnfocused);
            var1.println("chatScale:" + this.chatScale);
            var1.println("chatWidth:" + this.chatWidth);
            var1.println("mipmapLevels:" + this.mipmapLevels);
            var1.println("useNativeTransport:" + this.useNativeTransport);
            var1.println("mainHand:" + (this.mainHand == HumanoidArm.LEFT ? "left" : "right"));
            var1.println("attackIndicator:" + this.attackIndicator.getId());
            var1.println("narrator:" + this.narratorStatus.getId());
            var1.println("tutorialStep:" + this.tutorialStep.getName());
            var1.println("mouseWheelSensitivity:" + this.mouseWheelSensitivity);
            var1.println("rawMouseInput:" + Option.RAW_MOUSE_INPUT.get(this));
            var1.println("glDebugVerbosity:" + this.glDebugVerbosity);
            KeyMapping[] var3 = this.keyMappings;
            int var4 = var3.length;

            int var5;
            for(var5 = 0; var5 < var4; ++var5) {
               KeyMapping var6 = var3[var5];
               var1.println("key_" + var6.getName() + ":" + var6.saveString());
            }

            SoundSource[] var18 = SoundSource.values();
            var4 = var18.length;

            for(var5 = 0; var5 < var4; ++var5) {
               SoundSource var20 = var18[var5];
               var1.println("soundCategory_" + var20.getName() + ":" + this.getSoundSourceVolume(var20));
            }

            PlayerModelPart[] var19 = PlayerModelPart.values();
            var4 = var19.length;

            for(var5 = 0; var5 < var4; ++var5) {
               PlayerModelPart var21 = var19[var5];
               var1.println("modelPart_" + var21.getId() + ":" + this.modelParts.contains(var21));
            }
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
      } catch (Exception var17) {
         LOGGER.error("Failed to save options", var17);
      }

      this.broadcastOptions();
   }

   public float getSoundSourceVolume(SoundSource var1) {
      return this.sourceVolumes.containsKey(var1) ? (Float)this.sourceVolumes.get(var1) : 1.0F;
   }

   public void setSoundCategoryVolume(SoundSource var1, float var2) {
      this.sourceVolumes.put(var1, var2);
      this.minecraft.getSoundManager().updateSourceVolume(var1, var2);
   }

   public void broadcastOptions() {
      if (this.minecraft.player != null) {
         int var1 = 0;

         PlayerModelPart var3;
         for(Iterator var2 = this.modelParts.iterator(); var2.hasNext(); var1 |= var3.getMask()) {
            var3 = (PlayerModelPart)var2.next();
         }

         this.minecraft.player.connection.send((Packet)(new ServerboundClientInformationPacket(this.languageCode, this.renderDistance, this.chatVisibility, this.chatColors, var1, this.mainHand)));
      }

   }

   public Set getModelParts() {
      return ImmutableSet.copyOf(this.modelParts);
   }

   public void setModelPart(PlayerModelPart var1, boolean var2) {
      if (var2) {
         this.modelParts.add(var1);
      } else {
         this.modelParts.remove(var1);
      }

      this.broadcastOptions();
   }

   public void toggleModelPart(PlayerModelPart var1) {
      if (this.getModelParts().contains(var1)) {
         this.modelParts.remove(var1);
      } else {
         this.modelParts.add(var1);
      }

      this.broadcastOptions();
   }

   public CloudStatus getCloudsType() {
      return this.renderDistance >= 4 ? this.renderClouds : CloudStatus.OFF;
   }

   public boolean useNativeTransport() {
      return this.useNativeTransport;
   }

   public void loadResourcePacks(PackRepository var1) {
      var1.reload();
      LinkedHashSet var2 = Sets.newLinkedHashSet();
      Iterator var3 = this.resourcePacks.iterator();

      while(true) {
         while(var3.hasNext()) {
            String var4 = (String)var3.next();
            UnopenedResourcePack var5 = (UnopenedResourcePack)var1.getPack(var4);
            if (var5 == null && !var4.startsWith("file/")) {
               var5 = (UnopenedResourcePack)var1.getPack("file/" + var4);
            }

            if (var5 == null) {
               LOGGER.warn("Removed resource pack {} from options because it doesn't seem to exist anymore", var4);
               var3.remove();
            } else if (!var5.getCompatibility().isCompatible() && !this.incompatibleResourcePacks.contains(var4)) {
               LOGGER.warn("Removed resource pack {} from options because it is no longer compatible", var4);
               var3.remove();
            } else if (var5.getCompatibility().isCompatible() && this.incompatibleResourcePacks.contains(var4)) {
               LOGGER.info("Removed resource pack {} from incompatibility list because it's now compatible", var4);
               this.incompatibleResourcePacks.remove(var4);
            } else {
               var2.add(var5);
            }
         }

         var1.setSelected(var2);
         return;
      }
   }
}
