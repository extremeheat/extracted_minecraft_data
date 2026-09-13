package net.minecraft.network;

import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Queue;
import javax.crypto.SecretKey;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.CryptManager;
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class NetworkManager extends SimpleChannelInboundHandler {
   private static final Logger field_150735_g = LogManager.getLogger();
   public static final Marker field_150740_a = MarkerManager.getMarker("NETWORK");
   public static final Marker field_150738_b = MarkerManager.getMarker("NETWORK_PACKETS", field_150740_a);
   public static final Marker field_152461_c = MarkerManager.getMarker("NETWORK_STAT", field_150740_a);
   public static final AttributeKey field_150739_c = new AttributeKey("protocol");
   public static final AttributeKey field_150736_d = new AttributeKey("receivable_packets");
   public static final AttributeKey field_150737_e = new AttributeKey("sendable_packets");
   public static final NioEventLoopGroup field_150734_f = new NioEventLoopGroup(
      0, new ThreadFactoryBuilder().setNameFormat("Netty Client IO #%d").setDaemon(true).build()
   );
   public static final NetworkStatistics field_152462_h = new NetworkStatistics();
   private final boolean field_150747_h;
   private final Queue field_150748_i = Queues.newConcurrentLinkedQueue();
   private final Queue field_150745_j = Queues.newConcurrentLinkedQueue();
   private Channel field_150746_k;
   private SocketAddress field_150743_l;
   private INetHandler field_150744_m;
   private EnumConnectionState field_150741_n;
   private IChatComponent field_150742_o;
   private boolean field_152463_r;

   public NetworkManager(boolean var1) {
      super();
      this.field_150747_h = var1;
   }

   public void channelActive(ChannelHandlerContext var1) {
      super.channelActive(var1);
      this.field_150746_k = var1.channel();
      this.field_150743_l = this.field_150746_k.remoteAddress();
      this.func_150723_a(EnumConnectionState.HANDSHAKING);
   }

   public void func_150723_a(EnumConnectionState var1) {
      this.field_150741_n = (EnumConnectionState)this.field_150746_k.attr(field_150739_c).getAndSet(var1);
      this.field_150746_k.attr(field_150736_d).set(var1.func_150757_a(this.field_150747_h));
      this.field_150746_k.attr(field_150737_e).set(var1.func_150754_b(this.field_150747_h));
      this.field_150746_k.config().setAutoRead(true);
      field_150735_g.debug("Enabled auto read");
   }

   public void channelInactive(ChannelHandlerContext var1) {
      this.func_150718_a(new ChatComponentTranslation("disconnect.endOfStream"));
   }

   public void exceptionCaught(ChannelHandlerContext var1, Throwable var2) {
      ChatComponentTranslation var3;
      if (var2 instanceof TimeoutException) {
         var3 = new ChatComponentTranslation("disconnect.timeout");
      } else {
         var3 = new ChatComponentTranslation("disconnect.genericReason", "Internal Exception: " + var2);
      }

      this.func_150718_a(var3);
   }

   protected void channelRead0(ChannelHandlerContext var1, Packet var2) {
      if (this.field_150746_k.isOpen()) {
         if (var2.func_148836_a()) {
            var2.func_148833_a(this.field_150744_m);
         } else {
            this.field_150748_i.add(var2);
         }
      }
   }

   public void func_150719_a(INetHandler var1) {
      Validate.notNull(var1, "packetListener", new Object[0]);
      field_150735_g.debug("Set listener of {} to {}", new Object[]{this, var1});
      this.field_150744_m = var1;
   }

   public void func_150725_a(Packet var1, GenericFutureListener... var2) {
      if (this.field_150746_k != null && this.field_150746_k.isOpen()) {
         this.func_150733_h();
         this.func_150732_b(var1, var2);
      } else {
         this.field_150745_j.add(new NetworkManager$InboundHandlerTuplePacketListener(var1, var2));
      }
   }

   private void func_150732_b(Packet var1, GenericFutureListener[] var2) {
      EnumConnectionState var3 = EnumConnectionState.func_150752_a(var1);
      EnumConnectionState var4 = (EnumConnectionState)this.field_150746_k.attr(field_150739_c).get();
      if (var4 != var3) {
         field_150735_g.debug("Disabled auto read");
         this.field_150746_k.config().setAutoRead(false);
      }

      if (this.field_150746_k.eventLoop().inEventLoop()) {
         if (var3 != var4) {
            this.func_150723_a(var3);
         }

         this.field_150746_k.writeAndFlush(var1).addListeners(var2).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
      } else {
         this.field_150746_k.eventLoop().execute(new NetworkManager$1(this, var3, var4, var1, var2));
      }
   }

   private void func_150733_h() {
      if (this.field_150746_k != null && this.field_150746_k.isOpen()) {
         while(!this.field_150745_j.isEmpty()) {
            NetworkManager$InboundHandlerTuplePacketListener var1 = (NetworkManager$InboundHandlerTuplePacketListener)this.field_150745_j.poll();
            this.func_150732_b(
               NetworkManager$InboundHandlerTuplePacketListener.access$100(var1), NetworkManager$InboundHandlerTuplePacketListener.access$200(var1)
            );
         }
      }
   }

   public void func_74428_b() {
      this.func_150733_h();
      EnumConnectionState var1 = (EnumConnectionState)this.field_150746_k.attr(field_150739_c).get();
      if (this.field_150741_n != var1) {
         if (this.field_150741_n != null) {
            this.field_150744_m.func_147232_a(this.field_150741_n, var1);
         }

         this.field_150741_n = var1;
      }

      if (this.field_150744_m != null) {
         for(int var2 = 1000; !this.field_150748_i.isEmpty() && var2 >= 0; --var2) {
            Packet var3 = (Packet)this.field_150748_i.poll();
            var3.func_148833_a(this.field_150744_m);
         }

         this.field_150744_m.func_147233_a();
      }

      this.field_150746_k.flush();
   }

   public SocketAddress func_74430_c() {
      return this.field_150743_l;
   }

   public void func_150718_a(IChatComponent var1) {
      if (this.field_150746_k.isOpen()) {
         this.field_150746_k.close();
         this.field_150742_o = var1;
      }
   }

   public boolean func_150731_c() {
      return this.field_150746_k instanceof LocalChannel || this.field_150746_k instanceof LocalServerChannel;
   }

   public static NetworkManager func_150726_a(InetAddress var0, int var1) {
      NetworkManager var2 = new NetworkManager(true);
      ((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group(field_150734_f)).handler(new NetworkManager$2(var2))).channel(NioSocketChannel.class))
         .connect(var0, var1)
         .syncUninterruptibly();
      return var2;
   }

   public static NetworkManager func_150722_a(SocketAddress var0) {
      NetworkManager var1 = new NetworkManager(true);
      ((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group(field_150734_f)).handler(new NetworkManager$3(var1))).channel(LocalChannel.class))
         .connect(var0)
         .syncUninterruptibly();
      return var1;
   }

   public void func_150727_a(SecretKey var1) {
      this.field_150746_k.pipeline().addBefore("splitter", "decrypt", new NettyEncryptingDecoder(CryptManager.func_151229_a(2, var1)));
      this.field_150746_k.pipeline().addBefore("prepender", "encrypt", new NettyEncryptingEncoder(CryptManager.func_151229_a(1, var1)));
      this.field_152463_r = true;
   }

   public boolean func_150724_d() {
      return this.field_150746_k != null && this.field_150746_k.isOpen();
   }

   public INetHandler func_150729_e() {
      return this.field_150744_m;
   }

   public IChatComponent func_150730_f() {
      return this.field_150742_o;
   }

   public void func_150721_g() {
      this.field_150746_k.config().setAutoRead(false);
   }
}
