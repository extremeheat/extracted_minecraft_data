package net.minecraft.server.jsonrpc.methods;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.jsonrpc.api.PlayerDto;
import net.minecraft.server.jsonrpc.internalapi.MinecraftApi;
import net.minecraft.server.level.ServerPlayer;

public class PlayerService {
   private static final MutableComponent DEFAULT_KICK_MESSAGE = Component.translatable("multiplayer.disconnect.kicked");

   public PlayerService() {
      super();
   }

   public static List<PlayerDto> get(MinecraftApi var0) {
      return var0.playerListService().getPlayers().stream().map(PlayerDto::from).toList();
   }

   public static List<PlayerDto> kick(MinecraftApi var0, KickDto var1, ClientInfo var2) {
      ArrayList var3 = new ArrayList();

      for(PlayerDto var5 : var1.players()) {
         ServerPlayer var6 = getServerPlayer(var0, var5);
         if (var6 != null) {
            var0.playerListService().remove(var6, var2);
            Object var7;
            if (var1.message().isPresent() && (((Message)var1.message().get()).literal().isPresent() || ((Message)var1.message().get()).translatable().isPresent())) {
               Message var8 = (Message)var1.message().get();
               if (var8.translatable().isPresent()) {
                  if (var8.translatableParams().isPresent() && !((List)var8.translatableParams().get()).isEmpty()) {
                     var7 = Component.translatable((String)var8.translatable().get(), ((List)var8.translatableParams().get()).toArray());
                  } else {
                     var7 = Component.translatable((String)var8.translatable().get());
                  }
               } else {
                  var7 = (Component)var8.literal().map(Component::literal).orElse(DEFAULT_KICK_MESSAGE);
               }
            } else {
               var7 = DEFAULT_KICK_MESSAGE;
            }

            var6.connection.disconnect((Component)var7);
            var3.add(var5);
         }
      }

      return var3;
   }

   @Nullable
   private static ServerPlayer getServerPlayer(MinecraftApi var0, PlayerDto var1) {
      if (var1.id().isPresent()) {
         return var0.playerListService().getPlayer((UUID)var1.id().get());
      } else {
         return var1.name().isPresent() ? var0.playerListService().getPlayerByName((String)var1.name().get()) : null;
      }
   }

   public static record KickDto(List<PlayerDto> players, Optional<Message> message) {
      public static final Codec<KickDto> CODEC = RecordCodecBuilder.create((var0) -> var0.group(PlayerDto.CODEC.codec().listOf().fieldOf("players").forGetter(KickDto::players), Message.CODEC.optionalFieldOf("message").forGetter(KickDto::message)).apply(var0, KickDto::new));

      public KickDto(List<PlayerDto> var1, Optional<Message> var2) {
         super();
         this.players = var1;
         this.message = var2;
      }
   }
}
