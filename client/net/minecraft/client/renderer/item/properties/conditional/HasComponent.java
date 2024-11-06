package net.minecraft.client.renderer.item.properties.conditional;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public record HasComponent(DataComponentType<?> componentType) implements ConditionalItemModelProperty {
   public static final MapCodec<HasComponent> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(BuiltInRegistries.DATA_COMPONENT_TYPE.byNameCodec().fieldOf("component").forGetter(HasComponent::componentType)).apply(var0, HasComponent::new);
   });

   public HasComponent(DataComponentType<?> var1) {
      super();
      this.componentType = var1;
   }

   public boolean get(ItemStack var1, @Nullable ClientLevel var2, @Nullable LivingEntity var3, int var4) {
      return var1.has(this.componentType);
   }

   public MapCodec<HasComponent> type() {
      return MAP_CODEC;
   }

   public DataComponentType<?> componentType() {
      return this.componentType;
   }
}
