package net.minecraft.client.gui.screens;

import java.util.List;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.multiplayer.ServerReconfigScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;

public class ReceivingLevelScreen extends Screen {
   private static final Component DOWNLOADING_TERRAIN_TEXT = Component.translatable("multiplayer.downloadingTerrain");
   private static final int ENTERING_MAP_TEXT_COMPONENTS = 49;
   private static final List<Component> ENTERING_MAP_TEXT;
   private static final long CHUNK_LOADING_START_WAIT_LIMIT_MS = 120000L;
   private final long createdAt;
   private BooleanSupplier levelReceived;
   private Reason reason;
   @Nullable
   private TextureAtlasSprite cachedNetherPortalSprite;
   private int mapTextCountdown;
   @Nullable
   private Component activeText;
   private final RandomSource random;
   @Nullable
   private final Connection connection;

   public ReceivingLevelScreen(BooleanSupplier var1, Reason var2, RandomSource var3, @Nullable Connection var4) {
      super(GameNarrator.NO_TITLE);
      this.levelReceived = var1;
      this.reason = var2;
      this.random = var3;
      this.connection = var4;
      this.createdAt = Util.getMillis();
   }

   public void updateScreen(BooleanSupplier var1, Reason var2) {
      this.levelReceived = var1;
      this.reason = var2;
   }

   public Reason getReason() {
      return this.reason;
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   protected boolean shouldNarrateNavigation() {
      return false;
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      switch (this.reason.ordinal()) {
         case 2:
         case 3:
            if (this.activeText != null) {
               var1.drawCenteredString(this.font, (Component)this.activeText, this.width / 2, this.height / 2 - 50, -1);
            }
            break;
         default:
            var1.drawCenteredString(this.font, (Component)DOWNLOADING_TERRAIN_TEXT, this.width / 2, this.height / 2 - 50, -1);
      }

   }

   public void renderBackground(GuiGraphics var1, int var2, int var3, float var4) {
      switch (this.reason.ordinal()) {
         case 0:
            var1.blitSprite(RenderType::guiOpaqueTexturedBackground, (TextureAtlasSprite)this.getNetherPortalSprite(), 0, 0, var1.guiWidth(), var1.guiHeight());
            break;
         case 1:
            var1.fillRenderType(RenderType.endPortal(), 0, 0, this.width, this.height, 0);
            break;
         case 2:
         case 3:
            this.renderPanorama(var1, var4);
            break;
         case 4:
            this.renderPanorama(var1, var4);
            this.renderBlurredBackground();
            this.renderMenuBackground(var1);
      }

   }

   private TextureAtlasSprite getNetherPortalSprite() {
      if (this.cachedNetherPortalSprite != null) {
         return this.cachedNetherPortalSprite;
      } else {
         this.cachedNetherPortalSprite = this.minecraft.getBlockRenderer().getBlockModelShaper().getParticleIcon(Blocks.NETHER_PORTAL.defaultBlockState());
         return this.cachedNetherPortalSprite;
      }
   }

   public void tick() {
      if (--this.mapTextCountdown < 0) {
         this.mapTextCountdown = 100;
         this.activeText = (Component)Util.getRandom(ENTERING_MAP_TEXT, this.random);
      }

      if (this.levelReceived.getAsBoolean() || Util.getMillis() > this.createdAt + 120000L) {
         this.onClose();
      }

      if (this.connection != null) {
         ServerReconfigScreen.tickTheConnectionPls(this.connection);
      }

   }

   public void onClose() {
      this.minecraft.getNarrator().sayNow((Component)Component.translatable("narrator.ready_to_play"));
      super.onClose();
   }

   public boolean isPauseScreen() {
      return false;
   }

   static {
      Component[] var0 = new Component[49];

      for(int var1 = 0; var1 < 49; ++var1) {
         var0[var1] = Component.translatable("multiplayer.enteringMap." + var1);
      }

      ENTERING_MAP_TEXT = List.of(var0);
   }

   public static enum Reason {
      NETHER_PORTAL,
      END_PORTAL,
      ENTERING_MAP,
      RECONFIGURING,
      OTHER;

      private Reason() {
      }

      // $FF: synthetic method
      private static Reason[] $values() {
         return new Reason[]{NETHER_PORTAL, END_PORTAL, ENTERING_MAP, RECONFIGURING, OTHER};
      }
   }
}
