package net.minecraft.network.chat;

import java.security.SignatureException;
import java.time.Instant;
import java.util.UUID;
import net.minecraft.util.Crypt;
import net.minecraft.util.Signer;

public record MessageSigner(UUID a, Instant b, long c) {
   private final UUID sender;
   private final Instant timeStamp;
   private final long salt;

   public MessageSigner(UUID var1, Instant var2, long var3) {
      super();
      this.sender = var1;
      this.timeStamp = var2;
      this.salt = var3;
   }

   public static MessageSigner create(UUID var0) {
      return new MessageSigner(var0, Instant.now(), Crypt.SaltSupplier.getLong());
   }

   public MessageSignature sign(Signer var1, Component var2) {
      byte[] var3 = var1.sign(var2x -> MessageSignature.updateSignature(var2x, var2, this.sender, this.timeStamp, this.salt));
      return new MessageSignature(this.sender, this.timeStamp, new Crypt.SaltSignaturePair(this.salt, var3));
   }

   public MessageSignature sign(Signer var1, String var2) throws SignatureException {
      return this.sign(var1, Component.literal(var2));
   }
}
