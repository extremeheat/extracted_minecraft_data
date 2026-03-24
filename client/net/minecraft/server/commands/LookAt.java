package net.minecraft.server.commands;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

@FunctionalInterface
public interface LookAt {
   void perform(CommandSourceStack source, Entity target);

   public static record LookAtEntity(Entity entity, EntityAnchorArgument.Anchor anchor) implements LookAt {
      public LookAtEntity {
         super();
      }

      public void perform(final CommandSourceStack source, final Entity target) {
         if (target instanceof ServerPlayer targetPlayer) {
            targetPlayer.lookAt(source.getAnchor(), this.entity, this.anchor);
         } else {
            target.lookAt(source.getAnchor(), this.anchor.apply(this.entity));
         }

      }
   }

   public static record LookAtPosition(Vec3 position) implements LookAt {
      public LookAtPosition {
         super();
      }

      public void perform(final CommandSourceStack source, final Entity target) {
         target.lookAt(source.getAnchor(), this.position);
      }
   }
}
