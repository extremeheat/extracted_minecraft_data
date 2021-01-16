package com.mojang.brigadier;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.SuggestionContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CommandDispatcher<S> {
   public static final String ARGUMENT_SEPARATOR = " ";
   public static final char ARGUMENT_SEPARATOR_CHAR = ' ';
   private static final String USAGE_OPTIONAL_OPEN = "[";
   private static final String USAGE_OPTIONAL_CLOSE = "]";
   private static final String USAGE_REQUIRED_OPEN = "(";
   private static final String USAGE_REQUIRED_CLOSE = ")";
   private static final String USAGE_OR = "|";
   private final RootCommandNode<S> root;
   private final Predicate<CommandNode<S>> hasCommand;
   private ResultConsumer<S> consumer;

   public CommandDispatcher(RootCommandNode<S> var1) {
      super();
      this.hasCommand = new Predicate<CommandNode<S>>() {
         public boolean test(CommandNode<S> var1) {
            return var1 != null && (var1.getCommand() != null || var1.getChildren().stream().anyMatch(CommandDispatcher.this.hasCommand));
         }
      };
      this.consumer = (var0, var1x, var2) -> {
      };
      this.root = var1;
   }

   public CommandDispatcher() {
      this(new RootCommandNode());
   }

   public LiteralCommandNode<S> register(LiteralArgumentBuilder<S> var1) {
      LiteralCommandNode var2 = var1.build();
      this.root.addChild(var2);
      return var2;
   }

   public void setConsumer(ResultConsumer<S> var1) {
      this.consumer = var1;
   }

   public int execute(String var1, S var2) throws CommandSyntaxException {
      return this.execute(new StringReader(var1), var2);
   }

   public int execute(StringReader var1, S var2) throws CommandSyntaxException {
      ParseResults var3 = this.parse(var1, var2);
      return this.execute(var3);
   }

   public int execute(ParseResults<S> var1) throws CommandSyntaxException {
      if (var1.getReader().canRead()) {
         if (var1.getExceptions().size() == 1) {
            throw (CommandSyntaxException)var1.getExceptions().values().iterator().next();
         } else if (var1.getContext().getRange().isEmpty()) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext(var1.getReader());
         } else {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(var1.getReader());
         }
      } else {
         int var2 = 0;
         int var3 = 0;
         boolean var4 = false;
         boolean var5 = false;
         String var6 = var1.getReader().getString();
         CommandContext var7 = var1.getContext().build(var6);
         Object var8 = Collections.singletonList(var7);

         for(ArrayList var9 = null; var8 != null; var9 = null) {
            int var10 = ((List)var8).size();

            for(int var11 = 0; var11 < var10; ++var11) {
               CommandContext var12 = (CommandContext)((List)var8).get(var11);
               CommandContext var13 = var12.getChild();
               if (var13 != null) {
                  var4 |= var12.isForked();
                  if (var13.hasNodes()) {
                     var5 = true;
                     RedirectModifier var20 = var12.getRedirectModifier();
                     if (var20 == null) {
                        if (var9 == null) {
                           var9 = new ArrayList(1);
                        }

                        var9.add(var13.copyFor(var12.getSource()));
                     } else {
                        try {
                           Collection var15 = var20.apply(var12);
                           if (!var15.isEmpty()) {
                              if (var9 == null) {
                                 var9 = new ArrayList(var15.size());
                              }

                              Iterator var16 = var15.iterator();

                              while(var16.hasNext()) {
                                 Object var17 = var16.next();
                                 var9.add(var13.copyFor(var17));
                              }
                           }
                        } catch (CommandSyntaxException var18) {
                           this.consumer.onCommandComplete(var12, false, 0);
                           if (!var4) {
                              throw var18;
                           }
                        }
                     }
                  }
               } else if (var12.getCommand() != null) {
                  var5 = true;

                  try {
                     int var14 = var12.getCommand().run(var12);
                     var2 += var14;
                     this.consumer.onCommandComplete(var12, true, var14);
                     ++var3;
                  } catch (CommandSyntaxException var19) {
                     this.consumer.onCommandComplete(var12, false, 0);
                     if (!var4) {
                        throw var19;
                     }
                  }
               }
            }

            var8 = var9;
         }

         if (!var5) {
            this.consumer.onCommandComplete(var7, false, 0);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext(var1.getReader());
         } else {
            return var4 ? var3 : var2;
         }
      }
   }

   public ParseResults<S> parse(String var1, S var2) {
      return this.parse(new StringReader(var1), var2);
   }

   public ParseResults<S> parse(StringReader var1, S var2) {
      CommandContextBuilder var3 = new CommandContextBuilder(this, var2, this.root, var1.getCursor());
      return this.parseNodes(this.root, var1, var3);
   }

   private ParseResults<S> parseNodes(CommandNode<S> var1, StringReader var2, CommandContextBuilder<S> var3) {
      Object var4 = var3.getSource();
      LinkedHashMap var5 = null;
      ArrayList var6 = null;
      int var7 = var2.getCursor();
      Iterator var8 = var1.getRelevantNodes(var2).iterator();

      while(true) {
         CommandNode var9;
         CommandContextBuilder var10;
         StringReader var11;
         while(true) {
            do {
               if (!var8.hasNext()) {
                  if (var6 != null) {
                     if (var6.size() > 1) {
                        var6.sort((var0, var1x) -> {
                           if (!var0.getReader().canRead() && var1x.getReader().canRead()) {
                              return -1;
                           } else if (var0.getReader().canRead() && !var1x.getReader().canRead()) {
                              return 1;
                           } else if (var0.getExceptions().isEmpty() && !var1x.getExceptions().isEmpty()) {
                              return -1;
                           } else {
                              return !var0.getExceptions().isEmpty() && var1x.getExceptions().isEmpty() ? 1 : 0;
                           }
                        });
                     }

                     return (ParseResults)var6.get(0);
                  }

                  return new ParseResults(var3, var2, (Map)(var5 == null ? Collections.emptyMap() : var5));
               }

               var9 = (CommandNode)var8.next();
            } while(!var9.canUse(var4));

            var10 = var3.copy();
            var11 = new StringReader(var2);

            try {
               try {
                  var9.parse(var11, var10);
               } catch (RuntimeException var14) {
                  throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException().createWithContext(var11, var14.getMessage());
               }

               if (var11.canRead() && var11.peek() != ' ') {
                  throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherExpectedArgumentSeparator().createWithContext(var11);
               }
               break;
            } catch (CommandSyntaxException var15) {
               if (var5 == null) {
                  var5 = new LinkedHashMap();
               }

               var5.put(var9, var15);
               var11.setCursor(var7);
            }
         }

         var10.withCommand(var9.getCommand());
         if (var11.canRead(var9.getRedirect() == null ? 2 : 1)) {
            var11.skip();
            if (var9.getRedirect() != null) {
               CommandContextBuilder var16 = new CommandContextBuilder(this, var4, var9.getRedirect(), var11.getCursor());
               ParseResults var13 = this.parseNodes(var9.getRedirect(), var11, var16);
               var10.withChild(var13.getContext());
               return new ParseResults(var10, var13.getReader(), var13.getExceptions());
            }

            ParseResults var12 = this.parseNodes(var9, var11, var10);
            if (var6 == null) {
               var6 = new ArrayList(1);
            }

            var6.add(var12);
         } else {
            if (var6 == null) {
               var6 = new ArrayList(1);
            }

            var6.add(new ParseResults(var10, var11, Collections.emptyMap()));
         }
      }
   }

   public String[] getAllUsage(CommandNode<S> var1, S var2, boolean var3) {
      ArrayList var4 = new ArrayList();
      this.getAllUsage(var1, var2, var4, "", var3);
      return (String[])var4.toArray(new String[var4.size()]);
   }

   private void getAllUsage(CommandNode<S> var1, S var2, ArrayList<String> var3, String var4, boolean var5) {
      if (!var5 || var1.canUse(var2)) {
         if (var1.getCommand() != null) {
            var3.add(var4);
         }

         if (var1.getRedirect() != null) {
            String var6 = var1.getRedirect() == this.root ? "..." : "-> " + var1.getRedirect().getUsageText();
            var3.add(var4.isEmpty() ? var1.getUsageText() + " " + var6 : var4 + " " + var6);
         } else if (!var1.getChildren().isEmpty()) {
            Iterator var8 = var1.getChildren().iterator();

            while(var8.hasNext()) {
               CommandNode var7 = (CommandNode)var8.next();
               this.getAllUsage(var7, var2, var3, var4.isEmpty() ? var7.getUsageText() : var4 + " " + var7.getUsageText(), var5);
            }
         }

      }
   }

   public Map<CommandNode<S>, String> getSmartUsage(CommandNode<S> var1, S var2) {
      LinkedHashMap var3 = new LinkedHashMap();
      boolean var4 = var1.getCommand() != null;
      Iterator var5 = var1.getChildren().iterator();

      while(var5.hasNext()) {
         CommandNode var6 = (CommandNode)var5.next();
         String var7 = this.getSmartUsage(var6, var2, var4, false);
         if (var7 != null) {
            var3.put(var6, var7);
         }
      }

      return var3;
   }

   private String getSmartUsage(CommandNode<S> var1, S var2, boolean var3, boolean var4) {
      if (!var1.canUse(var2)) {
         return null;
      } else {
         String var5 = var3 ? "[" + var1.getUsageText() + "]" : var1.getUsageText();
         boolean var6 = var1.getCommand() != null;
         String var7 = var6 ? "[" : "(";
         String var8 = var6 ? "]" : ")";
         if (!var4) {
            if (var1.getRedirect() != null) {
               String var15 = var1.getRedirect() == this.root ? "..." : "-> " + var1.getRedirect().getUsageText();
               return var5 + " " + var15;
            }

            Collection var9 = (Collection)var1.getChildren().stream().filter((var1x) -> {
               return var1x.canUse(var2);
            }).collect(Collectors.toList());
            if (var9.size() == 1) {
               String var10 = this.getSmartUsage((CommandNode)var9.iterator().next(), var2, var6, var6);
               if (var10 != null) {
                  return var5 + " " + var10;
               }
            } else if (var9.size() > 1) {
               LinkedHashSet var16 = new LinkedHashSet();
               Iterator var11 = var9.iterator();

               while(var11.hasNext()) {
                  CommandNode var12 = (CommandNode)var11.next();
                  String var13 = this.getSmartUsage(var12, var2, var6, true);
                  if (var13 != null) {
                     var16.add(var13);
                  }
               }

               if (var16.size() == 1) {
                  String var18 = (String)var16.iterator().next();
                  return var5 + " " + (var6 ? "[" + var18 + "]" : var18);
               }

               if (var16.size() > 1) {
                  StringBuilder var17 = new StringBuilder(var7);
                  int var19 = 0;

                  for(Iterator var20 = var9.iterator(); var20.hasNext(); ++var19) {
                     CommandNode var14 = (CommandNode)var20.next();
                     if (var19 > 0) {
                        var17.append("|");
                     }

                     var17.append(var14.getUsageText());
                  }

                  if (var19 > 0) {
                     var17.append(var8);
                     return var5 + " " + var17.toString();
                  }
               }
            }
         }

         return var5;
      }
   }

   public CompletableFuture<Suggestions> getCompletionSuggestions(ParseResults<S> var1) {
      return this.getCompletionSuggestions(var1, var1.getReader().getTotalLength());
   }

   public CompletableFuture<Suggestions> getCompletionSuggestions(ParseResults<S> var1, int var2) {
      CommandContextBuilder var3 = var1.getContext();
      SuggestionContext var4 = var3.findSuggestionContext(var2);
      CommandNode var5 = var4.parent;
      int var6 = Math.min(var4.startPos, var2);
      String var7 = var1.getReader().getString();
      String var8 = var7.substring(0, var2);
      CompletableFuture[] var9 = new CompletableFuture[var5.getChildren().size()];
      int var10 = 0;

      CompletableFuture var13;
      for(Iterator var11 = var5.getChildren().iterator(); var11.hasNext(); var9[var10++] = var13) {
         CommandNode var12 = (CommandNode)var11.next();
         var13 = Suggestions.empty();

         try {
            var13 = var12.listSuggestions(var3.build(var8), new SuggestionsBuilder(var8, var6));
         } catch (CommandSyntaxException var15) {
         }
      }

      CompletableFuture var16 = new CompletableFuture();
      CompletableFuture.allOf(var9).thenRun(() -> {
         ArrayList var3 = new ArrayList();
         CompletableFuture[] var4 = var9;
         int var5 = var9.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            CompletableFuture var7x = var4[var6];
            var3.add(var7x.join());
         }

         var16.complete(Suggestions.merge(var7, var3));
      });
      return var16;
   }

   public RootCommandNode<S> getRoot() {
      return this.root;
   }

   public Collection<String> getPath(CommandNode<S> var1) {
      ArrayList var2 = new ArrayList();
      this.addPaths(this.root, var2, new ArrayList());
      Iterator var3 = var2.iterator();

      List var4;
      do {
         if (!var3.hasNext()) {
            return Collections.emptyList();
         }

         var4 = (List)var3.next();
      } while(var4.get(var4.size() - 1) != var1);

      ArrayList var5 = new ArrayList(var4.size());
      Iterator var6 = var4.iterator();

      while(var6.hasNext()) {
         CommandNode var7 = (CommandNode)var6.next();
         if (var7 != this.root) {
            var5.add(var7.getName());
         }
      }

      return var5;
   }

   public CommandNode<S> findNode(Collection<String> var1) {
      Object var2 = this.root;
      Iterator var3 = var1.iterator();

      do {
         if (!var3.hasNext()) {
            return (CommandNode)var2;
         }

         String var4 = (String)var3.next();
         var2 = ((CommandNode)var2).getChild(var4);
      } while(var2 != null);

      return null;
   }

   public void findAmbiguities(AmbiguityConsumer<S> var1) {
      this.root.findAmbiguities(var1);
   }

   private void addPaths(CommandNode<S> var1, List<List<CommandNode<S>>> var2, List<CommandNode<S>> var3) {
      ArrayList var4 = new ArrayList(var3);
      var4.add(var1);
      var2.add(var4);
      Iterator var5 = var1.getChildren().iterator();

      while(var5.hasNext()) {
         CommandNode var6 = (CommandNode)var5.next();
         this.addPaths(var6, var2, var4);
      }

   }
}
