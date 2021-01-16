package io.netty.handler.codec.socksx.v5;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.internal.StringUtil;
import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;

@ChannelHandler.Sharable
public class Socks5ClientEncoder extends MessageToByteEncoder<Socks5Message> {
   public static final Socks5ClientEncoder DEFAULT = new Socks5ClientEncoder();
   private final Socks5AddressEncoder addressEncoder;

   protected Socks5ClientEncoder() {
      this(Socks5AddressEncoder.DEFAULT);
   }

   public Socks5ClientEncoder(Socks5AddressEncoder var1) {
      super();
      if (var1 == null) {
         throw new NullPointerException("addressEncoder");
      } else {
         this.addressEncoder = var1;
      }
   }

   protected final Socks5AddressEncoder addressEncoder() {
      return this.addressEncoder;
   }

   protected void encode(ChannelHandlerContext var1, Socks5Message var2, ByteBuf var3) throws Exception {
      if (var2 instanceof Socks5InitialRequest) {
         encodeAuthMethodRequest((Socks5InitialRequest)var2, var3);
      } else if (var2 instanceof Socks5PasswordAuthRequest) {
         encodePasswordAuthRequest((Socks5PasswordAuthRequest)var2, var3);
      } else {
         if (!(var2 instanceof Socks5CommandRequest)) {
            throw new EncoderException("unsupported message type: " + StringUtil.simpleClassName((Object)var2));
         }

         this.encodeCommandRequest((Socks5CommandRequest)var2, var3);
      }

   }

   private static void encodeAuthMethodRequest(Socks5InitialRequest var0, ByteBuf var1) {
      var1.writeByte(var0.version().byteValue());
      List var2 = var0.authMethods();
      int var3 = var2.size();
      var1.writeByte(var3);
      if (var2 instanceof RandomAccess) {
         for(int var4 = 0; var4 < var3; ++var4) {
            var1.writeByte(((Socks5AuthMethod)var2.get(var4)).byteValue());
         }
      } else {
         Iterator var6 = var2.iterator();

         while(var6.hasNext()) {
            Socks5AuthMethod var5 = (Socks5AuthMethod)var6.next();
            var1.writeByte(var5.byteValue());
         }
      }

   }

   private static void encodePasswordAuthRequest(Socks5PasswordAuthRequest var0, ByteBuf var1) {
      var1.writeByte(1);
      String var2 = var0.username();
      var1.writeByte(var2.length());
      ByteBufUtil.writeAscii((ByteBuf)var1, var2);
      String var3 = var0.password();
      var1.writeByte(var3.length());
      ByteBufUtil.writeAscii((ByteBuf)var1, var3);
   }

   private void encodeCommandRequest(Socks5CommandRequest var1, ByteBuf var2) throws Exception {
      var2.writeByte(var1.version().byteValue());
      var2.writeByte(var1.type().byteValue());
      var2.writeByte(0);
      Socks5AddressType var3 = var1.dstAddrType();
      var2.writeByte(var3.byteValue());
      this.addressEncoder.encodeAddress(var3, var1.dstAddr(), var2);
      var2.writeShort(var1.dstPort());
   }
}
