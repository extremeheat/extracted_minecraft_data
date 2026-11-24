package net.minecraft.world.damagesource;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public record FellFromLocation(String id) {
   public static final FellFromLocation GENERIC = new FellFromLocation("generic");
   public static final FellFromLocation LADDER = new FellFromLocation("ladder");
   public static final FellFromLocation VINES = new FellFromLocation("vines");
   public static final FellFromLocation WEEPING_VINES = new FellFromLocation("weeping_vines");
   public static final FellFromLocation TWISTING_VINES = new FellFromLocation("twisting_vines");
   public static final FellFromLocation SCAFFOLDING = new FellFromLocation("scaffolding");
   public static final FellFromLocation OTHER_CLIMBABLE = new FellFromLocation("other_climbable");
   public static final FellFromLocation WATER = new FellFromLocation("water");

   public FellFromLocation {
      super();
   }

   public static @Nullable FellFromLocation getCurrentFellFromLocation(final LivingEntity mob) {
      Optional<BlockPos> lastClimbablePos = mob.getLastClimbablePos();
      if (lastClimbablePos.isPresent()) {
         Level level = mob.level();
         BlockPos pos = (BlockPos)lastClimbablePos.get();
         BlockState blockState = level.getBlockState(pos);
         return blockState.getFellFromLocation(level, pos, mob);
      } else {
         return mob.isInWater() ? WATER : null;
      }
   }

   public String languageKey() {
      return "death.fell.accident." + this.id;
   }
}
