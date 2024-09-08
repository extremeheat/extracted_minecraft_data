package net.minecraft.client.renderer.chunk;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.ListIterator;
import javax.annotation.Nullable;
import net.minecraft.world.phys.Vec3;

public class CompileTaskDynamicQueue {
   private static final int MAX_RECOMPILE_QUOTA = 2;
   private int recompileQuota = 2;
   private final List<SectionRenderDispatcher.RenderSection.CompileTask> tasks = new ObjectArrayList();

   public CompileTaskDynamicQueue() {
      super();
   }

   public synchronized void add(SectionRenderDispatcher.RenderSection.CompileTask var1) {
      this.tasks.add(var1);
   }

   @Nullable
   public synchronized SectionRenderDispatcher.RenderSection.CompileTask poll(Vec3 var1) {
      int var2 = -1;
      int var3 = -1;
      double var4 = 1.7976931348623157E308;
      double var6 = 1.7976931348623157E308;
      ListIterator var8 = this.tasks.listIterator();

      while (var8.hasNext()) {
         int var9 = var8.nextIndex();
         SectionRenderDispatcher.RenderSection.CompileTask var10 = (SectionRenderDispatcher.RenderSection.CompileTask)var8.next();
         if (var10.isCancelled.get()) {
            var8.remove();
         } else {
            double var11 = var10.getOrigin().distToCenterSqr(var1);
            if (!var10.isRecompile() && var11 < var4) {
               var4 = var11;
               var2 = var9;
            }

            if (var10.isRecompile() && var11 < var6) {
               var6 = var11;
               var3 = var9;
            }
         }
      }

      boolean var13 = var3 >= 0;
      boolean var14 = var2 >= 0;
      if (!var13 || var14 && (this.recompileQuota <= 0 || !(var6 < var4))) {
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
      return var1 >= 0 ? this.tasks.remove(var1) : null;
   }

   public synchronized void clear() {
      for (SectionRenderDispatcher.RenderSection.CompileTask var2 : this.tasks) {
         var2.cancel();
      }

      this.tasks.clear();
   }
}
