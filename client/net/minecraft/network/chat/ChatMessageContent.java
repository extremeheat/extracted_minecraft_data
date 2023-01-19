package net.minecraft.network.chat;

import java.util.Objects;
import net.minecraft.network.FriendlyByteBuf;

public record ChatMessageContent(String a, Component b) {
   private final String plain;
   private final Component decorated;

   public ChatMessageContent(String var1) {
      this(var1, Component.literal(var1));
   }

   public ChatMessageContent(String var1, Component var2) {
      super();
      this.plain = var1;
      this.decorated = var2;
   }

   public boolean isDecorated() {
      return !this.decorated.equals(Component.literal(this.plain));
   }

   public static ChatMessageContent read(FriendlyByteBuf var0) {
      String var1 = var0.readUtf(256);
      Component var2 = var0.readNullable(FriendlyByteBuf::readComponent);
      return new ChatMessageContent(var1, Objects.requireNonNullElse(var2, Component.literal(var1)));
   }

   public static void write(FriendlyByteBuf var0, ChatMessageContent var1) {
      var0.writeUtf(var1.plain(), 256);
      Component var2 = var1.isDecorated() ? var1.decorated() : null;
      var0.writeNullable(var2, FriendlyByteBuf::writeComponent);
   }
}
