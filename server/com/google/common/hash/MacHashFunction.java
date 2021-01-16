package com.google.common.hash;

import com.google.common.base.Preconditions;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;

final class MacHashFunction extends AbstractStreamingHashFunction {
   private final Mac prototype;
   private final Key key;
   private final String toString;
   private final int bits;
   private final boolean supportsClone;

   MacHashFunction(String var1, Key var2, String var3) {
      super();
      this.prototype = getMac(var1, var2);
      this.key = (Key)Preconditions.checkNotNull(var2);
      this.toString = (String)Preconditions.checkNotNull(var3);
      this.bits = this.prototype.getMacLength() * 8;
      this.supportsClone = supportsClone(this.prototype);
   }

   public int bits() {
      return this.bits;
   }

   private static boolean supportsClone(Mac var0) {
      try {
         var0.clone();
         return true;
      } catch (CloneNotSupportedException var2) {
         return false;
      }
   }

   private static Mac getMac(String var0, Key var1) {
      try {
         Mac var2 = Mac.getInstance(var0);
         var2.init(var1);
         return var2;
      } catch (NoSuchAlgorithmException var3) {
         throw new IllegalStateException(var3);
      } catch (InvalidKeyException var4) {
         throw new IllegalArgumentException(var4);
      }
   }

   public Hasher newHasher() {
      if (this.supportsClone) {
         try {
            return new MacHashFunction.MacHasher((Mac)this.prototype.clone());
         } catch (CloneNotSupportedException var2) {
         }
      }

      return new MacHashFunction.MacHasher(getMac(this.prototype.getAlgorithm(), this.key));
   }

   public String toString() {
      return this.toString;
   }

   private static final class MacHasher extends AbstractByteHasher {
      private final Mac mac;
      private boolean done;

      private MacHasher(Mac var1) {
         super();
         this.mac = var1;
      }

      protected void update(byte var1) {
         this.checkNotDone();
         this.mac.update(var1);
      }

      protected void update(byte[] var1) {
         this.checkNotDone();
         this.mac.update(var1);
      }

      protected void update(byte[] var1, int var2, int var3) {
         this.checkNotDone();
         this.mac.update(var1, var2, var3);
      }

      private void checkNotDone() {
         Preconditions.checkState(!this.done, "Cannot re-use a Hasher after calling hash() on it");
      }

      public HashCode hash() {
         this.checkNotDone();
         this.done = true;
         return HashCode.fromBytesNoCopy(this.mac.doFinal());
      }

      // $FF: synthetic method
      MacHasher(Mac var1, Object var2) {
         this(var1);
      }
   }
}
