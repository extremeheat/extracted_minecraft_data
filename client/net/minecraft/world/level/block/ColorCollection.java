package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableList;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.apache.commons.lang3.function.TriFunction;

public record ColorCollection<T>(T white, T orange, T magenta, T lightBlue, T yellow, T lime, T pink, T gray, T lightGray, T cyan, T purple, T blue, T brown, T green, T red, T black) {
   public ColorCollection {
      super();
   }

   public static <T> ColorCollection<T> make(final Function<DyeColor, T> factory) {
      return new ColorCollection<T>(factory.apply(DyeColor.WHITE), factory.apply(DyeColor.ORANGE), factory.apply(DyeColor.MAGENTA), factory.apply(DyeColor.LIGHT_BLUE), factory.apply(DyeColor.YELLOW), factory.apply(DyeColor.LIME), factory.apply(DyeColor.PINK), factory.apply(DyeColor.GRAY), factory.apply(DyeColor.LIGHT_GRAY), factory.apply(DyeColor.CYAN), factory.apply(DyeColor.PURPLE), factory.apply(DyeColor.BLUE), factory.apply(DyeColor.BROWN), factory.apply(DyeColor.GREEN), factory.apply(DyeColor.RED), factory.apply(DyeColor.BLACK));
   }

   public static <T extends Block> ColorCollection<Block> registerBlocks(final String id, final TriFunction<String, Function<BlockBehaviour.Properties, Block>, BlockBehaviour.Properties, Block> register, final BiFunction<DyeColor, BlockBehaviour.Properties, T> colorBlockFactory, final Function<DyeColor, BlockBehaviour.Properties> propertiesSupplier) {
      return make((color) -> (Block)register.apply(color.getName() + "_" + id, (Function)(p) -> (Block)colorBlockFactory.apply(color, p), (BlockBehaviour.Properties)propertiesSupplier.apply(color)));
   }

   public static ColorCollection<Item> registerItems(final ColorCollection<Block> blocks, final BiFunction<Block, DyeColor, Item> itemFactory) {
      return make((color) -> (Item)itemFactory.apply(blocks.pick(color), color));
   }

   public static ColorCollection<Item> registerItems(final String id, final BiFunction<String, DyeColor, Item> itemFactory) {
      return make((color) -> (Item)itemFactory.apply(color.getName() + "_" + id, color));
   }

   public ImmutableList<T> asList() {
      return ImmutableList.of(this.white, this.orange, this.magenta, this.lightBlue, this.yellow, this.lime, this.pink, this.gray, this.lightGray, this.cyan, this.purple, this.blue, new Object[]{this.brown, this.green, this.red, this.black});
   }

   public void forEach(final Consumer<T> consumer) {
      consumer.accept(this.white);
      consumer.accept(this.orange);
      consumer.accept(this.magenta);
      consumer.accept(this.lightBlue);
      consumer.accept(this.yellow);
      consumer.accept(this.lime);
      consumer.accept(this.pink);
      consumer.accept(this.gray);
      consumer.accept(this.lightGray);
      consumer.accept(this.cyan);
      consumer.accept(this.purple);
      consumer.accept(this.blue);
      consumer.accept(this.brown);
      consumer.accept(this.green);
      consumer.accept(this.red);
      consumer.accept(this.black);
   }

   public T pick(final DyeColor dyeColor) {
      Object var10000;
      switch (dyeColor) {
         case WHITE -> var10000 = this.white;
         case ORANGE -> var10000 = this.orange;
         case MAGENTA -> var10000 = this.magenta;
         case LIGHT_BLUE -> var10000 = this.lightBlue;
         case YELLOW -> var10000 = this.yellow;
         case LIME -> var10000 = this.lime;
         case PINK -> var10000 = this.pink;
         case GRAY -> var10000 = this.gray;
         case LIGHT_GRAY -> var10000 = this.lightGray;
         case CYAN -> var10000 = this.cyan;
         case PURPLE -> var10000 = this.purple;
         case BLUE -> var10000 = this.blue;
         case BROWN -> var10000 = this.brown;
         case GREEN -> var10000 = this.green;
         case RED -> var10000 = this.red;
         case BLACK -> var10000 = this.black;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return (T)var10000;
   }

   public static <T, U> void zipApply(final BiConsumer<T, U> consumer, final ColorCollection<T> first, final ColorCollection<U> second) {
      consumer.accept(first.white(), second.white());
      consumer.accept(first.orange(), second.orange());
      consumer.accept(first.magenta(), second.magenta());
      consumer.accept(first.lightBlue(), second.lightBlue());
      consumer.accept(first.yellow(), second.yellow());
      consumer.accept(first.lime(), second.lime());
      consumer.accept(first.pink(), second.pink());
      consumer.accept(first.gray(), second.gray());
      consumer.accept(first.lightGray(), second.lightGray());
      consumer.accept(first.cyan(), second.cyan());
      consumer.accept(first.purple(), second.purple());
      consumer.accept(first.blue(), second.blue());
      consumer.accept(first.brown(), second.brown());
      consumer.accept(first.green(), second.green());
      consumer.accept(first.red(), second.red());
      consumer.accept(first.black(), second.black());
   }
}
