package net.minecraft.client.audio;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class SoundEventAccessor implements ISoundEventAccessor<Sound> {
   private final List<ISoundEventAccessor<Sound>> field_188716_a = Lists.newArrayList();
   private final Random field_148734_b = new Random();
   private final ResourceLocation field_188717_c;
   private final ITextComponent field_188718_d;

   public SoundEventAccessor(ResourceLocation var1, @Nullable String var2) {
      super();
      this.field_188717_c = var1;
      this.field_188718_d = var2 == null ? null : new TextComponentTranslation(var2, new Object[0]);
   }

   public int func_148721_a() {
      int var1 = 0;

      ISoundEventAccessor var3;
      for(Iterator var2 = this.field_188716_a.iterator(); var2.hasNext(); var1 += var3.func_148721_a()) {
         var3 = (ISoundEventAccessor)var2.next();
      }

      return var1;
   }

   public Sound func_148720_g() {
      int var1 = this.func_148721_a();
      if (!this.field_188716_a.isEmpty() && var1 != 0) {
         int var2 = this.field_148734_b.nextInt(var1);
         Iterator var3 = this.field_188716_a.iterator();

         ISoundEventAccessor var4;
         do {
            if (!var3.hasNext()) {
               return SoundHandler.field_147700_a;
            }

            var4 = (ISoundEventAccessor)var3.next();
            var2 -= var4.func_148721_a();
         } while(var2 >= 0);

         return (Sound)var4.func_148720_g();
      } else {
         return SoundHandler.field_147700_a;
      }
   }

   public void func_188715_a(ISoundEventAccessor<Sound> var1) {
      this.field_188716_a.add(var1);
   }

   @Nullable
   public ITextComponent func_188712_c() {
      return this.field_188718_d;
   }

   // $FF: synthetic method
   public Object func_148720_g() {
      return this.func_148720_g();
   }
}
