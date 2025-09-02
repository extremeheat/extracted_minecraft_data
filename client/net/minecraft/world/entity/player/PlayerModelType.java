package net.minecraft.world.entity.player;

import io.netty.buffer.ByteBuf;
import javax.annotation.Nullable;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

public enum PlayerModelType implements StringRepresentable {
   SLIM("slim"),
   WIDE("default");

   public static final StringRepresentable.EnumCodec<PlayerModelType> CODEC = StringRepresentable.<PlayerModelType>fromEnum(PlayerModelType::values);
   public static final StreamCodec<ByteBuf, PlayerModelType> STREAM_CODEC = ByteBufCodecs.BOOL.map((var0) -> var0 ? SLIM : WIDE, (var0) -> var0 == SLIM);
   private final String id;

   private PlayerModelType(final String var3) {
      this.id = var3;
   }

   public static PlayerModelType byName(@Nullable String var0) {
      return (PlayerModelType)CODEC.byName(var0, WIDE);
   }

   public String getSerializedName() {
      return this.id;
   }

   // $FF: synthetic method
   private static PlayerModelType[] $values() {
      return new PlayerModelType[]{SLIM, WIDE};
   }
}
