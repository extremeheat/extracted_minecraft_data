package net.minecraft.client.renderer;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.Util;

public enum EnumFaceDirection {
   DOWN(new EnumFaceDirection.VertexInformation[]{new EnumFaceDirection.VertexInformation(EnumFaceDirection.Constants.field_179176_f, EnumFaceDirection.Constants.field_179178_e, EnumFaceDirection.Constants.field_179181_a), new EnumFaceDirection.VertexInformation(EnumFaceDirection.Constants.field_179176_f, EnumFaceDirection.Constants.field_179178_e, EnumFaceDirection.Constants.field_179177_d), new EnumFaceDirection.VertexInformation(EnumFaceDirection.Constants.field_179180_c, EnumFaceDirection.Constants.field_179178_e, EnumFaceDirection.Constants.field_179177_d), new EnumFaceDirection.VertexInformation(EnumFaceDirection.Constants.field_179180_c, EnumFaceDirection.Constants.field_179178_e, EnumFaceDirection.Constants.field_179181_a)}),
   UP(new EnumFaceDirection.VertexInformation[]{new EnumFaceDirection.VertexInformation(EnumFaceDirection.Constants.field_179176_f, EnumFaceDirection.Constants.field_179179_b, EnumFaceDirection.Constants.field_179177_d), new EnumFaceDirection.VertexInformation(EnumFaceDirection.Constants.field_179176_f, EnumFaceDirection.Constants.field_179179_b, EnumFaceDirection.Constants.field_179181_a), new EnumFaceDirection.VertexInformation(EnumFaceDirection.Constants.field_179180_c, EnumFaceDirection.Constants.field_179179_b, EnumFaceDirection.Constants.field_179181_a), new EnumFaceDirection.VertexInformation(EnumFaceDirection.Constants.field_179180_c, EnumFaceDirection.Constants.field_179179_b, EnumFaceDirection.Constants.field_179177_d)}),
   NORTH(new EnumFaceDirection.VertexInformation[]{new EnumFaceDirection.VertexInformation(EnumFaceDirection.Constants.field_179180_c, EnumFaceDirection.Constants.field_179179_b, EnumFaceDirection.Constants.field_179177_d), new EnumFaceDirection.VertexInformation(EnumFaceDirection.Constants.field_179180_c, EnumFaceDirection.Constants.field_179178_e, EnumFaceDirection.Constants.field_179177_d), new EnumFaceDirection.VertexInformation(EnumFaceDirection.Constants.field_179176_f, EnumFaceDirection.Constants.field_179178_e, EnumFaceDirection.Constants.field_179177_d), new EnumFaceDirection.VertexInformation(EnumFaceDirection.Constants.field_179176_f, EnumFaceDirection.Constants.field_179179_b, EnumFaceDirection.Constants.field_179177_d)}),
   SOUTH(new EnumFaceDirection.VertexInformation[]{new EnumFaceDirection.VertexInformation(EnumFaceDirection.Constants.field_179176_f, EnumFaceDirection.Constants.field_179179_b, EnumFaceDirection.Constants.field_179181_a), new EnumFaceDirection.VertexInformation(EnumFaceDirection.Constants.field_179176_f, EnumFaceDirection.Constants.field_179178_e, EnumFaceDirection.Constants.field_179181_a), new EnumFaceDirection.VertexInformation(EnumFaceDirection.Constants.field_179180_c, EnumFaceDirection.Constants.field_179178_e, EnumFaceDirection.Constants.field_179181_a), new EnumFaceDirection.VertexInformation(EnumFaceDirection.Constants.field_179180_c, EnumFaceDirection.Constants.field_179179_b, EnumFaceDirection.Constants.field_179181_a)}),
   WEST(new EnumFaceDirection.VertexInformation[]{new EnumFaceDirection.VertexInformation(EnumFaceDirection.Constants.field_179176_f, EnumFaceDirection.Constants.field_179179_b, EnumFaceDirection.Constants.field_179177_d), new EnumFaceDirection.VertexInformation(EnumFaceDirection.Constants.field_179176_f, EnumFaceDirection.Constants.field_179178_e, EnumFaceDirection.Constants.field_179177_d), new EnumFaceDirection.VertexInformation(EnumFaceDirection.Constants.field_179176_f, EnumFaceDirection.Constants.field_179178_e, EnumFaceDirection.Constants.field_179181_a), new EnumFaceDirection.VertexInformation(EnumFaceDirection.Constants.field_179176_f, EnumFaceDirection.Constants.field_179179_b, EnumFaceDirection.Constants.field_179181_a)}),
   EAST(new EnumFaceDirection.VertexInformation[]{new EnumFaceDirection.VertexInformation(EnumFaceDirection.Constants.field_179180_c, EnumFaceDirection.Constants.field_179179_b, EnumFaceDirection.Constants.field_179181_a), new EnumFaceDirection.VertexInformation(EnumFaceDirection.Constants.field_179180_c, EnumFaceDirection.Constants.field_179178_e, EnumFaceDirection.Constants.field_179181_a), new EnumFaceDirection.VertexInformation(EnumFaceDirection.Constants.field_179180_c, EnumFaceDirection.Constants.field_179178_e, EnumFaceDirection.Constants.field_179177_d), new EnumFaceDirection.VertexInformation(EnumFaceDirection.Constants.field_179180_c, EnumFaceDirection.Constants.field_179179_b, EnumFaceDirection.Constants.field_179177_d)});

   private static final EnumFaceDirection[] field_179029_g = (EnumFaceDirection[])Util.func_200696_a(new EnumFaceDirection[6], (var0) -> {
      var0[EnumFaceDirection.Constants.field_179178_e] = DOWN;
      var0[EnumFaceDirection.Constants.field_179179_b] = UP;
      var0[EnumFaceDirection.Constants.field_179177_d] = NORTH;
      var0[EnumFaceDirection.Constants.field_179181_a] = SOUTH;
      var0[EnumFaceDirection.Constants.field_179176_f] = WEST;
      var0[EnumFaceDirection.Constants.field_179180_c] = EAST;
   });
   private final EnumFaceDirection.VertexInformation[] field_179035_h;

   public static EnumFaceDirection func_179027_a(EnumFacing var0) {
      return field_179029_g[var0.func_176745_a()];
   }

   private EnumFaceDirection(EnumFaceDirection.VertexInformation... var3) {
      this.field_179035_h = var3;
   }

   public EnumFaceDirection.VertexInformation func_179025_a(int var1) {
      return this.field_179035_h[var1];
   }

   public static class VertexInformation {
      public final int field_179184_a;
      public final int field_179182_b;
      public final int field_179183_c;

      private VertexInformation(int var1, int var2, int var3) {
         super();
         this.field_179184_a = var1;
         this.field_179182_b = var2;
         this.field_179183_c = var3;
      }

      // $FF: synthetic method
      VertexInformation(int var1, int var2, int var3, Object var4) {
         this(var1, var2, var3);
      }
   }

   public static final class Constants {
      public static final int field_179181_a;
      public static final int field_179179_b;
      public static final int field_179180_c;
      public static final int field_179177_d;
      public static final int field_179178_e;
      public static final int field_179176_f;

      static {
         field_179181_a = EnumFacing.SOUTH.func_176745_a();
         field_179179_b = EnumFacing.UP.func_176745_a();
         field_179180_c = EnumFacing.EAST.func_176745_a();
         field_179177_d = EnumFacing.NORTH.func_176745_a();
         field_179178_e = EnumFacing.DOWN.func_176745_a();
         field_179176_f = EnumFacing.WEST.func_176745_a();
      }
   }
}
