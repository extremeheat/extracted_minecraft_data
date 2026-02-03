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

   private RecipeBookSettings(final TypeSettings crafting, final TypeSettings furnace, final TypeSettings blastFurnace, final TypeSettings smoker) {
      super();
      this.crafting = crafting;
      this.furnace = furnace;
      this.blastFurnace = blastFurnace;
      this.smoker = smoker;
   }

   @VisibleForTesting
   public TypeSettings getSettings(final RecipeBookType type) {
      TypeSettings var10000;
      switch (type) {
         case CRAFTING -> var10000 = this.crafting;
         case FURNACE -> var10000 = this.furnace;
         case BLAST_FURNACE -> var10000 = this.blastFurnace;
         case SMOKER -> var10000 = this.smoker;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   private void updateSettings(final RecipeBookType recipeBookType, final UnaryOperator<TypeSettings> operator) {
      switch (recipeBookType) {
         case CRAFTING -> this.crafting = (TypeSettings)operator.apply(this.crafting);
         case FURNACE -> this.furnace = (TypeSettings)operator.apply(this.furnace);
         case BLAST_FURNACE -> this.blastFurnace = (TypeSettings)operator.apply(this.blastFurnace);
         case SMOKER -> this.smoker = (TypeSettings)operator.apply(this.smoker);
      }

   }

   public boolean isOpen(final RecipeBookType type) {
      return this.getSettings(type).open;
   }

   public void setOpen(final RecipeBookType type, final boolean open) {
      this.updateSettings(type, (s) -> s.setOpen(open));
   }

   public boolean isFiltering(final RecipeBookType type) {
      return this.getSettings(type).filtering;
   }

   public void setFiltering(final RecipeBookType type, final boolean filtering) {
      this.updateSettings(type, (s) -> s.setFiltering(filtering));
   }

   public RecipeBookSettings copy() {
      return new RecipeBookSettings(this.crafting, this.furnace, this.blastFurnace, this.smoker);
   }

   public void replaceFrom(final RecipeBookSettings other) {
      this.crafting = other.crafting;
      this.furnace = other.furnace;
      this.blastFurnace = other.blastFurnace;
      this.smoker = other.smoker;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(RecipeBookSettings.TypeSettings.STREAM_CODEC, (o) -> o.crafting, RecipeBookSettings.TypeSettings.STREAM_CODEC, (o) -> o.furnace, RecipeBookSettings.TypeSettings.STREAM_CODEC, (o) -> o.blastFurnace, RecipeBookSettings.TypeSettings.STREAM_CODEC, (o) -> o.smoker, RecipeBookSettings::new);
      MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(RecipeBookSettings.TypeSettings.CRAFTING_MAP_CODEC.forGetter((o) -> o.crafting), RecipeBookSettings.TypeSettings.FURNACE_MAP_CODEC.forGetter((o) -> o.furnace), RecipeBookSettings.TypeSettings.BLAST_FURNACE_MAP_CODEC.forGetter((o) -> o.blastFurnace), RecipeBookSettings.TypeSettings.SMOKER_MAP_CODEC.forGetter((o) -> o.smoker)).apply(i, RecipeBookSettings::new));
   }

   public static record TypeSettings(boolean open, boolean filtering) {
      public static final TypeSettings DEFAULT = new TypeSettings(false, false);
      public static final MapCodec<TypeSettings> CRAFTING_MAP_CODEC = codec("isGuiOpen", "isFilteringCraftable");
      public static final MapCodec<TypeSettings> FURNACE_MAP_CODEC = codec("isFurnaceGuiOpen", "isFurnaceFilteringCraftable");
      public static final MapCodec<TypeSettings> BLAST_FURNACE_MAP_CODEC = codec("isBlastingFurnaceGuiOpen", "isBlastingFurnaceFilteringCraftable");
      public static final MapCodec<TypeSettings> SMOKER_MAP_CODEC = codec("isSmokerGuiOpen", "isSmokerFilteringCraftable");
      public static final StreamCodec<ByteBuf, TypeSettings> STREAM_CODEC;

      public TypeSettings {
         super();
      }

      public String toString() {
         return "[open=" + this.open + ", filtering=" + this.filtering + "]";
      }

      public TypeSettings setOpen(final boolean open) {
         return new TypeSettings(open, this.filtering);
      }

      public TypeSettings setFiltering(final boolean filtering) {
         return new TypeSettings(this.open, filtering);
      }

      private static MapCodec<TypeSettings> codec(final String openFieldName, final String filteringFieldName) {
         return RecordCodecBuilder.mapCodec((i) -> i.group(Codec.BOOL.optionalFieldOf(openFieldName, false).forGetter(TypeSettings::open), Codec.BOOL.optionalFieldOf(filteringFieldName, false).forGetter(TypeSettings::filtering)).apply(i, TypeSettings::new));
      }

      static {
         STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.BOOL, TypeSettings::open, ByteBufCodecs.BOOL, TypeSettings::filtering, TypeSettings::new);
      }
   }
}
