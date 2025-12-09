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

   public static List<IpBanDto> get(MinecraftApi var0) {
      return var0.banListService().getIpBanEntries().stream().map(IpBan::from).map(IpBanDto::from).toList();
   }

   public static List<IpBanDto> add(MinecraftApi var0, List<IncomingIpBanDto> var1, ClientInfo var2) {
      var1.stream().map((var2x) -> banIp(var0, var2x, var2)).flatMap(Collection::stream).forEach((var0x) -> var0x.connection.disconnect(Component.translatable("multiplayer.disconnect.ip_banned")));
      return get(var0);
   }

   private static List<ServerPlayer> banIp(MinecraftApi var0, IncomingIpBanDto var1, ClientInfo var2) {
      IpBan var3 = var1.toIpBan();
      if (var3 != null) {
         return banIp(var0, var3, var2);
      } else {
         if (var1.player().isPresent()) {
            Optional var4 = var0.playerListService().getPlayer(((PlayerDto)var1.player().get()).id(), ((PlayerDto)var1.player().get()).name());
            if (var4.isPresent()) {
               return banIp(var0, var1.toIpBan((ServerPlayer)var4.get()), var2);
            }
         }

         return List.of();
      }
   }

   private static List<ServerPlayer> banIp(MinecraftApi var0, IpBan var1, ClientInfo var2) {
      var0.banListService().addIpBan(var1.toIpBanEntry(), var2);
      return var0.playerListService().getPlayersWithAddress(var1.ip());
   }

   public static List<IpBanDto> clear(MinecraftApi var0, ClientInfo var1) {
      var0.banListService().clearIpBans(var1);
      return get(var0);
   }

   public static List<IpBanDto> remove(MinecraftApi var0, List<String> var1, ClientInfo var2) {
      var1.forEach((var2x) -> var0.banListService().removeIpBan(var2x, var2));
      return get(var0);
   }

   public static List<IpBanDto> set(MinecraftApi var0, List<IpBanDto> var1, ClientInfo var2) {
      Set var3 = (Set)var1.stream().filter((var0x) -> InetAddresses.isInetAddress(var0x.ip())).map(IpBanDto::toIpBan).collect(Collectors.toSet());
      Set var4 = (Set)var0.banListService().getIpBanEntries().stream().map(IpBan::from).collect(Collectors.toSet());
      var4.stream().filter((var1x) -> !var3.contains(var1x)).forEach((var2x) -> var0.banListService().removeIpBan(var2x.ip(), var2));
      var3.stream().filter((var1x) -> !var4.contains(var1x)).forEach((var2x) -> var0.banListService().addIpBan(var2x.toIpBanEntry(), var2));
      var3.stream().filter((var1x) -> !var4.contains(var1x)).flatMap((var1x) -> var0.playerListService().getPlayersWithAddress(var1x.ip()).stream()).forEach((var0x) -> var0x.connection.disconnect(Component.translatable("multiplayer.disconnect.ip_banned")));
      return get(var0);
   }

   public static record IncomingIpBanDto(Optional<PlayerDto> player, Optional<String> ip, Optional<String> reason, Optional<String> source, Optional<Instant> expires) {
      public static final MapCodec<IncomingIpBanDto> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(PlayerDto.CODEC.codec().optionalFieldOf("player").forGetter(IncomingIpBanDto::player), Codec.STRING.optionalFieldOf("ip").forGetter(IncomingIpBanDto::ip), Codec.STRING.optionalFieldOf("reason").forGetter(IncomingIpBanDto::reason), Codec.STRING.optionalFieldOf("source").forGetter(IncomingIpBanDto::source), ExtraCodecs.INSTANT_ISO8601.optionalFieldOf("expires").forGetter(IncomingIpBanDto::expires)).apply(var0, IncomingIpBanDto::new));

      public IncomingIpBanDto(Optional<PlayerDto> var1, Optional<String> var2, Optional<String> var3, Optional<String> var4, Optional<Instant> var5) {
         super();
         this.player = var1;
         this.ip = var2;
         this.reason = var3;
         this.source = var4;
         this.expires = var5;
      }

      IpBan toIpBan(ServerPlayer var1) {
         return new IpBan(var1.getIpAddress(), (String)this.reason().orElse((Object)null), (String)this.source().orElse("Management server"), this.expires());
      }

      @Nullable IpBan toIpBan() {
         return !this.ip().isEmpty() && InetAddresses.isInetAddress((String)this.ip().get()) ? new IpBan((String)this.ip().get(), (String)this.reason().orElse((Object)null), (String)this.source().orElse("Management server"), this.expires()) : null;
      }
   }

   public static record IpBanDto(String ip, Optional<String> reason, Optional<String> source, Optional<Instant> expires) {
      public static final MapCodec<IpBanDto> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Codec.STRING.fieldOf("ip").forGetter(IpBanDto::ip), Codec.STRING.optionalFieldOf("reason").forGetter(IpBanDto::reason), Codec.STRING.optionalFieldOf("source").forGetter(IpBanDto::source), ExtraCodecs.INSTANT_ISO8601.optionalFieldOf("expires").forGetter(IpBanDto::expires)).apply(var0, IpBanDto::new));

      public IpBanDto(String var1, Optional<String> var2, Optional<String> var3, Optional<Instant> var4) {
         super();
         this.ip = var1;
         this.reason = var2;
         this.source = var3;
         this.expires = var4;
      }

      private static IpBanDto from(IpBan var0) {
         return new IpBanDto(var0.ip(), Optional.ofNullable(var0.reason()), Optional.of(var0.source()), var0.expires());
      }

      public static IpBanDto from(IpBanListEntry var0) {
         return from(IpBanlistService.IpBan.from(var0));
      }

      private IpBan toIpBan() {
         return new IpBan(this.ip(), (String)this.reason().orElse((Object)null), (String)this.source().orElse("Management server"), this.expires());
      }
   }

   static record IpBan(String ip, @Nullable String reason, String source, Optional<Instant> expires) {
      IpBan(String var1, @Nullable String var2, String var3, Optional<Instant> var4) {
         super();
         this.ip = var1;
         this.reason = var2;
         this.source = var3;
         this.expires = var4;
      }

      static IpBan from(IpBanListEntry var0) {
         return new IpBan((String)Objects.requireNonNull((String)var0.getUser()), var0.getReason(), var0.getSource(), Optional.ofNullable(var0.getExpires()).map(Date::toInstant));
      }

      IpBanListEntry toIpBanEntry() {
         return new IpBanListEntry(this.ip(), (Date)null, this.source(), (Date)this.expires().map(Date::from).orElse((Object)null), this.reason());
      }
   }
}
