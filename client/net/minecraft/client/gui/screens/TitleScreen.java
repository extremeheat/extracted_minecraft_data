package net.minecraft.client.gui.screens;

import com.google.common.util.concurrent.Runnables;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.LevelStorageSource;

public class TitleScreen extends Screen {
   public static final CubeMap CUBE_MAP = new CubeMap(new ResourceLocation("textures/gui/title/background/panorama"));
   private static final ResourceLocation PANORAMA_OVERLAY = new ResourceLocation("textures/gui/title/background/panorama_overlay.png");
   private static final ResourceLocation ACCESSIBILITY_TEXTURE = new ResourceLocation("textures/gui/accessibility.png");
   private final boolean minceraftEasterEgg;
   @Nullable
   private String splash;
   private Button resetDemoButton;
   @Nullable
   private TitleScreen.WarningMessageWidget warningMessage;
   private static final ResourceLocation MINECRAFT_LOGO = new ResourceLocation("textures/gui/title/minecraft.png");
   private static final ResourceLocation MINECRAFT_EDITION = new ResourceLocation("textures/gui/title/edition.png");
   private boolean realmsNotificationsInitialized;
   private Screen realmsNotificationsScreen;
   private int copyrightWidth;
   private int copyrightX;
   private final PanoramaRenderer panorama;
   private final boolean fading;
   private long fadeInStart;

   public TitleScreen() {
      this(false);
   }

   public TitleScreen(boolean var1) {
      super(new TranslatableComponent("narrator.screen.title", new Object[0]));
      this.panorama = new PanoramaRenderer(CUBE_MAP);
      this.fading = var1;
      this.minceraftEasterEgg = (double)(new Random()).nextFloat() < 1.0E-4D;
      if (!GLX.supportsOpenGL2() && !GLX.isNextGen()) {
         this.warningMessage = new TitleScreen.WarningMessageWidget((new TranslatableComponent("title.oldgl.eol.line1", new Object[0])).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD), (new TranslatableComponent("title.oldgl.eol.line2", new Object[0])).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD), "https://help.mojang.com/customer/portal/articles/325948?ref=game");
      }

   }

   private boolean realmsNotificationsEnabled() {
      return this.minecraft.options.realmsNotifications && this.realmsNotificationsScreen != null;
   }

   public void tick() {
      if (this.realmsNotificationsEnabled()) {
         this.realmsNotificationsScreen.tick();
      }

   }

   public static CompletableFuture<Void> preloadResources(TextureManager var0, Executor var1) {
      return CompletableFuture.allOf(var0.preload(MINECRAFT_LOGO, var1), var0.preload(MINECRAFT_EDITION, var1), var0.preload(PANORAMA_OVERLAY, var1), CUBE_MAP.preload(var0, var1));
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

      this.copyrightWidth = this.font.width("Copyright Mojang AB. Do not distribute!");
      this.copyrightX = this.width - this.copyrightWidth - 2;
      boolean var1 = true;
      int var2 = this.height / 4 + 48;
      if (this.minecraft.isDemo()) {
         this.createDemoMenuOptions(var2, 24);
      } else {
         this.createNormalMenuOptions(var2, 24);
      }

      this.addButton(new ImageButton(this.width / 2 - 124, var2 + 72 + 12, 20, 20, 0, 106, 20, Button.WIDGETS_LOCATION, 256, 256, (var1x) -> {
         this.minecraft.setScreen(new LanguageSelectScreen(this, this.minecraft.options, this.minecraft.getLanguageManager()));
      }, I18n.get("narrator.button.language")));
      this.addButton(new Button(this.width / 2 - 100, var2 + 72 + 12, 98, 20, I18n.get("menu.options"), (var1x) -> {
         this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options));
      }));
      this.addButton(new Button(this.width / 2 + 2, var2 + 72 + 12, 98, 20, I18n.get("menu.quit"), (var1x) -> {
         this.minecraft.stop();
      }));
      this.addButton(new ImageButton(this.width / 2 + 104, var2 + 72 + 12, 20, 20, 0, 0, 20, ACCESSIBILITY_TEXTURE, 32, 64, (var1x) -> {
         this.minecraft.setScreen(new AccessibilityOptionsScreen(this, this.minecraft.options));
      }, I18n.get("narrator.button.accessibility")));
      if (this.warningMessage != null) {
         this.warningMessage.updatePosition(var2);
      }

      this.minecraft.setConnectedToRealms(false);
      if (this.minecraft.options.realmsNotifications && !this.realmsNotificationsInitialized) {
         RealmsBridge var3 = new RealmsBridge();
         this.realmsNotificationsScreen = var3.getNotificationScreen(this);
         this.realmsNotificationsInitialized = true;
      }

      if (this.realmsNotificationsEnabled()) {
         this.realmsNotificationsScreen.init(this.minecraft, this.width, this.height);
      }

   }

   private void createNormalMenuOptions(int var1, int var2) {
      this.addButton(new Button(this.width / 2 - 100, var1, 200, 20, I18n.get("menu.singleplayer"), (var1x) -> {
         this.minecraft.setScreen(new SelectWorldScreen(this));
      }));
      this.addButton(new Button(this.width / 2 - 100, var1 + var2 * 1, 200, 20, I18n.get("menu.multiplayer"), (var1x) -> {
         this.minecraft.setScreen(new JoinMultiplayerScreen(this));
      }));
      this.addButton(new Button(this.width / 2 - 100, var1 + var2 * 2, 200, 20, I18n.get("menu.online"), (var1x) -> {
         this.realmsButtonClicked();
      }));
   }

   private void createDemoMenuOptions(int var1, int var2) {
      this.addButton(new Button(this.width / 2 - 100, var1, 200, 20, I18n.get("menu.playdemo"), (var1x) -> {
         this.minecraft.selectLevel("Demo_World", "Demo_World", MinecraftServer.DEMO_SETTINGS);
      }));
      this.resetDemoButton = (Button)this.addButton(new Button(this.width / 2 - 100, var1 + var2 * 1, 200, 20, I18n.get("menu.resetdemo"), (var1x) -> {
         LevelStorageSource var2 = this.minecraft.getLevelSource();
         LevelData var3 = var2.getDataTagFor("Demo_World");
         if (var3 != null) {
            this.minecraft.setScreen(new ConfirmScreen(this::confirmDemo, new TranslatableComponent("selectWorld.deleteQuestion", new Object[0]), new TranslatableComponent("selectWorld.deleteWarning", new Object[]{var3.getLevelName()}), I18n.get("selectWorld.deleteButton"), I18n.get("gui.cancel")));
         }

      }));
      LevelStorageSource var3 = this.minecraft.getLevelSource();
      LevelData var4 = var3.getDataTagFor("Demo_World");
      if (var4 == null) {
         this.resetDemoButton.active = false;
      }

   }

   private void realmsButtonClicked() {
      RealmsBridge var1 = new RealmsBridge();
      var1.switchToRealms(this);
   }

   public void render(int var1, int var2, float var3) {
      if (this.fadeInStart == 0L && this.fading) {
         this.fadeInStart = Util.getMillis();
      }

      float var4 = this.fading ? (float)(Util.getMillis() - this.fadeInStart) / 1000.0F : 1.0F;
      fill(0, 0, this.width, this.height, -1);
      this.panorama.render(var3, Mth.clamp(var4, 0.0F, 1.0F));
      boolean var5 = true;
      int var6 = this.width / 2 - 137;
      boolean var7 = true;
      this.minecraft.getTextureManager().bind(PANORAMA_OVERLAY);
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, this.fading ? (float)Mth.ceil(Mth.clamp(var4, 0.0F, 1.0F)) : 1.0F);
      blit(0, 0, this.width, this.height, 0.0F, 0.0F, 16, 128, 16, 128);
      float var8 = this.fading ? Mth.clamp(var4 - 1.0F, 0.0F, 1.0F) : 1.0F;
      int var9 = Mth.ceil(var8 * 255.0F) << 24;
      if ((var9 & -67108864) != 0) {
         this.minecraft.getTextureManager().bind(MINECRAFT_LOGO);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, var8);
         if (this.minceraftEasterEgg) {
            this.blit(var6 + 0, 30, 0, 0, 99, 44);
            this.blit(var6 + 99, 30, 129, 0, 27, 44);
            this.blit(var6 + 99 + 26, 30, 126, 0, 3, 44);
            this.blit(var6 + 99 + 26 + 3, 30, 99, 0, 26, 44);
            this.blit(var6 + 155, 30, 0, 45, 155, 44);
         } else {
            this.blit(var6 + 0, 30, 0, 0, 155, 44);
            this.blit(var6 + 155, 30, 0, 45, 155, 44);
         }

         this.minecraft.getTextureManager().bind(MINECRAFT_EDITION);
         blit(var6 + 88, 67, 0.0F, 0.0F, 98, 14, 128, 16);
         if (this.splash != null) {
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float)(this.width / 2 + 90), 70.0F, 0.0F);
            GlStateManager.rotatef(-20.0F, 0.0F, 0.0F, 1.0F);
            float var10 = 1.8F - Mth.abs(Mth.sin((float)(Util.getMillis() % 1000L) / 1000.0F * 6.2831855F) * 0.1F);
            var10 = var10 * 100.0F / (float)(this.font.width(this.splash) + 32);
            GlStateManager.scalef(var10, var10, var10);
            this.drawCenteredString(this.font, this.splash, 0, -8, 16776960 | var9);
            GlStateManager.popMatrix();
         }

         String var13 = "Minecraft " + SharedConstants.getCurrentVersion().getName();
         if (this.minecraft.isDemo()) {
            var13 = var13 + " Demo";
         } else {
            var13 = var13 + ("release".equalsIgnoreCase(this.minecraft.getVersionType()) ? "" : "/" + this.minecraft.getVersionType());
         }

         this.drawString(this.font, var13, 2, this.height - 10, 16777215 | var9);
         this.drawString(this.font, "Copyright Mojang AB. Do not distribute!", this.copyrightX, this.height - 10, 16777215 | var9);
         if (var1 > this.copyrightX && var1 < this.copyrightX + this.copyrightWidth && var2 > this.height - 10 && var2 < this.height) {
            fill(this.copyrightX, this.height - 1, this.copyrightX + this.copyrightWidth, this.height, 16777215 | var9);
         }

         if (this.warningMessage != null) {
            this.warningMessage.render(var9);
         }

         Iterator var11 = this.buttons.iterator();

         while(var11.hasNext()) {
            AbstractWidget var12 = (AbstractWidget)var11.next();
            var12.setAlpha(var8);
         }

         super.render(var1, var2, var3);
         if (this.realmsNotificationsEnabled() && var8 >= 1.0F) {
            this.realmsNotificationsScreen.render(var1, var2, var3);
         }

      }
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (super.mouseClicked(var1, var3, var5)) {
         return true;
      } else if (this.warningMessage != null && this.warningMessage.mouseClicked(var1, var3)) {
         return true;
      } else if (this.realmsNotificationsEnabled() && this.realmsNotificationsScreen.mouseClicked(var1, var3, var5)) {
         return true;
      } else {
         if (var1 > (double)this.copyrightX && var1 < (double)(this.copyrightX + this.copyrightWidth) && var3 > (double)(this.height - 10) && var3 < (double)this.height) {
            this.minecraft.setScreen(new WinScreen(false, Runnables.doNothing()));
         }

         return false;
      }
   }

   public void removed() {
      if (this.realmsNotificationsScreen != null) {
         this.realmsNotificationsScreen.removed();
      }

   }

   private void confirmDemo(boolean var1) {
      if (var1) {
         LevelStorageSource var2 = this.minecraft.getLevelSource();
         var2.deleteLevel("Demo_World");
      }

      this.minecraft.setScreen(this);
   }

   class WarningMessageWidget {
      private int warningClickWidth;
      private int warningx0;
      private int warningy0;
      private int warningx1;
      private int warningy1;
      private final Component warningMessageTop;
      private final Component warningMessageBottom;
      private final String warningMessageUrl;

      public WarningMessageWidget(Component var2, Component var3, String var4) {
         super();
         this.warningMessageTop = var2;
         this.warningMessageBottom = var3;
         this.warningMessageUrl = var4;
      }

      public void updatePosition(int var1) {
         int var2 = TitleScreen.this.font.width(this.warningMessageTop.getString());
         this.warningClickWidth = TitleScreen.this.font.width(this.warningMessageBottom.getString());
         int var3 = Math.max(var2, this.warningClickWidth);
         this.warningx0 = (TitleScreen.this.width - var3) / 2;
         this.warningy0 = var1 - 24;
         this.warningx1 = this.warningx0 + var3;
         this.warningy1 = this.warningy0 + 24;
      }

      public void render(int var1) {
         GuiComponent.fill(this.warningx0 - 2, this.warningy0 - 2, this.warningx1 + 2, this.warningy1 - 1, 1428160512);
         TitleScreen.this.drawString(TitleScreen.this.font, this.warningMessageTop.getColoredString(), this.warningx0, this.warningy0, 16777215 | var1);
         TitleScreen.this.drawString(TitleScreen.this.font, this.warningMessageBottom.getColoredString(), (TitleScreen.this.width - this.warningClickWidth) / 2, this.warningy0 + 12, 16777215 | var1);
      }

      public boolean mouseClicked(double var1, double var3) {
         if (!StringUtil.isNullOrEmpty(this.warningMessageUrl) && var1 >= (double)this.warningx0 && var1 <= (double)this.warningx1 && var3 >= (double)this.warningy0 && var3 <= (double)this.warningy1) {
            TitleScreen.this.minecraft.setScreen(new ConfirmLinkScreen((var1x) -> {
               if (var1x) {
                  Util.getPlatform().openUri(this.warningMessageUrl);
               }

               TitleScreen.this.minecraft.setScreen(TitleScreen.this);
            }, this.warningMessageUrl, true));
            return true;
         } else {
            return false;
         }
      }
   }
}
