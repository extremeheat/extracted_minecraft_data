package net.minecraft.server.jsonrpc.api;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;

public record PlayerDto(Optional<UUID> id, Optional<String> name) {
   public static final MapCodec<PlayerDto> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(UUIDUtil.STRING_CODEC.optionalFieldOf("id").forGetter(PlayerDto::id), Codec.STRING.optionalFieldOf("name").forGetter(PlayerDto::name)).apply(var0, PlayerDto::new));

   public PlayerDto(Optional<UUID> var1, Optional<String> var2) {
      super();
      this.id = var1;
      this.name = var2;
   }

   public static PlayerDto from(GameProfile var0) {
      return new PlayerDto(Optional.of(var0.id()), Optional.of(var0.name()));
   }

   public static PlayerDto from(NameAndId var0) {
      return new PlayerDto(Optional.of(var0.id()), Optional.of(var0.name()));
   }

   public static PlayerDto from(ServerPlayer var0) {
      GameProfile var1 = var0.getGameProfile();
      return from(var1);
   }
}
