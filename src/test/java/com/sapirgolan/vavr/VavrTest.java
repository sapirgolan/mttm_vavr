package com.sapirgolan.vavr;

import static io.vavr.API.$;
import static org.assertj.core.api.Assertions.assertThat;

import io.vavr.API;
import io.vavr.Lazy;
import io.vavr.Predicates;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.collection.Stream;
import io.vavr.control.Option;
import io.vavr.control.Try;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

public class VavrTest {

  @Test
  void concepts() {
    /*
      JAVA8 Functional Interfaces are:
      Supplier<T>
      Consumer<T>
      Predicate<T>
      Operator<T>
      Function<T,V>
      BiFunction<T,X,Z>
      etc...
    */

    //In Vavr we have got...

    //Tuple...

    //Factory for Collection
  }

  @Test
  void Tuple() {
    Tuple2<String, String> vavr = API.Tuple("vavr", "1.0.0");
    //internal members
    assertThat(vavr._1).isEqualTo("vavr");
    assertThat(vavr._2).isEqualTo("1.0.0");

    //transform
    String concat = vavr.apply((name, version) -> name + "_" + version);
    //show concat
    System.out.println(concat);

    //N mappers
    Tuple2<String, Integer> that = vavr.map(
        name -> "ja" + name.substring(0, 2),
        version -> 8
    );
    System.out.println(that);

    //single value mapper
    Tuple2<String, String> previousRelease = vavr.map2(version -> "0.9.3");
    System.out.println(previousRelease);
  }

  @Test
  void Option() {
    //serialized as opposed to Optional
    String mttm = Option.of("mttm")
        .map(String::toUpperCase)
        .getOrNull();

    System.out.println(mttm);
  }

  @Test
  void Option_is_Iterable() {
    //java8
    java.util.List<Optional<Integer>> optionals = List.of(Optional.of(42), Optional.<Integer>empty(), Optional.of(4)).asJava();
    java.util.List<Integer> integers = optionals.stream()
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toList());
    System.out.println(integers);

    //vavr
    List<Option<Integer>> optionalsVavr = API.List(Option.of(42), Option.none(), Option.some(4));
    List<Integer> integersVavr = optionalsVavr.flatMap(o -> o);
    System.out.println(integersVavr);
  }

  @Test
  void tryOf() {
    String s1 = Try.of(() -> new URI("gap"))
        .recoverWith(URISyntaxException.class, Try.of(() -> new URI("2'nd try")))
        .recoverWith(throwable -> Try.of(() -> new URI("\"https://www.vavr.io/vavr-docs/\"")))
        .map(URI::toString)
        .filter(s -> true)
        .getOrElse("default");

    System.out.println(s1);
  }

  @Test
  void lazy_is_a_supplierThatRunJustOnce() {
    //java 8 Supplier
    Supplier<Integer> supplier = () -> {
      System.out.println("computing...");
      return 42;
    };

    System.out.println(supplier.get());
    System.out.println(supplier.get());
    System.out.println(supplier.get());

    //vavr Lazy
    Lazy<Integer> integerLazy = Lazy.of(() -> {
      System.out.println("Lazy computing...");
      return 42;
    });

    System.out.println(integerLazy.get());
    System.out.println(integerLazy.get());
    System.out.println(integerLazy.get());

    //vavr Lazy with map (lazy is passed around)
  }

  @Test
  void collections() {
    //JAVA Collections API is MUTABLE !!!

    //vavr collections are IMMUTABLE and PERSISTED
    //Stop and talk about naive IMMUTABLE

    List<Integer> list = List.of(1, 2, 3);
    System.out.println(list);
    List<Integer> drop = list.drop(1);
    System.out.println(drop);
    List<Integer> tail = list.tail();
    System.out.println(tail);
    assertThat(drop).isSameAs(tail);
    assertThat(drop).isEqualTo(tail);

    System.out.println(list.zipWithIndex());
  }

  @Test
  void more_collections_api() {
    char[] element = "abcde".toCharArray();
//    System.out.println(List.ofAll(element)
//        .shuffle());
//
////    System.out.println(List.ofAll(element)
////        .permutations());
//
//    System.out.println(List.ofAll(element)
//        .dropWhile(character -> 'a' == character));
//
//    System.out.println(List.ofAll(element)
//        .dropUntil(character -> 'c' == character));
//
//    System.out.println(List.ofAll(element)
//        .dropRightUntil(character -> 'c' == character));

    System.out.println(List.ofAll(element)
        .foldLeft("!", (s, character) -> s + character));

    /*System.out.println(List.ofAll(element).reverse());

    System.out.println(List.ofAll(element)
        .reverse()
        .foldLeft("!", (s, character) -> s + character));*/

    System.out.println(List.ofAll(element)
        .foldRight("!", (s, character) -> s + character));
  }

  @Test
  void stream() {
    //Stream is just one more collection
    Stream<Integer> iterate = Stream.iterate(0, i -> i + 1);
    iterate
        .take(10)
        .forEach(System.out::println);

    System.out.println("stop");
    iterate
        .take(16)
        .forEach(System.out::println);
  }

  @Test
  void repackageToJava() {
    //I want my JDK8 collection!
  }

  @Test
  void checkExceptions() {
    List<URI> map = List.of("")
        .map(API.unchecked(s -> new URI(s)));
//        .map(URI::new);
  }

  @Test
  void patternMatching() {
    //JDK 8
    int input = 2;
    String output;
    switch (input) {
      case 0:
        output = "zero";
        break;
      case 1:
        output = "one";
        break;
      case 2:
        output = "two";
        break;
      case 3:
        output = "three";
        break;
      default:
        output = "unknown";
        break;
    }

    assertThat(output).isEqualTo("two");

    //vavr
    output = API.Match(input).of(
        API.Case($(o -> o == 2), "one"),
        API.Case($(Predicates.is(2)), "two"),
        API.Case($(), "unknown")
    );

    assertThat(output).isEqualTo("two");
  }

  @Test
  void patternMatching_valueNotPresent() {
    int i = 10;
    //code with exception
    String two = API.Match(i).of(
        API.Case($(2), "Two")
    );

    //safe code
    Option<String> option = API.Match(i).option(
        API.Case($(2), "Two")
    );
    assertThat(option).isEmpty();
    System.out.println(option.isEmpty());
  }
}
