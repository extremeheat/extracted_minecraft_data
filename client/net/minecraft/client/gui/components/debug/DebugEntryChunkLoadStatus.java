package net.minecraft.client.gui.components.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.server.level.ChunkLevel;
import net.minecraft.server.level.progress.ChunkLoadStatusView;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

public class DebugEntryChunkLoadStatus implements DebugScreenEntry {
   private static final int CHUNK_SECTION_SIZE_PX = 3;
   private static final int CHUNK_SECTION_MARGIN_PX = 2;

   public DebugEntryChunkLoadStatus() {
      super();
   }

   public void display(final DebugScreenDisplayer displayer, final @Nullable Level serverOrClientLevel, final @Nullable LevelChunk clientChunk, final @Nullable LevelChunk serverChunk) {
      Minecraft minecraft = Minecraft.getInstance();
      IntegratedServer singleplayerServer = minecraft.getSingleplayerServer();
      if (singleplayerServer != null && minecraft.player != null) {
         ChunkLoadStatusView statusView = singleplayerServer.createChunkLoadStatusView(16 + ChunkLevel.RADIUS_AROUND_FULL_CHUNK);
         statusView.moveTo(minecraft.player.level().dimension(), minecraft.player.chunkPosition());
         displayer.addToGroup(DebugGroups.CHUNK_GENERATION, new Renderer(statusView));
      }

   }

   private static record Renderer(ChunkLoadStatusView statusView) implements DebugCustomRenderer {
      private Renderer {
         super();
      }

      public void extract(final GuiGraphicsExtractor graphics, final int left, final int top, final DebugColumn.Side side) {
         int size = this.height();
         LevelLoadingScreen.extractChunksForRendering(graphics, left + size / 2, top + size / 2, 3, 2, this.statusView, true);
      }

      public int height() {
         int diameter = this.statusView.radius() * 2 + 1;
         int width = 5;
         return diameter * 5 - 1;
      }

      public int width(final int groupWidth) {
         return this.height();
      }
   }
}
