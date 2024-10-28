package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.MapCodec;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public class ItemParticleOption implements ParticleOptions {
   public static final ParticleOptions.Deserializer<ItemParticleOption> DESERIALIZER = new ParticleOptions.Deserializer<ItemParticleOption>() {
      public ItemParticleOption fromCommand(ParticleType<ItemParticleOption> var1, StringReader var2, HolderLookup.Provider var3) throws CommandSyntaxException {
         var2.expect(' ');
         ItemParser.ItemResult var4 = (new ItemParser(var3)).parse(var2);
         ItemStack var5 = (new ItemInput(var4.item(), var4.components())).createItemStack(1, false);
         return new ItemParticleOption(var1, var5);
      }

      // $FF: synthetic method
      public ParticleOptions fromCommand(ParticleType var1, StringReader var2, HolderLookup.Provider var3) throws CommandSyntaxException {
         return this.fromCommand(var1, var2, var3);
      }
   };
   private final ParticleType<ItemParticleOption> type;
   private final ItemStack itemStack;

   public static MapCodec<ItemParticleOption> codec(ParticleType<ItemParticleOption> var0) {
      return ItemStack.CODEC.xmap((var1) -> {
         return new ItemParticleOption(var0, var1);
      }, (var0x) -> {
         return var0x.itemStack;
      }).fieldOf("value");
   }

   public static StreamCodec<? super RegistryFriendlyByteBuf, ItemParticleOption> streamCodec(ParticleType<ItemParticleOption> var0) {
      return ItemStack.STREAM_CODEC.map((var1) -> {
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

   public String writeToString(HolderLookup.Provider var1) {
      ItemInput var2 = new ItemInput(this.itemStack.getItemHolder(), this.itemStack.getComponents());
      String var10000 = String.valueOf(BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()));
      return var10000 + " " + var2.serialize(var1);
   }

   public ParticleType<ItemParticleOption> getType() {
      return this.type;
   }

   public ItemStack getItem() {
      return this.itemStack;
   }
}
