package io.netty.channel.rxtx;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.channel.oio.OioByteStreamChannel;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

/** @deprecated */
@Deprecated
public class RxtxChannel extends OioByteStreamChannel {
   private static final RxtxDeviceAddress LOCAL_ADDRESS = new RxtxDeviceAddress("localhost");
   private final RxtxChannelConfig config = new DefaultRxtxChannelConfig(this);
   private boolean open = true;
   private RxtxDeviceAddress deviceAddress;
   private SerialPort serialPort;

   public RxtxChannel() {
      super((Channel)null);
   }

   public RxtxChannelConfig config() {
      return this.config;
   }

   public boolean isOpen() {
      return this.open;
   }

   protected AbstractChannel.AbstractUnsafe newUnsafe() {
      return new RxtxChannel.RxtxUnsafe();
   }

   protected void doConnect(SocketAddress var1, SocketAddress var2) throws Exception {
      RxtxDeviceAddress var3 = (RxtxDeviceAddress)var1;
      CommPortIdentifier var4 = CommPortIdentifier.getPortIdentifier(var3.value());
      CommPort var5 = var4.open(this.getClass().getName(), 1000);
      var5.enableReceiveTimeout((Integer)this.config().getOption(RxtxChannelOption.READ_TIMEOUT));
      this.deviceAddress = var3;
      this.serialPort = (SerialPort)var5;
   }

   protected void doInit() throws Exception {
      this.serialPort.setSerialPortParams((Integer)this.config().getOption(RxtxChannelOption.BAUD_RATE), ((RxtxChannelConfig.Databits)this.config().getOption(RxtxChannelOption.DATA_BITS)).value(), ((RxtxChannelConfig.Stopbits)this.config().getOption(RxtxChannelOption.STOP_BITS)).value(), ((RxtxChannelConfig.Paritybit)this.config().getOption(RxtxChannelOption.PARITY_BIT)).value());
      this.serialPort.setDTR((Boolean)this.config().getOption(RxtxChannelOption.DTR));
      this.serialPort.setRTS((Boolean)this.config().getOption(RxtxChannelOption.RTS));
      this.activate(this.serialPort.getInputStream(), this.serialPort.getOutputStream());
   }

   public RxtxDeviceAddress localAddress() {
      return (RxtxDeviceAddress)super.localAddress();
   }

   public RxtxDeviceAddress remoteAddress() {
      return (RxtxDeviceAddress)super.remoteAddress();
   }

   protected RxtxDeviceAddress localAddress0() {
      return LOCAL_ADDRESS;
   }

   protected RxtxDeviceAddress remoteAddress0() {
      return this.deviceAddress;
   }

   protected void doBind(SocketAddress var1) throws Exception {
      throw new UnsupportedOperationException();
   }

   protected void doDisconnect() throws Exception {
      this.doClose();
   }

   protected void doClose() throws Exception {
      this.open = false;

      try {
         super.doClose();
      } finally {
         if (this.serialPort != null) {
            this.serialPort.removeEventListener();
            this.serialPort.close();
            this.serialPort = null;
         }

      }

   }

   protected boolean isInputShutdown() {
      return !this.open;
   }

   protected ChannelFuture shutdownInput() {
      return this.newFailedFuture(new UnsupportedOperationException("shutdownInput"));
   }

   private final class RxtxUnsafe extends AbstractChannel.AbstractUnsafe {
      private RxtxUnsafe() {
         super();
      }

      public void connect(SocketAddress var1, SocketAddress var2, final ChannelPromise var3) {
         if (var3.setUncancellable() && this.ensureOpen(var3)) {
            try {
               final boolean var4 = RxtxChannel.this.isActive();
               RxtxChannel.this.doConnect(var1, var2);
               int var5 = (Integer)RxtxChannel.this.config().getOption(RxtxChannelOption.WAIT_TIME);
               if (var5 > 0) {
                  RxtxChannel.this.eventLoop().schedule(new Runnable() {
                     public void run() {
                        try {
                           RxtxChannel.this.doInit();
                           RxtxUnsafe.this.safeSetSuccess(var3);
                           if (!var4 && RxtxChannel.this.isActive()) {
                              RxtxChannel.this.pipeline().fireChannelActive();
                           }
                        } catch (Throwable var2) {
                           RxtxUnsafe.this.safeSetFailure(var3, var2);
                           RxtxUnsafe.this.closeIfClosed();
                        }

                     }
                  }, (long)var5, TimeUnit.MILLISECONDS);
               } else {
                  RxtxChannel.this.doInit();
                  this.safeSetSuccess(var3);
                  if (!var4 && RxtxChannel.this.isActive()) {
                     RxtxChannel.this.pipeline().fireChannelActive();
                  }
               }
            } catch (Throwable var6) {
               this.safeSetFailure(var3, var6);
               this.closeIfClosed();
            }

         }
      }

      // $FF: synthetic method
      RxtxUnsafe(Object var2) {
         this();
      }
   }
}
