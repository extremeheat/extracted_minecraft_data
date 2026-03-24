package net.minecraft.util.parsing.packrat;

import java.util.ArrayList;
import java.util.List;

public interface Term<S> {
   boolean parse(ParseState<S> state, Scope scope, Control control);

   static <S, T> Term<S> marker(final Atom<T> name, final T value) {
      return new Marker(name, value);
   }

   @SafeVarargs
   static <S> Term<S> sequence(final Term<S>... terms) {
      return new Sequence<S>(terms);
   }

   @SafeVarargs
   static <S> Term<S> alternative(final Term<S>... terms) {
      return new Alternative<S>(terms);
   }

   static <S> Term<S> optional(final Term<S> term) {
      return new Maybe<S>(term);
   }

   static <S, T> Term<S> repeated(final NamedRule<S, T> element, final Atom<List<T>> listName) {
      return repeated(element, listName, 0);
   }

   static <S, T> Term<S> repeated(final NamedRule<S, T> element, final Atom<List<T>> listName, final int minRepetitions) {
      return new Repeated(element, listName, minRepetitions);
   }

   static <S, T> Term<S> repeatedWithTrailingSeparator(final NamedRule<S, T> element, final Atom<List<T>> listName, final Term<S> separator) {
      return repeatedWithTrailingSeparator(element, listName, separator, 0);
   }

   static <S, T> Term<S> repeatedWithTrailingSeparator(final NamedRule<S, T> element, final Atom<List<T>> listName, final Term<S> separator, final int minRepetitions) {
      return new RepeatedWithSeparator(element, listName, separator, minRepetitions, true);
   }

   static <S, T> Term<S> repeatedWithoutTrailingSeparator(final NamedRule<S, T> element, final Atom<List<T>> listName, final Term<S> separator) {
      return repeatedWithoutTrailingSeparator(element, listName, separator, 0);
   }

   static <S, T> Term<S> repeatedWithoutTrailingSeparator(final NamedRule<S, T> element, final Atom<List<T>> listName, final Term<S> separator, final int minRepetitions) {
      return new RepeatedWithSeparator(element, listName, separator, minRepetitions, false);
   }

   static <S> Term<S> positiveLookahead(final Term<S> term) {
      return new LookAhead<S>(term, true);
   }

   static <S> Term<S> negativeLookahead(final Term<S> term) {
      return new LookAhead<S>(term, false);
   }

   static <S> Term<S> cut() {
      return new Term<S>() {
         public boolean parse(final ParseState<S> state, final Scope scope, final Control control) {
            control.cut();
            return true;
         }

         public String toString() {
            return "\u2191";
         }
      };
   }

   static <S> Term<S> empty() {
      return new Term<S>() {
         public boolean parse(final ParseState<S> state, final Scope scope, final Control control) {
            return true;
         }

         public String toString() {
            return "\u03b5";
         }
      };
   }

   static <S> Term<S> fail(final Object message) {
      return new Term<S>() {
         public boolean parse(final ParseState<S> state, final Scope scope, final Control control) {
            state.errorCollector().store(state.mark(), message);
            return false;
         }

         public String toString() {
            return "fail";
         }
      };
   }

   public static record Marker<S, T>(Atom<T> name, T value) implements Term<S> {
      public Marker {
         super();
      }

      public boolean parse(final ParseState<S> state, final Scope scope, final Control control) {
         scope.put(this.name, this.value);
         return true;
      }
   }

   public static record Sequence<S>(Term<S>[] elements) implements Term<S> {
      public Sequence {
         super();
      }

      public boolean parse(final ParseState<S> state, final Scope scope, final Control control) {
         int mark = state.mark();

         for(Term<S> element : this.elements) {
            if (!element.parse(state, scope, control)) {
               state.restore(mark);
               return false;
            }
         }

         return true;
      }
   }

   public static record Alternative<S>(Term<S>[] elements) implements Term<S> {
      public Alternative {
         super();
      }

      public boolean parse(final ParseState<S> state, final Scope scope, final Control control) {
         Control controlForThis = state.acquireControl();

         try {
            int mark = state.mark();
            scope.splitFrame();

            for(Term<S> element : this.elements) {
               if (element.parse(state, scope, controlForThis)) {
                  scope.mergeFrame();
                  boolean var10 = true;
                  return var10;
               }

               scope.clearFrameValues();
               state.restore(mark);
               if (controlForThis.hasCut()) {
                  break;
               }
            }

            scope.popFrame();
            boolean var14 = false;
            return var14;
         } finally {
            state.releaseControl();
         }
      }
   }

   public static record Maybe<S>(Term<S> term) implements Term<S> {
      public Maybe {
         super();
      }

      public boolean parse(final ParseState<S> state, final Scope scope, final Control control) {
         int mark = state.mark();
         if (!this.term.parse(state, scope, control)) {
            state.restore(mark);
         }

         return true;
      }
   }

   public static record Repeated<S, T>(NamedRule<S, T> element, Atom<List<T>> listName, int minRepetitions) implements Term<S> {
      public Repeated {
         super();
      }

      public boolean parse(final ParseState<S> state, final Scope scope, final Control control) {
         int mark = state.mark();
         List<T> elements = new ArrayList(this.minRepetitions);

         while(true) {
            int entryMark = state.mark();
            T parsedElement = (T)state.parse(this.element);
            if (parsedElement == null) {
               state.restore(entryMark);
               if (elements.size() < this.minRepetitions) {
                  state.restore(mark);
                  return false;
               } else {
                  scope.put(this.listName, elements);
                  return true;
               }
            }

            elements.add(parsedElement);
         }
      }
   }

   public static record RepeatedWithSeparator<S, T>(NamedRule<S, T> element, Atom<List<T>> listName, Term<S> separator, int minRepetitions, boolean allowTrailingSeparator) implements Term<S> {
      public RepeatedWithSeparator {
         super();
      }

      public boolean parse(final ParseState<S> state, final Scope scope, final Control control) {
         int listMark = state.mark();
         List<T> elements = new ArrayList(this.minRepetitions);
         boolean first = true;

         while(true) {
            int markBeforeSeparator = state.mark();
            if (!first && !this.separator.parse(state, scope, control)) {
               state.restore(markBeforeSeparator);
               break;
            }

            int markAfterSeparator = state.mark();
            T parsedElement = (T)state.parse(this.element);
            if (parsedElement == null) {
               if (first) {
                  state.restore(markAfterSeparator);
               } else {
                  if (!this.allowTrailingSeparator) {
                     state.restore(listMark);
                     return false;
                  }

                  state.restore(markAfterSeparator);
               }
               break;
            }

            elements.add(parsedElement);
            first = false;
         }

         if (elements.size() < this.minRepetitions) {
            state.restore(listMark);
            return false;
         } else {
            scope.put(this.listName, elements);
            return true;
         }
      }
   }

   public static record LookAhead<S>(Term<S> term, boolean positive) implements Term<S> {
      public LookAhead {
         super();
      }

      public boolean parse(final ParseState<S> state, final Scope scope, final Control control) {
         int mark = state.mark();
         boolean result = this.term.parse(state.silent(), scope, control);
         state.restore(mark);
         return this.positive == result;
      }
   }
}
