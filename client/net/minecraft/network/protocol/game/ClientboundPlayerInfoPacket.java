package net.minecraft.network.protocol.game;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.level.GameType;

public class ClientboundPlayerInfoPacket implements Packet<ClientGamePacketListener> {
   private final ClientboundPlayerInfoPacket.Action action;
   private final List<ClientboundPlayerInfoPacket.PlayerUpdate> entries;

   public ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action var1, ServerPlayer... var2) {
      super();
      this.action = var1;
      this.entries = Lists.newArrayListWithCapacity(var2.length);

      for(ServerPlayer var6 : var2) {
         this.entries.add(createPlayerUpdate(var6));
      }
   }

   public ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action var1, Collection<ServerPlayer> var2) {
      super();
      this.action = var1;
      this.entries = Lists.newArrayListWithCapacity(var2.size());

      for(ServerPlayer var4 : var2) {
         this.entries.add(createPlayerUpdate(var4));
      }
   }

   public ClientboundPlayerInfoPacket(FriendlyByteBuf var1) {
      super();
      this.action = var1.readEnum(ClientboundPlayerInfoPacket.Action.class);
      this.entries = var1.readList(this.action::read);
   }

   private static ClientboundPlayerInfoPacket.PlayerUpdate createPlayerUpdate(ServerPlayer var0) {
      ProfilePublicKey var1 = var0.getProfilePublicKey();
      ProfilePublicKey.Data var2 = var1 != null ? var1.data() : null;
      return new ClientboundPlayerInfoPacket.PlayerUpdate(
         var0.getGameProfile(), var0.latency, var0.gameMode.getGameModeForPlayer(), var0.getTabListDisplayName(), var2
      );
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeEnum(this.action);
      var1.writeCollection(this.entries, this.action::write);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handlePlayerInfo(this);
   }

   public List<ClientboundPlayerInfoPacket.PlayerUpdate> getEntries() {
      return this.entries;
   }

   public ClientboundPlayerInfoPacket.Action getAction() {
      return this.action;
   }

   @Override
   public String toString() {
      return MoreObjects.toStringHelper(this).add("action", this.action).add("entries", this.entries).toString();
   }

   public static enum Action {
      ADD_PLAYER {
         @Override
         protected ClientboundPlayerInfoPacket.PlayerUpdate read(FriendlyByteBuf var1) {
            GameProfile var2 = var1.readGameProfile();
            GameType var3 = GameType.byId(var1.readVarInt());
            int var4 = var1.readVarInt();
            Component var5 = var1.readNullable(FriendlyByteBuf::readComponent);
            ProfilePublicKey.Data var6 = var1.readNullable(ProfilePublicKey.Data::new);
            return new ClientboundPlayerInfoPacket.PlayerUpdate(var2, var4, var3, var5, var6);
         }

         @Override
         protected void write(FriendlyByteBuf var1, ClientboundPlayerInfoPacket.PlayerUpdate var2) {
            var1.writeGameProfile(var2.getProfile());
            var1.writeVarInt(var2.getGameMode().getId());
            var1.writeVarInt(var2.getLatency());
            var1.writeNullable(var2.getDisplayName(), FriendlyByteBuf::writeComponent);
            var1.writeNullable(var2.getProfilePublicKey(), (var0, var1x) -> var1x.write(var0));
         }
      },
      UPDATE_GAME_MODE {
         @Override
         protected ClientboundPlayerInfoPacket.PlayerUpdate read(FriendlyByteBuf var1) {
            GameProfile var2 = new GameProfile(var1.readUUID(), null);
            GameType var3 = GameType.byId(var1.readVarInt());
            return new ClientboundPlayerInfoPacket.PlayerUpdate(var2, 0, var3, null, null);
         }

         @Override
         protected void write(FriendlyByteBuf var1, ClientboundPlayerInfoPacket.PlayerUpdate var2) {
            var1.writeUUID(var2.getProfile().getId());
            var1.writeVarInt(var2.getGameMode().getId());
         }
      },
      UPDATE_LATENCY {
         @Override
         protected ClientboundPlayerInfoPacket.PlayerUpdate read(FriendlyByteBuf var1) {
            GameProfile var2 = new GameProfile(var1.readUUID(), null);
            int var3 = var1.readVarInt();
            return new ClientboundPlayerInfoPacket.PlayerUpdate(var2, var3, null, null, null);
         }

         @Override
         protected void write(FriendlyByteBuf var1, ClientboundPlayerInfoPacket.PlayerUpdate var2) {
            var1.writeUUID(var2.getProfile().getId());
            var1.writeVarInt(var2.getLatency());
         }
      },
      UPDATE_DISPLAY_NAME {
         @Override
         protected ClientboundPlayerInfoPacket.PlayerUpdate read(FriendlyByteBuf var1) {
            GameProfile var2 = new GameProfile(var1.readUUID(), null);
            Component var3 = var1.readNullable(FriendlyByteBuf::readComponent);
            return new ClientboundPlayerInfoPacket.PlayerUpdate(var2, 0, null, var3, null);
         }

         @Override
         protected void write(FriendlyByteBuf var1, ClientboundPlayerInfoPacket.PlayerUpdate var2) {
            var1.writeUUID(var2.getProfile().getId());
            var1.writeNullable(var2.getDisplayName(), FriendlyByteBuf::writeComponent);
         }
      },
      REMOVE_PLAYER {
         @Override
         protected ClientboundPlayerInfoPacket.PlayerUpdate read(FriendlyByteBuf var1) {
            GameProfile var2 = new GameProfile(var1.readUUID(), null);
            return new ClientboundPlayerInfoPacket.PlayerUpdate(var2, 0, null, null, null);
         }

         @Override
         protected void write(FriendlyByteBuf var1, ClientboundPlayerInfoPacket.PlayerUpdate var2) {
            var1.writeUUID(var2.getProfile().getId());
         }
      };

      Action() {
      }

      protected abstract ClientboundPlayerInfoPacket.PlayerUpdate read(FriendlyByteBuf var1);

      protected abstract void write(FriendlyByteBuf var1, ClientboundPlayerInfoPacket.PlayerUpdate var2);
   }

   public static class PlayerUpdate {
      private final int latency;
      private final GameType gameMode;
      private final GameProfile profile;
      @Nullable
      private final Component displayName;
      @Nullable
      private final ProfilePublicKey.Data profilePublicKey;

      public PlayerUpdate(GameProfile var1, int var2, @Nullable GameType var3, @Nullable Component var4, @Nullable ProfilePublicKey.Data var5) {
         super();
         this.profile = var1;
         this.latency = var2;
         this.gameMode = var3;
         this.displayName = var4;
         this.profilePublicKey = var5;
      }

      public GameProfile getProfile() {
         return this.profile;
      }

      public int getLatency() {
         return this.latency;
      }

      public GameType getGameMode() {
         return this.gameMode;
      }

      @Nullable
      public Component getDisplayName() {
         return this.displayName;
      }

      @Nullable
      public ProfilePublicKey.Data getProfilePublicKey() {
         return this.profilePublicKey;
      }

      @Override
      public String toString() {
         return MoreObjects.toStringHelper(this)
            .add("latency", this.latency)
            .add("gameMode", this.gameMode)
            .add("profile", this.profile)
            .add("displayName", this.displayName == null ? null : Component.Serializer.toJson(this.displayName))
            .add("profilePublicKey", this.profilePublicKey)
            .toString();
      }
   }
}
