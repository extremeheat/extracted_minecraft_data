package net.minecraft.world.entity;

public class EntityDimensions {
   public final float width;
   public final float height;
   public final boolean fixed;

   public EntityDimensions(float var1, float var2, boolean var3) {
      this.width = var1;
      this.height = var2;
      this.fixed = var3;
   }

   public EntityDimensions scale(float var1) {
      return this.scale(var1, var1);
   }

   public EntityDimensions scale(float var1, float var2) {
      return !this.fixed && (var1 != 1.0F || var2 != 1.0F) ? scalable(this.width * var1, this.height * var2) : this;
   }

   public static EntityDimensions scalable(float var0, float var1) {
      return new EntityDimensions(var0, var1, false);
   }

   public static EntityDimensions fixed(float var0, float var1) {
      return new EntityDimensions(var0, var1, true);
   }

   public String toString() {
      return "EntityDimensions w=" + this.width + ", h=" + this.height + ", fixed=" + this.fixed;
   }
}
