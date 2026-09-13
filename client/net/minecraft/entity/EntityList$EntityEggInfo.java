package net.minecraft.entity;

import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;

public class EntityList$EntityEggInfo {
   public final int field_75613_a;
   public final int field_75611_b;
   public final int field_75612_c;
   public final StatBase field_151512_d;
   public final StatBase field_151513_e;

   public EntityList$EntityEggInfo(int var1, int var2, int var3) {
      super();
      this.field_75613_a = var1;
      this.field_75611_b = var2;
      this.field_75612_c = var3;
      this.field_151512_d = StatList.func_151182_a(this);
      this.field_151513_e = StatList.func_151176_b(this);
   }
}
