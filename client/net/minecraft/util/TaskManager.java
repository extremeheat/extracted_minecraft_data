package net.minecraft.util;

import java.util.concurrent.CompletableFuture;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TaskManager<K, T extends ITaskType<K, T>, R> {
   private static final Logger field_202929_a = LogManager.getLogger();
   private final Scheduler<K, T, R> field_202930_b;
   private boolean field_202931_c;
   private int field_202932_d = 1000;

   public TaskManager(Scheduler<K, T, R> var1) {
      super();
      this.field_202930_b = var1;
   }

   public void func_202925_a() throws InterruptedException {
      this.field_202930_b.func_202854_b();
   }

   public void func_202928_b() {
      if (this.field_202931_c) {
         throw new RuntimeException("Batch already started.");
      } else {
         this.field_202932_d = 1000;
         this.field_202931_c = true;
      }
   }

   public CompletableFuture<R> func_202926_a(K var1) {
      if (!this.field_202931_c) {
         throw new RuntimeException("Batch not properly started. Please use startBatch to create a new batch.");
      } else {
         CompletableFuture var2 = this.field_202930_b.func_202851_b(var1);
         --this.field_202932_d;
         if (this.field_202932_d == 0) {
            var2 = this.field_202930_b.func_202845_a();
            this.field_202932_d = 1000;
         }

         return var2;
      }
   }

   public CompletableFuture<R> func_202927_c() {
      if (!this.field_202931_c) {
         throw new RuntimeException("Batch not properly started. Please use startBatch to create a new batch.");
      } else {
         if (this.field_202932_d != 1000) {
            this.field_202930_b.func_202845_a();
         }

         this.field_202931_c = false;
         return this.field_202930_b.func_202846_c();
      }
   }
}
