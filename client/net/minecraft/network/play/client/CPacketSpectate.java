package net.minecraft.network.play.client;

import java.io.IOException;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.world.WorldServer;

public class CPacketSpectate implements Packet<INetHandlerPlayServer> {
   private UUID field_179729_a;

   public CPacketSpectate() {
      super();
   }

   public CPacketSpectate(UUID var1) {
      super();
      this.field_179729_a = var1;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_179729_a = var1.func_179253_g();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_179252_a(this.field_179729_a);
   }

   public void func_148833_a(INetHandlerPlayServer var1) {
      var1.func_175088_a(this);
   }

   @Nullable
   public Entity func_179727_a(WorldServer var1) {
      return var1.func_175733_a(this.field_179729_a);
   }
}
