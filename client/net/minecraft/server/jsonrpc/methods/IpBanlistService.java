package net.minecraft.server.jsonrpc.methods;

import com.google.common.net.InetAddresses;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.network.chat.Component;
import net.minecraft.server.jsonrpc.api.PlayerDto;
import net.minecraft.server.jsonrpc.internalapi.MinecraftApi;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.IpBanListEntry;
import net.minecraft.util.ExtraCodecs;
import org.jspecify.annotations.Nullable;

public class IpBanlistService {
   private static final String BAN_SOURCE = "Management server";

   public IpBanlistService() {
      super();
   }

   public static List<IpBanDto> get(final MinecraftApi minecraftApi) {
      return minecraftApi.banListService().getIpBanEntries().stream().map(IpBan::from).map(IpBanDto::from).toList();
   }

   public static List<IpBanDto> add(final MinecraftApi minecraftApi, final List<IncomingIpBanDto> bans, final ClientInfo clientInfo) {
      bans.stream().map((ban) -> banIp(minecraftApi, ban, clientInfo)).flatMap(Collection::stream).forEach((player) -> player.connection.disconnect(Component.translatable("multiplayer.disconnect.ip_banned")));
      return get(minecraftApi);
   }

   private static List<ServerPlayer> banIp(final MinecraftApi minecraftApi, final IncomingIpBanDto ban, final ClientInfo clientInfo) {
      IpBan ipBan = ban.toIpBan();
      if (ipBan != null) {
         return banIp(minecraftApi, ipBan, clientInfo);
      } else {
         if (ban.player().isPresent()) {
            Optional<ServerPlayer> player = minecraftApi.playerListService().getPlayer(((PlayerDto)ban.player().get()).id(), ((PlayerDto)ban.player().get()).name());
            if (player.isPresent()) {
               return banIp(minecraftApi, ban.toIpBan((ServerPlayer)player.get()), clientInfo);
            }
         }

         return List.of();
      }
   }

   private static List<ServerPlayer> banIp(final MinecraftApi minecraftApi, final IpBan ban, final ClientInfo clientInfo) {
      minecraftApi.banListService().addIpBan(ban.toIpBanEntry(), clientInfo);
      return minecraftApi.playerListService().getPlayersWithAddress(ban.ip());
   }

   public static List<IpBanDto> clear(final MinecraftApi minecraftApi, final ClientInfo clientInfo) {
      minecraftApi.banListService().clearIpBans(clientInfo);
      return get(minecraftApi);
   }

   public static List<IpBanDto> remove(final MinecraftApi minecraftApi, final List<String> ban, final ClientInfo clientInfo) {
      ban.forEach((ip) -> minecraftApi.banListService().removeIpBan(ip, clientInfo));
      return get(minecraftApi);
   }

   public static List<IpBanDto> set(final MinecraftApi minecraftApi, final List<IpBanDto> ips, final ClientInfo clientInfo) {
      Set<IpBan> finalBanlist = (Set)ips.stream().filter((ban) -> InetAddresses.isInetAddress(ban.ip())).map(IpBanDto::toIpBan).collect(Collectors.toSet());
      Set<IpBan> currentBans = (Set)minecraftApi.banListService().getIpBanEntries().stream().map(IpBan::from).collect(Collectors.toSet());
      currentBans.stream().filter((ban) -> !finalBanlist.contains(ban)).forEach((ban) -> minecraftApi.banListService().removeIpBan(ban.ip(), clientInfo));
      finalBanlist.stream().filter((ban) -> !currentBans.contains(ban)).forEach((ban) -> minecraftApi.banListService().addIpBan(ban.toIpBanEntry(), clientInfo));
      finalBanlist.stream().filter((ban) -> !currentBans.contains(ban)).flatMap((ban) -> minecraftApi.playerListService().getPlayersWithAddress(ban.ip()).stream()).forEach((player) -> player.connection.disconnect(Component.translatable("multiplayer.disconnect.ip_banned")));
      return get(minecraftApi);
   }

   public static record IncomingIpBanDto(Optional<PlayerDto> player, Optional<String> ip, Optional<String> reason, Optional<String> source, Optional<Instant> expires) {
      public static final MapCodec<IncomingIpBanDto> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(PlayerDto.CODEC.codec().optionalFieldOf("player").forGetter(IncomingIpBanDto::player), Codec.STRING.optionalFieldOf("ip").forGetter(IncomingIpBanDto::ip), Codec.STRING.optionalFieldOf("reason").forGetter(IncomingIpBanDto::reason), Codec.STRING.optionalFieldOf("source").forGetter(IncomingIpBanDto::source), ExtraCodecs.INSTANT_ISO8601.optionalFieldOf("expires").forGetter(IncomingIpBanDto::expires)).apply(i, IncomingIpBanDto::new));

      public IncomingIpBanDto {
         super();
      }

      private IpBan toIpBan(final ServerPlayer player) {
         return new IpBan(player.getIpAddress(), (String)this.reason().orElse((Object)null), (String)this.source().orElse("Management server"), this.expires());
      }

      private @Nullable IpBan toIpBan() {
         return !this.ip().isEmpty() && InetAddresses.isInetAddress((String)this.ip().get()) ? new IpBan((String)this.ip().get(), (String)this.reason().orElse((Object)null), (String)this.source().orElse("Management server"), this.expires()) : null;
      }
   }

   public static record IpBanDto(String ip, Optional<String> reason, Optional<String> source, Optional<Instant> expires) {
      public static final MapCodec<IpBanDto> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.STRING.fieldOf("ip").forGetter(IpBanDto::ip), Codec.STRING.optionalFieldOf("reason").forGetter(IpBanDto::reason), Codec.STRING.optionalFieldOf("source").forGetter(IpBanDto::source), ExtraCodecs.INSTANT_ISO8601.optionalFieldOf("expires").forGetter(IpBanDto::expires)).apply(i, IpBanDto::new));

      public IpBanDto {
         super();
      }

      private static IpBanDto from(final IpBan ban) {
         return new IpBanDto(ban.ip(), Optional.ofNullable(ban.reason()), Optional.of(ban.source()), ban.expires());
      }

      public static IpBanDto from(final IpBanListEntry ban) {
         return from(IpBanlistService.IpBan.from(ban));
      }

      private IpBan toIpBan() {
         return new IpBan(this.ip(), (String)this.reason().orElse((Object)null), (String)this.source().orElse("Management server"), this.expires());
      }
   }

   private static record IpBan(String ip, @Nullable String reason, String source, Optional<Instant> expires) {
      private IpBan {
         super();
      }

      private static IpBan from(final IpBanListEntry entry) {
         return new IpBan((String)Objects.requireNonNull((String)entry.getUser()), entry.getReason(), entry.getSource(), Optional.ofNullable(entry.getExpires()).map(Date::toInstant));
      }

      private IpBanListEntry toIpBanEntry() {
         return new IpBanListEntry(this.ip(), (Date)null, this.source(), (Date)this.expires().map(Date::from).orElse((Object)null), this.reason());
      }
   }
}
