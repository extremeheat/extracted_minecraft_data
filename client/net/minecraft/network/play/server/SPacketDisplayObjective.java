package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.scoreboard.ScoreObjective;

public class SPacketDisplayObjective implements Packet<INetHandlerPlayClient> {
   private int field_149374_a;
   private String field_149373_b;

   public SPacketDisplayObjective() {
      super();
   }

   public SPacketDisplayObjective(int var1, @Nullable ScoreObjective var2) {
      super();
      this.field_149374_a = var1;
      if (var2 == null) {
         this.field_149373_b = "";
      } else {
         this.field_149373_b = var2.func_96679_b();
      }

   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149374_a = var1.readByte();
      this.field_149373_b = var1.func_150789_c(16);
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.writeByte(this.field_149374_a);
      var1.func_180714_a(this.field_149373_b);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147254_a(this);
   }

   public int func_149371_c() {
      return this.field_149374_a;
   }

   @Nullable
   public String func_149370_d() {
      return Objects.equals(this.field_149373_b, "") ? null : this.field_149373_b;
   }
}
