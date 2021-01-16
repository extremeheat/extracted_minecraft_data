package org.apache.commons.io;

import java.io.File;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class FileCleaningTracker {
   ReferenceQueue<Object> q = new ReferenceQueue();
   final Collection<FileCleaningTracker.Tracker> trackers = Collections.synchronizedSet(new HashSet());
   final List<String> deleteFailures = Collections.synchronizedList(new ArrayList());
   volatile boolean exitWhenFinished = false;
   Thread reaper;

   public FileCleaningTracker() {
      super();
   }

   public void track(File var1, Object var2) {
      this.track((File)var1, var2, (FileDeleteStrategy)null);
   }

   public void track(File var1, Object var2, FileDeleteStrategy var3) {
      if (var1 == null) {
         throw new NullPointerException("The file must not be null");
      } else {
         this.addTracker(var1.getPath(), var2, var3);
      }
   }

   public void track(String var1, Object var2) {
      this.track((String)var1, var2, (FileDeleteStrategy)null);
   }

   public void track(String var1, Object var2, FileDeleteStrategy var3) {
      if (var1 == null) {
         throw new NullPointerException("The path must not be null");
      } else {
         this.addTracker(var1, var2, var3);
      }
   }

   private synchronized void addTracker(String var1, Object var2, FileDeleteStrategy var3) {
      if (this.exitWhenFinished) {
         throw new IllegalStateException("No new trackers can be added once exitWhenFinished() is called");
      } else {
         if (this.reaper == null) {
            this.reaper = new FileCleaningTracker.Reaper();
            this.reaper.start();
         }

         this.trackers.add(new FileCleaningTracker.Tracker(var1, var3, var2, this.q));
      }
   }

   public int getTrackCount() {
      return this.trackers.size();
   }

   public List<String> getDeleteFailures() {
      return this.deleteFailures;
   }

   public synchronized void exitWhenFinished() {
      this.exitWhenFinished = true;
      if (this.reaper != null) {
         synchronized(this.reaper) {
            this.reaper.interrupt();
         }
      }

   }

   private static final class Tracker extends PhantomReference<Object> {
      private final String path;
      private final FileDeleteStrategy deleteStrategy;

      Tracker(String var1, FileDeleteStrategy var2, Object var3, ReferenceQueue<? super Object> var4) {
         super(var3, var4);
         this.path = var1;
         this.deleteStrategy = var2 == null ? FileDeleteStrategy.NORMAL : var2;
      }

      public String getPath() {
         return this.path;
      }

      public boolean delete() {
         return this.deleteStrategy.deleteQuietly(new File(this.path));
      }
   }

   private final class Reaper extends Thread {
      Reaper() {
         super("File Reaper");
         this.setPriority(10);
         this.setDaemon(true);
      }

      public void run() {
         while(!FileCleaningTracker.this.exitWhenFinished || FileCleaningTracker.this.trackers.size() > 0) {
            try {
               FileCleaningTracker.Tracker var1 = (FileCleaningTracker.Tracker)FileCleaningTracker.this.q.remove();
               FileCleaningTracker.this.trackers.remove(var1);
               if (!var1.delete()) {
                  FileCleaningTracker.this.deleteFailures.add(var1.getPath());
               }

               var1.clear();
            } catch (InterruptedException var2) {
            }
         }

      }
   }
}
