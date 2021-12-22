package net.minecraft.client;

import com.google.common.base.Charsets;
import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.VideoMode;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.tutorial.TutorialSteps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundClientInformationPacket;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.ChatVisiblity;
import net.minecraft.world.entity.player.PlayerModelPart;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Options {
   static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = new Gson();
   private static final TypeToken<List<String>> RESOURCE_PACK_TYPE = new TypeToken<List<String>>() {
   };
   public static final int RENDER_DISTANCE_TINY = 2;
   public static final int RENDER_DISTANCE_SHORT = 4;
   public static final int RENDER_DISTANCE_NORMAL = 8;
   public static final int RENDER_DISTANCE_FAR = 12;
   public static final int RENDER_DISTANCE_REALLY_FAR = 16;
   public static final int RENDER_DISTANCE_EXTREME = 32;
   private static final Splitter OPTION_SPLITTER = Splitter.on(':').limit(2);
   private static final float DEFAULT_VOLUME = 1.0F;
   public static final String DEFAULT_SOUND_DEVICE = "";
   public boolean darkMojangStudiosBackground;
   public boolean hideLightningFlashes;
   public double sensitivity = 0.5D;
   public int renderDistance;
   public int simulationDistance;
   private int serverRenderDistance = 0;
   public float entityDistanceScaling = 1.0F;
   public int framerateLimit = 120;
   public CloudStatus renderClouds;
   public GraphicsStatus graphicsMode;
   public AmbientOcclusionStatus ambientOcclusion;
   public PrioritizeChunkUpdates prioritizeChunkUpdates;
   public List<String> resourcePacks;
   public List<String> incompatibleResourcePacks;
   public ChatVisiblity chatVisibility;
   public double chatOpacity;
   public double chatLineSpacing;
   public double textBackgroundOpacity;
   @Nullable
   public String fullscreenVideoModeString;
   public boolean hideServerAddress;
   public boolean advancedItemTooltips;
   public boolean pauseOnLostFocus;
   private final Set<PlayerModelPart> modelParts;
   public HumanoidArm mainHand;
   public int overrideWidth;
   public int overrideHeight;
   public boolean heldItemTooltips;
   public double chatScale;
   public double chatWidth;
   public double chatHeightUnfocused;
   public double chatHeightFocused;
   public double chatDelay;
   public int mipmapLevels;
   private final Object2FloatMap<SoundSource> sourceVolumes;
   public boolean useNativeTransport;
   public AttackIndicatorStatus attackIndicator;
   public TutorialSteps tutorialStep;
   public boolean joinedFirstServer;
   public boolean hideBundleTutorial;
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
   public boolean allowServerListing;
   public boolean reducedDebugInfo;
   public boolean showSubtitles;
   public boolean backgroundForChatOnly;
   public boolean touchscreen;
   public boolean fullscreen;
   public boolean bobView;
   public boolean toggleCrouch;
   public boolean toggleSprint;
   public boolean skipMultiplayerWarning;
   public boolean hideMatchedNames;
   public boolean showAutosaveIndicator;
   public final KeyMapping keyUp;
   public final KeyMapping keyLeft;
   public final KeyMapping keyDown;
   public final KeyMapping keyRight;
   public final KeyMapping keyJump;
   public final KeyMapping keyShift;
   public final KeyMapping keySprint;
   public final KeyMapping keyInventory;
   public final KeyMapping keySwapOffhand;
   public final KeyMapping keyDrop;
   public final KeyMapping keyUse;
   public final KeyMapping keyAttack;
   public final KeyMapping keyPickItem;
   public final KeyMapping keyChat;
   public final KeyMapping keyPlayerList;
   public final KeyMapping keyCommand;
   public final KeyMapping keySocialInteractions;
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
   private CameraType cameraType;
   public boolean renderDebug;
   public boolean renderDebugCharts;
   public boolean renderFpsChart;
   public String lastMpIp;
   public boolean smoothCamera;
   public double fov;
   public float screenEffectScale;
   public float fovEffectScale;
   public double gamma;
   public int guiScale;
   public ParticleStatus particles;
   public NarratorStatus narratorStatus;
   public String languageCode;
   public String soundDevice;
   public boolean syncWrites;

   public Options(Minecraft var1, File var2) {
      super();
      this.renderClouds = CloudStatus.FANCY;
      this.graphicsMode = GraphicsStatus.FANCY;
      this.ambientOcclusion = AmbientOcclusionStatus.MAX;
      this.prioritizeChunkUpdates = PrioritizeChunkUpdates.NONE;
      this.resourcePacks = Lists.newArrayList();
      this.incompatibleResourcePacks = Lists.newArrayList();
      this.chatVisibility = ChatVisiblity.FULL;
      this.chatOpacity = 1.0D;
      this.textBackgroundOpacity = 0.5D;
      this.pauseOnLostFocus = true;
      this.modelParts = EnumSet.allOf(PlayerModelPart.class);
      this.mainHand = HumanoidArm.RIGHT;
      this.heldItemTooltips = true;
      this.chatScale = 1.0D;
      this.chatWidth = 1.0D;
      this.chatHeightUnfocused = 0.44366195797920227D;
      this.chatHeightFocused = 1.0D;
      this.mipmapLevels = 4;
      this.sourceVolumes = (Object2FloatMap)Util.make(new Object2FloatOpenHashMap(), (var0) -> {
         var0.defaultReturnValue(1.0F);
      });
      this.useNativeTransport = true;
      this.attackIndicator = AttackIndicatorStatus.CROSSHAIR;
      this.tutorialStep = TutorialSteps.MOVEMENT;
      this.joinedFirstServer = false;
      this.hideBundleTutorial = false;
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
      this.allowServerListing = true;
      this.backgroundForChatOnly = true;
      this.bobView = true;
      this.hideMatchedNames = true;
      this.showAutosaveIndicator = true;
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
      this.keySwapOffhand = new KeyMapping("key.swapOffhand", 70, "key.categories.inventory");
      this.keyDrop = new KeyMapping("key.drop", 81, "key.categories.inventory");
      this.keyUse = new KeyMapping("key.use", InputConstants.Type.MOUSE, 1, "key.categories.gameplay");
      this.keyAttack = new KeyMapping("key.attack", InputConstants.Type.MOUSE, 0, "key.categories.gameplay");
      this.keyPickItem = new KeyMapping("key.pickItem", InputConstants.Type.MOUSE, 2, "key.categories.gameplay");
      this.keyChat = new KeyMapping("key.chat", 84, "key.categories.multiplayer");
      this.keyPlayerList = new KeyMapping("key.playerlist", 258, "key.categories.multiplayer");
      this.keyCommand = new KeyMapping("key.command", 47, "key.categories.multiplayer");
      this.keySocialInteractions = new KeyMapping("key.socialInteractions", 80, "key.categories.multiplayer");
      this.keyScreenshot = new KeyMapping("key.screenshot", 291, "key.categories.misc");
      this.keyTogglePerspective = new KeyMapping("key.togglePerspective", 294, "key.categories.misc");
      this.keySmoothCamera = new KeyMapping("key.smoothCamera", InputConstants.UNKNOWN.getValue(), "key.categories.misc");
      this.keyFullscreen = new KeyMapping("key.fullscreen", 300, "key.categories.misc");
      this.keySpectatorOutlines = new KeyMapping("key.spectatorOutlines", InputConstants.UNKNOWN.getValue(), "key.categories.misc");
      this.keyAdvancements = new KeyMapping("key.advancements", 76, "key.categories.misc");
      this.keyHotbarSlots = new KeyMapping[]{new KeyMapping("key.hotbar.1", 49, "key.categories.inventory"), new KeyMapping("key.hotbar.2", 50, "key.categories.inventory"), new KeyMapping("key.hotbar.3", 51, "key.categories.inventory"), new KeyMapping("key.hotbar.4", 52, "key.categories.inventory"), new KeyMapping("key.hotbar.5", 53, "key.categories.inventory"), new KeyMapping("key.hotbar.6", 54, "key.categories.inventory"), new KeyMapping("key.hotbar.7", 55, "key.categories.inventory"), new KeyMapping("key.hotbar.8", 56, "key.categories.inventory"), new KeyMapping("key.hotbar.9", 57, "key.categories.inventory")};
      this.keySaveHotbarActivator = new KeyMapping("key.saveToolbarActivator", 67, "key.categories.creative");
      this.keyLoadHotbarActivator = new KeyMapping("key.loadToolbarActivator", 88, "key.categories.creative");
      this.keyMappings = (KeyMapping[])ArrayUtils.addAll(new KeyMapping[]{this.keyAttack, this.keyUse, this.keyUp, this.keyLeft, this.keyDown, this.keyRight, this.keyJump, this.keyShift, this.keySprint, this.keyDrop, this.keyInventory, this.keyChat, this.keyPlayerList, this.keyPickItem, this.keyCommand, this.keySocialInteractions, this.keyScreenshot, this.keyTogglePerspective, this.keySmoothCamera, this.keyFullscreen, this.keySpectatorOutlines, this.keySwapOffhand, this.keySaveHotbarActivator, this.keyLoadHotbarActivator, this.keyAdvancements}, this.keyHotbarSlots);
      this.difficulty = Difficulty.NORMAL;
      this.cameraType = CameraType.FIRST_PERSON;
      this.lastMpIp = "";
      this.fov = 70.0D;
      this.screenEffectScale = 1.0F;
      this.fovEffectScale = 1.0F;
      this.particles = ParticleStatus.ALL;
      this.narratorStatus = NarratorStatus.OFF;
      this.languageCode = "en_us";
      this.soundDevice = "";
      this.minecraft = var1;
      this.optionsFile = new File(var2, "options.txt");
      if (var1.is64Bit() && Runtime.getRuntime().maxMemory() >= 1000000000L) {
         Option.RENDER_DISTANCE.setMaxValue(32.0F);
         Option.SIMULATION_DISTANCE.setMaxValue(32.0F);
      } else {
         Option.RENDER_DISTANCE.setMaxValue(16.0F);
         Option.SIMULATION_DISTANCE.setMaxValue(16.0F);
      }

      this.renderDistance = var1.is64Bit() ? 12 : 8;
      this.simulationDistance = var1.is64Bit() ? 12 : 8;
      this.gamma = 0.5D;
      this.syncWrites = Util.getPlatform() == Util.class_278.WINDOWS;
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

   private void processOptions(Options.FieldAccess var1) {
      this.autoJump = var1.process("autoJump", this.autoJump);
      this.autoSuggestions = var1.process("autoSuggestions", this.autoSuggestions);
      this.chatColors = var1.process("chatColors", this.chatColors);
      this.chatLinks = var1.process("chatLinks", this.chatLinks);
      this.chatLinksPrompt = var1.process("chatLinksPrompt", this.chatLinksPrompt);
      this.enableVsync = var1.process("enableVsync", this.enableVsync);
      this.entityShadows = var1.process("entityShadows", this.entityShadows);
      this.forceUnicodeFont = var1.process("forceUnicodeFont", this.forceUnicodeFont);
      this.discreteMouseScroll = var1.process("discrete_mouse_scroll", this.discreteMouseScroll);
      this.invertYMouse = var1.process("invertYMouse", this.invertYMouse);
      this.realmsNotifications = var1.process("realmsNotifications", this.realmsNotifications);
      this.reducedDebugInfo = var1.process("reducedDebugInfo", this.reducedDebugInfo);
      this.showSubtitles = var1.process("showSubtitles", this.showSubtitles);
      this.touchscreen = var1.process("touchscreen", this.touchscreen);
      this.fullscreen = var1.process("fullscreen", this.fullscreen);
      this.bobView = var1.process("bobView", this.bobView);
      this.toggleCrouch = var1.process("toggleCrouch", this.toggleCrouch);
      this.toggleSprint = var1.process("toggleSprint", this.toggleSprint);
      this.darkMojangStudiosBackground = var1.process("darkMojangStudiosBackground", this.darkMojangStudiosBackground);
      this.hideLightningFlashes = var1.process("hideLightningFlashes", this.hideLightningFlashes);
      this.sensitivity = var1.process("mouseSensitivity", this.sensitivity);
      this.fov = var1.process("fov", (this.fov - 70.0D) / 40.0D) * 40.0D + 70.0D;
      this.screenEffectScale = var1.process("screenEffectScale", this.screenEffectScale);
      this.fovEffectScale = var1.process("fovEffectScale", this.fovEffectScale);
      this.gamma = var1.process("gamma", this.gamma);
      this.renderDistance = (int)Mth.clamp((double)var1.process("renderDistance", this.renderDistance), Option.RENDER_DISTANCE.getMinValue(), Option.RENDER_DISTANCE.getMaxValue());
      this.simulationDistance = (int)Mth.clamp((double)var1.process("simulationDistance", this.simulationDistance), Option.SIMULATION_DISTANCE.getMinValue(), Option.SIMULATION_DISTANCE.getMaxValue());
      this.entityDistanceScaling = var1.process("entityDistanceScaling", this.entityDistanceScaling);
      this.guiScale = var1.process("guiScale", this.guiScale);
      this.particles = (ParticleStatus)var1.process("particles", this.particles, (IntFunction)(ParticleStatus::byId), (ToIntFunction)(ParticleStatus::getId));
      this.framerateLimit = var1.process("maxFps", this.framerateLimit);
      this.difficulty = (Difficulty)var1.process("difficulty", this.difficulty, (IntFunction)(Difficulty::byId), (ToIntFunction)(Difficulty::getId));
      this.graphicsMode = (GraphicsStatus)var1.process("graphicsMode", this.graphicsMode, (IntFunction)(GraphicsStatus::byId), (ToIntFunction)(GraphicsStatus::getId));
      this.ambientOcclusion = (AmbientOcclusionStatus)var1.process("ao", this.ambientOcclusion, (Function)(Options::readAmbientOcclusion), (Function)((var0) -> {
         return Integer.toString(var0.getId());
      }));
      this.prioritizeChunkUpdates = (PrioritizeChunkUpdates)var1.process("prioritizeChunkUpdates", this.prioritizeChunkUpdates, (IntFunction)(PrioritizeChunkUpdates::byId), (ToIntFunction)(PrioritizeChunkUpdates::getId));
      this.biomeBlendRadius = var1.process("biomeBlendRadius", this.biomeBlendRadius);
      this.renderClouds = (CloudStatus)var1.process("renderClouds", this.renderClouds, (Function)(Options::readCloudStatus), (Function)(Options::writeCloudStatus));
      List var10003 = this.resourcePacks;
      Function var10004 = Options::readPackList;
      Gson var10005 = GSON;
      Objects.requireNonNull(var10005);
      this.resourcePacks = (List)var1.process("resourcePacks", var10003, (Function)var10004, (Function)(var10005::toJson));
      var10003 = this.incompatibleResourcePacks;
      var10004 = Options::readPackList;
      var10005 = GSON;
      Objects.requireNonNull(var10005);
      this.incompatibleResourcePacks = (List)var1.process("incompatibleResourcePacks", var10003, (Function)var10004, (Function)(var10005::toJson));
      this.lastMpIp = var1.process("lastServer", this.lastMpIp);
      this.languageCode = var1.process("lang", this.languageCode);
      this.soundDevice = var1.process("soundDevice", this.soundDevice);
      this.chatVisibility = (ChatVisiblity)var1.process("chatVisibility", this.chatVisibility, (IntFunction)(ChatVisiblity::byId), (ToIntFunction)(ChatVisiblity::getId));
      this.chatOpacity = var1.process("chatOpacity", this.chatOpacity);
      this.chatLineSpacing = var1.process("chatLineSpacing", this.chatLineSpacing);
      this.textBackgroundOpacity = var1.process("textBackgroundOpacity", this.textBackgroundOpacity);
      this.backgroundForChatOnly = var1.process("backgroundForChatOnly", this.backgroundForChatOnly);
      this.hideServerAddress = var1.process("hideServerAddress", this.hideServerAddress);
      this.advancedItemTooltips = var1.process("advancedItemTooltips", this.advancedItemTooltips);
      this.pauseOnLostFocus = var1.process("pauseOnLostFocus", this.pauseOnLostFocus);
      this.overrideWidth = var1.process("overrideWidth", this.overrideWidth);
      this.overrideHeight = var1.process("overrideHeight", this.overrideHeight);
      this.heldItemTooltips = var1.process("heldItemTooltips", this.heldItemTooltips);
      this.chatHeightFocused = var1.process("chatHeightFocused", this.chatHeightFocused);
      this.chatDelay = var1.process("chatDelay", this.chatDelay);
      this.chatHeightUnfocused = var1.process("chatHeightUnfocused", this.chatHeightUnfocused);
      this.chatScale = var1.process("chatScale", this.chatScale);
      this.chatWidth = var1.process("chatWidth", this.chatWidth);
      this.mipmapLevels = var1.process("mipmapLevels", this.mipmapLevels);
      this.useNativeTransport = var1.process("useNativeTransport", this.useNativeTransport);
      this.mainHand = (HumanoidArm)var1.process("mainHand", this.mainHand, (Function)(Options::readMainHand), (Function)(Options::writeMainHand));
      this.attackIndicator = (AttackIndicatorStatus)var1.process("attackIndicator", this.attackIndicator, (IntFunction)(AttackIndicatorStatus::byId), (ToIntFunction)(AttackIndicatorStatus::getId));
      this.narratorStatus = (NarratorStatus)var1.process("narrator", this.narratorStatus, (IntFunction)(NarratorStatus::byId), (ToIntFunction)(NarratorStatus::getId));
      this.tutorialStep = (TutorialSteps)var1.process("tutorialStep", this.tutorialStep, (Function)(TutorialSteps::getByName), (Function)(TutorialSteps::getName));
      this.mouseWheelSensitivity = var1.process("mouseWheelSensitivity", this.mouseWheelSensitivity);
      this.rawMouseInput = var1.process("rawMouseInput", this.rawMouseInput);
      this.glDebugVerbosity = var1.process("glDebugVerbosity", this.glDebugVerbosity);
      this.skipMultiplayerWarning = var1.process("skipMultiplayerWarning", this.skipMultiplayerWarning);
      this.hideMatchedNames = var1.process("hideMatchedNames", this.hideMatchedNames);
      this.joinedFirstServer = var1.process("joinedFirstServer", this.joinedFirstServer);
      this.hideBundleTutorial = var1.process("hideBundleTutorial", this.hideBundleTutorial);
      this.syncWrites = var1.process("syncChunkWrites", this.syncWrites);
      this.showAutosaveIndicator = var1.process("showAutosaveIndicator", this.showAutosaveIndicator);
      this.allowServerListing = var1.process("allowServerListing", this.allowServerListing);
      KeyMapping[] var2 = this.keyMappings;
      int var3 = var2.length;

      int var4;
      for(var4 = 0; var4 < var3; ++var4) {
         KeyMapping var5 = var2[var4];
         String var6 = var5.saveString();
         String var7 = var1.process("key_" + var5.getName(), var6);
         if (!var6.equals(var7)) {
            var5.setKey(InputConstants.getKey(var7));
         }
      }

      SoundSource[] var8 = SoundSource.values();
      var3 = var8.length;

      for(var4 = 0; var4 < var3; ++var4) {
         SoundSource var10 = var8[var4];
         this.sourceVolumes.computeFloat(var10, (var1x, var2x) -> {
            return var1.process("soundCategory_" + var1x.getName(), var2x != null ? var2x : 1.0F);
         });
      }

      PlayerModelPart[] var9 = PlayerModelPart.values();
      var3 = var9.length;

      for(var4 = 0; var4 < var3; ++var4) {
         PlayerModelPart var11 = var9[var4];
         boolean var12 = this.modelParts.contains(var11);
         boolean var13 = var1.process("modelPart_" + var11.getId(), var12);
         if (var13 != var12) {
            this.setModelPart(var11, var13);
         }
      }

   }

   public void load() {
      try {
         if (!this.optionsFile.exists()) {
            return;
         }

         this.sourceVolumes.clear();
         CompoundTag var1 = new CompoundTag();
         BufferedReader var2 = Files.newReader(this.optionsFile, Charsets.UTF_8);

         try {
            var2.lines().forEach((var1x) -> {
               try {
                  Iterator var2 = OPTION_SPLITTER.split(var1x).iterator();
                  var1.putString((String)var2.next(), (String)var2.next());
               } catch (Exception var3) {
                  LOGGER.warn("Skipping bad option: {}", var1x);
               }

            });
         } catch (Throwable var6) {
            if (var2 != null) {
               try {
                  var2.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (var2 != null) {
            var2.close();
         }

         final CompoundTag var8 = this.dataFix(var1);
         if (!var8.contains("graphicsMode") && var8.contains("fancyGraphics")) {
            if (isTrue(var8.getString("fancyGraphics"))) {
               this.graphicsMode = GraphicsStatus.FANCY;
            } else {
               this.graphicsMode = GraphicsStatus.FAST;
            }
         }

         this.processOptions(new Options.FieldAccess() {
            @Nullable
            private String getValueOrNull(String var1) {
               return var8.contains(var1) ? var8.getString(var1) : null;
            }

            public int process(String var1, int var2) {
               String var3 = this.getValueOrNull(var1);
               if (var3 != null) {
                  try {
                     return Integer.parseInt(var3);
                  } catch (NumberFormatException var5) {
                     Options.LOGGER.warn("Invalid integer value for option {} = {}", var1, var3, var5);
                  }
               }

               return var2;
            }

            public boolean process(String var1, boolean var2) {
               String var3 = this.getValueOrNull(var1);
               return var3 != null ? Options.isTrue(var3) : var2;
            }

            public String process(String var1, String var2) {
               return (String)MoreObjects.firstNonNull(this.getValueOrNull(var1), var2);
            }

            public double process(String var1, double var2) {
               String var4 = this.getValueOrNull(var1);
               if (var4 != null) {
                  if (Options.isTrue(var4)) {
                     return 1.0D;
                  }

                  if (Options.isFalse(var4)) {
                     return 0.0D;
                  }

                  try {
                     return Double.parseDouble(var4);
                  } catch (NumberFormatException var6) {
                     Options.LOGGER.warn("Invalid floating point value for option {} = {}", var1, var4, var6);
                  }
               }

               return var2;
            }

            public float process(String var1, float var2) {
               String var3 = this.getValueOrNull(var1);
               if (var3 != null) {
                  if (Options.isTrue(var3)) {
                     return 1.0F;
                  }

                  if (Options.isFalse(var3)) {
                     return 0.0F;
                  }

                  try {
                     return Float.parseFloat(var3);
                  } catch (NumberFormatException var5) {
                     Options.LOGGER.warn("Invalid floating point value for option {} = {}", var1, var3, var5);
                  }
               }

               return var2;
            }

            public <T> T process(String var1, T var2, Function<String, T> var3, Function<T, String> var4) {
               String var5 = this.getValueOrNull(var1);
               return var5 == null ? var2 : var3.apply(var5);
            }

            public <T> T process(String var1, T var2, IntFunction<T> var3, ToIntFunction<T> var4) {
               String var5 = this.getValueOrNull(var1);
               if (var5 != null) {
                  try {
                     return var3.apply(Integer.parseInt(var5));
                  } catch (Exception var7) {
                     Options.LOGGER.warn("Invalid integer value for option {} = {}", var1, var5, var7);
                  }
               }

               return var2;
            }
         });
         if (var8.contains("fullscreenResolution")) {
            this.fullscreenVideoModeString = var8.getString("fullscreenResolution");
         }

         if (this.minecraft.getWindow() != null) {
            this.minecraft.getWindow().setFramerateLimit(this.framerateLimit);
         }

         KeyMapping.resetMapping();
      } catch (Exception var7) {
         LOGGER.error("Failed to load options", var7);
      }

   }

   static boolean isTrue(String var0) {
      return "true".equals(var0);
   }

   static boolean isFalse(String var0) {
      return "false".equals(var0);
   }

   private CompoundTag dataFix(CompoundTag var1) {
      int var2 = 0;

      try {
         var2 = Integer.parseInt(var1.getString("version"));
      } catch (RuntimeException var4) {
      }

      return NbtUtils.update(this.minecraft.getFixerUpper(), DataFixTypes.OPTIONS, var1, var2);
   }

   public void save() {
      try {
         final PrintWriter var1 = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.optionsFile), StandardCharsets.UTF_8));

         try {
            var1.println("version:" + SharedConstants.getCurrentVersion().getWorldVersion());
            this.processOptions(new Options.FieldAccess() {
               public void writePrefix(String var1x) {
                  var1.print(var1x);
                  var1.print(':');
               }

               public int process(String var1x, int var2) {
                  this.writePrefix(var1x);
                  var1.println(var2);
                  return var2;
               }

               public boolean process(String var1x, boolean var2) {
                  this.writePrefix(var1x);
                  var1.println(var2);
                  return var2;
               }

               public String process(String var1x, String var2) {
                  this.writePrefix(var1x);
                  var1.println(var2);
                  return var2;
               }

               public double process(String var1x, double var2) {
                  this.writePrefix(var1x);
                  var1.println(var2);
                  return var2;
               }

               public float process(String var1x, float var2) {
                  this.writePrefix(var1x);
                  var1.println(var2);
                  return var2;
               }

               public <T> T process(String var1x, T var2, Function<String, T> var3, Function<T, String> var4) {
                  this.writePrefix(var1x);
                  var1.println((String)var4.apply(var2));
                  return var2;
               }

               public <T> T process(String var1x, T var2, IntFunction<T> var3, ToIntFunction<T> var4) {
                  this.writePrefix(var1x);
                  var1.println(var4.applyAsInt(var2));
                  return var2;
               }
            });
            if (this.minecraft.getWindow().getPreferredFullscreenVideoMode().isPresent()) {
               var1.println("fullscreenResolution:" + ((VideoMode)this.minecraft.getWindow().getPreferredFullscreenVideoMode().get()).write());
            }
         } catch (Throwable var5) {
            try {
               var1.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }

            throw var5;
         }

         var1.close();
      } catch (Exception var6) {
         LOGGER.error("Failed to save options", var6);
      }

      this.broadcastOptions();
   }

   public float getSoundSourceVolume(SoundSource var1) {
      return this.sourceVolumes.getFloat(var1);
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

         this.minecraft.player.connection.send((Packet)(new ServerboundClientInformationPacket(this.languageCode, this.renderDistance, this.chatVisibility, this.chatColors, var1, this.mainHand, this.minecraft.isTextFilteringEnabled(), this.allowServerListing)));
      }

   }

   private void setModelPart(PlayerModelPart var1, boolean var2) {
      if (var2) {
         this.modelParts.add(var1);
      } else {
         this.modelParts.remove(var1);
      }

   }

   public boolean isModelPartEnabled(PlayerModelPart var1) {
      return this.modelParts.contains(var1);
   }

   public void toggleModelPart(PlayerModelPart var1, boolean var2) {
      this.setModelPart(var1, var2);
      this.broadcastOptions();
   }

   public CloudStatus getCloudsType() {
      return this.getEffectiveRenderDistance() >= 4 ? this.renderClouds : CloudStatus.OFF;
   }

   public boolean useNativeTransport() {
      return this.useNativeTransport;
   }

   public void loadSelectedResourcePacks(PackRepository var1) {
      LinkedHashSet var2 = Sets.newLinkedHashSet();
      Iterator var3 = this.resourcePacks.iterator();

      while(true) {
         while(var3.hasNext()) {
            String var4 = (String)var3.next();
            Pack var5 = var1.getPack(var4);
            if (var5 == null && !var4.startsWith("file/")) {
               var5 = var1.getPack("file/" + var4);
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
               var2.add(var5.getId());
            }
         }

         var1.setSelected(var2);
         return;
      }
   }

   public CameraType getCameraType() {
      return this.cameraType;
   }

   public void setCameraType(CameraType var1) {
      this.cameraType = var1;
   }

   private static List<String> readPackList(String var0) {
      List var1 = (List)GsonHelper.fromJson(GSON, var0, RESOURCE_PACK_TYPE);
      return (List)(var1 != null ? var1 : Lists.newArrayList());
   }

   private static CloudStatus readCloudStatus(String var0) {
      byte var2 = -1;
      switch(var0.hashCode()) {
      case 3135580:
         if (var0.equals("fast")) {
            var2 = 1;
         }
         break;
      case 3569038:
         if (var0.equals("true")) {
            var2 = 0;
         }
         break;
      case 97196323:
         if (var0.equals("false")) {
            var2 = 2;
         }
      }

      switch(var2) {
      case 0:
         return CloudStatus.FANCY;
      case 1:
         return CloudStatus.FAST;
      case 2:
      default:
         return CloudStatus.OFF;
      }
   }

   private static String writeCloudStatus(CloudStatus var0) {
      switch(var0) {
      case FANCY:
         return "true";
      case FAST:
         return "fast";
      case OFF:
      default:
         return "false";
      }
   }

   private static AmbientOcclusionStatus readAmbientOcclusion(String var0) {
      if (isTrue(var0)) {
         return AmbientOcclusionStatus.MAX;
      } else {
         return isFalse(var0) ? AmbientOcclusionStatus.OFF : AmbientOcclusionStatus.byId(Integer.parseInt(var0));
      }
   }

   private static HumanoidArm readMainHand(String var0) {
      return "left".equals(var0) ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
   }

   private static String writeMainHand(HumanoidArm var0) {
      return var0 == HumanoidArm.LEFT ? "left" : "right";
   }

   public File getFile() {
      return this.optionsFile;
   }

   public String dumpOptionsForReport() {
      ImmutableList var1 = ImmutableList.builder().add(Pair.of("ao", String.valueOf(this.ambientOcclusion))).add(Pair.of("biomeBlendRadius", String.valueOf(this.biomeBlendRadius))).add(Pair.of("enableVsync", String.valueOf(this.enableVsync))).add(Pair.of("entityDistanceScaling", String.valueOf(this.entityDistanceScaling))).add(Pair.of("entityShadows", String.valueOf(this.entityShadows))).add(Pair.of("forceUnicodeFont", String.valueOf(this.forceUnicodeFont))).add(Pair.of("fov", String.valueOf(this.fov))).add(Pair.of("fovEffectScale", String.valueOf(this.fovEffectScale))).add(Pair.of("prioritizeChunkUpdates", String.valueOf(this.prioritizeChunkUpdates))).add(Pair.of("fullscreen", String.valueOf(this.fullscreen))).add(Pair.of("fullscreenResolution", String.valueOf(this.fullscreenVideoModeString))).add(Pair.of("gamma", String.valueOf(this.gamma))).add(Pair.of("glDebugVerbosity", String.valueOf(this.glDebugVerbosity))).add(Pair.of("graphicsMode", String.valueOf(this.graphicsMode))).add(Pair.of("guiScale", String.valueOf(this.guiScale))).add(Pair.of("maxFps", String.valueOf(this.framerateLimit))).add(Pair.of("mipmapLevels", String.valueOf(this.mipmapLevels))).add(Pair.of("narrator", String.valueOf(this.narratorStatus))).add(Pair.of("overrideHeight", String.valueOf(this.overrideHeight))).add(Pair.of("overrideWidth", String.valueOf(this.overrideWidth))).add(Pair.of("particles", String.valueOf(this.particles))).add(Pair.of("reducedDebugInfo", String.valueOf(this.reducedDebugInfo))).add(Pair.of("renderClouds", String.valueOf(this.renderClouds))).add(Pair.of("renderDistance", String.valueOf(this.renderDistance))).add(Pair.of("simulationDistance", String.valueOf(this.simulationDistance))).add(Pair.of("resourcePacks", String.valueOf(this.resourcePacks))).add(Pair.of("screenEffectScale", String.valueOf(this.screenEffectScale))).add(Pair.of("syncChunkWrites", String.valueOf(this.syncWrites))).add(Pair.of("useNativeTransport", String.valueOf(this.useNativeTransport))).add(Pair.of("soundDevice", String.valueOf(this.soundDevice))).build();
      return (String)var1.stream().map((var0) -> {
         String var10000 = (String)var0.getFirst();
         return var10000 + ": " + (String)var0.getSecond();
      }).collect(Collectors.joining(System.lineSeparator()));
   }

   public void setServerRenderDistance(int var1) {
      this.serverRenderDistance = var1;
   }

   public int getEffectiveRenderDistance() {
      return this.serverRenderDistance > 0 ? Math.min(this.renderDistance, this.serverRenderDistance) : this.renderDistance;
   }

   interface FieldAccess {
      int process(String var1, int var2);

      boolean process(String var1, boolean var2);

      String process(String var1, String var2);

      double process(String var1, double var2);

      float process(String var1, float var2);

      <T> T process(String var1, T var2, Function<String, T> var3, Function<T, String> var4);

      <T> T process(String var1, T var2, IntFunction<T> var3, ToIntFunction<T> var4);
   }
}
