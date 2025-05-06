package net.minecraft.client.gui.screens;

import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;

public class ReceivingLevelScreen extends Screen {
   private static final Component DOWNLOADING_TERRAIN_TEXT = Component.translatable("multiplayer.downloadingTerrain");
   private static final long CHUNK_LOADING_START_WAIT_LIMIT_MS = 30000L;
   private final long createdAt;
   private final BooleanSupplier levelReceived;
   private final Reason reason;
   @Nullable
   private TextureAtlasSprite cachedNetherPortalSprite;

   public ReceivingLevelScreen(BooleanSupplier var1, Reason var2) {
      super(GameNarrator.NO_TITLE);
      this.levelReceived = var1;
      this.reason = var2;
      this.createdAt = Util.getMillis();
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   protected boolean shouldNarrateNavigation() {
      return false;
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      var1.drawCenteredString(this.font, (Component)DOWNLOADING_TERRAIN_TEXT, this.width / 2, this.height / 2 - 50, -1);
   }

   public void renderBackground(GuiGraphics var1, int var2, int var3, float var4) {
      switch (this.reason.ordinal()) {
         case 0:
            var1.blitSprite(RenderPipelines.GUI_OPAQUE_TEXTURED_BACKGROUND, (TextureAtlasSprite)this.getNetherPortalSprite(), 0, 0, var1.guiWidth(), var1.guiHeight());
            break;
         case 1:
            TextureManager var5 = Minecraft.getInstance().getTextureManager();
            TextureSetup var6 = TextureSetup.doubleTexture(var5.getTexture(TheEndPortalRenderer.END_SKY_LOCATION).getTextureView(), var5.getTexture(TheEndPortalRenderer.END_PORTAL_LOCATION).getTextureView());
            var1.fill(RenderPipelines.END_PORTAL, var6, 0, 0, this.width, this.height);
            break;
         case 2:
            this.renderPanorama(var1, var4);
            this.renderBlurredBackground(var1);
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
      if (this.levelReceived.getAsBoolean() || Util.getMillis() > this.createdAt + 30000L) {
         this.onClose();
      }

   }

   public void onClose() {
      this.minecraft.getNarrator().saySystemNow((Component)Component.translatable("narrator.ready_to_play"));
      super.onClose();
   }

   public boolean isPauseScreen() {
      return false;
   }

   public static enum Reason {
      NETHER_PORTAL,
      END_PORTAL,
      OTHER;

      private Reason() {
      }

      // $FF: synthetic method
      private static Reason[] $values() {
         return new Reason[]{NETHER_PORTAL, END_PORTAL, OTHER};
      }
   }
}
