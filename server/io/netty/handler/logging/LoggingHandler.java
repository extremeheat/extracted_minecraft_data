package io.netty.handler.logging;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogLevel;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.SocketAddress;

@ChannelHandler.Sharable
public class LoggingHandler extends ChannelDuplexHandler {
   private static final LogLevel DEFAULT_LEVEL;
   protected final InternalLogger logger;
   protected final InternalLogLevel internalLevel;
   private final LogLevel level;

   public LoggingHandler() {
      this(DEFAULT_LEVEL);
   }

   public LoggingHandler(LogLevel var1) {
      super();
      if (var1 == null) {
         throw new NullPointerException("level");
      } else {
         this.logger = InternalLoggerFactory.getInstance(this.getClass());
         this.level = var1;
         this.internalLevel = var1.toInternalLevel();
      }
   }

   public LoggingHandler(Class<?> var1) {
      this(var1, DEFAULT_LEVEL);
   }

   public LoggingHandler(Class<?> var1, LogLevel var2) {
      super();
      if (var1 == null) {
         throw new NullPointerException("clazz");
      } else if (var2 == null) {
         throw new NullPointerException("level");
      } else {
         this.logger = InternalLoggerFactory.getInstance(var1);
         this.level = var2;
         this.internalLevel = var2.toInternalLevel();
      }
   }

   public LoggingHandler(String var1) {
      this(var1, DEFAULT_LEVEL);
   }

   public LoggingHandler(String var1, LogLevel var2) {
      super();
      if (var1 == null) {
         throw new NullPointerException("name");
      } else if (var2 == null) {
         throw new NullPointerException("level");
      } else {
         this.logger = InternalLoggerFactory.getInstance(var1);
         this.level = var2;
         this.internalLevel = var2.toInternalLevel();
      }
   }

   public LogLevel level() {
      return this.level;
   }

   public void channelRegistered(ChannelHandlerContext var1) throws Exception {
      if (this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(var1, "REGISTERED"));
      }

      var1.fireChannelRegistered();
   }

   public void channelUnregistered(ChannelHandlerContext var1) throws Exception {
      if (this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(var1, "UNREGISTERED"));
      }

      var1.fireChannelUnregistered();
   }

   public void channelActive(ChannelHandlerContext var1) throws Exception {
      if (this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(var1, "ACTIVE"));
      }

      var1.fireChannelActive();
   }

   public void channelInactive(ChannelHandlerContext var1) throws Exception {
      if (this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(var1, "INACTIVE"));
      }

      var1.fireChannelInactive();
   }

   public void exceptionCaught(ChannelHandlerContext var1, Throwable var2) throws Exception {
      if (this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(var1, "EXCEPTION", var2), var2);
      }

      var1.fireExceptionCaught(var2);
   }

   public void userEventTriggered(ChannelHandlerContext var1, Object var2) throws Exception {
      if (this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(var1, "USER_EVENT", var2));
      }

      var1.fireUserEventTriggered(var2);
   }

   public void bind(ChannelHandlerContext var1, SocketAddress var2, ChannelPromise var3) throws Exception {
      if (this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(var1, "BIND", var2));
      }

      var1.bind(var2, var3);
   }

   public void connect(ChannelHandlerContext var1, SocketAddress var2, SocketAddress var3, ChannelPromise var4) throws Exception {
      if (this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(var1, "CONNECT", var2, var3));
      }

      var1.connect(var2, var3, var4);
   }

   public void disconnect(ChannelHandlerContext var1, ChannelPromise var2) throws Exception {
      if (this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(var1, "DISCONNECT"));
      }

      var1.disconnect(var2);
   }

   public void close(ChannelHandlerContext var1, ChannelPromise var2) throws Exception {
      if (this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(var1, "CLOSE"));
      }

      var1.close(var2);
   }

   public void deregister(ChannelHandlerContext var1, ChannelPromise var2) throws Exception {
      if (this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(var1, "DEREGISTER"));
      }

      var1.deregister(var2);
   }

   public void channelReadComplete(ChannelHandlerContext var1) throws Exception {
      if (this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(var1, "READ COMPLETE"));
      }

      var1.fireChannelReadComplete();
   }

   public void channelRead(ChannelHandlerContext var1, Object var2) throws Exception {
      if (this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(var1, "READ", var2));
      }

      var1.fireChannelRead(var2);
   }

   public void write(ChannelHandlerContext var1, Object var2, ChannelPromise var3) throws Exception {
      if (this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(var1, "WRITE", var2));
      }

      var1.write(var2, var3);
   }

   public void channelWritabilityChanged(ChannelHandlerContext var1) throws Exception {
      if (this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(var1, "WRITABILITY CHANGED"));
      }

      var1.fireChannelWritabilityChanged();
   }

   public void flush(ChannelHandlerContext var1) throws Exception {
      if (this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(var1, "FLUSH"));
      }

      var1.flush();
   }

   protected String format(ChannelHandlerContext var1, String var2) {
      String var3 = var1.channel().toString();
      return (new StringBuilder(var3.length() + 1 + var2.length())).append(var3).append(' ').append(var2).toString();
   }

   protected String format(ChannelHandlerContext var1, String var2, Object var3) {
      if (var3 instanceof ByteBuf) {
         return formatByteBuf(var1, var2, (ByteBuf)var3);
      } else {
         return var3 instanceof ByteBufHolder ? formatByteBufHolder(var1, var2, (ByteBufHolder)var3) : formatSimple(var1, var2, var3);
      }
   }

   protected String format(ChannelHandlerContext var1, String var2, Object var3, Object var4) {
      if (var4 == null) {
         return formatSimple(var1, var2, var3);
      } else {
         String var5 = var1.channel().toString();
         String var6 = String.valueOf(var3);
         String var7 = var4.toString();
         StringBuilder var8 = new StringBuilder(var5.length() + 1 + var2.length() + 2 + var6.length() + 2 + var7.length());
         var8.append(var5).append(' ').append(var2).append(": ").append(var6).append(", ").append(var7);
         return var8.toString();
      }
   }

   private static String formatByteBuf(ChannelHandlerContext var0, String var1, ByteBuf var2) {
      String var3 = var0.channel().toString();
      int var4 = var2.readableBytes();
      if (var4 == 0) {
         StringBuilder var7 = new StringBuilder(var3.length() + 1 + var1.length() + 4);
         var7.append(var3).append(' ').append(var1).append(": 0B");
         return var7.toString();
      } else {
         int var5 = var4 / 16 + (var4 % 15 == 0 ? 0 : 1) + 4;
         StringBuilder var6 = new StringBuilder(var3.length() + 1 + var1.length() + 2 + 10 + 1 + 2 + var5 * 80);
         var6.append(var3).append(' ').append(var1).append(": ").append(var4).append('B').append(StringUtil.NEWLINE);
         ByteBufUtil.appendPrettyHexDump(var6, var2);
         return var6.toString();
      }
   }

   private static String formatByteBufHolder(ChannelHandlerContext var0, String var1, ByteBufHolder var2) {
      String var3 = var0.channel().toString();
      String var4 = var2.toString();
      ByteBuf var5 = var2.content();
      int var6 = var5.readableBytes();
      if (var6 == 0) {
         StringBuilder var9 = new StringBuilder(var3.length() + 1 + var1.length() + 2 + var4.length() + 4);
         var9.append(var3).append(' ').append(var1).append(", ").append(var4).append(", 0B");
         return var9.toString();
      } else {
         int var7 = var6 / 16 + (var6 % 15 == 0 ? 0 : 1) + 4;
         StringBuilder var8 = new StringBuilder(var3.length() + 1 + var1.length() + 2 + var4.length() + 2 + 10 + 1 + 2 + var7 * 80);
         var8.append(var3).append(' ').append(var1).append(": ").append(var4).append(", ").append(var6).append('B').append(StringUtil.NEWLINE);
         ByteBufUtil.appendPrettyHexDump(var8, var5);
         return var8.toString();
      }
   }

   private static String formatSimple(ChannelHandlerContext var0, String var1, Object var2) {
      String var3 = var0.channel().toString();
      String var4 = String.valueOf(var2);
      StringBuilder var5 = new StringBuilder(var3.length() + 1 + var1.length() + 2 + var4.length());
      return var5.append(var3).append(' ').append(var1).append(": ").append(var4).toString();
   }

   static {
      DEFAULT_LEVEL = LogLevel.DEBUG;
   }
}
