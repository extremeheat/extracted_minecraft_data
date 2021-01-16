package io.netty.handler.codec.socksx.v5;

public class Socks5CommandStatus implements Comparable<Socks5CommandStatus> {
   public static final Socks5CommandStatus SUCCESS = new Socks5CommandStatus(0, "SUCCESS");
   public static final Socks5CommandStatus FAILURE = new Socks5CommandStatus(1, "FAILURE");
   public static final Socks5CommandStatus FORBIDDEN = new Socks5CommandStatus(2, "FORBIDDEN");
   public static final Socks5CommandStatus NETWORK_UNREACHABLE = new Socks5CommandStatus(3, "NETWORK_UNREACHABLE");
   public static final Socks5CommandStatus HOST_UNREACHABLE = new Socks5CommandStatus(4, "HOST_UNREACHABLE");
   public static final Socks5CommandStatus CONNECTION_REFUSED = new Socks5CommandStatus(5, "CONNECTION_REFUSED");
   public static final Socks5CommandStatus TTL_EXPIRED = new Socks5CommandStatus(6, "TTL_EXPIRED");
   public static final Socks5CommandStatus COMMAND_UNSUPPORTED = new Socks5CommandStatus(7, "COMMAND_UNSUPPORTED");
   public static final Socks5CommandStatus ADDRESS_UNSUPPORTED = new Socks5CommandStatus(8, "ADDRESS_UNSUPPORTED");
   private final byte byteValue;
   private final String name;
   private String text;

   public static Socks5CommandStatus valueOf(byte var0) {
      switch(var0) {
      case 0:
         return SUCCESS;
      case 1:
         return FAILURE;
      case 2:
         return FORBIDDEN;
      case 3:
         return NETWORK_UNREACHABLE;
      case 4:
         return HOST_UNREACHABLE;
      case 5:
         return CONNECTION_REFUSED;
      case 6:
         return TTL_EXPIRED;
      case 7:
         return COMMAND_UNSUPPORTED;
      case 8:
         return ADDRESS_UNSUPPORTED;
      default:
         return new Socks5CommandStatus(var0);
      }
   }

   public Socks5CommandStatus(int var1) {
      this(var1, "UNKNOWN");
   }

   public Socks5CommandStatus(int var1, String var2) {
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
      return this.byteValue == 0;
   }

   public int hashCode() {
      return this.byteValue;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof Socks5CommandStatus)) {
         return false;
      } else {
         return this.byteValue == ((Socks5CommandStatus)var1).byteValue;
      }
   }

   public int compareTo(Socks5CommandStatus var1) {
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
