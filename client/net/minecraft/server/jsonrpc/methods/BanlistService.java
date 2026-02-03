package net.minecraft.server.jsonrpc.methods;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import net.minecraft.network.chat.Component;
import net.minecraft.server.jsonrpc.api.PlayerDto;
import net.minecraft.server.jsonrpc.internalapi.MinecraftApi;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.UserBanListEntry;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

public class BanlistService {
   private static final String BAN_SOURCE = "Management server";

   public BanlistService() {
      super();
   }

   public static List<UserBanDto> get(final MinecraftApi minecraftApi) {
      return minecraftApi.banListService().getUserBanEntries().stream().filter((p) -> p.getUser() != null).map(UserBan::from).map(UserBanDto::from).toList();
   }

   public static List<UserBanDto> add(final MinecraftApi minecraftApi, final List<UserBanDto> bans, final ClientInfo clientInfo) {
      List<CompletableFuture<Optional<UserBan>>> fetch = bans.stream().map((banx) -> minecraftApi.playerListService().getUser(banx.player().id(), banx.player().name()).thenApply((u) -> {
            Objects.requireNonNull(banx);
            return u.map(banx::toUserBan);
         })).toList();

      for(Optional<UserBan> ban : (List)Util.sequence(fetch).join()) {
         if (!ban.isEmpty()) {
            UserBan userBan = (UserBan)ban.get();
            minecraftApi.banListService().addUserBan(userBan.toBanEntry(), clientInfo);
            ServerPlayer player = minecraftApi.playerListService().getPlayer(((UserBan)ban.get()).player().id());
            if (player != null) {
               player.connection.disconnect(Component.translatable("multiplayer.disconnect.banned"));
            }
         }
      }

      return get(minecraftApi);
   }

   public static List<UserBanDto> clear(final MinecraftApi minecraftApi, final ClientInfo clientInfo) {
      minecraftApi.banListService().clearUserBans(clientInfo);
      return get(minecraftApi);
   }

   public static List<UserBanDto> remove(final MinecraftApi minecraftApi, final List<PlayerDto> remove, final ClientInfo clientInfo) {
      List<CompletableFuture<Optional<NameAndId>>> fetch = remove.stream().map((playerDto) -> minecraftApi.playerListService().getUser(playerDto.id(), playerDto.name())).toList();

      for(Optional<NameAndId> user : (List)Util.sequence(fetch).join()) {
         if (!user.isEmpty()) {
            minecraftApi.banListService().removeUserBan((NameAndId)user.get(), clientInfo);
         }
      }

      return get(minecraftApi);
   }

   public static List<UserBanDto> set(final MinecraftApi minecraftApi, final List<UserBanDto> bans, final ClientInfo clientInfo) {
      List<CompletableFuture<Optional<UserBan>>> fetch = bans.stream().map((ban) -> minecraftApi.playerListService().getUser(ban.player().id(), ban.player().name()).thenApply((u) -> {
            Objects.requireNonNull(ban);
            return u.map(ban::toUserBan);
         })).toList();
      Set<UserBan> finalAllowList = (Set)((List)Util.sequence(fetch).join()).stream().flatMap(Optional::stream).collect(Collectors.toSet());
      Set<UserBan> currentAllowList = (Set)minecraftApi.banListService().getUserBanEntries().stream().filter((entry) -> entry.getUser() != null).map(UserBan::from).collect(Collectors.toSet());
      currentAllowList.stream().filter((ban) -> !finalAllowList.contains(ban)).forEach((ban) -> minecraftApi.banListService().removeUserBan(ban.player(), clientInfo));
      finalAllowList.stream().filter((ban) -> !currentAllowList.contains(ban)).forEach((ban) -> {
         minecraftApi.banListService().addUserBan(ban.toBanEntry(), clientInfo);
         ServerPlayer player = minecraftApi.playerListService().getPlayer(ban.player().id());
         if (player != null) {
            player.connection.disconnect(Component.translatable("multiplayer.disconnect.banned"));
         }

      });
      return get(minecraftApi);
   }

   public static record UserBanDto(PlayerDto player, Optional<String> reason, Optional<String> source, Optional<Instant> expires) {
      public static final MapCodec<UserBanDto> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(PlayerDto.CODEC.codec().fieldOf("player").forGetter(UserBanDto::player), Codec.STRING.optionalFieldOf("reason").forGetter(UserBanDto::reason), Codec.STRING.optionalFieldOf("source").forGetter(UserBanDto::source), ExtraCodecs.INSTANT_ISO8601.optionalFieldOf("expires").forGetter(UserBanDto::expires)).apply(i, UserBanDto::new));

      public UserBanDto {
         super();
      }

      private static UserBanDto from(final UserBan ban) {
         return new UserBanDto(PlayerDto.from(ban.player()), Optional.ofNullable(ban.reason()), Optional.of(ban.source()), ban.expires());
      }

      public static UserBanDto from(final UserBanListEntry entry) {
         return from(BanlistService.UserBan.from(entry));
      }

      private UserBan toUserBan(final NameAndId nameAndId) {
         return new UserBan(nameAndId, (String)this.reason().orElse((Object)null), (String)this.source().orElse("Management server"), this.expires());
      }
   }

   private static record UserBan(NameAndId player, @Nullable String reason, String source, Optional<Instant> expires) {
      private UserBan {
         super();
      }

      private static UserBan from(final UserBanListEntry entry) {
         return new UserBan((NameAndId)Objects.requireNonNull((NameAndId)entry.getUser()), entry.getReason(), entry.getSource(), Optional.ofNullable(entry.getExpires()).map(Date::toInstant));
      }

      private UserBanListEntry toBanEntry() {
         return new UserBanListEntry(new NameAndId(this.player().id(), this.player().name()), (Date)null, this.source(), (Date)this.expires().map(Date::from).orElse((Object)null), this.reason());
      }
   }
}
