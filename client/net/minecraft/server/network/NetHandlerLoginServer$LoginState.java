package net.minecraft.server.network;

enum NetHandlerLoginServer$LoginState {
   HELLO,
   KEY,
   AUTHENTICATING,
   READY_TO_ACCEPT,
   ACCEPTED;

   private NetHandlerLoginServer$LoginState() {
   }
}
