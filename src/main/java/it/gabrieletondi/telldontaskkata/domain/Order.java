package it.gabrieletondi.telldontaskkata.domain;

import static it.gabrieletondi.telldontaskkata.domain.OrderStatus.CREATED;
import static it.gabrieletondi.telldontaskkata.domain.OrderStatus.REJECTED;
import static it.gabrieletondi.telldontaskkata.domain.OrderStatus.SHIPPED;

import it.gabrieletondi.telldontaskkata.useCase.shipment.invariants.OrderCannotBeShippedTwiceException;
import it.gabrieletondi.telldontaskkata.useCase.shipment.invariants.OrderNotReadyForShippmentException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class Order {

  private int id;
  private OrderStatus status;
  private OrderStatusNew newStatus;
  private List<OrderItem> items;
  private String currency;
  private BigDecimal total;
  private BigDecimal tax;

  public Order(int id, OrderStatus status, OrderStatusNew newStatus,
      List<OrderItem> items, String currency, BigDecimal total, BigDecimal tax) {
    this.id = id;
    this.status = status;
    this.newStatus = newStatus;
    this.items = items;
    this.currency = currency;
    this.total = total;
    this.tax = tax;
  }

  public static Order withoutOrderItems() {
    return new Order(1, OrderStatus.CREATED, new Created(), new ArrayList<>(), "EUR", new BigDecimal("0.00"),
        new BigDecimal("0.00"));
  }

  public boolean hasId(int orderId) {
    return id == orderId;
  }

  public boolean has(OrderStatus thatStatus) {
    return this.status == thatStatus;
  }

  public void reject() {
    this.newStatus = newStatus.reject();
  }

  public void approve() {
    this.newStatus = newStatus.approve();
  }

  public void ship() {
//    assertCanBeShipped();
    this.newStatus = newStatus.ship();
//    changeStatusTo(SHIPPED);
  }

  public void addOrderItemFor(Product product, int quantity) {
    add(OrderItem.forA(product, quantity));
  }

  private void add(OrderItem orderItem) {
    items.add(orderItem);
    total = orderItem.addTaxedAmountTo(total);
    tax = orderItem.addTaxAmountTo(tax);
  }

  private void assertNotShippedAlready() {
    if (shippedAlready()) {
      throw new OrderCannotBeShippedTwiceException();
    }
  }

  private void assertReadyForShipment() {
    if (notReadyForShipment()) {
      throw new OrderNotReadyForShippmentException();
    }
  }

  private boolean notReadyForShipment() {
    return is(CREATED) || is(REJECTED);
  }

  private void changeStatusTo(OrderStatus approved) {
    this.status = approved;
  }

  private boolean shippedAlready() {
    return is(SHIPPED);
  }

  private void assertCanBeShipped() {
    assertNotShippedAlready();
    assertReadyForShipment();
  }

  private boolean is(OrderStatus thatStatus) {
    return this.status == thatStatus;
  }

  public boolean has(OrderStatusNew status) {
    return newStatus.equals(status);
  }
}
