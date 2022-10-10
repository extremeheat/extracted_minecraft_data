package net.minecraft.entity.ai;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.passive.AbstractGroupFish;

public class EntityAIFollowGroupLeader extends EntityAIBase {
   private final AbstractGroupFish field_203785_a;
   private int field_203787_c;
   private int field_212826_c;

   public EntityAIFollowGroupLeader(AbstractGroupFish var1) {
      super();
      this.field_203785_a = var1;
      this.field_212826_c = this.func_212825_a(var1);
   }

   protected int func_212825_a(AbstractGroupFish var1) {
      return 200 + var1.func_70681_au().nextInt(200) % 20;
   }

   public boolean func_75250_a() {
      if (this.field_203785_a.func_212812_dE()) {
         return false;
      } else if (this.field_203785_a.func_212802_dB()) {
         return true;
      } else if (this.field_212826_c > 0) {
         --this.field_212826_c;
         return false;
      } else {
         this.field_212826_c = this.func_212825_a(this.field_203785_a);
         Predicate var1 = (var0) -> {
            return var0.func_212811_dD() || !var0.func_212802_dB();
         };
         List var2 = this.field_203785_a.field_70170_p.func_175647_a(this.field_203785_a.getClass(), this.field_203785_a.func_174813_aQ().func_72314_b(8.0D, 8.0D, 8.0D), var1);
         AbstractGroupFish var3 = (AbstractGroupFish)var2.stream().filter(AbstractGroupFish::func_212811_dD).findAny().orElse(this.field_203785_a);
         var3.func_212810_a(var2.stream().filter((var0) -> {
            return !var0.func_212802_dB();
         }));
         return this.field_203785_a.func_212802_dB();
      }
   }

   public boolean func_75253_b() {
      return this.field_203785_a.func_212802_dB() && this.field_203785_a.func_212809_dF();
   }

   public void func_75249_e() {
      this.field_203787_c = 0;
   }

   public void func_75251_c() {
      this.field_203785_a.func_212808_dC();
   }

   public void func_75246_d() {
      if (--this.field_203787_c <= 0) {
         this.field_203787_c = 10;
         this.field_203785_a.func_212805_dG();
      }
   }
}
