package net.minecraft.network.protocol.game;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.Optionull;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.RemoteChatSession;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;

public class ClientboundPlayerInfoUpdatePacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundPlayerInfoUpdatePacket> STREAM_CODEC = Packet.codec(ClientboundPlayerInfoUpdatePacket::write, ClientboundPlayerInfoUpdatePacket::new);
   private final EnumSet<Action> actions;
   private final List<Entry> entries;

   public ClientboundPlayerInfoUpdatePacket(EnumSet<Action> var1, Collection<ServerPlayer> var2) {
      super();
      this.actions = var1;
      this.entries = var2.stream().map(Entry::new).toList();
   }

   public ClientboundPlayerInfoUpdatePacket(Action var1, ServerPlayer var2) {
      super();
      this.actions = EnumSet.of(var1);
      this.entries = List.of(new Entry(var2));
   }

   public static ClientboundPlayerInfoUpdatePacket createPlayerInitializing(Collection<ServerPlayer> var0) {
      EnumSet var1 = EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, ClientboundPlayerInfoUpdatePacket.Action.INITIALIZE_CHAT, ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE, ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED, ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LATENCY, ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME, ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LIST_ORDER);
      return new ClientboundPlayerInfoUpdatePacket(var1, var0);
   }

   private ClientboundPlayerInfoUpdatePacket(RegistryFriendlyByteBuf var1) {
      super();
      this.actions = var1.readEnumSet(Action.class);
      this.entries = var1.readList((var1x) -> {
         EntryBuilder var2 = new EntryBuilder(var1x.readUUID());
         Iterator var3 = this.actions.iterator();

         while(var3.hasNext()) {
            Action var4 = (Action)var3.next();
            var4.reader.read(var2, (RegistryFriendlyByteBuf)var1x);
         }

         return var2.build();
      });
   }

   private void write(RegistryFriendlyByteBuf var1) {
      var1.writeEnumSet(this.actions, Action.class);
      var1.writeCollection(this.entries, (var1x, var2) -> {
         var1x.writeUUID(var2.profileId());
         Iterator var3 = this.actions.iterator();

         while(var3.hasNext()) {
            Action var4 = (Action)var3.next();
            var4.writer.write((RegistryFriendlyByteBuf)var1x, var2);
         }

      });
   }

   public PacketType<ClientboundPlayerInfoUpdatePacket> type() {
      return GamePacketTypes.CLIENTBOUND_PLAYER_INFO_UPDATE;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handlePlayerInfoUpdate(this);
   }

   public EnumSet<Action> actions() {
      return this.actions;
   }

   public List<Entry> entries() {
      return this.entries;
   }

   public List<Entry> newEntries() {
      return this.actions.contains(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER) ? this.entries : List.of();
   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("actions", this.actions).add("entries", this.entries).toString();
   }

   public static record Entry(UUID profileId, @Nullable GameProfile profile, boolean listed, int latency, GameType gameMode, @Nullable Component displayName, int listOrder, @Nullable RemoteChatSession.Data chatSession) {
      final int listOrder;
      @Nullable
      final RemoteChatSession.Data chatSession;

      Entry(ServerPlayer var1) {
         this(var1.getUUID(), var1.getGameProfile(), true, var1.connection.latency(), var1.gameMode.getGameModeForPlayer(), var1.getTabListDisplayName(), var1.getTabListOrder(), (RemoteChatSession.Data)Optionull.map(var1.getChatSession(), RemoteChatSession::asData));
      }

      public Entry(UUID var1, @Nullable GameProfile var2, boolean var3, int var4, GameType var5, @Nullable Component var6, int var7, @Nullable RemoteChatSession.Data var8) {
         super();
         this.profileId = var1;
         this.profile = var2;
         this.listed = var3;
         this.latency = var4;
         this.gameMode = var5;
         this.displayName = var6;
         this.listOrder = var7;
         this.chatSession = var8;
      }

      public UUID profileId() {
         return this.profileId;
      }

      @Nullable
      public GameProfile profile() {
         return this.profile;
      }

      public boolean listed() {
         return this.listed;
      }

      public int latency() {
         return this.latency;
      }

      public GameType gameMode() {
         return this.gameMode;
      }

      @Nullable
      public Component displayName() {
         return this.displayName;
      }

      public int listOrder() {
         return this.listOrder;
      }

      @Nullable
      public RemoteChatSession.Data chatSession() {
         return this.chatSession;
      }
   }

   public static enum Action {
      ADD_PLAYER((var0, var1) -> {
         GameProfile var2 = new GameProfile(var0.profileId, var1.readUtf(16));
         var2.getProperties().putAll((Multimap)ByteBufCodecs.GAME_PROFILE_PROPERTIES.decode(var1));
         var0.profile = var2;
      }, (var0, var1) -> {
         GameProfile var2 = (GameProfile)Objects.requireNonNull(var1.profile());
         var0.writeUtf(var2.getName(), 16);
         ByteBufCodecs.GAME_PROFILE_PROPERTIES.encode(var0, var2.getProperties());
      }),
      INITIALIZE_CHAT((var0, var1) -> {
         var0.chatSession = (RemoteChatSession.Data)var1.readNullable(RemoteChatSession.Data::read);
      }, (var0, var1) -> {
         var0.writeNullable(var1.chatSession, RemoteChatSession.Data::write);
      }),
      UPDATE_GAME_MODE((var0, var1) -> {
         var0.gameMode = GameType.byId(var1.readVarInt());
      }, (var0, var1) -> {
         var0.writeVarInt(var1.gameMode().getId());
      }),
      UPDATE_LISTED((var0, var1) -> {
         var0.listed = var1.readBoolean();
      }, (var0, var1) -> {
         var0.writeBoolean(var1.listed());
      }),
      UPDATE_LATENCY((var0, var1) -> {
         var0.latency = var1.readVarInt();
      }, (var0, var1) -> {
         var0.writeVarInt(var1.latency());
      }),
      UPDATE_DISPLAY_NAME((var0, var1) -> {
         var0.displayName = (Component)FriendlyByteBuf.readNullable(var1, ComponentSerialization.TRUSTED_STREAM_CODEC);
      }, (var0, var1) -> {
         FriendlyByteBuf.writeNullable(var0, var1.displayName(), ComponentSerialization.TRUSTED_STREAM_CODEC);
      }),
      UPDATE_LIST_ORDER((var0, var1) -> {
         var0.listOrder = var1.readVarInt();
      }, (var0, var1) -> {
         var0.writeVarInt(var1.listOrder);
      });

      final Reader reader;
      final Writer writer;

      private Action(final Reader var3, final Writer var4) {
         this.reader = var3;
         this.writer = var4;
      }

      // $FF: synthetic method
      private static Action[] $values() {
         return new Action[]{ADD_PLAYER, INITIALIZE_CHAT, UPDATE_GAME_MODE, UPDATE_LISTED, UPDATE_LATENCY, UPDATE_DISPLAY_NAME, UPDATE_LIST_ORDER};
      }

      public interface Reader {
         void read(EntryBuilder var1, RegistryFriendlyByteBuf var2);
      }

      public interface Writer {
         void write(RegistryFriendlyByteBuf var1, Entry var2);
      }
   }

   private static class EntryBuilder {
      final UUID profileId;
      @Nullable
      GameProfile profile;
      boolean listed;
      int latency;
      GameType gameMode;
      @Nullable
      Component displayName;
      int listOrder;
      @Nullable
      RemoteChatSession.Data chatSession;

      EntryBuilder(UUID var1) {
         super();
         this.gameMode = GameType.DEFAULT_MODE;
         this.profileId = var1;
      }

      Entry build() {
         return new Entry(this.profileId, this.profile, this.listed, this.latency, this.gameMode, this.displayName, this.listOrder, this.chatSession);
      }
   }
}
