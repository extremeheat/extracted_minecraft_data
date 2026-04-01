package net.minecraft.advancements.criterion;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public record LivingBlockPredicate(ItemPredicate item) implements EntitySubPredicate {
   public static final MapCodec<LivingBlockPredicate> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(ItemPredicate.CODEC.fieldOf("item").forGetter(LivingBlockPredicate::item)).apply(i, LivingBlockPredicate::new));

   public LivingBlockPredicate {
      super();
   }

   public static LivingBlockPredicate ofItem(final ItemPredicate item) {
      return new LivingBlockPredicate(item);
   }

   public boolean matches(final Entity entity, final ServerLevel level, final @Nullable Vec3 position) {
      if (entity instanceof LivingBlock livingBlock) {
         return this.item.test((ItemInstance)livingBlock.getItemStack());
      } else {
         return false;
      }
   }

   public MapCodec<LivingBlockPredicate> codec() {
      return EntitySubPredicates.LIVING_BLOCK;
   }
}
