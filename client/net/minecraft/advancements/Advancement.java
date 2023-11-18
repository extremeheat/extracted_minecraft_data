package net.minecraft.advancements;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public record Advancement(
   Optional<ResourceLocation> a,
   Optional<DisplayInfo> b,
   AdvancementRewards c,
   Map<String, Criterion<?>> d,
   AdvancementRequirements e,
   boolean f,
   Optional<Component> g
) {
   private final Optional<ResourceLocation> parent;
   private final Optional<DisplayInfo> display;
   private final AdvancementRewards rewards;
   private final Map<String, Criterion<?>> criteria;
   private final AdvancementRequirements requirements;
   private final boolean sendsTelemetryEvent;
   private final Optional<Component> name;

   public Advancement(
      Optional<ResourceLocation> var1,
      Optional<DisplayInfo> var2,
      AdvancementRewards var3,
      Map<String, Criterion<?>> var4,
      AdvancementRequirements var5,
      boolean var6
   ) {
      this(var1, var2, var3, Map.copyOf(var4), var5, var6, var2.map(Advancement::decorateName));
   }

   public Advancement(
      Optional<ResourceLocation> var1,
      Optional<DisplayInfo> var2,
      AdvancementRewards var3,
      Map<String, Criterion<?>> var4,
      AdvancementRequirements var5,
      boolean var6,
      Optional<Component> var7
   ) {
      super();
      this.parent = var1;
      this.display = var2;
      this.rewards = var3;
      this.criteria = var4;
      this.requirements = var5;
      this.sendsTelemetryEvent = var6;
      this.name = var7;
   }

   private static Component decorateName(DisplayInfo var0) {
      Component var1 = var0.getTitle();
      ChatFormatting var2 = var0.getFrame().getChatColor();
      MutableComponent var3 = ComponentUtils.mergeStyles(var1.copy(), Style.EMPTY.withColor(var2)).append("\n").append(var0.getDescription());
      MutableComponent var4 = var1.copy().withStyle(var1x -> var1x.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, var3)));
      return ComponentUtils.wrapInSquareBrackets(var4).withStyle(var2);
   }

   public static Component name(AdvancementHolder var0) {
      return var0.value().name().orElseGet(() -> Component.literal(var0.id().toString()));
   }

   public JsonObject serializeToJson() {
      JsonObject var1 = new JsonObject();
      this.parent.ifPresent(var1x -> var1.addProperty("parent", var1x.toString()));
      this.display.ifPresent(var1x -> var1.add("display", var1x.serializeToJson()));
      var1.add("rewards", this.rewards.serializeToJson());
      JsonObject var2 = new JsonObject();

      for(Entry var4 : this.criteria.entrySet()) {
         var2.add((String)var4.getKey(), ((Criterion)var4.getValue()).serializeToJson());
      }

      var1.add("criteria", var2);
      var1.add("requirements", this.requirements.toJson());
      var1.addProperty("sends_telemetry_event", this.sendsTelemetryEvent);
      return var1;
   }

   public static Advancement fromJson(JsonObject var0, DeserializationContext var1) {
      Optional var2 = var0.has("parent") ? Optional.of(new ResourceLocation(GsonHelper.getAsString(var0, "parent"))) : Optional.empty();
      Optional var3 = var0.has("display") ? Optional.of(DisplayInfo.fromJson(GsonHelper.getAsJsonObject(var0, "display"))) : Optional.empty();
      AdvancementRewards var4 = var0.has("rewards") ? AdvancementRewards.deserialize(GsonHelper.getAsJsonObject(var0, "rewards")) : AdvancementRewards.EMPTY;
      Map var5 = Criterion.criteriaFromJson(GsonHelper.getAsJsonObject(var0, "criteria"), var1);
      if (var5.isEmpty()) {
         throw new JsonSyntaxException("Advancement criteria cannot be empty");
      } else {
         JsonArray var6 = GsonHelper.getAsJsonArray(var0, "requirements", new JsonArray());
         AdvancementRequirements var7;
         if (var6.isEmpty()) {
            var7 = AdvancementRequirements.allOf(var5.keySet());
         } else {
            var7 = AdvancementRequirements.fromJson(var6, var5.keySet());
         }

         boolean var8 = GsonHelper.getAsBoolean(var0, "sends_telemetry_event", false);
         return new Advancement(var2, var3, var4, var5, var7, var8);
      }
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeOptional(this.parent, FriendlyByteBuf::writeResourceLocation);
      var1.writeOptional(this.display, (var0, var1x) -> var1x.serializeToNetwork(var0));
      this.requirements.write(var1);
      var1.writeBoolean(this.sendsTelemetryEvent);
   }

   public static Advancement read(FriendlyByteBuf var0) {
      return new Advancement(
         var0.readOptional(FriendlyByteBuf::readResourceLocation),
         var0.readOptional(DisplayInfo::fromNetwork),
         AdvancementRewards.EMPTY,
         Map.of(),
         new AdvancementRequirements(var0),
         var0.readBoolean()
      );
   }

   public boolean isRoot() {
      return this.parent.isEmpty();
   }

   public static class Builder {
      private Optional<ResourceLocation> parent = Optional.empty();
      private Optional<DisplayInfo> display = Optional.empty();
      private AdvancementRewards rewards = AdvancementRewards.EMPTY;
      private final com.google.common.collect.ImmutableMap.Builder<String, Criterion<?>> criteria = ImmutableMap.builder();
      private Optional<AdvancementRequirements> requirements = Optional.empty();
      private AdvancementRequirements.Strategy requirementsStrategy = AdvancementRequirements.Strategy.AND;
      private boolean sendsTelemetryEvent;

      public Builder() {
         super();
      }

      public static Advancement.Builder advancement() {
         return new Advancement.Builder().sendsTelemetryEvent();
      }

      public static Advancement.Builder recipeAdvancement() {
         return new Advancement.Builder();
      }

      public Advancement.Builder parent(AdvancementHolder var1) {
         this.parent = Optional.of(var1.id());
         return this;
      }

      @Deprecated(
         forRemoval = true
      )
      public Advancement.Builder parent(ResourceLocation var1) {
         this.parent = Optional.of(var1);
         return this;
      }

      public Advancement.Builder display(
         ItemStack var1, Component var2, Component var3, @Nullable ResourceLocation var4, FrameType var5, boolean var6, boolean var7, boolean var8
      ) {
         return this.display(new DisplayInfo(var1, var2, var3, var4, var5, var6, var7, var8));
      }

      public Advancement.Builder display(
         ItemLike var1, Component var2, Component var3, @Nullable ResourceLocation var4, FrameType var5, boolean var6, boolean var7, boolean var8
      ) {
         return this.display(new DisplayInfo(new ItemStack(var1.asItem()), var2, var3, var4, var5, var6, var7, var8));
      }

      public Advancement.Builder display(DisplayInfo var1) {
         this.display = Optional.of(var1);
         return this;
      }

      public Advancement.Builder rewards(AdvancementRewards.Builder var1) {
         return this.rewards(var1.build());
      }

      public Advancement.Builder rewards(AdvancementRewards var1) {
         this.rewards = var1;
         return this;
      }

      public Advancement.Builder addCriterion(String var1, Criterion<?> var2) {
         this.criteria.put(var1, var2);
         return this;
      }

      public Advancement.Builder requirements(AdvancementRequirements.Strategy var1) {
         this.requirementsStrategy = var1;
         return this;
      }

      public Advancement.Builder requirements(AdvancementRequirements var1) {
         this.requirements = Optional.of(var1);
         return this;
      }

      public Advancement.Builder sendsTelemetryEvent() {
         this.sendsTelemetryEvent = true;
         return this;
      }

      public AdvancementHolder build(ResourceLocation var1) {
         ImmutableMap var2 = this.criteria.buildOrThrow();
         AdvancementRequirements var3 = this.requirements.orElseGet(() -> this.requirementsStrategy.create(var2.keySet()));
         return new AdvancementHolder(var1, new Advancement(this.parent, this.display, this.rewards, var2, var3, this.sendsTelemetryEvent));
      }

      public AdvancementHolder save(Consumer<AdvancementHolder> var1, String var2) {
         AdvancementHolder var3 = this.build(new ResourceLocation(var2));
         var1.accept(var3);
         return var3;
      }
   }
}
