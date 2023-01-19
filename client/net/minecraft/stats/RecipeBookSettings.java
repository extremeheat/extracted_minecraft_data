package net.minecraft.stats;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.RecipeBookType;

public final class RecipeBookSettings {
   private static final Map<RecipeBookType, Pair<String, String>> TAG_FIELDS = ImmutableMap.of(
      RecipeBookType.CRAFTING,
      Pair.of("isGuiOpen", "isFilteringCraftable"),
      RecipeBookType.FURNACE,
      Pair.of("isFurnaceGuiOpen", "isFurnaceFilteringCraftable"),
      RecipeBookType.BLAST_FURNACE,
      Pair.of("isBlastingFurnaceGuiOpen", "isBlastingFurnaceFilteringCraftable"),
      RecipeBookType.SMOKER,
      Pair.of("isSmokerGuiOpen", "isSmokerFilteringCraftable")
   );
   private final Map<RecipeBookType, RecipeBookSettings.TypeSettings> states;

   private RecipeBookSettings(Map<RecipeBookType, RecipeBookSettings.TypeSettings> var1) {
      super();
      this.states = var1;
   }

   public RecipeBookSettings() {
      this(Util.make(Maps.newEnumMap(RecipeBookType.class), var0 -> {
         for(RecipeBookType var4 : RecipeBookType.values()) {
            var0.put(var4, new RecipeBookSettings.TypeSettings(false, false));
         }
      }));
   }

   public boolean isOpen(RecipeBookType var1) {
      return this.states.get(var1).open;
   }

   public void setOpen(RecipeBookType var1, boolean var2) {
      this.states.get(var1).open = var2;
   }

   public boolean isFiltering(RecipeBookType var1) {
      return this.states.get(var1).filtering;
   }

   public void setFiltering(RecipeBookType var1, boolean var2) {
      this.states.get(var1).filtering = var2;
   }

   public static RecipeBookSettings read(FriendlyByteBuf var0) {
      EnumMap var1 = Maps.newEnumMap(RecipeBookType.class);

      for(RecipeBookType var5 : RecipeBookType.values()) {
         boolean var6 = var0.readBoolean();
         boolean var7 = var0.readBoolean();
         var1.put(var5, new RecipeBookSettings.TypeSettings(var6, var7));
      }

      return new RecipeBookSettings(var1);
   }

   public void write(FriendlyByteBuf var1) {
      for(RecipeBookType var5 : RecipeBookType.values()) {
         RecipeBookSettings.TypeSettings var6 = this.states.get(var5);
         if (var6 == null) {
            var1.writeBoolean(false);
            var1.writeBoolean(false);
         } else {
            var1.writeBoolean(var6.open);
            var1.writeBoolean(var6.filtering);
         }
      }
   }

   public static RecipeBookSettings read(CompoundTag var0) {
      EnumMap var1 = Maps.newEnumMap(RecipeBookType.class);
      TAG_FIELDS.forEach((var2, var3) -> {
         boolean var4 = var0.getBoolean((String)var3.getFirst());
         boolean var5 = var0.getBoolean((String)var3.getSecond());
         var1.put(var2, new RecipeBookSettings.TypeSettings(var4, var5));
      });
      return new RecipeBookSettings(var1);
   }

   public void write(CompoundTag var1) {
      TAG_FIELDS.forEach((var2, var3) -> {
         RecipeBookSettings.TypeSettings var4 = this.states.get(var2);
         var1.putBoolean((String)var3.getFirst(), var4.open);
         var1.putBoolean((String)var3.getSecond(), var4.filtering);
      });
   }

   public RecipeBookSettings copy() {
      EnumMap var1 = Maps.newEnumMap(RecipeBookType.class);

      for(RecipeBookType var5 : RecipeBookType.values()) {
         RecipeBookSettings.TypeSettings var6 = this.states.get(var5);
         var1.put(var5, var6.copy());
      }

      return new RecipeBookSettings(var1);
   }

   public void replaceFrom(RecipeBookSettings var1) {
      this.states.clear();

      for(RecipeBookType var5 : RecipeBookType.values()) {
         RecipeBookSettings.TypeSettings var6 = var1.states.get(var5);
         this.states.put(var5, var6.copy());
      }
   }

   @Override
   public boolean equals(Object var1) {
      return this == var1 || var1 instanceof RecipeBookSettings && this.states.equals(((RecipeBookSettings)var1).states);
   }

   @Override
   public int hashCode() {
      return this.states.hashCode();
   }

   static final class TypeSettings {
      boolean open;
      boolean filtering;

      public TypeSettings(boolean var1, boolean var2) {
         super();
         this.open = var1;
         this.filtering = var2;
      }

      public RecipeBookSettings.TypeSettings copy() {
         return new RecipeBookSettings.TypeSettings(this.open, this.filtering);
      }

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (!(var1 instanceof RecipeBookSettings.TypeSettings)) {
            return false;
         } else {
            RecipeBookSettings.TypeSettings var2 = (RecipeBookSettings.TypeSettings)var1;
            return this.open == var2.open && this.filtering == var2.filtering;
         }
      }

      @Override
      public int hashCode() {
         int var1 = this.open ? 1 : 0;
         return 31 * var1 + (this.filtering ? 1 : 0);
      }

      @Override
      public String toString() {
         return "[open=" + this.open + ", filtering=" + this.filtering + "]";
      }
   }
}
