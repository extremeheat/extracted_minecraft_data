package io.netty.handler.codec.http2;

public interface StreamByteDistributor {
   void updateStreamableBytes(StreamByteDistributor.StreamState var1);

   void updateDependencyTree(int var1, int var2, short var3, boolean var4);

   boolean distribute(int var1, StreamByteDistributor.Writer var2) throws Http2Exception;

   public interface Writer {
      void write(Http2Stream var1, int var2);
   }

   public interface StreamState {
      Http2Stream stream();

      long pendingBytes();

      boolean hasFrame();

      int windowSize();
   }
}
