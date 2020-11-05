package net.minecraft.client.model.geom.builders;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;

public class PartDefinition {
   private final List<CubeDefinition> cubes;
   private final PartPose partPose;
   private final Map<String, PartDefinition> children = Maps.newHashMap();

   PartDefinition(List<CubeDefinition> var1, PartPose var2) {
      super();
      this.cubes = var1;
      this.partPose = var2;
   }

   public PartDefinition addOrReplaceChild(String var1, CubeListBuilder var2, PartPose var3) {
      PartDefinition var4 = new PartDefinition(var2.getCubes(), var3);
      PartDefinition var5 = (PartDefinition)this.children.put(var1, var4);
      if (var5 != null) {
         var4.children.putAll(var5.children);
      }

      return var4;
   }

   public ModelPart bake(int var1, int var2) {
      Object2ObjectArrayMap var3 = (Object2ObjectArrayMap)this.children.entrySet().stream().collect(Collectors.toMap(Entry::getKey, (var2x) -> {
         return ((PartDefinition)var2x.getValue()).bake(var1, var2);
      }, (var0, var1x) -> {
         return var0;
      }, Object2ObjectArrayMap::new));
      List var4 = (List)this.cubes.stream().map((var2x) -> {
         return var2x.bake(var1, var2);
      }).collect(ImmutableList.toImmutableList());
      ModelPart var5 = new ModelPart(var4, var3);
      var5.loadPose(this.partPose);
      return var5;
   }

   public PartDefinition getChild(String var1) {
      return (PartDefinition)this.children.get(var1);
   }
}
