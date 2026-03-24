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

   public MultipleTestTracker(final Collection<GameTestInfo> tests) {
      super();
      this.tests.addAll(tests);
   }

   public void addTestToTrack(final GameTestInfo testInfo) {
      this.tests.add(testInfo);
      Collection var10000 = this.listeners;
      Objects.requireNonNull(testInfo);
      var10000.forEach(testInfo::addListener);
   }

   public void addListener(final GameTestListener listener) {
      this.listeners.add(listener);
      this.tests.forEach((testInfo) -> testInfo.addListener(listener));
   }

   public void addFailureListener(final Consumer<GameTestInfo> listener) {
      this.addListener(new GameTestListener() {
         {
            Objects.requireNonNull(MultipleTestTracker.this);
         }

         public void testStructureLoaded(final GameTestInfo testInfo) {
         }

         public void testPassed(final GameTestInfo testInfo, final GameTestRunner runner) {
         }

         public void testFailed(final GameTestInfo testInfo, final GameTestRunner runner) {
            listener.accept(testInfo);
         }

         public void testAddedForRerun(final GameTestInfo original, final GameTestInfo copy, final GameTestRunner runner) {
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
      StringBuffer buf = new StringBuffer();
      buf.append('[');
      this.tests.forEach((test) -> {
         if (!test.hasStarted()) {
            buf.append(' ');
         } else if (test.hasSucceeded()) {
            buf.append('+');
         } else if (test.hasFailed()) {
            buf.append((char)(test.isRequired() ? 'X' : 'x'));
         } else {
            buf.append('_');
         }

      });
      buf.append(']');
      return buf.toString();
   }

   public String toString() {
      return this.getProgressBar();
   }

   public void remove(final GameTestInfo testInfo) {
      this.tests.remove(testInfo);
   }
}
