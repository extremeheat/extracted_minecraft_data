package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.World;

public class S14PacketEntity implements Packet<INetHandlerPlayClient> {
   protected int field_149074_a;
   protected byte field_149072_b;
   protected byte field_149073_c;
   protected byte field_149070_d;
   protected byte field_149071_e;
   protected byte field_149068_f;
   protected boolean field_179743_g;
   protected boolean field_149069_g;

   public S14PacketEntity() {
      super();
   }

   public S14PacketEntity(int var1) {
      super();
      this.field_149074_a = var1;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149074_a = var1.func_150792_a();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_150787_b(this.field_149074_a);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147259_a(this);
   }

   public String toString() {
      return "Entity_" + super.toString();
   }

   public Entity func_149065_a(World var1) {
      return var1.func_73045_a(this.field_149074_a);
   }

   public byte func_149062_c() {
      return this.field_149072_b;
   }

   public byte func_149061_d() {
      return this.field_149073_c;
   }

   public byte func_149064_e() {
      return this.field_149070_d;
   }

   public byte func_149066_f() {
      return this.field_149071_e;
   }

   public byte func_149063_g() {
      return this.field_149068_f;
   }

   public boolean func_149060_h() {
      return this.field_149069_g;
   }

   public boolean func_179742_g() {
      return this.field_179743_g;
   }

   public static class S16PacketEntityLook extends S14PacketEntity {
      public S16PacketEntityLook() {
         super();
         this.field_149069_g = true;
      }

      public S16PacketEntityLook(int var1, byte var2, byte var3, boolean var4) {
         super(var1);
         this.field_149071_e = var2;
         this.field_149068_f = var3;
         this.field_149069_g = true;
         this.field_179743_g = var4;
      }

      public void func_148837_a(PacketBuffer var1) throws IOException {
         super.func_148837_a(var1);
         this.field_149071_e = var1.readByte();
         this.field_149068_f = var1.readByte();
         this.field_179743_g = var1.readBoolean();
      }

      public void func_148840_b(PacketBuffer var1) throws IOException {
         super.func_148840_b(var1);
         var1.writeByte(this.field_149071_e);
         var1.writeByte(this.field_149068_f);
         var1.writeBoolean(this.field_179743_g);
      }
   }

   public static class S15PacketEntityRelMove extends S14PacketEntity {
      public S15PacketEntityRelMove() {
         super();
      }

      public S15PacketEntityRelMove(int var1, byte var2, byte var3, byte var4, boolean var5) {
         super(var1);
         this.field_149072_b = var2;
         this.field_149073_c = var3;
         this.field_149070_d = var4;
         this.field_179743_g = var5;
      }

      public void func_148837_a(PacketBuffer var1) throws IOException {
         super.func_148837_a(var1);
         this.field_149072_b = var1.readByte();
         this.field_149073_c = var1.readByte();
         this.field_149070_d = var1.readByte();
         this.field_179743_g = var1.readBoolean();
      }

      public void func_148840_b(PacketBuffer var1) throws IOException {
         super.func_148840_b(var1);
         var1.writeByte(this.field_149072_b);
         var1.writeByte(this.field_149073_c);
         var1.writeByte(this.field_149070_d);
         var1.writeBoolean(this.field_179743_g);
      }
   }

   public static class S17PacketEntityLookMove extends S14PacketEntity {
      public S17PacketEntityLookMove() {
         super();
         this.field_149069_g = true;
      }

      public S17PacketEntityLookMove(int var1, byte var2, byte var3, byte var4, byte var5, byte var6, boolean var7) {
         super(var1);
         this.field_149072_b = var2;
         this.field_149073_c = var3;
         this.field_149070_d = var4;
         this.field_149071_e = var5;
         this.field_149068_f = var6;
         this.field_179743_g = var7;
         this.field_149069_g = true;
      }

      public void func_148837_a(PacketBuffer var1) throws IOException {
         super.func_148837_a(var1);
         this.field_149072_b = var1.readByte();
         this.field_149073_c = var1.readByte();
         this.field_149070_d = var1.readByte();
         this.field_149071_e = var1.readByte();
         this.field_149068_f = var1.readByte();
         this.field_179743_g = var1.readBoolean();
      }

      public void func_148840_b(PacketBuffer var1) throws IOException {
         super.func_148840_b(var1);
         var1.writeByte(this.field_149072_b);
         var1.writeByte(this.field_149073_c);
         var1.writeByte(this.field_149070_d);
         var1.writeByte(this.field_149071_e);
         var1.writeByte(this.field_149068_f);
         var1.writeBoolean(this.field_179743_g);
      }
   }
}
