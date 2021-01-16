package io.netty.handler.codec.marshalling;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.FastThreadLocal;
import org.jboss.marshalling.Marshaller;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.MarshallingConfiguration;

public class ThreadLocalMarshallerProvider implements MarshallerProvider {
   private final FastThreadLocal<Marshaller> marshallers = new FastThreadLocal();
   private final MarshallerFactory factory;
   private final MarshallingConfiguration config;

   public ThreadLocalMarshallerProvider(MarshallerFactory var1, MarshallingConfiguration var2) {
      super();
      this.factory = var1;
      this.config = var2;
   }

   public Marshaller getMarshaller(ChannelHandlerContext var1) throws Exception {
      Marshaller var2 = (Marshaller)this.marshallers.get();
      if (var2 == null) {
         var2 = this.factory.createMarshaller(this.config);
         this.marshallers.set(var2);
      }

      return var2;
   }
}
