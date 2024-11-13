package net.minecraft.client.data.models.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import net.minecraft.client.color.item.Constant;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.client.renderer.item.CompositeModel;
import net.minecraft.client.renderer.item.ConditionalItemModel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.RangeSelectItemModel;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.client.renderer.item.SpecialModelWrapper;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.client.renderer.item.properties.conditional.HasComponent;
import net.minecraft.client.renderer.item.properties.conditional.IsUsingItem;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.client.renderer.item.properties.select.ItemBlockState;
import net.minecraft.client.renderer.item.properties.select.LocalTime;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.Property;

public class ItemModelUtils {
   public ItemModelUtils() {
      super();
   }

   public static ItemModel.Unbaked plainModel(ResourceLocation var0) {
      return new BlockModelWrapper.Unbaked(var0, List.of());
   }

   public static ItemModel.Unbaked tintedModel(ResourceLocation var0, ItemTintSource... var1) {
      return new BlockModelWrapper.Unbaked(var0, List.of(var1));
   }

   public static ItemTintSource constantTint(int var0) {
      return new Constant(var0);
   }

   public static ItemModel.Unbaked composite(ItemModel.Unbaked... var0) {
      return new CompositeModel.Unbaked(List.of(var0));
   }

   public static ItemModel.Unbaked specialModel(ResourceLocation var0, SpecialModelRenderer.Unbaked var1) {
      return new SpecialModelWrapper.Unbaked(var0, var1);
   }

   public static RangeSelectItemModel.Entry override(ItemModel.Unbaked var0, float var1) {
      return new RangeSelectItemModel.Entry(var1, var0);
   }

   public static ItemModel.Unbaked rangeSelect(RangeSelectItemModelProperty var0, ItemModel.Unbaked var1, RangeSelectItemModel.Entry... var2) {
      return new RangeSelectItemModel.Unbaked(var0, 1.0F, List.of(var2), Optional.of(var1));
   }

   public static ItemModel.Unbaked rangeSelect(RangeSelectItemModelProperty var0, float var1, ItemModel.Unbaked var2, RangeSelectItemModel.Entry... var3) {
      return new RangeSelectItemModel.Unbaked(var0, var1, List.of(var3), Optional.of(var2));
   }

   public static ItemModel.Unbaked rangeSelect(RangeSelectItemModelProperty var0, ItemModel.Unbaked var1, List<RangeSelectItemModel.Entry> var2) {
      return new RangeSelectItemModel.Unbaked(var0, 1.0F, var2, Optional.of(var1));
   }

   public static ItemModel.Unbaked rangeSelect(RangeSelectItemModelProperty var0, List<RangeSelectItemModel.Entry> var1) {
      return new RangeSelectItemModel.Unbaked(var0, 1.0F, var1, Optional.empty());
   }

   public static ItemModel.Unbaked rangeSelect(RangeSelectItemModelProperty var0, float var1, List<RangeSelectItemModel.Entry> var2) {
      return new RangeSelectItemModel.Unbaked(var0, var1, var2, Optional.empty());
   }

   public static ItemModel.Unbaked conditional(ConditionalItemModelProperty var0, ItemModel.Unbaked var1, ItemModel.Unbaked var2) {
      return new ConditionalItemModel.Unbaked(var0, var1, var2);
   }

   public static <T> SelectItemModel.SwitchCase<T> when(T var0, ItemModel.Unbaked var1) {
      return new SelectItemModel.SwitchCase<T>(List.of(var0), var1);
   }

   public static <T> SelectItemModel.SwitchCase<T> when(List<T> var0, ItemModel.Unbaked var1) {
      return new SelectItemModel.SwitchCase<T>(var0, var1);
   }

   @SafeVarargs
   public static <T> ItemModel.Unbaked select(SelectItemModelProperty<T> var0, ItemModel.Unbaked var1, SelectItemModel.SwitchCase<T>... var2) {
      return select(var0, var1, List.of(var2));
   }

   public static <T> ItemModel.Unbaked select(SelectItemModelProperty<T> var0, ItemModel.Unbaked var1, List<SelectItemModel.SwitchCase<T>> var2) {
      return new SelectItemModel.Unbaked(new SelectItemModel.UnbakedSwitch(var0, var2), Optional.of(var1));
   }

   @SafeVarargs
   public static <T> ItemModel.Unbaked select(SelectItemModelProperty<T> var0, SelectItemModel.SwitchCase<T>... var1) {
      return select(var0, List.of(var1));
   }

   public static <T> ItemModel.Unbaked select(SelectItemModelProperty<T> var0, List<SelectItemModel.SwitchCase<T>> var1) {
      return new SelectItemModel.Unbaked(new SelectItemModel.UnbakedSwitch(var0, var1), Optional.empty());
   }

   public static ConditionalItemModelProperty isUsingItem() {
      return new IsUsingItem();
   }

   public static ConditionalItemModelProperty hasComponent(DataComponentType<?> var0) {
      return new HasComponent(var0, false);
   }

   public static <T extends Comparable<T>> ItemModel.Unbaked selectBlockItemProperty(Property<T> var0, ItemModel.Unbaked var1, Map<T, ItemModel.Unbaked> var2) {
      List var3 = var2.entrySet().stream().sorted(Entry.comparingByKey()).map((var1x) -> {
         String var2 = var0.getName((Comparable)var1x.getKey());
         return new SelectItemModel.SwitchCase(List.of(var2), (ItemModel.Unbaked)var1x.getValue());
      }).toList();
      return select(new ItemBlockState(var0.getName()), var1, var3);
   }

   public static ItemModel.Unbaked isXmas(ItemModel.Unbaked var0, ItemModel.Unbaked var1) {
      return select(LocalTime.create("MM-dd", "", Optional.empty()), var1, List.of(when(List.of("12-24", "12-25", "12-26"), var0)));
   }
}
