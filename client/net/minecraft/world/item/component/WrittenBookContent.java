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
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
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

public record WrittenBookContent(Filterable<String> title, String author, int generation, List<Filterable<Component>> pages, boolean resolved) implements BookContent<Component, WrittenBookContent>, TooltipProvider {
   public static final WrittenBookContent EMPTY = new WrittenBookContent(Filterable.passThrough(""), "", 0, List.of(), true);
   public static final int PAGE_LENGTH = 32767;
   public static final int TITLE_LENGTH = 16;
   public static final int TITLE_MAX_LENGTH = 32;
   public static final int MAX_GENERATION = 3;
   public static final int MAX_CRAFTABLE_GENERATION = 2;
   public static final Codec<Component> CONTENT_CODEC = ComponentSerialization.flatRestrictedCodec(32767);
   public static final Codec<List<Filterable<Component>>> PAGES_CODEC;
   public static final Codec<WrittenBookContent> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, WrittenBookContent> STREAM_CODEC;

   public WrittenBookContent(Filterable<String> var1, String var2, int var3, List<Filterable<Component>> var4, boolean var5) {
      super();
      if (var3 >= 0 && var3 <= 3) {
         this.title = var1;
         this.author = var2;
         this.generation = var3;
         this.pages = var4;
         this.resolved = var5;
      } else {
         throw new IllegalArgumentException("Generation was " + var3 + ", but must be between 0 and 3");
      }
   }

   private static Codec<Filterable<Component>> pageCodec(Codec<Component> var0) {
      return Filterable.codec(var0);
   }

   public static Codec<List<Filterable<Component>>> pagesCodec(Codec<Component> var0) {
      return pageCodec(var0).listOf();
   }

   @Nullable
   public WrittenBookContent tryCraftCopy() {
      return this.generation >= 2 ? null : new WrittenBookContent(this.title, this.author, this.generation + 1, this.pages, this.resolved);
   }

   public static boolean resolveForItem(ItemStack var0, CommandSourceStack var1, @Nullable Player var2) {
      WrittenBookContent var3 = (WrittenBookContent)var0.get(DataComponents.WRITTEN_BOOK_CONTENT);
      if (var3 != null && !var3.resolved()) {
         WrittenBookContent var4 = var3.resolve(var1, var2);
         if (var4 != null) {
            var0.set(DataComponents.WRITTEN_BOOK_CONTENT, var4);
            return true;
         }

         var0.set(DataComponents.WRITTEN_BOOK_CONTENT, var3.markResolved());
      }

      return false;
   }

   @Nullable
   public WrittenBookContent resolve(CommandSourceStack var1, @Nullable Player var2) {
      if (this.resolved) {
         return null;
      } else {
         ImmutableList.Builder var3 = ImmutableList.builderWithExpectedSize(this.pages.size());

         for(Filterable var5 : this.pages) {
            Optional var6 = resolvePage(var1, var2, var5);
            if (var6.isEmpty()) {
               return null;
            }

            var3.add((Filterable)var6.get());
         }

         return new WrittenBookContent(this.title, this.author, this.generation, var3.build(), true);
      }
   }

   public WrittenBookContent markResolved() {
      return new WrittenBookContent(this.title, this.author, this.generation, this.pages, true);
   }

   private static Optional<Filterable<Component>> resolvePage(CommandSourceStack var0, @Nullable Player var1, Filterable<Component> var2) {
      return var2.resolve((var2x) -> {
         try {
            MutableComponent var3 = ComponentUtils.updateForEntity(var0, var2x, var1, 0);
            return isPageTooLarge(var3, var0.registryAccess()) ? Optional.empty() : Optional.of(var3);
         } catch (Exception var4) {
            return Optional.of(var2x);
         }
      });
   }

   private static boolean isPageTooLarge(Component var0, HolderLookup.Provider var1) {
      DataResult var2 = ComponentSerialization.CODEC.encodeStart(var1.createSerializationContext(JsonOps.INSTANCE), var0);
      return var2.isSuccess() && GsonHelper.encodesLongerThan((JsonElement)var2.getOrThrow(), 32767);
   }

   public List<Component> getPages(boolean var1) {
      return Lists.transform(this.pages, (var1x) -> (Component)var1x.get(var1));
   }

   public WrittenBookContent withReplacedPages(List<Filterable<Component>> var1) {
      return new WrittenBookContent(this.title, this.author, this.generation, var1, false);
   }

   public void addToTooltip(Item.TooltipContext var1, Consumer<Component> var2, TooltipFlag var3, @Nullable Player var4, ItemStack var5) {
      if (!StringUtil.isBlank(this.author)) {
         var2.accept(Component.translatable("book.byAuthor", this.author).withStyle(ChatFormatting.GRAY));
      }

      var2.accept(Component.translatable("book.generation." + this.generation).withStyle(ChatFormatting.GRAY));
   }

   // $FF: synthetic method
   public Object withReplacedPages(final List var1) {
      return this.withReplacedPages(var1);
   }

   static {
      PAGES_CODEC = pagesCodec(CONTENT_CODEC);
      CODEC = RecordCodecBuilder.create((var0) -> var0.group(Filterable.codec(Codec.string(0, 32)).fieldOf("title").forGetter(WrittenBookContent::title), Codec.STRING.fieldOf("author").forGetter(WrittenBookContent::author), ExtraCodecs.intRange(0, 3).optionalFieldOf("generation", 0).forGetter(WrittenBookContent::generation), PAGES_CODEC.optionalFieldOf("pages", List.of()).forGetter(WrittenBookContent::pages), Codec.BOOL.optionalFieldOf("resolved", false).forGetter(WrittenBookContent::resolved)).apply(var0, WrittenBookContent::new));
      STREAM_CODEC = StreamCodec.composite(Filterable.streamCodec(ByteBufCodecs.stringUtf8(32)), WrittenBookContent::title, ByteBufCodecs.STRING_UTF8, WrittenBookContent::author, ByteBufCodecs.VAR_INT, WrittenBookContent::generation, Filterable.streamCodec(ComponentSerialization.STREAM_CODEC).apply(ByteBufCodecs.list()), WrittenBookContent::pages, ByteBufCodecs.BOOL, WrittenBookContent::resolved, WrittenBookContent::new);
   }
}
