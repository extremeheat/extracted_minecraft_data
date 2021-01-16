package org.apache.logging.log4j.core.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.util.PerformanceSensitive;

@PerformanceSensitive
public class AppenderControlArraySet {
   private final AtomicReference<AppenderControl[]> appenderArray = new AtomicReference(new AppenderControl[0]);

   public AppenderControlArraySet() {
      super();
   }

   public boolean add(AppenderControl var1) {
      boolean var2;
      do {
         AppenderControl[] var3 = (AppenderControl[])this.appenderArray.get();
         AppenderControl[] var4 = var3;
         int var5 = var3.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            AppenderControl var7 = var4[var6];
            if (var7.equals(var1)) {
               return false;
            }
         }

         var4 = (AppenderControl[])Arrays.copyOf(var3, var3.length + 1);
         var4[var4.length - 1] = var1;
         var2 = this.appenderArray.compareAndSet(var3, var4);
      } while(!var2);

      return true;
   }

   public AppenderControl remove(String var1) {
      boolean var2;
      do {
         var2 = true;
         AppenderControl[] var3 = (AppenderControl[])this.appenderArray.get();

         for(int var4 = 0; var4 < var3.length; ++var4) {
            AppenderControl var5 = var3[var4];
            if (Objects.equals(var1, var5.getAppenderName())) {
               AppenderControl[] var6 = this.removeElementAt(var4, var3);
               if (this.appenderArray.compareAndSet(var3, var6)) {
                  return var5;
               }

               var2 = false;
               break;
            }
         }
      } while(!var2);

      return null;
   }

   private AppenderControl[] removeElementAt(int var1, AppenderControl[] var2) {
      AppenderControl[] var3 = (AppenderControl[])Arrays.copyOf(var2, var2.length - 1);
      System.arraycopy(var2, var1 + 1, var3, var1, var3.length - var1);
      return var3;
   }

   public Map<String, Appender> asMap() {
      HashMap var1 = new HashMap();
      AppenderControl[] var2 = (AppenderControl[])this.appenderArray.get();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         AppenderControl var5 = var2[var4];
         var1.put(var5.getAppenderName(), var5.getAppender());
      }

      return var1;
   }

   public AppenderControl[] clear() {
      return (AppenderControl[])this.appenderArray.getAndSet(new AppenderControl[0]);
   }

   public boolean isEmpty() {
      return ((AppenderControl[])this.appenderArray.get()).length == 0;
   }

   public AppenderControl[] get() {
      return (AppenderControl[])this.appenderArray.get();
   }
}
