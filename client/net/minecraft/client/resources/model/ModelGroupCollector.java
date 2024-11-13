package net.minecraft.client.resources.model;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.block.model.UnbakedBlockStateModel;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

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
         List var5 = (List)var2.computeIfAbsent(var4x.state().getBlock(), (var1) -> List.copyOf(var0.getColoringProperties(var1)));
         GroupKey var6 = ModelGroupCollector.GroupKey.create(var4x.state(), var4x.model(), var5);
         ((Set)var3.computeIfAbsent(var6, (var0x) -> Sets.newIdentityHashSet())).add(var4x.state());
      });
      int var4 = 1;
      Object2IntOpenHashMap var5 = new Object2IntOpenHashMap();
      var5.defaultReturnValue(-1);

      for(Set var7 : var3.values()) {
         Iterator var8 = var7.iterator();

         while(var8.hasNext()) {
            BlockState var9 = (BlockState)var8.next();
            if (var9.getRenderShape() != RenderShape.MODEL) {
               var8.remove();
               var5.put(var9, 0);
            }
         }

         if (var7.size() > 1) {
            int var10 = var4++;
            var7.forEach((var2x) -> var5.put(var2x, var10));
         }
      }

      return var5;
   }

   static record GroupKey(Object equalityGroup, List<Object> coloringValues) {
      private GroupKey(Object var1, List<Object> var2) {
         super();
         this.equalityGroup = var1;
         this.coloringValues = var2;
      }

      public static GroupKey create(BlockState var0, UnbakedBlockStateModel var1, List<Property<?>> var2) {
         List var3 = getColoringValues(var0, var2);
         Object var4 = var1.visualEqualityGroup(var0);
         return new GroupKey(var4, var3);
      }

      private static List<Object> getColoringValues(BlockState var0, List<Property<?>> var1) {
         Object[] var2 = new Object[var1.size()];

         for(int var3 = 0; var3 < var1.size(); ++var3) {
            var2[var3] = var0.getValue((Property)var1.get(var3));
         }

         return List.of(var2);
      }
   }
}
