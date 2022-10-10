package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkRenderWorker implements Runnable {
   private static final Logger field_152478_a = LogManager.getLogger();
   private final ChunkRenderDispatcher field_178477_b;
   private final RegionRenderCacheBuilder field_178478_c;
   private boolean field_188265_d;

   public ChunkRenderWorker(ChunkRenderDispatcher var1) {
      this(var1, (RegionRenderCacheBuilder)null);
   }

   public ChunkRenderWorker(ChunkRenderDispatcher var1, @Nullable RegionRenderCacheBuilder var2) {
      super();
      this.field_188265_d = true;
      this.field_178477_b = var1;
      this.field_178478_c = var2;
   }

   public void run() {
      while(this.field_188265_d) {
         try {
            this.func_178474_a(this.field_178477_b.func_178511_d());
         } catch (InterruptedException var3) {
            field_152478_a.debug("Stopping chunk worker due to interrupt");
            return;
         } catch (Throwable var4) {
            CrashReport var2 = CrashReport.func_85055_a(var4, "Batching chunks");
            Minecraft.func_71410_x().func_71404_a(Minecraft.func_71410_x().func_71396_d(var2));
            return;
         }
      }

   }

   protected void func_178474_a(final ChunkRenderTask var1) throws InterruptedException {
      var1.func_178540_f().lock();

      try {
         if (var1.func_178546_a() != ChunkRenderTask.Status.PENDING) {
            if (!var1.func_178537_h()) {
               field_152478_a.warn("Chunk render task was {} when I expected it to be pending; ignoring task", var1.func_178546_a());
            }

            return;
         }

         BlockPos var2 = new BlockPos(Minecraft.func_71410_x().field_71439_g);
         BlockPos var3 = var1.func_178536_b().func_178568_j();
         boolean var4 = true;
         boolean var5 = true;
         boolean var6 = true;
         if (var3.func_177982_a(8, 8, 8).func_177951_i(var2) > 576.0D) {
            World var7 = var1.func_178536_b().func_188283_p();
            BlockPos.MutableBlockPos var8 = new BlockPos.MutableBlockPos(var3);
            if (!this.func_188263_a(var8.func_189533_g(var3).func_189534_c(EnumFacing.WEST, 16), var7) || !this.func_188263_a(var8.func_189533_g(var3).func_189534_c(EnumFacing.NORTH, 16), var7) || !this.func_188263_a(var8.func_189533_g(var3).func_189534_c(EnumFacing.EAST, 16), var7) || !this.func_188263_a(var8.func_189533_g(var3).func_189534_c(EnumFacing.SOUTH, 16), var7)) {
               return;
            }
         }

         var1.func_178535_a(ChunkRenderTask.Status.COMPILING);
      } finally {
         var1.func_178540_f().unlock();
      }

      Entity var20 = Minecraft.func_71410_x().func_175606_aa();
      if (var20 == null) {
         var1.func_178542_e();
      } else {
         var1.func_178541_a(this.func_178475_b());
         Vec3d var21 = ActiveRenderInfo.func_178806_a(var20, 1.0D);
         float var22 = (float)var21.field_72450_a;
         float var23 = (float)var21.field_72448_b;
         float var24 = (float)var21.field_72449_c;
         ChunkRenderTask.Type var25 = var1.func_178538_g();
         if (var25 == ChunkRenderTask.Type.REBUILD_CHUNK) {
            var1.func_178536_b().func_178581_b(var22, var23, var24, var1);
         } else if (var25 == ChunkRenderTask.Type.RESORT_TRANSPARENCY) {
            var1.func_178536_b().func_178570_a(var22, var23, var24, var1);
         }

         var1.func_178540_f().lock();

         try {
            if (var1.func_178546_a() != ChunkRenderTask.Status.COMPILING) {
               if (!var1.func_178537_h()) {
                  field_152478_a.warn("Chunk render task was {} when I expected it to be compiling; aborting task", var1.func_178546_a());
               }

               this.func_178473_b(var1);
               return;
            }

            var1.func_178535_a(ChunkRenderTask.Status.UPLOADING);
         } finally {
            var1.func_178540_f().unlock();
         }

         final CompiledChunk var26 = var1.func_178544_c();
         ArrayList var9 = Lists.newArrayList();
         if (var25 == ChunkRenderTask.Type.REBUILD_CHUNK) {
            BlockRenderLayer[] var10 = BlockRenderLayer.values();
            int var11 = var10.length;

            for(int var12 = 0; var12 < var11; ++var12) {
               BlockRenderLayer var13 = var10[var12];
               if (var26.func_178492_d(var13)) {
                  var9.add(this.field_178477_b.func_188245_a(var13, var1.func_178545_d().func_179038_a(var13), var1.func_178536_b(), var26, var1.func_188228_i()));
               }
            }
         } else if (var25 == ChunkRenderTask.Type.RESORT_TRANSPARENCY) {
            var9.add(this.field_178477_b.func_188245_a(BlockRenderLayer.TRANSLUCENT, var1.func_178545_d().func_179038_a(BlockRenderLayer.TRANSLUCENT), var1.func_178536_b(), var26, var1.func_188228_i()));
         }

         ListenableFuture var27 = Futures.allAsList(var9);
         var1.func_178539_a(() -> {
            var27.cancel(false);
         });
         Futures.addCallback(var27, new FutureCallback<List<Object>>() {
            public void onSuccess(@Nullable List<Object> var1x) {
               ChunkRenderWorker.this.func_178473_b(var1);
               var1.func_178540_f().lock();

               try {
                  if (var1.func_178546_a() != ChunkRenderTask.Status.UPLOADING) {
                     if (!var1.func_178537_h()) {
                        ChunkRenderWorker.field_152478_a.warn("Chunk render task was {} when I expected it to be uploading; aborting task", var1.func_178546_a());
                     }

                     return;
                  }

                  var1.func_178535_a(ChunkRenderTask.Status.DONE);
               } finally {
                  var1.func_178540_f().unlock();
               }

               var1.func_178536_b().func_178580_a(var26);
            }

            public void onFailure(Throwable var1x) {
               ChunkRenderWorker.this.func_178473_b(var1);
               if (!(var1x instanceof CancellationException) && !(var1x instanceof InterruptedException)) {
                  Minecraft.func_71410_x().func_71404_a(CrashReport.func_85055_a(var1x, "Rendering chunk"));
               }

            }

            // $FF: synthetic method
            public void onSuccess(@Nullable Object var1x) {
               this.onSuccess((List)var1x);
            }
         });
      }
   }

   private boolean func_188263_a(BlockPos var1, World var2) {
      return !var2.func_72964_e(var1.func_177958_n() >> 4, var1.func_177952_p() >> 4).func_76621_g();
   }

   private RegionRenderCacheBuilder func_178475_b() throws InterruptedException {
      return this.field_178478_c != null ? this.field_178478_c : this.field_178477_b.func_178515_c();
   }

   private void func_178473_b(ChunkRenderTask var1) {
      if (this.field_178478_c == null) {
         this.field_178477_b.func_178512_a(var1.func_178545_d());
      }

   }

   public void func_188264_a() {
      this.field_188265_d = false;
   }
}
