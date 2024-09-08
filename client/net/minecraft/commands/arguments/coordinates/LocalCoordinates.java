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
   private final double up;
   private final double forwards;

   public LocalCoordinates(double var1, double var3, double var5) {
      super();
      this.left = var1;
      this.up = var3;
      this.forwards = var5;
   }

   @Override
   public Vec3 getPosition(CommandSourceStack var1, boolean var2) {
      Vec2 var3 = var1.getRotation();
      Vec3 var4 = var1.getAnchor().apply(var1);
      float var5 = Mth.cos((var3.y + 90.0F) * 0.017453292F);
      float var6 = Mth.sin((var3.y + 90.0F) * 0.017453292F);
      float var7 = Mth.cos(-var3.x * 0.017453292F);
      float var8 = Mth.sin(-var3.x * 0.017453292F);
      float var9 = Mth.cos((-var3.x + 90.0F) * 0.017453292F);
      float var10 = Mth.sin((-var3.x + 90.0F) * 0.017453292F);
      Vec3 var11 = new Vec3((double)(var5 * var7), (double)var8, (double)(var6 * var7));
      Vec3 var12 = new Vec3((double)(var5 * var9), (double)var10, (double)(var6 * var9));
      Vec3 var13 = var11.cross(var12).scale(-1.0);
      double var14 = var11.x * this.forwards + var12.x * this.up + var13.x * this.left;
      double var16 = var11.y * this.forwards + var12.y * this.up + var13.y * this.left;
      double var18 = var11.z * this.forwards + var12.z * this.up + var13.z * this.left;
      double var20 = var2 ? 0.0 : var4.x;
      double var22 = var2 ? 0.0 : var4.y;
      double var24 = var2 ? 0.0 : var4.z;
      return new Vec3(var20 + var14, var22 + var16, var24 + var18);
   }

   @Override
   public Vec2 getRotation(CommandSourceStack var1, boolean var2) {
      return !var2 ? Vec2.ZERO : new Vec2(this.isXRelative() ? -var1.getRotation().x : 0.0F, this.isYRelative() ? -var1.getRotation().y : 0.0F);
   }

   @Override
   public boolean isXRelative() {
      return true;
   }

   @Override
   public boolean isYRelative() {
      return true;
   }

   @Override
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
         return var0.canRead() && var0.peek() != ' ' ? var0.readDouble() : 0.0;
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return !(var1 instanceof LocalCoordinates var2) ? false : this.left == var2.left && this.up == var2.up && this.forwards == var2.forwards;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.left, this.up, this.forwards);
   }
}
