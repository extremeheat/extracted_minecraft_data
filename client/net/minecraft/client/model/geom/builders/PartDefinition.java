package net.minecraft.client.model.geom.builders;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;
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
      return this.addOrReplaceChild(var1, var4);
   }

   public PartDefinition addOrReplaceChild(String var1, PartDefinition var2) {
      PartDefinition var3 = (PartDefinition)this.children.put(var1, var2);
      if (var3 != null) {
         var2.children.putAll(var3.children);
      }

      return var2;
   }

   public PartDefinition clearRecursively() {
      for(String var2 : this.children.keySet()) {
         this.clearChild(var2).clearRecursively();
      }

      return this;
   }

   public PartDefinition clearChild(String var1) {
      PartDefinition var2 = (PartDefinition)this.children.get(var1);
      if (var2 == null) {
         throw new IllegalArgumentException("No child with name: " + var1);
      } else {
         return this.addOrReplaceChild(var1, CubeListBuilder.create(), var2.partPose);
      }
   }

   public void retainPartsAndChildren(Set<String> var1) {
      for(Map.Entry var3 : this.children.entrySet()) {
         PartDefinition var4 = (PartDefinition)var3.getValue();
         if (!var1.contains(var3.getKey())) {
            this.addOrReplaceChild((String)var3.getKey(), CubeListBuilder.create(), var4.partPose).retainPartsAndChildren(var1);
         }
      }

   }

   public void retainExactParts(Set<String> var1) {
      for(Map.Entry var3 : this.children.entrySet()) {
         PartDefinition var4 = (PartDefinition)var3.getValue();
         if (var1.contains(var3.getKey())) {
            var4.clearRecursively();
         } else {
            this.addOrReplaceChild((String)var3.getKey(), CubeListBuilder.create(), var4.partPose).retainExactParts(var1);
         }
      }

   }

   public ModelPart bake(int var1, int var2) {
      Object2ObjectArrayMap var3 = (Object2ObjectArrayMap)this.children.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, (var2x) -> ((PartDefinition)var2x.getValue()).bake(var1, var2), (var0, var1x) -> var0, Object2ObjectArrayMap::new));
      List var4 = this.cubes.stream().map((var2x) -> var2x.bake(var1, var2)).toList();
      ModelPart var5 = new ModelPart(var4, var3);
      var5.setInitialPose(this.partPose);
      var5.loadPose(this.partPose);
      return var5;
   }

   public PartDefinition getChild(String var1) {
      return (PartDefinition)this.children.get(var1);
   }

   public Set<Map.Entry<String, PartDefinition>> getChildren() {
      return this.children.entrySet();
   }

   public PartDefinition transformed(UnaryOperator<PartPose> var1) {
      PartDefinition var2 = new PartDefinition(this.cubes, (PartPose)var1.apply(this.partPose));
      var2.children.putAll(this.children);
      return var2;
   }
}
