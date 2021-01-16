package io.netty.channel.epoll;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.unix.IovArray;
import io.netty.channel.unix.Limits;
import io.netty.channel.unix.NativeInetAddress;
import io.netty.util.concurrent.FastThreadLocal;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;

final class NativeDatagramPacketArray implements ChannelOutboundBuffer.MessageProcessor {
   private static final FastThreadLocal<NativeDatagramPacketArray> ARRAY = new FastThreadLocal<NativeDatagramPacketArray>() {
      protected NativeDatagramPacketArray initialValue() throws Exception {
         return new NativeDatagramPacketArray();
      }

      protected void onRemoval(NativeDatagramPacketArray var1) throws Exception {
         NativeDatagramPacketArray.NativeDatagramPacket[] var2 = var1.packets;
         NativeDatagramPacketArray.NativeDatagramPacket[] var3 = var2;
         int var4 = var2.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            NativeDatagramPacketArray.NativeDatagramPacket var6 = var3[var5];
            var6.release();
         }

      }
   };
   private final NativeDatagramPacketArray.NativeDatagramPacket[] packets;
   private int count;

   private NativeDatagramPacketArray() {
      super();
      this.packets = new NativeDatagramPacketArray.NativeDatagramPacket[Limits.UIO_MAX_IOV];

      for(int var1 = 0; var1 < this.packets.length; ++var1) {
         this.packets[var1] = new NativeDatagramPacketArray.NativeDatagramPacket();
      }

   }

   boolean add(DatagramPacket var1) {
      if (this.count == this.packets.length) {
         return false;
      } else {
         ByteBuf var2 = (ByteBuf)var1.content();
         int var3 = var2.readableBytes();
         if (var3 == 0) {
            return true;
         } else {
            NativeDatagramPacketArray.NativeDatagramPacket var4 = this.packets[this.count];
            InetSocketAddress var5 = (InetSocketAddress)var1.recipient();
            if (!var4.init(var2, var5)) {
               return false;
            } else {
               ++this.count;
               return true;
            }
         }
      }
   }

   public boolean processMessage(Object var1) throws Exception {
      return var1 instanceof DatagramPacket && this.add((DatagramPacket)var1);
   }

   int count() {
      return this.count;
   }

   NativeDatagramPacketArray.NativeDatagramPacket[] packets() {
      return this.packets;
   }

   static NativeDatagramPacketArray getInstance(ChannelOutboundBuffer var0) throws Exception {
      NativeDatagramPacketArray var1 = (NativeDatagramPacketArray)ARRAY.get();
      var1.count = 0;
      var0.forEachFlushedMessage(var1);
      return var1;
   }

   // $FF: synthetic method
   NativeDatagramPacketArray(Object var1) {
      this();
   }

   static final class NativeDatagramPacket {
      private final IovArray array = new IovArray();
      private long memoryAddress;
      private int count;
      private byte[] addr;
      private int scopeId;
      private int port;

      NativeDatagramPacket() {
         super();
      }

      private void release() {
         this.array.release();
      }

      private boolean init(ByteBuf var1, InetSocketAddress var2) {
         this.array.clear();
         if (!this.array.add(var1)) {
            return false;
         } else {
            this.memoryAddress = this.array.memoryAddress(0);
            this.count = this.array.count();
            InetAddress var3 = var2.getAddress();
            if (var3 instanceof Inet6Address) {
               this.addr = var3.getAddress();
               this.scopeId = ((Inet6Address)var3).getScopeId();
            } else {
               this.addr = NativeInetAddress.ipv4MappedIpv6Address(var3.getAddress());
               this.scopeId = 0;
            }

            this.port = var2.getPort();
            return true;
         }
      }
   }
}
