package net.minecraft.world.scores;

import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.network.chat.numbers.NumberFormatTypes;

public class Score implements ReadOnlyScoreInfo {
   private static final String TAG_SCORE = "Score";
   private static final String TAG_LOCKED = "Locked";
   private static final String TAG_DISPLAY = "display";
   private static final String TAG_FORMAT = "format";
   private int value;
   private boolean locked = true;
   @Nullable
   private Component display;
   @Nullable
   private NumberFormat numberFormat;

   public Score() {
      super();
   }

   public int value() {
      return this.value;
   }

   public void value(int var1) {
      this.value = var1;
   }

   public boolean isLocked() {
      return this.locked;
   }

   public void setLocked(boolean var1) {
      this.locked = var1;
   }

   @Nullable
   public Component display() {
      return this.display;
   }

   public void display(@Nullable Component var1) {
      this.display = var1;
   }

   @Nullable
   public NumberFormat numberFormat() {
      return this.numberFormat;
   }

   public void numberFormat(@Nullable NumberFormat var1) {
      this.numberFormat = var1;
   }

   public CompoundTag write(HolderLookup.Provider var1) {
      CompoundTag var2 = new CompoundTag();
      var2.putInt("Score", this.value);
      var2.putBoolean("Locked", this.locked);
      if (this.display != null) {
         var2.putString("display", Component.Serializer.toJson(this.display, var1));
      }

      if (this.numberFormat != null) {
         NumberFormatTypes.CODEC.encodeStart(var1.createSerializationContext(NbtOps.INSTANCE), this.numberFormat).ifSuccess((var1x) -> {
            var2.put("format", var1x);
         });
      }

      return var2;
   }

   public static Score read(CompoundTag var0, HolderLookup.Provider var1) {
      Score var2 = new Score();
      var2.value = var0.getInt("Score");
      var2.locked = var0.getBoolean("Locked");
      if (var0.contains("display", 8)) {
         var2.display = Component.Serializer.fromJson(var0.getString("display"), var1);
      }

      if (var0.contains("format", 10)) {
         NumberFormatTypes.CODEC.parse(var1.createSerializationContext(NbtOps.INSTANCE), var0.get("format")).ifSuccess((var1x) -> {
            var2.numberFormat = var1x;
         });
      }

      return var2;
   }
}
