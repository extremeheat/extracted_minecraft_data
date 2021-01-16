package org.apache.logging.log4j.core.jmx;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;
import javax.management.MBeanNotificationInfo;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.ObjectName;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.status.StatusData;
import org.apache.logging.log4j.status.StatusListener;
import org.apache.logging.log4j.status.StatusLogger;

public class StatusLoggerAdmin extends NotificationBroadcasterSupport implements StatusListener, StatusLoggerAdminMBean {
   private final AtomicLong sequenceNo = new AtomicLong();
   private final ObjectName objectName;
   private final String contextName;
   private Level level;

   public StatusLoggerAdmin(String var1, Executor var2) {
      super(var2, new MBeanNotificationInfo[]{createNotificationInfo()});
      this.level = Level.WARN;
      this.contextName = var1;

      try {
         String var3 = String.format("org.apache.logging.log4j2:type=%s,component=StatusLogger", Server.escape(var1));
         this.objectName = new ObjectName(var3);
      } catch (Exception var4) {
         throw new IllegalStateException(var4);
      }

      this.removeListeners(var1);
      StatusLogger.getLogger().registerListener(this);
   }

   private void removeListeners(String var1) {
      StatusLogger var2 = StatusLogger.getLogger();
      Iterable var3 = var2.getListeners();
      Iterator var4 = var3.iterator();

      while(var4.hasNext()) {
         StatusListener var5 = (StatusListener)var4.next();
         if (var5 instanceof StatusLoggerAdmin) {
            StatusLoggerAdmin var6 = (StatusLoggerAdmin)var5;
            if (var1 != null && var1.equals(var6.contextName)) {
               var2.removeListener(var6);
            }
         }
      }

   }

   private static MBeanNotificationInfo createNotificationInfo() {
      String[] var0 = new String[]{"com.apache.logging.log4j.core.jmx.statuslogger.data", "com.apache.logging.log4j.core.jmx.statuslogger.message"};
      String var1 = Notification.class.getName();
      String var2 = "StatusLogger has logged an event";
      return new MBeanNotificationInfo(var0, var1, "StatusLogger has logged an event");
   }

   public String[] getStatusDataHistory() {
      List var1 = this.getStatusData();
      String[] var2 = new String[var1.size()];

      for(int var3 = 0; var3 < var2.length; ++var3) {
         var2[var3] = ((StatusData)var1.get(var3)).getFormattedStatus();
      }

      return var2;
   }

   public List<StatusData> getStatusData() {
      return StatusLogger.getLogger().getStatusData();
   }

   public String getLevel() {
      return this.level.name();
   }

   public Level getStatusLevel() {
      return this.level;
   }

   public void setLevel(String var1) {
      this.level = Level.toLevel(var1, Level.ERROR);
   }

   public String getContextName() {
      return this.contextName;
   }

   public void log(StatusData var1) {
      Notification var2 = new Notification("com.apache.logging.log4j.core.jmx.statuslogger.message", this.getObjectName(), this.nextSeqNo(), this.nowMillis(), var1.getFormattedStatus());
      this.sendNotification(var2);
      Notification var3 = new Notification("com.apache.logging.log4j.core.jmx.statuslogger.data", this.getObjectName(), this.nextSeqNo(), this.nowMillis());
      var3.setUserData(var1);
      this.sendNotification(var3);
   }

   public ObjectName getObjectName() {
      return this.objectName;
   }

   private long nextSeqNo() {
      return this.sequenceNo.getAndIncrement();
   }

   private long nowMillis() {
      return System.currentTimeMillis();
   }

   public void close() throws IOException {
   }
}
