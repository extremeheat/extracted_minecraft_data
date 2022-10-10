package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class CPacketPlayerTryUseItemOnBlock implements Packet<INetHandlerPlayServer> {
   private BlockPos field_179725_b;
   private EnumFacing field_149579_d;
   private EnumHand field_187027_c;
   private float field_149577_f;
   private float field_149578_g;
   private float field_149584_h;

   public CPacketPlayerTryUseItemOnBlock() {
      super();
   }

   public CPacketPlayerTryUseItemOnBlock(BlockPos var1, EnumFacing var2, EnumHand var3, float var4, float var5, float var6) {
      super();
      this.field_179725_b = var1;
      this.field_149579_d = var2;
      this.field_187027_c = var3;
      this.field_149577_f = var4;
      this.field_149578_g = var5;
      this.field_149584_h = var6;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_179725_b = var1.func_179259_c();
      this.field_149579_d = (EnumFacing)var1.func_179257_a(EnumFacing.class);
      this.field_187027_c = (EnumHand)var1.func_179257_a(EnumHand.class);
      this.field_149577_f = var1.readFloat();
      this.field_149578_g = var1.readFloat();
      this.field_149584_h = var1.readFloat();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_179255_a(this.field_179725_b);
      var1.func_179249_a(this.field_149579_d);
      var1.func_179249_a(this.field_187027_c);
      var1.writeFloat(this.field_149577_f);
      var1.writeFloat(this.field_149578_g);
      var1.writeFloat(this.field_149584_h);
   }

   public void func_148833_a(INetHandlerPlayServer var1) {
      var1.func_184337_a(this);
   }

   public BlockPos func_187023_a() {
      return this.field_179725_b;
   }

   public EnumFacing func_187024_b() {
      return this.field_149579_d;
   }

   public EnumHand func_187022_c() {
      return this.field_187027_c;
   }

   public float func_187026_d() {
      return this.field_149577_f;
   }

   public float func_187025_e() {
      return this.field_149578_g;
   }

   public float func_187020_f() {
      return this.field_149584_h;
   }
}
