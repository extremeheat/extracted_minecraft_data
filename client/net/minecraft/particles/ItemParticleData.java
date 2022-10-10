package net.minecraft.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.arguments.ItemInput;
import net.minecraft.command.arguments.ItemParser;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

public class ItemParticleData implements IParticleData {
   public static final IParticleData.IDeserializer<ItemParticleData> field_197557_a = new IParticleData.IDeserializer<ItemParticleData>() {
      public ItemParticleData func_197544_b(ParticleType<ItemParticleData> var1, StringReader var2) throws CommandSyntaxException {
         var2.expect(' ');
         ItemParser var3 = (new ItemParser(var2, false)).func_197327_f();
         ItemStack var4 = (new ItemInput(var3.func_197326_b(), var3.func_197325_c())).func_197320_a(1, false);
         return new ItemParticleData(var1, var4);
      }

      public ItemParticleData func_197543_b(ParticleType<ItemParticleData> var1, PacketBuffer var2) {
         return new ItemParticleData(var1, var2.func_150791_c());
      }

      // $FF: synthetic method
      public IParticleData func_197543_b(ParticleType var1, PacketBuffer var2) {
         return this.func_197543_b(var1, var2);
      }

      // $FF: synthetic method
      public IParticleData func_197544_b(ParticleType var1, StringReader var2) throws CommandSyntaxException {
         return this.func_197544_b(var1, var2);
      }
   };
   private final ParticleType<ItemParticleData> field_197558_b;
   private final ItemStack field_197559_c;

   public ItemParticleData(ParticleType<ItemParticleData> var1, ItemStack var2) {
      super();
      this.field_197558_b = var1;
      this.field_197559_c = var2;
   }

   public void func_197553_a(PacketBuffer var1) {
      var1.func_150788_a(this.field_197559_c);
   }

   public String func_197555_a() {
      return this.func_197554_b().func_197570_d() + " " + (new ItemInput(this.field_197559_c.func_77973_b(), this.field_197559_c.func_77978_p())).func_197321_c();
   }

   public ParticleType<ItemParticleData> func_197554_b() {
      return this.field_197558_b;
   }

   public ItemStack func_197556_c() {
      return this.field_197559_c;
   }
}
