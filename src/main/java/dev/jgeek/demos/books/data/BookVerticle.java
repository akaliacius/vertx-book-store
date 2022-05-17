package dev.jgeek.demos.books.data;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.core.eventbus.Message;

import java.util.Map;

public class BookVerticle extends AbstractVerticle {
  public enum Action {
    LIST("book.service.list");
    public final String address;

    Action(String address) {
      this.address = address;
    }
  }

  // in memory repository
  private final Map<Long, JsonObject> bookRepo = Map.of(
    1L, new JsonObject().put("id", 1L).put("title", "Clean Code").put("author", "Robert C. Martin"),
    2L, new JsonObject().put("id", 2L).put("title", "Effective Java").put("author", "Joshua Bloch"),
    3L, new JsonObject().put("id", 3L).put("title", "Vert.x in Action").put("author", "Julien Ponge")
  );

  @Override
  public Completable rxStart() {
    vertx.eventBus().consumer(Action.LIST.address, this::listBookHandler);
    return Completable.complete();
  }

  void listBookHandler(Message<Void> message){
    Observable.fromIterable(bookRepo.entrySet())
      .map(Map.Entry::getValue)
      .toList()
      .map(JsonArray::new)
      .subscribe(books -> message.reply(books));
  }
}
