package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumWorldBlockLayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkRenderWorker implements Runnable {
   private static final Logger field_152478_a = LogManager.getLogger();
   private final ChunkRenderDispatcher field_178477_b;
   private final RegionRenderCacheBuilder field_178478_c;

   public ChunkRenderWorker(ChunkRenderDispatcher var1) {
      this(var1, (RegionRenderCacheBuilder)null);
   }

   public ChunkRenderWorker(ChunkRenderDispatcher var1, RegionRenderCacheBuilder var2) {
      super();
      this.field_178477_b = var1;
      this.field_178478_c = var2;
   }

   public void run() {
      while(true) {
         try {
            this.func_178474_a(this.field_178477_b.func_178511_d());
         } catch (InterruptedException var3) {
            field_152478_a.debug("Stopping due to interrupt");
            return;
         } catch (Throwable var4) {
            CrashReport var2 = CrashReport.func_85055_a(var4, "Batching chunks");
            Minecraft.func_71410_x().func_71404_a(Minecraft.func_71410_x().func_71396_d(var2));
            return;
         }
      }
   }

   protected void func_178474_a(final ChunkCompileTaskGenerator var1) throws InterruptedException {
      var1.func_178540_f().lock();

      label242: {
         try {
            if (var1.func_178546_a() == ChunkCompileTaskGenerator.Status.PENDING) {
               var1.func_178535_a(ChunkCompileTaskGenerator.Status.COMPILING);
               break label242;
            }

            if (!var1.func_178537_h()) {
               field_152478_a.warn("Chunk render task was " + var1.func_178546_a() + " when I expected it to be pending; ignoring task");
            }
         } finally {
            var1.func_178540_f().unlock();
         }

         return;
      }

      Entity var2 = Minecraft.func_71410_x().func_175606_aa();
      if (var2 == null) {
         var1.func_178542_e();
      } else {
         var1.func_178541_a(this.func_178475_b());
         float var3 = (float)var2.field_70165_t;
         float var4 = (float)var2.field_70163_u + var2.func_70047_e();
         float var5 = (float)var2.field_70161_v;
         ChunkCompileTaskGenerator.Type var6 = var1.func_178538_g();
         if (var6 == ChunkCompileTaskGenerator.Type.REBUILD_CHUNK) {
            var1.func_178536_b().func_178581_b(var3, var4, var5, var1);
         } else if (var6 == ChunkCompileTaskGenerator.Type.RESORT_TRANSPARENCY) {
            var1.func_178536_b().func_178570_a(var3, var4, var5, var1);
         }

         var1.func_178540_f().lock();

         try {
            if (var1.func_178546_a() != ChunkCompileTaskGenerator.Status.COMPILING) {
               if (!var1.func_178537_h()) {
                  field_152478_a.warn("Chunk render task was " + var1.func_178546_a() + " when I expected it to be compiling; aborting task");
               }

               this.func_178473_b(var1);
               return;
            }

            var1.func_178535_a(ChunkCompileTaskGenerator.Status.UPLOADING);
         } finally {
            var1.func_178540_f().unlock();
         }

         final CompiledChunk var7 = var1.func_178544_c();
         ArrayList var8 = Lists.newArrayList();
         if (var6 == ChunkCompileTaskGenerator.Type.REBUILD_CHUNK) {
            EnumWorldBlockLayer[] var9 = EnumWorldBlockLayer.values();
            int var10 = var9.length;

            for(int var11 = 0; var11 < var10; ++var11) {
               EnumWorldBlockLayer var12 = var9[var11];
               if (var7.func_178492_d(var12)) {
                  var8.add(this.field_178477_b.func_178503_a(var12, var1.func_178545_d().func_179038_a(var12), var1.func_178536_b(), var7));
               }
            }
         } else if (var6 == ChunkCompileTaskGenerator.Type.RESORT_TRANSPARENCY) {
            var8.add(this.field_178477_b.func_178503_a(EnumWorldBlockLayer.TRANSLUCENT, var1.func_178545_d().func_179038_a(EnumWorldBlockLayer.TRANSLUCENT), var1.func_178536_b(), var7));
         }

         final ListenableFuture var19 = Futures.allAsList(var8);
         var1.func_178539_a(new Runnable() {
            public void run() {
               var19.cancel(false);
            }
         });
         Futures.addCallback(var19, new FutureCallback<List<Object>>() {
            public void onSuccess(List<Object> var1x) {
               ChunkRenderWorker.this.func_178473_b(var1);
               var1.func_178540_f().lock();

               label53: {
                  try {
                     if (var1.func_178546_a() == ChunkCompileTaskGenerator.Status.UPLOADING) {
                        var1.func_178535_a(ChunkCompileTaskGenerator.Status.DONE);
                        break label53;
                     }

                     if (!var1.func_178537_h()) {
                        ChunkRenderWorker.field_152478_a.warn("Chunk render task was " + var1.func_178546_a() + " when I expected it to be uploading; aborting task");
                     }
                  } finally {
                     var1.func_178540_f().unlock();
                  }

                  return;
               }

               var1.func_178536_b().func_178580_a(var7);
            }

            public void onFailure(Throwable var1x) {
               ChunkRenderWorker.this.func_178473_b(var1);
               if (!(var1x instanceof CancellationException) && !(var1x instanceof InterruptedException)) {
                  Minecraft.func_71410_x().func_71404_a(CrashReport.func_85055_a(var1x, "Rendering chunk"));
               }

            }

            // $FF: synthetic method
            public void onSuccess(Object var1x) {
               this.onSuccess((List)var1x);
            }
         });
      }
   }

   private RegionRenderCacheBuilder func_178475_b() throws InterruptedException {
      return this.field_178478_c != null ? this.field_178478_c : this.field_178477_b.func_178515_c();
   }

   private void func_178473_b(ChunkCompileTaskGenerator var1) {
      if (this.field_178478_c == null) {
         this.field_178477_b.func_178512_a(var1.func_178545_d());
      }

   }
}
