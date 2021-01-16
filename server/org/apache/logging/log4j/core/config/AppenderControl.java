package org.apache.logging.log4j.core.config;

import java.util.Objects;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.filter.AbstractFilterable;
import org.apache.logging.log4j.core.filter.Filterable;
import org.apache.logging.log4j.util.PerformanceSensitive;

public class AppenderControl extends AbstractFilterable {
   private final ThreadLocal<AppenderControl> recursive = new ThreadLocal();
   private final Appender appender;
   private final Level level;
   private final int intLevel;
   private final String appenderName;

   public AppenderControl(Appender var1, Level var2, Filter var3) {
      super(var3);
      this.appender = var1;
      this.appenderName = var1.getName();
      this.level = var2;
      this.intLevel = var2 == null ? Level.ALL.intLevel() : var2.intLevel();
      this.start();
   }

   public String getAppenderName() {
      return this.appenderName;
   }

   public Appender getAppender() {
      return this.appender;
   }

   public void callAppender(LogEvent var1) {
      if (!this.shouldSkip(var1)) {
         this.callAppenderPreventRecursion(var1);
      }
   }

   private boolean shouldSkip(LogEvent var1) {
      return this.isFilteredByAppenderControl(var1) || this.isFilteredByLevel(var1) || this.isRecursiveCall();
   }

   @PerformanceSensitive
   private boolean isFilteredByAppenderControl(LogEvent var1) {
      Filter var2 = this.getFilter();
      return var2 != null && Filter.Result.DENY == var2.filter(var1);
   }

   @PerformanceSensitive
   private boolean isFilteredByLevel(LogEvent var1) {
      return this.level != null && this.intLevel < var1.getLevel().intLevel();
   }

   @PerformanceSensitive
   private boolean isRecursiveCall() {
      if (this.recursive.get() != null) {
         this.appenderErrorHandlerMessage("Recursive call to appender ");
         return true;
      } else {
         return false;
      }
   }

   private String appenderErrorHandlerMessage(String var1) {
      String var2 = this.createErrorMsg(var1);
      this.appender.getHandler().error(var2);
      return var2;
   }

   private void callAppenderPreventRecursion(LogEvent var1) {
      try {
         this.recursive.set(this);
         this.callAppender0(var1);
      } finally {
         this.recursive.set((Object)null);
      }

   }

   private void callAppender0(LogEvent var1) {
      this.ensureAppenderStarted();
      if (!this.isFilteredByAppender(var1)) {
         this.tryCallAppender(var1);
      }

   }

   private void ensureAppenderStarted() {
      if (!this.appender.isStarted()) {
         this.handleError("Attempted to append to non-started appender ");
      }

   }

   private void handleError(String var1) {
      String var2 = this.appenderErrorHandlerMessage(var1);
      if (!this.appender.ignoreExceptions()) {
         throw new AppenderLoggingException(var2);
      }
   }

   private String createErrorMsg(String var1) {
      return var1 + this.appender.getName();
   }

   private boolean isFilteredByAppender(LogEvent var1) {
      return this.appender instanceof Filterable && ((Filterable)this.appender).isFiltered(var1);
   }

   private void tryCallAppender(LogEvent var1) {
      try {
         this.appender.append(var1);
      } catch (RuntimeException var3) {
         this.handleAppenderError(var3);
      } catch (Exception var4) {
         this.handleAppenderError(new AppenderLoggingException(var4));
      }

   }

   private void handleAppenderError(RuntimeException var1) {
      this.appender.getHandler().error(this.createErrorMsg("An exception occurred processing Appender "), var1);
      if (!this.appender.ignoreExceptions()) {
         throw var1;
      }
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof AppenderControl)) {
         return false;
      } else {
         AppenderControl var2 = (AppenderControl)var1;
         return Objects.equals(this.appenderName, var2.appenderName);
      }
   }

   public int hashCode() {
      return this.appenderName.hashCode();
   }

   public String toString() {
      return super.toString() + "[appender=" + this.appender + ", appenderName=" + this.appenderName + ", level=" + this.level + ", intLevel=" + this.intLevel + ", recursive=" + this.recursive + ", filter=" + this.getFilter() + "]";
   }
}
