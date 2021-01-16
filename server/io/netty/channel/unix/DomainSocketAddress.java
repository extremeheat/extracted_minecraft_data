package io.netty.channel.unix;

import java.io.File;
import java.net.SocketAddress;

public final class DomainSocketAddress extends SocketAddress {
   private static final long serialVersionUID = -6934618000832236893L;
   private final String socketPath;

   public DomainSocketAddress(String var1) {
      super();
      if (var1 == null) {
         throw new NullPointerException("socketPath");
      } else {
         this.socketPath = var1;
      }
   }

   public DomainSocketAddress(File var1) {
      this(var1.getPath());
   }

   public String path() {
      return this.socketPath;
   }

   public String toString() {
      return this.path();
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return !(var1 instanceof DomainSocketAddress) ? false : ((DomainSocketAddress)var1).socketPath.equals(this.socketPath);
      }
   }

   public int hashCode() {
      return this.socketPath.hashCode();
   }
}
