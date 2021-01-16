package io.netty.handler.codec.socksx.v4;

public class Socks4CommandStatus implements Comparable<Socks4CommandStatus> {
   public static final Socks4CommandStatus SUCCESS = new Socks4CommandStatus(90, "SUCCESS");
   public static final Socks4CommandStatus REJECTED_OR_FAILED = new Socks4CommandStatus(91, "REJECTED_OR_FAILED");
   public static final Socks4CommandStatus IDENTD_UNREACHABLE = new Socks4CommandStatus(92, "IDENTD_UNREACHABLE");
   public static final Socks4CommandStatus IDENTD_AUTH_FAILURE = new Socks4CommandStatus(93, "IDENTD_AUTH_FAILURE");
   private final byte byteValue;
   private final String name;
   private String text;

   public static Socks4CommandStatus valueOf(byte var0) {
      switch(var0) {
      case 90:
         return SUCCESS;
      case 91:
         return REJECTED_OR_FAILED;
      case 92:
         return IDENTD_UNREACHABLE;
      case 93:
         return IDENTD_AUTH_FAILURE;
      default:
         return new Socks4CommandStatus(var0);
      }
   }

   public Socks4CommandStatus(int var1) {
      this(var1, "UNKNOWN");
   }

   public Socks4CommandStatus(int var1, String var2) {
      super();
      if (var2 == null) {
         throw new NullPointerException("name");
      } else {
         this.byteValue = (byte)var1;
         this.name = var2;
      }
   }

   public byte byteValue() {
      return this.byteValue;
   }

   public boolean isSuccess() {
      return this.byteValue == 90;
   }

   public int hashCode() {
      return this.byteValue;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof Socks4CommandStatus)) {
         return false;
      } else {
         return this.byteValue == ((Socks4CommandStatus)var1).byteValue;
      }
   }

   public int compareTo(Socks4CommandStatus var1) {
      return this.byteValue - var1.byteValue;
   }

   public String toString() {
      String var1 = this.text;
      if (var1 == null) {
         this.text = var1 = this.name + '(' + (this.byteValue & 255) + ')';
      }

      return var1;
   }
}
