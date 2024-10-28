package net.minecraft.network.protocol.game;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.scores.PlayerTeam;

public class ClientboundSetPlayerTeamPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSetPlayerTeamPacket> STREAM_CODEC = Packet.codec(ClientboundSetPlayerTeamPacket::write, ClientboundSetPlayerTeamPacket::new);
   private static final int METHOD_ADD = 0;
   private static final int METHOD_REMOVE = 1;
   private static final int METHOD_CHANGE = 2;
   private static final int METHOD_JOIN = 3;
   private static final int METHOD_LEAVE = 4;
   private static final int MAX_VISIBILITY_LENGTH = 40;
   private static final int MAX_COLLISION_LENGTH = 40;
   private final int method;
   private final String name;
   private final Collection<String> players;
   private final Optional<Parameters> parameters;

   private ClientboundSetPlayerTeamPacket(String var1, int var2, Optional<Parameters> var3, Collection<String> var4) {
      super();
      this.name = var1;
      this.method = var2;
      this.parameters = var3;
      this.players = ImmutableList.copyOf(var4);
   }

   public static ClientboundSetPlayerTeamPacket createAddOrModifyPacket(PlayerTeam var0, boolean var1) {
      return new ClientboundSetPlayerTeamPacket(var0.getName(), var1 ? 0 : 2, Optional.of(new Parameters(var0)), (Collection)(var1 ? var0.getPlayers() : ImmutableList.of()));
   }

   public static ClientboundSetPlayerTeamPacket createRemovePacket(PlayerTeam var0) {
      return new ClientboundSetPlayerTeamPacket(var0.getName(), 1, Optional.empty(), ImmutableList.of());
   }

   public static ClientboundSetPlayerTeamPacket createPlayerPacket(PlayerTeam var0, String var1, Action var2) {
      return new ClientboundSetPlayerTeamPacket(var0.getName(), var2 == ClientboundSetPlayerTeamPacket.Action.ADD ? 3 : 4, Optional.empty(), ImmutableList.of(var1));
   }

   private ClientboundSetPlayerTeamPacket(RegistryFriendlyByteBuf var1) {
      super();
      this.name = var1.readUtf();
      this.method = var1.readByte();
      if (shouldHaveParameters(this.method)) {
         this.parameters = Optional.of(new Parameters(var1));
      } else {
         this.parameters = Optional.empty();
      }

      if (shouldHavePlayerList(this.method)) {
         this.players = var1.readList(FriendlyByteBuf::readUtf);
      } else {
         this.players = ImmutableList.of();
      }

   }

   private void write(RegistryFriendlyByteBuf var1) {
      var1.writeUtf(this.name);
      var1.writeByte(this.method);
      if (shouldHaveParameters(this.method)) {
         ((Parameters)this.parameters.orElseThrow(() -> {
            return new IllegalStateException("Parameters not present, but method is" + this.method);
         })).write(var1);
      }

      if (shouldHavePlayerList(this.method)) {
         var1.writeCollection(this.players, FriendlyByteBuf::writeUtf);
      }

   }

   private static boolean shouldHavePlayerList(int var0) {
      return var0 == 0 || var0 == 3 || var0 == 4;
   }

   private static boolean shouldHaveParameters(int var0) {
      return var0 == 0 || var0 == 2;
   }

   @Nullable
   public Action getPlayerAction() {
      Action var10000;
      switch (this.method) {
         case 0:
         case 3:
            var10000 = ClientboundSetPlayerTeamPacket.Action.ADD;
            break;
         case 1:
         case 2:
         default:
            var10000 = null;
            break;
         case 4:
            var10000 = ClientboundSetPlayerTeamPacket.Action.REMOVE;
      }

      return var10000;
   }

   @Nullable
   public Action getTeamAction() {
      Action var10000;
      switch (this.method) {
         case 0 -> var10000 = ClientboundSetPlayerTeamPacket.Action.ADD;
         case 1 -> var10000 = ClientboundSetPlayerTeamPacket.Action.REMOVE;
         default -> var10000 = null;
      }

      return var10000;
   }

   public PacketType<ClientboundSetPlayerTeamPacket> type() {
      return GamePacketTypes.CLIENTBOUND_SET_PLAYER_TEAM;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSetPlayerTeamPacket(this);
   }

   public String getName() {
      return this.name;
   }

   public Collection<String> getPlayers() {
      return this.players;
   }

   public Optional<Parameters> getParameters() {
      return this.parameters;
   }

   public static class Parameters {
      private final Component displayName;
      private final Component playerPrefix;
      private final Component playerSuffix;
      private final String nametagVisibility;
      private final String collisionRule;
      private final ChatFormatting color;
      private final int options;

      public Parameters(PlayerTeam var1) {
         super();
         this.displayName = var1.getDisplayName();
         this.options = var1.packOptions();
         this.nametagVisibility = var1.getNameTagVisibility().name;
         this.collisionRule = var1.getCollisionRule().name;
         this.color = var1.getColor();
         this.playerPrefix = var1.getPlayerPrefix();
         this.playerSuffix = var1.getPlayerSuffix();
      }

      public Parameters(RegistryFriendlyByteBuf var1) {
         super();
         this.displayName = (Component)ComponentSerialization.TRUSTED_STREAM_CODEC.decode(var1);
         this.options = var1.readByte();
         this.nametagVisibility = var1.readUtf(40);
         this.collisionRule = var1.readUtf(40);
         this.color = (ChatFormatting)var1.readEnum(ChatFormatting.class);
         this.playerPrefix = (Component)ComponentSerialization.TRUSTED_STREAM_CODEC.decode(var1);
         this.playerSuffix = (Component)ComponentSerialization.TRUSTED_STREAM_CODEC.decode(var1);
      }

      public Component getDisplayName() {
         return this.displayName;
      }

      public int getOptions() {
         return this.options;
      }

      public ChatFormatting getColor() {
         return this.color;
      }

      public String getNametagVisibility() {
         return this.nametagVisibility;
      }

      public String getCollisionRule() {
         return this.collisionRule;
      }

      public Component getPlayerPrefix() {
         return this.playerPrefix;
      }

      public Component getPlayerSuffix() {
         return this.playerSuffix;
      }

      public void write(RegistryFriendlyByteBuf var1) {
         ComponentSerialization.TRUSTED_STREAM_CODEC.encode(var1, this.displayName);
         var1.writeByte(this.options);
         var1.writeUtf(this.nametagVisibility);
         var1.writeUtf(this.collisionRule);
         var1.writeEnum(this.color);
         ComponentSerialization.TRUSTED_STREAM_CODEC.encode(var1, this.playerPrefix);
         ComponentSerialization.TRUSTED_STREAM_CODEC.encode(var1, this.playerSuffix);
      }
   }

   public static enum Action {
      ADD,
      REMOVE;

      private Action() {
      }

      // $FF: synthetic method
      private static Action[] $values() {
         return new Action[]{ADD, REMOVE};
      }
   }
}
