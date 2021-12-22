package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Objects;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.progress.StoringChunkProgressListener;
import net.minecraft.util.Mth;
import net.minecraft.world.level.chunk.ChunkStatus;

public class LevelLoadingScreen extends Screen {
   private static final long NARRATION_DELAY_MS = 2000L;
   private final StoringChunkProgressListener progressListener;
   private long lastNarration = -1L;
   private boolean done;
   private static final Object2IntMap<ChunkStatus> COLORS = (Object2IntMap)Util.make(new Object2IntOpenHashMap(), (var0) -> {
      var0.defaultReturnValue(0);
      var0.put(ChunkStatus.EMPTY, 5526612);
      var0.put(ChunkStatus.STRUCTURE_STARTS, 10066329);
      var0.put(ChunkStatus.STRUCTURE_REFERENCES, 6250897);
      var0.put(ChunkStatus.BIOMES, 8434258);
      var0.put(ChunkStatus.NOISE, 13750737);
      var0.put(ChunkStatus.SURFACE, 7497737);
      var0.put(ChunkStatus.CARVERS, 7169628);
      var0.put(ChunkStatus.LIQUID_CARVERS, 3159410);
      var0.put(ChunkStatus.FEATURES, 2213376);
      var0.put(ChunkStatus.LIGHT, 13421772);
      var0.put(ChunkStatus.SPAWN, 15884384);
      var0.put(ChunkStatus.HEIGHTMAPS, 15658734);
      var0.put(ChunkStatus.FULL, 16777215);
   });

   public LevelLoadingScreen(StoringChunkProgressListener var1) {
      super(NarratorChatListener.NO_TITLE);
      this.progressListener = var1;
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public void removed() {
      this.done = true;
      this.triggerImmediateNarration(true);
   }

   protected void updateNarratedWidget(NarrationElementOutput var1) {
      if (this.done) {
         var1.add(NarratedElementType.TITLE, (Component)(new TranslatableComponent("narrator.loading.done")));
      } else {
         String var2 = this.getFormattedProgress();
         var1.add(NarratedElementType.TITLE, var2);
      }

   }

   private String getFormattedProgress() {
      int var10000 = this.progressListener.getProgress();
      return Mth.clamp((int)var10000, (int)0, (int)100) + "%";
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
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
      Font var10001 = this.font;
      String var10002 = this.getFormattedProgress();
      Objects.requireNonNull(this.font);
      drawCenteredString(var1, var10001, var10002, var7, var8 - 9 / 2 - 30, 16777215);
   }

   public static void renderChunks(PoseStack var0, StoringChunkProgressListener var1, int var2, int var3, int var4, int var5) {
      int var6 = var4 + var5;
      int var7 = var1.getFullDiameter();
      int var8 = var7 * var6 - var5;
      int var9 = var1.getDiameter();
      int var10 = var9 * var6 - var5;
      int var11 = var2 - var10 / 2;
      int var12 = var3 - var10 / 2;
      int var13 = var8 / 2 + 1;
      int var14 = -16772609;
      if (var5 != 0) {
         fill(var0, var2 - var13, var3 - var13, var2 - var13 + 1, var3 + var13, -16772609);
         fill(var0, var2 + var13 - 1, var3 - var13, var2 + var13, var3 + var13, -16772609);
         fill(var0, var2 - var13, var3 - var13, var2 + var13, var3 - var13 + 1, -16772609);
         fill(var0, var2 - var13, var3 + var13 - 1, var2 + var13, var3 + var13, -16772609);
      }

      for(int var15 = 0; var15 < var9; ++var15) {
         for(int var16 = 0; var16 < var9; ++var16) {
            ChunkStatus var17 = var1.getStatus(var15, var16);
            int var18 = var11 + var15 * var6;
            int var19 = var12 + var16 * var6;
            fill(var0, var18, var19, var18 + var4, var19 + var4, COLORS.getInt(var17) | -16777216);
         }
      }

   }
}
