package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Objects;
import net.minecraft.command.CommandSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class LocalLocationArgument implements ILocationArgument {
   private final double field_200144_a;
   private final double field_200145_b;
   private final double field_200146_c;

   public LocalLocationArgument(double var1, double var3, double var5) {
      super();
      this.field_200144_a = var1;
      this.field_200145_b = var3;
      this.field_200146_c = var5;
   }

   public Vec3d func_197281_a(CommandSource var1) {
      Vec2f var2 = var1.func_201004_i();
      Vec3d var3 = var1.func_201008_k().func_201015_a(var1);
      float var4 = MathHelper.func_76134_b((var2.field_189983_j + 90.0F) * 0.017453292F);
      float var5 = MathHelper.func_76126_a((var2.field_189983_j + 90.0F) * 0.017453292F);
      float var6 = MathHelper.func_76134_b(-var2.field_189982_i * 0.017453292F);
      float var7 = MathHelper.func_76126_a(-var2.field_189982_i * 0.017453292F);
      float var8 = MathHelper.func_76134_b((-var2.field_189982_i + 90.0F) * 0.017453292F);
      float var9 = MathHelper.func_76126_a((-var2.field_189982_i + 90.0F) * 0.017453292F);
      Vec3d var10 = new Vec3d((double)(var4 * var6), (double)var7, (double)(var5 * var6));
      Vec3d var11 = new Vec3d((double)(var4 * var8), (double)var9, (double)(var5 * var8));
      Vec3d var12 = var10.func_72431_c(var11).func_186678_a(-1.0D);
      double var13 = var10.field_72450_a * this.field_200146_c + var11.field_72450_a * this.field_200145_b + var12.field_72450_a * this.field_200144_a;
      double var15 = var10.field_72448_b * this.field_200146_c + var11.field_72448_b * this.field_200145_b + var12.field_72448_b * this.field_200144_a;
      double var17 = var10.field_72449_c * this.field_200146_c + var11.field_72449_c * this.field_200145_b + var12.field_72449_c * this.field_200144_a;
      return new Vec3d(var3.field_72450_a + var13, var3.field_72448_b + var15, var3.field_72449_c + var17);
   }

   public Vec2f func_197282_b(CommandSource var1) {
      return Vec2f.field_189974_a;
   }

   public boolean func_200380_a() {
      return true;
   }

   public boolean func_200381_b() {
      return true;
   }

   public boolean func_200382_c() {
      return true;
   }

   public static LocalLocationArgument func_200142_a(StringReader var0) throws CommandSyntaxException {
      int var1 = var0.getCursor();
      double var2 = func_200143_a(var0, var1);
      if (var0.canRead() && var0.peek() == ' ') {
         var0.skip();
         double var4 = func_200143_a(var0, var1);
         if (var0.canRead() && var0.peek() == ' ') {
            var0.skip();
            double var6 = func_200143_a(var0, var1);
            return new LocalLocationArgument(var2, var4, var6);
         } else {
            var0.setCursor(var1);
            throw Vec3Argument.field_197304_a.createWithContext(var0);
         }
      } else {
         var0.setCursor(var1);
         throw Vec3Argument.field_197304_a.createWithContext(var0);
      }
   }

   private static double func_200143_a(StringReader var0, int var1) throws CommandSyntaxException {
      if (!var0.canRead()) {
         throw LocationPart.field_197311_b.createWithContext(var0);
      } else if (var0.peek() != '^') {
         var0.setCursor(var1);
         throw Vec3Argument.field_200149_b.createWithContext(var0);
      } else {
         var0.skip();
         return var0.canRead() && var0.peek() != ' ' ? var0.readDouble() : 0.0D;
      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof LocalLocationArgument)) {
         return false;
      } else {
         LocalLocationArgument var2 = (LocalLocationArgument)var1;
         return this.field_200144_a == var2.field_200144_a && this.field_200145_b == var2.field_200145_b && this.field_200146_c == var2.field_200146_c;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.field_200144_a, this.field_200145_b, this.field_200146_c});
   }
}
