package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.DimensionTransition;

public interface Portal {
   default int getPortalTransitionTime(ServerLevel var1, Entity var2) {
      return 0;
   }

   @Nullable
   DimensionTransition getPortalDestination(ServerLevel var1, Entity var2, BlockPos var3);

   default Transition getLocalTransition() {
      return Portal.Transition.NONE;
   }

   public static enum Transition {
      CONFUSION,
      NONE;

      private Transition() {
      }

      // $FF: synthetic method
      private static Transition[] $values() {
         return new Transition[]{CONFUSION, NONE};
      }
   }
}
