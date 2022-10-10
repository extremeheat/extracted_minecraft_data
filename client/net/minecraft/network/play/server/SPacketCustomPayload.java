package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.ResourceLocation;

public class SPacketCustomPayload implements Packet<INetHandlerPlayClient> {
   public static final ResourceLocation field_209910_a = new ResourceLocation("minecraft:trader_list");
   public static final ResourceLocation field_209911_b = new ResourceLocation("minecraft:brand");
   public static final ResourceLocation field_209912_c = new ResourceLocation("minecraft:book_open");
   public static final ResourceLocation field_209913_d = new ResourceLocation("minecraft:debug/path");
   public static final ResourceLocation field_209914_e = new ResourceLocation("minecraft:debug/neighbors_update");
   public static final ResourceLocation field_209915_f = new ResourceLocation("minecraft:debug/caves");
   public static final ResourceLocation field_209916_g = new ResourceLocation("minecraft:debug/structures");
   public static final ResourceLocation field_209917_h = new ResourceLocation("minecraft:debug/worldgen_attempt");
   private ResourceLocation field_149172_a;
   private PacketBuffer field_149171_b;

   public SPacketCustomPayload() {
      super();
   }

   public SPacketCustomPayload(ResourceLocation var1, PacketBuffer var2) {
      super();
      this.field_149172_a = var1;
      this.field_149171_b = var2;
      if (var2.writerIndex() > 1048576) {
         throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
      }
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149172_a = var1.func_192575_l();
      int var2 = var1.readableBytes();
      if (var2 >= 0 && var2 <= 1048576) {
         this.field_149171_b = new PacketBuffer(var1.readBytes(var2));
      } else {
         throw new IOException("Payload may not be larger than 1048576 bytes");
      }
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_192572_a(this.field_149172_a);
      var1.writeBytes(this.field_149171_b.copy());
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147240_a(this);
   }

   public ResourceLocation func_149169_c() {
      return this.field_149172_a;
   }

   public PacketBuffer func_180735_b() {
      return new PacketBuffer(this.field_149171_b.copy());
   }
}
