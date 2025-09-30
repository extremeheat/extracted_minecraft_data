package net.minecraft.client.multiplayer;

import com.mojang.logging.LogUtils;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.progress.ChunkLoadStatusView;
import net.minecraft.server.level.progress.LevelLoadListener;
import net.minecraft.server.level.progress.LevelLoadProgressTracker;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

public class LevelLoadTracker implements LevelLoadListener {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final long CLIENT_WAIT_TIMEOUT_MS;
   public static final long LEVEL_LOAD_CLOSE_DELAY_MS = 500L;
   private final LevelLoadProgressTracker serverProgressTracker;
   @Nullable
   private ChunkLoadStatusView serverChunkStatusView;
   @Nullable
   private volatile LevelLoadListener.Stage serverStage;
   @Nullable
   private ClientState clientState;
   private final long closeDelayMs;

   public LevelLoadTracker() {
      this(0L);
   }

   public LevelLoadTracker(long var1) {
      super();
      this.serverProgressTracker = new LevelLoadProgressTracker(true);
      this.closeDelayMs = var1;
   }

   public void setServerChunkStatusView(ChunkLoadStatusView var1) {
      this.serverChunkStatusView = var1;
   }

   public void startClientLoad(LocalPlayer var1, ClientLevel var2, LevelRenderer var3) {
      this.clientState = new WaitingForServer(var1, var2, var3, Util.getMillis() + CLIENT_WAIT_TIMEOUT_MS);
   }

   public void tickClientLoad() {
      if (this.clientState != null) {
         this.clientState = this.clientState.tick();
      }

   }

   public boolean isLevelReady() {
      ClientState var4 = this.clientState;
      boolean var9;
      if (var4 instanceof ClientLevelReady var3) {
         ClientLevelReady var10000 = var3;

         try {
            var8 = var10000.readyAt();
         } catch (Throwable var7) {
            throw new MatchException(var7.toString(), var7);
         }

         long var5 = var8;
         if (Util.getMillis() >= var5 + this.closeDelayMs) {
            var9 = true;
            return var9;
         }
      }

      var9 = false;
      return var9;
   }

   public void loadingPacketsReceived() {
      if (this.clientState != null) {
         this.clientState = this.clientState.loadingPacketsReceived();
      }

   }

   public void start(LevelLoadListener.Stage var1, int var2) {
      this.serverProgressTracker.start(var1, var2);
      this.serverStage = var1;
   }

   public void update(LevelLoadListener.Stage var1, int var2, int var3) {
      this.serverProgressTracker.update(var1, var2, var3);
   }

   public void finish(LevelLoadListener.Stage var1) {
      this.serverProgressTracker.finish(var1);
   }

   public void updateFocus(ResourceKey<Level> var1, ChunkPos var2) {
      if (this.serverChunkStatusView != null) {
         this.serverChunkStatusView.moveTo(var1, var2);
      }

   }

   @Nullable
   public ChunkLoadStatusView statusView() {
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

   sealed interface ClientState permits LevelLoadTracker.WaitingForServer, LevelLoadTracker.WaitingForPlayerChunk, LevelLoadTracker.ClientLevelReady {
      default ClientState tick() {
         return this;
      }

      default ClientState loadingPacketsReceived() {
         return this;
      }
   }

   static record WaitingForServer(LocalPlayer player, ClientLevel level, LevelRenderer levelRenderer, long timeoutAfter) implements ClientState {
      WaitingForServer(LocalPlayer var1, ClientLevel var2, LevelRenderer var3, long var4) {
         super();
         this.player = var1;
         this.level = var2;
         this.levelRenderer = var3;
         this.timeoutAfter = var4;
      }

      public ClientState loadingPacketsReceived() {
         return new WaitingForPlayerChunk(this.player, this.level, this.levelRenderer, this.timeoutAfter);
      }
   }

   static record WaitingForPlayerChunk(LocalPlayer player, ClientLevel level, LevelRenderer levelRenderer, long timeoutAfter) implements ClientState {
      WaitingForPlayerChunk(LocalPlayer var1, ClientLevel var2, LevelRenderer var3, long var4) {
         super();
         this.player = var1;
         this.level = var2;
         this.levelRenderer = var3;
         this.timeoutAfter = var4;
      }

      public ClientState tick() {
         return (ClientState)(this.isReady() ? new ClientLevelReady(Util.getMillis()) : this);
      }

      private boolean isReady() {
         if (Util.getMillis() > this.timeoutAfter) {
            LevelLoadTracker.LOGGER.warn("Timed out while waiting for the client to load chunks, letting the player into the world anyway");
            return true;
         } else {
            BlockPos var1 = this.player.blockPosition();
            return !this.level.isOutsideBuildHeight(var1.getY()) && !this.player.isSpectator() && this.player.isAlive() ? this.levelRenderer.isSectionCompiled(var1) : true;
         }
      }
   }

   static record ClientLevelReady(long readyAt) implements ClientState {
      ClientLevelReady(long var1) {
         super();
         this.readyAt = var1;
      }
   }
}
