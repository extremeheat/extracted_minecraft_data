package net.minecraft.commands.arguments.coordinates;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class WorldCoordinates implements Coordinates {
   // $FF: renamed from: x net.minecraft.commands.arguments.coordinates.WorldCoordinate
   private final WorldCoordinate field_131;
   // $FF: renamed from: y net.minecraft.commands.arguments.coordinates.WorldCoordinate
   private final WorldCoordinate field_132;
   // $FF: renamed from: z net.minecraft.commands.arguments.coordinates.WorldCoordinate
   private final WorldCoordinate field_133;

   public WorldCoordinates(WorldCoordinate var1, WorldCoordinate var2, WorldCoordinate var3) {
      super();
      this.field_131 = var1;
      this.field_132 = var2;
      this.field_133 = var3;
   }

   public Vec3 getPosition(CommandSourceStack var1) {
      Vec3 var2 = var1.getPosition();
      return new Vec3(this.field_131.get(var2.field_414), this.field_132.get(var2.field_415), this.field_133.get(var2.field_416));
   }

   public Vec2 getRotation(CommandSourceStack var1) {
      Vec2 var2 = var1.getRotation();
      return new Vec2((float)this.field_131.get((double)var2.field_412), (float)this.field_132.get((double)var2.field_413));
   }

   public boolean isXRelative() {
      return this.field_131.isRelative();
   }

   public boolean isYRelative() {
      return this.field_132.isRelative();
   }

   public boolean isZRelative() {
      return this.field_133.isRelative();
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof WorldCoordinates)) {
         return false;
      } else {
         WorldCoordinates var2 = (WorldCoordinates)var1;
         if (!this.field_131.equals(var2.field_131)) {
            return false;
         } else {
            return !this.field_132.equals(var2.field_132) ? false : this.field_133.equals(var2.field_133);
         }
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
      return new WorldCoordinates(new WorldCoordinate(false, (double)var0.field_412), new WorldCoordinate(false, (double)var0.field_413), new WorldCoordinate(true, 0.0D));
   }

   public static WorldCoordinates current() {
      return new WorldCoordinates(new WorldCoordinate(true, 0.0D), new WorldCoordinate(true, 0.0D), new WorldCoordinate(true, 0.0D));
   }

   public int hashCode() {
      int var1 = this.field_131.hashCode();
      var1 = 31 * var1 + this.field_132.hashCode();
      var1 = 31 * var1 + this.field_133.hashCode();
      return var1;
   }
}
