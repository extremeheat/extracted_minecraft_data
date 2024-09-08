package net.minecraft.commands.arguments.coordinates;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class WorldCoordinates implements Coordinates {
   private final WorldCoordinate x;
   private final WorldCoordinate y;
   private final WorldCoordinate z;

   public WorldCoordinates(WorldCoordinate var1, WorldCoordinate var2, WorldCoordinate var3) {
      super();
      this.x = var1;
      this.y = var2;
      this.z = var3;
   }

   @Override
   public Vec3 getPosition(CommandSourceStack var1, boolean var2) {
      double var3 = this.x.isRelative() && var2 ? 0.0 : var1.getPosition().x;
      double var5 = this.y.isRelative() && var2 ? 0.0 : var1.getPosition().y;
      double var7 = this.z.isRelative() && var2 ? 0.0 : var1.getPosition().z;
      return new Vec3(this.x.get(var3), this.y.get(var5), this.z.get(var7));
   }

   @Override
   public Vec2 getRotation(CommandSourceStack var1, boolean var2) {
      double var3 = this.x.isRelative() && var2 ? 0.0 : (double)var1.getRotation().x;
      double var5 = this.y.isRelative() && var2 ? 0.0 : (double)var1.getRotation().y;
      return new Vec2((float)this.x.get(var3), (float)this.y.get(var5));
   }

   @Override
   public boolean isXRelative() {
      return this.x.isRelative();
   }

   @Override
   public boolean isYRelative() {
      return this.y.isRelative();
   }

   @Override
   public boolean isZRelative() {
      return this.z.isRelative();
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof WorldCoordinates var2)) {
         return false;
      } else if (!this.x.equals(var2.x)) {
         return false;
      } else {
         return !this.y.equals(var2.y) ? false : this.z.equals(var2.z);
      }
   }

   public static WorldCoordinates parseInt(StringReader var0) throws CommandSyntaxException {
      int var1 = var0.getCursor();
      WorldCoordinate var2 = WorldCoordinate.parseInt(var0);
      if (var0.canRead() && var0.peek() == ' ') {
         var0.skip();
         WorldCoordinate var3 = WorldCoordinate.parseInt(var0);
         if (var0.canRead() && var0.peek() == ' ') {
            var0.skip();
            WorldCoordinate var4 = WorldCoordinate.parseInt(var0);
            return new WorldCoordinates(var2, var3, var4);
         } else {
            var0.setCursor(var1);
            throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(var0);
         }
      } else {
         var0.setCursor(var1);
         throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(var0);
      }
   }

   public static WorldCoordinates parseDouble(StringReader var0, boolean var1) throws CommandSyntaxException {
      int var2 = var0.getCursor();
      WorldCoordinate var3 = WorldCoordinate.parseDouble(var0, var1);
      if (var0.canRead() && var0.peek() == ' ') {
         var0.skip();
         WorldCoordinate var4 = WorldCoordinate.parseDouble(var0, false);
         if (var0.canRead() && var0.peek() == ' ') {
            var0.skip();
            WorldCoordinate var5 = WorldCoordinate.parseDouble(var0, var1);
            return new WorldCoordinates(var3, var4, var5);
         } else {
            var0.setCursor(var2);
            throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(var0);
         }
      } else {
         var0.setCursor(var2);
         throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(var0);
      }
   }

   public static WorldCoordinates absolute(double var0, double var2, double var4) {
      return new WorldCoordinates(new WorldCoordinate(false, var0), new WorldCoordinate(false, var2), new WorldCoordinate(false, var4));
   }

   public static WorldCoordinates absolute(Vec2 var0) {
      return new WorldCoordinates(new WorldCoordinate(false, (double)var0.x), new WorldCoordinate(false, (double)var0.y), new WorldCoordinate(true, 0.0));
   }

   public static WorldCoordinates current() {
      return new WorldCoordinates(new WorldCoordinate(true, 0.0), new WorldCoordinate(true, 0.0), new WorldCoordinate(true, 0.0));
   }

   @Override
   public int hashCode() {
      int var1 = this.x.hashCode();
      var1 = 31 * var1 + this.y.hashCode();
      return 31 * var1 + this.z.hashCode();
   }
}
