package net.minecraft.util.crypto;

import java.security.GeneralSecurityException;
import java.util.Arrays;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import org.apache.commons.codec.binary.Base32;

public record SessionKeys(SessionCommonInfo sessionCommonInfo, PublicPeerInfo peerInfoA, PublicPeerInfo peerInfoB, SecretKey streamAToB, SecretKey streamBToA, SecretKey authSignature, SecretKey integritySignature) {
   private static final Base32 BASE_32_HEX = Base32.builder().setHexEncodeTable(true).get();
   public static final byte DIGEST_SEPARATOR = 43;

   public SessionKeys {
      super();
   }

   public SecretKey inboundStreamKey(final SessionKeyExchange.Side ownSide) {
      SecretKey var10000;
      switch (ownSide) {
         case A -> var10000 = this.streamBToA();
         case B -> var10000 = this.streamAToB();
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public SecretKey outboundStreamKey(final SessionKeyExchange.Side ownSide) {
      SecretKey var10000;
      switch (ownSide) {
         case A -> var10000 = this.streamAToB();
         case B -> var10000 = this.streamBToA();
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public PublicPeerInfo peerInfo(final SessionKeyExchange.Side ownSide) {
      PublicPeerInfo var10000;
      switch (ownSide) {
         case A -> var10000 = this.peerInfoA;
         case B -> var10000 = this.peerInfoB;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public String computeKeysAuthenticationDigest() {
      try {
         Mac mac = Mac.getInstance("HmacSHA256");
         mac.init(this.authSignature);
         mac.update(this.peerInfoA.publicKey().getEncoded());
         mac.update((byte)43);
         mac.update(this.peerInfoB.publicKey().getEncoded());
         byte[] authDigest = mac.doFinal();
         return BASE_32_HEX.encodeToString(Arrays.copyOf(authDigest, 25));
      } catch (Exception e) {
         throw new UnsupportedOperationException(e);
      }
   }

   public byte[] computeIntegrityDigest() {
      try {
         Mac mac = Mac.getInstance("HmacSHA256");
         mac.init(this.integritySignature);
         this.sessionCommonInfo.addToDigest(mac);
         mac.update((byte)43);
         this.peerInfoA.addToDigest(mac);
         mac.update((byte)43);
         this.peerInfoB.addToDigest(mac);
         return mac.doFinal();
      } catch (GeneralSecurityException e) {
         throw new UnsupportedOperationException(e);
      }
   }
}
