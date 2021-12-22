package net.minecraft.network.protocol.game;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;

public class ClientboundPlayerInfoPacket implements Packet<ClientGamePacketListener> {
   private final ClientboundPlayerInfoPacket.Action action;
   private final List<ClientboundPlayerInfoPacket.PlayerUpdate> entries;

   public ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action var1, ServerPlayer... var2) {
      super();
      this.action = var1;
      this.entries = Lists.newArrayListWithCapacity(var2.length);
      ServerPlayer[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ServerPlayer var6 = var3[var5];
         this.entries.add(new ClientboundPlayerInfoPacket.PlayerUpdate(var6.getGameProfile(), var6.latency, var6.gameMode.getGameModeForPlayer(), var6.getTabListDisplayName()));
      }

   }

   public ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action var1, Collection<ServerPlayer> var2) {
      super();
      this.action = var1;
      this.entries = Lists.newArrayListWithCapacity(var2.size());
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         ServerPlayer var4 = (ServerPlayer)var3.next();
         this.entries.add(new ClientboundPlayerInfoPacket.PlayerUpdate(var4.getGameProfile(), var4.latency, var4.gameMode.getGameModeForPlayer(), var4.getTabListDisplayName()));
      }

   }

   public ClientboundPlayerInfoPacket(FriendlyByteBuf var1) {
      super();
      this.action = (ClientboundPlayerInfoPacket.Action)var1.readEnum(ClientboundPlayerInfoPacket.Action.class);
      ClientboundPlayerInfoPacket.Action var10002 = this.action;
      Objects.requireNonNull(var10002);
      this.entries = var1.readList(var10002::read);
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeEnum(this.action);
      List var10001 = this.entries;
      ClientboundPlayerInfoPacket.Action var10002 = this.action;
      Objects.requireNonNull(var10002);
      var1.writeCollection(var10001, var10002::write);
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

   @Nullable
   static Component readDisplayName(FriendlyByteBuf var0) {
      return var0.readBoolean() ? var0.readComponent() : null;
   }

   static void writeDisplayName(FriendlyByteBuf var0, @Nullable Component var1) {
      if (var1 == null) {
         var0.writeBoolean(false);
      } else {
         var0.writeBoolean(true);
         var0.writeComponent(var1);
      }

   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("action", this.action).add("entries", this.entries).toString();
   }

   public static enum Action {
      ADD_PLAYER {
         protected ClientboundPlayerInfoPacket.PlayerUpdate read(FriendlyByteBuf var1) {
            GameProfile var2 = new GameProfile(var1.readUUID(), var1.readUtf(16));
            PropertyMap var3 = var2.getProperties();
            var1.readWithCount((var1x) -> {
               String var2 = var1x.readUtf();
               String var3x = var1x.readUtf();
               if (var1x.readBoolean()) {
                  String var4 = var1x.readUtf();
                  var3.put(var2, new Property(var2, var3x, var4));
               } else {
                  var3.put(var2, new Property(var2, var3x));
               }

            });
            GameType var4 = GameType.byId(var1.readVarInt());
            int var5 = var1.readVarInt();
            Component var6 = ClientboundPlayerInfoPacket.readDisplayName(var1);
            return new ClientboundPlayerInfoPacket.PlayerUpdate(var2, var5, var4, var6);
         }

         protected void write(FriendlyByteBuf var1, ClientboundPlayerInfoPacket.PlayerUpdate var2) {
            var1.writeUUID(var2.getProfile().getId());
            var1.writeUtf(var2.getProfile().getName());
            var1.writeCollection(var2.getProfile().getProperties().values(), (var0, var1x) -> {
               var0.writeUtf(var1x.getName());
               var0.writeUtf(var1x.getValue());
               if (var1x.hasSignature()) {
                  var0.writeBoolean(true);
                  var0.writeUtf(var1x.getSignature());
               } else {
                  var0.writeBoolean(false);
               }

            });
            var1.writeVarInt(var2.getGameMode().getId());
            var1.writeVarInt(var2.getLatency());
            ClientboundPlayerInfoPacket.writeDisplayName(var1, var2.getDisplayName());
         }
      },
      UPDATE_GAME_MODE {
         protected ClientboundPlayerInfoPacket.PlayerUpdate read(FriendlyByteBuf var1) {
            GameProfile var2 = new GameProfile(var1.readUUID(), (String)null);
            GameType var3 = GameType.byId(var1.readVarInt());
            return new ClientboundPlayerInfoPacket.PlayerUpdate(var2, 0, var3, (Component)null);
         }

         protected void write(FriendlyByteBuf var1, ClientboundPlayerInfoPacket.PlayerUpdate var2) {
            var1.writeUUID(var2.getProfile().getId());
            var1.writeVarInt(var2.getGameMode().getId());
         }
      },
      UPDATE_LATENCY {
         protected ClientboundPlayerInfoPacket.PlayerUpdate read(FriendlyByteBuf var1) {
            GameProfile var2 = new GameProfile(var1.readUUID(), (String)null);
            int var3 = var1.readVarInt();
            return new ClientboundPlayerInfoPacket.PlayerUpdate(var2, var3, (GameType)null, (Component)null);
         }

         protected void write(FriendlyByteBuf var1, ClientboundPlayerInfoPacket.PlayerUpdate var2) {
            var1.writeUUID(var2.getProfile().getId());
            var1.writeVarInt(var2.getLatency());
         }
      },
      UPDATE_DISPLAY_NAME {
         protected ClientboundPlayerInfoPacket.PlayerUpdate read(FriendlyByteBuf var1) {
            GameProfile var2 = new GameProfile(var1.readUUID(), (String)null);
            Component var3 = ClientboundPlayerInfoPacket.readDisplayName(var1);
            return new ClientboundPlayerInfoPacket.PlayerUpdate(var2, 0, (GameType)null, var3);
         }

         protected void write(FriendlyByteBuf var1, ClientboundPlayerInfoPacket.PlayerUpdate var2) {
            var1.writeUUID(var2.getProfile().getId());
            ClientboundPlayerInfoPacket.writeDisplayName(var1, var2.getDisplayName());
         }
      },
      REMOVE_PLAYER {
         protected ClientboundPlayerInfoPacket.PlayerUpdate read(FriendlyByteBuf var1) {
            GameProfile var2 = new GameProfile(var1.readUUID(), (String)null);
            return new ClientboundPlayerInfoPacket.PlayerUpdate(var2, 0, (GameType)null, (Component)null);
         }

         protected void write(FriendlyByteBuf var1, ClientboundPlayerInfoPacket.PlayerUpdate var2) {
            var1.writeUUID(var2.getProfile().getId());
         }
      };

      Action() {
      }

      protected abstract ClientboundPlayerInfoPacket.PlayerUpdate read(FriendlyByteBuf var1);

      protected abstract void write(FriendlyByteBuf var1, ClientboundPlayerInfoPacket.PlayerUpdate var2);

      // $FF: synthetic method
      private static ClientboundPlayerInfoPacket.Action[] $values() {
         return new ClientboundPlayerInfoPacket.Action[]{ADD_PLAYER, UPDATE_GAME_MODE, UPDATE_LATENCY, UPDATE_DISPLAY_NAME, REMOVE_PLAYER};
      }
   }

   public static class PlayerUpdate {
      private final int latency;
      private final GameType gameMode;
      private final GameProfile profile;
      @Nullable
      private final Component displayName;

      public PlayerUpdate(GameProfile var1, int var2, @Nullable GameType var3, @Nullable Component var4) {
         super();
         this.profile = var1;
         this.latency = var2;
         this.gameMode = var3;
         this.displayName = var4;
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

      public String toString() {
         return MoreObjects.toStringHelper(this).add("latency", this.latency).add("gameMode", this.gameMode).add("profile", this.profile).add("displayName", this.displayName == null ? null : Component.Serializer.toJson(this.displayName)).toString();
      }
   }
}
