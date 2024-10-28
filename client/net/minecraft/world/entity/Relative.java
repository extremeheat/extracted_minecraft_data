package net.minecraft.world.entity;

import io.netty.buffer.ByteBuf;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public enum Relative {
   X(0),
   Y(1),
   Z(2),
   Y_ROT(3),
   X_ROT(4),
   DELTA_X(5),
   DELTA_Y(6),
   DELTA_Z(7),
   ROTATE_DELTA(8);

   public static final Set<Relative> ALL = Set.of(values());
   public static final Set<Relative> ROTATION = Set.of(X_ROT, Y_ROT);
   public static final Set<Relative> DELTA = Set.of(DELTA_X, DELTA_Y, DELTA_Z, ROTATE_DELTA);
   public static final StreamCodec<ByteBuf, Set<Relative>> SET_STREAM_CODEC = ByteBufCodecs.INT.map(Relative::unpack, Relative::pack);
   private final int bit;

   @SafeVarargs
   public static Set<Relative> union(Set<Relative>... var0) {
      HashSet var1 = new HashSet();
      Set[] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Set var5 = var2[var4];
         var1.addAll(var5);
      }

      return var1;
   }

   private Relative(final int var3) {
      this.bit = var3;
   }

   private int getMask() {
      return 1 << this.bit;
   }

   private boolean isSet(int var1) {
      return (var1 & this.getMask()) == this.getMask();
   }

   public static Set<Relative> unpack(int var0) {
      EnumSet var1 = EnumSet.noneOf(Relative.class);
      Relative[] var2 = values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Relative var5 = var2[var4];
         if (var5.isSet(var0)) {
            var1.add(var5);
         }
      }

      return var1;
   }

   public static int pack(Set<Relative> var0) {
      int var1 = 0;

      Relative var3;
      for(Iterator var2 = var0.iterator(); var2.hasNext(); var1 |= var3.getMask()) {
         var3 = (Relative)var2.next();
      }

      return var1;
   }

   // $FF: synthetic method
   private static Relative[] $values() {
      return new Relative[]{X, Y, Z, Y_ROT, X_ROT, DELTA_X, DELTA_Y, DELTA_Z, ROTATE_DELTA};
   }
}
