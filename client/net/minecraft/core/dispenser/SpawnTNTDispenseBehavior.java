package net.minecraft.core.dispenser;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gamerules.GameRules;

public class SpawnTNTDispenseBehavior implements DispenseBehavior {
   public static final SpawnTNTDispenseBehavior INSTANCE = new SpawnTNTDispenseBehavior();
   public static final MapCodec<SpawnTNTDispenseBehavior> MAP_CODEC;

   private SpawnTNTDispenseBehavior() {
      super();
   }

   public DispenseBehavior.DispenseResult execute(final BlockSource source, final ItemStack dispensed) {
      ServerLevel level = source.level();
      if (!(Boolean)level.getGameRules().get(GameRules.TNT_EXPLODES)) {
         return DispenseBehavior.DispenseResult.failure();
      } else {
         BlockPos target = source.pos().relative((Direction)source.state().getValue(DispenserBlock.FACING));
         PrimedTnt tnt = new PrimedTnt(level, (double)target.getX() + 0.5, (double)target.getY(), (double)target.getZ() + 0.5, (LivingEntity)null);
         level.addFreshEntity(tnt);
         level.playSound((Entity)null, tnt.getX(), tnt.getY(), tnt.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS);
         level.gameEvent((Entity)null, GameEvent.ENTITY_PLACE, target);
         dispensed.shrink(1);
         return DispenseBehavior.DispenseResult.success(dispensed);
      }
   }

   public MapCodec<SpawnTNTDispenseBehavior> codec() {
      return MAP_CODEC;
   }

   static {
      MAP_CODEC = MapCodec.unit(INSTANCE);
   }
}
