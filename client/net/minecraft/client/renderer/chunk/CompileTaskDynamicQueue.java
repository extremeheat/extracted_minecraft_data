package net.minecraft.client.renderer.chunk;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.phys.Vec3;

public class CompileTaskDynamicQueue {
   private static final int MAX_RECOMPILE_QUOTA = 2;
   private int recompileQuota = 2;
   private final List<SectionRenderDispatcher.RenderSection.CompileTask> tasks = new ObjectArrayList();
   private final Object writeLock = new Object();

   public CompileTaskDynamicQueue() {
      super();
   }

   public void add(SectionRenderDispatcher.RenderSection.CompileTask var1) {
      synchronized (this.writeLock) {
         this.tasks.add(var1);
      }
   }

   @Nullable
   public SectionRenderDispatcher.RenderSection.CompileTask poll(Vec3 var1) {
      int var2 = -1;
      int var3 = -1;
      double var4 = 1.7976931348623157E308;
      double var6 = 1.7976931348623157E308;

      for (int var8 = 0; var8 < this.tasks.size(); var8++) {
         SectionRenderDispatcher.RenderSection.CompileTask var9 = this.tasks.get(var8);
         double var10 = var9.getOrigin().distToCenterSqr(var1);
         if (!var9.isRecompile() && var10 < var4) {
            var4 = var10;
            var2 = var8;
         }

         if (var9.isRecompile() && var10 < var6) {
            var6 = var10;
            var3 = var8;
         }
      }

      boolean var12 = var3 >= 0;
      boolean var13 = var2 >= 0;
      if (!var12 || var13 && (this.recompileQuota <= 0 || !(var6 < var4))) {
         this.recompileQuota = 2;
         return this.removeTaskByIndex(var2);
      } else {
         this.recompileQuota--;
         return this.removeTaskByIndex(var3);
      }
   }

   public int size() {
      return this.tasks.size();
   }

   @Nullable
   private SectionRenderDispatcher.RenderSection.CompileTask removeTaskByIndex(int var1) {
      if (var1 >= 0) {
         synchronized (this.writeLock) {
            return this.tasks.remove(var1);
         }
      } else {
         return null;
      }
   }

   public void clear() {
      synchronized (this.writeLock) {
         for (SectionRenderDispatcher.RenderSection.CompileTask var3 : this.tasks) {
            var3.cancel();
         }

         this.tasks.clear();
      }
   }
}
