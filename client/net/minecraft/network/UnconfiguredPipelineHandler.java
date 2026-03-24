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

   public static <T extends PacketListener> InboundConfigurationTask setupInboundProtocol(final ProtocolInfo<T> protocolInfo) {
      return setupInboundHandler(new PacketDecoder(protocolInfo));
   }

   private static InboundConfigurationTask setupInboundHandler(final ChannelInboundHandler newHandler) {
      return (ctx) -> {
         ctx.pipeline().replace(ctx.name(), "decoder", newHandler);
         ctx.channel().config().setAutoRead(true);
      };
   }

   public static <T extends PacketListener> OutboundConfigurationTask setupOutboundProtocol(final ProtocolInfo<T> codecData) {
      return setupOutboundHandler(new PacketEncoder(codecData));
   }

   private static OutboundConfigurationTask setupOutboundHandler(final ChannelOutboundHandler newHandler) {
      return (ctx) -> ctx.pipeline().replace(ctx.name(), "encoder", newHandler);
   }

   public static class Inbound extends ChannelDuplexHandler {
      public Inbound() {
         super();
      }

      public void channelRead(final ChannelHandlerContext ctx, final Object msg) {
         if (!(msg instanceof ByteBuf) && !(msg instanceof Packet)) {
            ctx.fireChannelRead(msg);
         } else {
            ReferenceCountUtil.release(msg);
            throw new DecoderException("Pipeline has no inbound protocol configured, can't process packet " + String.valueOf(msg));
         }
      }

      public void write(final ChannelHandlerContext ctx, final Object msg, final ChannelPromise promise) throws Exception {
         if (msg instanceof InboundConfigurationTask configurationTask) {
            try {
               configurationTask.run(ctx);
            } finally {
               ReferenceCountUtil.release(msg);
            }

            promise.setSuccess();
         } else {
            ctx.write(msg, promise);
         }

      }
   }

   public static class Outbound extends ChannelOutboundHandlerAdapter {
      public Outbound() {
         super();
      }

      public void write(final ChannelHandlerContext ctx, final Object msg, final ChannelPromise promise) throws Exception {
         if (msg instanceof Packet) {
            ReferenceCountUtil.release(msg);
            throw new EncoderException("Pipeline has no outbound protocol configured, can't process packet " + String.valueOf(msg));
         } else {
            if (msg instanceof OutboundConfigurationTask) {
               OutboundConfigurationTask configurationTask = (OutboundConfigurationTask)msg;

               try {
                  configurationTask.run(ctx);
               } finally {
                  ReferenceCountUtil.release(msg);
               }

               promise.setSuccess();
            } else {
               ctx.write(msg, promise);
            }

         }
      }
   }

   @FunctionalInterface
   public interface InboundConfigurationTask {
      void run(ChannelHandlerContext ctx);

      default InboundConfigurationTask andThen(final InboundConfigurationTask otherTask) {
         return (ctx) -> {
            this.run(ctx);
            otherTask.run(ctx);
         };
      }
   }

   @FunctionalInterface
   public interface OutboundConfigurationTask {
      void run(ChannelHandlerContext ctx);

      default OutboundConfigurationTask andThen(final OutboundConfigurationTask otherTask) {
         return (ctx) -> {
            this.run(ctx);
            otherTask.run(ctx);
         };
      }
   }
}
