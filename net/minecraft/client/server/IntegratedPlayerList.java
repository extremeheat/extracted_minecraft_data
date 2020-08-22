package net.minecraft.client.server;

import com.mojang.authlib.GameProfile;
import java.net.SocketAddress;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;

public class IntegratedPlayerList extends PlayerList {
   private CompoundTag playerData;

   public IntegratedPlayerList(IntegratedServer var1) {
      super(var1, 8);
      this.setViewDistance(10);
   }

   protected void save(ServerPlayer var1) {
      if (var1.getName().getString().equals(this.getServer().getSingleplayerName())) {
         this.playerData = var1.saveWithoutId(new CompoundTag());
      }

      super.save(var1);
   }

   public Component canPlayerLogin(SocketAddress var1, GameProfile var2) {
      return (Component)(var2.getName().equalsIgnoreCase(this.getServer().getSingleplayerName()) && this.getPlayerByName(var2.getName()) != null ? new TranslatableComponent("multiplayer.disconnect.name_taken", new Object[0]) : super.canPlayerLogin(var1, var2));
   }

   public IntegratedServer getServer() {
      return (IntegratedServer)super.getServer();
   }

   public CompoundTag getSingleplayerData() {
      return this.playerData;
   }

   // $FF: synthetic method
   public MinecraftServer getServer() {
      return this.getServer();
   }
}
