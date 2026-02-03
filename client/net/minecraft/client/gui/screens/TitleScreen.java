package net.minecraft.client.gui.screens;

import com.mojang.authlib.minecraft.BanDetails;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.gui.screens.RealmsNotificationsScreen;
import java.io.IOException;
import java.util.Objects;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CommonButtons;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.SafetyScreen;
import net.minecraft.client.gui.screens.options.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screens.options.LanguageSelectScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class TitleScreen extends Screen {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Component TITLE = Component.translatable("narrator.screen.title");
   private static final Component COPYRIGHT_TEXT = Component.translatable("title.credits");
   private static final String DEMO_LEVEL_ID = "Demo_World";
   private @Nullable SplashRenderer splash;
   private @Nullable RealmsNotificationsScreen realmsNotificationsScreen;
   private boolean fading;
   private long fadeInStart;
   private final LogoRenderer logoRenderer;

   public TitleScreen() {
      this(false);
   }

   public TitleScreen(final boolean fading) {
      this(fading, (LogoRenderer)null);
   }

   public TitleScreen(final boolean fading, final @Nullable LogoRenderer logoRenderer) {
      super(TITLE);
      this.fading = fading;
      this.logoRenderer = (LogoRenderer)Objects.requireNonNullElseGet(logoRenderer, () -> new LogoRenderer(false));
   }

   private boolean realmsNotificationsEnabled() {
      return this.realmsNotificationsScreen != null;
   }

   public void tick() {
      if (this.realmsNotificationsEnabled()) {
         this.realmsNotificationsScreen.tick();
      }

   }

   public static void registerTextures(final TextureManager textureManager) {
      textureManager.registerForNextReload(LogoRenderer.MINECRAFT_LOGO);
      textureManager.registerForNextReload(LogoRenderer.MINECRAFT_EDITION);
      textureManager.registerForNextReload(PanoramaRenderer.PANORAMA_OVERLAY);
   }

   public boolean isPauseScreen() {
      return false;
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   protected void init() {
      if (this.splash == null) {
         this.splash = this.minecraft.getSplashManager().getSplash();
      }

      int copyrightWidth = this.font.width((FormattedText)COPYRIGHT_TEXT);
      int copyrightX = this.width - copyrightWidth - 2;
      int spacing = 24;
      int topPos = this.height / 4 + 48;
      if (this.minecraft.isDemo()) {
         topPos = this.createDemoMenuOptions(topPos, 24);
      } else {
         topPos = this.createNormalMenuOptions(topPos, 24);
      }

      topPos = this.createTestWorldButton(topPos, 24);
      SpriteIconButton language = (SpriteIconButton)this.addRenderableWidget(CommonButtons.language(20, (button) -> this.minecraft.setScreen(new LanguageSelectScreen(this, this.minecraft.options, this.minecraft.getLanguageManager())), true));
      int var10001 = this.width / 2 - 124;
      topPos += 36;
      language.setPosition(var10001, topPos);
      this.addRenderableWidget(Button.builder(Component.translatable("menu.options"), (button) -> this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options, false))).bounds(this.width / 2 - 100, topPos, 98, 20).build());
      this.addRenderableWidget(Button.builder(Component.translatable("menu.quit"), (button) -> this.minecraft.stop()).bounds(this.width / 2 + 2, topPos, 98, 20).build());
      SpriteIconButton accessibility = (SpriteIconButton)this.addRenderableWidget(CommonButtons.accessibility(20, (button) -> this.minecraft.setScreen(new AccessibilityOptionsScreen(this, this.minecraft.options)), true));
      accessibility.setPosition(this.width / 2 + 104, topPos);
      this.addRenderableWidget(new PlainTextButton(copyrightX, this.height - 10, copyrightWidth, 10, COPYRIGHT_TEXT, (button) -> this.minecraft.setScreen(new CreditsAndAttributionScreen(this)), this.font));
      if (this.realmsNotificationsScreen == null) {
         this.realmsNotificationsScreen = new RealmsNotificationsScreen();
      }

      if (this.realmsNotificationsEnabled()) {
         this.realmsNotificationsScreen.init(this.width, this.height);
      }

   }

   private int createTestWorldButton(int topPos, final int spacing) {
      if (SharedConstants.IS_RUNNING_IN_IDE) {
         this.addRenderableWidget(Button.builder(Component.literal("Create Test World"), (button) -> CreateWorldScreen.testWorld(this.minecraft, () -> this.minecraft.setScreen(this))).bounds(this.width / 2 - 100, topPos += spacing, 200, 20).build());
      }

      return topPos;
   }

   private int createNormalMenuOptions(int topPos, final int spacing) {
      this.addRenderableWidget(Button.builder(Component.translatable("menu.singleplayer"), (button) -> this.minecraft.setScreen(new SelectWorldScreen(this))).bounds(this.width / 2 - 100, topPos, 200, 20).build());
      Component multiplayerDisabledReason = this.getMultiplayerDisabledReason();
      boolean multiplayerAllowed = multiplayerDisabledReason == null;
      Tooltip tooltip = multiplayerDisabledReason != null ? Tooltip.create(multiplayerDisabledReason) : null;
      int var6;
      ((Button)this.addRenderableWidget(Button.builder(Component.translatable("menu.multiplayer"), (button) -> {
         Screen screen = (Screen)(this.minecraft.options.skipMultiplayerWarning ? new JoinMultiplayerScreen(this) : new SafetyScreen(this));
         this.minecraft.setScreen(screen);
      }).bounds(this.width / 2 - 100, var6 = topPos + spacing, 200, 20).tooltip(tooltip).build())).active = multiplayerAllowed;
      ((Button)this.addRenderableWidget(Button.builder(Component.translatable("menu.online"), (button) -> this.minecraft.setScreen(new RealmsMainScreen(this))).bounds(this.width / 2 - 100, topPos = var6 + spacing, 200, 20).tooltip(tooltip).build())).active = multiplayerAllowed;
      return topPos;
   }

   private @Nullable Component getMultiplayerDisabledReason() {
      if (this.minecraft.allowsMultiplayer()) {
         return null;
      } else if (this.minecraft.isNameBanned()) {
         return Component.translatable("title.multiplayer.disabled.banned.name");
      } else {
         BanDetails multiplayerBan = this.minecraft.multiplayerBan();
         if (multiplayerBan != null) {
            return multiplayerBan.expires() != null ? Component.translatable("title.multiplayer.disabled.banned.temporary") : Component.translatable("title.multiplayer.disabled.banned.permanent");
         } else {
            return Component.translatable("title.multiplayer.disabled");
         }
      }
   }

   private int createDemoMenuOptions(int topPos, final int spacing) {
      boolean demoWorldPresent = this.checkDemoWorldPresence();
      this.addRenderableWidget(Button.builder(Component.translatable("menu.playdemo"), (button) -> {
         if (demoWorldPresent) {
            this.minecraft.createWorldOpenFlows().openWorld("Demo_World", () -> this.minecraft.setScreen(this));
         } else {
            this.minecraft.createWorldOpenFlows().createFreshLevel("Demo_World", MinecraftServer.DEMO_SETTINGS, WorldOptions.DEMO_OPTIONS, WorldPresets::createNormalWorldDimensions, this);
         }

      }).bounds(this.width / 2 - 100, topPos, 200, 20).build());
      int var5;
      Button resetDemoButton = (Button)this.addRenderableWidget(Button.builder(Component.translatable("menu.resetdemo"), (button) -> {
         LevelStorageSource levelSource = this.minecraft.getLevelSource();

         try (LevelStorageSource.LevelStorageAccess levelAccess = levelSource.createAccess("Demo_World")) {
            if (levelAccess.hasWorldData()) {
               this.minecraft.setScreen(new ConfirmScreen(this::confirmDemo, Component.translatable("selectWorld.deleteQuestion"), Component.translatable("selectWorld.deleteWarning", MinecraftServer.DEMO_SETTINGS.levelName()), Component.translatable("selectWorld.deleteButton"), CommonComponents.GUI_CANCEL));
            }
         } catch (IOException e) {
            SystemToast.onWorldAccessFailure(this.minecraft, "Demo_World");
            LOGGER.warn("Failed to access demo world", e);
         }

      }).bounds(this.width / 2 - 100, var5 = topPos + spacing, 200, 20).build());
      resetDemoButton.active = demoWorldPresent;
      return var5;
   }

   private boolean checkDemoWorldPresence() {
      try (LevelStorageSource.LevelStorageAccess levelSource = this.minecraft.getLevelSource().createAccess("Demo_World")) {
         return levelSource.hasWorldData();
      } catch (IOException e) {
         SystemToast.onWorldAccessFailure(this.minecraft, "Demo_World");
         LOGGER.warn("Failed to read demo world data", e);
         return false;
      }
   }

   public void render(final GuiGraphics graphics, final int mouseX, final int mouseY, final float a) {
      if (this.fadeInStart == 0L && this.fading) {
         this.fadeInStart = Util.getMillis();
      }

      float widgetFade = 1.0F;
      if (this.fading) {
         float fade = (float)(Util.getMillis() - this.fadeInStart) / 2000.0F;
         if (fade > 1.0F) {
            this.fading = false;
         } else {
            fade = Mth.clamp(fade, 0.0F, 1.0F);
            widgetFade = Mth.clampedMap(fade, 0.5F, 1.0F, 0.0F, 1.0F);
         }

         this.fadeWidgets(widgetFade);
      }

      this.renderPanorama(graphics, a);
      super.render(graphics, mouseX, mouseY, a);
      this.logoRenderer.renderLogo(graphics, this.width, this.logoRenderer.keepLogoThroughFade() ? 1.0F : widgetFade);
      if (this.splash != null && !(Boolean)this.minecraft.options.hideSplashTexts().get()) {
         this.splash.render(graphics, this.width, this.font, widgetFade);
      }

      String versionString = "Minecraft " + SharedConstants.getCurrentVersion().name();
      if (this.minecraft.isDemo()) {
         versionString = versionString + " Demo";
      }

      if (Minecraft.checkModStatus().shouldReportAsModified()) {
         versionString = versionString + I18n.get("menu.modded");
      }

      graphics.drawString(this.font, (String)versionString, 2, this.height - 10, ARGB.white(widgetFade));
      if (this.realmsNotificationsEnabled() && widgetFade >= 1.0F) {
         this.realmsNotificationsScreen.render(graphics, mouseX, mouseY, a);
      }

   }

   public void renderBackground(final GuiGraphics graphics, final int mouseX, final int mouseY, final float a) {
   }

   public boolean mouseClicked(final MouseButtonEvent event, final boolean doubleClick) {
      if (super.mouseClicked(event, doubleClick)) {
         return true;
      } else {
         return this.realmsNotificationsEnabled() && this.realmsNotificationsScreen.mouseClicked(event, doubleClick);
      }
   }

   public void removed() {
      if (this.realmsNotificationsScreen != null) {
         this.realmsNotificationsScreen.removed();
      }

   }

   public void added() {
      super.added();
      if (this.realmsNotificationsScreen != null) {
         this.realmsNotificationsScreen.added();
      }

   }

   private void confirmDemo(final boolean result) {
      if (result) {
         try (LevelStorageSource.LevelStorageAccess levelSource = this.minecraft.getLevelSource().createAccess("Demo_World")) {
            levelSource.deleteLevel();
         } catch (IOException e) {
            SystemToast.onWorldDeleteFailure(this.minecraft, "Demo_World");
            LOGGER.warn("Failed to delete demo world", e);
         }
      }

      this.minecraft.setScreen(this);
   }

   public boolean canInterruptWithAnotherScreen() {
      return true;
   }
}
