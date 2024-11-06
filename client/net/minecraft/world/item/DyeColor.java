package net.minecraft.world.item;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ARGB;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.Contract;

public enum DyeColor implements StringRepresentable {
   WHITE(0, "white", 16383998, MapColor.SNOW, 15790320, 16777215),
   ORANGE(1, "orange", 16351261, MapColor.COLOR_ORANGE, 15435844, 16738335),
   MAGENTA(2, "magenta", 13061821, MapColor.COLOR_MAGENTA, 12801229, 16711935),
   LIGHT_BLUE(3, "light_blue", 3847130, MapColor.COLOR_LIGHT_BLUE, 6719955, 10141901),
   YELLOW(4, "yellow", 16701501, MapColor.COLOR_YELLOW, 14602026, 16776960),
   LIME(5, "lime", 8439583, MapColor.COLOR_LIGHT_GREEN, 4312372, 12582656),
   PINK(6, "pink", 15961002, MapColor.COLOR_PINK, 14188952, 16738740),
   GRAY(7, "gray", 4673362, MapColor.COLOR_GRAY, 4408131, 8421504),
   LIGHT_GRAY(8, "light_gray", 10329495, MapColor.COLOR_LIGHT_GRAY, 11250603, 13882323),
   CYAN(9, "cyan", 1481884, MapColor.COLOR_CYAN, 2651799, 65535),
   PURPLE(10, "purple", 8991416, MapColor.COLOR_PURPLE, 8073150, 10494192),
   BLUE(11, "blue", 3949738, MapColor.COLOR_BLUE, 2437522, 255),
   BROWN(12, "brown", 8606770, MapColor.COLOR_BROWN, 5320730, 9127187),
   GREEN(13, "green", 6192150, MapColor.COLOR_GREEN, 3887386, 65280),
   RED(14, "red", 11546150, MapColor.COLOR_RED, 11743532, 16711680),
   BLACK(15, "black", 1908001, MapColor.COLOR_BLACK, 1973019, 0);

   private static final IntFunction<DyeColor> BY_ID = ByIdMap.continuous(DyeColor::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
   private static final Int2ObjectOpenHashMap<DyeColor> BY_FIREWORK_COLOR = new Int2ObjectOpenHashMap((Map)Arrays.stream(values()).collect(Collectors.toMap((var0) -> {
      return var0.fireworkColor;
   }, (var0) -> {
      return var0;
   })));
   public static final StringRepresentable.EnumCodec<DyeColor> CODEC = StringRepresentable.fromEnum(DyeColor::values);
   public static final StreamCodec<ByteBuf, DyeColor> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, DyeColor::getId);
   private final int id;
   private final String name;
   private final MapColor mapColor;
   private final int textureDiffuseColor;
   private final int fireworkColor;
   private final int textColor;

   private DyeColor(final int var3, final String var4, final int var5, final MapColor var6, final int var7, final int var8) {
      this.id = var3;
      this.name = var4;
      this.mapColor = var6;
      this.textColor = var8;
      this.textureDiffuseColor = ARGB.opaque(var5);
      this.fireworkColor = var7;
   }

   public int getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public int getTextureDiffuseColor() {
      return this.textureDiffuseColor;
   }

   public MapColor getMapColor() {
      return this.mapColor;
   }

   public int getFireworkColor() {
      return this.fireworkColor;
   }

   public int getTextColor() {
      return this.textColor;
   }

   public static DyeColor byId(int var0) {
      return (DyeColor)BY_ID.apply(var0);
   }

   @Nullable
   @Contract("_,!null->!null;_,null->_")
   public static DyeColor byName(String var0, @Nullable DyeColor var1) {
      DyeColor var2 = (DyeColor)CODEC.byName(var0);
      return var2 != null ? var2 : var1;
   }

   @Nullable
   public static DyeColor byFireworkColor(int var0) {
      return (DyeColor)BY_FIREWORK_COLOR.get(var0);
   }

   public String toString() {
      return this.name;
   }

   public String getSerializedName() {
      return this.name;
   }

   public static DyeColor getMixedColor(ServerLevel var0, DyeColor var1, DyeColor var2) {
      CraftingInput var3 = makeCraftColorInput(var1, var2);
      Optional var10000 = var0.recipeAccess().getRecipeFor(RecipeType.CRAFTING, var3, var0).map((var2x) -> {
         return ((CraftingRecipe)var2x.value()).assemble(var3, var0.registryAccess());
      }).map(ItemStack::getItem);
      Objects.requireNonNull(DyeItem.class);
      var10000 = var10000.filter(DyeItem.class::isInstance);
      Objects.requireNonNull(DyeItem.class);
      return (DyeColor)var10000.map(DyeItem.class::cast).map(DyeItem::getDyeColor).orElseGet(() -> {
         return var0.random.nextBoolean() ? var1 : var2;
      });
   }

   private static CraftingInput makeCraftColorInput(DyeColor var0, DyeColor var1) {
      return CraftingInput.of(2, 1, List.of(new ItemStack(DyeItem.byColor(var0)), new ItemStack(DyeItem.byColor(var1))));
   }

   // $FF: synthetic method
   private static DyeColor[] $values() {
      return new DyeColor[]{WHITE, ORANGE, MAGENTA, LIGHT_BLUE, YELLOW, LIME, PINK, GRAY, LIGHT_GRAY, CYAN, PURPLE, BLUE, BROWN, GREEN, RED, BLACK};
   }
}
