package net.minecraft.world.item.component;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.network.Filterable;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jspecify.annotations.Nullable;

public record WrittenBookContent(Filterable<String> title, String author, int generation, List<Filterable<Component>> pages, boolean resolved) implements BookContent<Component, WrittenBookContent>, TooltipProvider {
   public static final WrittenBookContent EMPTY = new WrittenBookContent(Filterable.passThrough(""), "", 0, List.of(), true);
   public static final int PAGE_LENGTH = 32767;
   public static final int TITLE_LENGTH = 16;
   public static final int TITLE_MAX_LENGTH = 32;
   public static final int MAX_GENERATION = 3;
   public static final Codec<Component> CONTENT_CODEC = ComponentSerialization.flatRestrictedCodec(32767);
   public static final Codec<List<Filterable<Component>>> PAGES_CODEC;
   public static final Codec<WrittenBookContent> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, WrittenBookContent> STREAM_CODEC;

   public WrittenBookContent(Filterable<String> title, String author, int generation, List<Filterable<Component>> pages, boolean resolved) {
      super();
      if (generation >= 0 && generation <= 3) {
         this.title = title;
         this.author = author;
         this.generation = generation;
         this.pages = pages;
         this.resolved = resolved;
      } else {
         throw new IllegalArgumentException("Generation was " + generation + ", but must be between 0 and 3");
      }
   }

   private static Codec<Filterable<Component>> pageCodec(final Codec<Component> contentCodec) {
      return Filterable.codec(contentCodec);
   }

   public static Codec<List<Filterable<Component>>> pagesCodec(final Codec<Component> contentCodec) {
      return pageCodec(contentCodec).listOf();
   }

   public WrittenBookContent craftCopy() {
      return new WrittenBookContent(this.title, this.author, this.generation + 1, this.pages, this.resolved);
   }

   public static boolean resolveForItem(final ItemStack itemStack, final CommandSourceStack source, final @Nullable Player player) {
      WrittenBookContent content = (WrittenBookContent)itemStack.get(DataComponents.WRITTEN_BOOK_CONTENT);
      if (content != null && !content.resolved()) {
         WrittenBookContent resolvedContent = content.resolve(source, player);
         if (resolvedContent != null) {
            itemStack.set(DataComponents.WRITTEN_BOOK_CONTENT, resolvedContent);
            return true;
         }

         itemStack.set(DataComponents.WRITTEN_BOOK_CONTENT, content.markResolved());
      }

      return false;
   }

   public @Nullable WrittenBookContent resolve(final CommandSourceStack source, final @Nullable Player player) {
      if (this.resolved) {
         return null;
      } else {
         ImmutableList.Builder<Filterable<Component>> newPages = ImmutableList.builderWithExpectedSize(this.pages.size());

         for(Filterable<Component> page : this.pages) {
            Optional<Filterable<Component>> resolvedPage = resolvePage(source, player, page);
            if (resolvedPage.isEmpty()) {
               return null;
            }

            newPages.add((Filterable)resolvedPage.get());
         }

         return new WrittenBookContent(this.title, this.author, this.generation, newPages.build(), true);
      }
   }

   public WrittenBookContent markResolved() {
      return new WrittenBookContent(this.title, this.author, this.generation, this.pages, true);
   }

   private static Optional<Filterable<Component>> resolvePage(final CommandSourceStack source, final @Nullable Player player, final Filterable<Component> page) {
      return page.resolve((component) -> {
         try {
            Component newComponent = ComponentUtils.updateForEntity(source, component, player, 0);
            return isPageTooLarge(newComponent, source.registryAccess()) ? Optional.empty() : Optional.of(newComponent);
         } catch (Exception var4) {
            return Optional.of(component);
         }
      });
   }

   private static boolean isPageTooLarge(final Component page, final HolderLookup.Provider registries) {
      DataResult<JsonElement> json = ComponentSerialization.CODEC.encodeStart(registries.createSerializationContext(JsonOps.INSTANCE), page);
      return json.isSuccess() && GsonHelper.encodesLongerThan((JsonElement)json.getOrThrow(), 32767);
   }

   public List<Component> getPages(final boolean filterEnabled) {
      return Lists.transform(this.pages, (page) -> (Component)page.get(filterEnabled));
   }

   public WrittenBookContent withReplacedPages(final List<Filterable<Component>> newPages) {
      return new WrittenBookContent(this.title, this.author, this.generation, newPages, false);
   }

   public void addToTooltip(final Item.TooltipContext context, final Consumer<Component> consumer, final TooltipFlag flag, final DataComponentGetter components) {
      if (!StringUtil.isBlank(this.author)) {
         consumer.accept(Component.translatable("book.byAuthor", this.author).withStyle(ChatFormatting.GRAY));
      }

      consumer.accept(Component.translatable("book.generation." + this.generation).withStyle(ChatFormatting.GRAY));
   }

   static {
      PAGES_CODEC = pagesCodec(CONTENT_CODEC);
      CODEC = RecordCodecBuilder.create((i) -> i.group(Filterable.codec(Codec.string(0, 32)).fieldOf("title").forGetter(WrittenBookContent::title), Codec.STRING.fieldOf("author").forGetter(WrittenBookContent::author), ExtraCodecs.intRange(0, 3).optionalFieldOf("generation", 0).forGetter(WrittenBookContent::generation), PAGES_CODEC.optionalFieldOf("pages", List.of()).forGetter(WrittenBookContent::pages), Codec.BOOL.optionalFieldOf("resolved", false).forGetter(WrittenBookContent::resolved)).apply(i, WrittenBookContent::new));
      STREAM_CODEC = StreamCodec.composite(Filterable.streamCodec(ByteBufCodecs.stringUtf8(32)), WrittenBookContent::title, ByteBufCodecs.STRING_UTF8, WrittenBookContent::author, ByteBufCodecs.VAR_INT, WrittenBookContent::generation, Filterable.streamCodec(ComponentSerialization.STREAM_CODEC).apply(ByteBufCodecs.list()), WrittenBookContent::pages, ByteBufCodecs.BOOL, WrittenBookContent::resolved, WrittenBookContent::new);
   }
}
