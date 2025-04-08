package net.minecraft.client.server;

import com.mojang.authlib.GameProfile;
import java.net.SocketAddress;
import javax.annotation.Nullable;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.storage.PlayerDataStorage;

public class IntegratedPlayerList extends PlayerList {
   @Nullable
   private CompoundTag playerData;

   public IntegratedPlayerList(IntegratedServer var1, LayeredRegistryAccess<RegistryLayer> var2, PlayerDataStorage var3) {
      super(var1, var2, var3, 8);
      this.setViewDistance(10);
   }

   protected void save(ServerPlayer var1) {
      if (this.getServer().isSingleplayerOwner(var1.getGameProfile())) {
         this.playerData = var1.saveWithoutId(new CompoundTag());
      }

      super.save(var1);
   }

   public Component canPlayerLogin(SocketAddress var1, GameProfile var2) {
      return (Component)(this.getServer().isSingleplayerOwner(var2) && this.getPlayerByName(var2.getName()) != null ? Component.translatable("multiplayer.disconnect.name_taken") : super.canPlayerLogin(var1, var2));
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
