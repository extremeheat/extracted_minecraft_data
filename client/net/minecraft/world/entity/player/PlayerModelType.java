package net.minecraft.world.entity.player;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.Nullable;

public enum PlayerModelType implements StringRepresentable {
   SLIM("slim", "slim"),
   WIDE("wide", "default");

   public static final Codec<PlayerModelType> CODEC = StringRepresentable.<PlayerModelType>fromEnum(PlayerModelType::values);
   private static final Function<String, PlayerModelType> NAME_LOOKUP = StringRepresentable.createNameLookup(values(), (var0) -> var0.legacyServicesId);
   public static final StreamCodec<ByteBuf, PlayerModelType> STREAM_CODEC = ByteBufCodecs.BOOL.map((var0) -> var0 ? SLIM : WIDE, (var0) -> var0 == SLIM);
   private final String id;
   private final String legacyServicesId;

   private PlayerModelType(final String var3, final String var4) {
      this.id = var3;
      this.legacyServicesId = var4;
   }

   public static PlayerModelType byLegacyServicesName(@Nullable String var0) {
      return (PlayerModelType)Objects.requireNonNullElse((PlayerModelType)NAME_LOOKUP.apply(var0), WIDE);
   }

   public String getSerializedName() {
      return this.id;
   }

   // $FF: synthetic method
   private static PlayerModelType[] $values() {
      return new PlayerModelType[]{SLIM, WIDE};
   }
}
