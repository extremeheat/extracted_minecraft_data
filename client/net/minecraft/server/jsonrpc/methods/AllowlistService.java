package net.minecraft.server.jsonrpc.methods;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.Util;
import net.minecraft.server.jsonrpc.api.PlayerDto;
import net.minecraft.server.jsonrpc.internalapi.MinecraftApi;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.StoredUserEntry;
import net.minecraft.server.players.UserWhiteListEntry;

public class AllowlistService {
   public AllowlistService() {
      super();
   }

   public static List<PlayerDto> get(MinecraftApi var0) {
      return var0.allowListService().getEntries().stream().filter((var0x) -> var0x.getUser() != null).map((var0x) -> PlayerDto.from((NameAndId)var0x.getUser())).toList();
   }

   public static List<PlayerDto> add(MinecraftApi var0, List<PlayerDto> var1, ClientInfo var2) {
      List var3 = var1.stream().map((var1x) -> var0.playerListService().getUser(var1x.id(), var1x.name())).toList();

      for(Optional var5 : (List)Util.sequence(var3).join()) {
         var5.ifPresent((var2x) -> var0.allowListService().add(new UserWhiteListEntry(var2x), var2));
      }

      return get(var0);
   }

   public static List<PlayerDto> clear(MinecraftApi var0, ClientInfo var1) {
      var0.allowListService().clear(var1);
      return get(var0);
   }

   public static List<PlayerDto> remove(MinecraftApi var0, List<PlayerDto> var1, ClientInfo var2) {
      List var3 = var1.stream().map((var1x) -> var0.playerListService().getUser(var1x.id(), var1x.name())).toList();

      for(Optional var5 : (List)Util.sequence(var3).join()) {
         var5.ifPresent((var2x) -> var0.allowListService().remove(var2x, var2));
      }

      var0.allowListService().kickUnlistedPlayers(var2);
      return get(var0);
   }

   public static List<PlayerDto> set(MinecraftApi var0, List<PlayerDto> var1, ClientInfo var2) {
      List var3 = var1.stream().map((var1x) -> var0.playerListService().getUser(var1x.id(), var1x.name())).toList();
      Set var4 = (Set)((List)Util.sequence(var3).join()).stream().filter(Optional::isPresent).map(Optional::get).collect(Collectors.toSet());
      Set var5 = (Set)var0.allowListService().getEntries().stream().map(StoredUserEntry::getUser).collect(Collectors.toSet());
      var5.stream().filter((var1x) -> !var4.contains(var1x)).forEach((var2x) -> var0.allowListService().remove(var2x, var2));
      var4.stream().filter((var1x) -> !var5.contains(var1x)).forEach((var2x) -> var0.allowListService().add(new UserWhiteListEntry(var2x), var2));
      var0.allowListService().kickUnlistedPlayers(var2);
      return get(var0);
   }
}
