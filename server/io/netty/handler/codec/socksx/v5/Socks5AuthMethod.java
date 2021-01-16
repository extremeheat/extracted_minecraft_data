package io.netty.handler.codec.socksx.v5;

public class Socks5AuthMethod implements Comparable<Socks5AuthMethod> {
   public static final Socks5AuthMethod NO_AUTH = new Socks5AuthMethod(0, "NO_AUTH");
   public static final Socks5AuthMethod GSSAPI = new Socks5AuthMethod(1, "GSSAPI");
   public static final Socks5AuthMethod PASSWORD = new Socks5AuthMethod(2, "PASSWORD");
   public static final Socks5AuthMethod UNACCEPTED = new Socks5AuthMethod(255, "UNACCEPTED");
   private final byte byteValue;
   private final String name;
   private String text;

   public static Socks5AuthMethod valueOf(byte var0) {
      switch(var0) {
      case -1:
         return UNACCEPTED;
      case 0:
         return NO_AUTH;
      case 1:
         return GSSAPI;
      case 2:
         return PASSWORD;
      default:
         return new Socks5AuthMethod(var0);
      }
   }

   public Socks5AuthMethod(int var1) {
      this(var1, "UNKNOWN");
   }

   public Socks5AuthMethod(int var1, String var2) {
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
      if (!(var1 instanceof Socks5AuthMethod)) {
         return false;
      } else {
         return this.byteValue == ((Socks5AuthMethod)var1).byteValue;
      }
   }

   public int compareTo(Socks5AuthMethod var1) {
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
