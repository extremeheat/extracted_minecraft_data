package net.minecraft.stats;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.function.UnaryOperator;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.inventory.RecipeBookType;

public final class RecipeBookSettings {
   public static final StreamCodec<FriendlyByteBuf, RecipeBookSettings> STREAM_CODEC;
   public static final MapCodec<RecipeBookSettings> MAP_CODEC;
   private TypeSettings crafting;
   private TypeSettings furnace;
   private TypeSettings blastFurnace;
   private TypeSettings smoker;

   public RecipeBookSettings() {
      this(RecipeBookSettings.TypeSettings.DEFAULT, RecipeBookSettings.TypeSettings.DEFAULT, RecipeBookSettings.TypeSettings.DEFAULT, RecipeBookSettings.TypeSettings.DEFAULT);
   }

   private RecipeBookSettings(TypeSettings var1, TypeSettings var2, TypeSettings var3, TypeSettings var4) {
      super();
      this.crafting = var1;
      this.furnace = var2;
      this.blastFurnace = var3;
      this.smoker = var4;
   }

   @VisibleForTesting
   public TypeSettings getSettings(RecipeBookType var1) {
      TypeSettings var10000;
      switch (var1) {
         case CRAFTING -> var10000 = this.crafting;
         case FURNACE -> var10000 = this.furnace;
         case BLAST_FURNACE -> var10000 = this.blastFurnace;
         case SMOKER -> var10000 = this.smoker;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   private void updateSettings(RecipeBookType var1, UnaryOperator<TypeSettings> var2) {
      switch (var1) {
         case CRAFTING -> this.crafting = (TypeSettings)var2.apply(this.crafting);
         case FURNACE -> this.furnace = (TypeSettings)var2.apply(this.furnace);
         case BLAST_FURNACE -> this.blastFurnace = (TypeSettings)var2.apply(this.blastFurnace);
         case SMOKER -> this.smoker = (TypeSettings)var2.apply(this.smoker);
      }

   }

   public boolean isOpen(RecipeBookType var1) {
      return this.getSettings(var1).open;
   }

   public void setOpen(RecipeBookType var1, boolean var2) {
      this.updateSettings(var1, (var1x) -> var1x.setOpen(var2));
   }

   public boolean isFiltering(RecipeBookType var1) {
      return this.getSettings(var1).filtering;
   }

   public void setFiltering(RecipeBookType var1, boolean var2) {
      this.updateSettings(var1, (var1x) -> var1x.setFiltering(var2));
   }

   public RecipeBookSettings copy() {
      return new RecipeBookSettings(this.crafting, this.furnace, this.blastFurnace, this.smoker);
   }

   public void replaceFrom(RecipeBookSettings var1) {
      this.crafting = var1.crafting;
      this.furnace = var1.furnace;
      this.blastFurnace = var1.blastFurnace;
      this.smoker = var1.smoker;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(RecipeBookSettings.TypeSettings.STREAM_CODEC, (var0) -> var0.crafting, RecipeBookSettings.TypeSettings.STREAM_CODEC, (var0) -> var0.furnace, RecipeBookSettings.TypeSettings.STREAM_CODEC, (var0) -> var0.blastFurnace, RecipeBookSettings.TypeSettings.STREAM_CODEC, (var0) -> var0.smoker, RecipeBookSettings::new);
      MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(RecipeBookSettings.TypeSettings.CRAFTING_MAP_CODEC.forGetter((var0x) -> var0x.crafting), RecipeBookSettings.TypeSettings.FURNACE_MAP_CODEC.forGetter((var0x) -> var0x.furnace), RecipeBookSettings.TypeSettings.BLAST_FURNACE_MAP_CODEC.forGetter((var0x) -> var0x.blastFurnace), RecipeBookSettings.TypeSettings.SMOKER_MAP_CODEC.forGetter((var0x) -> var0x.smoker)).apply(var0, RecipeBookSettings::new));
   }

   public static record TypeSettings(boolean open, boolean filtering) {
      final boolean open;
      final boolean filtering;
      public static final TypeSettings DEFAULT = new TypeSettings(false, false);
      public static final MapCodec<TypeSettings> CRAFTING_MAP_CODEC = codec("isGuiOpen", "isFilteringCraftable");
      public static final MapCodec<TypeSettings> FURNACE_MAP_CODEC = codec("isFurnaceGuiOpen", "isFurnaceFilteringCraftable");
      public static final MapCodec<TypeSettings> BLAST_FURNACE_MAP_CODEC = codec("isBlastingFurnaceGuiOpen", "isBlastingFurnaceFilteringCraftable");
      public static final MapCodec<TypeSettings> SMOKER_MAP_CODEC = codec("isSmokerGuiOpen", "isSmokerFilteringCraftable");
      public static final StreamCodec<ByteBuf, TypeSettings> STREAM_CODEC;

      public TypeSettings(boolean var1, boolean var2) {
         super();
         this.open = var1;
         this.filtering = var2;
      }

      public String toString() {
         return "[open=" + this.open + ", filtering=" + this.filtering + "]";
      }

      public TypeSettings setOpen(boolean var1) {
         return new TypeSettings(var1, this.filtering);
      }

      public TypeSettings setFiltering(boolean var1) {
         return new TypeSettings(this.open, var1);
      }

      private static MapCodec<TypeSettings> codec(String var0, String var1) {
         return RecordCodecBuilder.mapCodec((var2) -> var2.group(Codec.BOOL.optionalFieldOf(var0, false).forGetter(TypeSettings::open), Codec.BOOL.optionalFieldOf(var1, false).forGetter(TypeSettings::filtering)).apply(var2, TypeSettings::new));
      }

      static {
         STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.BOOL, TypeSettings::open, ByteBufCodecs.BOOL, TypeSettings::filtering, TypeSettings::new);
      }
   }
}
