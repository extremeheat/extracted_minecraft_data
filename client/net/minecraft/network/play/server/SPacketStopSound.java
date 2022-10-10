package net.minecraft.network.play.server;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;

public class SPacketStopSound implements Packet<INetHandlerPlayClient> {
   private ResourceLocation field_197705_a;
   private SoundCategory field_197706_b;

   public SPacketStopSound() {
      super();
   }

   public SPacketStopSound(@Nullable ResourceLocation var1, @Nullable SoundCategory var2) {
      super();
      this.field_197705_a = var1;
      this.field_197706_b = var2;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      byte var2 = var1.readByte();
      if ((var2 & 1) > 0) {
         this.field_197706_b = (SoundCategory)var1.func_179257_a(SoundCategory.class);
      }

      if ((var2 & 2) > 0) {
         this.field_197705_a = var1.func_192575_l();
      }

   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      if (this.field_197706_b != null) {
         if (this.field_197705_a != null) {
            var1.writeByte(3);
            var1.func_179249_a(this.field_197706_b);
            var1.func_192572_a(this.field_197705_a);
         } else {
            var1.writeByte(1);
            var1.func_179249_a(this.field_197706_b);
         }
      } else if (this.field_197705_a != null) {
         var1.writeByte(2);
         var1.func_192572_a(this.field_197705_a);
      } else {
         var1.writeByte(0);
      }

   }

   @Nullable
   public ResourceLocation func_197703_a() {
      return this.field_197705_a;
   }

   @Nullable
   public SoundCategory func_197704_b() {
      return this.field_197706_b;
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_195512_a(this);
   }
}
