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
   public ArmorModelSet {
      super();
   }

   public T get(final EquipmentSlot slot) {
      Object var10000;
      switch (slot) {
         case HEAD -> var10000 = this.head;
         case CHEST -> var10000 = this.chest;
         case LEGS -> var10000 = this.legs;
         case FEET -> var10000 = this.feet;
         default -> throw new IllegalStateException("No model for slot: " + String.valueOf(slot));
      }

      return (T)var10000;
   }

   public <U> ArmorModelSet<U> map(final Function<? super T, ? extends U> mapper) {
      return new ArmorModelSet<U>(mapper.apply(this.head), mapper.apply(this.chest), mapper.apply(this.legs), mapper.apply(this.feet));
   }

   public void putFrom(final ArmorModelSet<LayerDefinition> values, final ImmutableMap.Builder<T, LayerDefinition> output) {
      output.put(this.head, (LayerDefinition)values.head);
      output.put(this.chest, (LayerDefinition)values.chest);
      output.put(this.legs, (LayerDefinition)values.legs);
      output.put(this.feet, (LayerDefinition)values.feet);
   }

   public static <M extends HumanoidModel<?>> ArmorModelSet<M> bake(final ArmorModelSet<ModelLayerLocation> locations, final EntityModelSet modelSet, final Function<ModelPart, M> factory) {
      return locations.<M>map((id) -> (HumanoidModel)factory.apply(modelSet.bakeLayer(id)));
   }
}
