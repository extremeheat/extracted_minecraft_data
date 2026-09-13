package net.minecraft.network;

import com.google.common.collect.BiMap;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Packet {
   private static final Logger field_148841_a = LogManager.getLogger();

   public Packet() {
      super();
   }

   public static Packet func_148839_a(BiMap var0, int var1) {
      try {
         Class var2 = (Class)var0.get(var1);
         return var2 == null ? null : (Packet)var2.newInstance();
      } catch (Exception var3) {
         field_148841_a.error("Couldn't create packet " + var1, var3);
         return null;
      }
   }

   public static void func_148838_a(ByteBuf var0, byte[] var1) {
      var0.writeShort(var1.length);
      var0.writeBytes(var1);
   }

   public static byte[] func_148834_a(ByteBuf var0) {
      short var1 = var0.readShort();
      if (var1 < 0) {
         throw new IOException("Key was smaller than nothing!  Weird key!");
      } else {
         byte[] var2 = new byte[var1];
         var0.readBytes(var2);
         return var2;
      }
   }

   public abstract void func_148837_a(PacketBuffer var1);

   public abstract void func_148840_b(PacketBuffer var1);

   public abstract void func_148833_a(INetHandler var1);

   public boolean func_148836_a() {
      return false;
   }

   @Override
   public String toString() {
      return this.getClass().getSimpleName();
   }

   public String func_148835_b() {
      return "";
   }
}
