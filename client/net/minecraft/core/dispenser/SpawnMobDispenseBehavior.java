package net.minecraft.core.dispenser;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import org.slf4j.Logger;

public class SpawnMobDispenseBehavior implements DispenseBehavior {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final SpawnMobDispenseBehavior INSTANCE = new SpawnMobDispenseBehavior();
   public static final MapCodec<SpawnMobDispenseBehavior> MAP_CODEC;

   private SpawnMobDispenseBehavior() {
      super();
   }

   public DispenseBehavior.DispenseResult execute(final BlockSource source, final ItemStack dispensed) {
      Direction direction = (Direction)source.state().getValue(DispenserBlock.FACING);
      EntityType<?> type = TypedEntityData.getType(dispensed);
      if (type == null) {
         return DispenseBehavior.DispenseResult.failure();
      } else {
         try {
            Entity spawned = type.spawn(source.level(), dispensed, (LivingEntity)null, source.pos().relative(direction), EntitySpawnReason.DISPENSER, direction != Direction.UP, false);
            if (spawned == null) {
               return DispenseBehavior.DispenseResult.failure();
            }
         } catch (Exception e) {
            LOGGER.error("Error while dispensing spawn egg from dispenser at {}", source.pos(), e);
            return DispenseBehavior.DispenseResult.failure();
         }

         dispensed.shrink(1);
         source.level().gameEvent((Entity)null, GameEvent.ENTITY_PLACE, source.pos());
         return DispenseBehavior.DispenseResult.success(dispensed);
      }
   }

   public MapCodec<SpawnMobDispenseBehavior> codec() {
      return MAP_CODEC;
   }

   static {
      MAP_CODEC = MapCodec.unit(INSTANCE);
   }
}
