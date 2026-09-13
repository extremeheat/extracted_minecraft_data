package net.minecraft.network;

import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;
import net.minecraft.network.status.server.S00PacketServerInfo;
import net.minecraft.network.status.server.S01PacketPong;

enum EnumConnectionState$3 {
   EnumConnectionState$3(int var3) {
      this.func_150751_a(0, C00PacketServerQuery.class);
      this.func_150756_b(0, S00PacketServerInfo.class);
      this.func_150751_a(1, C01PacketPing.class);
      this.func_150756_b(1, S01PacketPong.class);
   }
}
