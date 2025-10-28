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
import java.util.stream.Collectors;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.server.jsonrpc.api.PlayerDto;
import net.minecraft.server.jsonrpc.internalapi.MinecraftApi;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.UserBanListEntry;
import net.minecraft.util.ExtraCodecs;
import org.jspecify.annotations.Nullable;

public class BanlistService {
   private static final String BAN_SOURCE = "Management server";

   public BanlistService() {
      super();
   }

   public static List<UserBanDto> get(MinecraftApi var0) {
      return var0.banListService().getUserBanEntries().stream().filter((var0x) -> var0x.getUser() != null).map(UserBan::from).map(UserBanDto::from).toList();
   }

   public static List<UserBanDto> add(MinecraftApi var0, List<UserBanDto> var1, ClientInfo var2) {
      List var3 = var1.stream().map((var1x) -> var0.playerListService().getUser(var1x.player().id(), var1x.player().name()).thenApply((var1) -> {
            Objects.requireNonNull(var1x);
            return var1.map(var1x::toUserBan);
         })).toList();

      for(Optional var5 : (List)Util.sequence(var3).join()) {
         if (!var5.isEmpty()) {
            UserBan var6 = (UserBan)var5.get();
            var0.banListService().addUserBan(var6.toBanEntry(), var2);
            ServerPlayer var7 = var0.playerListService().getPlayer(((UserBan)var5.get()).player().id());
            if (var7 != null) {
               var7.connection.disconnect(Component.translatable("multiplayer.disconnect.banned"));
            }
         }
      }

      return get(var0);
   }

   public static List<UserBanDto> clear(MinecraftApi var0, ClientInfo var1) {
      var0.banListService().clearUserBans(var1);
      return get(var0);
   }

   public static List<UserBanDto> remove(MinecraftApi var0, List<PlayerDto> var1, ClientInfo var2) {
      List var3 = var1.stream().map((var1x) -> var0.playerListService().getUser(var1x.id(), var1x.name())).toList();

      for(Optional var5 : (List)Util.sequence(var3).join()) {
         if (!var5.isEmpty()) {
            var0.banListService().removeUserBan((NameAndId)var5.get(), var2);
         }
      }

      return get(var0);
   }

   public static List<UserBanDto> set(MinecraftApi var0, List<UserBanDto> var1, ClientInfo var2) {
      List var3 = var1.stream().map((var1x) -> var0.playerListService().getUser(var1x.player().id(), var1x.player().name()).thenApply((var1) -> {
            Objects.requireNonNull(var1x);
            return var1.map(var1x::toUserBan);
         })).toList();
      Set var4 = (Set)((List)Util.sequence(var3).join()).stream().flatMap(Optional::stream).collect(Collectors.toSet());
      Set var5 = (Set)var0.banListService().getUserBanEntries().stream().filter((var0x) -> var0x.getUser() != null).map(UserBan::from).collect(Collectors.toSet());
      var5.stream().filter((var1x) -> !var4.contains(var1x)).forEach((var2x) -> var0.banListService().removeUserBan(var2x.player(), var2));
      var4.stream().filter((var1x) -> !var5.contains(var1x)).forEach((var2x) -> {
         var0.banListService().addUserBan(var2x.toBanEntry(), var2);
         ServerPlayer var3 = var0.playerListService().getPlayer(var2x.player().id());
         if (var3 != null) {
            var3.connection.disconnect(Component.translatable("multiplayer.disconnect.banned"));
         }

      });
      return get(var0);
   }

   public static record UserBanDto(PlayerDto player, Optional<String> reason, Optional<String> source, Optional<Instant> expires) {
      public static final MapCodec<UserBanDto> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(PlayerDto.CODEC.codec().fieldOf("player").forGetter(UserBanDto::player), Codec.STRING.optionalFieldOf("reason").forGetter(UserBanDto::reason), Codec.STRING.optionalFieldOf("source").forGetter(UserBanDto::source), ExtraCodecs.INSTANT_ISO8601.optionalFieldOf("expires").forGetter(UserBanDto::expires)).apply(var0, UserBanDto::new));

      public UserBanDto(PlayerDto var1, Optional<String> var2, Optional<String> var3, Optional<Instant> var4) {
         super();
         this.player = var1;
         this.reason = var2;
         this.source = var3;
         this.expires = var4;
      }

      private static UserBanDto from(UserBan var0) {
         return new UserBanDto(PlayerDto.from(var0.player()), Optional.ofNullable(var0.reason()), Optional.of(var0.source()), var0.expires());
      }

      public static UserBanDto from(UserBanListEntry var0) {
         return from(BanlistService.UserBan.from(var0));
      }

      private UserBan toUserBan(NameAndId var1) {
         return new UserBan(var1, (String)this.reason().orElse((Object)null), (String)this.source().orElse("Management server"), this.expires());
      }
   }

   static record UserBan(NameAndId player, @Nullable String reason, String source, Optional<Instant> expires) {
      UserBan(NameAndId var1, @Nullable String var2, String var3, Optional<Instant> var4) {
         super();
         this.player = var1;
         this.reason = var2;
         this.source = var3;
         this.expires = var4;
      }

      static UserBan from(UserBanListEntry var0) {
         return new UserBan((NameAndId)Objects.requireNonNull((NameAndId)var0.getUser()), var0.getReason(), var0.getSource(), Optional.ofNullable(var0.getExpires()).map(Date::toInstant));
      }

      UserBanListEntry toBanEntry() {
         return new UserBanListEntry(new NameAndId(this.player().id(), this.player().name()), (Date)null, this.source(), (Date)this.expires().map(Date::from).orElse((Object)null), this.reason());
      }
   }
}
