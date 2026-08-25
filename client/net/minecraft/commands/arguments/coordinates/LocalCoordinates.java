package net.minecraft.commands.arguments.coordinates;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public record LocalCoordinates(double left, double up, double forwards) implements Coordinates {
   public static final char PREFIX_LOCAL_COORDINATE = '^';

   public LocalCoordinates {
      super();
   }

   public Vec3 getPosition(final CommandSourceStack sender) {
      Vec3 source = sender.getAnchor().apply(sender);
      return this.apply(source, sender.getRotation());
   }

   public Vec3 apply(final Vec3 source, final Vec2 rotation) {
      float yCos = Mth.cos((double)((rotation.y + 90.0F) * 0.017453292F));
      float ySin = Mth.sin((double)((rotation.y + 90.0F) * 0.017453292F));
      float xCos = Mth.cos((double)(-rotation.x * 0.017453292F));
      float xSin = Mth.sin((double)(-rotation.x * 0.017453292F));
      float xCosUp = Mth.cos((double)((-rotation.x + 90.0F) * 0.017453292F));
      float xSinUp = Mth.sin((double)((-rotation.x + 90.0F) * 0.017453292F));
      Vec3 forwards = new Vec3((double)(yCos * xCos), (double)xSin, (double)(ySin * xCos));
      Vec3 up = new Vec3((double)(yCos * xCosUp), (double)xSinUp, (double)(ySin * xCosUp));
      Vec3 left = forwards.cross(up).scale(-1.0);
      return source.add(forwards.x * this.forwards + up.x * this.up + left.x * this.left, forwards.y * this.forwards + up.y * this.up + left.y * this.left, forwards.z * this.forwards + up.z * this.up + left.z * this.left);
   }

   public Vec2 getRotation(final CommandSourceStack sender) {
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

   public static LocalCoordinates parse(final StringReader reader) throws CommandSyntaxException {
      int start = reader.getCursor();
      double left = readDouble(reader, start);
      if (reader.canRead() && reader.peek() == ' ') {
         reader.skip();
         double up = readDouble(reader, start);
         if (reader.canRead() && reader.peek() == ' ') {
            reader.skip();
            double forwards = readDouble(reader, start);
            return new LocalCoordinates(left, up, forwards);
         } else {
            reader.setCursor(start);
            throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(reader);
         }
      } else {
         reader.setCursor(start);
         throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(reader);
      }
   }

   private static double readDouble(final StringReader reader, final int start) throws CommandSyntaxException {
      if (!reader.canRead()) {
         throw WorldCoordinate.ERROR_EXPECTED_DOUBLE.createWithContext(reader);
      } else if (reader.peek() != '^') {
         reader.setCursor(start);
         throw Vec3Argument.ERROR_MIXED_TYPE.createWithContext(reader);
      } else {
         reader.skip();
         return reader.canRead() && reader.peek() != ' ' ? reader.readDouble() : 0.0;
      }
   }
}
