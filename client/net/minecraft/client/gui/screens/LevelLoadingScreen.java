package net.minecraft.client.gui.screens;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.multiplayer.LevelLoadTracker;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.blockentity.AbstractEndPortalRenderer;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.progress.ChunkLoadStatusView;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.status.ChunkStatus;

public class LevelLoadingScreen extends Screen {
   private static final Component DOWNLOADING_TERRAIN_TEXT = Component.translatable("multiplayer.downloadingTerrain");
   private static final Component READY_TO_PLAY_TEXT = Component.translatable("narrator.ready_to_play");
   private static final long NARRATION_DELAY_MS = 2000L;
   private static final int PROGRESS_BAR_WIDTH = 200;
   private LevelLoadTracker loadTracker;
   private float smoothedProgress;
   private long lastNarration = -1L;
   private Reason reason;
   @Nullable
   private TextureAtlasSprite cachedNetherPortalSprite;
   private static final Object2IntMap<ChunkStatus> COLORS = (Object2IntMap)Util.make(new Object2IntOpenHashMap(), (var0) -> {
      var0.defaultReturnValue(0);
      var0.put(ChunkStatus.EMPTY, 5526612);
      var0.put(ChunkStatus.STRUCTURE_STARTS, 10066329);
      var0.put(ChunkStatus.STRUCTURE_REFERENCES, 6250897);
      var0.put(ChunkStatus.BIOMES, 8434258);
      var0.put(ChunkStatus.NOISE, 13750737);
      var0.put(ChunkStatus.SURFACE, 7497737);
      var0.put(ChunkStatus.CARVERS, 3159410);
      var0.put(ChunkStatus.FEATURES, 2213376);
      var0.put(ChunkStatus.INITIALIZE_LIGHT, 13421772);
      var0.put(ChunkStatus.LIGHT, 16769184);
      var0.put(ChunkStatus.SPAWN, 15884384);
      var0.put(ChunkStatus.FULL, 16777215);
   });

   public LevelLoadingScreen(LevelLoadTracker var1, Reason var2) {
      super(GameNarrator.NO_TITLE);
      this.loadTracker = var1;
      this.reason = var2;
   }

   public void update(LevelLoadTracker var1, Reason var2) {
      this.loadTracker = var1;
      this.reason = var2;
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   protected boolean shouldNarrateNavigation() {
      return false;
   }

   protected void updateNarratedWidget(NarrationElementOutput var1) {
      if (this.loadTracker.hasProgress()) {
         var1.add(NarratedElementType.TITLE, (Component)Component.translatable("loading.progress", Mth.floor(this.loadTracker.serverProgress() * 100.0F)));
      }

   }

   public void tick() {
      super.tick();
      this.smoothedProgress += (this.loadTracker.serverProgress() - this.smoothedProgress) * 0.2F;
      if (this.loadTracker.isLevelReady()) {
         this.onClose();
      }

   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      long var5 = Util.getMillis();
      if (var5 - this.lastNarration > 2000L) {
         this.lastNarration = var5;
         this.triggerImmediateNarration(true);
      }

      int var7 = this.width / 2;
      int var8 = this.height / 2;
      ChunkLoadStatusView var9 = this.loadTracker.statusView();
      int var10;
      if (var9 != null) {
         boolean var11 = true;
         renderChunks(var1, var7, var8, 2, 0, var9);
         int var10000 = var8 - var9.radius() * 2;
         Objects.requireNonNull(this.font);
         var10 = var10000 - 9 * 3;
      } else {
         var10 = var8 - 50;
      }

      var1.drawCenteredString(this.font, (Component)DOWNLOADING_TERRAIN_TEXT, var7, var10, -1);
      if (this.loadTracker.hasProgress()) {
         int var10002 = var7 - 100;
         Objects.requireNonNull(this.font);
         this.drawProgressBar(var1, var10002, var10 + 9 + 3, 200, 2, this.smoothedProgress);
      }

   }

   private void drawProgressBar(GuiGraphics var1, int var2, int var3, int var4, int var5, float var6) {
      var1.fill(var2, var3, var2 + var4, var3 + var5, -16777216);
      var1.fill(var2, var3, var2 + Math.round(var6 * (float)var4), var3 + var5, -16711936);
   }

   public static void renderChunks(GuiGraphics var0, int var1, int var2, int var3, int var4, ChunkLoadStatusView var5) {
      int var6 = var3 + var4;
      int var7 = var5.radius() * 2 + 1;
      int var8 = var7 * var6 - var4;
      int var9 = var1 - var8 / 2;
      int var10 = var2 - var8 / 2;
      if (Minecraft.getInstance().debugEntries.isCurrentlyEnabled(DebugScreenEntries.VISUALIZE_CHUNKS_ON_SERVER)) {
         int var11 = var6 / 2 + 1;
         var0.fill(var1 - var11, var2 - var11, var1 + var11, var2 + var11, -65536);
      }

      for(int var16 = 0; var16 < var7; ++var16) {
         for(int var12 = 0; var12 < var7; ++var12) {
            ChunkStatus var13 = var5.get(var16, var12);
            int var14 = var9 + var16 * var6;
            int var15 = var10 + var12 * var6;
            var0.fill(var14, var15, var14 + var3, var15 + var3, ARGB.opaque(COLORS.getInt(var13)));
         }
      }

   }

   public void renderBackground(GuiGraphics var1, int var2, int var3, float var4) {
      switch (this.reason.ordinal()) {
         case 0:
            var1.blitSprite(RenderPipelines.GUI_OPAQUE_TEXTURED_BACKGROUND, (TextureAtlasSprite)this.getNetherPortalSprite(), 0, 0, var1.guiWidth(), var1.guiHeight());
            break;
         case 1:
            TextureManager var5 = Minecraft.getInstance().getTextureManager();
            AbstractTexture var6 = var5.getTexture(AbstractEndPortalRenderer.END_SKY_LOCATION);
            AbstractTexture var7 = var5.getTexture(AbstractEndPortalRenderer.END_PORTAL_LOCATION);
            TextureSetup var8 = TextureSetup.doubleTexture(var6.getTextureView(), var6.getSampler(), var7.getTextureView(), var7.getSampler());
            var1.fill(RenderPipelines.END_PORTAL, var8, 0, 0, this.width, this.height);
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

   public void onClose() {
      this.minecraft.getNarrator().saySystemNow(READY_TO_PLAY_TEXT);
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
