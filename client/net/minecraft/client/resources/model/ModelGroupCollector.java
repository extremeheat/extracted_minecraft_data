package net.minecraft.client.resources.model;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

public class ModelGroupCollector {
   static final int SINGLETON_MODEL_GROUP = -1;
   private static final int INVISIBLE_MODEL_GROUP = 0;

   public ModelGroupCollector() {
      super();
   }

   public static Object2IntMap<BlockState> build(BlockColors var0, BlockStateModelLoader.LoadedModels var1) {
      HashMap var2 = new HashMap();
      HashMap var3 = new HashMap();
      var1.models().forEach((var3x, var4x) -> {
         List var5x = var2.computeIfAbsent(var4x.state().getBlock(), var1xx -> List.copyOf(var0.getColoringProperties(var1xx)));
         ModelGroupCollector.GroupKey var6 = ModelGroupCollector.GroupKey.create(var4x.state(), var4x.model(), var5x);
         var3.computeIfAbsent(var6, var0xx -> Sets.newIdentityHashSet()).add(var4x.state());
      });
      int var4 = 1;
      Object2IntOpenHashMap var5 = new Object2IntOpenHashMap();
      var5.defaultReturnValue(-1);

      for (Set var7 : var3.values()) {
         Iterator var8 = var7.iterator();

         while (var8.hasNext()) {
            BlockState var9 = (BlockState)var8.next();
            if (var9.getRenderShape() != RenderShape.MODEL) {
               var8.remove();
               var5.put(var9, 0);
            }
         }

         if (var7.size() > 1) {
            int var10 = var4++;
            var7.forEach(var2x -> var5.put(var2x, var10));
         }
      }

      return var5;
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "varName" is null
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
