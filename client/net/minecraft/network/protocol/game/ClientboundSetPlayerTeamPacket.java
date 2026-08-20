package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import net.minecraft.world.scores.TeamColor;
import org.jspecify.annotations.Nullable;

public class ClientboundSetPlayerTeamPacket implements Packet<ClientGamePacketListener> {
   private static final StreamCodec<ByteBuf, List<String>> PLAYER_LIST_STREAM_CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSetPlayerTeamPacket> STREAM_CODEC;
   private static final int METHOD_ADD = 0;
   private static final int METHOD_REMOVE = 1;
   private static final int METHOD_CHANGE = 2;
   private static final int METHOD_JOIN = 3;
   private static final int METHOD_LEAVE = 4;
   private final int method;
   private final String name;
   private final List<String> players;
   private final Optional<Parameters> parameters;

   private ClientboundSetPlayerTeamPacket(final String name, final int method, final Optional<Parameters> parameters, final Collection<String> players) {
      super();
      this.name = name;
      this.method = method;
      this.parameters = parameters;
      this.players = List.copyOf(players);
   }

   public static ClientboundSetPlayerTeamPacket createAddOrModifyPacket(final PlayerTeam team, final boolean createNew) {
      return new ClientboundSetPlayerTeamPacket(team.getName(), createNew ? 0 : 2, Optional.of(new Parameters(team)), (Collection)(createNew ? team.getPlayers() : List.of()));
   }

   public static ClientboundSetPlayerTeamPacket createRemovePacket(final PlayerTeam team) {
      return new ClientboundSetPlayerTeamPacket(team.getName(), 1, Optional.empty(), List.of());
   }

   public static ClientboundSetPlayerTeamPacket createPlayerPacket(final PlayerTeam team, final String player, final Action action) {
      return new ClientboundSetPlayerTeamPacket(team.getName(), action == ClientboundSetPlayerTeamPacket.Action.ADD ? 3 : 4, Optional.empty(), List.of(player));
   }

   private ClientboundSetPlayerTeamPacket(final RegistryFriendlyByteBuf input) {
      super();
      this.name = input.readUtf();
      this.method = input.readByte();
      if (shouldHaveParameters(this.method)) {
         this.parameters = Optional.of((Parameters)ClientboundSetPlayerTeamPacket.Parameters.STREAM_CODEC.decode(input));
      } else {
         this.parameters = Optional.empty();
      }

      if (shouldHavePlayerList(this.method)) {
         this.players = (List)PLAYER_LIST_STREAM_CODEC.decode(input);
      } else {
         this.players = List.of();
      }

   }

   private void write(final RegistryFriendlyByteBuf output) {
      output.writeUtf(this.name);
      output.writeByte(this.method);
      if (shouldHaveParameters(this.method)) {
         ClientboundSetPlayerTeamPacket.Parameters.STREAM_CODEC.encode(output, (Parameters)this.parameters.orElseThrow(() -> new IllegalStateException("Parameters not present, but method is" + this.method)));
      }

      if (shouldHavePlayerList(this.method)) {
         PLAYER_LIST_STREAM_CODEC.encode(output, this.players);
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

   static {
      PLAYER_LIST_STREAM_CODEC = ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list());
      STREAM_CODEC = Packet.<RegistryFriendlyByteBuf, ClientboundSetPlayerTeamPacket>codec(ClientboundSetPlayerTeamPacket::write, ClientboundSetPlayerTeamPacket::new);
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

   public static record Parameters(Component displayName, Component playerPrefix, Component playerSuffix, Team.Visibility nameTagVisibility, Team.CollisionRule collisionRule, Optional<TeamColor> color, @PlayerTeam.OptionFlags byte options) {
      public static final StreamCodec<RegistryFriendlyByteBuf, Parameters> STREAM_CODEC;

      public Parameters(final PlayerTeam team) {
         this(team.getDisplayName(), team.getPlayerPrefix(), team.getPlayerSuffix(), team.getNameTagVisibility(), team.getCollisionRule(), team.getColor(), team.packOptions());
      }

      public Parameters {
         super();
      }

      static {
         STREAM_CODEC = StreamCodec.composite(ComponentSerialization.TRUSTED_STREAM_CODEC, Parameters::displayName, ComponentSerialization.TRUSTED_STREAM_CODEC, Parameters::playerPrefix, ComponentSerialization.TRUSTED_STREAM_CODEC, Parameters::playerSuffix, Team.Visibility.STREAM_CODEC, Parameters::nameTagVisibility, Team.CollisionRule.STREAM_CODEC, Parameters::collisionRule, ByteBufCodecs.optional(TeamColor.STREAM_CODEC), Parameters::color, ByteBufCodecs.BYTE, Parameters::options, Parameters::new);
      }
   }
}
