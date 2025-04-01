package net.minecraft.client.server;

import com.mojang.authlib.GameProfile;
import java.net.SocketAddress;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.TheGame;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.storage.PlayerDataStorage;

public class IntegratedPlayerList extends PlayerList {
   @Nullable
   private CompoundTag playerData;

   public IntegratedPlayerList(TheGame var1, PlayerDataStorage var2) {
      super(var1, var2, 8);
      this.setViewDistance(10);
   }

   protected void save(ServerPlayer var1) {
      if (this.theGame().server().isSingleplayerOwner(var1.getGameProfile())) {
         this.playerData = var1.saveWithoutId(new CompoundTag());
      }

      super.save(var1);
   }

   public Component canPlayerLogin(SocketAddress var1, GameProfile var2) {
      return (Component)(this.theGame().server().isSingleplayerOwner(var2) && this.getPlayerByName(var2.getName()) != null ? Component.translatable("multiplayer.disconnect.name_taken") : super.canPlayerLogin(var1, var2));
   }

   @Nullable
   public CompoundTag getSingleplayerData() {
      return this.playerData;
   }
}
