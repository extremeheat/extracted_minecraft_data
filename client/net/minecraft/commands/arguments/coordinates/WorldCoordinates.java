package net.minecraft.commands.arguments.coordinates;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public record WorldCoordinates(WorldCoordinate x, WorldCoordinate y, WorldCoordinate z) implements Coordinates {
   public static final WorldCoordinates ZERO_ROTATION = absolute(new Vec2(0.0F, 0.0F));
   public static final SimpleCommandExceptionType ERROR_NOT_COMPLETE_2D = new SimpleCommandExceptionType(Component.translatable("argument.pos2d.incomplete"));
   public static final SimpleCommandExceptionType ERROR_NOT_COMPLETE_3D = new SimpleCommandExceptionType(Component.translatable("argument.pos3d.incomplete"));
   public static final SimpleCommandExceptionType ERROR_MIXED_TYPE = new SimpleCommandExceptionType(Component.translatable("argument.pos.mixed"));
   public static final WorldCoordinate UNUSED = new WorldCoordinate(false, 0.0);

   public WorldCoordinates {
      super();
   }

   public Vec3 getPosition(final CommandSourceStack sender) {
      Vec3 pos = sender.getPosition();
      return new Vec3(this.x.get(pos.x), this.y.get(pos.y), this.z.get(pos.z));
   }

   public Vec2 getRotation(final CommandSourceStack sender) {
      Vec2 rot = sender.getRotation();
      return new Vec2((float)this.x.get((double)rot.x), (float)this.y.get((double)rot.y));
   }

   public boolean isXRelative() {
      return this.x.isRelative();
   }

   public boolean isYRelative() {
      return this.y.isRelative();
   }

   public boolean isZRelative() {
      return this.z.isRelative();
   }

   public static WorldCoordinates parseXYZ(final StringReader reader, final WorldCoordinate.Parser parser) throws CommandSyntaxException {
      int start = reader.getCursor();
      WorldCoordinate x = parser.parse(reader);
      if (reader.canRead() && reader.peek() == ' ') {
         reader.skip();
         WorldCoordinate y = parser.parse(reader);
         if (reader.canRead() && reader.peek() == ' ') {
            reader.skip();
            WorldCoordinate z = parser.parse(reader);
            return new WorldCoordinates(x, y, z);
         } else {
            reader.setCursor(start);
            throw ERROR_NOT_COMPLETE_3D.createWithContext(reader);
         }
      } else {
         reader.setCursor(start);
         throw ERROR_NOT_COMPLETE_3D.createWithContext(reader);
      }
   }

   public static WorldCoordinates parseXZ(final StringReader reader, final WorldCoordinate.Parser parser) throws CommandSyntaxException {
      int start = reader.getCursor();
      if (!reader.canRead()) {
         throw ERROR_NOT_COMPLETE_2D.createWithContext(reader);
      } else {
         WorldCoordinate x = parser.parse(reader);
         if (reader.canRead() && reader.peek() == ' ') {
            reader.skip();
            WorldCoordinate z = parser.parse(reader);
            return new WorldCoordinates(x, UNUSED, z);
         } else {
            reader.setCursor(start);
            throw ERROR_NOT_COMPLETE_2D.createWithContext(reader);
         }
      }
   }

   public static WorldCoordinates absolute(final double x, final double y, final double z) {
      return new WorldCoordinates(new WorldCoordinate(false, x), new WorldCoordinate(false, y), new WorldCoordinate(false, z));
   }

   public static WorldCoordinates absolute(final Vec2 rotation) {
      return new WorldCoordinates(new WorldCoordinate(false, (double)rotation.x), new WorldCoordinate(false, (double)rotation.y), new WorldCoordinate(true, 0.0));
   }
}
