package net.minecraft.client.gui.screens;

import com.google.common.util.concurrent.Runnables;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.io.IOException;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.SafetyScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TitleScreen extends Screen {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final CubeMap CUBE_MAP = new CubeMap(new ResourceLocation("textures/gui/title/background/panorama"));
   private static final ResourceLocation PANORAMA_OVERLAY = new ResourceLocation("textures/gui/title/background/panorama_overlay.png");
   private static final ResourceLocation ACCESSIBILITY_TEXTURE = new ResourceLocation("textures/gui/accessibility.png");
   private final boolean minceraftEasterEgg;
   @Nullable
   private String splash;
   private Button resetDemoButton;
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
      super(new TranslatableComponent("narrator.screen.title"));
      this.panorama = new PanoramaRenderer(CUBE_MAP);
      this.fading = var1;
      this.minceraftEasterEgg = (double)(new Random()).nextFloat() < 1.0E-4D;
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
      }, new TranslatableComponent("narrator.button.language")));
      this.addButton(new Button(this.width / 2 - 100, var2 + 72 + 12, 98, 20, new TranslatableComponent("menu.options"), (var1x) -> {
         this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options));
      }));
      this.addButton(new Button(this.width / 2 + 2, var2 + 72 + 12, 98, 20, new TranslatableComponent("menu.quit"), (var1x) -> {
         this.minecraft.stop();
      }));
      this.addButton(new ImageButton(this.width / 2 + 104, var2 + 72 + 12, 20, 20, 0, 0, 20, ACCESSIBILITY_TEXTURE, 32, 64, (var1x) -> {
         this.minecraft.setScreen(new AccessibilityOptionsScreen(this, this.minecraft.options));
      }, new TranslatableComponent("narrator.button.accessibility")));
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
      this.addButton(new Button(this.width / 2 - 100, var1, 200, 20, new TranslatableComponent("menu.singleplayer"), (var1x) -> {
         this.minecraft.setScreen(new SelectWorldScreen(this));
      }));
      boolean var3 = this.minecraft.allowsMultiplayer();
      Button.OnTooltip var4 = var3 ? Button.NO_TOOLTIP : (var1x, var2x, var3x, var4x) -> {
         if (!var1x.active) {
            this.renderTooltip(var2x, this.minecraft.font.split(new TranslatableComponent("title.multiplayer.disabled"), Math.max(this.width / 2 - 43, 170)), var3x, var4x);
         }

      };
      ((Button)this.addButton(new Button(this.width / 2 - 100, var1 + var2 * 1, 200, 20, new TranslatableComponent("menu.multiplayer"), (var1x) -> {
         Object var2 = this.minecraft.options.skipMultiplayerWarning ? new JoinMultiplayerScreen(this) : new SafetyScreen(this);
         this.minecraft.setScreen((Screen)var2);
      }, var4))).active = var3;
      ((Button)this.addButton(new Button(this.width / 2 - 100, var1 + var2 * 2, 200, 20, new TranslatableComponent("menu.online"), (var1x) -> {
         this.realmsButtonClicked();
      }, var4))).active = var3;
   }

   private void createDemoMenuOptions(int var1, int var2) {
      boolean var3 = this.checkDemoWorldPresence();
      this.addButton(new Button(this.width / 2 - 100, var1, 200, 20, new TranslatableComponent("menu.playdemo"), (var2x) -> {
         if (var3) {
            this.minecraft.loadLevel("Demo_World");
         } else {
            RegistryAccess.RegistryHolder var3x = RegistryAccess.builtin();
            this.minecraft.createLevel("Demo_World", MinecraftServer.DEMO_SETTINGS, var3x, WorldGenSettings.demoSettings(var3x));
         }

      }));
      this.resetDemoButton = (Button)this.addButton(new Button(this.width / 2 - 100, var1 + var2 * 1, 200, 20, new TranslatableComponent("menu.resetdemo"), (var1x) -> {
         LevelStorageSource var2 = this.minecraft.getLevelSource();

         try {
            LevelStorageSource.LevelStorageAccess var3 = var2.createAccess("Demo_World");
            Throwable var4 = null;

            try {
               LevelSummary var5 = var3.getSummary();
               if (var5 != null) {
                  this.minecraft.setScreen(new ConfirmScreen(this::confirmDemo, new TranslatableComponent("selectWorld.deleteQuestion"), new TranslatableComponent("selectWorld.deleteWarning", new Object[]{var5.getLevelName()}), new TranslatableComponent("selectWorld.deleteButton"), CommonComponents.GUI_CANCEL));
               }
            } catch (Throwable var14) {
               var4 = var14;
               throw var14;
            } finally {
               if (var3 != null) {
                  if (var4 != null) {
                     try {
                        var3.close();
                     } catch (Throwable var13) {
                        var4.addSuppressed(var13);
                     }
                  } else {
                     var3.close();
                  }
               }

            }
         } catch (IOException var16) {
            SystemToast.onWorldAccessFailure(this.minecraft, "Demo_World");
            LOGGER.warn("Failed to access demo world", var16);
         }

      }));
      this.resetDemoButton.active = var3;
   }

   private boolean checkDemoWorldPresence() {
      try {
         LevelStorageSource.LevelStorageAccess var1 = this.minecraft.getLevelSource().createAccess("Demo_World");
         Throwable var2 = null;

         boolean var3;
         try {
            var3 = var1.getSummary() != null;
         } catch (Throwable var13) {
            var2 = var13;
            throw var13;
         } finally {
            if (var1 != null) {
               if (var2 != null) {
                  try {
                     var1.close();
                  } catch (Throwable var12) {
                     var2.addSuppressed(var12);
                  }
               } else {
                  var1.close();
               }
            }

         }

         return var3;
      } catch (IOException var15) {
         SystemToast.onWorldAccessFailure(this.minecraft, "Demo_World");
         LOGGER.warn("Failed to read demo world data", var15);
         return false;
      }
   }

   private void realmsButtonClicked() {
      RealmsBridge var1 = new RealmsBridge();
      var1.switchToRealms(this);
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      if (this.fadeInStart == 0L && this.fading) {
         this.fadeInStart = Util.getMillis();
      }

      float var5 = this.fading ? (float)(Util.getMillis() - this.fadeInStart) / 1000.0F : 1.0F;
      fill(var1, 0, 0, this.width, this.height, -1);
      this.panorama.render(var4, Mth.clamp(var5, 0.0F, 1.0F));
      boolean var6 = true;
      int var7 = this.width / 2 - 137;
      boolean var8 = true;
      this.minecraft.getTextureManager().bind(PANORAMA_OVERLAY);
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.fading ? (float)Mth.ceil(Mth.clamp(var5, 0.0F, 1.0F)) : 1.0F);
      blit(var1, 0, 0, this.width, this.height, 0.0F, 0.0F, 16, 128, 16, 128);
      float var9 = this.fading ? Mth.clamp(var5 - 1.0F, 0.0F, 1.0F) : 1.0F;
      int var10 = Mth.ceil(var9 * 255.0F) << 24;
      if ((var10 & -67108864) != 0) {
         this.minecraft.getTextureManager().bind(MINECRAFT_LOGO);
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, var9);
         if (this.minceraftEasterEgg) {
            this.blitOutlineBlack(var7, 30, (var2x, var3x) -> {
               this.blit(var1, var2x + 0, var3x, 0, 0, 99, 44);
               this.blit(var1, var2x + 99, var3x, 129, 0, 27, 44);
               this.blit(var1, var2x + 99 + 26, var3x, 126, 0, 3, 44);
               this.blit(var1, var2x + 99 + 26 + 3, var3x, 99, 0, 26, 44);
               this.blit(var1, var2x + 155, var3x, 0, 45, 155, 44);
            });
         } else {
            this.blitOutlineBlack(var7, 30, (var2x, var3x) -> {
               this.blit(var1, var2x + 0, var3x, 0, 0, 155, 44);
               this.blit(var1, var2x + 155, var3x, 0, 45, 155, 44);
            });
         }

         this.minecraft.getTextureManager().bind(MINECRAFT_EDITION);
         blit(var1, var7 + 88, 67, 0.0F, 0.0F, 98, 14, 128, 16);
         if (this.splash != null) {
            RenderSystem.pushMatrix();
            RenderSystem.translatef((float)(this.width / 2 + 90), 70.0F, 0.0F);
            RenderSystem.rotatef(-20.0F, 0.0F, 0.0F, 1.0F);
            float var11 = 1.8F - Mth.abs(Mth.sin((float)(Util.getMillis() % 1000L) / 1000.0F * 6.2831855F) * 0.1F);
            var11 = var11 * 100.0F / (float)(this.font.width(this.splash) + 32);
            RenderSystem.scalef(var11, var11, var11);
            drawCenteredString(var1, this.font, this.splash, 0, -8, 16776960 | var10);
            RenderSystem.popMatrix();
         }

         String var14 = "Minecraft " + SharedConstants.getCurrentVersion().getName();
         if (this.minecraft.isDemo()) {
            var14 = var14 + " Demo";
         } else {
            var14 = var14 + ("release".equalsIgnoreCase(this.minecraft.getVersionType()) ? "" : "/" + this.minecraft.getVersionType());
         }

         if (this.minecraft.isProbablyModded()) {
            var14 = var14 + I18n.get("menu.modded");
         }

         drawString(var1, this.font, var14, 2, this.height - 10, 16777215 | var10);
         drawString(var1, this.font, "Copyright Mojang AB. Do not distribute!", this.copyrightX, this.height - 10, 16777215 | var10);
         if (var2 > this.copyrightX && var2 < this.copyrightX + this.copyrightWidth && var3 > this.height - 10 && var3 < this.height) {
            fill(var1, this.copyrightX, this.height - 1, this.copyrightX + this.copyrightWidth, this.height, 16777215 | var10);
         }

         Iterator var12 = this.buttons.iterator();

         while(var12.hasNext()) {
            AbstractWidget var13 = (AbstractWidget)var12.next();
            var13.setAlpha(var9);
         }

         super.render(var1, var2, var3, var4);
         if (this.realmsNotificationsEnabled() && var9 >= 1.0F) {
            this.realmsNotificationsScreen.render(var1, var2, var3, var4);
         }

      }
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (super.mouseClicked(var1, var3, var5)) {
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
         try {
            LevelStorageSource.LevelStorageAccess var2 = this.minecraft.getLevelSource().createAccess("Demo_World");
            Throwable var3 = null;

            try {
               var2.deleteLevel();
            } catch (Throwable var13) {
               var3 = var13;
               throw var13;
            } finally {
               if (var2 != null) {
                  if (var3 != null) {
                     try {
                        var2.close();
                     } catch (Throwable var12) {
                        var3.addSuppressed(var12);
                     }
                  } else {
                     var2.close();
                  }
               }

            }
         } catch (IOException var15) {
            SystemToast.onWorldDeleteFailure(this.minecraft, "Demo_World");
            LOGGER.warn("Failed to delete demo world", var15);
         }
      }

      this.minecraft.setScreen(this);
   }
}
