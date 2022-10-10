package net.minecraft.network.play.server;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.ResourceLocation;

public class SPacketSelectAdvancementsTab implements Packet<INetHandlerPlayClient> {
   @Nullable
   private ResourceLocation field_194155_a;

   public SPacketSelectAdvancementsTab() {
      super();
   }

   public SPacketSelectAdvancementsTab(@Nullable ResourceLocation var1) {
      super();
      this.field_194155_a = var1;
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_194022_a(this);
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      if (var1.readBoolean()) {
         this.field_194155_a = var1.func_192575_l();
      }

   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.writeBoolean(this.field_194155_a != null);
      if (this.field_194155_a != null) {
         var1.func_192572_a(this.field_194155_a);
      }

   }

   @Nullable
   public ResourceLocation func_194154_a() {
      return this.field_194155_a;
   }
}
