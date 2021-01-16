package io.netty.handler.codec.spdy;

public class SpdySessionStatus implements Comparable<SpdySessionStatus> {
   public static final SpdySessionStatus OK = new SpdySessionStatus(0, "OK");
   public static final SpdySessionStatus PROTOCOL_ERROR = new SpdySessionStatus(1, "PROTOCOL_ERROR");
   public static final SpdySessionStatus INTERNAL_ERROR = new SpdySessionStatus(2, "INTERNAL_ERROR");
   private final int code;
   private final String statusPhrase;

   public static SpdySessionStatus valueOf(int var0) {
      switch(var0) {
      case 0:
         return OK;
      case 1:
         return PROTOCOL_ERROR;
      case 2:
         return INTERNAL_ERROR;
      default:
         return new SpdySessionStatus(var0, "UNKNOWN (" + var0 + ')');
      }
   }

   public SpdySessionStatus(int var1, String var2) {
      super();
      if (var2 == null) {
         throw new NullPointerException("statusPhrase");
      } else {
         this.code = var1;
         this.statusPhrase = var2;
      }
   }

   public int code() {
      return this.code;
   }

   public String statusPhrase() {
      return this.statusPhrase;
   }

   public int hashCode() {
      return this.code();
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof SpdySessionStatus)) {
         return false;
      } else {
         return this.code() == ((SpdySessionStatus)var1).code();
      }
   }

   public String toString() {
      return this.statusPhrase();
   }

   public int compareTo(SpdySessionStatus var1) {
      return this.code() - var1.code();
   }
}
