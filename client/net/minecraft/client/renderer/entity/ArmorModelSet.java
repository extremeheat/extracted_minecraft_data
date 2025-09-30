package net.minecraft.client.renderer.entity;

import com.google.common.collect.ImmutableMap;
import java.util.function.Function;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.world.entity.EquipmentSlot;

public record ArmorModelSet<T>(T head, T chest, T legs, T feet) {
   public ArmorModelSet(T var1, T var2, T var3, T var4) {
      super();
      this.head = var1;
      this.chest = var2;
      this.legs = var3;
      this.feet = var4;
   }

   public T get(EquipmentSlot var1) {
      Object var10000;
      switch (var1) {
         case HEAD -> var10000 = this.head;
         case CHEST -> var10000 = this.chest;
         case LEGS -> var10000 = this.legs;
         case FEET -> var10000 = this.feet;
         default -> throw new IllegalStateException("No model for slot: " + String.valueOf(var1));
      }

      return (T)var10000;
   }

   public <U> ArmorModelSet<U> map(Function<? super T, ? extends U> var1) {
      return new ArmorModelSet<U>(var1.apply(this.head), var1.apply(this.chest), var1.apply(this.legs), var1.apply(this.feet));
   }

   public void putFrom(ArmorModelSet<LayerDefinition> var1, ImmutableMap.Builder<T, LayerDefinition> var2) {
      var2.put(this.head, (LayerDefinition)var1.head);
      var2.put(this.chest, (LayerDefinition)var1.chest);
      var2.put(this.legs, (LayerDefinition)var1.legs);
      var2.put(this.feet, (LayerDefinition)var1.feet);
   }

   public static <M extends HumanoidModel<?>> ArmorModelSet<M> bake(ArmorModelSet<ModelLayerLocation> var0, EntityModelSet var1, Function<ModelPart, M> var2) {
      return var0.<M>map((var2x) -> (HumanoidModel)var2.apply(var1.bakeLayer(var2x)));
   }
}
