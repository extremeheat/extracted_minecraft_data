package net.minecraft.gametest.framework;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MultipleTestTracker {
   private static final char NOT_STARTED_TEST_CHAR = ' ';
   private static final char ONGOING_TEST_CHAR = '_';
   private static final char SUCCESSFUL_TEST_CHAR = '+';
   private static final char FAILED_OPTIONAL_TEST_CHAR = 'x';
   private static final char FAILED_REQUIRED_TEST_CHAR = 'X';
   private final Collection<GameTestInfo> tests = Lists.newArrayList();
   private final Collection<GameTestListener> listeners = Lists.newArrayList();

   public MultipleTestTracker() {
      super();
   }

   public MultipleTestTracker(Collection<GameTestInfo> var1) {
      super();
      this.tests.addAll(var1);
   }

   public void addTestToTrack(GameTestInfo var1) {
      this.tests.add(var1);
      Collection var10000 = this.listeners;
      Objects.requireNonNull(var1);
      var10000.forEach(var1::addListener);
   }

   public void addListener(GameTestListener var1) {
      this.listeners.add(var1);
      this.tests.forEach((var1x) -> {
         var1x.addListener(var1);
      });
   }

   public void addFailureListener(final Consumer<GameTestInfo> var1) {
      this.addListener(new GameTestListener(this) {
         public void testStructureLoaded(GameTestInfo var1x) {
         }

         public void testPassed(GameTestInfo var1x, GameTestRunner var2) {
         }

         public void testFailed(GameTestInfo var1x, GameTestRunner var2) {
            var1.accept(var1x);
         }

         public void testAddedForRerun(GameTestInfo var1x, GameTestInfo var2, GameTestRunner var3) {
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

   public Collection<GameTestInfo> getFailedRequired() {
      return (Collection)this.tests.stream().filter(GameTestInfo::hasFailed).filter(GameTestInfo::isRequired).collect(Collectors.toList());
   }

   public Collection<GameTestInfo> getFailedOptional() {
      return (Collection)this.tests.stream().filter(GameTestInfo::hasFailed).filter(GameTestInfo::isOptional).collect(Collectors.toList());
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

   public void remove(GameTestInfo var1) {
      this.tests.remove(var1);
   }
}
