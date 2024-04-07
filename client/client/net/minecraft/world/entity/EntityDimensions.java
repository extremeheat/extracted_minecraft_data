package net.minecraft.world.entity;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public record EntityDimensions(float width, float height, float eyeHeight, EntityAttachments attachments, boolean fixed) {
   private EntityDimensions(float var1, float var2, boolean var3) {
      this(var1, var2, defaultEyeHeight(var2), EntityAttachments.createDefault(var1, var2), var3);
   }

   public EntityDimensions(float width, float height, float eyeHeight, EntityAttachments attachments, boolean fixed) {
      super();
      this.width = width;
      this.height = height;
      this.eyeHeight = eyeHeight;
      this.attachments = attachments;
      this.fixed = fixed;
   }

   private static float defaultEyeHeight(float var0) {
      return var0 * 0.85F;
   }

   public AABB makeBoundingBox(Vec3 var1) {
      return this.makeBoundingBox(var1.x, var1.y, var1.z);
   }

   public AABB makeBoundingBox(double var1, double var3, double var5) {
      float var7 = this.width / 2.0F;
      float var8 = this.height;
      return new AABB(var1 - (double)var7, var3, var5 - (double)var7, var1 + (double)var7, var3 + (double)var8, var5 + (double)var7);
   }

   public EntityDimensions scale(float var1) {
      return this.scale(var1, var1);
   }

   public EntityDimensions scale(float var1, float var2) {
      return !this.fixed && (var1 != 1.0F || var2 != 1.0F)
         ? new EntityDimensions(this.width * var1, this.height * var2, this.eyeHeight * var2, this.attachments.scale(var1, var2, var1), false)
         : this;
   }

   public static EntityDimensions scalable(float var0, float var1) {
      return new EntityDimensions(var0, var1, false);
   }

   public static EntityDimensions fixed(float var0, float var1) {
      return new EntityDimensions(var0, var1, true);
   }

   public EntityDimensions withEyeHeight(float var1) {
      return new EntityDimensions(this.width, this.height, var1, this.attachments, this.fixed);
   }

   public EntityDimensions withAttachments(EntityAttachments.Builder var1) {
      return new EntityDimensions(this.width, this.height, this.eyeHeight, var1.build(this.width, this.height), this.fixed);
   }
}
