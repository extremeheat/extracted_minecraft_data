package net.minecraft.gametest.framework;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.function.Consumer;
import javax.annotation.Nullable;

public class MultipleTestTracker {
   private final Collection<GameTestInfo> tests = Lists.newArrayList();
   @Nullable
   private Collection<GameTestListener> listeners = Lists.newArrayList();

   public MultipleTestTracker() {
      super();
   }

   public MultipleTestTracker(Collection<GameTestInfo> var1) {
      super();
      this.tests.addAll(var1);
   }

   public void addTestToTrack(GameTestInfo var1) {
      this.tests.add(var1);
      this.listeners.forEach(var1::addListener);
   }

   public void addListener(GameTestListener var1) {
      this.listeners.add(var1);
      this.tests.forEach((var1x) -> {
         var1x.addListener(var1);
      });
   }

   public void addFailureListener(final Consumer<GameTestInfo> var1) {
      this.addListener(new GameTestListener() {
         public void testStructureLoaded(GameTestInfo var1x) {
         }

         public void testFailed(GameTestInfo var1x) {
            var1.accept(var1x);
         }
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
