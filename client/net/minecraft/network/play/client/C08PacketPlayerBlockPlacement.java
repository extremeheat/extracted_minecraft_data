package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.BlockPos;

public class C08PacketPlayerBlockPlacement implements Packet<INetHandlerPlayServer> {
   private static final BlockPos field_179726_a = new BlockPos(-1, -1, -1);
   private BlockPos field_179725_b;
   private int field_149579_d;
   private ItemStack field_149580_e;
   private float field_149577_f;
   private float field_149578_g;
   private float field_149584_h;

   public C08PacketPlayerBlockPlacement() {
      super();
   }

   public C08PacketPlayerBlockPlacement(ItemStack var1) {
      this(field_179726_a, 255, var1, 0.0F, 0.0F, 0.0F);
   }

   public C08PacketPlayerBlockPlacement(BlockPos var1, int var2, ItemStack var3, float var4, float var5, float var6) {
      super();
      this.field_179725_b = var1;
      this.field_149579_d = var2;
      this.field_149580_e = var3 != null ? var3.func_77946_l() : null;
      this.field_149577_f = var4;
      this.field_149578_g = var5;
      this.field_149584_h = var6;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_179725_b = var1.func_179259_c();
      this.field_149579_d = var1.readUnsignedByte();
      this.field_149580_e = var1.func_150791_c();
      this.field_149577_f = (float)var1.readUnsignedByte() / 16.0F;
      this.field_149578_g = (float)var1.readUnsignedByte() / 16.0F;
      this.field_149584_h = (float)var1.readUnsignedByte() / 16.0F;
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_179255_a(this.field_179725_b);
      var1.writeByte(this.field_149579_d);
      var1.func_150788_a(this.field_149580_e);
      var1.writeByte((int)(this.field_149577_f * 16.0F));
      var1.writeByte((int)(this.field_149578_g * 16.0F));
      var1.writeByte((int)(this.field_149584_h * 16.0F));
   }

   public void func_148833_a(INetHandlerPlayServer var1) {
      var1.func_147346_a(this);
   }

   public BlockPos func_179724_a() {
      return this.field_179725_b;
   }

   public int func_149568_f() {
      return this.field_149579_d;
   }

   public ItemStack func_149574_g() {
      return this.field_149580_e;
   }

   public float func_149573_h() {
      return this.field_149577_f;
   }

   public float func_149569_i() {
      return this.field_149578_g;
   }

   public float func_149575_j() {
      return this.field_149584_h;
   }
}
