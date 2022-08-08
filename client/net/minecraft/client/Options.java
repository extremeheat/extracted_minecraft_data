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
import com.mojang.blaze3d.platform.VideoMode;
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
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.ChatComponent;
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
import net.minecraft.network.protocol.Packet;
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
   private final OptionInstance<Boolean> darkMojangStudiosBackground;
   private static final Component ACCESSIBILITY_TOOLTIP_HIDE_LIGHTNING_FLASHES = Component.translatable("options.hideLightningFlashes.tooltip");
   private final OptionInstance<Boolean> hideLightningFlash;
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
   private final OptionInstance<AmbientOcclusionStatus> ambientOcclusion;
   private static final Component PRIORITIZE_CHUNK_TOOLTIP_NONE;
   private static final Component PRIORITIZE_CHUNK_TOOLTIP_PLAYER_AFFECTED;
   private static final Component PRIORITIZE_CHUNK_TOOLTIP_NEARBY;
   private final OptionInstance<PrioritizeChunkUpdates> prioritizeChunkUpdates;
   public List<String> resourcePacks;
   public List<String> incompatibleResourcePacks;
   private final OptionInstance<ChatVisiblity> chatVisibility;
   private final OptionInstance<Double> chatOpacity;
   private final OptionInstance<Double> chatLineSpacing;
   private final OptionInstance<Double> textBackgroundOpacity;
   @Nullable
   public String fullscreenVideoModeString;
   public boolean hideServerAddress;
   public boolean advancedItemTooltips;
   public boolean pauseOnLostFocus;
   private final Set<PlayerModelPart> modelParts;
   private final OptionInstance<HumanoidArm> mainHand;
   public int overrideWidth;
   public int overrideHeight;
   public boolean heldItemTooltips;
   private final OptionInstance<Double> chatScale;
   private final OptionInstance<Double> chatWidth;
   private final OptionInstance<Double> chatHeightUnfocused;
   private final OptionInstance<Double> chatHeightFocused;
   private final OptionInstance<Double> chatDelay;
   private final OptionInstance<Integer> mipmapLevels;
   private final Object2FloatMap<SoundSource> sourceVolumes;
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
   private final OptionInstance<Boolean> autoSuggestions;
   private final OptionInstance<Boolean> chatColors;
   private final OptionInstance<Boolean> chatLinks;
   private final OptionInstance<Boolean> chatLinksPrompt;
   private final OptionInstance<Boolean> enableVsync;
   private final OptionInstance<Boolean> entityShadows;
   private final OptionInstance<Boolean> forceUnicodeFont;
   private final OptionInstance<Boolean> invertYMouse;
   private final OptionInstance<Boolean> discreteMouseScroll;
   private final OptionInstance<Boolean> realmsNotifications;
   private static final Component ALLOW_SERVER_LISTING_TOOLTIP;
   private final OptionInstance<Boolean> allowServerListing;
   private final OptionInstance<Boolean> reducedDebugInfo;
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
   public boolean skipRealms32bitWarning;
   private static final Component CHAT_TOOLTIP_HIDE_MATCHED_NAMES;
   private final OptionInstance<Boolean> hideMatchedNames;
   private final OptionInstance<Boolean> showAutosaveIndicator;
   private static final Component CHAT_TOOLTIP_PREVIEW;
   private final OptionInstance<Boolean> chatPreview;
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
   public boolean renderDebug;
   public boolean renderDebugCharts;
   public boolean renderFpsChart;
   public String lastMpIp;
   public boolean smoothCamera;
   private final OptionInstance<Integer> fov;
   private static final Component ACCESSIBILITY_TOOLTIP_SCREEN_EFFECT;
   private final OptionInstance<Double> screenEffectScale;
   private static final Component ACCESSIBILITY_TOOLTIP_FOV_EFFECT;
   private final OptionInstance<Double> fovEffectScale;
   private static final Component ACCESSIBILITY_TOOLTIP_DARKNESS_EFFECT;
   private final OptionInstance<Double> darknessEffectScale;
   private final OptionInstance<Double> gamma;
   private final OptionInstance<Integer> guiScale;
   private final OptionInstance<ParticleStatus> particles;
   private final OptionInstance<NarratorStatus> narrator;
   public String languageCode;
   private final OptionInstance<String> soundDevice;
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

   public OptionInstance<Boolean> chatPreview() {
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
      this.darkMojangStudiosBackground = OptionInstance.createBoolean("options.darkMojangStudiosBackgroundColor", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_DARK_MOJANG_BACKGROUND), false);
      this.hideLightningFlash = OptionInstance.createBoolean("options.hideLightningFlashes", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_HIDE_LIGHTNING_FLASHES), false);
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
      this.cloudStatus = new OptionInstance("options.renderClouds", OptionInstance.noTooltip(), OptionInstance.forOptionEnum(), new OptionInstance.Enum(Arrays.asList(CloudStatus.values()), Codec.either(Codec.BOOL, Codec.STRING).xmap((var0) -> {
         return (CloudStatus)var0.map((var0x) -> {
            return var0x ? CloudStatus.FANCY : CloudStatus.OFF;
         }, (var0x) -> {
            CloudStatus var10000;
            switch (var0x) {
               case "true":
                  var10000 = CloudStatus.FANCY;
                  break;
               case "fast":
                  var10000 = CloudStatus.FAST;
                  break;
               default:
                  var10000 = CloudStatus.OFF;
            }

            return var10000;
         });
      }, (var0) -> {
         String var10000;
         switch (var0) {
            case FANCY:
               var10000 = "true";
               break;
            case FAST:
               var10000 = "fast";
               break;
            case OFF:
               var10000 = "false";
               break;
            default:
               throw new IncompatibleClassChangeError();
         }

         return Either.right(var10000);
      })), CloudStatus.FANCY, (var0) -> {
         if (Minecraft.useShaderTransparency()) {
            RenderTarget var1 = Minecraft.getInstance().levelRenderer.getCloudsTarget();
            if (var1 != null) {
               var1.clear(Minecraft.ON_OSX);
            }
         }

      });
      this.graphicsMode = new OptionInstance("options.graphics", (var0) -> {
         List var1 = OptionInstance.splitTooltip(var0, GRAPHICS_TOOLTIP_FAST);
         List var2 = OptionInstance.splitTooltip(var0, GRAPHICS_TOOLTIP_FANCY);
         List var3 = OptionInstance.splitTooltip(var0, GRAPHICS_TOOLTIP_FABULOUS);
         return (var3x) -> {
            List var10000;
            switch (var3x) {
               case FANCY:
                  var10000 = var2;
                  break;
               case FAST:
                  var10000 = var1;
                  break;
               case FABULOUS:
                  var10000 = var3;
                  break;
               default:
                  throw new IncompatibleClassChangeError();
            }

            return var10000;
         };
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
      this.ambientOcclusion = new OptionInstance("options.ao", OptionInstance.noTooltip(), OptionInstance.forOptionEnum(), new OptionInstance.Enum(Arrays.asList(AmbientOcclusionStatus.values()), Codec.either(Codec.BOOL.xmap((var0) -> {
         return var0 ? AmbientOcclusionStatus.MAX.getId() : AmbientOcclusionStatus.OFF.getId();
      }, (var0) -> {
         return var0 == AmbientOcclusionStatus.MAX.getId();
      }), Codec.INT).xmap((var0) -> {
         return (Integer)var0.map((var0x) -> {
            return var0x;
         }, (var0x) -> {
            return var0x;
         });
      }, Either::right).xmap(AmbientOcclusionStatus::byId, AmbientOcclusionStatus::getId)), AmbientOcclusionStatus.MAX, (var0) -> {
         Minecraft.getInstance().levelRenderer.allChanged();
      });
      this.prioritizeChunkUpdates = new OptionInstance("options.prioritizeChunkUpdates", (var0) -> {
         List var1 = OptionInstance.splitTooltip(var0, PRIORITIZE_CHUNK_TOOLTIP_NONE);
         List var2 = OptionInstance.splitTooltip(var0, PRIORITIZE_CHUNK_TOOLTIP_PLAYER_AFFECTED);
         List var3 = OptionInstance.splitTooltip(var0, PRIORITIZE_CHUNK_TOOLTIP_NEARBY);
         return (var3x) -> {
            List var10000;
            switch (var3x) {
               case NONE:
                  var10000 = var1;
                  break;
               case PLAYER_AFFECTED:
                  var10000 = var2;
                  break;
               case NEARBY:
                  var10000 = var3;
                  break;
               default:
                  throw new IncompatibleClassChangeError();
            }

            return var10000;
         };
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
      this.textBackgroundOpacity = new OptionInstance("options.accessibility.text_background_opacity", OptionInstance.noTooltip(), Options::percentValueLabel, OptionInstance.UnitDouble.INSTANCE, 0.5, (var0) -> {
         Minecraft.getInstance().gui.getChat().rescaleChat();
      });
      this.pauseOnLostFocus = true;
      this.modelParts = EnumSet.allOf(PlayerModelPart.class);
      this.mainHand = new OptionInstance("options.mainHand", OptionInstance.noTooltip(), OptionInstance.forOptionEnum(), new OptionInstance.Enum(Arrays.asList(HumanoidArm.values()), Codec.STRING.xmap((var0) -> {
         return "left".equals(var0) ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
      }, (var0) -> {
         return var0 == HumanoidArm.LEFT ? "left" : "right";
      })), HumanoidArm.RIGHT, (var1x) -> {
         this.broadcastOptions();
      });
      this.heldItemTooltips = true;
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
         return var1x <= 0.0 ? Component.translatable("options.chat.delay_none") : Component.translatable("options.chat.delay", String.format("%.1f", var1x));
      }, (new OptionInstance.IntRange(0, 60)).xmap((var0) -> {
         return (double)var0 / 10.0;
      }, (var0) -> {
         return (int)(var0 * 10.0);
      }), Codec.doubleRange(0.0, 6.0), 0.0, (var0) -> {
      });
      this.mipmapLevels = new OptionInstance("options.mipmapLevels", OptionInstance.noTooltip(), (var0, var1x) -> {
         return (Component)(var1x == 0 ? CommonComponents.optionStatus(var0, false) : genericValueLabel(var0, var1x));
      }, new OptionInstance.IntRange(0, 4), 4, (var0) -> {
      });
      this.sourceVolumes = (Object2FloatMap)Util.make(new Object2FloatOpenHashMap(), (var0) -> {
         var0.defaultReturnValue(1.0F);
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
         return genericValueLabel(var0, Component.literal(String.format("%.2f", var1x)));
      }, (new OptionInstance.IntRange(-200, 100)).xmap(Options::logMouse, Options::unlogMouse), Codec.doubleRange(logMouse(-200), logMouse(100)), logMouse(0), (var0) -> {
      });
      this.rawMouseInput = OptionInstance.createBoolean("options.rawMouseInput", true, (var0) -> {
         Window var1 = Minecraft.getInstance().getWindow();
         if (var1 != null) {
            var1.updateRawMouseInput(var0);
         }

      });
      this.glDebugVerbosity = 1;
      this.autoJump = OptionInstance.createBoolean("options.autoJump", true);
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
         Minecraft var1 = Minecraft.getInstance();
         if (var1.getWindow() != null) {
            var1.selectMainFont(var0);
            var1.resizeDisplay();
         }

      });
      this.invertYMouse = OptionInstance.createBoolean("options.invertMouse", false);
      this.discreteMouseScroll = OptionInstance.createBoolean("options.discrete_mouse_scroll", false);
      this.realmsNotifications = OptionInstance.createBoolean("options.realmsNotifications", true);
      this.allowServerListing = OptionInstance.createBoolean("options.allowServerListing", OptionInstance.cachedConstantTooltip(ALLOW_SERVER_LISTING_TOOLTIP), true, (var1x) -> {
         this.broadcastOptions();
      });
      this.reducedDebugInfo = OptionInstance.createBoolean("options.reducedDebugInfo", false);
      this.showSubtitles = OptionInstance.createBoolean("options.showSubtitles", false);
      this.directionalAudio = OptionInstance.createBoolean("options.directionalAudio", (var0) -> {
         List var1 = OptionInstance.splitTooltip(var0, DIRECTIONAL_AUDIO_TOOLTIP_ON);
         List var2 = OptionInstance.splitTooltip(var0, DIRECTIONAL_AUDIO_TOOLTIP_OFF);
         return (var2x) -> {
            return var2x ? var1 : var2;
         };
      }, false, (var0) -> {
         SoundManager var1 = Minecraft.getInstance().getSoundManager();
         var1.reload();
         var1.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
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
      this.chatPreview = OptionInstance.createBoolean("options.chatPreview", OptionInstance.cachedConstantTooltip(CHAT_TOOLTIP_PREVIEW), true);
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
            case 70:
               var10000 = genericValueLabel(var0, Component.translatable("options.fov.min"));
               break;
            case 110:
               var10000 = genericValueLabel(var0, Component.translatable("options.fov.max"));
               break;
            default:
               var10000 = genericValueLabel(var0, var1x);
         }

         return var10000;
      }, new OptionInstance.IntRange(30, 110), Codec.DOUBLE.xmap((var0) -> {
         return (int)(var0 * 40.0 + 70.0);
      }, (var0) -> {
         return ((double)var0 - 70.0) / 40.0;
      }), 70, (var0) -> {
         Minecraft.getInstance().levelRenderer.needsUpdate();
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
      }), 0, (var0) -> {
      });
      this.particles = new OptionInstance("options.particles", OptionInstance.noTooltip(), OptionInstance.forOptionEnum(), new OptionInstance.Enum(Arrays.asList(ParticleStatus.values()), Codec.INT.xmap(ParticleStatus::byId, ParticleStatus::getId)), ParticleStatus.ALL, (var0) -> {
      });
      this.narrator = new OptionInstance("options.narrator", OptionInstance.noTooltip(), (var0, var1x) -> {
         return (Component)(NarratorChatListener.INSTANCE.isActive() ? var1x.getName() : Component.translatable("options.narrator.notavailable"));
      }, new OptionInstance.Enum(Arrays.asList(NarratorStatus.values()), Codec.INT.xmap(NarratorStatus::byId, NarratorStatus::getId)), NarratorStatus.OFF, (var0) -> {
         NarratorChatListener.INSTANCE.updateNarratorStatus(var0);
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
         var1.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
      });
      this.minecraft = var1;
      this.optionsFile = new File(var2, "options.txt");
      boolean var3 = var1.is64Bit();
      boolean var4 = var3 && Runtime.getRuntime().maxMemory() >= 1000000000L;
      this.renderDistance = new OptionInstance("options.renderDistance", OptionInstance.noTooltip(), (var0, var1x) -> {
         return genericValueLabel(var0, Component.translatable("options.chunks", var1x));
      }, new OptionInstance.IntRange(2, var4 ? 32 : 16), var3 ? 12 : 8, (var0) -> {
         Minecraft.getInstance().levelRenderer.needsUpdate();
      });
      this.simulationDistance = new OptionInstance("options.simulationDistance", OptionInstance.noTooltip(), (var0, var1x) -> {
         return genericValueLabel(var0, Component.translatable("options.chunks", var1x));
      }, new OptionInstance.IntRange(5, var4 ? 32 : 16), var3 ? 12 : 8, (var0) -> {
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

   private void processOptions(FieldAccess var1) {
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
      List var10003 = this.resourcePacks;
      Function var10004 = Options::readPackList;
      Gson var10005 = GSON;
      Objects.requireNonNull(var10005);
      this.resourcePacks = (List)var1.process("resourcePacks", var10003, var10004, var10005::toJson);
      var10003 = this.incompatibleResourcePacks;
      var10004 = Options::readPackList;
      var10005 = GSON;
      Objects.requireNonNull(var10005);
      this.incompatibleResourcePacks = (List)var1.process("incompatibleResourcePacks", var10003, var10004, var10005::toJson);
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
      this.tutorialStep = (TutorialSteps)var1.process("tutorialStep", this.tutorialStep, TutorialSteps::getByName, TutorialSteps::getName);
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
               this.graphicsMode.set(GraphicsStatus.FANCY);
            } else {
               this.graphicsMode.set(GraphicsStatus.FAST);
            }
         }

         this.processOptions(new FieldAccess() {
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
                     Options.LOGGER.error("Error parsing option value " + var3 + " for option " + var2 + ": " + var2x.message());
                  });
                  Optional var10000 = var6.result();
                  Objects.requireNonNull(var2);
                  var10000.ifPresent(var2::set);
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

      return NbtUtils.update(this.minecraft.getFixerUpper(), DataFixTypes.OPTIONS, var1, var2);
   }

   public void save() {
      try {
         final PrintWriter var1 = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.optionsFile), StandardCharsets.UTF_8));

         try {
            var1.println("version:" + SharedConstants.getCurrentVersion().getWorldVersion());
            this.processOptions(new FieldAccess() {
               public void writePrefix(String var1x) {
                  var1.print(var1x);
                  var1.print(':');
               }

               public <T> void process(String var1x, OptionInstance<T> var2) {
                  DataResult var3 = var2.codec().encodeStart(JsonOps.INSTANCE, var2.get());
                  var3.error().ifPresent((var1xx) -> {
                     Options.LOGGER.error("Error saving option " + var2 + ": " + var1xx);
                  });
                  var3.result().ifPresent((var3x) -> {
                     this.writePrefix(var1x);
                     var1.println(Options.GSON.toJson(var3x));
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

         this.minecraft.player.connection.send((Packet)(new ServerboundClientInformationPacket(this.languageCode, (Integer)this.renderDistance.get(), (ChatVisiblity)this.chatVisibility.get(), (Boolean)this.chatColors.get(), var1, (HumanoidArm)this.mainHand.get(), this.minecraft.isTextFilteringEnabled(), (Boolean)this.allowServerListing.get())));
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

   private static List<String> readPackList(String var0) {
      List var1 = (List)GsonHelper.fromJson(GSON, var0, RESOURCE_PACK_TYPE);
      return (List)(var1 != null ? var1 : Lists.newArrayList());
   }

   public File getFile() {
      return this.optionsFile;
   }

   public String dumpOptionsForReport() {
      Stream var1 = Stream.builder().add(Pair.of("ao", this.ambientOcclusion.get())).add(Pair.of("biomeBlendRadius", this.biomeBlendRadius.get())).add(Pair.of("enableVsync", this.enableVsync.get())).add(Pair.of("entityDistanceScaling", this.entityDistanceScaling.get())).add(Pair.of("entityShadows", this.entityShadows.get())).add(Pair.of("forceUnicodeFont", this.forceUnicodeFont.get())).add(Pair.of("fov", this.fov.get())).add(Pair.of("fovEffectScale", this.fovEffectScale.get())).add(Pair.of("darknessEffectScale", this.darknessEffectScale.get())).add(Pair.of("prioritizeChunkUpdates", this.prioritizeChunkUpdates.get())).add(Pair.of("fullscreen", this.fullscreen.get())).add(Pair.of("fullscreenResolution", String.valueOf(this.fullscreenVideoModeString))).add(Pair.of("gamma", this.gamma.get())).add(Pair.of("glDebugVerbosity", this.glDebugVerbosity)).add(Pair.of("graphicsMode", this.graphicsMode.get())).add(Pair.of("guiScale", this.guiScale.get())).add(Pair.of("maxFps", this.framerateLimit.get())).add(Pair.of("mipmapLevels", this.mipmapLevels.get())).add(Pair.of("narrator", this.narrator.get())).add(Pair.of("overrideHeight", this.overrideHeight)).add(Pair.of("overrideWidth", this.overrideWidth)).add(Pair.of("particles", this.particles.get())).add(Pair.of("reducedDebugInfo", this.reducedDebugInfo.get())).add(Pair.of("renderClouds", this.cloudStatus.get())).add(Pair.of("renderDistance", this.renderDistance.get())).add(Pair.of("simulationDistance", this.simulationDistance.get())).add(Pair.of("resourcePacks", this.resourcePacks)).add(Pair.of("screenEffectScale", this.screenEffectScale.get())).add(Pair.of("syncChunkWrites", this.syncWrites)).add(Pair.of("useNativeTransport", this.useNativeTransport)).add(Pair.of("soundDevice", this.soundDevice.get())).build();
      return (String)var1.map((var0) -> {
         String var10000 = (String)var0.getFirst();
         return var10000 + ": " + var0.getSecond();
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
      ALLOW_SERVER_LISTING_TOOLTIP = Component.translatable("options.allowServerListing.tooltip");
      DIRECTIONAL_AUDIO_TOOLTIP_ON = Component.translatable("options.directionalAudio.on.tooltip");
      DIRECTIONAL_AUDIO_TOOLTIP_OFF = Component.translatable("options.directionalAudio.off.tooltip");
      MOVEMENT_TOGGLE = Component.translatable("options.key.toggle");
      MOVEMENT_HOLD = Component.translatable("options.key.hold");
      CHAT_TOOLTIP_HIDE_MATCHED_NAMES = Component.translatable("options.hideMatchedNames.tooltip");
      CHAT_TOOLTIP_PREVIEW = Component.translatable("options.chatPreview.tooltip");
      CHAT_TOOLTIP_ONLY_SHOW_SECURE = Component.translatable("options.onlyShowSecureChat.tooltip");
      ACCESSIBILITY_TOOLTIP_SCREEN_EFFECT = Component.translatable("options.screenEffectScale.tooltip");
      ACCESSIBILITY_TOOLTIP_FOV_EFFECT = Component.translatable("options.fovEffectScale.tooltip");
      ACCESSIBILITY_TOOLTIP_DARKNESS_EFFECT = Component.translatable("options.darknessEffectScale.tooltip");
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
