package net.minecraft.core.dispenser;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PostSpawnProcessor;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ArmorStandItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;

public class PlaceArmorStandDispenseBehavior implements DispenseBehavior {
   public static final PlaceArmorStandDispenseBehavior INSTANCE = new PlaceArmorStandDispenseBehavior();
   public static final MapCodec<PlaceArmorStandDispenseBehavior> MAP_CODEC;

   private PlaceArmorStandDispenseBehavior() {
      super();
   }

   public DispenseBehavior.DispenseResult execute(final BlockSource source, final ItemStack dispensed) {
      Direction direction = (Direction)source.state().getValue(DispenserBlock.FACING);
      BlockPos pos = source.pos().relative(direction);
      ServerLevel serverLevel = source.level();
      PostSpawnProcessor<ArmorStand> postSpawnConfig = EntityType.<ArmorStand>appendDefaultStackConfig((armorStandx) -> armorStandx.setYRot(direction.toYRot()), serverLevel, dispensed, (LivingEntity)null);
      ArmorStand armorStand = EntityTypes.ARMOR_STAND.spawn(serverLevel, postSpawnConfig, pos, EntitySpawnReason.DISPENSER, false, false);
      if (armorStand != null) {
         ArmorStandItem.playSound(serverLevel, armorStand);
         dispensed.shrink(1);
      }

      return DispenseBehavior.DispenseResult.success(dispensed);
   }

   public MapCodec<PlaceArmorStandDispenseBehavior> codec() {
      return MAP_CODEC;
   }

   static {
      MAP_CODEC = MapCodec.unit(INSTANCE);
   }
}
