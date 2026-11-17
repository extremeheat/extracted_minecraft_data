package net.minecraft.util.crypto;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import javax.crypto.Mac;

public record SessionCommonInfo(int protocolVersion, long salt, boolean authenticationRequested) {
   public SessionCommonInfo {
      super();
   }

   public void addToDigest(final Mac mac) {
      mac.update(Ints.toByteArray(this.protocolVersion));
      mac.update((byte)43);
      mac.update(Longs.toByteArray(this.salt));
      mac.update((byte)43);
      mac.update((byte)(this.authenticationRequested ? 1 : 2));
   }
}
