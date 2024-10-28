package net.minecraft.client;

import com.google.common.base.Charsets;
import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.VideoMode;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.GpuWarnlistManager;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.tutorial.TutorialSteps;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.common.ServerboundClientInformationPacket;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.ChatVisiblity;
import net.minecraft.world.entity.player.PlayerModelPart;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;

public class Options {
   static final Logger LOGGER = LogUtils.getLogger();
   static final Gson GSON = new Gson();
   private static final TypeToken<List<String>> LIST_OF_STRINGS_TYPE = new TypeToken<List<String>>() {
   };
   public static final int RENDER_DISTANCE_TINY = 2;
   public static final int RENDER_DISTANCE_SHORT = 4;
   public static final int RENDER_DISTANCE_NORMAL = 8;
   public static final int RENDER_DISTANCE_FAR = 12;
   public static final int RENDER_DISTANCE_REALLY_FAR = 16;
   public static final int RENDER_DISTANCE_EXTREME = 32;
   private static final Splitter OPTION_SPLITTER = Splitter.on(':').limit(2);
   public static final String DEFAULT_SOUND_DEVICE = "";
   private static final Component ACCESSIBILITY_TOOLTIP_DARK_MOJANG_BACKGROUND = Component.translatable("options.darkMojangStudiosBackgroundColor.tooltip");
   private final OptionInstance<Boolean> darkMojangStudiosBackground;
   private static final Component ACCESSIBILITY_TOOLTIP_HIDE_LIGHTNING_FLASHES = Component.translatable("options.hideLightningFlashes.tooltip");
   private final OptionInstance<Boolean> hideLightningFlash;
   private static final Component ACCESSIBILITY_TOOLTIP_HIDE_SPLASH_TEXTS = Component.translatable("options.hideSplashTexts.tooltip");
   private final OptionInstance<Boolean> hideSplashTexts;
   private final OptionInstance<Double> sensitivity;
   private final OptionInstance<Integer> renderDistance;
   private final OptionInstance<Integer> simulationDistance;
   private int serverRenderDistance;
   private final OptionInstance<Double> entityDistanceScaling;
   public static final int UNLIMITED_FRAMERATE_CUTOFF = 260;
   private final OptionInstance<Integer> framerateLimit;
   private final OptionInstance<CloudStatus> cloudStatus;
   private static final Component GRAPHICS_TOOLTIP_FAST = Component.translatable("options.graphics.fast.tooltip");
   private static final Component GRAPHICS_TOOLTIP_FABULOUS;
   private static final Component GRAPHICS_TOOLTIP_FANCY;
   private final OptionInstance<GraphicsStatus> graphicsMode;
   private final OptionInstance<Boolean> ambientOcclusion;
   private static final Component PRIORITIZE_CHUNK_TOOLTIP_NONE;
   private static final Component PRIORITIZE_CHUNK_TOOLTIP_PLAYER_AFFECTED;
   private static final Component PRIORITIZE_CHUNK_TOOLTIP_NEARBY;
   private final OptionInstance<PrioritizeChunkUpdates> prioritizeChunkUpdates;
   public List<String> resourcePacks;
   public List<String> incompatibleResourcePacks;
   private final OptionInstance<ChatVisiblity> chatVisibility;
   private final OptionInstance<Double> chatOpacity;
   private final OptionInstance<Double> chatLineSpacing;
   private static final Component MENU_BACKGROUND_BLURRINESS_TOOLTIP;
   private static final double BLURRINESS_DEFAULT_VALUE = 0.5;
   private final OptionInstance<Double> menuBackgroundBlurriness;
   private final OptionInstance<Double> textBackgroundOpacity;
   private final OptionInstance<Double> panoramaSpeed;
   private static final Component ACCESSIBILITY_TOOLTIP_CONTRAST_MODE;
   private final OptionInstance<Boolean> highContrast;
   private final OptionInstance<Boolean> narratorHotkey;
   @Nullable
   public String fullscreenVideoModeString;
   public boolean hideServerAddress;
   public boolean advancedItemTooltips;
   public boolean pauseOnLostFocus;
   private final Set<PlayerModelPart> modelParts;
   private final OptionInstance<HumanoidArm> mainHand;
   public int overrideWidth;
   public int overrideHeight;
   private final OptionInstance<Double> chatScale;
   private final OptionInstance<Double> chatWidth;
   private final OptionInstance<Double> chatHeightUnfocused;
   private final OptionInstance<Double> chatHeightFocused;
   private final OptionInstance<Double> chatDelay;
   private static final Component ACCESSIBILITY_TOOLTIP_NOTIFICATION_DISPLAY_TIME;
   private final OptionInstance<Double> notificationDisplayTime;
   private final OptionInstance<Integer> mipmapLevels;
   public boolean useNativeTransport;
   private final OptionInstance<AttackIndicatorStatus> attackIndicator;
   public TutorialSteps tutorialStep;
   public boolean joinedFirstServer;
   public boolean hideBundleTutorial;
   private final OptionInstance<Integer> biomeBlendRadius;
   private final OptionInstance<Double> mouseWheelSensitivity;
   private final OptionInstance<Boolean> rawMouseInput;
   public int glDebugVerbosity;
   private final OptionInstance<Boolean> autoJump;
   private final OptionInstance<Boolean> operatorItemsTab;
   private final OptionInstance<Boolean> autoSuggestions;
   private final OptionInstance<Boolean> chatColors;
   private final OptionInstance<Boolean> chatLinks;
   private final OptionInstance<Boolean> chatLinksPrompt;
   private final OptionInstance<Boolean> enableVsync;
   private final OptionInstance<Boolean> entityShadows;
   private final OptionInstance<Boolean> forceUnicodeFont;
   private final OptionInstance<Boolean> japaneseGlyphVariants;
   private final OptionInstance<Boolean> invertYMouse;
   private final OptionInstance<Boolean> discreteMouseScroll;
   private final OptionInstance<Boolean> realmsNotifications;
   private static final Component ALLOW_SERVER_LISTING_TOOLTIP;
   private final OptionInstance<Boolean> allowServerListing;
   private final OptionInstance<Boolean> reducedDebugInfo;
   private final Map<SoundSource, OptionInstance<Double>> soundSourceVolumes;
   private final OptionInstance<Boolean> showSubtitles;
   private static final Component DIRECTIONAL_AUDIO_TOOLTIP_ON;
   private static final Component DIRECTIONAL_AUDIO_TOOLTIP_OFF;
   private final OptionInstance<Boolean> directionalAudio;
   private final OptionInstance<Boolean> backgroundForChatOnly;
   private final OptionInstance<Boolean> touchscreen;
   private final OptionInstance<Boolean> fullscreen;
   private final OptionInstance<Boolean> bobView;
   private static final Component MOVEMENT_TOGGLE;
   private static final Component MOVEMENT_HOLD;
   private final OptionInstance<Boolean> toggleCrouch;
   private final OptionInstance<Boolean> toggleSprint;
   public boolean skipMultiplayerWarning;
   private static final Component CHAT_TOOLTIP_HIDE_MATCHED_NAMES;
   private final OptionInstance<Boolean> hideMatchedNames;
   private final OptionInstance<Boolean> showAutosaveIndicator;
   private static final Component CHAT_TOOLTIP_ONLY_SHOW_SECURE;
   private final OptionInstance<Boolean> onlyShowSecureChat;
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
   public boolean hideGui;
   private CameraType cameraType;
   public String lastMpIp;
   public boolean smoothCamera;
   private final OptionInstance<Integer> fov;
   private static final Component TELEMETRY_TOOLTIP;
   private final OptionInstance<Boolean> telemetryOptInExtra;
   private static final Component ACCESSIBILITY_TOOLTIP_SCREEN_EFFECT;
   private final OptionInstance<Double> screenEffectScale;
   private static final Component ACCESSIBILITY_TOOLTIP_FOV_EFFECT;
   private final OptionInstance<Double> fovEffectScale;
   private static final Component ACCESSIBILITY_TOOLTIP_DARKNESS_EFFECT;
   private final OptionInstance<Double> darknessEffectScale;
   private static final Component ACCESSIBILITY_TOOLTIP_GLINT_SPEED;
   private final OptionInstance<Double> glintSpeed;
   private static final Component ACCESSIBILITY_TOOLTIP_GLINT_STRENGTH;
   private final OptionInstance<Double> glintStrength;
   private static final Component ACCESSIBILITY_TOOLTIP_DAMAGE_TILT_STRENGTH;
   private final OptionInstance<Double> damageTiltStrength;
   private final OptionInstance<Double> gamma;
   public static final int AUTO_GUI_SCALE = 0;
   private static final int MAX_GUI_SCALE_INCLUSIVE = 2147483646;
   private final OptionInstance<Integer> guiScale;
   private final OptionInstance<ParticleStatus> particles;
   private final OptionInstance<NarratorStatus> narrator;
   public String languageCode;
   private final OptionInstance<String> soundDevice;
   public boolean onboardAccessibility;
   public boolean syncWrites;

   public OptionInstance<Boolean> darkMojangStudiosBackground() {
      return this.darkMojangStudiosBackground;
   }

   public OptionInstance<Boolean> hideLightningFlash() {
      return this.hideLightningFlash;
   }

   public OptionInstance<Boolean> hideSplashTexts() {
      return this.hideSplashTexts;
   }

   public OptionInstance<Double> sensitivity() {
      return this.sensitivity;
   }

   public OptionInstance<Integer> renderDistance() {
      return this.renderDistance;
   }

   public OptionInstance<Integer> simulationDistance() {
      return this.simulationDistance;
   }

   public OptionInstance<Double> entityDistanceScaling() {
      return this.entityDistanceScaling;
   }

   public OptionInstance<Integer> framerateLimit() {
      return this.framerateLimit;
   }

   public OptionInstance<CloudStatus> cloudStatus() {
      return this.cloudStatus;
   }

   public OptionInstance<GraphicsStatus> graphicsMode() {
      return this.graphicsMode;
   }

   public OptionInstance<Boolean> ambientOcclusion() {
      return this.ambientOcclusion;
   }

   public OptionInstance<PrioritizeChunkUpdates> prioritizeChunkUpdates() {
      return this.prioritizeChunkUpdates;
   }

   public void updateResourcePacks(PackRepository var1) {
      ImmutableList var2 = ImmutableList.copyOf(this.resourcePacks);
      this.resourcePacks.clear();
      this.incompatibleResourcePacks.clear();
      Iterator var3 = var1.getSelectedPacks().iterator();

      while(var3.hasNext()) {
         Pack var4 = (Pack)var3.next();
         if (!var4.isFixedPosition()) {
            this.resourcePacks.add(var4.getId());
            if (!var4.getCompatibility().isCompatible()) {
               this.incompatibleResourcePacks.add(var4.getId());
            }
         }
      }

      this.save();
      ImmutableList var5 = ImmutableList.copyOf(this.resourcePacks);
      if (!var5.equals(var2)) {
         this.minecraft.reloadResourcePacks();
      }

   }

   public OptionInstance<ChatVisiblity> chatVisibility() {
      return this.chatVisibility;
   }

   public OptionInstance<Double> chatOpacity() {
      return this.chatOpacity;
   }

   public OptionInstance<Double> chatLineSpacing() {
      return this.chatLineSpacing;
   }

   public OptionInstance<Double> menuBackgroundBlurriness() {
      return this.menuBackgroundBlurriness;
   }

   public double getMenuBackgroundBlurriness() {
      return (Double)this.menuBackgroundBlurriness().get();
   }

   public OptionInstance<Double> textBackgroundOpacity() {
      return this.textBackgroundOpacity;
   }

   public OptionInstance<Double> panoramaSpeed() {
      return this.panoramaSpeed;
   }

   public OptionInstance<Boolean> highContrast() {
      return this.highContrast;
   }

   public OptionInstance<Boolean> narratorHotkey() {
      return this.narratorHotkey;
   }

   public OptionInstance<HumanoidArm> mainHand() {
      return this.mainHand;
   }

   public OptionInstance<Double> chatScale() {
      return this.chatScale;
   }

   public OptionInstance<Double> chatWidth() {
      return this.chatWidth;
   }

   public OptionInstance<Double> chatHeightUnfocused() {
      return this.chatHeightUnfocused;
   }

   public OptionInstance<Double> chatHeightFocused() {
      return this.chatHeightFocused;
   }

   public OptionInstance<Double> chatDelay() {
      return this.chatDelay;
   }

   public OptionInstance<Double> notificationDisplayTime() {
      return this.notificationDisplayTime;
   }

   public OptionInstance<Integer> mipmapLevels() {
      return this.mipmapLevels;
   }

   public OptionInstance<AttackIndicatorStatus> attackIndicator() {
      return this.attackIndicator;
   }

   public OptionInstance<Integer> biomeBlendRadius() {
      return this.biomeBlendRadius;
   }

   private static double logMouse(int var0) {
      return Math.pow(10.0, (double)var0 / 100.0);
   }

   private static int unlogMouse(double var0) {
      return Mth.floor(Math.log10(var0) * 100.0);
   }

   public OptionInstance<Double> mouseWheelSensitivity() {
      return this.mouseWheelSensitivity;
   }

   public OptionInstance<Boolean> rawMouseInput() {
      return this.rawMouseInput;
   }

   public OptionInstance<Boolean> autoJump() {
      return this.autoJump;
   }

   public OptionInstance<Boolean> operatorItemsTab() {
      return this.operatorItemsTab;
   }

   public OptionInstance<Boolean> autoSuggestions() {
      return this.autoSuggestions;
   }

   public OptionInstance<Boolean> chatColors() {
      return this.chatColors;
   }

   public OptionInstance<Boolean> chatLinks() {
      return this.chatLinks;
   }

   public OptionInstance<Boolean> chatLinksPrompt() {
      return this.chatLinksPrompt;
   }

   public OptionInstance<Boolean> enableVsync() {
      return this.enableVsync;
   }

   public OptionInstance<Boolean> entityShadows() {
      return this.entityShadows;
   }

   private static void updateFontOptions() {
      Minecraft var0 = Minecraft.getInstance();
      if (var0.getWindow() != null) {
         var0.updateFontOptions();
         var0.resizeDisplay();
      }

   }

   public OptionInstance<Boolean> forceUnicodeFont() {
      return this.forceUnicodeFont;
   }

   private static boolean japaneseGlyphVariantsDefault() {
      return Locale.getDefault().getLanguage().equalsIgnoreCase("ja");
   }

   public OptionInstance<Boolean> japaneseGlyphVariants() {
      return this.japaneseGlyphVariants;
   }

   public OptionInstance<Boolean> invertYMouse() {
      return this.invertYMouse;
   }

   public OptionInstance<Boolean> discreteMouseScroll() {
      return this.discreteMouseScroll;
   }

   public OptionInstance<Boolean> realmsNotifications() {
      return this.realmsNotifications;
   }

   public OptionInstance<Boolean> allowServerListing() {
      return this.allowServerListing;
   }

   public OptionInstance<Boolean> reducedDebugInfo() {
      return this.reducedDebugInfo;
   }

   public final float getSoundSourceVolume(SoundSource var1) {
      return ((Double)this.getSoundSourceOptionInstance(var1).get()).floatValue();
   }

   public final OptionInstance<Double> getSoundSourceOptionInstance(SoundSource var1) {
      return (OptionInstance)Objects.requireNonNull((OptionInstance)this.soundSourceVolumes.get(var1));
   }

   private OptionInstance<Double> createSoundSliderOptionInstance(String var1, SoundSource var2) {
      return new OptionInstance(var1, OptionInstance.noTooltip(), (var0, var1x) -> {
         return var1x == 0.0 ? genericValueLabel(var0, CommonComponents.OPTION_OFF) : percentValueLabel(var0, var1x);
      }, OptionInstance.UnitDouble.INSTANCE, 1.0, (var1x) -> {
         Minecraft.getInstance().getSoundManager().updateSourceVolume(var2, var1x.floatValue());
      });
   }

   public OptionInstance<Boolean> showSubtitles() {
      return this.showSubtitles;
   }

   public OptionInstance<Boolean> directionalAudio() {
      return this.directionalAudio;
   }

   public OptionInstance<Boolean> backgroundForChatOnly() {
      return this.backgroundForChatOnly;
   }

   public OptionInstance<Boolean> touchscreen() {
      return this.touchscreen;
   }

   public OptionInstance<Boolean> fullscreen() {
      return this.fullscreen;
   }

   public OptionInstance<Boolean> bobView() {
      return this.bobView;
   }

   public OptionInstance<Boolean> toggleCrouch() {
      return this.toggleCrouch;
   }

   public OptionInstance<Boolean> toggleSprint() {
      return this.toggleSprint;
   }

   public OptionInstance<Boolean> hideMatchedNames() {
      return this.hideMatchedNames;
   }

   public OptionInstance<Boolean> showAutosaveIndicator() {
      return this.showAutosaveIndicator;
   }

   public OptionInstance<Boolean> onlyShowSecureChat() {
      return this.onlyShowSecureChat;
   }

   public OptionInstance<Integer> fov() {
      return this.fov;
   }

   public OptionInstance<Boolean> telemetryOptInExtra() {
      return this.telemetryOptInExtra;
   }

   public OptionInstance<Double> screenEffectScale() {
      return this.screenEffectScale;
   }

   public OptionInstance<Double> fovEffectScale() {
      return this.fovEffectScale;
   }

   public OptionInstance<Double> darknessEffectScale() {
      return this.darknessEffectScale;
   }

   public OptionInstance<Double> glintSpeed() {
      return this.glintSpeed;
   }

   public OptionInstance<Double> glintStrength() {
      return this.glintStrength;
   }

   public OptionInstance<Double> damageTiltStrength() {
      return this.damageTiltStrength;
   }

   public OptionInstance<Double> gamma() {
      return this.gamma;
   }

   public OptionInstance<Integer> guiScale() {
      return this.guiScale;
   }

   public OptionInstance<ParticleStatus> particles() {
      return this.particles;
   }

   public OptionInstance<NarratorStatus> narrator() {
      return this.narrator;
   }

   public OptionInstance<String> soundDevice() {
      return this.soundDevice;
   }

   public Options(Minecraft var1, File var2) {
      super();
      this.darkMojangStudiosBackground = OptionInstance.createBoolean("options.darkMojangStudiosBackgroundColor", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_DARK_MOJANG_BACKGROUND), false);
      this.hideLightningFlash = OptionInstance.createBoolean("options.hideLightningFlashes", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_HIDE_LIGHTNING_FLASHES), false);
      this.hideSplashTexts = OptionInstance.createBoolean("options.hideSplashTexts", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_HIDE_SPLASH_TEXTS), false);
      this.sensitivity = new OptionInstance("options.sensitivity", OptionInstance.noTooltip(), (var0, var1x) -> {
         if (var1x == 0.0) {
            return genericValueLabel(var0, Component.translatable("options.sensitivity.min"));
         } else {
            return var1x == 1.0 ? genericValueLabel(var0, Component.translatable("options.sensitivity.max")) : percentValueLabel(var0, 2.0 * var1x);
         }
      }, OptionInstance.UnitDouble.INSTANCE, 0.5, (var0) -> {
      });
      this.serverRenderDistance = 0;
      this.entityDistanceScaling = new OptionInstance("options.entityDistanceScaling", OptionInstance.noTooltip(), Options::percentValueLabel, (new OptionInstance.IntRange(2, 20)).xmap((var0) -> {
         return (double)var0 / 4.0;
      }, (var0) -> {
         return (int)(var0 * 4.0);
      }), Codec.doubleRange(0.5, 5.0), 1.0, (var0) -> {
      });
      this.framerateLimit = new OptionInstance("options.framerateLimit", OptionInstance.noTooltip(), (var0, var1x) -> {
         return var1x == 260 ? genericValueLabel(var0, Component.translatable("options.framerateLimit.max")) : genericValueLabel(var0, Component.translatable("options.framerate", var1x));
      }, (new OptionInstance.IntRange(1, 26)).xmap((var0) -> {
         return var0 * 10;
      }, (var0) -> {
         return var0 / 10;
      }), Codec.intRange(10, 260), 120, (var0) -> {
         Minecraft.getInstance().getWindow().setFramerateLimit(var0);
      });
      this.cloudStatus = new OptionInstance("options.renderClouds", OptionInstance.noTooltip(), OptionInstance.forOptionEnum(), new OptionInstance.Enum(Arrays.asList(CloudStatus.values()), Codec.withAlternative(CloudStatus.CODEC, Codec.BOOL, (var0) -> {
         return var0 ? CloudStatus.FANCY : CloudStatus.OFF;
      })), CloudStatus.FANCY, (var0) -> {
         if (Minecraft.useShaderTransparency()) {
            RenderTarget var1 = Minecraft.getInstance().levelRenderer.getCloudsTarget();
            if (var1 != null) {
               var1.clear(Minecraft.ON_OSX);
            }
         }

      });
      this.graphicsMode = new OptionInstance("options.graphics", (var0) -> {
         Tooltip var10000;
         switch (var0) {
            case FANCY -> var10000 = Tooltip.create(GRAPHICS_TOOLTIP_FANCY);
            case FAST -> var10000 = Tooltip.create(GRAPHICS_TOOLTIP_FAST);
            case FABULOUS -> var10000 = Tooltip.create(GRAPHICS_TOOLTIP_FABULOUS);
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }, (var0, var1x) -> {
         MutableComponent var2 = Component.translatable(var1x.getKey());
         return var1x == GraphicsStatus.FABULOUS ? var2.withStyle(ChatFormatting.ITALIC) : var2;
      }, new OptionInstance.AltEnum(Arrays.asList(GraphicsStatus.values()), (List)Stream.of(GraphicsStatus.values()).filter((var0) -> {
         return var0 != GraphicsStatus.FABULOUS;
      }).collect(Collectors.toList()), () -> {
         return Minecraft.getInstance().isRunning() && Minecraft.getInstance().getGpuWarnlistManager().isSkippingFabulous();
      }, (var0, var1x) -> {
         Minecraft var2 = Minecraft.getInstance();
         GpuWarnlistManager var3 = var2.getGpuWarnlistManager();
         if (var1x == GraphicsStatus.FABULOUS && var3.willShowWarning()) {
            var3.showWarning();
         } else {
            var0.set(var1x);
            var2.levelRenderer.allChanged();
         }
      }, Codec.INT.xmap(GraphicsStatus::byId, GraphicsStatus::getId)), GraphicsStatus.FANCY, (var0) -> {
      });
      this.ambientOcclusion = OptionInstance.createBoolean("options.ao", true, (var0) -> {
         Minecraft.getInstance().levelRenderer.allChanged();
      });
      this.prioritizeChunkUpdates = new OptionInstance("options.prioritizeChunkUpdates", (var0) -> {
         Tooltip var10000;
         switch (var0) {
            case NONE -> var10000 = Tooltip.create(PRIORITIZE_CHUNK_TOOLTIP_NONE);
            case PLAYER_AFFECTED -> var10000 = Tooltip.create(PRIORITIZE_CHUNK_TOOLTIP_PLAYER_AFFECTED);
            case NEARBY -> var10000 = Tooltip.create(PRIORITIZE_CHUNK_TOOLTIP_NEARBY);
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }, OptionInstance.forOptionEnum(), new OptionInstance.Enum(Arrays.asList(PrioritizeChunkUpdates.values()), Codec.INT.xmap(PrioritizeChunkUpdates::byId, PrioritizeChunkUpdates::getId)), PrioritizeChunkUpdates.NONE, (var0) -> {
      });
      this.resourcePacks = Lists.newArrayList();
      this.incompatibleResourcePacks = Lists.newArrayList();
      this.chatVisibility = new OptionInstance("options.chat.visibility", OptionInstance.noTooltip(), OptionInstance.forOptionEnum(), new OptionInstance.Enum(Arrays.asList(ChatVisiblity.values()), Codec.INT.xmap(ChatVisiblity::byId, ChatVisiblity::getId)), ChatVisiblity.FULL, (var0) -> {
      });
      this.chatOpacity = new OptionInstance("options.chat.opacity", OptionInstance.noTooltip(), (var0, var1x) -> {
         return percentValueLabel(var0, var1x * 0.9 + 0.1);
      }, OptionInstance.UnitDouble.INSTANCE, 1.0, (var0) -> {
         Minecraft.getInstance().gui.getChat().rescaleChat();
      });
      this.chatLineSpacing = new OptionInstance("options.chat.line_spacing", OptionInstance.noTooltip(), Options::percentValueLabel, OptionInstance.UnitDouble.INSTANCE, 0.0, (var0) -> {
      });
      this.menuBackgroundBlurriness = new OptionInstance("options.accessibility.menu_background_blurriness", OptionInstance.cachedConstantTooltip(MENU_BACKGROUND_BLURRINESS_TOOLTIP), Options::percentValueLabel, OptionInstance.UnitDouble.INSTANCE, 0.5, (var0) -> {
      });
      this.textBackgroundOpacity = new OptionInstance("options.accessibility.text_background_opacity", OptionInstance.noTooltip(), Options::percentValueLabel, OptionInstance.UnitDouble.INSTANCE, 0.5, (var0) -> {
         Minecraft.getInstance().gui.getChat().rescaleChat();
      });
      this.panoramaSpeed = new OptionInstance("options.accessibility.panorama_speed", OptionInstance.noTooltip(), Options::percentValueLabel, OptionInstance.UnitDouble.INSTANCE, 1.0, (var0) -> {
      });
      this.highContrast = OptionInstance.createBoolean("options.accessibility.high_contrast", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_CONTRAST_MODE), false, (var1x) -> {
         PackRepository var2 = Minecraft.getInstance().getResourcePackRepository();
         boolean var3 = var2.getSelectedIds().contains("high_contrast");
         if (!var3 && var1x) {
            if (var2.addPack("high_contrast")) {
               this.updateResourcePacks(var2);
            }
         } else if (var3 && !var1x && var2.removePack("high_contrast")) {
            this.updateResourcePacks(var2);
         }

      });
      this.narratorHotkey = OptionInstance.createBoolean("options.accessibility.narrator_hotkey", OptionInstance.cachedConstantTooltip(Minecraft.ON_OSX ? Component.translatable("options.accessibility.narrator_hotkey.mac.tooltip") : Component.translatable("options.accessibility.narrator_hotkey.tooltip")), true);
      this.pauseOnLostFocus = true;
      this.modelParts = EnumSet.allOf(PlayerModelPart.class);
      this.mainHand = new OptionInstance("options.mainHand", OptionInstance.noTooltip(), OptionInstance.forOptionEnum(), new OptionInstance.Enum(Arrays.asList(HumanoidArm.values()), HumanoidArm.CODEC), HumanoidArm.RIGHT, (var1x) -> {
         this.broadcastOptions();
      });
      this.chatScale = new OptionInstance("options.chat.scale", OptionInstance.noTooltip(), (var0, var1x) -> {
         return (Component)(var1x == 0.0 ? CommonComponents.optionStatus(var0, false) : percentValueLabel(var0, var1x));
      }, OptionInstance.UnitDouble.INSTANCE, 1.0, (var0) -> {
         Minecraft.getInstance().gui.getChat().rescaleChat();
      });
      this.chatWidth = new OptionInstance("options.chat.width", OptionInstance.noTooltip(), (var0, var1x) -> {
         return pixelValueLabel(var0, ChatComponent.getWidth(var1x));
      }, OptionInstance.UnitDouble.INSTANCE, 1.0, (var0) -> {
         Minecraft.getInstance().gui.getChat().rescaleChat();
      });
      this.chatHeightUnfocused = new OptionInstance("options.chat.height.unfocused", OptionInstance.noTooltip(), (var0, var1x) -> {
         return pixelValueLabel(var0, ChatComponent.getHeight(var1x));
      }, OptionInstance.UnitDouble.INSTANCE, ChatComponent.defaultUnfocusedPct(), (var0) -> {
         Minecraft.getInstance().gui.getChat().rescaleChat();
      });
      this.chatHeightFocused = new OptionInstance("options.chat.height.focused", OptionInstance.noTooltip(), (var0, var1x) -> {
         return pixelValueLabel(var0, ChatComponent.getHeight(var1x));
      }, OptionInstance.UnitDouble.INSTANCE, 1.0, (var0) -> {
         Minecraft.getInstance().gui.getChat().rescaleChat();
      });
      this.chatDelay = new OptionInstance("options.chat.delay_instant", OptionInstance.noTooltip(), (var0, var1x) -> {
         return var1x <= 0.0 ? Component.translatable("options.chat.delay_none") : Component.translatable("options.chat.delay", String.format(Locale.ROOT, "%.1f", var1x));
      }, (new OptionInstance.IntRange(0, 60)).xmap((var0) -> {
         return (double)var0 / 10.0;
      }, (var0) -> {
         return (int)(var0 * 10.0);
      }), Codec.doubleRange(0.0, 6.0), 0.0, (var0) -> {
         Minecraft.getInstance().getChatListener().setMessageDelay(var0);
      });
      this.notificationDisplayTime = new OptionInstance("options.notifications.display_time", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_NOTIFICATION_DISPLAY_TIME), (var0, var1x) -> {
         return genericValueLabel(var0, Component.translatable("options.multiplier", var1x));
      }, (new OptionInstance.IntRange(5, 100)).xmap((var0) -> {
         return (double)var0 / 10.0;
      }, (var0) -> {
         return (int)(var0 * 10.0);
      }), Codec.doubleRange(0.5, 10.0), 1.0, (var0) -> {
      });
      this.mipmapLevels = new OptionInstance("options.mipmapLevels", OptionInstance.noTooltip(), (var0, var1x) -> {
         return (Component)(var1x == 0 ? CommonComponents.optionStatus(var0, false) : genericValueLabel(var0, var1x));
      }, new OptionInstance.IntRange(0, 4), 4, (var0) -> {
      });
      this.useNativeTransport = true;
      this.attackIndicator = new OptionInstance("options.attackIndicator", OptionInstance.noTooltip(), OptionInstance.forOptionEnum(), new OptionInstance.Enum(Arrays.asList(AttackIndicatorStatus.values()), Codec.INT.xmap(AttackIndicatorStatus::byId, AttackIndicatorStatus::getId)), AttackIndicatorStatus.CROSSHAIR, (var0) -> {
      });
      this.tutorialStep = TutorialSteps.MOVEMENT;
      this.joinedFirstServer = false;
      this.hideBundleTutorial = false;
      this.biomeBlendRadius = new OptionInstance("options.biomeBlendRadius", OptionInstance.noTooltip(), (var0, var1x) -> {
         int var2 = var1x * 2 + 1;
         return genericValueLabel(var0, Component.translatable("options.biomeBlendRadius." + var2));
      }, new OptionInstance.IntRange(0, 7), 2, (var0) -> {
         Minecraft.getInstance().levelRenderer.allChanged();
      });
      this.mouseWheelSensitivity = new OptionInstance("options.mouseWheelSensitivity", OptionInstance.noTooltip(), (var0, var1x) -> {
         return genericValueLabel(var0, Component.literal(String.format(Locale.ROOT, "%.2f", var1x)));
      }, (new OptionInstance.IntRange(-200, 100)).xmap(Options::logMouse, Options::unlogMouse), Codec.doubleRange(logMouse(-200), logMouse(100)), logMouse(0), (var0) -> {
      });
      this.rawMouseInput = OptionInstance.createBoolean("options.rawMouseInput", true, (var0) -> {
         Window var1 = Minecraft.getInstance().getWindow();
         if (var1 != null) {
            var1.updateRawMouseInput(var0);
         }

      });
      this.glDebugVerbosity = 1;
      this.autoJump = OptionInstance.createBoolean("options.autoJump", false);
      this.operatorItemsTab = OptionInstance.createBoolean("options.operatorItemsTab", false);
      this.autoSuggestions = OptionInstance.createBoolean("options.autoSuggestCommands", true);
      this.chatColors = OptionInstance.createBoolean("options.chat.color", true);
      this.chatLinks = OptionInstance.createBoolean("options.chat.links", true);
      this.chatLinksPrompt = OptionInstance.createBoolean("options.chat.links.prompt", true);
      this.enableVsync = OptionInstance.createBoolean("options.vsync", true, (var0) -> {
         if (Minecraft.getInstance().getWindow() != null) {
            Minecraft.getInstance().getWindow().updateVsync(var0);
         }

      });
      this.entityShadows = OptionInstance.createBoolean("options.entityShadows", true);
      this.forceUnicodeFont = OptionInstance.createBoolean("options.forceUnicodeFont", false, (var0) -> {
         updateFontOptions();
      });
      this.japaneseGlyphVariants = OptionInstance.createBoolean("options.japaneseGlyphVariants", OptionInstance.cachedConstantTooltip(Component.translatable("options.japaneseGlyphVariants.tooltip")), japaneseGlyphVariantsDefault(), (var0) -> {
         updateFontOptions();
      });
      this.invertYMouse = OptionInstance.createBoolean("options.invertMouse", false);
      this.discreteMouseScroll = OptionInstance.createBoolean("options.discrete_mouse_scroll", false);
      this.realmsNotifications = OptionInstance.createBoolean("options.realmsNotifications", true);
      this.allowServerListing = OptionInstance.createBoolean("options.allowServerListing", OptionInstance.cachedConstantTooltip(ALLOW_SERVER_LISTING_TOOLTIP), true, (var1x) -> {
         this.broadcastOptions();
      });
      this.reducedDebugInfo = OptionInstance.createBoolean("options.reducedDebugInfo", false);
      this.soundSourceVolumes = (Map)Util.make(new EnumMap(SoundSource.class), (var1x) -> {
         SoundSource[] var2 = SoundSource.values();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            SoundSource var5 = var2[var4];
            var1x.put(var5, this.createSoundSliderOptionInstance("soundCategory." + var5.getName(), var5));
         }

      });
      this.showSubtitles = OptionInstance.createBoolean("options.showSubtitles", false);
      this.directionalAudio = OptionInstance.createBoolean("options.directionalAudio", (var0) -> {
         return var0 ? Tooltip.create(DIRECTIONAL_AUDIO_TOOLTIP_ON) : Tooltip.create(DIRECTIONAL_AUDIO_TOOLTIP_OFF);
      }, false, (var0) -> {
         SoundManager var1 = Minecraft.getInstance().getSoundManager();
         var1.reload();
         var1.play(SimpleSoundInstance.forUI((Holder)SoundEvents.UI_BUTTON_CLICK, 1.0F));
      });
      this.backgroundForChatOnly = new OptionInstance("options.accessibility.text_background", OptionInstance.noTooltip(), (var0, var1x) -> {
         return var1x ? Component.translatable("options.accessibility.text_background.chat") : Component.translatable("options.accessibility.text_background.everywhere");
      }, OptionInstance.BOOLEAN_VALUES, true, (var0) -> {
      });
      this.touchscreen = OptionInstance.createBoolean("options.touchscreen", false);
      this.fullscreen = OptionInstance.createBoolean("options.fullscreen", false, (var1x) -> {
         Minecraft var2 = Minecraft.getInstance();
         if (var2.getWindow() != null && var2.getWindow().isFullscreen() != var1x) {
            var2.getWindow().toggleFullScreen();
            this.fullscreen().set(var2.getWindow().isFullscreen());
         }

      });
      this.bobView = OptionInstance.createBoolean("options.viewBobbing", true);
      this.toggleCrouch = new OptionInstance("key.sneak", OptionInstance.noTooltip(), (var0, var1x) -> {
         return var1x ? MOVEMENT_TOGGLE : MOVEMENT_HOLD;
      }, OptionInstance.BOOLEAN_VALUES, false, (var0) -> {
      });
      this.toggleSprint = new OptionInstance("key.sprint", OptionInstance.noTooltip(), (var0, var1x) -> {
         return var1x ? MOVEMENT_TOGGLE : MOVEMENT_HOLD;
      }, OptionInstance.BOOLEAN_VALUES, false, (var0) -> {
      });
      this.hideMatchedNames = OptionInstance.createBoolean("options.hideMatchedNames", OptionInstance.cachedConstantTooltip(CHAT_TOOLTIP_HIDE_MATCHED_NAMES), true);
      this.showAutosaveIndicator = OptionInstance.createBoolean("options.autosaveIndicator", true);
      this.onlyShowSecureChat = OptionInstance.createBoolean("options.onlyShowSecureChat", OptionInstance.cachedConstantTooltip(CHAT_TOOLTIP_ONLY_SHOW_SECURE), false);
      this.keyUp = new KeyMapping("key.forward", 87, "key.categories.movement");
      this.keyLeft = new KeyMapping("key.left", 65, "key.categories.movement");
      this.keyDown = new KeyMapping("key.back", 83, "key.categories.movement");
      this.keyRight = new KeyMapping("key.right", 68, "key.categories.movement");
      this.keyJump = new KeyMapping("key.jump", 32, "key.categories.movement");
      OptionInstance var10006 = this.toggleCrouch;
      Objects.requireNonNull(var10006);
      this.keyShift = new ToggleKeyMapping("key.sneak", 340, "key.categories.movement", var10006::get);
      var10006 = this.toggleSprint;
      Objects.requireNonNull(var10006);
      this.keySprint = new ToggleKeyMapping("key.sprint", 341, "key.categories.movement", var10006::get);
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
      this.cameraType = CameraType.FIRST_PERSON;
      this.lastMpIp = "";
      this.fov = new OptionInstance("options.fov", OptionInstance.noTooltip(), (var0, var1x) -> {
         Component var10000;
         switch (var1x) {
            case 70 -> var10000 = genericValueLabel(var0, Component.translatable("options.fov.min"));
            case 110 -> var10000 = genericValueLabel(var0, Component.translatable("options.fov.max"));
            default -> var10000 = genericValueLabel(var0, var1x);
         }

         return var10000;
      }, new OptionInstance.IntRange(30, 110), Codec.DOUBLE.xmap((var0) -> {
         return (int)(var0 * 40.0 + 70.0);
      }, (var0) -> {
         return ((double)var0 - 70.0) / 40.0;
      }), 70, (var0) -> {
         Minecraft.getInstance().levelRenderer.needsUpdate();
      });
      this.telemetryOptInExtra = OptionInstance.createBoolean("options.telemetry.button", OptionInstance.cachedConstantTooltip(TELEMETRY_TOOLTIP), (var0, var1x) -> {
         Minecraft var2 = Minecraft.getInstance();
         if (!var2.allowsTelemetry()) {
            return Component.translatable("options.telemetry.state.none");
         } else {
            return var1x && var2.extraTelemetryAvailable() ? Component.translatable("options.telemetry.state.all") : Component.translatable("options.telemetry.state.minimal");
         }
      }, false, (var0) -> {
      });
      this.screenEffectScale = new OptionInstance("options.screenEffectScale", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_SCREEN_EFFECT), (var0, var1x) -> {
         return var1x == 0.0 ? genericValueLabel(var0, CommonComponents.OPTION_OFF) : percentValueLabel(var0, var1x);
      }, OptionInstance.UnitDouble.INSTANCE, 1.0, (var0) -> {
      });
      this.fovEffectScale = new OptionInstance("options.fovEffectScale", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_FOV_EFFECT), (var0, var1x) -> {
         return var1x == 0.0 ? genericValueLabel(var0, CommonComponents.OPTION_OFF) : percentValueLabel(var0, var1x);
      }, OptionInstance.UnitDouble.INSTANCE.xmap(Mth::square, Math::sqrt), Codec.doubleRange(0.0, 1.0), 1.0, (var0) -> {
      });
      this.darknessEffectScale = new OptionInstance("options.darknessEffectScale", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_DARKNESS_EFFECT), (var0, var1x) -> {
         return var1x == 0.0 ? genericValueLabel(var0, CommonComponents.OPTION_OFF) : percentValueLabel(var0, var1x);
      }, OptionInstance.UnitDouble.INSTANCE.xmap(Mth::square, Math::sqrt), 1.0, (var0) -> {
      });
      this.glintSpeed = new OptionInstance("options.glintSpeed", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_GLINT_SPEED), (var0, var1x) -> {
         return var1x == 0.0 ? genericValueLabel(var0, CommonComponents.OPTION_OFF) : percentValueLabel(var0, var1x);
      }, OptionInstance.UnitDouble.INSTANCE, 0.5, (var0) -> {
      });
      this.glintStrength = new OptionInstance("options.glintStrength", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_GLINT_STRENGTH), (var0, var1x) -> {
         return var1x == 0.0 ? genericValueLabel(var0, CommonComponents.OPTION_OFF) : percentValueLabel(var0, var1x);
      }, OptionInstance.UnitDouble.INSTANCE, 0.75, RenderSystem::setShaderGlintAlpha);
      this.damageTiltStrength = new OptionInstance("options.damageTiltStrength", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_DAMAGE_TILT_STRENGTH), (var0, var1x) -> {
         return var1x == 0.0 ? genericValueLabel(var0, CommonComponents.OPTION_OFF) : percentValueLabel(var0, var1x);
      }, OptionInstance.UnitDouble.INSTANCE, 1.0, (var0) -> {
      });
      this.gamma = new OptionInstance("options.gamma", OptionInstance.noTooltip(), (var0, var1x) -> {
         int var2 = (int)(var1x * 100.0);
         if (var2 == 0) {
            return genericValueLabel(var0, Component.translatable("options.gamma.min"));
         } else if (var2 == 50) {
            return genericValueLabel(var0, Component.translatable("options.gamma.default"));
         } else {
            return var2 == 100 ? genericValueLabel(var0, Component.translatable("options.gamma.max")) : genericValueLabel(var0, var2);
         }
      }, OptionInstance.UnitDouble.INSTANCE, 0.5, (var0) -> {
      });
      this.guiScale = new OptionInstance("options.guiScale", OptionInstance.noTooltip(), (var0, var1x) -> {
         return var1x == 0 ? Component.translatable("options.guiScale.auto") : Component.literal(Integer.toString(var1x));
      }, new OptionInstance.ClampingLazyMaxIntRange(0, () -> {
         Minecraft var0 = Minecraft.getInstance();
         return !var0.isRunning() ? 2147483646 : var0.getWindow().calculateScale(0, var0.isEnforceUnicode());
      }, 2147483646), 0, (var1x) -> {
         this.minecraft.resizeDisplay();
      });
      this.particles = new OptionInstance("options.particles", OptionInstance.noTooltip(), OptionInstance.forOptionEnum(), new OptionInstance.Enum(Arrays.asList(ParticleStatus.values()), Codec.INT.xmap(ParticleStatus::byId, ParticleStatus::getId)), ParticleStatus.ALL, (var0) -> {
      });
      this.narrator = new OptionInstance("options.narrator", OptionInstance.noTooltip(), (var1x, var2x) -> {
         return (Component)(this.minecraft.getNarrator().isActive() ? var2x.getName() : Component.translatable("options.narrator.notavailable"));
      }, new OptionInstance.Enum(Arrays.asList(NarratorStatus.values()), Codec.INT.xmap(NarratorStatus::byId, NarratorStatus::getId)), NarratorStatus.OFF, (var1x) -> {
         this.minecraft.getNarrator().updateNarratorStatus(var1x);
      });
      this.languageCode = "en_us";
      this.soundDevice = new OptionInstance("options.audioDevice", OptionInstance.noTooltip(), (var0, var1x) -> {
         if ("".equals(var1x)) {
            return Component.translatable("options.audioDevice.default");
         } else {
            return var1x.startsWith("OpenAL Soft on ") ? Component.literal(var1x.substring(SoundEngine.OPEN_AL_SOFT_PREFIX_LENGTH)) : Component.literal(var1x);
         }
      }, new OptionInstance.LazyEnum(() -> {
         return Stream.concat(Stream.of(""), Minecraft.getInstance().getSoundManager().getAvailableSoundDevices().stream()).toList();
      }, (var0) -> {
         return Minecraft.getInstance().isRunning() && var0 != "" && !Minecraft.getInstance().getSoundManager().getAvailableSoundDevices().contains(var0) ? Optional.empty() : Optional.of(var0);
      }, Codec.STRING), "", (var0) -> {
         SoundManager var1 = Minecraft.getInstance().getSoundManager();
         var1.reload();
         var1.play(SimpleSoundInstance.forUI((Holder)SoundEvents.UI_BUTTON_CLICK, 1.0F));
      });
      this.onboardAccessibility = true;
      this.minecraft = var1;
      this.optionsFile = new File(var2, "options.txt");
      boolean var3 = Runtime.getRuntime().maxMemory() >= 1000000000L;
      this.renderDistance = new OptionInstance("options.renderDistance", OptionInstance.noTooltip(), (var0, var1x) -> {
         return genericValueLabel(var0, Component.translatable("options.chunks", var1x));
      }, new OptionInstance.IntRange(2, var3 ? 32 : 16), 12, (var0) -> {
         Minecraft.getInstance().levelRenderer.needsUpdate();
      });
      this.simulationDistance = new OptionInstance("options.simulationDistance", OptionInstance.noTooltip(), (var0, var1x) -> {
         return genericValueLabel(var0, Component.translatable("options.chunks", var1x));
      }, new OptionInstance.IntRange(5, var3 ? 32 : 16), 12, (var0) -> {
      });
      this.syncWrites = Util.getPlatform() == Util.OS.WINDOWS;
      this.load();
   }

   public float getBackgroundOpacity(float var1) {
      return (Boolean)this.backgroundForChatOnly.get() ? var1 : ((Double)this.textBackgroundOpacity().get()).floatValue();
   }

   public int getBackgroundColor(float var1) {
      return (int)(this.getBackgroundOpacity(var1) * 255.0F) << 24 & -16777216;
   }

   public int getBackgroundColor(int var1) {
      return (Boolean)this.backgroundForChatOnly.get() ? var1 : (int)((Double)this.textBackgroundOpacity.get() * 255.0) << 24 & -16777216;
   }

   public void setKey(KeyMapping var1, InputConstants.Key var2) {
      var1.setKey(var2);
      this.save();
   }

   private void processDumpedOptions(OptionAccess var1) {
      var1.process("ao", this.ambientOcclusion);
      var1.process("biomeBlendRadius", this.biomeBlendRadius);
      var1.process("enableVsync", this.enableVsync);
      var1.process("entityDistanceScaling", this.entityDistanceScaling);
      var1.process("entityShadows", this.entityShadows);
      var1.process("forceUnicodeFont", this.forceUnicodeFont);
      var1.process("japaneseGlyphVariants", this.japaneseGlyphVariants);
      var1.process("fov", this.fov);
      var1.process("fovEffectScale", this.fovEffectScale);
      var1.process("darknessEffectScale", this.darknessEffectScale);
      var1.process("glintSpeed", this.glintSpeed);
      var1.process("glintStrength", this.glintStrength);
      var1.process("prioritizeChunkUpdates", this.prioritizeChunkUpdates);
      var1.process("fullscreen", this.fullscreen);
      var1.process("gamma", this.gamma);
      var1.process("graphicsMode", this.graphicsMode);
      var1.process("guiScale", this.guiScale);
      var1.process("maxFps", this.framerateLimit);
      var1.process("mipmapLevels", this.mipmapLevels);
      var1.process("narrator", this.narrator);
      var1.process("particles", this.particles);
      var1.process("reducedDebugInfo", this.reducedDebugInfo);
      var1.process("renderClouds", this.cloudStatus);
      var1.process("renderDistance", this.renderDistance);
      var1.process("simulationDistance", this.simulationDistance);
      var1.process("screenEffectScale", this.screenEffectScale);
      var1.process("soundDevice", this.soundDevice);
   }

   private void processOptions(FieldAccess var1) {
      this.processDumpedOptions(var1);
      var1.process("autoJump", this.autoJump);
      var1.process("operatorItemsTab", this.operatorItemsTab);
      var1.process("autoSuggestions", this.autoSuggestions);
      var1.process("chatColors", this.chatColors);
      var1.process("chatLinks", this.chatLinks);
      var1.process("chatLinksPrompt", this.chatLinksPrompt);
      var1.process("discrete_mouse_scroll", this.discreteMouseScroll);
      var1.process("invertYMouse", this.invertYMouse);
      var1.process("realmsNotifications", this.realmsNotifications);
      var1.process("showSubtitles", this.showSubtitles);
      var1.process("directionalAudio", this.directionalAudio);
      var1.process("touchscreen", this.touchscreen);
      var1.process("bobView", this.bobView);
      var1.process("toggleCrouch", this.toggleCrouch);
      var1.process("toggleSprint", this.toggleSprint);
      var1.process("darkMojangStudiosBackground", this.darkMojangStudiosBackground);
      var1.process("hideLightningFlashes", this.hideLightningFlash);
      var1.process("hideSplashTexts", this.hideSplashTexts);
      var1.process("mouseSensitivity", this.sensitivity);
      var1.process("damageTiltStrength", this.damageTiltStrength);
      var1.process("highContrast", this.highContrast);
      var1.process("narratorHotkey", this.narratorHotkey);
      List var10003 = this.resourcePacks;
      Function var10004 = Options::readListOfStrings;
      Gson var10005 = GSON;
      Objects.requireNonNull(var10005);
      this.resourcePacks = (List)var1.process("resourcePacks", var10003, var10004, var10005::toJson);
      var10003 = this.incompatibleResourcePacks;
      var10004 = Options::readListOfStrings;
      var10005 = GSON;
      Objects.requireNonNull(var10005);
      this.incompatibleResourcePacks = (List)var1.process("incompatibleResourcePacks", var10003, var10004, var10005::toJson);
      this.lastMpIp = var1.process("lastServer", this.lastMpIp);
      this.languageCode = var1.process("lang", this.languageCode);
      var1.process("chatVisibility", this.chatVisibility);
      var1.process("chatOpacity", this.chatOpacity);
      var1.process("chatLineSpacing", this.chatLineSpacing);
      var1.process("textBackgroundOpacity", this.textBackgroundOpacity);
      var1.process("backgroundForChatOnly", this.backgroundForChatOnly);
      this.hideServerAddress = var1.process("hideServerAddress", this.hideServerAddress);
      this.advancedItemTooltips = var1.process("advancedItemTooltips", this.advancedItemTooltips);
      this.pauseOnLostFocus = var1.process("pauseOnLostFocus", this.pauseOnLostFocus);
      this.overrideWidth = var1.process("overrideWidth", this.overrideWidth);
      this.overrideHeight = var1.process("overrideHeight", this.overrideHeight);
      var1.process("chatHeightFocused", this.chatHeightFocused);
      var1.process("chatDelay", this.chatDelay);
      var1.process("chatHeightUnfocused", this.chatHeightUnfocused);
      var1.process("chatScale", this.chatScale);
      var1.process("chatWidth", this.chatWidth);
      var1.process("notificationDisplayTime", this.notificationDisplayTime);
      this.useNativeTransport = var1.process("useNativeTransport", this.useNativeTransport);
      var1.process("mainHand", this.mainHand);
      var1.process("attackIndicator", this.attackIndicator);
      this.tutorialStep = (TutorialSteps)var1.process("tutorialStep", this.tutorialStep, TutorialSteps::getByName, TutorialSteps::getName);
      var1.process("mouseWheelSensitivity", this.mouseWheelSensitivity);
      var1.process("rawMouseInput", this.rawMouseInput);
      this.glDebugVerbosity = var1.process("glDebugVerbosity", this.glDebugVerbosity);
      this.skipMultiplayerWarning = var1.process("skipMultiplayerWarning", this.skipMultiplayerWarning);
      var1.process("hideMatchedNames", this.hideMatchedNames);
      this.joinedFirstServer = var1.process("joinedFirstServer", this.joinedFirstServer);
      this.hideBundleTutorial = var1.process("hideBundleTutorial", this.hideBundleTutorial);
      this.syncWrites = var1.process("syncChunkWrites", this.syncWrites);
      var1.process("showAutosaveIndicator", this.showAutosaveIndicator);
      var1.process("allowServerListing", this.allowServerListing);
      var1.process("onlyShowSecureChat", this.onlyShowSecureChat);
      var1.process("panoramaScrollSpeed", this.panoramaSpeed);
      var1.process("telemetryOptInExtra", this.telemetryOptInExtra);
      this.onboardAccessibility = var1.process("onboardAccessibility", this.onboardAccessibility);
      var1.process("menuBackgroundBlurriness", this.menuBackgroundBlurriness);
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
         var1.process("soundCategory_" + var10.getName(), (OptionInstance)this.soundSourceVolumes.get(var10));
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
               this.graphicsMode.set(GraphicsStatus.FANCY);
            } else {
               this.graphicsMode.set(GraphicsStatus.FAST);
            }
         }

         this.processOptions(new FieldAccess(this) {
            @Nullable
            private String getValueOrNull(String var1) {
               return var8.contains(var1) ? var8.getString(var1) : null;
            }

            public <T> void process(String var1, OptionInstance<T> var2) {
               String var3 = this.getValueOrNull(var1);
               if (var3 != null) {
                  JsonReader var4 = new JsonReader(new StringReader(var3.isEmpty() ? "\"\"" : var3));
                  JsonElement var5 = JsonParser.parseReader(var4);
                  DataResult var6 = var2.codec().parse(JsonOps.INSTANCE, var5);
                  var6.error().ifPresent((var2x) -> {
                     Options.LOGGER.error("Error parsing option value " + var3 + " for option " + String.valueOf(var2) + ": " + var2x.message());
                  });
                  Objects.requireNonNull(var2);
                  var6.ifSuccess(var2::set);
               }

            }

            public int process(String var1, int var2) {
               String var3 = this.getValueOrNull(var1);
               if (var3 != null) {
                  try {
                     return Integer.parseInt(var3);
                  } catch (NumberFormatException var5) {
                     Options.LOGGER.warn("Invalid integer value for option {} = {}", new Object[]{var1, var3, var5});
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
                     Options.LOGGER.warn("Invalid floating point value for option {} = {}", new Object[]{var1, var3, var5});
                  }
               }

               return var2;
            }

            public <T> T process(String var1, T var2, Function<String, T> var3, Function<T, String> var4) {
               String var5 = this.getValueOrNull(var1);
               return var5 == null ? var2 : var3.apply(var5);
            }
         });
         if (var8.contains("fullscreenResolution")) {
            this.fullscreenVideoModeString = var8.getString("fullscreenResolution");
         }

         if (this.minecraft.getWindow() != null) {
            this.minecraft.getWindow().setFramerateLimit((Integer)this.framerateLimit.get());
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

      return DataFixTypes.OPTIONS.updateToCurrentVersion(this.minecraft.getFixerUpper(), var1, var2);
   }

   public void save() {
      try {
         final PrintWriter var1 = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.optionsFile), StandardCharsets.UTF_8));

         try {
            var1.println("version:" + SharedConstants.getCurrentVersion().getDataVersion().getVersion());
            this.processOptions(new FieldAccess(this) {
               public void writePrefix(String var1x) {
                  var1.print(var1x);
                  var1.print(':');
               }

               public <T> void process(String var1x, OptionInstance<T> var2) {
                  var2.codec().encodeStart(JsonOps.INSTANCE, var2.get()).ifError((var1xx) -> {
                     Logger var10000 = Options.LOGGER;
                     String var10001 = String.valueOf(var2);
                     var10000.error("Error saving option " + var10001 + ": " + String.valueOf(var1xx));
                  }).ifSuccess((var3) -> {
                     this.writePrefix(var1x);
                     var1.println(Options.GSON.toJson(var3));
                  });
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

   public ClientInformation buildPlayerInformation() {
      int var1 = 0;

      PlayerModelPart var3;
      for(Iterator var2 = this.modelParts.iterator(); var2.hasNext(); var1 |= var3.getMask()) {
         var3 = (PlayerModelPart)var2.next();
      }

      return new ClientInformation(this.languageCode, (Integer)this.renderDistance.get(), (ChatVisiblity)this.chatVisibility.get(), (Boolean)this.chatColors.get(), var1, (HumanoidArm)this.mainHand.get(), this.minecraft.isTextFilteringEnabled(), (Boolean)this.allowServerListing.get());
   }

   public void broadcastOptions() {
      if (this.minecraft.player != null) {
         this.minecraft.player.connection.send(new ServerboundClientInformationPacket(this.buildPlayerInformation()));
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
      return this.getEffectiveRenderDistance() >= 4 ? (CloudStatus)this.cloudStatus.get() : CloudStatus.OFF;
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

   private static List<String> readListOfStrings(String var0) {
      List var1 = (List)GsonHelper.fromNullableJson(GSON, var0, LIST_OF_STRINGS_TYPE);
      return (List)(var1 != null ? var1 : Lists.newArrayList());
   }

   public File getFile() {
      return this.optionsFile;
   }

   public String dumpOptionsForReport() {
      final ArrayList var1 = new ArrayList();
      this.processDumpedOptions(new OptionAccess(this) {
         public <T> void process(String var1x, OptionInstance<T> var2) {
            var1.add(Pair.of(var1x, var2.get()));
         }
      });
      var1.add(Pair.of("fullscreenResolution", String.valueOf(this.fullscreenVideoModeString)));
      var1.add(Pair.of("glDebugVerbosity", this.glDebugVerbosity));
      var1.add(Pair.of("overrideHeight", this.overrideHeight));
      var1.add(Pair.of("overrideWidth", this.overrideWidth));
      var1.add(Pair.of("syncChunkWrites", this.syncWrites));
      var1.add(Pair.of("useNativeTransport", this.useNativeTransport));
      var1.add(Pair.of("resourcePacks", this.resourcePacks));
      return (String)var1.stream().sorted(Comparator.comparing(Pair::getFirst)).map((var0) -> {
         String var10000 = (String)var0.getFirst();
         return var10000 + ": " + String.valueOf(var0.getSecond());
      }).collect(Collectors.joining(System.lineSeparator()));
   }

   public void setServerRenderDistance(int var1) {
      this.serverRenderDistance = var1;
   }

   public int getEffectiveRenderDistance() {
      return this.serverRenderDistance > 0 ? Math.min((Integer)this.renderDistance.get(), this.serverRenderDistance) : (Integer)this.renderDistance.get();
   }

   private static Component pixelValueLabel(Component var0, int var1) {
      return Component.translatable("options.pixel_value", var0, var1);
   }

   private static Component percentValueLabel(Component var0, double var1) {
      return Component.translatable("options.percent_value", var0, (int)(var1 * 100.0));
   }

   public static Component genericValueLabel(Component var0, Component var1) {
      return Component.translatable("options.generic_value", var0, var1);
   }

   public static Component genericValueLabel(Component var0, int var1) {
      return genericValueLabel(var0, Component.literal(Integer.toString(var1)));
   }

   static {
      GRAPHICS_TOOLTIP_FABULOUS = Component.translatable("options.graphics.fabulous.tooltip", Component.translatable("options.graphics.fabulous").withStyle(ChatFormatting.ITALIC));
      GRAPHICS_TOOLTIP_FANCY = Component.translatable("options.graphics.fancy.tooltip");
      PRIORITIZE_CHUNK_TOOLTIP_NONE = Component.translatable("options.prioritizeChunkUpdates.none.tooltip");
      PRIORITIZE_CHUNK_TOOLTIP_PLAYER_AFFECTED = Component.translatable("options.prioritizeChunkUpdates.byPlayer.tooltip");
      PRIORITIZE_CHUNK_TOOLTIP_NEARBY = Component.translatable("options.prioritizeChunkUpdates.nearby.tooltip");
      MENU_BACKGROUND_BLURRINESS_TOOLTIP = Component.translatable("options.accessibility.menu_background_blurriness.tooltip");
      ACCESSIBILITY_TOOLTIP_CONTRAST_MODE = Component.translatable("options.accessibility.high_contrast.tooltip");
      ACCESSIBILITY_TOOLTIP_NOTIFICATION_DISPLAY_TIME = Component.translatable("options.notifications.display_time.tooltip");
      ALLOW_SERVER_LISTING_TOOLTIP = Component.translatable("options.allowServerListing.tooltip");
      DIRECTIONAL_AUDIO_TOOLTIP_ON = Component.translatable("options.directionalAudio.on.tooltip");
      DIRECTIONAL_AUDIO_TOOLTIP_OFF = Component.translatable("options.directionalAudio.off.tooltip");
      MOVEMENT_TOGGLE = Component.translatable("options.key.toggle");
      MOVEMENT_HOLD = Component.translatable("options.key.hold");
      CHAT_TOOLTIP_HIDE_MATCHED_NAMES = Component.translatable("options.hideMatchedNames.tooltip");
      CHAT_TOOLTIP_ONLY_SHOW_SECURE = Component.translatable("options.onlyShowSecureChat.tooltip");
      TELEMETRY_TOOLTIP = Component.translatable("options.telemetry.button.tooltip", Component.translatable("options.telemetry.state.minimal"), Component.translatable("options.telemetry.state.all"));
      ACCESSIBILITY_TOOLTIP_SCREEN_EFFECT = Component.translatable("options.screenEffectScale.tooltip");
      ACCESSIBILITY_TOOLTIP_FOV_EFFECT = Component.translatable("options.fovEffectScale.tooltip");
      ACCESSIBILITY_TOOLTIP_DARKNESS_EFFECT = Component.translatable("options.darknessEffectScale.tooltip");
      ACCESSIBILITY_TOOLTIP_GLINT_SPEED = Component.translatable("options.glintSpeed.tooltip");
      ACCESSIBILITY_TOOLTIP_GLINT_STRENGTH = Component.translatable("options.glintStrength.tooltip");
      ACCESSIBILITY_TOOLTIP_DAMAGE_TILT_STRENGTH = Component.translatable("options.damageTiltStrength.tooltip");
   }

   private interface OptionAccess {
      <T> void process(String var1, OptionInstance<T> var2);
   }

   interface FieldAccess extends OptionAccess {
      int process(String var1, int var2);

      boolean process(String var1, boolean var2);

      String process(String var1, String var2);

      float process(String var1, float var2);

      <T> T process(String var1, T var2, Function<String, T> var3, Function<T, String> var4);
   }
}
