package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public class ItemParticleOption implements ParticleOptions {
   public static final ParticleOptions.Deserializer DESERIALIZER = new ParticleOptions.Deserializer() {
      public ItemParticleOption fromCommand(ParticleType var1, StringReader var2) throws CommandSyntaxException {
         var2.expect(' ');
         ItemParser var3 = (new ItemParser(var2, false)).parse();
         ItemStack var4 = (new ItemInput(var3.getItem(), var3.getNbt())).createItemStack(1, false);
         return new ItemParticleOption(var1, var4);
      }

      public ItemParticleOption fromNetwork(ParticleType var1, FriendlyByteBuf var2) {
         return new ItemParticleOption(var1, var2.readItem());
      }

      // $FF: synthetic method
      public ParticleOptions fromNetwork(ParticleType var1, FriendlyByteBuf var2) {
         return this.fromNetwork(var1, var2);
      }

      // $FF: synthetic method
      public ParticleOptions fromCommand(ParticleType var1, StringReader var2) throws CommandSyntaxException {
         return this.fromCommand(var1, var2);
      }
   };
   private final ParticleType type;
   private final ItemStack itemStack;

   public ItemParticleOption(ParticleType var1, ItemStack var2) {
      this.type = var1;
      this.itemStack = var2;
   }

   public void writeToNetwork(FriendlyByteBuf var1) {
      var1.writeItem(this.itemStack);
   }

   public String writeToString() {
      return Registry.PARTICLE_TYPE.getKey(this.getType()) + " " + (new ItemInput(this.itemStack.getItem(), this.itemStack.getTag())).serialize();
   }

   public ParticleType getType() {
      return this.type;
   }

   public ItemStack getItem() {
      return this.itemStack;
   }
}
