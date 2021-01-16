package io.netty.handler.codec.http2;

import io.netty.channel.ChannelHandlerContext;

public interface Http2RemoteFlowController extends Http2FlowController {
   ChannelHandlerContext channelHandlerContext();

   void addFlowControlled(Http2Stream var1, Http2RemoteFlowController.FlowControlled var2);

   boolean hasFlowControlled(Http2Stream var1);

   void writePendingBytes() throws Http2Exception;

   void listener(Http2RemoteFlowController.Listener var1);

   boolean isWritable(Http2Stream var1);

   void channelWritabilityChanged() throws Http2Exception;

   void updateDependencyTree(int var1, int var2, short var3, boolean var4);

   public interface Listener {
      void writabilityChanged(Http2Stream var1);
   }

   public interface FlowControlled {
      int size();

      void error(ChannelHandlerContext var1, Throwable var2);

      void writeComplete();

      void write(ChannelHandlerContext var1, int var2);

      boolean merge(ChannelHandlerContext var1, Http2RemoteFlowController.FlowControlled var2);
   }
}
