package net.minecraft.util;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Set;

public enum EnumDirection8 {
   NORTH(new EnumFacing[]{EnumFacing.NORTH}),
   NORTH_EAST(new EnumFacing[]{EnumFacing.NORTH, EnumFacing.EAST}),
   EAST(new EnumFacing[]{EnumFacing.EAST}),
   SOUTH_EAST(new EnumFacing[]{EnumFacing.SOUTH, EnumFacing.EAST}),
   SOUTH(new EnumFacing[]{EnumFacing.SOUTH}),
   SOUTH_WEST(new EnumFacing[]{EnumFacing.SOUTH, EnumFacing.WEST}),
   WEST(new EnumFacing[]{EnumFacing.WEST}),
   NORTH_WEST(new EnumFacing[]{EnumFacing.NORTH, EnumFacing.WEST});

   private static final int field_208500_i = 1 << NORTH_WEST.ordinal();
   private static final int field_208501_j = 1 << WEST.ordinal();
   private static final int field_208502_k = 1 << SOUTH_WEST.ordinal();
   private static final int field_208503_l = 1 << SOUTH.ordinal();
   private static final int field_208504_m = 1 << SOUTH_EAST.ordinal();
   private static final int field_208505_n = 1 << EAST.ordinal();
   private static final int field_208506_o = 1 << NORTH_EAST.ordinal();
   private static final int field_208507_p = 1 << NORTH.ordinal();
   private final Set<EnumFacing> field_197541_i;

   private EnumDirection8(EnumFacing... var3) {
      this.field_197541_i = Sets.immutableEnumSet(Arrays.asList(var3));
   }

   public Set<EnumFacing> func_197532_a() {
      return this.field_197541_i;
   }
}
