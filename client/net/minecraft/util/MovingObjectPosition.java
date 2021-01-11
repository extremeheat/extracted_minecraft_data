package net.minecraft.util;

import net.minecraft.entity.Entity;

public class MovingObjectPosition {
   private BlockPos field_178783_e;
   public MovingObjectPosition.MovingObjectType field_72313_a;
   public EnumFacing field_178784_b;
   public Vec3 field_72307_f;
   public Entity field_72308_g;

   public MovingObjectPosition(Vec3 var1, EnumFacing var2, BlockPos var3) {
      this(MovingObjectPosition.MovingObjectType.BLOCK, var1, var2, var3);
   }

   public MovingObjectPosition(Vec3 var1, EnumFacing var2) {
      this(MovingObjectPosition.MovingObjectType.BLOCK, var1, var2, BlockPos.field_177992_a);
   }

   public MovingObjectPosition(Entity var1) {
      this(var1, new Vec3(var1.field_70165_t, var1.field_70163_u, var1.field_70161_v));
   }

   public MovingObjectPosition(MovingObjectPosition.MovingObjectType var1, Vec3 var2, EnumFacing var3, BlockPos var4) {
      super();
      this.field_72313_a = var1;
      this.field_178783_e = var4;
      this.field_178784_b = var3;
      this.field_72307_f = new Vec3(var2.field_72450_a, var2.field_72448_b, var2.field_72449_c);
   }

   public MovingObjectPosition(Entity var1, Vec3 var2) {
      super();
      this.field_72313_a = MovingObjectPosition.MovingObjectType.ENTITY;
      this.field_72308_g = var1;
      this.field_72307_f = var2;
   }

   public BlockPos func_178782_a() {
      return this.field_178783_e;
   }

   public String toString() {
      return "HitResult{type=" + this.field_72313_a + ", blockpos=" + this.field_178783_e + ", f=" + this.field_178784_b + ", pos=" + this.field_72307_f + ", entity=" + this.field_72308_g + '}';
   }

   public static enum MovingObjectType {
      MISS,
      BLOCK,
      ENTITY;

      private MovingObjectType() {
      }
   }
}
