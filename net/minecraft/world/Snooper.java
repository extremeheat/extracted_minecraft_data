package net.minecraft.world;

import com.google.common.collect.Maps;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Timer;
import java.util.UUID;

public class Snooper {
   private final Map fixedData = Maps.newHashMap();
   private final Map dynamicData = Maps.newHashMap();
   private final String token = UUID.randomUUID().toString();
   private final URL url;
   private final SnooperPopulator populator;
   private final Timer timer = new Timer("Snooper Timer", true);
   private final Object lock = new Object();
   private final long startupTime;
   private boolean started;

   public Snooper(String var1, SnooperPopulator var2, long var3) {
      try {
         this.url = new URL("http://snoop.minecraft.net/" + var1 + "?version=" + 2);
      } catch (MalformedURLException var6) {
         throw new IllegalArgumentException();
      }

      this.populator = var2;
      this.startupTime = var3;
   }

   public void start() {
      if (!this.started) {
      }

   }

   public void prepare() {
      this.setFixedData("memory_total", Runtime.getRuntime().totalMemory());
      this.setFixedData("memory_max", Runtime.getRuntime().maxMemory());
      this.setFixedData("memory_free", Runtime.getRuntime().freeMemory());
      this.setFixedData("cpu_cores", Runtime.getRuntime().availableProcessors());
      this.populator.populateSnooper(this);
   }

   public void setDynamicData(String var1, Object var2) {
      synchronized(this.lock) {
         this.dynamicData.put(var1, var2);
      }
   }

   public void setFixedData(String var1, Object var2) {
      synchronized(this.lock) {
         this.fixedData.put(var1, var2);
      }
   }

   public boolean isStarted() {
      return this.started;
   }

   public void interrupt() {
      this.timer.cancel();
   }

   public String getToken() {
      return this.token;
   }

   public long getStartupTime() {
      return this.startupTime;
   }
}
