package net.minecraft.network.protocol.login.custom;

import net.minecraft.network.FriendlyByteBuf;

public record DiscardedQueryAnswerPayload() implements CustomQueryAnswerPayload {
   public static final DiscardedQueryAnswerPayload INSTANCE = new DiscardedQueryAnswerPayload();

   public DiscardedQueryAnswerPayload() {
      super();
   }

   @Override
   public void write(FriendlyByteBuf var1) {
   }
}
