package net.minecraft.client.audio;

import net.minecraft.util.ResourceLocation;

class SoundHandler$2 implements ISoundEventAccessor {
   final ResourceLocation field_148726_a;

   SoundHandler$2(SoundHandler var1, String var2, SoundList$SoundEntry var3) {
      super();
      this.field_148723_d = var1;
      this.field_148724_b = var2;
      this.field_148725_c = var3;
      this.field_148726_a = new ResourceLocation(this.field_148724_b, this.field_148725_c.func_148556_a());
   }

   @Override
   public int func_148721_a() {
      SoundEventAccessorComposite var1 = (SoundEventAccessorComposite)SoundHandler.access$000(this.field_148723_d).func_82594_a(this.field_148726_a);
      return var1 == null ? 0 : var1.func_148721_a();
   }

   public SoundPoolEntry func_148720_g() {
      SoundEventAccessorComposite var1 = (SoundEventAccessorComposite)SoundHandler.access$000(this.field_148723_d).func_82594_a(this.field_148726_a);
      return var1 == null ? SoundHandler.field_147700_a : var1.func_148720_g();
   }
}
