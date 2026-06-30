package net.minecraft.client;

import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.input.InputQuirks;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.extract.LevelExtractor;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.SoundPreviewHandler;
import net.minecraft.client.tutorial.TutorialSteps;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ParticleStatus;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ARGB;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.LenientJsonParser;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.ChatVisiblity;
import net.minecraft.world.entity.player.PlayerModelPart;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class Options {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Gson GSON = new Gson();
   private static final TypeToken<List<String>> LIST_OF_STRINGS_TYPE = new TypeToken<List<String>>() {
   };
   public static final int RENDER_DISTANCE_SHORT = 4;
   public static final int RENDER_DISTANCE_FAR = 12;
   public static final int RENDER_DISTANCE_REALLY_FAR = 16;
   public static final int RENDER_DISTANCE_EXTREME = 32;
   private static final Splitter OPTION_SPLITTER = Splitter.on(':').limit(2);
   private static final String DEFAULT_SOUND_DEVICE = "";
   private static final Component TOOLTIP_NEEDS_RESTART = Component.translatable("options.needsRestart");
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
   private PreferredGraphicsApi preferredGraphicsBackendFromStartup;
   private static final Component GRAPHICS_API_TOOLTIP = Component.translatable("options.graphicsApi.tooltip");
   private static final Component GRAPHICS_API_TOOLTIP_VULKAN = Component.translatable("options.graphicsApi.tooltip.vulkan");
   private final OptionInstance<PreferredGraphicsApi> preferredGraphicsBackend;
   private boolean isApplyingGraphicsPreset;
   private final OptionInstance<GraphicsPreset> graphicsPreset;
   private static final Component INACTIVITY_FPS_LIMIT_TOOLTIP_MINIMIZED = Component.translatable("options.inactivityFpsLimit.minimized.tooltip");
   private static final Component INACTIVITY_FPS_LIMIT_TOOLTIP_AFK = Component.translatable("options.inactivityFpsLimit.afk.tooltip");
   private final OptionInstance<InactivityFpsLimit> inactivityFpsLimit;
   private final OptionInstance<CloudStatus> cloudStatus;
   private final OptionInstance<Integer> cloudRange;
   private static final Component GRAPHICS_TOOLTIP_WEATHER_RADIUS = Component.translatable("options.weatherRadius.tooltip");
   private final OptionInstance<Integer> weatherRadius;
   private static final Component GRAPHICS_TOOLTIP_CUTOUT_LEAVES = Component.translatable("options.cutoutLeaves.tooltip");
   private final OptionInstance<Boolean> cutoutLeaves;
   private static final Component GRAPHICS_TOOLTIP_VIGNETTE = Component.translatable("options.vignette.tooltip");
   private final OptionInstance<Boolean> vignette;
   private static final Component GRAPHICS_TOOLTIP_IMPROVED_TRANSPARENCY = Component.translatable("options.improvedTransparency.tooltip");
   private final OptionInstance<Boolean> improvedTransparency;
   private final OptionInstance<Boolean> ambientOcclusion;
   private static final Component GRAPHICS_TOOLTIP_CHUNK_FADE = Component.translatable("options.chunkFade.tooltip");
   private final OptionInstance<Double> chunkSectionFadeInTime;
   private static final Component PRIORITIZE_CHUNK_TOOLTIP_NONE = Component.translatable("options.prioritizeChunkUpdates.none.tooltip");
   private static final Component PRIORITIZE_CHUNK_TOOLTIP_PLAYER_AFFECTED = Component.translatable("options.prioritizeChunkUpdates.byPlayer.tooltip");
   private static final Component PRIORITIZE_CHUNK_TOOLTIP_NEARBY = Component.translatable("options.prioritizeChunkUpdates.nearby.tooltip");
   private final OptionInstance<PrioritizeChunkUpdates> prioritizeChunkUpdates;
   public List<String> resourcePacks;
   public List<String> incompatibleResourcePacks;
   private final OptionInstance<ChatVisiblity> chatVisibility;
   private final OptionInstance<Double> chatOpacity;
   private final OptionInstance<Double> chatLineSpacing;
   private static final Component MENU_BACKGROUND_BLURRINESS_TOOLTIP = Component.translatable("options.accessibility.menu_background_blurriness.tooltip");
   private static final int BLURRINESS_DEFAULT_VALUE = 5;
   private final OptionInstance<Integer> menuBackgroundBlurriness;
   private final OptionInstance<Double> textBackgroundOpacity;
   private final OptionInstance<Double> panoramaSpeed;
   private static final Component ACCESSIBILITY_TOOLTIP_CONTRAST_MODE = Component.translatable("options.accessibility.high_contrast.tooltip");
   private final OptionInstance<Boolean> highContrast;
   private static final Component HIGH_CONTRAST_BLOCK_OUTLINE_TOOLTIP = Component.translatable("options.accessibility.high_contrast_block_outline.tooltip");
   private final OptionInstance<Boolean> highContrastBlockOutline;
   private final OptionInstance<Boolean> narratorHotkey;
   public @Nullable String fullscreenVideoModeString;
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
   private static final Component ACCESSIBILITY_TOOLTIP_NOTIFICATION_DISPLAY_TIME = Component.translatable("options.notifications.display_time.tooltip");
   private final OptionInstance<Double> notificationDisplayTime;
   private final OptionInstance<Integer> mipmapLevels;
   private static final Component GRAPHICS_TOOLTIP_ANISOTROPIC_FILTERING = Component.translatable("options.maxAnisotropy.tooltip");
   private final OptionInstance<Integer> maxAnisotropyBit;
   private static final Component FILTERING_NONE_TOOLTIP = Component.translatable("options.textureFiltering.none.tooltip");
   private static final Component FILTERING_RGSS_TOOLTIP = Component.translatable("options.textureFiltering.rgss.tooltip");
   private static final Component FILTERING_ANISOTROPIC_TOOLTIP = Component.translatable("options.textureFiltering.anisotropic.tooltip");
   private final OptionInstance<TextureFilteringMethod> textureFiltering;
   private boolean useNativeTransport;
   private final OptionInstance<AttackIndicatorStatus> attackIndicator;
   public TutorialSteps tutorialStep;
   public boolean joinedFirstServer;
   private final OptionInstance<Integer> biomeBlendRadius;
   private final OptionInstance<Double> mouseWheelSensitivity;
   private final OptionInstance<Boolean> rawMouseInput;
   private static final Component ALLOW_CURSOR_CHANGES_TOOLTIP = Component.translatable("options.allowCursorChanges.tooltip");
   private final OptionInstance<Boolean> allowCursorChanges;
   public int glDebugVerbosity;
   private final OptionInstance<Boolean> autoJump;
   private static final Component ACCESSIBILITY_TOOLTIP_ROTATE_WITH_MINECART = Component.translatable("options.rotateWithMinecart.tooltip");
   private final OptionInstance<Boolean> rotateWithMinecart;
   private final OptionInstance<Boolean> operatorItemsTab;
   private final OptionInstance<Boolean> autoSuggestions;
   private final OptionInstance<Boolean> chatColors;
   private final OptionInstance<Boolean> chatLinks;
   private final OptionInstance<Boolean> chatLinksPrompt;
   private final OptionInstance<Boolean> enableVsync;
   private final OptionInstance<Boolean> entityShadows;
   private final OptionInstance<Boolean> forceUnicodeFont;
   private final OptionInstance<Boolean> japaneseGlyphVariants;
   private final OptionInstance<Boolean> invertXMouse;
   private final OptionInstance<Boolean> invertYMouse;
   private final OptionInstance<Boolean> discreteMouseScroll;
   private static final Component REALMS_NOTIFICATIONS_TOOLTIP = Component.translatable("options.realmsNotifications.tooltip");
   private final OptionInstance<Boolean> realmsNotifications;
   private static final Component ALLOW_SERVER_LISTING_TOOLTIP = Component.translatable("options.allowServerListing.tooltip");
   private final OptionInstance<Boolean> allowServerListing;
   private final OptionInstance<Boolean> reducedDebugInfo;
   private static final Component IN_GAME_NOTIFICATION_TOOLTIP = Component.translatable("options.inGameNotification.tooltip");
   private final OptionInstance<Boolean> inGameNotification;
   private final OptionInstance<PresenceSharing> sharePresence;
   private final Map<SoundSource, OptionInstance<Double>> soundSourceVolumes;
   private static final Component CLOSED_CAPTIONS_TOOLTIP = Component.translatable("options.showSubtitles.tooltip");
   private final OptionInstance<Boolean> showSubtitles;
   private static final Component DIRECTIONAL_AUDIO_TOOLTIP_ON = Component.translatable("options.directionalAudio.on.tooltip");
   private static final Component DIRECTIONAL_AUDIO_TOOLTIP_OFF = Component.translatable("options.directionalAudio.off.tooltip");
   private final OptionInstance<Boolean> directionalAudio;
   private final OptionInstance<Boolean> backgroundForChatOnly;
   private final OptionInstance<Boolean> fullscreen;
   private boolean exclusiveFullscreenFromStartup;
   private static final Component TOOLTIP_EXCLUSIVE_FULLSCREEN_ON = Component.translatable("options.exclusiveFullscreen.on.tooltip");
   private static final Component TOOLTIP_EXCLUSIVE_FULLSCREEN_OFF = Component.translatable("options.exclusiveFullscreen.off.tooltip");
   private final OptionInstance<Boolean> exclusiveFullscreen;
   private final OptionInstance<Boolean> bobView;
   private static final Component KEY_TOGGLE = Component.translatable("options.key.toggle");
   private static final Component KEY_HOLD = Component.translatable("options.key.hold");
   private final OptionInstance<Boolean> toggleCrouch;
   private final OptionInstance<Boolean> toggleSprint;
   private final OptionInstance<Boolean> toggleAttack;
   private final OptionInstance<Boolean> toggleUse;
   private static final Component SPRINT_WINDOW_TOOLTIP = Component.translatable("options.sprintWindow.tooltip");
   private final OptionInstance<Integer> sprintWindow;
   public boolean skipMultiplayerWarning;
   private static final Component CHAT_TOOLTIP_HIDE_MATCHED_NAMES = Component.translatable("options.hideMatchedNames.tooltip");
   private final OptionInstance<Boolean> hideMatchedNames;
   private final OptionInstance<Boolean> showAutosaveIndicator;
   private static final Component CHAT_TOOLTIP_ONLY_SHOW_SECURE = Component.translatable("options.onlyShowSecureChat.tooltip");
   private final OptionInstance<Boolean> onlyShowSecureChat;
   private static final Component CHAT_TOOLTIP_SAVE_DRAFTS = Component.translatable("options.chat.drafts.tooltip");
   private final OptionInstance<Boolean> saveChatDrafts;
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
   public final KeyMapping keyFriends;
   public final KeyMapping keySocialInteractions;
   public final KeyMapping keyScreenshot;
   public final KeyMapping keyTogglePerspective;
   public final KeyMapping keySmoothCamera;
   public final KeyMapping keyFullscreen;
   public final KeyMapping keyAdvancements;
   public final KeyMapping keyQuickActions;
   public final KeyMapping keyToggleGui;
   public final KeyMapping keyToggleSpectatorShaderEffects;
   public final KeyMapping[] keyHotbarSlots;
   public final KeyMapping keySaveHotbarActivator;
   public final KeyMapping keyLoadHotbarActivator;
   public final KeyMapping keySpectatorOutlines;
   public final KeyMapping keySpectatorHotbar;
   public final KeyMapping keyDebugOverlay;
   public final KeyMapping keyDebugModifier;
   public final KeyMapping keyDebugCrash;
   public final KeyMapping keyDebugReloadChunk;
   public final KeyMapping keyDebugShowHitboxes;
   public final KeyMapping keyDebugClearChat;
   public final KeyMapping keyDebugShowChunkBorders;
   public final KeyMapping keyDebugShowAdvancedTooltips;
   public final KeyMapping keyDebugCopyRecreateCommand;
   public final KeyMapping keyDebugSpectate;
   public final KeyMapping keyDebugSwitchGameMode;
   public final KeyMapping keyDebugDebugOptions;
   public final KeyMapping keyDebugFocusPause;
   public final KeyMapping keyDebugDumpDynamicTextures;
   public final KeyMapping keyDebugReloadResourcePacks;
   public final KeyMapping keyDebugProfiling;
   public final KeyMapping keyDebugCopyLocation;
   public final KeyMapping keyDebugDumpVersion;
   public final KeyMapping keyDebugPofilingChart;
   public final KeyMapping keyDebugFpsCharts;
   public final KeyMapping keyDebugNetworkCharts;
   public final KeyMapping keyDebugLightmapTexture;
   public final KeyMapping keyDebugSwitchTranslucencyMode;
   public final KeyMapping[] debugKeys;
   public final KeyMapping[] keyMappings;
   protected Minecraft minecraft;
   private final File optionsFile;
   private CameraType cameraType;
   public String lastMpIp;
   public boolean smoothCamera;
   private final OptionInstance<Integer> fov;
   private static final Component TELEMETRY_TOOLTIP = Component.translatable("options.telemetry.button.tooltip", Component.translatable("options.telemetry.state.minimal"), Component.translatable("options.telemetry.state.all"));
   private final OptionInstance<Boolean> telemetryOptInExtra;
   private static final Component ACCESSIBILITY_TOOLTIP_SCREEN_EFFECT = Component.translatable("options.screenEffectScale.tooltip");
   private final OptionInstance<Double> screenEffectScale;
   private static final Component ACCESSIBILITY_TOOLTIP_FOV_EFFECT = Component.translatable("options.fovEffectScale.tooltip");
   private final OptionInstance<Double> fovEffectScale;
   private static final Component ACCESSIBILITY_TOOLTIP_DARKNESS_EFFECT = Component.translatable("options.darknessEffectScale.tooltip");
   private final OptionInstance<Double> darknessEffectScale;
   private static final Component ACCESSIBILITY_TOOLTIP_GLINT_SPEED = Component.translatable("options.glintSpeed.tooltip");
   private final OptionInstance<Double> glintSpeed;
   private static final Component ACCESSIBILITY_TOOLTIP_GLINT_STRENGTH = Component.translatable("options.glintStrength.tooltip");
   private final OptionInstance<Double> glintStrength;
   private static final Component ACCESSIBILITY_TOOLTIP_DAMAGE_TILT_STRENGTH = Component.translatable("options.damageTiltStrength.tooltip");
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
   private static final Component MUSIC_FREQUENCY_TOOLTIP = Component.translatable("options.music_frequency.tooltip");
   private final OptionInstance<MusicManager.MusicFrequency> musicFrequency;
   private final OptionInstance<MusicToastDisplayState> musicToast;
   public boolean syncWrites;
   public boolean startedCleanly;

   public static boolean isSoundDeviceDefault(final String deviceName) {
      return deviceName.equals("");
   }

   private static void operateOnLevelExtractor(final Consumer<LevelExtractor> consumer) {
      LevelExtractor levelExtractor = Minecraft.getInstance().levelExtractor;
      if (levelExtractor != null) {
         consumer.accept(levelExtractor);
      }

   }

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

   public OptionInstance<PreferredGraphicsApi> preferredGraphicsBackend() {
      return this.preferredGraphicsBackend;
   }

   public boolean isRestartRequiredToApplyVideoSettings() {
      return this.preferredGraphicsBackend.get() != this.preferredGraphicsBackendFromStartup || (Boolean)this.exclusiveFullscreen.get() != this.exclusiveFullscreenFromStartup;
   }

   public void applyGraphicsPreset(final GraphicsPreset value) {
      this.isApplyingGraphicsPreset = true;
      value.apply(this.minecraft);
      this.isApplyingGraphicsPreset = false;
   }

   public OptionInstance<GraphicsPreset> graphicsPreset() {
      return this.graphicsPreset;
   }

   public OptionInstance<InactivityFpsLimit> inactivityFpsLimit() {
      return this.inactivityFpsLimit;
   }

   public OptionInstance<CloudStatus> cloudStatus() {
      return this.cloudStatus;
   }

   public OptionInstance<Integer> cloudRange() {
      return this.cloudRange;
   }

   public OptionInstance<Integer> weatherRadius() {
      return this.weatherRadius;
   }

   public OptionInstance<Boolean> cutoutLeaves() {
      return this.cutoutLeaves;
   }

   public OptionInstance<Boolean> vignette() {
      return this.vignette;
   }

   public OptionInstance<Boolean> improvedTransparency() {
      return this.improvedTransparency;
   }

   public OptionInstance<Boolean> ambientOcclusion() {
      return this.ambientOcclusion;
   }

   public OptionInstance<Double> chunkSectionFadeInTime() {
      return this.chunkSectionFadeInTime;
   }

   public OptionInstance<PrioritizeChunkUpdates> prioritizeChunkUpdates() {
      return this.prioritizeChunkUpdates;
   }

   public void updateResourcePacks(final PackRepository packRepository) {
      List<String> oldPacks = ImmutableList.copyOf(this.resourcePacks);
      this.resourcePacks.clear();
      this.incompatibleResourcePacks.clear();

      for(Pack entry : packRepository.getSelectedPacks()) {
         if (!entry.isFixedPosition()) {
            this.resourcePacks.add(entry.getId());
            if (!entry.getCompatibility().isCompatible()) {
               this.incompatibleResourcePacks.add(entry.getId());
            }
         }
      }

      this.save();
      List<String> newPacks = ImmutableList.copyOf(this.resourcePacks);
      if (!newPacks.equals(oldPacks)) {
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

   public OptionInstance<Integer> menuBackgroundBlurriness() {
      return this.menuBackgroundBlurriness;
   }

   public int getMenuBackgroundBlurriness() {
      return (Integer)this.menuBackgroundBlurriness().get();
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

   public OptionInstance<Boolean> highContrastBlockOutline() {
      return this.highContrastBlockOutline;
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

   public OptionInstance<Integer> maxAnisotropyBit() {
      return this.maxAnisotropyBit;
   }

   public int maxAnisotropyValue() {
      return Math.min(1 << (Integer)this.maxAnisotropyBit.get(), RenderSystem.getDevice().getDeviceInfo().limits().maxAnisotropy());
   }

   public OptionInstance<TextureFilteringMethod> textureFiltering() {
      return this.textureFiltering;
   }

   public OptionInstance<AttackIndicatorStatus> attackIndicator() {
      return this.attackIndicator;
   }

   public OptionInstance<Integer> biomeBlendRadius() {
      return this.biomeBlendRadius;
   }

   private static double logMouse(final int value) {
      return Math.pow(10.0, (double)value / 100.0);
   }

   private static int unlogMouse(final double value) {
      return Mth.floor(Math.log10(value) * 100.0);
   }

   public OptionInstance<Double> mouseWheelSensitivity() {
      return this.mouseWheelSensitivity;
   }

   public OptionInstance<Boolean> rawMouseInput() {
      return this.rawMouseInput;
   }

   public OptionInstance<Boolean> allowCursorChanges() {
      return this.allowCursorChanges;
   }

   public OptionInstance<Boolean> autoJump() {
      return this.autoJump;
   }

   public OptionInstance<Boolean> rotateWithMinecart() {
      return this.rotateWithMinecart;
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
      Minecraft instance = Minecraft.getInstance();
      if (instance.getWindow() != null) {
         instance.updateFontOptions();
         instance.resizeGui();
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

   public OptionInstance<Boolean> invertMouseX() {
      return this.invertXMouse;
   }

   public OptionInstance<Boolean> invertMouseY() {
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

   public OptionInstance<Boolean> inGameNotification() {
      return this.inGameNotification;
   }

   public OptionInstance<PresenceSharing> sharePresence() {
      return this.sharePresence;
   }

   public final float getFinalSoundSourceVolume(final SoundSource source) {
      return source == SoundSource.MASTER ? this.getSoundSourceVolume(source) : this.getSoundSourceVolume(source) * this.getSoundSourceVolume(SoundSource.MASTER);
   }

   public final float getSoundSourceVolume(final SoundSource source) {
      return ((Double)this.getSoundSourceOptionInstance(source).get()).floatValue();
   }

   public final OptionInstance<Double> getSoundSourceOptionInstance(final SoundSource source) {
      return (OptionInstance)Objects.requireNonNull((OptionInstance)this.soundSourceVolumes.get(source));
   }

   private OptionInstance<Double> createSoundSliderOptionInstance(final String captionId, final SoundSource category) {
      return new OptionInstance<Double>(captionId, OptionInstance.noTooltip(), Options::percentValueOrOffLabel, OptionInstance.UnitDouble.INSTANCE, 1.0, (value) -> {
         Minecraft minecraft = Minecraft.getInstance();
         SoundManager soundManager = minecraft.getSoundManager();
         if ((category == SoundSource.MASTER || category == SoundSource.MUSIC) && this.getFinalSoundSourceVolume(SoundSource.MUSIC) > 0.0F) {
            minecraft.getMusicManager().showNowPlayingToastIfNeeded();
         }

         soundManager.refreshCategoryVolume(category);
         if (minecraft.level == null) {
            SoundPreviewHandler.preview(soundManager, category, value.floatValue());
         }

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

   public OptionInstance<Boolean> fullscreen() {
      return this.fullscreen;
   }

   public OptionInstance<Boolean> exclusiveFullscreen() {
      return this.exclusiveFullscreen;
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

   public OptionInstance<Boolean> toggleAttack() {
      return this.toggleAttack;
   }

   public OptionInstance<Boolean> toggleUse() {
      return this.toggleUse;
   }

   public OptionInstance<Integer> sprintWindow() {
      return this.sprintWindow;
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

   public OptionInstance<Boolean> saveChatDrafts() {
      return this.saveChatDrafts;
   }

   private void setGraphicsPresetToCustom() {
      if (!this.isApplyingGraphicsPreset) {
         this.graphicsPreset.set(GraphicsPreset.CUSTOM);
         Screen var2 = this.minecraft.gui.screen();
         if (var2 instanceof OptionsSubScreen) {
            OptionsSubScreen optionsSubScreen = (OptionsSubScreen)var2;
            optionsSubScreen.resetOption(this.graphicsPreset);
         }

      }
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

   public void onboardingAccessibilityFinished() {
      this.onboardAccessibility = false;
      this.save();
   }

   public OptionInstance<MusicManager.MusicFrequency> musicFrequency() {
      return this.musicFrequency;
   }

   public OptionInstance<MusicToastDisplayState> musicToast() {
      return this.musicToast;
   }

   public Options(final Minecraft minecraft, final File workingDirectory) {
      super();
      this.darkMojangStudiosBackground = OptionInstance.createBoolean("options.darkMojangStudiosBackgroundColor", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_DARK_MOJANG_BACKGROUND), false);
      this.hideLightningFlash = OptionInstance.createBoolean("options.hideLightningFlashes", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_HIDE_LIGHTNING_FLASHES), false);
      this.hideSplashTexts = OptionInstance.createBoolean("options.hideSplashTexts", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_HIDE_SPLASH_TEXTS), false);
      this.sensitivity = new OptionInstance<Double>("options.sensitivity", OptionInstance.noTooltip(), (caption, value) -> {
         if (value == 0.0) {
            return genericValueLabel(caption, Component.translatable("options.sensitivity.min"));
         } else {
            return value == 1.0 ? genericValueLabel(caption, Component.translatable("options.sensitivity.max")) : percentValueLabel(caption, 2.0 * value);
         }
      }, OptionInstance.UnitDouble.INSTANCE, 0.5, OptionInstance.NO_ACTION);
      this.serverRenderDistance = 0;
      this.entityDistanceScaling = new OptionInstance<Double>("options.entityDistanceScaling", OptionInstance.noTooltip(), Options::percentValueLabel, (new OptionInstance.IntRange(2, 20)).xmap((value) -> (double)value / 4.0, (value) -> (int)(value * 4.0), true), Codec.doubleRange(0.5, 5.0), 1.0, (var1) -> this.setGraphicsPresetToCustom());
      this.framerateLimit = new OptionInstance<Integer>("options.framerateLimit", OptionInstance.noTooltip(), (caption, value) -> value == 260 ? genericValueLabel(caption, Component.translatable("options.framerateLimit.max")) : genericValueLabel(caption, Component.translatable("options.framerate", value)), (new OptionInstance.IntRange(1, 26)).xmap((value) -> value * 10, (value) -> value / 10, true), Codec.intRange(10, 260), 120, (value) -> Minecraft.getInstance().getFramerateLimitTracker().setFramerateLimit(value));
      this.preferredGraphicsBackendFromStartup = PreferredGraphicsApi.DEFAULT;
      this.preferredGraphicsBackend = new OptionInstance<PreferredGraphicsApi>("options.graphicsApi", (value) -> {
         List<Component> tooltipLines = new ArrayList();
         if (value != this.preferredGraphicsBackendFromStartup) {
            tooltipLines.add(TOOLTIP_NEEDS_RESTART);
            tooltipLines.add(CommonComponents.EMPTY);
         }

         tooltipLines.add(GRAPHICS_API_TOOLTIP);
         if (value == PreferredGraphicsApi.VULKAN) {
            tooltipLines.add(CommonComponents.EMPTY);
            tooltipLines.add(GRAPHICS_API_TOOLTIP_VULKAN);
         }

         return Tooltip.create(CommonComponents.joinLines((Collection)tooltipLines));
      }, (caption, value) -> value.caption(), new OptionInstance.Enum(List.of(PreferredGraphicsApi.values()), PreferredGraphicsApi.CODEC), PreferredGraphicsApi.CODEC, PreferredGraphicsApi.DEFAULT, OptionInstance.NO_ACTION);
      this.graphicsPreset = new OptionInstance<GraphicsPreset>("options.graphics.preset", OptionInstance.cachedConstantTooltip(Component.translatable("options.graphics.preset.tooltip")), (caption, value) -> genericValueLabel(caption, Component.translatable(value.getKey())), new OptionInstance.SliderableEnum(List.of(GraphicsPreset.values()), GraphicsPreset.CODEC), GraphicsPreset.CODEC, GraphicsPreset.FANCY, this::applyGraphicsPreset);
      this.inactivityFpsLimit = new OptionInstance<InactivityFpsLimit>("options.inactivityFpsLimit", (value) -> {
         Tooltip var10000;
         switch (value) {
            case MINIMIZED -> var10000 = Tooltip.create(INACTIVITY_FPS_LIMIT_TOOLTIP_MINIMIZED);
            case AFK -> var10000 = Tooltip.create(INACTIVITY_FPS_LIMIT_TOOLTIP_AFK);
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }, (caption, value) -> value.caption(), new OptionInstance.Enum(Arrays.asList(InactivityFpsLimit.values()), InactivityFpsLimit.CODEC), InactivityFpsLimit.AFK, OptionInstance.NO_ACTION);
      this.cloudStatus = new OptionInstance<CloudStatus>("options.renderClouds", OptionInstance.noTooltip(), (caption, value) -> value.caption(), new OptionInstance.Enum(Arrays.asList(CloudStatus.values()), Codec.withAlternative(CloudStatus.CODEC, Codec.BOOL, (b) -> b ? CloudStatus.FANCY : CloudStatus.OFF)), CloudStatus.FANCY, (var1) -> this.setGraphicsPresetToCustom());
      this.cloudRange = new OptionInstance<Integer>("options.renderCloudsDistance", OptionInstance.noTooltip(), (caption, value) -> genericValueLabel(caption, Component.translatable("options.chunks", value)), new OptionInstance.IntRange(2, 128, true), 128, (var1) -> {
         operateOnLevelExtractor(LevelExtractor::allChanged);
         this.setGraphicsPresetToCustom();
      });
      this.weatherRadius = new OptionInstance<Integer>("options.weatherRadius", OptionInstance.cachedConstantTooltip(GRAPHICS_TOOLTIP_WEATHER_RADIUS), (caption, value) -> genericValueLabel(caption, Component.translatable("options.blocks", value)), new OptionInstance.IntRange(3, 10, true), 10, (var1) -> this.setGraphicsPresetToCustom());
      this.cutoutLeaves = OptionInstance.createBoolean("options.cutoutLeaves", OptionInstance.cachedConstantTooltip(GRAPHICS_TOOLTIP_CUTOUT_LEAVES), true, (var1) -> {
         operateOnLevelExtractor(LevelExtractor::allChanged);
         this.setGraphicsPresetToCustom();
      });
      this.vignette = OptionInstance.createBoolean("options.vignette", OptionInstance.cachedConstantTooltip(GRAPHICS_TOOLTIP_VIGNETTE), true);
      this.improvedTransparency = OptionInstance.createBoolean("options.improvedTransparency", OptionInstance.cachedConstantTooltip(GRAPHICS_TOOLTIP_IMPROVED_TRANSPARENCY), false, (value) -> {
         operateOnLevelExtractor(LevelExtractor::allChanged);
         this.setGraphicsPresetToCustom();
      });
      this.ambientOcclusion = OptionInstance.createBoolean("options.ao", true, (var1) -> {
         operateOnLevelExtractor(LevelExtractor::allChanged);
         this.setGraphicsPresetToCustom();
      });
      this.chunkSectionFadeInTime = new OptionInstance<Double>("options.chunkFade", OptionInstance.cachedConstantTooltip(GRAPHICS_TOOLTIP_CHUNK_FADE), (caption, value) -> value <= 0.0 ? Component.translatable("options.chunkFade.none") : Component.translatable("options.chunkFade.seconds", String.format(Locale.ROOT, "%.2f", value)), (new OptionInstance.IntRange(0, 40)).xmap((value) -> (double)value / 20.0, (value) -> (int)(value * 20.0), true), Codec.doubleRange(0.0, 2.0), 0.75, OptionInstance.NO_ACTION);
      this.prioritizeChunkUpdates = new OptionInstance<PrioritizeChunkUpdates>("options.prioritizeChunkUpdates", (value) -> {
         Tooltip var10000;
         switch (value) {
            case NONE -> var10000 = Tooltip.create(PRIORITIZE_CHUNK_TOOLTIP_NONE);
            case PLAYER_AFFECTED -> var10000 = Tooltip.create(PRIORITIZE_CHUNK_TOOLTIP_PLAYER_AFFECTED);
            case NEARBY -> var10000 = Tooltip.create(PRIORITIZE_CHUNK_TOOLTIP_NEARBY);
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }, (caption, value) -> value.caption(), new OptionInstance.Enum(Arrays.asList(PrioritizeChunkUpdates.values()), PrioritizeChunkUpdates.LEGACY_CODEC), PrioritizeChunkUpdates.NONE, (var1) -> this.setGraphicsPresetToCustom());
      this.resourcePacks = Lists.newArrayList();
      this.incompatibleResourcePacks = Lists.newArrayList();
      this.chatVisibility = new OptionInstance<ChatVisiblity>("options.chat.visibility", OptionInstance.noTooltip(), (caption, value) -> value.caption(), new OptionInstance.Enum(Arrays.asList(ChatVisiblity.values()), ChatVisiblity.LEGACY_CODEC), ChatVisiblity.FULL, (var0) -> {
         LocalPlayer player = Minecraft.getInstance().player;
         if (player != null) {
            player.refreshChatAbilities();
         }

      });
      this.chatOpacity = new OptionInstance<Double>("options.chat.opacity", OptionInstance.noTooltip(), (caption, value) -> percentValueLabel(caption, value * 0.9 + 0.1), OptionInstance.UnitDouble.INSTANCE, 1.0, (var0) -> Minecraft.getInstance().gui.hud.getChat().rescaleChat());
      this.chatLineSpacing = new OptionInstance<Double>("options.chat.line_spacing", OptionInstance.noTooltip(), Options::percentValueLabel, OptionInstance.UnitDouble.INSTANCE, 0.0, OptionInstance.NO_ACTION);
      this.menuBackgroundBlurriness = new OptionInstance<Integer>("options.accessibility.menu_background_blurriness", OptionInstance.cachedConstantTooltip(MENU_BACKGROUND_BLURRINESS_TOOLTIP), Options::genericValueOrOffLabel, new OptionInstance.IntRange(0, 10), 5, (var1) -> this.setGraphicsPresetToCustom());
      this.textBackgroundOpacity = new OptionInstance<Double>("options.accessibility.text_background_opacity", OptionInstance.noTooltip(), Options::percentValueLabel, OptionInstance.UnitDouble.INSTANCE, 0.5, (var0) -> Minecraft.getInstance().gui.hud.getChat().rescaleChat());
      this.panoramaSpeed = new OptionInstance<Double>("options.accessibility.panorama_speed", OptionInstance.noTooltip(), Options::percentValueLabel, OptionInstance.UnitDouble.INSTANCE, 1.0, OptionInstance.NO_ACTION);
      this.highContrast = OptionInstance.createBoolean("options.accessibility.high_contrast", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_CONTRAST_MODE), false, (value) -> {
         PackRepository packRepo = Minecraft.getInstance().getResourcePackRepository();
         boolean isSelected = packRepo.getSelectedIds().contains("high_contrast");
         if (!isSelected && value) {
            if (packRepo.addPack("high_contrast")) {
               this.updateResourcePacks(packRepo);
            }
         } else if (isSelected && !value && packRepo.removePack("high_contrast")) {
            this.updateResourcePacks(packRepo);
         }

      });
      this.highContrastBlockOutline = OptionInstance.createBoolean("options.accessibility.high_contrast_block_outline", OptionInstance.cachedConstantTooltip(HIGH_CONTRAST_BLOCK_OUTLINE_TOOLTIP), false);
      this.narratorHotkey = OptionInstance.createBoolean("options.accessibility.narrator_hotkey", OptionInstance.cachedConstantTooltip(InputQuirks.REPLACE_CTRL_KEY_WITH_CMD_KEY ? Component.translatable("options.accessibility.narrator_hotkey.mac.tooltip") : Component.translatable("options.accessibility.narrator_hotkey.tooltip")), true);
      this.pauseOnLostFocus = true;
      this.modelParts = EnumSet.allOf(PlayerModelPart.class);
      this.mainHand = new OptionInstance<HumanoidArm>("options.mainHand", OptionInstance.noTooltip(), (caption, value) -> value.caption(), new OptionInstance.Enum(Arrays.asList(HumanoidArm.values()), HumanoidArm.CODEC), HumanoidArm.RIGHT, OptionInstance.NO_ACTION);
      this.chatScale = new OptionInstance<Double>("options.chat.scale", OptionInstance.noTooltip(), (caption, value) -> (Component)(value == 0.0 ? CommonComponents.optionStatus(caption, false) : percentValueLabel(caption, value)), OptionInstance.UnitDouble.INSTANCE, 1.0, (var0) -> Minecraft.getInstance().gui.hud.getChat().rescaleChat());
      this.chatWidth = new OptionInstance<Double>("options.chat.width", OptionInstance.noTooltip(), (caption, value) -> pixelValueLabel(caption, ChatComponent.getWidth(value)), OptionInstance.UnitDouble.INSTANCE, 1.0, (var0) -> Minecraft.getInstance().gui.hud.getChat().rescaleChat());
      this.chatHeightUnfocused = new OptionInstance<Double>("options.chat.height.unfocused", OptionInstance.noTooltip(), (caption, value) -> pixelValueLabel(caption, ChatComponent.getHeight(value)), OptionInstance.UnitDouble.INSTANCE, ChatComponent.defaultUnfocusedPct(), (var0) -> Minecraft.getInstance().gui.hud.getChat().rescaleChat());
      this.chatHeightFocused = new OptionInstance<Double>("options.chat.height.focused", OptionInstance.noTooltip(), (caption, value) -> pixelValueLabel(caption, ChatComponent.getHeight(value)), OptionInstance.UnitDouble.INSTANCE, 1.0, (var0) -> Minecraft.getInstance().gui.hud.getChat().rescaleChat());
      this.chatDelay = new OptionInstance<Double>("options.chat.delay_instant", OptionInstance.noTooltip(), (caption, value) -> value <= 0.0 ? Component.translatable("options.chat.delay_none") : Component.translatable("options.chat.delay", String.format(Locale.ROOT, "%.1f", value)), (new OptionInstance.IntRange(0, 60)).xmap((value) -> (double)value / 10.0, (value) -> (int)(value * 10.0), true), Codec.doubleRange(0.0, 6.0), 0.0, (value) -> Minecraft.getInstance().gui.chatListener().setMessageDelay(value));
      this.notificationDisplayTime = new OptionInstance<Double>("options.notifications.display_time", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_NOTIFICATION_DISPLAY_TIME), (caption, value) -> genericValueLabel(caption, Component.translatable("options.multiplier", value)), (new OptionInstance.IntRange(5, 100)).xmap((value) -> (double)value / 10.0, (value) -> (int)(value * 10.0), true), Codec.doubleRange(0.5, 10.0), 1.0, OptionInstance.NO_ACTION);
      this.mipmapLevels = new OptionInstance<Integer>("options.mipmapLevels", OptionInstance.noTooltip(), (caption, value) -> (Component)(value == 0 ? CommonComponents.optionStatus(caption, false) : genericValueLabel(caption, value)), new OptionInstance.IntRange(0, 4), 4, (var1) -> this.setGraphicsPresetToCustom());
      this.maxAnisotropyBit = new OptionInstance<Integer>("options.maxAnisotropy", OptionInstance.cachedConstantTooltip(GRAPHICS_TOOLTIP_ANISOTROPIC_FILTERING), (caption, value) -> (Component)(value == 0 ? CommonComponents.optionStatus(caption, false) : genericValueLabel(caption, Component.translatable("options.multiplier", Integer.toString(1 << value)))), new OptionInstance.IntRange(1, 3), 2, (var1) -> {
         this.setGraphicsPresetToCustom();
         operateOnLevelExtractor(LevelExtractor::resetSampler);
      });
      this.textureFiltering = new OptionInstance<TextureFilteringMethod>("options.textureFiltering", (value) -> {
         Tooltip var10000;
         switch (value) {
            case NONE -> var10000 = Tooltip.create(FILTERING_NONE_TOOLTIP);
            case RGSS -> var10000 = Tooltip.create(FILTERING_RGSS_TOOLTIP);
            case ANISOTROPIC -> var10000 = Tooltip.create(FILTERING_ANISOTROPIC_TOOLTIP);
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }, (caption, value) -> value.caption(), new OptionInstance.Enum(Arrays.asList(TextureFilteringMethod.values()), TextureFilteringMethod.LEGACY_CODEC), TextureFilteringMethod.NONE, (var1) -> {
         this.setGraphicsPresetToCustom();
         operateOnLevelExtractor(LevelExtractor::resetSampler);
      });
      this.useNativeTransport = true;
      this.attackIndicator = new OptionInstance<AttackIndicatorStatus>("options.attackIndicator", OptionInstance.noTooltip(), (caption, value) -> value.caption(), new OptionInstance.Enum(Arrays.asList(AttackIndicatorStatus.values()), AttackIndicatorStatus.LEGACY_CODEC), AttackIndicatorStatus.CROSSHAIR, OptionInstance.NO_ACTION);
      this.tutorialStep = TutorialSteps.MOVEMENT;
      this.joinedFirstServer = false;
      this.biomeBlendRadius = new OptionInstance<Integer>("options.biomeBlendRadius", OptionInstance.noTooltip(), (caption, value) -> {
         int dist = value * 2 + 1;
         return genericValueLabel(caption, Component.translatable("options.biomeBlendRadius." + dist));
      }, new OptionInstance.IntRange(0, 7, false), 2, (var1) -> {
         operateOnLevelExtractor(LevelExtractor::allChanged);
         this.setGraphicsPresetToCustom();
      });
      this.mouseWheelSensitivity = new OptionInstance<Double>("options.mouseWheelSensitivity", OptionInstance.noTooltip(), (caption, value) -> genericValueLabel(caption, Component.literal(String.format(Locale.ROOT, "%.2f", value))), (new OptionInstance.IntRange(-200, 100)).xmap(Options::logMouse, Options::unlogMouse, false), Codec.doubleRange(logMouse(-200), logMouse(100)), logMouse(0), OptionInstance.NO_ACTION);
      this.rawMouseInput = OptionInstance.createBoolean("options.rawMouseInput", true, (value) -> {
         Window window = Minecraft.getInstance().getWindow();
         if (window != null) {
            window.updateRawMouseInput(value);
         }

      });
      this.allowCursorChanges = OptionInstance.createBoolean("options.allowCursorChanges", OptionInstance.cachedConstantTooltip(ALLOW_CURSOR_CHANGES_TOOLTIP), true, (value) -> {
         Window window = Minecraft.getInstance().getWindow();
         if (window != null) {
            window.setAllowCursorChanges(value);
         }

      });
      this.glDebugVerbosity = 1;
      this.autoJump = OptionInstance.createBoolean("options.autoJump", false);
      this.rotateWithMinecart = OptionInstance.createBoolean("options.rotateWithMinecart", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_ROTATE_WITH_MINECART), false);
      this.operatorItemsTab = OptionInstance.createBoolean("options.operatorItemsTab", false);
      this.autoSuggestions = OptionInstance.createBoolean("options.autoSuggestCommands", true);
      this.chatColors = OptionInstance.createBoolean("options.chat.color", true);
      this.chatLinks = OptionInstance.createBoolean("options.chat.links", true);
      this.chatLinksPrompt = OptionInstance.createBoolean("options.chat.links.prompt", true);
      this.enableVsync = OptionInstance.createBoolean("options.vsync", true, (var0) -> Minecraft.getInstance().invalidateSurfaceConfiguration());
      this.entityShadows = OptionInstance.createBoolean("options.entityShadows", OptionInstance.noTooltip(), true, (var1) -> this.setGraphicsPresetToCustom());
      this.forceUnicodeFont = OptionInstance.createBoolean("options.forceUnicodeFont", false, (var0) -> updateFontOptions());
      this.japaneseGlyphVariants = OptionInstance.createBoolean("options.japaneseGlyphVariants", OptionInstance.cachedConstantTooltip(Component.translatable("options.japaneseGlyphVariants.tooltip")), japaneseGlyphVariantsDefault(), (var0) -> updateFontOptions());
      this.invertXMouse = OptionInstance.createBoolean("options.invertMouseX", false);
      this.invertYMouse = OptionInstance.createBoolean("options.invertMouseY", false);
      this.discreteMouseScroll = OptionInstance.createBoolean("options.discrete_mouse_scroll", false);
      this.realmsNotifications = OptionInstance.createBoolean("options.realmsNotifications.button", OptionInstance.cachedConstantTooltip(REALMS_NOTIFICATIONS_TOOLTIP), true);
      this.allowServerListing = OptionInstance.createBoolean("options.allowServerListing", OptionInstance.cachedConstantTooltip(ALLOW_SERVER_LISTING_TOOLTIP), true, OptionInstance.NO_ACTION);
      this.reducedDebugInfo = OptionInstance.createBoolean("options.reducedDebugInfo", OptionInstance.noTooltip(), false, (var0) -> Minecraft.getInstance().debugEntries.rebuildCurrentList());
      this.inGameNotification = OptionInstance.createBoolean("options.inGameNotification", OptionInstance.cachedConstantTooltip(IN_GAME_NOTIFICATION_TOOLTIP), false);
      this.sharePresence = new OptionInstance<PresenceSharing>("options.sharePresence", (value) -> Tooltip.create(value.getTooltip()), (var0, value) -> value.getTranslation(), new OptionInstance.Enum(List.of(PresenceSharing.values()), PresenceSharing.CODEC), PresenceSharing.CODEC, PresenceSharing.ALL, (var0) -> {
      });
      this.soundSourceVolumes = Util.<SoundSource, OptionInstance<Double>>makeEnumMap(SoundSource.class, (source) -> this.createSoundSliderOptionInstance("soundCategory." + source.getName(), source));
      this.showSubtitles = OptionInstance.createBoolean("options.showSubtitles", OptionInstance.cachedConstantTooltip(CLOSED_CAPTIONS_TOOLTIP), false);
      this.directionalAudio = OptionInstance.createBoolean("options.directionalAudio", (value) -> value ? Tooltip.create(DIRECTIONAL_AUDIO_TOOLTIP_ON) : Tooltip.create(DIRECTIONAL_AUDIO_TOOLTIP_OFF), false, (var0) -> {
         SoundManager soundManager = Minecraft.getInstance().getSoundManager();
         soundManager.reload();
         soundManager.play(SimpleSoundInstance.forUI((Holder)SoundEvents.UI_BUTTON_CLICK, 1.0F));
      });
      this.backgroundForChatOnly = new OptionInstance<Boolean>("options.accessibility.text_background", OptionInstance.noTooltip(), (caption, value) -> value ? Component.translatable("options.accessibility.text_background.chat") : Component.translatable("options.accessibility.text_background.everywhere"), OptionInstance.BOOLEAN_VALUES, true, OptionInstance.NO_ACTION);
      this.fullscreen = OptionInstance.createBoolean("options.fullscreen", false, (value) -> {
         Minecraft minecraft = Minecraft.getInstance();
         if (minecraft.getWindow() != null && minecraft.getWindow().isFullscreen() != value) {
            minecraft.getWindow().toggleFullScreen();
            this.fullscreen().set(minecraft.getWindow().isFullscreen());
         }

      });
      this.exclusiveFullscreen = OptionInstance.createBoolean("options.exclusiveFullscreen", (value) -> {
         List<Component> tooltipLines = new ArrayList();
         if (value != this.exclusiveFullscreenFromStartup) {
            tooltipLines.add(TOOLTIP_NEEDS_RESTART);
            tooltipLines.add(CommonComponents.EMPTY);
         }

         tooltipLines.add(value ? TOOLTIP_EXCLUSIVE_FULLSCREEN_ON : TOOLTIP_EXCLUSIVE_FULLSCREEN_OFF);
         return Tooltip.create(CommonComponents.joinLines((Collection)tooltipLines));
      }, false);
      this.bobView = OptionInstance.createBoolean("options.viewBobbing", true);
      this.toggleCrouch = new OptionInstance<Boolean>("key.sneak", OptionInstance.noTooltip(), (caption, value) -> value ? KEY_TOGGLE : KEY_HOLD, OptionInstance.BOOLEAN_VALUES, false, OptionInstance.NO_ACTION);
      this.toggleSprint = new OptionInstance<Boolean>("key.sprint", OptionInstance.noTooltip(), (caption, value) -> value ? KEY_TOGGLE : KEY_HOLD, OptionInstance.BOOLEAN_VALUES, false, OptionInstance.NO_ACTION);
      this.toggleAttack = new OptionInstance<Boolean>("key.attack", OptionInstance.noTooltip(), (caption, value) -> value ? KEY_TOGGLE : KEY_HOLD, OptionInstance.BOOLEAN_VALUES, false, OptionInstance.NO_ACTION);
      this.toggleUse = new OptionInstance<Boolean>("key.use", OptionInstance.noTooltip(), (caption, value) -> value ? KEY_TOGGLE : KEY_HOLD, OptionInstance.BOOLEAN_VALUES, false, OptionInstance.NO_ACTION);
      this.sprintWindow = new OptionInstance<Integer>("options.sprintWindow", OptionInstance.cachedConstantTooltip(SPRINT_WINDOW_TOOLTIP), (caption, value) -> value == 0 ? genericValueLabel(caption, Component.translatable("options.off")) : genericValueLabel(caption, Component.translatable("options.value", value)), new OptionInstance.IntRange(0, 10), 7, OptionInstance.NO_ACTION);
      this.hideMatchedNames = OptionInstance.createBoolean("options.hideMatchedNames", OptionInstance.cachedConstantTooltip(CHAT_TOOLTIP_HIDE_MATCHED_NAMES), true);
      this.showAutosaveIndicator = OptionInstance.createBoolean("options.autosaveIndicator", true);
      this.onlyShowSecureChat = OptionInstance.createBoolean("options.onlyShowSecureChat", OptionInstance.cachedConstantTooltip(CHAT_TOOLTIP_ONLY_SHOW_SECURE), false);
      this.saveChatDrafts = OptionInstance.createBoolean("options.chat.drafts", OptionInstance.cachedConstantTooltip(CHAT_TOOLTIP_SAVE_DRAFTS), false);
      this.keyUp = new KeyMapping("key.forward", 87, KeyMapping.Category.MOVEMENT);
      this.keyLeft = new KeyMapping("key.left", 65, KeyMapping.Category.MOVEMENT);
      this.keyDown = new KeyMapping("key.back", 83, KeyMapping.Category.MOVEMENT);
      this.keyRight = new KeyMapping("key.right", 68, KeyMapping.Category.MOVEMENT);
      this.keyJump = new KeyMapping("key.jump", 32, KeyMapping.Category.MOVEMENT);
      KeyMapping.Category var10005 = KeyMapping.Category.MOVEMENT;
      OptionInstance var10006 = this.toggleCrouch;
      Objects.requireNonNull(var10006);
      this.keyShift = new ToggleKeyMapping("key.sneak", 340, var10005, var10006::get, true);
      var10005 = KeyMapping.Category.MOVEMENT;
      var10006 = this.toggleSprint;
      Objects.requireNonNull(var10006);
      this.keySprint = new ToggleKeyMapping("key.sprint", 341, var10005, var10006::get, true);
      this.keyInventory = new KeyMapping("key.inventory", 69, KeyMapping.Category.INVENTORY);
      this.keySwapOffhand = new KeyMapping("key.swapOffhand", 70, KeyMapping.Category.INVENTORY);
      this.keyDrop = new KeyMapping("key.drop", 81, KeyMapping.Category.INVENTORY);
      InputConstants.Type var10004 = InputConstants.Type.MOUSE;
      KeyMapping.Category var7 = KeyMapping.Category.GAMEPLAY;
      OptionInstance var10007 = this.toggleUse;
      Objects.requireNonNull(var10007);
      this.keyUse = new ToggleKeyMapping("key.use", var10004, 1, var7, var10007::get, false);
      var10004 = InputConstants.Type.MOUSE;
      var7 = KeyMapping.Category.GAMEPLAY;
      var10007 = this.toggleAttack;
      Objects.requireNonNull(var10007);
      this.keyAttack = new ToggleKeyMapping("key.attack", var10004, 0, var7, var10007::get, true);
      this.keyPickItem = new KeyMapping("key.pickItem", InputConstants.Type.MOUSE, 2, KeyMapping.Category.GAMEPLAY);
      this.keyChat = new KeyMapping("key.chat", 84, KeyMapping.Category.MULTIPLAYER);
      this.keyPlayerList = new KeyMapping("key.playerlist", 258, KeyMapping.Category.MULTIPLAYER);
      this.keyCommand = new KeyMapping("key.command", 47, KeyMapping.Category.MULTIPLAYER);
      this.keyFriends = new KeyMapping("key.friends", 79, KeyMapping.Category.MULTIPLAYER);
      this.keySocialInteractions = new KeyMapping("key.socialInteractions", 80, KeyMapping.Category.MULTIPLAYER);
      this.keyScreenshot = new KeyMapping("key.screenshot", 291, KeyMapping.Category.MISC);
      this.keyTogglePerspective = new KeyMapping("key.togglePerspective", 294, KeyMapping.Category.MISC);
      this.keySmoothCamera = new KeyMapping("key.smoothCamera", InputConstants.UNKNOWN.getValue(), KeyMapping.Category.MISC);
      this.keyFullscreen = new KeyMapping("key.fullscreen", 300, KeyMapping.Category.MISC);
      this.keyAdvancements = new KeyMapping("key.advancements", 76, KeyMapping.Category.MISC);
      this.keyQuickActions = new KeyMapping("key.quickActions", 71, KeyMapping.Category.MISC);
      this.keyToggleGui = new KeyMapping("key.toggleGui", 290, KeyMapping.Category.MISC);
      this.keyToggleSpectatorShaderEffects = new KeyMapping("key.toggleSpectatorShaderEffects", 293, KeyMapping.Category.MISC);
      this.keyHotbarSlots = new KeyMapping[]{new KeyMapping("key.hotbar.1", 49, KeyMapping.Category.INVENTORY), new KeyMapping("key.hotbar.2", 50, KeyMapping.Category.INVENTORY), new KeyMapping("key.hotbar.3", 51, KeyMapping.Category.INVENTORY), new KeyMapping("key.hotbar.4", 52, KeyMapping.Category.INVENTORY), new KeyMapping("key.hotbar.5", 53, KeyMapping.Category.INVENTORY), new KeyMapping("key.hotbar.6", 54, KeyMapping.Category.INVENTORY), new KeyMapping("key.hotbar.7", 55, KeyMapping.Category.INVENTORY), new KeyMapping("key.hotbar.8", 56, KeyMapping.Category.INVENTORY), new KeyMapping("key.hotbar.9", 57, KeyMapping.Category.INVENTORY)};
      this.keySaveHotbarActivator = new KeyMapping("key.saveToolbarActivator", 67, KeyMapping.Category.CREATIVE);
      this.keyLoadHotbarActivator = new KeyMapping("key.loadToolbarActivator", 88, KeyMapping.Category.CREATIVE);
      this.keySpectatorOutlines = new KeyMapping("key.spectatorOutlines", InputConstants.UNKNOWN.getValue(), KeyMapping.Category.SPECTATOR);
      this.keySpectatorHotbar = new KeyMapping("key.spectatorHotbar", InputConstants.Type.MOUSE, 2, KeyMapping.Category.SPECTATOR);
      this.keyDebugOverlay = new KeyMapping("key.debug.overlay", InputConstants.Type.KEYSYM, 292, KeyMapping.Category.DEBUG, -2);
      this.keyDebugModifier = new KeyMapping("key.debug.modifier", InputConstants.Type.KEYSYM, 292, KeyMapping.Category.DEBUG, -1);
      this.keyDebugCrash = new KeyMapping("key.debug.crash", InputConstants.Type.KEYSYM, 67, KeyMapping.Category.DEBUG);
      this.keyDebugReloadChunk = new KeyMapping("key.debug.reloadChunk", InputConstants.Type.KEYSYM, 65, KeyMapping.Category.DEBUG);
      this.keyDebugShowHitboxes = new KeyMapping("key.debug.showHitboxes", InputConstants.Type.KEYSYM, 66, KeyMapping.Category.DEBUG);
      this.keyDebugClearChat = new KeyMapping("key.debug.clearChat", InputConstants.Type.KEYSYM, 68, KeyMapping.Category.DEBUG);
      this.keyDebugShowChunkBorders = new KeyMapping("key.debug.showChunkBorders", InputConstants.Type.KEYSYM, 71, KeyMapping.Category.DEBUG);
      this.keyDebugShowAdvancedTooltips = new KeyMapping("key.debug.showAdvancedTooltips", InputConstants.Type.KEYSYM, 72, KeyMapping.Category.DEBUG);
      this.keyDebugCopyRecreateCommand = new KeyMapping("key.debug.copyRecreateCommand", InputConstants.Type.KEYSYM, 73, KeyMapping.Category.DEBUG);
      this.keyDebugSpectate = new KeyMapping("key.debug.spectate", InputConstants.Type.KEYSYM, 78, KeyMapping.Category.DEBUG);
      this.keyDebugSwitchGameMode = new KeyMapping("key.debug.switchGameMode", InputConstants.Type.KEYSYM, 293, KeyMapping.Category.DEBUG);
      this.keyDebugDebugOptions = new KeyMapping("key.debug.debugOptions", InputConstants.Type.KEYSYM, 295, KeyMapping.Category.DEBUG);
      this.keyDebugFocusPause = new KeyMapping("key.debug.focusPause", InputConstants.Type.KEYSYM, 80, KeyMapping.Category.DEBUG);
      this.keyDebugDumpDynamicTextures = new KeyMapping("key.debug.dumpDynamicTextures", InputConstants.Type.KEYSYM, 83, KeyMapping.Category.DEBUG);
      this.keyDebugReloadResourcePacks = new KeyMapping("key.debug.reloadResourcePacks", InputConstants.Type.KEYSYM, 84, KeyMapping.Category.DEBUG);
      this.keyDebugProfiling = new KeyMapping("key.debug.profiling", InputConstants.Type.KEYSYM, 76, KeyMapping.Category.DEBUG);
      this.keyDebugCopyLocation = new KeyMapping("key.debug.copyLocation", InputConstants.Type.KEYSYM, 67, KeyMapping.Category.DEBUG);
      this.keyDebugDumpVersion = new KeyMapping("key.debug.dumpVersion", InputConstants.Type.KEYSYM, 86, KeyMapping.Category.DEBUG);
      this.keyDebugPofilingChart = new KeyMapping("key.debug.profilingChart", InputConstants.Type.KEYSYM, 49, KeyMapping.Category.DEBUG, 1);
      this.keyDebugFpsCharts = new KeyMapping("key.debug.fpsCharts", InputConstants.Type.KEYSYM, 50, KeyMapping.Category.DEBUG, 2);
      this.keyDebugNetworkCharts = new KeyMapping("key.debug.networkCharts", InputConstants.Type.KEYSYM, 51, KeyMapping.Category.DEBUG, 3);
      this.keyDebugLightmapTexture = new KeyMapping("key.debug.lightmapTexture", InputConstants.Type.KEYSYM, 52, KeyMapping.Category.DEBUG, 4);
      this.keyDebugSwitchTranslucencyMode = new KeyMapping("key.debug.improvedTransparency", InputConstants.Type.KEYSYM, 88, KeyMapping.Category.DEBUG);
      this.debugKeys = new KeyMapping[]{this.keyDebugReloadChunk, this.keyDebugShowHitboxes, this.keyDebugClearChat, this.keyDebugCrash, this.keyDebugShowChunkBorders, this.keyDebugShowAdvancedTooltips, this.keyDebugCopyRecreateCommand, this.keyDebugSpectate, this.keyDebugSwitchGameMode, this.keyDebugDebugOptions, this.keyDebugFocusPause, this.keyDebugDumpDynamicTextures, this.keyDebugReloadResourcePacks, this.keyDebugProfiling, this.keyDebugCopyLocation, this.keyDebugDumpVersion, this.keyDebugPofilingChart, this.keyDebugFpsCharts, this.keyDebugNetworkCharts, this.keyDebugLightmapTexture, this.keyDebugSwitchTranslucencyMode};
      this.keyMappings = (KeyMapping[])Stream.of(new KeyMapping[]{this.keyAttack, this.keyUse, this.keyUp, this.keyLeft, this.keyDown, this.keyRight, this.keyJump, this.keyShift, this.keySprint, this.keyDrop, this.keyInventory, this.keyChat, this.keyPlayerList, this.keyPickItem, this.keyCommand, this.keyFriends, this.keySocialInteractions, this.keyToggleGui, this.keyToggleSpectatorShaderEffects, this.keyScreenshot, this.keyTogglePerspective, this.keySmoothCamera, this.keyFullscreen, this.keySpectatorOutlines, this.keySpectatorHotbar, this.keySwapOffhand, this.keySaveHotbarActivator, this.keyLoadHotbarActivator, this.keyAdvancements, this.keyQuickActions, this.keyDebugOverlay, this.keyDebugModifier}, this.keyHotbarSlots, this.debugKeys).flatMap(Stream::of).toArray((x$0) -> new KeyMapping[x$0]);
      this.cameraType = CameraType.FIRST_PERSON;
      this.lastMpIp = "";
      this.fov = new OptionInstance<Integer>("options.fov", OptionInstance.noTooltip(), (caption, value) -> {
         Component var10000;
         switch (value) {
            case 70 -> var10000 = genericValueLabel(caption, Component.translatable("options.fov.min"));
            case 110 -> var10000 = genericValueLabel(caption, Component.translatable("options.fov.max"));
            default -> var10000 = genericValueLabel(caption, value);
         }

         return var10000;
      }, new OptionInstance.IntRange(30, 110), Codec.DOUBLE.xmap((value) -> (int)(value * 40.0 + 70.0), (value) -> ((double)value - 70.0) / 40.0), 70, OptionInstance.NO_ACTION);
      this.telemetryOptInExtra = OptionInstance.createBoolean("options.telemetry.button", OptionInstance.cachedConstantTooltip(TELEMETRY_TOOLTIP), (caption, value) -> {
         Minecraft minecraft = Minecraft.getInstance();
         if (!minecraft.allowsTelemetry()) {
            return Component.translatable("options.telemetry.state.none");
         } else {
            return value && minecraft.extraTelemetryAvailable() ? Component.translatable("options.telemetry.state.all") : Component.translatable("options.telemetry.state.minimal");
         }
      }, false, OptionInstance.NO_ACTION);
      this.screenEffectScale = new OptionInstance<Double>("options.screenEffectScale", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_SCREEN_EFFECT), Options::percentValueOrOffLabel, OptionInstance.UnitDouble.INSTANCE, 1.0, OptionInstance.NO_ACTION);
      this.fovEffectScale = new OptionInstance<Double>("options.fovEffectScale", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_FOV_EFFECT), Options::percentValueOrOffLabel, OptionInstance.UnitDouble.INSTANCE.xmap(Mth::square, Math::sqrt), Codec.doubleRange(0.0, 1.0), 1.0, OptionInstance.NO_ACTION);
      this.darknessEffectScale = new OptionInstance<Double>("options.darknessEffectScale", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_DARKNESS_EFFECT), Options::percentValueOrOffLabel, OptionInstance.UnitDouble.INSTANCE.xmap(Mth::square, Math::sqrt), 1.0, OptionInstance.NO_ACTION);
      this.glintSpeed = new OptionInstance<Double>("options.glintSpeed", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_GLINT_SPEED), Options::percentValueOrOffLabel, OptionInstance.UnitDouble.INSTANCE, 0.5, OptionInstance.NO_ACTION);
      this.glintStrength = new OptionInstance<Double>("options.glintStrength", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_GLINT_STRENGTH), Options::percentValueOrOffLabel, OptionInstance.UnitDouble.INSTANCE, 0.75, OptionInstance.NO_ACTION);
      this.damageTiltStrength = new OptionInstance<Double>("options.damageTiltStrength", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_DAMAGE_TILT_STRENGTH), Options::percentValueOrOffLabel, OptionInstance.UnitDouble.INSTANCE, 1.0, OptionInstance.NO_ACTION);
      this.gamma = new OptionInstance<Double>("options.gamma", OptionInstance.noTooltip(), (caption, value) -> {
         int progressValueToDisplay = (int)(value * 100.0);
         if (progressValueToDisplay == 0) {
            return genericValueLabel(caption, Component.translatable("options.gamma.min"));
         } else if (progressValueToDisplay == 50) {
            return genericValueLabel(caption, Component.translatable("options.gamma.default"));
         } else {
            return progressValueToDisplay == 100 ? genericValueLabel(caption, Component.translatable("options.gamma.max")) : genericValueLabel(caption, progressValueToDisplay);
         }
      }, OptionInstance.UnitDouble.INSTANCE, 0.5, OptionInstance.NO_ACTION);
      this.guiScale = new OptionInstance<Integer>("options.guiScale", OptionInstance.noTooltip(), (caption, value) -> value == 0 ? Component.translatable("options.guiScale.auto") : Component.literal(Integer.toString(value)), new OptionInstance.ClampingLazyMaxIntRange(0, () -> {
         Minecraft minecraft = Minecraft.getInstance();
         return !minecraft.isRunning() ? 2147483646 : minecraft.getWindow().calculateScale(0, minecraft.isEnforceUnicode());
      }, 2147483646), 0, (var1) -> this.minecraft.resizeGui());
      this.particles = new OptionInstance<ParticleStatus>("options.particles", OptionInstance.noTooltip(), (caption, value) -> value.caption(), new OptionInstance.Enum(Arrays.asList(ParticleStatus.values()), ParticleStatus.LEGACY_CODEC), ParticleStatus.ALL, (var1) -> this.setGraphicsPresetToCustom());
      this.narrator = new OptionInstance<NarratorStatus>("options.narrator", OptionInstance.noTooltip(), (caption, value) -> (Component)(this.minecraft.getNarrator().isActive() ? value.getName() : Component.translatable("options.narrator.notavailable")), new OptionInstance.Enum(Arrays.asList(NarratorStatus.values()), NarratorStatus.LEGACY_CODEC), NarratorStatus.OFF, (value) -> this.minecraft.getNarrator().updateNarratorStatus(value));
      this.languageCode = "en_us";
      this.soundDevice = new OptionInstance<String>("options.audioDevice", OptionInstance.noTooltip(), (caption, value) -> {
         if ("".equals(value)) {
            return Component.translatable("options.audioDevice.default");
         } else {
            return value.startsWith("OpenAL Soft on ") ? Component.literal(value.substring(SoundEngine.OPEN_AL_SOFT_PREFIX_LENGTH)) : Component.literal(value);
         }
      }, new OptionInstance.LazyEnum(() -> Stream.concat(Stream.of(""), Minecraft.getInstance().getSoundManager().getAvailableSoundDevices().stream()).toList(), (device) -> Minecraft.getInstance().isRunning() && !isSoundDeviceDefault(device) && !Minecraft.getInstance().getSoundManager().getAvailableSoundDevices().contains(device) ? Optional.empty() : Optional.of(device), Codec.STRING), "", (var0) -> {
         SoundManager soundManager = Minecraft.getInstance().getSoundManager();
         soundManager.reload();
         soundManager.play(SimpleSoundInstance.forUI((Holder)SoundEvents.UI_BUTTON_CLICK, 1.0F));
      });
      this.onboardAccessibility = true;
      this.musicFrequency = new OptionInstance<MusicManager.MusicFrequency>("options.music_frequency", OptionInstance.cachedConstantTooltip(MUSIC_FREQUENCY_TOOLTIP), (caption, value) -> value.caption(), new OptionInstance.Enum(Arrays.asList(MusicManager.MusicFrequency.values()), MusicManager.MusicFrequency.CODEC), MusicManager.MusicFrequency.DEFAULT, (value) -> Minecraft.getInstance().getMusicManager().setMinutesBetweenSongs(value));
      this.musicToast = new OptionInstance<MusicToastDisplayState>("options.musicToast", (value) -> Tooltip.create(value.tooltip()), (caption, value) -> value.text(), new OptionInstance.Enum(Arrays.asList(MusicToastDisplayState.values()), MusicToastDisplayState.CODEC), MusicToastDisplayState.NEVER, (value) -> this.minecraft.gui.toastManager().setMusicToastDisplayState(value));
      this.startedCleanly = true;
      this.minecraft = minecraft;
      this.optionsFile = new File(workingDirectory, "options.txt");
      boolean largeDistances = Runtime.getRuntime().maxMemory() >= 1000000000L;
      this.renderDistance = new OptionInstance<Integer>("options.renderDistance", OptionInstance.noTooltip(), (caption, value) -> genericValueLabel(caption, Component.translatable("options.chunks", value)), new OptionInstance.IntRange(2, largeDistances ? 32 : 16, false), 12, (var1) -> this.setGraphicsPresetToCustom());
      this.simulationDistance = new OptionInstance<Integer>("options.simulationDistance", OptionInstance.noTooltip(), (caption, value) -> genericValueLabel(caption, Component.translatable("options.chunks", value)), new OptionInstance.IntRange(SharedConstants.DEBUG_ALLOW_LOW_SIM_DISTANCE ? 2 : 5, largeDistances ? 32 : 16, false), 12, (var1) -> this.setGraphicsPresetToCustom());
      this.syncWrites = Util.getPlatform() == Util.OS.WINDOWS;
      this.load();
   }

   public float getBackgroundOpacity(final float defaultOpacity) {
      return (Boolean)this.backgroundForChatOnly.get() ? defaultOpacity : ((Double)this.textBackgroundOpacity().get()).floatValue();
   }

   public int getBackgroundColor(final float defaultOpacity) {
      return ARGB.colorFromFloat(this.getBackgroundOpacity(defaultOpacity), 0.0F, 0.0F, 0.0F);
   }

   public int getBackgroundColor(final int defaultColor) {
      return (Boolean)this.backgroundForChatOnly.get() ? defaultColor : ARGB.colorFromFloat(((Double)this.textBackgroundOpacity.get()).floatValue(), 0.0F, 0.0F, 0.0F);
   }

   private void processDumpedOptions(final OptionAccess access) {
      access.process("ao", this.ambientOcclusion);
      access.process("biomeBlendRadius", this.biomeBlendRadius);
      access.process("chunkSectionFadeInTime", this.chunkSectionFadeInTime);
      access.process("cutoutLeaves", this.cutoutLeaves);
      access.process("enableVsync", this.enableVsync);
      access.process("entityDistanceScaling", this.entityDistanceScaling);
      access.process("entityShadows", this.entityShadows);
      access.process("forceUnicodeFont", this.forceUnicodeFont);
      access.process("japaneseGlyphVariants", this.japaneseGlyphVariants);
      access.process("fov", this.fov);
      access.process("fovEffectScale", this.fovEffectScale);
      access.process("darknessEffectScale", this.darknessEffectScale);
      access.process("glintSpeed", this.glintSpeed);
      access.process("glintStrength", this.glintStrength);
      access.process("preferredGraphicsBackend", this.preferredGraphicsBackend);
      access.process("graphicsPreset", this.graphicsPreset);
      access.process("prioritizeChunkUpdates", this.prioritizeChunkUpdates);
      access.process("fullscreen", this.fullscreen);
      access.process("exclusiveFullscreen", this.exclusiveFullscreen);
      access.process("gamma", this.gamma);
      access.process("guiScale", this.guiScale);
      access.process("maxAnisotropyBit", this.maxAnisotropyBit);
      access.process("textureFiltering", this.textureFiltering);
      access.process("maxFps", this.framerateLimit);
      access.process("improvedTransparency", this.improvedTransparency);
      access.process("inactivityFpsLimit", this.inactivityFpsLimit);
      access.process("mipmapLevels", this.mipmapLevels);
      access.process("narrator", this.narrator);
      access.process("particles", this.particles);
      access.process("reducedDebugInfo", this.reducedDebugInfo);
      access.process("renderClouds", this.cloudStatus);
      access.process("cloudRange", this.cloudRange);
      access.process("renderDistance", this.renderDistance);
      access.process("simulationDistance", this.simulationDistance);
      access.process("screenEffectScale", this.screenEffectScale);
      access.process("soundDevice", this.soundDevice);
      access.process("vignette", this.vignette);
      access.process("weatherRadius", this.weatherRadius);
   }

   private void processOptions(final FieldAccess access) {
      this.processDumpedOptions(access);
      access.process("autoJump", this.autoJump);
      access.process("rotateWithMinecart", this.rotateWithMinecart);
      access.process("operatorItemsTab", this.operatorItemsTab);
      access.process("autoSuggestions", this.autoSuggestions);
      access.process("chatColors", this.chatColors);
      access.process("chatLinks", this.chatLinks);
      access.process("chatLinksPrompt", this.chatLinksPrompt);
      access.process("discrete_mouse_scroll", this.discreteMouseScroll);
      access.process("invertXMouse", this.invertXMouse);
      access.process("invertYMouse", this.invertYMouse);
      access.process("realmsNotifications", this.realmsNotifications);
      access.process("showSubtitles", this.showSubtitles);
      access.process("directionalAudio", this.directionalAudio);
      access.process("bobView", this.bobView);
      access.process("toggleCrouch", this.toggleCrouch);
      access.process("toggleSprint", this.toggleSprint);
      access.process("toggleAttack", this.toggleAttack);
      access.process("toggleUse", this.toggleUse);
      access.process("sprintWindow", this.sprintWindow);
      access.process("darkMojangStudiosBackground", this.darkMojangStudiosBackground);
      access.process("hideLightningFlashes", this.hideLightningFlash);
      access.process("hideSplashTexts", this.hideSplashTexts);
      access.process("mouseSensitivity", this.sensitivity);
      access.process("damageTiltStrength", this.damageTiltStrength);
      access.process("highContrast", this.highContrast);
      access.process("highContrastBlockOutline", this.highContrastBlockOutline);
      access.process("narratorHotkey", this.narratorHotkey);
      List var10003 = this.resourcePacks;
      Function var10004 = Options::readListOfStrings;
      Gson var10005 = GSON;
      Objects.requireNonNull(var10005);
      this.resourcePacks = (List)access.process("resourcePacks", var10003, var10004, var10005::toJson);
      var10003 = this.incompatibleResourcePacks;
      var10004 = Options::readListOfStrings;
      var10005 = GSON;
      Objects.requireNonNull(var10005);
      this.incompatibleResourcePacks = (List)access.process("incompatibleResourcePacks", var10003, var10004, var10005::toJson);
      this.lastMpIp = access.process("lastServer", this.lastMpIp);
      this.languageCode = access.process("lang", this.languageCode);
      access.process("chatVisibility", this.chatVisibility);
      access.process("chatOpacity", this.chatOpacity);
      access.process("chatLineSpacing", this.chatLineSpacing);
      access.process("textBackgroundOpacity", this.textBackgroundOpacity);
      access.process("backgroundForChatOnly", this.backgroundForChatOnly);
      this.hideServerAddress = access.process("hideServerAddress", this.hideServerAddress);
      this.advancedItemTooltips = access.process("advancedItemTooltips", this.advancedItemTooltips);
      this.pauseOnLostFocus = access.process("pauseOnLostFocus", this.pauseOnLostFocus);
      this.overrideWidth = access.process("overrideWidth", this.overrideWidth);
      this.overrideHeight = access.process("overrideHeight", this.overrideHeight);
      access.process("chatHeightFocused", this.chatHeightFocused);
      access.process("chatDelay", this.chatDelay);
      access.process("chatHeightUnfocused", this.chatHeightUnfocused);
      access.process("chatScale", this.chatScale);
      access.process("chatWidth", this.chatWidth);
      access.process("notificationDisplayTime", this.notificationDisplayTime);
      this.useNativeTransport = access.process("useNativeTransport", this.useNativeTransport);
      access.process("mainHand", this.mainHand);
      access.process("attackIndicator", this.attackIndicator);
      this.tutorialStep = (TutorialSteps)access.process("tutorialStep", this.tutorialStep, TutorialSteps::getByName, TutorialSteps::getName);
      access.process("mouseWheelSensitivity", this.mouseWheelSensitivity);
      access.process("rawMouseInput", this.rawMouseInput);
      access.process("allowCursorChanges", this.allowCursorChanges);
      this.glDebugVerbosity = access.process("glDebugVerbosity", this.glDebugVerbosity);
      this.skipMultiplayerWarning = access.process("skipMultiplayerWarning", this.skipMultiplayerWarning);
      access.process("hideMatchedNames", this.hideMatchedNames);
      this.joinedFirstServer = access.process("joinedFirstServer", this.joinedFirstServer);
      this.syncWrites = access.process("syncChunkWrites", this.syncWrites);
      access.process("showAutosaveIndicator", this.showAutosaveIndicator);
      access.process("allowServerListing", this.allowServerListing);
      access.process("inGameNotification", this.inGameNotification);
      access.process("sharePresence", this.sharePresence);
      access.process("onlyShowSecureChat", this.onlyShowSecureChat);
      access.process("saveChatDrafts", this.saveChatDrafts);
      access.process("panoramaScrollSpeed", this.panoramaSpeed);
      access.process("telemetryOptInExtra", this.telemetryOptInExtra);
      this.onboardAccessibility = access.process("onboardAccessibility", this.onboardAccessibility);
      access.process("menuBackgroundBlurriness", this.menuBackgroundBlurriness);
      this.startedCleanly = access.process("startedCleanly", this.startedCleanly);
      access.process("musicToast", this.musicToast);
      access.process("musicFrequency", this.musicFrequency);

      for(KeyMapping keyMapping : this.keyMappings) {
         String currentValue = keyMapping.saveString();
         String newValue = access.process("key_" + keyMapping.getName(), currentValue);
         if (!currentValue.equals(newValue)) {
            keyMapping.setKey(InputConstants.getKey(newValue));
         }
      }

      for(SoundSource source : SoundSource.values()) {
         access.process("soundCategory_" + source.getName(), (OptionInstance)this.soundSourceVolumes.get(source));
      }

      for(PlayerModelPart part : PlayerModelPart.values()) {
         boolean wasEnabled = this.modelParts.contains(part);
         boolean isEnabled = access.process("modelPart_" + part.getId(), wasEnabled);
         if (isEnabled != wasEnabled) {
            this.setModelPart(part, isEnabled);
         }
      }

   }

   public void load() {
      try {
         if (!this.optionsFile.exists()) {
            return;
         }

         CompoundTag rawOptions = new CompoundTag();
         BufferedReader reader = Files.newReader(this.optionsFile, StandardCharsets.UTF_8);

         try {
            reader.lines().forEach((line) -> {
               try {
                  Iterator<String> iterator = OPTION_SPLITTER.split(line).iterator();
                  rawOptions.putString((String)iterator.next(), (String)iterator.next());
               } catch (Exception var3) {
                  LOGGER.warn("Skipping bad option: {}", line);
               }

            });
         } catch (Throwable var6) {
            if (reader != null) {
               try {
                  reader.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (reader != null) {
            reader.close();
         }

         final CompoundTag options = this.dataFix(rawOptions);
         this.processOptions(new FieldAccess() {
            {
               Objects.requireNonNull(Options.this);
            }

            private @Nullable String getValue(final String name) {
               Tag tag = options.get(name);
               if (tag == null) {
                  return null;
               } else if (tag instanceof StringTag) {
                  StringTag var3 = (StringTag)tag;
                  StringTag var10000 = var3;

                  try {
                     var7 = var10000.value();
                  } catch (Throwable var6) {
                     throw new MatchException(var6.toString(), var6);
                  }

                  String value = var7;
                  return value;
               } else {
                  throw new IllegalStateException("Cannot read field of wrong type, expected string: " + String.valueOf(tag));
               }
            }

            public <T> void process(final String name, final OptionInstance<T> option) {
               String result = this.getValue(name);
               if (result != null) {
                  JsonElement element = LenientJsonParser.parse(result.isEmpty() ? "\"\"" : result);
                  DataResult var10000 = option.codec().parse(JsonOps.INSTANCE, element).ifError((error) -> Options.LOGGER.error("Error parsing option value {} for option {}: {}", new Object[]{result, option, error.message()}));
                  Objects.requireNonNull(option);
                  var10000.ifSuccess(option::set);
               }

            }

            public int process(final String name, final int current) {
               String result = this.getValue(name);
               if (result != null) {
                  try {
                     return Integer.parseInt(result);
                  } catch (NumberFormatException e) {
                     Options.LOGGER.warn("Invalid integer value for option {} = {}", new Object[]{name, result, e});
                  }
               }

               return current;
            }

            public boolean process(final String name, final boolean current) {
               String result = this.getValue(name);
               return result != null ? Options.isTrue(result) : current;
            }

            public String process(final String name, final String current) {
               return (String)MoreObjects.firstNonNull(this.getValue(name), current);
            }

            public float process(final String name, final float current) {
               String result = this.getValue(name);
               if (result != null) {
                  if (Options.isTrue(result)) {
                     return 1.0F;
                  }

                  if (Options.isFalse(result)) {
                     return 0.0F;
                  }

                  try {
                     return Float.parseFloat(result);
                  } catch (NumberFormatException e) {
                     Options.LOGGER.warn("Invalid floating point value for option {} = {}", new Object[]{name, result, e});
                  }
               }

               return current;
            }

            public <T> T process(final String name, final T current, final Function<String, T> reader, final Function<T, String> writer) {
               String rawResult = this.getValue(name);
               return (T)(rawResult == null ? current : reader.apply(rawResult));
            }
         });
         options.getString("fullscreenResolution").ifPresent((fullscreenResolution) -> this.fullscreenVideoModeString = fullscreenResolution);
         KeyMapping.resetMapping();
      } catch (Exception e) {
         LOGGER.error("Failed to load options", e);
      }

      this.preferredGraphicsBackendFromStartup = this.preferredGraphicsBackend.get();
      this.exclusiveFullscreenFromStartup = (Boolean)this.exclusiveFullscreen.get();
   }

   private static boolean isTrue(final String value) {
      return "true".equals(value);
   }

   private static boolean isFalse(final String value) {
      return "false".equals(value);
   }

   private CompoundTag dataFix(final CompoundTag tag) {
      int version = 0;

      try {
         version = (Integer)tag.getString("version").map(Integer::parseInt).orElse(0);
      } catch (RuntimeException var4) {
      }

      return DataFixTypes.OPTIONS.updateToCurrentVersion(this.minecraft.getFixerUpper(), tag, version);
   }

   public void save() {
      try {
         final PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.optionsFile), StandardCharsets.UTF_8));

         try {
            writer.println("version:" + SharedConstants.getCurrentVersion().dataVersion().version());
            this.processOptions(new FieldAccess() {
               {
                  Objects.requireNonNull(Options.this);
               }

               public void writePrefix(final String name) {
                  writer.print(name);
                  writer.print(':');
               }

               public <T> void process(final String name, final OptionInstance<T> option) {
                  option.codec().encodeStart(JsonOps.INSTANCE, option.get()).ifError((error) -> Options.LOGGER.error("Error saving option {}: {}", option, error.message())).ifSuccess((element) -> {
                     this.writePrefix(name);
                     writer.println(Options.GSON.toJson(element));
                  });
               }

               public int process(final String name, final int value) {
                  this.writePrefix(name);
                  writer.println(value);
                  return value;
               }

               public boolean process(final String name, final boolean value) {
                  this.writePrefix(name);
                  writer.println(value);
                  return value;
               }

               public String process(final String name, final String value) {
                  this.writePrefix(name);
                  writer.println(value);
                  return value;
               }

               public float process(final String name, final float value) {
                  this.writePrefix(name);
                  writer.println(value);
                  return value;
               }

               public <T> T process(final String name, final T value, final Function<String, T> reader, final Function<T, String> converter) {
                  this.writePrefix(name);
                  writer.println((String)converter.apply(value));
                  return value;
               }
            });
            String fullscreenVideoModeString = this.getFullscreenVideoModeString();
            if (fullscreenVideoModeString != null) {
               writer.println("fullscreenResolution:" + fullscreenVideoModeString);
            }
         } catch (Throwable var5) {
            try {
               writer.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }

            throw var5;
         }

         writer.close();
      } catch (Exception e) {
         LOGGER.error("Failed to save options", e);
      }

      this.broadcastOptions();
   }

   private @Nullable String getFullscreenVideoModeString() {
      Window window = this.minecraft.getWindow();
      if (window == null) {
         return this.fullscreenVideoModeString;
      } else {
         return window.getPreferredFullscreenVideoMode().isPresent() ? ((VideoMode)window.getPreferredFullscreenVideoMode().get()).write() : null;
      }
   }

   public ClientInformation buildPlayerInformation() {
      int parts = 0;

      for(PlayerModelPart part : this.modelParts) {
         parts |= part.getMask();
      }

      return new ClientInformation(this.languageCode, (Integer)this.renderDistance.get(), this.chatVisibility.get(), (Boolean)this.chatColors.get(), parts, this.mainHand.get(), this.minecraft.isTextFilteringEnabled(), (Boolean)this.allowServerListing.get(), this.particles.get());
   }

   public void broadcastOptions() {
      if (this.minecraft.player != null) {
         this.minecraft.player.connection.broadcastClientInformation(this.buildPlayerInformation());
      }

   }

   public void setModelPart(final PlayerModelPart part, final boolean visible) {
      if (visible) {
         this.modelParts.add(part);
      } else {
         this.modelParts.remove(part);
      }

   }

   public boolean isModelPartEnabled(final PlayerModelPart part) {
      return this.modelParts.contains(part);
   }

   public CloudStatus getCloudStatus() {
      return this.cloudStatus.get();
   }

   public boolean useNativeTransport() {
      return this.useNativeTransport;
   }

   public void loadSelectedResourcePacks(final PackRepository repository) {
      Set<String> selected = Sets.newLinkedHashSet();
      Iterator<String> iterator = this.resourcePacks.iterator();

      while(iterator.hasNext()) {
         String id = (String)iterator.next();
         Pack pack = repository.getPack(id);
         if (pack == null && !id.startsWith("file/")) {
            pack = repository.getPack("file/" + id);
         }

         if (pack == null) {
            LOGGER.warn("Removed resource pack {} from options because it doesn't seem to exist anymore", id);
            iterator.remove();
         } else if (!pack.getCompatibility().isCompatible() && !this.incompatibleResourcePacks.contains(id)) {
            LOGGER.warn("Removed resource pack {} from options because it is no longer compatible", id);
            iterator.remove();
         } else if (pack.getCompatibility().isCompatible() && this.incompatibleResourcePacks.contains(id)) {
            LOGGER.info("Removed resource pack {} from incompatibility list because it's now compatible", id);
            this.incompatibleResourcePacks.remove(id);
         } else {
            selected.add(pack.getId());
         }
      }

      repository.setSelected(selected);
   }

   public CameraType getCameraType() {
      return this.cameraType;
   }

   public void setCameraType(final CameraType cameraType) {
      this.cameraType = cameraType;
   }

   private static List<String> readListOfStrings(final String value) {
      List<String> result = (List)GsonHelper.fromNullableJson(GSON, value, LIST_OF_STRINGS_TYPE);
      return (List<String>)(result != null ? result : Lists.newArrayList());
   }

   public File getFile() {
      return this.optionsFile;
   }

   public String dumpOptionsForReport() {
      final List<Pair<String, Object>> optionsForReport = new ArrayList();
      this.processDumpedOptions(new OptionAccess() {
         {
            Objects.requireNonNull(Options.this);
         }

         public <T> void process(final String name, final OptionInstance<T> option) {
            optionsForReport.add(Pair.of(name, option.get()));
         }
      });
      optionsForReport.add(Pair.of("fullscreenResolution", String.valueOf(this.fullscreenVideoModeString)));
      optionsForReport.add(Pair.of("glDebugVerbosity", this.glDebugVerbosity));
      optionsForReport.add(Pair.of("overrideHeight", this.overrideHeight));
      optionsForReport.add(Pair.of("overrideWidth", this.overrideWidth));
      optionsForReport.add(Pair.of("syncChunkWrites", this.syncWrites));
      optionsForReport.add(Pair.of("useNativeTransport", this.useNativeTransport));
      optionsForReport.add(Pair.of("resourcePacks", this.resourcePacks));
      return (String)optionsForReport.stream().sorted(Comparator.comparing(Pair::getFirst)).map((e) -> {
         String var10000 = (String)e.getFirst();
         return var10000 + ": " + String.valueOf(e.getSecond());
      }).collect(Collectors.joining(System.lineSeparator()));
   }

   public void setServerRenderDistance(final int serverRenderDistance) {
      this.serverRenderDistance = serverRenderDistance;
   }

   public int getEffectiveRenderDistance() {
      return this.serverRenderDistance > 0 ? Math.min((Integer)this.renderDistance.get(), this.serverRenderDistance) : (Integer)this.renderDistance.get();
   }

   private static Component pixelValueLabel(final Component caption, final int value) {
      return Component.translatable("options.pixel_value", caption, value);
   }

   private static Component percentValueLabel(final Component caption, final double value) {
      return Component.translatable("options.percent_value", caption, (int)(value * 100.0));
   }

   public static Component genericValueLabel(final Component caption, final Component value) {
      return Component.translatable("options.generic_value", caption, value);
   }

   public static Component genericValueLabel(final Component caption, final int value) {
      return genericValueLabel(caption, Component.literal(Integer.toString(value)));
   }

   public static Component genericValueOrOffLabel(final Component caption, final int value) {
      return value == 0 ? genericValueLabel(caption, CommonComponents.OPTION_OFF) : genericValueLabel(caption, value);
   }

   private static Component percentValueOrOffLabel(final Component caption, final double value) {
      return value == 0.0 ? genericValueLabel(caption, CommonComponents.OPTION_OFF) : percentValueLabel(caption, value);
   }

   private interface FieldAccess extends OptionAccess {
      int process(String name, int value);

      boolean process(String name, boolean value);

      String process(String name, String value);

      float process(String name, float value);

      <T> T process(String name, T value, Function<String, T> reader, Function<T, String> writer);
   }

   private interface OptionAccess {
      <T> void process(String name, OptionInstance<T> option);
   }
}
