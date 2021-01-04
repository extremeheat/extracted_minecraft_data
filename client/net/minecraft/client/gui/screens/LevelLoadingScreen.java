package net.minecraft.client.gui.screens;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.progress.StoringChunkProgressListener;
import net.minecraft.util.Mth;
import net.minecraft.world.level.chunk.ChunkStatus;

public class LevelLoadingScreen extends Screen {
   private final StoringChunkProgressListener progressListener;
   private long lastNarration = -1L;
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
      NarratorChatListener.INSTANCE.sayNow(I18n.get("narrator.loading.done"));
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      String var4 = Mth.clamp(this.progressListener.getProgress(), 0, 100) + "%";
      long var5 = Util.getMillis();
      if (var5 - this.lastNarration > 2000L) {
         this.lastNarration = var5;
         NarratorChatListener.INSTANCE.sayNow((new TranslatableComponent("narrator.loading", new Object[]{var4})).getString());
      }

      int var7 = this.width / 2;
      int var8 = this.height / 2;
      boolean var9 = true;
      renderChunks(this.progressListener, var7, var8 + 30, 2, 0);
      Font var10001 = this.font;
      this.font.getClass();
      this.drawCenteredString(var10001, var4, var7, var8 - 9 / 2 - 30, 16777215);
   }

   public static void renderChunks(StoringChunkProgressListener var0, int var1, int var2, int var3, int var4) {
      int var5 = var3 + var4;
      int var6 = var0.getFullDiameter();
      int var7 = var6 * var5 - var4;
      int var8 = var0.getDiameter();
      int var9 = var8 * var5 - var4;
      int var10 = var1 - var9 / 2;
      int var11 = var2 - var9 / 2;
      int var12 = var7 / 2 + 1;
      int var13 = -16772609;
      if (var4 != 0) {
         fill(var1 - var12, var2 - var12, var1 - var12 + 1, var2 + var12, -16772609);
         fill(var1 + var12 - 1, var2 - var12, var1 + var12, var2 + var12, -16772609);
         fill(var1 - var12, var2 - var12, var1 + var12, var2 - var12 + 1, -16772609);
         fill(var1 - var12, var2 + var12 - 1, var1 + var12, var2 + var12, -16772609);
      }

      for(int var14 = 0; var14 < var8; ++var14) {
         for(int var15 = 0; var15 < var8; ++var15) {
            ChunkStatus var16 = var0.getStatus(var14, var15);
            int var17 = var10 + var14 * var5;
            int var18 = var11 + var15 * var5;
            fill(var17, var18, var17 + var3, var18 + var3, COLORS.getInt(var16) | -16777216);
         }
      }

   }
}
