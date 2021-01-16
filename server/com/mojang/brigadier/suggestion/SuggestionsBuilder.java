package com.mojang.brigadier.suggestion;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.StringRange;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SuggestionsBuilder {
   private final String input;
   private final int start;
   private final String remaining;
   private final List<Suggestion> result = new ArrayList();

   public SuggestionsBuilder(String var1, int var2) {
      super();
      this.input = var1;
      this.start = var2;
      this.remaining = var1.substring(var2);
   }

   public String getInput() {
      return this.input;
   }

   public int getStart() {
      return this.start;
   }

   public String getRemaining() {
      return this.remaining;
   }

   public Suggestions build() {
      return Suggestions.create(this.input, this.result);
   }

   public CompletableFuture<Suggestions> buildFuture() {
      return CompletableFuture.completedFuture(this.build());
   }

   public SuggestionsBuilder suggest(String var1) {
      if (var1.equals(this.remaining)) {
         return this;
      } else {
         this.result.add(new Suggestion(StringRange.between(this.start, this.input.length()), var1));
         return this;
      }
   }

   public SuggestionsBuilder suggest(String var1, Message var2) {
      if (var1.equals(this.remaining)) {
         return this;
      } else {
         this.result.add(new Suggestion(StringRange.between(this.start, this.input.length()), var1, var2));
         return this;
      }
   }

   public SuggestionsBuilder suggest(int var1) {
      this.result.add(new IntegerSuggestion(StringRange.between(this.start, this.input.length()), var1));
      return this;
   }

   public SuggestionsBuilder suggest(int var1, Message var2) {
      this.result.add(new IntegerSuggestion(StringRange.between(this.start, this.input.length()), var1, var2));
      return this;
   }

   public SuggestionsBuilder add(SuggestionsBuilder var1) {
      this.result.addAll(var1.result);
      return this;
   }

   public SuggestionsBuilder createOffset(int var1) {
      return new SuggestionsBuilder(this.input, var1);
   }

   public SuggestionsBuilder restart() {
      return new SuggestionsBuilder(this.input, this.start);
   }
}
