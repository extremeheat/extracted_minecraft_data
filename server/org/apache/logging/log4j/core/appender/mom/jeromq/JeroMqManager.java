package org.apache.logging.log4j.core.appender.mom.jeromq;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractManager;
import org.apache.logging.log4j.core.appender.ManagerFactory;
import org.apache.logging.log4j.core.util.ShutdownCallbackRegistry;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

public class JeroMqManager extends AbstractManager {
   public static final String SYS_PROPERTY_ENABLE_SHUTDOWN_HOOK = "log4j.jeromq.enableShutdownHook";
   public static final String SYS_PROPERTY_IO_THREADS = "log4j.jeromq.ioThreads";
   private static final JeroMqManager.JeroMqManagerFactory FACTORY = new JeroMqManager.JeroMqManagerFactory();
   private static final Context CONTEXT;
   private final Socket publisher;

   private JeroMqManager(String var1, JeroMqManager.JeroMqConfiguration var2) {
      super((LoggerContext)null, var1);
      this.publisher = CONTEXT.socket(1);
      this.publisher.setAffinity(var2.affinity);
      this.publisher.setBacklog(var2.backlog);
      this.publisher.setDelayAttachOnConnect(var2.delayAttachOnConnect);
      if (var2.identity != null) {
         this.publisher.setIdentity(var2.identity);
      }

      this.publisher.setIPv4Only(var2.ipv4Only);
      this.publisher.setLinger(var2.linger);
      this.publisher.setMaxMsgSize(var2.maxMsgSize);
      this.publisher.setRcvHWM(var2.rcvHwm);
      this.publisher.setReceiveBufferSize(var2.receiveBufferSize);
      this.publisher.setReceiveTimeOut(var2.receiveTimeOut);
      this.publisher.setReconnectIVL(var2.reconnectIVL);
      this.publisher.setReconnectIVLMax(var2.reconnectIVLMax);
      this.publisher.setSendBufferSize(var2.sendBufferSize);
      this.publisher.setSendTimeOut(var2.sendTimeOut);
      this.publisher.setSndHWM(var2.sndHwm);
      this.publisher.setTCPKeepAlive(var2.tcpKeepAlive);
      this.publisher.setTCPKeepAliveCount(var2.tcpKeepAliveCount);
      this.publisher.setTCPKeepAliveIdle(var2.tcpKeepAliveIdle);
      this.publisher.setTCPKeepAliveInterval(var2.tcpKeepAliveInterval);
      this.publisher.setXpubVerbose(var2.xpubVerbose);
      Iterator var3 = var2.endpoints.iterator();

      while(var3.hasNext()) {
         String var4 = (String)var3.next();
         this.publisher.bind(var4);
      }

      LOGGER.debug((String)"Created JeroMqManager with {}", (Object)var2);
   }

   public boolean send(byte[] var1) {
      return this.publisher.send(var1);
   }

   protected boolean releaseSub(long var1, TimeUnit var3) {
      this.publisher.close();
      return true;
   }

   public static JeroMqManager getJeroMqManager(String var0, long var1, long var3, boolean var5, byte[] var6, boolean var7, long var8, long var10, long var12, long var14, int var16, long var17, long var19, long var21, int var23, long var24, int var26, long var27, long var29, long var31, boolean var33, List<String> var34) {
      return (JeroMqManager)getManager(var0, FACTORY, new JeroMqManager.JeroMqConfiguration(var1, var3, var5, var6, var7, var8, var10, var12, var14, var16, var17, var19, var21, var23, var24, var26, var27, var29, var31, var33, var34));
   }

   public static Context getContext() {
      return CONTEXT;
   }

   // $FF: synthetic method
   JeroMqManager(String var1, JeroMqManager.JeroMqConfiguration var2, Object var3) {
      this(var1, var2);
   }

   static {
      LOGGER.trace((String)"JeroMqManager using ZMQ version {}", (Object)ZMQ.getVersionString());
      int var0 = PropertiesUtil.getProperties().getIntegerProperty("log4j.jeromq.ioThreads", 1);
      LOGGER.trace((String)"JeroMqManager creating ZMQ context with ioThreads = {}", (Object)var0);
      CONTEXT = ZMQ.context(var0);
      boolean var1 = PropertiesUtil.getProperties().getBooleanProperty("log4j.jeromq.enableShutdownHook", true);
      if (var1) {
         ((ShutdownCallbackRegistry)LogManager.getFactory()).addShutdownCallback(new Runnable() {
            public void run() {
               JeroMqManager.CONTEXT.close();
            }
         });
      }

   }

   private static class JeroMqManagerFactory implements ManagerFactory<JeroMqManager, JeroMqManager.JeroMqConfiguration> {
      private JeroMqManagerFactory() {
         super();
      }

      public JeroMqManager createManager(String var1, JeroMqManager.JeroMqConfiguration var2) {
         return new JeroMqManager(var1, var2);
      }

      // $FF: synthetic method
      JeroMqManagerFactory(Object var1) {
         this();
      }
   }

   private static class JeroMqConfiguration {
      private final long affinity;
      private final long backlog;
      private final boolean delayAttachOnConnect;
      private final byte[] identity;
      private final boolean ipv4Only;
      private final long linger;
      private final long maxMsgSize;
      private final long rcvHwm;
      private final long receiveBufferSize;
      private final int receiveTimeOut;
      private final long reconnectIVL;
      private final long reconnectIVLMax;
      private final long sendBufferSize;
      private final int sendTimeOut;
      private final long sndHwm;
      private final int tcpKeepAlive;
      private final long tcpKeepAliveCount;
      private final long tcpKeepAliveIdle;
      private final long tcpKeepAliveInterval;
      private final boolean xpubVerbose;
      private final List<String> endpoints;

      private JeroMqConfiguration(long var1, long var3, boolean var5, byte[] var6, boolean var7, long var8, long var10, long var12, long var14, int var16, long var17, long var19, long var21, int var23, long var24, int var26, long var27, long var29, long var31, boolean var33, List<String> var34) {
         super();
         this.affinity = var1;
         this.backlog = var3;
         this.delayAttachOnConnect = var5;
         this.identity = var6;
         this.ipv4Only = var7;
         this.linger = var8;
         this.maxMsgSize = var10;
         this.rcvHwm = var12;
         this.receiveBufferSize = var14;
         this.receiveTimeOut = var16;
         this.reconnectIVL = var17;
         this.reconnectIVLMax = var19;
         this.sendBufferSize = var21;
         this.sendTimeOut = var23;
         this.sndHwm = var24;
         this.tcpKeepAlive = var26;
         this.tcpKeepAliveCount = var27;
         this.tcpKeepAliveIdle = var29;
         this.tcpKeepAliveInterval = var31;
         this.xpubVerbose = var33;
         this.endpoints = var34;
      }

      public String toString() {
         return "JeroMqConfiguration{affinity=" + this.affinity + ", backlog=" + this.backlog + ", delayAttachOnConnect=" + this.delayAttachOnConnect + ", identity=" + Arrays.toString(this.identity) + ", ipv4Only=" + this.ipv4Only + ", linger=" + this.linger + ", maxMsgSize=" + this.maxMsgSize + ", rcvHwm=" + this.rcvHwm + ", receiveBufferSize=" + this.receiveBufferSize + ", receiveTimeOut=" + this.receiveTimeOut + ", reconnectIVL=" + this.reconnectIVL + ", reconnectIVLMax=" + this.reconnectIVLMax + ", sendBufferSize=" + this.sendBufferSize + ", sendTimeOut=" + this.sendTimeOut + ", sndHwm=" + this.sndHwm + ", tcpKeepAlive=" + this.tcpKeepAlive + ", tcpKeepAliveCount=" + this.tcpKeepAliveCount + ", tcpKeepAliveIdle=" + this.tcpKeepAliveIdle + ", tcpKeepAliveInterval=" + this.tcpKeepAliveInterval + ", xpubVerbose=" + this.xpubVerbose + ", endpoints=" + this.endpoints + '}';
      }

      // $FF: synthetic method
      JeroMqConfiguration(long var1, long var3, boolean var5, byte[] var6, boolean var7, long var8, long var10, long var12, long var14, int var16, long var17, long var19, long var21, int var23, long var24, int var26, long var27, long var29, long var31, boolean var33, List var34, Object var35) {
         this(var1, var3, var5, var6, var7, var8, var10, var12, var14, var16, var17, var19, var21, var23, var24, var26, var27, var29, var31, var33, var34);
      }
   }
}
