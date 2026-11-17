package net.minecraft.core.dispenser;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.BlockHitResult;

public class EmptyBucketDispenseBehavior implements DispenseBehavior {
   public static final EmptyBucketDispenseBehavior INSTANCE = new EmptyBucketDispenseBehavior();
   public static final MapCodec<EmptyBucketDispenseBehavior> MAP_CODEC;

   private EmptyBucketDispenseBehavior() {
      super();
   }

   public DispenseBehavior.DispenseResult execute(final BlockSource source, final ItemStack dispensed) {
      Item var4 = dispensed.getItem();
      if (var4 instanceof DispensibleContainerItem bucket) {
         BlockPos var6 = source.pos().relative((Direction)source.state().getValue(DispenserBlock.FACING));
         ServerLevel level = source.level();
         if (bucket.emptyContents((LivingEntity)null, level, var6, (BlockHitResult)null)) {
            bucket.checkExtraContent((LivingEntity)null, level, dispensed, var6);
            return DispenseBehavior.consumeWithRemainder(source, dispensed, new ItemStack(Items.BUCKET));
         } else {
            return DispenseBehavior.DispenseResult.failure();
         }
      } else {
         return DispenseBehavior.DispenseResult.failure();
      }
   }

   public MapCodec<EmptyBucketDispenseBehavior> codec() {
      return MAP_CODEC;
   }

   static {
      MAP_CODEC = MapCodec.unit(INSTANCE);
   }
}
