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

   public PartDefinition clearChild(String var1) {
      return this.addOrReplaceChild(var1, CubeListBuilder.create(), PartPose.ZERO);
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
