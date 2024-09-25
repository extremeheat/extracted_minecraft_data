package net.minecraft.network.protocol.game;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
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
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundPlayerInfoUpdatePacket> STREAM_CODEC = Packet.codec(
      ClientboundPlayerInfoUpdatePacket::write, ClientboundPlayerInfoUpdatePacket::new
   );
   private final EnumSet<ClientboundPlayerInfoUpdatePacket.Action> actions;
   private final List<ClientboundPlayerInfoUpdatePacket.Entry> entries;

   public ClientboundPlayerInfoUpdatePacket(EnumSet<ClientboundPlayerInfoUpdatePacket.Action> var1, Collection<ServerPlayer> var2) {
      super();
      this.actions = var1;
      this.entries = var2.stream().map(ClientboundPlayerInfoUpdatePacket.Entry::new).toList();
   }

   public ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action var1, ServerPlayer var2) {
      super();
      this.actions = EnumSet.of(var1);
      this.entries = List.of(new ClientboundPlayerInfoUpdatePacket.Entry(var2));
   }

   public static ClientboundPlayerInfoUpdatePacket createPlayerInitializing(Collection<ServerPlayer> var0) {
      EnumSet var1 = EnumSet.of(
         ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER,
         ClientboundPlayerInfoUpdatePacket.Action.INITIALIZE_CHAT,
         ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE,
         ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED,
         ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LATENCY,
         ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME,
         ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LIST_ORDER
      );
      return new ClientboundPlayerInfoUpdatePacket(var1, var0);
   }

   private ClientboundPlayerInfoUpdatePacket(RegistryFriendlyByteBuf var1) {
      super();
      this.actions = var1.readEnumSet(ClientboundPlayerInfoUpdatePacket.Action.class);
      this.entries = var1.readList(var1x -> {
         ClientboundPlayerInfoUpdatePacket.EntryBuilder var2 = new ClientboundPlayerInfoUpdatePacket.EntryBuilder(var1x.readUUID());

         for (ClientboundPlayerInfoUpdatePacket.Action var4 : this.actions) {
            var4.reader.read(var2, (RegistryFriendlyByteBuf)var1x);
         }

         return var2.build();
      });
   }

   private void write(RegistryFriendlyByteBuf var1) {
      var1.writeEnumSet(this.actions, ClientboundPlayerInfoUpdatePacket.Action.class);
      var1.writeCollection(this.entries, (var1x, var2) -> {
         var1x.writeUUID(var2.profileId());

         for (ClientboundPlayerInfoUpdatePacket.Action var4 : this.actions) {
            var4.writer.write((RegistryFriendlyByteBuf)var1x, var2);
         }
      });
   }

   @Override
   public PacketType<ClientboundPlayerInfoUpdatePacket> type() {
      return GamePacketTypes.CLIENTBOUND_PLAYER_INFO_UPDATE;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handlePlayerInfoUpdate(this);
   }

   public EnumSet<ClientboundPlayerInfoUpdatePacket.Action> actions() {
      return this.actions;
   }

   public List<ClientboundPlayerInfoUpdatePacket.Entry> entries() {
      return this.entries;
   }

   public List<ClientboundPlayerInfoUpdatePacket.Entry> newEntries() {
      return this.actions.contains(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER) ? this.entries : List.of();
   }

   @Override
   public String toString() {
      return MoreObjects.toStringHelper(this).add("actions", this.actions).add("entries", this.entries).toString();
   }

   public static enum Action {
      ADD_PLAYER((var0, var1) -> {
         GameProfile var2 = new GameProfile(var0.profileId, var1.readUtf(16));
         var2.getProperties().putAll((Multimap)ByteBufCodecs.GAME_PROFILE_PROPERTIES.decode(var1));
         var0.profile = var2;
      }, (var0, var1) -> {
         GameProfile var2 = Objects.requireNonNull(var1.profile());
         var0.writeUtf(var2.getName(), 16);
         ByteBufCodecs.GAME_PROFILE_PROPERTIES.encode(var0, var2.getProperties());
      }),
      INITIALIZE_CHAT(
         (var0, var1) -> var0.chatSession = var1.readNullable(RemoteChatSession.Data::read),
         (var0, var1) -> var0.writeNullable(var1.chatSession, RemoteChatSession.Data::write)
      ),
      UPDATE_GAME_MODE((var0, var1) -> var0.gameMode = GameType.byId(var1.readVarInt()), (var0, var1) -> var0.writeVarInt(var1.gameMode().getId())),
      UPDATE_LISTED((var0, var1) -> var0.listed = var1.readBoolean(), (var0, var1) -> var0.writeBoolean(var1.listed())),
      UPDATE_LATENCY((var0, var1) -> var0.latency = var1.readVarInt(), (var0, var1) -> var0.writeVarInt(var1.latency())),
      UPDATE_DISPLAY_NAME(
         (var0, var1) -> var0.displayName = FriendlyByteBuf.readNullable(var1, ComponentSerialization.TRUSTED_STREAM_CODEC),
         (var0, var1) -> FriendlyByteBuf.writeNullable(var0, var1.displayName(), ComponentSerialization.TRUSTED_STREAM_CODEC)
      ),
      UPDATE_LIST_ORDER((var0, var1) -> var0.listOrder = var1.readVarInt(), (var0, var1) -> var0.writeVarInt(var1.listOrder));

      final ClientboundPlayerInfoUpdatePacket.Action.Reader reader;
      final ClientboundPlayerInfoUpdatePacket.Action.Writer writer;

      private Action(final ClientboundPlayerInfoUpdatePacket.Action.Reader nullxx, final ClientboundPlayerInfoUpdatePacket.Action.Writer nullxxx) {
         this.reader = nullxx;
         this.writer = nullxxx;
      }

      public interface Reader {
         void read(ClientboundPlayerInfoUpdatePacket.EntryBuilder var1, RegistryFriendlyByteBuf var2);
      }

      public interface Writer {
         void write(RegistryFriendlyByteBuf var1, ClientboundPlayerInfoUpdatePacket.Entry var2);
      }
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

   static class EntryBuilder {
      final UUID profileId;
      @Nullable
      GameProfile profile;
      boolean listed;
      int latency;
      GameType gameMode = GameType.DEFAULT_MODE;
      @Nullable
      Component displayName;
      int listOrder;
      @Nullable
      RemoteChatSession.Data chatSession;

      EntryBuilder(UUID var1) {
         super();
         this.profileId = var1;
      }

      ClientboundPlayerInfoUpdatePacket.Entry build() {
         return new ClientboundPlayerInfoUpdatePacket.Entry(
            this.profileId, this.profile, this.listed, this.latency, this.gameMode, this.displayName, this.listOrder, this.chatSession
         );
      }
   }
}