package org.apache.logging.log4j.core.net;

import java.net.Socket;
import java.net.SocketException;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.util.Builder;

@Plugin(
   name = "SocketOptions",
   category = "Core",
   printObject = true
)
public class SocketOptions implements Builder<SocketOptions>, Cloneable {
   @PluginBuilderAttribute
   private Boolean keepAlive;
   @PluginBuilderAttribute
   private Boolean oobInline;
   @PluginElement("PerformancePreferences")
   private SocketPerformancePreferences performancePreferences;
   @PluginBuilderAttribute
   private Integer receiveBufferSize;
   @PluginBuilderAttribute
   private Boolean reuseAddress;
   @PluginBuilderAttribute
   private Rfc1349TrafficClass rfc1349TrafficClass;
   @PluginBuilderAttribute
   private Integer sendBufferSize;
   @PluginBuilderAttribute
   private Integer soLinger;
   @PluginBuilderAttribute
   private Integer soTimeout;
   @PluginBuilderAttribute
   private Boolean tcpNoDelay;
   @PluginBuilderAttribute
   private Integer trafficClass;

   public SocketOptions() {
      super();
   }

   @PluginBuilderFactory
   public static SocketOptions newBuilder() {
      return new SocketOptions();
   }

   public void apply(Socket var1) throws SocketException {
      if (this.keepAlive != null) {
         var1.setKeepAlive(this.keepAlive);
      }

      if (this.oobInline != null) {
         var1.setOOBInline(this.oobInline);
      }

      if (this.reuseAddress != null) {
         var1.setReuseAddress(this.reuseAddress);
      }

      if (this.performancePreferences != null) {
         this.performancePreferences.apply(var1);
      }

      if (this.receiveBufferSize != null) {
         var1.setReceiveBufferSize(this.receiveBufferSize);
      }

      if (this.soLinger != null) {
         var1.setSoLinger(true, this.soLinger);
      }

      if (this.soTimeout != null) {
         var1.setSoTimeout(this.soTimeout);
      }

      if (this.tcpNoDelay != null) {
         var1.setTcpNoDelay(this.tcpNoDelay);
      }

      Integer var2 = this.getActualTrafficClass();
      if (var2 != null) {
         var1.setTrafficClass(var2);
      }

   }

   public SocketOptions build() {
      try {
         return (SocketOptions)this.clone();
      } catch (CloneNotSupportedException var2) {
         throw new IllegalStateException(var2);
      }
   }

   public Integer getActualTrafficClass() {
      if (this.trafficClass != null && this.rfc1349TrafficClass != null) {
         throw new IllegalStateException("You MUST not set both customTrafficClass and trafficClass.");
      } else if (this.trafficClass != null) {
         return this.trafficClass;
      } else {
         return this.rfc1349TrafficClass != null ? this.rfc1349TrafficClass.value() : null;
      }
   }

   public SocketPerformancePreferences getPerformancePreferences() {
      return this.performancePreferences;
   }

   public Integer getReceiveBufferSize() {
      return this.receiveBufferSize;
   }

   public Rfc1349TrafficClass getRfc1349TrafficClass() {
      return this.rfc1349TrafficClass;
   }

   public Integer getSendBufferSize() {
      return this.sendBufferSize;
   }

   public Integer getSoLinger() {
      return this.soLinger;
   }

   public Integer getSoTimeout() {
      return this.soTimeout;
   }

   public Integer getTrafficClass() {
      return this.trafficClass;
   }

   public Boolean isKeepAlive() {
      return this.keepAlive;
   }

   public Boolean isOobInline() {
      return this.oobInline;
   }

   public Boolean isReuseAddress() {
      return this.reuseAddress;
   }

   public Boolean isTcpNoDelay() {
      return this.tcpNoDelay;
   }

   public void setKeepAlive(boolean var1) {
      this.keepAlive = var1;
   }

   public void setOobInline(boolean var1) {
      this.oobInline = var1;
   }

   public void setPerformancePreferences(SocketPerformancePreferences var1) {
      this.performancePreferences = var1;
   }

   public void setReceiveBufferSize(int var1) {
      this.receiveBufferSize = var1;
   }

   public void setReuseAddress(boolean var1) {
      this.reuseAddress = var1;
   }

   public void setRfc1349TrafficClass(Rfc1349TrafficClass var1) {
      this.rfc1349TrafficClass = var1;
   }

   public void setSendBufferSize(int var1) {
      this.sendBufferSize = var1;
   }

   public void setSoLinger(int var1) {
      this.soLinger = var1;
   }

   public void setSoTimeout(int var1) {
      this.soTimeout = var1;
   }

   public void setTcpNoDelay(boolean var1) {
      this.tcpNoDelay = var1;
   }

   public void setTrafficClass(int var1) {
      this.trafficClass = var1;
   }

   public String toString() {
      return "SocketOptions [keepAlive=" + this.keepAlive + ", oobInline=" + this.oobInline + ", performancePreferences=" + this.performancePreferences + ", receiveBufferSize=" + this.receiveBufferSize + ", reuseAddress=" + this.reuseAddress + ", rfc1349TrafficClass=" + this.rfc1349TrafficClass + ", sendBufferSize=" + this.sendBufferSize + ", soLinger=" + this.soLinger + ", soTimeout=" + this.soTimeout + ", tcpNoDelay=" + this.tcpNoDelay + ", trafficClass=" + this.trafficClass + "]";
   }
}
