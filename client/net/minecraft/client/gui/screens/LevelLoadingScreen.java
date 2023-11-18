package net.minecraft.client.gui.screens;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.Util;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.progress.StoringChunkProgressListener;
import net.minecraft.util.Mth;
import net.minecraft.world.level.chunk.ChunkStatus;

public class LevelLoadingScreen extends Screen {
   private static final long NARRATION_DELAY_MS = 2000L;
   private final StoringChunkProgressListener progressListener;
   private long lastNarration = -1L;
   private boolean done;
   private static final Object2IntMap<ChunkStatus> COLORS = Util.make(new Object2IntOpenHashMap(), var0 -> {
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

   public LevelLoadingScreen(StoringChunkProgressListener var1) {
      super(GameNarrator.NO_TITLE);
      this.progressListener = var1;
   }

   @Override
   public boolean shouldCloseOnEsc() {
      return false;
   }

   @Override
   protected boolean shouldNarrateNavigation() {
      return false;
   }

   @Override
   public void removed() {
      this.done = true;
      this.triggerImmediateNarration(true);
   }

   @Override
   protected void updateNarratedWidget(NarrationElementOutput var1) {
      if (this.done) {
         var1.add(NarratedElementType.TITLE, Component.translatable("narrator.loading.done"));
      } else {
         String var2 = this.getFormattedProgress();
         var1.add(NarratedElementType.TITLE, var2);
      }
   }

   private String getFormattedProgress() {
      return Mth.clamp(this.progressListener.getProgress(), 0, 100) + "%";
   }

   @Override
   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      long var5 = Util.getMillis();
      if (var5 - this.lastNarration > 2000L) {
         this.lastNarration = var5;
         this.triggerImmediateNarration(true);
      }

      int var7 = this.width / 2;
      int var8 = this.height / 2;
      boolean var9 = true;
      renderChunks(var1, this.progressListener, var7, var8 + 30, 2, 0);
      var1.drawCenteredString(this.font, this.getFormattedProgress(), var7, var8 - 9 / 2 - 30, 16777215);
   }

   public static void renderChunks(GuiGraphics var0, StoringChunkProgressListener var1, int var2, int var3, int var4, int var5) {
      int var6 = var4 + var5;
      int var7 = var1.getFullDiameter();
      int var8 = var7 * var6 - var5;
      int var9 = var1.getDiameter();
      int var10 = var9 * var6 - var5;
      int var11 = var2 - var10 / 2;
      int var12 = var3 - var10 / 2;
      int var13 = var8 / 2 + 1;
      int var14 = -16772609;
      var0.drawManaged(() -> {
         if (var5 != 0) {
            var0.fill(var2 - var13, var3 - var13, var2 - var13 + 1, var3 + var13, -16772609);
            var0.fill(var2 + var13 - 1, var3 - var13, var2 + var13, var3 + var13, -16772609);
            var0.fill(var2 - var13, var3 - var13, var2 + var13, var3 - var13 + 1, -16772609);
            var0.fill(var2 - var13, var3 + var13 - 1, var2 + var13, var3 + var13, -16772609);
         }

         for(int var11x = 0; var11x < var9; ++var11x) {
            for(int var12x = 0; var12x < var9; ++var12x) {
               ChunkStatus var13x = var1.getStatus(var11x, var12x);
               int var14x = var11 + var11x * var6;
               int var15 = var12 + var12x * var6;
               var0.fill(var14x, var15, var14x + var4, var15 + var4, COLORS.getInt(var13x) | 0xFF000000);
            }
         }
      });
   }
}
