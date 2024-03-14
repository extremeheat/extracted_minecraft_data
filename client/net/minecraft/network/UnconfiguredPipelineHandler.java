package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import io.netty.util.ReferenceCountUtil;
import net.minecraft.network.protocol.Packet;

public class UnconfiguredPipelineHandler {
   public UnconfiguredPipelineHandler() {
      super();
   }

   public static <T extends PacketListener> UnconfiguredPipelineHandler.InboundConfigurationTask setupInboundProtocol(ProtocolInfo<T> var0) {
      return setupInboundHandler(new PacketDecoder(var0));
   }

   private static UnconfiguredPipelineHandler.InboundConfigurationTask setupInboundHandler(ChannelInboundHandler var0) {
      return var1 -> {
         var1.pipeline().replace(var1.name(), "decoder", var0);
         var1.channel().config().setAutoRead(true);
      };
   }

   public static <T extends PacketListener> UnconfiguredPipelineHandler.OutboundConfigurationTask setupOutboundProtocol(ProtocolInfo<T> var0) {
      return setupOutboundHandler(new PacketEncoder(var0));
   }

   private static UnconfiguredPipelineHandler.OutboundConfigurationTask setupOutboundHandler(ChannelOutboundHandler var0) {
      return var1 -> var1.pipeline().replace(var1.name(), "encoder", var0);
   }

   public static class Inbound extends ChannelDuplexHandler {
      public Inbound() {
         super();
      }

      public void channelRead(ChannelHandlerContext var1, Object var2) {
         if (!(var2 instanceof ByteBuf) && !(var2 instanceof Packet)) {
            var1.fireChannelRead(var2);
         } else {
            ReferenceCountUtil.release(var2);
            throw new DecoderException("Pipeline has no inbound protocol configured, can't process packet " + var2);
         }
      }

      // $VF: Could not properly define all variable types!
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      public void write(ChannelHandlerContext var1, Object var2, ChannelPromise var3) throws Exception {
         if (var2 instanceof UnconfiguredPipelineHandler.InboundConfigurationTask var4) {
            try {
               var4.run(var1);
            } finally {
               ReferenceCountUtil.release(var2);
            }

            var3.setSuccess();
         } else {
            var1.write(var2, var3);
         }
      }
   }

   @FunctionalInterface
   public interface InboundConfigurationTask {
      void run(ChannelHandlerContext var1);

      default UnconfiguredPipelineHandler.InboundConfigurationTask andThen(UnconfiguredPipelineHandler.InboundConfigurationTask var1) {
         return var2 -> {
            this.run(var2);
            var1.run(var2);
         };
      }
   }

   public static class Outbound extends ChannelOutboundHandlerAdapter {
      public Outbound() {
         super();
      }

      // $VF: Could not properly define all variable types!
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      public void write(ChannelHandlerContext var1, Object var2, ChannelPromise var3) throws Exception {
         if (var2 instanceof Packet) {
            ReferenceCountUtil.release(var2);
            throw new EncoderException("Pipeline has no outbound protocol configured, can't process packet " + var2);
         } else {
            if (var2 instanceof UnconfiguredPipelineHandler.OutboundConfigurationTask var4) {
               try {
                  var4.run(var1);
               } finally {
                  ReferenceCountUtil.release(var2);
               }

               var3.setSuccess();
            } else {
               var1.write(var2, var3);
            }
         }
      }
   }

   @FunctionalInterface
   public interface OutboundConfigurationTask {
      void run(ChannelHandlerContext var1);

      default UnconfiguredPipelineHandler.OutboundConfigurationTask andThen(UnconfiguredPipelineHandler.OutboundConfigurationTask var1) {
         return var2 -> {
            this.run(var2);
            var1.run(var2);
         };
      }
   }
}
