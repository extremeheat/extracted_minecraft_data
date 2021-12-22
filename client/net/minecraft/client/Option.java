package net.minecraft.client;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.Window;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.renderer.GpuWarnlistManager;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.ChatVisiblity;

public abstract class Option {
   protected static final int OPTIONS_TOOLTIP_WIDTH = 200;
   public static final ProgressOption BIOME_BLEND_RADIUS = new ProgressOption("options.biomeBlendRadius", 0.0D, 7.0D, 1.0F, (var0) -> {
      return (double)var0.biomeBlendRadius;
   }, (var0, var1) -> {
      var0.biomeBlendRadius = Mth.clamp((int)((int)var1), (int)0, (int)7);
      Minecraft.getInstance().levelRenderer.allChanged();
   }, (var0, var1) -> {
      double var2 = var1.get(var0);
      int var4 = (int)var2 * 2 + 1;
      return var1.genericValueLabel(new TranslatableComponent("options.biomeBlendRadius." + var4));
   });
   public static final ProgressOption CHAT_HEIGHT_FOCUSED = new ProgressOption("options.chat.height.focused", 0.0D, 1.0D, 0.0F, (var0) -> {
      return var0.chatHeightFocused;
   }, (var0, var1) -> {
      var0.chatHeightFocused = var1;
      Minecraft.getInstance().gui.getChat().rescaleChat();
   }, (var0, var1) -> {
      double var2 = var1.toPct(var1.get(var0));
      return var1.pixelValueLabel(ChatComponent.getHeight(var2));
   });
   public static final ProgressOption CHAT_HEIGHT_UNFOCUSED = new ProgressOption("options.chat.height.unfocused", 0.0D, 1.0D, 0.0F, (var0) -> {
      return var0.chatHeightUnfocused;
   }, (var0, var1) -> {
      var0.chatHeightUnfocused = var1;
      Minecraft.getInstance().gui.getChat().rescaleChat();
   }, (var0, var1) -> {
      double var2 = var1.toPct(var1.get(var0));
      return var1.pixelValueLabel(ChatComponent.getHeight(var2));
   });
   public static final ProgressOption CHAT_OPACITY = new ProgressOption("options.chat.opacity", 0.0D, 1.0D, 0.0F, (var0) -> {
      return var0.chatOpacity;
   }, (var0, var1) -> {
      var0.chatOpacity = var1;
      Minecraft.getInstance().gui.getChat().rescaleChat();
   }, (var0, var1) -> {
      double var2 = var1.toPct(var1.get(var0));
      return var1.percentValueLabel(var2 * 0.9D + 0.1D);
   });
   public static final ProgressOption CHAT_SCALE = new ProgressOption("options.chat.scale", 0.0D, 1.0D, 0.0F, (var0) -> {
      return var0.chatScale;
   }, (var0, var1) -> {
      var0.chatScale = var1;
      Minecraft.getInstance().gui.getChat().rescaleChat();
   }, (var0, var1) -> {
      double var2 = var1.toPct(var1.get(var0));
      return (Component)(var2 == 0.0D ? CommonComponents.optionStatus(var1.getCaption(), false) : var1.percentValueLabel(var2));
   });
   public static final ProgressOption CHAT_WIDTH = new ProgressOption("options.chat.width", 0.0D, 1.0D, 0.0F, (var0) -> {
      return var0.chatWidth;
   }, (var0, var1) -> {
      var0.chatWidth = var1;
      Minecraft.getInstance().gui.getChat().rescaleChat();
   }, (var0, var1) -> {
      double var2 = var1.toPct(var1.get(var0));
      return var1.pixelValueLabel(ChatComponent.getWidth(var2));
   });
   public static final ProgressOption CHAT_LINE_SPACING = new ProgressOption("options.chat.line_spacing", 0.0D, 1.0D, 0.0F, (var0) -> {
      return var0.chatLineSpacing;
   }, (var0, var1) -> {
      var0.chatLineSpacing = var1;
   }, (var0, var1) -> {
      return var1.percentValueLabel(var1.toPct(var1.get(var0)));
   });
   public static final ProgressOption CHAT_DELAY = new ProgressOption("options.chat.delay_instant", 0.0D, 6.0D, 0.1F, (var0) -> {
      return var0.chatDelay;
   }, (var0, var1) -> {
      var0.chatDelay = var1;
   }, (var0, var1) -> {
      double var2 = var1.get(var0);
      return var2 <= 0.0D ? new TranslatableComponent("options.chat.delay_none") : new TranslatableComponent("options.chat.delay", new Object[]{String.format("%.1f", var2)});
   });
   public static final ProgressOption FOV = new ProgressOption("options.fov", 30.0D, 110.0D, 1.0F, (var0) -> {
      return var0.fov;
   }, (var0, var1) -> {
      var0.fov = var1;
      Minecraft.getInstance().levelRenderer.needsUpdate();
   }, (var0, var1) -> {
      double var2 = var1.get(var0);
      if (var2 == 70.0D) {
         return var1.genericValueLabel(new TranslatableComponent("options.fov.min"));
      } else {
         return var2 == var1.getMaxValue() ? var1.genericValueLabel(new TranslatableComponent("options.fov.max")) : var1.genericValueLabel((int)var2);
      }
   });
   private static final Component ACCESSIBILITY_TOOLTIP_FOV_EFFECT = new TranslatableComponent("options.fovEffectScale.tooltip");
   public static final ProgressOption FOV_EFFECTS_SCALE = new ProgressOption("options.fovEffectScale", 0.0D, 1.0D, 0.0F, (var0) -> {
      return Math.pow((double)var0.fovEffectScale, 2.0D);
   }, (var0, var1) -> {
      var0.fovEffectScale = (float)Math.sqrt(var1);
   }, (var0, var1) -> {
      double var2 = var1.toPct(var1.get(var0));
      return var2 == 0.0D ? var1.genericValueLabel(CommonComponents.OPTION_OFF) : var1.percentValueLabel(var2);
   }, (var0) -> {
      return var0.font.split(ACCESSIBILITY_TOOLTIP_FOV_EFFECT, 200);
   });
   private static final Component ACCESSIBILITY_TOOLTIP_SCREEN_EFFECT = new TranslatableComponent("options.screenEffectScale.tooltip");
   public static final ProgressOption SCREEN_EFFECTS_SCALE = new ProgressOption("options.screenEffectScale", 0.0D, 1.0D, 0.0F, (var0) -> {
      return (double)var0.screenEffectScale;
   }, (var0, var1) -> {
      var0.screenEffectScale = var1.floatValue();
   }, (var0, var1) -> {
      double var2 = var1.toPct(var1.get(var0));
      return var2 == 0.0D ? var1.genericValueLabel(CommonComponents.OPTION_OFF) : var1.percentValueLabel(var2);
   }, (var0) -> {
      return var0.font.split(ACCESSIBILITY_TOOLTIP_SCREEN_EFFECT, 200);
   });
   public static final ProgressOption FRAMERATE_LIMIT = new ProgressOption("options.framerateLimit", 10.0D, 260.0D, 10.0F, (var0) -> {
      return (double)var0.framerateLimit;
   }, (var0, var1) -> {
      var0.framerateLimit = (int)var1;
      Minecraft.getInstance().getWindow().setFramerateLimit(var0.framerateLimit);
   }, (var0, var1) -> {
      double var2 = var1.get(var0);
      return var2 == var1.getMaxValue() ? var1.genericValueLabel(new TranslatableComponent("options.framerateLimit.max")) : var1.genericValueLabel(new TranslatableComponent("options.framerate", new Object[]{(int)var2}));
   });
   public static final ProgressOption GAMMA = new ProgressOption("options.gamma", 0.0D, 1.0D, 0.0F, (var0) -> {
      return var0.gamma;
   }, (var0, var1) -> {
      var0.gamma = var1;
   }, (var0, var1) -> {
      double var2 = var1.toPct(var1.get(var0));
      int var4 = (int)(var2 * 100.0D);
      if (var4 == 0) {
         return var1.genericValueLabel(new TranslatableComponent("options.gamma.min"));
      } else if (var4 == 50) {
         return var1.genericValueLabel(new TranslatableComponent("options.gamma.default"));
      } else {
         return var4 == 100 ? var1.genericValueLabel(new TranslatableComponent("options.gamma.max")) : var1.genericValueLabel(var4);
      }
   });
   public static final ProgressOption MIPMAP_LEVELS = new ProgressOption("options.mipmapLevels", 0.0D, 4.0D, 1.0F, (var0) -> {
      return (double)var0.mipmapLevels;
   }, (var0, var1) -> {
      var0.mipmapLevels = (int)var1;
   }, (var0, var1) -> {
      double var2 = var1.get(var0);
      return (Component)(var2 == 0.0D ? CommonComponents.optionStatus(var1.getCaption(), false) : var1.genericValueLabel((int)var2));
   });
   public static final ProgressOption MOUSE_WHEEL_SENSITIVITY = new LogaritmicProgressOption("options.mouseWheelSensitivity", 0.01D, 10.0D, 0.01F, (var0) -> {
      return var0.mouseWheelSensitivity;
   }, (var0, var1) -> {
      var0.mouseWheelSensitivity = var1;
   }, (var0, var1) -> {
      double var2 = var1.toPct(var1.get(var0));
      return var1.genericValueLabel(new TextComponent(String.format("%.2f", var1.toValue(var2))));
   });
   public static final CycleOption<Boolean> RAW_MOUSE_INPUT = CycleOption.createOnOff("options.rawMouseInput", (var0) -> {
      return var0.rawMouseInput;
   }, (var0, var1, var2) -> {
      var0.rawMouseInput = var2;
      Window var3 = Minecraft.getInstance().getWindow();
      if (var3 != null) {
         var3.updateRawMouseInput(var2);
      }

   });
   public static final ProgressOption RENDER_DISTANCE = new ProgressOption("options.renderDistance", 2.0D, 16.0D, 1.0F, (var0) -> {
      return (double)var0.renderDistance;
   }, (var0, var1) -> {
      var0.renderDistance = var1.intValue();
      Minecraft.getInstance().levelRenderer.needsUpdate();
   }, (var0, var1) -> {
      double var2 = var1.get(var0);
      return var1.genericValueLabel(new TranslatableComponent("options.chunks", new Object[]{(int)var2}));
   });
   public static final ProgressOption SIMULATION_DISTANCE = new ProgressOption("options.simulationDistance", 5.0D, 16.0D, 1.0F, (var0) -> {
      return (double)var0.simulationDistance;
   }, (var0, var1) -> {
      var0.simulationDistance = var1.intValue();
   }, (var0, var1) -> {
      double var2 = var1.get(var0);
      return var1.genericValueLabel(new TranslatableComponent("options.chunks", new Object[]{(int)var2}));
   });
   public static final ProgressOption ENTITY_DISTANCE_SCALING = new ProgressOption("options.entityDistanceScaling", 0.5D, 5.0D, 0.25F, (var0) -> {
      return (double)var0.entityDistanceScaling;
   }, (var0, var1) -> {
      var0.entityDistanceScaling = (float)var1;
   }, (var0, var1) -> {
      double var2 = var1.get(var0);
      return var1.percentValueLabel(var2);
   });
   public static final ProgressOption SENSITIVITY = new ProgressOption("options.sensitivity", 0.0D, 1.0D, 0.0F, (var0) -> {
      return var0.sensitivity;
   }, (var0, var1) -> {
      var0.sensitivity = var1;
   }, (var0, var1) -> {
      double var2 = var1.toPct(var1.get(var0));
      if (var2 == 0.0D) {
         return var1.genericValueLabel(new TranslatableComponent("options.sensitivity.min"));
      } else {
         return var2 == 1.0D ? var1.genericValueLabel(new TranslatableComponent("options.sensitivity.max")) : var1.percentValueLabel(2.0D * var2);
      }
   });
   public static final ProgressOption TEXT_BACKGROUND_OPACITY = new ProgressOption("options.accessibility.text_background_opacity", 0.0D, 1.0D, 0.0F, (var0) -> {
      return var0.textBackgroundOpacity;
   }, (var0, var1) -> {
      var0.textBackgroundOpacity = var1;
      Minecraft.getInstance().gui.getChat().rescaleChat();
   }, (var0, var1) -> {
      return var1.percentValueLabel(var1.toPct(var1.get(var0)));
   });
   public static final CycleOption<AmbientOcclusionStatus> AMBIENT_OCCLUSION = CycleOption.create("options.ao", (Object[])AmbientOcclusionStatus.values(), (var0) -> {
      return new TranslatableComponent(var0.getKey());
   }, (var0) -> {
      return var0.ambientOcclusion;
   }, (var0, var1, var2) -> {
      var0.ambientOcclusion = var2;
      Minecraft.getInstance().levelRenderer.allChanged();
   });
   private static final Component PRIORITIZE_CHUNK_TOOLTIP_NONE = new TranslatableComponent("options.prioritizeChunkUpdates.none.tooltip");
   private static final Component PRIORITIZE_CHUNK_TOOLTIP_PLAYER_AFFECTED = new TranslatableComponent("options.prioritizeChunkUpdates.byPlayer.tooltip");
   private static final Component PRIORITIZE_CHUNK_TOOLTIP_NEARBY = new TranslatableComponent("options.prioritizeChunkUpdates.nearby.tooltip");
   public static final CycleOption<PrioritizeChunkUpdates> PRIORITIZE_CHUNK_UPDATES = CycleOption.create("options.prioritizeChunkUpdates", (Object[])PrioritizeChunkUpdates.values(), (var0) -> {
      return new TranslatableComponent(var0.getKey());
   }, (var0) -> {
      return var0.prioritizeChunkUpdates;
   }, (var0, var1, var2) -> {
      var0.prioritizeChunkUpdates = var2;
   }).setTooltip((var0) -> {
      return (var1) -> {
         Object var10000;
         switch(var1) {
         case NONE:
            var10000 = var0.font.split(PRIORITIZE_CHUNK_TOOLTIP_NONE, 200);
            break;
         case PLAYER_AFFECTED:
            var10000 = var0.font.split(PRIORITIZE_CHUNK_TOOLTIP_PLAYER_AFFECTED, 200);
            break;
         case NEARBY:
            var10000 = var0.font.split(PRIORITIZE_CHUNK_TOOLTIP_NEARBY, 200);
            break;
         default:
            var10000 = ImmutableList.of();
         }

         return (List)var10000;
      };
   });
   public static final CycleOption<AttackIndicatorStatus> ATTACK_INDICATOR = CycleOption.create("options.attackIndicator", (Object[])AttackIndicatorStatus.values(), (var0) -> {
      return new TranslatableComponent(var0.getKey());
   }, (var0) -> {
      return var0.attackIndicator;
   }, (var0, var1, var2) -> {
      var0.attackIndicator = var2;
   });
   public static final CycleOption<ChatVisiblity> CHAT_VISIBILITY = CycleOption.create("options.chat.visibility", (Object[])ChatVisiblity.values(), (var0) -> {
      return new TranslatableComponent(var0.getKey());
   }, (var0) -> {
      return var0.chatVisibility;
   }, (var0, var1, var2) -> {
      var0.chatVisibility = var2;
   });
   private static final Component GRAPHICS_TOOLTIP_FAST = new TranslatableComponent("options.graphics.fast.tooltip");
   private static final Component GRAPHICS_TOOLTIP_FABULOUS;
   private static final Component GRAPHICS_TOOLTIP_FANCY;
   public static final CycleOption<GraphicsStatus> GRAPHICS;
   public static final CycleOption GUI_SCALE;
   public static final CycleOption<String> AUDIO_DEVICE;
   public static final CycleOption<HumanoidArm> MAIN_HAND;
   public static final CycleOption<NarratorStatus> NARRATOR;
   public static final CycleOption<ParticleStatus> PARTICLES;
   public static final CycleOption<CloudStatus> RENDER_CLOUDS;
   public static final CycleOption<Boolean> TEXT_BACKGROUND;
   private static final Component CHAT_TOOLTIP_HIDE_MATCHED_NAMES;
   public static final CycleOption<Boolean> AUTO_JUMP;
   public static final CycleOption<Boolean> AUTO_SUGGESTIONS;
   public static final CycleOption<Boolean> CHAT_COLOR;
   public static final CycleOption<Boolean> HIDE_MATCHED_NAMES;
   public static final CycleOption<Boolean> CHAT_LINKS;
   public static final CycleOption<Boolean> CHAT_LINKS_PROMPT;
   public static final CycleOption<Boolean> DISCRETE_MOUSE_SCROLL;
   public static final CycleOption<Boolean> ENABLE_VSYNC;
   public static final CycleOption<Boolean> ENTITY_SHADOWS;
   public static final CycleOption<Boolean> FORCE_UNICODE_FONT;
   public static final CycleOption<Boolean> INVERT_MOUSE;
   public static final CycleOption<Boolean> REALMS_NOTIFICATIONS;
   private static final Component ALLOW_SERVER_LISTING_TOOLTIP;
   public static final CycleOption<Boolean> ALLOW_SERVER_LISTING;
   public static final CycleOption<Boolean> REDUCED_DEBUG_INFO;
   public static final CycleOption<Boolean> SHOW_SUBTITLES;
   private static final Component MOVEMENT_TOGGLE;
   private static final Component MOVEMENT_HOLD;
   public static final CycleOption<Boolean> TOGGLE_CROUCH;
   public static final CycleOption<Boolean> TOGGLE_SPRINT;
   public static final CycleOption<Boolean> TOUCHSCREEN;
   public static final CycleOption<Boolean> USE_FULLSCREEN;
   public static final CycleOption<Boolean> VIEW_BOBBING;
   private static final Component ACCESSIBILITY_TOOLTIP_DARK_MOJANG_BACKGROUND;
   public static final CycleOption<Boolean> DARK_MOJANG_STUDIOS_BACKGROUND_COLOR;
   private static final Component ACCESSIBILITY_TOOLTIP_HIDE_LIGHTNING_FLASHES;
   public static final CycleOption<Boolean> HIDE_LIGHTNING_FLASH;
   public static final CycleOption<Boolean> AUTOSAVE_INDICATOR;
   private final Component caption;

   public Option(String var1) {
      super();
      this.caption = new TranslatableComponent(var1);
   }

   public abstract AbstractWidget createButton(Options var1, int var2, int var3, int var4);

   protected Component getCaption() {
      return this.caption;
   }

   protected Component pixelValueLabel(int var1) {
      return new TranslatableComponent("options.pixel_value", new Object[]{this.getCaption(), var1});
   }

   protected Component percentValueLabel(double var1) {
      return new TranslatableComponent("options.percent_value", new Object[]{this.getCaption(), (int)(var1 * 100.0D)});
   }

   protected Component percentAddValueLabel(int var1) {
      return new TranslatableComponent("options.percent_add_value", new Object[]{this.getCaption(), var1});
   }

   protected Component genericValueLabel(Component var1) {
      return new TranslatableComponent("options.generic_value", new Object[]{this.getCaption(), var1});
   }

   protected Component genericValueLabel(int var1) {
      return this.genericValueLabel(new TextComponent(Integer.toString(var1)));
   }

   static {
      GRAPHICS_TOOLTIP_FABULOUS = new TranslatableComponent("options.graphics.fabulous.tooltip", new Object[]{(new TranslatableComponent("options.graphics.fabulous")).withStyle(ChatFormatting.ITALIC)});
      GRAPHICS_TOOLTIP_FANCY = new TranslatableComponent("options.graphics.fancy.tooltip");
      GRAPHICS = CycleOption.create("options.graphics", Arrays.asList(GraphicsStatus.values()), (List)Stream.of(GraphicsStatus.values()).filter((var0) -> {
         return var0 != GraphicsStatus.FABULOUS;
      }).collect(Collectors.toList()), () -> {
         return Minecraft.getInstance().getGpuWarnlistManager().isSkippingFabulous();
      }, (var0) -> {
         TranslatableComponent var1 = new TranslatableComponent(var0.getKey());
         return (Component)(var0 == GraphicsStatus.FABULOUS ? var1.withStyle(ChatFormatting.ITALIC) : var1);
      }, (var0) -> {
         return var0.graphicsMode;
      }, (var0, var1, var2) -> {
         Minecraft var3 = Minecraft.getInstance();
         GpuWarnlistManager var4 = var3.getGpuWarnlistManager();
         if (var2 == GraphicsStatus.FABULOUS && var4.willShowWarning()) {
            var4.showWarning();
         } else {
            var0.graphicsMode = var2;
            var3.levelRenderer.allChanged();
         }
      }).setTooltip((var0) -> {
         List var1 = var0.font.split(GRAPHICS_TOOLTIP_FAST, 200);
         List var2 = var0.font.split(GRAPHICS_TOOLTIP_FANCY, 200);
         List var3 = var0.font.split(GRAPHICS_TOOLTIP_FABULOUS, 200);
         return (var3x) -> {
            switch(var3x) {
            case FANCY:
               return var2;
            case FAST:
               return var1;
            case FABULOUS:
               return var3;
            default:
               return ImmutableList.of();
            }
         };
      });
      GUI_SCALE = CycleOption.create("options.guiScale", () -> {
         return (List)IntStream.rangeClosed(0, Minecraft.getInstance().getWindow().calculateScale(0, Minecraft.getInstance().isEnforceUnicode())).boxed().collect(Collectors.toList());
      }, (var0) -> {
         return (Component)(var0 == 0 ? new TranslatableComponent("options.guiScale.auto") : new TextComponent(Integer.toString(var0)));
      }, (var0) -> {
         return var0.guiScale;
      }, (var0, var1, var2) -> {
         var0.guiScale = var2;
      });
      AUDIO_DEVICE = CycleOption.create("options.audioDevice", () -> {
         return Stream.concat(Stream.of(""), Minecraft.getInstance().getSoundManager().getAvailableSoundDevices().stream()).toList();
      }, (var0) -> {
         if ("".equals(var0)) {
            return new TranslatableComponent("options.audioDevice.default");
         } else {
            return var0.startsWith("OpenAL Soft on ") ? new TextComponent(var0.substring(SoundEngine.OPEN_AL_SOFT_PREFIX_LENGTH)) : new TextComponent(var0);
         }
      }, (var0) -> {
         return var0.soundDevice;
      }, (var0, var1, var2) -> {
         var0.soundDevice = var2;
         SoundManager var3 = Minecraft.getInstance().getSoundManager();
         var3.reload();
         var3.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
      });
      MAIN_HAND = CycleOption.create("options.mainHand", (Object[])HumanoidArm.values(), HumanoidArm::getName, (var0) -> {
         return var0.mainHand;
      }, (var0, var1, var2) -> {
         var0.mainHand = var2;
         var0.broadcastOptions();
      });
      NARRATOR = CycleOption.create("options.narrator", (Object[])NarratorStatus.values(), (var0) -> {
         return (Component)(NarratorChatListener.INSTANCE.isActive() ? var0.getName() : new TranslatableComponent("options.narrator.notavailable"));
      }, (var0) -> {
         return var0.narratorStatus;
      }, (var0, var1, var2) -> {
         var0.narratorStatus = var2;
         NarratorChatListener.INSTANCE.updateNarratorStatus(var2);
      });
      PARTICLES = CycleOption.create("options.particles", (Object[])ParticleStatus.values(), (var0) -> {
         return new TranslatableComponent(var0.getKey());
      }, (var0) -> {
         return var0.particles;
      }, (var0, var1, var2) -> {
         var0.particles = var2;
      });
      RENDER_CLOUDS = CycleOption.create("options.renderClouds", (Object[])CloudStatus.values(), (var0) -> {
         return new TranslatableComponent(var0.getKey());
      }, (var0) -> {
         return var0.renderClouds;
      }, (var0, var1, var2) -> {
         var0.renderClouds = var2;
         if (Minecraft.useShaderTransparency()) {
            RenderTarget var3 = Minecraft.getInstance().levelRenderer.getCloudsTarget();
            if (var3 != null) {
               var3.clear(Minecraft.ON_OSX);
            }
         }

      });
      TEXT_BACKGROUND = CycleOption.createBinaryOption("options.accessibility.text_background", new TranslatableComponent("options.accessibility.text_background.chat"), new TranslatableComponent("options.accessibility.text_background.everywhere"), (var0) -> {
         return var0.backgroundForChatOnly;
      }, (var0, var1, var2) -> {
         var0.backgroundForChatOnly = var2;
      });
      CHAT_TOOLTIP_HIDE_MATCHED_NAMES = new TranslatableComponent("options.hideMatchedNames.tooltip");
      AUTO_JUMP = CycleOption.createOnOff("options.autoJump", (var0) -> {
         return var0.autoJump;
      }, (var0, var1, var2) -> {
         var0.autoJump = var2;
      });
      AUTO_SUGGESTIONS = CycleOption.createOnOff("options.autoSuggestCommands", (var0) -> {
         return var0.autoSuggestions;
      }, (var0, var1, var2) -> {
         var0.autoSuggestions = var2;
      });
      CHAT_COLOR = CycleOption.createOnOff("options.chat.color", (var0) -> {
         return var0.chatColors;
      }, (var0, var1, var2) -> {
         var0.chatColors = var2;
      });
      HIDE_MATCHED_NAMES = CycleOption.createOnOff("options.hideMatchedNames", CHAT_TOOLTIP_HIDE_MATCHED_NAMES, (var0) -> {
         return var0.hideMatchedNames;
      }, (var0, var1, var2) -> {
         var0.hideMatchedNames = var2;
      });
      CHAT_LINKS = CycleOption.createOnOff("options.chat.links", (var0) -> {
         return var0.chatLinks;
      }, (var0, var1, var2) -> {
         var0.chatLinks = var2;
      });
      CHAT_LINKS_PROMPT = CycleOption.createOnOff("options.chat.links.prompt", (var0) -> {
         return var0.chatLinksPrompt;
      }, (var0, var1, var2) -> {
         var0.chatLinksPrompt = var2;
      });
      DISCRETE_MOUSE_SCROLL = CycleOption.createOnOff("options.discrete_mouse_scroll", (var0) -> {
         return var0.discreteMouseScroll;
      }, (var0, var1, var2) -> {
         var0.discreteMouseScroll = var2;
      });
      ENABLE_VSYNC = CycleOption.createOnOff("options.vsync", (var0) -> {
         return var0.enableVsync;
      }, (var0, var1, var2) -> {
         var0.enableVsync = var2;
         if (Minecraft.getInstance().getWindow() != null) {
            Minecraft.getInstance().getWindow().updateVsync(var0.enableVsync);
         }

      });
      ENTITY_SHADOWS = CycleOption.createOnOff("options.entityShadows", (var0) -> {
         return var0.entityShadows;
      }, (var0, var1, var2) -> {
         var0.entityShadows = var2;
      });
      FORCE_UNICODE_FONT = CycleOption.createOnOff("options.forceUnicodeFont", (var0) -> {
         return var0.forceUnicodeFont;
      }, (var0, var1, var2) -> {
         var0.forceUnicodeFont = var2;
         Minecraft var3 = Minecraft.getInstance();
         if (var3.getWindow() != null) {
            var3.selectMainFont(var2);
            var3.resizeDisplay();
         }

      });
      INVERT_MOUSE = CycleOption.createOnOff("options.invertMouse", (var0) -> {
         return var0.invertYMouse;
      }, (var0, var1, var2) -> {
         var0.invertYMouse = var2;
      });
      REALMS_NOTIFICATIONS = CycleOption.createOnOff("options.realmsNotifications", (var0) -> {
         return var0.realmsNotifications;
      }, (var0, var1, var2) -> {
         var0.realmsNotifications = var2;
      });
      ALLOW_SERVER_LISTING_TOOLTIP = new TranslatableComponent("options.allowServerListing.tooltip");
      ALLOW_SERVER_LISTING = CycleOption.createOnOff("options.allowServerListing", ALLOW_SERVER_LISTING_TOOLTIP, (var0) -> {
         return var0.allowServerListing;
      }, (var0, var1, var2) -> {
         var0.allowServerListing = var2;
         var0.broadcastOptions();
      });
      REDUCED_DEBUG_INFO = CycleOption.createOnOff("options.reducedDebugInfo", (var0) -> {
         return var0.reducedDebugInfo;
      }, (var0, var1, var2) -> {
         var0.reducedDebugInfo = var2;
      });
      SHOW_SUBTITLES = CycleOption.createOnOff("options.showSubtitles", (var0) -> {
         return var0.showSubtitles;
      }, (var0, var1, var2) -> {
         var0.showSubtitles = var2;
      });
      MOVEMENT_TOGGLE = new TranslatableComponent("options.key.toggle");
      MOVEMENT_HOLD = new TranslatableComponent("options.key.hold");
      TOGGLE_CROUCH = CycleOption.createBinaryOption("key.sneak", MOVEMENT_TOGGLE, MOVEMENT_HOLD, (var0) -> {
         return var0.toggleCrouch;
      }, (var0, var1, var2) -> {
         var0.toggleCrouch = var2;
      });
      TOGGLE_SPRINT = CycleOption.createBinaryOption("key.sprint", MOVEMENT_TOGGLE, MOVEMENT_HOLD, (var0) -> {
         return var0.toggleSprint;
      }, (var0, var1, var2) -> {
         var0.toggleSprint = var2;
      });
      TOUCHSCREEN = CycleOption.createOnOff("options.touchscreen", (var0) -> {
         return var0.touchscreen;
      }, (var0, var1, var2) -> {
         var0.touchscreen = var2;
      });
      USE_FULLSCREEN = CycleOption.createOnOff("options.fullscreen", (var0) -> {
         return var0.fullscreen;
      }, (var0, var1, var2) -> {
         var0.fullscreen = var2;
         Minecraft var3 = Minecraft.getInstance();
         if (var3.getWindow() != null && var3.getWindow().isFullscreen() != var0.fullscreen) {
            var3.getWindow().toggleFullScreen();
            var0.fullscreen = var3.getWindow().isFullscreen();
         }

      });
      VIEW_BOBBING = CycleOption.createOnOff("options.viewBobbing", (var0) -> {
         return var0.bobView;
      }, (var0, var1, var2) -> {
         var0.bobView = var2;
      });
      ACCESSIBILITY_TOOLTIP_DARK_MOJANG_BACKGROUND = new TranslatableComponent("options.darkMojangStudiosBackgroundColor.tooltip");
      DARK_MOJANG_STUDIOS_BACKGROUND_COLOR = CycleOption.createOnOff("options.darkMojangStudiosBackgroundColor", ACCESSIBILITY_TOOLTIP_DARK_MOJANG_BACKGROUND, (var0) -> {
         return var0.darkMojangStudiosBackground;
      }, (var0, var1, var2) -> {
         var0.darkMojangStudiosBackground = var2;
      });
      ACCESSIBILITY_TOOLTIP_HIDE_LIGHTNING_FLASHES = new TranslatableComponent("options.hideLightningFlashes.tooltip");
      HIDE_LIGHTNING_FLASH = CycleOption.createOnOff("options.hideLightningFlashes", ACCESSIBILITY_TOOLTIP_HIDE_LIGHTNING_FLASHES, (var0) -> {
         return var0.hideLightningFlashes;
      }, (var0, var1, var2) -> {
         var0.hideLightningFlashes = var2;
      });
      AUTOSAVE_INDICATOR = CycleOption.createOnOff("options.autosaveIndicator", (var0) -> {
         return var0.showAutosaveIndicator;
      }, (var0, var1, var2) -> {
         var0.showAutosaveIndicator = var2;
      });
   }
}
