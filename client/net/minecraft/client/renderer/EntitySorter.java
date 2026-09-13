package net.minecraft.client.renderer;

import java.util.Comparator;
import net.minecraft.entity.Entity;

public class EntitySorter implements Comparator {
   private double field_78949_a;
   private double field_78947_b;
   private double field_78948_c;

   public EntitySorter(Entity var1) {
      super();
      this.field_78949_a = -var1.field_70165_t;
      this.field_78947_b = -var1.field_70163_u;
      this.field_78948_c = -var1.field_70161_v;
   }

   public int compare(WorldRenderer var1, WorldRenderer var2) {
      double var3 = (double)var1.field_78925_n + this.field_78949_a;
      double var5 = (double)var1.field_78926_o + this.field_78947_b;
      double var7 = (double)var1.field_78940_p + this.field_78948_c;
      double var9 = (double)var2.field_78925_n + this.field_78949_a;
      double var11 = (double)var2.field_78926_o + this.field_78947_b;
      double var13 = (double)var2.field_78940_p + this.field_78948_c;
      return (int)((var3 * var3 + var5 * var5 + var7 * var7 - (var9 * var9 + var11 * var11 + var13 * var13)) * 1024.0);
   }
}
