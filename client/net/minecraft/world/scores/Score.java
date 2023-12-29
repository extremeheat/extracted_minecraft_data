package net.minecraft.world.scores;

import javax.annotation.Nullable;
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

   @Override
   public int value() {
      return this.value;
   }

   public void value(int var1) {
      this.value = var1;
   }

   @Override
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
   @Override
   public NumberFormat numberFormat() {
      return this.numberFormat;
   }

   public void numberFormat(@Nullable NumberFormat var1) {
      this.numberFormat = var1;
   }

   public CompoundTag write() {
      CompoundTag var1 = new CompoundTag();
      var1.putInt("Score", this.value);
      var1.putBoolean("Locked", this.locked);
      if (this.display != null) {
         var1.putString("display", Component.Serializer.toJson(this.display));
      }

      if (this.numberFormat != null) {
         NumberFormatTypes.CODEC.encodeStart(NbtOps.INSTANCE, this.numberFormat).result().ifPresent(var1x -> var1.put("format", var1x));
      }

      return var1;
   }

   public static Score read(CompoundTag var0) {
      Score var1 = new Score();
      var1.value = var0.getInt("Score");
      var1.locked = var0.getBoolean("Locked");
      if (var0.contains("display", 8)) {
         var1.display = Component.Serializer.fromJson(var0.getString("display"));
      }

      if (var0.contains("format", 10)) {
         NumberFormatTypes.CODEC.parse(NbtOps.INSTANCE, var0.get("format")).result().ifPresent(var1x -> var1.numberFormat = var1x);
      }

      return var1;
   }
}
