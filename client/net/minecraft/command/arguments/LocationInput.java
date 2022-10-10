package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class LocationInput implements ILocationArgument {
   private final LocationPart field_197284_b;
   private final LocationPart field_197285_c;
   private final LocationPart field_197286_d;

   public LocationInput(LocationPart var1, LocationPart var2, LocationPart var3) {
      super();
      this.field_197284_b = var1;
      this.field_197285_c = var2;
      this.field_197286_d = var3;
   }

   public Vec3d func_197281_a(CommandSource var1) {
      Vec3d var2 = var1.func_197036_d();
      return new Vec3d(this.field_197284_b.func_197306_a(var2.field_72450_a), this.field_197285_c.func_197306_a(var2.field_72448_b), this.field_197286_d.func_197306_a(var2.field_72449_c));
   }

   public Vec2f func_197282_b(CommandSource var1) {
      Vec2f var2 = var1.func_201004_i();
      return new Vec2f((float)this.field_197284_b.func_197306_a((double)var2.field_189982_i), (float)this.field_197285_c.func_197306_a((double)var2.field_189983_j));
   }

   public boolean func_200380_a() {
      return this.field_197284_b.func_200386_a();
   }

   public boolean func_200381_b() {
      return this.field_197285_c.func_200386_a();
   }

   public boolean func_200382_c() {
      return this.field_197286_d.func_200386_a();
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof LocationInput)) {
         return false;
      } else {
         LocationInput var2 = (LocationInput)var1;
         if (!this.field_197284_b.equals(var2.field_197284_b)) {
            return false;
         } else {
            return !this.field_197285_c.equals(var2.field_197285_c) ? false : this.field_197286_d.equals(var2.field_197286_d);
         }
      }
   }

   public static LocationInput func_200148_a(StringReader var0) throws CommandSyntaxException {
      int var1 = var0.getCursor();
      LocationPart var2 = LocationPart.func_197307_a(var0);
      if (var0.canRead() && var0.peek() == ' ') {
         var0.skip();
         LocationPart var3 = LocationPart.func_197307_a(var0);
         if (var0.canRead() && var0.peek() == ' ') {
            var0.skip();
            LocationPart var4 = LocationPart.func_197307_a(var0);
            return new LocationInput(var2, var3, var4);
         } else {
            var0.setCursor(var1);
            throw Vec3Argument.field_197304_a.createWithContext(var0);
         }
      } else {
         var0.setCursor(var1);
         throw Vec3Argument.field_197304_a.createWithContext(var0);
      }
   }

   public static LocationInput func_200147_a(StringReader var0, boolean var1) throws CommandSyntaxException {
      int var2 = var0.getCursor();
      LocationPart var3 = LocationPart.func_197308_a(var0, var1);
      if (var0.canRead() && var0.peek() == ' ') {
         var0.skip();
         LocationPart var4 = LocationPart.func_197308_a(var0, false);
         if (var0.canRead() && var0.peek() == ' ') {
            var0.skip();
            LocationPart var5 = LocationPart.func_197308_a(var0, var1);
            return new LocationInput(var3, var4, var5);
         } else {
            var0.setCursor(var2);
            throw Vec3Argument.field_197304_a.createWithContext(var0);
         }
      } else {
         var0.setCursor(var2);
         throw Vec3Argument.field_197304_a.createWithContext(var0);
      }
   }

   public static LocationInput func_200383_d() {
      return new LocationInput(new LocationPart(true, 0.0D), new LocationPart(true, 0.0D), new LocationPart(true, 0.0D));
   }

   public int hashCode() {
      int var1 = this.field_197284_b.hashCode();
      var1 = 31 * var1 + this.field_197285_c.hashCode();
      var1 = 31 * var1 + this.field_197286_d.hashCode();
      return var1;
   }
}
