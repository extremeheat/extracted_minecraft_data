package net.minecraft.network.protocol.game;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import org.jspecify.annotations.Nullable;

public class ClientboundSetPlayerTeamPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSetPlayerTeamPacket> STREAM_CODEC = Packet.<RegistryFriendlyByteBuf, ClientboundSetPlayerTeamPacket>codec(ClientboundSetPlayerTeamPacket::write, ClientboundSetPlayerTeamPacket::new);
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

   private ClientboundSetPlayerTeamPacket(final String name, final int method, final Optional<Parameters> parameters, final Collection<String> players) {
      super();
      this.name = name;
      this.method = method;
      this.parameters = parameters;
      this.players = ImmutableList.copyOf(players);
   }

   public static ClientboundSetPlayerTeamPacket createAddOrModifyPacket(final PlayerTeam team, final boolean createNew) {
      return new ClientboundSetPlayerTeamPacket(team.getName(), createNew ? 0 : 2, Optional.of(new Parameters(team)), (Collection)(createNew ? team.getPlayers() : ImmutableList.of()));
   }

   public static ClientboundSetPlayerTeamPacket createRemovePacket(final PlayerTeam team) {
      return new ClientboundSetPlayerTeamPacket(team.getName(), 1, Optional.empty(), ImmutableList.of());
   }

   public static ClientboundSetPlayerTeamPacket createPlayerPacket(final PlayerTeam team, final String player, final Action action) {
      return new ClientboundSetPlayerTeamPacket(team.getName(), action == ClientboundSetPlayerTeamPacket.Action.ADD ? 3 : 4, Optional.empty(), ImmutableList.of(player));
   }

   private ClientboundSetPlayerTeamPacket(final RegistryFriendlyByteBuf input) {
      super();
      this.name = input.readUtf();
      this.method = input.readByte();
      if (shouldHaveParameters(this.method)) {
         this.parameters = Optional.of(new Parameters(input));
      } else {
         this.parameters = Optional.empty();
      }

      if (shouldHavePlayerList(this.method)) {
         this.players = input.readList(FriendlyByteBuf::readUtf);
      } else {
         this.players = ImmutableList.of();
      }

   }

   private void write(final RegistryFriendlyByteBuf output) {
      output.writeUtf(this.name);
      output.writeByte(this.method);
      if (shouldHaveParameters(this.method)) {
         ((Parameters)this.parameters.orElseThrow(() -> new IllegalStateException("Parameters not present, but method is" + this.method))).write(output);
      }

      if (shouldHavePlayerList(this.method)) {
         output.writeCollection(this.players, FriendlyByteBuf::writeUtf);
      }

   }

   private static boolean shouldHavePlayerList(final int method) {
      return method == 0 || method == 3 || method == 4;
   }

   private static boolean shouldHaveParameters(final int method) {
      return method == 0 || method == 2;
   }

   public @Nullable Action getPlayerAction() {
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

   public @Nullable Action getTeamAction() {
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

   public void handle(final ClientGamePacketListener listener) {
      listener.handleSetPlayerTeamPacket(this);
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

   public static class Parameters {
      private final Component displayName;
      private final Component playerPrefix;
      private final Component playerSuffix;
      private final Team.Visibility nametagVisibility;
      private final Team.CollisionRule collisionRule;
      private final ChatFormatting color;
      private final int options;

      public Parameters(final PlayerTeam team) {
         super();
         this.displayName = team.getDisplayName();
         this.options = team.packOptions();
         this.nametagVisibility = team.getNameTagVisibility();
         this.collisionRule = team.getCollisionRule();
         this.color = team.getColor();
         this.playerPrefix = team.getPlayerPrefix();
         this.playerSuffix = team.getPlayerSuffix();
      }

      public Parameters(final RegistryFriendlyByteBuf input) {
         super();
         this.displayName = (Component)ComponentSerialization.TRUSTED_STREAM_CODEC.decode(input);
         this.options = input.readByte();
         this.nametagVisibility = (Team.Visibility)Team.Visibility.STREAM_CODEC.decode(input);
         this.collisionRule = (Team.CollisionRule)Team.CollisionRule.STREAM_CODEC.decode(input);
         this.color = (ChatFormatting)input.readEnum(ChatFormatting.class);
         this.playerPrefix = (Component)ComponentSerialization.TRUSTED_STREAM_CODEC.decode(input);
         this.playerSuffix = (Component)ComponentSerialization.TRUSTED_STREAM_CODEC.decode(input);
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

      public Team.Visibility getNametagVisibility() {
         return this.nametagVisibility;
      }

      public Team.CollisionRule getCollisionRule() {
         return this.collisionRule;
      }

      public Component getPlayerPrefix() {
         return this.playerPrefix;
      }

      public Component getPlayerSuffix() {
         return this.playerSuffix;
      }

      public void write(final RegistryFriendlyByteBuf output) {
         ComponentSerialization.TRUSTED_STREAM_CODEC.encode(output, this.displayName);
         output.writeByte(this.options);
         Team.Visibility.STREAM_CODEC.encode(output, this.nametagVisibility);
         Team.CollisionRule.STREAM_CODEC.encode(output, this.collisionRule);
         output.writeEnum(this.color);
         ComponentSerialization.TRUSTED_STREAM_CODEC.encode(output, this.playerPrefix);
         ComponentSerialization.TRUSTED_STREAM_CODEC.encode(output, this.playerSuffix);
      }
   }
}
