package net.minecraft.stats;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.UnaryOperator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.inventory.RecipeBookType;

public final class RecipeBookSettings {
   public static final StreamCodec<FriendlyByteBuf, RecipeBookSettings> STREAM_CODEC = StreamCodec.<FriendlyByteBuf, RecipeBookSettings>ofMember(RecipeBookSettings::write, RecipeBookSettings::read);
   private static final Map<RecipeBookType, Pair<String, String>> TAG_FIELDS;
   private final Map<RecipeBookType, TypeSettings> states;

   private RecipeBookSettings(Map<RecipeBookType, TypeSettings> var1) {
      super();
      this.states = var1;
   }

   public RecipeBookSettings() {
      this(new EnumMap(RecipeBookType.class));
   }

   private TypeSettings getSettings(RecipeBookType var1) {
      return (TypeSettings)this.states.getOrDefault(var1, RecipeBookSettings.TypeSettings.DEFAULT);
   }

   private void updateSettings(RecipeBookType var1, UnaryOperator<TypeSettings> var2) {
      this.states.compute(var1, (var1x, var2x) -> {
         if (var2x == null) {
            var2x = RecipeBookSettings.TypeSettings.DEFAULT;
         }

         var2x = (TypeSettings)var2.apply(var2x);
         if (var2x.equals(RecipeBookSettings.TypeSettings.DEFAULT)) {
            var2x = null;
         }

         return var2x;
      });
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

   private static RecipeBookSettings read(FriendlyByteBuf var0) {
      EnumMap var1 = new EnumMap(RecipeBookType.class);

      for(RecipeBookType var5 : RecipeBookType.values()) {
         boolean var6 = var0.readBoolean();
         boolean var7 = var0.readBoolean();
         if (var6 || var7) {
            var1.put(var5, new TypeSettings(var6, var7));
         }
      }

      return new RecipeBookSettings(var1);
   }

   private void write(FriendlyByteBuf var1) {
      for(RecipeBookType var5 : RecipeBookType.values()) {
         TypeSettings var6 = (TypeSettings)this.states.getOrDefault(var5, RecipeBookSettings.TypeSettings.DEFAULT);
         var1.writeBoolean(var6.open);
         var1.writeBoolean(var6.filtering);
      }

   }

   public static RecipeBookSettings read(CompoundTag var0) {
      EnumMap var1 = new EnumMap(RecipeBookType.class);
      TAG_FIELDS.forEach((var2, var3) -> {
         boolean var4 = var0.getBoolean((String)var3.getFirst());
         boolean var5 = var0.getBoolean((String)var3.getSecond());
         if (var4 || var5) {
            var1.put(var2, new TypeSettings(var4, var5));
         }

      });
      return new RecipeBookSettings(var1);
   }

   public void write(CompoundTag var1) {
      TAG_FIELDS.forEach((var2, var3) -> {
         TypeSettings var4 = (TypeSettings)this.states.getOrDefault(var2, RecipeBookSettings.TypeSettings.DEFAULT);
         var1.putBoolean((String)var3.getFirst(), var4.open);
         var1.putBoolean((String)var3.getSecond(), var4.filtering);
      });
   }

   public RecipeBookSettings copy() {
      return new RecipeBookSettings(new EnumMap(this.states));
   }

   public void replaceFrom(RecipeBookSettings var1) {
      this.states.clear();
      this.states.putAll(var1.states);
   }

   public boolean equals(Object var1) {
      return this == var1 || var1 instanceof RecipeBookSettings && this.states.equals(((RecipeBookSettings)var1).states);
   }

   public int hashCode() {
      return this.states.hashCode();
   }

   static {
      TAG_FIELDS = ImmutableMap.of(RecipeBookType.CRAFTING, Pair.of("isGuiOpen", "isFilteringCraftable"), RecipeBookType.FURNACE, Pair.of("isFurnaceGuiOpen", "isFurnaceFilteringCraftable"), RecipeBookType.BLAST_FURNACE, Pair.of("isBlastingFurnaceGuiOpen", "isBlastingFurnaceFilteringCraftable"), RecipeBookType.SMOKER, Pair.of("isSmokerGuiOpen", "isSmokerFilteringCraftable"));
   }

   static record TypeSettings(boolean open, boolean filtering) {
      final boolean open;
      final boolean filtering;
      public static final TypeSettings DEFAULT = new TypeSettings(false, false);

      TypeSettings(boolean var1, boolean var2) {
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
   }
}
