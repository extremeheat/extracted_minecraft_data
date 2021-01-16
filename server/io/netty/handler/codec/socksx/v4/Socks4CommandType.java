package io.netty.handler.codec.socksx.v4;

public class Socks4CommandType implements Comparable<Socks4CommandType> {
   public static final Socks4CommandType CONNECT = new Socks4CommandType(1, "CONNECT");
   public static final Socks4CommandType BIND = new Socks4CommandType(2, "BIND");
   private final byte byteValue;
   private final String name;
   private String text;

   public static Socks4CommandType valueOf(byte var0) {
      switch(var0) {
      case 1:
         return CONNECT;
      case 2:
         return BIND;
      default:
         return new Socks4CommandType(var0);
      }
   }

   public Socks4CommandType(int var1) {
      this(var1, "UNKNOWN");
   }

   public Socks4CommandType(int var1, String var2) {
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

   public int hashCode() {
      return this.byteValue;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof Socks4CommandType)) {
         return false;
      } else {
         return this.byteValue == ((Socks4CommandType)var1).byteValue;
      }
   }

   public int compareTo(Socks4CommandType var1) {
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
