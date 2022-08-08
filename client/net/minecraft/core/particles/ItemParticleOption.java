package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class ItemParticleOption implements ParticleOptions {
   public static final ParticleOptions.Deserializer<ItemParticleOption> DESERIALIZER = new ParticleOptions.Deserializer<ItemParticleOption>() {
      public ItemParticleOption fromCommand(ParticleType<ItemParticleOption> var1, StringReader var2) throws CommandSyntaxException {
         var2.expect(' ');
         ItemParser.ItemResult var3 = ItemParser.parseForItem(HolderLookup.forRegistry(Registry.ITEM), var2);
         ItemStack var4 = (new ItemInput(var3.item(), var3.nbt())).createItemStack(1, false);
         return new ItemParticleOption(var1, var4);
      }

      public ItemParticleOption fromNetwork(ParticleType<ItemParticleOption> var1, FriendlyByteBuf var2) {
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
   private final ParticleType<ItemParticleOption> type;
   private final ItemStack itemStack;

   public static Codec<ItemParticleOption> codec(ParticleType<ItemParticleOption> var0) {
      return ItemStack.CODEC.xmap((var1) -> {
         return new ItemParticleOption(var0, var1);
      }, (var0x) -> {
         return var0x.itemStack;
      });
   }

   public ItemParticleOption(ParticleType<ItemParticleOption> var1, ItemStack var2) {
      super();
      this.type = var1;
      this.itemStack = var2;
   }

   public void writeToNetwork(FriendlyByteBuf var1) {
      var1.writeItem(this.itemStack);
   }

   public String writeToString() {
      ResourceLocation var10000 = Registry.PARTICLE_TYPE.getKey(this.getType());
      return "" + var10000 + " " + (new ItemInput(this.itemStack.getItemHolder(), this.itemStack.getTag())).serialize();
   }

   public ParticleType<ItemParticleOption> getType() {
      return this.type;
   }

   public ItemStack getItem() {
      return this.itemStack;
   }
}
