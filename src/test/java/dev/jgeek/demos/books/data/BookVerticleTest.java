package dev.jgeek.demos.books.data;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(VertxExtension.class)
public class BookVerticleTest {

  @BeforeEach
  @DisplayName("deploy book verticle")
  void setup(Vertx vertx, VertxTestContext testContext){
    vertx.deployVerticle(new BookVerticle(), testContext.succeedingThenComplete());
  }

  @RepeatedTest(5)
  @Timeout(value = 500)
  void list_all_books_test(Vertx vertx, VertxTestContext testContext) {
    vertx.eventBus().<JsonArray>request(
      BookVerticle.Action.LIST.address,
      null,
      asyncResult -> testContext.verify(() -> {
        assertTrue(asyncResult.succeeded());
        var bookList = asyncResult.result().body();
        assertNotNull(bookList);
        assertEquals(3, bookList.size());
        testContext.completeNow();
      })
    );
  }
}
