package net.minecraft.client.multiplayer;

import com.mojang.logging.LogUtils;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.progress.ChunkLoadStatusView;
import net.minecraft.server.level.progress.LevelLoadListener;
import net.minecraft.server.level.progress.LevelLoadProgressTracker;
import net.minecraft.util.Util;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class LevelLoadTracker implements LevelLoadListener {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final long CLIENT_WAIT_TIMEOUT_MS;
   public static final long LEVEL_LOAD_CLOSE_DELAY_MS = 500L;
   private final LevelLoadProgressTracker serverProgressTracker;
   private @Nullable ChunkLoadStatusView serverChunkStatusView;
   private volatile LevelLoadListener.@Nullable Stage serverStage;
   private @Nullable ClientState clientState;
   private final long closeDelayMs;

   public LevelLoadTracker() {
      this(0L);
   }

   public LevelLoadTracker(final long closeDelayMs) {
      super();
      this.serverProgressTracker = new LevelLoadProgressTracker(true);
      this.closeDelayMs = closeDelayMs;
   }

   public void setServerChunkStatusView(final ChunkLoadStatusView serverChunkStatusView) {
      this.serverChunkStatusView = serverChunkStatusView;
   }

   public void startClientLoad(final LocalPlayer player, final ClientLevel level, final LevelRenderer levelRenderer) {
      this.clientState = new WaitingForServer(player, level, levelRenderer, Util.getMillis() + CLIENT_WAIT_TIMEOUT_MS);
   }

   public void tickClientLoad() {
      if (this.clientState != null) {
         this.clientState = this.clientState.tick();
      }

   }

   public boolean isLevelReady() {
      ClientState var4 = this.clientState;
      boolean var11;
      if (var4 instanceof ClientLevelReady var3) {
         ClientLevelReady var10000 = var3;

         try {
            var10 = var10000.readyAt();
         } catch (Throwable var9) {
            throw new MatchException(var9.toString(), var9);
         }

         long readyAt = var10;
         if (true && Util.getMillis() >= readyAt + this.closeDelayMs) {
            var11 = true;
            return var11;
         }
      }

      var11 = false;
      return var11;
   }

   public void loadingPacketsReceived() {
      if (this.clientState != null) {
         this.clientState = this.clientState.loadingPacketsReceived();
      }

   }

   public void start(final LevelLoadListener.Stage stage, final int totalChunks) {
      this.serverProgressTracker.start(stage, totalChunks);
      this.serverStage = stage;
   }

   public void update(final LevelLoadListener.Stage stage, final int currentChunks, final int totalChunks) {
      this.serverProgressTracker.update(stage, currentChunks, totalChunks);
   }

   public void finish(final LevelLoadListener.Stage stage) {
      this.serverProgressTracker.finish(stage);
   }

   public void updateFocus(final ResourceKey<Level> dimension, final ChunkPos chunkPos) {
      if (this.serverChunkStatusView != null) {
         this.serverChunkStatusView.moveTo(dimension, chunkPos);
      }

   }

   public @Nullable ChunkLoadStatusView statusView() {
      return this.serverChunkStatusView;
   }

   public float serverProgress() {
      return this.serverProgressTracker.get();
   }

   public boolean hasProgress() {
      return this.serverStage != null;
   }

   static {
      CLIENT_WAIT_TIMEOUT_MS = TimeUnit.SECONDS.toMillis(30L);
   }

   private sealed interface ClientState permits LevelLoadTracker.ClientLevelReady, LevelLoadTracker.WaitingForPlayerChunk, LevelLoadTracker.WaitingForServer {
      default ClientState tick() {
         return this;
      }

      default ClientState loadingPacketsReceived() {
         return this;
      }
   }

   private static record WaitingForServer(LocalPlayer player, ClientLevel level, LevelRenderer levelRenderer, long timeoutAfter) implements ClientState {
      private WaitingForServer {
         super();
      }

      public ClientState loadingPacketsReceived() {
         return new WaitingForPlayerChunk(this.player, this.level, this.levelRenderer, this.timeoutAfter);
      }
   }

   private static record WaitingForPlayerChunk(LocalPlayer player, ClientLevel level, LevelRenderer levelRenderer, long timeoutAfter) implements ClientState {
      private WaitingForPlayerChunk {
         super();
      }

      public ClientState tick() {
         return (ClientState)(this.isReady() ? new ClientLevelReady(Util.getMillis()) : this);
      }

      private boolean isReady() {
         if (Util.getMillis() > this.timeoutAfter) {
            LevelLoadTracker.LOGGER.warn("Timed out while waiting for the client to load chunks, letting the player into the world anyway");
            return true;
         } else {
            BlockPos playerPos = this.player.blockPosition();
            return !this.level.isOutsideBuildHeight(playerPos.getY()) && !this.player.isSpectator() && this.player.isAlive() ? this.levelRenderer.isSectionCompiledAndVisible(playerPos) : true;
         }
      }
   }

   private static record ClientLevelReady(long readyAt) implements ClientState {
      private ClientLevelReady {
         super();
      }
   }
}
