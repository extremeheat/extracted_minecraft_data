package net.minecraft.client.resources.model;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class MultiPartBakedModel extends DelegateBakedModel {
   private final List<MultiPartBakedModel.Selector> selectors;
   private final Map<BlockState, BitSet> selectorCache = new Reference2ObjectOpenHashMap();

   private static BakedModel getFirstModel(List<MultiPartBakedModel.Selector> var0) {
      if (var0.isEmpty()) {
         throw new IllegalArgumentException("Model must have at least one selector");
      } else {
         return ((MultiPartBakedModel.Selector)var0.getFirst()).model();
      }
   }

   public MultiPartBakedModel(List<MultiPartBakedModel.Selector> var1) {
      super(getFirstModel(var1));
      this.selectors = var1;
   }

   @Override
   public List<BakedQuad> getQuads(@Nullable BlockState var1, @Nullable Direction var2, RandomSource var3) {
      if (var1 == null) {
         return Collections.emptyList();
      } else {
         BitSet var4 = this.selectorCache.get(var1);
         if (var4 == null) {
            var4 = new BitSet();

            for (int var5 = 0; var5 < this.selectors.size(); var5++) {
               if (this.selectors.get(var5).condition.test(var1)) {
                  var4.set(var5);
               }
            }

            this.selectorCache.put(var1, var4);
         }

         ArrayList var9 = new ArrayList();
         long var6 = var3.nextLong();

         for (int var8 = 0; var8 < var4.length(); var8++) {
            if (var4.get(var8)) {
               var3.setSeed(var6);
               var9.addAll(this.selectors.get(var8).model.getQuads(var1, var2, var3));
            }
         }

         return var9;
      }
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
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
