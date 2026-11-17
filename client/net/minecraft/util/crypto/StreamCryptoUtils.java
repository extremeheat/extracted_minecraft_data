package net.minecraft.util.crypto;

import java.security.Key;
import java.util.Arrays;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;

public class StreamCryptoUtils {
   public static final String STREAM_ALGORITHM = "AES/CFB8/NoPadding";
   public static final String STREAM_ALGORITHM_KEY_TYPE = "AES";
   public static final int STREAM_ALGORITHM_KEY_SIZE = 128;

   public StreamCryptoUtils() {
      super();
   }

   public static Cipher getCipher(final int opMode, final Key key, final IvParameterSpec iv) throws CryptException {
      try {
         Cipher cip = Cipher.getInstance("AES/CFB8/NoPadding");
         cip.init(opMode, key, iv);
         return cip;
      } catch (Exception e) {
         throw new CryptException(e);
      }
   }

   public static class IVGenerator {
      private final Random random;

      public IVGenerator(final byte[] seed) {
         super();
         this.random = new Random((long)Arrays.hashCode(seed));
      }

      public IvParameterSpec generate() {
         byte[] bytes = new byte[16];
         this.random.nextBytes(bytes);
         return new IvParameterSpec(bytes);
      }

      public IVPair generatePair() {
         return new IVPair(this.generate(), this.generate());
      }
   }

   public static record IVPair(IvParameterSpec serverToClient, IvParameterSpec clientToServer) {
      public IVPair {
         super();
      }
   }
}
