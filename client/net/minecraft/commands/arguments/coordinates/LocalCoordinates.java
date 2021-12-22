package net.minecraft.commands.arguments.coordinates;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Objects;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class LocalCoordinates implements Coordinates {
   public static final char PREFIX_LOCAL_COORDINATE = '^';
   private final double left;
   // $FF: renamed from: up double
   private final double field_242;
   private final double forwards;

   public LocalCoordinates(double var1, double var3, double var5) {
      super();
      this.left = var1;
      this.field_242 = var3;
      this.forwards = var5;
   }

   public Vec3 getPosition(CommandSourceStack var1) {
      Vec2 var2 = var1.getRotation();
      Vec3 var3 = var1.getAnchor().apply(var1);
      float var4 = Mth.cos((var2.field_413 + 90.0F) * 0.017453292F);
      float var5 = Mth.sin((var2.field_413 + 90.0F) * 0.017453292F);
      float var6 = Mth.cos(-var2.field_412 * 0.017453292F);
      float var7 = Mth.sin(-var2.field_412 * 0.017453292F);
      float var8 = Mth.cos((-var2.field_412 + 90.0F) * 0.017453292F);
      float var9 = Mth.sin((-var2.field_412 + 90.0F) * 0.017453292F);
      Vec3 var10 = new Vec3((double)(var4 * var6), (double)var7, (double)(var5 * var6));
      Vec3 var11 = new Vec3((double)(var4 * var8), (double)var9, (double)(var5 * var8));
      Vec3 var12 = var10.cross(var11).scale(-1.0D);
      double var13 = var10.field_414 * this.forwards + var11.field_414 * this.field_242 + var12.field_414 * this.left;
      double var15 = var10.field_415 * this.forwards + var11.field_415 * this.field_242 + var12.field_415 * this.left;
      double var17 = var10.field_416 * this.forwards + var11.field_416 * this.field_242 + var12.field_416 * this.left;
      return new Vec3(var3.field_414 + var13, var3.field_415 + var15, var3.field_416 + var17);
   }

   public Vec2 getRotation(CommandSourceStack var1) {
      return Vec2.ZERO;
   }

   public boolean isXRelative() {
      return true;
   }

   public boolean isYRelative() {
      return true;
   }

   public boolean isZRelative() {
      return true;
   }

   public static LocalCoordinates parse(StringReader var0) throws CommandSyntaxException {
      int var1 = var0.getCursor();
      double var2 = readDouble(var0, var1);
      if (var0.canRead() && var0.peek() == ' ') {
         var0.skip();
         double var4 = readDouble(var0, var1);
         if (var0.canRead() && var0.peek() == ' ') {
            var0.skip();
            double var6 = readDouble(var0, var1);
            return new LocalCoordinates(var2, var4, var6);
         } else {
            var0.setCursor(var1);
            throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(var0);
         }
      } else {
         var0.setCursor(var1);
         throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(var0);
      }
   }

   private static double readDouble(StringReader var0, int var1) throws CommandSyntaxException {
      if (!var0.canRead()) {
         throw WorldCoordinate.ERROR_EXPECTED_DOUBLE.createWithContext(var0);
      } else if (var0.peek() != '^') {
         var0.setCursor(var1);
         throw Vec3Argument.ERROR_MIXED_TYPE.createWithContext(var0);
      } else {
         var0.skip();
         return var0.canRead() && var0.peek() != ' ' ? var0.readDouble() : 0.0D;
      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof LocalCoordinates)) {
         return false;
      } else {
         LocalCoordinates var2 = (LocalCoordinates)var1;
         return this.left == var2.left && this.field_242 == var2.field_242 && this.forwards == var2.forwards;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.left, this.field_242, this.forwards});
   }
}
