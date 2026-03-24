package net.minecraft.server.jsonrpc.methods;

public record ClientInfo(Integer connectionId) {
   public ClientInfo {
      super();
   }

   public static ClientInfo of(final Integer connectionId) {
      return new ClientInfo(connectionId);
   }
}
