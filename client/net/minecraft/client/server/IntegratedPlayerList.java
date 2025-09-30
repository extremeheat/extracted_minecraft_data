package net.minecraft.client.server;

import com.mojang.logging.LogUtils;
import java.net.SocketAddress;
import javax.annotation.Nullable;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.PlayerDataStorage;
import net.minecraft.world.level.storage.TagValueOutput;
import org.slf4j.Logger;

public class IntegratedPlayerList extends PlayerList {
   private static final Logger LOGGER = LogUtils.getLogger();
   @Nullable
   private CompoundTag playerData;

   public IntegratedPlayerList(IntegratedServer var1, LayeredRegistryAccess<RegistryLayer> var2, PlayerDataStorage var3) {
      super(var1, var2, var3, var1.notificationManager());
      this.setViewDistance(10);
   }

   protected void save(ServerPlayer var1) {
      if (this.getServer().isSingleplayerOwner(var1.nameAndId())) {
         try (ProblemReporter.ScopedCollector var2 = new ProblemReporter.ScopedCollector(var1.problemPath(), LOGGER)) {
            TagValueOutput var3 = TagValueOutput.createWithContext(var2, var1.registryAccess());
            var1.saveWithoutId(var3);
            this.playerData = var3.buildResult();
         }
      }

      super.save(var1);
   }

   public Component canPlayerLogin(SocketAddress var1, NameAndId var2) {
      return (Component)(this.getServer().isSingleplayerOwner(var2) && this.getPlayerByName(var2.name()) != null ? Component.translatable("multiplayer.disconnect.name_taken") : super.canPlayerLogin(var1, var2));
   }

   public IntegratedServer getServer() {
      return (IntegratedServer)super.getServer();
   }

   @Nullable
   public CompoundTag getSingleplayerData() {
      return this.playerData;
   }

   // $FF: synthetic method
   public MinecraftServer getServer() {
      return this.getServer();
   }
}
