package net.minecraft.server.jsonrpc.methods;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.server.jsonrpc.api.PlayerDto;
import net.minecraft.server.jsonrpc.internalapi.MinecraftApi;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.ServerOpListEntry;
import net.minecraft.util.Util;

public class OperatorService {
   public OperatorService() {
      super();
   }

   public static List<OperatorDto> get(MinecraftApi var0) {
      return var0.operatorListService().getEntries().stream().filter((var0x) -> var0x.getUser() != null).map(OperatorDto::from).toList();
   }

   public static List<OperatorDto> clear(MinecraftApi var0, ClientInfo var1) {
      var0.operatorListService().clear(var1);
      return get(var0);
   }

   public static List<OperatorDto> remove(MinecraftApi var0, List<PlayerDto> var1, ClientInfo var2) {
      List var3 = var1.stream().map((var1x) -> var0.playerListService().getUser(var1x.id(), var1x.name())).toList();

      for(Optional var5 : (List)Util.sequence(var3).join()) {
         var5.ifPresent((var2x) -> var0.operatorListService().deop(var2x, var2));
      }

      return get(var0);
   }

   public static List<OperatorDto> add(MinecraftApi var0, List<OperatorDto> var1, ClientInfo var2) {
      List var3 = var1.stream().map((var1x) -> var0.playerListService().getUser(var1x.player().id(), var1x.player().name()).thenApply((var1) -> var1.map((var1xx) -> new Op(var1xx, var1x.permissionLevel(), var1x.bypassesPlayerLimit())))).toList();

      for(Optional var5 : (List)Util.sequence(var3).join()) {
         var5.ifPresent((var2x) -> var0.operatorListService().op(var2x.user(), var2x.permissionLevel(), var2x.bypassesPlayerLimit(), var2));
      }

      return get(var0);
   }

   public static List<OperatorDto> set(MinecraftApi var0, List<OperatorDto> var1, ClientInfo var2) {
      List var3 = var1.stream().map((var1x) -> var0.playerListService().getUser(var1x.player().id(), var1x.player().name()).thenApply((var1) -> var1.map((var1xx) -> new Op(var1xx, var1x.permissionLevel(), var1x.bypassesPlayerLimit())))).toList();
      Set var4 = (Set)((List)Util.sequence(var3).join()).stream().flatMap(Optional::stream).collect(Collectors.toSet());
      Set var5 = (Set)var0.operatorListService().getEntries().stream().filter((var0x) -> var0x.getUser() != null).map((var0x) -> new Op((NameAndId)var0x.getUser(), Optional.of(var0x.permissions().level()), Optional.of(var0x.getBypassesPlayerLimit()))).collect(Collectors.toSet());
      var5.stream().filter((var1x) -> !var4.contains(var1x)).forEach((var2x) -> var0.operatorListService().deop(var2x.user(), var2));
      var4.stream().filter((var1x) -> !var5.contains(var1x)).forEach((var2x) -> var0.operatorListService().op(var2x.user(), var2x.permissionLevel(), var2x.bypassesPlayerLimit(), var2));
      return get(var0);
   }

   public static record OperatorDto(PlayerDto player, Optional<PermissionLevel> permissionLevel, Optional<Boolean> bypassesPlayerLimit) {
      public static final MapCodec<OperatorDto> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(PlayerDto.CODEC.codec().fieldOf("player").forGetter(OperatorDto::player), PermissionLevel.INT_CODEC.optionalFieldOf("permissionLevel").forGetter(OperatorDto::permissionLevel), Codec.BOOL.optionalFieldOf("bypassesPlayerLimit").forGetter(OperatorDto::bypassesPlayerLimit)).apply(var0, OperatorDto::new));

      public OperatorDto(PlayerDto var1, Optional<PermissionLevel> var2, Optional<Boolean> var3) {
         super();
         this.player = var1;
         this.permissionLevel = var2;
         this.bypassesPlayerLimit = var3;
      }

      public static OperatorDto from(ServerOpListEntry var0) {
         return new OperatorDto(PlayerDto.from((NameAndId)Objects.requireNonNull((NameAndId)var0.getUser())), Optional.of(var0.permissions().level()), Optional.of(var0.getBypassesPlayerLimit()));
      }
   }

   static record Op(NameAndId user, Optional<PermissionLevel> permissionLevel, Optional<Boolean> bypassesPlayerLimit) {
      Op(NameAndId var1, Optional<PermissionLevel> var2, Optional<Boolean> var3) {
         super();
         this.user = var1;
         this.permissionLevel = var2;
         this.bypassesPlayerLimit = var3;
      }
   }
}
