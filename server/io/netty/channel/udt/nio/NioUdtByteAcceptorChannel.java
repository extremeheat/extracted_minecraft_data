package io.netty.channel.udt.nio;

import com.barchart.udt.TypeUDT;
import com.barchart.udt.nio.SocketChannelUDT;
import io.netty.channel.udt.UdtChannel;

/** @deprecated */
@Deprecated
public class NioUdtByteAcceptorChannel extends NioUdtAcceptorChannel {
   public NioUdtByteAcceptorChannel() {
      super(TypeUDT.STREAM);
   }

   protected UdtChannel newConnectorChannel(SocketChannelUDT var1) {
      return new NioUdtByteConnectorChannel(this, var1);
   }
}
