package net.minecraft.client.gui.screens;

import com.mojang.authlib.minecraft.BanDetails;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.gui.screens.RealmsNotificationsScreen;
import java.io.IOException;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
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
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.slf4j.Logger;

public class TitleScreen extends Screen {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Component TITLE = Component.translatable("narrator.screen.title");
   private static final Component COPYRIGHT_TEXT = Component.translatable("title.credits");
   private static final String DEMO_LEVEL_ID = "Demo_World";
   @Nullable
   private SplashRenderer splash;
   @Nullable
   private RealmsNotificationsScreen realmsNotificationsScreen;
   private boolean fading;
   private long fadeInStart;
   private final LogoRenderer logoRenderer;

   public TitleScreen() {
      this(false);
   }

   public TitleScreen(boolean var1) {
      this(var1, (LogoRenderer)null);
   }

   public TitleScreen(boolean var1, @Nullable LogoRenderer var2) {
      super(TITLE);
      this.fading = var1;
      this.logoRenderer = (LogoRenderer)Objects.requireNonNullElseGet(var2, () -> new LogoRenderer(false));
   }

   private boolean realmsNotificationsEnabled() {
      return this.realmsNotificationsScreen != null;
   }

   public void tick() {
      if (this.realmsNotificationsEnabled()) {
         this.realmsNotificationsScreen.tick();
      }

   }

   public static void registerTextures(TextureManager var0) {
      var0.registerForNextReload(LogoRenderer.MINECRAFT_LOGO);
      var0.registerForNextReload(LogoRenderer.MINECRAFT_EDITION);
      var0.registerForNextReload(PanoramaRenderer.PANORAMA_OVERLAY);
      CUBE_MAP.registerTextures(var0);
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

      int var1 = this.font.width((FormattedText)COPYRIGHT_TEXT);
      int var2 = this.width - var1 - 2;
      boolean var3 = true;
      int var4 = this.height / 4 + 48;
      if (this.minecraft.isDemo()) {
         var4 = this.createDemoMenuOptions(var4, 24);
      } else {
         var4 = this.createNormalMenuOptions(var4, 24);
      }

      var4 = this.createTestWorldButton(var4, 24);
      SpriteIconButton var5 = (SpriteIconButton)this.addRenderableWidget(CommonButtons.language(20, (var1x) -> this.minecraft.setScreen(new LanguageSelectScreen(this, this.minecraft.options, this.minecraft.getLanguageManager())), true));
      int var10001 = this.width / 2 - 124;
      var4 += 36;
      var5.setPosition(var10001, var4);
      this.addRenderableWidget(Button.builder(Component.translatable("menu.options"), (var1x) -> this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options))).bounds(this.width / 2 - 100, var4, 98, 20).build());
      this.addRenderableWidget(Button.builder(Component.translatable("menu.quit"), (var1x) -> this.minecraft.stop()).bounds(this.width / 2 + 2, var4, 98, 20).build());
      SpriteIconButton var6 = (SpriteIconButton)this.addRenderableWidget(CommonButtons.accessibility(20, (var1x) -> this.minecraft.setScreen(new AccessibilityOptionsScreen(this, this.minecraft.options)), true));
      var6.setPosition(this.width / 2 + 104, var4);
      this.addRenderableWidget(new PlainTextButton(var2, this.height - 10, var1, 10, COPYRIGHT_TEXT, (var1x) -> this.minecraft.setScreen(new CreditsAndAttributionScreen(this)), this.font));
      if (this.realmsNotificationsScreen == null) {
         this.realmsNotificationsScreen = new RealmsNotificationsScreen();
      }

      if (this.realmsNotificationsEnabled()) {
         this.realmsNotificationsScreen.init(this.minecraft, this.width, this.height);
      }

   }

   private int createTestWorldButton(int var1, int var2) {
      if (SharedConstants.IS_RUNNING_IN_IDE) {
         this.addRenderableWidget(Button.builder(Component.literal("Create Test World"), (var1x) -> CreateWorldScreen.testWorld(this.minecraft, this)).bounds(this.width / 2 - 100, var1 += var2, 200, 20).build());
      }

      return var1;
   }

   private int createNormalMenuOptions(int var1, int var2) {
      this.addRenderableWidget(Button.builder(Component.translatable("menu.singleplayer"), (var1x) -> this.minecraft.setScreen(new SelectWorldScreen(this))).bounds(this.width / 2 - 100, var1, 200, 20).build());
      Component var3 = this.getMultiplayerDisabledReason();
      boolean var4 = var3 == null;
      Tooltip var5 = var3 != null ? Tooltip.create(var3) : null;
      int var6;
      ((Button)this.addRenderableWidget(Button.builder(Component.translatable("menu.multiplayer"), (var1x) -> {
         Object var2 = this.minecraft.options.skipMultiplayerWarning ? new JoinMultiplayerScreen(this) : new SafetyScreen(this);
         this.minecraft.setScreen((Screen)var2);
      }).bounds(this.width / 2 - 100, var6 = var1 + var2, 200, 20).tooltip(var5).build())).active = var4;
      ((Button)this.addRenderableWidget(Button.builder(Component.translatable("menu.online"), (var1x) -> this.minecraft.setScreen(new RealmsMainScreen(this))).bounds(this.width / 2 - 100, var1 = var6 + var2, 200, 20).tooltip(var5).build())).active = var4;
      return var1;
   }

   @Nullable
   private Component getMultiplayerDisabledReason() {
      if (this.minecraft.allowsMultiplayer()) {
         return null;
      } else if (this.minecraft.isNameBanned()) {
         return Component.translatable("title.multiplayer.disabled.banned.name");
      } else {
         BanDetails var1 = this.minecraft.multiplayerBan();
         if (var1 != null) {
            return var1.expires() != null ? Component.translatable("title.multiplayer.disabled.banned.temporary") : Component.translatable("title.multiplayer.disabled.banned.permanent");
         } else {
            return Component.translatable("title.multiplayer.disabled");
         }
      }
   }

   private int createDemoMenuOptions(int var1, int var2) {
      boolean var3 = this.checkDemoWorldPresence();
      this.addRenderableWidget(Button.builder(Component.translatable("menu.playdemo"), (var2x) -> {
         if (var3) {
            this.minecraft.createWorldOpenFlows().openWorld("Demo_World", () -> this.minecraft.setScreen(this));
         } else {
            this.minecraft.createWorldOpenFlows().createFreshLevel("Demo_World", MinecraftServer.DEMO_SETTINGS, WorldOptions.DEMO_OPTIONS, WorldPresets::createNormalWorldDimensions, this);
         }

      }).bounds(this.width / 2 - 100, var1, 200, 20).build());
      int var5;
      Button var4 = (Button)this.addRenderableWidget(Button.builder(Component.translatable("menu.resetdemo"), (var1x) -> {
         LevelStorageSource var2 = this.minecraft.getLevelSource();

         try (LevelStorageSource.LevelStorageAccess var3 = var2.createAccess("Demo_World")) {
            if (var3.hasWorldData()) {
               this.minecraft.setScreen(new ConfirmScreen(this::confirmDemo, Component.translatable("selectWorld.deleteQuestion"), Component.translatable("selectWorld.deleteWarning", MinecraftServer.DEMO_SETTINGS.levelName()), Component.translatable("selectWorld.deleteButton"), CommonComponents.GUI_CANCEL));
            }
         } catch (IOException var8) {
            SystemToast.onWorldAccessFailure(this.minecraft, "Demo_World");
            LOGGER.warn("Failed to access demo world", var8);
         }

      }).bounds(this.width / 2 - 100, var5 = var1 + var2, 200, 20).build());
      var4.active = var3;
      return var5;
   }

   private boolean checkDemoWorldPresence() {
      try (LevelStorageSource.LevelStorageAccess var1 = this.minecraft.getLevelSource().createAccess("Demo_World")) {
         return var1.hasWorldData();
      } catch (IOException var6) {
         SystemToast.onWorldAccessFailure(this.minecraft, "Demo_World");
         LOGGER.warn("Failed to read demo world data", var6);
         return false;
      }
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      if (this.fadeInStart == 0L && this.fading) {
         this.fadeInStart = Util.getMillis();
      }

      float var5 = 1.0F;
      if (this.fading) {
         float var6 = (float)(Util.getMillis() - this.fadeInStart) / 2000.0F;
         if (var6 > 1.0F) {
            this.fading = false;
         } else {
            var6 = Mth.clamp(var6, 0.0F, 1.0F);
            var5 = Mth.clampedMap(var6, 0.5F, 1.0F, 0.0F, 1.0F);
         }

         this.fadeWidgets(var5);
      }

      this.renderPanorama(var1, var4);
      super.render(var1, var2, var3, var4);
      this.logoRenderer.renderLogo(var1, this.width, this.logoRenderer.keepLogoThroughFade() ? 1.0F : var5);
      if (this.splash != null && !(Boolean)this.minecraft.options.hideSplashTexts().get()) {
         this.splash.render(var1, this.width, this.font, var5);
      }

      String var8 = "Minecraft " + SharedConstants.getCurrentVersion().name();
      if (this.minecraft.isDemo()) {
         var8 = var8 + " Demo";
      } else {
         var8 = var8 + ("release".equalsIgnoreCase(this.minecraft.getVersionType()) ? "" : "/" + this.minecraft.getVersionType());
      }

      if (Minecraft.checkModStatus().shouldReportAsModified()) {
         var8 = var8 + I18n.get("menu.modded");
      }

      var1.drawString(this.font, (String)var8, 2, this.height - 10, ARGB.color(var5, -1));
      if (this.realmsNotificationsEnabled() && var5 >= 1.0F) {
         this.realmsNotificationsScreen.render(var1, var2, var3, var4);
      }

   }

   public void renderBackground(GuiGraphics var1, int var2, int var3, float var4) {
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (super.mouseClicked(var1, var3, var5)) {
         return true;
      } else {
         return this.realmsNotificationsEnabled() && this.realmsNotificationsScreen.mouseClicked(var1, var3, var5);
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

   private void confirmDemo(boolean var1) {
      if (var1) {
         try (LevelStorageSource.LevelStorageAccess var2 = this.minecraft.getLevelSource().createAccess("Demo_World")) {
            var2.deleteLevel();
         } catch (IOException var7) {
            SystemToast.onWorldDeleteFailure(this.minecraft, "Demo_World");
            LOGGER.warn("Failed to delete demo world", var7);
         }
      }

      this.minecraft.setScreen(this);
   }
}
