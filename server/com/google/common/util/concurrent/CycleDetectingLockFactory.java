package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.j2objc.annotations.Weak;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

@Beta
@CanIgnoreReturnValue
@ThreadSafe
@GwtIncompatible
public class CycleDetectingLockFactory {
   private static final ConcurrentMap<Class<? extends Enum>, Map<? extends Enum, CycleDetectingLockFactory.LockGraphNode>> lockGraphNodesPerType = (new MapMaker()).weakKeys().makeMap();
   private static final Logger logger = Logger.getLogger(CycleDetectingLockFactory.class.getName());
   final CycleDetectingLockFactory.Policy policy;
   private static final ThreadLocal<ArrayList<CycleDetectingLockFactory.LockGraphNode>> acquiredLocks = new ThreadLocal<ArrayList<CycleDetectingLockFactory.LockGraphNode>>() {
      protected ArrayList<CycleDetectingLockFactory.LockGraphNode> initialValue() {
         return Lists.newArrayListWithCapacity(3);
      }
   };

   public static CycleDetectingLockFactory newInstance(CycleDetectingLockFactory.Policy var0) {
      return new CycleDetectingLockFactory(var0);
   }

   public ReentrantLock newReentrantLock(String var1) {
      return this.newReentrantLock(var1, false);
   }

   public ReentrantLock newReentrantLock(String var1, boolean var2) {
      return (ReentrantLock)(this.policy == CycleDetectingLockFactory.Policies.DISABLED ? new ReentrantLock(var2) : new CycleDetectingLockFactory.CycleDetectingReentrantLock(new CycleDetectingLockFactory.LockGraphNode(var1), var2));
   }

   public ReentrantReadWriteLock newReentrantReadWriteLock(String var1) {
      return this.newReentrantReadWriteLock(var1, false);
   }

   public ReentrantReadWriteLock newReentrantReadWriteLock(String var1, boolean var2) {
      return (ReentrantReadWriteLock)(this.policy == CycleDetectingLockFactory.Policies.DISABLED ? new ReentrantReadWriteLock(var2) : new CycleDetectingLockFactory.CycleDetectingReentrantReadWriteLock(new CycleDetectingLockFactory.LockGraphNode(var1), var2));
   }

   public static <E extends Enum<E>> CycleDetectingLockFactory.WithExplicitOrdering<E> newInstanceWithExplicitOrdering(Class<E> var0, CycleDetectingLockFactory.Policy var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      Map var2 = getOrCreateNodes(var0);
      return new CycleDetectingLockFactory.WithExplicitOrdering(var1, var2);
   }

   private static Map<? extends Enum, CycleDetectingLockFactory.LockGraphNode> getOrCreateNodes(Class<? extends Enum> var0) {
      Map var1 = (Map)lockGraphNodesPerType.get(var0);
      if (var1 != null) {
         return var1;
      } else {
         Map var2 = createNodes(var0);
         var1 = (Map)lockGraphNodesPerType.putIfAbsent(var0, var2);
         return (Map)MoreObjects.firstNonNull(var1, var2);
      }
   }

   @VisibleForTesting
   static <E extends Enum<E>> Map<E, CycleDetectingLockFactory.LockGraphNode> createNodes(Class<E> var0) {
      EnumMap var1 = Maps.newEnumMap(var0);
      Enum[] var2 = (Enum[])var0.getEnumConstants();
      int var3 = var2.length;
      ArrayList var4 = Lists.newArrayListWithCapacity(var3);
      Enum[] var5 = var2;
      int var6 = var2.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         Enum var8 = var5[var7];
         CycleDetectingLockFactory.LockGraphNode var9 = new CycleDetectingLockFactory.LockGraphNode(getLockName(var8));
         var4.add(var9);
         var1.put(var8, var9);
      }

      int var10;
      for(var10 = 1; var10 < var3; ++var10) {
         ((CycleDetectingLockFactory.LockGraphNode)var4.get(var10)).checkAcquiredLocks(CycleDetectingLockFactory.Policies.THROW, var4.subList(0, var10));
      }

      for(var10 = 0; var10 < var3 - 1; ++var10) {
         ((CycleDetectingLockFactory.LockGraphNode)var4.get(var10)).checkAcquiredLocks(CycleDetectingLockFactory.Policies.DISABLED, var4.subList(var10 + 1, var3));
      }

      return Collections.unmodifiableMap(var1);
   }

   private static String getLockName(Enum<?> var0) {
      return var0.getDeclaringClass().getSimpleName() + "." + var0.name();
   }

   private CycleDetectingLockFactory(CycleDetectingLockFactory.Policy var1) {
      super();
      this.policy = (CycleDetectingLockFactory.Policy)Preconditions.checkNotNull(var1);
   }

   private void aboutToAcquire(CycleDetectingLockFactory.CycleDetectingLock var1) {
      if (!var1.isAcquiredByCurrentThread()) {
         ArrayList var2 = (ArrayList)acquiredLocks.get();
         CycleDetectingLockFactory.LockGraphNode var3 = var1.getLockGraphNode();
         var3.checkAcquiredLocks(this.policy, var2);
         var2.add(var3);
      }

   }

   private static void lockStateChanged(CycleDetectingLockFactory.CycleDetectingLock var0) {
      if (!var0.isAcquiredByCurrentThread()) {
         ArrayList var1 = (ArrayList)acquiredLocks.get();
         CycleDetectingLockFactory.LockGraphNode var2 = var0.getLockGraphNode();

         for(int var3 = var1.size() - 1; var3 >= 0; --var3) {
            if (var1.get(var3) == var2) {
               var1.remove(var3);
               break;
            }
         }
      }

   }

   // $FF: synthetic method
   CycleDetectingLockFactory(CycleDetectingLockFactory.Policy var1, Object var2) {
      this(var1);
   }

   private class CycleDetectingReentrantWriteLock extends WriteLock {
      @Weak
      final CycleDetectingLockFactory.CycleDetectingReentrantReadWriteLock readWriteLock;

      CycleDetectingReentrantWriteLock(CycleDetectingLockFactory.CycleDetectingReentrantReadWriteLock var2) {
         super(var2);
         this.readWriteLock = var2;
      }

      public void lock() {
         CycleDetectingLockFactory.this.aboutToAcquire(this.readWriteLock);

         try {
            super.lock();
         } finally {
            CycleDetectingLockFactory.lockStateChanged(this.readWriteLock);
         }

      }

      public void lockInterruptibly() throws InterruptedException {
         CycleDetectingLockFactory.this.aboutToAcquire(this.readWriteLock);

         try {
            super.lockInterruptibly();
         } finally {
            CycleDetectingLockFactory.lockStateChanged(this.readWriteLock);
         }

      }

      public boolean tryLock() {
         CycleDetectingLockFactory.this.aboutToAcquire(this.readWriteLock);

         boolean var1;
         try {
            var1 = super.tryLock();
         } finally {
            CycleDetectingLockFactory.lockStateChanged(this.readWriteLock);
         }

         return var1;
      }

      public boolean tryLock(long var1, TimeUnit var3) throws InterruptedException {
         CycleDetectingLockFactory.this.aboutToAcquire(this.readWriteLock);

         boolean var4;
         try {
            var4 = super.tryLock(var1, var3);
         } finally {
            CycleDetectingLockFactory.lockStateChanged(this.readWriteLock);
         }

         return var4;
      }

      public void unlock() {
         try {
            super.unlock();
         } finally {
            CycleDetectingLockFactory.lockStateChanged(this.readWriteLock);
         }

      }
   }

   private class CycleDetectingReentrantReadLock extends ReadLock {
      @Weak
      final CycleDetectingLockFactory.CycleDetectingReentrantReadWriteLock readWriteLock;

      CycleDetectingReentrantReadLock(CycleDetectingLockFactory.CycleDetectingReentrantReadWriteLock var2) {
         super(var2);
         this.readWriteLock = var2;
      }

      public void lock() {
         CycleDetectingLockFactory.this.aboutToAcquire(this.readWriteLock);

         try {
            super.lock();
         } finally {
            CycleDetectingLockFactory.lockStateChanged(this.readWriteLock);
         }

      }

      public void lockInterruptibly() throws InterruptedException {
         CycleDetectingLockFactory.this.aboutToAcquire(this.readWriteLock);

         try {
            super.lockInterruptibly();
         } finally {
            CycleDetectingLockFactory.lockStateChanged(this.readWriteLock);
         }

      }

      public boolean tryLock() {
         CycleDetectingLockFactory.this.aboutToAcquire(this.readWriteLock);

         boolean var1;
         try {
            var1 = super.tryLock();
         } finally {
            CycleDetectingLockFactory.lockStateChanged(this.readWriteLock);
         }

         return var1;
      }

      public boolean tryLock(long var1, TimeUnit var3) throws InterruptedException {
         CycleDetectingLockFactory.this.aboutToAcquire(this.readWriteLock);

         boolean var4;
         try {
            var4 = super.tryLock(var1, var3);
         } finally {
            CycleDetectingLockFactory.lockStateChanged(this.readWriteLock);
         }

         return var4;
      }

      public void unlock() {
         try {
            super.unlock();
         } finally {
            CycleDetectingLockFactory.lockStateChanged(this.readWriteLock);
         }

      }
   }

   final class CycleDetectingReentrantReadWriteLock extends ReentrantReadWriteLock implements CycleDetectingLockFactory.CycleDetectingLock {
      private final CycleDetectingLockFactory.CycleDetectingReentrantReadLock readLock;
      private final CycleDetectingLockFactory.CycleDetectingReentrantWriteLock writeLock;
      private final CycleDetectingLockFactory.LockGraphNode lockGraphNode;

      private CycleDetectingReentrantReadWriteLock(CycleDetectingLockFactory.LockGraphNode var2, boolean var3) {
         super(var3);
         this.readLock = CycleDetectingLockFactory.this.new CycleDetectingReentrantReadLock(this);
         this.writeLock = CycleDetectingLockFactory.this.new CycleDetectingReentrantWriteLock(this);
         this.lockGraphNode = (CycleDetectingLockFactory.LockGraphNode)Preconditions.checkNotNull(var2);
      }

      public ReadLock readLock() {
         return this.readLock;
      }

      public WriteLock writeLock() {
         return this.writeLock;
      }

      public CycleDetectingLockFactory.LockGraphNode getLockGraphNode() {
         return this.lockGraphNode;
      }

      public boolean isAcquiredByCurrentThread() {
         return this.isWriteLockedByCurrentThread() || this.getReadHoldCount() > 0;
      }

      // $FF: synthetic method
      CycleDetectingReentrantReadWriteLock(CycleDetectingLockFactory.LockGraphNode var2, boolean var3, Object var4) {
         this(var2, var3);
      }
   }

   final class CycleDetectingReentrantLock extends ReentrantLock implements CycleDetectingLockFactory.CycleDetectingLock {
      private final CycleDetectingLockFactory.LockGraphNode lockGraphNode;

      private CycleDetectingReentrantLock(CycleDetectingLockFactory.LockGraphNode var2, boolean var3) {
         super(var3);
         this.lockGraphNode = (CycleDetectingLockFactory.LockGraphNode)Preconditions.checkNotNull(var2);
      }

      public CycleDetectingLockFactory.LockGraphNode getLockGraphNode() {
         return this.lockGraphNode;
      }

      public boolean isAcquiredByCurrentThread() {
         return this.isHeldByCurrentThread();
      }

      public void lock() {
         CycleDetectingLockFactory.this.aboutToAcquire(this);

         try {
            super.lock();
         } finally {
            CycleDetectingLockFactory.lockStateChanged(this);
         }

      }

      public void lockInterruptibly() throws InterruptedException {
         CycleDetectingLockFactory.this.aboutToAcquire(this);

         try {
            super.lockInterruptibly();
         } finally {
            CycleDetectingLockFactory.lockStateChanged(this);
         }

      }

      public boolean tryLock() {
         CycleDetectingLockFactory.this.aboutToAcquire(this);

         boolean var1;
         try {
            var1 = super.tryLock();
         } finally {
            CycleDetectingLockFactory.lockStateChanged(this);
         }

         return var1;
      }

      public boolean tryLock(long var1, TimeUnit var3) throws InterruptedException {
         CycleDetectingLockFactory.this.aboutToAcquire(this);

         boolean var4;
         try {
            var4 = super.tryLock(var1, var3);
         } finally {
            CycleDetectingLockFactory.lockStateChanged(this);
         }

         return var4;
      }

      public void unlock() {
         try {
            super.unlock();
         } finally {
            CycleDetectingLockFactory.lockStateChanged(this);
         }

      }

      // $FF: synthetic method
      CycleDetectingReentrantLock(CycleDetectingLockFactory.LockGraphNode var2, boolean var3, Object var4) {
         this(var2, var3);
      }
   }

   private static class LockGraphNode {
      final Map<CycleDetectingLockFactory.LockGraphNode, CycleDetectingLockFactory.ExampleStackTrace> allowedPriorLocks = (new MapMaker()).weakKeys().makeMap();
      final Map<CycleDetectingLockFactory.LockGraphNode, CycleDetectingLockFactory.PotentialDeadlockException> disallowedPriorLocks = (new MapMaker()).weakKeys().makeMap();
      final String lockName;

      LockGraphNode(String var1) {
         super();
         this.lockName = (String)Preconditions.checkNotNull(var1);
      }

      String getLockName() {
         return this.lockName;
      }

      void checkAcquiredLocks(CycleDetectingLockFactory.Policy var1, List<CycleDetectingLockFactory.LockGraphNode> var2) {
         int var3 = 0;

         for(int var4 = var2.size(); var3 < var4; ++var3) {
            this.checkAcquiredLock(var1, (CycleDetectingLockFactory.LockGraphNode)var2.get(var3));
         }

      }

      void checkAcquiredLock(CycleDetectingLockFactory.Policy var1, CycleDetectingLockFactory.LockGraphNode var2) {
         Preconditions.checkState(this != var2, "Attempted to acquire multiple locks with the same rank %s", (Object)var2.getLockName());
         if (!this.allowedPriorLocks.containsKey(var2)) {
            CycleDetectingLockFactory.PotentialDeadlockException var3 = (CycleDetectingLockFactory.PotentialDeadlockException)this.disallowedPriorLocks.get(var2);
            if (var3 != null) {
               CycleDetectingLockFactory.PotentialDeadlockException var7 = new CycleDetectingLockFactory.PotentialDeadlockException(var2, this, var3.getConflictingStackTrace());
               var1.handlePotentialDeadlock(var7);
            } else {
               Set var4 = Sets.newIdentityHashSet();
               CycleDetectingLockFactory.ExampleStackTrace var5 = var2.findPathTo(this, var4);
               if (var5 == null) {
                  this.allowedPriorLocks.put(var2, new CycleDetectingLockFactory.ExampleStackTrace(var2, this));
               } else {
                  CycleDetectingLockFactory.PotentialDeadlockException var6 = new CycleDetectingLockFactory.PotentialDeadlockException(var2, this, var5);
                  this.disallowedPriorLocks.put(var2, var6);
                  var1.handlePotentialDeadlock(var6);
               }

            }
         }
      }

      @Nullable
      private CycleDetectingLockFactory.ExampleStackTrace findPathTo(CycleDetectingLockFactory.LockGraphNode var1, Set<CycleDetectingLockFactory.LockGraphNode> var2) {
         if (!var2.add(this)) {
            return null;
         } else {
            CycleDetectingLockFactory.ExampleStackTrace var3 = (CycleDetectingLockFactory.ExampleStackTrace)this.allowedPriorLocks.get(var1);
            if (var3 != null) {
               return var3;
            } else {
               Iterator var4 = this.allowedPriorLocks.entrySet().iterator();

               Entry var5;
               CycleDetectingLockFactory.LockGraphNode var6;
               do {
                  if (!var4.hasNext()) {
                     return null;
                  }

                  var5 = (Entry)var4.next();
                  var6 = (CycleDetectingLockFactory.LockGraphNode)var5.getKey();
                  var3 = var6.findPathTo(var1, var2);
               } while(var3 == null);

               CycleDetectingLockFactory.ExampleStackTrace var7 = new CycleDetectingLockFactory.ExampleStackTrace(var6, this);
               var7.setStackTrace(((CycleDetectingLockFactory.ExampleStackTrace)var5.getValue()).getStackTrace());
               var7.initCause(var3);
               return var7;
            }
         }
      }
   }

   private interface CycleDetectingLock {
      CycleDetectingLockFactory.LockGraphNode getLockGraphNode();

      boolean isAcquiredByCurrentThread();
   }

   @Beta
   public static final class PotentialDeadlockException extends CycleDetectingLockFactory.ExampleStackTrace {
      private final CycleDetectingLockFactory.ExampleStackTrace conflictingStackTrace;

      private PotentialDeadlockException(CycleDetectingLockFactory.LockGraphNode var1, CycleDetectingLockFactory.LockGraphNode var2, CycleDetectingLockFactory.ExampleStackTrace var3) {
         super(var1, var2);
         this.conflictingStackTrace = var3;
         this.initCause(var3);
      }

      public CycleDetectingLockFactory.ExampleStackTrace getConflictingStackTrace() {
         return this.conflictingStackTrace;
      }

      public String getMessage() {
         StringBuilder var1 = new StringBuilder(super.getMessage());

         for(Object var2 = this.conflictingStackTrace; var2 != null; var2 = ((Throwable)var2).getCause()) {
            var1.append(", ").append(((Throwable)var2).getMessage());
         }

         return var1.toString();
      }

      // $FF: synthetic method
      PotentialDeadlockException(CycleDetectingLockFactory.LockGraphNode var1, CycleDetectingLockFactory.LockGraphNode var2, CycleDetectingLockFactory.ExampleStackTrace var3, Object var4) {
         this(var1, var2, var3);
      }
   }

   private static class ExampleStackTrace extends IllegalStateException {
      static final StackTraceElement[] EMPTY_STACK_TRACE = new StackTraceElement[0];
      static final Set<String> EXCLUDED_CLASS_NAMES = ImmutableSet.of(CycleDetectingLockFactory.class.getName(), CycleDetectingLockFactory.ExampleStackTrace.class.getName(), CycleDetectingLockFactory.LockGraphNode.class.getName());

      ExampleStackTrace(CycleDetectingLockFactory.LockGraphNode var1, CycleDetectingLockFactory.LockGraphNode var2) {
         super(var1.getLockName() + " -> " + var2.getLockName());
         StackTraceElement[] var3 = this.getStackTrace();
         int var4 = 0;

         for(int var5 = var3.length; var4 < var5; ++var4) {
            if (CycleDetectingLockFactory.WithExplicitOrdering.class.getName().equals(var3[var4].getClassName())) {
               this.setStackTrace(EMPTY_STACK_TRACE);
               break;
            }

            if (!EXCLUDED_CLASS_NAMES.contains(var3[var4].getClassName())) {
               this.setStackTrace((StackTraceElement[])Arrays.copyOfRange(var3, var4, var5));
               break;
            }
         }

      }
   }

   @Beta
   public static final class WithExplicitOrdering<E extends Enum<E>> extends CycleDetectingLockFactory {
      private final Map<E, CycleDetectingLockFactory.LockGraphNode> lockGraphNodes;

      @VisibleForTesting
      WithExplicitOrdering(CycleDetectingLockFactory.Policy var1, Map<E, CycleDetectingLockFactory.LockGraphNode> var2) {
         super(var1, null);
         this.lockGraphNodes = var2;
      }

      public ReentrantLock newReentrantLock(E var1) {
         return this.newReentrantLock(var1, false);
      }

      public ReentrantLock newReentrantLock(E var1, boolean var2) {
         return (ReentrantLock)(this.policy == CycleDetectingLockFactory.Policies.DISABLED ? new ReentrantLock(var2) : new CycleDetectingLockFactory.CycleDetectingReentrantLock((CycleDetectingLockFactory.LockGraphNode)this.lockGraphNodes.get(var1), var2));
      }

      public ReentrantReadWriteLock newReentrantReadWriteLock(E var1) {
         return this.newReentrantReadWriteLock(var1, false);
      }

      public ReentrantReadWriteLock newReentrantReadWriteLock(E var1, boolean var2) {
         return (ReentrantReadWriteLock)(this.policy == CycleDetectingLockFactory.Policies.DISABLED ? new ReentrantReadWriteLock(var2) : new CycleDetectingLockFactory.CycleDetectingReentrantReadWriteLock((CycleDetectingLockFactory.LockGraphNode)this.lockGraphNodes.get(var1), var2));
      }
   }

   @Beta
   public static enum Policies implements CycleDetectingLockFactory.Policy {
      THROW {
         public void handlePotentialDeadlock(CycleDetectingLockFactory.PotentialDeadlockException var1) {
            throw var1;
         }
      },
      WARN {
         public void handlePotentialDeadlock(CycleDetectingLockFactory.PotentialDeadlockException var1) {
            CycleDetectingLockFactory.logger.log(Level.SEVERE, "Detected potential deadlock", var1);
         }
      },
      DISABLED {
         public void handlePotentialDeadlock(CycleDetectingLockFactory.PotentialDeadlockException var1) {
         }
      };

      private Policies() {
      }

      // $FF: synthetic method
      Policies(Object var3) {
         this();
      }
   }

   @Beta
   @ThreadSafe
   public interface Policy {
      void handlePotentialDeadlock(CycleDetectingLockFactory.PotentialDeadlockException var1);
   }
}
