package net.minecraft.world.entity.livingblock;

public enum CollisionInteraction {
   BLOCK,
   ENTITY,
   NONE;

   private CollisionInteraction() {
   }

   public boolean canBeCollidedWith() {
      return this == BLOCK;
   }

   public boolean canBeNudged() {
      return this == ENTITY;
   }

   public boolean isPushable() {
      return this != NONE;
   }

   // $FF: synthetic method
   private static CollisionInteraction[] $values() {
      return new CollisionInteraction[]{BLOCK, ENTITY, NONE};
   }
}
