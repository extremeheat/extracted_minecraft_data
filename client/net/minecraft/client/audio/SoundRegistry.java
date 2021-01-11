package net.minecraft.client.audio;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.util.RegistrySimple;
import net.minecraft.util.ResourceLocation;

public class SoundRegistry extends RegistrySimple<ResourceLocation, SoundEventAccessorComposite> {
   private Map<ResourceLocation, SoundEventAccessorComposite> field_148764_a;

   public SoundRegistry() {
      super();
   }

   protected Map<ResourceLocation, SoundEventAccessorComposite> func_148740_a() {
      this.field_148764_a = Maps.newHashMap();
      return this.field_148764_a;
   }

   public void func_148762_a(SoundEventAccessorComposite var1) {
      this.func_82595_a(var1.func_148729_c(), var1);
   }

   public void func_148763_c() {
      this.field_148764_a.clear();
   }
}
