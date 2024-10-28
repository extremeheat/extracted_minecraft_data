package net.minecraft.client.multiplayer.chat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import net.minecraft.util.StringRepresentable;

public interface LoggedChatEvent {
   Codec<LoggedChatEvent> CODEC = StringRepresentable.fromEnum(Type::values).dispatch(LoggedChatEvent::type, Type::codec);

   Type type();

   public static enum Type implements StringRepresentable {
      PLAYER("player", () -> {
         return LoggedChatMessage.Player.CODEC;
      }),
      SYSTEM("system", () -> {
         return LoggedChatMessage.System.CODEC;
      });

      private final String serializedName;
      private final Supplier<MapCodec<? extends LoggedChatEvent>> codec;

      private Type(final String var3, final Supplier var4) {
         this.serializedName = var3;
         this.codec = var4;
      }

      private MapCodec<? extends LoggedChatEvent> codec() {
         return (MapCodec)this.codec.get();
      }

      public String getSerializedName() {
         return this.serializedName;
      }

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{PLAYER, SYSTEM};
      }
   }
}
