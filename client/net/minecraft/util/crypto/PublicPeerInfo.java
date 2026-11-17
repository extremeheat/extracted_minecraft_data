package net.minecraft.util.crypto;

import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import javax.crypto.Mac;

public record PublicPeerInfo(PublicKey publicKey, String name) {
   public PublicPeerInfo {
      super();
   }

   public void addToDigest(final Mac mac) {
      mac.update(this.name.getBytes(StandardCharsets.UTF_8));
      mac.update((byte)43);
      mac.update(this.publicKey.getEncoded());
   }
}
