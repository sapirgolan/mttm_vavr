package com.sapirgolan.vavr;

import static org.assertj.core.api.Assertions.assertThat;

import io.vavr.API;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.control.Option;
import java.util.Optional;
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
    List<Option<Integer>> optionalsVavr = List.of(Option.of(42), Option.none(), Option.some(4));
    List<Integer> integersVavr = optionalsVavr.flatMap(o -> o);
    System.out.println(integersVavr);
  }
}
