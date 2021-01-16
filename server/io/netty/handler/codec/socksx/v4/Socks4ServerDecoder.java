package io.netty.handler.codec.socksx.v4;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.handler.codec.socksx.SocksVersion;
import io.netty.util.CharsetUtil;
import io.netty.util.NetUtil;
import java.util.List;

public class Socks4ServerDecoder extends ReplayingDecoder<Socks4ServerDecoder.State> {
   private static final int MAX_FIELD_LENGTH = 255;
   private Socks4CommandType type;
   private String dstAddr;
   private int dstPort;
   private String userId;

   public Socks4ServerDecoder() {
      super(Socks4ServerDecoder.State.START);
      this.setSingleDecode(true);
   }

   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      try {
         switch((Socks4ServerDecoder.State)this.state()) {
         case START:
            short var4 = var2.readUnsignedByte();
            if (var4 != SocksVersion.SOCKS4a.byteValue()) {
               throw new DecoderException("unsupported protocol version: " + var4);
            }

            this.type = Socks4CommandType.valueOf(var2.readByte());
            this.dstPort = var2.readUnsignedShort();
            this.dstAddr = NetUtil.intToIpAddress(var2.readInt());
            this.checkpoint(Socks4ServerDecoder.State.READ_USERID);
         case READ_USERID:
            this.userId = readString("userid", var2);
            this.checkpoint(Socks4ServerDecoder.State.READ_DOMAIN);
         case READ_DOMAIN:
            if (!"0.0.0.0".equals(this.dstAddr) && this.dstAddr.startsWith("0.0.0.")) {
               this.dstAddr = readString("dstAddr", var2);
            }

            var3.add(new DefaultSocks4CommandRequest(this.type, this.dstAddr, this.dstPort, this.userId));
            this.checkpoint(Socks4ServerDecoder.State.SUCCESS);
         case SUCCESS:
            int var6 = this.actualReadableBytes();
            if (var6 > 0) {
               var3.add(var2.readRetainedSlice(var6));
            }
            break;
         case FAILURE:
            var2.skipBytes(this.actualReadableBytes());
         }
      } catch (Exception var5) {
         this.fail(var3, var5);
      }

   }

   private void fail(List<Object> var1, Exception var2) {
      if (!(var2 instanceof DecoderException)) {
         var2 = new DecoderException((Throwable)var2);
      }

      DefaultSocks4CommandRequest var3 = new DefaultSocks4CommandRequest(this.type != null ? this.type : Socks4CommandType.CONNECT, this.dstAddr != null ? this.dstAddr : "", this.dstPort != 0 ? this.dstPort : '\uffff', this.userId != null ? this.userId : "");
      var3.setDecoderResult(DecoderResult.failure((Throwable)var2));
      var1.add(var3);
      this.checkpoint(Socks4ServerDecoder.State.FAILURE);
   }

   private static String readString(String var0, ByteBuf var1) {
      int var2 = var1.bytesBefore(256, (byte)0);
      if (var2 < 0) {
         throw new DecoderException("field '" + var0 + "' longer than " + 255 + " chars");
      } else {
         String var3 = var1.readSlice(var2).toString(CharsetUtil.US_ASCII);
         var1.skipBytes(1);
         return var3;
      }
   }

   static enum State {
      START,
      READ_USERID,
      READ_DOMAIN,
      SUCCESS,
      FAILURE;

      private State() {
      }
   }
}
