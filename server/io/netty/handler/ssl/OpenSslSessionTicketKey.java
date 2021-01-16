package io.netty.handler.ssl;

import io.netty.internal.tcnative.SessionTicketKey;

public final class OpenSslSessionTicketKey {
   public static final int NAME_SIZE = 16;
   public static final int HMAC_KEY_SIZE = 16;
   public static final int AES_KEY_SIZE = 16;
   public static final int TICKET_KEY_SIZE = 48;
   final SessionTicketKey key;

   public OpenSslSessionTicketKey(byte[] var1, byte[] var2, byte[] var3) {
      super();
      this.key = new SessionTicketKey((byte[])var1.clone(), (byte[])var2.clone(), (byte[])var3.clone());
   }

   public byte[] name() {
      return (byte[])this.key.getName().clone();
   }

   public byte[] hmacKey() {
      return (byte[])this.key.getHmacKey().clone();
   }

   public byte[] aesKey() {
      return (byte[])this.key.getAesKey().clone();
   }
}
