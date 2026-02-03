package net.minecraft.server.network.config;

import com.mojang.logging.LogUtils;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ChunkLoadCounter;
import net.minecraft.server.level.PlayerSpawnFinder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.server.level.progress.LevelLoadListener;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.players.NameAndId;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class PrepareSpawnTask implements ConfigurationTask {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final ConfigurationTask.Type TYPE = new ConfigurationTask.Type("prepare_spawn");
   public static final int PREPARE_CHUNK_RADIUS = 3;
   private final MinecraftServer server;
   private final NameAndId nameAndId;
   private final LevelLoadListener loadListener;
   private @Nullable State state;

   public PrepareSpawnTask(final MinecraftServer server, final NameAndId nameAndId) {
      super();
      this.server = server;
      this.nameAndId = nameAndId;
      this.loadListener = server.getLevelLoadListener();
   }

   public void start(final Consumer<Packet<?>> connection) {
      try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(LOGGER)) {
         Optional<ValueInput> loadedData = this.server.getPlayerList().loadPlayerData(this.nameAndId).map((tag) -> TagValueInput.create(reporter, this.server.registryAccess(), tag));
         ServerPlayer.SavedPosition loadedPosition = (ServerPlayer.SavedPosition)loadedData.flatMap((tag) -> tag.read(ServerPlayer.SavedPosition.MAP_CODEC)).orElse(ServerPlayer.SavedPosition.EMPTY);
         LevelData.RespawnData respawnData = this.server.getWorldData().overworldData().getRespawnData();
         Optional var10000 = loadedPosition.dimension();
         MinecraftServer var10001 = this.server;
         Objects.requireNonNull(var10001);
         ServerLevel spawnLevel = (ServerLevel)var10000.map(var10001::getLevel).orElseGet(() -> {
            ServerLevel spawnDataLevel = this.server.getLevel(respawnData.dimension());
            return spawnDataLevel != null ? spawnDataLevel : this.server.overworld();
         });
         CompletableFuture<Vec3> spawnPosition = (CompletableFuture)loadedPosition.position().map(CompletableFuture::completedFuture).orElseGet(() -> PlayerSpawnFinder.findSpawn(spawnLevel, respawnData.pos()));
         Vec2 spawnAngle = (Vec2)loadedPosition.rotation().orElse(new Vec2(respawnData.yaw(), respawnData.pitch()));
         this.state = new Preparing(spawnLevel, spawnPosition, spawnAngle);
      }

   }

   public boolean tick() {
      State var1 = this.state;
      byte var2 = 0;
      boolean var10000;
      //$FF: var2->value
      //0->net/minecraft/server/network/config/PrepareSpawnTask$Preparing
      //1->net/minecraft/server/network/config/PrepareSpawnTask$Ready
      switch (var1.typeSwitch<invokedynamic>(var1, var2)) {
         case -1:
            var10000 = false;
            break;
         case 0:
            Preparing preparing = (Preparing)var1;
            Ready ready = preparing.tick();
            if (ready != null) {
               this.state = ready;
               var10000 = true;
            } else {
               var10000 = false;
            }
            break;
         case 1:
            Ready ignored = (Ready)var1;
            var10000 = true;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public ServerPlayer spawnPlayer(final Connection connection, final CommonListenerCookie cookie) {
      State var4 = this.state;
      if (var4 instanceof Ready ready) {
         return ready.spawn(connection, cookie);
      } else {
         throw new IllegalStateException("Player spawn was not ready");
      }
   }

   public void keepAlive() {
      State var2 = this.state;
      if (var2 instanceof Ready ready) {
         ready.keepAlive();
      }

   }

   public void close() {
      State var2 = this.state;
      if (var2 instanceof Preparing preparing) {
         preparing.cancel();
      }

      this.state = null;
   }

   public ConfigurationTask.Type type() {
      return TYPE;
   }

   private final class Preparing implements State {
      private final ServerLevel spawnLevel;
      private final CompletableFuture<Vec3> spawnPosition;
      private final Vec2 spawnAngle;
      private @Nullable CompletableFuture<?> chunkLoadFuture;
      private final ChunkLoadCounter chunkLoadCounter;

      private Preparing(final ServerLevel spawnLevel, final CompletableFuture<Vec3> spawnPosition, final Vec2 spawnAngle) {
         Objects.requireNonNull(PrepareSpawnTask.this);
         super();
         this.chunkLoadCounter = new ChunkLoadCounter();
         this.spawnLevel = spawnLevel;
         this.spawnPosition = spawnPosition;
         this.spawnAngle = spawnAngle;
      }

      public void cancel() {
         this.spawnPosition.cancel(false);
      }

      public @Nullable Ready tick() {
         if (!this.spawnPosition.isDone()) {
            return null;
         } else {
            Vec3 spawnPosition = (Vec3)this.spawnPosition.join();
            if (this.chunkLoadFuture == null) {
               ChunkPos spawnChunk = ChunkPos.containing(BlockPos.containing(spawnPosition));
               this.chunkLoadCounter.track(this.spawnLevel, () -> this.chunkLoadFuture = this.spawnLevel.getChunkSource().addTicketAndLoadWithRadius(TicketType.PLAYER_SPAWN, spawnChunk, 3));
               PrepareSpawnTask.this.loadListener.start(LevelLoadListener.Stage.LOAD_PLAYER_CHUNKS, this.chunkLoadCounter.totalChunks());
               PrepareSpawnTask.this.loadListener.updateFocus(this.spawnLevel.dimension(), spawnChunk);
            }

            PrepareSpawnTask.this.loadListener.update(LevelLoadListener.Stage.LOAD_PLAYER_CHUNKS, this.chunkLoadCounter.readyChunks(), this.chunkLoadCounter.totalChunks());
            if (!this.chunkLoadFuture.isDone()) {
               return null;
            } else {
               PrepareSpawnTask.this.loadListener.finish(LevelLoadListener.Stage.LOAD_PLAYER_CHUNKS);
               return PrepareSpawnTask.this.new Ready(this.spawnLevel, spawnPosition, this.spawnAngle);
            }
         }
      }
   }

   private final class Ready implements State {
      private final ServerLevel spawnLevel;
      private final Vec3 spawnPosition;
      private final Vec2 spawnAngle;

      private Ready(final ServerLevel spawnLevel, final Vec3 spawnPosition, final Vec2 spawnAngle) {
         Objects.requireNonNull(PrepareSpawnTask.this);
         super();
         this.spawnLevel = spawnLevel;
         this.spawnPosition = spawnPosition;
         this.spawnAngle = spawnAngle;
      }

      public void keepAlive() {
         this.spawnLevel.getChunkSource().addTicketWithRadius(TicketType.PLAYER_SPAWN, ChunkPos.containing(BlockPos.containing(this.spawnPosition)), 3);
      }

      public ServerPlayer spawn(final Connection connection, final CommonListenerCookie cookie) {
         ChunkPos spawnChunk = ChunkPos.containing(BlockPos.containing(this.spawnPosition));
         this.spawnLevel.waitForEntities(spawnChunk, 3);
         ServerPlayer player = new ServerPlayer(PrepareSpawnTask.this.server, this.spawnLevel, cookie.gameProfile(), cookie.clientInformation());

         try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(player.problemPath(), PrepareSpawnTask.LOGGER)) {
            Optional<ValueInput> input = PrepareSpawnTask.this.server.getPlayerList().loadPlayerData(PrepareSpawnTask.this.nameAndId).map((tag) -> TagValueInput.create(reporter, PrepareSpawnTask.this.server.registryAccess(), tag));
            Objects.requireNonNull(player);
            input.ifPresent(player::load);
            player.snapTo(this.spawnPosition, this.spawnAngle.x, this.spawnAngle.y);
            PrepareSpawnTask.this.server.getPlayerList().placeNewPlayer(connection, player, cookie);
            input.ifPresent((tag) -> {
               player.loadAndSpawnEnderPearls(tag);
               player.loadAndSpawnParentVehicle(tag);
            });
            return player;
         }
      }
   }

   private sealed interface State permits PrepareSpawnTask.Preparing, PrepareSpawnTask.Ready {
   }
}
