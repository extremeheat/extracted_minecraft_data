package net.minecraft.world.level.chunk;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.IdMapper;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.CrudeIncrementalIntIdentityHashBiMap;

public class HashMapPalette implements Palette {
   private final IdMapper registry;
   private final CrudeIncrementalIntIdentityHashBiMap values;
   private final PaletteResize resizeHandler;
   private final Function reader;
   private final Function writer;
   private final int bits;

   public HashMapPalette(IdMapper var1, int var2, PaletteResize var3, Function var4, Function var5) {
      this.registry = var1;
      this.bits = var2;
      this.resizeHandler = var3;
      this.reader = var4;
      this.writer = var5;
      this.values = new CrudeIncrementalIntIdentityHashBiMap(1 << var2);
   }

   public int idFor(Object var1) {
      int var2 = this.values.getId(var1);
      if (var2 == -1) {
         var2 = this.values.add(var1);
         if (var2 >= 1 << this.bits) {
            var2 = this.resizeHandler.onResize(this.bits + 1, var1);
         }
      }

      return var2;
   }

   public boolean maybeHas(Object var1) {
      return this.values.getId(var1) != -1;
   }

   @Nullable
   public Object valueFor(int var1) {
      return this.values.byId(var1);
   }

   public void read(FriendlyByteBuf var1) {
      this.values.clear();
      int var2 = var1.readVarInt();

      for(int var3 = 0; var3 < var2; ++var3) {
         this.values.add(this.registry.byId(var1.readVarInt()));
      }

   }

   public void write(FriendlyByteBuf var1) {
      int var2 = this.getSize();
      var1.writeVarInt(var2);

      for(int var3 = 0; var3 < var2; ++var3) {
         var1.writeVarInt(this.registry.getId(this.values.byId(var3)));
      }

   }

   public int getSerializedSize() {
      int var1 = FriendlyByteBuf.getVarIntSize(this.getSize());

      for(int var2 = 0; var2 < this.getSize(); ++var2) {
         var1 += FriendlyByteBuf.getVarIntSize(this.registry.getId(this.values.byId(var2)));
      }

      return var1;
   }

   public int getSize() {
      return this.values.size();
   }

   public void read(ListTag var1) {
      this.values.clear();

      for(int var2 = 0; var2 < var1.size(); ++var2) {
         this.values.add(this.reader.apply(var1.getCompound(var2)));
      }

   }

   public void write(ListTag var1) {
      for(int var2 = 0; var2 < this.getSize(); ++var2) {
         var1.add(this.writer.apply(this.values.byId(var2)));
      }

   }
}
