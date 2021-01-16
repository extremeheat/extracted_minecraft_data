package org.apache.logging.log4j.core.appender.rolling;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LifeCycle;
import org.apache.logging.log4j.core.LifeCycle2;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConfigurationFactoryData;
import org.apache.logging.log4j.core.appender.FileManager;
import org.apache.logging.log4j.core.appender.ManagerFactory;
import org.apache.logging.log4j.core.appender.rolling.action.AbstractAction;
import org.apache.logging.log4j.core.appender.rolling.action.Action;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.util.Constants;
import org.apache.logging.log4j.core.util.FileUtils;
import org.apache.logging.log4j.core.util.Log4jThreadFactory;

public class RollingFileManager extends FileManager {
   private static RollingFileManager.RollingFileManagerFactory factory = new RollingFileManager.RollingFileManagerFactory();
   private static final int MAX_TRIES = 3;
   private static final int MIN_DURATION = 100;
   protected long size;
   private long initialTime;
   private final PatternProcessor patternProcessor;
   private final Semaphore semaphore;
   private final Log4jThreadFactory threadFactory;
   private volatile TriggeringPolicy triggeringPolicy;
   private volatile RolloverStrategy rolloverStrategy;
   private volatile boolean renameEmptyFiles;
   private volatile boolean initialized;
   private volatile String fileName;
   private FileExtension fileExtension;
   private ExecutorService asyncExecutor;
   private static final AtomicReferenceFieldUpdater<RollingFileManager, TriggeringPolicy> triggeringPolicyUpdater = AtomicReferenceFieldUpdater.newUpdater(RollingFileManager.class, TriggeringPolicy.class, "triggeringPolicy");
   private static final AtomicReferenceFieldUpdater<RollingFileManager, RolloverStrategy> rolloverStrategyUpdater = AtomicReferenceFieldUpdater.newUpdater(RollingFileManager.class, RolloverStrategy.class, "rolloverStrategy");

   /** @deprecated */
   @Deprecated
   protected RollingFileManager(String var1, String var2, OutputStream var3, boolean var4, long var5, long var7, TriggeringPolicy var9, RolloverStrategy var10, String var11, Layout<? extends Serializable> var12, int var13, boolean var14) {
      this(var1, var2, var3, var4, var5, var7, var9, var10, var11, var12, var14, ByteBuffer.wrap(new byte[Constants.ENCODER_BYTE_BUFFER_SIZE]));
   }

   /** @deprecated */
   @Deprecated
   protected RollingFileManager(String var1, String var2, OutputStream var3, boolean var4, long var5, long var7, TriggeringPolicy var9, RolloverStrategy var10, String var11, Layout<? extends Serializable> var12, boolean var13, ByteBuffer var14) {
      super(var1, var3, var4, false, var11, var12, var13, var14);
      this.semaphore = new Semaphore(1);
      this.threadFactory = Log4jThreadFactory.createThreadFactory("RollingFileManager");
      this.renameEmptyFiles = false;
      this.initialized = false;
      this.asyncExecutor = new ThreadPoolExecutor(0, 2147483647, 0L, TimeUnit.MILLISECONDS, new RollingFileManager.EmptyQueue(), this.threadFactory);
      this.size = var5;
      this.initialTime = var7;
      this.triggeringPolicy = var9;
      this.rolloverStrategy = var10;
      this.patternProcessor = new PatternProcessor(var2);
      this.patternProcessor.setPrevFileTime(var7);
      this.fileName = var1;
      this.fileExtension = FileExtension.lookupForFile(var2);
   }

   protected RollingFileManager(LoggerContext var1, String var2, String var3, OutputStream var4, boolean var5, boolean var6, long var7, long var9, TriggeringPolicy var11, RolloverStrategy var12, String var13, Layout<? extends Serializable> var14, boolean var15, ByteBuffer var16) {
      super(var1, var2, var4, var5, false, var6, var13, var14, var15, var16);
      this.semaphore = new Semaphore(1);
      this.threadFactory = Log4jThreadFactory.createThreadFactory("RollingFileManager");
      this.renameEmptyFiles = false;
      this.initialized = false;
      this.asyncExecutor = new ThreadPoolExecutor(0, 2147483647, 0L, TimeUnit.MILLISECONDS, new RollingFileManager.EmptyQueue(), this.threadFactory);
      this.size = var7;
      this.initialTime = var9;
      this.triggeringPolicy = var11;
      this.rolloverStrategy = var12;
      this.patternProcessor = new PatternProcessor(var3);
      this.patternProcessor.setPrevFileTime(var9);
      this.fileName = var2;
      this.fileExtension = FileExtension.lookupForFile(var3);
   }

   public void initialize() {
      if (!this.initialized) {
         LOGGER.debug((String)"Initializing triggering policy {}", (Object)this.triggeringPolicy);
         this.initialized = true;
         this.triggeringPolicy.initialize(this);
         if (this.triggeringPolicy instanceof LifeCycle) {
            ((LifeCycle)this.triggeringPolicy).start();
         }
      }

   }

   public static RollingFileManager getFileManager(String var0, String var1, boolean var2, boolean var3, TriggeringPolicy var4, RolloverStrategy var5, String var6, Layout<? extends Serializable> var7, int var8, boolean var9, boolean var10, Configuration var11) {
      String var12 = var0 == null ? var1 : var0;
      return (RollingFileManager)getManager(var12, new RollingFileManager.FactoryData(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11), factory);
   }

   public String getFileName() {
      if (this.rolloverStrategy instanceof DirectFileRolloverStrategy) {
         this.fileName = ((DirectFileRolloverStrategy)this.rolloverStrategy).getCurrentFileName(this);
      }

      return this.fileName;
   }

   public FileExtension getFileExtension() {
      return this.fileExtension;
   }

   protected synchronized void write(byte[] var1, int var2, int var3, boolean var4) {
      super.write(var1, var2, var3, var4);
   }

   protected synchronized void writeToDestination(byte[] var1, int var2, int var3) {
      this.size += (long)var3;
      super.writeToDestination(var1, var2, var3);
   }

   public boolean isRenameEmptyFiles() {
      return this.renameEmptyFiles;
   }

   public void setRenameEmptyFiles(boolean var1) {
      this.renameEmptyFiles = var1;
   }

   public long getFileSize() {
      return this.size + (long)this.byteBuffer.position();
   }

   public long getFileTime() {
      return this.initialTime;
   }

   public synchronized void checkRollover(LogEvent var1) {
      if (this.triggeringPolicy.isTriggeringEvent(var1)) {
         this.rollover();
      }

   }

   public boolean releaseSub(long var1, TimeUnit var3) {
      LOGGER.debug("Shutting down RollingFileManager {}" + this.getName());
      boolean var4 = true;
      if (this.triggeringPolicy instanceof LifeCycle2) {
         var4 &= ((LifeCycle2)this.triggeringPolicy).stop(var1, var3);
      } else if (this.triggeringPolicy instanceof LifeCycle) {
         ((LifeCycle)this.triggeringPolicy).stop();
         var4 &= true;
      }

      boolean var5 = super.releaseSub(var1, var3) && var4;
      this.asyncExecutor.shutdown();

      try {
         long var6 = var3.toMillis(var1);
         long var8 = 100L < var6 ? var6 : 100L;

         for(int var10 = 1; var10 <= 3 && !this.asyncExecutor.isTerminated(); ++var10) {
            this.asyncExecutor.awaitTermination(var8 * (long)var10, TimeUnit.MILLISECONDS);
         }

         if (this.asyncExecutor.isTerminated()) {
            LOGGER.debug("All asynchronous threads have terminated");
         } else {
            this.asyncExecutor.shutdownNow();

            try {
               this.asyncExecutor.awaitTermination(var1, var3);
               if (this.asyncExecutor.isTerminated()) {
                  LOGGER.debug("All asynchronous threads have terminated");
               } else {
                  LOGGER.debug("RollingFileManager shutting down but some asynchronous services may not have completed");
               }
            } catch (InterruptedException var12) {
               LOGGER.warn("RollingFileManager stopped but some asynchronous services may not have completed.");
            }
         }
      } catch (InterruptedException var13) {
         this.asyncExecutor.shutdownNow();

         try {
            this.asyncExecutor.awaitTermination(var1, var3);
            if (this.asyncExecutor.isTerminated()) {
               LOGGER.debug("All asynchronous threads have terminated");
            }
         } catch (InterruptedException var11) {
            LOGGER.warn("RollingFileManager stopped but some asynchronous services may not have completed.");
         }

         Thread.currentThread().interrupt();
      }

      LOGGER.debug((String)"RollingFileManager shutdown completed with status {}", (Object)var5);
      return var5;
   }

   public synchronized void rollover() {
      if (this.hasOutputStream()) {
         if (this.rollover(this.rolloverStrategy)) {
            try {
               this.size = 0L;
               this.initialTime = System.currentTimeMillis();
               this.createFileAfterRollover();
            } catch (IOException var2) {
               this.logError("Failed to create file after rollover", var2);
            }
         }

      }
   }

   protected void createFileAfterRollover() throws IOException {
      this.setOutputStream(this.createOutputStream());
   }

   public PatternProcessor getPatternProcessor() {
      return this.patternProcessor;
   }

   public void setTriggeringPolicy(TriggeringPolicy var1) {
      var1.initialize(this);
      TriggeringPolicy var2 = this.triggeringPolicy;
      int var3 = 0;
      boolean var4 = false;

      do {
         ++var3;
      } while(!(var4 = triggeringPolicyUpdater.compareAndSet(this, this.triggeringPolicy, var1)) && var3 < 3);

      if (var4) {
         if (var1 instanceof LifeCycle) {
            ((LifeCycle)var1).start();
         }

         if (var2 instanceof LifeCycle) {
            ((LifeCycle)var2).stop();
         }
      } else if (var1 instanceof LifeCycle) {
         ((LifeCycle)var1).stop();
      }

   }

   public void setRolloverStrategy(RolloverStrategy var1) {
      rolloverStrategyUpdater.compareAndSet(this, this.rolloverStrategy, var1);
   }

   public <T extends TriggeringPolicy> T getTriggeringPolicy() {
      return this.triggeringPolicy;
   }

   public RolloverStrategy getRolloverStrategy() {
      return this.rolloverStrategy;
   }

   private boolean rollover(RolloverStrategy var1) {
      boolean var2 = false;

      try {
         this.semaphore.acquire();
         var2 = true;
      } catch (InterruptedException var11) {
         this.logError("Thread interrupted while attempting to check rollover", var11);
         return false;
      }

      boolean var3 = true;

      boolean var5;
      try {
         RolloverDescription var4 = var1.rollover(this);
         if (var4 != null) {
            this.writeFooter();
            this.closeOutputStream();
            if (var4.getSynchronous() != null) {
               LOGGER.debug((String)"RollingFileManager executing synchronous {}", (Object)var4.getSynchronous());

               try {
                  var3 = var4.getSynchronous().execute();
               } catch (Exception var10) {
                  var3 = false;
                  this.logError("Caught error in synchronous task", var10);
               }
            }

            if (var3 && var4.getAsynchronous() != null) {
               LOGGER.debug((String)"RollingFileManager executing async {}", (Object)var4.getAsynchronous());
               this.asyncExecutor.execute(new RollingFileManager.AsyncAction(var4.getAsynchronous(), this));
               var2 = false;
            }

            var5 = true;
            return var5;
         }

         var5 = false;
      } finally {
         if (var2) {
            this.semaphore.release();
         }

      }

      return var5;
   }

   public void updateData(Object var1) {
      RollingFileManager.FactoryData var2 = (RollingFileManager.FactoryData)var1;
      this.setRolloverStrategy(var2.getRolloverStrategy());
      this.setTriggeringPolicy(var2.getTriggeringPolicy());
   }

   private static class EmptyQueue extends ArrayBlockingQueue<Runnable> {
      EmptyQueue() {
         super(1);
      }

      public int remainingCapacity() {
         return 0;
      }

      public boolean add(Runnable var1) {
         throw new IllegalStateException("Queue is full");
      }

      public void put(Runnable var1) throws InterruptedException {
         throw new InterruptedException("Unable to insert into queue");
      }

      public boolean offer(Runnable var1, long var2, TimeUnit var4) throws InterruptedException {
         Thread.sleep(var4.toMillis(var2));
         return false;
      }

      public boolean addAll(Collection<? extends Runnable> var1) {
         if (var1.size() > 0) {
            throw new IllegalArgumentException("Too many items in collection");
         } else {
            return false;
         }
      }
   }

   private static class RollingFileManagerFactory implements ManagerFactory<RollingFileManager, RollingFileManager.FactoryData> {
      private RollingFileManagerFactory() {
         super();
      }

      public RollingFileManager createManager(String var1, RollingFileManager.FactoryData var2) {
         long var3 = 0L;
         boolean var5 = !var2.append;
         File var6 = null;
         if (var2.fileName != null) {
            var6 = new File(var2.fileName);
            var5 = !var2.append || !var6.exists();

            try {
               FileUtils.makeParentDirs(var6);
               boolean var7 = var2.createOnDemand ? false : var6.createNewFile();
               RollingFileManager.LOGGER.trace((String)"New file '{}' created = {}", (Object)var1, (Object)var7);
            } catch (IOException var12) {
               RollingFileManager.LOGGER.error((String)("Unable to create file " + var1), (Throwable)var12);
               return null;
            }

            var3 = var2.append ? var6.length() : 0L;
         }

         try {
            int var14 = var2.bufferedIO ? var2.bufferSize : Constants.ENCODER_BYTE_BUFFER_SIZE;
            ByteBuffer var8 = ByteBuffer.wrap(new byte[var14]);
            FileOutputStream var9 = !var2.createOnDemand && var2.fileName != null ? new FileOutputStream(var2.fileName, var2.append) : null;
            long var10 = !var2.createOnDemand && var6 != null ? var6.lastModified() : System.currentTimeMillis();
            return new RollingFileManager(var2.getLoggerContext(), var2.fileName, var2.pattern, var9, var2.append, var2.createOnDemand, var3, var10, var2.policy, var2.strategy, var2.advertiseURI, var2.layout, var5, var8);
         } catch (IOException var13) {
            RollingFileManager.LOGGER.error((String)("RollingFileManager (" + var1 + ") " + var13), (Throwable)var13);
            return null;
         }
      }

      // $FF: synthetic method
      RollingFileManagerFactory(Object var1) {
         this();
      }
   }

   private static class FactoryData extends ConfigurationFactoryData {
      private final String fileName;
      private final String pattern;
      private final boolean append;
      private final boolean bufferedIO;
      private final int bufferSize;
      private final boolean immediateFlush;
      private final boolean createOnDemand;
      private final TriggeringPolicy policy;
      private final RolloverStrategy strategy;
      private final String advertiseURI;
      private final Layout<? extends Serializable> layout;

      public FactoryData(String var1, String var2, boolean var3, boolean var4, TriggeringPolicy var5, RolloverStrategy var6, String var7, Layout<? extends Serializable> var8, int var9, boolean var10, boolean var11, Configuration var12) {
         super(var12);
         this.fileName = var1;
         this.pattern = var2;
         this.append = var3;
         this.bufferedIO = var4;
         this.bufferSize = var9;
         this.policy = var5;
         this.strategy = var6;
         this.advertiseURI = var7;
         this.layout = var8;
         this.immediateFlush = var10;
         this.createOnDemand = var11;
      }

      public TriggeringPolicy getTriggeringPolicy() {
         return this.policy;
      }

      public RolloverStrategy getRolloverStrategy() {
         return this.strategy;
      }

      public String toString() {
         StringBuilder var1 = new StringBuilder();
         var1.append(super.toString());
         var1.append("[pattern=");
         var1.append(this.pattern);
         var1.append(", append=");
         var1.append(this.append);
         var1.append(", bufferedIO=");
         var1.append(this.bufferedIO);
         var1.append(", bufferSize=");
         var1.append(this.bufferSize);
         var1.append(", policy=");
         var1.append(this.policy);
         var1.append(", strategy=");
         var1.append(this.strategy);
         var1.append(", advertiseURI=");
         var1.append(this.advertiseURI);
         var1.append(", layout=");
         var1.append(this.layout);
         var1.append("]");
         return var1.toString();
      }
   }

   private static class AsyncAction extends AbstractAction {
      private final Action action;
      private final RollingFileManager manager;

      public AsyncAction(Action var1, RollingFileManager var2) {
         super();
         this.action = var1;
         this.manager = var2;
      }

      public boolean execute() throws IOException {
         boolean var1;
         try {
            var1 = this.action.execute();
         } finally {
            this.manager.semaphore.release();
         }

         return var1;
      }

      public void close() {
         this.action.close();
      }

      public boolean isComplete() {
         return this.action.isComplete();
      }

      public String toString() {
         StringBuilder var1 = new StringBuilder();
         var1.append(super.toString());
         var1.append("[action=");
         var1.append(this.action);
         var1.append(", manager=");
         var1.append(this.manager);
         var1.append(", isComplete()=");
         var1.append(this.isComplete());
         var1.append(", isInterrupted()=");
         var1.append(this.isInterrupted());
         var1.append("]");
         return var1.toString();
      }
   }
}
