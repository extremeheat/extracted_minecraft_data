package net.minecraft.gametest.framework;

import com.google.common.collect.Lists;
import java.util.Collection;
import javax.annotation.Nullable;

public class MultipleTestTracker {
   private final Collection tests = Lists.newArrayList();
   @Nullable
   private GameTestListener listener;

   public MultipleTestTracker() {
   }

   public MultipleTestTracker(Collection var1) {
      this.tests.addAll(var1);
   }

   public void add(GameTestInfo var1) {
      this.tests.add(var1);
      if (this.listener != null) {
         var1.addListener(this.listener);
      }

   }

   public void setListener(GameTestListener var1) {
      this.listener = var1;
      this.tests.forEach((var1x) -> {
         var1x.addListener(var1);
      });
   }

   public int getFailedRequiredCount() {
      return (int)this.tests.stream().filter(GameTestInfo::hasFailed).filter(GameTestInfo::isRequired).count();
   }

   public int getFailedOptionalCount() {
      return (int)this.tests.stream().filter(GameTestInfo::hasFailed).filter(GameTestInfo::isOptional).count();
   }

   public int getDoneCount() {
      return (int)this.tests.stream().filter(GameTestInfo::isDone).count();
   }

   public boolean hasFailedRequired() {
      return this.getFailedRequiredCount() > 0;
   }

   public boolean hasFailedOptional() {
      return this.getFailedOptionalCount() > 0;
   }

   public int getTotalCount() {
      return this.tests.size();
   }

   public boolean isDone() {
      return this.getDoneCount() == this.getTotalCount();
   }

   public String getProgressBar() {
      StringBuffer var1 = new StringBuffer();
      var1.append('[');
      this.tests.forEach((var1x) -> {
         if (!var1x.hasStarted()) {
            var1.append(' ');
         } else if (var1x.hasSucceeded()) {
            var1.append('+');
         } else if (var1x.hasFailed()) {
            var1.append((char)(var1x.isRequired() ? 'X' : 'x'));
         } else {
            var1.append('_');
         }

      });
      var1.append(']');
      return var1.toString();
   }

   public String toString() {
      return this.getProgressBar();
   }
}
