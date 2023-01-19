package net.minecraft.network.chat;

import com.google.common.primitives.Ints;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.security.SignatureException;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.UUIDUtil;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.SignatureUpdater;

public record SignedMessageLink(int b, UUID c, UUID d) {
   private final int index;
   private final UUID sender;
   private final UUID sessionId;
   public static final Codec<SignedMessageLink> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               ExtraCodecs.NON_NEGATIVE_INT.fieldOf("index").forGetter(SignedMessageLink::index),
               UUIDUtil.CODEC.fieldOf("sender").forGetter(SignedMessageLink::sender),
               UUIDUtil.CODEC.fieldOf("session_id").forGetter(SignedMessageLink::sessionId)
            )
            .apply(var0, SignedMessageLink::new)
   );

   public SignedMessageLink(int var1, UUID var2, UUID var3) {
      super();
      this.index = var1;
      this.sender = var2;
      this.sessionId = var3;
   }

   public static SignedMessageLink unsigned(UUID var0) {
      return root(var0, Util.NIL_UUID);
   }

   public static SignedMessageLink root(UUID var0, UUID var1) {
      return new SignedMessageLink(0, var0, var1);
   }

   public void updateSignature(SignatureUpdater.Output var1) throws SignatureException {
      var1.update(UUIDUtil.uuidToByteArray(this.sender));
      var1.update(UUIDUtil.uuidToByteArray(this.sessionId));
      var1.update(Ints.toByteArray(this.index));
   }

   public boolean isDescendantOf(SignedMessageLink var1) {
      return this.index > var1.index() && this.sender.equals(var1.sender()) && this.sessionId.equals(var1.sessionId());
   }

   @Nullable
   public SignedMessageLink advance() {
      return this.index == 2147483647 ? null : new SignedMessageLink(this.index + 1, this.sender, this.sessionId);
   }
}
