package net.minecraft.client.server;

import com.mojang.logging.LogUtils;
import java.net.SocketAddress;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.PlayerDataStorage;
import net.minecraft.world.level.storage.TagValueOutput;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class IntegratedPlayerList extends PlayerList {
   private static final Logger LOGGER = LogUtils.getLogger();
   private @Nullable CompoundTag playerData;

   public IntegratedPlayerList(final IntegratedServer server, final LayeredRegistryAccess<RegistryLayer> registryHolder, final PlayerDataStorage playerDataStorage) {
      super(server, registryHolder, playerDataStorage, server.notificationManager());
      this.setViewDistance(10);
   }

   protected void save(final ServerPlayer player) {
      if (this.getServer().isSingleplayerOwner(player.nameAndId())) {
         try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(player.problemPath(), LOGGER)) {
            TagValueOutput output = TagValueOutput.createWithContext(reporter, player.registryAccess());
            player.saveWithoutId(output);
            this.playerData = output.buildResult();
         }
      }

      super.save(player);
   }

   public Component canPlayerLogin(final SocketAddress address, final NameAndId nameAndId) {
      return (Component)(this.getServer().isSingleplayerOwner(nameAndId) && this.getPlayerByName(nameAndId.name()) != null ? Component.translatable("multiplayer.disconnect.name_taken") : super.canPlayerLogin(address, nameAndId));
   }

   public IntegratedServer getServer() {
      return (IntegratedServer)super.getServer();
   }

   public @Nullable CompoundTag getSingleplayerData() {
      return this.playerData;
   }
}
