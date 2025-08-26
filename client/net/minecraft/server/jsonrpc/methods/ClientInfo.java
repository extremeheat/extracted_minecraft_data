package net.minecraft.server.jsonrpc.methods;

public record ClientInfo(Integer connectionId) {
   public ClientInfo(Integer var1) {
      super();
      this.connectionId = var1;
   }

   public static ClientInfo of(Integer var0) {
      return new ClientInfo(var0);
   }
}
