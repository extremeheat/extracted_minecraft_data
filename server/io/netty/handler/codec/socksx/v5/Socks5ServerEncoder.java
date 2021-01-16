package io.netty.handler.codec.socksx.v5;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.internal.StringUtil;

@ChannelHandler.Sharable
public class Socks5ServerEncoder extends MessageToByteEncoder<Socks5Message> {
   public static final Socks5ServerEncoder DEFAULT;
   private final Socks5AddressEncoder addressEncoder;

   protected Socks5ServerEncoder() {
      this(Socks5AddressEncoder.DEFAULT);
   }

   public Socks5ServerEncoder(Socks5AddressEncoder var1) {
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
      if (var2 instanceof Socks5InitialResponse) {
         encodeAuthMethodResponse((Socks5InitialResponse)var2, var3);
      } else if (var2 instanceof Socks5PasswordAuthResponse) {
         encodePasswordAuthResponse((Socks5PasswordAuthResponse)var2, var3);
      } else {
         if (!(var2 instanceof Socks5CommandResponse)) {
            throw new EncoderException("unsupported message type: " + StringUtil.simpleClassName((Object)var2));
         }

         this.encodeCommandResponse((Socks5CommandResponse)var2, var3);
      }

   }

   private static void encodeAuthMethodResponse(Socks5InitialResponse var0, ByteBuf var1) {
      var1.writeByte(var0.version().byteValue());
      var1.writeByte(var0.authMethod().byteValue());
   }

   private static void encodePasswordAuthResponse(Socks5PasswordAuthResponse var0, ByteBuf var1) {
      var1.writeByte(1);
      var1.writeByte(var0.status().byteValue());
   }

   private void encodeCommandResponse(Socks5CommandResponse var1, ByteBuf var2) throws Exception {
      var2.writeByte(var1.version().byteValue());
      var2.writeByte(var1.status().byteValue());
      var2.writeByte(0);
      Socks5AddressType var3 = var1.bndAddrType();
      var2.writeByte(var3.byteValue());
      this.addressEncoder.encodeAddress(var3, var1.bndAddr(), var2);
      var2.writeShort(var1.bndPort());
   }

   static {
      DEFAULT = new Socks5ServerEncoder(Socks5AddressEncoder.DEFAULT);
   }
}
