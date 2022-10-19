package quitada;

import java.util.ArrayList;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class SimpleNonBlockingTest {
  public static void main(String[] args){
    List<Integer> elements = new ArrayList<>();

    Flux.just(1, 2, 3, 4, 1, 2, 3, 4, 1, 2, 3, 4, 1, 2, 3, 4, 1, 2, 3, 4, 1, 2, 3, 4, 1, 2, 3, 4, 1, 2, 3, 4, 1, 2, 3, 4)
        .log()
        .publishOn(Schedulers.parallel())
        .subscribe(element->{
          System.out.println("added " + element);
          elements.add(element);
        });

    System.out.println("the end of this sample");
  }
}
