package org.apache.logging.log4j.core.net;

import java.net.Socket;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.util.Builder;

@Plugin(
   name = "SocketPerformancePreferences",
   category = "Core",
   printObject = true
)
public class SocketPerformancePreferences implements Builder<SocketPerformancePreferences>, Cloneable {
   @PluginBuilderAttribute
   @Required
   private int bandwidth;
   @PluginBuilderAttribute
   @Required
   private int connectionTime;
   @PluginBuilderAttribute
   @Required
   private int latency;

   public SocketPerformancePreferences() {
      super();
   }

   @PluginBuilderFactory
   public static SocketPerformancePreferences newBuilder() {
      return new SocketPerformancePreferences();
   }

   public void apply(Socket var1) {
      var1.setPerformancePreferences(this.connectionTime, this.latency, this.bandwidth);
   }

   public SocketPerformancePreferences build() {
      try {
         return (SocketPerformancePreferences)this.clone();
      } catch (CloneNotSupportedException var2) {
         throw new IllegalStateException(var2);
      }
   }

   public int getBandwidth() {
      return this.bandwidth;
   }

   public int getConnectionTime() {
      return this.connectionTime;
   }

   public int getLatency() {
      return this.latency;
   }

   public void setBandwidth(int var1) {
      this.bandwidth = var1;
   }

   public void setConnectionTime(int var1) {
      this.connectionTime = var1;
   }

   public void setLatency(int var1) {
      this.latency = var1;
   }

   public String toString() {
      return "SocketPerformancePreferences [bandwidth=" + this.bandwidth + ", connectionTime=" + this.connectionTime + ", latency=" + this.latency + "]";
   }
}
