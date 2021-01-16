package io.netty.handler.codec.marshalling;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.FastThreadLocal;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.MarshallingConfiguration;
import org.jboss.marshalling.Unmarshaller;

public class ThreadLocalUnmarshallerProvider implements UnmarshallerProvider {
   private final FastThreadLocal<Unmarshaller> unmarshallers = new FastThreadLocal();
   private final MarshallerFactory factory;
   private final MarshallingConfiguration config;

   public ThreadLocalUnmarshallerProvider(MarshallerFactory var1, MarshallingConfiguration var2) {
      super();
      this.factory = var1;
      this.config = var2;
   }

   public Unmarshaller getUnmarshaller(ChannelHandlerContext var1) throws Exception {
      Unmarshaller var2 = (Unmarshaller)this.unmarshallers.get();
      if (var2 == null) {
         var2 = this.factory.createUnmarshaller(this.config);
         this.unmarshallers.set(var2);
      }

      return var2;
   }
}
