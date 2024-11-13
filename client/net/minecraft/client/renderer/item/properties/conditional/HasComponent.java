package net.minecraft.client.renderer.item.properties.conditional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public record HasComponent(DataComponentType<?> componentType, boolean ignoreDefault) implements ConditionalItemModelProperty {
   public static final MapCodec<HasComponent> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(BuiltInRegistries.DATA_COMPONENT_TYPE.byNameCodec().fieldOf("component").forGetter(HasComponent::componentType), Codec.BOOL.optionalFieldOf("ignore_default", false).forGetter(HasComponent::ignoreDefault)).apply(var0, HasComponent::new));

   public HasComponent(DataComponentType<?> var1, boolean var2) {
      super();
      this.componentType = var1;
      this.ignoreDefault = var2;
   }

   public boolean get(ItemStack var1, @Nullable ClientLevel var2, @Nullable LivingEntity var3, int var4, ItemDisplayContext var5) {
      return this.ignoreDefault ? var1.hasNonDefault(this.componentType) : var1.has(this.componentType);
   }

   public MapCodec<HasComponent> type() {
      return MAP_CODEC;
   }
}
