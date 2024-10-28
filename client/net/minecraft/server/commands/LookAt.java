package net.minecraft.server.commands;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

@FunctionalInterface
public interface LookAt {
   void perform(CommandSourceStack var1, Entity var2);

   public static record LookAtPosition(Vec3 position) implements LookAt {
      public LookAtPosition(Vec3 var1) {
         super();
         this.position = var1;
      }

      public void perform(CommandSourceStack var1, Entity var2) {
         var2.lookAt(var1.getAnchor(), this.position);
      }

      public Vec3 position() {
         return this.position;
      }
   }

   public static record LookAtEntity(Entity entity, EntityAnchorArgument.Anchor anchor) implements LookAt {
      public LookAtEntity(Entity var1, EntityAnchorArgument.Anchor var2) {
         super();
         this.entity = var1;
         this.anchor = var2;
      }

      public void perform(CommandSourceStack var1, Entity var2) {
         if (var2 instanceof ServerPlayer var3) {
            var3.lookAt(var1.getAnchor(), this.entity, this.anchor);
         } else {
            var2.lookAt(var1.getAnchor(), this.anchor.apply(this.entity));
         }

      }

      public Entity entity() {
         return this.entity;
      }

      public EntityAnchorArgument.Anchor anchor() {
         return this.anchor;
      }
   }
}
