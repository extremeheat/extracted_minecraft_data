package net.minecraft.client.audio;

import com.google.common.collect.Lists;
import java.util.List;

public class SoundList {
   private final List field_148577_a = Lists.newArrayList();
   private boolean field_148575_b;
   private SoundCategory field_148576_c;

   public SoundList() {
      super();
   }

   public List func_148570_a() {
      return this.field_148577_a;
   }

   public boolean func_148574_b() {
      return this.field_148575_b;
   }

   public void func_148572_a(boolean var1) {
      this.field_148575_b = var1;
   }

   public SoundCategory func_148573_c() {
      return this.field_148576_c;
   }

   public void func_148571_a(SoundCategory var1) {
      this.field_148576_c = var1;
   }
}
