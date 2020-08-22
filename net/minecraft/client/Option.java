package net.minecraft.client;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.Util;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.ChatVisiblity;

public abstract class Option {
   public static final ProgressOption BIOME_BLEND_RADIUS = new ProgressOption("options.biomeBlendRadius", 0.0D, 7.0D, 1.0F, (var0) -> {
      return (double)var0.biomeBlendRadius;
   }, (var0, var1) -> {
      var0.biomeBlendRadius = Mth.clamp((int)var1, 0, 7);
      Minecraft.getInstance().levelRenderer.allChanged();
   }, (var0, var1) -> {
      double var2 = var1.get(var0);
      String var4 = var1.getCaption();
      int var5 = (int)var2 * 2 + 1;
      return var4 + I18n.get("options.biomeBlendRadius." + var5);
   });
   public static final ProgressOption CHAT_HEIGHT_FOCUSED = new ProgressOption("options.chat.height.focused", 0.0D, 1.0D, 0.0F, (var0) -> {
      return var0.chatHeightFocused;
   }, (var0, var1) -> {
      var0.chatHeightFocused = var1;
      Minecraft.getInstance().gui.getChat().rescaleChat();
   }, (var0, var1) -> {
      double var2 = var1.toPct(var1.get(var0));
      return var1.getCaption() + ChatComponent.getHeight(var2) + "px";
   });
   public static final ProgressOption CHAT_HEIGHT_UNFOCUSED = new ProgressOption("options.chat.height.unfocused", 0.0D, 1.0D, 0.0F, (var0) -> {
      return var0.chatHeightUnfocused;
   }, (var0, var1) -> {
      var0.chatHeightUnfocused = var1;
      Minecraft.getInstance().gui.getChat().rescaleChat();
   }, (var0, var1) -> {
      double var2 = var1.toPct(var1.get(var0));
      return var1.getCaption() + ChatComponent.getHeight(var2) + "px";
   });
   public static final ProgressOption CHAT_OPACITY = new ProgressOption("options.chat.opacity", 0.0D, 1.0D, 0.0F, (var0) -> {
      return var0.chatOpacity;
   }, (var0, var1) -> {
      var0.chatOpacity = var1;
      Minecraft.getInstance().gui.getChat().rescaleChat();
   }, (var0, var1) -> {
      double var2 = var1.toPct(var1.get(var0));
      return var1.getCaption() + (int)(var2 * 90.0D + 10.0D) + "%";
   });
   public static final ProgressOption CHAT_SCALE = new ProgressOption("options.chat.scale", 0.0D, 1.0D, 0.0F, (var0) -> {
      return var0.chatScale;
   }, (var0, var1) -> {
      var0.chatScale = var1;
      Minecraft.getInstance().gui.getChat().rescaleChat();
   }, (var0, var1) -> {
      double var2 = var1.toPct(var1.get(var0));
      String var4 = var1.getCaption();
      return var2 == 0.0D ? var4 + I18n.get("options.off") : var4 + (int)(var2 * 100.0D) + "%";
   });
   public static final ProgressOption CHAT_WIDTH = new ProgressOption("options.chat.width", 0.0D, 1.0D, 0.0F, (var0) -> {
      return var0.chatWidth;
   }, (var0, var1) -> {
      var0.chatWidth = var1;
      Minecraft.getInstance().gui.getChat().rescaleChat();
   }, (var0, var1) -> {
      double var2 = var1.toPct(var1.get(var0));
      return var1.getCaption() + ChatComponent.getWidth(var2) + "px";
   });
   public static final ProgressOption FOV = new ProgressOption("options.fov", 30.0D, 110.0D, 1.0F, (var0) -> {
      return var0.fov;
   }, (var0, var1) -> {
      var0.fov = var1;
   }, (var0, var1) -> {
      double var2 = var1.get(var0);
      String var4 = var1.getCaption();
      if (var2 == 70.0D) {
         return var4 + I18n.get("options.fov.min");
      } else {
         return var2 == var1.getMaxValue() ? var4 + I18n.get("options.fov.max") : var4 + (int)var2;
      }
   });
   public static final ProgressOption FRAMERATE_LIMIT = new ProgressOption("options.framerateLimit", 10.0D, 260.0D, 10.0F, (var0) -> {
      return (double)var0.framerateLimit;
   }, (var0, var1) -> {
      var0.framerateLimit = (int)var1;
      Minecraft.getInstance().getWindow().setFramerateLimit(var0.framerateLimit);
   }, (var0, var1) -> {
      double var2 = var1.get(var0);
      String var4 = var1.getCaption();
      return var2 == var1.getMaxValue() ? var4 + I18n.get("options.framerateLimit.max") : var4 + I18n.get("options.framerate", (int)var2);
   });
   public static final ProgressOption GAMMA = new ProgressOption("options.gamma", 0.0D, 1.0D, 0.0F, (var0) -> {
      return var0.gamma;
   }, (var0, var1) -> {
      var0.gamma = var1;
   }, (var0, var1) -> {
      double var2 = var1.toPct(var1.get(var0));
      String var4 = var1.getCaption();
      if (var2 == 0.0D) {
         return var4 + I18n.get("options.gamma.min");
      } else {
         return var2 == 1.0D ? var4 + I18n.get("options.gamma.max") : var4 + "+" + (int)(var2 * 100.0D) + "%";
      }
   });
   public static final ProgressOption MIPMAP_LEVELS = new ProgressOption("options.mipmapLevels", 0.0D, 4.0D, 1.0F, (var0) -> {
      return (double)var0.mipmapLevels;
   }, (var0, var1) -> {
      var0.mipmapLevels = (int)var1;
   }, (var0, var1) -> {
      double var2 = var1.get(var0);
      String var4 = var1.getCaption();
      return var2 == 0.0D ? var4 + I18n.get("options.off") : var4 + (int)var2;
   });
   public static final ProgressOption MOUSE_WHEEL_SENSITIVITY = new LogaritmicProgressOption("options.mouseWheelSensitivity", 0.01D, 10.0D, 0.01F, (var0) -> {
      return var0.mouseWheelSensitivity;
   }, (var0, var1) -> {
      var0.mouseWheelSensitivity = var1;
   }, (var0, var1) -> {
      double var2 = var1.toPct(var1.get(var0));
      return var1.getCaption() + String.format("%.2f", var1.toValue(var2));
   });
   public static final BooleanOption RAW_MOUSE_INPUT = new BooleanOption("options.rawMouseInput", (var0) -> {
      return var0.rawMouseInput;
   }, (var0, var1) -> {
      var0.rawMouseInput = var1;
      Window var2 = Minecraft.getInstance().getWindow();
      if (var2 != null) {
         var2.updateRawMouseInput(var1);
      }

   });
   public static final ProgressOption RENDER_DISTANCE = new ProgressOption("options.renderDistance", 2.0D, 16.0D, 1.0F, (var0) -> {
      return (double)var0.renderDistance;
   }, (var0, var1) -> {
      var0.renderDistance = (int)var1;
      Minecraft.getInstance().levelRenderer.needsUpdate();
   }, (var0, var1) -> {
      double var2 = var1.get(var0);
      return var1.getCaption() + I18n.get("options.chunks", (int)var2);
   });
   public static final ProgressOption SENSITIVITY = new ProgressOption("options.sensitivity", 0.0D, 1.0D, 0.0F, (var0) -> {
      return var0.sensitivity;
   }, (var0, var1) -> {
      var0.sensitivity = var1;
   }, (var0, var1) -> {
      double var2 = var1.toPct(var1.get(var0));
      String var4 = var1.getCaption();
      if (var2 == 0.0D) {
         return var4 + I18n.get("options.sensitivity.min");
      } else {
         return var2 == 1.0D ? var4 + I18n.get("options.sensitivity.max") : var4 + (int)(var2 * 200.0D) + "%";
      }
   });
   public static final ProgressOption TEXT_BACKGROUND_OPACITY = new ProgressOption("options.accessibility.text_background_opacity", 0.0D, 1.0D, 0.0F, (var0) -> {
      return var0.textBackgroundOpacity;
   }, (var0, var1) -> {
      var0.textBackgroundOpacity = var1;
      Minecraft.getInstance().gui.getChat().rescaleChat();
   }, (var0, var1) -> {
      return var1.getCaption() + (int)(var1.toPct(var1.get(var0)) * 100.0D) + "%";
   });
   public static final CycleOption AMBIENT_OCCLUSION = new CycleOption("options.ao", (var0, var1) -> {
      var0.ambientOcclusion = AmbientOcclusionStatus.byId(var0.ambientOcclusion.getId() + var1);
      Minecraft.getInstance().levelRenderer.allChanged();
   }, (var0, var1) -> {
      return var1.getCaption() + I18n.get(var0.ambientOcclusion.getKey());
   });
   public static final CycleOption ATTACK_INDICATOR = new CycleOption("options.attackIndicator", (var0, var1) -> {
      var0.attackIndicator = AttackIndicatorStatus.byId(var0.attackIndicator.getId() + var1);
   }, (var0, var1) -> {
      return var1.getCaption() + I18n.get(var0.attackIndicator.getKey());
   });
   public static final CycleOption CHAT_VISIBILITY = new CycleOption("options.chat.visibility", (var0, var1) -> {
      var0.chatVisibility = ChatVisiblity.byId((var0.chatVisibility.getId() + var1) % 3);
   }, (var0, var1) -> {
      return var1.getCaption() + I18n.get(var0.chatVisibility.getKey());
   });
   public static final CycleOption GRAPHICS = new CycleOption("options.graphics", (var0, var1) -> {
      var0.fancyGraphics = !var0.fancyGraphics;
      Minecraft.getInstance().levelRenderer.allChanged();
   }, (var0, var1) -> {
      return var0.fancyGraphics ? var1.getCaption() + I18n.get("options.graphics.fancy") : var1.getCaption() + I18n.get("options.graphics.fast");
   });
   public static final CycleOption GUI_SCALE = new CycleOption("options.guiScale", (var0, var1) -> {
      var0.guiScale = Integer.remainderUnsigned(var0.guiScale + var1, Minecraft.getInstance().getWindow().calculateScale(0, Minecraft.getInstance().isEnforceUnicode()) + 1);
   }, (var0, var1) -> {
      return var1.getCaption() + (var0.guiScale == 0 ? I18n.get("options.guiScale.auto") : var0.guiScale);
   });
   public static final CycleOption MAIN_HAND = new CycleOption("options.mainHand", (var0, var1) -> {
      var0.mainHand = var0.mainHand.getOpposite();
   }, (var0, var1) -> {
      return var1.getCaption() + var0.mainHand;
   });
   public static final CycleOption NARRATOR = new CycleOption("options.narrator", (var0, var1) -> {
      if (NarratorChatListener.INSTANCE.isActive()) {
         var0.narratorStatus = NarratorStatus.byId(var0.narratorStatus.getId() + var1);
      } else {
         var0.narratorStatus = NarratorStatus.OFF;
      }

      NarratorChatListener.INSTANCE.updateNarratorStatus(var0.narratorStatus);
   }, (var0, var1) -> {
      return NarratorChatListener.INSTANCE.isActive() ? var1.getCaption() + I18n.get(var0.narratorStatus.getKey()) : var1.getCaption() + I18n.get("options.narrator.notavailable");
   });
   public static final CycleOption PARTICLES = new CycleOption("options.particles", (var0, var1) -> {
      var0.particles = ParticleStatus.byId(var0.particles.getId() + var1);
   }, (var0, var1) -> {
      return var1.getCaption() + I18n.get(var0.particles.getKey());
   });
   public static final CycleOption RENDER_CLOUDS = new CycleOption("options.renderClouds", (var0, var1) -> {
      var0.renderClouds = CloudStatus.byId(var0.renderClouds.getId() + var1);
   }, (var0, var1) -> {
      return var1.getCaption() + I18n.get(var0.renderClouds.getKey());
   });
   public static final CycleOption TEXT_BACKGROUND = new CycleOption("options.accessibility.text_background", (var0, var1) -> {
      var0.backgroundForChatOnly = !var0.backgroundForChatOnly;
   }, (var0, var1) -> {
      return var1.getCaption() + I18n.get(var0.backgroundForChatOnly ? "options.accessibility.text_background.chat" : "options.accessibility.text_background.everywhere");
   });
   public static final BooleanOption AUTO_JUMP = new BooleanOption("options.autoJump", (var0) -> {
      return var0.autoJump;
   }, (var0, var1) -> {
      var0.autoJump = var1;
   });
   public static final BooleanOption AUTO_SUGGESTIONS = new BooleanOption("options.autoSuggestCommands", (var0) -> {
      return var0.autoSuggestions;
   }, (var0, var1) -> {
      var0.autoSuggestions = var1;
   });
   public static final BooleanOption CHAT_COLOR = new BooleanOption("options.chat.color", (var0) -> {
      return var0.chatColors;
   }, (var0, var1) -> {
      var0.chatColors = var1;
   });
   public static final BooleanOption CHAT_LINKS = new BooleanOption("options.chat.links", (var0) -> {
      return var0.chatLinks;
   }, (var0, var1) -> {
      var0.chatLinks = var1;
   });
   public static final BooleanOption CHAT_LINKS_PROMPT = new BooleanOption("options.chat.links.prompt", (var0) -> {
      return var0.chatLinksPrompt;
   }, (var0, var1) -> {
      var0.chatLinksPrompt = var1;
   });
   public static final BooleanOption DISCRETE_MOUSE_SCROLL = new BooleanOption("options.discrete_mouse_scroll", (var0) -> {
      return var0.discreteMouseScroll;
   }, (var0, var1) -> {
      var0.discreteMouseScroll = var1;
   });
   public static final BooleanOption ENABLE_VSYNC = new BooleanOption("options.vsync", (var0) -> {
      return var0.enableVsync;
   }, (var0, var1) -> {
      var0.enableVsync = var1;
      if (Minecraft.getInstance().getWindow() != null) {
         Minecraft.getInstance().getWindow().updateVsync(var0.enableVsync);
      }

   });
   public static final BooleanOption ENTITY_SHADOWS = new BooleanOption("options.entityShadows", (var0) -> {
      return var0.entityShadows;
   }, (var0, var1) -> {
      var0.entityShadows = var1;
   });
   public static final BooleanOption FORCE_UNICODE_FONT = new BooleanOption("options.forceUnicodeFont", (var0) -> {
      return var0.forceUnicodeFont;
   }, (var0, var1) -> {
      var0.forceUnicodeFont = var1;
      Minecraft var2 = Minecraft.getInstance();
      if (var2.getFontManager() != null) {
         var2.getFontManager().setForceUnicode(var0.forceUnicodeFont, Util.backgroundExecutor(), var2);
      }

   });
   public static final BooleanOption INVERT_MOUSE = new BooleanOption("options.invertMouse", (var0) -> {
      return var0.invertYMouse;
   }, (var0, var1) -> {
      var0.invertYMouse = var1;
   });
   public static final BooleanOption REALMS_NOTIFICATIONS = new BooleanOption("options.realmsNotifications", (var0) -> {
      return var0.realmsNotifications;
   }, (var0, var1) -> {
      var0.realmsNotifications = var1;
   });
   public static final BooleanOption REDUCED_DEBUG_INFO = new BooleanOption("options.reducedDebugInfo", (var0) -> {
      return var0.reducedDebugInfo;
   }, (var0, var1) -> {
      var0.reducedDebugInfo = var1;
   });
   public static final BooleanOption SHOW_SUBTITLES = new BooleanOption("options.showSubtitles", (var0) -> {
      return var0.showSubtitles;
   }, (var0, var1) -> {
      var0.showSubtitles = var1;
   });
   public static final BooleanOption SNOOPER_ENABLED = new BooleanOption("options.snooper", (var0) -> {
      if (var0.snooperEnabled) {
      }

      return false;
   }, (var0, var1) -> {
      var0.snooperEnabled = var1;
   });
   public static final CycleOption TOGGLE_CROUCH = new CycleOption("key.sneak", (var0, var1) -> {
      var0.toggleCrouch = !var0.toggleCrouch;
   }, (var0, var1) -> {
      return var1.getCaption() + I18n.get(var0.toggleCrouch ? "options.key.toggle" : "options.key.hold");
   });
   public static final CycleOption TOGGLE_SPRINT = new CycleOption("key.sprint", (var0, var1) -> {
      var0.toggleSprint = !var0.toggleSprint;
   }, (var0, var1) -> {
      return var1.getCaption() + I18n.get(var0.toggleSprint ? "options.key.toggle" : "options.key.hold");
   });
   public static final BooleanOption TOUCHSCREEN = new BooleanOption("options.touchscreen", (var0) -> {
      return var0.touchscreen;
   }, (var0, var1) -> {
      var0.touchscreen = var1;
   });
   public static final BooleanOption USE_FULLSCREEN = new BooleanOption("options.fullscreen", (var0) -> {
      return var0.fullscreen;
   }, (var0, var1) -> {
      var0.fullscreen = var1;
      Minecraft var2 = Minecraft.getInstance();
      if (var2.getWindow() != null && var2.getWindow().isFullscreen() != var0.fullscreen) {
         var2.getWindow().toggleFullScreen();
         var0.fullscreen = var2.getWindow().isFullscreen();
      }

   });
   public static final BooleanOption VIEW_BOBBING = new BooleanOption("options.viewBobbing", (var0) -> {
      return var0.bobView;
   }, (var0, var1) -> {
      var0.bobView = var1;
   });
   private final String captionId;

   public Option(String var1) {
      this.captionId = var1;
   }

   public abstract AbstractWidget createButton(Options var1, int var2, int var3, int var4);

   public String getCaption() {
      return I18n.get(this.captionId) + ": ";
   }
}
