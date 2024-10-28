package net.minecraft.client.resources.model;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class MultiPartBakedModel extends DelegateBakedModel {
   private final List<Selector> selectors;
   private final Map<BlockState, BitSet> selectorCache = new Reference2ObjectOpenHashMap();

   private static BakedModel getFirstModel(List<Selector> var0) {
      if (var0.isEmpty()) {
         throw new IllegalArgumentException("Model must have at least one selector");
      } else {
         return ((Selector)var0.getFirst()).model();
      }
   }

   public MultiPartBakedModel(List<Selector> var1) {
      super(getFirstModel(var1));
      this.selectors = var1;
   }

   public List<BakedQuad> getQuads(@Nullable BlockState var1, @Nullable Direction var2, RandomSource var3) {
      if (var1 == null) {
         return Collections.emptyList();
      } else {
         BitSet var4 = (BitSet)this.selectorCache.get(var1);
         if (var4 == null) {
            var4 = new BitSet();

            for(int var5 = 0; var5 < this.selectors.size(); ++var5) {
               if (((Selector)this.selectors.get(var5)).condition.test(var1)) {
                  var4.set(var5);
               }
            }

            this.selectorCache.put(var1, var4);
         }

         ArrayList var9 = new ArrayList();
         long var6 = var3.nextLong();

         for(int var8 = 0; var8 < var4.length(); ++var8) {
            if (var4.get(var8)) {
               var3.setSeed(var6);
               var9.addAll(((Selector)this.selectors.get(var8)).model.getQuads(var1, var2, var3));
            }
         }

         return var9;
      }
   }

   public static record Selector(Predicate<BlockState> condition, BakedModel model) {
      final Predicate<BlockState> condition;
      final BakedModel model;

      public Selector(Predicate<BlockState> var1, BakedModel var2) {
         super();
         this.condition = var1;
         this.model = var2;
      }

      public Predicate<BlockState> condition() {
         return this.condition;
      }

      public BakedModel model() {
         return this.model;
      }
   }
}
