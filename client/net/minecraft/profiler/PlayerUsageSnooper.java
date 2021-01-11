package net.minecraft.profiler;

import com.google.common.collect.Maps;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.util.HttpUtil;

public class PlayerUsageSnooper {
   private final Map<String, Object> field_152773_a = Maps.newHashMap();
   private final Map<String, Object> field_152774_b = Maps.newHashMap();
   private final String field_76480_b = UUID.randomUUID().toString();
   private final URL field_76481_c;
   private final IPlayerUsage field_76478_d;
   private final Timer field_76479_e = new Timer("Snooper Timer", true);
   private final Object field_76476_f = new Object();
   private final long field_98224_g;
   private boolean field_76477_g;
   private int field_76483_h;

   public PlayerUsageSnooper(String var1, IPlayerUsage var2, long var3) {
      super();

      try {
         this.field_76481_c = new URL("http://snoop.minecraft.net/" + var1 + "?version=" + 2);
      } catch (MalformedURLException var6) {
         throw new IllegalArgumentException();
      }

      this.field_76478_d = var2;
      this.field_98224_g = var3;
   }

   public void func_76463_a() {
      if (!this.field_76477_g) {
         this.field_76477_g = true;
         this.func_152766_h();
         this.field_76479_e.schedule(new TimerTask() {
            public void run() {
               if (PlayerUsageSnooper.this.field_76478_d.func_70002_Q()) {
                  HashMap var1;
                  synchronized(PlayerUsageSnooper.this.field_76476_f) {
                     var1 = Maps.newHashMap(PlayerUsageSnooper.this.field_152774_b);
                     if (PlayerUsageSnooper.this.field_76483_h == 0) {
                        var1.putAll(PlayerUsageSnooper.this.field_152773_a);
                     }

                     var1.put("snooper_count", PlayerUsageSnooper.this.field_76483_h++);
                     var1.put("snooper_token", PlayerUsageSnooper.this.field_76480_b);
                  }

                  HttpUtil.func_151226_a(PlayerUsageSnooper.this.field_76481_c, var1, true);
               }
            }
         }, 0L, 900000L);
      }
   }

   private void func_152766_h() {
      this.func_76467_g();
      this.func_152768_a("snooper_token", this.field_76480_b);
      this.func_152767_b("snooper_token", this.field_76480_b);
      this.func_152767_b("os_name", System.getProperty("os.name"));
      this.func_152767_b("os_version", System.getProperty("os.version"));
      this.func_152767_b("os_architecture", System.getProperty("os.arch"));
      this.func_152767_b("java_version", System.getProperty("java.version"));
      this.func_152768_a("version", "1.8.9");
      this.field_76478_d.func_70001_b(this);
   }

   private void func_76467_g() {
      RuntimeMXBean var1 = ManagementFactory.getRuntimeMXBean();
      List var2 = var1.getInputArguments();
      int var3 = 0;
      Iterator var4 = var2.iterator();

      while(var4.hasNext()) {
         String var5 = (String)var4.next();
         if (var5.startsWith("-X")) {
            this.func_152768_a("jvm_arg[" + var3++ + "]", var5);
         }
      }

      this.func_152768_a("jvm_args", var3);
   }

   public void func_76471_b() {
      this.func_152767_b("memory_total", Runtime.getRuntime().totalMemory());
      this.func_152767_b("memory_max", Runtime.getRuntime().maxMemory());
      this.func_152767_b("memory_free", Runtime.getRuntime().freeMemory());
      this.func_152767_b("cpu_cores", Runtime.getRuntime().availableProcessors());
      this.field_76478_d.func_70000_a(this);
   }

   public void func_152768_a(String var1, Object var2) {
      synchronized(this.field_76476_f) {
         this.field_152774_b.put(var1, var2);
      }
   }

   public void func_152767_b(String var1, Object var2) {
      synchronized(this.field_76476_f) {
         this.field_152773_a.put(var1, var2);
      }
   }

   public Map<String, String> func_76465_c() {
      LinkedHashMap var1 = Maps.newLinkedHashMap();
      synchronized(this.field_76476_f) {
         this.func_76471_b();
         Iterator var3 = this.field_152773_a.entrySet().iterator();

         Entry var4;
         while(var3.hasNext()) {
            var4 = (Entry)var3.next();
            var1.put(var4.getKey(), var4.getValue().toString());
         }

         var3 = this.field_152774_b.entrySet().iterator();

         while(var3.hasNext()) {
            var4 = (Entry)var3.next();
            var1.put(var4.getKey(), var4.getValue().toString());
         }

         return var1;
      }
   }

   public boolean func_76468_d() {
      return this.field_76477_g;
   }

   public void func_76470_e() {
      this.field_76479_e.cancel();
   }

   public String func_80006_f() {
      return this.field_76480_b;
   }

   public long func_130105_g() {
      return this.field_98224_g;
   }
}
