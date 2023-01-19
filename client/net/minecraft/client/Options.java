package net.minecraft.client;

import com.google.common.base.Charsets;
import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
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
import com.mojang.blaze3d.platform.Window;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
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
import net.minecraft.client.multiplayer.chat.ChatPreviewStatus;
import net.minecraft.client.renderer.GpuWarnlistManager;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.tutorial.TutorialSteps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ServerboundClientInformationPacket;
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
   private static final Component ACCESSIBILITY_TOOLTIP_DARK_MOJANG_BACKGROUND = Component.translatable("options.darkMojangStudiosBackgroundColor.tooltip");
   private final OptionInstance<Boolean> darkMojangStudiosBackground = OptionInstance.createBoolean(
      "options.darkMojangStudiosBackgroundColor", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_DARK_MOJANG_BACKGROUND), false
   );
   private static final Component ACCESSIBILITY_TOOLTIP_HIDE_LIGHTNING_FLASHES = Component.translatable("options.hideLightningFlashes.tooltip");
   private final OptionInstance<Boolean> hideLightningFlash = OptionInstance.createBoolean(
      "options.hideLightningFlashes", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_HIDE_LIGHTNING_FLASHES), false
   );
   private final OptionInstance<Double> sensitivity = new OptionInstance<>("options.sensitivity", OptionInstance.noTooltip(), (var0, var1x) -> {
      if (var1x == 0.0) {
         return genericValueLabel(var0, Component.translatable("options.sensitivity.min"));
      } else {
         return var1x == 1.0 ? genericValueLabel(var0, Component.translatable("options.sensitivity.max")) : percentValueLabel(var0, 2.0 * var1x);
      }
   }, OptionInstance.UnitDouble.INSTANCE, 0.5, var0 -> {
   });
   private final OptionInstance<Integer> renderDistance;
   private final OptionInstance<Integer> simulationDistance;
   private int serverRenderDistance = 0;
   private final OptionInstance<Double> entityDistanceScaling = new OptionInstance<>(
      "options.entityDistanceScaling",
      OptionInstance.noTooltip(),
      Options::percentValueLabel,
      new OptionInstance.IntRange(2, 20).xmap(var0 -> (double)var0 / 4.0, var0 -> (int)(var0 * 4.0)),
      Codec.doubleRange(0.5, 5.0),
      1.0,
      var0 -> {
      }
   );
   public static final int UNLIMITED_FRAMERATE_CUTOFF = 260;
   private final OptionInstance<Integer> framerateLimit = new OptionInstance<>(
      "options.framerateLimit",
      OptionInstance.noTooltip(),
      (var0, var1x) -> var1x == 260
            ? genericValueLabel(var0, Component.translatable("options.framerateLimit.max"))
            : genericValueLabel(var0, Component.translatable("options.framerate", var1x)),
      new OptionInstance.IntRange(1, 26).xmap(var0 -> var0 * 10, var0 -> var0 / 10),
      Codec.intRange(10, 260),
      120,
      var0 -> Minecraft.getInstance().getWindow().setFramerateLimit(var0)
   );
   private final OptionInstance<CloudStatus> cloudStatus = new OptionInstance<>(
      "options.renderClouds",
      OptionInstance.noTooltip(),
      OptionInstance.forOptionEnum(),
      new OptionInstance.Enum<>(
         Arrays.asList(CloudStatus.values()),
         Codec.either(Codec.BOOL, Codec.STRING).xmap(var0 -> (CloudStatus)var0.map(var0x -> var0x ? CloudStatus.FANCY : CloudStatus.OFF, var0x -> {
               return switch(var0x) {
                  case "true" -> CloudStatus.FANCY;
                  case "fast" -> CloudStatus.FAST;
                  default -> CloudStatus.OFF;
               };
            }), var0 -> {
            return Either.right(switch(var0) {
               case FANCY -> "true";
               case FAST -> "fast";
               case OFF -> "false";
            });
         })
      ),
      CloudStatus.FANCY,
      var0 -> {
         if (Minecraft.useShaderTransparency()) {
            RenderTarget var1x = Minecraft.getInstance().levelRenderer.getCloudsTarget();
            if (var1x != null) {
               var1x.clear(Minecraft.ON_OSX);
            }
         }
      }
   );
   private static final Component GRAPHICS_TOOLTIP_FAST = Component.translatable("options.graphics.fast.tooltip");
   private static final Component GRAPHICS_TOOLTIP_FABULOUS = Component.translatable(
      "options.graphics.fabulous.tooltip", Component.translatable("options.graphics.fabulous").withStyle(ChatFormatting.ITALIC)
   );
   private static final Component GRAPHICS_TOOLTIP_FANCY = Component.translatable("options.graphics.fancy.tooltip");
   private final OptionInstance<GraphicsStatus> graphicsMode = new OptionInstance<>(
      "options.graphics",
      var0 -> {
         List var1x = OptionInstance.splitTooltip(var0, GRAPHICS_TOOLTIP_FAST);
         List var2x = OptionInstance.splitTooltip(var0, GRAPHICS_TOOLTIP_FANCY);
         List var3x = OptionInstance.splitTooltip(var0, GRAPHICS_TOOLTIP_FABULOUS);
         return var3xx -> {
            return switch(var3xx) {
               case FANCY -> var2x;
               case FAST -> var1x;
               case FABULOUS -> var3x;
            };
         };
      },
      (var0, var1x) -> {
         MutableComponent var2x = Component.translatable(var1x.getKey());
         return var1x == GraphicsStatus.FABULOUS ? var2x.withStyle(ChatFormatting.ITALIC) : var2x;
      },
      new OptionInstance.AltEnum<>(
         Arrays.asList(GraphicsStatus.values()),
         Stream.of(GraphicsStatus.values()).filter(var0 -> var0 != GraphicsStatus.FABULOUS).collect(Collectors.toList()),
         () -> Minecraft.getInstance().isRunning() && Minecraft.getInstance().getGpuWarnlistManager().isSkippingFabulous(),
         (var0, var1x) -> {
            Minecraft var2x = Minecraft.getInstance();
            GpuWarnlistManager var3x = var2x.getGpuWarnlistManager();
            if (var1x == GraphicsStatus.FABULOUS && var3x.willShowWarning()) {
               var3x.showWarning();
            } else {
               var0.set(var1x);
               var2x.levelRenderer.allChanged();
            }
         },
         Codec.INT.xmap(GraphicsStatus::byId, GraphicsStatus::getId)
      ),
      GraphicsStatus.FANCY,
      var0 -> {
      }
   );
   private final OptionInstance<AmbientOcclusionStatus> ambientOcclusion = new OptionInstance<>(
      "options.ao",
      OptionInstance.noTooltip(),
      OptionInstance.forOptionEnum(),
      new OptionInstance.Enum<>(
         Arrays.asList(AmbientOcclusionStatus.values()),
         Codec.either(
               Codec.BOOL
                  .xmap(
                     var0 -> var0 ? AmbientOcclusionStatus.MAX.getId() : AmbientOcclusionStatus.OFF.getId(),
                     var0 -> var0 == AmbientOcclusionStatus.MAX.getId()
                  ),
               Codec.INT
            )
            .xmap(var0 -> (Integer)var0.map(var0x -> var0x, var0x -> var0x), Either::right)
            .xmap(AmbientOcclusionStatus::byId, AmbientOcclusionStatus::getId)
      ),
      AmbientOcclusionStatus.MAX,
      var0 -> Minecraft.getInstance().levelRenderer.allChanged()
   );
   private static final Component PRIORITIZE_CHUNK_TOOLTIP_NONE = Component.translatable("options.prioritizeChunkUpdates.none.tooltip");
   private static final Component PRIORITIZE_CHUNK_TOOLTIP_PLAYER_AFFECTED = Component.translatable("options.prioritizeChunkUpdates.byPlayer.tooltip");
   private static final Component PRIORITIZE_CHUNK_TOOLTIP_NEARBY = Component.translatable("options.prioritizeChunkUpdates.nearby.tooltip");
   private final OptionInstance<PrioritizeChunkUpdates> prioritizeChunkUpdates = new OptionInstance<>(
      "options.prioritizeChunkUpdates",
      var0 -> {
         List var1x = OptionInstance.splitTooltip(var0, PRIORITIZE_CHUNK_TOOLTIP_NONE);
         List var2x = OptionInstance.splitTooltip(var0, PRIORITIZE_CHUNK_TOOLTIP_PLAYER_AFFECTED);
         List var3x = OptionInstance.splitTooltip(var0, PRIORITIZE_CHUNK_TOOLTIP_NEARBY);
         return var3xx -> {
            return switch(var3xx) {
               case NONE -> var1x;
               case PLAYER_AFFECTED -> var2x;
               case NEARBY -> var3x;
            };
         };
      },
      OptionInstance.forOptionEnum(),
      new OptionInstance.Enum<>(Arrays.asList(PrioritizeChunkUpdates.values()), Codec.INT.xmap(PrioritizeChunkUpdates::byId, PrioritizeChunkUpdates::getId)),
      PrioritizeChunkUpdates.NONE,
      var0 -> {
      }
   );
   public List<String> resourcePacks = Lists.newArrayList();
   public List<String> incompatibleResourcePacks = Lists.newArrayList();
   private final OptionInstance<ChatVisiblity> chatVisibility = new OptionInstance<>(
      "options.chat.visibility",
      OptionInstance.noTooltip(),
      OptionInstance.forOptionEnum(),
      new OptionInstance.Enum<>(Arrays.asList(ChatVisiblity.values()), Codec.INT.xmap(ChatVisiblity::byId, ChatVisiblity::getId)),
      ChatVisiblity.FULL,
      var0 -> {
      }
   );
   private final OptionInstance<Double> chatOpacity = new OptionInstance<>(
      "options.chat.opacity",
      OptionInstance.noTooltip(),
      (var0, var1x) -> percentValueLabel(var0, var1x * 0.9 + 0.1),
      OptionInstance.UnitDouble.INSTANCE,
      1.0,
      var0 -> Minecraft.getInstance().gui.getChat().rescaleChat()
   );
   private final OptionInstance<Double> chatLineSpacing = new OptionInstance<>(
      "options.chat.line_spacing", OptionInstance.noTooltip(), Options::percentValueLabel, OptionInstance.UnitDouble.INSTANCE, 0.0, var0 -> {
      }
   );
   private final OptionInstance<Double> textBackgroundOpacity = new OptionInstance<>(
      "options.accessibility.text_background_opacity",
      OptionInstance.noTooltip(),
      Options::percentValueLabel,
      OptionInstance.UnitDouble.INSTANCE,
      0.5,
      var0 -> Minecraft.getInstance().gui.getChat().rescaleChat()
   );
   @Nullable
   public String fullscreenVideoModeString;
   public boolean hideServerAddress;
   public boolean advancedItemTooltips;
   public boolean pauseOnLostFocus = true;
   private final Set<PlayerModelPart> modelParts = EnumSet.allOf(PlayerModelPart.class);
   private final OptionInstance<HumanoidArm> mainHand = new OptionInstance<>(
      "options.mainHand",
      OptionInstance.noTooltip(),
      OptionInstance.forOptionEnum(),
      new OptionInstance.Enum<>(
         Arrays.asList(HumanoidArm.values()),
         Codec.STRING.xmap(var0 -> "left".equals(var0) ? HumanoidArm.LEFT : HumanoidArm.RIGHT, var0 -> var0 == HumanoidArm.LEFT ? "left" : "right")
      ),
      HumanoidArm.RIGHT,
      var1x -> this.broadcastOptions()
   );
   public int overrideWidth;
   public int overrideHeight;
   public boolean heldItemTooltips = true;
   private final OptionInstance<Double> chatScale = new OptionInstance<>(
      "options.chat.scale",
      OptionInstance.noTooltip(),
      (var0, var1x) -> (Component)(var1x == 0.0 ? CommonComponents.optionStatus(var0, false) : percentValueLabel(var0, var1x)),
      OptionInstance.UnitDouble.INSTANCE,
      1.0,
      var0 -> Minecraft.getInstance().gui.getChat().rescaleChat()
   );
   private final OptionInstance<Double> chatWidth = new OptionInstance<>(
      "options.chat.width",
      OptionInstance.noTooltip(),
      (var0, var1x) -> pixelValueLabel(var0, ChatComponent.getWidth(var1x)),
      OptionInstance.UnitDouble.INSTANCE,
      1.0,
      var0 -> Minecraft.getInstance().gui.getChat().rescaleChat()
   );
   private final OptionInstance<Double> chatHeightUnfocused = new OptionInstance<>(
      "options.chat.height.unfocused",
      OptionInstance.noTooltip(),
      (var0, var1x) -> pixelValueLabel(var0, ChatComponent.getHeight(var1x)),
      OptionInstance.UnitDouble.INSTANCE,
      ChatComponent.defaultUnfocusedPct(),
      var0 -> Minecraft.getInstance().gui.getChat().rescaleChat()
   );
   private final OptionInstance<Double> chatHeightFocused = new OptionInstance<>(
      "options.chat.height.focused",
      OptionInstance.noTooltip(),
      (var0, var1x) -> pixelValueLabel(var0, ChatComponent.getHeight(var1x)),
      OptionInstance.UnitDouble.INSTANCE,
      1.0,
      var0 -> Minecraft.getInstance().gui.getChat().rescaleChat()
   );
   private final OptionInstance<Double> chatDelay = new OptionInstance<>(
      "options.chat.delay_instant",
      OptionInstance.noTooltip(),
      (var0, var1x) -> var1x <= 0.0
            ? Component.translatable("options.chat.delay_none")
            : Component.translatable("options.chat.delay", String.format(Locale.ROOT, "%.1f", var1x)),
      new OptionInstance.IntRange(0, 60).xmap(var0 -> (double)var0 / 10.0, var0 -> (int)(var0 * 10.0)),
      Codec.doubleRange(0.0, 6.0),
      0.0,
      var0 -> Minecraft.getInstance().getChatListener().setMessageDelay(var0)
   );
   private final OptionInstance<Integer> mipmapLevels = new OptionInstance<>(
      "options.mipmapLevels",
      OptionInstance.noTooltip(),
      (var0, var1x) -> (Component)(var1x == 0 ? CommonComponents.optionStatus(var0, false) : genericValueLabel(var0, var1x)),
      new OptionInstance.IntRange(0, 4),
      4,
      var0 -> {
      }
   );
   private final Object2FloatMap<SoundSource> sourceVolumes = Util.make(new Object2FloatOpenHashMap(), var0 -> var0.defaultReturnValue(1.0F));
   public boolean useNativeTransport = true;
   private final OptionInstance<AttackIndicatorStatus> attackIndicator = new OptionInstance<>(
      "options.attackIndicator",
      OptionInstance.noTooltip(),
      OptionInstance.forOptionEnum(),
      new OptionInstance.Enum<>(Arrays.asList(AttackIndicatorStatus.values()), Codec.INT.xmap(AttackIndicatorStatus::byId, AttackIndicatorStatus::getId)),
      AttackIndicatorStatus.CROSSHAIR,
      var0 -> {
      }
   );
   public TutorialSteps tutorialStep = TutorialSteps.MOVEMENT;
   public boolean joinedFirstServer = false;
   public boolean hideBundleTutorial = false;
   private final OptionInstance<Integer> biomeBlendRadius = new OptionInstance<>("options.biomeBlendRadius", OptionInstance.noTooltip(), (var0, var1x) -> {
      int var2x = var1x * 2 + 1;
      return genericValueLabel(var0, Component.translatable("options.biomeBlendRadius." + var2x));
   }, new OptionInstance.IntRange(0, 7), 2, var0 -> Minecraft.getInstance().levelRenderer.allChanged());
   private final OptionInstance<Double> mouseWheelSensitivity = new OptionInstance<>(
      "options.mouseWheelSensitivity",
      OptionInstance.noTooltip(),
      (var0, var1x) -> genericValueLabel(var0, Component.literal(String.format(Locale.ROOT, "%.2f", var1x))),
      new OptionInstance.IntRange(-200, 100).xmap(Options::logMouse, Options::unlogMouse),
      Codec.doubleRange(logMouse(-200), logMouse(100)),
      logMouse(0),
      var0 -> {
      }
   );
   private final OptionInstance<Boolean> rawMouseInput = OptionInstance.createBoolean("options.rawMouseInput", true, var0 -> {
      Window var1x = Minecraft.getInstance().getWindow();
      if (var1x != null) {
         var1x.updateRawMouseInput(var0);
      }
   });
   public int glDebugVerbosity = 1;
   private final OptionInstance<Boolean> autoJump = OptionInstance.createBoolean("options.autoJump", true);
   private final OptionInstance<Boolean> autoSuggestions = OptionInstance.createBoolean("options.autoSuggestCommands", true);
   private final OptionInstance<Boolean> chatColors = OptionInstance.createBoolean("options.chat.color", true);
   private final OptionInstance<Boolean> chatLinks = OptionInstance.createBoolean("options.chat.links", true);
   private final OptionInstance<Boolean> chatLinksPrompt = OptionInstance.createBoolean("options.chat.links.prompt", true);
   private final OptionInstance<Boolean> enableVsync = OptionInstance.createBoolean("options.vsync", true, var0 -> {
      if (Minecraft.getInstance().getWindow() != null) {
         Minecraft.getInstance().getWindow().updateVsync(var0);
      }
   });
   private final OptionInstance<Boolean> entityShadows = OptionInstance.createBoolean("options.entityShadows", true);
   private final OptionInstance<Boolean> forceUnicodeFont = OptionInstance.createBoolean("options.forceUnicodeFont", false, var0 -> {
      Minecraft var1x = Minecraft.getInstance();
      if (var1x.getWindow() != null) {
         var1x.selectMainFont(var0);
         var1x.resizeDisplay();
      }
   });
   private final OptionInstance<Boolean> invertYMouse = OptionInstance.createBoolean("options.invertMouse", false);
   private final OptionInstance<Boolean> discreteMouseScroll = OptionInstance.createBoolean("options.discrete_mouse_scroll", false);
   private final OptionInstance<Boolean> realmsNotifications = OptionInstance.createBoolean("options.realmsNotifications", true);
   private static final Component ALLOW_SERVER_LISTING_TOOLTIP = Component.translatable("options.allowServerListing.tooltip");
   private final OptionInstance<Boolean> allowServerListing = OptionInstance.createBoolean(
      "options.allowServerListing", OptionInstance.cachedConstantTooltip(ALLOW_SERVER_LISTING_TOOLTIP), true, var1x -> this.broadcastOptions()
   );
   private final OptionInstance<Boolean> reducedDebugInfo = OptionInstance.createBoolean("options.reducedDebugInfo", false);
   private final OptionInstance<Boolean> showSubtitles = OptionInstance.createBoolean("options.showSubtitles", false);
   private static final Component DIRECTIONAL_AUDIO_TOOLTIP_ON = Component.translatable("options.directionalAudio.on.tooltip");
   private static final Component DIRECTIONAL_AUDIO_TOOLTIP_OFF = Component.translatable("options.directionalAudio.off.tooltip");
   private final OptionInstance<Boolean> directionalAudio = OptionInstance.createBoolean("options.directionalAudio", var0 -> {
      List var1x = OptionInstance.splitTooltip(var0, DIRECTIONAL_AUDIO_TOOLTIP_ON);
      List var2x = OptionInstance.splitTooltip(var0, DIRECTIONAL_AUDIO_TOOLTIP_OFF);
      return var2xx -> var2xx ? var1x : var2x;
   }, false, var0 -> {
      SoundManager var1x = Minecraft.getInstance().getSoundManager();
      var1x.reload();
      var1x.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
   });
   private final OptionInstance<Boolean> backgroundForChatOnly = new OptionInstance<>(
      "options.accessibility.text_background",
      OptionInstance.noTooltip(),
      (var0, var1x) -> var1x
            ? Component.translatable("options.accessibility.text_background.chat")
            : Component.translatable("options.accessibility.text_background.everywhere"),
      OptionInstance.BOOLEAN_VALUES,
      true,
      var0 -> {
      }
   );
   private final OptionInstance<Boolean> touchscreen = OptionInstance.createBoolean("options.touchscreen", false);
   private final OptionInstance<Boolean> fullscreen = OptionInstance.createBoolean("options.fullscreen", false, var1x -> {
      Minecraft var2x = Minecraft.getInstance();
      if (var2x.getWindow() != null && var2x.getWindow().isFullscreen() != var1x) {
         var2x.getWindow().toggleFullScreen();
         this.fullscreen().set(var2x.getWindow().isFullscreen());
      }
   });
   private final OptionInstance<Boolean> bobView = OptionInstance.createBoolean("options.viewBobbing", true);
   private static final Component MOVEMENT_TOGGLE = Component.translatable("options.key.toggle");
   private static final Component MOVEMENT_HOLD = Component.translatable("options.key.hold");
   private final OptionInstance<Boolean> toggleCrouch = new OptionInstance<>(
      "key.sneak", OptionInstance.noTooltip(), (var0, var1x) -> var1x ? MOVEMENT_TOGGLE : MOVEMENT_HOLD, OptionInstance.BOOLEAN_VALUES, false, var0 -> {
      }
   );
   private final OptionInstance<Boolean> toggleSprint = new OptionInstance<>(
      "key.sprint", OptionInstance.noTooltip(), (var0, var1x) -> var1x ? MOVEMENT_TOGGLE : MOVEMENT_HOLD, OptionInstance.BOOLEAN_VALUES, false, var0 -> {
      }
   );
   public boolean skipMultiplayerWarning;
   public boolean skipRealms32bitWarning;
   private static final Component CHAT_TOOLTIP_HIDE_MATCHED_NAMES = Component.translatable("options.hideMatchedNames.tooltip");
   private final OptionInstance<Boolean> hideMatchedNames = OptionInstance.createBoolean(
      "options.hideMatchedNames", OptionInstance.cachedConstantTooltip(CHAT_TOOLTIP_HIDE_MATCHED_NAMES), true
   );
   private final OptionInstance<Boolean> showAutosaveIndicator = OptionInstance.createBoolean("options.autosaveIndicator", true);
   private static final Component CHAT_PREVIEW_OFF_TOOLTIP = Component.translatable("options.chatPreview.tooltip.off");
   private static final Component CHAT_PREVIEW_LIVE_TOOLTIP = Component.translatable("options.chatPreview.tooltip.live");
   private static final Component CHAT_PREVIEW_CONFIRM_TOOLTIP = Component.translatable("options.chatPreview.tooltip.confirm");
   private final OptionInstance<ChatPreviewStatus> chatPreview = new OptionInstance<>(
      "options.chatPreview",
      var0 -> var1x -> {
            return switch(var1x) {
               case OFF -> OptionInstance.splitTooltip(var0, CHAT_PREVIEW_OFF_TOOLTIP);
               case LIVE -> OptionInstance.splitTooltip(var0, CHAT_PREVIEW_LIVE_TOOLTIP);
               case CONFIRM -> OptionInstance.splitTooltip(var0, CHAT_PREVIEW_CONFIRM_TOOLTIP);
            };
         },
      OptionInstance.forOptionEnum(),
      new OptionInstance.Enum<>(Arrays.asList(ChatPreviewStatus.values()), Codec.INT.xmap(ChatPreviewStatus::byId, ChatPreviewStatus::getId)),
      ChatPreviewStatus.LIVE,
      var0 -> {
      }
   );
   private static final Component CHAT_TOOLTIP_ONLY_SHOW_SECURE = Component.translatable("options.onlyShowSecureChat.tooltip");
   private final OptionInstance<Boolean> onlyShowSecureChat = OptionInstance.createBoolean(
      "options.onlyShowSecureChat", OptionInstance.cachedConstantTooltip(CHAT_TOOLTIP_ONLY_SHOW_SECURE), false
   );
   public final KeyMapping keyUp = new KeyMapping("key.forward", 87, "key.categories.movement");
   public final KeyMapping keyLeft = new KeyMapping("key.left", 65, "key.categories.movement");
   public final KeyMapping keyDown = new KeyMapping("key.back", 83, "key.categories.movement");
   public final KeyMapping keyRight = new KeyMapping("key.right", 68, "key.categories.movement");
   public final KeyMapping keyJump = new KeyMapping("key.jump", 32, "key.categories.movement");
   public final KeyMapping keyShift = new ToggleKeyMapping("key.sneak", 340, "key.categories.movement", this.toggleCrouch::get);
   public final KeyMapping keySprint = new ToggleKeyMapping("key.sprint", 341, "key.categories.movement", this.toggleSprint::get);
   public final KeyMapping keyInventory = new KeyMapping("key.inventory", 69, "key.categories.inventory");
   public final KeyMapping keySwapOffhand = new KeyMapping("key.swapOffhand", 70, "key.categories.inventory");
   public final KeyMapping keyDrop = new KeyMapping("key.drop", 81, "key.categories.inventory");
   public final KeyMapping keyUse = new KeyMapping("key.use", InputConstants.Type.MOUSE, 1, "key.categories.gameplay");
   public final KeyMapping keyAttack = new KeyMapping("key.attack", InputConstants.Type.MOUSE, 0, "key.categories.gameplay");
   public final KeyMapping keyPickItem = new KeyMapping("key.pickItem", InputConstants.Type.MOUSE, 2, "key.categories.gameplay");
   public final KeyMapping keyChat = new KeyMapping("key.chat", 84, "key.categories.multiplayer");
   public final KeyMapping keyPlayerList = new KeyMapping("key.playerlist", 258, "key.categories.multiplayer");
   public final KeyMapping keyCommand = new KeyMapping("key.command", 47, "key.categories.multiplayer");
   public final KeyMapping keySocialInteractions = new KeyMapping("key.socialInteractions", 80, "key.categories.multiplayer");
   public final KeyMapping keyScreenshot = new KeyMapping("key.screenshot", 291, "key.categories.misc");
   public final KeyMapping keyTogglePerspective = new KeyMapping("key.togglePerspective", 294, "key.categories.misc");
   public final KeyMapping keySmoothCamera = new KeyMapping("key.smoothCamera", InputConstants.UNKNOWN.getValue(), "key.categories.misc");
   public final KeyMapping keyFullscreen = new KeyMapping("key.fullscreen", 300, "key.categories.misc");
   public final KeyMapping keySpectatorOutlines = new KeyMapping("key.spectatorOutlines", InputConstants.UNKNOWN.getValue(), "key.categories.misc");
   public final KeyMapping keyAdvancements = new KeyMapping("key.advancements", 76, "key.categories.misc");
   public final KeyMapping[] keyHotbarSlots = new KeyMapping[]{
      new KeyMapping("key.hotbar.1", 49, "key.categories.inventory"),
      new KeyMapping("key.hotbar.2", 50, "key.categories.inventory"),
      new KeyMapping("key.hotbar.3", 51, "key.categories.inventory"),
      new KeyMapping("key.hotbar.4", 52, "key.categories.inventory"),
      new KeyMapping("key.hotbar.5", 53, "key.categories.inventory"),
      new KeyMapping("key.hotbar.6", 54, "key.categories.inventory"),
      new KeyMapping("key.hotbar.7", 55, "key.categories.inventory"),
      new KeyMapping("key.hotbar.8", 56, "key.categories.inventory"),
      new KeyMapping("key.hotbar.9", 57, "key.categories.inventory")
   };
   public final KeyMapping keySaveHotbarActivator = new KeyMapping("key.saveToolbarActivator", 67, "key.categories.creative");
   public final KeyMapping keyLoadHotbarActivator = new KeyMapping("key.loadToolbarActivator", 88, "key.categories.creative");
   public final KeyMapping[] keyMappings = (KeyMapping[])ArrayUtils.addAll(
      new KeyMapping[]{
         this.keyAttack,
         this.keyUse,
         this.keyUp,
         this.keyLeft,
         this.keyDown,
         this.keyRight,
         this.keyJump,
         this.keyShift,
         this.keySprint,
         this.keyDrop,
         this.keyInventory,
         this.keyChat,
         this.keyPlayerList,
         this.keyPickItem,
         this.keyCommand,
         this.keySocialInteractions,
         this.keyScreenshot,
         this.keyTogglePerspective,
         this.keySmoothCamera,
         this.keyFullscreen,
         this.keySpectatorOutlines,
         this.keySwapOffhand,
         this.keySaveHotbarActivator,
         this.keyLoadHotbarActivator,
         this.keyAdvancements
      },
      this.keyHotbarSlots
   );
   protected Minecraft minecraft;
   private final File optionsFile;
   public boolean hideGui;
   private CameraType cameraType = CameraType.FIRST_PERSON;
   public boolean renderDebug;
   public boolean renderDebugCharts;
   public boolean renderFpsChart;
   public String lastMpIp = "";
   public boolean smoothCamera;
   private final OptionInstance<Integer> fov = new OptionInstance<>(
      "options.fov",
      OptionInstance.noTooltip(),
      (var0, var1x) -> {
         return switch(var1x) {
            case 70 -> genericValueLabel(var0, Component.translatable("options.fov.min"));
            case 110 -> genericValueLabel(var0, Component.translatable("options.fov.max"));
            default -> genericValueLabel(var0, var1x);
         };
      },
      new OptionInstance.IntRange(30, 110),
      Codec.DOUBLE.xmap(var0 -> (int)(var0 * 40.0 + 70.0), var0 -> ((double)var0.intValue() - 70.0) / 40.0),
      70,
      var0 -> Minecraft.getInstance().levelRenderer.needsUpdate()
   );
   private static final Component ACCESSIBILITY_TOOLTIP_SCREEN_EFFECT = Component.translatable("options.screenEffectScale.tooltip");
   private final OptionInstance<Double> screenEffectScale = new OptionInstance<>(
      "options.screenEffectScale",
      OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_SCREEN_EFFECT),
      (var0, var1x) -> var1x == 0.0 ? genericValueLabel(var0, CommonComponents.OPTION_OFF) : percentValueLabel(var0, var1x),
      OptionInstance.UnitDouble.INSTANCE,
      1.0,
      var0 -> {
      }
   );
   private static final Component ACCESSIBILITY_TOOLTIP_FOV_EFFECT = Component.translatable("options.fovEffectScale.tooltip");
   private final OptionInstance<Double> fovEffectScale = new OptionInstance<>(
      "options.fovEffectScale",
      OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_FOV_EFFECT),
      (var0, var1x) -> var1x == 0.0 ? genericValueLabel(var0, CommonComponents.OPTION_OFF) : percentValueLabel(var0, var1x),
      OptionInstance.UnitDouble.INSTANCE.xmap(Mth::square, Math::sqrt),
      Codec.doubleRange(0.0, 1.0),
      1.0,
      var0 -> {
      }
   );
   private static final Component ACCESSIBILITY_TOOLTIP_DARKNESS_EFFECT = Component.translatable("options.darknessEffectScale.tooltip");
   private final OptionInstance<Double> darknessEffectScale = new OptionInstance<>(
      "options.darknessEffectScale",
      OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_DARKNESS_EFFECT),
      (var0, var1x) -> var1x == 0.0 ? genericValueLabel(var0, CommonComponents.OPTION_OFF) : percentValueLabel(var0, var1x),
      OptionInstance.UnitDouble.INSTANCE.xmap(Mth::square, Math::sqrt),
      1.0,
      var0 -> {
      }
   );
   private final OptionInstance<Double> gamma = new OptionInstance<>("options.gamma", OptionInstance.noTooltip(), (var0, var1x) -> {
      int var2x = (int)(var1x * 100.0);
      if (var2x == 0) {
         return genericValueLabel(var0, Component.translatable("options.gamma.min"));
      } else if (var2x == 50) {
         return genericValueLabel(var0, Component.translatable("options.gamma.default"));
      } else {
         return var2x == 100 ? genericValueLabel(var0, Component.translatable("options.gamma.max")) : genericValueLabel(var0, var2x);
      }
   }, OptionInstance.UnitDouble.INSTANCE, 0.5, var0 -> {
   });
   private final OptionInstance<Integer> guiScale = new OptionInstance<>(
      "options.guiScale",
      OptionInstance.noTooltip(),
      (var0, var1x) -> var1x == 0 ? Component.translatable("options.guiScale.auto") : Component.literal(Integer.toString(var1x)),
      new OptionInstance.ClampingLazyMaxIntRange(0, () -> {
         Minecraft var0 = Minecraft.getInstance();
         return !var0.isRunning() ? 2147483646 : var0.getWindow().calculateScale(0, var0.isEnforceUnicode());
      }),
      0,
      var0 -> {
      }
   );
   private final OptionInstance<ParticleStatus> particles = new OptionInstance<>(
      "options.particles",
      OptionInstance.noTooltip(),
      OptionInstance.forOptionEnum(),
      new OptionInstance.Enum<>(Arrays.asList(ParticleStatus.values()), Codec.INT.xmap(ParticleStatus::byId, ParticleStatus::getId)),
      ParticleStatus.ALL,
      var0 -> {
      }
   );
   private final OptionInstance<NarratorStatus> narrator = new OptionInstance<>(
      "options.narrator",
      OptionInstance.noTooltip(),
      (var1x, var2x) -> (Component)(this.minecraft.getNarrator().isActive() ? var2x.getName() : Component.translatable("options.narrator.notavailable")),
      new OptionInstance.Enum<>(Arrays.asList(NarratorStatus.values()), Codec.INT.xmap(NarratorStatus::byId, NarratorStatus::getId)),
      NarratorStatus.OFF,
      var1x -> this.minecraft.getNarrator().updateNarratorStatus(var1x)
   );
   public String languageCode = "en_us";
   private final OptionInstance<String> soundDevice = new OptionInstance<>(
      "options.audioDevice",
      OptionInstance.noTooltip(),
      (var0, var1x) -> {
         if ("".equals(var1x)) {
            return Component.translatable("options.audioDevice.default");
         } else {
            return var1x.startsWith("OpenAL Soft on ") ? Component.literal(var1x.substring(SoundEngine.OPEN_AL_SOFT_PREFIX_LENGTH)) : Component.literal(var1x);
         }
      },
      new OptionInstance.LazyEnum<>(
         () -> Stream.concat(Stream.of(""), Minecraft.getInstance().getSoundManager().getAvailableSoundDevices().stream()).toList(),
         var0 -> Minecraft.getInstance().isRunning() && var0 != "" && !Minecraft.getInstance().getSoundManager().getAvailableSoundDevices().contains(var0)
               ? Optional.empty()
               : Optional.of(var0),
         Codec.STRING
      ),
      "",
      var0 -> {
         SoundManager var1x = Minecraft.getInstance().getSoundManager();
         var1x.reload();
         var1x.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
      }
   );
   public boolean syncWrites;

   public OptionInstance<Boolean> darkMojangStudiosBackground() {
      return this.darkMojangStudiosBackground;
   }

   public OptionInstance<Boolean> hideLightningFlash() {
      return this.hideLightningFlash;
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

   public OptionInstance<AmbientOcclusionStatus> ambientOcclusion() {
      return this.ambientOcclusion;
   }

   public OptionInstance<PrioritizeChunkUpdates> prioritizeChunkUpdates() {
      return this.prioritizeChunkUpdates;
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

   public OptionInstance<Double> textBackgroundOpacity() {
      return this.textBackgroundOpacity;
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

   public OptionInstance<Boolean> forceUnicodeFont() {
      return this.forceUnicodeFont;
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

   public OptionInstance<ChatPreviewStatus> chatPreview() {
      return this.chatPreview;
   }

   public OptionInstance<Boolean> onlyShowSecureChat() {
      return this.onlyShowSecureChat;
   }

   public OptionInstance<Integer> fov() {
      return this.fov;
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
      this.minecraft = var1;
      this.optionsFile = new File(var2, "options.txt");
      boolean var3 = var1.is64Bit();
      boolean var4 = var3 && Runtime.getRuntime().maxMemory() >= 1000000000L;
      this.renderDistance = new OptionInstance<>(
         "options.renderDistance",
         OptionInstance.noTooltip(),
         (var0, var1x) -> genericValueLabel(var0, Component.translatable("options.chunks", var1x)),
         new OptionInstance.IntRange(2, var4 ? 32 : 16),
         var3 ? 12 : 8,
         var0 -> Minecraft.getInstance().levelRenderer.needsUpdate()
      );
      this.simulationDistance = new OptionInstance<>(
         "options.simulationDistance",
         OptionInstance.noTooltip(),
         (var0, var1x) -> genericValueLabel(var0, Component.translatable("options.chunks", var1x)),
         new OptionInstance.IntRange(5, var4 ? 32 : 16),
         var3 ? 12 : 8,
         var0 -> {
         }
      );
      this.syncWrites = Util.getPlatform() == Util.OS.WINDOWS;
      this.load();
   }

   public float getBackgroundOpacity(float var1) {
      return this.backgroundForChatOnly.get() ? var1 : this.textBackgroundOpacity().get().floatValue();
   }

   public int getBackgroundColor(float var1) {
      return (int)(this.getBackgroundOpacity(var1) * 255.0F) << 24 & 0xFF000000;
   }

   public int getBackgroundColor(int var1) {
      return this.backgroundForChatOnly.get() ? var1 : (int)(this.textBackgroundOpacity.get() * 255.0) << 24 & 0xFF000000;
   }

   public void setKey(KeyMapping var1, InputConstants.Key var2) {
      var1.setKey(var2);
      this.save();
   }

   private void processOptions(Options.FieldAccess var1) {
      var1.process("autoJump", this.autoJump);
      var1.process("autoSuggestions", this.autoSuggestions);
      var1.process("chatColors", this.chatColors);
      var1.process("chatLinks", this.chatLinks);
      var1.process("chatLinksPrompt", this.chatLinksPrompt);
      var1.process("enableVsync", this.enableVsync);
      var1.process("entityShadows", this.entityShadows);
      var1.process("forceUnicodeFont", this.forceUnicodeFont);
      var1.process("discrete_mouse_scroll", this.discreteMouseScroll);
      var1.process("invertYMouse", this.invertYMouse);
      var1.process("realmsNotifications", this.realmsNotifications);
      var1.process("reducedDebugInfo", this.reducedDebugInfo);
      var1.process("showSubtitles", this.showSubtitles);
      var1.process("directionalAudio", this.directionalAudio);
      var1.process("touchscreen", this.touchscreen);
      var1.process("fullscreen", this.fullscreen);
      var1.process("bobView", this.bobView);
      var1.process("toggleCrouch", this.toggleCrouch);
      var1.process("toggleSprint", this.toggleSprint);
      var1.process("darkMojangStudiosBackground", this.darkMojangStudiosBackground);
      var1.process("hideLightningFlashes", this.hideLightningFlash);
      var1.process("mouseSensitivity", this.sensitivity);
      var1.process("fov", this.fov);
      var1.process("screenEffectScale", this.screenEffectScale);
      var1.process("fovEffectScale", this.fovEffectScale);
      var1.process("darknessEffectScale", this.darknessEffectScale);
      var1.process("gamma", this.gamma);
      var1.process("renderDistance", this.renderDistance);
      var1.process("simulationDistance", this.simulationDistance);
      var1.process("entityDistanceScaling", this.entityDistanceScaling);
      var1.process("guiScale", this.guiScale);
      var1.process("particles", this.particles);
      var1.process("maxFps", this.framerateLimit);
      var1.process("graphicsMode", this.graphicsMode);
      var1.process("ao", this.ambientOcclusion);
      var1.process("prioritizeChunkUpdates", this.prioritizeChunkUpdates);
      var1.process("biomeBlendRadius", this.biomeBlendRadius);
      var1.process("renderClouds", this.cloudStatus);
      this.resourcePacks = var1.process("resourcePacks", this.resourcePacks, Options::readPackList, GSON::toJson);
      this.incompatibleResourcePacks = var1.process("incompatibleResourcePacks", this.incompatibleResourcePacks, Options::readPackList, GSON::toJson);
      this.lastMpIp = var1.process("lastServer", this.lastMpIp);
      this.languageCode = var1.process("lang", this.languageCode);
      var1.process("soundDevice", this.soundDevice);
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
      this.heldItemTooltips = var1.process("heldItemTooltips", this.heldItemTooltips);
      var1.process("chatHeightFocused", this.chatHeightFocused);
      var1.process("chatDelay", this.chatDelay);
      var1.process("chatHeightUnfocused", this.chatHeightUnfocused);
      var1.process("chatScale", this.chatScale);
      var1.process("chatWidth", this.chatWidth);
      var1.process("mipmapLevels", this.mipmapLevels);
      this.useNativeTransport = var1.process("useNativeTransport", this.useNativeTransport);
      var1.process("mainHand", this.mainHand);
      var1.process("attackIndicator", this.attackIndicator);
      var1.process("narrator", this.narrator);
      this.tutorialStep = var1.process("tutorialStep", this.tutorialStep, TutorialSteps::getByName, TutorialSteps::getName);
      var1.process("mouseWheelSensitivity", this.mouseWheelSensitivity);
      var1.process("rawMouseInput", this.rawMouseInput);
      this.glDebugVerbosity = var1.process("glDebugVerbosity", this.glDebugVerbosity);
      this.skipMultiplayerWarning = var1.process("skipMultiplayerWarning", this.skipMultiplayerWarning);
      this.skipRealms32bitWarning = var1.process("skipRealms32bitWarning", this.skipRealms32bitWarning);
      var1.process("hideMatchedNames", this.hideMatchedNames);
      this.joinedFirstServer = var1.process("joinedFirstServer", this.joinedFirstServer);
      this.hideBundleTutorial = var1.process("hideBundleTutorial", this.hideBundleTutorial);
      this.syncWrites = var1.process("syncChunkWrites", this.syncWrites);
      var1.process("showAutosaveIndicator", this.showAutosaveIndicator);
      var1.process("allowServerListing", this.allowServerListing);
      var1.process("chatPreview", this.chatPreview);
      var1.process("onlyShowSecureChat", this.onlyShowSecureChat);

      for(KeyMapping var5 : this.keyMappings) {
         String var6 = var5.saveString();
         String var7 = var1.process("key_" + var5.getName(), var6);
         if (!var6.equals(var7)) {
            var5.setKey(InputConstants.getKey(var7));
         }
      }

      for(SoundSource var14 : SoundSource.values()) {
         this.sourceVolumes.computeFloat(var14, (var1x, var2) -> var1.process("soundCategory_" + var1x.getName(), var2 != null ? var2 : 1.0F));
      }

      for(PlayerModelPart var15 : PlayerModelPart.values()) {
         boolean var16 = this.modelParts.contains(var15);
         boolean var17 = var1.process("modelPart_" + var15.getId(), var16);
         if (var17 != var16) {
            this.setModelPart(var15, var17);
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

         try (BufferedReader var2 = Files.newReader(this.optionsFile, Charsets.UTF_8)) {
            var2.lines().forEach(var1x -> {
               try {
                  Iterator var2x = OPTION_SPLITTER.split(var1x).iterator();
                  var1.putString((String)var2x.next(), (String)var2x.next());
               } catch (Exception var3) {
                  LOGGER.warn("Skipping bad option: {}", var1x);
               }
            });
         }

         final CompoundTag var8 = this.dataFix(var1);
         if (!var8.contains("graphicsMode") && var8.contains("fancyGraphics")) {
            if (isTrue(var8.getString("fancyGraphics"))) {
               this.graphicsMode.set(GraphicsStatus.FANCY);
            } else {
               this.graphicsMode.set(GraphicsStatus.FAST);
            }
         }

         this.processOptions(new Options.FieldAccess() {
            @Nullable
            private String getValueOrNull(String var1) {
               return var8.contains(var1) ? var8.getString(var1) : null;
            }

            @Override
            public <T> void process(String var1, OptionInstance<T> var2) {
               String var3 = this.getValueOrNull(var1);
               if (var3 != null) {
                  JsonReader var4 = new JsonReader(new StringReader(var3.isEmpty() ? "\"\"" : var3));
                  JsonElement var5 = JsonParser.parseReader(var4);
                  DataResult var6 = var2.codec().parse(JsonOps.INSTANCE, var5);
                  var6.error().ifPresent(var2x -> Options.LOGGER.error("Error parsing option value " + var3 + " for option " + var2 + ": " + var2x.message()));
                  var6.result().ifPresent(var2::set);
               }
            }

            @Override
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

            @Override
            public boolean process(String var1, boolean var2) {
               String var3 = this.getValueOrNull(var1);
               return var3 != null ? Options.isTrue(var3) : var2;
            }

            @Override
            public String process(String var1, String var2) {
               return (String)MoreObjects.firstNonNull(this.getValueOrNull(var1), var2);
            }

            @Override
            public float process(String var1, float var2) {
               String var3 = this.getValueOrNull(var1);
               if (var3 == null) {
                  return var2;
               } else if (Options.isTrue(var3)) {
                  return 1.0F;
               } else if (Options.isFalse(var3)) {
                  return 0.0F;
               } else {
                  try {
                     return Float.parseFloat(var3);
                  } catch (NumberFormatException var5) {
                     Options.LOGGER.warn("Invalid floating point value for option {} = {}", new Object[]{var1, var3, var5});
                     return var2;
                  }
               }
            }

            @Override
            public <T> T process(String var1, T var2, Function<String, T> var3, Function<T, String> var4) {
               String var5 = this.getValueOrNull(var1);
               return (T)(var5 == null ? var2 : var3.apply(var5));
            }
         });
         if (var8.contains("fullscreenResolution")) {
            this.fullscreenVideoModeString = var8.getString("fullscreenResolution");
         }

         if (this.minecraft.getWindow() != null) {
            this.minecraft.getWindow().setFramerateLimit(this.framerateLimit.get());
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
      try (final PrintWriter var1 = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.optionsFile), StandardCharsets.UTF_8))) {
         var1.println("version:" + SharedConstants.getCurrentVersion().getWorldVersion());
         this.processOptions(new Options.FieldAccess() {
            public void writePrefix(String var1x) {
               var1.print(var1x);
               var1.print(':');
            }

            @Override
            public <T> void process(String var1x, OptionInstance<T> var2) {
               DataResult var3 = var2.codec().encodeStart(JsonOps.INSTANCE, var2.get());
               var3.error().ifPresent(var1xxx -> Options.LOGGER.error("Error saving option " + var2 + ": " + var1xxx));
               var3.result().ifPresent(var3x -> {
                  this.writePrefix(var1x);
                  var1.println(Options.GSON.toJson(var3x));
               });
            }

            @Override
            public int process(String var1x, int var2) {
               this.writePrefix(var1x);
               var1.println(var2);
               return var2;
            }

            @Override
            public boolean process(String var1x, boolean var2) {
               this.writePrefix(var1x);
               var1.println(var2);
               return var2;
            }

            @Override
            public String process(String var1x, String var2) {
               this.writePrefix(var1x);
               var1.println(var2);
               return var2;
            }

            @Override
            public float process(String var1x, float var2) {
               this.writePrefix(var1x);
               var1.println(var2);
               return var2;
            }

            @Override
            public <T> T process(String var1x, T var2, Function<String, T> var3, Function<T, String> var4) {
               this.writePrefix(var1x);
               var1.println((String)var4.apply(var2));
               return (T)var2;
            }
         });
         if (this.minecraft.getWindow().getPreferredFullscreenVideoMode().isPresent()) {
            var1.println("fullscreenResolution:" + this.minecraft.getWindow().getPreferredFullscreenVideoMode().get().write());
         }
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

         for(PlayerModelPart var3 : this.modelParts) {
            var1 |= var3.getMask();
         }

         this.minecraft
            .player
            .connection
            .send(
               new ServerboundClientInformationPacket(
                  this.languageCode,
                  this.renderDistance.get(),
                  this.chatVisibility.get(),
                  this.chatColors.get(),
                  var1,
                  this.mainHand.get(),
                  this.minecraft.isTextFilteringEnabled(),
                  this.allowServerListing.get()
               )
            );
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
      return this.getEffectiveRenderDistance() >= 4 ? this.cloudStatus.get() : CloudStatus.OFF;
   }

   public boolean useNativeTransport() {
      return this.useNativeTransport;
   }

   public void loadSelectedResourcePacks(PackRepository var1) {
      LinkedHashSet var2 = Sets.newLinkedHashSet();
      Iterator var3 = this.resourcePacks.iterator();

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
   }

   public CameraType getCameraType() {
      return this.cameraType;
   }

   public void setCameraType(CameraType var1) {
      this.cameraType = var1;
   }

   private static List<String> readPackList(String var0) {
      List var1 = GsonHelper.fromJson(GSON, var0, RESOURCE_PACK_TYPE);
      return (List<String>)(var1 != null ? var1 : Lists.newArrayList());
   }

   public File getFile() {
      return this.optionsFile;
   }

   public String dumpOptionsForReport() {
      Stream var1 = Stream.builder()
         .add(Pair.of("ao", this.ambientOcclusion.get()))
         .add(Pair.of("biomeBlendRadius", this.biomeBlendRadius.get()))
         .add(Pair.of("enableVsync", this.enableVsync.get()))
         .add(Pair.of("entityDistanceScaling", this.entityDistanceScaling.get()))
         .add(Pair.of("entityShadows", this.entityShadows.get()))
         .add(Pair.of("forceUnicodeFont", this.forceUnicodeFont.get()))
         .add(Pair.of("fov", this.fov.get()))
         .add(Pair.of("fovEffectScale", this.fovEffectScale.get()))
         .add(Pair.of("darknessEffectScale", this.darknessEffectScale.get()))
         .add(Pair.of("prioritizeChunkUpdates", this.prioritizeChunkUpdates.get()))
         .add(Pair.of("fullscreen", this.fullscreen.get()))
         .add(Pair.of("fullscreenResolution", String.valueOf(this.fullscreenVideoModeString)))
         .add(Pair.of("gamma", this.gamma.get()))
         .add(Pair.of("glDebugVerbosity", this.glDebugVerbosity))
         .add(Pair.of("graphicsMode", this.graphicsMode.get()))
         .add(Pair.of("guiScale", this.guiScale.get()))
         .add(Pair.of("maxFps", this.framerateLimit.get()))
         .add(Pair.of("mipmapLevels", this.mipmapLevels.get()))
         .add(Pair.of("narrator", this.narrator.get()))
         .add(Pair.of("overrideHeight", this.overrideHeight))
         .add(Pair.of("overrideWidth", this.overrideWidth))
         .add(Pair.of("particles", this.particles.get()))
         .add(Pair.of("reducedDebugInfo", this.reducedDebugInfo.get()))
         .add(Pair.of("renderClouds", this.cloudStatus.get()))
         .add(Pair.of("renderDistance", this.renderDistance.get()))
         .add(Pair.of("simulationDistance", this.simulationDistance.get()))
         .add(Pair.of("resourcePacks", this.resourcePacks))
         .add(Pair.of("screenEffectScale", this.screenEffectScale.get()))
         .add(Pair.of("syncChunkWrites", this.syncWrites))
         .add(Pair.of("useNativeTransport", this.useNativeTransport))
         .add(Pair.of("soundDevice", this.soundDevice.get()))
         .build();
      return var1.<String>map(var0 -> (String)var0.getFirst() + ": " + var0.getSecond()).collect(Collectors.joining(System.lineSeparator()));
   }

   public void setServerRenderDistance(int var1) {
      this.serverRenderDistance = var1;
   }

   public int getEffectiveRenderDistance() {
      return this.serverRenderDistance > 0 ? Math.min(this.renderDistance.get(), this.serverRenderDistance) : this.renderDistance.get();
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

   interface FieldAccess {
      <T> void process(String var1, OptionInstance<T> var2);

      int process(String var1, int var2);

      boolean process(String var1, boolean var2);

      String process(String var1, String var2);

      float process(String var1, float var2);

      <T> T process(String var1, T var2, Function<String, T> var3, Function<T, String> var4);
   }
}