package net.minecraft.client.player;

import com.mojang.authlib.GameProfile;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.server.players.ProfileResolver;

public class LocalPlayerResolver implements ProfileResolver {
   private final Minecraft minecraft;
   private final ProfileResolver parentResolver;

   public LocalPlayerResolver(Minecraft var1, ProfileResolver var2) {
      super();
      this.minecraft = var1;
      this.parentResolver = var2;
   }

   public Optional<GameProfile> fetchByName(String var1) {
      ClientPacketListener var2 = this.minecraft.getConnection();
      if (var2 != null) {
         PlayerInfo var3 = var2.getPlayerInfoIgnoreCase(var1);
         if (var3 != null) {
            return Optional.of(var3.getProfile());
         }
      }

      return this.parentResolver.fetchByName(var1);
   }

   public Optional<GameProfile> fetchById(UUID var1) {
      ClientPacketListener var2 = this.minecraft.getConnection();
      if (var2 != null) {
         PlayerInfo var3 = var2.getPlayerInfo(var1);
         if (var3 != null) {
            return Optional.of(var3.getProfile());
         }
      }

      return this.parentResolver.fetchById(var1);
   }
}
