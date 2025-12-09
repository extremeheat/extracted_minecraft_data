package net.minecraft.client.renderer.item.properties.conditional;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.component.predicates.DataComponentPredicate;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public record ComponentMatches(DataComponentPredicate.Single<?> predicate) implements ConditionalItemModelProperty {
   public static final MapCodec<ComponentMatches> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(DataComponentPredicate.singleCodec("predicate").forGetter(ComponentMatches::predicate)).apply(var0, ComponentMatches::new));

   public ComponentMatches(DataComponentPredicate.Single<?> var1) {
      super();
      this.predicate = var1;
   }

   public boolean get(ItemStack var1, @Nullable ClientLevel var2, @Nullable LivingEntity var3, int var4, ItemDisplayContext var5) {
      return this.predicate.predicate().matches(var1);
   }

   public MapCodec<ComponentMatches> type() {
      return MAP_CODEC;
   }
}
