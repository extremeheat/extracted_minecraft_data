package net.minecraft.network;

import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.login.client.C01PacketEncryptionResponse;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.login.server.S01PacketEncryptionRequest;
import net.minecraft.network.login.server.S02PacketLoginSuccess;

enum EnumConnectionState$1 {
   EnumConnectionState$1(int var3) {
      this.func_150756_b(0, S00PacketDisconnect.class);
      this.func_150756_b(1, S01PacketEncryptionRequest.class);
      this.func_150756_b(2, S02PacketLoginSuccess.class);
      this.func_150751_a(0, C00PacketLoginStart.class);
      this.func_150751_a(1, C01PacketEncryptionResponse.class);
   }
}
